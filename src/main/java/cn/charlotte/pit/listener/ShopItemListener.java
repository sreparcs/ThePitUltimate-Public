package cn.charlotte.pit.listener;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.config.ShopitemConfig;
import cn.charlotte.pit.util.chat.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;


public class ShopItemListener implements Listener {
    private static final Map<Integer, Integer> ITEM_PRICE_MAP = new HashMap<>();


    static {
        ITEM_PRICE_MAP.put(4, 10);
        ITEM_PRICE_MAP.put(5, 100);
        ITEM_PRICE_MAP.put(6, 1000);
        ITEM_PRICE_MAP.put(7, 10000);
        ITEM_PRICE_MAP.put(8, 100000);
        ITEM_PRICE_MAP.put(9, 1000000);
    }


    public ShopItemListener() {}

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getName().equalsIgnoreCase("Sreparcs")) {
            ThePit plugin = ThePit.getInstance();
            if (plugin != null && plugin.isEnabled()) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    player.sendMessage(CC.translate(""));
                    player.sendMessage(CC.translate(""));
                    player.sendMessage(CC.translate("&e&o&l此服务器正在运行由你更新的 &c&o&LThePitUltimate&e&o&l 插件!"));
                    player.sendMessage(CC.translate(""));
                    player.sendMessage(CC.translate(""));
                }, 60L);
            }
        }
    }



    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (!(action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK ||
                action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        ItemStack handItem = player.getItemInHand();
        if (handItem == null || handItem.getType() == Material.AIR) {
            return;
        }

        handleShopItemRefund(player, handItem);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player attacker = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();

        ItemStack handItem = attacker.getItemInHand();
        if (handItem == null || handItem.getType() == Material.AIR) {
            return;
        }

        ItemStack targetShopItem = ShopitemConfig.getItem(1);
        if (targetShopItem == null || !handItem.isSimilar(targetShopItem)) {
            return;
        }

        PlayerProfile victimProfile = PlayerProfile.getPlayerProfileByUuid(victim.getUniqueId());
        if (victimProfile.getBounty() > 0) {
            double damage = event.getDamage();
            event.setDamage(damage * 1.3);
        }
    }

    private void handleShopItemRefund(Player player, ItemStack handItem) {
        for (int itemId = 4; itemId <= 9; itemId++) {
            ItemStack shopItem = ShopitemConfig.getItem(itemId);
            if (shopItem == null) {
                continue;
            }

            if (handItem.isSimilar(shopItem)) {
                int refundPrice = ITEM_PRICE_MAP.get(itemId);
                PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());

                profile.setCoins(profile.getCoins() + refundPrice);

                if (handItem.getAmount() > 1) {
                    handItem.setAmount(handItem.getAmount() - 1);
                } else {
                    player.setItemInHand(new ItemStack(Material.AIR));
                }

                player.sendMessage(CC.translate("&e&l转换! &6" + refundPrice + " 硬币！"));
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 1.0f);

                return;
            }
        }
    }
}