package dev.onlooker.gui.mainmenu.button;

import dev.onlooker.gui.Screen;
import dev.onlooker.utils.animations.Animation;
import dev.onlooker.utils.animations.Direction;
import dev.onlooker.utils.animations.impl.DecelerateAnimation;
import dev.onlooker.utils.render.ColorUtil;
import dev.onlooker.utils.render.RenderUtil;
import dev.onlooker.utils.render.RoundedUtil;
import dev.onlooker.utils.render.blur.GaussianBlur;

import java.awt.*;

public class MenuButton implements Screen
{
    public final String text;
    private Animation hoverAnimation;
    public float x;
    public float y;
    public float width;
    public float height;
    public Runnable clickAction;

    public MenuButton(final String text) {
        this.text = text;
    }

    @Override
    public void initGui() {
        this.hoverAnimation = new DecelerateAnimation(500, 1.0);
    }

    @Override
    public void keyTyped(final char typedChar, final int keyCode) {
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY) {
        final boolean hovered = RenderUtil.isHovering(this.x, this.y, this.width, this.height, mouseX, mouseY);
        this.hoverAnimation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);
        GaussianBlur.startBlur();
        RoundedUtil.drawRoundOutline(x, this.y + -3, width, height + 2, (float)3.5, 0.0015f,  new Color(0, 0, 0, 0) , new Color(0, 0, 0, 0));
        GaussianBlur.endBlur(20,2);
        hoverAnimation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);
        float percent = (float) hoverAnimation.getOut();
        percent = Math.min(1F, percent);
        RoundedUtil.drawRoundOutline(x, y + -3, width, height + 2, (float)3.5, 0.0015f,  new Color(255, 255, 255,  (int) (75 * percent)) , new Color(0, 0, 0,  (int) (125 * percent)));
        fluxIcon28.drawCenteredString(this.text, this.x + this.width / 2.0f, this.y + tenacityBoldFont20.getMiddleOfBox(this.height) - 2, -1);
    }

    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int button) {
        final boolean hovered = RenderUtil.isHovering(this.x, this.y, this.width, this.height, mouseX, mouseY);
        if (hovered) {
            this.clickAction.run();
        }
    }

    @Override
    public void mouseReleased(final int mouseX, final int mouseY, final int state) {
    }
}