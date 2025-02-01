package dev.onlooker.module.impl.combat;

import dev.onlooker.event.impl.game.WorldEvent;
import dev.onlooker.event.impl.network.PacketEvent;
import dev.onlooker.event.impl.player.EventAttack;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.settings.impl.BooleanSetting;
import dev.onlooker.utils.player.MoveUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public final class SuperKnockback extends Module {
    private final BooleanSetting onlymove = new BooleanSetting("OnlyMove",false);
    private final BooleanSetting onlyground = new BooleanSetting("OnlyGround", false);
    public final BooleanSetting bf = new BooleanSetting("Bypass BadPacketsF",true);
    boolean lastSprinting;

    public SuperKnockback() {
        super("SuperKnockback", Category.COMBAT, "Makes the player your attacking take extra knockback");
        addSettings(onlyground, onlymove, bf);
    }

    @Override
    public void onWorldEvent(WorldEvent event) {
        this.lastSprinting = false;
    }

    @Override
    public void onPacketEvent(PacketEvent event) {
        if (isNull()) return;
        final Packet<?> packet = event.getPacket();
        if (bf.getValue() && packet instanceof C0BPacketEntityAction) {
            if (((C0BPacketEntityAction)packet).getAction() == C0BPacketEntityAction.Action.START_SPRINTING) {
                if (this.lastSprinting) {
                    event.setCancelled(true);
                }
                this.lastSprinting = true;
            }
            else if (((C0BPacketEntityAction)packet).getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                if (!this.lastSprinting) {
                    event.setCancelled(true);
                }
                this.lastSprinting = false;
            }
        }
    }

    @Override
    public void onAttackEvent(EventAttack event) {
        if ((!MoveUtil.isMoving() && this.onlymove.getValue()) || (!SuperKnockback.mc.thePlayer.onGround && this.onlyground.getValue())) {
            return;
        }
        if (SuperKnockback.mc.thePlayer.isSprinting()) {
            SuperKnockback.mc.thePlayer.setSprinting(true);
        }
        SuperKnockback.mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(SuperKnockback.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
        SuperKnockback.mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(SuperKnockback.mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
        SuperKnockback.mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(SuperKnockback.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
        SuperKnockback.mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(SuperKnockback.mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
        SuperKnockback.mc.thePlayer.setSprinting(true);
        EntityPlayerSP.serverSprintState = true;
    };

}
