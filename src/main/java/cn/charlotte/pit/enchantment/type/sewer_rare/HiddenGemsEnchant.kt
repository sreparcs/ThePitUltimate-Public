package cn.charlotte.pit.enchantment.type.sewer_rare

import cn.charlotte.pit.ThePit
import com.google.common.util.concurrent.AtomicDouble
import cn.charlotte.pit.enchantment.AbstractEnchantment
import cn.charlotte.pit.enchantment.IActionDisplayEnchant
import cn.charlotte.pit.enchantment.param.event.PlayerOnly
import cn.charlotte.pit.enchantment.param.item.ArmorOnly
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity
import cn.charlotte.pit.item.IMythicItem
import cn.charlotte.pit.item.MythicColor
import cn.charlotte.pit.item.type.mythic.MythicLeggingsItem
import cn.charlotte.pit.parm.listener.IPlayerBeKilledByEntity
import cn.charlotte.pit.parm.listener.IPlayerKilledEntity
import cn.charlotte.pit.util.chat.CC
import cn.charlotte.pit.util.cooldown.Cooldown
import cn.charlotte.pit.util.random.RandomUtil
import cn.charlotte.pit.util.toMythicItem
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import kotlin.random.Random

/**
 * @author Araykal
 * @since 2025/5/13
 */
@ArmorOnly
class HiddenGemsEnchant : AbstractEnchantment(), IPlayerKilledEntity, IPlayerBeKilledByEntity, IActionDisplayEnchant {
    private val kills: MutableMap<String, Int> = mutableMapOf()
    private val killThreshold = 117

    override fun getEnchantName(): String = "隐藏的宝石"
    override fun getMaxEnchantLevel(): Int = 1
    override fun getNbtName(): String = "hidden_gems_enchant"
    override fun getRarity(): EnchantmentRarity = EnchantmentRarity.SEWER_RARE
    override fun getCooldown(): Cooldown? = null

    override fun getUsefulnessLore(enchantLevel: Int): String {
        return "&7穿戴此下水道之甲, 连续击杀 &e$killThreshold &7名玩家/s&7将变为 &eI 阶 &7神话之甲, &7并随机带来一个 &cIII 级 &7附魔"
    }

    @PlayerOnly
    override fun handlePlayerKilled(
        enchantLevel: Int, myself: Player, target: Entity, coins: AtomicDouble, experience: AtomicDouble
    ) {
        val playerName = myself.name
        val currentKills = kills.getOrDefault(playerName, 0)

        if (currentKills >= killThreshold) {
            val enchantmentFactor = ThePit.getInstance().enchantmentFactor
            val rareEnchantments = enchantmentFactor.enchantments.filter { it.rarity == EnchantmentRarity.RARE }
                .filter { it.canApply(MythicLeggingsItem().toItemStack()) }

            val commonEnchantments = enchantmentFactor.enchantments.filter { it.rarity == EnchantmentRarity.NORMAL }
                .filter { it.canApply(MythicLeggingsItem().toItemStack()) }

            val selectedEnchantment =
                (if (RandomUtil.hasSuccessfullyByChance(0.2)) rareEnchantments else commonEnchantments)
                    .randomOrNull() ?: return


            myself.inventory.leggings?.let { itemStack ->
                val item = itemStack.toMythicItem() as? IMythicItem ?: return
                val randomLive = Random.nextInt(2, 5)

                item.apply {
                    enchantments.clear()
                    color = MythicColor.YELLOW
                    maxLive = randomLive
                    live = randomLive
                    enchantments[selectedEnchantment] = 3
                    tier = 1
                }

                myself.inventory.leggings = item.toItemStack()
                myself.updateInventory()
                ThePit.getInstance().soundFactory.playSound("gems_sound", myself)
                kills.remove(playerName)
                myself.sendMessage(CC.translate("&d&l宝石! &7转换为&d神话之甲"))
                return
            }
        }

        kills[playerName] = currentKills + 1
        myself.sendMessage(CC.translate("&d&l宝石! &7击杀 &7(&e${currentKills + 1}&7/&c$killThreshold&7)"))
    }

    override fun handlePlayerBeKilledByEntity(
        enchantLevel: Int, myself: Player, target: Entity?, coins: AtomicDouble?, experience: AtomicDouble?
    ) {
        kills[myself.name] = 0
        myself.sendMessage(CC.translate("&d&l宝石! &7击杀清零 (&e0&7/&c$killThreshold&7)"))
    }

    override fun getText(level: Int, player: Player): String {
        val playerKills = kills.getOrDefault(player.name, 0)
        return if (playerKills >= killThreshold) "&dDone" else "&e${playerKills}&7/&c$killThreshold"
    }
}
