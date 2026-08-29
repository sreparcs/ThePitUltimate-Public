package cn.charlotte.pit.enchantment.type.dj;

import cn.charlotte.pit.ThePit;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.ITickTask;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.music.NBSDecoder;
import cn.charlotte.pit.util.music.PositionSongPlayer;
import cn.charlotte.pit.util.music.Song;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import cn.charlotte.pit.movement.MovementHandler;
import cn.charlotte.pit.movement.iSpigot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

@ArmorOnly
public class BadApple extends AbstractEnchantment implements ITickTask, MovementHandler {
    private final Map<UUID, PositionSongPlayer> playerMap = new HashMap<>();
    private final Song music;

    @SneakyThrows // 简化异常处理，与原版风格一致
    public BadApple() {
        this.music = NBSDecoder.parse(this.getClass().getClassLoader().getResourceAsStream("BadApple.nbs"));
        new BukkitRunnable() {
            @Override
            public void run() {

                for (Map.Entry<UUID, PositionSongPlayer> entry : new HashSet<>(playerMap.entrySet())) {
                    Player player = Bukkit.getPlayer(entry.getKey());
                    if (player == null || !player.isOnline()) {

                        PositionSongPlayer remove = playerMap.remove(entry.getKey());
                        if (remove != null) remove.setPlaying(false);
                        continue;
                    }
                    if (player.getInventory().getLeggings() == null || getItemEnchantLevel(player.getInventory().getLeggings()) == -1) {
                        PositionSongPlayer remove = playerMap.remove(entry.getKey());
                        if (remove != null) remove.setPlaying(false);
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
        return "DJ #8";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 1;
    }

    @Override
    public String getNbtName() {
        return "bad_apple_dj";
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
        return "&7此附魔只能通过 &e抽奖活动 &7获得."
                + "/s&7向周围的玩家播放音乐: &fBad Apple";
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
    public void handleUpdateLocation(Player player, Location from, Location to, PacketPlayInFlying packet) {
        PositionSongPlayer songPlayer = playerMap.get(player.getUniqueId());
        if (songPlayer != null) {
            songPlayer.setTargetLocation(player.getLocation());
        }
    }

    @Override
    public void handleUpdateRotation(Player player, Location from, Location to, PacketPlayInFlying packet) {
    }
}