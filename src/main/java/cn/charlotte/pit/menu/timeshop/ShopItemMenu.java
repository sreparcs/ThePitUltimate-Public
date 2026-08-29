package cn.charlotte.pit.menu.timeshop;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.config.ShopitemConfig;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.inventory.InventoryUtil;
import cn.charlotte.pit.util.item.ItemBuilder;
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
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class ShopItemMenu implements Listener {
    private static final int GUI_SIZE = 45;
    private static final String GUI_TITLE = "§0时空商人";

    private static final int ITEM_1_SLOT = 10;
    private static final int ITEM_2_SLOT = 11;
    private static final int ITEM_3_SLOT = 12;
    private static final int ITEM_4_SLOT = 19;
    private static final int ITEM_5_SLOT = 20;
    private static final int ITEM_6_SLOT = 21;
    private static final int ITEM_7_SLOT = 28;
    private static final int ITEM_8_SLOT = 29;
    private static final int ITEM_9_SLOT = 30;
    private static final int ITEM_10_SLOT = 16;

    // 价格全部改为 double
    private static final double ITEM_1_PRICE = 1000;
    private static final double ITEM_2_PRICE = 500;
    private static final double ITEM_3_PRICE = 300;
    private static final double ITEM_4_PRICE = 10;
    private static final double ITEM_5_PRICE = 100;
    private static final double ITEM_6_PRICE = 1000;
    private static final double ITEM_7_PRICE = 10000;
    private static final double ITEM_8_PRICE = 100000;
    private static final double ITEM_9_PRICE = 1000000;
    private static final double ITEM_10_PRICE = 10000000;

    private static final int ITEM_10_RENOWN = 200;

    private static Plugin pluginInstance;

    public ShopItemMenu(Plugin plugin) {
        if (pluginInstance == null) {
            pluginInstance = plugin;
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    public ShopItemMenu() {
        this(ThePit.getInstance());
    }

    public void openMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, GUI_SIZE, GUI_TITLE);
        fillShopItems(gui, player);
        player.openInventory(gui);
    }

    private void fillShopItems(Inventory gui, Player player) {
        fillSingleItem(gui, player, 1, ITEM_1_SLOT, ITEM_1_PRICE);
        fillSingleItem(gui, player, 2, ITEM_2_SLOT, ITEM_2_PRICE);
        fillSingleItem(gui, player, 3, ITEM_3_SLOT, ITEM_3_PRICE);
        fillSingleItem(gui, player, 4, ITEM_4_SLOT, ITEM_4_PRICE);
        fillSingleItem(gui, player, 5, ITEM_5_SLOT, ITEM_5_PRICE);
        fillSingleItem(gui, player, 6, ITEM_6_SLOT, ITEM_6_PRICE);
        fillSingleItem(gui, player, 7, ITEM_7_SLOT, ITEM_7_PRICE);
        fillSingleItem(gui, player, 8, ITEM_8_SLOT, ITEM_8_PRICE);
        fillSingleItem(gui, player, 9, ITEM_9_SLOT, ITEM_9_PRICE);
        fillItemWithRenown(gui, player, 10, ITEM_10_SLOT, ITEM_10_PRICE, ITEM_10_RENOWN);
    }


    private void fillSingleItem(Inventory gui, Player player, int itemConfigIndex, int guiSlot, double price) {
        ItemStack targetItem = ShopitemConfig.getItem(itemConfigIndex);
        if (targetItem != null && targetItem.getType() != Material.AIR) {
            PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
            double playerCoins = profile.getCoins();

            List<String> lore = new ArrayList<>();
            if (targetItem.hasItemMeta() && targetItem.getItemMeta().hasLore()) {
                lore.addAll(targetItem.getItemMeta().getLore());
            }
            lore.add("");
            lore.add(CC.translate("&7价格: &6" + price + " 硬币"));
            lore.add("");
            if (playerCoins >= price) {
                lore.add(CC.translate("&e&l点击购买!"));
            } else {
                lore.add(CC.translate("&c硬币不足!"));
            }

            ItemStack displayItem = new ItemBuilder(targetItem)
                    .lore(lore)
                    .build();
            gui.setItem(guiSlot, displayItem);
        }
    }

    private void fillItemWithRenown(Inventory gui, Player player, int itemConfigIndex, int guiSlot, double price, int requiredRenown) {
        ItemStack targetItem = ShopitemConfig.getItem(itemConfigIndex);
        if (targetItem != null && targetItem.getType() != Material.AIR) {
            PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
            double playerCoins = profile.getCoins();
            int playerRenown = profile.getRenown();

            List<String> lore = new ArrayList<>();
            if (targetItem.hasItemMeta() && targetItem.getItemMeta().hasLore()) {
                lore.addAll(targetItem.getItemMeta().getLore());
            }
            lore.add("");
            lore.add(CC.translate("&7价格: &6" + price + " 硬币"));
            lore.add(CC.translate("&7声望: &d" + requiredRenown + " 点"));
            lore.add("");
            if (playerCoins >= price && playerRenown >= requiredRenown) {
                lore.add(CC.translate("&e&l点击购买!"));
            } else {
                lore.add(CC.translate("&c硬币或声望不足!"));
            }

            ItemStack displayItem = new ItemBuilder(targetItem)
                    .lore(lore)
                    .build();
            gui.setItem(guiSlot, displayItem);
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

        int clickedSlot = event.getRawSlot();
        if (clickedSlot == ITEM_1_SLOT) {
            handleItemPurchase(player, 1, ITEM_1_PRICE, event.getInventory());
        } else if (clickedSlot == ITEM_2_SLOT) {
            handleItemPurchase(player, 2, ITEM_2_PRICE, event.getInventory());
        } else if (clickedSlot == ITEM_3_SLOT) {
            handleItemPurchase(player, 3, ITEM_3_PRICE, event.getInventory());
        } else if (clickedSlot == ITEM_4_SLOT) {
            handleItemPurchase(player, 4, ITEM_4_PRICE, event.getInventory());
        } else if (clickedSlot == ITEM_5_SLOT) {
            handleItemPurchase(player, 5, ITEM_5_PRICE, event.getInventory());
        } else if (clickedSlot == ITEM_6_SLOT) {
            handleItemPurchase(player, 6, ITEM_6_PRICE, event.getInventory());
        } else if (clickedSlot == ITEM_7_SLOT) {
            handleItemPurchase(player, 7, ITEM_7_PRICE, event.getInventory());
        } else if (clickedSlot == ITEM_8_SLOT) {
            handleItemPurchase(player, 8, ITEM_8_PRICE, event.getInventory());
        } else if (clickedSlot == ITEM_9_SLOT) {
            handleItemPurchase(player, 9, ITEM_9_PRICE, event.getInventory());
        } else if (clickedSlot == ITEM_10_SLOT) {
            handleItemPurchaseWithRenown(player, 10, ITEM_10_PRICE, ITEM_10_RENOWN, event.getInventory());
        }
    }


    private void handleItemPurchase(Player player, int itemConfigIndex, double price, Inventory currentGui) {
        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
        double playerCoins = profile.getCoins();

        if (playerCoins < price) {
            player.sendMessage(CC.translate("&c你的硬币不足！需要 &6" + price + " &c硬币"));
            player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 0.1F);
            return;
        }

        if (InventoryUtil.isInvFull(player)) {
            player.sendMessage(CC.translate("&c&l背包已满! &7你的背包已满，暂时无法购买物品。"));
            player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 0.1F);
            return;
        }

        profile.setCoins(playerCoins - price);
        player.getInventory().addItem(ShopitemConfig.getItem(itemConfigIndex));

        player.sendMessage(CC.translate("&a购买成功! 消耗 &6" + price + "&a 硬币"));
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);

        fillSingleItem(currentGui, player, itemConfigIndex, getSlotByItemIndex(itemConfigIndex), price);
    }

    private void handleItemPurchaseWithRenown(Player player, int itemConfigIndex, double price, int requiredRenown, Inventory currentGui) {
        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
        double playerCoins = profile.getCoins();
        int playerRenown = profile.getRenown();

        if (playerCoins < price || playerRenown < requiredRenown) {
            player.sendMessage(CC.translate("&c你的硬币或声望不足！需要 " + price + " 硬币和 " + requiredRenown + " 声望"));
            player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 0.1F);
            return;
        }

        if (InventoryUtil.isInvFull(player)) {
            player.sendMessage(CC.translate("&c&l背包已满! &7你的背包已满，暂时无法购买物品。"));
            player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 0.1F);
            return;
        }

        profile.setCoins(playerCoins - price);
        profile.setRenown(playerRenown - requiredRenown);

        player.getInventory().addItem(ShopitemConfig.getItem(itemConfigIndex));

        player.sendMessage(CC.translate("&a购买成功! 消耗 &6" + price + " &a硬币和 &d" + requiredRenown + "&a 声望"));
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);

        fillItemWithRenown(currentGui, player, itemConfigIndex, getSlotByItemIndex(itemConfigIndex), price, requiredRenown);
    }

    private int getSlotByItemIndex(int itemConfigIndex) {
        return switch (itemConfigIndex) {
            case 1 -> ITEM_1_SLOT;
            case 2 -> ITEM_2_SLOT;
            case 3 -> ITEM_3_SLOT;
            case 4 -> ITEM_4_SLOT;
            case 5 -> ITEM_5_SLOT;
            case 6 -> ITEM_6_SLOT;
            case 7 -> ITEM_7_SLOT;
            case 8 -> ITEM_8_SLOT;
            case 9 -> ITEM_9_SLOT;
            case 10 -> ITEM_10_SLOT;
            default -> -1;
        };
    }
}