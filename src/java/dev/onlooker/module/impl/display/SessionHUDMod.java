package dev.onlooker.module.impl.display;

import dev.onlooker.Client;
import dev.onlooker.event.impl.player.EventAttack;
import dev.onlooker.event.impl.player.LivingDeathEvent;
import dev.onlooker.event.impl.player.UpdateEvent;
import dev.onlooker.event.impl.render.Render2DEvent;
import dev.onlooker.event.impl.render.ShaderEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.utils.misc.Recorder;
import dev.onlooker.utils.objects.Dragging;
import dev.onlooker.utils.render.*;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SessionHUDMod extends Module {
    public final Dragging drag = Client.INSTANCE.createDrag(this, "SessionHUD", 5.0f, 40.0f);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
    public int killCounts = 0;
    public static int win = 0;
    private EntityLivingBase syncEntity = null;
    public long startTime = System.currentTimeMillis();

    public SessionHUDMod() {
        super("SessionHUD", Category.DISPLAY, "description");
        if (!enabled) this.toggleSilent();
    }

    @Override
    public void onLivingDeathEvent(LivingDeathEvent event) {
        killCounts++;
    }

    @Override
    public void onShaderEvent(ShaderEvent event) {
        String playtime;
        float x = this.drag.getX();
        float y = this.drag.getY();
        float sessionInfoX = 120.0f;
        float sessionInfoY = 60.0f;
        Color c1 = ColorUtil.applyOpacity(HUDMod.getClientColors().getFirst(), 255.0f);
        Color c2 = ColorUtil.applyOpacity(HUDMod.getClientColors().getSecond(), 255.0f);
        this.drag.setWidth(sessionInfoX);
        this.drag.setHeight(sessionInfoY);
        String kills = String.valueOf(killCounts);
        String wins = String.valueOf(win);
        playtime = DATE_FORMAT.format(new Date(System.currentTimeMillis() - Recorder.startTime - 28800000L));
        if (HUDMod.info.is("Novoline")) {
            RenderUtil.drawHorizontalGradientRect(x, y, x + 120.0f, y + 2.0f, c1.getRGB(), c2.getRGB());
            RenderUtil.drawRect(x, y, x + 120.0f, y + 55.0f, Color.BLACK);
            for (double i = (x); i < (x + 120.0f); i += 1.0) {
                Gui.drawRect(i, y, i + 1.0, y + 1.0f, Theme.getColor((int)(200.0 + i * 20.0)));
            }
            tenacityBoldFont18.drawString("Session Info", x + 35.0f, y + 7.0f, Color.WHITE);
            tenacityBoldFont18.drawString("Play Time", x + 3.0f, y + 20.0f, Color.WHITE);
            tenacityBoldFont18.drawString("Kills", x + 3.0f, y + 32.0f, Color.WHITE);
            tenacityBoldFont18.drawString("Wins", x + 3.0f, y + 44.0f, Color.WHITE);
            int roundedWidth = 120;
            int roundedX = (int)(x + (float)roundedWidth);
            tenacityBoldFont18.drawString(playtime, (float)roundedX - tenacityBoldFont18.getStringWidth(playtime) - 3.0f, y + 20.0f, Color.WHITE);
            tenacityBoldFont18.drawString(kills, (float)roundedX - tenacityBoldFont18.getStringWidth(kills) - 3.0f, y + 32.0f, Color.WHITE);
            tenacityBoldFont18.drawString(wins, (float)roundedX - tenacityBoldFont18.getStringWidth(wins) - 3.0f, y + 44.0f, Color.WHITE);
        }
        if (HUDMod.info.is("Onlooker")) {
            HUDMod hudMod = Client.INSTANCE.getModuleCollection().getModule(HUDMod.class);
            RoundedUtil.drawRound(x, y,  120.0f, 60.0f,6.0f, hudMod.getThemeShaderColor());
        }
    }

    @Override
    public void onRender2DEvent(Render2DEvent event) {
        String playtime;
        Color c1 = ColorUtil.applyOpacity(HUDMod.getClientColors().getFirst(), 255.0f);
        Color c2 = ColorUtil.applyOpacity(HUDMod.getClientColors().getSecond(), 255.0f);
        float x = this.drag.getX();
        float y = this.drag.getY();
        float sessionInfoX = 120.0f;
        float sessionInfoY = 60.0f;
        this.drag.setWidth(sessionInfoX);
        this.drag.setHeight(sessionInfoY);
        String kills = String.valueOf(killCounts);
        String wins = String.valueOf(win);
        int roundedWidth = 120;
        int roundedX = (int)(x + (float)roundedWidth);
        playtime = DATE_FORMAT.format(new Date(System.currentTimeMillis() - Recorder.startTime - 28800000L));
        if (HUDMod.info.is("Novoline")) {
            RenderUtil.drawHorizontalGradientRect(x, y, x + 120.0f, y + 2.0f, c1.getRGB(), c2.getRGB());
            RenderUtil.drawRect(x, y, x + 120.0f, y + 55.0f, new Color(0, 0, 0, 80));
            for (double i = (x); i < (x + 120.0f); i += 1.0) {
                Gui.drawRect(i, y, i + 1.0, y + 1.0f, Theme.getColor((int)(200.0 + i * 20.0)));
            }
            tenacityBoldFont18.drawString("Session Info", x + 35.0f, y + 7.0f, Color.WHITE);
            tenacityBoldFont18.drawString("Play Time", x + 3.0f, y + 20.0f, Color.WHITE);
            tenacityBoldFont18.drawString("Kills", x + 3.0f, y + 32.0f, Color.WHITE);
            tenacityBoldFont18.drawString("Wins", x + 3.0f, y + 44.0f, Color.WHITE);
            tenacityBoldFont18.drawString(playtime, (float)roundedX - tenacityBoldFont18.getStringWidth(playtime) - 3.0f, y + 20.0f, Color.WHITE);
            tenacityBoldFont18.drawString(kills, (float)roundedX - tenacityBoldFont18.getStringWidth(kills) - 3.0f, y + 32.0f, Color.WHITE);
            tenacityBoldFont18.drawString(wins, (float)roundedX - tenacityBoldFont18.getStringWidth(wins) - 3.0f, y + 44.0f, Color.WHITE);
        }
        if (HUDMod.info.is("Onlooker")){
            HUDMod hudMod = Client.INSTANCE.getModuleCollection().getModule(HUDMod.class);
            playtime = DATE_FORMAT.format(new Date(System.currentTimeMillis() - startTime - 28800000L));

            RoundedUtil.drawRound(x, y, 120.0f, 60.0f,6.0f, hudMod.getThemeBgColor());
            RoundedUtil.drawCircle(x, y, 20, 20.0f, 7.5f,
                    hudMod.getThemeCircleColor());
            RoundedUtil.drawRound(x+4, y+20f, 112.0f, 0.5f,1.0f, new Color(255, 255 ,255, 150));
            iconFont18.drawString("u", x + 5.0f, y + 8.2f, hudMod.getThemefontColor2());
            tenacityBoldFont18.drawString("Session Info", x + 20.0f, y + 6f,  hudMod.getThemefontColor());
            tenacityBoldFont16.drawString("Kills: ", x + 4.0f, y + 23.5f,hudMod.getThemefontColor());
            tenacityBoldFont16.drawString("Wins: ", x + 4.0f, y + 36f, hudMod.getThemefontColor());
            tenacityBoldFont16.drawString(kills, x+ tenacityBoldFont18.getStringWidth("Kills") + 5, y + 23.5f,
                    hudMod.getThemefontColor());
            tenacityBoldFont16.drawString(wins, x+tenacityBoldFont18.getStringWidth("Wins") +5, y + 36f,
                    hudMod.getThemefontColor());
            tenacityBoldFont16.drawString("Play Time: ", x + 4.0f, y + 48.0f, hudMod.getThemefontColor());
            tenacityBoldFont16.drawString(playtime,  x+tenacityBoldFont18.getStringWidth("Play Time") + 3.0f, y + 48.0f,
                    hudMod.getThemefontColor());
        }
    }

    @Override
    public void onAttackEvent(EventAttack event) {
        this.syncEntity = event.getTargetEntity();
    }

    @Override
    public void onUpdateEvent(UpdateEvent event) {
        if (this.syncEntity != null && this.syncEntity.isDead) {
            ++this.killCounts;
            this.syncEntity = null;
        }
    }
}


