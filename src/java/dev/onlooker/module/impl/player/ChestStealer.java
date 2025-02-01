package dev.onlooker.module.impl.player;

import dev.onlooker.event.impl.player.UpdateEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.settings.impl.BooleanSetting;
import dev.onlooker.module.settings.impl.NumberSetting;
import dev.onlooker.utils.player.ItemComponent;
import dev.onlooker.utils.player.WeaponDetection;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;

import java.util.Random;

public class ChestStealer extends Module {
    private final BooleanSetting chest = new BooleanSetting("Chest", true);
    private final BooleanSetting furnace = new BooleanSetting("Furnace", true);
    private final BooleanSetting brewingStand = new BooleanSetting("BrewingStand", true);

    private final NumberSetting stealdelay = new NumberSetting("StealDelay", 0.0, 1000.0, 0.0, 10.0);
    private final NumberSetting delay = new NumberSetting("CloseDelay", 0.0, 1000.0, 0.0, 10.0);
    private final BooleanSetting trash = new BooleanSetting("PickTrash", true);
    public static BooleanSetting freeLook = new BooleanSetting("Free Look", true);

    public static int opentime = 0;

    public static int stealDelay = 0;
    public static boolean isChest = false;


    public ChestStealer() {
        super("ChestStealer", Category.PLAYER,"sb");
        addSettings(chest,furnace,brewingStand,stealdelay,delay,trash,freeLook);
    }

    @Override
    public void onUpdateEvent(UpdateEvent event) {
        setSuffix(delay.getValue().toString());
        int i;
        Container container;
        if (WeaponDetection.isInLobby()) {
            return;
        }
        if (ChestStealer.mc.thePlayer.openContainer == null) {
            return;
        }
        opentime ++;
        stealDelay ++;

        if(stealDelay < Math.floor(stealdelay.getValue()/50)) return;

        if (ChestStealer.mc.thePlayer.openContainer instanceof ContainerFurnace && this.furnace.isEnabled()) {
            container = ChestStealer.mc.thePlayer.openContainer;
            if (this.isFurnaceEmpty((ContainerFurnace)container) && opentime > Math.floor(delay.getValue()/50)) {
                ChestStealer.mc.thePlayer.closeScreen();
                opentime = 0;
                return;
            }
            for (i = 0; i < ((ContainerFurnace)container).tileFurnace.getSizeInventory(); ++i) {
                if (((ContainerFurnace)container).tileFurnace.getStackInSlot(i) == null) continue;
                if (new Random().nextInt(100) > 80) continue;
                ChestStealer.mc.playerController.windowClick(container.windowId, i, 0, 1, ChestStealer.mc.thePlayer);
                mc.getNetHandler().addToSendQueue(new C0FPacketConfirmTransaction(container.windowId, (short) 1, true));
                stealDelay = 0;
            }
        }
        if (ChestStealer.mc.thePlayer.openContainer instanceof ContainerBrewingStand && this.brewingStand.isEnabled()) {
            container = ChestStealer.mc.thePlayer.openContainer;
            if (this.isBrewingStandEmpty((ContainerBrewingStand)container)&& opentime > Math.floor(delay.getValue()/50)) {
                ChestStealer.mc.thePlayer.closeScreen();
                opentime = 0;
                return;
            }
            for (i = 0; i < ((ContainerBrewingStand)container).tileBrewingStand.getSizeInventory(); ++i) {
                if (((ContainerBrewingStand) container).tileBrewingStand.getStackInSlot(i) == null) continue;
                if (new Random().nextInt(100) > 80) continue;
                ChestStealer.mc.playerController.windowClick(container.windowId, i, 0, 1, ChestStealer.mc.thePlayer);
                mc.getNetHandler().addToSendQueue(new C0FPacketConfirmTransaction(container.windowId, (short) 1, true));
                stealDelay = 0;
            }
        }
        if (ChestStealer.mc.thePlayer.openContainer instanceof ContainerChest && this.chest.isEnabled() && isChest) {
            container = ChestStealer.mc.thePlayer.openContainer;
            if (this.isChestEmpty((ContainerChest)container) && opentime > Math.floor(delay.getValue()/50)) {
                ChestStealer.mc.thePlayer.closeScreen();
                opentime = 0;
                return;
            }
            for (i = 0; i < ((ContainerChest)container).getLowerChestInventory().getSizeInventory(); ++i) {
                if (((ContainerChest)container).getLowerChestInventory().getStackInSlot(i) == null || this.isItemUseful((ContainerChest) container, i) && !this.trash.isEnabled()) continue;
                if (new Random().nextInt(100) > 80) continue;
                ChestStealer.mc.playerController.windowClick(container.windowId, i, 0, 1, ChestStealer.mc.thePlayer);
                mc.getNetHandler().addToSendQueue(new C0FPacketConfirmTransaction(container.windowId, (short) 1, true));
                stealDelay = 0;
            }
        }
    }

    private boolean isChestEmpty(ContainerChest c) {
        for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
            if (c.getLowerChestInventory().getStackInSlot(i) == null || this.isItemUseful(c, i) && !this.trash.isEnabled()) continue;
            return false;
        }
        return true;
    }

    private boolean isFurnaceEmpty(ContainerFurnace c) {
        for (int i = 0; i < c.tileFurnace.getSizeInventory(); ++i) {
            if (c.tileFurnace.getStackInSlot(i) == null) continue;
            return false;
        }
        return true;
    }

    private boolean isBrewingStandEmpty(ContainerBrewingStand c) {
        for (int i = 0; i < c.tileBrewingStand.getSizeInventory(); ++i) {
            if (c.tileBrewingStand.getStackInSlot(i) == null) continue;
            return false;
        }
        return true;
    }

    private boolean isItemUseful(ContainerChest c, int i) {
        ItemStack itemStack = c.getLowerChestInventory().getStackInSlot(i);
        Item item = itemStack.getItem();
        if (item instanceof ItemAxe || item instanceof ItemPickaxe) {
            return false;
        }
        if (item instanceof ItemFood) {
            return false;
        }
        if (item instanceof ItemBow || item == Items.arrow) {
            return false;
        }
        if (item instanceof ItemPotion && !ItemComponent.isPotionNegative(itemStack)) {
            return false;
        }
        if (item instanceof ItemSword && ItemComponent.isBestSword(c, itemStack)) {
            return false;
        }
        if (item instanceof ItemArmor && ItemComponent.isBestArmor(c, itemStack)) {
            return false;
        }
        if (item instanceof ItemBlock) {
            return false;
        }
        return !(item instanceof ItemEnderPearl);
    }
}
