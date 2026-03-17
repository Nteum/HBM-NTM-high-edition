package com.hbm.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.List;

public abstract class BaseMachineGui<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    public BaseMachineGui(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
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
}
