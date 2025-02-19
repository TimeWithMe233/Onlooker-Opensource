package dev.onlooker.gui.clickguis.searchbar;

import dev.onlooker.Client;
import dev.onlooker.gui.Screen;
import dev.onlooker.module.impl.display.HUDMod;
import dev.onlooker.utils.animations.Animation;
import dev.onlooker.utils.animations.Direction;
import dev.onlooker.utils.animations.impl.DecelerateAnimation;
import dev.onlooker.utils.misc.HoveringUtil;
import dev.onlooker.utils.objects.TextField;
import dev.onlooker.utils.render.ColorUtil;
import dev.onlooker.utils.render.RoundedUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import java.awt.*;
@Getter
@Setter
public class SearchBar implements Screen {

    private boolean focused, typing, hoveringBottomOfScreen;
    private final Animation focusAnimation = new DecelerateAnimation(175, 1).setDirection(Direction.BACKWARDS),
            hoverAnimation = new DecelerateAnimation(175, 1).setDirection(Direction.BACKWARDS),
            openAnimation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);
    public Color categoryColor = new Color(30, 31, 35,80);
    public Color categoryColor2 = new Color(30, 31, 35,80);
    private final TextField searchField = new TextField(tenacityFont18);
    private float alpha;

    @Override
    public void initGui() {
        openAnimation.setDirection(Direction.FORWARDS);
        searchField.setText("");
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            searchField.setFocused(false);
            return;
        }

        if (GuiScreen.isCtrlKeyDown() && keyCode == Keyboard.KEY_F) {
            searchField.setFocused(true);
            Client.INSTANCE.getModuleCollection().getModules().forEach(module -> module.setExpanded(false));
            return;
        }

        searchField.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if (HUDMod.watermarkTheme.is("Light")) {
            categoryColor = new Color(205, 205, 205, 80);
            categoryColor2 = new Color(205, 205, 205, 200);
        }else {
            categoryColor = new Color(0, 0, 0, 80);
            categoryColor2 = new Color(0, 0, 0, 200);
        }
        focused = searchField.isFocused() || !searchField.getText().isEmpty();
        typing = searchField.isFocused();
        ScaledResolution sr = new ScaledResolution(mc);

        float width = sr.getScaledWidth(), height = sr.getScaledHeight();
        hoveringBottomOfScreen = HoveringUtil.isHovering(width / 2f - 120,
                sr.getScaledHeight() - (100), 240, 100, mouseX, mouseY);


        hoverAnimation.setDirection(hoveringBottomOfScreen && !focused ? Direction.FORWARDS : Direction.BACKWARDS);
        focusAnimation.setDirection(focused ? Direction.FORWARDS : Direction.BACKWARDS);
        float focusAnim = focusAnimation.getOutput().floatValue();

        float hover = hoverAnimation.getOutput().floatValue();


        float openAnim = Math.min(1, alpha);
        float searchAlpha = Math.min(1, hover + focusAnim);
        tenacityFont26.drawCenteredString("Do §lCTRL§r+§lF§r to open the search bar", sr.getScaledWidth() / 2f, sr.getScaledHeight() - 75,
                ColorUtil.applyOpacity(-1, (.3f * (1 - searchAlpha)) * openAnim));


        searchField.setWidth(200);
        searchField.setHeight(25);
        searchField.setFont(tenacityFont24);
        searchField.setXPosition(sr.getScaledWidth() / 2f - 100);

        searchField.setYPosition(sr.getScaledHeight() - (70 + (25 * hover) + (60 * focusAnim)));
        searchField.setRadius(5);
        searchField.setAlpha(Math.max(hover * .85f, focusAnim));
        searchField.setTextAlpha(searchField.getAlpha());
        searchField.setFill(categoryColor);
        searchField.setOutline(null);
        searchField.setBackgroundText("Search");
        searchField.drawTextBox();


    }

    public void drawEffects() {
        ScaledResolution sr = new ScaledResolution(mc);
        float hover = hoverAnimation.getOutput().floatValue();
        float focusAnim = focusAnimation.getOutput().floatValue();

        float openAnim = Math.min(1, alpha);
        float searchAlpha = Math.min(1, hover + focusAnim);

        tenacityFont26.drawCenteredString("Do §lCTRL§r+§lF§r to open the search bar", sr.getScaledWidth() / 2f, sr.getScaledHeight() - 75,
                ColorUtil.applyOpacity(Color.BLACK, (1 * (1 - searchAlpha)) * openAnim));


        RoundedUtil.drawRound(searchField.getXPosition(), searchField.getYPosition(), 200, searchField.getHeight(), 5,
                ColorUtil.applyOpacity(Color.BLACK, Math.max(hover, focusAnim)));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean focused = searchField.isFocused();
        searchField.mouseClicked(mouseX, mouseY, button);
        if (!focused && searchField.isFocused()) {
            Client.INSTANCE.getModuleCollection().getModules().forEach(module -> module.setExpanded(false));
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }
}
