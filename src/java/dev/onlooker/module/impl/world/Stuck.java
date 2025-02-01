package dev.onlooker.module.impl.world;

import dev.onlooker.event.impl.network.PacketReceiveEvent;
import dev.onlooker.event.impl.network.PacketSendEvent;
import dev.onlooker.event.impl.player.UpdateEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.utils.server.PacketUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import javax.vecmath.Tuple2f;
import javax.vecmath.Vector2f;

public class Stuck extends Module {
    private double x;
    private double y;
    private double z;
    private boolean onGround;
    private Vector2f rotation;
    public Stuck() {
        super("Stuck", Category.WORLD, "Stops you from getting stuck in blocks");
    }

    @Override
    public void onEnable() {
        this.onGround = mc.thePlayer.onGround;
        this.x = Stuck.mc.thePlayer.posX;
        this.y = Stuck.mc.thePlayer.posY;
        this.z = Stuck.mc.thePlayer.posZ;
        this.rotation = new Vector2f(Stuck.mc.thePlayer.rotationYaw, Stuck.mc.thePlayer.rotationPitch);
        final float f = Stuck.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        final float gcd = f * f * f * 1.2f;
        final Vector2f rotation = this.rotation;
        rotation.x -= this.rotation.x % gcd;
        final Vector2f rotation2 = this.rotation;
        rotation2.y -= this.rotation.y % gcd;
        super.onEnable();
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent event) {
        if (event.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            final Vector2f current = new Vector2f(Stuck.mc.thePlayer.rotationYaw, Stuck.mc.thePlayer.rotationPitch);
            final float f = Stuck.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
            final float gcd = f * f * f * 1.2f;
            current.x -= current.x % gcd;
            current.y -= current.y % gcd;
            if (this.rotation.equals(current)) {
                return;
            }
            this.rotation = current;
            event.setCancelled(true);
            PacketUtils.sendNoEvent(new C03PacketPlayer.C05PacketPlayerLook(current.x, current.y, this.onGround));
            PacketUtils.sendNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
        }
        if (event.getPacket() instanceof C03PacketPlayer) {
            event.setCancelled(true);
        }
    }
    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            toggleSilent();
        }
    }
    @Override
    public void onUpdateEvent(UpdateEvent event) {
        Stuck.mc.thePlayer.motionX = 0.0;
        Stuck.mc.thePlayer.motionY = 0.0;
        Stuck.mc.thePlayer.motionZ = 0.0;
        Stuck.mc.thePlayer.setPosition(this.x, this.y, this.z);
    }
    public void throwPearl(final Vector2f current) {
        if (!this.isEnabled()) {
            return;
        }
        Stuck.mc.thePlayer.rotationYaw = current.x;
        Stuck.mc.thePlayer.rotationPitch = current.y;
        final float f = Stuck.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        final float gcd = f * f * f * 1.2f;
        current.x -= current.x % gcd;
        current.y -= current.y % gcd;
        if (!rotation.equals((Tuple2f)current)) {
            PacketUtils.sendPacketNoEvent((Packet<?>)new C03PacketPlayer.C05PacketPlayerLook(current.x, current.y,onGround));
        }
        rotation = current;
        PacketUtils.sendPacketNoEvent((Packet<?>)new C08PacketPlayerBlockPlacement(Stuck.mc.thePlayer.getHeldItem()));
    }
}
