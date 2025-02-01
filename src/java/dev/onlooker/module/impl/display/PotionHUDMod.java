package dev.onlooker.module.impl.display;

import dev.onlooker.Client;
import dev.onlooker.event.impl.render.Render2DEvent;
import dev.onlooker.event.impl.render.ShaderEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.utils.animations.ContinualAnimation;
import dev.onlooker.utils.animations.Direction;
import dev.onlooker.utils.animations.impl.EaseBackIn;
import dev.onlooker.utils.objects.Dragging;
import dev.onlooker.utils.render.RenderUtil;
import dev.onlooker.utils.render.RoundedUtil;
import dev.onlooker.utils.render.StencilUtil;
import dev.onlooker.utils.render.Theme;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class PotionHUDMod extends Module {
    private int maxString;
    private final Map<Integer, Integer> potionMaxDurations;
    private final ContinualAnimation widthanimation;
    private final ContinualAnimation heightanimation;
    private final EaseBackIn animation;
    List<PotionEffect> effects;
    public final Dragging drag = Client.INSTANCE.createDrag(this, "PotionHUD", 20f, 40.0f);

    public PotionHUDMod() {
        super("PotionHUD", Category.DISPLAY, "CNM");
        this.maxString = 0;
        this.potionMaxDurations = new HashMap<>();
        this.widthanimation = new ContinualAnimation();
        this.heightanimation = new ContinualAnimation();
        this.animation = new EaseBackIn(200, 1.0, 1.3f);
        this.effects = new ArrayList<>();
        if (!enabled) this.toggleSilent();
    }

    private String get(final PotionEffect potioneffect) {
        final Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
        String s1 = I18n.format(potion.getName());
        s1 = s1 + " " + this.intToRomanByGreedy(potioneffect.getAmplifier() + 1);
        return s1;
    }

    private String intToRomanByGreedy(int num) {
        final int[] values = { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 };
        final String[] symbols = { "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I" };
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < values.length && num >= 0; ++i) {
            while (values[i] <= num) {
                num -= values[i];
                stringBuilder.append(symbols[i]);
            }
        }
        return stringBuilder.toString();
    }
    @Override
    public void onShaderEvent(ShaderEvent event) {
        this.drag.setWidth(100);
        this.drag.setHeight(60);
        if (HUDMod.potion.is("Novoline")) {
            RenderUtil.scaleStart((float) (this.drag.getX() + 50.0), (float) (this.drag.getY() + 15.0), this.animation.getOutput().floatValue());
            RoundedUtil.drawRound(this.drag.getX(), this.drag.getY(), 100.0f, 30.0f, 0.0f, new Color(0, 0, 0, 255));
            RenderUtil.scaleEnd();
            final int x = (int) this.drag.getX();
            final int y = (int) this.drag.getY();
            Gui.drawRect3(x, y, (int) this.widthanimation.getOutput(), (int) this.heightanimation.getOutput(), new Color(0, 0, 0, 255).getRGB());
        }
        if (HUDMod.potion.is("OnLooker")) {
            HUDMod hudMod = Client.INSTANCE.getModuleCollection().getModule(HUDMod.class);
            RenderUtil.scaleStart((float) (this.drag.getX() + 50.0), (float) (this.drag.getY() + 15.0), this.animation.getOutput().floatValue());
            RoundedUtil.drawRound(this.drag.getX(), this.drag.getY(), 100.0f, 30.0f, 6.0f, Color.BLACK);
            RenderUtil.scaleEnd();
            final int x = (int) this.drag.getX();
            final int y = (int) this.drag.getY();
            RoundedUtil.drawRound(x, y, (int) this.widthanimation.getOutput(), this.heightanimation.getOutput(), 6.0f, hudMod.getThemeShaderColor());
        }
    }
    @Override
    public void onRender2DEvent(Render2DEvent event) {
        this.drag.setWidth(100);
        this.drag.setHeight(60);
        this.effects = mc.thePlayer.getActivePotionEffects().stream().sorted(Comparator.comparingInt(it -> (int) tenacityBoldFont18.getStringWidth(this.get(it)))).collect(Collectors.toList());
        final int x = (int) this.drag.getX();
        final int y = (int) this.drag.getY();
        final int offsetX = 21;
        final int offsetY = 14;
        int i2 = 16;
        final ArrayList<Integer> needRemove = new ArrayList<>();
        for (final Map.Entry<Integer, Integer> entry : this.potionMaxDurations.entrySet()) {
            if (mc.thePlayer.getActivePotionEffect(Potion.potionTypes[entry.getKey()]) == null) {
                needRemove.add(entry.getKey());
            }
        }
        for (final int id : needRemove) {
            this.potionMaxDurations.remove(id);
        }
        for (final PotionEffect effect : this.effects) {
            if (!this.potionMaxDurations.containsKey(effect.getPotionID()) || this.potionMaxDurations.get(effect.getPotionID()) < effect.getDuration()) {
                this.potionMaxDurations.put(effect.getPotionID(), effect.getDuration());
            }
        }
        if (HUDMod.potion.is("Novoline")) {
            final float width = this.effects.isEmpty() ? 0.0f : (Math.max(50 + tenacityBoldFont18.getStringWidth(this.get(this.effects.get(this.effects.size() - 1))), 60 + tenacityBoldFont18.getStringWidth(this.get(this.effects.get(this.effects.size() - 1)))));
            final float height = (float) (this.effects.size() * 25);
            this.widthanimation.animate(width, 20);
            this.heightanimation.animate(height, 20);
            if (mc.currentScreen instanceof GuiChat && this.effects.isEmpty()) {
                this.animation.setDirection(Direction.FORWARDS);
            } else if (!(mc.currentScreen instanceof GuiChat)) {
                this.animation.setDirection(Direction.BACKWARDS);
            }
            RenderUtil.scaleStart((float) (x + 50), (float) (y + 15), this.animation.getOutput().floatValue());
            tenacityBoldFont18.drawString("Potion Example",
                    x + 52.0f - tenacityBoldFont18.getStringWidth("Potion Example") / 2, (float) (y + 15 - tenacityBoldFont18.getHeight() / 2),
                    new Color(255, 255, 255, 60).getRGB());
            RenderUtil.scaleEnd();
            if (this.effects.isEmpty()) {
                this.maxString = 0;
            }
            if (!this.effects.isEmpty()) {
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.disableLighting();
                final int l = 24;
                Gui.drawRect3(x, y, (int) this.widthanimation.getOutput(), (int) this.heightanimation.getOutput(),
                        new Color(0, 0, 0, 100).getRGB());
                for (double i = x; i < (x - 1 + widthanimation.getOutput()); i += 1.0) {
                    Gui.drawRect(i, y, i + 1.0, y + 1.0f, Theme.getColor((int)(200.0 + i * 20.0)));
                }
                RenderUtil.startGlScissor(x, y, (int) this.widthanimation.getOutput(), (int) this.heightanimation.getOutput());
                for (final PotionEffect potioneffect : this.effects) {
                    final Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    if (potion.hasStatusIcon()) {
                        mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
                        final int i3 = potion.getStatusIconIndex();
                        GlStateManager.enableBlend();
                        mc.ingameGUI.drawTexturedModalRect(x + offsetX - 17, y + i2 - offsetY + 2, i3 % 8 * 18, 198 + i3 / 8 * 18, 18, 18);
                    }
                    final String s = Potion.getDurationString(potioneffect);
                    final String s2 = this.get(potioneffect);
                    tenacityBoldFont18.drawString(s2, (float) (x + offsetX + 3), (float) (y + i2 - offsetY + 2), -1);
                    tenacityBoldFont18.drawString(s, (float) (x + offsetX + 3), (float) (y + i2 + 11 - offsetY + 2), -1);
                    i2 += l;
                    if (this.maxString < mc.fontRendererObj.getStringWidth(s2)) {
                        this.maxString = (int) mc.fontRendererObj.getStringWidth(s2);
                    }
                }
                RenderUtil.stopGlScissor();
            }
        }
        if (HUDMod.potion.is("OnLooker")) {
            HUDMod hudMod = Client.INSTANCE.getModuleCollection().getModule(HUDMod.class);
            this.effects = mc.thePlayer.getActivePotionEffects().stream().sorted(Comparator.comparingInt(it -> (int) tenacityBoldFont18.getStringWidth(this.get(it)))).collect(Collectors.toList());
            final float width = this.effects.isEmpty() ? 0.0f : Math.max(35 + tenacityBoldFont18.getStringWidth(this.get(this.effects.get(this.effects.size() - 1))), 40 + tenacityBoldFont18.getStringWidth(this.get(this.effects.get(this.effects.size() - 1))));
            final float height = (float) (this.effects.size() * 25)+24;
            this.widthanimation.animate(width, 35);
            this.heightanimation.animate(height, 35);
            if (mc.currentScreen instanceof GuiChat && this.effects.isEmpty()) {
                this.animation.setDirection(Direction.FORWARDS);
            } else if (!(mc.currentScreen instanceof GuiChat)) {
                this.animation.setDirection(Direction.BACKWARDS);
            }
            RenderUtil.scaleStart((float) (x + 50), (float) (y + 15), this.animation.getOutput().floatValue());
            tenacityBoldFont18.drawString("Potion Example", x + 52.0f - tenacityBoldFont18.getStringWidth("Potion Example") / 2,
                    (float) (y + 20- tenacityBoldFont18.getHeight() / 2), new Color(255, 255, 255, 60).getRGB());
            RenderUtil.scaleEnd();
            if(this.effects.size() != 0) {
                RoundedUtil.drawRound(x, y, (int) this.widthanimation.getOutput(), this.heightanimation.getOutput(), 6.0f, hudMod.getThemeBgColor());
                RoundedUtil.drawCircle(x + 1, y + 1, 20, 20.0f, 7.5f, hudMod.getThemeCircleColor());
                iconFont22.drawString("s", x + 5.8f, y + 9f, hudMod.getThemefontColor2());
                tenacityBoldFont18.drawString("Potion", (float) (x + 22), (float) (y + 7),
                        hudMod.getThemefontColor());
                RoundedUtil.drawRound((x + offsetX - 16), y + 22f, (int) this.widthanimation.getOutput() - 13, 0.5f, 1.0f,
                        new Color(255, 255, 255, 150));
            }
            if (this.effects.isEmpty()) {
                this.maxString = 0;
            }
            if (!this.effects.isEmpty()) {
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.disableLighting();
                final int l = 25;
                RenderUtil.startGlScissor(x, y, (int) this.widthanimation.getOutput(), (int) this.heightanimation.getOutput());
                for (final PotionEffect potioneffect : this.effects) {
                    final Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    if (potion.hasStatusIcon()) {
                        mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
                        final int i3 = potion.getStatusIconIndex();
                        GlStateManager.enableBlend();
                        mc.ingameGUI.drawTexturedModalRect(x + offsetX - 16, y + i2 - offsetY + 24, i3 % 8 * 18,
                                198 + i3 / 8 * 18, 18, 18);
                    }
                    final String s = Potion.getDurationString(potioneffect);
                    final String s2 = this.get(potioneffect);
                    tenacityBoldFont18.drawString(s2, (float) (x + offsetX + 4), (float) (y + i2 - offsetY + 25),
                            hudMod.getThemefontColor());
                    tenacityBoldFont16.drawString(s, (float) (x + offsetX + 4), (float) (y + i2 + 11 - offsetY + 24),
                            hudMod.getThemefontColor());

                    //
                    StencilUtil.initStencilToWrite();
                    RoundedUtil.drawRound(x, y, (int) this.widthanimation.getOutput(), this.heightanimation.getOutput()-10,
                            8.0f, new Color(0, 0, 0,80));
                    RenderUtil.bindReadStencilBuffer(1);
                    RenderUtil.resetColor();
                    RenderUtil.setAlphaLimit(0);
                    RenderUtil.resetColor();
                    RoundedUtil.drawRound((x + offsetX -16), (y + i2 + 11 - offsetY + 34.5f), (int) this.widthanimation.getOutput() - 13,
                            0.5f, 1.0f, new Color(255, 255, 255,80));
                    StencilUtil.uninitStencilBuffer();
                    //

                    i2 += l;
                    if (this.maxString < mc.fontRendererObj.getStringWidth(s2)) {
                        this.maxString = (int) mc.fontRendererObj.getStringWidth(s2);
                    }
                }
                RenderUtil.stopGlScissor();
            }
        }
    }
}
