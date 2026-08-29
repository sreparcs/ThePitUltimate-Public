package cn.charlotte.pit.enchantment.type.ragerare;

import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.event.PlayerOnly;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IPlayerDamaged;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import cn.charlotte.pit.register.IMagicLicense;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

@ArmorOnly
public class End extends AbstractEnchantment implements IMagicLicense, IPlayerDamaged {
    private static final Random random = new Random();

    @Override
    public String getEnchantName() {
        return "终点";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "End";
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
        double maxDamage = this.getDamage(level);
        return "&7单次最多受到 " + maxDamage + "❤ &7伤害 &7(不含真实伤害) (大于时生效)";
    }

    private double getDamage(int level) {
        switch (level) {
            case 1 -> {
                return (double)3.0F;
            }
            case 2 -> {
                return (double)2.5F;
            }
            case 3 -> {
                return (double)2.0F;
            }
            default -> {
                return (double)0.0F;
            }
        }
    }

    @PlayerOnly
    @Override
    public void handlePlayerDamaged(int i, Player player, Entity entity, double v, AtomicDouble atomicDouble, AtomicDouble atomicDouble1, AtomicBoolean atomicBoolean) {
        Player target = (Player)entity;
        if (i != -1) {
            if (atomicDouble1.get() > this.getDamage(i) * (double)0.5F) {
                atomicDouble1.set(this.getDamage(i) * (double)0.5F);
            }

            double maxDamage = this.getDamage(i);
            player.sendMessage(CC.translate("&c&l终点! &7抵消对方部分伤害,受到 &d" + maxDamage + "❤ &7伤害"));
            target.sendMessage(CC.translate("&c&l终点! &7你的攻击被对方附魔抵消至 &d" + maxDamage + "❤"));
        }
    }
}