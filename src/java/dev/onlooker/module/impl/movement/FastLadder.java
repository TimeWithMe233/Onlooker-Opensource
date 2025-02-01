package dev.onlooker.module.impl.movement;

import dev.onlooker.event.impl.player.MotionEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.utils.player.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.util.BlockPos;

public class FastLadder extends Module {
    public FastLadder() {
        super("FastLadder", Category.MOVEMENT, "CNM");
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        if (event.isPost()) {
            return;
        }
        Block block = BlockUtils.getBlock(new BlockPos(FastLadder.mc.thePlayer.posX, FastLadder.mc.thePlayer.posY + 1.0, FastLadder.mc.thePlayer.posZ));
        if (block instanceof BlockLadder && FastLadder.mc.thePlayer.isCollidedHorizontally) {
            if (mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown()) {
                return;
            }
            FastLadder.mc.thePlayer.motionY = 0.1786;
            FastLadder.mc.thePlayer.motionX = 0.0;
            FastLadder.mc.thePlayer.motionZ = 0.0;
        }
    }
}
