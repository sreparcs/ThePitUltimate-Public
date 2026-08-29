package cn.charlotte.pit.npc.type;

import net.jitse.npclib.api.NPC;
import net.jitse.npclib.api.skin.Skin;
import cn.charlotte.pit.config.NewConfiguration;
import cn.charlotte.pit.npc.AbstractPitNPC;

public abstract class SkinNPC extends AbstractPitNPC {

    @Override
    public void initSkin(NPC npc) {
        final String skinValue = NewConfiguration.INSTANCE.getConfig().getString(this.getNpcInternalName() + "-npc-skin");
        String signature = NewConfiguration.INSTANCE
                .getConfig()
                .getString("not-netease-skins." + getNpcInternalName() + "-signature");
        if (skinValue != null && signature != null) {
            npc.setSkin(new Skin(skinValue, signature));
        } else {
            npc.setSkin(getNpcSkin());
        }
    }
}
