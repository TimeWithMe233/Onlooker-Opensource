package dev.onlooker.commands.impl;

import dev.onlooker.commands.Command;
import dev.onlooker.utils.misc.Multithreading;

public class LoadCommand extends Command {

    public LoadCommand() {
        super("load", "Loads a script or config from the cloud.", ".load <share code>");
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 1) {
            usage();
        } else {
            String shareCode = args[0];
            Multithreading.runAsync(() -> {
                sendChatWithPrefix("Loading config from the cloud...");
            });
        }
    }
}
