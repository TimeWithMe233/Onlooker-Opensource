package dev.onlooker.module.impl.player;


import dev.onlooker.gui.notifications.NotificationManager;
import dev.onlooker.gui.notifications.NotificationType;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.settings.impl.ModeSetting;
import dev.onlooker.utils.player.ChatUtil;

public final class GetBlock extends Module {
    private final ModeSetting blockmode = new ModeSetting("Type Block", "Glass",  "Stone","Diamond_Block", "Glass");



    public GetBlock() {
        super("GetBlock", Category.PLAYER, "Quickly get 64 block in Loyisa server");
        addSettings(blockmode);
    }

    @Override
    public void onEnable() {
        ChatUtil.send("/give " + blockmode.getName().toLowerCase() + " 64");
        setEnabled(false);
    }

    @Override
    public void onDisable() {
        NotificationManager.post(NotificationType.SUCCESS,"GetBlock","Get 64 "+blockmode.getName().toLowerCase()+" for you");
    }
}
