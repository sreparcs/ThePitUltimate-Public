package cn.charlotte.pit.enchantment.type.normal;

import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.event.PlayerOnly;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.param.item.BowOnly;
import cn.charlotte.pit.enchantment.param.item.WeaponOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IPlayerKilledEntity;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import cn.charlotte.pit.register.IMagicLicense;

@WeaponOnly
@BowOnly
@ArmorOnly
public class Endeavor extends AbstractEnchantment implements IPlayerKilledEntity, IMagicLicense {

    @Override
    public String getEnchantName() {
        return "竭力之获";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "try_to_give_enchant";
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
        return "&7击杀获得 &b+" + 4 * enchantLevel + " 经验值";
    }

    @PlayerOnly
    @Override
    public void handlePlayerKilled(int enchantLevel, Player myself, Entity target, AtomicDouble coins, AtomicDouble experience) {
        experience.addAndGet((double)(4 * enchantLevel));
    }
}