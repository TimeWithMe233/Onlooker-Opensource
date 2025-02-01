package dev.onlooker.module.impl.world;

import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.settings.impl.BooleanSetting;

public class PacketFix extends Module {
    public final BooleanSetting fixLadder = new BooleanSetting("Fix Ladder", true);
    public final BooleanSetting fixMotionJump = new BooleanSetting("Fix Motion Jump", true);
    public final BooleanSetting fixLilyPad = new BooleanSetting("Fix LilyPad", true);
    public final BooleanSetting fixPane = new BooleanSetting("Fix Pane", true);
    public final BooleanSetting fixFarmland = new BooleanSetting("Fix Farmland", true);

    public PacketFix() {
        super("PacketFix", Category.WORLD,"fix ViaMCP");
        addSettings(fixLadder, fixMotionJump, fixLilyPad, fixPane, fixFarmland);
    }

}
