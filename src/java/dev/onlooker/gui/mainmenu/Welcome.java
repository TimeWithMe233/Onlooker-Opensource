package dev.onlooker.gui.mainmenu;

import dev.onlooker.Client;
import dev.onlooker.utils.animations.AnimationUtil;
import dev.onlooker.utils.animations.impl.Translate;
import dev.onlooker.utils.font.CustomFont;
import dev.onlooker.utils.time.WelcomrUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.*;
import java.io.IOException;

public class Welcome extends GuiScreen {
    private final FshShader backgroundShader;
    private final long initTime = System.currentTimeMillis();
    private static final WelcomrUtil timer = new WelcomrUtil();
    boolean rev = false;
    boolean skiped = false;
    double anim,anim2,anim3 = new ScaledResolution(mc).getScaledWidth();
    Translate translate = new Translate(0,new ScaledResolution(mc).getScaledHeight());
    Translate translate2 = new Translate(new ScaledResolution(mc).getScaledWidth(),new ScaledResolution(mc).getScaledHeight());

    public Welcome() {
        try {
            this.backgroundShader = new FshShader("/assets/minecraft/OnLooker/Shaders/fragment/shader.fsh");
        }
        catch (IOException e) {
            throw new IllegalStateException("Failed to load backgound shader", e);
        }
    }

    @Override
    public void initGui(){
        timer.reset();
        translate = new Translate(0,new ScaledResolution(mc).getScaledHeight());
        translate2 = new Translate(new ScaledResolution(mc).getScaledWidth(),new ScaledResolution(mc).getScaledHeight());
    }

    @Override
    protected void keyTyped(char var1, int var2) {
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton){
        if (mouseButton == 0){
            skiped = true;
        }
    }

    public void useShaderToyBackground(int scale) {
        GlStateManager.disableCull();
        backgroundShader.useShader(this.width * scale, this.height * scale, (System.currentTimeMillis() - initTime) / 1000.0f);
        GL11.glBegin(7);
        GL11.glVertex2d(-1.0, -1.0);
        GL11.glVertex2d(-1.0, 1.0);
        GL11.glVertex2d(1.0, 1.0);
        GL11.glVertex2d(1.0, -1.0);
        GL11.glEnd();
        GL20.glUseProgram(0);
        GlStateManager.enableAlpha();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (!timer.hasReached(500)) {
            anim = anim2 = anim3 = new ScaledResolution(mc).getScaledWidth();
            rev = true;
        }
        if (rev) {
            anim = AnimationUtil.animate((float) new ScaledResolution(mc).getScaledWidth(), (float) anim, (skiped ? 12.0f : 6.0f) / Minecraft.getDebugFPS());
            anim2 = AnimationUtil.animate((float) new ScaledResolution(mc).getScaledWidth(), (float) anim2, (skiped ? 8.0f : 4.0f) / Minecraft.getDebugFPS());
            anim3 = AnimationUtil.animate((float) new ScaledResolution(mc).getScaledWidth(), (float) anim3, (skiped ? 11.0f : 5.5f) / Minecraft.getDebugFPS());
        } else {
            anim = AnimationUtil.animate((float) 0, (float) anim, (skiped ? 6.0f : 3.0f) / Minecraft.getDebugFPS());
            anim2 = AnimationUtil.animate((float) 0, (float) anim2, (skiped ? 10.0f : 5.0f) / Minecraft.getDebugFPS());
            anim3 = AnimationUtil.animate((float) 0, (float) anim3, (skiped ? 9f : 4.5f) / Minecraft.getDebugFPS());
        }
        ScaledResolution sr = new ScaledResolution(mc);

        CustomFont fontwel = tenacityBoldFont32;
        useShaderToyBackground(2);
        if (!timer.hasReached(3500)) {
            translate.interpolate((float) sr.getScaledWidth() / 2, (float) sr.getScaledHeight() / 2 - 3f, (float) 0.14);
        }
        fontwel.drawCenteredStringWithShadow("Welcome back to " + Client.NAME, translate.getX(), translate.getY(), new Color(255, 255, 255).getRGB());

        drawRect(-10, -10, anim, height + 10, new Color(203, 50, 255).getRGB());
        drawRect(-10, -10, anim3, height + 10, new Color(0, 217, 255).getRGB());
        drawRect(-10, -10, anim2, height + 10, new Color(47, 47, 47).getRGB());

        if (timer.hasReached(3500)) {
            translate.interpolate(0, new ScaledResolution(mc).getScaledHeight() + 5, (float) 0.14);
            translate2.interpolate(new ScaledResolution(mc).getScaledWidth(), new ScaledResolution(mc).getScaledHeight() + 5, (float) 0.14);
        }

        //mc.displayGuiScreen(new GuiMainMenu());
        if (timer.hasReached(4000) || skiped) {
            rev = true;
            if (anim2 >= width - 5) {
                mc.displayGuiScreen(new MainMenu());
            }
        } else {
            rev = false;
        }
    }
    public static void drawRect(double x2, double y2, double x1, double y1, int color) {
        enableGL2D();
        glColor(color);
        drawRect(x2, y2, x1, y1);
        disableGL2D();
    }

    public static void glColor(int hex) {
        float alpha = (hex >> 24 & 0xFF) / 255.0F;
        float red = (hex >> 16 & 0xFF) / 255.0F;
        float green = (hex >> 8 & 0xFF) / 255.0F;
        float blue = (hex & 0xFF) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
    }

    private static void drawRect(double x2, double y2, double x1, double y1) {
        GL11.glBegin(7);
        GL11.glVertex2d(x2, y1);
        GL11.glVertex2d(x1, y1);
        GL11.glVertex2d(x1, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
    }

    public static void enableGL2D() {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
    }

    public static void disableGL2D() {
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }
}
