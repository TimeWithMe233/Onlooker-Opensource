package dev.onlooker.module;

import dev.onlooker.utils.font.FontUtil;
import dev.onlooker.utils.objects.Drag;
import dev.onlooker.utils.objects.Scroll;
import lombok.Getter;

public enum Category {

    COMBAT("Combat", FontUtil.BOMB),
    MOVEMENT("Movement", FontUtil.WHEELCHAIR),
    RENDER("Render", FontUtil.EYE),
    WORLD("World", FontUtil.WORLD),
    PLAYER("Player", FontUtil.PERSON),
    DISPLAY("Display",FontUtil.DISPLAY),
    MISC("Misc", FontUtil.LIST);

    public final String name;
    public final String icon;
    public final int posX;
    public final boolean expanded;

    @Getter
    private final Scroll scroll = new Scroll();

    @Getter
    private final Drag drag;
    public int posY = 20;

    Category(String name, String icon) {
        this.name = name;
        this.icon = icon;
        posX = 20 + (Module.categoryCount * 120);
        drag = new Drag(posX, posY);
        expanded = true;
        Module.categoryCount++;
    }

}
