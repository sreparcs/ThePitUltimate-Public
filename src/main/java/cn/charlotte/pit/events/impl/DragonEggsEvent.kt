package cn.charlotte.pit.events.impl

import cn.charlotte.pit.ThePit
import cn.charlotte.pit.data.PlayerProfile
import cn.charlotte.pit.events.AbstractEvent
import cn.charlotte.pit.events.trigger.type.INormalEvent
import cn.charlotte.pit.util.hologram.Hologram
import cn.charlotte.pit.util.hologram.HologramAPI
import cn.charlotte.pit.config.NewConfiguration.eventOnlineRequired
import cn.charlotte.pit.util.chat.CC
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import java.util.*

class DragonEggsEvent : AbstractEvent(), INormalEvent, Listener {
    @Volatile
    private var eggLocation: Location? = null
    private var clicks: Int = 0
    private var firstHologram: Hologram? = null
    private var secondHologram: Hologram? = null

    @Volatile
    private var running = false

    @Volatile
    private var clickable = false

    @Volatile
    private var hologramsReady = false

    @Volatile
    private var generation = 0
    private val placedEggs = HashSet<Location>()

    companion object {
        private const val MAX_CLICKS = 230
        private const val CLICK_THRESHOLD = 50
        private const val SEARCH_RADIUS = 10
        private const val MAX_ATTEMPTS = 20
    }

    override fun getEventInternalName(): String = "dragon_egg"

    override fun getEventName(): String = "&5龙蛋"

    override fun requireOnline(): Int = eventOnlineRequired[eventInternalName]!!

    private fun registerEvents() {
        Bukkit.getPluginManager().registerEvents(this, ThePit.getInstance())
    }

    private fun unregisterEvents() {
        HandlerList.unregisterAll(this)
    }

    private fun clearPlacedEggs() {
        if (placedEggs.isEmpty()) {
            return
        }
        for (location in placedEggs) {
            try {
                val world = location.world ?: continue
                val block = world.getBlockAt(location)
                if (block.type == Material.DRAGON_EGG) {
                    block.type = Material.AIR
                }
            } catch (e: Exception) {
                Bukkit.getLogger().warning("清理旧龙蛋失败: ${e.message}")
            }
        }
        placedEggs.clear()
    }

    override fun onActive() {
        val origin = ThePit.getInstance().pitConfig.dragonEggLoc ?: run {
            Bukkit.broadcastMessage(CC.translate("&5&l龙蛋！ &7活动区域未设置，请联系管理员设置！"))
            deactivateLater()
            return
        }
        generation++
        running = true
        clickable = false
        hologramsReady = false
        clicks = 0
        registerEvents()
        CC.boardCast(CC.translate("&5&l龙蛋！ &d龙蛋已在中心点位刷新,请前往点击！"))
        moveEggTo(origin.clone())
        playSoundToOnlinePlayers(Sound.ENDERDRAGON_GROWL, 1.5f, 1.5f)
    }

    private fun moveEggTo(location: Location) {
        val expected = generation
        runOnMainThread {
            if (expected != generation || !running) {
                return@runOnMainThread
            }

            clearPlacedEggs()
            despawnHolograms()

            val world = location.world
            if (world == null) {
                Bukkit.getLogger().warning("龙蛋位置无效，世界为空！")
                hologramsReady = false
                clickable = false
                return@runOnMainThread
            }

            val block = world.getBlockAt(location)
            block.type = Material.DRAGON_EGG
            placedEggs.add(block.location)
            eggLocation = block.location
            reCreateHologram(block.location)
            hologramsReady = true
            clickable = true
        }
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (!running || !clickable || !hologramsReady) {
            return
        }

        val block = event.clickedBlock ?: return
        if (block.type != Material.DRAGON_EGG || block.location != eggLocation) {
            return
        }

        event.isCancelled = true

        val player = event.player
        val p = PlayerProfile.getPlayerProfileByUuid(player.uniqueId) ?: run {
            player.sendMessage(CC.translate("&5&l龙蛋！ &c你的玩家数据未加载，无法获得奖励！"))
            return
        }

        val random = Random()
        val randomMultiplier = random.nextInt(3) + 1
        val (coins, exp) = when (clicks) {
            0 -> Pair(3 * randomMultiplier, 3 * (random.nextInt(5) + 1))
            else -> Pair(clicks * 0.5 * randomMultiplier, clicks * 0.5 * (random.nextInt(5) + 1))
        }

        if (clicks <= MAX_CLICKS) {
            p.coins += coins.toDouble()
            p.experience += exp.toDouble()
        }

        player.playSound(player.location, Sound.CLICK, 1.5f, 1.5f)
        player.sendMessage(CC.translate("&5&l龙蛋！ &7点击龙蛋 获得 &e$coins &6金币 &e$exp &b经验&7"))

        addClicks()
        handleClickEvents()
    }

