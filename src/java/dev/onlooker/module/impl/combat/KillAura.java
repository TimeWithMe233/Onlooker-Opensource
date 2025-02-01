package dev.onlooker.module.impl.combat;

import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viarewind.utils.PacketUtil;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import dev.onlooker.Client;
import dev.onlooker.commands.impl.FriendCommand;
import dev.onlooker.event.impl.player.EventAttack;
import dev.onlooker.event.impl.player.KeepSprintEvent;
import dev.onlooker.event.impl.player.MotionEvent;
import dev.onlooker.event.impl.render.Render3DEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.impl.display.HUDMod;
import dev.onlooker.module.impl.misc.Teams;
import dev.onlooker.module.impl.player.Blink;
import dev.onlooker.module.impl.world.Scaffold;
import dev.onlooker.module.settings.impl.BooleanSetting;
import dev.onlooker.module.settings.impl.ModeSetting;
import dev.onlooker.module.settings.impl.MultipleBoolSetting;
import dev.onlooker.module.settings.impl.NumberSetting;
import dev.onlooker.utils.animations.Animation;
import dev.onlooker.utils.animations.Direction;
import dev.onlooker.utils.animations.impl.DecelerateAnimation;
import dev.onlooker.utils.misc.MathUtil;
import dev.onlooker.utils.misc.MathUtils;
import dev.onlooker.utils.player.InventoryUtils;
import dev.onlooker.utils.player.RotationComponent;
import dev.onlooker.utils.player.RotationUtils;
import dev.onlooker.utils.render.RenderUtil;
import dev.onlooker.utils.server.PacketUtils;
import dev.onlooker.utils.time.TimerUtil;
import dev.onlooker.utils.vector.Vector2f;
import dev.onlooker.utils.addons.viamcp.viamcp.ViaMCP;
import dev.onlooker.utils.addons.viamcp.viamcp.fixes.AttackOrder;
import io.netty.buffer.ByteBuf;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class KillAura extends Module {
    private int cps;
    private int targetIndex = 0;
    public static float yaw = 0.0F;
    public static boolean attacking;
    public static boolean blocking;
    public static boolean wasBlocking;
    public static float[] rotation = new float[]{0, 0};
    public static EntityLivingBase target;
    private EntityLivingBase auraESPTarget;
    private final TimerUtil attackTimer = new TimerUtil();
    private final TimerUtil switchTimer = new TimerUtil();
    public static final List<EntityLivingBase> targets = new ArrayList<>();

    private static final MultipleBoolSetting targetsSetting = new MultipleBoolSetting("Targets",
            new BooleanSetting("Players", true),
            new BooleanSetting("Animals", false),
            new BooleanSetting("Mobs", false),
            new BooleanSetting("Invisibles", false));

    private final ModeSetting mode = new ModeSetting("Mode", "Single", "Single", "Switch");
    public final NumberSetting switchDelay = new NumberSetting("SwitchDelay", 170, 1000.0, 0.0, 1.0);

    private final BooleanSetting rotations = new BooleanSetting("Rotations", true);
    private final NumberSetting rotationSpeed = new NumberSetting("Rotation speed", 5, 10, 5, 1);
    private final ModeSetting rotationMode = new ModeSetting("Rotation Mode", "HvH", "HvH", "Vanilla", "Normal");

    public static final BooleanSetting autoblock = new BooleanSetting("Autoblock", true);
    private final ModeSetting autoblockMode = new ModeSetting("Autoblock Mode", "Grim", "Fake", "Grim", "Watchdog");
    private final ModeSetting sortMode = new ModeSetting("Sort Mode", "Range", "Range", "Hurt Time", "Health", "Armor");
    public static final ModeSetting auraESP = new ModeSetting("Target ESP", "Nurikzapen", "Nurikzapen", "Round", "Circle", "Tracer", "Box", "Tracer", "None");

    private final NumberSetting minCPS = new NumberSetting("Min CPS", 10, 20, 1, 1);
    private final NumberSetting maxCPS = new NumberSetting("Max CPS", 20, 20, 1, 1);
    public static final NumberSetting reach = new NumberSetting("Reach", 4, 6, 3, 0.1);
    private final BooleanSetting strafefix = new BooleanSetting("Movement Fix", true);
    private final BooleanSetting KeepSprint = new BooleanSetting("Keep Sprint", true);
    private static final BooleanSetting ThroughWalls = new BooleanSetting("Through Walls", false);
    private final BooleanSetting RayCast = new BooleanSetting("Ray Cast", true);

    public KillAura() {
        super("KillAura", Category.COMBAT, "Automatically attacks players");
        autoblockMode.addParent(autoblock, a -> autoblock.isEnabled());
        this.switchDelay.addParent(this.mode, (m) -> this.mode.is("Switch"));
        this.addSettings(targetsSetting, mode, switchDelay, rotations, rotationMode, rotationSpeed, autoblock, autoblockMode, auraESP, minCPS, maxCPS, reach, sortMode, strafefix, KeepSprint, ThroughWalls, RayCast);
    }

    private void attack() {
        if (target != null) {
            this.attackEntity(target);
            if (mc.thePlayer.fallDistance > 0.0f && !mc.thePlayer.onGround && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWater() && !mc.thePlayer.isPotionActive(Potion.blindness) && mc.thePlayer.ridingEntity == null) {
                mc.thePlayer.onCriticalHit(target);
            }
            if (EnchantmentHelper.getModifierForCreature(mc.thePlayer.getHeldItem(), target.getCreatureAttribute()) > 0.0f) {
                mc.thePlayer.onEnchantmentCritical(target);
                PacketUtils.sendPacket(new C0APacketAnimation());
            }
        }
    }

    private void attackEntity(final Entity target) {
        AttackOrder.sendFixedAttack(mc.thePlayer, target);
        this.attackTimer.reset();
    }

    @Override
    public void onDisable() {
        target = null;
        targets.clear();
        attacking = false;
        blocking = false;
        if (wasBlocking) {
            if (this.autoblockMode.is("Grim")) {
                KillAura.mc.gameSettings.keyBindUseItem.pressed = false;
            }
            if (this.autoblockMode.is("Hypixel")) {
                KillAura.mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            }
        }
        wasBlocking = false;
        super.onDisable();
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        this.setSuffix(mode.getMode());

        if (minCPS.getValue() > maxCPS.getValue()) {
            minCPS.setValue(minCPS.getValue() - 1);
        }

        if (Client.INSTANCE.getModuleCollection().get(Scaffold.class).isEnabled()) return;
        // Gets all entities in specified range, sorts them using your specified sort mode, and adds them to target list

        this.sortTargets();
        if (target == null) {
            yaw = mc.thePlayer.rotationYaw;
        }

        if (event.isPre()) {
            attacking = !targets.isEmpty();
            blocking = autoblock.isEnabled() && attacking && InventoryUtils.isHoldingSword();
            if (attacking) {
                if (mode.is("Switch")) {
                    if (switchTimer.hasTimeElapsed(switchDelay.getValue().intValue(), true)) {
                        targetIndex = (targetIndex + 1) % targets.size();
                    }
                    if (targetIndex < targets.size()) {
                        target = targets.get(targetIndex);
                    } else {
                        target = null;
                    }
                } else {
                    if (!targets.isEmpty()) {
                        target = targets.get(0);
                    } else {
                        target = null;
                    }
                }

                if (rotations.isEnabled()) {
                    final double minRotationSpeed = this.rotationSpeed.getValue();
                    final double maxRotationSpeed = this.rotationSpeed.getValue();
                    final float rotationSpeed = (float) MathUtil.getRandom(minRotationSpeed, maxRotationSpeed);
                    switch (rotationMode.getMode()) {
                        case "HvH":
                            if (target != null) {
                                rotation = RotationUtils.getHVHRotation(target, reach.getValue() + 0.1);
                            }
                            break;
                        case "Vanilla":
                            if (KillAura.target != null) {
                                rotation = RotationUtils.getRotationsNeeded(KillAura.target);
                            }
                            break;
                        case "Normal":
                            if (target != null) {
                                rotation = KillAura.getRotationNormal(target);
                            }
                    }
                    RotationComponent.setRotations(new Vector2f(rotation[0],Math.min(90.0f, rotation[1])), rotationSpeed, strafefix.getValue());
                }

                if (RayCast.isEnabled() && !RotationUtils.isMouseOver(event.getYaw(), event.getPitch(), target, reach.getValue().floatValue()))
                    return;

                if (attackTimer.hasTimeElapsed(cps, true)) {
                    final int maxValue = (int) ((minCPS.getMaxValue() - maxCPS.getValue()) * 5.0);
                    final int minValue = (int) ((minCPS.getMaxValue() - minCPS.getValue()) * 5.0);
                    cps = MathUtils.getRandomInRange(minValue, maxValue);
                    EventAttack attackEvent = new EventAttack(target);
                    Client.INSTANCE.getEventProtocol().handleEvent(attackEvent);
                    attack();
                }

            } else {
                attackTimer.reset();
                target = null;
            }
        }

        if (blocking) {
            switch (this.autoblockMode.getMode()) {
                case "Grim":
                    if (event.isPost()) {
                        if (ViaMCP.INSTANCE.getAsyncVersionSlider().id <= 47) {
                            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
                            break;
                        }
                        if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                            PacketUtils.sendC0F();
                            PacketWrapper useItem_1_9 = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
                            useItem_1_9.write(Type.VAR_INT, 1);
                            PacketUtils.sendToServer(useItem_1_9, Protocol1_8To1_9.class, true, true);
                            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
                            wasBlocking = true;
                        }
                    }
                    break;
                case "Watchdog":
                    if (event.isPre()) {
                        if (!mc.isSingleplayer()) {
                            PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F));
                            PacketWrapper useItem = PacketWrapper.create(29, (ByteBuf) null, (UserConnection) Via.getManager().getConnectionManager().getConnections().iterator().next());
                            useItem.write(Type.VAR_INT, 1);
                            PacketUtil.sendToServer(useItem, Protocol1_8To1_9.class, true, true);
                            wasBlocking = true;
                        }
                    }
                    break;
                case "Fake":
                    break;
            }
        } else if (wasBlocking && this.autoblockMode.is("Grim") && event.isPre()) {
            KillAura.mc.gameSettings.keyBindUseItem.pressed = false;
            wasBlocking = false;
        } else if (wasBlocking && this.autoblockMode.is("Watchdog") && event.isPre()) {
            PacketUtils.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            wasBlocking = false;
        }
    }

    public void sortTargets() {
        targets.clear();
        for (Entity entity : mc.theWorld.getLoadedEntityList()) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                if (mc.thePlayer.getDistanceToEntity(entity) <= reach.getValue() && isValid(entity) && mc.thePlayer != entityLivingBase && !FriendCommand.isFriend(entityLivingBase.getName())) {
                    targets.add(entityLivingBase);
                }
            }
        }
        switch (sortMode.getMode()) {
            case "Range":
                targets.sort(Comparator.comparingDouble(mc.thePlayer::getDistanceToEntity));
                break;
            case "Hurt Time":
                targets.sort(Comparator.comparingInt(EntityLivingBase::getHurtTime));
                break;
            case "Health":
                targets.sort(Comparator.comparingDouble(EntityLivingBase::getHealth));
                break;
            case "Armor":
                targets.sort(Comparator.comparingInt(EntityLivingBase::getTotalArmorValue));
                break;
        }
    }

    public static boolean isValid(Entity entity) {

        if (entity instanceof EntityPlayer && targetsSetting.getSetting("Players").isEnabled() && !entity.isInvisible() && mc.thePlayer.canEntityBeSeen(entity))
            return true;

        if (entity instanceof EntityPlayer && targetsSetting.getSetting("Invisibles").isEnabled() && entity.isInvisible())
            return true;

        if (entity instanceof EntityPlayer && ThroughWalls.isEnabled() && !mc.thePlayer.canEntityBeSeen(entity))
            return true;

        if (entity instanceof EntityAnimal && targetsSetting.getSetting("Animals").isEnabled())
            return true;

        if (entity instanceof EntityMob && targetsSetting.getSetting("Mobs").isEnabled())
            return true;

        if (entity.isInvisible() && targetsSetting.getSetting("Invisibles").isEnabled())
            return true;

        if (Teams.isSameTeam(target)) {
            return false;
        }
        return false;
    }

    @Override
    public void onKeepSprintEvent(KeepSprintEvent event) {
        if (KeepSprint.isEnabled()) {
            event.cancel();
        }
    }

    @Override
    public void onAttackEvent(EventAttack event) {
        if (event.getTargetEntity() != null) {
            try {
                auraESPTarget = event.getTargetEntity();
            } catch (ClassCastException e) {
                auraESPTarget = null;
            }
        }
    }

    private final Animation auraESPAnim = new DecelerateAnimation(300, 1);

    @Override
    public void onRender3DEvent(Render3DEvent event) {
        auraESPAnim.setDirection(target != null ? Direction.FORWARDS : Direction.BACKWARDS);

        if (target != null) {
            auraESPTarget = target;
        }

        if (auraESPAnim.finished(Direction.BACKWARDS)) {
            auraESPTarget = null;
        }

        Color color = HUDMod.getClientColors().getFirst();
        Color color2 = HUDMod.getClientColors().getSecond();
        float dst = mc.thePlayer.getSmoothDistanceToEntity(auraESPTarget);

        if (auraESPTarget != null) {
            if (auraESP.is("Box")) {
                RenderUtil.renderBoundingBox(auraESPTarget, color, auraESPAnim.getOutput().floatValue());
            }
            if (auraESP.is("Circle")) {
                RenderUtil.drawCircle(auraESPTarget, event.getTicks(), .75f, color.getRGB(), auraESPAnim.getOutput().floatValue());
            }
            if (auraESP.is("Tracer")) {
                RenderUtil.drawTracerLine(auraESPTarget, 4f, Color.BLACK, auraESPAnim.getOutput().floatValue());
                RenderUtil.drawTracerLine(auraESPTarget, 2.5f, color, auraESPAnim.getOutput().floatValue());
            }
            try {
                RenderUtil.drawTargetESP2D(Objects.requireNonNull(RenderUtil.targetESPSPos(auraESPTarget)).x, Objects.requireNonNull(RenderUtil.targetESPSPos(auraESPTarget)).y, color, color2,
                        (1.0f - MathHelper.clamp_float(Math.abs(dst - 6.0f) / 60.0f, 0f, 0.75f)) * 1, targetIndex, auraESPAnim.getOutput().floatValue());
            } catch (Exception e) {
                auraESPTarget = null;
            }
        }
    }

    public static float[] getRotationNormal(EntityLivingBase target) {
        double xDiff = target.posX - KillAura.mc.thePlayer.posX;
        double yDiff = target.posY + (double) (target.getEyeHeight() / 5.0f * 4.0f) - (KillAura.mc.thePlayer.posY + (double) KillAura.mc.thePlayer.getEyeHeight());
        return KillAura.getRotationFloat(target, xDiff, yDiff);
    }

    private static float[] getRotationFloat(EntityLivingBase target, double xDiff, double yDiff) {
        double zDiff = target.posZ - KillAura.mc.thePlayer.posZ;
        double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) (-Math.atan2(yDiff, dist) * 180.0 / Math.PI);
        float[] array = new float[2];
        int n = 0;
        float rotationYaw = rotation[0];
        array[n] = rotationYaw + MathHelper.wrapAngleTo180_float(yaw - rotation[0]);
        int n3 = 1;
        float rotationPitch = KillAura.mc.thePlayer.rotationPitch;
        array[n3] = rotationPitch + MathHelper.wrapAngleTo180_float(pitch - KillAura.mc.thePlayer.rotationPitch);
        return array;
    }
}
