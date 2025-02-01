package dev.onlooker.module.impl.world;

import dev.onlooker.Client;
import dev.onlooker.event.impl.game.WorldEvent;
import dev.onlooker.event.impl.player.UpdateEvent;
import dev.onlooker.event.impl.render.Render3DEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.impl.combat.KillAura;
import dev.onlooker.module.impl.player.Blink;
import dev.onlooker.module.impl.player.ChestStealer;
import dev.onlooker.module.impl.player.InvManager;
import dev.onlooker.module.impl.player.NoSlow;
import dev.onlooker.module.settings.impl.BooleanSetting;
import dev.onlooker.module.settings.impl.NumberSetting;
import dev.onlooker.utils.player.RayCastUtil;
import dev.onlooker.utils.player.RotationComponent;
import dev.onlooker.utils.player.RotationUtil;
import dev.onlooker.utils.player.RotationUtils;
import dev.onlooker.utils.render.RenderUtil;
import dev.onlooker.utils.vector.Vector2f;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ContainerAura extends Module {
    private final NumberSetting range = new NumberSetting("Range", 3.0, 7.0, 1.0, 0.1);
    private final BooleanSetting throughWalls = new BooleanSetting("ThroughWalls", true);
    private final List<BlockPos> openedContainers = new ArrayList<>();

    public ContainerAura() {
        super("ContainerAura", Category.WORLD, "CNM");
        addSettings(range, throughWalls);
    }

    @Override
    public void onDisable() {
        this.openedContainers.clear();
        super.onDisable();
    }

    @Override
    public void onUpdateEvent(UpdateEvent event) {
        if (KillAura.target != null || Client.INSTANCE.getModuleCollection().getModule(Scaffold.class).isEnabled() || Client.INSTANCE.getModuleCollection().getModule(NoSlow.class).hasDroppedFood || ChestStealer.opentime > 0 || Client.INSTANCE.getModuleCollection().getModule(InvManager.class).clientOpen || ContainerAura.mc.currentScreen != null || ContainerAura.mc.thePlayer.isUsingItem()) {
            //return;
        }
        List<TileEntity> containers = ContainerAura.mc.theWorld.loadedTileEntityList.stream().filter(e -> e instanceof IInventory && ContainerAura.mc.thePlayer.getDistanceSq(e.getPos()) <= (this.range.getValue() * this.range.getValue())).collect(Collectors.toList());
        for (TileEntity container : containers) {
            BlockPos containerPos = container.getPos();
            Block block = ContainerAura.mc.theWorld.getBlockState(containerPos).getBlock();
            if (this.openedContainers.contains(containerPos) || !(ContainerAura.mc.thePlayer.getDistance((double) containerPos.getX() + 0.5, (double) containerPos.getY() + 0.5, (double) containerPos.getZ() + 0.5) <= this.range.getValue()))
                continue;
            EnumFacing facing = RotationUtil.calculateFacing(ContainerAura.mc.thePlayer, containerPos);
            Vector2f rotations = RotationUtil.getRotations(containerPos, facing);
            this.openContainer(rotations, container, containerPos, facing);
        }
    }

    @Override
    public void onWorldEvent(WorldEvent event) {
        this.openedContainers.clear();
    }

    @Override
    public void onRender3DEvent(Render3DEvent event) {
        for (BlockPos pos : ContainerAura.mc.theWorld.loadedTileEntityList.stream().filter(e -> e instanceof IInventory).map(TileEntity::getPos).collect(Collectors.toList())) {
            Color color = this.openedContainers.contains(pos) ? new Color(255, 0, 0, 60) : new Color(0, 255, 0, 60);
            if (mc.thePlayer.getDistance(pos) < 20.0) {
                RenderUtil.drawBlockBox(pos, color, false);
            }
        }
    }
    private void openContainer(Vector2f rotation, Object container, BlockPos pos, EnumFacing side) {
        if (!RayCastUtil.overBlock(rotation,side, pos, true) && !throughWalls.getValue()) {
            return;
        }
        RotationComponent.setRotations(rotation, 180, true);
        if (container instanceof TileEntity) {
            mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), pos, side, new Vec3((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()));
        } else if (container instanceof EntityMinecartContainer) {
            EntityMinecartContainer minecartContainer = (EntityMinecartContainer)container;
            mc.playerController.interactWithEntitySendPacket(mc.thePlayer, minecartContainer);
        }this.openedContainers.add(pos);
    }
}
