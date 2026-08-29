package cn.charlotte.pit.enchantment.type.op;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.PlayerProfile;
import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.IActionDisplayEnchant;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.parm.listener.IPlayerDamaged;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import cn.charlotte.pit.register.IMagicLicense;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@ArmorOnly
public class DoomPact extends AbstractEnchantment implements IMagicLicense, IAttackEntity, IPlayerDamaged, Listener, IActionDisplayEnchant {
    private static final HashMap<UUID, UUID> lockPlayer = new HashMap();

    @Override
    public String getEnchantName() {
        return "厄兆同契";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "doom_pact";
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
        return "&7命中 &f非Bot &7目标时将锁定该目标, 并将此后受到的伤害作为 &c普通伤害 &7分出 &c" + (10 + enchantLevel * 5) + "% &7施加给锁定目标, 同时, 自身恢复 &c0.75❤ /s&7(自身死亡后取消锁定)";
    }

    @Override
    public void handleAttackEntity(int i, Player attacker, Entity entity, double v, AtomicDouble atomicDouble, AtomicDouble atomicDouble1, AtomicBoolean atomicBoolean) {
        if (entity instanceof Player && !entity.hasMetadata("NPC") && lockPlayer.get(attacker.getUniqueId()) == null) {
            lockPlayer.put(attacker.getUniqueId(), entity.getUniqueId());
        }
    }

    @Override
    public void handlePlayerDamaged(int enchantLevel, Player victim, Entity entity, double damage, AtomicDouble atomicDouble, AtomicDouble finalDamage, AtomicBoolean atomicBoolean) {
        if (lockPlayer.get(victim.getUniqueId()) != null) {
            Player target = Bukkit.getPlayer((UUID)lockPlayer.get(victim.getUniqueId()));
            if (target != null) {
                PlayerProfile tp = PlayerProfile.getPlayerProfileByUuid(target.getUniqueId());
                if (tp.isInArena()) {
                    PlayerUtil.heal(victim, (double)1.5F);
                    double gotDamage = damage * ((double)1.0F - (0.1 + (double)enchantLevel * 0.05));
                    target.damage(gotDamage);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (ThePit.getApi().getItemEnchantLevel(player.getInventory().getLeggings(), this.getNbtName()) >= 1 && lockPlayer.get(player.getUniqueId()) != null) {
            lockPlayer.remove(player.getUniqueId());
        }
    }

    @Override
    public String getText(int i, Player player) {
        Player target = Bukkit.getPlayer((UUID)lockPlayer.get(player.getUniqueId()));
        return target == null ? CC.translate("&c无目标") : CC.translate("&e锁定: " + target.getDisplayName());
    }
}