package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.blockentity.machine.RadiolysisEntity;
import com.hbm.gui.menu.RadiolysisMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 辐射裂解装置 GUI。
 * 显示能量条、热量与流体罐。
 */
public class RadiolysisGui extends BaseMachineGui<RadiolysisMenu> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/gui_radiolysis.png");
    private final RadiolysisEntity be;

    public RadiolysisGui(RadiolysisMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.be = pMenu.getEntity();
        this.imageWidth = 230;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int power = (int) (be.power * 34 / RadiolysisEntity.MAX_POWER);
        gui.blit(TEXTURE, leftPos + 8, topPos + 51 - power, 240, 34 - power, 16, power);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 4210752);
    }
}
