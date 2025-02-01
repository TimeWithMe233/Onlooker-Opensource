package dev.onlooker.module.impl.combat;

import dev.onlooker.Client;
import dev.onlooker.event.impl.player.MotionEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.impl.world.Scaffold;
import dev.onlooker.module.settings.impl.BooleanSetting;
import dev.onlooker.module.settings.impl.NumberSetting;
import dev.onlooker.utils.player.RotationComponent;
import dev.onlooker.utils.player.RotationUtils;
import dev.onlooker.utils.time.TimerUtil;
import dev.onlooker.utils.vector.Vector2f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ThrowableAura extends Module {
    private final NumberSetting fov = new NumberSetting("Fov",150.0,180.0,90.0,0.1);
    private final NumberSetting range = new NumberSetting("Range",5.0,10.0,3.0,0.1);
    private final NumberSetting delay = new NumberSetting("Delay",20.0,1000.0,0.0,1);
    private final BooleanSetting fishRod = new BooleanSetting("Fish_Rod",false);
    private final NumberSetting backdelay = new NumberSetting("BackDelay",20.0,1000.0,0.0,1);
    private final TimerUtil timer = new TimerUtil();
    private final TimerUtil timer2 = new TimerUtil();
    public static final List<EntityPlayer> targets = new ArrayList<>();
    public static EntityPlayer target;
    public static int tick = 0;
    public static boolean isthrowout = false;

    public ThrowableAura() {
        super("ThrowableAura", Category.COMBAT, "ThrowableAura");
        addSettings(range, delay, fov, fishRod, backdelay);
    }

    @Override
    public void onMotionEvent(MotionEvent event){
        for (Entity entity : mc.theWorld.getLoadedEntityList()) {
            if (entity instanceof EntityFishHook) {
                if (isthrowout) {
                    if ((((EntityFishHook) entity).caughtEntity != null && ((EntityFishHook) entity).caughtEntity == target) || entity.onGround) {
                        isthrowout = false;
                        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    }
                }
            }
        }
        if (isthrowout) {
            if (timer2.hasTimeElapsed(backdelay.getValue()) || KillAura.target != null || Client.INSTANCE.getModuleCollection().getModule(Scaffold.class).isEnabled()) {
                isthrowout = false;
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }
        }

        if(isthrowout || (findBall() == -1 && findfishRod() == -1) || !timer.hasTimeElapsed(delay.getValue()) || KillAura.target != null || Client.INSTANCE.getModuleCollection().getModule(Scaffold.class).isEnabled()) return;

        for (Entity entity : mc.theWorld.getLoadedEntityList()) {
            if (entity instanceof EntityPlayer) {
                if (mc.thePlayer.getDistanceToEntity(entity) <= range.getValue() && mc.thePlayer != entity) {
                    targets.add((EntityPlayer) entity);
                }
            }
        }

        targets.sort(Comparator.comparingDouble(mc.thePlayer::getDistanceToEntity));
        if (!targets.isEmpty()) {
            target = targets.get(0);
        } else {
            target = null;
        }
        float[] rotation = RotationUtils.getThrowRotation(target,range.getValue().floatValue());

        if(target != null && mc.thePlayer.getDistanceToEntity(target) <= range.getValue() && RotationUtils.getRotationDifference(target) <= fov.getValue() && mc.thePlayer.canEntityBeSeen(target)){
            tick += 1;
            assert rotation != null;
            RotationComponent.setRotations(new Vector2f(rotation[0],rotation[1]),10, true);
            if(tick > 3) {
                if(findBall() != -1 && mc.thePlayer.inventory.currentItem != findBall() - 36) {
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(findBall() - 36));
                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getStackInSlot(findBall() - 36)));
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                }else{
                    if(findfishRod() != -1 && !isthrowout && fishRod.isEnabled() && mc.thePlayer.inventory.currentItem != findfishRod()){
                        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(findfishRod() - 36));
                        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getStackInSlot(findfishRod() - 36)));
                        isthrowout = true;
                        timer2.reset();
                    }
                }
                target = null;
                targets.clear();
                timer.reset();
                tick = 0;
            }
        }else{
            tick = 0;
        }
    }
    public static int findfishRod() {
        for (int i = 36; i < 45; ++i) {
            final ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (itemStack != null && (itemStack.getItem().equals(Items.fishing_rod))){
                return i;
            }
        }
        return -1;
    }
    public static int findBall() {
        for (int i = 36; i < 45; ++i) {
            final ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (itemStack != null && (itemStack.getItem().equals(Items.snowball) || itemStack.getItem().equals(Items.egg)) && itemStack.stackSize > 0) {
                return i;
            }
        }
        return -1;
    }
}
