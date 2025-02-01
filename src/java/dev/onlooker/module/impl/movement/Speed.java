package dev.onlooker.module.impl.movement;

import dev.onlooker.event.impl.player.MotionEvent;
import dev.onlooker.event.impl.player.UpdateEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.impl.combat.KillAura;
import dev.onlooker.module.settings.impl.ModeSetting;
import dev.onlooker.module.settings.impl.NumberSetting;
import dev.onlooker.utils.player.MoveUtil;
import dev.onlooker.utils.player.MovementUtils;
import dev.onlooker.utils.player.RotationComponent;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public final class Speed extends Module {
    private float speed = 0.08F;
    private final ModeSetting mode = new ModeSetting("Mode", "Entity", "Hypixel", "Strafe", "Entity", "Grim");
    private final ModeSetting watchdogMode = new ModeSetting("Watchdog Mode", "GroundStrafe", "Strafe", "GroundStrafe");
    private final NumberSetting range = new NumberSetting("CheckRange", 1, 5, 0.1,0.1);
    private final NumberSetting boostAmount = new NumberSetting("BoostAmount", 1, 2, 0.1,0.1);
    int onGroundticks = 0;

    public Speed() {
        super("Speed",Category.MOVEMENT, "Makes you go faster");
        watchdogMode.addParent(mode, modeSetting -> modeSetting.is("Hypixel"));
        range.addParent(mode, modeSetting -> modeSetting.is("Entity"));
        boostAmount.addParent(mode, modeSetting -> modeSetting.is("Entity"));
        this.addSettings(mode, watchdogMode, range, boostAmount);
    }

    @Override
    public void onUpdateEvent(UpdateEvent event) {
        switch (mode.getMode()) {
            case "Entity":
                if(isNull())return;
                if (mc.thePlayer.moveForward == 0.0f && mc.thePlayer.moveStrafing == 0.0f) {
                    return;
                }
                double collisions = 0;
                for (Entity entity : mc.thePlayer.getEntityWorld().loadedEntityList) {
                    if (canCauseSpeed(entity) && mc.thePlayer.getDistanceToEntity(entity) <= range.get()) {
                        collisions = boostAmount.get();
                    }
                }
                double yaw = Math.toRadians(mc.thePlayer.movementYaw);
                double boost = this.speed * collisions;
                for (Entity entity : mc.thePlayer.getEntityWorld().loadedEntityList) {
                    if (canCauseSpeed(entity) && mc.thePlayer.getDistanceToEntity(entity) <= range.get()) {
                        mc.thePlayer.addVelocity(-Math.sin(yaw) * boost, 0.0, Math.cos(yaw) * boost);
                    }
                }
                break;
        }
    }

    private boolean canCauseSpeed(Entity entity) {
        return entity != mc.thePlayer && entity instanceof EntityPlayer;
    }


    @Override
    public void onMotionEvent(MotionEvent e) {
        this.setSuffix(mode.getMode());
        switch (mode.getMode()) {
            case "Strafe":
                if (e.isPre() && MovementUtils.isMoving()) {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                    } else {
                        MovementUtils.setSpeed(MovementUtils.getSpeed());
                    }
                }
                break;
            case "Hypixel":
                mc.gameSettings.keyBindJump.pressed = false;
                if (watchdogMode.is("Strafe")) {
                    if (e.isPre() && MovementUtils.isMoving()) {
                        if (Math.abs(mc.thePlayer.movementInput.moveStrafe) < 0.1F) {
                            mc.thePlayer.jumpMovementFactor = 0.022499F;
                        } else {
                            mc.thePlayer.jumpMovementFactor = 0.0234F;
                        }
                        mc.gameSettings.keyBindJump.pressed = mc.gameSettings.isKeyDown(mc.gameSettings.keyBindJump);
                        if (MovementUtils.getSpeed() < 0.21F && !mc.thePlayer.onGround) {
                            MovementUtils.Strafe(0.22F);
                        }
                        if (mc.thePlayer.onGround) {
                            mc.gameSettings.keyBindJump.pressed = false;
                            mc.thePlayer.motionY = 0.0008;
                            mc.thePlayer.jump();
                            if (mc.thePlayer.isAirBorne) {
                                MovementUtils.strafe(0.45);
                                if (MovementUtils.getSpeed() < (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.55F : 0.5F))
                                    MovementUtils.Strafe(mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.5349F : 0.4849F);
                            }
                        }
                    }
                } else if (watchdogMode.is("GroundStrafe")) {
                    if (mc.thePlayer.isCollidedVertically && MovementUtils.isMoving()) {
                        BlockPos blockPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                        if (mc.thePlayer.onGround && MovementUtils.isMoving() && !(mc.theWorld.getBlockState(blockPos).getBlock() instanceof BlockStairs)) {
                            mc.thePlayer.motionY = MovementUtils.getJumpBoostModifier(0.41999998688698F);
                            MovementUtils.setSpeed((float) Math.max(MovementUtils.getBaseMoveSpeed(), 0.475f + 0.04F * MovementUtils.getSpeedEffect()));
                        }
                    }
                    if (!MovementUtils.isMoving()) {
                        mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
                    }
                    break;
                }
            case "Grim" :
                AxisAlignedBB playerBox = Speed.mc.thePlayer.boundingBox.expand(1.0, 1.0, 1.0);
                int c = 0;
                for (Entity entity : Speed.mc.theWorld.loadedEntityList) {
                    if (!(entity instanceof EntityLivingBase) && !(entity instanceof EntityBoat) && !(entity instanceof EntityMinecart) && !(entity instanceof EntityFishHook) || entity instanceof EntityArmorStand || entity.getEntityId() == Speed.mc.thePlayer.getEntityId() || !playerBox.intersectsWith(entity.boundingBox) || entity.getEntityId() == -8 || entity.getEntityId() == -1337) continue;
                    ++c;
                }
                if (c > 0 && MoveUtil.isMoving()) {
                    double strafeOffset = (double)Math.min(c, 3) * 0.04;
                    float yaw = this.getMoveYaw();
                    double mx = -Math.sin(Math.toRadians(yaw));
                    double mz = Math.cos(Math.toRadians(yaw));
                    Speed.mc.thePlayer.addVelocity(mx * strafeOffset, 0.0, mz * strafeOffset);
                    if (c < 4 && KillAura.target != null && this.shouldFollow()) {
                        Speed.mc.gameSettings.keyBindLeft.pressed = true;
                        break;
                    }
                    Speed.mc.gameSettings.keyBindLeft.pressed = GameSettings.isKeyDown(Speed.mc.gameSettings.keyBindLeft);
                    break;
                }
                Speed.mc.gameSettings.keyBindLeft.pressed = GameSettings.isKeyDown(Speed.mc.gameSettings.keyBindLeft);
                break;
        }
    }
    public boolean shouldFollow() {
        return isEnabled() && Speed.mc.gameSettings.keyBindJump.isKeyDown();
    }
    private float getMoveYaw() {
        EntityPlayerSP thePlayer = Speed.mc.thePlayer;
        float moveYaw = thePlayer.rotationYaw;
        if (thePlayer.moveForward != 0.0f && thePlayer.moveStrafing == 0.0f) {
            moveYaw += thePlayer.moveForward > 0.0f ? 0.0f : 180.0f;
        } else if (thePlayer.moveForward != 0.0f && thePlayer.moveStrafing != 0.0f) {
            moveYaw = thePlayer.moveForward > 0.0f ? (moveYaw += thePlayer.moveStrafing > 0.0f ? -45.0f : 45.0f) : (moveYaw -= thePlayer.moveStrafing > 0.0f ? -45.0f : 45.0f);
            moveYaw += thePlayer.moveForward > 0.0f ? 0.0f : 180.0f;
        } else if (thePlayer.moveStrafing != 0.0f && thePlayer.moveForward == 0.0f) {
            moveYaw += thePlayer.moveStrafing > 0.0f ? -70.0f : 70.0f;
        }
        if (KillAura.target != null && Speed.mc.gameSettings.keyBindJump.isKeyDown()) {
            moveYaw = RotationComponent.rotation.getX();
        }
        return moveYaw;
    }
    public void onDisable() {
        this.onGroundticks = 0;
        mc.timer.timerSpeed = 1.0F;
        super.onDisable();
    }
}
