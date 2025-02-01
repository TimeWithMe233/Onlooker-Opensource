package dev.onlooker.module;

import dev.onlooker.Client;
import dev.onlooker.commands.CommandHandler;
import dev.onlooker.commands.impl.*;
import dev.onlooker.config.ConfigManager;
import dev.onlooker.config.DragManager;
import dev.onlooker.gui.SplashScreen;
import dev.onlooker.gui.mainmenu.altmanager.GuiAltManager;
import dev.onlooker.module.api.BackgroundProcess;
import dev.onlooker.module.api.ModuleCollection;
import dev.onlooker.module.impl.combat.*;
import dev.onlooker.module.impl.display.*;
import dev.onlooker.module.impl.misc.*;
import dev.onlooker.module.impl.movement.*;
import dev.onlooker.module.impl.player.*;
import dev.onlooker.module.impl.render.*;
import dev.onlooker.module.impl.render.wings.DragonWings;
import dev.onlooker.module.impl.world.*;
import dev.onlooker.utils.player.FallDistanceComponent;
import dev.onlooker.utils.player.RenderSlotComponent;
import dev.onlooker.utils.player.RotationComponent;
import dev.onlooker.utils.player.SlotComponent;
import dev.onlooker.utils.render.EntityCulling;
import dev.onlooker.utils.render.Theme;
import dev.onlooker.utils.server.PingerUtils;
import dev.onlooker.utils.addons.viamcp.viamcp.ViaMCP;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

public class ProtectedLaunch {

    private static final HashMap<Object, Module> modules = new HashMap<>();

