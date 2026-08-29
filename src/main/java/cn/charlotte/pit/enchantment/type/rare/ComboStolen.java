package cn.charlotte.pit.enchantment.type.rare;

import cn.charlotte.pit.data.PlayerProfile;
import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.IActionDisplayEnchant;
import cn.charlotte.pit.enchantment.param.event.PlayerOnly;
import cn.charlotte.pit.enchantment.param.item.WeaponOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicBoolean;

@WeaponOnly
public class ComboStolen extends AbstractEnchantment implements IAttackEntity, IActionDisplayEnchant {
    private final DecimalFormat numFormat = new DecimalFormat("0.0");

    public String getEnchantName() {
        return "强力击: 窃取";
    }

    public int getMaxEnchantLevel() {
        return 3;
    }

    public String getNbtName() {
        return "ComboQieQu_enchant";
    }

    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.RARE;
    }

    @Nullable
    public Cooldown getCooldown() {
        return null;
    }

    public String getUsefulnessLore(int enchantLevel) {
        String var10000 = this.numFormat.format((double)0.5F * (double)enchantLevel);
        return "&7每 &e5 &7次击中使目标受到 &c" + var10000 + "❤ &7的&c必中&7伤害,/s同时恢复自身 &c" + this.numFormat.format((double)0.5F * (double)enchantLevel) + "❤ &7生命值./s&c(必中伤害无法被免疫与抵抗)";
    }

    @PlayerOnly
    public void handleAttackEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        if (PlayerProfile.getPlayerProfileByUuid(attacker.getUniqueId()).getMeleeHit() % 5 == 0) {
            ((Player)target).setHealth(Math.max(0.1, ((Player)target).getHealth() - (double)(2 * enchantLevel)));
            PlayerUtil.heal(attacker, (double)enchantLevel);
        }
    }

    public String getText(int level, Player player) {
        int hit = player.getItemInHand() != null && player.getItemInHand().getType() == Material.BOW ? PlayerProfile.getPlayerProfileByUuid(player.getUniqueId()).getBowHit() : PlayerProfile.getPlayerProfileByUuid(player.getUniqueId()).getMeleeHit();
        return hit % 5 == 0 ? "&a&l✔" : "&e&l" + (5 - hit % 5);
    }
}