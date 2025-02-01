package dev.onlooker.utils.player;


import dev.onlooker.event.ListenerAdapter;
import dev.onlooker.event.impl.player.MotionEvent;

import static dev.onlooker.utils.Utils.mc;

public final class FallDistanceComponent extends ListenerAdapter {

    public static float distance;
    private float lastDistance;

    @Override
    public void onMotionEvent(MotionEvent e) {
        if (e.isPre()) {
            final float fallDistance = mc.thePlayer.fallDistance;

            if (fallDistance == 0) {
                distance = 0;
            }

            distance += fallDistance - lastDistance;
            lastDistance = fallDistance;
        }
    }
}
