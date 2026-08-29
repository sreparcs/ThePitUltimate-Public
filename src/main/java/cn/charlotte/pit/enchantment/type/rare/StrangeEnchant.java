package cn.charlotte.pit.enchantment.type.rare;

import cn.charlotte.pit.data.PlayerProfile;
import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.event.PlayerOnly;
import cn.charlotte.pit.enchantment.param.item.WeaponOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IPlayerKilledEntity;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.random.RandomUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@WeaponOnly
public class StrangeEnchant extends AbstractEnchantment implements IPlayerKilledEntity {
    @NotNull
    public String getEnchantName() {
        return "窃取";
    }

    public int getMaxEnchantLevel() {
        return 3;
    }

    @NotNull
    public String getNbtName() {
        return "strange";
    }

    @NotNull
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.RARE;
    }

    @Nullable
    public Cooldown getCooldown() {
        return null;
    }

    @NotNull
    public String getUsefulnessLore(int i) {
        int var10000 = this.getlevelLore(i);
        return "&7每击杀玩家有50%概率窃取敌人 &6" + var10000 + " &7硬币";
    }

    private final int getlevelLore(int i) {
        short var10000;
        switch (i) {
            case 1 -> var10000 = 600;
            case 2 -> var10000 = 1000;
            case 3 -> var10000 = 1500;
            default -> var10000 = 0;
        }

        return var10000;
    }

    @PlayerOnly
    public void handlePlayerKilled(int i, @NotNull Player player, @NotNull Entity entity, @NotNull AtomicDouble atomicDouble, @NotNull AtomicDouble atomicDouble1) {
        if (!entity.hasMetadata("bot")) {
            if (RandomUtil.hasSuccessfullyByChance((double)0.5F)) {
                PlayerProfile playerProfile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
                PlayerProfile targetProfile = PlayerProfile.getPlayerProfileByUuid(entity.getUniqueId());
                if (targetProfile.getCoins() < (double)this.getlevelLore(i)) {
                    return;
                }

                playerProfile.setCoins(playerProfile.getCoins() + (double)this.getlevelLore(i));
                targetProfile.setCoins(targetProfile.getCoins() - (double)this.getlevelLore(i));
                String var10001 = entity.getName();
                player.sendMessage("§c§l窃取! §7你窃取了§e" + var10001 + " §6" + this.getlevelLore(i) + " §7硬币");
                entity.sendMessage("§c§l窃取! §7你的硬币被§e" + player.getName() + "窃取");
            }
        }
    }
}