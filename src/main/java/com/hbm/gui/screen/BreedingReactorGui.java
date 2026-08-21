package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.blockentity.machine.BreedingReactorEntity;
import com.hbm.gui.menu.BreedingReactorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 增殖反应堆 GUI。
 */
public class BreedingReactorGui extends BaseMachineGui<BreedingReactorMenu> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/processing/gui_breeder.png");
    private final BreedingReactorEntity be;

    public BreedingReactorGui(BreedingReactorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.be = pMenu.getEntity();
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int j = be.getProgressScaled(14);
        if (j > 0){
            gui.blit(TEXTURE, leftPos + 62, topPos + 38, 176, 0, j, 14);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 4210752);
    }
}
