package dev.onlooker.gui.clickguis.sidegui;

import dev.onlooker.gui.Screen;
import dev.onlooker.gui.clickguis.sidegui.forms.Form;
import dev.onlooker.gui.clickguis.sidegui.utils.TooltipObject;
import dev.onlooker.utils.animations.Animation;
import dev.onlooker.utils.animations.Direction;
import dev.onlooker.utils.animations.impl.DecelerateAnimation;
import dev.onlooker.utils.misc.HoveringUtil;
import dev.onlooker.utils.objects.Drag;
import dev.onlooker.utils.time.TimerUtil;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Getter
public class SideGUI implements Screen {

    @Setter
    private boolean focused;
    private final Animation openAnimation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);
    private final Animation hoverAnimation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);
    private final Animation clickAnimation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);
    private SideGUIHotbar hotbar = new SideGUIHotbar();
    private static Form currentForm;
    private final List<TooltipObject> tooltips = new ArrayList<>();
    public boolean typing = false;
    private final Color greenEnabledColor = new Color(70, 220, 130);
    private final Color redBadColor = new Color(209, 56, 56);
    private final Animation formFadeAnimation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);

    private Drag drag;
    private TimerUtil timerUtil;

    @Override
    public void onDrag(int mouseX, int mouseY) {
        if (drag != null) {
            drag.onDraw(mouseX, mouseY);
        }
    }

    @Override
    public void initGui() {
    }


    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }

    private float animateX = 0;

    @Override
    public void drawScreen(int mouseX, int mouseY) {
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
    }

    public void addTooltip(TooltipObject tooltip) {
        if (tooltips.contains(tooltip)) return;
        tooltips.add(tooltip);
    }

    public Form displayForm(String form) {
        if (form == null) {
            currentForm.clear();
            formFadeAnimation.setDirection(Direction.BACKWARDS);
            return null;
        }

        formFadeAnimation.setDirection(Direction.FORWARDS);
        return currentForm;
    }

    public static boolean isHovering(float x, float y, float width, float height, int mouseX, int mouseY) {
        return currentForm == null && HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);
    }

}
