package dev.onlooker.module.impl.player;

import dev.onlooker.event.impl.game.WorldEvent;
import dev.onlooker.event.impl.network.PacketReceiveEvent;
import dev.onlooker.event.impl.network.PacketSendEvent;
import dev.onlooker.event.impl.player.MotionEvent;
import dev.onlooker.event.impl.player.UpdateEvent;
import dev.onlooker.event.impl.render.Render2DEvent;
import dev.onlooker.event.impl.render.Render3DEvent;
import dev.onlooker.event.impl.render.ShaderEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.impl.display.HUDMod;
import dev.onlooker.module.settings.impl.BooleanSetting;
import dev.onlooker.utils.BlinkUtils;
import dev.onlooker.utils.animations.Animation;
import dev.onlooker.utils.animations.Direction;
import dev.onlooker.utils.animations.impl.DecelerateAnimation;
import dev.onlooker.utils.animations.impl.SmoothStepAnimation;
import dev.onlooker.utils.render.ColorUtil;
import dev.onlooker.utils.render.RenderUtil;
import dev.onlooker.utils.render.RoundedUtil;
import dev.onlooker.utils.server.PacketUtils;
import dev.onlooker.utils.time.TimerUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C16PacketClientStatus;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

public class Blink extends Module {
    private final BooleanSetting render = new BooleanSetting("Render",true);
    private final TimerUtil pulseTimer = new TimerUtil();
    private static EntityOtherPlayerMP fakePlayer = null;
    private final LinkedList<double[]> positions = new LinkedList();
    private final LinkedBlockingQueue<Packet<INetHandlerPlayClient>> packets = new LinkedBlockingQueue();
    private final Animation anim = new SmoothStepAnimation(250, 1);

    public Blink() {
        super("Blink", Category.PLAYER, "CNM");
        addSettings(render);
    }

    @Override
    public void onEnable() {
        if (Blink.mc.thePlayer == null) {
            return;
        }
        BlinkUtils.setBlinkState(false, false, true, false, false, false, false, false, false, false, false);
        fakePlayer = new EntityOtherPlayerMP(Blink.mc.theWorld, Blink.mc.thePlayer.gameProfile);
        fakePlayer.clonePlayer(Blink.mc.thePlayer, true);
        fakePlayer.copyLocationAndAnglesFrom(Blink.mc.thePlayer);
        Blink.fakePlayer.rotationYawHead = Blink.mc.thePlayer.rotationYawHead;
        Blink.mc.theWorld.addEntityToWorld(-1337, fakePlayer);
        this.packets.clear();
        LinkedList<double[]> linkedList = this.positions;
        synchronized (linkedList) {
            this.positions.add(new double[]{Blink.mc.thePlayer.posX, Blink.mc.thePlayer.getEntityBoundingBox().minY + (double) (Blink.mc.thePlayer.getEyeHeight() / 2.0f), Blink.mc.thePlayer.posZ});
            this.positions.add(new double[]{Blink.mc.thePlayer.posX, Blink.mc.thePlayer.getEntityBoundingBox().minY, Blink.mc.thePlayer.posZ});
        }
        this.pulseTimer.reset();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        LinkedList<double[]> linkedList = this.positions;
        synchronized (linkedList) {
            this.positions.clear();
        }
        if (Blink.mc.thePlayer == null) {
            return;
        }
        BlinkUtils.setBlinkState(true, true, false, false, false, false, false, false, false, false, false);
        BlinkUtils.clearPacket(null, false, -1);
        this.packets.clear();
        this.clearPackets();
        if (fakePlayer != null) {
            Blink.mc.theWorld.removeEntityFromWorld(fakePlayer.getEntityId());
            fakePlayer = null;
        }
        super.onDisable();
    }

    @Override
    public void onWorldEvent(WorldEvent event) {
        setEnabled(false);
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        if (event.isPre()) {
            if (mc.thePlayer.ticksExisted % 2 == 1) {
                PacketUtils.sendC0F(1234, (short) 1234, true, true);
            }
        }
    }

    @Override
    public void onUpdateEvent(UpdateEvent event) {
        this.setSuffix(String.valueOf(BlinkUtils.bufferSize(null)));
        LinkedList<double[]> linkedList = this.positions;
        synchronized (linkedList) {
            this.positions.add(new double[]{Blink.mc.thePlayer.posX, Blink.mc.thePlayer.getEntityBoundingBox().minY, Blink.mc.thePlayer.posZ});
        }
        if (Blink.mc.thePlayer.ticksExisted % 2 == 1) {
            PacketUtils.sendC0F();
        }
    }

