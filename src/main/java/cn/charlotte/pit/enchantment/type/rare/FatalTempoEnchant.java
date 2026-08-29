package cn.charlotte.pit.enchantment.type.rare;

import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.param.item.BowOnly;
import cn.charlotte.pit.enchantment.param.item.WeaponOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.parm.listener.IPlayerShootEntity;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.random.RandomUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

@WeaponOnly
@ArmorOnly
@BowOnly
public class FatalTempoEnchant extends AbstractEnchantment implements IAttackEntity, IPlayerShootEntity {
    public String getEnchantName() {
        return "致密节奏";
    }

    public int getMaxEnchantLevel() {
        return 3;
    }

    public String getNbtName() {
        return "FatalTempo";
    }

    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.RARE;
    }

    public @Nullable Cooldown getCooldown() {
        return null;
    }

    private double getChanceInt(int enchantLevel) {
        switch (enchantLevel) {
            case 2 -> {
                return 0.1;
            }
            case 3 -> {
                return 0.15;
            }
            default -> {
                return 0.05;
            }
        }
    }

    private String getChancePrefix(int enchantLevel) {
        switch (enchantLevel) {
            case 2 -> {
                return "&6";
            }
            case 3 -> {
                return "&c";
            }
            default -> {
                return "&e";
            }
        }
    }

    public String getUsefulnessLore(int i) {
        String var10000 = this.getChancePrefix(i);
        return "&7每次攻击命中有 " + var10000 + this.getChanceInt(i) * (double)100.0F + "% &7的概率造成双倍伤害.";
    }

    public void handleAttackEntity(int i, Player player, Entity entity, double v, AtomicDouble atomicDouble, AtomicDouble atomicDouble1, AtomicBoolean atomicBoolean) {
        if (RandomUtil.hasSuccessfullyByChance(this.getChanceInt(i))) {
            atomicDouble1.getAndAdd((double)1.0F);
        }
    }

    @cn.charlotte.pit.parm.type.BowOnly
    public void handleShootEntity(int i, Player player, Entity entity, double v, AtomicDouble atomicDouble, AtomicDouble atomicDouble1, AtomicBoolean atomicBoolean) {
        if (RandomUtil.hasSuccessfullyByChance(this.getChanceInt(i))) {
            atomicDouble1.getAndAdd((double)1.0F);
        }
    }
}