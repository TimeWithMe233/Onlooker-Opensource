package dev.onlooker.module.impl.combat;

import dev.onlooker.Client;
import dev.onlooker.event.impl.network.PacketReceiveEvent;
import dev.onlooker.event.impl.network.PacketSendEvent;
import dev.onlooker.event.impl.player.EventAttack;
import dev.onlooker.event.impl.player.UpdateEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.impl.world.Disabler;
import dev.onlooker.module.settings.Setting;
import dev.onlooker.module.settings.impl.BooleanSetting;
import dev.onlooker.module.settings.impl.ModeSetting;
import dev.onlooker.module.settings.impl.NumberSetting;
import dev.onlooker.utils.Utils;
import dev.onlooker.utils.addons.viamcp.vialoadingbase.ViaLoadingBase;
import dev.onlooker.utils.player.RayCastUtil;
import dev.onlooker.utils.server.PacketUtils;
import dev.onlooker.utils.vector.Vector2f;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.MovingObjectPosition;

public class Velocity extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Grim", "Strict", "Hypixel", "Grim");
    private final NumberSetting horizontal = new NumberSetting("Horizontal", 0, 100, -100, 1);
    private final NumberSetting vertical = new NumberSetting("Vertical", 0, 100, -100, 1);
    private final NumberSetting airhorizontal = new NumberSetting("Air Horizontal", 0, 100, -100, 1);
    private final NumberSetting airvertical = new NumberSetting("Air Vertical", 0, 100, -100, 1);
    private final BooleanSetting legitSprint = new BooleanSetting("Legit Sprint", false);
    double motion;
    boolean attacked;
    public static boolean velocityInput;
    public static boolean velocityOverrideSprint;
    boolean lastSprinting;

    public Velocity() {
        super("Velocity", Category.COMBAT, "Reduces your knockback");
        Setting.addParent(mode, m -> m.is("Strict"), horizontal, vertical, airhorizontal, airvertical);
        Setting.addParent(mode, m -> m.is("Grim"), legitSprint);
        this.addSettings(mode, horizontal, vertical, airhorizontal, airvertical, legitSprint);
    }

    @Override
    public void onUpdateEvent(UpdateEvent event) {
        if (mode.is("Grim")) {
            if (ViaLoadingBase.getInstance().getTargetVersion().getVersion() > 47) {
                if (velocityInput) {
                    if (this.attacked) {
                        this.mc.thePlayer.motionX *= this.motion;
                        this.mc.thePlayer.motionZ *= this.motion;
                        this.attacked = false;
                    }
                    if (this.mc.thePlayer.hurtTime == 0) {
                        velocityInput = false;
                    }
                }
            } else if (this.mc.thePlayer.hurtTime > 0 && this.mc.thePlayer.onGround) {
                this.mc.thePlayer.addVelocity(-1.3E-10, -1.3E-10, -1.3E-10);
                this.mc.thePlayer.setSprinting(false);
            }
        }
    }

    @Override
    public void onDisable() {
        mc.thePlayer.speedInAir = 0.02f;
        super.onDisable();
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent e) {
        setSuffix(mode.getMode());
        Packet<?> packet = e.getPacket();
        if (e.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity s12 = ((S12PacketEntityVelocity) e.getPacket());
            switch (mode.getMode()) {
                case "Strict": {
                    if (Utils.mc.thePlayer.isOnGround()) {
                        if (horizontal.getValue() == 0.0d && vertical.getValue() == 0.0d) {
                            e.cancel();
                            return;
                        }
                        s12.motionX *= horizontal.getValue().intValue() / 100;
                        s12.motionZ *= horizontal.getValue().intValue() / 100;
                        s12.motionX *= vertical.getValue().intValue() / 100;
                    } else {
                        if (airhorizontal.getValue() == 0.0d && airvertical.getValue() == 0.0d) {
                            e.cancel();
                            return;
                        }
                        s12.motionX *= (int) airhorizontal.getValue().intValue() / 100;
                        s12.motionZ *= (int) airhorizontal.getValue().intValue() / 100;
                        s12.motionX *= (int) airvertical.getValue().intValue() / 100;
                    }
                }
                break;
                case "Watchdog":
                    e.cancel();
                    mc.thePlayer.motionY = (double) s12.motionY / 8000.0;
                    break;
                case "Grim": {
                    if (ViaLoadingBase.getInstance().getTargetVersion().getVersion() > 47) {
                        EntityLivingBase targets;
                        velocityInput = true;
                        MovingObjectPosition movingObjectPosition = RayCastUtil.rayCast(new Vector2f(this.mc.thePlayer.lastReportedYaw, this.mc.thePlayer.lastReportedPitch), 3.0);
                        targets = movingObjectPosition != null && MovingObjectPosition.entityHit == KillAura.target ? (EntityLivingBase) MovingObjectPosition.entityHit : KillAura.target;
                        if (targets != null && !this.mc.thePlayer.isOnLadder()) {
                            boolean state = EntityPlayerSP.serverSprintState;
                            if (!state) {
                                PacketUtils.sendPacket(new C0BPacketEntityAction(this.mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                            }
                            for (int i = 0; i < 5; ++i) {
                                Client.INSTANCE.getEventProtocol().handleEvent(new EventAttack(targets));
                                PacketUtils.sendPacket(new C02PacketUseEntity((Entity) targets, C02PacketUseEntity.Action.ATTACK));
                                PacketUtils.sendPacket(new C0APacketAnimation());
                            }
                            velocityOverrideSprint = true;
                            this.mc.thePlayer.setSprinting(true);
                            EntityPlayerSP.serverSprintState = true;
                            this.attacked = true;
                            this.motion = this.getMotion();
                        }
                    }
                }

                if (e.getPacket() instanceof S27PacketExplosion) {
                    S27PacketExplosion s27 = ((S27PacketExplosion) e.getPacket());
                    switch (mode.getMode()) {
                        case "Strict": {
                            if (Utils.mc.thePlayer.onGround) {
                                if (horizontal.getValue() == 0.0d && vertical.getValue() == 0.0d) {
                                    e.cancel();
                                    return;
                                }
                                s27.motionX *= horizontal.getValue().intValue() / 100;
                                s27.motionZ *= horizontal.getValue().intValue() / 100;
                                s27.motionX *= vertical.getValue().intValue() / 100;
                            } else {
                                if (airhorizontal.getValue() == 0.0d && airvertical.getValue() == 0.0d) {
                                    e.cancel();
                                    return;
                                }
                                s27.motionX *= (int) airhorizontal.getValue().intValue() / 100;
                                s27.motionZ *= (int) airhorizontal.getValue().intValue() / 100;
                                s27.motionX *= (int) airvertical.getValue().intValue() / 100;
                            }
                        }
                        break;
                    }
                }
            }
        }

    }

    @Override
    public void onPacketSendEvent(PacketSendEvent e) {
        if (this.mc.thePlayer == null) {
            return;
        }
        Packet packet = e.getPacket();
        if (mode.is("Grim") && ViaLoadingBase.getInstance().getTargetVersion().getVersion() > 47 && !Client.INSTANCE.getModuleCollection().getModule(Disabler.class).isEnabled() && packet instanceof C0BPacketEntityAction && velocityInput) {
            if (((C0BPacketEntityAction) packet).getAction() == C0BPacketEntityAction.Action.START_SPRINTING) {
                if (this.lastSprinting) {
                    e.setCancelled(true);
                }
                this.lastSprinting = true;
            } else if (((C0BPacketEntityAction) packet).getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                if (!this.lastSprinting) {
                    e.setCancelled(true);
                }
                this.lastSprinting = false;
            }
        }
    }

    private double getMotion() {
        return 0.07776;
    }
}