package cn.charlotte.pit.enchantment.type.normal;

import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.item.WeaponOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import cn.charlotte.pit.register.IMagicLicense;

import java.util.concurrent.atomic.AtomicBoolean;

@WeaponOnly
public class Outnumbered extends AbstractEnchantment implements IAttackEntity, IMagicLicense {

    @Override
    public String getEnchantName() {
        return "敌众我寡";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "they_big_our_small_enchant";
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
        return "&7在以你为中心的 &b12 &7格范围内,/s&7每存在一名除你以外的敌人,/s&7你造成的&e近战&7伤害 &c+" + 2 * enchantLevel + "% &7(可叠加, 最高3层)";
    }

    @Override
    public void handleAttackEntity(int enchantLevel, Player myself, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        double boost = (double)PlayerUtil.getNearbyPlayers(myself.getLocation(), (double)12.0F).size();
        if (boost >= (double)3.0F) {
            boost = (double)3.0F;
        }

        boostDamage.getAndAdd(0.01 * (double)(2 * enchantLevel) * boost);
    }
}