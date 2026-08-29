package cn.charlotte.pit.enchantment.type.ragerare;

import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IPlayerKilledEntity;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import cn.charlotte.pit.register.IMagicLicense;

@ArmorOnly
public class Angel extends AbstractEnchantment implements IPlayerKilledEntity, IMagicLicense {

    @Override
    public String getEnchantName() {
        return "天使";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "Angel";
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
        double expBonus = this.getExpBonus(level);
        return "&7击杀获得 &dx" + expBonus + "% &7经验值";
    }

    private double getExpBonus(int level) {
        switch (level) {
            case 1 -> {
                return (double)2.0F;
            }
            case 2 -> {
                return (double)5.0F;
            }
            case 3 -> {
                return (double)8.0F;
            }
            default -> {
                return (double)0.0F;
            }
        }
    }

    @Override
    public void handlePlayerKilled(int i, Player player, Entity entity, AtomicDouble atomicDouble, AtomicDouble atomicDouble1) {
        atomicDouble1.addAndGet(this.getExpBonus(i) * atomicDouble1.get() * 0.04);
    }
}