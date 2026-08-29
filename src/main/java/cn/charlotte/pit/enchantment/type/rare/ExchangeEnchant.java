package cn.charlotte.pit.enchantment.type.rare;

import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.IActionDisplayEnchant;
import cn.charlotte.pit.enchantment.param.item.BowOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IPlayerShootEntity;
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

@BowOnly
public class ExchangeEnchant extends AbstractEnchantment implements IPlayerShootEntity, IActionDisplayEnchant, Listener {
    private static final HashMap<UUID, Cooldown> COOLDOWN = new HashMap();

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        COOLDOWN.remove(e.getPlayer().getUniqueId());
    }

    public String getEnchantName() {
        return "置换";
    }

    public int getMaxEnchantLevel() {
        return 3;
    }

    public String getNbtName() {
        return "ST_LocationEX";
    }

    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.RARE;
    }

    @Nullable
    public Cooldown getCooldown() {
        return null;
    }

    public String getUsefulnessLore(int i) {
        return "&7箭矢命中时互换你与目标的位置./s&7(" + (30 - 5 * i) + "秒冷却)";
    }

    public void handleShootEntity(int i, Player player, Entity entity, double v, AtomicDouble atomicDouble, AtomicDouble atomicDouble1, AtomicBoolean atomicBoolean) {
        if (((Cooldown)COOLDOWN.getOrDefault(player.getUniqueId(), new Cooldown(0L))).hasExpired()) {
            COOLDOWN.put(player.getUniqueId(), new Cooldown(30L - 5L * (long)i, TimeUnit.SECONDS));
            Location myLoc = player.getLocation();
            Location targetLoc = entity.getLocation();
            player.teleport(targetLoc);
            entity.teleport(myLoc);
            player.sendMessage(CC.translate("&a&l玄冥置换! &7你的位置已经和对方互换!"));
            entity.sendMessage(CC.translate("&a&l玄冥置换! &7对方的附魔互换了你们的位置!"));
        }
    }

    public String getText(int level, Player player) {
        return ((Cooldown)COOLDOWN.getOrDefault(player.getUniqueId(), new Cooldown(0L))).hasExpired() ? "&a&l✔" : "&c&l" + TimeUtil.millisToRoundedTime(((Cooldown)COOLDOWN.get(player.getUniqueId())).getRemaining()).replace(" ", "") + " ";
    }
}