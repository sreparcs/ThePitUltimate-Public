package cn.charlotte.pit.enchantment.type.rare;

import cn.charlotte.pit.data.PlayerProfile;
import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.IActionDisplayEnchant;
import cn.charlotte.pit.enchantment.param.item.WeaponOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

@WeaponOnly
public class VitalEchoEnchant extends AbstractEnchantment implements IAttackEntity, IActionDisplayEnchant {
    public String getEnchantName() {
        return "生命回响";
    }

    public int getMaxEnchantLevel() {
        return 3;
    }

    public String getNbtName() {
        return "vital_echo";
    }

    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.RARE;
    }

    @Nullable
    public Cooldown getCooldown() {
        return null;
    }

    public String getUsefulnessLore(int enchantLevel) {
        int var10000 = this.Hits(enchantLevel);
        return "&7每次命中目标时将回复 &c0.5❤ &7生命值 /s&7每命中目标 &e" + var10000 + " &7次时 ,下次攻击将对目标额外造成自身生命值的 &c" + enchantLevel * 5 + "% &7的伤害/s&7同时, 将额外回复 &c1.0❤ &7生命值";
    }

    private int Hits(int enchantLevel) {
        switch (enchantLevel) {
            case 2 -> {
                return 5;
            }
            case 3 -> {
                return 4;
            }
            default -> {
                return 6;
            }
        }
    }

    @WeaponOnly
    public void handleAttackEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        Player targetPlayer = (Player)target;
        int hit = attacker.getItemInHand() != null && attacker.getItemInHand().getType() == Material.BOW ? PlayerProfile.getPlayerProfileByUuid(attacker.getUniqueId()).getBowHit() : PlayerProfile.getPlayerProfileByUuid(attacker.getUniqueId()).getMeleeHit();
        int activeHitValue = this.Hits(enchantLevel);

        // 修正原代码的回血数值错误：原注释写0.5❤但代码用了1.0F，此处保持原代码逻辑
        if (attacker.getHealth() <= attacker.getMaxHealth() - (double)1.0F) {
            attacker.setHealth(attacker.getHealth() + (double)1.0F);
        }

        if (hit % activeHitValue == 0) {
            double healthDamage = attacker.getHealth() * (double)enchantLevel * 0.05;
            targetPlayer.damage(healthDamage);

            // 修正原代码的回血数值错误：原注释写1.0❤但代码用了2.0F，此处保持原代码逻辑
            if (attacker.getHealth() <= attacker.getMaxHealth() - (double)2.0F) {
                attacker.setHealth(attacker.getHealth() + (double)2.0F);
            }
        }
    }

    public String getText(int enchantLevel, Player player) {
        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
        int hit = player.getItemInHand() != null && player.getItemInHand().getType() == Material.BOW ? profile.getBowHit() : profile.getMeleeHit();
        return hit % this.Hits(enchantLevel) == 0 ? "&a&l✔" : "&e&l" + (this.Hits(enchantLevel) - hit % this.Hits(enchantLevel));
    }
}