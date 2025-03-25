package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.blockentity.machine.DifurnaceEntity;
import com.hbm.gui.menu.DifurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class DifurnaceGui extends AbstractContainerScreen<DifurnaceMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(HBM.MODID , "textures/gui/difurnace_gui.png");

    public DifurnaceGui(DifurnaceMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    /**
     * 初始化gui
     * */
    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;  //标题居中
    }
    /**
     * 渲染GUI
     */
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics,pMouseX,pMouseY,pPartialTick);
        renderTooltip(pGuiGraphics,pMouseX,pMouseY);
    }
    /**
     * 渲染背景
     * */
    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        pGuiGraphics.blit(TEXTURE,x,y,0,0,imageWidth,imageHeight);
        int progress = menu.getProgress();
        int fuel = menu.getFuel();
        if (progress > 0){
            int progressBarXLen = 24 * progress / DifurnaceEntity.maxProcess;
            /* 渲染工作的小火苗 */
            pGuiGraphics.blit(TEXTURE,x+63,y+37,176,0,14,14);
            /* 渲染工作进度箭头 */
            pGuiGraphics.blit(TEXTURE,x+101,y+35,176,14,progressBarXLen+1,17);
        }
        int fuelBarYLen = 52 * fuel / DifurnaceEntity.maxFuel;
        int fuelBarYStart = 70 - fuelBarYLen;
        /* 渲染燃料槽 */
        pGuiGraphics.blit(TEXTURE,x+44,y+fuelBarYStart,201,53-fuelBarYLen,16,fuelBarYLen);
    }
}
