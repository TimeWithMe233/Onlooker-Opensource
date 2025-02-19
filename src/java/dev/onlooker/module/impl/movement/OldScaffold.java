package dev.onlooker.module.impl.movement;

import dev.onlooker.event.impl.game.TickEvent;
import dev.onlooker.event.impl.network.PacketSendEvent;
import dev.onlooker.event.impl.player.BlockPlaceableEvent;
import dev.onlooker.event.impl.player.MotionEvent;
import dev.onlooker.event.impl.player.MoveEvent;
import dev.onlooker.event.impl.player.SafeWalkEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.settings.ParentAttribute;
import dev.onlooker.module.settings.impl.BooleanSetting;
import dev.onlooker.module.settings.impl.ModeSetting;
import dev.onlooker.module.settings.impl.NumberSetting;
import dev.onlooker.utils.animations.Animation;
import dev.onlooker.utils.animations.Direction;
import dev.onlooker.utils.animations.impl.EaseBackIn;
import dev.onlooker.utils.misc.MathUtils;
import dev.onlooker.utils.player.MovementUtils;
import dev.onlooker.utils.player.OldScaffoldUtils;
import dev.onlooker.utils.player.RotationUtils;
import dev.onlooker.utils.player.ScaffoldUtils;
import dev.onlooker.utils.render.ColorUtil;
import dev.onlooker.utils.render.RenderUtil;
import dev.onlooker.utils.render.RoundedUtil;
import dev.onlooker.utils.server.PacketUtils;
import dev.onlooker.utils.time.TimerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.gui.IFontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class OldScaffold extends Module {
    private final ModeSetting countMode = new ModeSetting("Block Counter", "OnLooker", "None", "OnLooker", "Basic", "Polar");
    public static final ModeSetting mode = new ModeSetting("Scaffold Mode", "Normal", "Normal");
    private final BooleanSetting rotations = new BooleanSetting("Rotations", true);
    private final ModeSetting rotationMode = new ModeSetting("Rotation Mode", "Watchdog", "Watchdog", "NCP", "Backwards", "Better");
    public static ModeSetting sprintMode = new ModeSetting("Sprint Mode", "Vanilla", "Vanilla", "Watchdog", "Legit", "None");
    private final NumberSetting JumpStrafe = new NumberSetting("Jump Strafe", 0.47, 0.55, 0.4, 0.01);
    public static ModeSetting towerMode = new ModeSetting("Tower Mode", "Legit", "Vanilla", "NCP", "Legit", "Watchdog");
    public static NumberSetting delay = new NumberSetting("Delay", 0.0, 2.0, 0.0, 0.05);
    private final NumberSetting timer = new NumberSetting("Timer", 1.0, 5.0, 0.1, 0.1);
    public static BooleanSetting speedcontrol = new BooleanSetting("Custom Speed", false);
    public static final BooleanSetting swing = new BooleanSetting("Swing", false);
    public static NumberSetting customspeed = new NumberSetting("Speed", 4.32, 5.75, 4.0, 0.01);
    public static final BooleanSetting safewalk = new BooleanSetting("Safewalk", false);
    private OldScaffoldUtils.BlockCache blockCache;
    private OldScaffoldUtils.BlockCache lastBlockCache;
    private float y;
    private float speed;
    private final TimerUtil delayTimer = new TimerUtil();
    private final TimerUtil timerUtil = new TimerUtil();
    public static double keepYCoord;
    private boolean firstJump;
    private boolean pre;
    private int jumpTimer;
    private int slot;
    private int prevSlot;
    boolean onGround = false;
    private float[] cachedRots = new float[2];
    private final Animation anim = new EaseBackIn(250, 1.0, (float) 1.0);
    float yaw = 0.0F;
    boolean setFastFall = false;
    public static double moveTicks = 0.0;
    int ticks = 0;

    public OldScaffold() {
        super("OldScaffold", Category.MOVEMENT, "Automatically places blocks under you");
        this.addSettings(this.countMode, this.rotations, this.rotationMode, sprintMode, this.JumpStrafe, delay, this.timer, safewalk, swing);
        this.rotationMode.addParent(this.rotations, ParentAttribute.BOOLEAN_CONDITION);
        this.JumpStrafe.addParent(sprintMode, (sprintMode) -> {
            return sprintMode.is("Watchdog");
        });
    }

    @Override
    public void onTickEvent(TickEvent event) {
        if (!mc.gameSettings.keyBindJump.isKeyDown() && MovementUtils.isMoving() && !mc.thePlayer.onGround && sprintMode.is("Watchdog")) {
        }
        //this.setSuffix(mode.getMode());
        if (MovementUtils.isMoving()) {
            ++moveTicks;
        } else {
            moveTicks = 0.0;
        }
    }

    @Override
    public void onMotionEvent(MotionEvent e) {
        if (sprintMode.is("Watchdog") && mc.thePlayer.onGround && MovementUtils.isMoving()) {
            mc.thePlayer.setSprinting(false);
            MovementUtils.setSpeed(this.JumpStrafe.getValue());
            mc.thePlayer.jump();
        }

        if (!mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.timer.timerSpeed = this.timer.getValue().floatValue();
        }

        if (mc.gameSettings.keyBindJump.pressed) {
            keepYCoord = mc.thePlayer.posY - 1.0;
        }

        if (sprintMode.is("Legit")) {
            if (Math.abs(MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw) - MathHelper.wrapAngleTo180_float(this.yaw)) > 90.0F) {
                mc.gameSettings.keyBindSprint.pressed = false;
                mc.thePlayer.setSprinting(false);
            } else {
                mc.gameSettings.keyBindSprint.pressed = true;
                mc.thePlayer.setSprinting(true);
            }
        } else if (sprintMode.is("None")) {
            mc.gameSettings.keyBindSprint.pressed = false;
            mc.thePlayer.setSprinting(false);
        } else {
            mc.gameSettings.keyBindSprint.pressed = true;
            mc.thePlayer.setSprinting(true);
        }

        if (!mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.timer.timerSpeed = this.timer.getValue().floatValue();
        }

        if (this.rotations.isEnabled()) {
            float[] rotations = new float[]{0.0F, 0.0F};
            float prevYaw;
            float f;
            switch (this.rotationMode.getMode()) {
                case "Watchdog":
                    rotations = new float[]{MovementUtils.getMoveYaw(e.getYaw()) - 360.0F, this.y};
                    if (mc.thePlayer.onGround && !MovementUtils.isMoving()) {
                        prevYaw = this.cachedRots[0];
                        if ((this.blockCache = OldScaffoldUtils.getBlockInfo()) == null) {
                            this.blockCache = this.lastBlockCache;
                        }

                        if (this.blockCache != null && (mc.thePlayer.ticksExisted % 2 == 0 || mc.theWorld.getBlockState(new BlockPos(e.getX(), OldScaffoldUtils.getYLevel(), e.getZ())).getBlock() == Blocks.air)) {
                            this.cachedRots = RotationUtils.getRotations(this.blockCache.getPosition(), this.blockCache.getFacing());
                        }

                        rotations = this.cachedRots;
                        this.yaw = rotations[0];
                        e.setRotations(rotations[0], rotations[1]);
                    } else {
                        e.setRotations(rotations[0], rotations[1]);
                    }
                    break;
                case "NCP":
                    prevYaw = this.cachedRots[0];
                    if ((this.blockCache = OldScaffoldUtils.getBlockInfo()) == null) {
                        this.blockCache = this.lastBlockCache;
                    }

                    if (this.blockCache != null && (mc.thePlayer.ticksExisted % 3 == 0 || mc.theWorld.getBlockState(new BlockPos(e.getX(), OldScaffoldUtils.getYLevel(), e.getZ())).getBlock() == Blocks.air)) {
                        this.cachedRots = RotationUtils.getRotations(this.blockCache.getPosition(), this.blockCache.getFacing());
                    }

                    rotations = this.cachedRots;
                    this.yaw = rotations[0];
                    e.setRotations(rotations[0], rotations[1]);
                    break;
                case "Backwards":
                    rotations = new float[]{MovementUtils.getMoveYaw(e.getYaw()) - 180.0F, 77.0F};
                    this.yaw = rotations[0];
                    e.setRotations(rotations[0], rotations[1]);
                    break;
                case "Down":
                    e.setPitch(90.0F);
                    break;
                case "Better":
                    float val;
                    if (MovementUtils.isMoving()) {
                        f = MovementUtils.getMoveYaw(e.getYaw()) - 180.0F;
                        float[] numbers = new float[]{-135.0F, -90.0F, -45.0F, 0.0F, 45.0F, 90.0F, 135.0F, 180.0F};
                        float lastDiff = 999.0F;
                        val = f;
                        float[] var10 = numbers;
                        int var11 = numbers.length;

                        for (int var12 = 0; var12 < var11; ++var12) {
                            float v = var10[var12];
                            float diff = Math.abs(v - f);
                            if (diff < lastDiff) {
                                lastDiff = diff;
                                val = v;
                            }
                        }
                    } else {
                        val = rotations[0];
                    }

                    rotations = new float[]{(val + MathHelper.wrapAngleTo180_float(mc.thePlayer.prevRotationYawHead)) / 2.0F, (77.0F + MathHelper.wrapAngleTo180_float(mc.thePlayer.prevRotationPitchHead)) / 2.0F};
                    e.setRotations(rotations[0], rotations[1]);
                    break;
                case "Enum":
                    if (this.lastBlockCache != null) {
                        f = RotationUtils.getEnumRotations(this.lastBlockCache.getFacing());
                        e.setRotations(f, 77.0F);
                    } else {
                        e.setRotations(mc.thePlayer.rotationYaw + 180.0F, 77.0F);
                    }
                    break;
                case "0":
                    e.setRotations(0.0F, 0.0F);
            }

            this.yaw = rotations[0];
            RotationUtils.setVisualRotations(e);
        }

        if (mc.thePlayer.onGround) {
            keepYCoord = Math.floor(mc.thePlayer.posY - 1.0);
        }
        switch (towerMode.getMode()) {
            case "Watchdog":
                if (ticks == 1) {
                    mc.thePlayer.motionY = 0.33;
                } else if (ticks == 2) {
                    mc.thePlayer.motionY = 1 - mc.thePlayer.posY % 1;
                    ticks = -1;
                }
                if (mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown()) {
                    mc.thePlayer.motionY = 0.241;
                }

                if (this.onGround) {
                    this.ticks = 0;
                } else {
                    ticks = ticks + 1;
                }
        }
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            double centerX = Math.floor(e.getX()) + 0.5;
            double centerZ = Math.floor(e.getZ()) + 0.5;
            switch (towerMode.getMode()) {
                case "Vanilla":
                    mc.thePlayer.motionY = 0.41999998688697815;
                    break;
                case "NCP":
                    if (!MovementUtils.isMoving() || (double) MovementUtils.getSpeed() < 0.16) {
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.motionY = 0.42;
                        } else if (mc.thePlayer.motionY < 0.23) {
                            mc.thePlayer.setPosition(mc.thePlayer.posX, (double) ((int) mc.thePlayer.posY), mc.thePlayer.posZ);
                            mc.thePlayer.motionY = 0.42;
                        }
                    }
            }
        }

        this.blockCache = OldScaffoldUtils.getBlockInfo();
        if (this.blockCache == null) {
            return;
        }

        this.lastBlockCache = OldScaffoldUtils.getBlockInfo();
        if (mc.thePlayer.ticksExisted % 4 == 0) {
            this.pre = true;
        }
        this.pre = false;
    }

    public static boolean isAirOrLiquid(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        return block instanceof BlockAir || block instanceof BlockLiquid;
    }

    @Override
    public void onMoveEvent(MoveEvent event) {
        if (speedcontrol.isEnabled()) {
            mc.thePlayer.setSprinting(false);
            if (mc.thePlayer.onGround && MovementUtils.isMoving()) {
                event.setSpeed(customspeed.getValue() / 20.0);
            }
        }

    }

    private boolean place() {
        int slot = OldScaffoldUtils.getBlockSlot();
        if (this.blockCache != null && this.lastBlockCache != null && slot != -1) {
            if (this.slot != slot) {
                this.slot = slot;
                PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(this.slot));
            }

            boolean placed = false;
            if (this.delayTimer.hasTimeElapsed(delay.getValue() * 1000.0)) {
                this.firstJump = false;
                if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getStackInSlot(this.slot), this.lastBlockCache.getPosition(), this.lastBlockCache.getFacing(), OldScaffoldUtils.getHypixelVec3(this.lastBlockCache))) {
                    placed = true;
                    this.y = MathUtils.getRandomInRange(79.5F, 83.5F);
                    PacketUtils.sendPacket(new C0APacketAnimation());
                }

                this.delayTimer.reset();
                this.blockCache = null;
            }
            if (swing.get()) {
                mc.thePlayer.swingItem();
            }
            return placed;
        } else {
            return false;
        }
    }

    public void onBlockPlaceable(BlockPlaceableEvent event) {
        this.place();
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer != null && this.slot != mc.thePlayer.inventory.currentItem) {
            PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }

        mc.timer.timerSpeed = 1.0F;
        mc.gameSettings.keyBindSneak.pressed = false;
        super.onDisable();
    }

    @Override
    public void onEnable() {
        this.lastBlockCache = null;
        if (mc.thePlayer != null) {
            this.prevSlot = mc.thePlayer.inventory.currentItem;
            this.slot = mc.thePlayer.inventory.currentItem;
            if (mc.thePlayer.isSprinting() && !sprintMode.is("None") && !sprintMode.is("Vanilla") && !sprintMode.is("Legit")) {
                PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, Action.STOP_SPRINTING));
            }
        }

        this.firstJump = true;
        this.speed = 1.1F;
        this.timerUtil.reset();
        this.jumpTimer = 0;
        this.y = 80.0F;
        super.onEnable();
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent e) {
        if (e.getPacket() instanceof C0BPacketEntityAction && ((C0BPacketEntityAction) e.getPacket()).getAction() == Action.START_SPRINTING && !sprintMode.is("None") && !sprintMode.is("Vanilla") && !sprintMode.is("Legit")) {
            e.cancel();
        }

        if (e.getPacket() instanceof C09PacketHeldItemChange) {
            e.cancel();
        }

        if (e.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            C08PacketPlayerBlockPlacement.setStack(mc.thePlayer.inventory.getStackInSlot(this.slot));
        }

        if (e.getPacket() instanceof C03PacketPlayer) {
            this.setFastFall = true;
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
    @Override
    public void onSafeWalkEvent(SafeWalkEvent event) {
        if (safewalk.isEnabled() || OldScaffoldUtils.getBlockCount() == 0) {
            event.setSafe(true);
        }

    }
}
