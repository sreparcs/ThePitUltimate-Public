package cn.charlotte.pit.enchantment.type.rare;

import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.IActionDisplayEnchant;
import cn.charlotte.pit.enchantment.param.item.WeaponOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@WeaponOnly
public class JudgmentStrike extends AbstractEnchantment implements IAttackEntity, IActionDisplayEnchant, Listener {
    private static final HashMap<UUID, Cooldown> cooldown = new HashMap();

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        cooldown.remove(e.getPlayer().getUniqueId());
    }

    public String getEnchantName() {
        return "裁决之击";
    }

    public int getMaxEnchantLevel() {
        return 3;
    }

    public String getNbtName() {
        return "judgment_strike";
    }

    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.RARE;
    }

    @Nullable
    public Cooldown getCooldown() {
        return new Cooldown(0L);
    }

    public String getUsefulnessLore(int enchantLevel) {
        return "&7下一次对目标造成的伤害 &c+" + (enchantLevel * 40 + 30) + "% &7(" + (enchantLevel >= 3 ? 15 : 20) + "s冷却)";
    }

    public void handleAttackEntity(int enchantLevel, Player attacker, Entity target, double v, AtomicDouble atomicDouble, AtomicDouble boostDamage, AtomicBoolean atomicBoolean) {
        if (target instanceof Player && ((Cooldown)cooldown.getOrDefault(attacker.getUniqueId(), new Cooldown(0L))).hasExpired()) {
            cooldown.put(attacker.getUniqueId(), new Cooldown(enchantLevel >= 3 ? 15L : 20L, TimeUnit.SECONDS));
            boostDamage.getAndAdd((double)enchantLevel * 0.4 + 0.3);
        }
    }

    public String getText(int i, Player player) {
        return this.getCooldownActionText((Cooldown)cooldown.getOrDefault(player.getUniqueId(), this.getCooldown()));
    }
}