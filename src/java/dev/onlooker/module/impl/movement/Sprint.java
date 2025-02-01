package dev.onlooker.module.impl.movement;

import dev.onlooker.event.impl.player.UpdateEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import net.minecraft.client.settings.KeyBinding;

public class Sprint extends Module {
    public Sprint() {
        super("Sprint", Category.MOVEMENT, "Sprints automatically");
        if (!enabled) this.toggleSilent();
    }
    public static boolean sprint = true;
    @Override
    public void onUpdateEvent(UpdateEvent event) {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
    }
}