package dev.onlooker.module.impl.render;

import dev.onlooker.Client;
import dev.onlooker.commands.impl.FriendCommand;
import dev.onlooker.event.impl.render.NametagRenderEvent;
import dev.onlooker.event.impl.render.Render2DEvent;
import dev.onlooker.event.impl.render.Render3DEvent;
import dev.onlooker.event.impl.render.ShaderEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.impl.combat.KillAura;
import dev.onlooker.module.impl.display.HUDMod;
import dev.onlooker.module.impl.misc.HackerDetector;
import dev.onlooker.module.settings.impl.BooleanSetting;
import dev.onlooker.module.settings.impl.MultipleBoolSetting;
import dev.onlooker.module.settings.impl.NumberSetting;
import dev.onlooker.utils.client.RegionalAbuseUtil;
import dev.onlooker.utils.player.WeaponDetection;
import dev.onlooker.utils.render.*;
import dev.onlooker.utils.skidfont.FontDrawer;
import dev.onlooker.utils.skidfont.FontManager;
import dev.onlooker.utils.tuples.Pair;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StringUtils;
import org.lwjgl.util.vector.Vector4f;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class ESP2D extends Module {

    private final MultipleBoolSetting validEntities = new MultipleBoolSetting("Valid Entities",
            new BooleanSetting("Players", true),
            new BooleanSetting("Animals", true),
            new BooleanSetting("Mobs", true));
    private final BooleanSetting nametags = new BooleanSetting("Nametags", true);

    private final NumberSetting scale = new NumberSetting("Tag Scale", .75, 1, .35, .05);

    private final MultipleBoolSetting nametagSettings = new MultipleBoolSetting("Nametag Settings",
            new BooleanSetting("Formatted Tags", false),
            new BooleanSetting("Add PostProcessing", false),
            new BooleanSetting("Health Text", true),
            new BooleanSetting("Background", true),
            new BooleanSetting("Round", true));

    public ESP2D() {
        super("2D ESP", Category.RENDER, "Draws a box in 2D space around entitys");
        RegionalAbuseUtil.getAddressByIP();
        addSettings(validEntities, nametags, scale, nametagSettings);
    }


    private final Map<Entity, Vector4f> entityPosition = new HashMap<>();

    @Override
    public void onRender3DEvent(Render3DEvent event) {
        entityPosition.clear();
        for (final Entity entity : mc.theWorld.loadedEntityList) {
            if (shouldRender(entity) && ESPUtil.isInView(entity)) {
                entityPosition.put(entity, ESPUtil.getEntityPositionsOn2D(entity));
            }
        }
    }

    @Override
    public void onShaderEvent(ShaderEvent e) {

        if (nametagSettings.getSetting("Add PostProcessing").isEnabled()) {
            for (Entity entity : entityPosition.keySet()) {
                Vector4f pos = entityPosition.get(entity);
                float x = pos.getX(), y = pos.getY(), right = pos.getZ(), bottom = pos.getW();

                if (entity instanceof EntityLivingBase) {
                    FontDrawer font = FontManager.Naven18;
                    HackerDetector antiCheat = Client.INSTANCE.getModuleCollection().getModule(HackerDetector.class);
                    EntityLivingBase renderingEntity = (EntityLivingBase) entity;
                    String name = (nametagSettings.getSetting("Formatted Tags").isEnabled() ? renderingEntity.getDisplayName().getFormattedText() : StringUtils.stripControlCodes(renderingEntity.getDisplayName().getUnformattedText()));
                    StringBuilder text = new StringBuilder((FriendCommand.isFriend(renderingEntity.getName()) ? "§d" : "§f") + name);
                    String hackname = "";
                    if (nametagSettings.getSetting("Health Text").isEnabled()) {
                        text.append(String.format("§f | %s HP", df.format(renderingEntity.getHealth())));
                    }
                    List<String> ranks = new ArrayList<>();
                    if (renderingEntity == mc.thePlayer) {
                        ranks.add("§a[You] ");
                    }
                    if (renderingEntity == KillAura.target) {
                        ranks.add("§c[Target] ");
                    }
                    if (WeaponDetection.isStrength((EntityPlayer) renderingEntity) > 0) {
                        ranks.add("§c[Strength] ");
                    }
                    if (WeaponDetection.isRegen((EntityPlayer) renderingEntity) > 0) {
                        ranks.add("§a[Regen] ");
                    }
                    if (WeaponDetection.isHoldingGodAxe((EntityPlayer) renderingEntity)) {
                        ranks.add("§c[GodAxe] ");
                    }
                    if (WeaponDetection.isKBBall(renderingEntity.getHeldItem())) {
                        ranks.add("§8[KBBall] ");
                    }
                    if (WeaponDetection.hasEatenGoldenApple((EntityPlayer) renderingEntity) > 0) {
                        ranks.add("§c[Gapple] ");
                    }
                    String rank = String.join(" ", ranks);
                    if (antiCheat.isCheater(renderingEntity.getName())) {
                        hackname = "§cHacker§f | ";
                    }

                    double fontScale = scale.getValue();
                    float middle = x + ((right - x) / 2);
                    float textWidth = 0;
                    double fontHeight;
                    textWidth = font.getStringWidth( hackname + rank + text);
                    middle -= (textWidth * fontScale) / 2f;
                    fontHeight = font.getHeight() * fontScale;

                    glPushMatrix();
                    glTranslated(middle, y - (fontHeight + 2), 0);
                    glScaled(fontScale, fontScale, 1);
                    glTranslated(-middle, -(y - (fontHeight + 2)), 0);

                    Color backgroundTagColor = Color.BLACK;
                    RenderUtil.resetColor();
                    GLUtil.startBlend();
                    if (nametagSettings.getSetting("Round").isEnabled()) {

                        RoundedUtil.drawRound(middle - 3, (float) (y - (fontHeight + 7)), textWidth + 6,
                                (float) ((fontHeight / fontScale) - 3.0f), 4, backgroundTagColor);
                    } else {
                        Gui.drawRect2(middle - 3, (float) (y - (fontHeight + 7)), textWidth + 6,
                                (fontHeight / fontScale) - 3.0f , backgroundTagColor.getRGB());
                    }
                    GLUtil.endBlend();
                    glPopMatrix();

                }
            }
        }
    }

    @Override
    public void onNametagRenderEvent(NametagRenderEvent e) {
        if (nametags.isEnabled()) e.cancel();
    }

    private final NumberFormat df = new DecimalFormat("0.#");
    private final Color backgroundColor = new Color(10, 10, 10, 130);

    private Color firstColor = Color.BLACK, secondColor = Color.BLACK, thirdColor = Color.BLACK, fourthColor = Color.BLACK;


    @Override
    public void onRender2DEvent(Render2DEvent e) {
        if (HUDMod.isRainbowTheme()) {
            firstColor = HUDMod.color1.getRainbow().getColor(0);
            secondColor = HUDMod.color1.getRainbow().getColor(90);
            thirdColor = HUDMod.color1.getRainbow().getColor(180);
            fourthColor = HUDMod.color1.getRainbow().getColor(270);
        } else {
            gradientColorWheel(HUDMod.getClientColors());
        }

        for (Entity entity : entityPosition.keySet()) {
            Vector4f pos = entityPosition.get(entity);
            float x = pos.getX(),
                    y = pos.getY(),
                    right = pos.getZ();

            if (entity instanceof EntityLivingBase) {
                FontDrawer font = FontManager.Naven18;
                EntityLivingBase renderingEntity = (EntityLivingBase) entity;{
                    HackerDetector antiCheat = Client.INSTANCE.getModuleCollection().getModule(HackerDetector.class);
                    float healthValue = renderingEntity.getHealth() / renderingEntity.getMaxHealth();
                    Color healthColor = healthValue > .75 ? new Color(0, 255, 81) : healthValue > .5 ? new Color(228, 255, 105) : healthValue > .35 ? new Color(236, 100, 64) : new Color(255, 65, 68);
                    String name = (nametagSettings.getSetting("Formatted Tags").isEnabled() ? renderingEntity.getDisplayName().getFormattedText() : StringUtils.stripControlCodes(renderingEntity.getDisplayName().getUnformattedText()));
                    StringBuilder text = new StringBuilder((FriendCommand.isFriend(renderingEntity.getName()) ? "§d" : "§f") + name);
                    String hackname = "";
                    if (nametagSettings.getSetting("Health Text").isEnabled()) {
                        text.append(String.format("§f | %s HP", df.format(renderingEntity.getHealth())));
                    }
                    List<String> ranks = new ArrayList<>();
                    if (renderingEntity == mc.thePlayer) {
                        ranks.add("§a[You] ");
                    }
                    if (renderingEntity == KillAura.target) {
                        ranks.add("§c[Target] ");
                    }
                    if (WeaponDetection.isStrength((EntityPlayer) renderingEntity) > 0) {
                        ranks.add("§c[Strength] ");
                    }
                    if (WeaponDetection.isRegen((EntityPlayer) renderingEntity) > 0) {
                        ranks.add("§a[Regen] ");
                    }
                    if (WeaponDetection.isHoldingGodAxe((EntityPlayer) renderingEntity)) {
                        ranks.add("§c[GodAxe] ");
                    }
                    if (WeaponDetection.isKBBall(renderingEntity.getHeldItem())) {
                        ranks.add("§8[KBBall] ");
                    }
                    if (WeaponDetection.hasEatenGoldenApple((EntityPlayer) renderingEntity) > 0) {
                        ranks.add("§c[Gapple] ");
                    }
                    String rank = String.join(" ", ranks);
                    if (antiCheat.isCheater(renderingEntity.getName())) {
                        hackname = "§cHacker§f | ";
                    }

                    double fontScale = scale.getValue();
                    float middle = x + ((right - x) / 2);
                    float textWidth;
                    double fontHeight = font.getHeight() * fontScale;
                    textWidth = font.getStringWidth( hackname + rank + text);
                    middle -= (textWidth * fontScale) / 2f;

                    GLUtils.pushMatrix();
                    glTranslated(middle, y - (fontHeight + 2), 0);
                    glScaled(fontScale, fontScale, 1);
                    glTranslated(-middle, -(y - (fontHeight + 2)), 0);

                    GLUtils.pushMatrix();
                    if (nametagSettings.getSetting("Background").isEnabled()) {
                        Color backgroundTagColor = backgroundColor;
                        if (nametagSettings.getSetting("Round").isEnabled()) {
                            RoundedUtil.drawRound(middle - 3, (float) (y - (fontHeight + 7)), textWidth + 6,
                                    (float) ((fontHeight / fontScale) - 3.0f), 4, backgroundTagColor);
                        } else {
                            Gui.drawRect2(middle - 3, (float) (y - (fontHeight + 7)), textWidth + 6,
                                    (fontHeight / fontScale) - 3.0f, backgroundTagColor.getRGB());
                        }
                    }
                    GLUtils.popMatrix();
                    FontManager.Naven18.drawStringWithShadow(hackname + rank + text, middle, (float) (y - (fontHeight + 5)), healthColor.getRGB());
                    GLUtils.resetColor();

                   GLUtils.popMatrix();
                }
            }
        }
    }

    private void gradientColorWheel(Pair<Color, Color> colors) {
        firstColor = ColorUtil.interpolateColorsBackAndForth(15, 0, colors.getFirst(), colors.getSecond(), false);
        secondColor = ColorUtil.interpolateColorsBackAndForth(15, 90, colors.getFirst(), colors.getSecond(), false);
        thirdColor = ColorUtil.interpolateColorsBackAndForth(15, 180, colors.getFirst(), colors.getSecond(), false);
        fourthColor = ColorUtil.interpolateColorsBackAndForth(15, 270, colors.getFirst(), colors.getSecond(), false);
    }

    private boolean shouldRender(Entity entity) {
        if (entity.isDead || entity.isInvisible()) {
            return false;
        }
        if (validEntities.getSetting("Players").isEnabled() && entity instanceof EntityPlayer) {
            if (entity == mc.thePlayer) {
                return mc.gameSettings.thirdPersonView != 0;
            }
            return !entity.getDisplayName().getUnformattedText().contains("[NPC");
        }
        if (validEntities.getSetting("Animals").isEnabled() && entity instanceof EntityAnimal) {
            return true;
        }
        return validEntities.getSetting("mobs").isEnabled() && entity instanceof EntityMob;
    }
}
