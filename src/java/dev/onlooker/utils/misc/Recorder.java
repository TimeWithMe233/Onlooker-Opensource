package dev.onlooker.utils.misc;

import dev.onlooker.event.Event;
import dev.onlooker.event.EventListener;
import net.minecraft.entity.EntityLivingBase;

public class Recorder implements EventListener {
    public EntityLivingBase syncEntity = null;
    public int killCounts = 0;
    public int totalPlayed = 0;
    public int win = 0;
    public int ban = 0;
    public static long startTime = System.currentTimeMillis();

    @Override
    public void onEvent(Event event) {
    }
}

