package dev.onlooker.module.impl.display;

import dev.onlooker.Client;
import dev.onlooker.event.impl.render.Render2DEvent;
import dev.onlooker.event.impl.render.ShaderEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.impl.combat.KillAura;
import dev.onlooker.module.impl.player.ChestStealer;
import dev.onlooker.module.impl.player.InvManager;
import dev.onlooker.module.settings.Setting;
import dev.onlooker.module.settings.impl.*;
import dev.onlooker.utils.font.AbstractFontRenderer;
import dev.onlooker.utils.font.CustomFont;
import dev.onlooker.utils.addons.fshShader.FshShaderRender;
import dev.onlooker.utils.render.*;
import dev.onlooker.utils.server.PingerUtils;
import dev.onlooker.utils.skidfont.FontManager;
import dev.onlooker.utils.tuples.Pair;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class HUDMod extends Module {
    public final StringSetting clientName = new StringSetting("Client Name");
    public static final ModeSetting watermarkMode = new ModeSetting("Watermark Mode", "OnLooker", "Novoline","OnLooker","Exhibition", "Moon", "Minecraft", "None");
    public static final ModeSetting watermarkTheme = new ModeSetting("OnLooker Theme","Light", "IOS", "Light", "Dark");
    public static final ModeSetting sound = new ModeSetting("Sound Mode", "Inertia", "Inertia", "Minecraft", "None");
    public static final ModeSetting info = new ModeSetting("Info Mode", "OnLooker", "Novoline", "OnLooker");
    public static final ModeSetting potion = new ModeSetting("Potion Mode", "OnLooker", "Novoline", "OnLooker");
    public static final ModeSetting inventorydisplay = new ModeSetting("Inventory Mode", "OnLooker", "Novoline", "OnLooker");
    public static final ColorSetting color1 = new ColorSetting("Color 1", new Color(0x7878FF));
    public static final ColorSetting color2 = new ColorSetting("Color 2", new Color(0x280A8D));public static final NumberSetting fadespeed = new NumberSetting("ColorSpeed", 2500, 5000, 200, 10);
    public static final ModeSetting theme = Theme.getModeSetting("Theme Selection", "OnLooker");

    private static final MultipleBoolSetting infoCustomization = new MultipleBoolSetting("Info Options",
            new BooleanSetting("White Info", false),
            new BooleanSetting("Info Shadow", true),
            new BooleanSetting("Client Info", true));

    public static final MultipleBoolSetting hudCustomization = new MultipleBoolSetting("HUD Options",
            new BooleanSetting("Radial Gradients", true),
            new BooleanSetting("Render Cape", true),
            new BooleanSetting("Lowercase", false));

    private static final MultipleBoolSetting disableButtons = new MultipleBoolSetting("Disable Buttons",
            new BooleanSetting("Disable KillAura", false),
            new BooleanSetting("Disable InvManager", false),
            new BooleanSetting("Disable ChestStealer", false));

    public HUDMod() {
        super("Interface", Category.DISPLAY, "customizes the client's appearance");
        Setting.addParent(watermarkMode, m -> m.is("OnLooker"), watermarkTheme);
        color1.addParent(theme, modeSetting -> modeSetting.is("Custom Theme"));
        color2.addParent(theme, modeSetting -> modeSetting.is("Custom Theme") && !color1.isRainbow());
        this.addSettings(clientName, watermarkMode, sound, watermarkTheme, info, potion, inventorydisplay, theme, color1, color2,fadespeed, infoCustomization, hudCustomization, disableButtons);
        if (!enabled) this.toggleSilent();
    }

    public static int offsetValue = 0;
    public static float xOffset = 0;

    @Override
    public void onShaderEvent(ShaderEvent e) {
        Pair<Color, Color> clientColors = HUDMod.getClientColors();
        String name = Client.NAME;
        if (!clientName.getString().isEmpty()) {
            name = clientName.getString().replace("%time%", getCurrentTimeStamp());
        }
        String client = name;
        switch (watermarkMode.getMode()) {
            case "Minecraft":
                String text3 = client + " | " + Minecraft.getDebugFPS() + " Fps";
                mc.fontRendererObj.drawStringWithShadow(text3, 5, 5,Color.BLACK);
                break;
            case "Moon":
                String text = client+" | " + Client.INSTANCE.getVersion() + " | " + HUDMod.mc.thePlayer.getName() + " | " + (mc.isSingleplayer() ? "SinglePlayer" : HUDMod.mc.getCurrentServerData().serverIP) + " | " + Minecraft.getDebugFPS() + " fps";
                float textW = tenacityBoldFont16.getStringWidth(text);
                tenacityBoldFont16.drawString(text, 12.0f, 14.0f, Color.BLACK);
                RoundedUtil.drawRound(8.0f, 9.0f, textW + 7.1f, 16.0f, 4.0f, clientColors.getFirst());
                break;
            case "OnLooker":
                String Atext1 = "OnLooker | Alpha";
                float AtextQ = FontManager.PingFang_bold16.getStringWidth(Atext1);
                RoundedUtil.drawCircle(5.0f, 4.0f, 27, 27.0f, 7.5f, this.getThemeCircleColor());
                RoundedUtil.drawRound(8.0f, 8.0f, AtextQ + 30.0f, 18.5f, 6.0f,this.getThemeShaderColor());
                break;
            case "Novoline":
                LocalTime currentTime = LocalTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                String formattedTime = currentTime.format(formatter);
                CustomFont font20 = museoSansBoldFont20;
                CustomFont font18 = museoSansFont14;
                String ping = PingerUtils.getPing() + " Ping";
                if (PingerUtils.getPing().equals("SinglePlayer")) ping = "SinglePlayer";
                String string = " §7| §f " + Client.INSTANCE.getVersion() + " §7| §f" + HUDMod.mc.thePlayer.getName() + " §7| §f" + ping + " §7| §f" + formattedTime;
                float clientw = font20.getStringWidth(client);
                float stringw = font18.getStringWidth(string);
                float aw = clientw + stringw;
                FshShaderRender.drawRoundedShadow(5.0f, 5.0f, aw + 11.0f, 18.0f, 4.0f, 1.0f, Color.BLACK.getRGB());
                RenderUtil.resetColor();
                GradientUtil.applyGradientHorizontal(10.0f, 10.0f, fr.getStringWidth(client), 18.0f, 1.0f, clientColors.getFirst(), clientColors.getSecond(), () -> {
                    RenderUtil.setAlphaLimit(0.0f);
                    font20.drawString(client, 9.0f, 13.0f, -1);
                });
                font18.drawString(string, clientw + 12.0f, 14.0f, -1);
                break;
            case "Exhibition":
                String text2 = "§fonl§rooker§f" + " - " + HUDMod.mc.thePlayer.getName() + " - " + PingerUtils.getPing() + "ms ";

                float x = 4.5f, y = 4.5f;

                int lineColor = new Color(59, 57, 57).darker().getRGB();
                Gui.drawRect2(x, y, tenacityFont16.getStringWidth(text2) + 7, 18.5, new Color(59, 57, 57).getRGB());

                Gui.drawRect2(x + 2.5, y + 2.5, tenacityFont16.getStringWidth(text2) + 2, 13, new Color(23, 23, 23).getRGB());

                // Top small bar
                Gui.drawRect2(x + 1, y + 1, tenacityFont16.getStringWidth(text2) + 5, .5, lineColor);

                // Bottom small bar
                Gui.drawRect2(x + 1, y + 17, tenacityFont16.getStringWidth(text2) + 5, .5, lineColor);

                // Left bar
                Gui.drawRect2(x + 1, y + 1.5, .5, 16, lineColor);

                // Right Bar
                Gui.drawRect2((x + 1.5) + tenacityFont16.getStringWidth(text2) + 4, y + 1.5, .5, 16, lineColor);

                // Lowly saturated rainbow bar

                // Bottom of the rainbow bar
                Gui.drawRect2(x + 2.5, y + 16, tenacityFont16.getStringWidth(text2) + 2, .5, lineColor);
                tenacityFont16.drawString(text2, x + 4.5f, y + 5.5f, clientColors.getSecond().getRGB());
                break;
        }
        RenderUtil.resetColor();
        this.drawBottomRight();
        RenderUtil.resetColor();
    }
    @Override
    public void onRender2DEvent(Render2DEvent e) {
        Pair<Color, Color> clientColors = getClientColors();
        String name = Client.NAME;
        if (!clientName.getString().isEmpty()) {
            name = clientName.getString().replace("%time%", getCurrentTimeStamp());
        }

        String client = name;
        switch (watermarkMode.getMode()) {
            case "Minecraft":
                String text3 = client + " | " + Minecraft.getDebugFPS() + " Fps";
                mc.fontRendererObj.drawStringWithShadow(text3, 5, 5, -1);
                break;
            case "Moon":
                String text = client + " | " + Client.INSTANCE.getVersion() + " | " + HUDMod.mc.thePlayer.getName() + " | " + (mc.isSingleplayer() ? "SinglePlayer" : HUDMod.mc.getCurrentServerData().serverIP) + " | " + Minecraft.getDebugFPS() + " fps";
                float textW = tenacityBoldFont16.getStringWidth(text);
                RoundedUtil.drawRound(8.0f, 9.0f, textW + 7.5f, 16.0f, 4.0f, new Color(0, 0, 0, 120));
                tenacityBoldFont16.drawString(text, 12.0f, 14.0f, -1);
                break;
            case "OnLooker":
                String Atext1 = "OnLooker | Alpha";
                float AtextQ = FontManager.PingFang_bold16.getStringWidth(Atext1);
                RoundedUtil.drawRound(8.0f, 8.0f, AtextQ + 30.0f, 18.5f, 6.0f, this.getThemeBgColor());
                RoundedUtil.drawCircle(5.0f, 4.0f, 27, 27.0f, 7.5f, this.getThemeCircleColor());
                iconFont20.drawString("q", 13.5f, 15.5f, this.getThemefontColor2());
                tenacityBoldFont18.drawString(Atext1, 29.0f, 13.5f, this.getThemefontColor());
                break;
            case "Novoline":
                LocalTime currentTime = LocalTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                String formattedTime = currentTime.format(formatter);
                CustomFont font20 = museoSansBoldFont20;
                CustomFont font18 = museoSansFont14;
                String ping = PingerUtils.getPing() + " Ping";
                if (PingerUtils.getPing().equals("SinglePlayer")) ping = "SinglePlayer";
                String string = " §7| §f " + Client.INSTANCE.getVersion() + " §7| §f" + HUDMod.mc.thePlayer.getName() + " §7| §f" + ping + " §7| §f" + formattedTime;
                float clientw = font20.getStringWidth(client);
                float stringw = font18.getStringWidth(string);
                float aw = clientw + stringw;
                FshShaderRender.drawRoundedShadow(5.0f, 5.0f, aw + 11.0f, 18.0f, 4.0f, 1.0f, new Color(0, 0, 0, 150).getRGB());
                RenderUtil.resetColor();
                for (int i = 0; i < client.length(); i++) {
                    font20.drawString(client, 9.0f, 13.0f, Theme.getColor((int)(200.0 + i * 20.0)));
                }
                font18.drawString(string, clientw + 12.0f, 14.0f, -1);
                break;
            case "Exhibition":
                String text2 = "§fonl§rooker§f" + " - " + HUDMod.mc.thePlayer.getName() + " - " + " - " + PingerUtils.getPing() + "ms ";

                float x = 4.5f, y = 4.5f;

                int lineColor = new Color(59, 57, 57).darker().getRGB();
                Gui.drawRect2(x, y, tenacityFont16.getStringWidth(text2) + 7, 18.5, new Color(59, 57, 57).getRGB());

                Gui.drawRect2(x + 2.5, y + 2.5, tenacityFont16.getStringWidth(text2) + 2, 13, new Color(23, 23, 23).getRGB());

                // Top small bar
                Gui.drawRect2(x + 1, y + 1, tenacityFont16.getStringWidth(text2) + 5, .5, lineColor);

                // Bottom small bar
                Gui.drawRect2(x + 1, y + 17, tenacityFont16.getStringWidth(text2) + 5, .5, lineColor);

                // Left bar
                Gui.drawRect2(x + 1, y + 1.5, .5, 16, lineColor);

                // Right Bar
                Gui.drawRect2((x + 1.5) + tenacityFont16.getStringWidth(text2) + 4, y + 1.5, .5, 16, lineColor);

                // Lowly saturated rainbow bar

                GradientUtil.drawGradientLR(x + 2.5f, y + 14.5f, tenacityFont16.getStringWidth(text2) + 2, 1, 1, clientColors.getFirst(), clientColors.getSecond());

                // Bottom of the rainbow bar
                Gui.drawRect2(x + 2.5, y + 16, tenacityFont16.getStringWidth(text2) + 2, .5, lineColor);
                tenacityFont16.drawString(text2, x + 4.5f, y + 5.5f, clientColors.getSecond().getRGB());
                break;
        }
        RenderUtil.resetColor();
        this.drawBottomRight();
        RenderUtil.resetColor();
    }

    private void drawBottomRight() {
        boolean whiteInfo = infoCustomization.isEnabled("White Info");
        AbstractFontRenderer fr = tenacityBoldFont18 ;
        ScaledResolution sr = new ScaledResolution(mc);
        float yOffset = (float)(12.5 * (double)GuiChat.openingAnimation.getOutput().floatValue());
        if (infoCustomization.isEnabled("Client Info")) {
            String text;{
                text = Client.INSTANCE.getVersion() + " | " + "§r" + " | " + Client.RELEASE;
            }
            text = HUDMod.get(text);
            float x = (float)sr.getScaledWidth() - (fr.getStringWidth(text) + 3.0f);
            float y = (float)(sr.getScaledHeight() - (fr.getHeight() + 3)) - yOffset;
            String finalText = text;
            float f =  1.0f;
            fr.drawString(finalText, x + f, y + f, -16777216);
            if (whiteInfo) {
                fr.drawString(finalText, x, y, -1);
            } else {
                GradientUtil.applyGradientHorizontal(x, y, fr.getStringWidth(text), 20.0f, 1.0f, HUDMod.getColor(0), HUDMod.getColor(500), () -> {
                    RenderUtil.setAlphaLimit(0.0f);
                    fr.drawString(finalText, x, y, -1);
                });
            }
        }
    }

    public static Pair<Color, Color> getClientColors() {
        return Theme.getThemeColors(theme.getMode());
    }
    public static Color getColor(int offset) {
        return ColorUtil.getColor(HUDMod.getClientColors().getFirst(),HUDMod.getClientColors().getSecond(), 2500, offset);
    }

    public static String getCurrentTimeStamp() {
        return new SimpleDateFormat("h:mm a").format(new Date());
    }

    public static String get(String text) {
        return hudCustomization.getSetting("Lowercase").isEnabled() ? text.toLowerCase() : text;
    }

    public static boolean isRainbowTheme() {return theme.is("Custom Theme") && color1.isRainbow();}
    public static boolean drawRadialGradients() {return hudCustomization.getSetting("Radial Gradients").isEnabled();}

    public static void addButtons(List<GuiButton> buttonList) {
        for (ModuleButton mb : ModuleButton.values()) {
            if (!mb.getSetting().isEnabled()) continue;
            buttonList.add(mb.getButton());
        }
    }

    public static void updateButtonStatus() {
        for (ModuleButton mb : ModuleButton.values()) {
            mb.getButton().enabled = Client.INSTANCE.getModuleCollection().getModule(mb.getModule()).isEnabled();
        }
    }

    public static void handleActionPerformed(GuiButton button) {
        for (ModuleButton mb : ModuleButton.values()) {
            if (mb.getButton() != button) continue;
            Module m = Client.INSTANCE.getModuleCollection().getModule(mb.getModule());
            if (!m.isEnabled()) break;
            m.toggle();
            break;
        }
    }

    static /* synthetic */ MultipleBoolSetting access$000() {
        return disableButtons;
    }

    private Color bg,font;
    public  Color getThemeBgColor() {
        switch (watermarkTheme.getMode()) {
            case "Light":
                return bg = new Color(205, 205, 205, 60);
            case "Dark":
                return bg = new Color(30, 30, 30, 80);
            case "IOS":
                return bg = new Color(225, 225, 225, 100);
        }
        return bg;
    }
    public Color getThemefontColor() {
        switch (watermarkTheme.getMode()) {
            case "Light":
                return font = new Color(255, 255, 255, 180);
            case "Dark":
                return font = new Color(255, 255, 255, 190);
            case "IOS":
                return font = new Color(10, 10, 10, 220);
        }
        return font;
    }
    public Color getThemefontColor2() {
        switch (watermarkTheme.getMode()) {
            case "Dark":
                return font = new Color(35, 35, 35, 122);
            case "Light":
                return font = new Color(255, 255, 255, 180);
            case "IOS":
                return font = new Color(13, 13, 13, 230);
        }
        return font;
    }
    public Color getThemeCircleColor() {
        switch (watermarkTheme.getMode()) {
            case "Light":
                return font = new Color(35, 35, 35, 25);
            case "Dark":
            case "IOS":
                return font = new Color(255, 255, 255, 80);
        }
        return font;
    }
    public Color getThemeShaderColor() {
        switch (watermarkTheme.getMode()) {
            case "Dark":
                return font = new Color(55, 55, 55);
            case "Light":
                return font = new Color(135, 135, 135);
            case "IOS":
                return font = new Color(255, 255, 255);
        }
        return font;
    }

    @Getter
    public enum ModuleButton {
        AURA(KillAura.class, HUDMod.access$000().getSetting("Disable KillAura"), new GuiButton(2461, 3, 4, 120, 20, "Disable KillAura")),
        INVMANAGER(InvManager.class, HUDMod.access$000().getSetting("Disable InvManager"), new GuiButton(2462, 3, 26, 120, 20, "Disable InvManager")),
        CHESTSTEALER(ChestStealer.class, HUDMod.access$000().getSetting("Disable ChestStealer"), new GuiButton(2463, 3, 48, 120, 20, "Disable ChestStealer"));

        private final Class<? extends Module> module;
        private final BooleanSetting setting;
        private final GuiButton button;

        ModuleButton(Class<? extends Module> module, BooleanSetting setting, GuiButton button) {
            this.module = module;
            this.setting = setting;
            this.button = button;
        }
    }
}