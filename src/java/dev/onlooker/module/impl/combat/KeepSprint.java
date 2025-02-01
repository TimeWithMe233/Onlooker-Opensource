package dev.onlooker.module.impl.combat;

import dev.onlooker.event.impl.player.KeepSprintEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;

public final class KeepSprint extends Module {

    public KeepSprint() {
        super("KeepSprint", Category.COMBAT, "Stops sprint reset after hitting");
    }

    @Override
    public void onKeepSprintEvent(KeepSprintEvent event) {
        event.cancel();
    }

}
