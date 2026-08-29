package cn.charlotte.pit.enchantment.type.normal;

import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.IActionDisplayEnchant;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.ITickTask;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.time.TimeUtil;
import org.bukkit.entity.Player;
import cn.charlotte.pit.register.IMagicLicense;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@ArmorOnly
public class UniversalNature extends AbstractEnchantment implements ITickTask, IActionDisplayEnchant, IMagicLicense {
    public static final HashMap<UUID, Cooldown> COOLDOWN = new HashMap();

    @Override
    public String getEnchantName() {
        return "万象";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "zhulu_enchant";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.NORMAL;
    }

    @Override
    public Cooldown getCooldown() {
        return null;
    }

    private static int getCooldownInt(int enchantLevel) {
        switch (enchantLevel) {
            case 2 -> {
                return 8;
            }
            case 3 -> {
                return 7;
            }
            default -> {
                return 9;
            }
        }
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        int cooldown = getCooldownInt(enchantLevel);
        return "&7装备时每 &a" + cooldown + " &7秒额外恢复相当于自身最大生命值 &c" + (enchantLevel > 2 ? "17" : "13") + "% &7的生命.";
    }

    @Override
    public void handle(int enchantLevel, Player player) {
        if (player.getHealth() != player.getMaxHealth()) {
            if (((Cooldown)COOLDOWN.getOrDefault(player.getUniqueId(), new Cooldown(0L))).hasExpired() && !PlayerUtil.isVenom(player)) {
                COOLDOWN.put(player.getUniqueId(), new Cooldown((long)getCooldownInt(enchantLevel), TimeUnit.SECONDS));
                PlayerUtil.heal(player, (double)2.0F * Math.max(0.1, 0.01 * player.getMaxHealth() * (double)(enchantLevel > 2 ? 17 : 13)));
            }
        }
    }

    @Override
    public int loopTick(int enchantLevel) {
        return 20;
    }

    @Override
    public String getText(int level, Player player) {
        return ((Cooldown)COOLDOWN.getOrDefault(player.getUniqueId(), new Cooldown(0L))).hasExpired() ? "&a&l✔" : "&c&l" + TimeUtil.millisToRoundedTime(((Cooldown)COOLDOWN.get(player.getUniqueId())).getRemaining()).replace(" ", "");
    }
}