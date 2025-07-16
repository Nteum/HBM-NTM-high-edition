package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.gui.menu.ElectricFurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ElectricFurnaceGui extends AbstractContainerScreen<ElectricFurnaceMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(HBM.MODID , "textures/gui/gui_electric_furnace.png");
    public ElectricFurnaceGui(ElectricFurnaceMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;  //标题居中
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, imageWidth, imageHeight);

        int process = this.menu.containerData.get(0);
        int maxProcess = this.menu.containerData.get(1);
        int power = this.menu.containerData.get(2);
        int maxPower = this.menu.containerData.get(3);

        if (power > 0){
            int barLen = (int)(52 * power / (double)maxPower);
            // 能量条
            pGuiGraphics.blit(TEXTURE, leftPos + 20, topPos + 69 - barLen, 200, 52 - barLen, 16, barLen);
        }

        if (process > 0){
            int barLen = (int)(24 * process / (double)maxProcess);
            // 进度条
            pGuiGraphics.blit(TEXTURE, leftPos + 79, topPos + 34, 176, 17, barLen + 1, 17);
            // 电炉发亮的条
            pGuiGraphics.blit(TEXTURE, leftPos + 56, topPos + 35, 176, 0, 16, 16);
        }
    }
}
