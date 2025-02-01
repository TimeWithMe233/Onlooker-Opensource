package dev.onlooker.utils.skidfont;

import java.awt.*;
import java.io.InputStream;

public class FontManager {
    public static FontDrawer Naven10 = new FontDrawer(getFont("Naven.ttf", 10), true, true);
    public static FontDrawer Naven16 = new FontDrawer(getFont("Naven.ttf", 16), true, true);
    public static FontDrawer Naven18 = new FontDrawer(getFont("Naven.ttf", 18), true, true);
    public static FontDrawer Naven20 = new FontDrawer(getFont("Naven.ttf", 20), true, true);
    public static FontDrawer Naven22 = new FontDrawer(getFont("Naven.ttf", 22), true, true);
    public static FontDrawer Naven25 = new FontDrawer(getFont("Naven.ttf", 25), true, true);
    public static FontDrawer Naven34 = new FontDrawer(getFont("Naven.ttf", 34), true, true);
    public static FontDrawer Naven64 = new FontDrawer(getFont("Naven.ttf", 64), true, true);

    public static FontDrawer GenShinGothic18 = new FontDrawer(getFont("x-GenShinGothic-Medium.ttf", 18), true, true);
    public static FontDrawer GenShinGothic20 = new FontDrawer(getFont("x-GenShinGothic-Medium.ttf", 20), true, true);
    public static FontDrawer GenShinGothic24 = new FontDrawer(getFont("x-GenShinGothic-Medium.ttf", 24), true, true);

    public static FontDrawer PingFang_bold16 = new FontDrawer(getFont("PingFang-bold.ttf", 16), true, true);
    public static FontDrawer PingFang_bold13 = new FontDrawer(getFont("PingFang-bold.ttf", 13), true, true);
    public static FontDrawer PingFang_bold18 = new FontDrawer(getFont("PingFang-bold.ttf", 18), true, true);
    public static FontDrawer PingFang_bold20 = new FontDrawer(getFont("PingFang-bold.ttf", 20), true, true);
    public static FontDrawer PingFang_bold22 = new FontDrawer(getFont("PingFang-bold.ttf", 22), true, true);
    public static FontDrawer PingFang_bold28 = new FontDrawer(getFont("PingFang-bold.ttf", 28), true, true);
    public static FontDrawer PingFang_bold34 = new FontDrawer(getFont("PingFang-bold.ttf", 34), true, true);
    public static FontDrawer PingFang_bold80 = new FontDrawer(getFont("PingFang-bold.ttf", 80), true, true);

    public static FontDrawer icon22 = new FontDrawer(getFont("iconfont.ttf", 22), true, true);

    public static Font getFont(String name, int size) {
        Font font;
        try {
            InputStream is = FontManager.class.getResourceAsStream("/assets/minecraft/OnLooker/font/" + name);
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
            System.out.println("Loading " + name);
        } catch (Exception ex) {
            System.out.println("Error loading font " + name);
            font = new Font("Arial", Font.PLAIN, size);
        }
        return font;
    }
}