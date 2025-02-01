package dev.onlooker.module.impl.player;

import dev.onlooker.event.impl.player.SafeWalkEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;

public final class SafeWalk extends Module {
    @Override
    public void onSafeWalkEvent(SafeWalkEvent e) {
        if (mc.thePlayer == null) return;
        e.setSafe(true);
    }

    public SafeWalk() {
        super("SafeWalk", Category.PLAYER, "prevents walking off blocks");
    }

}
