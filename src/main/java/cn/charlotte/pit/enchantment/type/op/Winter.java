package cn.charlotte.pit.enchantment.type.op;

import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.IActionDisplayEnchant;
import cn.charlotte.pit.enchantment.param.item.WeaponOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import cn.charlotte.pit.register.IMagicLicense;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@WeaponOnly
public class Winter extends AbstractEnchantment implements IMagicLicense, IAttackEntity, Listener, IActionDisplayEnchant {
    private static final String WINTER_FRAIL = "WinterFrail";
    private static final HashMap<UUID, Cooldown> cooldown = new HashMap();
    private static final org.bukkit.plugin.Plugin PLUGIN = org.bukkit.plugin.java.JavaPlugin.getProvidingPlugin(Winter.class);

    @Override
    public String getEnchantName() {
        return "凛冬";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "winter";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.OP;
    }

    @Override
    public Cooldown getCooldown() {
        return null;
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        return "&7攻击目标时将对其施加: /s   &f▶ &b霜寒 &7(00:0" + (enchantLevel + 1) + ") /s   &f▶ &8脆弱 &7(00:0" + (enchantLevel * 2 + 2) + ") /s&7效果 &b霜寒&7: 移速大幅降低, 对他人造成的伤害 &9-20% /s&7效果 &8脆弱&7: 受到的暴击伤害 &c+20% /s" + (enchantLevel >= 3 ? "&7同时, 攻击目标时将清除自身的 &c缓慢 &7效果 /s&8每秒只可触发一次" : "&8每秒只可触发一次");
    }

    @Override
    public void handleAttackEntity(int enchantLevel, Player player, Entity target, double v, AtomicDouble atomicDouble, AtomicDouble atomicDouble1, AtomicBoolean atomicBoolean) {
        if (target instanceof Player && ((Cooldown)cooldown.getOrDefault(player.getUniqueId(), new Cooldown(0L))).hasExpired()) {
            cooldown.put(player.getUniqueId(), new Cooldown(1L, TimeUnit.SECONDS));
            Player targetPlayer = (Player)target;

            for(PotionEffect potionEffect : targetPlayer.getActivePotionEffects()) {
                if (potionEffect.getType().equals(PotionEffectType.SLOW)) {
                    int level = potionEffect.getAmplifier();
                    if (level >= 1) {
                        return;
                    }

                    targetPlayer.removePotionEffect(potionEffect.getType());
                }
            }

            targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (enchantLevel + 1) * 20, 1, false, true));
            if (targetPlayer.hasMetadata("WinterFrail")) {
                targetPlayer.removeMetadata("WinterFrail", PLUGIN);
            }

            targetPlayer.setMetadata("WinterFrail", new FixedMetadataValue(PLUGIN, System.currentTimeMillis() + ((long)enchantLevel * 2L + 2L) * 1000L));
            if (enchantLevel >= 3 && player.hasPotionEffect(PotionEffectType.SLOW)) {
                player.removePotionEffect(PotionEffectType.SLOW);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getDamager().hasMetadata("WinterFrail") && ((MetadataValue)event.getDamager().getMetadata("WinterFrail").get(0)).asLong() > System.currentTimeMillis()) {
            event.setDamage(event.getDamage() * 0.8);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onProtect(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getEntity().hasMetadata("WinterFrail") && ((MetadataValue)event.getEntity().getMetadata("WinterFrail").get(0)).asLong() > System.currentTimeMillis()) {
            event.setDamage(event.getDamage() * 1.2);
        }
    }

    @Override
    public String getText(int i, Player player) {
        return this.getCooldownActionText((Cooldown)cooldown.getOrDefault(player.getUniqueId(), new Cooldown(0L)));
    }
}