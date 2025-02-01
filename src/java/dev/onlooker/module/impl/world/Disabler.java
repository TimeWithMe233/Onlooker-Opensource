package dev.onlooker.module.impl.world;

import com.viaversion.viaversion.util.MathUtil;
import dev.onlooker.Client;
import dev.onlooker.event.impl.game.WorldEvent;
import dev.onlooker.event.impl.network.PacketReceiveEvent;
import dev.onlooker.event.impl.network.PacketSendEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.impl.player.Blink;
import dev.onlooker.module.impl.player.TimerBalance;
import dev.onlooker.utils.server.PacketUtils;
import dev.onlooker.utils.addons.viamcp.viamcp.ViaMCP;
import lombok.Getter;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Disabler extends Module {

    @Getter
    public CopyOnWriteArrayList<Packet<INetHandler>> storedPackets = new CopyOnWriteArrayList<>();
    @Getter
    public ConcurrentLinkedDeque<Integer> pingPackets = new ConcurrentLinkedDeque<>();

    private static boolean lastResult;

    public Disabler() {
        super("Disabler", Category.WORLD, "Disables some anticheats");
    }

    private int slot;

    @Override
    public void onWorldEvent(WorldEvent event) {
        slot = -1;
    }

    @Override
    public void onEnable() {
        setSuffix("Grim");
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent event) {
        if (isNull()) return;
        final Packet<?> packet = event.getPacket();
        expandPacket(event, packet);
        if (packet instanceof C07PacketPlayerDigging) {
            if (((C07PacketPlayerDigging) packet).getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM && ViaMCP.INSTANCE.getAsyncVersionSlider().id >= 47) {
                if (((C07PacketPlayerDigging) packet).getFacing() != EnumFacing.DOWN
                        || ((C07PacketPlayerDigging) packet).getPosition().getX() != 0
                        || ((C07PacketPlayerDigging) packet).getPosition().getY() != 0
                        || ((C07PacketPlayerDigging) packet).getPosition().getZ() != 0) {
                    ((C07PacketPlayerDigging) packet).setFacing(EnumFacing.DOWN);
                    ((C07PacketPlayerDigging) packet).setPosition(BlockPos.ORIGIN);
                }
            }
        }
        if (packet instanceof C03PacketPlayer.C06PacketPlayerPosLook ) {
            if (((C03PacketPlayer)packet).getPitch() > 90 || ((C03PacketPlayer)packet).getPitch() < -90) {
                ((C03PacketPlayer)packet).setPitch((float) MathUtil.clamp((int) ((C03PacketPlayer)packet).getPitch(), -90, 90));
            }
        }
        handlePacketHeldItemChange(event);
    }

    private void expandPacket(PacketSendEvent event, Packet<?> packet) {
        if (packet instanceof C08PacketPlayerBlockPlacement && ((C08PacketPlayerBlockPlacement) packet).getPlacedBlockDirection() >= 0 && ((C08PacketPlayerBlockPlacement) packet).getPlacedBlockDirection() <= 5) {
            event.setCancelled(true);
            expandAndSendBlockPlacementPacket((C08PacketPlayerBlockPlacement) packet);
        }
        if (packet instanceof C13PacketPlayerAbilities) {
            handleFlyingAbilities((C13PacketPlayerAbilities) packet);
        }
    }

    private void expandAndSendBlockPlacementPacket(C08PacketPlayerBlockPlacement packet) {
        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(
                packet.getPosition(),
                6 + packet.getPlacedBlockDirection() * 7,
                packet.getStack(),
                packet.getPlacedBlockOffsetX(),
                packet.getPlacedBlockOffsetY(),
                packet.getPlacedBlockOffsetZ()
        ));
    }

    private void handleFlyingAbilities(C13PacketPlayerAbilities packet) {
        if (packet.isFlying() && !mc.thePlayer.capabilities.isFlying) {
            packet.setFlying(false);
        }
    }

    private void handlePacketHeldItemChange(PacketSendEvent event) {
        if (event.getPacket() instanceof C09PacketHeldItemChange) {
            int slotId = ((C09PacketHeldItemChange) event.getPacket()).getSlotId();
            handleSlotIdChange(event, slotId);
        }
    }

    private void handleSlotIdChange(PacketSendEvent event, int slotId) {
        if (slot == slotId) {
            event.setCancelled(true);
        } else {
            slot = slotId;
        }
    }


    public boolean getGrimPost() {
        boolean result = mc.thePlayer != null && mc.thePlayer.isEntityAlive() && mc.thePlayer.ticksExisted >= 10 && !(mc.currentScreen instanceof GuiDownloadTerrain);

        if(mc.thePlayer != null && mc.thePlayer.getHeldItem() != null && (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood || (mc.thePlayer.getHeldItem().getItem() instanceof ItemPotion && !ItemPotion.isSplash(mc.thePlayer.getHeldItem().getMetadata())))){
            result = false;
        }

        if (lastResult && !result) {
            lastResult = false;
            mc.addScheduledTask(this::processPackets);
        }
        lastResult = result;
        return result;
    }

    public boolean grimPostDelay(Packet<?> packet) {
        if (mc.thePlayer == null || mc.currentScreen instanceof GuiDownloadTerrain || packet instanceof S00PacketServerInfo || packet instanceof S01PacketEncryptionRequest || packet instanceof S38PacketPlayerListItem || packet instanceof S00PacketDisconnect || packet instanceof S21PacketChunkData || packet instanceof S01PacketPong || packet instanceof S44PacketWorldBorder || packet instanceof S01PacketJoinGame || packet instanceof S19PacketEntityHeadLook || packet instanceof S3EPacketTeams || packet instanceof S02PacketChat || packet instanceof S2FPacketSetSlot || packet instanceof S1CPacketEntityMetadata || packet instanceof S20PacketEntityProperties || packet instanceof S35PacketUpdateTileEntity || packet instanceof S03PacketTimeUpdate || packet instanceof S47PacketPlayerListHeaderFooter) {
            return false;
        }
        if (packet instanceof S12PacketEntityVelocity) {
            return ((S12PacketEntityVelocity) packet).getEntityID() == mc.thePlayer.getEntityId();
        } else if (packet instanceof S27PacketExplosion || packet instanceof S13PacketDestroyEntities || packet instanceof S32PacketConfirmTransaction || packet instanceof S08PacketPlayerPosLook || packet instanceof S18PacketEntityTeleport || packet instanceof S19PacketEntityStatus || packet instanceof S04PacketEntityEquipment || packet instanceof S23PacketBlockChange || packet instanceof S22PacketMultiBlockChange || packet instanceof S00PacketKeepAlive || packet instanceof S06PacketUpdateHealth || packet instanceof S14PacketEntity || packet instanceof S0FPacketSpawnMob || packet instanceof S2DPacketOpenWindow || packet instanceof S30PacketWindowItems || packet instanceof S3FPacketCustomPayload) {
            return true;
        } else {
            return packet instanceof S2EPacketCloseWindow;
        }
    }

    public void processPackets() {
        if (!storedPackets.isEmpty()) {
            for (Packet<INetHandler> packet : storedPackets) {
                PacketReceiveEvent event = new PacketReceiveEvent(packet);

                Client.INSTANCE.getEventProtocol().handleEvent(event);

                if (!event.isCancelled()) {
                    packet.processPacket(mc.getNetHandler());
                }
            }
            storedPackets.clear();
        }
    }

    public void fixC0F(C0FPacketConfirmTransaction packet) {
        int id = packet.getUid();
        if (id >= 0 || pingPackets.isEmpty()) {
            if (Client.INSTANCE.getModuleCollection().get(TimerBalance.class).isEnabled()) {
                mc.getNetHandler().addToSendQueue(packet);
            } else {
                PacketUtils.sendNoEvent(packet);
            }
            return;
        }
        do {
            int current = pingPackets.getFirst();
            PacketUtils.sendNoEvent(new C0FPacketConfirmTransaction(packet.getWindowId(), (short) current, true));
            pingPackets.pollFirst();
            if (current == id) {
                return;
            }
        } while (!pingPackets.isEmpty());
    }
}


