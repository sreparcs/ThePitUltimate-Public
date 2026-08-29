package cn.charlotte.pit.menu.preview;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.config.PreviewConfig;
import cn.charlotte.pit.util.chat.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class PreviewGUI implements Listener {

    private static final int GUI_SIZE = 45;
    private static final String GUI_TITLE = "§0物品展示";
    private static final int START_SLOT = 10;
    private static final int ITEMS_PER_ROW = 7;
    private static final int TOTAL_ITEMS = 21;
    private static Plugin pluginInstance;
    public PreviewGUI(Plugin plugin) {
        if (pluginInstance == null) {
            pluginInstance = plugin;
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    public PreviewGUI() {
        this(ThePit.getInstance());
    }

    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, GUI_SIZE, GUI_TITLE);
        fillWhiteGlassPane(gui);
        fillPreviewItems(gui);
        player.openInventory(gui);
    }

    private void fillWhiteGlassPane(Inventory gui) {
        ItemStack whiteGlass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0);
        ItemMeta meta = whiteGlass.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§f");
            whiteGlass.setItemMeta(meta);
        }
        gui.setItem(9, whiteGlass);
        gui.setItem(17, whiteGlass);
        gui.setItem(18, whiteGlass);
        gui.setItem(26, whiteGlass);
        gui.setItem(27, whiteGlass);
        gui.setItem(35, whiteGlass);
    }

    private void fillPreviewItems(Inventory gui) {
        int currentGuiSlot = START_SLOT;
        int currentItemIndex = 1;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < ITEMS_PER_ROW; col++) {
                if (currentItemIndex > TOTAL_ITEMS) {
                    break;
                }
                ItemStack item = PreviewConfig.getItem(currentItemIndex);
                if (item != null && item.getType() != Material.AIR) {
                    gui.setItem(currentGuiSlot, item);
                }
                currentItemIndex++;
                if (col != ITEMS_PER_ROW - 1) {
                    currentGuiSlot++;
                }
            }
            currentGuiSlot += 3;
            if (currentItemIndex > TOTAL_ITEMS) {
                break;
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getTitle().equals(GUI_TITLE) && event.getInventory().getSize() == GUI_SIZE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().getTitle().equals(GUI_TITLE) || event.getInventory().getSize() != GUI_SIZE) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        event.setCancelled(true);

        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }
        if (clickedItem.getType() == Material.STAINED_GLASS_PANE && clickedItem.getDurability() == 0) {
            return;
        }

        if (player.hasPermission("pit.admin")) {
            player.getInventory().addItem(clickedItem.clone());
            player.playSound(player.getLocation(), Sound.VILLAGER_YES, 1.5F, 1.5F);
        } else {
            player.sendMessage(CC.translate("§c请前往神话附魔台获取！"));
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.5F, 1.5F);
        }
    }
}