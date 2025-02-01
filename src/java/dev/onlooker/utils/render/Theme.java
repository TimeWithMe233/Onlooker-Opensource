package dev.onlooker.utils.render;

import dev.onlooker.event.impl.render.Render2DEvent;
import dev.onlooker.module.impl.display.HUDMod;
import dev.onlooker.module.settings.impl.ModeSetting;
import dev.onlooker.utils.tuples.Pair;
import lombok.Getter;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum Theme {
    Silver("Silver", new Color(255, 255, 255), new Color(140, 140, 140)),
    COKE("COKE", new Color(198, 22, 22), new Color(0, 208, 255)),
    LiquidBounce("LiquidBounce", new Color(0, 111, 255), new Color(100, 100, 100,100)),
    ONLOOKER("OnLooker", new Color(205, 205, 205), new Color(100,100, 100), true),
    LAVENDER("Lavender", new Color(219, 166, 247), new Color(152, 115, 172)),
    CUSTOM_THEME("Custom Theme", HUDMod.color1.getColor(), HUDMod.color2.getColor());
    private static final Map<String, Theme> themeMap = new HashMap<>();
    private static boolean colorsSet;
    public static Color color1;
    public static Color color2;
    private final String name;
    private final Pair<Color, Color> colors;
    private final boolean gradient;

    public void onRender2DEvent(Render2DEvent event) {
        this.colorsSet = true;
    }

    Theme(String name, Color color, Color colorAlt) {
        this(name, color, colorAlt, false);
    }

    Theme(String name, Color color, Color colorAlt, boolean gradient) {
        this.name = name;
        colors = Pair.of(color, colorAlt);
        this.gradient = gradient;
    }

    public static void init() {
        Arrays.stream(values()).forEach(theme -> themeMap.put(theme.getName(), theme));
    }

    public Pair<Color, Color> getColors() {
        if (this.equals(Theme.CUSTOM_THEME)) {
            if (HUDMod.color1.isRainbow()) {
                return Pair.of(HUDMod.color1.getColor(), HUDMod.color1.getAltColor());
            } else return Pair.of(HUDMod.color1.getColor(), HUDMod.color2.getColor());
        } else return colors;
    }

    public static Pair<Color, Color> getThemeColors(String name) {
        return get(name).getColors();
    }

    public static ModeSetting getModeSetting(String name, String defaultValue) {
        return new ModeSetting(name, defaultValue, Arrays.stream(Theme.values()).map(Theme::getName).toArray(String[]::new));
    }

    public static Theme get(String name) {
        return themeMap.get(name);
    }

    public static Theme getCurrentTheme() {
        return Theme.get(HUDMod.theme.getMode());
    }
    public static int getColor(int offset) {
        if (!colorsSet) {
            colorsSet = true;
        }
        return ColorUtil.getColor3(HUDMod.getClientColors().getFirst(), HUDMod.getClientColors().getSecond(), HUDMod.fadespeed.getValue().intValue(), offset);
    }
}
