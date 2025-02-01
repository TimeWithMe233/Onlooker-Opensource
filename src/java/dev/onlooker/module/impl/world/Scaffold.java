package dev.onlooker.module.impl.world;

import dev.onlooker.event.impl.game.TickEvent;
import dev.onlooker.event.impl.player.BlockPlaceableEvent;
import dev.onlooker.event.impl.player.MotionEvent;
import dev.onlooker.event.impl.player.UpdateEvent;
import dev.onlooker.event.impl.render.Render3DEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.settings.impl.BooleanSetting;
import dev.onlooker.module.settings.impl.ModeSetting;
import dev.onlooker.utils.animations.Animation;
import dev.onlooker.utils.animations.Direction;
import dev.onlooker.utils.animations.impl.DecelerateAnimation;
import dev.onlooker.utils.misc.MathUtil;
import dev.onlooker.utils.player.*;
import dev.onlooker.utils.render.ColorUtil;
import dev.onlooker.utils.render.RenderUtil;
import dev.onlooker.utils.render.RoundedUtil;
import dev.onlooker.utils.vector.Rotation;
import dev.onlooker.utils.vector.Vector2f;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.gui.IFontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.*;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;
import java.util.List;


public class Scaffold extends Module {
    public static final List<Block> invalidBlocks = Arrays.asList(Blocks.enchanting_table, Blocks.furnace, Blocks.carpet, Blocks.crafting_table, Blocks.trapped_chest, Blocks.chest, Blocks.dispenser, Blocks.air, Blocks.water, Blocks.lava, Blocks.flowing_water, Blocks.flowing_lava, Blocks.snow_layer, Blocks.torch, Blocks.anvil, Blocks.jukebox, Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.noteblock, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.wooden_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_slab, Blocks.wooden_slab, Blocks.stone_slab2, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.yellow_flower, Blocks.red_flower, Blocks.anvil, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.cactus, Blocks.ladder, Blocks.web, Blocks.tnt);
    private static final BooleanSetting keepYValue = new BooleanSetting("Keep Y", false);
    public static Scaffold INSTANCE;
    public static double keepYCoord;
    public final BooleanSetting swing = new BooleanSetting("Swing", true);
    public final BooleanSetting sprintValue = new BooleanSetting("Sprint", false);
    public final BooleanSetting eagle = new BooleanSetting("Eagle", false);
    public final BooleanSetting telly = new BooleanSetting("Telly", true);
    public final BooleanSetting upValue = new BooleanSetting("Up", false);
    public final BooleanSetting esp = new BooleanSetting("ESP", true);
    private final ModeSetting countMode = new ModeSetting("Block Counter", "OnLooker", "None", "OnLooker", "Basic", "Polar");
    int idkTick = 0;
    @Getter
    private int slot;
    private BlockPos data;
    private boolean canTellyPlace;
    private int prevItem = 0;
    private EnumFacing enumFacing;
    private final Animation anim = new DecelerateAnimation(250, 1);

    public Scaffold() {
        super("Scaffold", Category.WORLD, "Rise Moment");
        addSettings(swing, sprintValue, eagle, telly, upValue, esp,countMode);
        upValue.addParent(telly, a -> telly.isEnabled() && !keepYValue.getValue());
    }

    public static double getYLevel() {
        if (!(Boolean) keepYValue.getValue()) {
            return Scaffold.mc.thePlayer.posY - 1.0;
        }
        return !MoveUtil.isMoving() ? Scaffold.mc.thePlayer.posY - 1.0 : keepYCoord;
    }

