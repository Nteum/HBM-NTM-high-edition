package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.blockentity.machine.CokerEntity;
import com.hbm.gui.menu.CokerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 焦化装置 GUI。
 */
public class CokerGui extends BaseMachineGui<CokerMenu> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/processing/gui_coker.png");
    private final CokerEntity be;

    public CokerGui(CokerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.be = pMenu.getEntity();
        this.imageWidth = 176;
        this.imageHeight = 204;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int progress = Math.min(be.progress * 40 / 100, 40);
        gui.blit(TEXTURE, leftPos + 61, topPos + 46, 176, 0, progress, 5);

        int heat = Math.min(be.heat * 40 / 100, 40);
        gui.blit(TEXTURE, leftPos + 61, topPos + 55, 176, 5, heat, 5);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 0xC7C1A3);
    }
}
