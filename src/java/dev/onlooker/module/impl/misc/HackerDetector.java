package dev.onlooker.module.impl.misc;

import dev.onlooker.event.impl.game.WorldEvent;
import dev.onlooker.event.impl.network.PacketReceiveEvent;
import dev.onlooker.event.impl.player.UpdateEvent;
import dev.onlooker.gui.notifications.NotificationManager;
import dev.onlooker.gui.notifications.NotificationType;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.settings.impl.BooleanSetting;
import dev.onlooker.utils.player.ChatUtil;
import dev.onlooker.utils.player.MoveUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HackerDetector extends Module {
    private final BooleanSetting self = new BooleanSetting("Self", false);
    private final BooleanSetting badPacketA = new BooleanSetting("BadPackets (A)", true);
    private final BooleanSetting noSlowA = new BooleanSetting("NoSlow (A)", true);
    private final BooleanSetting noSlowB = new BooleanSetting("NoSlow (B)", true);
    private final BooleanSetting reachA = new BooleanSetting("Reach (A)", true);
    private final BooleanSetting flightA = new BooleanSetting("Flight (A)", true);
    private final BooleanSetting freeze = new BooleanSetting("Freeze", false);
    private final BooleanSetting aim = new BooleanSetting("Aim", true);
    private final BooleanSetting specialName = new BooleanSetting("SpecialName", true);
    private final BooleanSetting chat = new BooleanSetting("Chat", false);
    private final BooleanSetting noti = new BooleanSetting("Notifications", true);
    private final BooleanSetting changeWorldClear = new BooleanSetting("ChangeWorld Clear", true);
    private final Map<String, Map<String, Integer>> playerViolations = new HashMap<>();
    private final Set<String> identifiedPlayers = new HashSet<>();
    private static final Map<String, Integer> checkAlertFrequencies = new HashMap<>();
    private final Map<String, String> alertedPlayersServerSide = new HashMap<>();
    public final Set<String> cheaters = new HashSet<>();
    private int freezeTicks;


    public HackerDetector() {
        super("Hackerdetector",Category.MISC,"Detects people using cheats inside your game");
        addSettings(self, badPacketA, noSlowA, noSlowB, reachA, flightA, freeze, aim, specialName, chat, noti, changeWorldClear);
    }

    static {
        checkAlertFrequencies.put("BadPackets (A)", 3);
        checkAlertFrequencies.put("NoSlow (A)", 5);
        checkAlertFrequencies.put("NoSlow (B)", 3);
        checkAlertFrequencies.put("SpecialName", 1);
        checkAlertFrequencies.put("Ground Spoof", 2);
        checkAlertFrequencies.put("Freeze", 2);
        checkAlertFrequencies.put("Aim", 1);
    }

    @Override
    public void onEnable() {
        freezeTicks = 0;
        identifiedPlayers.clear();
        alertedPlayersServerSide.clear();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        freezeTicks = 0;
        identifiedPlayers.clear();
        alertedPlayersServerSide.clear();
        super.onDisable();
    }

    @Override
    public void onWorldEvent(WorldEvent event ) {
        if (isNull()) return;

        if (changeWorldClear.getValue()) {
            playerViolations.clear();
            alertedPlayersServerSide.clear();
            freezeTicks = 0;
        }

    }

    @Override
    public void onUpdateEvent(UpdateEvent event) {
        if (isNull()) return;

        for (EntityPlayer player : mc.theWorld.playerEntities) {
            if (!self.getValue() && player.equals(mc.thePlayer)) {
                continue;
            }

            if (badPacketA.getValue()) {
                if (player.rotationPitch > 90 || player.rotationPitch < -90) {
                    incrementViolation(player, "BadPackets (A)");
                }
            }

            if (player.isUsingItem()) {
                if (noSlowA.getValue()) {
                    double deltaX = Math.abs(player.posX - player.lastTickPosX);
                    double deltaZ = Math.abs(player.posZ - player.lastTickPosZ);

                    if (!player.onGround && player.getItemInUseDuration() < 12) {
                        return;
                    }

                    if (deltaX > 0.2 || deltaZ > 0.2) {
                        incrementViolation(player, "NoSlow (A)");
                    }
                }
            }

            if (noSlowB.getValue()) {
                if (player.getFoodStats().getFoodLevel() < 6.0 && player.isSprinting()) {
                    incrementViolation(player, "NoSlow (B)");
                }
            }

            if (flightA.getValue()) {
                if (!player.onGround && player.motionY == 0.0 && MoveUtil.isMoving(player)) {
                    incrementViolation(player, "Flight (A)");
                }
            }

            if (player.posY - player.lastTickPosY == 0.0) {
                if (freeze.getValue()) {
                    if (!isSurrounded(player) && !player.onGround && !player.isOnLadder() && !player.capabilities.isFlying) {
                        freezeTicks++;
                        if (freezeTicks > 40 && player.hurtTime == 0) {
                            incrementViolation(player, "Freeze");
                            freezeTicks = 0;
                        }
                    }
                }
            }

            if (aim.getValue()) {
                double deltaYaw = Math.abs(player.posX - player.lastTickPosX);
                double deltaPitch = Math.abs(player.posZ - player.lastTickPosZ);
                if (deltaYaw == 360 || deltaYaw == 180 || deltaYaw == 90 || deltaPitch == 90 || deltaPitch == 45) {
                    incrementViolation(player, "Aim");
                }
            }

            if (specialName.getValue() && isSpecialName(player.getCommandSenderName()) && !identifiedPlayers.contains(player.getCommandSenderName())) {
                if (identifiedPlayers.add(player.getCommandSenderName())) {
                    if (chat.getValue()) {
                        alert(player, "SpecialName", 1);
                    }
                }
            }
        }
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent event){
        if (isNull()) return;

        Packet<?> packet = event.getPacket();

        if (packet instanceof S19PacketEntityStatus) {
            S19PacketEntityStatus wrapper = new S19PacketEntityStatus();
            if (reachA.getValue()) {
                if (wrapper.getOpCode() == 2) {
                    World world = mc.theWorld;
                    Entity attackerEntity = mc.theWorld.getEntityByID(wrapper.getEntityId());
                    EntityPlayer attacker = mc.thePlayer;
                    if (attackerEntity instanceof EntityPlayer) {
                        attacker = (EntityPlayer) attackerEntity;
                    }

                    EntityPlayer closestPlayer = null;
                    double minDistance = Double.MAX_VALUE;

                    for (EntityPlayer player : world.playerEntities) {
                        double distance = attacker.getDistanceToEntity(player);
                        if (distance < minDistance && player.getEntityId() != attacker.getEntityId()) {
                            minDistance = distance;
                            closestPlayer = player;
                        }
                    }

                    if (closestPlayer != null && closestPlayer.getEntityId() == wrapper.getEntityId() && attacker.getDistanceToEntity(closestPlayer) < 7) {
                        double maxDistance = attacker.capabilities.isCreativeMode ? 5.0 : 3.0;

                        if (minDistance > maxDistance) {
                            incrementViolation(attacker, "Reach (A)", "Distance: " + minDistance);
                        }
                    }
                }
            }
        }
    }

    private boolean isSurrounded(EntityPlayer player) {
        BlockPos playerPos = new BlockPos(player.posX, player.posY, player.posZ);
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos checkPos = playerPos.add(x, -1, z);
                Block block = mc.theWorld.getBlockState(checkPos).getBlock();
                if (block == Blocks.air) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isSpecialName(String playerName) {
        String playerNameLower = playerName.toLowerCase();

        boolean specialPrefix = playerNameLower.startsWith("hel") ||
                playerNameLower.startsWith("mcbb") ||
                playerNameLower.contains("xylitol") ||
                playerNameLower.contains("silence") ||
                playerNameLower.contains("快速宏") ||
                playerNameLower.contains("老安卓") ||
                playerNameLower.contains("木糖醇") ||
                playerNameLower.contains("欣欣");

        boolean isRandomName = playerName.matches("(?=.*[0-9])(?=.*[a-zA-Z]).{4,12}") || playerName.matches("^[a-z0-9]{4,12}$");

        return specialPrefix || isRandomName;
    }

    private void incrementViolation(EntityPlayer player, String checkName) {
        String playerName = player.getCommandSenderName();

        playerViolations.putIfAbsent(playerName, new HashMap<>());
        Map<String, Integer> violations = playerViolations.get(playerName);

        int newVl = violations.getOrDefault(checkName, 0) + 1;
        violations.put(checkName, newVl);

        Integer alertFrequency = checkAlertFrequencies.getOrDefault(checkName, Integer.MAX_VALUE);
        if (newVl % alertFrequency == 0) {
            if (chat.getValue()) {
                alert(player, checkName, newVl);
            }
            cheaters.add(playerName);
            if (noti.getValue()) {
                NotificationManager.post(NotificationType.WARNING, "AntiCheat", player.getCommandSenderName() + " failed " + checkName, 2);
            }
        }
    }

    private void incrementViolation(EntityPlayer player, String checkName, String msg) {
        String playerName = player.getCommandSenderName();

        playerViolations.putIfAbsent(playerName, new HashMap<>());
        Map<String, Integer> violations = playerViolations.get(playerName);

        int newVl = violations.getOrDefault(checkName, 0) + 1;
        violations.put(checkName, newVl);
        Integer alertFrequency = checkAlertFrequencies.getOrDefault(checkName, Integer.MAX_VALUE);
        if (newVl % alertFrequency == 0) {
            if (chat.getValue()) {
                alert(player, checkName, newVl, msg);
            }
            cheaters.add(playerName);
        }
    }

    public void alert(EntityPlayer player, String checkName, int vl, String msg) {
        ChatUtil.print("AntiCheat " + EnumChatFormatting.YELLOW + player.getCommandSenderName() + EnumChatFormatting.RESET + " failed " + EnumChatFormatting.RED + checkName + EnumChatFormatting.GRAY + msg + EnumChatFormatting.RESET + ". (VL." + vl + ") ");
    }

    public void alert(EntityPlayer player, String checkName, int vl) {
        ChatUtil.print("AntiCheat " + EnumChatFormatting.YELLOW + player.getCommandSenderName() + EnumChatFormatting.RESET + " failed " + EnumChatFormatting.RED + checkName + EnumChatFormatting.RESET + ". (VL." + vl + ")");
    }

    public boolean isCheater(String playerName) {
        return cheaters.contains(playerName);
    }

}
