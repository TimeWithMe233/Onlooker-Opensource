package dev.onlooker.gui.clickguis.click.modern.components;

import dev.onlooker.module.Category;
import dev.onlooker.module.impl.display.HUDMod;
import dev.onlooker.utils.animations.Animation;
import dev.onlooker.utils.animations.Direction;
import dev.onlooker.utils.animations.impl.DecelerateAnimation;
import dev.onlooker.utils.animations.impl.EaseInOutQuad;
import dev.onlooker.utils.font.FontUtil;
import dev.onlooker.utils.misc.HoveringUtil;
import dev.onlooker.utils.render.ColorUtil;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

import static dev.onlooker.utils.Utils.tenacityFont24;

public class CategoryButton extends Component {
    public final Category category;
    public Category currentCategory;
    private Animation hoverAnimation;
    private Animation enableAnimation;
    public Animation expandAnimation;
    public Color categoryColor = new Color(30, 31, 35,80);
    public Color categoryColor2 = new Color(30, 31, 35,80);
    public Color categoryColor3 = new Color(30, 31, 35,80);

    public CategoryButton(Category category) {
        this.category = category;
    }

    @Override
    public void initGui() {
        hoverAnimation = new EaseInOutQuad(200, 1);
        enableAnimation = new DecelerateAnimation(250, 1);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {

        if (HUDMod.watermarkTheme.is("Light")) {
            categoryColor = new Color(205, 205, 205, 80);
            categoryColor2 = new Color(205, 205, 205, 100);
            categoryColor3 = new Color(205, 205, 205, 100);
        }else {
            categoryColor = new Color(0, 0, 0, 80);
            categoryColor2 = new Color(0, 0, 0, 100);
            categoryColor3 = new Color(205, 205, 205, 100);
        }

        boolean hovering = HoveringUtil.isHovering(x, y - 3, 83 -
                (expandAnimation.getDirection().forwards() ? 62 : 0), 18, mouseX, mouseY);

        hoverAnimation.setDirection(hovering ? Direction.FORWARDS : Direction.BACKWARDS);
        hoverAnimation.setDuration(hovering ? 200 : 350);
        enableAnimation.setDirection(currentCategory == category ? Direction.FORWARDS : Direction.BACKWARDS);

        int color = ColorUtil.interpolateColor(categoryColor3, new Color(115, 115, 115), (float) hoverAnimation.getOutput().floatValue());
        color = ColorUtil.interpolateColor(new Color(color), new Color(-1), (float) enableAnimation.getOutput().floatValue());


        float adjustment = 0;
        if (category == Category.COMBAT) adjustment = 2.5f;

        GlStateManager.color(1, 1, 1);
        FontUtil.iconFont35.drawCenteredString(category.icon, x + 10 + adjustment, y, color);
        GlStateManager.color(1, 1, 1);


        float xDiff = 10 * expandAnimation.getOutput().floatValue();
        tenacityFont24.drawString(category.name, x + 27 + xDiff, y, color);


    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean hovering = HoveringUtil.isHovering(x, y - 3, 83 -
                (expandAnimation.getDirection().forwards() ? 62 : 0), 18, mouseX, mouseY);
        this.hovering = hovering && button == 0;

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {

    }
}