    public static Vec3 getVec3(BlockPos pos, EnumFacing face) {
        double x2 = (double) pos.getX() + 0.5;
        double y2 = (double) pos.getY() + 0.5;
        double z = (double) pos.getZ() + 0.5;
        if (face == EnumFacing.UP || face == EnumFacing.DOWN) {
            x2 += MathUtil.getRandomInRange(0.3, -0.3);
            z += MathUtil.getRandomInRange(0.3, -0.3);
        } else {
            y2 += 0.08;
        }
        if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
            z += MathUtil.getRandomInRange(0.3, -0.3);
        }
        if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
            x2 += MathUtil.getRandomInRange(0.3, -0.3);
        }
        return new Vec3(x2, y2, z);
    }

    @Override
    public void onEnable() {
        this.idkTick = 5;
        if (Scaffold.mc.thePlayer == null) {
            return;
        }
        this.prevItem = Scaffold.mc.thePlayer.inventory.currentItem;
        Scaffold.mc.thePlayer.setSprinting(this.sprintValue.getValue() || !this.canTellyPlace);
        Scaffold.mc.gameSettings.keyBindSprint.pressed = this.sprintValue.getValue() || !this.canTellyPlace;
        this.canTellyPlace = false;
        this.data = null;
        this.slot = -1;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (Scaffold.mc.thePlayer == null) {
            return;
        }
        KeyBinding.setKeyBindState(Scaffold.mc.gameSettings.keyBindSneak.getKeyCode(), false);
        Scaffold.mc.thePlayer.inventory.currentItem = this.prevItem;
        RenderSlotComponent renderSlotComponent = new RenderSlotComponent();
        renderSlotComponent.stopSpoofing();
        super.onDisable();
    }

    @Override
    public void onRender3DEvent(Render3DEvent event) {
        if (this.data != null) {
            for(int i = 0; i < 2; ++i) {
                BlockPos blockPos = this.data;
                PlaceInfo placeInfo = PlaceInfo.get(blockPos);
                if (BlockUtils.isValidBock(blockPos) && placeInfo != null && (Boolean)this.esp.getValue()) {
                    RenderUtil.drawBlockBox(blockPos, new Color(255, 0, 0, 70), false);
                    break;
                }
            }

        }
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        //挥手动画
        if (this.idkTick > 0) {
            --this.idkTick;
        }
        if (event.isPre()) {
            if (this.slot < 0) {
                return;
            }
            if (this.getBlockCount() <= 0) {
                int spoofSlot = this.getBestSpoofSlot();
                this.getBlock(spoofSlot);
            }
            if (this.slot < 0) {
                return;
            }
            Scaffold.mc.thePlayer.inventoryContainer.getSlot(this.slot + 36).getStack();
            if (eagle.getValue()) {
                if (PlayerUtil.getBlockUnderPlayer(Scaffold.mc.thePlayer) instanceof BlockAir) {
                    if (Scaffold.mc.thePlayer.onGround) {
                        KeyBinding.setKeyBindState(Scaffold.mc.gameSettings.keyBindSneak.getKeyCode(), true);
                    }
                } else if (Scaffold.mc.thePlayer.onGround) {
                    KeyBinding.setKeyBindState(Scaffold.mc.gameSettings.keyBindSneak.getKeyCode(), false);
                }
            }
            if (this.telly.getValue()) {
                if (this.canTellyPlace && !Scaffold.mc.thePlayer.onGround && MoveUtil.isMoving()) {
                    Scaffold.mc.thePlayer.setSprinting(false);
                }
                this.canTellyPlace = Scaffold.mc.thePlayer.offGroundTicks >= (this.upValue.getValue() ? (Scaffold.mc.thePlayer.ticksExisted % 16 == 0 ? 2 : 1) : 3);
            }
            if (!this.canTellyPlace) {
                return;
            }
            if (this.data != null) {
                float yaw = RotationUtil.getRotationBlock(this.data)[0];
                float pitch = RotationUtil.getRotationBlock(this.data)[1];
                RotationComponent.setRotations(new Vector2f(yaw, pitch), 1800.0f, true);
            }
        }
    }


    @Override
    public void onBlockPlaceable(BlockPlaceableEvent event) {
        if (mc.thePlayer == null) {
            return;
        }
        // Same Y
        if ((this.upValue.getValue() || keepYValue.getValue()) && Scaffold.mc.thePlayer.onGround && MoveUtil.isMoving() && !Scaffold.mc.gameSettings.keyBindJump.isKeyDown()) {
            Scaffold.mc.thePlayer.jump();
        }
        this.slot = this.getBlockSlot();
        if (this.slot < 0) {
            return;
        }
        if (!this.telly.getValue()) {
            Scaffold.mc.thePlayer.setSprinting(this.sprintValue.getValue());
            Scaffold.mc.gameSettings.keyBindSprint.pressed = false;
        }
        event.setCancelled(true);
        if (Scaffold.mc.thePlayer == null) {
            return;
        }
        this.place();
        mc.sendClickBlockToController(Scaffold.mc.currentScreen == null && Scaffold.mc.gameSettings.keyBindAttack.isKeyDown() && Scaffold.mc.inGameHasFocus);

    }

    @Override
    public void onTickEvent(TickEvent event) {
        if (Scaffold.mc.thePlayer == null) {
            return;
        }
        if (this.slot < 0) {
            return;
        }
        if (!((Boolean) this.telly.getValue())) {
            this.canTellyPlace = true;
        }
    }

    @Override
    public void onUpdateEvent(UpdateEvent event) {
        if (this.telly.getValue()) {
            if (Scaffold.mc.gameSettings.keyBindJump.pressed) {
                this.upValue.setState(true);
                keepYValue.setState(false);
            } else {
                this.upValue.setState(false);
                keepYValue.setState(true);
            }
        }
        if (Scaffold.mc.thePlayer.onGround) {
            keepYCoord = Math.floor(Scaffold.mc.thePlayer.posY - 1.0);
        }
        this.slot = this.getBlockSlot();
        if (this.slot < 0) {
            return;
        }
        Scaffold.mc.thePlayer.inventory.currentItem = this.slot;
        RenderSlotComponent renderSlotComponent = new RenderSlotComponent();
        renderSlotComponent.startSpoofing(this.prevItem);
        this.findBlock();
    }

    private void place() {
        if (!this.canTellyPlace) {
            return;
        }
        this.slot = this.getBlockSlot();
        if (this.slot < 0) {
            return;
        }
        if (this.data != null) {
            EnumFacing enumFacing;
            enumFacing = keepYValue.getValue() ? this.enumFacing : this.getPlaceSide(this.data);
            if (enumFacing == null) {
                return;
            }
            if (Scaffold.mc.playerController.onPlayerRightClick(Scaffold.mc.thePlayer, Scaffold.mc.theWorld, Scaffold.mc.thePlayer.getCurrentEquippedItem(), this.data, enumFacing, Scaffold.getVec3(this.data, enumFacing))) {
                if (this.swing.getValue()) {
                    Scaffold.mc.thePlayer.swingItem();
                } else {
                    Scaffold.mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                }
            }
        }
    }

    private void findBlock() {
        if (MoveUtil.isMoving() && keepYValue.getValue()) {
            boolean shouldGoDown = false;
            BlockPos blockPosition = new BlockPos(Scaffold.mc.thePlayer.posX, Scaffold.getYLevel(), Scaffold.mc.thePlayer.posZ);
            if (BlockUtils.isValidBock(blockPosition) || this.search(blockPosition, !shouldGoDown)) {
                return;
            }
            for (int x2 = -1; x2 <= 1; ++x2) {
                for (int z = -1; z <= 1; ++z) {
                    if (!this.search(blockPosition.add(x2, 0, z), !shouldGoDown)) continue;
                    return;
                }
            }
        } else {
            this.data = this.getBlockPos();
        }
    }

    private double calcStepSize(double range) {
        double accuracy = 6.0;
        accuracy += accuracy % 2.0;
        return Math.max(range / accuracy, 0.01);
    }

    private boolean search(BlockPos blockPosition, boolean checks) {
        Vec3 eyesPos = new Vec3(Scaffold.mc.thePlayer.posX, Scaffold.mc.thePlayer.getEntityBoundingBox().minY + (double) Scaffold.mc.thePlayer.getEyeHeight(), Scaffold.mc.thePlayer.posZ);
        PlaceRotation placeRotation = null;
        double xzRV = 0.5;
        double yRV = 0.5;
        double xzSSV = this.calcStepSize(xzRV);
        double ySSV = this.calcStepSize(xzRV);
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbor = blockPosition.offset(side);
            if (!BlockUtils.isValidBock(neighbor)) continue;
            Vec3 dirVec = new Vec3(side.getDirectionVec());
            for (double xSearch = 0.5 - xzRV / 2.0; xSearch <= 0.5 + xzRV / 2.0; xSearch += xzSSV) {
                for (double ySearch = 0.5 - yRV / 2.0; ySearch <= 0.5 + yRV / 2.0; ySearch += ySSV) {
                    for (double zSearch = 0.5 - xzRV / 2.0; zSearch <= 0.5 + xzRV / 2.0; zSearch += xzSSV) {
                        Vec3 posVec = new Vec3(blockPosition).addVector(xSearch, ySearch, zSearch);
                        double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
                        Vec3 hitVec = posVec.add(new Vec3(dirVec.xCoord * 0.5, dirVec.yCoord * 0.5, dirVec.zCoord * 0.5));
                        if (checks && (eyesPos.squareDistanceTo(hitVec) > 18.0 || distanceSqPosVec > eyesPos.squareDistanceTo(posVec.add(dirVec)) || Scaffold.mc.theWorld.rayTraceBlocks(eyesPos, hitVec, false, true, false) != null))
                            continue;
                        Rotation rotation = getRotation(hitVec, eyesPos);
                        Vec3 rotationVector = new Vec3(RotationUtil.getVectorForRotation(rotation.toVec2f()).xCoord, RotationUtil.getVectorForRotation(rotation.toVec2f()).yCoord, RotationUtil.getVectorForRotation(rotation.toVec2f()).zCoord);
                        Vec3 vector = eyesPos.addVector(rotationVector.xCoord * 4.0, rotationVector.yCoord * 4.0, rotationVector.zCoord * 4.0);
                        MovingObjectPosition obj = Scaffold.mc.theWorld.rayTraceBlocks(eyesPos, vector, false, false, true);
                        if (obj.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || !obj.getBlockPos().equals(neighbor) || placeRotation != null && !(RotationComponent.getRotationDifference(rotation) < RotationComponent.getRotationDifference(placeRotation.getRotation())))
                            continue;
                        placeRotation = new PlaceRotation(new PlaceInfo(neighbor, side.getOpposite(), hitVec), rotation);
                    }
                }
            }
        }
        if (placeRotation == null) {
            return false;
        }
        this.data = placeRotation.getPlaceInfo().getBlockPos();
        this.enumFacing = placeRotation.getPlaceInfo().getEnumFacing();
        return true;
    }

    private static Rotation getRotation(Vec3 hitVec, Vec3 eyesPos) {
        double diffX = hitVec.xCoord - eyesPos.xCoord;
        double diffY = hitVec.yCoord - eyesPos.yCoord;
        double diffZ = hitVec.zCoord - eyesPos.zCoord;
        double diffXZ = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        return new Rotation(MathHelper.wrapAngleTo180_float((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f), MathHelper.wrapAngleTo180_float((float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)))));
    }

    private EnumFacing getPlaceSide(BlockPos blockPos) {
        Vec3 vec3;
        BlockPos bp;
        ArrayList<Vec3> positions = new ArrayList<Vec3>();
        HashMap<Vec3, EnumFacing> hashMap = new HashMap<Vec3, EnumFacing>();
        BlockPos playerPos = new BlockPos(Scaffold.mc.thePlayer.posX, Scaffold.mc.thePlayer.posY, Scaffold.mc.thePlayer.posZ);
        if (BlockUtils.isAirBlock(blockPos.add(0, 1, 0)) && !blockPos.add(0, 1, 0).equals(playerPos) && !Scaffold.mc.thePlayer.onGround) {
            bp = blockPos.add(0, 1, 0);
            vec3 = this.getBestHitFeet(bp);
            positions.add(vec3);
            hashMap.put(vec3, EnumFacing.UP);
        }
        if (BlockUtils.isAirBlock(blockPos.add(1, 0, 0)) && !blockPos.add(1, 0, 0).equals(playerPos)) {
            bp = blockPos.add(1, 0, 0);
            vec3 = this.getBestHitFeet(bp);
            positions.add(vec3);
            hashMap.put(vec3, EnumFacing.EAST);
        }
        if (BlockUtils.isAirBlock(blockPos.add(-1, 0, 0)) && !blockPos.add(-1, 0, 0).equals(playerPos)) {
            bp = blockPos.add(-1, 0, 0);
            vec3 = this.getBestHitFeet(bp);
            positions.add(vec3);
            hashMap.put(vec3, EnumFacing.WEST);
        }
        if (BlockUtils.isAirBlock(blockPos.add(0, 0, 1)) && !blockPos.add(0, 0, 1).equals(playerPos)) {
            bp = blockPos.add(0, 0, 1);
            vec3 = this.getBestHitFeet(bp);
            positions.add(vec3);
            hashMap.put(vec3, EnumFacing.SOUTH);
        }
        if (BlockUtils.isAirBlock(blockPos.add(0, 0, -1)) && !blockPos.add(0, 0, -1).equals(playerPos)) {
            bp = blockPos.add(0, 0, -1);
            vec3 = this.getBestHitFeet(bp);
            positions.add(vec3);
            hashMap.put(vec3, EnumFacing.NORTH);
        }
        positions.sort(Comparator.comparingDouble(vec3x -> Scaffold.mc.thePlayer.getDistance(vec3x.xCoord, vec3x.yCoord, vec3x.zCoord)));
        if (!positions.isEmpty()) {
            vec3 = this.getBestHitFeet(this.data);
            if (Scaffold.mc.thePlayer.getDistance(vec3.xCoord, vec3.yCoord, vec3.zCoord) >= Scaffold.mc.thePlayer.getDistance(positions.get(0).xCoord, positions.get(0).yCoord, positions.get(0).zCoord)) {
                return hashMap.get(positions.get(0));
            }
        }
        return null;
    }

    private Vec3 getBestHitFeet(BlockPos blockPos) {
        Block block = Scaffold.mc.theWorld.getBlockState(blockPos).getBlock();
        double ex = MathHelper.clamp_double(Scaffold.mc.thePlayer.posX, blockPos.getX(), (double) blockPos.getX() + block.getBlockBoundsMaxX());
        double ey = MathHelper.clamp_double(keepYValue.getValue() ? Scaffold.getYLevel() : Scaffold.mc.thePlayer.posY, blockPos.getY(), (double) blockPos.getY() + block.getBlockBoundsMaxY());
        double ez = MathHelper.clamp_double(Scaffold.mc.thePlayer.posZ, blockPos.getZ(), (double) blockPos.getZ() + block.getBlockBoundsMaxZ());
        return new Vec3(ex, ey, ez);
    }

    private BlockPos getBlockPos() {
        BlockPos playerPos = new BlockPos(Scaffold.mc.thePlayer.posX, Scaffold.getYLevel(), Scaffold.mc.thePlayer.posZ);
        ArrayList<Vec3> positions = new ArrayList<Vec3>();
        HashMap<Vec3, BlockPos> hashMap = new HashMap<Vec3, BlockPos>();
        for (int x2 = playerPos.getX() - 5; x2 <= playerPos.getX() + 5; ++x2) {
            for (int y2 = playerPos.getY() - 1; y2 <= playerPos.getY(); ++y2) {
                for (int z = playerPos.getZ() - 5; z <= playerPos.getZ() + 5; ++z) {
                    if (!BlockUtils.isValidBock(new BlockPos(x2, y2, z))) continue;
                    BlockPos blockPos = new BlockPos(x2, y2, z);
                    Block block = Scaffold.mc.theWorld.getBlockState(blockPos).getBlock();
                    double ex = MathHelper.clamp_double(Scaffold.mc.thePlayer.posX, blockPos.getX(), (double) blockPos.getX() + block.getBlockBoundsMaxX());
                    double ey = MathHelper.clamp_double(keepYValue.getValue() ? Scaffold.getYLevel() : Scaffold.mc.thePlayer.posY, blockPos.getY(), (double) blockPos.getY() + block.getBlockBoundsMaxY());
                    double ez = MathHelper.clamp_double(Scaffold.mc.thePlayer.posZ, blockPos.getZ(), (double) blockPos.getZ() + block.getBlockBoundsMaxZ());
                    Vec3 vec3 = new Vec3(ex, ey, ez);
                    positions.add(vec3);
                    hashMap.put(vec3, blockPos);
                }
            }
        }
        if (!positions.isEmpty()) {
            positions.sort(Comparator.comparingDouble(this::getBestBlock));
            return hashMap.get(positions.get(0));
        }
        return null;
    }

    private double getBestBlock(Vec3 vec3) {
        return Scaffold.mc.thePlayer.getDistanceSq(vec3.xCoord, vec3.yCoord, vec3.zCoord);
    }

    public int getBlockSlot() {
        for (int i = 0; i < 9; ++i) {
            if (!Scaffold.mc.thePlayer.inventoryContainer.getSlot(i + 36).getHasStack() || !(Scaffold.mc.thePlayer.inventoryContainer.getSlot(i + 36).getStack().getItem() instanceof ItemBlock))
                continue;
            return i;
        }
        return -1;
    }

    public int getBlockCount() {
        int n = 0;
        for (int i = 36; i < 45; ++i) {
            if (!Scaffold.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) continue;
            ItemStack stack = Scaffold.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            Item item = stack.getItem();
            if (!(stack.getItem() instanceof ItemBlock) || !this.isValid(item)) continue;
            n += stack.stackSize;
        }
        return n;
    }

    public boolean isValid(Item item) {
        return item instanceof ItemBlock && !invalidBlocks.contains(((ItemBlock) item).getBlock());
    }

    private void getBlock(int switchSlot) {
        for (int i = 9; i < 45; ++i) {
            ItemStack is;
            if (!Scaffold.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() || Scaffold.mc.currentScreen != null && !(Scaffold.mc.currentScreen instanceof GuiInventory) || !((is = Scaffold.mc.thePlayer.inventoryContainer.getSlot(i).getStack()).getItem() instanceof ItemBlock) || !this.isValid(is.getItem()))
                continue;
            if (36 + switchSlot == i) break;
            InventoryUtils.swap(i, switchSlot);
            break;
        }
    }
    public void renderCounterBlur() {
        if (!enabled && anim.isDone()) return;
        int slot = ScaffoldUtils.getBlockSlot();
        ItemStack heldItem = slot == -1 ? null : mc.thePlayer.inventory.mainInventory[slot];
        int count = slot == -1 ? 0 : ScaffoldUtils.getBlockCount();
        String countStr = String.valueOf(count);
        IFontRenderer fr = mc.fontRendererObj;
        ScaledResolution sr = new ScaledResolution(mc);
        int color;
        float x, y;
        String str = countStr + " block" + (count != 1 ? "s" : "");
        float output = anim.getOutput().floatValue();
        switch (countMode.getMode()) {
            case "OnLooker":
                float blockWH = heldItem != null ? 15 : -2;
                int spacing = 3;
                String text = "§l" + countStr + "§r block" + (count != 1 ? "s" : "");
                float textWidth = tenacityFont18.getStringWidth(text);

                float totalWidth = ((textWidth + blockWH + spacing) + 6) * output;
                x = sr.getScaledWidth() / 2f - (totalWidth / 2f);
                y = sr.getScaledHeight() - (sr.getScaledHeight() / 2f - 20);
                float height = 20;
                RenderUtil.scissorStart(x - 1.5, y - 1.5, totalWidth + 3, height + 3);

                RoundedUtil.drawRound(x, y, totalWidth, height, 5, Color.BLACK);
                RenderUtil.scissorEnd();
                break;
            case "Basic":
                x = sr.getScaledWidth() / 2F - fr.getStringWidth(str) / 2F + 1;
                y = sr.getScaledHeight() / 2F + 10;
                RenderUtil.scaleStart(sr.getScaledWidth() / 2.0F, y + fr.FONT_HEIGHT / 2.0F, output);
                fr.drawStringWithShadow(str, x, y, 0x000000);
                RenderUtil.scaleEnd();
                break;
            case "Polar":
                x = sr.getScaledWidth() / 2F - fr.getStringWidth(countStr) / 2F + (heldItem != null ? 6 : 1);
                y = sr.getScaledHeight() / 2F + 10;

                GlStateManager.pushMatrix();
                RenderUtil.fixBlendIssues();
                GL11.glTranslatef(x + (heldItem == null ? 1 : 0), y, 1);
                GL11.glScaled(anim.getOutput().floatValue(), anim.getOutput().floatValue(), 1);
                GL11.glTranslatef(-x - (heldItem == null ? 1 : 0), -y, 1);

                fr.drawOutlinedString(countStr, x, y, ColorUtil.applyOpacity(0x000000, output), true);

                if (heldItem != null) {
                    double scale = 0.7;
                    GlStateManager.color(1, 1, 1, 1);
                    GlStateManager.scale(scale, scale, scale);
                    RenderHelper.enableGUIStandardItemLighting();
                    mc.getRenderItem().renderItemAndEffectIntoGUI(
                            heldItem,
                            (int) ((sr.getScaledWidth() / 2F - fr.getStringWidth(countStr) / 2F - 7) / scale),
                            (int) ((sr.getScaledHeight() / 2F + 8.5F) / scale)
                    );
                    RenderHelper.disableStandardItemLighting();
                }
                GlStateManager.popMatrix();
                break;
        }
    }

    public void renderCounter() {
        anim.setDirection(enabled ? Direction.FORWARDS : Direction.BACKWARDS);
        if (!enabled && anim.isDone()) return;
        int slot = ScaffoldUtils.getBlockSlot();
        ItemStack heldItem = slot == -1 ? null : mc.thePlayer.inventory.mainInventory[slot];
        int count = slot == -1 ? 0 : ScaffoldUtils.getBlockCount();
        String countStr = String.valueOf(count);
        IFontRenderer fr = mc.fontRendererObj;
        ScaledResolution sr = new ScaledResolution(mc);
        int color;
        float x, y;
        String str = countStr + " block" + (count != 1 ? "s" : "");
        float output = anim.getOutput().floatValue();
        switch (countMode.getMode()) {
            case "OnLooker":
                float blockWH = heldItem != null ? 15 : -2;
                int spacing = 3;
                String text = "§l" + countStr + "§r block" + (count != 1 ? "s" : "");
                float textWidth = tenacityFont18.getStringWidth(text);

                float totalWidth = ((textWidth + blockWH + spacing) + 6) * output;
                x = sr.getScaledWidth() / 2f - (totalWidth / 2f);
                y = sr.getScaledHeight() - (sr.getScaledHeight() / 2f - 20);
                float height = 20;
                RenderUtil.scissorStart(x - 1.5, y - 1.5, totalWidth + 3, height + 3);

                RoundedUtil.drawRound(x, y, totalWidth, height, 5, ColorUtil.tripleColor(20, .45f));

                tenacityFont18.drawString(text, x + 3 + blockWH + spacing, y + tenacityFont18.getMiddleOfBox(height) + .5f, -1);

                if (heldItem != null) {
                    RenderHelper.enableGUIStandardItemLighting();
                    mc.getRenderItem().renderItemAndEffectIntoGUI(heldItem, (int) x + 3, (int) (y + 10 - (blockWH / 2)));
                    RenderHelper.disableStandardItemLighting();
                }
                RenderUtil.scissorEnd();
                break;
            case "Basic":
                x = sr.getScaledWidth() / 2F - fr.getStringWidth(str) / 2F + 1;
                y = sr.getScaledHeight() / 2F + 10;
                RenderUtil.scaleStart(sr.getScaledWidth() / 2.0F, y + fr.FONT_HEIGHT / 2.0F, output);
                fr.drawStringWithShadow(str, x, y, -1);
                RenderUtil.scaleEnd();
                break;
            case "Polar":
                color = count < 24 ? 0xFFFF5555 : count < 128 ? 0xFFFFFF55 : 0xFF55FF55;
                x = sr.getScaledWidth() / 2F - fr.getStringWidth(countStr) / 2F + (heldItem != null ? 6 : 1);
                y = sr.getScaledHeight() / 2F + 10;

                GlStateManager.pushMatrix();
                RenderUtil.fixBlendIssues();
                GL11.glTranslatef(x + (heldItem == null ? 1 : 0), y, 1);
                GL11.glScaled(anim.getOutput().floatValue(), anim.getOutput().floatValue(), 1);
                GL11.glTranslatef(-x - (heldItem == null ? 1 : 0), -y, 1);

                fr.drawOutlinedString(countStr, x, y, ColorUtil.applyOpacity(color, output), true);

                if (heldItem != null) {
                    double scale = 0.7;
                    GlStateManager.color(1, 1, 1, 1);
                    GlStateManager.scale(scale, scale, scale);
                    RenderHelper.enableGUIStandardItemLighting();
                    mc.getRenderItem().renderItemAndEffectIntoGUI(
                            heldItem,
                            (int) ((sr.getScaledWidth() / 2F - fr.getStringWidth(countStr) / 2F - 7) / scale),
                            (int) ((sr.getScaledHeight() / 2F + 8.5F) / scale)
                    );
                    RenderHelper.disableStandardItemLighting();
                }
                GlStateManager.popMatrix();
                break;
        }
    }
    int getBestSpoofSlot() {
        int spoofSlot = 5;
        for (int i = 36; i < 45; ++i) {
            if (Scaffold.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) continue;
            spoofSlot = i - 36;
            break;
        }
        return spoofSlot;
    }
}

