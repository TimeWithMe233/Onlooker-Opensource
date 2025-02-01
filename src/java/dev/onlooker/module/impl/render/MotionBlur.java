package dev.onlooker.module.impl.render;

import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.settings.impl.NumberSetting;

public class MotionBlur extends Module {
    public final NumberSetting blurAmount = new NumberSetting("Amount", 7.0, 10.0, 0.0, 0.1);
    public MotionBlur(){
        super("MotionBlur", Category.RENDER,"MotionBlur");
        addSettings(blurAmount);
        if (!enabled) this.toggleSilent();
    }
}
