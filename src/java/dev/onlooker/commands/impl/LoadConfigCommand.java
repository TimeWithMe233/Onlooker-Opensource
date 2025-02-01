package dev.onlooker.commands.impl;

import dev.onlooker.Client;
import dev.onlooker.commands.Command;
import dev.onlooker.config.ConfigManager;
import dev.onlooker.config.LocalConfig;
import dev.onlooker.gui.notifications.NotificationManager;
import dev.onlooker.gui.notifications.NotificationType;
import dev.onlooker.utils.misc.FileUtils;
import dev.onlooker.utils.player.ChatUtil;

public class LoadConfigCommand extends Command {
    public LoadConfigCommand() {
        super("config", "load / save your current config", ".config [load/save] [config name]");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            ChatUtil.error("参数不足 Usage: .config [load/save] [config name]");
            return;
        }

        if(args[0].equals("load")) {
            ChatUtil.print("Try to Load...");
            for (LocalConfig i : ConfigManager.localConfigs) {
                if(i.getName().equals(args[1])) {
                    Client.INSTANCE.getConfigManager().loadConfig(FileUtils.readFile(i.getFile()), true);
                    NotificationManager.post(NotificationType.SUCCESS,"Config","Successfully loaded " + i.getName() + " from " + i.getFile());
                }
            }
        }

        if(args[0].equals("save")) {
            ChatUtil.print("Try to Save...");
            Client.INSTANCE.getConfigManager().saveConfig(args[1]);
            NotificationManager.post(NotificationType.SUCCESS,"Config","Successfully saved config.");
        }
    }
}
