package cn.charlotte.pit.enchantment.type.rare;

import cn.charlotte.pit.ThePit;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.parm.listener.IPlayerShootEntity;
import cn.charlotte.pit.parm.listener.ITickTask;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

@ArmorOnly
public class NightMareEnchant extends AbstractEnchantment implements IAttackEntity, IPlayerShootEntity, ITickTask {
    public boolean day() {
        Server server = Bukkit.getServer();
        long time = server.getWorld("world").getTime();
        return time > 0L && time < 12300L;
    }

    public String getLeggingsColor(ItemStack item) {
        if (item != null && item.getType() != Material.AIR) {
            net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
            NBTTagCompound tag = nmsItem.getTag();
            if (tag == null) {
                return null;
            } else {
                NBTTagCompound extra = tag.getCompound("extra");
                if (extra == null) {
                    return null;
                } else {
                    return !extra.hasKey("mythic_color") ? null : extra.getString("mythic_color");
                }
            }
        } else {
            return null;
        }
    }

    public String getEnchantName() {
        return "梦魇";
    }

    public int getMaxEnchantLevel() {
        return 1;
    }

    public String getNbtName() {
        return "Nightmare";
    }

    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.RARE;
    }

    @Nullable
    public Cooldown getCooldown() {
        return null;
    }

    public String getUsefulnessLore(int i) {
        return "&7在白天时,获得 &8永久虚弱II &7并对穿戴 &6皮革装备 &7的玩家造成的伤害 &c-10%,/s在夜晚时,获得永久 &f抗性提升I &7同时攻击穿戴 &6皮革装备 &7的玩家造成的伤害 &c+20%.";
    }

    public void handleAttackEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        Player targetPlayer = (Player)target;
        if (attacker.getInventory().getLeggings() != null && "dark".equals(this.getLeggingsColor(attacker.getInventory().getLeggings())) && this.isItemHasEnchant(attacker.getInventory().getLeggings()) && Arrays.stream(targetPlayer.getInventory().getArmorContents()).anyMatch((itemStack) -> itemStack != null && itemStack.getType().name().contains("LEATHER_"))) {
            if (this.day()) {
                boostDamage.set(boostDamage.get() - 0.1);
            } else {
                boostDamage.set(boostDamage.get() + 0.2);
            }
        }
    }

    public void handleShootEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        Player targetPlayer = (Player)target;
        if (attacker.getInventory().getLeggings() != null && "dark".equals(this.getLeggingsColor(attacker.getInventory().getLeggings())) && this.isItemHasEnchant(attacker.getInventory().getLeggings()) && Arrays.stream(targetPlayer.getInventory().getArmorContents()).anyMatch((itemStack) -> itemStack != null && itemStack.getType().name().contains("LEATHER_"))) {
            if (attacker.getWorld().getTime() >= 0L && attacker.getWorld().getTime() < 12000L) {
                boostDamage.set(boostDamage.get() - 0.1);
            } else {
                boostDamage.set(boostDamage.get() + 0.2);
            }
        }
    }

    public void handle(int enchantLevel, Player player) {
        try {
            if (!PlayerUtil.isVenom(player)) {
                if (!this.day()) {
                    Bukkit.getScheduler().runTaskAsynchronously(ThePit.getInstance(), () -> {
                        player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 0), true);
                    });
                } else {
                    Bukkit.getScheduler().runTaskAsynchronously(ThePit.getInstance(), () -> {
                        player.removePotionEffect(PotionEffectType.WEAKNESS);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 1), true);
                    });
                }
            }
        } catch (Throwable $ex) {
            throw $ex;
        }
    }

    public int loopTick(int enchantLevel) {
        return 20;
    }
}