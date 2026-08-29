package cn.charlotte.pit.enchantment.type.rare;

import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.IActionDisplayEnchant;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.parm.listener.IPlayerDamaged;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@ArmorOnly
public class ComboLadder extends AbstractEnchantment implements IAttackEntity, IPlayerDamaged, IActionDisplayEnchant, Listener {
    @NotNull
    private final Map<UUID, Integer> comboLayers = new ConcurrentHashMap<>();

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        comboLayers.remove(e.getPlayer().getUniqueId());
    }

    @NotNull
    @Override
    public String getEnchantName() {
        return "连击高手";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @NotNull
    @Override
    public String getNbtName() {
        return "combo_ladder";
    }

    @NotNull
    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.RARE;
    }

    @Nullable
    @Override
    public Cooldown getCooldown() {
        return null;
    }

    @NotNull
    @Override
    public String getUsefulnessLore(int enchantLevel) {
        double attackBoost = (double) enchantLevel * 2.5F;
        return "&7每当你击中一个目标, 你的攻击力将会 &c+" + attackBoost + "% &7(上限&e10&7层) /s&7但同时, 若你被任意目标所攻击时, 你的层数将会清零";
    }

    @Override
    public void handleAttackEntity(int enchantLevel, @NotNull Player attacker, @NotNull Entity entity, double v, @NotNull AtomicDouble atomicDouble, @NotNull AtomicDouble boostDamage, @NotNull AtomicBoolean atomicBoolean) {
        if (entity instanceof Player) {
            UUID attackerUUID = attacker.getUniqueId();
            int layers = ((Number) comboLayers.getOrDefault(attackerUUID, 0)).intValue();
            if (layers < 10) {
                comboLayers.put(attackerUUID, layers + 1);
            }

            if (layers > 0) {
                boostDamage.getAndAdd((double) (layers * enchantLevel) * 0.025);
            }
        }
    }

    @Override
    public void handlePlayerDamaged(int enchantLevel, @NotNull Player victim, @NotNull Entity entity, double v, @NotNull AtomicDouble atomicDouble, @NotNull AtomicDouble atomicDouble1, @NotNull AtomicBoolean atomicBoolean) {
        if (entity instanceof Player) {
            comboLayers.put(victim.getUniqueId(), 0);
        }
    }

    @NotNull
    @Override
    public String getText(int enchantLevel, @NotNull Player player) {
        int layers = ((Number) comboLayers.getOrDefault(player.getUniqueId(), 0)).intValue();
        return CC.translate("&e层数: " + layers);
    }
}