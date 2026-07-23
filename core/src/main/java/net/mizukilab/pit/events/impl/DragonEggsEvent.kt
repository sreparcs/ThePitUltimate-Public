package net.mizukilab.pit.events.impl

import cn.charlotte.pit.ThePit
import cn.charlotte.pit.data.PlayerProfile
import cn.charlotte.pit.events.AbstractEvent
import cn.charlotte.pit.events.trigger.type.INormalEvent
import cn.charlotte.pit.util.hologram.Hologram
import cn.charlotte.pit.util.hologram.HologramAPI
import net.mizukilab.pit.config.NewConfiguration.eventOnlineRequired
import net.mizukilab.pit.util.chat.CC
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
    private var eggLocation: Location? = null
    private var clicks: Int = 0
    private var firstHologram: Hologram? = null
    private var secondHologram: Hologram? = null
    private var isActive = false
    private var isClick = false
    private var hologramsReady = false
    private var lastEggLocation: Location? = null

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

    private fun removeOldEgg() {
        lastEggLocation?.let { lastLoc ->
            try {
                if (lastLoc.world != null && lastLoc.block.type == Material.DRAGON_EGG) {
                    lastLoc.block.type = Material.AIR
                }
            } catch (e: Exception) {
                Bukkit.getLogger().warning("清理旧龙蛋失败: ${e.message}")
            }
        }
        eggLocation?.let { currentLoc ->
            if (currentLoc != lastEggLocation && currentLoc.world != null && currentLoc.block.type == Material.DRAGON_EGG) {
                currentLoc.block.type = Material.AIR
            }
        }
    }

    private fun prepareNewLocation() {
        despawnHolograms()
        removeOldEgg()
        hologramsReady = false
    }

    private fun calculateOffset(origin: Location, random: Random): Int {
        return random.nextInt(31) - SEARCH_RADIUS
    }

    override fun onActive() {
        eggLocation = ThePit.getInstance().pitConfig.dragonEggLoc ?: run {
            Bukkit.broadcastMessage(CC.translate("&5&l龙蛋！ &7活动区域未设置，请联系管理员设置！"))
            ThePit.getInstance().getEventFactory().inactiveEvent(this)
            return
        }
        lastEggLocation = null
        isActive = true
        isClick = false
        hologramsReady = false
        registerEvents()
        CC.boardCast(CC.translate("&5&l龙蛋！ &d龙蛋已在中心点位刷新,请前往点击！"))
        setEggLocation(eggLocation!!)
        playSoundToOnlinePlayers(Sound.ENDERDRAGON_GROWL, 1.5f, 1.5f)
    }

    private fun setEggLocation(location: Location) {
        Bukkit.getScheduler().runTask(ThePit.getInstance()) {
            prepareNewLocation()
            lastEggLocation = eggLocation
            eggLocation = location

            if (location.world != null) {
                location.block.type = Material.DRAGON_EGG
                reCreateHologram(location)
                hologramsReady = true
                isClick = true
            } else {
                Bukkit.getLogger().warning("龙蛋位置无效，世界为空！")
                hologramsReady = false
                isClick = false
            }
        }
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (!isActive ||
            !isClick ||
            !hologramsReady ||
            eggLocation != event.clickedBlock?.location ||
            event.clickedBlock?.type != Material.DRAGON_EGG) {
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
            ThePit.getInstance().getEventFactory().inactiveEvent(this)
        } else if (clicks % CLICK_THRESHOLD == 0 || (clicks + 1) % CLICK_THRESHOLD == 0) {
            isClick = false
            hologramsReady = false
            setNewLocation()
        }
    }

    private fun setNewLocation() {
        prepareNewLocation()
        eggLocation?.let { setEggLocation(findRandomLocation(it)) }
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
            val hologramLoc1 = location.block.location.clone().add(0.5, 2.4, 0.5)
            val hologramLoc2 = location.block.location.clone().add(0.5, 2.0, 0.5)

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
        Bukkit.getScheduler().runTask(ThePit.getInstance()) {
            isActive = false
            isClick = false
            hologramsReady = false
            unregisterEvents()
            cleanup()
            playSoundToOnlinePlayers(Sound.ENDERDRAGON_DEATH, 1.5f, 1.5f)
            CC.boardCast(CC.translate("&5&l龙蛋！ &7活动已结束！"))
        }
    }

    private fun cleanup() {
        eggLocation?.block?.type = Material.AIR
        removeOldEgg()
        despawnHolograms()
        isActive = false
        eggLocation = null
        lastEggLocation = null
        clicks = 0
        isClick = false
        hologramsReady = false
    }

    private fun findRandomLocation(origin: Location): Location {
        val random = Random()
        var newLocation: Location
        var attempts = 0

        while (true) {
            val x = origin.x + calculateOffset(origin, random)
            val z = origin.z + calculateOffset(origin, random)
            newLocation = Location(origin.world, x, origin.y, z)

            if (newLocation.block.type == Material.AIR || attempts >= MAX_ATTEMPTS) {
                break
            }
            attempts++
        }

        return newLocation
    }
}