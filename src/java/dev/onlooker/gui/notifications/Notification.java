package dev.onlooker.gui.notifications;

import dev.onlooker.Client;
import dev.onlooker.module.impl.display.HUDMod;
import dev.onlooker.utils.Utils;
import dev.onlooker.utils.animations.Animation;
import dev.onlooker.utils.animations.impl.DecelerateAnimation;
import dev.onlooker.utils.font.FontUtil;
import dev.onlooker.utils.render.ColorUtil;
import dev.onlooker.utils.render.RenderUtil;
import dev.onlooker.utils.render.RoundedUtil;
import dev.onlooker.utils.render.StencilUtil;
import dev.onlooker.utils.skidfont.FontManager;
import dev.onlooker.utils.time.TimerUtil;
import lombok.Getter;

import java.awt.*;

@Getter
public class Notification implements Utils {

    private final NotificationType notificationType;
    private final String title, description;
    private final float time;
    private final TimerUtil timerUtil;
    private final Animation animation;

    public Notification(NotificationType type, String title, String description) {
        this(type, title, description, NotificationManager.getToggleTime());
    }

    public Notification(NotificationType type, String title, String description, float time) {
        this.title = title;
        this.description = description;
        this.time = (long) (time * 1000);
        timerUtil = new TimerUtil();
        this.notificationType = type;
        animation = new DecelerateAnimation(250, 1);
    }


    public void drawonlooker(float x, float y, float width, float height) {
        Color color = this.getNotificationType().getColor();
        Color transparentColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 108);
        HUDMod hudMod = Client.INSTANCE.getModuleCollection().getModule(HUDMod.class);
        Color color2 = hudMod.getThemeBgColor();
        RoundedUtil.drawRound(x - 8, y, width + 4, height, 6.0f, color2);
        RoundedUtil.drawRound(x - 4, y + 4, ((getTime() - getTimerUtil().getTime()) / getTime()) * width,
                height-7.6f, 2.5f, transparentColor);

        FontUtil.iconFont26.drawString(getNotificationType().getIcon(), x - 2, (y + FontUtil.iconFont26.getMiddleOfBox(height) + 1.25f), -1);
        FontManager.PingFang_bold18.drawString(getDescription(), x + 12, y + -10.2f + FontManager.PingFang_bold18.getHeight(), new Color(245, 245, 245, 215));
    }

    public void bluronlooker(float x, float y, float width, float height, float alpha) {
        HUDMod hudMod = Client.INSTANCE.getModuleCollection().getModule(HUDMod.class);
        RoundedUtil.drawRound(x - 8, y, width + 4, height, 6.0f, hudMod.getThemeShaderColor());
    }

    public void drawDefault(float x, float y, float width, float height, float alpha) {
        Color notificationColor = ColorUtil.applyOpacity(getNotificationType().getColor(), alpha);
        //Icon
        FontUtil.iconFont35.drawString(getNotificationType().getIcon(), x - 4, (y + FontUtil.iconFont35.getMiddleOfBox(height) + 1), notificationColor);

        RoundedUtil.drawRound(x - 8, y, width + 10, height, 1.5f, new Color(0, 0, 0, 100));
        tenacityBoldFont14.drawString(getTitle(), x + 16, y + 2.5f, new Color(255, 255, 255, 125));
        RoundedUtil.drawRound(x - 8, y + height, ((getTime() - getTimerUtil().getTime()) / getTime()) * width + 10, 0.5f, 1.5f, notificationType.getColor());

        FontManager.PingFang_bold18.drawString(getDescription(), x + 16, y + -9.5f + FontManager.PingFang_bold18.getHeight(), new Color(255, 255, 255, 125));
    }

    public void blurDefault(float x, float y, float width, float height, float alpha) {
        RoundedUtil.drawRound(x - 8, y + height, ((getTime() - getTimerUtil().getTime()) / getTime()) * width + 10, 0.5f, 1.0f, notificationType.getColor());
        RoundedUtil.drawRound(x - 8, y, width + 10, height, 1.5f, Color.BLACK);
    }
}
