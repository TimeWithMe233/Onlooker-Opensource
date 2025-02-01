package dev.onlooker.module.impl.world;

import dev.onlooker.event.impl.game.TickEvent;
import dev.onlooker.event.impl.game.WorldEvent;
import dev.onlooker.event.impl.network.PacketSendEvent;
import dev.onlooker.event.impl.player.EventAttack;
import dev.onlooker.event.impl.player.UpdateEvent;
import dev.onlooker.gui.notifications.NotificationManager;
import dev.onlooker.gui.notifications.NotificationType;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.impl.combat.KillAura;
import dev.onlooker.module.settings.impl.BooleanSetting;
import dev.onlooker.utils.player.WeaponDetection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C01PacketChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerWarn extends Module {
    private final BooleanSetting tarckNoti = new BooleanSetting("PlayerTrack Noti",true);

    public static List<Entity> flaggedEntity = new ArrayList<>();
    private final Random random = new Random();
    private EntityLivingBase target = null;
    public static String name;
    public static int kill = 0;
    public static boolean regen = false;
    public static boolean strength = false;
    public static boolean gapple = false;
    public static boolean godaxe = false;
    public static boolean kbball = false;

    public PlayerWarn() {
        super("PlayerWarn", Category.WORLD,"PlayerWarn");
        addSettings(tarckNoti);
    }

    @Override
    public void onWorldEvent(WorldEvent event){
        flaggedEntity.clear();
        strength = false;
        regen = false;
        godaxe = false;
        gapple = false;
        kbball = false;
    }

    @Override
    public void onAttackEvent(EventAttack event){
        if (isNull()) return;
        EntityLivingBase targetEntity = KillAura.target;
        if (targetEntity instanceof EntityPlayer) {
            target = targetEntity;
        }
    }

    @Override
    public void onUpdateEvent(UpdateEvent event){
        if (isNull()) return;
        if (target != null && this.target.getHealth() <= 0.0f && !mc.theWorld.loadedEntityList.contains(this.target)) {
            if (isNull()) return;
            if (target.isDead) {
                kill++;
            }
            target = null;
        }
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent event){
        if (isNull()) return;
        final Packet<?> packet = event.getPacket();
        if (packet instanceof C01PacketChatMessage) {
            C01PacketChatMessage chatPacket = (C01PacketChatMessage) packet;
            String message = chatPacket.getMessage();
            if (message.startsWith("/")) {
                return;
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (char c : message.toCharArray()) {
                if (c >= 33 && c <= 128) {
                    stringBuilder.append((char) (c + 65248));
                } else {
                    stringBuilder.append(c);
                }
            }
            chatPacket.message = stringBuilder.toString();
        }
    };

    @Override
    public void onTickEvent(TickEvent event){
        if (isNull()) return;
        if (mc.theWorld == null || mc.theWorld.loadedEntityList.isEmpty()) {
            strength = false;
            regen = false;
            godaxe = false;
            gapple = false;
            kbball = false;
            return;
        }
        if (WeaponDetection.isInLobby()) {
            strength = false;
            regen = false;
            godaxe = false;
            gapple = false;
            kbball = false;
            return;
        }
        if (mc.thePlayer.ticksExisted % 6 == 0) {
            for (final Entity ent : mc.theWorld.loadedEntityList) {
                if (ent instanceof EntityPlayer && ent != mc.thePlayer) {
                    final EntityPlayer player = (EntityPlayer) ent;
                    if (WeaponDetection.isStrength(player) > 0 && !flaggedEntity.contains(player)) {
                        flaggedEntity.add(player);
                        if (tarckNoti.getValue()) {
                            NotificationManager.post(NotificationType.WARNING, "有新的傻逼", player.getName() + " 拥有力量药水", 20.0f);
                        }
                        name = player.getName();
                        strength = true;
                    }
                    if (WeaponDetection.isRegen(player) > 0 && !flaggedEntity.contains(player)) {
                        flaggedEntity.add(player);
                        if (tarckNoti.getValue()) {
                            NotificationManager.post(NotificationType.WARNING, "有新的傻逼", player.getName() + " 拥有恢复药水", 20.0f);
                        }
                        name = player.getName();
                        regen = true;
                    }
                    if (WeaponDetection.isHoldingGodAxe(player) && !flaggedEntity.contains(player)) {
                        flaggedEntity.add(player);
                        if (tarckNoti.getValue()) {
                            NotificationManager.post(NotificationType.WARNING, "有新的傻逼", player.getName() + " 正在使用秒人斧", 20.0f);
                        }
                        name = player.getName();
                        godaxe = true;
                    }
                    if (WeaponDetection.isKBBall(player.getHeldItem()) && !flaggedEntity.contains(player)) {
                        flaggedEntity.add(player);
                        if (tarckNoti.getValue()) {
                            NotificationManager.post(NotificationType.WARNING, "有新的傻逼", player.getName() + " 正在使用击退球,请小心点", 20.0f);
                        }
                        name = player.getName();
                        kbball = true;
                    }
                    if (WeaponDetection.hasEatenGoldenApple(player) <= 0 || flaggedEntity.contains(player)) {
                        continue;
                    }
                    name = player.getName();
                    gapple = true;
                    flaggedEntity.add(player);
                    if (tarckNoti.getValue()) {
                        NotificationManager.post(NotificationType.WARNING, "有新的傻逼", player.getName() + " 拥有附魔金苹果", 20.0f);
                    }
                }
            }
        }
    };
}

