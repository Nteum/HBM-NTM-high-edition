package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.blockentity.machine.VacuumDistillEntity;
import com.hbm.gui.menu.VacuumDistillMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 真空蒸馏塔 GUI。
 */
public class VacuumDistillGui extends BaseMachineGui<VacuumDistillMenu> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/processing/gui_vacuum_distill.png");
    private final VacuumDistillEntity be;

    public VacuumDistillGui(VacuumDistillMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.be = pMenu.getEntity();
        this.imageWidth = 176;
        this.imageHeight = 238;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int j = (int) (be.power * 54 / VacuumDistillEntity.MAX_POWER);
        gui.blit(TEXTURE, leftPos + 26, topPos + 70 - j, 176, 52 - j, 16, j);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 0xffffff);
    }
}
