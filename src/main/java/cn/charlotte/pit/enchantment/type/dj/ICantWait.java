package cn.charlotte.pit.enchantment.type.dj;

import cn.charlotte.pit.ThePit;
import com.google.common.util.concurrent.AtomicDouble;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.parm.listener.IPlayerDamaged;
import cn.charlotte.pit.parm.listener.ITickTask;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.music.NBSDecoder;
import cn.charlotte.pit.util.music.PositionSongPlayer;
import cn.charlotte.pit.util.music.Song;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import cn.charlotte.pit.movement.MovementHandler;
import cn.charlotte.pit.movement.iSpigot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@ArmorOnly
public class ICantWait extends AbstractEnchantment implements ITickTask, MovementHandler, IAttackEntity, IPlayerDamaged {
    private final Map<UUID, PositionSongPlayer> playerMap = new HashMap<>();
    private final Song music;

    @SneakyThrows
    public ICantWait() {
        this.music = NBSDecoder.parse(ThePit.getInstance().getClass().getClassLoader().getResourceAsStream("ICantWait.nbs"));

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, PositionSongPlayer> entry : new HashSet<>(playerMap.entrySet())) {
                    Player player = Bukkit.getPlayer(entry.getKey());
                    if (player == null || !player.isOnline()) {
                        PositionSongPlayer remove = playerMap.remove(entry.getKey());
                        remove.setPlaying(false);
                        continue;
                    }
                    if (player.getInventory().getLeggings() == null || getItemEnchantLevel(player.getInventory().getLeggings()) == -1) {
                        PositionSongPlayer remove = playerMap.remove(entry.getKey());
                        remove.setPlaying(false);
                    }
                }
            }
        }.runTaskTimerAsynchronously(ThePit.getInstance(), 20L, 20L);

        try {
            iSpigot.INSTANCE.addMovementHandler(this);
        } catch (NoClassDefFoundError ignore) {
        }
    }

    @Override
    public String getEnchantName() {
        return "DJ #16";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 1;
    }

    @Override
    public String getNbtName() {
        return "ICantWait_dj";
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
        return "&7此附魔只能通过 &e抽奖活动 &7获得. /s&7向周围的玩家播放音乐: &cI Can't Wait./s&7同时, 自身伤害 &c+8%&7, 受击伤害 &9-8%";
    }

    @Override
    public void handle(int enchantLevel, Player target) {
        PositionSongPlayer songPlayer = playerMap.get(target.getUniqueId());
        if (songPlayer == null) {
            PositionSongPlayer player = new PositionSongPlayer(music);
            player.setTargetLocation(target.getLocation());
            player.setAutoDestroy(false);
            player.setLoop(true);
            player.setPlaying(true);
            player.setVolume((byte) 0.05);
            playerMap.put(target.getUniqueId(), player);
        } else {
            target.getWorld().playEffect(target.getLocation().clone().add(0, 3, 0), Effect.NOTE, 1);
        }
    }

    @Override
    public int loopTick(int enchantLevel) {
        return 10;
    }

    @Override
    public void handleUpdateLocation(Player player, Location location, Location location1, PacketPlayInFlying packetPlayInFlying) {
        PositionSongPlayer songPlayer = this.playerMap.get(player.getUniqueId());
        if (songPlayer != null) {
            songPlayer.setTargetLocation(player.getLocation());
        }
    }

    @Override
    public void handleUpdateRotation(Player player, Location location, Location location1, PacketPlayInFlying packetPlayInFlying) {
    }

    @Override
    public void handleAttackEntity(int enchantLevel, Player attacker, Entity entity, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        boostDamage.getAndAdd(0.08);
    }

    @Override
    public void handlePlayerDamaged(int enchantLevel, Player player, Entity entity, double damage, AtomicDouble finalDamage, AtomicDouble reduceDamage, AtomicBoolean cancel) {
        reduceDamage.set(reduceDamage.get() * 0.92);
    }
}