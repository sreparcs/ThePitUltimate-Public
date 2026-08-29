package cn.charlotte.pit.enchantment.type.dark_normal;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.PlayerProfile;
import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.event.PlayerOnly;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.AutoRegister;
import cn.charlotte.pit.parm.listener.IPlayerKilledEntity;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.item.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * @author Araykal
 * @since 2025/4/14
 */
@ArmorOnly
@AutoRegister
public class CurseEnchant extends AbstractEnchantment implements IPlayerKilledEntity {
    @Override
    public String getEnchantName() {
        return "诅咒";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 1;
    }

    @Override
    public String getNbtName() {
        return "curse_enchant";
    }

    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.DARK_NORMAL;
    }

    @Nullable
    @Override
    public Cooldown getCooldown() {
        return null;
    }

    @Override
    public String getUsefulnessLore(int enchantLevel) {
        return "&7击杀穿戴 &6神话之甲 &7的玩家 施加 &c诅咒 &f(00:10) &7状态" + "/s&7状态 &c诅咒 &7: 复活将延长15秒";
    }

    @Override
    @PlayerOnly
    public void handlePlayerKilled(int enchantLevel, Player myself, Entity target, AtomicDouble coins, AtomicDouble experience) {
        Player targetPlayer = (Player) target;
        if (targetPlayer.getInventory().getLeggings() != null && "mythic_leggings".equals(ItemUtil.getInternalName(targetPlayer.getInventory().getLeggings()))) {
            PlayerProfile playerProfile = PlayerProfile.getPlayerProfileByUuid(targetPlayer.getUniqueId());
            if (playerProfile != null) {
                targetPlayer.sendMessage(CC.translate("&c&l诅咒! &7你被诅咒缠绕,复活将延迟15秒!"));
                playerProfile.setRespawnTime(1.5 * 1 + 14);
                Bukkit.getScheduler().runTaskLater(ThePit.getInstance(), () -> {
                    playerProfile.setRespawnTime(0.1d);
                    targetPlayer.sendMessage(CC.translate("&c&l诅咒! &7诅咒驱散!"));
                }, 20 * 10);
            }
        }
    }
}
