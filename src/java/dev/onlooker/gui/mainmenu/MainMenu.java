package dev.onlooker.gui.mainmenu;

import dev.onlooker.Client;
import dev.onlooker.gui.mainmenu.button.MenuButton;
import dev.onlooker.utils.Utils;
import dev.onlooker.utils.animations.*;
import dev.onlooker.utils.animations.impl.DecelerateAnimation;
import dev.onlooker.utils.animations.impl.RippleAnimation;
import dev.onlooker.utils.client.RegionalAbuseUtil;
import dev.onlooker.utils.render.RenderUtil;
import dev.onlooker.utils.render.RoundedUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.src.Config;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static dev.onlooker.Client.RELEASE;

public class MainMenu extends GuiScreen {
    public boolean hoverSwitch;
    public boolean hoverFlushed;
    double anim, anim2, anim3 = 0;
    public static boolean logined = false;
    private final FshShader backgroundShader;
    public RippleAnimation a3 = new RippleAnimation();
    private final long initTime = System.currentTimeMillis();
    public static final String location = RegionalAbuseUtil.country;
    public Animation a = new DecelerateAnimation(500, 1.0);
    public Animation a2 = new DecelerateAnimation(500, 1.0);
    private final List<MenuButton> buttons = Arrays.asList(
            new MenuButton("K"),
            new MenuButton("L"),
            new MenuButton("M"),
            new MenuButton("N"),
            new MenuButton("O"));
    public MainMenu() {
        try {
            this.backgroundShader = new FshShader("/assets/minecraft/OnLooker/Shaders/fragment/shader.fsh");
        }
        catch (IOException e) {
            throw new IllegalStateException("Failed to load backgound shader", e);
        }
        RegionalAbuseUtil.getAddressByIP();
        AnimationRise fadeAnimation = new AnimationRise(Easing.LINEAR, 4000);
        fadeAnimation.run(1);
    }

    @Override
    public void initGui() {
        this.buttons.forEach(MenuButton::initGui);
    }

    public void useShaderToyBackground(int scale) {
        GlStateManager.disableCull();
        backgroundShader.useShader(this.width * scale, this.height * scale, (System.currentTimeMillis() - initTime) / 1000.0f);
        GL11.glBegin(7);
        GL11.glVertex2d(-1.0, -1.0);
        GL11.glVertex2d(-1.0, 1.0);
        GL11.glVertex2d(1.0, 1.0);
        GL11.glVertex2d(1.0, -1.0);
        GL11.glEnd();
        GL20.glUseProgram(0);
        GlStateManager.enableAlpha();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Display.setTitle(Client.NAME + " " + Client.INSTANCE.getVersion() + " - "+ RegionalAbuseUtil.country);
        int w = new ScaledResolution(this.mc).getScaledWidth();
        anim = AnimationUtil.animate((float) w, (float) anim, 6.0f / Minecraft.getDebugFPS());
        anim3 = AnimationUtil.animate((float) w, (float) anim3, 5.5f / Minecraft.getDebugFPS());
        anim2 = AnimationUtil.animate((float) w, (float) anim2, 4.0f / Minecraft.getDebugFPS());
        Welcome.drawRect(-10, -10, anim, height + 10, new Color(203, 50, 255).getRGB());
        Welcome.drawRect(-10, -10, anim3, height + 10, new Color(0, 217, 255).getRGB());
        Welcome.drawRect(-10, -10, anim2, height + 10, new Color(47, 47, 47).getRGB());


        useShaderToyBackground(2);

        this.a.setDirection(this.hoverSwitch ? Direction.FORWARDS : Direction.BACKWARDS);
        this.a2.setDirection(this.hoverFlushed ? Direction.FORWARDS : Direction.BACKWARDS);

        RenderUtil.scissorEnd();
        float buttonWidth = 30;
        float buttonHeight = 25;
        RoundedUtil.drawRound(this.width / 2f - 45.0f * 2.45f, this.height / 2.0F - 2.0f, buttonWidth + 178, buttonHeight, 2.0f, new Color(0,0,0,105));

        int count = 0;
        for (MenuButton button : buttons) {
            button.x = (float) this.width / 2f - 45.5f * 2.45f + count * 1.50f;
            button.y = this.height / 2f;
            button.width = buttonWidth;
            button.height = buttonHeight;
            button.clickAction = () -> {
                switch (button.text) {
                    case "K": {
                        Utils.mc.displayGuiScreen(new GuiSelectWorld(this));
                        break;
                    }
                    case "L": {
                        Utils.mc.displayGuiScreen(new GuiMultiplayer(this));
                        break;
                    }
                    case "N": {
                        Utils.mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                        break;
                    }
                    case "M": {
                        mc.displayGuiScreen(Client.INSTANCE.getAltManager());
                        break;
                    }
                    case "O": {
                        Utils.mc.shutdown();
                    }
                }
            };
            button.drawScreen(mouseX, mouseY);
            count = (int) ((float) count + (buttonHeight + 5.0f));
        }
        super.drawScreen(mouseX, mouseY, partialTicks);

        //客户端信息
        RoundedUtil.drawRound(this.width / 2f - 45.0f * 0.55f, this.height / 2.0F - 44.0f, buttonWidth + 8, buttonHeight + 12, 2.0f, new Color(0,0,0,105));
        iconFont68.drawStringWithShadow("q",this.width / 2f - 45.0f * 0.5f, this.height / 2.0F - 40.5f, new Color(255, 255, 255).getRGB());
        if (Objects.equals(RELEASE.getName(), "Developer")) {
            tenacityFont16.drawString(Client.NAME + " " + Client.VERSION + " §4(Developer Build)", 10, height - 35, new Color(255, 255, 255, 100).getRGB());
        } else {
            if (Objects.equals(RELEASE.getName(), "Alpha")) {
                tenacityFont16.drawString(Client.NAME + " " + Client.VERSION + " §c(Alpha Build)", 10, height - 35, new Color(255, 255, 255, 100).getRGB());
            } else  {
                tenacityFont16.drawString(Client.NAME + " " + Client.VERSION + " §a(Latest)", 10, height - 35, new Color(255, 255, 255, 100).getRGB());
            }
        }

        tenacityFont16.drawString("Copyright Mojang AB. Do not distribute!", 10, height - 25, new Color(255, 255, 255, 100).getRGB());
        tenacityFont16.drawString("Minecraft 1.8.9 (" + Config.getVersion() + ")", 10, height - 15, new Color(255, 255, 255, 100).getRGB());
        tenacityFont16.drawString("Welcome, " + RELEASE.getName(), width - tenacityFont16.getStringWidth("Welcome, " + RELEASE.getName()) - 8, height - 25, new Color(255, 255, 255, 100).getRGB());
        tenacityFont16.drawString(Client.CREDIT, width - tenacityFont16.getStringWidth(Client.CREDIT) - 8, height - 14, new Color(255, 255, 255, 100).getRGB());
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.buttons.forEach(button -> button.mouseClicked(mouseX, mouseY, mouseButton));
        this.a3.mouseClicked(mouseX, mouseY);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}


