package cn.charlotte.pit.enchantment.type.normal;

import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IPlayerDamaged;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import cn.charlotte.pit.register.IMagicLicense;

import java.util.concurrent.atomic.AtomicBoolean;

@ArmorOnly
public class Thorns extends AbstractEnchantment implements IPlayerDamaged, IMagicLicense, Listener {

    @Override
    public String getEnchantName() {
        return "荆棘";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 1;
    }

    @Override
    public String getNbtName() {
        return "Thistles";
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
        return "&7受击时将对攻击者造成 &c" + (double)enchantLevel * (double)0.25F + "❤ &7的&c必中&7伤害";
    }

    @Override
    public void handlePlayerDamaged(int enchantLevel, Player player, Entity attacker, double damage, AtomicDouble boostDamage, AtomicDouble reduceDamage, AtomicBoolean cancel) {
        if (attacker instanceof Player && ((Player)attacker).getHealth() >= (double)enchantLevel * (double)0.5F) {
            ((Player)attacker).setHealth(((Player)attacker).getHealth() - (double)enchantLevel * (double)0.5F);
        } else if (attacker instanceof Player) {
            ((Player)attacker).damage(((Player)attacker).getHealth() * (double)100.0F);
        }
    }
}