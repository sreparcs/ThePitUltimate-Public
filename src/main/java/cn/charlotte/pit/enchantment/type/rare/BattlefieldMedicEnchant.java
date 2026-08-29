package cn.charlotte.pit.enchantment.type.rare;

import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.IActionDisplayEnchant;
import cn.charlotte.pit.enchantment.param.event.PlayerOnly;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IPlayerDamaged;
import cn.charlotte.pit.parm.listener.IPlayerKilledEntity;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.time.TimeUtil;
import nya.Skip;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
@Skip
@ArmorOnly
public class BattlefieldMedicEnchant extends AbstractEnchantment implements IPlayerKilledEntity, IPlayerDamaged, IActionDisplayEnchant, Listener {

    private static final HashMap<UUID, Cooldown> resistanceCooldown = new HashMap<>();
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        resistanceCooldown.remove(e.getPlayer().getUniqueId());
    }
    @Override
    public String getEnchantName() {
        return "战地医师";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "battlefield_medic_enchant";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.RARE;
    }

    @Override
    public Cooldown getCooldown() {
        return null;
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        double healAmount = 1.0 + enchantLevel * 0.5; // 1.5, 2.0, 2.5 hearts
        int resistanceChance = enchantLevel * 15 + 10; // 25%, 40%, 55%
        return "&7击杀敌人时恢复 &c" + healAmount + "❤ &7生命值" +
                "/s&7受到伤害时有 &e" + resistanceChance + "% &7的概率获得" +
                "/s&3抗性提升 " + (enchantLevel == 1 ? "I" : enchantLevel == 2 ? "II" : "III") +
                " &f(00:0" + (enchantLevel + 2) + ") &7(8秒冷却)";
    }

    @Override
    public String getText(int level, Player player) {
        return resistanceCooldown.getOrDefault(player.getUniqueId(), new Cooldown(0)).hasExpired() ?
                "&a&l✔" : "&c&l" + TimeUtil.millisToRoundedTime(resistanceCooldown.getOrDefault(player.getUniqueId(), new Cooldown(0)).getRemaining()).replace(" ", "");
    }

    @Override
    @PlayerOnly
    public void handlePlayerKilled(int enchantLevel, Player killer, Entity target, AtomicDouble coin, AtomicDouble exp) {
        if (target.hasMetadata("NPC") || target.hasMetadata("Bot")) {
            return;
        }
        double healAmount = (1.0 + enchantLevel * 0.5) * 2;
        PlayerUtil.heal(killer, healAmount);

        killer.playSound(killer.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.5f);
    }

    @Override
    public void handlePlayerDamaged(int enchantLevel, Player myself, Entity attacker, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        if (resistanceCooldown.getOrDefault(myself.getUniqueId(), new Cooldown(0)).hasExpired()) {
            int resistanceChance = enchantLevel * 15 + 10;
            if (Math.random() * 100 < resistanceChance) {

                myself.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, (enchantLevel + 2) * 20, enchantLevel - 1), true);


                myself.playSound(myself.getLocation(), Sound.ANVIL_USE, 0.8f, 1.2f);

                resistanceCooldown.put(myself.getUniqueId(), new Cooldown(8, TimeUnit.SECONDS));
            }
        }
    }
} 