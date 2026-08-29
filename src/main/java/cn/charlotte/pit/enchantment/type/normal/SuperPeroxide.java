package cn.charlotte.pit.enchantment.type.normal;

import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IPlayerDamaged;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import cn.charlotte.pit.register.IMagicLicense;

import java.util.concurrent.atomic.AtomicBoolean;

@ArmorOnly
public class SuperPeroxide extends AbstractEnchantment implements IPlayerDamaged, IMagicLicense {

    @Override
    public String getEnchantName() {
        return "超氧化物";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "Superoxide";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.NORMAL;
    }

    @Override
    public Cooldown getCooldown() {
        return null;
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        return "&7受到攻击时如自身没有 &c生命恢复 &7效果,则/s&7为自身添加 &c生命恢复 " + (enchantLevel >= 3 ? "II" : "I") + " &f(00:0" + (enchantLevel >= 2 ? "8" : "4") + ") &7效果";
    }

    private void processEnchant(int enchantLevel, Player player) {
        for(PotionEffect potionEffect : player.getActivePotionEffects()) {
            if ("REGENERATION".equals(potionEffect.getType().getName())) {
                return;
            }
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * (enchantLevel >= 2 ? 8 : 4), enchantLevel >= 3 ? 1 : 0), true);
    }

    @Override
    public void handlePlayerDamaged(int enchantLevel, Player myself, Entity attacker, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        this.processEnchant(enchantLevel, myself);
    }
}