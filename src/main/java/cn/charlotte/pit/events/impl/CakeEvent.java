package cn.charlotte.pit.events.impl;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.events.AbstractEvent;
import cn.charlotte.pit.events.trigger.type.INormalEvent;
import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.util.TaskManager;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.regions.CuboidRegion;
import cn.charlotte.pit.config.NewConfiguration;
import cn.charlotte.pit.config.PitWorldConfig;
import cn.charlotte.pit.medal.impl.challenge.CakeEventMedal;
import cn.charlotte.pit.util.LocationUtil;
import cn.charlotte.pit.util.aabb.AABB;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.chat.MessageType;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.random.RandomUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Author: EmptyIrony
 * @Date: 2021/2/5 23:37
 */
public class CakeEvent extends AbstractEvent implements INormalEvent, Listener {

    private static final int CLEANUP_BLOCKS_PER_TICK = 4096;
    private static volatile BukkitRunnable runningCleanup;

    private final DecimalFormat numFormatTwo = new DecimalFormat("0.00");
    private final DecimalFormat df = new DecimalFormat(",###,###,###,###");
    private volatile EditSession session;
    private volatile AABB alignedBB;
    private volatile Map<UUID, CakePlayerData> dataCache;
    private volatile CuboidRegion activeRegion;

    @Override
    public String getEventInternalName() {
        return "cake";
    }

    @Override
    public String getEventName() {
        return "&d蛋糕争夺战";
    }

    @Override
    public int requireOnline() {
        return NewConfiguration.INSTANCE.getEventOnlineRequired().get(getEventInternalName());
    }

    private CuboidRegion[] buildRegions() {
        final PitWorldConfig config = ThePit.getInstance().getPitConfig();
        final Location[][] pairs = new Location[][]{
                {config.getCakeZoneAPosA(), config.getCakeZoneAPosB()},
                {config.getCakeZoneBPosA(), config.getCakeZoneBPosB()},
                {config.getCakeZoneCPosA(), config.getCakeZoneCPosB()},
                {config.getCakeZoneDPosA(), config.getCakeZoneDPosB()}
        };

        int count = 0;
        for (Location[] pair : pairs) {
            if (pair[0] != null && pair[1] != null) {
                count++;
            }
        }

        if (count == 0) {
            return null;
        }

        final CuboidRegion[] built = new CuboidRegion[count];
        int index = 0;
        for (Location[] pair : pairs) {
            if (pair[0] != null && pair[1] != null) {
                built[index++] = new CuboidRegion(BukkitUtil.toVector(pair[0]), BukkitUtil.toVector(pair[1]));
            }
        }
        return built;
    }

