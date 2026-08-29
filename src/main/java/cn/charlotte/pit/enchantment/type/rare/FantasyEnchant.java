package cn.charlotte.pit.enchantment.type.rare;

import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.IActionDisplayEnchant;
import cn.charlotte.pit.enchantment.param.event.PlayerOnly;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IPlayerDamaged;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.random.RandomUtil;
import cn.charlotte.pit.util.time.TimeUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@ArmorOnly
public class FantasyEnchant extends AbstractEnchantment implements IPlayerDamaged, IActionDisplayEnchant, Listener {
    private final HashMap<UUID, Cooldown> cooldown = new HashMap();

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        cooldown.remove(e.getPlayer().getUniqueId());
    }

    public String getEnchantName() {
        return "幻像";
    }

    public int getMaxEnchantLevel() {
        return 3;
    }

    public String getNbtName() {
        return "fantasy";
    }

    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.RARE;
    }

    public Cooldown getCooldown() {
        return new Cooldown(5L, TimeUnit.SECONDS);
    }

    private static double getChance(int enchantLevel) {
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

    public String getUsefulnessLore(int i) {
        return "&7受到伤害时有 &b" + (double)100.0F * getChance(i) + "% &7的概率无视普通伤害并反弹给攻击者. (5秒冷却)";
    }

    @PlayerOnly
    public void handlePlayerDamaged(int i, Player player, Entity entity, double v, AtomicDouble atomicDouble, AtomicDouble atomicDouble1, AtomicBoolean atomicBoolean) {
        if (RandomUtil.hasSuccessfullyByChance(getChance(i)) && ((Cooldown)this.cooldown.getOrDefault(player.getUniqueId(), new Cooldown(0L))).hasExpired()) {
            this.cooldown.put(player.getUniqueId(), this.getCooldown());
            atomicBoolean.set(true);
            player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
            player.sendMessage(CC.translate("&a你的附魔反弹了对方的攻击!!"));
            entity.sendMessage(CC.translate("&c对方的附魔反弹了你的攻击!"));
            ((Player)entity).damage((double)2.0F * atomicDouble.get());
        }
    }

    public String getText(int level, Player player) {
        return ((Cooldown)this.cooldown.getOrDefault(player.getUniqueId(), new Cooldown(0L))).hasExpired() ? "&a&l✔" : "&c&l" + TimeUtil.millisToRoundedTime(((Cooldown)this.cooldown.get(player.getUniqueId())).getRemaining()).replace(" ", "") + " ";
    }
}