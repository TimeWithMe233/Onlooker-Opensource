package dev.onlooker.module.impl.player;

import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viarewind.utils.PacketUtil;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import dev.onlooker.event.impl.player.MotionEvent;
import dev.onlooker.event.impl.player.SlowDownEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.settings.impl.BooleanSetting;
import dev.onlooker.module.settings.impl.ModeSetting;
import dev.onlooker.utils.player.MoveUtil;
import dev.onlooker.utils.server.PacketUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.item.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoSlow extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "GrimAC", "Grim", "Hypixel");
    private final BooleanSetting food = new BooleanSetting("Food", true);
    private final BooleanSetting bow = new BooleanSetting("Bow", true);

    public boolean hasDroppedFood = false;
    public static boolean fix = false;

    public NoSlow() {
        super("NoSlow", Category.PLAYER, "prevent item slowdown");
        food.addParent(mode, modeSetting -> modeSetting.is("Grim"));
        bow.addParent(mode, modeSetting -> modeSetting.is("Grim"));
        this.addSettings(mode, food, bow);
    }

    public static boolean hasSwordwithout() {
        return  Minecraft.getMinecraft().thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    public void onSlowDownEvent(SlowDownEvent event) {
        if (this.mode.is("Grim")) {
            if (mc.thePlayer == null || mc.theWorld == null || mc.thePlayer.getHeldItem() == null) return;
            if (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood && food.getValue()) return;
            if (mc.thePlayer.getHeldItem() != null && (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword
                    || (mc.thePlayer.getHeldItem().getItem() instanceof ItemBow && bow.getValue())) && mc.thePlayer.isUsingItem())
                event.setCancelled(true);
            if (!mc.thePlayer.isSprinting() && !mc.thePlayer.isSneaking() && MoveUtil.isMoving()) {
                mc.thePlayer.setSprinting(true);
            }
        }
        if (this.mode.is("Hypixel")) {
            if (!(mc.thePlayer.getHeldItem().getItem() instanceof net.minecraft.item.ItemPotion))
                event.cancel();
            if (!mc.thePlayer.isSprinting() && !mc.thePlayer.isSneaking() && MoveUtil.isMoving()) {
                mc.thePlayer.setSprinting(true);
            }
        }
    }

    public void onMotionEvent(MotionEvent e) {
        setSuffix(mode.getMode());
        switch (this.mode.getMode()) {
            case "Hypixel":
                if (e.isPre()) {
                    if (mc.thePlayer.getHeldItem() == null) {
                        return;
                    } else if (mc.thePlayer.isUsingItem()) {
                        if (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood) {
                            PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), EnumFacing.UP.getIndex(), (ItemStack)null, 0.0F, 0.0F, 0.0F));

                        } else if (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                            PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                            PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));

                        }
                    }
                }
                break;
            case "Grim":
                if  (!mc.isSingleplayer()) {
                    if (e.isPre()) {
                        if (mc.thePlayer.onGround && mc.thePlayer.isInWeb) {
                            MoveUtil.strafe(0.29);
                        }
                        if (mc.thePlayer == null || mc.theWorld == null || mc.thePlayer.getHeldItem() == null) return;
                        ItemStack itemInHand = mc.thePlayer.getCurrentEquippedItem();
                        ItemStack itemStack = mc.thePlayer.getHeldItem();
                        int itemID = Item.getIdFromItem(itemInHand.getItem());
                        int itemMeta = itemInHand.getMetadata();
                        String itemId = itemInHand.getItem().getUnlocalizedName();
                        if(mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemFood && food.getValue()) {
                            if (mc.thePlayer.getHeldItem() != null && (!((itemID == 322 && itemMeta == 1) || itemId.equals("item.appleGoldEnchanted")))) {
                                if (Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem() != null) {

                                    if (Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBlock) {
                                        Minecraft.getMinecraft().rightClickDelayTimer = 4;
                                    } else {
                                        Minecraft.getMinecraft().rightClickDelayTimer = 4;
                                    }
                                }
                                if (mc.thePlayer.isUsingItem() && !hasDroppedFood  && itemStack.stackSize > 1) {
                                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN));
                                    hasDroppedFood = true;
                                    fix =true;
                                } else if (!mc.thePlayer.isUsingItem()) {
                                    hasDroppedFood = false;
                                    new Thread(() -> {try {Thread.sleep(500); fix =false;} catch (InterruptedException ex) {ex.printStackTrace();}}).start();

                                }
                            }
                        }else {
                            fix =false;
                        }
                        if (Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem() != null) {
                            if ((mc.thePlayer.isBlocking()) ||mc.thePlayer.isUsingItem() && hasSwordwithout()) {
                                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9));
                                mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("MadeByFire", new PacketBuffer(Unpooled.buffer())));
                                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                            }
                            if (mc.thePlayer.getHeldItem().getItem() instanceof ItemBow && mc.thePlayer.isUsingItem() && bow.getValue() && !mc.thePlayer.isSneaking()) {
                                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9));
                                mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("MadeByFire", new PacketBuffer(Unpooled.buffer())));
                                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                            }
                        }
                    }
                    if (e.isPost()) {
                        if (mc.thePlayer.getHeldItem() == null) return;
                        if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && mc.thePlayer.isUsingItem()) {
                            PacketUtils.sendC0F();
                            PacketWrapper useItem = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
                            useItem.write(Type.VAR_INT, 1);
                            PacketUtil.sendToServer(useItem, Protocol1_8To1_9.class, true, true);
                            PacketWrapper useItem2 = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
                            useItem2.write(Type.VAR_INT, 0);
                            PacketUtil.sendToServer(useItem2, Protocol1_8To1_9.class, true, true);
                        }
                        if (mc.thePlayer.getHeldItem().getItem() instanceof ItemBow && mc.thePlayer.isUsingItem() && bow.getValue()) {
                            PacketUtils.sendC0F();
                            PacketWrapper useItem = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
                            useItem.write(Type.VAR_INT, 1);
                            PacketUtil.sendToServer(useItem, Protocol1_8To1_9.class, true, true);
                            PacketWrapper useItem2 = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
                            useItem2.write(Type.VAR_INT, 0);
                            PacketUtil.sendToServer(useItem2, Protocol1_8To1_9.class, true, true);
                        }
                    }
                }
                break;
        }
    }
}
