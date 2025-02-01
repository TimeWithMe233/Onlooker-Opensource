package dev.onlooker.module.impl.render;

import dev.onlooker.event.impl.player.UpdateEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import net.minecraft.potion.Potion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RemoveEffects extends Module {
    public RemoveEffects() {
        super("AntiBadEffects", Category.RENDER,"Remove bad effects.");
    }
    @Override
    public void onUpdateEvent(UpdateEvent e) {
        List effectIdsToRemove = (List)(new ArrayList());
        effectIdsToRemove.add(Potion.blindness.id);
        Iterator effectid = effectIdsToRemove.iterator();
        while(effectid.hasNext()) {
            int effectId = ((Number)effectid.next()).intValue();
             mc.thePlayer.removePotionEffectClient(effectId);
        }
    }
}
