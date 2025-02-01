package dev.onlooker.module.impl.movement;

import dev.onlooker.event.impl.player.UpdateEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.utils.server.PacketUtils;
import dev.onlooker.utils.server.ServerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoLiquid extends Module {

    public NoLiquid() {
        super("NoLiquid", Category.MOVEMENT, "CNMCNMCNM");
    }

    @Override
    public void onUpdateEvent(UpdateEvent event) {
        setSuffix("Grim");
        final BlockPos playerPos = new BlockPos(mc.thePlayer);
        BlockPos[] blockPoses = new BlockPos[4];

        for (int i = -3; i < 3; i++) {
            for (int i2 = -12; i2 < 12; i2++) {
                blockPoses[0] = playerPos.add(i2, i, 7);
                blockPoses[1] = playerPos.add(i2, i, -7);
                blockPoses[2] = playerPos.add(7, i, i2);
                blockPoses[3] = playerPos.add(-7, i, i2);

                for (BlockPos blockPos : blockPoses) {
                    IBlockState blockState = mc.theWorld.getBlockState(blockPos);
                    Block block = blockState.getBlock();

                    if (block instanceof BlockLiquid && !ServerUtils.isOnLoyisa()) {
                        PacketUtils.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                        PacketUtils.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                        mc.theWorld.setBlockToAir(blockPos);
                    }
                }
            }
        }
    }
}
