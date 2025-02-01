package dev.onlooker.module.impl.display;

import dev.onlooker.Client;
import dev.onlooker.event.impl.render.Render2DEvent;
import dev.onlooker.event.impl.render.ShaderEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.utils.objects.Dragging;
import dev.onlooker.utils.render.RoundedUtil;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArmorHUDMod extends Module {
    private final Dragging dragging = Client.INSTANCE.createDrag(this, "ArmorMod", 5, 200);
    float inventory_width = 80;
    float inventory_height = 30;
    HUDMod hudMod = Client.INSTANCE.getModuleCollection().getModule(HUDMod.class);

    public ArmorHUDMod() {
        super("ArmorHUD", Category.DISPLAY, "ArmorMod");
        if (!enabled) this.toggleSilent();
    }

    @Override
    public void onShaderEvent(ShaderEvent event) {
        HUDMod hudMod = Client.INSTANCE.getModuleCollection().getModule(HUDMod.class);
        RoundedUtil.drawRound(dragging.getX(), dragging.getY(), dragging.getWidth()-2, inventory_height, 6.0f, hudMod.getThemeShaderColor());
    }

    @Override
    public void onRender2DEvent(Render2DEvent event) {
        HUDMod hudMod = Client.INSTANCE.getModuleCollection().getModule(HUDMod.class);
        Color color = hudMod.getThemeBgColor();
        RoundedUtil.drawRound(dragging.getX(), dragging.getY(), dragging.getWidth()-2, inventory_height, 6.0f, color);

        tenacityBoldFont18.drawString("Armor",dragging.getX() + 5, dragging.getY()+3.5f,new Color(255, 255, 255, 180));

        ScaledResolution sr = new ScaledResolution(mc);
        dragging.setWidth(inventory_width);
        dragging.setHeight(inventory_height);
        int itemX = (int) (dragging.getX() + 58.0f);
        int itemY = (int) (dragging.getY() + 12.0f);
        List<ItemStack> equipment = new ArrayList<>();
        boolean inWater = mc.thePlayer.isEntityAlive() && mc.thePlayer.isInsideOfMaterial(Material.water);
        int x = -94;

        ItemStack armorPiece;
        for (int i = 3; i >= 0; i--) {
            if ((armorPiece = mc.thePlayer.inventory.armorInventory[i]) != null) {
                equipment.add(armorPiece);
            }
        }
        Collections.reverse(equipment);

        for (ItemStack itemStack : equipment) {
            armorPiece = itemStack;
            RenderHelper.enableGUIStandardItemLighting();
            x += 15;
            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.clear(256);
            mc.getRenderItem().zLevel = -150.0F;
            int s = mc.thePlayer.capabilities.isCreativeMode ? 15 : 0;
            mc.getRenderItem().renderItemAndEffectIntoGUI(armorPiece, itemX-1, itemY);
            mc.getRenderItem().zLevel = 0.0F;
            GlStateManager.disableBlend();
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
            armorPiece.getEnchantmentTagList();
            if (itemX < dragging.getX() + x + (float) sr.getScaledWidth() / 2 - 4) {
                itemX -= 18;
            } else {
                itemX = (int) (dragging.getX() +
                        (sr.getScaledHeight() - (inWater ? 65 : 55) + s));
                itemY += 16;
            }
        }
    }
}