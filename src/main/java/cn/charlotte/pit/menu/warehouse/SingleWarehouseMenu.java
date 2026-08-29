package cn.charlotte.pit.menu.warehouse;

import cn.charlotte.pit.data.PlayerProfile;
import cn.charlotte.pit.util.menu.Button;
import cn.charlotte.pit.util.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Araykal
 * @Date: 2025/6/21
 */
public class SingleWarehouseMenu extends Menu {
    
    private final int warehouseId;
    
    public SingleWarehouseMenu(int warehouseId) {
        this.warehouseId = warehouseId;
    }
    
    @Override
    public String getTitle(Player player) {
        return "寄存箱 #" + warehouseId;
    }
    
    @Override
    public Map<Integer, Button> getButtons(Player player) {
        return new HashMap<>();
    }
    
    @Override
    public void openMenu(Player player) {
        PlayerProfile profile = PlayerProfile.getPlayerProfileByUuid(player.getUniqueId());
        Inventory warehouse = profile.getWarehouse().getWarehouse(warehouseId);
        player.openInventory(warehouse);
    }
    
    @Override
    public int getSize() {
        return 45;
    }
} 