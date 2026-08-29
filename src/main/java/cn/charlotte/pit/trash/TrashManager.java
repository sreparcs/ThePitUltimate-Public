package cn.charlotte.pit.trash;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.data.sub.PlayerTrash;
import cn.charlotte.pit.config.NewConfiguration;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.item.ItemBuilder;
import cn.charlotte.pit.util.item.ItemUtil;
import cn.charlotte.pit.util.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class TrashManager {

    public static final String BUTTON_INTERNAL_NAME = "trash_clean_button";
    public static final String FILLER_INTERNAL_NAME = "trash_filler";

    private static long cycleStartTime = System.currentTimeMillis();
    private static boolean started = false;

    private TrashManager() {
    }

    public static void start() {
        if (started) {
            return;
        }
        started = true;

        long persisted = NewConfiguration.INSTANCE.getTrashCycleStartTime();
        if (persisted <= 0L || persisted > System.currentTimeMillis()) {
            cycleStartTime = System.currentTimeMillis();
            NewConfiguration.INSTANCE.saveTrashCycleStartTime(cycleStartTime);
        } else {
            cycleStartTime = persisted;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    tick();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskTimer(ThePit.getInstance(), 20L, 20L);
    }

    private static void tick() {
        if (getRemainingMillis() <= 0) {
            cleanAll();
        }
        refreshViewers();
    }

    public static long getIntervalMillis() {
        int minutes = Math.max(1, NewConfiguration.INSTANCE.getTrashCleanInterval());
        return TimeUnit.MINUTES.toMillis(minutes);
    }

    public static long getCycleStartTime() {
        return cycleStartTime;
    }

    public static long getNextCleanTime() {
        return cycleStartTime + getIntervalMillis();
    }

    public static long getRemainingMillis() {
        return getNextCleanTime() - System.currentTimeMillis();
    }

    public static String getFormattedRemainingTime() {
        long remaining = Math.max(0L, getRemainingMillis());
        long hours = TimeUnit.MILLISECONDS.toHours(remaining);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(remaining) - TimeUnit.HOURS.toMinutes(hours);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(remaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remaining));

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("%02d:%02d", minutes, seconds);
    }

    public static void cleanAll() {
        cycleStartTime = System.currentTimeMillis();
        NewConfiguration.INSTANCE.saveTrashCycleStartTime(cycleStartTime);

        final List<PlayerProfile> dirty = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
            if (profile == null || !profile.isLoaded()) {
                continue;
            }

            PlayerTrash trash = profile.getTrash();
            boolean hadItems = !trash.isEmpty();
            trash.snapshotAndClean(cycleStartTime);

            if (isViewingTrash(player)) {
                decorate(trash.getInventory());
                player.updateInventory();
            }

            dirty.add(profile);

            if (hadItems) {
                player.sendMessage(CC.translate("&c&l垃圾桶已清理! &7使用 &e/lj back &7可恢复上一次的垃圾桶."));
            }
        }

        if (dirty.isEmpty()) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(ThePit.getInstance(), () -> {
            for (PlayerProfile profile : dirty) {
                try {
                    profile.save(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void cleanIfExpired(PlayerProfile profile) {
        PlayerTrash trash = profile.getTrash();
        if (trash.getLastCleanTime() < cycleStartTime) {
            trash.snapshotAndClean(cycleStartTime);
        }
    }

    public static void openTrash(Player player) {
        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
        if (profile == null || !profile.isLoaded()) {
            player.sendMessage(CC.translate("&c档案还没加载完, 请稍后再试."));
            return;
        }

        cleanIfExpired(profile);

        player.closeInventory();
        Menu.currentlyOpenedMenus.remove(player.getName());

        Inventory inventory = profile.getTrash().getInventory();
        decorate(inventory);
        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1F, 0.6F);
    }

    public static String restore(Player player) {
        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
        if (profile == null || !profile.isLoaded()) {
            return "&c档案还没加载完, 请稍后再试.";
        }

        cleanIfExpired(profile);

        PlayerTrash trash = profile.getTrash();
        if (!trash.hasBackup()) {
            return "&c没有找到上一次的垃圾桶备份.";
        }

        ItemStack[] current = trash.getStorageContents();
        int freeSlots = 0;
        for (ItemStack item : current) {
            if (item == null || item.getTypeId() == 0) {
                freeSlots++;
            }
        }

        ItemStack[] backup = readBackupPreview(trash);
        int needed = 0;
        for (ItemStack item : backup) {
            if (item != null && item.getTypeId() != 0) {
                needed++;
            }
        }

        if (needed > freeSlots) {
            return "&c垃圾桶空间不足, 需要 &e" + needed + " &c个空格, 当前只有 &e" + freeSlots + " &c个.";
        }

        ItemStack[] items = trash.consumeBackup();
        if (items == null) {
            return "&c没有找到上一次的垃圾桶备份.";
        }

        int cursor = 0;
        for (ItemStack item : items) {
            if (item == null || item.getTypeId() == 0) {
                continue;
            }
            while (cursor < current.length && current[cursor] != null && current[cursor].getTypeId() != 0) {
                cursor++;
            }
            if (cursor >= current.length) {
                break;
            }
            current[cursor++] = item;
        }
        trash.setStorageContents(current);
        profile.save(player);

        if (isViewingTrash(player)) {
            decorate(trash.getInventory());
            player.updateInventory();
        }
        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1F, 1F);
        return "&a垃圾桶已恢复到上一次清理前的状态.";
    }

    private static ItemStack[] readBackupPreview(PlayerTrash trash) {
        PlayerTrash preview = PlayerTrash.deserialization(trash.getBackup(), "", 0L);
        return preview.getStorageContents();
    }

    public static boolean isViewingTrash(Player player) {
        return player.getOpenInventory() != null
                && player.getOpenInventory().getTopInventory() != null
                && PlayerTrash.TITLE.equals(player.getOpenInventory().getTopInventory().getTitle());
    }

    private static void refreshViewers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!isViewingTrash(player)) {
                continue;
            }
            PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
            if (profile == null || !profile.isLoaded()) {
                continue;
            }
            decorate(profile.getTrash().getInventory());
            player.updateInventory();
        }
    }

    public static void decorate(Inventory inventory) {
        ItemStack filler = createFiller();
        for (int slot = PlayerTrash.STORAGE_SIZE; slot < inventory.getSize(); slot++) {
            if (slot == PlayerTrash.CLEAN_BUTTON_SLOT) {
                inventory.setItem(slot, createCleanButton());
            } else {
                inventory.setItem(slot, filler);
            }
        }
    }

    public static ItemStack createCleanButton() {
        return new ItemBuilder(Material.BARRIER)
                .name("&c&l清理垃圾桶")
                .lore(Arrays.asList(
                        "",
                        "&7距离自动清理: &e" + getFormattedRemainingTime(),
                        "&7清理后可用 &e/lj back &7恢复一次.",
                        "",
                        "&e点击立即清理!"
                ))
                .internalName(BUTTON_INTERNAL_NAME)
                .canDrop(false)
                .canTrade(false)
                .canSaveToEnderChest(false)
                .removeOnJoin(true)
                .build();
    }

    public static ItemStack createFiller() {
        return new ItemBuilder(Material.STAINED_GLASS_PANE)
                .durability(15)
                .name(" ")
                .internalName(FILLER_INTERNAL_NAME)
                .canDrop(false)
                .canTrade(false)
                .canSaveToEnderChest(false)
                .removeOnJoin(true)
                .build();
    }

    public static boolean isMenuItem(ItemStack item) {
        if (item == null || item.getTypeId() == 0) {
            return false;
        }
        String internalName = ItemUtil.getInternalName(item);
        return BUTTON_INTERNAL_NAME.equals(internalName) || FILLER_INTERNAL_NAME.equals(internalName);
    }
}
