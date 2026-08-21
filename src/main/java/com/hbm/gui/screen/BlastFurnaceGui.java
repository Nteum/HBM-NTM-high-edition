package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.blockentity.machine.BlastFurnaceEntity;
import com.hbm.gui.menu.BlastFurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 高炉 GUI。
 */
public class BlastFurnaceGui extends BaseMachineGui<BlastFurnaceMenu> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/processing/gui_blast_furnace.png");
    private final BlastFurnaceEntity be;

    public BlastFurnaceGui(BlastFurnaceMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.be = pMenu.getEntity();
        this.imageWidth = 176;
        this.imageHeight = 222;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int fuel = (int) Math.round((double) be.fuel * 26D / (double) BlastFurnaceEntity.MAX_FUEL);
        int prog = (int) Math.round(be.progress * (88D - fuel));
        if (prog > 0){
            gui.blit(TEXTURE, leftPos + 62, topPos + 106 - prog - fuel, 176, 102 - prog - fuel, 56, prog);
        }
        if (fuel > 0){
            gui.blit(TEXTURE, leftPos + 62, topPos + 106 - fuel, 176, 128 - fuel, 56, fuel);
        }
        if (be.isProgressing){
            gui.blit(TEXTURE, leftPos + 81, topPos + 64, 176, 0, 14, 14);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 4210752);
    }
}
