package dev.onlooker.utils.player;

import dev.onlooker.event.ListenerAdapter;
import lombok.Getter;
import net.minecraft.item.ItemStack;

import static dev.onlooker.gui.mainmenu.altmanager.helpers.Alt.mc;

public class RenderSlotComponent extends ListenerAdapter {

    private static int spoofedSlot;

    @Getter
    private static boolean spoofing;

    public void startSpoofing(int slot) {
        this.spoofing = true;
        this.spoofedSlot = slot;
    }

    public void stopSpoofing() {
        this.spoofing = false;
    }

    public static int getSpoofedSlot() {
        return spoofing ? spoofedSlot : mc.thePlayer.inventory.currentItem;
    }

    public static ItemStack getSpoofedStack() {
        return spoofing ? mc.thePlayer.inventory.getStackInSlot(spoofedSlot) : mc.thePlayer.inventory.getCurrentItem();
    }

}