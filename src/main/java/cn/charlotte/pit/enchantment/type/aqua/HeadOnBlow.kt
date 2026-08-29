package cn.charlotte.pit.enchantment.type.aqua

import cn.charlotte.pit.ThePit // 新增：引入核心实例类
import cn.charlotte.pit.enchantment.AbstractEnchantment
import cn.charlotte.pit.enchantment.param.item.RodOnly
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity
import cn.charlotte.pit.parm.AutoRegister
import cn.charlotte.pit.util.cooldown.Cooldown
import cn.charlotte.pit.util.toMythicItem
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerFishEvent

/**
 * 当头一棒附魔 - 钓鱼竿专属
 * 效果：钩中玩家时，击退力度随附魔等级提升（基础20%，每级+10%）
 */
@AutoRegister
@RodOnly
class HeadOnBlow : AbstractEnchantment(), Listener {
    override fun getEnchantName(): String {
        return "当头一棒"
    }

    override fun getMaxEnchantLevel(): Int {
        return 3
    }

    override fun getNbtName(): String {
        return "head-on_blow"
    }

    @EventHandler
    fun onPlayerFish(event: PlayerFishEvent) {
        // 1. 跳过非钩中实体状态、非玩家目标
        if (event.state != PlayerFishEvent.State.CAUGHT_ENTITY || event.caught !is Player) return

        val attacker = event.player
        val victim = event.caught as Player

        // 新增：排除NPC（核心修复点）
        if (ThePit.getInstance().getNpcFactory().hasNPC(victim)) return

        // 2. 获取玩家手持钓鱼竿的附魔等级
        val mythicItem = attacker.inventory.itemInHand.toMythicItem()
        val enchantLevel = mythicItem?.enchantments?.getInt(this) ?: 0
        if (enchantLevel <= 0) return

        // 3. 计算击退力度：基础1.2（20%） + 每级0.1（10%）
        val knockbackMultiplier = 1.2 + (enchantLevel - 1) * 0.1
        val knockback = victim.location.toVector()
            .subtract(attacker.location.toVector())
            .normalize()
            .multiply(knockbackMultiplier)

        // 4. 应用击退（保留Y轴少量高度，避免贴地）
        knockback.y = 0.2
        victim.velocity = knockback
    }

    override fun getRarity(): EnchantmentRarity {
        return EnchantmentRarity.FISH_NORMAL
    }

    override fun getCooldown(): Cooldown? {
        return null
    }

    override fun getUsefulnessLore(enchantLevel: Int): String {
        val extraPercent = 20 + (enchantLevel - 1) * 10
        return "甩出鱼钩击退玩家力度增加${extraPercent}%"
    }
}