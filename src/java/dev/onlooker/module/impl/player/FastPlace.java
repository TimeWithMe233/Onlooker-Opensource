package dev.onlooker.module.impl.player;

import dev.onlooker.event.impl.player.MotionEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.settings.impl.BooleanSetting;
import dev.onlooker.module.settings.impl.NumberSetting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemSnowball;

public final class FastPlace extends Module {

    private final NumberSetting ticks = new NumberSetting("Ticks", 0, 4, 0, 1);
    private final BooleanSetting blocks = new BooleanSetting("Blocks", true);
    private final BooleanSetting projectiles = new BooleanSetting("Projectiles", true);

    @Override
    public void onMotionEvent(MotionEvent event) {
        if (canFastPlace()) {
            mc.rightClickDelayTimer = Math.min(mc.rightClickDelayTimer, ticks.getValue().intValue());
        }
    }

    @Override
    public void onDisable() {
        mc.rightClickDelayTimer = 4;
        super.onDisable();
    }

    private boolean canFastPlace() {
        if (mc.thePlayer == null || mc.thePlayer.getCurrentEquippedItem() == null || mc.thePlayer.getCurrentEquippedItem().getItem() == null)
            return false;
        Item heldItem = mc.thePlayer.getCurrentEquippedItem().getItem();
        return (blocks.isEnabled() && heldItem instanceof ItemBlock) || (projectiles.isEnabled() && (heldItem instanceof ItemSnowball || heldItem instanceof ItemEgg));
    }

    public FastPlace() {
        super("FastPlace", Category.PLAYER, "place blocks fast");
        this.addSettings(ticks, blocks, projectiles);
    }

}
