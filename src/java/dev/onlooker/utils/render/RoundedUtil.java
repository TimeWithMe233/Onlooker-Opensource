package dev.onlooker.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static dev.onlooker.utils.Utils.mc;
import static org.lwjgl.opengl.GL11.*;

public class RoundedUtil {

    public static ShaderUtil roundedShader = new ShaderUtil("roundedRect");
    public static ShaderUtil roundedOutlineShader = new ShaderUtil("roundRectOutline");
    private static final ShaderUtil roundedTexturedShader = new ShaderUtil("roundRectTexture");
    private static final ShaderUtil roundedGradientShader = new ShaderUtil("roundedRectGradient");
    private static final ShaderUtil circle = new ShaderUtil("circle");
    private static final ShaderUtil circleShader = new ShaderUtil("OnLooker/Shaders/circle-arc.frag");

    public static void drawCircle(float x, float y, float width, float height, float radius, Color color) {
        GlStateManager.resetColor();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        circle.init();

        setupRoundedRectUniforms(x, y, width, height, radius, circle);
        circle.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

        ShaderUtil.drawQuads(x - 1, y - 1, width + 2, height + 2);
        circle.unload();
        GlStateManager.disableBlend();
    }
    public static void drawButtonRound(int x, int y, int width, int height, int radius, Color color) {
        drawRound(x, y, width, height, radius, false, color);
    }

    public static void drawRound(float x, float y, float width, float height, float radius, Color color) {
        drawRound(x, y, width, height, radius, false, color);
    }

    public static void drawGradientHorizontal(float x, float y, float width, float height, float radius, Color left, Color right) {
        drawGradientRound(x, y, width, height, radius, left, left, right, right);
    }

    public static void drawGradientVertical(float x, float y, float width, float height, float radius, Color top, Color bottom) {
        drawGradientRound(x, y, width, height, radius, bottom, top, bottom, top);
    }

    public static void drawGradientCornerLR(float x, float y, float width, float height, float radius, Color topLeft, Color bottomRight) {
        Color mixedColor = ColorUtil.interpolateColorC(topLeft, bottomRight, .5f);
        drawGradientRound(x, y, width, height, radius, mixedColor, topLeft, bottomRight, mixedColor);
    }

    public static void drawGradientCornerRL(float x, float y, float width, float height, float radius, Color bottomLeft, Color topRight) {
        Color mixedColor = ColorUtil.interpolateColorC(topRight, bottomLeft, .5f);
        drawGradientRound(x, y, width, height, radius, bottomLeft, mixedColor, mixedColor, topRight);
    }

    public static void drawGradientRound(float x, float y, float width, float height, float radius, Color bottomLeft, Color topLeft, Color bottomRight, Color topRight) {
        RenderUtil.setAlphaLimit(0);
        RenderUtil.resetColor();
        GLUtil.startBlend();
        roundedGradientShader.init();
        setupRoundedRectUniforms(x, y, width, height, radius, roundedGradientShader);
        //Top left
        roundedGradientShader.setUniformf("color1", topLeft.getRed() / 255f, topLeft.getGreen() / 255f, topLeft.getBlue() / 255f, topLeft.getAlpha() / 255f);
        // Bottom Left
        roundedGradientShader.setUniformf("color2", bottomLeft.getRed() / 255f, bottomLeft.getGreen() / 255f, bottomLeft.getBlue() / 255f, bottomLeft.getAlpha() / 255f);
        //Top Right
        roundedGradientShader.setUniformf("color3", topRight.getRed() / 255f, topRight.getGreen() / 255f, topRight.getBlue() / 255f, topRight.getAlpha() / 255f);
        //Bottom Right
        roundedGradientShader.setUniformf("color4", bottomRight.getRed() / 255f, bottomRight.getGreen() / 255f, bottomRight.getBlue() / 255f, bottomRight.getAlpha() / 255f);
        ShaderUtil.drawQuads(x - 1, y - 1, width + 2, height + 2);
        roundedGradientShader.unload();
        GLUtil.endBlend();
    }


