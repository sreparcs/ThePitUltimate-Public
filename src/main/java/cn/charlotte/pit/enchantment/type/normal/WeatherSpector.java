package cn.charlotte.pit.enchantment.type.normal;

import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.parm.listener.IPlayerDamaged;
import cn.charlotte.pit.parm.listener.IPlayerShootEntity;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import cn.charlotte.pit.register.IMagicLicense;

import java.util.concurrent.atomic.AtomicBoolean;

@ArmorOnly
public class WeatherSpector extends AbstractEnchantment implements IAttackEntity, IPlayerDamaged, IPlayerShootEntity, IMagicLicense {

    private boolean day() {
        Server server = Bukkit.getServer();
        long time = server.getWorld("world").getTime();
        return time > 0L && time < 12300L;
    }

    @Override
    public String getEnchantName() {
        return "气象";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "weather_spector";
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
    public String getUsefulnessLore(int i) {
        return "&7在&f白天&7穿戴时: &7攻击造成的伤害 &c+" + i * 4 + "%./s&7在&5夜晚&7穿戴时: &7受到的的伤害 &9-" + i * 4 + "%.";
    }

    @Override
    public void handleAttackEntity(int i, Player player, Entity entity, double v, AtomicDouble atomicDouble, AtomicDouble atomicDouble1, AtomicBoolean atomicBoolean) {
        if (this.day()) {
            atomicDouble1.getAndAdd(0.04 * (double)i);
        }
    }

    @Override
    public void handleShootEntity(int i, Player player, Entity entity, double v, AtomicDouble atomicDouble, AtomicDouble atomicDouble1, AtomicBoolean atomicBoolean) {
        if (this.day()) {
            atomicDouble1.getAndAdd(0.04 * (double)i);
        }
    }

    @Override
    public void handlePlayerDamaged(int i, Player player, Entity entity, double v, AtomicDouble atomicDouble, AtomicDouble atomicDouble1, AtomicBoolean atomicBoolean) {
        if (!this.day()) {
            atomicDouble1.getAndAdd(-0.04 * (double)i);
        }
    }
}