package cn.charlotte.pit.enchantment.type.rare;

import cn.charlotte.pit.ThePit;
import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.IActionDisplayEnchant;
import cn.charlotte.pit.enchantment.param.item.WeaponOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.time.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@WeaponOnly
public class ShockerEnchant extends AbstractEnchantment implements IActionDisplayEnchant, IAttackEntity, Listener {
    private static final HashMap<UUID, Cooldown> COOLDOWN = new HashMap();

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        COOLDOWN.remove(e.getPlayer().getUniqueId());
    }

    public String getEnchantName() {
        return "震撼";
    }

    public int getMaxEnchantLevel() {
        return 3;
    }

    public String getNbtName() {
        return "Shocker";
    }

    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.RARE;
    }

    @Nullable
    public Cooldown getCooldown() {
        return new Cooldown(5L, TimeUnit.SECONDS);
    }

    public String getUsefulnessLore(int enchantLevel) {
        return "&7使用这把剑攻击将会使目标向后击退一段距离, &7(5秒冷却)";
    }

    public void handleAttackEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        if (((Cooldown)COOLDOWN.getOrDefault(attacker.getUniqueId(), new Cooldown(0L))).hasExpired() && !PlayerUtil.isVenom(attacker) && !PlayerUtil.isVenom((Player)target) && !PlayerUtil.isEquippingSomber(attacker) && !PlayerUtil.isEquippingSomber((Player)target)) {
            Bukkit.getScheduler().runTaskLater(ThePit.getInstance(), () -> {
                Vector dir = attacker.getLocation().getDirection();
                Vector vec = new Vector(dir.getX(), 0.2, dir.getZ());
                target.setVelocity(vec);
            }, 1L);
            COOLDOWN.put(attacker.getUniqueId(), this.getCooldown());
            attacker.sendMessage(CC.translate("&b&l震撼! &7你的附魔将对方击退了！"));
            target.sendMessage(CC.translate("&b&l震撼! &7对方的附魔将你击退了！"));
        }
    }

    public String getText(int level, Player player) {
        return ((Cooldown)COOLDOWN.getOrDefault(player.getUniqueId(), new Cooldown(0L))).hasExpired() ? "&a&l✔" : "&c&l" + TimeUtil.millisToRoundedTime(((Cooldown)COOLDOWN.get(player.getUniqueId())).getRemaining()).replace(" ", " ");
    }
}