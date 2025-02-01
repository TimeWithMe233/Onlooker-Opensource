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
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

import java.awt.*;

public class Rise2TargetHUD extends TargetHUD {
    private final ContinualAnimation animatedHealthBar = new ContinualAnimation();

    public Rise2TargetHUD() {
        super("Rise2");
    }

    @Override
    public void render(float x, float y, float alpha, EntityLivingBase target) {
        float width;
        float height;
        height = 32;
        width = Math.max(120.0F, FontManager.PingFang_bold18.getStringWidth(target.getName()) + 50.0F);
        this.setWidth(width);
        this.setHeight(height);
        float healthPercentme = MathHelper.clamp_float((mc.thePlayer.getHealth() + mc.thePlayer.getAbsorptionAmount()) / (mc.thePlayer.getMaxHealth() + mc.thePlayer.getAbsorptionAmount()), 0, 1);
        Color healthColor = null;
        //background
        RoundedUtil.drawRound(x, y, width, getHeight(), 4, new Color(0, 0, 0, 120));
        //health bar
        float healthPercent = (target.getHealth() + target.getAbsorptionAmount()) / (target.getMaxHealth() + target.getAbsorptionAmount());
        float var = (getWidth() - 37.5f) * healthPercent;
        animatedHealthBar.animate(var, 18);
        RoundedUtil.drawGradientHorizontal(x + 34f, (y + getHeight() - 13), width - 37.2F, 8, 1, new Color(0, 0, 0, 150), new Color(0, 0, 0, 85));
        RoundedUtil.drawGradientHorizontal(x + 34f, (y + getHeight() - 13), animatedHealthBar.getOutput(), 8, 1, HUDMod.getClientColors().getFirst(), HUDMod.getClientColors().getSecond());
        //render playerface
        final int scaleOffset = (int) (target.hurtTime * 0.7f);
        if (target instanceof AbstractClientPlayer) {
            StencilUtil.initStencilToWrite();
            RenderUtil.renderRoundedRect(x + 2, y + 2, 28, 28, 2, -1);
            StencilUtil.readStencilBuffer(1);
            RenderUtil.color(-1, alpha);
            renderPlayer2D(x + 2 + scaleOffset / 2f, y + 2 + scaleOffset / 2f, 29 - scaleOffset, 29 - scaleOffset, (AbstractClientPlayer) target);
            //renderPlayer2D(x + (float)1.5, y + (float)1.5, 29, 29, (AbstractClientPlayer) target);
            StencilUtil.uninitStencilBuffer();
            GlStateManager.disableBlend();
        }
        //target name
        FontManager.PingFang_bold18.drawString(target.getName(), x + 33f, (float) (y + 3.5) + 1f, Color.WHITE.getRGB());
        //health text
        String healthText = (int) MathUtils.round(healthPercent * 100, .01) + ".0%";
        FontManager.PingFang_bold18.drawString(healthText, x + 59, y + 18.5f, Color.WHITE.getRGB());
        if (healthPercent > healthPercentme) {
            healthColor = HUDMod.getClientColors().getSecond();
        }
        if (healthPercentme >= healthPercent) {
            healthColor = HUDMod.getClientColors().getFirst();
        }
        iconFont18.drawStringWithShadow("n", x + 38f + FontManager.PingFang_bold18.getStringWidth(target.getName()), (float) (y + 3.5) + 3.5f, healthColor);

    }

    @Override
    public void renderEffects(float x, float y, float alpha) {
        RoundedUtil.drawRound(x, y, getWidth(), getHeight(), 4, ColorUtil.applyOpacity(Color.BLACK, 100));
    }
}