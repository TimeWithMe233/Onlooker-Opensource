package dev.onlooker.event.impl.player;

import dev.onlooker.event.Event;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

public class SafeWalkEvent extends Event {

    private boolean safe;

    public boolean isSafe() {
        return this.safe;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void setSafe(boolean safe) {
        this.safe = safe;
    }

}
