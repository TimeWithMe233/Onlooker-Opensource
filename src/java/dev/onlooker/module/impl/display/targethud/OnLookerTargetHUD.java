package dev.onlooker.module.impl.display.targethud;

import dev.onlooker.module.impl.display.HUDMod;
import dev.onlooker.utils.animations.ContinualAnimation;
import dev.onlooker.utils.misc.MathUtil;
import dev.onlooker.utils.render.*;
import dev.onlooker.utils.skidfont.FontManager;
import dev.onlooker.utils.time.TimerUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class OnLookerTargetHUD extends TargetHUD {
    public OnLookerTargetHUD() {
        super("OnLooker");
    }
    private final TimerUtil timer = new TimerUtil();
    private boolean sentParticles;
    public final List<Particle> particles = new ArrayList<Particle>();
    private final ContinualAnimation animatedHealthBar = new ContinualAnimation();

    public void render(float x, float y, float alpha, EntityLivingBase target) {
        float width;
        float height;
        width = Math.max(120.0F, FontManager.PingFang_bold18.getStringWidth(target.getName()) + 80.0f);
        height = 38.0F;
        this.setWidth(width);
        this.setHeight(height);
        final int scaleOffset = (int) (target.hurtTime * 0.0);
        int textColor = ColorUtil.applyOpacity(-1, alpha);
        //绘制背景
        RoundedUtil.drawRoundOutline(x, y, width , height, 6F, 0.1f ,new Color(0, 0, 0, 100), HUDMod.getClientColors().getFirst());

        //绘制头
        if (target instanceof AbstractClientPlayer) {
            final double offset = -(target.hurtTime * 23);
            StencilUtil.initStencilToWrite();
            RenderUtil.renderRoundedRect(x + 4.5f, y + 5.0f, 28, 29, 6, -1);
            StencilUtil.readStencilBuffer(1);
            RenderUtil.color(-1, alpha);
            RenderUtil.color(ColorUtil.applyOpacity(new Color(255, (int) (255 + offset), (int) (255 + offset)), alpha).getRGB());
            renderPlayer2D(x + 4.5f + scaleOffset, y + 5.0f + scaleOffset, 28 - scaleOffset, 29 - scaleOffset, (AbstractClientPlayer) target);
            StencilUtil.uninitStencilBuffer();
            GlStateManager.disableBlend();
        } else {
            //遇到怪物等  头  则会变为？
            FontManager.PingFang_bold18.drawStringWithShadow("?", x + 13, y + 20 - FontManager.PingFang_bold18.getHeight() / 2f, textColor);
        }
        GradientUtil.applyGradientHorizontal(x + 36.0F, y + 10f,
                FontManager.PingFang_bold18.getStringWidth(target.getName()), 20, 1, getClientColors()[0], getClientColors()[1], () -> {
            RenderUtil.setAlphaLimit(10);
            FontManager.PingFang_bold18.drawString(target.getName(), x + 36.0F, y + 10f, 0);
            FontManager.PingFang_bold18.drawString(String.valueOf(MathUtil.round(target.getHealth() + target.getAbsorptionAmount(),1)), width + x - 29.8f, y + 8f, 0);
        });

        //绘制血条
        float healthPercent = (target.getHealth() + target.getAbsorptionAmount()) / (target.getMaxHealth() + target.getAbsorptionAmount());
        float var = (getWidth() - 45.0f) * healthPercent;
        animatedHealthBar.animate(var, 18);
        RoundedUtil.drawRound(x + 36.0F, y + 18.0f + (float) FontManager.PingFang_bold18.getHeight() - 12.0f, width - 45.0f, 5.0F, 2f, new Color(0, 0, 0, 30));
        RoundedUtil.drawGradientHorizontal(x + 36.0F, y + 18.0f + (float) FontManager.PingFang_bold18.getHeight() - 12.0f, animatedHealthBar.getOutput(), 5.0F, 2f, (Color) HUDMod.getClientColors().getFirst(), (Color) HUDMod.getClientColors().getSecond());

        if (timer.hasTimeElapsed(1000 / 60, true)) {
            for (Particle p : particles) {
                p.updatePosition();
                if (p.opacity < 1) particles.remove(p);
            }
        }

        if (target.hurtTime == 9 && !sentParticles) {
            for (int i = 0; i <= 15; i++) {
                Particle particle = new Particle();
                particle.init(x + 20, y + 20, (float) (((Math.random() - 0.5) * 2) * 1.4), (float) (((Math.random() - 0.5) * 2) * 1.4),
                        (float) (Math.random() * 4), i % 2 == 0 ? colorWheel.getColor1() : colorWheel.getColor2());
                particles.add(particle);
            }
            sentParticles = true;
        }
        if (target.hurtTime == 8) sentParticles = false;
    }

    public static class Particle {
        public float x, y, adjustedX, adjustedY, deltaX, deltaY, size, opacity;
        public Color color;
        public void updatePosition() {
            for (int i = 1; i <= 2; i++) {
                adjustedX += deltaX;
                adjustedY += deltaY;
                deltaY *= 0.97;
                deltaX *= 0.97;
                opacity -= 1f;
                if (opacity < 1) opacity = 1;
            }
        }

        public void init(float x, float y, float deltaX, float deltaY, float size, Color color) {
            this.x = x;
            this.y = y;
            this.deltaX = deltaX;
            this.deltaY = deltaY;
            this.size = size;
            this.opacity = 254;
            this.color = color;
        }
    }

    @Override
    public void renderEffects(float x, float y, float alpha) {
        RoundedUtil.drawRoundOutline(x, y, getWidth(), getHeight(), 6.0F, 0.1f, Color.BLACK, Color.BLACK);
    }

    private Color getClientColor () {
        Color theme1 = HUDMod.getClientColors().getFirst();
        return new Color(theme1.getRGB());

    }
    private Color getAlternateClientColor () {
        Color theme2 = HUDMod.getClientColors().getSecond();
        return new Color(theme2.getRGB());
    }
    public Color[] getClientColors () {
        Color firstColor = ColorUtil.mixColors2(getClientColor(), getAlternateClientColor());
        Color secondColor = ColorUtil.mixColors2(getAlternateClientColor(), getClientColor());
        return new Color[]{firstColor, secondColor};
    }
}