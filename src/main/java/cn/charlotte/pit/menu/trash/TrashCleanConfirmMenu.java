package cn.charlotte.pit.menu.trash;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.data.sub.PlayerTrash;
import cn.charlotte.pit.trash.TrashManager;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.item.ItemBuilder;
import cn.charlotte.pit.util.menu.Button;
import cn.charlotte.pit.util.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TrashCleanConfirmMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "&c确认清理垃圾桶?";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(11, new ConfirmButton());
        buttons.put(15, new CancelButton());
        return buttons;
    }

    @Override
    public boolean isAutoUpdate() {
        return false;
    }

    @Override
    public int getSize() {
        return 3 * 9;
    }

    private static class ConfirmButton extends Button {
        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.STAINED_CLAY)
                    .durability(5)
                    .name("&a&l确认清理")
                    .lore(Arrays.asList(
                            "",
                            "&7清理后可用 &e/lj back &7恢复一次.",
                            "&c注意: 恢复机会只有一次!"
                    ))
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton, ItemStack currentItem) {
            PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
            if (profile == null || !profile.isLoaded()) {
                player.sendMessage(CC.translate("&c档案还没加载完, 请稍后再试."));
                player.closeInventory();
                return;
            }

            PlayerTrash trash = profile.getTrash();
            if (trash.isEmpty()) {
                player.sendMessage(CC.translate("&c垃圾桶里没有东西可以清理."));
                playFail(player);
                player.closeInventory();
                return;
            }

            trash.snapshotAndClean(TrashManager.getCycleStartTime());
            profile.save(player);

            player.sendMessage(CC.translate("&a&l垃圾桶已清理! &7使用 &e/lj back &7可恢复一次."));
            player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1F, 1F);
            player.closeInventory();
        }
    }

    private static class CancelButton extends Button {
        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.STAINED_CLAY)
                    .durability(14)
                    .name("&c&l取消")
                    .lore(Arrays.asList("", "&7返回垃圾桶."))
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton, ItemStack currentItem) {
            playNeutral(player);
            Bukkit.getScheduler().runTask(ThePit.getInstance(), () -> TrashManager.openTrash(player));
        }
    }
}