    private void clearPackets() {
        while (!this.packets.isEmpty()) {
            try {
                PacketUtils.handlePacket(this.packets.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent event) {
        Packet<?> packet = event.getPacket();
        if (packet.getClass().getSimpleName().startsWith("S")) {
            if (Blink.mc.thePlayer.ticksExisted < 20) {
                return;
            }
            event.setCancelled(true);
            this.packets.add((Packet<INetHandlerPlayClient>) packet);
        }
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent event) {
        Packet<?> packet = event.getPacket();
        if ((event.getPacket() instanceof C16PacketClientStatus || event.getPacket() instanceof C00PacketKeepAlive)) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onRender3DEvent(Render3DEvent event) {
        LinkedList<double[]> linkedList = this.positions;
        synchronized (linkedList) {
            GL11.glPushMatrix();
            GL11.glDisable(3553);
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(2848);
            GL11.glEnable(3042);
            GL11.glDisable(2929);
            Blink.mc.entityRenderer.disableLightmap();
            GL11.glLineWidth(2.0f);
            GL11.glBegin(3);
            RenderUtil.glColor(new Color(68, 131, 123, 255).getRGB());
            double renderPosX = Blink.mc.getRenderManager().viewerPosX;
            double renderPosY = Blink.mc.getRenderManager().viewerPosY;
            double renderPosZ = Blink.mc.getRenderManager().viewerPosZ;
            for (double[] pos : this.positions) {
                GL11.glVertex3d(pos[0] - renderPosX, pos[1] - renderPosY, pos[2] - renderPosZ);
            }
            GL11.glColor4d(1.0, 1.0, 1.0, 1.0);
            GL11.glEnd();
            GL11.glEnable(2929);
            GL11.glDisable(2848);
            GL11.glDisable(3042);
            GL11.glEnable(3553);
            GL11.glPopMatrix();
        }
    }
    @Override
    public void onRender2DEvent(Render2DEvent event) {
        if (mc.thePlayer != null && render.isEnabled()) {
            renderCounter();
        }
    }

    @Override
    public void onShaderEvent(ShaderEvent event) {
        if (mc.thePlayer != null && render.isEnabled()) {
            renderCounterBlur();
        }
    }

    public void renderCounterBlur() {
        if (!isEnabled() && anim.isDone()) return;

        String countStr = String.valueOf(BlinkUtils.bufferSize(null));
        ScaledResolution sr = new ScaledResolution(mc);
        float x, y;
        float output = anim.getOutput().floatValue();
        int spacing = 3;
        String text = "§l" + countStr + "§r packets";
        float textWidth = tenacityFont18.getStringWidth(text);
        float totalWidth = ((textWidth + spacing) + 6) * output;
        x = sr.getScaledWidth() / 2F - 60F;
        y = sr.getScaledHeight() / 1.85F;
        float height = 20;
        tenacityFont18.drawString(text, x + 30.0f  + spacing, y + tenacityFont18.getMiddleOfBox(height) + .5f , -1);
        RoundedUtil.drawRound(x + 10, y, 100, 5.0F, 2f, Color.BLACK);
        RoundedUtil.drawGradientHorizontal(x + 10, y,
                (totalWidth + 25) * Math.min(Math.max((BlinkUtils.bufferSize(null) / 12500f), 0F), 1.08f), 5, 1.5f, Color.BLACK, Color.BLACK);
    }

    public void renderCounter() {
        anim.setDirection(isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);
        if (!isEnabled() && anim.isDone()) return;

        String countStr = String.valueOf(BlinkUtils.bufferSize(null));
        ScaledResolution sr = new ScaledResolution(mc);
        float x, y;
        float output = anim.getOutput().floatValue();
        int spacing = 3;
        String text = "§l" + countStr + "§r packets";
        float textWidth = tenacityFont18.getStringWidth(text);
        float totalWidth = ((textWidth + spacing) + 6) * output;
        x = sr.getScaledWidth() / 2F - 60F;
        y = sr.getScaledHeight() / 1.85F;
        float height = 20;
        Color c1 = ColorUtil.applyOpacity(HUDMod.getClientColors().getFirst(), 222);
        Color c2 = ColorUtil.applyOpacity(HUDMod.getClientColors().getSecond(), 222);

        RenderUtil.scissorStart(x - 1.5, y - 1.5, totalWidth + 3, height + 23);
        RenderUtil.scissorEnd();
        tenacityFont18.drawString(text, x + 30.0f  + spacing, y + tenacityFont18.getMiddleOfBox(height) + .5f , -1);
        RoundedUtil.drawRound(x + 10, y, 100, 5.0F, 2f, new Color(0, 0, 0, 125));
        RoundedUtil.drawGradientHorizontal(x + 10, y,
                (totalWidth + 25) * Math.min(Math.max((BlinkUtils.bufferSize(null) / 12500f), 0F), 1.08f), 5, 1.5f, c1, c2);
    }
}
