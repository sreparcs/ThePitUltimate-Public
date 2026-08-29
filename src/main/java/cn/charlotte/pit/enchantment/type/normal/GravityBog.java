package cn.charlotte.pit.enchantment.type.normal;

import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IPlayerDamaged;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import cn.charlotte.pit.register.IMagicLicense;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@ArmorOnly
public class GravityBog extends AbstractEnchantment implements IPlayerDamaged, IMagicLicense {
    private static final HashMap<UUID, Cooldown> COOLDOWN = new HashMap();

    @Override
    public String getEnchantName() {
        return "重力泥沼";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "GravityBog";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.NORMAL;
    }

    @Override
    public @Nullable Cooldown getCooldown() {
        return new Cooldown(3L, TimeUnit.SECONDS);
    }

    @Override
    public String getUsefulnessLore(int i) {
        String var10000 = i > 1 ? "/s且恢复自身 &c" + (double)0.5F * (double)i + "❤" : "";
        return "&7当你受到弓箭伤害时使你免疫本次伤害" + var10000 + " &7(3秒冷却)";
    }

    @Override
    public void handlePlayerDamaged(int i, Player player, Entity entity, double v, AtomicDouble atomicDouble, AtomicDouble atomicDouble1, AtomicBoolean atomicBoolean) {
        if (entity instanceof Arrow && ((Cooldown)COOLDOWN.getOrDefault(player.getUniqueId(), new Cooldown(0L))).hasExpired()) {
            atomicBoolean.set(true);
            if (i > 1) {
                PlayerUtil.heal(player, (double)i);
            }

            COOLDOWN.put(player.getUniqueId(), this.getCooldown());
        }
    }
}