package dev.onlooker.event.impl.player;

import dev.onlooker.event.Event;
import lombok.AllArgsConstructor;
import net.minecraft.entity.EntityLivingBase;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

@AllArgsConstructor
public class EventAttack extends Event {

    private final EntityLivingBase targetEntity;

    @Exclude(Strategy.NAME_REMAPPING)
    public EntityLivingBase getTargetEntity() {
        return targetEntity;
    }

}