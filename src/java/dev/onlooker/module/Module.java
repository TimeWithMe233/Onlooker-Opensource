package dev.onlooker.module;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import dev.onlooker.Client;
import dev.onlooker.config.ConfigSetting;
import dev.onlooker.event.ListenerAdapter;
import dev.onlooker.gui.notifications.NotificationManager;
import dev.onlooker.gui.notifications.NotificationType;
import dev.onlooker.module.impl.display.HUDMod;
import dev.onlooker.module.impl.display.NotificationsMod;
import dev.onlooker.module.settings.Setting;
import dev.onlooker.module.settings.impl.*;
import dev.onlooker.utils.Utils;
import dev.onlooker.utils.animations.Animation;
import dev.onlooker.utils.animations.Direction;
import dev.onlooker.utils.animations.impl.DecelerateAnimation;
import dev.onlooker.utils.misc.SoundUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
public class Module extends ListenerAdapter implements Utils {
    @Expose
    @SerializedName("name")
    private final String name;
    private final String description;
    private final Category category;
    private final CopyOnWriteArrayList<Setting> settingsList = new CopyOnWriteArrayList<>();
    private String suffix;
    private String author = "";

    @Expose
    @SerializedName("toggled")
    protected boolean enabled;
    @Expose
    @SerializedName("settings")
    public ConfigSetting[] cfgSettings;


    private boolean expanded;
    private final Animation animation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);

    public static int categoryCount;
    public static float allowedClickGuiHeight = 300;
    public static boolean scaffoldshouldstop = false;
    private final KeybindSetting keybind = new KeybindSetting(Keyboard.KEY_NONE);

    public Module(String name, Category category, String description) {
        this.name = name;
        this.category = category;
        this.description = description;
        addSettings(keybind);
    }

    public boolean isInGame() {
        return mc.theWorld != null && mc.thePlayer != null;
    }

    public void addSettings(Setting... settings) {
        settingsList.addAll(Arrays.asList(settings));
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void setToggled(boolean toggled) {
        this.enabled = toggled;
        if (toggled) {
            this.onEnable();
        } else {
            this.onDisable();
        }
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void toggleSilent() {
        this.enabled = !this.enabled;
        if (this.enabled) {
            this.onEnable();
        } else {
            this.onDisable();
        }
    }
    public boolean isNull() {
        return mc.thePlayer == null && mc.theWorld == null;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void toggleSilent(boolean toggled) {
        String titleToggle = " ";
        String descriptionToggleOn = this.getName() + " §aEnabled\r";
        String descriptionToggleOff = this.getName() + " §cDisabled\r";
        this.enabled = toggled;
        if (enabled) {
            if (HUDMod.sound.is("Minecraft")) {
                mc.thePlayer.playSound("random.click", 1f, 1f);
            }
            if (HUDMod.sound.is("Inertia")) {
                SoundUtils.playSound(new ResourceLocation("OnLooker/Sounds/enable.wav"), 1.0f);
            }
            NotificationManager.post(NotificationType.SUCCESS, titleToggle, descriptionToggleOn);
        } else {
            if (HUDMod.sound.is("Minecraft")) {
                mc.thePlayer.playSound("random.click", 1f, 0.4f);
            }
            if (HUDMod.sound.is("Inertia")) {
                SoundUtils.playSound(new ResourceLocation("OnLooker/Sounds/disable.wav"), 1.0f);
            }
            NotificationManager.post(NotificationType.DISABLE, titleToggle, descriptionToggleOff);
        }
    }

    //TODO: wtf is this code.
    @Exclude(Strategy.NAME_REMAPPING)
    public void toggle() {
        toggleSilent();
        if (NotificationsMod.toggleNotifications.isEnabled()) {
            String titleToggle = " ";
            String descriptionToggleOn = this.getName() + " §aEnabled";
            String descriptionToggleOff = this.getName() + " §cDisabled";

            switch (NotificationsMod.mode.getMode()) {
                case "Default":
                    if (NotificationsMod.onlyTitle.isEnabled()) titleToggle = this.getName() + " toggled";
                    break;
                case "OnLooker":
                    descriptionToggleOn = this.getName() + " §aEnabled";
                    descriptionToggleOff = this.getName() + " §cDisabled";
                    if (NotificationsMod.onlyTitle.isEnabled()) titleToggle = this.getName() + " toggled";
                    break;
            }
            if (enabled) {
                if (HUDMod.sound.is("Minecraft")) {
                    mc.thePlayer.playSound("random.click", 1f, 1f);
                }
                if (HUDMod.sound.is("Inertia")) {
                    SoundUtils.playSound(new ResourceLocation("OnLooker/Sounds/enable.wav"), 1.0f);
                }
                NotificationManager.post(NotificationType.SUCCESS, titleToggle, descriptionToggleOn);
            } else {
                if (HUDMod.sound.is("Minecraft")) {
                    mc.thePlayer.playSound("random.click", 1f, 0.4f);
                }
                if (HUDMod.sound.is("Inertia")) {
                    SoundUtils.playSound(new ResourceLocation("OnLooker/Sounds/disable.wav"), 1.0f);
                }
                NotificationManager.post(NotificationType.DISABLE, titleToggle, descriptionToggleOff);
            }
        }
    }

    public boolean hasMode() {
        return suffix != null;
    }


    public void onEnable() {
        Client.INSTANCE.getEventProtocol().register(this);
    }

    public void onDisable() {

        Client.INSTANCE.getEventProtocol().unregister(this);

    }

    @Exclude(Strategy.NAME_REMAPPING)
    public void setKey(int code) {
        this.keybind.setCode(code);
    }


    @Exclude(Strategy.NAME_REMAPPING)
    public String getName() {
        return name;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public String getDescription() {
        return description;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public boolean isEnabled() {
        return enabled;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public int getKeybindCode() {
        return keybind.getCode();
    }


    @Exclude(Strategy.NAME_REMAPPING)
    public NumberSetting getNumberSetting(String name) {
        for (Setting setting : settingsList) {
            if (setting instanceof NumberSetting && setting.getName().equalsIgnoreCase(name)) {
                return (NumberSetting) setting;
            }
        }
        return null;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public BooleanSetting getBooleanSetting(String name) {
        for (Setting setting : settingsList) {
            if (setting instanceof BooleanSetting && setting.getName().equalsIgnoreCase(name)) {
                return (BooleanSetting) setting;
            }
        }
        return null;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public ModeSetting getModeSetting(String name) {
        for (Setting setting : settingsList) {
            if (setting instanceof ModeSetting && setting.getName().equalsIgnoreCase(name)) {
                return (ModeSetting) setting;
            }
        }
        return null;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public StringSetting getStringSetting(String name) {
        for (Setting setting : settingsList) {
            if (setting instanceof StringSetting && setting.getName().equalsIgnoreCase(name)) {
                return (StringSetting) setting;
            }
        }
        return null;
    }

    @Exclude(Strategy.NAME_REMAPPING)
    public MultipleBoolSetting getMultiBoolSetting(String name) {
        for (Setting setting : settingsList) {
            if (setting instanceof MultipleBoolSetting && setting.getName().equalsIgnoreCase(name)) {
                return (MultipleBoolSetting) setting;
            }
        }
        return null;
    }


    @Exclude(Strategy.NAME_REMAPPING)
    public ColorSetting getColorSetting(String name) {
        for (Setting setting : settingsList) {
            if (setting instanceof ColorSetting && setting.getName().equalsIgnoreCase(name)) {
                return (ColorSetting) setting;
            }
        }
        return null;
    }

}