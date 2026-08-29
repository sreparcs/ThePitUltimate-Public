package cn.charlotte.pit.enchantment.type.rare;

import cn.charlotte.pit.ThePit;
import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.IActionDisplayEnchant;
import cn.charlotte.pit.enchantment.param.event.PlayerOnly;
import cn.charlotte.pit.enchantment.param.item.BowOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.AutoRegister;
import cn.charlotte.pit.parm.listener.IPlayerShootEntity;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.time.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: EmptyIrony & Sreparcs
 * @Date: 2021/3/6 22:22
 * @FixDate: 2026/1/31 15:06
 * 吸力附魔 - 修复NPC吸引问题
 */
@AutoRegister
@BowOnly
public class PullBowEnchant extends AbstractEnchantment implements Listener, IPlayerShootEntity, IActionDisplayEnchant {


    private static final HashMap<UUID, Cooldown> COOLDOWN_MAP = new HashMap<>();

    private static final double PULL_RANGE = 2.5D;

    private static final long BASE_COOLDOWN = 8L;

    @Override
    public String getEnchantName() {
        return "吸力";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "pullbow_enchant";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.RARE;
    }

    @Override
    public Cooldown getCooldown() {
        return null;
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        return "&7箭矢命中可以将敌人" + (enchantLevel >= 2 ? "及其周围2.5格的玩家" : "") + "&7向你所在的位置拖拽"
                + "/s&7此附魔" + (enchantLevel >= 3 ? "每 &f8 &7秒只能触发一次." : "&7每影响一名玩家,此附魔需要额外等待 &f8 &7秒才能再次触发.");
    }

    @Override
    @PlayerOnly
    public void handleShootEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        if (!(target instanceof Player)
                || ThePit.getInstance().getNpcFactory().hasNPC((Player) target)
                || !attacker.isOnline()) {
            return;
        }

        Player realPlayerTarget = (Player) target;
        Cooldown attackerCd = COOLDOWN_MAP.getOrDefault(attacker.getUniqueId(), new Cooldown(0));
        if (!attackerCd.hasExpired()) {
            return;
        }


        Bukkit.getScheduler().runTaskLater(ThePit.getInstance(), () -> {
            if (!attacker.isOnline() || !realPlayerTarget.isOnline()
                    || ThePit.getInstance().getNpcFactory().hasNPC(attacker)
                    || ThePit.getInstance().getNpcFactory().hasNPC(realPlayerTarget)) {
                return;
            }

            int pulledPlayerCount = 0;

            for (Player nearbyPlayer : PlayerUtil.getNearbyPlayers(target.getLocation(), PULL_RANGE)) {

                if (nearbyPlayer == attacker
                        || !nearbyPlayer.isOnline()
                        || ThePit.getInstance().getNpcFactory().hasNPC(nearbyPlayer)) {
                    continue;
                }


                if (enchantLevel <= 1 && !nearbyPlayer.getUniqueId().equals(realPlayerTarget.getUniqueId())) {
                    continue;
                }


                Vector pullDirection = attacker.getLocation().toVector()
                        .subtract(nearbyPlayer.getLocation().toVector())
                        .normalize();
                // pullDirection.setY(0.1);
                nearbyPlayer.setVelocity(pullDirection);

                pulledPlayerCount++; 
            }

            long finalCdTime = enchantLevel >= 3 ? BASE_COOLDOWN : BASE_COOLDOWN * pulledPlayerCount;
            COOLDOWN_MAP.put(attacker.getUniqueId(), new Cooldown(finalCdTime, TimeUnit.SECONDS));
        }, 1L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        COOLDOWN_MAP.remove(e.getPlayer().getUniqueId());
    }

    @Override
    public String getText(int level, Player player) {
        Cooldown playerCd = COOLDOWN_MAP.getOrDefault(player.getUniqueId(), new Cooldown(0));
        return playerCd.hasExpired()
                ? "&a&l✔"
                : "&c&l" + TimeUtil.millisToRoundedTime(playerCd.getRemaining()).replace(" ", "");
    }
}