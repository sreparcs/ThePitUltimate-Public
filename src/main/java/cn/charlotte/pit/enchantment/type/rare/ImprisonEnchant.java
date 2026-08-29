package cn.charlotte.pit.enchantment.type.rare;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.IActionDisplayEnchant;
import cn.charlotte.pit.enchantment.param.item.BowOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import cn.charlotte.pit.register.IMagicLicense;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@BowOnly
public class ImprisonEnchant extends AbstractEnchantment implements Listener, IActionDisplayEnchant, IMagicLicense {
    private static final Map<UUID, Cooldown> cdMap = new HashMap();
    public static final Map<UUID, Long> imprisonMap = new HashMap();

    @Override
    public String getEnchantName() {
        return "禁锢";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "Imprison";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.REMOVED;
    }

    @Override
    public Cooldown getCooldown() {
        return new Cooldown(0L);
    }

    @Override
    public String getUsefulnessLore(int level) {
        String lore;
        switch (level) {
            case 1 -> lore = "&7命中玩家造成 &f1 &7秒钟的禁锢.无法移动 &7(25秒冷却)";
            case 2 -> lore = "&7命中玩家造成 &f2 &7秒钟的禁锢.无法移动 &7(20秒冷却)";
            case 3 -> lore = "&7命中玩家造成 &f3 &7秒钟的禁锢.无法移动 &7(15秒冷却)";
            default -> lore = "NULL";
        }
        return lore;
    }

    @Override
    public String getText(int level, Player player) {
        Cooldown cooldown = cdMap.getOrDefault(player.getUniqueId(), null);
        return cooldown == null ? this.getCooldownActionText(this.getCooldown()) + " " : this.getCooldownActionText(cooldown) + " ";
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMove(PlayerMoveEvent event) {
        long imprisonEndTime = imprisonMap.getOrDefault(event.getPlayer().getUniqueId(), 0L);
        if (System.currentTimeMillis() <= imprisonEndTime && !event.getFrom().getBlock().equals(event.getTo().getBlock()) && event.getFrom().getBlockY() <= event.getTo().getBlockY() && event.getFrom().getY() < event.getTo().getY() && event.getPlayer().getVelocity().getY() > 0.0F) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
            ProjectileSource shooter = ((Arrow)event.getDamager()).getShooter();
            if (shooter instanceof Player) {
                Player player = (Player)shooter;
                if (!PlayerUtil.shouldIgnoreEnchant(player, (Player)event.getEntity())) {
                    int level = this.getItemEnchantLevel(player.getItemInHand());
                    if (level != -1) {
                        this.applyImprison(player, (Player)event.getEntity(), level);
                    }
                }
            }
        }
    }

    private void applyImprison(Player player, final Player target, int level) {
        Cooldown cooldown = cdMap.getOrDefault(player.getUniqueId(), new Cooldown(0L));
        cdMap.put(player.getUniqueId(), cooldown);
        if (cooldown.hasExpired()) {
            int imprisonTime;
            switch (level) {
                case 1:
                    cooldown.setDuration(25000L);
                    imprisonTime = 1000;
                    break;
                case 2:
                    cooldown.setDuration(20000L);
                    imprisonTime = 2000;
                    break;
                case 3:
                    cooldown.setDuration(15000L);
                    imprisonTime = 3000;
                    break;
                default:
                    return;
            }

            cooldown.reset();
            target.setWalkSpeed(0.0F);
            String playerName = player.getName();
            target.sendMessage(CC.translate("§d§l禁锢! §7你被 §c" + playerName + " §7禁锢了 §f" + imprisonTime / 1000 + " §7秒"));
            imprisonMap.put(target.getUniqueId(), System.currentTimeMillis() + imprisonTime);
            new BukkitRunnable() {
                public void run() {
                    target.sendMessage(CC.translate("§d§l禁锢! §7§l禁锢解除!"));
                    target.setWalkSpeed(0.2F);
                    ImprisonEnchant.imprisonMap.remove(target.getUniqueId());
                }
            }.runTaskLater(ThePit.getInstance(), (long)(imprisonTime / 50));
        }
    }

    public static Map<UUID, Cooldown> getCdMap() {
        return cdMap;
    }
}