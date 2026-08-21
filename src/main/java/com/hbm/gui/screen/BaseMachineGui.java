package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.core.client.gui.GuiMachineBase;
import com.hbm.gui.menu.slot.FilterSlot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

public abstract class BaseMachineGui<T extends AbstractContainerMenu> extends GuiMachineBase<T> {
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

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        super.renderLabels(pGuiGraphics, pMouseX, pMouseY);
        // 遍历所有槽位，找到你的 FilterSlot
        for (Slot slot : this.menu.slots) {
            if (slot instanceof FilterSlot && slot.hasItem()) {
                // 此时原版已经画了一个 100% 不透明的物品
                // 我们在这里画一个半透明的白色方块覆盖在上面，制造"虚化"感
                int x = slot.x;
                int y = slot.y;

                // 渲染一个半透明层 (ARGB: 0x88FFFFFF)
                pGuiGraphics.fillGradient(RenderType.guiOverlay(), x, y, x + 16, y + 16, 0x88FFFFFF, 0x88FFFFFF, 0);
            }
        }
    }
}
