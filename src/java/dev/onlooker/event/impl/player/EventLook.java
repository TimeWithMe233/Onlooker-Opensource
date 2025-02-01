package dev.onlooker.event.impl.player;


import dev.onlooker.event.Event;
import dev.onlooker.utils.vector.Vector2f;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventLook
        extends Event {
    private Vector2f rotation;
    public EventLook(Vector2f rotation) {
        this.rotation = rotation;
    }

}

