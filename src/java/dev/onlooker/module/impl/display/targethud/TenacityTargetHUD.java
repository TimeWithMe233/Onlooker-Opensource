package dev.onlooker.module.impl.display.targethud;

import dev.onlooker.module.impl.display.HUDMod;
import dev.onlooker.utils.animations.ContinualAnimation;
import dev.onlooker.utils.misc.MathUtils;
import dev.onlooker.utils.render.ColorUtil;
import dev.onlooker.utils.render.RenderUtil;
import dev.onlooker.utils.render.RoundedUtil;
import dev.onlooker.utils.render.StencilUtil;
import dev.onlooker.utils.skidfont.FontManager;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

import java.awt.*;

public class TenacityTargetHUD extends TargetHUD {

    private final ContinualAnimation animation = new ContinualAnimation();

    public TenacityTargetHUD() {
        super("Tenacity");
    }

    @Override
    public void render(float x, float y, float alpha, EntityLivingBase target) {
        setWidth(Math.max(145, FontManager.GenShinGothic24.getStringWidth(target.getName()) + 60));
        setHeight(37);

        Color c1 = ColorUtil.applyOpacity(HUDMod.getClientColors().getFirst(), alpha);
        Color c2 = ColorUtil.applyOpacity(HUDMod.getClientColors().getSecond(), alpha);
        Color color = HUDMod.watermarkTheme.is("Light") ? new Color(205, 205, 205,80) :new Color(0, 0, 0, 80);
        RoundedUtil.drawRound(x, y, getWidth(), getHeight(), 6, color);

        if (target instanceof AbstractClientPlayer) {
            StencilUtil.initStencilToWrite();
            StencilUtil.readStencilBuffer(1);
            RenderUtil.color(-1, alpha);
            RenderUtil.drawHead(target.getLocationSkin(), x + 3, y + 3, 31, 31, 4.0f, 255.0f);
            StencilUtil.uninitStencilBuffer();
            GlStateManager.disableBlend();
        } else {
            RoundedUtil.drawRound(x + 3, y + 3, 31, 31, 8, new Color(0, 0, 0,152));
            tenacityBoldFont40.drawCenteredString("?", x + 19, y + 20 - FontManager.GenShinGothic24.getHeight() / 2f, new Color(255,255,255,190));
        }


        FontManager.Naven20.drawString(target.getName(), x + 39, y + 5, new Color(255,255,255,180));

        float healthPercent = MathHelper.clamp_float((target.getHealth() + target.getAbsorptionAmount()) / (target.getMaxHealth() + target.getAbsorptionAmount()), 0, 1);

        float realHealthWidth = getWidth() - 44;
        float realHealthHeight = 3;
        animation.animate(realHealthWidth * healthPercent, 18);
        Color backgroundHealthColor = new Color(0, 0, 0, ((int) alpha * 110));

        float healthWidth = animation.getOutput();

        RoundedUtil.drawRound(x + 39, (y + getHeight() - 9), 98, realHealthHeight, 1.5f, backgroundHealthColor);
        RoundedUtil.drawGradientHorizontal(x + 39, (y + getHeight() - 9), healthWidth, realHealthHeight, 1.5f, c1, c2);

        String healthText = (int) MathUtils.round(healthPercent * 100, .01) + "%";
        StencilUtil.initStencilToWrite();
        RoundedUtil.drawGradientHorizontal(x + 39, (y + getHeight() - 20), healthWidth+7, 8, 1.5f, c1, c2);
        StencilUtil.readStencilBuffer(1);
        RenderUtil.color(-1, alpha);
        tenacityBoldFont16.drawString(healthText, x + 33 + Math.min(Math.max(1, healthWidth), realHealthWidth - 15), y + getHeight() - (6 + FontManager.GenShinGothic20.getHeight())+8,
                new Color(255,255,255,160));
        StencilUtil.uninitStencilBuffer();
        GlStateManager.disableBlend();

    }


    @Override
    public void renderEffects(float x, float y, float alpha) {
        RoundedUtil.drawRound(x, y, getWidth(), getHeight(), 6, ColorUtil.applyOpacity(Color.BLACK, alpha));
    }

}
