package dev.onlooker.module.impl.misc;

import dev.onlooker.Client;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.settings.impl.BooleanSetting;
import dev.onlooker.utils.player.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public final class Teams extends Module {
    private static final BooleanSetting armorValue = new BooleanSetting("ArmorColor", true);
    private static final BooleanSetting colorValue = new BooleanSetting("Color", true);
    private static final BooleanSetting scoreboardValue = new BooleanSetting("ScoreboardTeam", true);

    public Teams() {
        super("Teams", Category.MISC, "lol");
    }

    public static boolean isSameTeam(Entity entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer) entity;
            if (Client.INSTANCE.isEnabled(Teams.class)) {
                return armorValue.getValue() && PlayerUtil.armorTeam(entityPlayer) || colorValue.getValue() && PlayerUtil.colorTeam(entityPlayer) || scoreboardValue.getValue() && PlayerUtil.scoreTeam(entityPlayer);
            }
            return false;
        }
        return false;
    }
}
