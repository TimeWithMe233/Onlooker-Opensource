package dev.onlooker.module.impl.player;

import dev.onlooker.event.impl.network.PacketReceiveEvent;
import dev.onlooker.event.impl.network.PacketSendEvent;
import dev.onlooker.event.impl.player.MotionEvent;
import dev.onlooker.event.impl.render.Render2DEvent;
import dev.onlooker.event.impl.render.ShaderEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.impl.display.HUDMod;
import dev.onlooker.module.settings.impl.BooleanSetting;
import dev.onlooker.module.settings.impl.NumberSetting;
import dev.onlooker.utils.animations.Animation;
import dev.onlooker.utils.animations.Direction;
import dev.onlooker.utils.animations.impl.SmoothStepAnimation;
import dev.onlooker.utils.player.MovementUtils;
import dev.onlooker.utils.render.ColorUtil;
import dev.onlooker.utils.render.RenderUtil;
import dev.onlooker.utils.render.RoundedUtil;
import dev.onlooker.utils.server.PacketUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;

import java.awt.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class TimerBalance extends Module {
    private final NumberSetting amount = new NumberSetting("Amount", 5, 10, 0.1, 0.1);
    private final BooleanSetting balance = new BooleanSetting("Balance", false);
    private final BooleanSetting render = new BooleanSetting("Render", false);
    private int count = 0;

    final ConcurrentLinkedQueue<Packet<?>> packets = new ConcurrentLinkedQueue<>();

    public TimerBalance() {
        super("BalanceTimer", Category.PLAYER, "Changes game speed");
        this.addSettings(amount, balance, render);
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        if (!balance.isEnabled()) {
            mc.timer.timerSpeed = amount.getValue().floatValue();
        } else {
            PacketUtils.sendPacketNoEvent(new C0FPacketConfirmTransaction(0, (short) 0, true));
            if (count >= 0) {
                mc.timer.timerSpeed = MovementUtils.isMoving() ? amount.getValue().floatValue() : 1f;
            } else {
                toggle();
            }
        }
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent event) {
        if (event.getPacket() instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity) event.getPacket()).entityID == mc.thePlayer.getEntityId()) {
            toggle();
        }
        if (event.getPacket() instanceof S18PacketEntityTeleport && ((S18PacketEntityTeleport) event.getPacket()).getEntityId() == mc.thePlayer.getEntityId()) {
            toggle();
        }
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            toggle();
        }
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent event) {
        if (event.getPacket() instanceof C03PacketPlayer) {
            if (!((C03PacketPlayer) event.getPacket()).isMoving()) {
                count += 50;
                event.cancel();
            } else {
                count -= 50;
            }
        }
        if (event.getPacket() instanceof C0FPacketConfirmTransaction) {
            event.cancel();
            packets.add(event.getPacket());
        }
        if (event.getPacket() instanceof C02PacketUseEntity && ((C02PacketUseEntity) event.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK) {
            toggle();
        }
    }

    @Override
    public void onDisable() {
        if (!packets.isEmpty()) {
            packets.forEach(PacketUtils::sendPacketNoEvent);
            packets.clear();
        }
        count = 0;
        mc.timer.timerSpeed = 1;
        super.onDisable();
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

    private final Animation anim = new SmoothStepAnimation(250, 1);

    public void renderCounterBlur() {
        if (!isEnabled() && anim.isDone()) return;

        String countStr = String.valueOf(count);
        ScaledResolution sr = new ScaledResolution(mc);
        float x, y;
        float output = anim.getOutput().floatValue();
        int spacing = 3;
        String text = "§l" + countStr + "§r balance";
        float textWidth = tenacityFont18.getStringWidth(text);
        float totalWidth = ((textWidth + spacing) + 6) * output;
        x = sr.getScaledWidth() / 2F - 60F;
        y = sr.getScaledHeight() / 1.85F;
        float height = 20;
        tenacityFont18.drawString(text, x + 30.0f  + spacing, y + tenacityFont18.getMiddleOfBox(height) + .5f , -1);
        RoundedUtil.drawRound(x + 10, y, 100, 5.0F, 2f, Color.BLACK);
        RoundedUtil.drawGradientHorizontal(x + 10, y,
                (totalWidth + 25) * Math.min(Math.max((count / 12500f), 0F), 1.08f), 5, 1.5f, Color.BLACK, Color.BLACK);
    }

    public void renderCounter() {
        anim.setDirection(isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);
        if (!isEnabled() && anim.isDone()) return;

        String countStr = String.valueOf(count);
        ScaledResolution sr = new ScaledResolution(mc);
        float x, y;
        float output = anim.getOutput().floatValue();
        int spacing = 3;
        String text = "§l" + countStr + "§r balance";
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
                (totalWidth + 25) * Math.min(Math.max((count / 12500f), 0F), 1.08f), 5, 1.5f, c1, c2);
    }
}

