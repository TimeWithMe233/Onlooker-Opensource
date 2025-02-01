package dev.onlooker.gui.clickguis.click.modern;

import dev.onlooker.Client;
import dev.onlooker.module.Module;
import dev.onlooker.module.impl.display.HUDMod;
import dev.onlooker.module.impl.display.ClickGUIMod;
import dev.onlooker.gui.clickguis.click.modern.components.ModuleRect;
import dev.onlooker.utils.animations.Animation;
import dev.onlooker.utils.animations.Direction;
import dev.onlooker.utils.animations.impl.DecelerateAnimation;
import dev.onlooker.utils.misc.HoveringUtil;
import dev.onlooker.utils.objects.PasswordField;
import dev.onlooker.utils.objects.Scroll;
import dev.onlooker.utils.render.RenderUtil;
import dev.onlooker.utils.render.StencilUtil;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchPanel extends Panel {


    private final PasswordField searchBar;
    private final boolean reinit = false;
    public Animation expandAnim2;
    Scroll settingScroll = new Scroll();
    private List<Module> modules;
    private List<ModuleRect> moduleRects;
    public Color categoryColor = new Color(30, 31, 35,80);
    public Color categoryColor2 = new Color(30, 31, 35,80);
    private String text = " ";
    private Scroll scroll;
    private ModuleRect currentlySelected;
    private HashMap<Module, SettingsPanel> settingsPanelHashMap;
    private boolean rightClicked = false;

    public SearchPanel(PasswordField passwordField) {
        searchBar = passwordField;
    }

    @Override
    public void initGui() {
        expandAnim2 = new DecelerateAnimation(300, 1, Direction.FORWARDS);
        expandAnim2.setDirection(Direction.BACKWARDS);
        moduleRects = new ArrayList<>();
        modules = Client.INSTANCE.getModuleCollection().getModules();
        if (settingsPanelHashMap == null) {
            settingsPanelHashMap = new HashMap<>();
            for (Module module : Client.INSTANCE.getModuleCollection().getModules()) {
                settingsPanelHashMap.put(module, new SettingsPanel(module));
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        moduleRects.forEach(moduleRect -> moduleRect.keyTyped(typedChar, keyCode));

        if (currentlySelected != null) {
            settingsPanelHashMap.get(currentlySelected.module).keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if (ModernClickGui.searching) {
            expandAnim2.setDirection(rightClicked ? Direction.FORWARDS : Direction.BACKWARDS);
            List<Module> possibleMods = Client.INSTANCE.getModuleCollection().getModulesThatContainText(text);
            if (!text.equals(searchBar.getText())) {
                moduleRects.clear();
                scroll = new Scroll();
                possibleMods.forEach(module -> moduleRects.add(new ModuleRect(module)));
                moduleRects.forEach(ModuleRect::initGui);
                text = searchBar.getText();
                return;
            }

            StencilUtil.initStencilToWrite();
            Gui.drawRect2(x, y + 50, 335, 255 - 65, -1);
            StencilUtil.readStencilBuffer(1);

            if (HoveringUtil.isHovering(x, bigRecty, 370, 225, mouseX, mouseY)) {
                scroll.onScroll(35);
            }

            scroll.setMaxScroll(Math.max(0, (moduleRects.size() - 2) * 50));
            int spacing = 0;
            for (ModuleRect moduleRect : moduleRects) {
                moduleRect.rectWidth = 305;
                moduleRect.x = this.x + 10;
                moduleRect.y = this.y + 70 + spacing + scroll.getScroll();
                moduleRect.bigRecty = this.bigRecty;
                moduleRect.rectOffset = 70;
                moduleRect.drawSettingThing = currentlySelected == moduleRect;
                if (moduleRect.rightClicked) {
                    if (currentlySelected == moduleRect) {
                        rightClicked = false;
                        currentlySelected = null;
                        moduleRect.rightClicked = false;
                        continue;
                    } else {
                        rightClicked = true;
                    }
                    settingScroll = new Scroll();
                    currentlySelected = moduleRect;
                    moduleRect.rightClicked = false;
                }
                moduleRect.drawScreen(mouseX, mouseY);
                spacing += 50;
            }
            StencilUtil.uninitStencilBuffer();

            if (currentlySelected != null) {
                if (HoveringUtil.isHovering(x + (float) (335), y, 135, 250, mouseX, mouseY)) {
                    settingScroll.onScroll(35);
                }

                StencilUtil.initStencilToWrite();
                Gui.drawRect2(x, y - 15, (float) (370 + (125 * expandAnim2.getOutput().floatValue())), 255, -1);
                StencilUtil.readStencilBuffer(1);
                //  settingScroll.setMaxScroll(Math.max(0,(Mo - 4) * 50));
                float newX = x + 5 + 335;
                if (HUDMod.watermarkTheme.is("Light")) {
                    categoryColor = new Color(205, 205, 205, 80);
                    categoryColor2 = new Color(205, 205, 205, 200);
                }else {
                    categoryColor = new Color(0, 0, 0, 80);
                    categoryColor2 = new Color(0, 0, 0, 200);
                }
                RenderUtil.renderRoundedRect(newX, y - 15, 130, 255, 10, categoryColor.getRGB());
                Gui.drawGradientRect2(newX, y + 5, 130, 8, new Color(0, 0, 0, 70).getRGB(), new Color(0, 0, 0, 1).getRGB());
                tenacityFont24.drawCenteredString(currentlySelected.module.getName(), newX + 125 / 2f, y - 10, -1);

                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                RenderUtil.scissor(newX, y + 5.5, 135 * expandAnim2.getOutput().floatValue(), 255);

                SettingsPanel settingsPanel = settingsPanelHashMap.get(currentlySelected.module);
                settingScroll.setMaxScroll(Math.max(0, settingsPanel.maxScroll - 100));
                settingsPanel.x = x + 335;
                settingsPanel.y = y - 15 + settingScroll.getScroll();
                settingsPanel.drawScreen(mouseX, mouseY);

                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                StencilUtil.uninitStencilBuffer();
            }

            ClickGUIMod.modernClickGui.adjustWidth((125 * expandAnim2.getOutput().floatValue()));

        } else {
            currentlySelected = null;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        moduleRects.forEach(moduleRect -> moduleRect.mouseClicked(mouseX, mouseY, button));

        if (currentlySelected != null) {
            settingsPanelHashMap.get(currentlySelected.module).mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        moduleRects.forEach(moduleRect -> moduleRect.mouseReleased(mouseX, mouseY, state));

        if (currentlySelected != null) {
            settingsPanelHashMap.get(currentlySelected.module).mouseReleased(mouseX, mouseY, state);
        }
    }

}
