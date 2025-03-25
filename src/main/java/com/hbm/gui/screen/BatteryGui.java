package com.hbm.gui.screen;

import com.hbm.gui.menu.BatteryMenu;
import com.hbm.HBM;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BatteryGui extends AbstractContainerScreen<BatteryMenu> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/gui_battery.png");
    public BatteryGui(BatteryMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        imageWidth = 176;
        imageHeight = 166;
        titleLabelX = (imageWidth - font.width(title)) / 2;  //标题居中
        super.init();
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        int button = -1;
        double d0 = pMouseX - (double) (leftPos + 133);
        double d1 = pMouseY - (double) (topPos + 16);
        if (d0 >= 0.0D && d1 >= 0.0D && d0 <= 18.0D && d1 <= 18.0D)button = 0;
        double d2 = pMouseY - (double) (topPos + 52);
        if (d0 >= 0.0D && d2 >= 0.0D && d0 <= 18.0D && d2 <= 18.0D)button = 1;
        double d3 = pMouseX - (double) (leftPos + 152);
        double d4 = pMouseY - (double) (topPos + 35);
        if (d3 >= 0.0D && d4 >= 0.0D && d3 <= 16.0D && d4 <= 16.0D)button = 2;
        if (button < 0)return super.mouseClicked(pMouseX, pMouseY, pButton);
        else {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, button);
            return true;
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics,pMouseX,pMouseY,pPartialTick);
        renderTooltip(pGuiGraphics,pMouseX,pMouseY);
        //打印电池电量
        pGuiGraphics.drawString(this.font,Component.literal((double)menu.getPower()/1000+"kHE/"+menu.getMaxPower()/1000+"kHE"),leftPos+this.titleLabelX,topPos+this.titleLabelY+58+this.font.lineHeight,4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        //渲染背景图
        pGuiGraphics.blit(TEXTURE,leftPos,topPos,0,0,imageWidth,imageHeight);
        //渲染能量条
        long power = menu.getPower();
        if (power > 0){
            int i = (int)menu.getPowerRemainingScaled(52);
            pGuiGraphics.blit(TEXTURE,leftPos + 62, topPos + 69 - i, 176, 52 - i, 52, i);
        }
        pGuiGraphics.blit(TEXTURE, leftPos + 133, topPos + 16, 176, 52 + menu.getRedLow() * 18, 18, 18);
        pGuiGraphics.blit(TEXTURE, leftPos + 133, topPos + 52, 176, 52 + menu.getRedHeight() * 18, 18, 18);
        pGuiGraphics.blit(TEXTURE, leftPos + 152, topPos + 35, 194, 52 + menu.getConnPriority() * 16, 16, 16);
    }

}
