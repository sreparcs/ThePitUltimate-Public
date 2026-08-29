package cn.charlotte.pit.enchantment.type.normal;

import cn.charlotte.pit.data.PlayerProfile;
import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import cn.charlotte.pit.register.IMagicLicense;

import java.util.concurrent.atomic.AtomicBoolean;

@ArmorOnly
public class ListEnchant extends AbstractEnchantment implements IAttackEntity, IMagicLicense {

    @Override
    public String getEnchantName() {
        return "榜上有名";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "Tbilly_enchant";
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
        return "&7你每持有 &6&l1000g &7赏金, 造成的&e近战&7伤害 &c+" + (enchantLevel + 2) + "% &7(可叠加, 最高2层)";
    }

    @Override
    public void handleAttackEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(attacker.getUniqueId());
        int boost = 0;
        int bounty = profile.getBounty();
        if (bounty >= 1000) {
            boost = 1;
        }

        if (bounty >= 2000) {
            boost = 2;
        }

        if (profile.getBounty() >= 1000) {
            boostDamage.getAndAdd(0.01 * (double)(2 * enchantLevel) * (double)boost);
        }
    }
}