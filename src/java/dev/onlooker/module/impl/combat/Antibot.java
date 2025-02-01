package dev.onlooker.module.impl.combat;

import dev.onlooker.event.impl.game.WorldEvent;
import dev.onlooker.event.impl.network.PacketReceiveEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.settings.impl.BooleanSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S14PacketEntity;

import java.util.ArrayList;
import java.util.List;

public class Antibot extends Module {

    private static final List<Integer> groundBotList = new ArrayList<>();
    private static final List<String> playerName = new ArrayList<>();

    private static BooleanSetting entityUD = new BooleanSetting("Entity", false);
    private static BooleanSetting dead = new BooleanSetting("Dead", true);
    private static BooleanSetting health = new BooleanSetting("Health", false);
    private static BooleanSetting ground = new BooleanSetting("Ground", false);
    public Antibot() {
        super("Anti bot", Category.COMBAT, "Anti HYT Bots AND Maybe more");
        addSettings(entityUD,dead,health,ground);

    }


    @Override
    public void onWorldEvent(WorldEvent event) {
        clearAll();
    }

    private void clearAll() {
        playerName.clear();
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent event) {
        if (mc.thePlayer== null || mc.theWorld == null) return;
        Packet<?> packet = event.getPacket();
        if (event.getPacket() instanceof S14PacketEntity && ground.getValue()) {
            Entity entity = ((S14PacketEntity) event.getPacket()).getEntity(mc.theWorld);

            if (entity instanceof EntityPlayer) {
                if (((S14PacketEntity) event.getPacket()).onGround
                        && !groundBotList.contains(entity.getEntityId())) {
                    groundBotList.add(entity.getEntityId());
                }
            }
        }

    }

    public static boolean isServerBot(Entity entity) {
            if (entity instanceof EntityPlayer) {

                if (dead.getValue() && entity.isDead) {
                    return true;
                }
                if (health.getValue() && ((EntityPlayer) entity).getHealth() == 0.0F) {
                    return true;
                }
                if (entityUD.getValue() && (entity.getEntityId() >= 1000000000 || entity.getEntityId() <= -1)) {
                    return true;
                }
                if (ground.getValue() && !groundBotList.contains(entity.getEntityId())) {
                    return true;
                }
            }
            return false;
    }
}
