package dev.onlooker.module.impl.world;

import dev.onlooker.event.impl.player.MotionEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.settings.impl.ModeSetting;
import dev.onlooker.utils.player.SlotComponent;
import dev.onlooker.utils.player.SlotUtil;
import net.minecraft.init.Items;

public class KeyPearl extends Module {
    private final ModeSetting mode = new ModeSetting("Modes","Middle","Middle","Key");
    private boolean flag;
    public KeyPearl() {
        super("KeyPearl", Category.WORLD, "quickly throw pearls");
        addSettings(mode);
    }

    @Override
    public void onEnable() {
        if (mode.getMode().equalsIgnoreCase("Key")) {
            throwPearl();
            this.setEnabled(false);
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        flag = false;
        super.onDisable();
    }
    @Override
    public void onMotionEvent(MotionEvent event) {
        if (event.isPre()) {
            if (isNull() || mode.getMode().equalsIgnoreCase("Key")) return;
            if (mc.gameSettings.keyBindPickBlock.isKeyDown()) {
                flag = true;
            }
            if (flag && !mc.gameSettings.keyBindPickBlock.isKeyDown()) {
                throwPearl();
                flag = false;
            }
        }
    }
    public void throwPearl() {
        SlotComponent.setSlot(SlotUtil.findItem(Items.ender_pearl), false);
        mc.rightClickMouse();
    }
}
