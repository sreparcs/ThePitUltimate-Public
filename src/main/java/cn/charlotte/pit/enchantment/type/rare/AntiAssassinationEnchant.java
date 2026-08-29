package cn.charlotte.pit.enchantment.type.rare;

import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.IActionDisplayEnchant;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IPlayerDamaged;
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

@ArmorOnly
public class AntiAssassinationEnchant extends AbstractEnchantment implements IPlayerDamaged, IActionDisplayEnchant, IMagicLicense {
    private final DecimalFormat numFormat = new DecimalFormat("0.0");
    private final Map<UUID, Cooldown> cooldown = new HashMap();

    @Override
    public String getEnchantName() {
        return "反刺";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "Fanci";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.RARE;
    }

    @Override
    public Cooldown getCooldown() {
        return new Cooldown(10L, TimeUnit.SECONDS);
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        String damageValue = this.numFormat.format((long)enchantLevel);
        return "&7每次受到伤害有 &b20% &7的概率对目标造成 &f" + damageValue + "❤ &7的&c必中&7伤害./s&c(必中伤害无法被免疫与抵抗) &7(10秒冷却)";
    }

    @Override
    public void handlePlayerDamaged(int enchantLevel, Player myself, Entity attacker, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        this.cooldown.putIfAbsent(myself.getUniqueId(), new Cooldown(0L));
        if (RandomUtil.hasSuccessfullyByChance(0.2) && this.cooldown.get(myself.getUniqueId()).hasExpired()) {
            this.cooldown.put(myself.getUniqueId(), this.getCooldown());
            if (((Player)attacker).getHealth() > (double)(enchantLevel * 2)) {
                ((Player)attacker).setHealth(Math.max(0.1, ((Player)attacker).getHealth() - (double)(enchantLevel * 2)));
            } else {
                ((Player)attacker).damage(((Player)attacker).getMaxHealth() * (double)100.0F);
            }
        }
    }

    @Override
    public String getText(int level, Player player) {
        Cooldown playerCooldown = this.cooldown.getOrDefault(player.getUniqueId(), new Cooldown(0L));
        return playerCooldown.hasExpired() ? "&a&l✔" : "&c&l" + TimeUtil.millisToRoundedTime(playerCooldown.getRemaining()).replace(" ", "") + " ";
    }
}