    public static void start() {
        Client.INSTANCE.setModuleCollection(new ModuleCollection());
        Client.LOGGER.info("Starting ViaMCP...");
        ViaMCP.create();
        ViaMCP.INSTANCE.initAsyncSlider();

        // Combat
        modules.put(Antibot.class, new Antibot());
        modules.put(KillAura.class, new KillAura());
        modules.put(Velocity.class, new Velocity());
        modules.put(Criticals.class, new Criticals());
        modules.put(KeepSprint.class, new KeepSprint());
        modules.put(ThrowableAura.class, new ThrowableAura());
        modules.put(SuperKnockback.class, new SuperKnockback());

        // Movement
        modules.put(Sprint.class, new Sprint());
        modules.put(OldScaffold.class, new OldScaffold());
        modules.put(Speed.class, new Speed());
        modules.put(Flight.class, new Flight());
        modules.put(FastLadder.class, new FastLadder());
        modules.put(NoWeb.class, new NoWeb());
        modules.put(NoLiquid.class, new NoLiquid());
        modules.put(LongJump.class, new LongJump());
        modules.put(Step.class, new Step());
        modules.put(InventoryMove.class, new InventoryMove());

        // World
        modules.put(ContainerAura.class, new ContainerAura());
        modules.put(AutoStuck.class, new AutoStuck());
        modules.put(KeyPearl.class, new KeyPearl());
        modules.put(PacketFix.class, new PacketFix());
        modules.put(Disabler.class, new Disabler());
        modules.put(PlayerWarn.class, new PlayerWarn());
        modules.put(Scaffold.class, new Scaffold());
        modules.put(Stuck.class, new Stuck());

        // Misc
        modules.put(HackerDetector.class, new HackerDetector());
        modules.put(Protocol.class, new Protocol());
        modules.put(Teams.class, new Teams());
        modules.put(NoRotateSet.class, new NoRotateSet());
        modules.put(MCF.class, new MCF());

        // Player
        //modules.put(Blink.class, new Blink());
        modules.put(SpeedMine.class, new SpeedMine());
        modules.put(ChestStealer.class, new ChestStealer());
        modules.put(GetBlock.class, new GetBlock());
        modules.put(InvManager.class, new InvManager());
        modules.put(TimerBalance.class, new TimerBalance());
        modules.put(Regen.class, new Regen());
        modules.put(Freecam.class, new Freecam());
        modules.put(FastPlace.class, new FastPlace());
        modules.put(FakePlayer.class, new FakePlayer());
        modules.put(SafeWalk.class, new SafeWalk());
        modules.put(NoSlow.class, new NoSlow());
        modules.put(AutoTool.class, new AutoTool());
        modules.put(AntiVoid.class, new AntiVoid());
        modules.put(AutoPlay.class, new AutoPlay());

        // Render
        modules.put(Projectile.class, new Projectile());
        modules.put(ClickGUIMod.class, new ClickGUIMod());
        modules.put(Animations.class, new Animations());
        modules.put(Ambience.class, new Ambience());
        modules.put(ChinaHat.class, new ChinaHat());
        modules.put(GlowESP.class, new GlowESP());
        modules.put(Brightness.class, new Brightness());
        modules.put(RemoveEffects.class, new RemoveEffects());
        modules.put(KillEffect.class, new KillEffect());
        modules.put(ESP2D.class, new ESP2D());
//        modules.put(Statistics.class, new Statistics());
        modules.put(TargetHUDMod.class, new TargetHUDMod());
        modules.put(Glint.class, new Glint());
        modules.put(Breadcrumbs.class, new Breadcrumbs());
        modules.put(Streamer.class, new Streamer());
        modules.put(NoHurtCam.class, new NoHurtCam());
        modules.put(ItemPhysics.class, new ItemPhysics());
        modules.put(EntityCulling.class, new EntityCulling());
        modules.put(DragonWings.class, new DragonWings());
        modules.put(JumpCircle.class, new JumpCircle());
        modules.put(CustomModel.class, new CustomModel());
        modules.put(MoBendsMod.class, new MoBendsMod());
        modules.put(MotionBlur.class,new MotionBlur());
        modules.put(EntityEffects.class, new EntityEffects());
        modules.put(Chams.class, new Chams());
        modules.put(BrightPlayers.class, new BrightPlayers());
        modules.put(XRay.class, new XRay());

        //Display
        modules.put(ArrayListMod.class, new ArrayListMod());
        modules.put(NotificationsMod.class, new NotificationsMod());
        modules.put(ArmorHUDMod.class, new ArmorHUDMod());
        modules.put(InventoryHUDMod.class, new InventoryHUDMod());
        modules.put(ScoreboardMod.class, new ScoreboardMod());
        modules.put(HUDMod.class, new HUDMod());
        modules.put(PostProcessing.class, new PostProcessing());
        modules.put(SessionHUDMod.class, new SessionHUDMod());
        modules.put(PotionHUDMod.class, new PotionHUDMod());

        SplashScreen.drawSplashScreen(55);
        Client.INSTANCE.getModuleCollection().setModules(modules);
        Theme.init();
        Client.INSTANCE.setPingerUtils(new PingerUtils());

        CommandHandler commandHandler = new CommandHandler();
        commandHandler.commands.addAll(Arrays.asList(
                new FriendCommand(), new CopyNameCommand(), new BindCommand(), new UnbindCommand(),
                new LoadConfigCommand(), new SettingCommand(), new HelpCommand(),
                new VClipCommand(), new ClearBindsCommand(), new ClearConfigCommand(),
                new LoadCommand(), new ToggleCommand(),new NotiTestCommand()
        ));
        Client.INSTANCE.setCommandHandler(commandHandler);
        Client.INSTANCE.getEventProtocol().register(new BackgroundProcess());
        Client.INSTANCE.getEventProtocol().register(new RotationComponent());
        Client.INSTANCE.getEventProtocol().register(new SlotComponent());
        Client.INSTANCE.getEventProtocol().register(new FallDistanceComponent());
        Client.INSTANCE.getEventProtocol().register(new RenderSlotComponent());
        Client.INSTANCE.setConfigManager(new ConfigManager());
        ConfigManager.defaultConfig = new File(Minecraft.getMinecraft().mcDataDir + "/OnLooker/Config.json");
        Client.INSTANCE.getConfigManager().collectConfigs();
        if (ConfigManager.defaultConfig.exists()) {
            Client.INSTANCE.getConfigManager().loadConfig(Client.INSTANCE.getConfigManager().readConfigData(ConfigManager.defaultConfig.toPath()), true);
        }
        DragManager.loadDragData();
        Client.INSTANCE.setAltManager(new GuiAltManager());
        SplashScreen.drawSplashScreen(60);
    }

    @SafeVarargs
    private static void addModules(Class<? extends Module>... classes) {
        for (Class<? extends Module> moduleClass : classes) {
            try {
                modules.put(moduleClass, moduleClass.newInstance());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
