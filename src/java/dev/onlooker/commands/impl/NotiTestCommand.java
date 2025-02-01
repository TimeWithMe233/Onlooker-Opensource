package dev.onlooker.commands.impl;

import dev.onlooker.commands.Command;
import dev.onlooker.gui.notifications.NotificationManager;
import dev.onlooker.gui.notifications.NotificationType;

public class NotiTestCommand extends Command {

    public NotiTestCommand() {
        super("n", "Test notification", ".n [mes]", "n");
    }

    @Override
    public void execute(String[] args) {
            NotificationManager.post(NotificationType.WARNING, "测试", " 这是一个测试文本");
        }
    }

