package dev.onlooker.gui.clickguis.sidegui.panels;

import dev.onlooker.gui.Screen;
import dev.onlooker.module.impl.display.HUDMod;
import dev.onlooker.utils.render.ColorUtil;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
public abstract class Panel implements Screen {
    private float x, y, width, height, alpha;

    public Color getTextColor() {
        return ColorUtil.applyOpacity(Color.WHITE, alpha);
    }

    public Color getAccentColor() {
        return ColorUtil.applyOpacity(HUDMod.getClientColors().getFirst(), alpha);
    }

}
