package cn.charlotte.pit.enchantment.type.rare;

import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.item.BowOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.Nullable;

@BowOnly
public class RadiantShootingEnchant extends AbstractEnchantment implements Listener {
    public String getEnchantName() {
        return "璀璨射击";
    }

    public int getMaxEnchantLevel() {
        return 3;
    }

    public String getNbtName() {
        return "RadiantShooting";
    }

    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.RARE;
    }

    @Nullable
    public Cooldown getCooldown() {
        return null;
    }

    public String getUsefulnessLore(int level) {
        switch (level) {
            case 1 -> {
                return "&7命中被攻击玩家,造成 &c0.5❤ &7的真实伤害&7且命中的目标低于 &c1.5❤ &7直接致死.";
            }
            case 2 -> {
                return "&7命中被攻击的玩家,造成 &c1.0❤ &7的真实伤害&7且命中的目标低于 &c2.0❤ &7直接致死.";
            }
            case 3 -> {
                return "&7命中被攻击的玩家,造成 &c1.5❤ &7的真实伤害&7且命中的目标低于 &c2.5❤ &7直接致死.";
            }
            default -> {
                return "NULL";
            }
        }
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.MONITOR
    )
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
            ProjectileSource shooter = ((Arrow)event.getDamager()).getShooter();
            if (shooter instanceof Player) {
                Player player = (Player)shooter;
                if (!PlayerUtil.shouldIgnoreEnchant(player, (Player)event.getEntity())) {
                    int level = this.getItemEnchantLevel(player.getItemInHand());
                    if (level != -1) {
                        this.applyDamage((LivingEntity)event.getEntity(), level);
                    }
                }
            }
        }
    }

    private void applyDamage(LivingEntity entity, int level) {
        double damage;
        double threshold;
        switch (level) {
            case 1:
                damage = (double)0.5F;
                threshold = (double)1.5F;
                break;
            case 2:
                damage = (double)1.0F;
                threshold = (double)2.0F;
                break;
            case 3:
                damage = (double)1.5F;
                threshold = (double)2.5F;
                break;
            default:
                return;
        }

        if (entity.getHealth() < threshold) {
            entity.setHealth((double)0.0F);
        } else {
            entity.setHealth(Math.max((double)0.0F, entity.getHealth() - damage));
        }
    }
}