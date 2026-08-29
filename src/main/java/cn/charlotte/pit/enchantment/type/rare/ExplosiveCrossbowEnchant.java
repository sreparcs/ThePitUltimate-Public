package cn.charlotte.pit.enchantment.type.rare;

import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.item.BowOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IPlayerShootEntity;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

@BowOnly
public class ExplosiveCrossbowEnchant extends AbstractEnchantment implements IPlayerShootEntity {
    public String getEnchantName() {
        return "爆炸弓";
    }

    public int getMaxEnchantLevel() {
        return 3;
    }

    public String getNbtName() {
        return "explosive_bow";
    }

    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.RARE;
    }

    @Nullable
    public Cooldown getCooldown() {
        return null;
    }

    public String getUsefulnessLore(int enchantLevel) {
        return "&7当弓箭命中目标时, 将以目标为中心的半径 &f2 &7格内的玩家 /s&7造成 &c" + (double)enchantLevel * (double)0.5F + "❤ &7的扩散伤害";
    }

    public void handleShootEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        Player targetPlayer = (Player)target;
        this.customExplosion(enchantLevel, targetPlayer.getLocation());
    }

    private void customExplosion(int enchantLevel, Location location) {
        for(Entity entity : location.getWorld().getNearbyEntities(location, (double)2.0F, (double)2.0F, (double)2.0F)) {
            if (entity instanceof Player player) {
                player.damage((double)enchantLevel);
                player.getWorld().playSound(location, Sound.EXPLODE, 2.0F, 2.0F);
                player.getWorld().playEffect(location, Effect.EXPLOSION_LARGE, (Object)null);
                Vector currentVelocity = player.getVelocity();
                player.setVelocity(new Vector(currentVelocity.getX(), (double)0.5F, currentVelocity.getZ()));
            }
        }
    }
}