    @Override
    public void onActive() {
        final CuboidRegion[] zones = buildRegions();
        if (zones == null) {
            CC.boardCast(MessageType.EVENT, "&d蛋糕! &7蛋糕区域未设置, 请联系管理员!");
            deactivateLater();
            return;
        }

        cancelRunningCleanup();

        CC.boardCast(MessageType.EVENT, "&d蛋糕! &7巨型蛋糕已生成在地图上，吃掉蛋糕获得大量金币和经验!");

        this.dataCache = new HashMap<>();

        final CuboidRegion region = zones[RandomUtil.random.nextInt(zones.length)];
        final Vector pos1 = region.getPos1();
        final Vector pos2 = region.getPos2();
        this.alignedBB = new AABB(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ());
        this.activeRegion = region;

        TaskManager.IMP.async(() -> {
            try {
                BukkitWorld world = new BukkitWorld(Bukkit.getWorlds().get(0));
                this.session = FaweAPI.getEditSessionBuilder(world).build();

                final RandomPattern pattern = new RandomPattern();
                pattern.add(new BaseBlock(BlockID.CAKE_BLOCK), 0.94);
                pattern.add(new BaseBlock(BlockID.STAINED_CLAY, 2), 0.03);
                pattern.add(new BaseBlock(BlockID.STAINED_CLAY, 15), 0.03);

                session.setBlocks(region, pattern);

                session.flushQueue();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });

        Bukkit.getPluginManager()
                .registerEvents(this, ThePit.getInstance());


    }

    private void deactivateLater() {
        final ThePit plugin = ThePit.getInstance();
        if (!plugin.isEnabled()) {
            return;
        }
        Bukkit.getScheduler().runTask(plugin, () -> plugin.getEventFactory().inactiveEvent(this));
    }

    @Override
    public void onInactive() {
        HandlerList.unregisterAll(this);

        final EditSession current = this.session;
        this.session = null;
        if (current != null) {
            try {
                TaskManager.IMP.async(() -> {
                    try {
                        current.undo(current);
                        current.flushQueue();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                });
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        final CuboidRegion region = this.activeRegion;
        this.activeRegion = null;
        if (region != null) {
            clearRemainingCake(region);
        }

        if (this.dataCache != null) {
            for (UUID uuid : this.dataCache.keySet()) {
                if (this.dataCache.get(uuid).coins >= 5000 && Bukkit.getPlayer(uuid) != null) {
                    final PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(uuid);
                    if (profile != null) {
                        new CakeEventMedal().addProgress(profile, 1);
                    }
                }
            }
            this.dataCache = null;
        }
    }

    private void clearRemainingCake(CuboidRegion region) {
        final World world = Bukkit.getWorlds().get(0);
        final Vector pos1 = region.getPos1();
        final Vector pos2 = region.getPos2();

        final int minX = (int) Math.floor(Math.min(pos1.getX(), pos2.getX()));
        final int maxX = (int) Math.floor(Math.max(pos1.getX(), pos2.getX()));
        final int minY = Math.max(0, (int) Math.floor(Math.min(pos1.getY(), pos2.getY())));
        final int maxY = Math.min(world.getMaxHeight() - 1, (int) Math.floor(Math.max(pos1.getY(), pos2.getY())));
        final int minZ = (int) Math.floor(Math.min(pos1.getZ(), pos2.getZ()));
        final int maxZ = (int) Math.floor(Math.max(pos1.getZ(), pos2.getZ()));

        cancelRunningCleanup();

        if (!ThePit.getInstance().isEnabled()) {
            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        clearCakeBlock(world, x, y, z);
                    }
                }
            }
            return;
        }

        final BukkitRunnable task = new BukkitRunnable() {
            private int x = minX;
            private int y = minY;
            private int z = minZ;

            @Override
            public void run() {
                int processed = 0;
                while (processed < CLEANUP_BLOCKS_PER_TICK) {
                    if (x > maxX) {
                        if (runningCleanup == this) {
                            runningCleanup = null;
                        }
                        cancel();
                        return;
                    }

                    clearCakeBlock(world, x, y, z);
                    processed++;

                    z++;
                    if (z > maxZ) {
                        z = minZ;
                        y++;
                        if (y > maxY) {
                            y = minY;
                            x++;
                        }
                    }
                }
            }
        };

        runningCleanup = task;
        task.runTaskTimer(ThePit.getInstance(), 20L, 1L);
    }

    private static void cancelRunningCleanup() {
        final BukkitRunnable previous = runningCleanup;
        runningCleanup = null;
        if (previous == null) {
            return;
        }
        try {
            previous.cancel();
        } catch (Throwable ignored) {
        }
    }

    private void clearCakeBlock(World world, int x, int y, int z) {
        final Block block = world.getBlockAt(x, y, z);
        final Material type = block.getType();
        if (type == Material.CAKE_BLOCK) {
            block.setType(Material.AIR);
            return;
        }
        if (type == Material.STAINED_CLAY) {
            final byte data = block.getData();
            if (data == 2 || data == 15) {
                block.setType(Material.AIR);
            }
        }
    }

    @EventHandler
    public void onEatCake(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (this.alignedBB == null || this.dataCache == null) {
            return;
        }

        final Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        final Location location = block.getLocation();
        final AABB blockAABB = new AABB(location.getX() - 1, location.getY() - 1, location.getZ() - 1, location.getX() + 1, location.getY() + 1, location.getZ() + 1);
        final Player player = event.getPlayer();
        final PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());

        if (profile == null) {
            return;
        }

        if (alignedBB.intersectsWith(blockAABB)) {
            CakePlayerData cakePlayerData = dataCache.get(player.getUniqueId());
            if (cakePlayerData == null) {
                cakePlayerData = new CakePlayerData();
                cakePlayerData.uuid = player.getUniqueId();
                cakePlayerData.name = player.getDisplayName();
                cakePlayerData.cooldown = new Cooldown(100);

                dataCache.put(player.getUniqueId(), cakePlayerData);
            }

            if (!cakePlayerData.cooldown.hasExpired()) {
                return;
            }

          //  if (!canEat(block.getLocation())) {
           //     CC.send(MessageType.EVENT, player, "&d&l蛋糕! &c你不能吃里面的蛋糕! 请先吃掉周围的蛋糕!");
           //     return;
          //  }

            cakePlayerData.cooldown = new Cooldown(100);

            if (block.getType() == Material.CAKE_BLOCK) {
                int baseCoins = 1;

                if (block.getData() == 6) {
                    baseCoins += 5;
                    block.setType(Material.AIR);
                } else {
                    block.setData((byte) (block.getData() + 1));
                }
                block.getState().update();

                // final double totalCoins = baseCoins + baseCoins * i * 0.25D;
                final double totalCoins = baseCoins;

                cakePlayerData.clicked++;
                cakePlayerData.coins += totalCoins;

                profile.grindCoins(totalCoins);
                profile.setCoins(profile.getCoins() + totalCoins);

                player.playSound(player.getLocation(), Sound.EAT, 1, 1);

                CC.send(MessageType.EVENT, player, "&d&l蛋糕! &6+" + totalCoins + "金币 &d" + cakePlayerData.clicked + "次吃蛋糕" + " &6(" + numFormatTwo.format(cakePlayerData.coins) + "金币在这次事件中)");

            } else if (block.getType() == Material.STAINED_CLAY) {
                final byte data = block.getState().getData().getData();
                if (data == 2) {
                    block.setType(Material.AIR);

                    double totalCoins = 20;
                    cakePlayerData.clicked++;
                    cakePlayerData.coins += totalCoins;

                    profile.grindCoins(totalCoins);
                    profile.setCoins(profile.getCoins() + totalCoins);

                    player.playSound(player.getLocation(), Sound.EAT, 1, 1);

                    CC.send(MessageType.EVENT, player, "&d&l蛋糕! &7吃下了 &c樱桃 &6+20金币&7!");
                } else if (data == 15) {
                    block.setType(Material.AIR);

                    double totalXp = 20;
                    cakePlayerData.clicked++;
                    cakePlayerData.coins += totalXp;

                    profile.setExperience(profile.getExperience() + totalXp);
                    profile.applyExperienceToPlayer(player);

                    player.playSound(player.getLocation(), Sound.EAT, 1, 1);

                    CC.send(MessageType.EVENT, player, "&d&l蛋糕! &7吃下了 &4巧克力 &b+20经验&7!");
                }
            }
        }

    }

    private boolean canEat(Location location) {
        for (Location face : LocationUtil.getFaces(location)) {
            if (face.getBlock().getType() != Material.CAKE_BLOCK && face.getBlock().getType() != Material.STAINED_CLAY) {
                return true;
            }
        }
        return false;
    }

    public static class CakePlayerData {

        private UUID uuid;
        private String name;
        private int coins;
        private int clicked;
        private Cooldown cooldown;
    }
}
