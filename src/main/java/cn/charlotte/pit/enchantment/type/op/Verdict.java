package cn.charlotte.pit.enchantment.type.op;

import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.IActionDisplayEnchant;
import cn.charlotte.pit.enchantment.param.item.WeaponOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.parm.listener.IPlayerKilledEntity;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import cn.charlotte.pit.register.IMagicLicense;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@WeaponOnly
public class Verdict extends AbstractEnchantment implements IMagicLicense, IAttackEntity, IActionDisplayEnchant, IPlayerKilledEntity {
    private static final Map<UUID, Cooldown> cooldown = new ConcurrentHashMap();

    @Override
    public String getEnchantName() {
        return "裁决";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "verdict";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.OP;
    }

    @Override
    public Cooldown getCooldown() {
        return null;
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        double threshold = (double)2.0F + (double)enchantLevel * (double)0.5F;
        String cooldownText = enchantLevel >= 4 ? "" : "(" + (10 - enchantLevel * 2) + "s 冷却)";
        return String.format("若命中时使目标生命值低于 &c%.1f❤ &7时, 则该次命中直接致死 %s/s&7(每击杀一位目标将减少此附魔1s冷却)", threshold, cooldownText);
    }

    @Override
    public void handleAttackEntity(int enchantLevel, Player attacker, Entity entity, double damage, AtomicDouble atomicDouble, AtomicDouble atomicDouble1, AtomicBoolean atomicBoolean) {
        if (entity instanceof Player target) {
            double threshold = ((double)2.0F + (double)enchantLevel * (double)0.5F) * (double)2.0F;
            Cooldown attackerCooldown = cooldown.getOrDefault(attacker.getUniqueId(), new Cooldown(0L));
            if (target.getHealth() <= threshold && attackerCooldown.hasExpired()) {
                cooldown.put(attacker.getUniqueId(), new Cooldown(10L - (long)enchantLevel * 2L, TimeUnit.SECONDS));
                target.playEffect(target.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
                target.playSound(target.getLocation(), Sound.STEP_STONE, 1.0F, 1.0F);
                target.damage(target.getMaxHealth() * (double)20.0F);
            }
        }
    }

    @Override
    public void handlePlayerKilled(int enchantLevel, Player killer, Entity entity, AtomicDouble atomicDouble, AtomicDouble atomicDouble1) {
        Cooldown killerCooldown = cooldown.getOrDefault(killer.getUniqueId(), new Cooldown(0L));
        if (!killerCooldown.hasExpired()) {
            cooldown.put(killer.getUniqueId(), new Cooldown(Math.max(0L, killerCooldown.getRemaining() - 1000L)));
        }
    }

    @Override
    public String getText(int level, Player attacker) {
        Cooldown attackerCooldown = cooldown.getOrDefault(attacker.getUniqueId(), new Cooldown(0L));
        return PlayerUtil.isVenom(attacker) ? "&c&l✘" : this.getCooldownActionText(attackerCooldown);
    }
}