package dev.onlooker.gui.clickguis.sidegui.utils;

import dev.onlooker.gui.Screen;
import dev.onlooker.gui.clickguis.sidegui.SideGUI;
import dev.onlooker.utils.animations.Animation;
import dev.onlooker.utils.animations.Direction;
import dev.onlooker.utils.animations.impl.DecelerateAnimation;
import dev.onlooker.utils.font.CustomFont;
import dev.onlooker.utils.misc.HoveringUtil;
import dev.onlooker.utils.render.ColorUtil;
import dev.onlooker.utils.render.RoundedUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.awt.*;

@Setter
@Getter
@RequiredArgsConstructor
public class ActionButton implements Screen {
    private float x, y, width, height, alpha;
    private boolean bypass = false;
    public final String name;
    private boolean bold = false;
    private CustomFont font;
    private Color color = ColorUtil.tripleColor(55);
    private Runnable clickAction;

    private final Animation hoverAnimation = new DecelerateAnimation(250, 1);

    @Override
    public void initGui() {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        boolean hovering = SideGUI.isHovering(x, y, width, height, mouseX, mouseY);

        if (bypass) {
            hovering = HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);
        }

        hoverAnimation.setDirection(hovering ? Direction.FORWARDS : Direction.BACKWARDS);

        Color rectColor = ColorUtil.interpolateColorC(color, color.brighter(), hoverAnimation.getOutput().floatValue());
        RoundedUtil.drawRound(x, y, width, height, 5, ColorUtil.applyOpacity(rectColor, alpha));
        if (font != null) {
            font.drawCenteredString(name, x + width / 2f, y + font.getMiddleOfBox(height), ColorUtil.applyOpacity(-1, alpha));
        } else {
            if (bold) {
                tenacityBoldFont18.drawCenteredString(name, x + width / 2f, y + tenacityFont18.getMiddleOfBox(height), ColorUtil.applyOpacity(-1, alpha));
            } else {
                tenacityFont18.drawCenteredString(name, x + width / 2f, y + tenacityFont18.getMiddleOfBox(height), ColorUtil.applyOpacity(-1, alpha));
            }
        }

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean hovering = SideGUI.isHovering(x, y, width, height, mouseX, mouseY);
        if (bypass) {
            hovering = HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);
        }
        if (hovering && button == 0) {
            //TODO: remove this if statement
            if (clickAction != null) {
                clickAction.run();
            }
        }

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }
}
