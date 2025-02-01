package dev.onlooker.gui.clickguis.click.dropdown;

import dev.onlooker.Client;
import dev.onlooker.gui.Screen;
import dev.onlooker.gui.clickguis.click.dropdown.components.ModuleRect;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.impl.display.HUDMod;
import dev.onlooker.module.impl.display.ClickGUIMod;
import dev.onlooker.module.settings.Setting;
import dev.onlooker.utils.Utils;
import dev.onlooker.utils.animations.Animation;
import dev.onlooker.utils.misc.HoveringUtil;
import dev.onlooker.utils.misc.MathUtils;
import dev.onlooker.utils.render.*;
import dev.onlooker.utils.tuples.Pair;
import lombok.Getter;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryPanel implements Screen {

    private final Category category;

    private final float rectWidth = 105;
    private final float categoryRectHeight = 15;
    @Getter
    private boolean typing;

    public final Pair<Animation, Animation> openingAnimations;
    private List<ModuleRect> moduleRects;

    public CategoryPanel(Category category, Pair<Animation, Animation> openingAnimations) {
        this.category = category;
        this.openingAnimations = openingAnimations;
    }

    @Override
    public void initGui() {
        if (moduleRects == null) {
            moduleRects = new ArrayList<>();
            for (Module module : Client.INSTANCE.getModuleCollection().getModulesInCategory(category).stream().sorted(Comparator.comparing(Module::getName)).collect(Collectors.toList())) {
                moduleRects.add(new ModuleRect(module));
            }
        }

        if (moduleRects != null) {
            moduleRects.forEach(ModuleRect::initGui);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (moduleRects != null) {
            moduleRects.forEach(moduleRect -> moduleRect.keyTyped(typedChar, keyCode));
        }
    }

    public void onDrag(int mouseX, int mouseY) {
        category.getDrag().onDraw(mouseX, mouseY);
    }


    float actualHeight = 0;

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if (moduleRects == null) {
            return;
        }
        if (openingAnimations == null) return;


        float alpha = Math.min(1, openingAnimations.getFirst().getOutput().floatValue());


        Pair<Color, Color> ClientColors = HUDMod.getClientColors();

        //Multiply it by the alpha again so that it eases faster
        float alphaValue = ClickGUIMod.alpha.getValue().floatValue();
        Color ClientFirst = ColorUtil.applyOpacity(Color.BLACK, alphaValue);
        int textColor = ColorUtil.applyOpacity(-1, alpha);


        float x = category.getDrag().getX(), y = category.getDrag().getY();
        Module.allowedClickGuiHeight = 225;


        boolean hoveringMods = HoveringUtil.isHovering(x, y + categoryRectHeight, rectWidth, Module.allowedClickGuiHeight, mouseX, mouseY);

        RenderUtil.resetColor();
        float realHeight = Math.min(Module.allowedClickGuiHeight, actualHeight);

        RoundedUtil.drawRound(x - .75f, y - .5f, rectWidth + 1.5f, realHeight + categoryRectHeight + 1.5f, ClickGUIMod.radius.getValue().floatValue(), ClientFirst);

        StencilUtil.initStencilToWrite();
        RoundedUtil.drawRound(x + 1, y + categoryRectHeight + 5, rectWidth - 2, realHeight - 6, ClickGUIMod.radius.getValue().floatValue(), Color.BLACK);
        Gui.drawRect2(x, y + categoryRectHeight, rectWidth, 10, Color.BLACK.getRGB());
        StencilUtil.readStencilBuffer(1);


        double scroll = category.getScroll().getScroll();
        double count = 0;

        float rectHeight = 14;


        for (ModuleRect moduleRect : getModuleRects()) {
            moduleRect.alpha = alpha;
            moduleRect.x = x - .1f;
            moduleRect.height = rectHeight;
            moduleRect.panelLimitY = y + categoryRectHeight - 2;
            moduleRect.y = (float) (y + categoryRectHeight + (count * rectHeight) + MathUtils.roundToHalf(scroll));
            moduleRect.width = rectWidth + 1;
            moduleRect.drawScreen(mouseX, mouseY);

            // count ups by one but then accounts for setting animation opening
            count += 1 + (moduleRect.getSettingSize() * (16 / 14f));
        }

        typing = getModuleRects().stream().anyMatch(ModuleRect::isTyping);


        actualHeight = (float) (count * rectHeight);

        if (hoveringMods) {
            category.getScroll().onScroll(15);
            float hiddenHeight = (float) ((count * rectHeight) - Module.allowedClickGuiHeight);
            category.getScroll().setMaxScroll(Math.max(0, hiddenHeight));
        }

        StencilUtil.uninitStencilBuffer();
        RenderUtil.resetColor();


        float yMovement;
        switch (category.name) {
            case "Movement":
            case "Player":
            case "Misc":
                yMovement = .5f;
                break;
            case "Render":
                yMovement = 1f;
                break;
            case "Exploit":
            case "Scripts":
                yMovement = 1;
                break;
            default:
                yMovement = 0;
                break;

        }


        RenderUtil.resetColor();
        float textWidth = Utils.tenacityBoldFont22.getStringWidth(category.name + " ") / 2f;
        Utils.iconFont20.drawCenteredString(category.icon, x + rectWidth / 2f + textWidth,
                y + Utils.iconFont20.getMiddleOfBox(categoryRectHeight) + yMovement, textColor);

        RenderUtil.resetColor();
        Utils.tenacityBoldFont22.drawString(category.name, x + ((rectWidth / 2f - textWidth) - (Utils.iconFont20.getStringWidth(category.icon) / 2f)),
                y + Utils.tenacityBoldFont22.getMiddleOfBox(categoryRectHeight), textColor);

    }

    public void renderEffects() {
        float x = category.getDrag().getX(), y = category.getDrag().getY();

        float alpha = Math.min(1, openingAnimations.getFirst().getOutput().floatValue());
        alpha *= alpha;

        Theme theme = Theme.getCurrentTheme();
        Pair<Color, Color> ClientColors = theme.getColors();
        Color ClientFirst = ColorUtil.applyOpacity(ClientColors.getFirst(), alpha);
        Color ClientSecond = ColorUtil.applyOpacity(ClientColors.getSecond(), alpha);

        float allowedHeight = Math.min(actualHeight, Module.allowedClickGuiHeight);

        RoundedUtil.drawRound(x - .75f, y - .5f, rectWidth + 1.5f, allowedHeight + categoryRectHeight + 1.5f, ClickGUIMod.radius.getValue().floatValue(),
                ColorUtil.applyOpacity(Color.BLACK, alpha));
    }

    public void drawToolTips(int mouseX, int mouseY) {
        getModuleRects().forEach(moduleRect -> moduleRect.tooltipObject.drawScreen(mouseX, mouseY));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean canDrag = HoveringUtil.isHovering(category.getDrag().getX(), category.getDrag().getY(), rectWidth, categoryRectHeight, mouseX, mouseY);
        category.getDrag().onClick(mouseX, mouseY, button, canDrag);
        getModuleRects().forEach(moduleRect -> moduleRect.mouseClicked(mouseX, mouseY, button));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        category.getDrag().onRelease(state);
        getModuleRects().forEach(moduleRect -> moduleRect.mouseReleased(mouseX, mouseY, state));
    }

    private final List<String> searchTerms = new ArrayList<>();
    private String searchText;
    private final List<ModuleRect> moduleRectFilter = new ArrayList<>();

    public List<ModuleRect> getModuleRects() {
        if (!Client.INSTANCE.getSearchBar().isFocused()) {
            return moduleRects;
        }

        String search = Client.INSTANCE.getSearchBar().getSearchField().getText();

        if (search.equals(searchText)) {
            return moduleRectFilter;
        } else {
            searchText = search;
        }

        moduleRectFilter.clear();
        for (ModuleRect moduleRect : moduleRects) {
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
        }

        moduleRectFilter.addAll(moduleRects.stream().filter(moduleRect -> moduleRect.getSearchScore() > 60)
                .sorted(Comparator.comparingInt(ModuleRect::getSearchScore).reversed()).collect(Collectors.toList()));

        return moduleRectFilter;
    }

}
