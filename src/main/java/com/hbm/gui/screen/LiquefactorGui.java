package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.blockentity.machine.LiquefactorEntity;
import com.hbm.gui.menu.LiquefactorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 工业液化机 GUI。
 */
public class LiquefactorGui extends BaseMachineGui<LiquefactorMenu> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/processing/gui_liquefactor.png");
    private final LiquefactorEntity be;

    public LiquefactorGui(LiquefactorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.be = pMenu.getEntity();
        this.imageWidth = 176;
        this.imageHeight = 204;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int power = (int) (be.power * 52 / be.MAX_POWER);
        gui.blit(TEXTURE, leftPos + 134, topPos + 70 - power, 176, 52 - power, 16, power);

        int progress = be.processTime == 0 ? 0 : be.progress * 42 / be.processTime;
        gui.blit(TEXTURE, leftPos + 42, topPos + 17, 192, 0, progress, 35);

        if (power > 0)
            gui.blit(TEXTURE, leftPos + 138, topPos + 4, 176, 52, 9, 12);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 0xC7C1A3);
    }
}
