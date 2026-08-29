package cn.charlotte.pit.enchantment.type.rare;

import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.event.PlayerOnly;
import cn.charlotte.pit.enchantment.param.item.WeaponOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.random.RandomUtil;
import nya.Skip;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: Misoryan
 * @Created_In: 2021/2/2 18:35
 */
@WeaponOnly
@Skip
public class GambleEnchant extends AbstractEnchantment implements IAttackEntity {

    @Override
    public String getEnchantName() {
        return "赌徒";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "gamble_enchant";
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
        return "&7攻击时有 &e50% &7的几率对自身或敌人"
                + "/s&7额外造成 &c" + enchantLevel + "❤ &7的&c必中&7伤害"
                + "/s&7&c(必中伤害无法被免疫与抵抗)";
    }

    @Override
    @PlayerOnly
    public void handleAttackEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        if (RandomUtil.hasSuccessfullyByChance(0.5)) {
            Player targetPlayer = (Player) target;
            Player gamblePlayer = attacker;
            if (RandomUtil.hasSuccessfullyByChance(0.5)) {
                gamblePlayer = targetPlayer;
                attacker.sendMessage("§4赌徒 §a你赌赢了!");
            } else {
                attacker.sendMessage( "§4赌徒 §c你赌输了!");
            }
            if (gamblePlayer.getHealth() > enchantLevel * 2) {
                PlayerUtil.damage(attacker,gamblePlayer, PlayerUtil.DamageType.TRUE, enchantLevel * 2,false);
            } else {
                gamblePlayer.damage(1000);
            }
        }
    }
}
