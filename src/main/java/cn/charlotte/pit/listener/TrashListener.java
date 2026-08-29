package cn.charlotte.pit.listener;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.data.sub.PlayerTrash;
import cn.charlotte.pit.menu.trash.TrashCleanConfirmMenu;
import cn.charlotte.pit.trash.TrashManager;
import cn.charlotte.pit.util.item.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class TrashListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Inventory top = event.getView().getTopInventory();
        if (top == null || !PlayerTrash.TITLE.equals(top.getTitle())) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        boolean clickedTop = event.getRawSlot() >= 0 && event.getRawSlot() < top.getSize();

        if (event.getClick() == ClickType.NUMBER_KEY
                || event.getClick() == ClickType.DOUBLE_CLICK
                || event.getAction() == InventoryAction.COLLECT_TO_CURSOR
                || event.getAction() == InventoryAction.HOTBAR_SWAP
                || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD) {
            event.setCancelled(true);
            player.updateInventory();
            return;
        }

        if (TrashManager.isMenuItem(event.getCurrentItem()) || TrashManager.isMenuItem(event.getCursor())) {
            event.setCancelled(true);
            if (clickedTop && event.getRawSlot() == PlayerTrash.CLEAN_BUTTON_SLOT
                    && !TrashManager.isMenuItem(event.getCursor())) {
                openConfirmNextTick(player);
            }
            return;
        }

        if (clickedTop && event.getRawSlot() >= PlayerTrash.STORAGE_SIZE) {
            event.setCancelled(true);
            return;
        }

        if (event.isShiftClick()) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getTypeId() == 0) {
                return;
            }

            if (clickedTop) {
                moveToPlayerInventory(player, top, event.getRawSlot(), clicked);
            } else {
                if (!canStore(clicked)) {
                    player.sendMessage("§c该物品不能放入垃圾桶!");
                    return;
                }
                moveToTrash(player, top, event.getSlot(), clicked);
            }
            player.updateInventory();
            return;
        }

        if (clickedTop && event.getCursor() != null && event.getCursor().getTypeId() != 0 && !canStore(event.getCursor())) {
            event.setCancelled(true);
            player.sendMessage("§c该物品不能放入垃圾桶!");
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDrag(InventoryDragEvent event) {
        Inventory top = event.getView().getTopInventory();
        if (top == null || !PlayerTrash.TITLE.equals(top.getTitle())) {
            return;
        }

        if (TrashManager.isMenuItem(event.getOldCursor()) || !canStore(event.getOldCursor())) {
            event.setCancelled(true);
            return;
        }

        for (int rawSlot : event.getRawSlots()) {
            if (rawSlot < top.getSize() && rawSlot >= PlayerTrash.STORAGE_SIZE) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Inventory inventory = event.getInventory();
        if (inventory == null || !PlayerTrash.TITLE.equals(inventory.getTitle())) {
            return;
        }

        Player player = (Player) event.getPlayer();
        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
        if (profile != null && profile.isLoaded()) {
            profile.save(player);
        }
        player.playSound(player.getLocation(), Sound.CHEST_CLOSE, 1F, 0.6F);
    }

    private void openConfirmNextTick(Player player) {
        Bukkit.getScheduler().runTask(ThePit.getInstance(), () -> new TrashCleanConfirmMenu().openMenu(player));
    }

    private void moveToTrash(Player player, Inventory trash, int playerSlot, ItemStack item) {
        int remaining = item.getAmount();
        int maxStack = item.getType().getMaxStackSize();

        for (int slot = 0; slot < PlayerTrash.STORAGE_SIZE && remaining > 0; slot++) {
            ItemStack existing = trash.getItem(slot);
            if (existing == null || existing.getTypeId() == 0 || !existing.isSimilar(item)) {
                continue;
            }
            int space = maxStack - existing.getAmount();
            if (space <= 0) {
                continue;
            }
            int moved = Math.min(space, remaining);
            existing.setAmount(existing.getAmount() + moved);
            trash.setItem(slot, existing);
            remaining -= moved;
        }

        for (int slot = 0; slot < PlayerTrash.STORAGE_SIZE && remaining > 0; slot++) {
            ItemStack existing = trash.getItem(slot);
            if (existing != null && existing.getTypeId() != 0) {
                continue;
            }
            ItemStack copy = item.clone();
            int moved = Math.min(maxStack, remaining);
            copy.setAmount(moved);
            trash.setItem(slot, copy);
            remaining -= moved;
        }

        writeBackPlayerSlot(player, playerSlot, item, remaining);
    }

    private void moveToPlayerInventory(Player player, Inventory trash, int trashSlot, ItemStack item) {
        ItemStack copy = item.clone();
        int before = copy.getAmount();
        int leftOver = 0;
        for (ItemStack rest : player.getInventory().addItem(copy).values()) {
            leftOver += rest.getAmount();
        }

        if (leftOver >= before) {
            player.sendMessage("§c你的背包已经满了!");
            return;
        }

        if (leftOver <= 0) {
            trash.setItem(trashSlot, null);
        } else {
            ItemStack remain = item.clone();
            remain.setAmount(leftOver);
            trash.setItem(trashSlot, remain);
            player.sendMessage("§c你的背包已经满了!");
        }
    }

    private void writeBackPlayerSlot(Player player, int playerSlot, ItemStack item, int remaining) {
        if (remaining <= 0) {
            player.getInventory().setItem(playerSlot, null);
            return;
        }
        if (remaining == item.getAmount()) {
            player.sendMessage("§c垃圾桶已经放不下了!");
            return;
        }
        ItemStack remain = item.clone();
        remain.setAmount(remaining);
        player.getInventory().setItem(playerSlot, remain);
        player.sendMessage("§c垃圾桶已经放不下了!");
    }

    private boolean canStore(ItemStack item) {
        if (item == null || item.getTypeId() == 0) {
            return true;
        }
        if (TrashManager.isMenuItem(item)) {
            return false;
        }
        return !ItemUtil.isDefaultItem(item) && !ItemUtil.isRemovedOnJoin(item);
    }
}
