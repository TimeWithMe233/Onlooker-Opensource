package dev.onlooker.module.impl.player;

import dev.onlooker.event.impl.network.PacketSendEvent;
import dev.onlooker.event.impl.player.BoundingBoxEvent;
import dev.onlooker.event.impl.player.PushOutOfBlockEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import net.minecraft.network.play.client.C03PacketPlayer;

public final class Freecam extends Module {

    @Override
    public void onBoundingBoxEvent(BoundingBoxEvent event) {
        if (mc.thePlayer != null) {
            event.cancel();
        }
    }

    @Override
    public void onPushOutOfBlockEvent(PushOutOfBlockEvent event) {
        if (mc.thePlayer != null) {
            event.cancel();
        }
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent event) {
        if (event.getPacket() instanceof C03PacketPlayer) {
            event.cancel();
        }
    }

    @Override
    public void onEnable() {
        if (mc.thePlayer != null) {
            mc.thePlayer.capabilities.allowFlying = true;
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer != null) {
            mc.thePlayer.capabilities.allowFlying = false;
            mc.thePlayer.capabilities.isFlying = false;
        }
        super.onDisable();
    }

    public Freecam() {
        super("Freecam", Category.PLAYER, "allows you to look around freely");
    }

}
