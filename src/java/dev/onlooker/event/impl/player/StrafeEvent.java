package dev.onlooker.event.impl.player;


import dev.onlooker.event.Event;

public class StrafeEvent extends Event {
    public float strafe;
    public float forward;
    public float friction;

    public StrafeEvent(float strafe, float forward, float friction) {
        this.strafe = strafe;
        this.forward = forward;
        this.friction = friction;
    }
}
