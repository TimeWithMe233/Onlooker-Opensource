package dev.onlooker.module.impl.player;

import dev.onlooker.event.impl.player.MotionEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.settings.impl.ModeSetting;
import dev.onlooker.module.settings.impl.NumberSetting;
import dev.onlooker.utils.server.PacketUtils;
import net.minecraft.network.play.client.C03PacketPlayer;

public final class Regen extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Vifa");
    private final NumberSetting health = new NumberSetting("Health", 1, 20, 1, 1);

    private boolean teleported;

    @Override
    public void onMotionEvent(MotionEvent event) {
        this.setSuffix(mode.getMode());
        switch (mode.getMode()) {
            case "Vanilla":
                if (mc.thePlayer.getFoodStats().getFoodLevel() > 3 && mc.thePlayer.getHealth() <= health.getValue().floatValue()) {
                    for (int i = 0; i < 5; i++) {
                        PacketUtils.sendPacketNoEvent(new C03PacketPlayer(true));
                    }
                }
                break;
            case "Vifa":
                if (mc.thePlayer.getFoodStats().getFoodLevel() > 3 && mc.thePlayer.getHealth() <= health.getValue().floatValue()) {
                    if (!teleported) {
                        mc.thePlayer.setPosition(mc.thePlayer.posX, -999, mc.thePlayer.posZ);
                        if (!mc.thePlayer.onGround) {
                            teleported = true;
                        }
                    }

                    if (teleported) {
                        for (int i = 0; i < 20; i++) {
                            PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.lastTickPosX, mc.thePlayer.lastTickPosY, mc.thePlayer.lastTickPosZ, false));
                        }
                        mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                    }
                } else {
                    teleported = false;
                }
                break;
        }
    }

    public Regen() {
        super("Regen", Category.PLAYER, "regen hp faster");
        this.addSettings(mode, health);
    }

    @Override
    public void onDisable() {
        teleported = false;
        super.onDisable();
    }

}
