package dev.onlooker.commands.impl;

import dev.onlooker.Client;
import dev.onlooker.commands.Command;
import dev.onlooker.module.Module;

public class ClearConfigCommand extends Command {

    public ClearConfigCommand() {
        super("clearconfig", "Turns off all enabled modules", ".clearconfig");
    }

    @Override
    public void execute(String[] args) {
        Client.INSTANCE.getModuleCollection().getModules().stream().filter(Module::isEnabled).forEach(Module::toggle);
    }
}
