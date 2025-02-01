package dev.onlooker.event.impl.player;

import dev.onlooker.event.Event;

public class ClickEvent extends Event {
    boolean fake;

    public ClickEvent(boolean fake) {
        this.fake = fake;
    }

    public boolean isFake() {
        return fake;
    }
}
