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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import cn.charlotte.pit.util.backports.SWMRHashTable;

@ArmorOnly
public class MortalKombat extends AbstractEnchantment implements ITickTask, MovementHandler {

    private final Map<UUID, PositionSongPlayer> playerMap = new SWMRHashTable<>();
    private final Song music;

    @SneakyThrows
    public MortalKombat() {
        this.music = NBSDecoder.parse(ThePit.getInstance().getClass().getClassLoader().getResourceAsStream("MortalKombat.nbs"));
        startCleanupTask();
        registerMovementHandler();
    }

    private void startCleanupTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Set<Map.Entry<UUID, PositionSongPlayer>> entries = new HashSet<>(playerMap.entrySet());
                for (Map.Entry<UUID, PositionSongPlayer> entry : entries) {
                    Player player = Bukkit.getPlayer(entry.getKey());

                    if (player == null || !player.isOnline()) {
                        stopMusic(entry.getKey());
                        continue;
                    }

                    if (player.getInventory().getLeggings() == null || getItemEnchantLevel(player.getInventory().getLeggings()) == -1) {
                        stopMusic(entry.getKey());
                    }
                }
            }
        }.runTaskTimerAsynchronously(ThePit.getInstance(), 20L, 20L);
    }

    private void registerMovementHandler() {
        try {
            iSpigot.INSTANCE.addMovementHandler(this);
        } catch (NoClassDefFoundError ignore) {
        }
    }

    private void stopMusic(UUID playerId) {
        PositionSongPlayer songPlayer = playerMap.remove(playerId);
        if (songPlayer != null) {
            songPlayer.setPlaying(false);
        }
    }

    @Override
    public String getEnchantName() {
        return "DJ #10";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 1;
    }

    @Override
    public String getNbtName() {
        return "mortal_kombat_dj";
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
        return "&7此附魔只能通过 &e抽奖活动 &7获得." +
                "/s&7向周围的玩家播放音乐: &fMortal Kombat";
    }

    @Override
    public void handle(int enchantLevel, Player target) {
        PositionSongPlayer songPlayer = playerMap.get(target.getUniqueId());

        if (songPlayer == null) {
            PositionSongPlayer player = new PositionSongPlayer(this.music);
            player.setTargetLocation(target.getLocation());
            player.setAutoDestroy(false);
            player.setLoop(true);
            player.setPlaying(true);
            player.setVolume((byte) 0.05);

            this.playerMap.put(target.getUniqueId(), player);
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
        PositionSongPlayer songPlayer = this.playerMap.get(player.getUniqueId());
        if (songPlayer != null) {
            songPlayer.setTargetLocation(player.getLocation());
        }
    }

    @Override
    public void handleUpdateRotation(Player player, Location from, Location to, PacketPlayInFlying packet) {
    }
}