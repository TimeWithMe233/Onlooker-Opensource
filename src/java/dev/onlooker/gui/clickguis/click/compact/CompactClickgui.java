package dev.onlooker.gui.clickguis.click.compact;

import dev.onlooker.Client;
import dev.onlooker.gui.clickguis.click.compact.impl.ModuleRect;
import dev.onlooker.gui.clickguis.searchbar.SearchBar;
import dev.onlooker.gui.clickguis.sidegui.SideGUI;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.api.ModuleCollection;
import dev.onlooker.module.impl.display.HUDMod;
import dev.onlooker.module.impl.movement.InventoryMove;
import dev.onlooker.module.impl.display.ClickGUIMod;
import dev.onlooker.module.settings.Setting;
import dev.onlooker.utils.animations.Animation;
import dev.onlooker.utils.animations.Direction;
import dev.onlooker.utils.animations.impl.DecelerateAnimation;
import dev.onlooker.utils.font.FontUtil;
import dev.onlooker.utils.misc.HoveringUtil;
import dev.onlooker.utils.misc.IOUtils;
import dev.onlooker.utils.objects.DiscordAccount;
import dev.onlooker.utils.objects.Drag;
import dev.onlooker.utils.render.ColorUtil;
import dev.onlooker.utils.render.RenderUtil;
import dev.onlooker.utils.render.RoundedUtil;
import dev.onlooker.utils.render.StencilUtil;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class CompactClickgui extends GuiScreen {

    private final Animation openingAnimation = new DecelerateAnimation(250, 1);
    private final Drag drag = new Drag(40, 40);
    private final ModulePanel modulePanel = new ModulePanel();
    public Color categoryColor = new Color(30, 31, 35,80);
    public Color categoryColor2 = new Color(30, 31, 35,80);
    private float rectWidth = 400;
    private float rectHeight = 300;
    public boolean typing;
    private HashMap<Category, ArrayList<ModuleRect>> moduleRects;


    @Override
    public void onDrag(int mouseX, int mouseY) {
        boolean focusedConfigGui = Client.INSTANCE.getSideGui().isFocused();
        int fakeMouseX = focusedConfigGui ? 0 : mouseX, fakeMouseY = focusedConfigGui ? 0 : mouseY;

        drag.onDraw(fakeMouseX, fakeMouseY);
        Client.INSTANCE.getSideGui().onDrag(mouseX, mouseY);
    }

    @Override
    public void initGui() {
        openingAnimation.setDirection(Direction.FORWARDS);
        rectWidth = 500;
        rectHeight = 350;
        if (moduleRects != null) {
            moduleRects.forEach((cat, list) -> list.forEach(ModuleRect::initGui));
        }
        modulePanel.initGui();
        Client.INSTANCE.getSideGui().initGui();
    }

    public void bloom() {
        float x = drag.getX(), y = drag.getY();
        if (!openingAnimation.isDone()) {
            x -= width + rectWidth / 2f;
            x += (width + rectWidth / 2f) * openingAnimation.getOutput().floatValue();
        }
        Gui.drawRect2(x, y, rectWidth, rectHeight, new Color(20, 20, 20).getRGB());

    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) {
            if (Client.INSTANCE.getSearchBar().isFocused()) {
                Client.INSTANCE.getSearchBar().getSearchField().setText("");
                Client.INSTANCE.getSearchBar().getSearchField().setFocused(false);
                return;
            }

            if (Client.INSTANCE.getSideGui().isFocused()) {
                Client.INSTANCE.getSideGui().setFocused(false);
                return;
            }
            
            openingAnimation.setDirection(Direction.BACKWARDS);
        }
        modulePanel.keyTyped(typedChar, keyCode);
        Client.INSTANCE.getSideGui().keyTyped(typedChar, keyCode);
        Client.INSTANCE.getSearchBar().keyTyped(typedChar, keyCode);
    }


    private Color firstColor = Color.BLACK, secondColor = Color.BLACK;

    private final List<ModuleRect> searchResults = new ArrayList<>();
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (HUDMod.watermarkTheme.is("Light")) {
            categoryColor = new Color(205, 205, 205, 40);
            categoryColor2 = new Color(205, 205, 205, 100);
        }else {
            categoryColor = new Color(0, 0, 0, 40);
            categoryColor2 = new Color(0, 0, 0, 100);
        }
        if (ModuleCollection.reloadModules || moduleRects == null) {
            if (moduleRects == null) {
                moduleRects = new HashMap<>();
            } else moduleRects.clear();
            for (Category category : Category.values()) {
                ArrayList<ModuleRect> modules = new ArrayList<>();
                for (Module module : Client.INSTANCE.getModuleCollection().getModulesInCategory(category)) {
                    modules.add(new ModuleRect(module));
                }

                moduleRects.put(category, modules);
            }
            moduleRects.forEach((cat, list) -> list.forEach(ModuleRect::initGui));
            ModuleCollection.reloadModules = false;
            return;
        }



        typing = modulePanel.typing || (Client.INSTANCE.getSideGui().isFocused() && Client.INSTANCE.getSideGui().isTyping()) || Client.INSTANCE.getSearchBar().isTyping();

        if (ClickGUIMod.walk.isEnabled() && !typing) {
            InventoryMove.updateStates();
        }

        boolean focusedConfigGui = Client.INSTANCE.getSideGui().isFocused();
        int fakeMouseX = focusedConfigGui ? 0 : mouseX, fakeMouseY = focusedConfigGui ? 0 : mouseY;

        float x = drag.getX(), y = drag.getY();

        if (!openingAnimation.isDone()) {
            x -= width + rectWidth / 2f;
            x += (width + rectWidth / 2f) * openingAnimation.getOutput().floatValue();
        } else if (openingAnimation.getDirection().equals(Direction.BACKWARDS)) {
            mc.displayGuiScreen(null);
            return;
        }

        rectWidth = 475;
        rectHeight = 300;


        Gui.drawRect2(x, y, rectWidth, rectHeight, categoryColor.getRGB());


        Gui.drawRect2(x, y, 90, rectHeight, categoryColor2.getRGB());

        GlStateManager.color(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_BLEND);
        mc.getTextureManager().bindTexture(new ResourceLocation("OnLooker/41.png"));
        Gui.drawModalRectWithCustomSizedTexture(x + 5, y + 5, 0, 0, 20.5f, 20.5f, 20.5f, 20.5f);

        FontUtil.tenacityBoldFont22.drawString(Client.NAME, x + 33, y + 7, -1);
        FontUtil.tenacityFont16.drawCenteredString(Client.VERSION,
                (float) (x + 31 + FontUtil.tenacityBoldFont22.getStringWidth(Client.NAME) / 2f), y + 19, -1);

        boolean searching = Client.INSTANCE.getSearchBar().isFocused();

        float bannerHeight = 75 / 2f;
        Gui.drawRect2(x + 5, y + 31, 80, .5, categoryColor2.getRGB());

        Gui.drawRect2(x + 5, y + rectHeight - (bannerHeight + 3), 80, .5,categoryColor2.getRGB());


        if (Client.INSTANCE.getDiscordAccount() != null) {
            DiscordAccount discordAccount = Client.INSTANCE.getDiscordAccount();
            float avatarSize = 40 / 2f;
            float bannerWidth = 180 / 2f;
            boolean hoveringDiscord = HoveringUtil.isHovering(x, y + rectHeight - bannerHeight, bannerWidth, bannerHeight, fakeMouseX, fakeMouseY);

            //Drawing the banner and if they dont have a banner then we draw a rect with their banner color
            if (discordAccount.discordBanner != null) {
                float alpha = 50 + (float) (hoveringDiscord ? 120 : 0);
                //Draw discord stuff
                mc.getTextureManager().bindTexture(discordAccount.discordBanner);
                //GlStateManager.color(1, 1, 1);
                GlStateManager.color(1, 1, 1, alpha / 255f);
                GL11.glEnable(GL11.GL_BLEND);
                Gui.drawModalRectWithCustomSizedTexture(x, y + rectHeight - (bannerHeight), 0, 0, bannerWidth, bannerHeight, bannerWidth, bannerHeight);
            } else {
                String stringBuilder = "ff" + discordAccount.bannerColor;
                int integer = (int) Long.parseLong(stringBuilder, 16);
                GlStateManager.color(1, 1, 1, 1);
                float alpha = hoveringDiscord ? 170 : 0;
                Gui.drawRect2(x, y + rectHeight - bannerHeight, bannerWidth, bannerHeight, ColorUtil.applyOpacity(integer, alpha / 255f));
            }


            //Drawing the avatar
            mc.getTextureManager().bindTexture(discordAccount.discordAvatar);
            RoundedUtil.drawRoundTextured(x + 5, y + rectHeight - (avatarSize + 15), avatarSize, avatarSize, 10, 1);


            String discordTag = Minecraft.getMinecraft().session.getUsername() + "#1337";

            float middleY = (y + rectHeight - (avatarSize - 9));

            FontUtil.tenacityBoldFont18.drawString(discordTag.substring(0, discordTag.indexOf("#")), x + 5,
                    middleY, -1);

            //Move the tag around if the username is too long
            Color tagColor = hoveringDiscord ? ColorUtil.tripleColor(175) : Color.WHITE;
            if ((discordTag.length() - 4) > 13) {
                FontUtil.tenacityFont14.drawString(discordTag.substring(discordTag.indexOf("#")),
                        x + 5 + FontUtil.tenacityBoldFont18.getStringWidth(discordTag.substring(0, discordTag.indexOf("#"))) -
                                FontUtil.tenacityFont14.getStringWidth(discordTag.substring(discordTag.indexOf("#"))),
                        middleY - 6, tagColor);
            } else {
                FontUtil.tenacityFont14.drawString(discordTag.substring(discordTag.indexOf("#")),
                        x + 5 + FontUtil.tenacityBoldFont18.getStringWidth(discordTag.substring(0, discordTag.indexOf("#"))),
                        middleY + 2, tagColor);
            }
        }


        float minus = (bannerHeight + 3) + 33;
        ClickGUIMod clickGUIMod = Client.INSTANCE.getModuleCollection().getModule(ClickGUIMod.class);
        float catHeight = ((rectHeight - minus) / (Category.values().length));
        float seperation = 0;
        for (Category category : Category.values()) {
            float catY = y + 33 + seperation;
            boolean hovering = HoveringUtil.isHovering(x, catY + 8, 90, catHeight - 16, fakeMouseX, fakeMouseY);

            Color categoryColor = hovering ? categoryColor2 : categoryColor2;
            Color selectColor = (clickGUIMod.getActiveCategory() == category) ? Color.WHITE : categoryColor;

            if(!searching && (clickGUIMod.getActiveCategory() == category)) {
                Gui.drawRect2(x, catY, 90, catHeight, categoryColor.getRGB());
            }

            RenderUtil.resetColor();
            tenacityBoldFont22.drawString(category.name, x + 8, catY + tenacityFont22.getMiddleOfBox(catHeight), selectColor.getRGB());
            RenderUtil.resetColor();
            seperation += catHeight;
        }

        modulePanel.currentCat = searching ? null : clickGUIMod.getActiveCategory();
        modulePanel.moduleRects = getModuleRects(clickGUIMod.getActiveCategory());
        modulePanel.x = x;
        modulePanel.y = y;
        modulePanel.rectHeight = rectHeight;
        modulePanel.rectWidth = rectWidth;

        StencilUtil.initStencilToWrite();
        Gui.drawRect2(x, y, rectWidth, rectHeight, -1);
        StencilUtil.readStencilBuffer(1);
        modulePanel.drawScreen(fakeMouseX, fakeMouseY);
        StencilUtil.uninitStencilBuffer();

        modulePanel.drawTooltips(fakeMouseX, fakeMouseY);

        SideGUI sideGUI = Client.INSTANCE.getSideGui();
        sideGUI.getOpenAnimation().setDirection(openingAnimation.getDirection());
        sideGUI.drawScreen(mouseX, mouseY);

        SearchBar searchBar = Client.INSTANCE.getSearchBar();
        searchBar.setAlpha(openingAnimation.getOutput().floatValue() * (1 - sideGUI.getClickAnimation().getOutput().floatValue()));
        searchBar.drawScreen(fakeMouseX, fakeMouseY);

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!Client.INSTANCE.getSideGui().isFocused()) {
            drag.onClick(mouseX, mouseY, mouseButton, HoveringUtil.isHovering(drag.getX(), drag.getY(), rectWidth, 10, mouseX, mouseY));
            float bannerWidth = 180 / 2f;
            float bannerHeight = 75 / 2f;

            ClickGUIMod clickGUIMod = Client.INSTANCE.getModuleCollection().getModule(ClickGUIMod.class);

            //If hovering the discord thing lol
            if (HoveringUtil.isHovering(drag.getX(), drag.getY() + rectHeight - bannerHeight, bannerWidth, bannerHeight, mouseX, mouseY)) {
                if (RandomUtils.nextBoolean()) {
                    IOUtils.openLink("https://www.youtube.com/channel/UC2tPaPIMGeDETMTr1FQuMSA?sub_confirmation=1");
                } else {
                    IOUtils.openLink("https://www.youtube.com/channel/UCC5eswf_s4GMyH4W-K0RUuA?sub_confirmation=1");
                }
            }

            int separation = 0;
            float minus = (bannerHeight + 3) + 33;
            float catHeight = ((rectHeight - minus) / (Category.values().length));
            for (Category category : Category.values()) {
                float catY = drag.getY() + 33 + separation;
                boolean hovering = HoveringUtil.isHovering(drag.getX(), catY + 8, 90, catHeight - 16, mouseX, mouseY);
                if (hovering) {
                    clickGUIMod.setActiveCategory(category);
                }
                separation += catHeight;
            }

            modulePanel.mouseClicked(mouseX, mouseY, mouseButton);
            Client.INSTANCE.getSearchBar().mouseClicked(mouseX, mouseY, mouseButton);
        }
        Client.INSTANCE.getSideGui().mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (!Client.INSTANCE.getSideGui().isFocused()) {
            drag.onRelease(state);
            modulePanel.mouseReleased(mouseX, mouseY, state);
        }
        Client.INSTANCE.getSideGui().mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private final List<String> searchTerms = new ArrayList<>();
    private String searchText;

    public List<ModuleRect> getModuleRects(Category category) {
        if (!Client.INSTANCE.getSearchBar().isFocused()) {
            return moduleRects.get(category);
        }

        String search = Client.INSTANCE.getSearchBar().getSearchField().getText();

        if (search.equals(searchText)) {
            return searchResults;
        } else {
            searchText = search;
        }

        List<ModuleRect> moduleRects1 = moduleRects.values().stream().flatMap(Collection::stream).collect(Collectors.toList());

        searchResults.clear();
        moduleRects1.forEach(moduleRect -> {
            searchTerms.clear();
            Module module = moduleRect.module;

            searchTerms.add(module.getName());
            searchTerms.add(module.getCategory().name);
            if (!module.getAuthor().isEmpty()) {
                searchTerms.add(module.getAuthor());
            }
            for (Setting setting : module.getSettingsList()) {
                searchTerms.add(setting.name);
            }

            moduleRect.setSearchScore(FuzzySearch.extractOne(search, searchTerms).getScore());
        });

        searchResults.addAll(moduleRects1.stream().filter(moduleRect -> moduleRect.getSearchScore() > 60)
                .sorted(Comparator.comparingInt(ModuleRect::getSearchScore).reversed()).collect(Collectors.toList()));

        return searchResults;
    }
}
