package cn.charlotte.pit.enchantment.type.normal;

import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.util.cooldown.Cooldown;
import cn.charlotte.pit.util.item.ItemUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import cn.charlotte.pit.register.IMagicLicense;
import cn.charlotte.pit.movement.iSpigot;
import cn.charlotte.pit.movement.MovementHandler;
import cn.charlotte.pit.enchantment.type.rare.ImprisonEnchant;

@ArmorOnly
public class Scurry extends AbstractEnchantment implements MovementHandler, IMagicLicense {
    private static final Scurry SCURRY = new Scurry();

    public Scurry() {
        try {
            try {
                iSpigot.INSTANCE.addMovementHandler(this);
            } catch (NoClassDefFoundError var2) {
            }

        } catch (Throwable $ex) {
            throw $ex;
        }
    }

    @Override
    public String getEnchantName() {
        return "疾走";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @Override
    public String getNbtName() {
        return "Trot";
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
        return "&7穿戴时行走速度提升 &b" + (enchantLevel == 3 ? "20%" : (enchantLevel == 2 ? "10%" : "5%"));
    }

    @Override
    public void handleUpdateLocation(Player player, Location location, Location location1, PacketPlayInFlying packetPlayInFlying) {
        long imprisonEndTime = (Long)ImprisonEnchant.imprisonMap.getOrDefault(player.getUniqueId(), 0L);
        if (System.currentTimeMillis() > imprisonEndTime) {
            this.setPlayerWalkSpeed(player);
        }
    }

    private void setPlayerWalkSpeed(Player player) {
        if (player.getInventory().getLeggings() != null && "mythic_leggings".equals(ItemUtil.getInternalName(player.getInventory().getLeggings())) && SCURRY.isItemHasEnchant(player.getInventory().getLeggings())) {
            int level = SCURRY.getItemEnchantLevel(player.getInventory().getLeggings());
            switch (level) {
                case 1 -> player.setWalkSpeed(0.21F);
                case 2 -> player.setWalkSpeed(0.22F);
                case 3 -> player.setWalkSpeed(0.24F);
                default -> player.setWalkSpeed(0.2F);
            }
        } else {
            player.setWalkSpeed(0.2F);
        }
    }

    @Override
    public void handleUpdateRotation(Player player, Location location, Location location1, PacketPlayInFlying packetPlayInFlying) {
    }
}