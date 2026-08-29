package cn.charlotte.pit.enchantment.type.rare;

import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.event.PlayerOnly;
import cn.charlotte.pit.enchantment.param.item.WeaponOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

@WeaponOnly
public class RedShark extends AbstractEnchantment implements IAttackEntity {
    private static final Random random = new Random();

    public String getEnchantName() {
        return "赤鲨";
    }

    public int getMaxEnchantLevel() {
        return 3;
    }

    public String getNbtName() {
        return "RedShark";
    }

    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.RARE;
    }

    @Nullable
    public Cooldown getCooldown() {
        return new Cooldown(0L);
    }

    public String getUsefulnessLore(int level) {
        double var10000 = this.getDamage(level);
        return "&7攻击生命值大于 50% 的敌人造成伤害 &c+" + var10000 + "%";
    }

    public double getDamage(int level) {
        double var10000;
        switch (level) {
            case 1 -> var10000 = (double)15.0F;
            case 2 -> var10000 = (double)35.0F;
            case 3 -> var10000 = (double)55.0F;
            default -> var10000 = (double)0.0F;
        }

        return var10000;
    }

    @PlayerOnly
    public void handleAttackEntity(int i, Player player, Entity entity, double v, AtomicDouble atomicDouble, AtomicDouble atomicDouble1, AtomicBoolean atomicBoolean) {
        if (i >= 1) {
            Player target = (Player)entity;
            double maxHealth = target.getMaxHealth();
            double health = target.getHealth();
            if (health >= maxHealth / (double)2.0F) {
                atomicDouble1.getAndAdd(this.getDamage(i) / (double)100.0F);
            }
        }
    }
}