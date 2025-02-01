package dev.onlooker.module.impl.display;

import dev.onlooker.gui.clickguis.click.compact.CompactClickgui;
import dev.onlooker.gui.clickguis.click.dropdown.DropdownClickGUI;
import dev.onlooker.gui.clickguis.click.modern.ModernClickGui;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.settings.impl.BooleanSetting;
import dev.onlooker.module.settings.impl.ModeSetting;
import dev.onlooker.module.settings.impl.NumberSetting;
import dev.onlooker.utils.render.Theme;
import org.lwjgl.input.Keyboard;

public class ClickGUIMod extends Module {

    public static final ModeSetting clickguiMode = new ModeSetting("ClickGui", "Modern", "Dropdown", "Modern","Compact");
    public static final ModeSetting scrollMode = new ModeSetting("Scroll Mode", "Screen Height", "Screen Height", "Value");
    public static final BooleanSetting gradient = new BooleanSetting("Gradient", false);
    public static final BooleanSetting transparent = new BooleanSetting("Transparent", false);
    public static final BooleanSetting walk = new BooleanSetting("Allow Movement", true);
    public static final NumberSetting radius = new NumberSetting("Radius", 1, 10, 0, 1);
    public static final NumberSetting alpha = new NumberSetting("Alpha", 0.25f, 1, 0.01, 0.01);
    public static final NumberSetting clickHeight = new NumberSetting("Tab Height", 250, 500, 100, 1);
    public static final BooleanSetting rescale = new BooleanSetting("Rescale GUI", true);


    public static final DropdownClickGUI dropdownClickGui = new DropdownClickGUI();
    public static final ModernClickGui modernClickGui = new ModernClickGui();
    public static final CompactClickgui compactClickgui = new CompactClickgui();

    public static int prevGuiScale;
    private Category activeCategory2 = Category.COMBAT;

    public ClickGUIMod() {
        super("ClickGUI", Category.DISPLAY, "Displays modules");
        clickHeight.addParent(scrollMode, selection -> selection.is("Value"));

        gradient.addParent(clickguiMode, selection -> selection.is("Dropdown") && !Theme.getCurrentTheme().isGradient());
        transparent.addParent(clickguiMode, selection -> selection.is("Dropdown"));
        scrollMode.addParent(clickguiMode, selection -> selection.is("Dropdown"));

        this.addSettings(clickguiMode, scrollMode, gradient, transparent, walk, radius, alpha, clickHeight, rescale);
        this.setKey(Keyboard.KEY_RSHIFT);
    }

    public void toggle() {
        this.onEnable();
    }

    public void onEnable() {
        if (rescale.isEnabled()) {
            prevGuiScale = mc.gameSettings.guiScale;
            mc.gameSettings.guiScale = 2;
        }
        switch (clickguiMode.getMode()) {
            case "Dropdown":
                mc.displayGuiScreen(dropdownClickGui);
                break;
            case "Modern":
                mc.displayGuiScreen(modernClickGui);
                break;
            case "Compact":
                mc.displayGuiScreen(compactClickgui);
                break;
        }

    }
    public Category getActiveCategory() {
        return activeCategory2;
    }

    public void setActiveCategory(Category activeCategory) {
        this.activeCategory2 = activeCategory;
    }
}
