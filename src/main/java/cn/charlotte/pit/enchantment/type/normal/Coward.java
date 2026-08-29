package cn.charlotte.pit.enchantment.type.normal;

import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.event.PlayerOnly;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IPlayerDamaged;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import cn.charlotte.pit.register.IMagicLicense;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

@ArmorOnly
public class Coward extends AbstractEnchantment implements IPlayerDamaged, IMagicLicense {
    private static final Random random = new Random();

    @Override
    public String getEnchantName() {
        return "胆小鬼";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "Coward";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.NORMAL;
    }

    @Override
    public Cooldown getCooldown() {
        return new Cooldown(0L);
    }

    @Override
    public String getUsefulnessLore(int level) {
        double probability = this.getProbability(level);
        return "&7被攻击命中时有 &e" + probability * (double)100.0F + "%e &7概率获得 &b速度 II &f(00:03)";
    }

    private void applySpeedEffect(Player player, int level) {
        double probability = this.getProbability(level);
        if (random.nextDouble() < probability) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 1), true);
        }
    }

    private double getProbability(int level) {
        double probability;
        switch (level) {
            case 1 -> probability = 0.1;
            case 2 -> probability = 0.15;
            case 3 -> probability = 0.2;
            default -> probability = (double)0.0F;
        }
        return probability;
    }

    @PlayerOnly
    @Override
    public void handlePlayerDamaged(int i, Player player, Entity entity, double v, AtomicDouble atomicDouble, AtomicDouble atomicDouble1, AtomicBoolean atomicBoolean) {
        this.applySpeedEffect(player, i);
    }
}