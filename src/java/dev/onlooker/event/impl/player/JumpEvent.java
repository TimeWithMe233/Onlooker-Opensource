package dev.onlooker.event.impl.player;

import dev.onlooker.event.Event;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class JumpEvent extends Event {
    private float yaw;
    private float jumpMotion;
}
