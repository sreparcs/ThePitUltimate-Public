package cn.charlotte.pit.enchantment.type.rare;

import cn.charlotte.pit.ThePit;
import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.IActionDisplayEnchant;
import cn.charlotte.pit.enchantment.param.item.BowOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IPlayerShootEntity;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@BowOnly
public class LunarDeity extends AbstractEnchantment implements Listener, IPlayerShootEntity, IActionDisplayEnchant {
    @NotNull
    private static final Map<UUID, Cooldown> cooldowns = new HashMap<>();

    @NotNull
    public String getEnchantName() {
        return "月神之矢";
    }

    public int getMaxEnchantLevel() {
        return 3;
    }

    @NotNull
    public String getNbtName() {
        return "lunar_deity";
    }

    @NotNull
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.OP;
    }

    @Nullable
    public Cooldown getCooldown() {
        return null;
    }

    @NotNull
    public String getUsefulnessLore(int enchantLevel) {
        int radius = this.checkRadius(enchantLevel);
        long cooldownTime = enchantLevel >= 4 ? 0L : 8L - (long)enchantLevel * 2L;
        return "&7射出箭矢时, 箭矢将锁定于自身中心的 &f" + radius + " &7格内距离最近的目标 &7(" + cooldownTime + "s冷却) /s&7射出的箭矢速度将会加快, 同时命中目标时将获得 &3抗性提升 I &f(00:04)";
    }

    private final int checkRadius(int enchantLevel) {
        return enchantLevel >= 3 ? enchantLevel * 8 : enchantLevel + 12;
    }

    @EventHandler
    public final void onPlayerShootBow(@NotNull EntityShootBowEvent event) {
        Entity enchantLevel = event.getProjectile();
        Arrow arrow = null;
        if (enchantLevel instanceof Arrow) {
            arrow = (Arrow) enchantLevel;
        }
        if (arrow != null) {
            ProjectileSource shooterCooldown = arrow.getShooter();
            Player shooter = null;
            if (shooterCooldown instanceof Player) {
                shooter = (Player) shooterCooldown;
            }
            if (shooter != null) {
                int level = ThePit.getApi().getItemEnchantLevel(shooter.getInventory().getItemInHand(), this.getNbtName());
                if (level >= 1 && !PlayerUtil.shouldIgnoreEnchant(shooter)) {
                    Cooldown cd = (Cooldown) cooldowns.getOrDefault(shooter.getUniqueId(), new Cooldown(0L));
                    if (cd.hasExpired()) {
                        Location arrowLoc = arrow.getLocation().clone();
                        int radius = this.checkRadius(level);
                        Player nearestPlayer = null;
                        double nearestDistanceSquared = Double.MAX_VALUE;
                        double radiusSquared = (double) (radius * radius);
                        List<Entity> nearbyEntities = arrow.getNearbyEntities((double) radius, (double) radius, (double) radius);
                        for (Entity entity : nearbyEntities) {
                            if (entity instanceof Player && !entity.equals(shooter)) {
                                double dx = arrowLoc.getX() - ((Player) entity).getLocation().getX();
                                double dy = arrowLoc.getY() - ((Player) entity).getLocation().getY();
                                double dz = arrowLoc.getZ() - ((Player) entity).getLocation().getZ();
                                double distanceSquared = dx * dx + dy * dy + dz * dz;
                                if (distanceSquared < nearestDistanceSquared && distanceSquared <= radiusSquared) {
                                    nearestDistanceSquared = distanceSquared;
                                    nearestPlayer = (Player) entity;
                                }
                            }
                        }
                        if (nearestPlayer != null) {
                            arrow.setVelocity(this.optimizedTrajectory(arrowLoc, nearestPlayer, arrow.getVelocity().length() + 0.2));
                        }
                    }
                }
            }
        }
    }

    private final Vector optimizedTrajectory(Location start, Player target, double speed) {
        Location targetHead = target.getLocation().add(0.0F, 1.55, 0.0F);
        double tx = targetHead.getX() - start.getX();
        double ty = targetHead.getY() - start.getY();
        double tz = targetHead.getZ() - start.getZ();
        double hDist = Math.sqrt(tx * tx + tz * tz);
        double gravityComp = 0.05;
        double adjustedTy = ty + gravityComp * hDist;
        double angle = Math.atan2(adjustedTy, hDist);
        double yVel = Math.sin(angle) * speed;
        double hVel = Math.cos(angle) * speed;
        return hDist > 1.0E-5 ? new Vector(tx / hDist * hVel, yVel, tz / hDist * hVel) : new Vector(0.0F, yVel, 0.0F);
    }

    public void handleShootEntity(int enchantLevel, @NotNull Player shooter, @Nullable Entity target, double v, @Nullable AtomicDouble atomicDouble, @Nullable AtomicDouble atomicDouble1, @Nullable AtomicBoolean atomicBoolean) {
        Cooldown cd = (Cooldown) cooldowns.getOrDefault(shooter.getUniqueId(), new Cooldown(0L));
        if (cd.hasExpired()) {
            cooldowns.put(shooter.getUniqueId(), new Cooldown(enchantLevel >= 4 ? 0L : 8L - (long) enchantLevel * 2L, TimeUnit.SECONDS));
            shooter.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            shooter.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 80, 0, false, true));
        }
    }

    @Nullable
    public String getText(int level, @NotNull Player player) {
        String text;
        if (PlayerUtil.isVenom(player)) {
            text = "&c&l✘";
        } else {
            Cooldown cd = (Cooldown) cooldowns.getOrDefault(player.getUniqueId(), new Cooldown(0L));
            text = this.getCooldownActionText(cd);
        }
        return text;
    }
}