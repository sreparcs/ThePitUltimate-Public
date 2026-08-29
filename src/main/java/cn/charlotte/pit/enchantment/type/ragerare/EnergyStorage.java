package cn.charlotte.pit.enchantment.type.ragerare;

import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.IActionDisplayEnchant;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.Nullable;
import cn.charlotte.pit.register.IMagicLicense;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ArmorOnly
public class EnergyStorage extends AbstractEnchantment implements Listener, IActionDisplayEnchant, IMagicLicense {
    private static final Map<UUID, Integer> addMap = new HashMap();
    private static final Map<UUID, Long> latestMove = new HashMap();

    @Override
    public String getEnchantName() {
        return "蓄力";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "EnergyStorage";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.RAGE_RARE;
    }

    @Override
    public @Nullable Cooldown getCooldown() {
        return new Cooldown(0L);
    }

    @Override
    public String getUsefulnessLore(int level) {
        switch (level) {
            case 1 -> {
                return "&7每20秒不移动 &c+2%的伤害, 最高上限25%";
            }
            case 2 -> {
                return "&7每20秒不移动 &c+4%的伤害, 最高上限35%";
            }
            case 3 -> {
                return "&7每20秒不移动 &c+6%的伤害, 最高上限50%";
            }
            default -> {
                return "NULL";
            }
        }
    }

    @Override
    public String getText(int level, Player player) {
        if (!latestMove.containsKey(player.getUniqueId())) {
            return "+0%";
        } else {
            long elapsedTime = System.currentTimeMillis() - latestMove.getOrDefault(player.getUniqueId(), 0L);
            int add = (int)(elapsedTime / 20000L);
            int max;
            switch (level) {
                case 1:
                    max = 25;
                    add *= 2;
                    break;
                case 2:
                    max = 35;
                    add *= 4;
                    break;
                case 3:
                    max = 50;
                    add *= 6;
                    break;
                default:
                    return "NULL";
            }

            add = Math.min(max, add);
            addMap.put(player.getUniqueId(), add);
            return "+" + add + "%";
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (!event.getFrom().getBlock().equals(event.getTo().getBlock())) {
            latestMove.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player && !event.getDamager().isOnGround()) {
            Player player = (Player)event.getEntity();
            int level = this.getItemEnchantLevel(player.getItemInHand());
            if (level != -1) {
                int add = addMap.getOrDefault(event.getDamager().getUniqueId(), 0);
                event.setDamage(event.getDamage() * ((double)1.0F + (double)add / (double)100.0F));
            }
        }
    }
}