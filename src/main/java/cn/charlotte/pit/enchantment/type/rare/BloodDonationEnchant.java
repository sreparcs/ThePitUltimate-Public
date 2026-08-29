package cn.charlotte.pit.enchantment.type.rare;

import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.IActionDisplayEnchant;
import cn.charlotte.pit.enchantment.param.event.PlayerOnly;
import cn.charlotte.pit.enchantment.param.item.WeaponOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.random.RandomUtil;
import cn.charlotte.pit.util.time.TimeUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import cn.charlotte.pit.register.IMagicLicense;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@WeaponOnly
public class BloodDonationEnchant extends AbstractEnchantment implements IAttackEntity, IActionDisplayEnchant, IMagicLicense {
    private final DecimalFormat numFormat = new DecimalFormat("0.0");
    private final Map<UUID, Cooldown> cooldown = new HashMap();

    @Override
    public String getEnchantName() {
        return "献血";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "Xianxie_enchant";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.RARE;
    }

    @Override
    public Cooldown getCooldown() {
        return new Cooldown(20L, TimeUnit.SECONDS);
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        return "&7当你的生命值大于 &c" + (enchantLevel + 2) + "❤ &7时攻击命中,/s有 &b20% &7的概率消耗自身 &c" + enchantLevel + "❤ &7生命值,/s对目标造成 &f" + this.numFormat.format((long)(enchantLevel + 1)) + "❤ &7的&c必中&7伤害./s&c(必中伤害无法被免疫与抵抗) &7(20秒冷却)";
    }

    @PlayerOnly
    @Override
    public void handleAttackEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        this.cooldown.putIfAbsent(attacker.getUniqueId(), new Cooldown(0L));
        if (attacker.getHealth() >= (double)(2 * (enchantLevel + 2)) && this.cooldown.get(attacker.getUniqueId()).hasExpired() && RandomUtil.hasSuccessfullyByChance(0.2)) {
            this.cooldown.put(attacker.getUniqueId(), this.getCooldown());
            attacker.setHealth(Math.max(0.1, attacker.getHealth() - (double)(2 * enchantLevel)));
            if (((Player)target).getHealth() > (double)(2 * (enchantLevel + 1))) {
                ((Player)target).setHealth(Math.max(0.1, ((Player)target).getHealth() - (double)(2 * (enchantLevel + 1))));
            } else {
                ((Player)target).damage(((Player)target).getMaxHealth() * (double)100.0F);
            }
        }
    }

    @Override
    public String getText(int level, Player player) {
        Cooldown playerCooldown = this.cooldown.getOrDefault(player.getUniqueId(), new Cooldown(0L));
        return playerCooldown.hasExpired() ? "&a&l✔" : "&c&l" + TimeUtil.millisToRoundedTime(playerCooldown.getRemaining()).replace(" ", "") + " ";
    }
}