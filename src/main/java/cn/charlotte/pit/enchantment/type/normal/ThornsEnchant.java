package cn.charlotte.pit.enchantment.type.normal;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.AutoRegister;
import cn.charlotte.pit.parm.listener.IPlayerDamaged;
import cn.charlotte.pit.util.cooldown.Cooldown;
import com.google.common.util.concurrent.AtomicDouble;
import nya.Skip;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: Misoryan
 * @Created_In: 2021/1/17 21:30
 */
@AutoRegister
@ArmorOnly
public class ThornsEnchant extends AbstractEnchantment implements IPlayerDamaged {

    @Override
    public String getEnchantName() {
        return "生命感知";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "Thorns";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.RARE;
    }

    @Override
    public Cooldown getCooldown() {
        return null;
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        return "&7自身生命值低于最大生命值 &c25% &7时受到攻击,/s对攻击者造成 &c" + 0.5 * (enchantLevel + 1) + "❤ &7普通伤害并附带击退";
    }

    @Override
    public void handlePlayerDamaged(int enchantLevel, Player myself, Entity attacker, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        // 核心修复：1.判断攻击者是真实玩家 2.排除NPC 3.玩家在线判断 避免空指针/强转异常
        if (!(attacker instanceof Player)
                || ThePit.getInstance().getNpcFactory().hasNPC((Player) attacker)
                || !((Player) attacker).isOnline()) {
            return;
        }

        // 生命值低于25%才触发
        if (myself.getHealth() / myself.getMaxHealth() <= 0.25) {
            Player realAttacker = (Player) attacker;
            // 原逻辑的反伤（保持原有伤害计算）
            realAttacker.damage((0.5F * (enchantLevel + 1)) * 2, myself);

            // 补上附魔描述中承诺的击退效果（原代码缺失，此处补充）
            Vector knockback = realAttacker.getLocation().toVector()
                    .subtract(myself.getLocation().toVector())
                    .normalize()
                    .multiply(1.2)
                    .setY(0.3);
            realAttacker.setVelocity(knockback);
        }
    }
}