    public static void drawRound(float x, float y, float width, float height, float radius, boolean blur, Color color) {
        RenderUtil.resetColor();
        GLUtil.startBlend();
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        RenderUtil.setAlphaLimit(0);
        roundedShader.init();

        setupRoundedRectUniforms(x, y, width, height, radius, roundedShader);
        roundedShader.setUniformi("blur", blur ? 1 : 0);
        roundedShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

        ShaderUtil.drawQuads(x - 1, y - 1, width + 2, height + 2);
        roundedShader.unload();
        GLUtil.endBlend();
    }

    public static void drawCircle(float x, float y, float radius, float progress, int change, Color color, float smoothness) {
        GLUtil.startBlend();
        float borderThickness = 1;
        circleShader.init();
        circleShader.setUniformf("radialSmoothness", smoothness);
        circleShader.setUniformf("radius", radius);
        circleShader.setUniformf("borderThickness", borderThickness);
        circleShader.setUniformf("progress", progress);
        circleShader.setUniformi("change", change);
        circleShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        float wh = radius + 10;
        ScaledResolution sr = new ScaledResolution(mc);
        circleShader.setUniformf("pos", (x + ((wh / 2f) - ((radius + borderThickness) / 2f))) * sr.getScaleFactor(),
                (Minecraft.getMinecraft().displayHeight - ((radius + borderThickness) * sr.getScaleFactor())) - ((y + ((wh / 2f) - ((radius + borderThickness) / 2f))) * sr.getScaleFactor()));
        ShaderUtil.drawQuads(x, y, wh, wh);
        circleShader.unload();
        GLUtil.endBlend();
    }
    public static void drawRoundOutline(float x, float y, float width, float height, float radius, float outlineThickness, Color color, Color outlineColor) {
        RenderUtil.resetColor();
        GLUtil.startBlend();
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        RenderUtil.setAlphaLimit(0);
        roundedOutlineShader.init();

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        setupRoundedRectUniforms(x, y, width, height, radius, roundedOutlineShader);
        roundedOutlineShader.setUniformf("outlineThickness", outlineThickness * sr.getScaleFactor());
        roundedOutlineShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        roundedOutlineShader.setUniformf("outlineColor", outlineColor.getRed() / 255f, outlineColor.getGreen() / 255f, outlineColor.getBlue() / 255f, outlineColor.getAlpha() / 255f);


        ShaderUtil.drawQuads(x - (2 + outlineThickness), y - (2 + outlineThickness), width + (4 + outlineThickness * 2), height + (4 + outlineThickness * 2));
        roundedOutlineShader.unload();
        GLUtil.endBlend();
    }


    public static void drawRoundTextured(float x, float y, float width, float height, float radius, float alpha) {
        RenderUtil.resetColor();
        RenderUtil.setAlphaLimit(0);
        GLUtil.startBlend();
        roundedTexturedShader.init();
        roundedTexturedShader.setUniformi("textureIn", 0);
        setupRoundedRectUniforms(x, y, width, height, radius, roundedTexturedShader);
        roundedTexturedShader.setUniformf("alpha", alpha);
        ShaderUtil.drawQuads(x - 1, y - 1, width + 2, height + 2);
        roundedTexturedShader.unload();
        GLUtil.endBlend();
    }

    private static void setupRoundedRectUniforms(float x, float y, float width, float height, float radius, ShaderUtil roundedTexturedShader) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        roundedTexturedShader.setUniformf("location", x * sr.getScaleFactor(),
                (Minecraft.getMinecraft().displayHeight - (height * sr.getScaleFactor())) - (y * sr.getScaleFactor()));
        roundedTexturedShader.setUniformf("rectSize", width * sr.getScaleFactor(), height * sr.getScaleFactor());
        roundedTexturedShader.setUniformf("radius", radius * sr.getScaleFactor());
    }


}
