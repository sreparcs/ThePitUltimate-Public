package cn.charlotte.pit.enchantment.type.rare;

import com.google.common.util.concurrent.AtomicDouble;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.enchantment.IActionDisplayEnchant;
import cn.charlotte.pit.enchantment.param.item.BowOnly;
import cn.charlotte.pit.enchantment.param.item.WeaponOnly;
import cn.charlotte.pit.enchantment.rarity.EnchantmentRarity;
import cn.charlotte.pit.parm.listener.IAttackEntity;
import cn.charlotte.pit.parm.listener.IPlayerShootEntity;
import cn.charlotte.pit.util.chat.CC;
import cn.charlotte.pit.util.cooldown.Cooldown;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import cn.charlotte.pit.register.IMagicLicense;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@BowOnly
@WeaponOnly
public final class BrokenStringEnchant extends AbstractEnchantment implements IMagicLicense, IPlayerShootEntity, IAttackEntity, Listener, IActionDisplayEnchant {
    @NotNull
    private final String brokenString = "BrokenString";
    @NotNull
    private final Map<UUID, Cooldown> cooldown = new LinkedHashMap<>();
    // 替换Extend.INSTANCE为通用插件实例获取方式
    private static final Plugin PLUGIN = org.bukkit.plugin.java.JavaPlugin.getProvidingPlugin(BrokenStringEnchant.class);

    @NotNull
    @Override
    public String getEnchantName() {
        return "断弦";
    }

    @Override
    public int getMaxEnchantLevel() {
        return 3;
    }

    @NotNull
    @Override
    public String getNbtName() {
        return "Combo_Broken_String";
    }

    @NotNull
    @Override
    public EnchantmentRarity getRarity() {
        return EnchantmentRarity.RARE;
    }

    @Nullable
    @Override
    public Cooldown getCooldown() {
        return null;
    }

    @NotNull
    @Override
    public String getUsefulnessLore(int enchantLevel) {
        int cooldownTime = 30 - enchantLevel * 7;
        return "&7命中目标时对目标施加 &8断弦 &7(00:02) 效果 (" + cooldownTime + "s冷却) /s&7效果 &8断弦&7: 无法射出箭矢";
    }

    @Override
    public void handleAttackEntity(int enchantLevel, @NotNull Player attacker, @NotNull Entity target, double v, @NotNull AtomicDouble atomicDouble, @NotNull AtomicDouble atomicDouble1, @NotNull AtomicBoolean atomicBoolean) {
        if (target instanceof Player) {
            Cooldown playerCooldown = this.cooldown.get(attacker.getUniqueId());
            if (playerCooldown == null || playerCooldown.hasExpired()) {
                this.cooldown.put(attacker.getUniqueId(), new Cooldown((long) (30 - enchantLevel * 7), TimeUnit.SECONDS));
                this.onActive(attacker, (Player) target);
            }
        }
    }

    @Override
    public void handleShootEntity(int enchantLevel, @NotNull Player shooter, @NotNull Entity target, double v, @NotNull AtomicDouble atomicDouble, @NotNull AtomicDouble atomicDouble1, @NotNull AtomicBoolean atomicBoolean) {
        if (target instanceof Player) {
            Cooldown playerCooldown = this.cooldown.get(shooter.getUniqueId());
            if (playerCooldown == null || playerCooldown.hasExpired()) {
                this.cooldown.put(shooter.getUniqueId(), new Cooldown((long) (30 - enchantLevel * 7), TimeUnit.SECONDS));
                this.onActive(shooter, (Player) target);
            }
        }
    }

    @EventHandler
    public final void onShoot(@NotNull EntityShootBowEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player shooter) {
            if (shooter.hasMetadata(this.brokenString) && !shooter.getMetadata(this.brokenString).isEmpty()) {
                MetadataValue metadataValue = shooter.getMetadata(this.brokenString).get(0);
                if (metadataValue.asLong() > System.currentTimeMillis()) {
                    event.setCancelled(true);
                    shooter.sendMessage(CC.translate("&c&l断弦! &7你现在无法发射箭矢!"));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public final void onDeath(@NotNull PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player.hasMetadata(this.brokenString)) {
            player.removeMetadata(this.brokenString, PLUGIN);
        }
    }

    private final void onActive(Player attacker, Player targetPlayer) {
        // 移除旧的断弦元数据
        if (targetPlayer.hasMetadata(this.brokenString)) {
            targetPlayer.removeMetadata(this.brokenString, PLUGIN);
        }

        // 添加挖掘缓慢效果（断弦视觉效果）
        targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 40, 0, false, false));
        // 设置断弦元数据（2秒有效期）
        targetPlayer.setMetadata(this.brokenString, new FixedMetadataValue(PLUGIN, System.currentTimeMillis() + 2000L));

        // 发送提示消息
        attacker.sendMessage(CC.translate("&c&l断弦! &f" + targetPlayer.getDisplayName() + " &7将在接下来 &e2s &7内无法发射箭矢!"));
        targetPlayer.sendMessage(CC.translate("&c&l断弦! &7你将在接下来 &e2s &7内无法发射箭矢!"));
    }

    @NotNull
    @Override
    public String getText(int enchantLevel, @NotNull Player player) {
        Cooldown playerCooldown = this.cooldown.getOrDefault(player.getUniqueId(), new Cooldown(0L));
        String cooldownText = this.getCooldownActionText(playerCooldown);
        return cooldownText;
    }
}