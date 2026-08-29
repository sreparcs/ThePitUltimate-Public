package cn.charlotte.pit.menu.sewers;

import cn.charlotte.pit.menu.sewers.button.RedeemButton;
import cn.charlotte.pit.util.menu.Button;
import cn.charlotte.pit.util.menu.Menu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Araykal
 * @since 2025/5/2
 */
public class SewersMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return "下水道";
    }

    @Override
    public int getSize() {
        return 3 * 9;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(13, new RedeemButton());
        return buttons;
    }
}
