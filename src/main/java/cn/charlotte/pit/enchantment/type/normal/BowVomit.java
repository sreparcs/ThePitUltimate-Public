package cn.charlotte.pit.enchantment.type.normal;

import cn.charlotte.pit.ThePit;
import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.IActionDisplayEnchant;
import cn.charlotte.pit.enchantment.param.item.BowOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.AutoRegister;
import cn.charlotte.pit.parm.listener.IPlayerShootEntity;
import cn.charlotte.pit.util.chat.RomanUtil;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.time.TimeUtil;
import nya.Skip;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import cn.charlotte.pit.register.IMagicLicense;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@AutoRegister
@BowOnly
@Skip
public class BowVomit extends AbstractEnchantment implements IPlayerShootEntity, IActionDisplayEnchant, IMagicLicense, Listener {
    private static final HashMap<UUID, Cooldown> cooldown = new HashMap<>();

    @Override
    public String getEnchantName() {
        return "呕吐";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "BowVomit";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.NORMAL;
    }

    @Override
    public Cooldown getCooldown() {
        return null;
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        int durationSec = getEffectDuration(enchantLevel);
        return "&7箭矢命中玩家施加 &a反胃 " + RomanUtil.convert(enchantLevel) + " &f("
                + String.format("%02d:%02d", durationSec / 60, durationSec % 60)
                + ") &7(20秒冷却)";
    }

    private int getEffectDuration(int level) {
        return 4 * (level - 1);
    }

    private int getEffectAmplifier(int level) {
        return level - 1;
    }

    private void applyConfusionEffect(LivingEntity entity, int level) {
        int durationTicks = getEffectDuration(level) * 20;
        int amplifier = getEffectAmplifier(level);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, durationTicks, amplifier, true), true);
    }

    @Override
    public void handleShootEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        if (target instanceof LivingEntity && cooldown.getOrDefault(attacker.getUniqueId(), new Cooldown(0L)).hasExpired()) {
            cooldown.put(attacker.getUniqueId(), new Cooldown(20L, TimeUnit.SECONDS));
            applyConfusionEffect((LivingEntity) target, enchantLevel);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        cooldown.remove(e.getPlayer().getUniqueId());
    }

    @Override
    public String getText(int level, Player player) {
        Cooldown cd = cooldown.getOrDefault(player.getUniqueId(), new Cooldown(0L));
        return cd.hasExpired() ? "&a&l✔" : "&c&l" + TimeUtil.millisToRoundedTime(cd.getRemaining()).replace(" ", "");
    }
}