    private fun handleClickEvents() {
        if (clicks >= MAX_CLICKS) {
            deactivateLater()
        } else if (clicks % CLICK_THRESHOLD == 0) {
            clickable = false
            hologramsReady = false
            setNewLocation()
        }
    }

    private fun setNewLocation() {
        val current = eggLocation ?: return
        moveEggTo(findRandomLocation(current))
        CC.boardCast("&5&l龙蛋！ &7龙蛋已被移动到了新的位置！")
        playSoundToOnlinePlayers(Sound.ENDERDRAGON_HIT, 1.5f, 1.5f)
    }

    private fun playSoundToOnlinePlayers(sound: Sound, volume: Float, pitch: Float) {
        Bukkit.getOnlinePlayers().forEach {
            it.playSound(it.location, sound, volume, pitch)
        }
    }

    private fun reCreateHologram(location: Location) {
        despawnHolograms()

        try {
            val hologramLoc1 = location.clone().add(0.5, 2.4, 0.5)
            val hologramLoc2 = location.clone().add(0.5, 2.0, 0.5)

            firstHologram = HologramAPI.createHologram(hologramLoc1, "§a$clicks")
            secondHologram = HologramAPI.createHologram(hologramLoc2, "§e§l点击")

            firstHologram?.spawn()
            secondHologram?.spawn()

            if (firstHologram == null || secondHologram == null) {
                Bukkit.getLogger().warning("龙蛋全息图创建失败！")
                hologramsReady = false
            } else {
                hologramsReady = true
            }
        } catch (e: Exception) {
            Bukkit.getLogger().severe("创建龙蛋全息图时发生异常: ${e.message}")
            e.printStackTrace()
            firstHologram = null
            secondHologram = null
            hologramsReady = false
        }
    }

    private fun despawnHolograms() {
        try {
            firstHologram?.deSpawn()
            secondHologram?.deSpawn()
        } catch (e: Exception) {
            Bukkit.getLogger().warning("销毁龙蛋全息图时发生异常: ${e.message}")
        }
        firstHologram = null
        secondHologram = null
        hologramsReady = false
    }

    private fun addClicks() {
        clicks++
        try {
            firstHologram?.let { hologram ->
                hologram.text = "§a$clicks"
            } ?: run {
                eggLocation?.let { reCreateHologram(it) }
            }
        } catch (e: Exception) {
            Bukkit.getLogger().warning("更新龙蛋点击数全息图时发生异常: ${e.message}")
        }
    }

    override fun onInactive() {
        generation++
        running = false
        clickable = false
        hologramsReady = false
        unregisterEvents()
        runOnMainThread { cleanup() }
        playSoundToOnlinePlayers(Sound.ENDERDRAGON_DEATH, 1.5f, 1.5f)
        CC.boardCast(CC.translate("&5&l龙蛋！ &7活动已结束！"))
    }

    private fun deactivateLater() {
        val plugin = ThePit.getInstance()
        if (!plugin.isEnabled) {
            return
        }
        Bukkit.getScheduler().runTask(plugin, Runnable { plugin.eventFactory.inactiveEvent(this) })
    }

    private fun runOnMainThread(action: () -> Unit) {
        if (Bukkit.isPrimaryThread() || !ThePit.getInstance().isEnabled) {
            action()
            return
        }
        Bukkit.getScheduler().runTask(ThePit.getInstance(), Runnable { action() })
    }

    private fun cleanup() {
        clearPlacedEggs()
        despawnHolograms()
        running = false
        clickable = false
        hologramsReady = false
        eggLocation = null
        clicks = 0
    }

    private fun findRandomLocation(origin: Location): Location {
        val random = Random()
        val world = origin.world ?: return origin

        repeat(MAX_ATTEMPTS) {
            val x = origin.x + randomOffset(random)
            val z = origin.z + randomOffset(random)
            val candidate = Location(world, x, origin.y, z)
            if (candidate.block.type == Material.AIR) {
                return candidate
            }
        }

        return origin
    }

    private fun randomOffset(random: Random): Int = random.nextInt(31) - SEARCH_RADIUS
}
