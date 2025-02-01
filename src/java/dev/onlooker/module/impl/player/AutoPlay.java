package dev.onlooker.module.impl.player;

import dev.onlooker.Client;
import dev.onlooker.event.impl.game.WorldEvent;
import dev.onlooker.event.impl.network.PacketReceiveEvent;
import dev.onlooker.event.impl.player.UpdateEvent;
import dev.onlooker.gui.notifications.NotificationManager;
import dev.onlooker.gui.notifications.NotificationType;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.impl.combat.KillAura;
import dev.onlooker.module.impl.combat.Velocity;
import dev.onlooker.module.settings.impl.BooleanSetting;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;

public class AutoPlay extends Module {

    public AutoPlay() {
        super("AutoPlay", Category.PLAYER, "AutoPlay");
    }
    public static int wins = 0;
    public static int banned = 0;
    private final BooleanSetting autodis = new BooleanSetting("AutoDisable", true);




    public void onWorldChange(WorldEvent event) {
        disableModule();
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent event) {
        handlePacket(event.getPacket());
    }
    public void disableModule() {
        disableModule(KillAura.class);
        disableModule(InvManager.class);
        disableModule(ChestStealer.class);
    }

    private void disableModule(Class<? extends Module> moduleClass) {
        Module module = Client.INSTANCE.getModuleCollection().get(moduleClass);
        if (module.isEnabled()) {
            module.setEnabled(false);
        }
    }

    private void handlePacket(Packet<?> packet) {
        if (packet instanceof S01PacketJoinGame) {
            disableModule();
        } else if (packet instanceof S02PacketChat) {
            handleChatPacket((S02PacketChat) packet);
        }
    }

    private void handleChatPacket(S02PacketChat chatPacket) {
        String text = chatPacket.getChatComponent().getUnformattedText();

        if (text.contains("开始倒计时: 1 秒")) {
            enableImportantModules();
        } else if (text.contains("你在地图") && text.contains("赢得了")) {
            handleWin();
        } else if (text.contains("[起床战争] Game 结束！感谢您的参与！") || text.contains("喜欢 一般 不喜欢")) {
            disableImportantModules();
        } else if (text.contains("玩家") && text.contains("在本局游戏中行为异常")) {
            handleBannedPlayer();
        }
    }

    private void enableImportantModules() {
        Client.INSTANCE.getModuleCollection().get(KillAura.class).setEnabled(true);
        Client.INSTANCE.getModuleCollection().get(Velocity.class).setEnabled(true);
        Client.INSTANCE.getModuleCollection().get(ChestStealer.class).setEnabled(true);
        Client.INSTANCE.getModuleCollection().get(InvManager.class).setEnabled(true);
    }

    private void handleWin() {
        wins++;
        if (autodis.getValue()) {
            disableImportantModules();
        }
    }

    private void disableImportantModules() {
        if (autodis.getValue()) {
            disableModule(KillAura.class);
            disableModule(Velocity.class);
            disableModule(ChestStealer.class);
            disableModule(InvManager.class);
        }
    }

    private void handleBannedPlayer() {
        banned++;
        NotificationManager.post(NotificationType.WARNING, "BanChecker", "A player was banned.", 1.0f);
    }
}
