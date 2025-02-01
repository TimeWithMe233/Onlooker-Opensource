package dev.onlooker.module.impl.render;

import dev.onlooker.event.impl.render.HurtCamEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;

public class NoHurtCam extends Module {

    public NoHurtCam() {
        super("NoHurtCam", Category.RENDER, "removes shaking after being hit");
    }

    @Override
    public void onHurtCamEvent(HurtCamEvent e) {
        e.cancel();
    }

}
