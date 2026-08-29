package cn.charlotte.pit.enchantment.type.rare;

import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.event.PlayerOnly;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.param.item.WeaponOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IPlayerKilledEntity;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

@WeaponOnly
@ArmorOnly
public class MoreWisdomEnchant extends AbstractEnchantment implements IPlayerKilledEntity {
    public String getEnchantName() {
        return "更高的智慧";
    }

    public int getMaxEnchantLevel() {
        return 3;
    }

    public String getNbtName() {
        return "morewisdom";
    }

    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.RARE;
    }

    @Nullable
    public Cooldown getCooldown() {
        return null;
    }

    public String getUsefulnessLore(int enchantLevel) {
        return "&7击杀获得 &b+" + (30 + enchantLevel * 20) + "% 经验值";
    }

    @PlayerOnly
    public void handlePlayerKilled(int enchantLevel, Player myself, Entity target, AtomicDouble coins, AtomicDouble experience) {
        experience.set(experience.get() * ((double)1.0F + 0.01 * (double)(30 + enchantLevel * 20)));
    }
}