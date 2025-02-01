package dev.onlooker.module.impl.player;

import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;

import java.util.LinkedList;

public class FakePlayer extends Module {
    private EntityOtherPlayerMP fakePlayer = null;
    private final LinkedList<double[]> positions = new LinkedList<>();
    public FakePlayer() {
        super("FakePlayer", Category.PLAYER, "switches to the best tool");
    }

    @Override
    public void onEnable() {
        if (FakePlayer.mc.thePlayer == null) {
            return;
        }
        (this.fakePlayer = new EntityOtherPlayerMP(FakePlayer.mc.theWorld, FakePlayer.mc.thePlayer.getGameProfile())).clonePlayer(FakePlayer.mc.thePlayer, true);
        this.fakePlayer.copyLocationAndAnglesFrom(FakePlayer.mc.thePlayer);
        this.fakePlayer.rotationYawHead = FakePlayer.mc.thePlayer.rotationYawHead;
        FakePlayer.mc.theWorld.addEntityToWorld(-1337, this.fakePlayer);
        synchronized (this.positions) {
            this.positions.add(new double[] { FakePlayer.mc.thePlayer.posX, FakePlayer.mc.thePlayer.getEntityBoundingBox().minY + FakePlayer.mc.thePlayer.getEyeHeight() / 2.0f, FakePlayer.mc.thePlayer.posZ });
            this.positions.add(new double[] { FakePlayer.mc.thePlayer.posX, FakePlayer.mc.thePlayer.getEntityBoundingBox().minY, FakePlayer.mc.thePlayer.posZ });
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (FakePlayer.mc.thePlayer == null) {
            return;
        }
        if (this.fakePlayer != null) {
            FakePlayer.mc.theWorld.removeEntityFromWorld(this.fakePlayer.getEntityId());
            this.fakePlayer = null;
        }
        super.onDisable();
    }
}