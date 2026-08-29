package cn.charlotte.pit.enchantment.type.normal;

import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.item.WeaponOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IPlayerDamaged;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import cn.charlotte.pit.register.IMagicLicense;

import java.util.concurrent.atomic.AtomicBoolean;

@WeaponOnly
public class ProtectResentment extends AbstractEnchantment implements IPlayerDamaged, IMagicLicense {

    @Override
    public String getEnchantName() {
        return "兮积";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "protect_resentment";
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
        int boostRate = 0;
        switch (enchantLevel) {
            case 1 -> boostRate = 2;
            case 2 -> boostRate = 3;
            case 3 -> boostRate = 5;
        }

        return "&7生命值每低于最大生命值 &c1❤ &7, 你受到的伤害 &9-" + boostRate + "% &7(最高30%)";
    }

    @Override
    public void handlePlayerDamaged(int enchantLevel, Player myself, Entity attacker, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        double boostRate = (double)0.0F;
        switch (enchantLevel) {
            case 1 -> boostRate = (double)2.0F;
            case 2 -> boostRate = (double)3.0F;
            case 3 -> boostRate = (double)5.0F;
        }

        boostRate *= (double)Math.round(myself.getMaxHealth() - myself.getHealth());
        if (boostRate >= (double)60.0F) {
            boostRate = (double)60.0F;
        }

        boostDamage.getAndAdd(-0.01 * boostRate);
    }
}