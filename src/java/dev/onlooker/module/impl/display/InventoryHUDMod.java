package dev.onlooker.module.impl.display;

import dev.onlooker.Client;
import dev.onlooker.event.impl.render.Render2DEvent;
import dev.onlooker.event.impl.render.ShaderEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.utils.objects.Dragging;
import dev.onlooker.utils.render.RoundedUtil;
import dev.onlooker.utils.render.Theme;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class InventoryHUDMod extends Module {


    private final Dragging dragging = Client.INSTANCE.createDrag(this, "Inventory", 5, 150);
    float inventory_width = 165;
    float inventory_height = 70;

    public InventoryHUDMod() {
        super("InventoryHUD", Category.DISPLAY, "InventoryDisplay");
        addSettings();
    }

    @Override
    public void onShaderEvent(ShaderEvent event) {
        HUDMod hudMod = Client.INSTANCE.getModuleCollection().getModule(HUDMod.class);
        if (HUDMod.inventorydisplay.is("OnLooker")) {
            inventory_height =65;
            dragging.setWidth(inventory_width);
            dragging.setHeight(inventory_height);
            RoundedUtil.drawRound(dragging.getX(), dragging.getY()+5, dragging.getWidth(), dragging.getHeight(), 6.0f, hudMod.getThemeShaderColor());
        }
        if (HUDMod.inventorydisplay.is("Novoline")) {
            tenacityBoldFont22.drawStringWithShadow("Inventory", dragging.getX() + 5, dragging.getY() + 4, Color.BLACK);
            dragging.setWidth(inventory_width);
            dragging.setHeight(inventory_height);
            RoundedUtil.drawRound(dragging.getX(), dragging.getY(), dragging.getWidth(), dragging.getHeight(), 0.0f, Color.BLACK);
            for (double i = (double)(dragging.getX()); i < (dragging.getX() + 165.0f); i += 1.0) {
                Gui.drawRect(i, dragging.getY(), i + 1.0, dragging.getY() + 1.0f, Theme.getColor((int)(200.0 + i * 20.0)));
            }
        }
    }

    @Override
    public void onRender2DEvent(Render2DEvent event) {
        dragging.setWidth(inventory_width);
        dragging.setHeight(inventory_height);
        int itemX = (int) (dragging.getX() + 2.0f);
        int itemY = (int) (dragging.getY() + 18.0f);
        HUDMod hudMod = Client.INSTANCE.getModuleCollection().getModule(HUDMod.class);
        if (HUDMod.inventorydisplay.is("OnLooker")) {
            inventory_height =65;
            Color color = hudMod.getThemeBgColor();

            RoundedUtil.drawRound(dragging.getX(), dragging.getY()+5, dragging.getWidth(), inventory_height, 6.0f, color);

            tenacityBoldFont18.drawString("Inventory", dragging.getX() + 5, dragging.getY() + 8, new Color(255, 255, 255, 180));
        }
        if (HUDMod.inventorydisplay.is("Novoline")) {
            RoundedUtil.drawRound(dragging.getX(), dragging.getY(), dragging.getWidth(), inventory_height, 0.0f, new Color(0, 0, 0, 80));
            for (double i = (dragging.getX()); i < (dragging.getX() + 165.0f); i += 1.0) {
                Gui.drawRect(i, dragging.getY(), i + 1.0, dragging.getY() + 1.0f, Theme.getColor((int)(200.0 + i * 20.0)));
            }
            tenacityBoldFont24.drawStringWithShadow("Inventory", dragging.getX() + 55, dragging.getY() + 4, -1);
        }

        //渲染背包物品
        boolean hasStacks = false;
        for (int i = 9; i <= 35; ++i) {
            Slot slot = mc.thePlayer.inventoryContainer.inventorySlots.get(i);
            ItemStack stack = mc.thePlayer.inventoryContainer.inventorySlots.get(i).getStack();
            GL11.glPushMatrix();
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            if (this.mc.theWorld != null) {
                RenderHelper.enableGUIStandardItemLighting();
            }
            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.clear(256);
            if (slot.getHasStack()) hasStacks = true;
            mc.getRenderItem().zLevel = -150.0f;
            mc.getRenderItem().renderItemAndEffectIntoGUI(stack, itemX, itemY);
            mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, stack, itemX, itemY);
            mc.getRenderItem().zLevel = 0.0f;
            GlStateManager.disableBlend();
            GlStateManager.scale(0.5d, 0.5d, 0.5d);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.scale(2.0f, 2.0f, 2.0f);
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
            GL11.glPopMatrix();
            if (itemX < dragging.getX() + 144.0f) {
                itemX += 18;
            } else {
                itemX = (int) (dragging.getX() + 2.0f);
                itemY += 16;
            }
        }
        if (mc.currentScreen instanceof GuiInventory) {
            tenacityBoldFont16.drawString("Already in inventory", 39 + dragging.getX(), dragging.getY() + 35, new Color(255, 255, 255, 155).getRGB());
        } else  if (!hasStacks) {
            tenacityBoldFont16.drawString("Empty... ", 70 + dragging.getX(), dragging.getY() + 36, new Color(255, 255, 255, 155).getRGB());
        }
    }
}