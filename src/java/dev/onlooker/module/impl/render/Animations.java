package dev.onlooker.module.impl.render;

import dev.onlooker.event.impl.player.MotionEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.settings.impl.BooleanSetting;
import dev.onlooker.module.settings.impl.ModeSetting;
import dev.onlooker.module.settings.impl.NumberSetting;

public final class Animations extends Module {
    public static final ModeSetting mode = new ModeSetting("Mode", "Swing", "Swing", "Swung", "Swong", "Swonk", "Swang", "Swank", "Swack", "E", "1.8 (Loser)");
    public static final NumberSetting SwingSpeed = new NumberSetting("SwingSpeed", 1.0, 10.0, -10.0, 1.0);

    public Animations() {
        super("Animations", Category.RENDER, "changes animations");
        this.addSettings(mode, SwingSpeed);
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        this.setSuffix(mode.getMode());
    }

}