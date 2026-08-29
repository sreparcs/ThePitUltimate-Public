package cn.charlotte.pit.enchantment.type.rare;

import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.IActionDisplayEnchant;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.param.item.WeaponOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.time.TimeUtil;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@ArmorOnly
@WeaponOnly
public class LocExchange extends AbstractEnchantment implements IAttackEntity, IActionDisplayEnchant, Listener {
    private static final HashMap<UUID, Cooldown> COOLDOWN = new HashMap();

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        COOLDOWN.remove(e.getPlayer().getUniqueId());
    }

    public String getEnchantName() {
        return "设身处地";
    }

    public int getMaxEnchantLevel() {
        return 3;
    }

    public String getNbtName() {
        return "Health_LocExchangeEnch";
    }

    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.RARE;
    }

    @Nullable
    public Cooldown getCooldown() {
        return null;
    }

    public String getUsefulnessLore(int i) {
        return "&7潜行攻击目标时将互换你与对方的位置、血量/s(" + (30 - 5 * i) + "秒冷却)";
    }

    public void handleAttackEntity(int i, Player player, Entity entity, double v, AtomicDouble atomicDouble, AtomicDouble atomicDouble1, AtomicBoolean atomicBoolean) {
        if (((Cooldown)COOLDOWN.getOrDefault(player.getUniqueId(), new Cooldown(0L))).hasExpired() && player.isSneaking()) {
            COOLDOWN.put(player.getUniqueId(), new Cooldown(30L - 5L * (long)i, TimeUnit.SECONDS));
            Location myLoc = player.getLocation();
            double myHealth = player.getHealth();
            Location targetLoc = entity.getLocation();
            double targetHealth = ((Player)entity).getHealth();
            player.teleport(targetLoc);
            player.setHealth(targetHealth);
            entity.teleport(myLoc);
            ((Player)entity).setHealth(myHealth);
            player.sendMessage(CC.translate("&6&l设身处地! &7你的血量、位置已经和对方互换!"));
            entity.sendMessage(CC.translate("&6&l设身处地! &7对方的附魔互换了你们的血量、位置!"));
        }
    }

    public String getText(int level, Player player) {
        return ((Cooldown)COOLDOWN.getOrDefault(player.getUniqueId(), new Cooldown(0L))).hasExpired() ? "&a&l✔" : "&c&l" + TimeUtil.millisToRoundedTime(((Cooldown)COOLDOWN.get(player.getUniqueId())).getRemaining()).replace(" ", "") + " ";
    }
}