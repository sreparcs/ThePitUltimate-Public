package cn.charlotte.pit.item.type.mythic;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.enchantment.AbstractEnchantment;
import cn.charlotte.pit.item.IMythicItem;
import cn.charlotte.pit.util.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: EmptyIrony
 * @Date: 2021/2/7 1:10
 */

public class MagicFishingRod extends IMythicItem {
    @Override
    public String getInternalName() {
        return "magic_fishing_rod";
    }

    @Override
    public String getItemDisplayName() {
        return "神奇鱼竿";
    }

    @Override
    public Material getItemDisplayMaterial() {
        return Material.FISHING_ROD;
    }
}
