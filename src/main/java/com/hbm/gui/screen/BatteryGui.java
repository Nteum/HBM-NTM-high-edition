package com.hbm.gui.screen;

import com.hbm.blockentity.machine.BatteryEntity;
import com.hbm.gui.menu.BatteryMenu;
import com.hbm.main.HBMxx;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BatteryGui extends AbstractContainerScreen<BatteryMenu> {
    private static final ResourceLocation TEXTURE = HBMxx.hbm("textures/gui/gui_battery.png");
    public BatteryGui(BatteryMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        imageWidth = 176;
        imageHeight = 166;

        super.init();
    }
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics,pMouseX,pMouseY,pPartialTick);
        renderTooltip(pGuiGraphics,pMouseX,pMouseY);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        //渲染背景图
        pGuiGraphics.blit(TEXTURE,leftPos,topPos,0,0,imageWidth,imageHeight);
        //渲染能量条
        int power = menu.getPower();
        if (power > 0){
            int i = (int)menu.getPowerRemainingScaled(52);
            pGuiGraphics.blit(TEXTURE,leftPos + 62, topPos + 69 - i, 176, 52 - i, 52, i);
        }
        pGuiGraphics.blit(TEXTURE, leftPos + 133, topPos + 16, 176, 52 + menu.getRedLow() * 18, 18, 18);
        pGuiGraphics.blit(TEXTURE, leftPos + 133, topPos + 52, 176, 52 + menu.getRedHeight() * 18, 18, 18);
        pGuiGraphics.blit(TEXTURE, leftPos + 152, topPos + 35, 194, 52 + menu.getConnPriority() * 16 - 16, 16, 16);
    }

}
