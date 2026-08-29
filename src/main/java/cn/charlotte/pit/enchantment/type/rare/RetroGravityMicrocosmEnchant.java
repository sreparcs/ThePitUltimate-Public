package cn.charlotte.pit.enchantment.type.rare;

import cn.charlotte.pit.ThePit;
import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.param.item.ArmorOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.parm.listener.IPlayerDamaged;
import cn.charlotte.pit.parm.listener.IPlayerShootEntity;
import cn.charlotte.pit.util.PlayerUtil;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@ArmorOnly
public class RetroGravityMicrocosmEnchant extends AbstractEnchantment implements IPlayerDamaged, IAttackEntity, IPlayerShootEntity {
    public static HashMap<Player, Integer> attackCount = new HashMap();
    public static HashMap<Player, Cooldown> attackCoolDown = new HashMap();
    public static HashMap<Player, Cooldown> boostCoolDown = new HashMap();

    public String getEnchantName() {
        return "微观反重力";
    }

    public int getMaxEnchantLevel() {
        return 3;
    }

    public String getNbtName() {
        return "microantygravity";
    }

    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.RARE;
    }

    @Nullable
    public Cooldown getCooldown() {
        return null;
    }

    public String getUsefulnessLore(int enchantLevel) {
        String pre = "&7若你在 &b2s &7内连续在空中受到 &e3 &7次攻击, 则恢复 &c2❤ &7生命值";
        String lvl2 = "/s&7并且在接下来的 &b30 &7秒内, 使你造成的伤害增加 &c10% &7(可叠加,最高2层)";
        String lvl3 = "/s&7并且在接下来的 &b30 &7秒内, 使你造成的伤害增加 &c15% &7(可叠加,最高2层)";
        return enchantLevel == 1 ? pre : pre + (enchantLevel == 2 ? lvl2 : lvl3);
    }

    public void handlePlayerDamaged(int enchantLevel, Player player, Entity attacker, double v, AtomicDouble atomicDouble, AtomicDouble atomicDouble1, AtomicBoolean atomicBoolean) {
        if (!attackCount.containsKey(player)) {
            attackCount.put(player, 0);
        }

        this.attackHeal(player, (Player)attacker);
        if (enchantLevel != 1) {
            if (((Cooldown)boostCoolDown.getOrDefault(player, new Cooldown(0L))).hasExpired()) {
                boostCoolDown.put(player, new Cooldown(30L, TimeUnit.SECONDS));
                if (!player.hasMetadata("boost") || ((MetadataValue)player.getMetadata("boost").get(0)).asInt() > 2) {
                    player.setMetadata("boost", new FixedMetadataValue(ThePit.getInstance(), 0));
                }

                if (((MetadataValue)player.getMetadata("boost").get(0)).asInt() != 2) {
                    player.setMetadata("boost", new FixedMetadataValue(ThePit.getInstance(), ((MetadataValue)player.getMetadata("boost").get(0)).asInt() + 1));
                }
            }
        }
    }

    private void attackHeal(Player player, Player attacker) {
        if (!player.isOnGround()) {
            if ((Integer)attackCount.get(player) != 2) {
                if (((Cooldown)attackCoolDown.getOrDefault(player, new Cooldown(0L))).hasExpired()) {
                    attackCount.put(player, 1);
                    attackCoolDown.put(player, new Cooldown(2L, TimeUnit.SECONDS));
                } else {
                    attackCount.put(player, (Integer)attackCount.get(player) + 1);
                }
            } else {
                attackCoolDown.put(player, new Cooldown(2L, TimeUnit.SECONDS));
                attackCount.put(player, 0);
                PlayerUtil.heal(player, (double)4.0F);
                player.sendMessage(CC.translate("&d&l微观反重力！ &7附魔已被 " + attacker.getName() + " &7触发！"));
            }
        }
    }

    public void handleAttackEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        if (attacker.hasMetadata("boost") && !((Cooldown)boostCoolDown.getOrDefault(attacker, new Cooldown(0L))).hasExpired()) {
            int boost = ((MetadataValue)attacker.getMetadata("boost").get(0)).asInt();
            if (boost == 1) {
                if (enchantLevel == 2) {
                    boostDamage.getAndAdd(0.1);
                } else if (enchantLevel == 3) {
                    boostDamage.getAndAdd(0.15);
                }
            } else if (boost == 2) {
                if (enchantLevel == 2) {
                    boostDamage.getAndAdd(0.2);
                } else if (enchantLevel == 3) {
                    boostDamage.getAndAdd(0.3);
                }
            }
        }
    }

    public void handleShootEntity(int enchantLevel, Player attacker, Entity target, double damage, AtomicDouble finalDamage, AtomicDouble boostDamage, AtomicBoolean cancel) {
        if (attacker.hasMetadata("boost")) {
            int boost = ((MetadataValue)attacker.getMetadata("boost").get(0)).asInt();
            if (boost == 1) {
                if (enchantLevel == 2) {
                    boostDamage.set(boostDamage.get() + 0.1);
                } else if (enchantLevel == 3) {
                    boostDamage.set(boostDamage.get() + 0.15);
                }
            } else if (boost == 2) {
                if (enchantLevel == 2) {
                    boostDamage.set(boostDamage.get() + 0.2);
                } else if (enchantLevel == 3) {
                    boostDamage.set(boostDamage.get() + 0.3);
                }
            }
        }
    }
}