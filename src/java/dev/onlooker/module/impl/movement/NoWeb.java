package dev.onlooker.module.impl.movement;

import dev.onlooker.event.impl.player.MotionEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.impl.combat.KillAura;
import dev.onlooker.utils.player.BlockUtils;
import dev.onlooker.utils.server.PacketUtils;
import dev.onlooker.utils.server.ServerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWeb;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.Map;

public class NoWeb extends Module {
    public NoWeb() {
        super("NoWeb", Category.MOVEMENT, "CNMCNMCNM");
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        setSuffix("Grim");
        if (KillAura.target != null) return;
        if (event.isPost()) return;
        if (!NoWeb.mc.thePlayer.isInWeb) return;
        Map<BlockPos, Block> searchBlock = BlockUtils.searchBlocks(2);
        for (Map.Entry<BlockPos, Block> block : searchBlock.entrySet()) {
            if (ServerUtils.isOnLoyisa()) return;
            if (!(NoWeb.mc.theWorld.getBlockState(block.getKey()).getBlock() instanceof BlockWeb)) continue;
            PacketUtils.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, block.getKey(), EnumFacing.DOWN));
            PacketUtils.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, block.getKey(), EnumFacing.DOWN));
        }
        NoWeb.mc.thePlayer.isInWeb = false;
    }
}
