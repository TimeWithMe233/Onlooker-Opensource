package dev.onlooker.module.impl.display;

import dev.onlooker.gui.notifications.Notification;
import dev.onlooker.gui.notifications.NotificationManager;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.settings.impl.BooleanSetting;
import dev.onlooker.module.settings.impl.ModeSetting;
import dev.onlooker.module.settings.impl.NumberSetting;
import dev.onlooker.utils.Utils;
import dev.onlooker.utils.animations.Animation;
import dev.onlooker.utils.animations.Direction;
import dev.onlooker.utils.skidfont.FontManager;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;

public class NotificationsMod extends Module {
    private final NumberSetting time = new NumberSetting("Time on Screen", 2, 10, 1, .5);
    public static final ModeSetting mode = new ModeSetting("Mode", "OnLooker", "OnLooker","Default");
    public static final BooleanSetting onlyTitle = new BooleanSetting("Only Title", false);
    public static final BooleanSetting toggleNotifications = new BooleanSetting("Show Toggle", true);

    public NotificationsMod() {
        super("Notifications", Category.DISPLAY, "Allows you to customize the client notifications");
        onlyTitle.addParent(mode, modeSetting -> modeSetting.is("Default"));
        this.addSettings(time, mode, onlyTitle, toggleNotifications);
        if (!enabled) this.toggleSilent();
    }

    public void render() {
        float yOffset = 0;
        int notificationHeight = 0;
        int notificationWidth;
        int actualOffset = 0;
        ScaledResolution sr = new ScaledResolution(mc);

        NotificationManager.setToggleTime(time.getValue().floatValue());

        for (Notification notification : NotificationManager.getNotifications()) {
            Animation animation = notification.getAnimation();
            animation.setDirection(notification.getTimerUtil().hasTimeElapsed((long) notification.getTime()) ? Direction.BACKWARDS : Direction.FORWARDS);

            if (animation.finished(Direction.BACKWARDS)) {
                NotificationManager.getNotifications().remove(notification);
                continue;
            }

            float x, y;

            switch (mode.getMode()) {
                case "Default":
                    animation.setDuration(250);
                    actualOffset = 10;
                    notificationHeight = 24;
                    notificationWidth = (int) Math.max(Utils.tenacityBoldFont22.getStringWidth(notification.getTitle()), Utils.tenacityBoldFont18.getStringWidth(notification.getDescription())) + 25;

                    x = sr.getScaledWidth() - (notificationWidth + 5) * animation.getOutput().floatValue();
                    y = sr.getScaledHeight() - (yOffset + 18 + HUDMod.offsetValue + notificationHeight * animation.getOutput().floatValue() + (15 * GuiChat.openingAnimation.getOutput().floatValue()));

                    notification.drawDefault(x, y, notificationWidth, notificationHeight, animation.getOutput().floatValue());
                    break;
                case "OnLooker":
                    animation.setDuration(350);
                    actualOffset = 15;
                    notificationHeight = 24;
                    notificationWidth = Math.max(FontManager.PingFang_bold22.getStringWidth(notification.getTitle()), FontManager.PingFang_bold18.getStringWidth(notification.getDescription())) + 25;

                    x = sr.getScaledWidth() - (notificationWidth + 5) * animation.getOutput().floatValue();
                    y = sr.getScaledHeight() - (yOffset + 40 + HUDMod.offsetValue + notificationHeight * animation.getOutput().floatValue() + (15 * GuiChat.openingAnimation.getOutput().floatValue()));

                    notification.drawonlooker(x, y, notificationWidth, notificationHeight);

                }
            yOffset += (notificationHeight + actualOffset) * animation.getOutput().floatValue();

        }
    }

    public void renderEffects() {
        float yOffset = 0;
        int notificationHeight = 0;
        int notificationWidth;
        int actualOffset = 0;
        ScaledResolution sr = new ScaledResolution(mc);


        for (Notification notification : NotificationManager.getNotifications()) {
            Animation animation = notification.getAnimation();
            animation.setDirection(notification.getTimerUtil().hasTimeElapsed((long) notification.getTime()) ? Direction.BACKWARDS : Direction.FORWARDS);

            if (animation.finished(Direction.BACKWARDS)) {
                NotificationManager.getNotifications().remove(notification);
                continue;
            }

            float x, y;

            switch (mode.getMode()) {
                case "Default":
                    actualOffset = 10;
                    notificationHeight = 24;
                    notificationWidth = Math.max(FontManager.PingFang_bold22.getStringWidth(notification.getTitle()), FontManager.PingFang_bold18.getStringWidth(notification.getDescription())) + 25;

                    x = sr.getScaledWidth() - (notificationWidth + 5) * animation.getOutput().floatValue();
                    y = sr.getScaledHeight() - (yOffset + 18 + HUDMod.offsetValue + notificationHeight * animation.getOutput().floatValue() + (15 * GuiChat.openingAnimation.getOutput().floatValue()));

                    notification.blurDefault(x, y, notificationWidth, notificationHeight, animation.getOutput().floatValue());
                    break;
                case "OnLooker":
                    actualOffset = 15;
                    notificationHeight = 24;
                    notificationWidth = Math.max(FontManager.PingFang_bold22.getStringWidth(notification.getTitle()), FontManager.PingFang_bold18.getStringWidth(notification.getDescription())) + 25;

                    x = sr.getScaledWidth() - (notificationWidth + 5) * animation.getOutput().floatValue();
                    y = sr.getScaledHeight() - (yOffset + 40 + HUDMod.offsetValue + notificationHeight * animation.getOutput().floatValue() + (15 * GuiChat.openingAnimation.getOutput().floatValue()));

                    notification.bluronlooker(x, y, notificationWidth, notificationHeight, animation.getOutput().floatValue());
                    break;
            }


            yOffset += (notificationHeight + actualOffset) * animation.getOutput().floatValue();

        }
    }


}
