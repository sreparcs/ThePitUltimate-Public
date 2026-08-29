package cn.charlotte.pit.enchantment.type.ragerare;

import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.event.PlayerOnly;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.AutoRegister;
import cn.charlotte.pit.parm.listener.IPlayerKilledEntity;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * {@code @Creator} Sreparcs
 * @Date 2026/2/16 22:35
 */
@ArmorOnly
@AutoRegister
public class Plutocrat extends AbstractEnchantment implements IPlayerKilledEntity {

    @Override
    public String getEnchantName() {
        return "富豪";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "Plutocrat";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.RAGE_RARE;
    }

    @Nullable
    @Override
    public Cooldown getCooldown() {
        return null;
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        double bonusRate = getCoinsBonusRate(enchantLevel);
        return "&7击杀获得 &6+" + (bonusRate * 100) + "% &7硬币";
    }

    @Override
    @PlayerOnly
    public void handlePlayerKilled(int enchantLevel, Player killer, Entity target, AtomicDouble coins, AtomicDouble experience) {
        if (!(target instanceof Player) || coins == null) {
            return;
        }

        double bonusRate = getCoinsBonusRate(enchantLevel);
        coins.getAndAdd(coins.get() * bonusRate);
    }

    private double getCoinsBonusRate(int level) {
        return switch (level) {
            case 1 -> 0.3;
            case 2 -> 0.6;
            case 3 -> 1.0;
            default -> 0.0;
        };
    }
}