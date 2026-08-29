package cn.charlotte.pit.enchantment.type.dark_rare;

import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.event.PlayerOnly;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.AutoRegister;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.parm.listener.IPlayerShootEntity;
import cn.charlotte.pit.parm.type.BowOnly;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import cn.charlotte.pit.register.IMagicLicense;

import java.util.concurrent.atomic.AtomicBoolean;

@AutoRegister
@ArmorOnly
public class DeathKnellEnchant extends AbstractEnchantment implements IAttackEntity, IPlayerShootEntity {
    @Override
    public String getEnchantName() {
        return "丧钟";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 1;
    }

    @Override
    public String getNbtName() {
        return "Zainan_enchant";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.DARK_RARE;
    }

    @Override
    public Cooldown getCooldown() {
        return null;
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        return "&7每次攻击额外对目标造成 &f1❤ &7的&c必中&7伤害, 但每次使用扣除自身 &c0.5❤ &7生命值."
                + "/s&c(必中伤害无法被免疫与抵抗)";
    }

    @Override
    @PlayerOnly
    public void handleAttackEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        ((Player) target).setHealth(Math.max(0.1, ((Player) target).getHealth() - 2));
        attacker.setHealth(Math.max(0.1, attacker.getHealth() - 2 * 0.5));
    }

    @Override
    @PlayerOnly
    @BowOnly
    public void handleShootEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        ((Player) target).setHealth(Math.max(0.1, ((Player) target).getHealth() - 2));
        attacker.setHealth(Math.max(0.1, attacker.getHealth() - 2 * 0.5));
    }


}
