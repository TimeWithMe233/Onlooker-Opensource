package dev.onlooker.module.impl.display;

import dev.onlooker.Client;
import dev.onlooker.event.impl.render.PreRenderEvent;
import dev.onlooker.event.impl.render.Render2DEvent;
import dev.onlooker.event.impl.render.Render3DEvent;
import dev.onlooker.event.impl.render.ShaderEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.impl.combat.KillAura;
import dev.onlooker.module.impl.display.targethud.TargetHUD;
import dev.onlooker.module.settings.ParentAttribute;
import dev.onlooker.module.settings.impl.BooleanSetting;
import dev.onlooker.module.settings.impl.ModeSetting;
import dev.onlooker.utils.animations.Animation;
import dev.onlooker.utils.animations.Direction;
import dev.onlooker.utils.animations.impl.EaseBackIn;
import dev.onlooker.utils.objects.Dragging;
import dev.onlooker.utils.objects.GradientColorWheel;
import dev.onlooker.utils.render.ColorUtil;
import dev.onlooker.utils.render.ESPUtil;
import dev.onlooker.utils.render.RenderUtil;
import dev.onlooker.utils.tuples.Pair;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.util.vector.Vector4f;

import java.awt.*;

public class TargetHUDMod extends Module {

    private final ModeSetting targetHud = new ModeSetting("Mode", "OnLooker",  "OnLooker", "Rise", "Rise2", "Tenacity", "Astolfo", "Akrien", "Exhibition");
    private final BooleanSetting trackTarget = new BooleanSetting("Track Target", false);
    private final ModeSetting trackingMode = new ModeSetting("Tracking Mode", "Middle", "Middle", "Top", "Left", "Right");

    public static boolean renderLayers = true;

    private final GradientColorWheel colorWheel = new GradientColorWheel();

    public TargetHUDMod() {
        super("TargetHUD", Category.DISPLAY, "Displays info about the KillAura target");
        trackingMode.addParent(trackTarget, ParentAttribute.BOOLEAN_CONDITION);
        addSettings(targetHud, trackTarget, trackingMode, colorWheel.createModeSetting("Color Mode", "Dark"), colorWheel.getColorSetting());
        TargetHUD.init();
        if (!enabled) this.toggleSilent();
    }

    private EntityLivingBase target;
    private final Dragging drag = Client.INSTANCE.createDrag(this, "TargetHud", 300, 300);

    public Animation openAnimation1 = new EaseBackIn(200, 1.0, 1.3f);

    private KillAura killAura;

    private Vector4f targetVector;

    @Override
    public void onRender3DEvent(Render3DEvent event) {
        if (trackTarget.isEnabled() && target != null) {
            for (Entity entity : mc.theWorld.loadedEntityList) {
                if (entity instanceof EntityLivingBase) {
                    EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                    if (target.equals(entityLivingBase)) {
                        targetVector = ESPUtil.getEntityPositionsOn2D(entity);
                    }
                }
            }

        }
    }

    @Override
    public void onPreRenderEvent(PreRenderEvent event) {
        TargetHUD currentTargetHUD = TargetHUD.get(targetHud.getMode());
        drag.setWidth(currentTargetHUD.getWidth());
        drag.setHeight(currentTargetHUD.getHeight());

        if (killAura == null) {
            killAura = (KillAura) Client.INSTANCE.getModuleCollection().get(KillAura.class);
        }

        if (!(mc.currentScreen instanceof GuiChat)) {
            if (!killAura.isEnabled()) {
                openAnimation1.setDirection(Direction.BACKWARDS);
                if (openAnimation1.finished(Direction.BACKWARDS)) {
                }
            }

            if (target == null && killAura.target != null) {
                target = killAura.target;
                openAnimation1.setDirection(Direction.FORWARDS);

            } else if (killAura.target == null || target != killAura.target) {
                openAnimation1.setDirection(Direction.BACKWARDS);
            }

            if (openAnimation1.finished(Direction.BACKWARDS)) {
                target = null;
            }
        } else {
            openAnimation1.setDirection(Direction.FORWARDS);
            target = mc.thePlayer;
        }


        if (target != null) {
            colorWheel.setColorsForMode("Dark", ColorUtil.brighter(new Color(30, 30, 30), .65f));
            colorWheel.setColors();
            currentTargetHUD.setColorWheel(colorWheel);
        }

    }

    @Override
    public void onRender2DEvent(Render2DEvent e) {
        this.setSuffix(targetHud.getMode());
        boolean tracking = trackTarget.isEnabled() && targetVector != null && target != mc.thePlayer;

        TargetHUD currentTargetHUD = TargetHUD.get(targetHud.getMode());

        if (target != null) {


            float trackScale = 1;
            float x = drag.getX(), y = drag.getY();
            if (tracking) {
                float newWidth = (targetVector.getZ() - targetVector.getX()) * 1.4f;
                trackScale = Math.min(1, newWidth / currentTargetHUD.getWidth());

                Pair<Float, Float> coords = getTrackedCoords();
                x = coords.getFirst();
                y = coords.getSecond();
            }


            RenderUtil.scaleStart(x + drag.getWidth() / 2f, y + drag.getHeight() / 2f,
                    (float) (0 + openAnimation1.getOutput().floatValue()) * trackScale);
            float alpha = Math.min(1f, openAnimation1.getOutput().floatValue() * 3);

            currentTargetHUD.render(x, y, alpha, target);


            RenderUtil.scaleEnd();
        }
    }


    @Override
    public void onShaderEvent(ShaderEvent e) {
        float x = drag.getX(), y = drag.getY();
        float trackScale = 1;
        TargetHUD currentTargetHUD = TargetHUD.get(targetHud.getMode());
        if (trackTarget.isEnabled() && targetVector != null && target != mc.thePlayer) {
            Pair<Float, Float> coords = getTrackedCoords();
            x = coords.getFirst();
            y = coords.getSecond();

            float newWidth = (targetVector.getZ() - targetVector.getX()) * 1.4f;
            trackScale = Math.min(1, newWidth / currentTargetHUD.getWidth());
        }


        if (target != null) {

            RenderUtil.scaleStart(x + drag.getWidth() / 2f, y + drag.getHeight() / 2f,
                    (float) (0 + openAnimation1.getOutput().floatValue()) * trackScale);
            float alpha = Math.min(1f, openAnimation1.getOutput().floatValue() * 3);

            currentTargetHUD.renderEffects(x, y, alpha);

            RenderUtil.scaleEnd();
        }
    }


    @Override
    public void onEnable() {
        super.onEnable();

        target = null;
    }


    private Pair<Float, Float> getTrackedCoords() {
        ScaledResolution sr = new ScaledResolution(mc);
        float width = drag.getWidth(), height = drag.getHeight();
        float x = targetVector.getX(), y = targetVector.getY();
        float entityWidth = (targetVector.getZ() - targetVector.getX());
        float entityHeight = (targetVector.getW() - targetVector.getY());
        float middleX = x + entityWidth / 2f - width / 2f;
        float middleY = y + entityHeight / 2f - height / 2f;
        switch (trackingMode.getMode()) {
            case "Middle":
                return Pair.of(middleX, middleY);
            case "Top":
                return Pair.of(middleX, y - (height / 2f + height / 4f));
            case "Left":
                return Pair.of(x - (width / 2f + width / 4f), middleY);
            default:
                return Pair.of(x + entityWidth - (width / 4f), middleY);
        }
    }


}
