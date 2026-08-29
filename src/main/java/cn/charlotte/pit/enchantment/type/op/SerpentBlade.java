package cn.charlotte.pit.enchantment.type.op;

import cn.charlotte.pit.data.PlayerProfile;
import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.IActionDisplayEnchant;
import cn.charlotte.pit.enchantment.param.item.WeaponOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import cn.charlotte.pit.register.IMagicLicense;

import java.util.concurrent.atomic.AtomicBoolean;

@WeaponOnly
public class SerpentBlade extends AbstractEnchantment implements IAttackEntity, IActionDisplayEnchant, IMagicLicense {

    @Override
    public String getEnchantName() {
        return "月影";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "SeprentBlade";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.OP;
    }

    @Override
    public @Nullable Cooldown getCooldown() {
        return null;
    }

    @Override
    public String getUsefulnessLore(int i) {
        return "每 &e" + (15 - i * 2) + " &7次攻击命中, 使你造成的近战伤害增加至 &c202.5%";
    }

    @Override
    public void handleAttackEntity(int i, Player player, Entity entity, double v, AtomicDouble atomicDouble, AtomicDouble atomicDouble1, AtomicBoolean atomicBoolean) {
        if (PlayerProfile.getPlayerProfileByUuid(player.getUniqueId()).getMeleeHit() % (15 - i * 2) == 0) {
            atomicDouble1.getAndAdd(1.025);
        }
    }

    @Override
    public String getText(int level, Player player) {
        int hit = player.getItemInHand() != null && player.getItemInHand().getType() == Material.BOW ? PlayerProfile.getPlayerProfileByUuid(player.getUniqueId()).getBowHit() : PlayerProfile.getPlayerProfileByUuid(player.getUniqueId()).getMeleeHit();
        int require = 15 - level * 2;
        return hit % require == 0 ? "&a&l✔" : "&e&l" + (require - hit % require);
    }
}