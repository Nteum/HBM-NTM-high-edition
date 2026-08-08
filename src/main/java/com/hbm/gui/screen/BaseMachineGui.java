package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.gui.menu.slot.FilterSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

import java.util.List;

public abstract class BaseMachineGui<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    boolean firstInit = true;
    static final ResourceLocation GUI_UTIL = HBM.rl("textures/gui/gui_utility.png");
    public BaseMachineGui(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        if (firstInit){
            firstInit();
            firstInit = false;
        }
        super.init();
    }

    protected void firstInit(){
        titleLabelX = (imageWidth - font.width(title)) / 2;  //标题居中
        inventoryLabelY += imageHeight - 166;   // 修改“物品栏”三字位置
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics,pMouseX,pMouseY,pPartialTick);
        renderTooltip(pGuiGraphics,pMouseX,pMouseY);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {

    }

    protected void showBgTexture(GuiGraphics pGuiGraphics, ResourceLocation texture){
        pGuiGraphics.blit(texture,leftPos,topPos,0,0,imageWidth,imageHeight);
    }
    /** 在鼠标指针位置显示tooltip */
    public void drawCustomInfoStat(GuiGraphics pGuiGraphics, int mouseX, int mouseY, int x, int y, int width, int height, List<Component> tooltips) {
        if(x <= mouseX && x + width > mouseX && y < mouseY && y + height >= mouseY)
            pGuiGraphics.renderComponentTooltip(this.font, tooltips, mouseX, mouseY);
//        if (isHovering(x, y, width, height, mouseX, mouseY)) pGuiGraphics.renderComponentTooltip(this.font, tooltips, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        super.renderLabels(pGuiGraphics, pMouseX, pMouseY);
        // 遍历所有槽位，找到你的 FilterSlot
        for (Slot slot : this.menu.slots) {
            if (slot instanceof FilterSlot && slot.hasItem()) {
                // 此时原版已经画了一个 100% 不透明的物品
                // 我们在这里画一个半透明的白色方块覆盖在上面，制造“虚化”感
                int x = slot.x;
                int y = slot.y;

                // 渲染一个半透明层 (ARGB: 0x88FFFFFF)
                pGuiGraphics.fillGradient(RenderType.guiOverlay(), x, y, x + 16, y + 16, 0x88FFFFFF, 0x88FFFFFF, 0);
            }
        }
    }

    protected boolean isMouseInside(int mouseX, int mouseY, int x, int y, int w, int h) {
        return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
    }

    public void drawInfoPanel(GuiGraphics pGuiGraphics, int x, int y, int width, int type) {
        switch(type) {
            case 0: pGuiGraphics.blit(GUI_UTIL, x, y, 0, 0, 8, 8); break; //Small blue I
            case 1: pGuiGraphics.blit(GUI_UTIL, x, y, 0, 8, 8, 8); break; //Small green I
            case 2: pGuiGraphics.blit(GUI_UTIL, x, y, 8, 0, 16, 16); break; //Large blue I
            case 3: pGuiGraphics.blit(GUI_UTIL, x, y, 24, 0, 16, 16); break; //Large green I
            case 4: pGuiGraphics.blit(GUI_UTIL, x, y, 0, 16, 8, 8); break; //Small red !
            case 5: pGuiGraphics.blit(GUI_UTIL, x, y, 0, 24, 8, 8); break; //Small yellow !
            case 6: pGuiGraphics.blit(GUI_UTIL, x, y, 8, 16, 16, 16); break; //Large red !
            case 7: pGuiGraphics.blit(GUI_UTIL, x, y, 24, 16, 16, 16); break; //Large yellow !
            case 8: pGuiGraphics.blit(GUI_UTIL, x, y, 0, 32, 8, 8); break; //Small blue *
            case 9: pGuiGraphics.blit(GUI_UTIL, x, y, 0, 40, 8, 8); break; //Small grey *
            case 10: pGuiGraphics.blit(GUI_UTIL, x, y, 8, 32, 16, 16); break; //Large blue *
            case 11: pGuiGraphics.blit(GUI_UTIL, x, y, 24, 32, 16, 16); break; //Large grey *
        }
    }
}
