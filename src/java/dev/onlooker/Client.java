package dev.onlooker;

import dev.onlooker.commands.CommandHandler;
import dev.onlooker.config.ConfigManager;
import dev.onlooker.config.DragManager;
import dev.onlooker.event.EventProtocol;
import dev.onlooker.gui.mainmenu.altmanager.GuiAltManager;
import dev.onlooker.gui.clickguis.searchbar.SearchBar;
import dev.onlooker.gui.clickguis.sidegui.SideGUI;
import dev.onlooker.module.Module;
import dev.onlooker.module.api.ModuleCollection;
import dev.onlooker.utils.Utils;
import dev.onlooker.utils.client.ReleaseType;
import dev.onlooker.utils.client.cloud.CloudDataManager;
import dev.onlooker.utils.objects.DiscordAccount;
import dev.onlooker.utils.objects.Dragging;
import dev.onlooker.utils.server.PingerUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
@Setter
public class Client implements Utils {

    public static final Client INSTANCE = new Client();
    public static boolean DEV_MODE = false;
    public static final String NAME = "OnLooker";
    public static final String VERSION = "2.0";
    //默认PUBLIC，本地测试DEV，发布beta换beta
    public static final ReleaseType RELEASE = ReleaseType.DEV;
    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final File DIRECTORY = new File(mc.mcDataDir, NAME);
    public static final String CREDIT = "OnLooker is developed by The OnLooker Team";

    private final EventProtocol eventProtocol = new EventProtocol();
    private final CloudDataManager cloudDataManager = new CloudDataManager();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final SideGUI sideGui = new SideGUI();
    private final SearchBar searchBar = new SearchBar();
    private ModuleCollection moduleCollection;
    private ConfigManager configManager;
    private GuiAltManager altManager;
    private CommandHandler commandHandler;
    private PingerUtils pingerUtils;
    private DiscordAccount discordAccount;

    public static boolean updateGuiScale;
    public static int prevGuiScale;
    public static final ResourceLocation cape = new ResourceLocation("OnLooker/Capes/cape.png");
    public String getVersion() {
        return VERSION + (RELEASE != ReleaseType.PUBLIC ? " " + RELEASE.getName() : "");
    }

    public final Color getClientColor() {
        return new Color(231, 0, 255);
    }

    public final Color getAlternateClientColor() {
        return new Color(0, 206, 255);
    }

    public boolean isEnabled(Class<? extends Module> c) {
        Module m = INSTANCE.moduleCollection.get(c);
        return m != null && m.isEnabled();
    }
    public Dragging createDrag(Module module, String name, float x, float y) {
        DragManager.draggables.put(name, new Dragging(module, name, x, y));
        return DragManager.draggables.get(name);
    }
}
