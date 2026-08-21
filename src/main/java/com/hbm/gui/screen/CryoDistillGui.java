package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.blockentity.machine.CryoDistillEntity;
import com.hbm.gui.menu.CryoDistillMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 低温蒸馏器 GUI。
 */
public class CryoDistillGui extends BaseMachineGui<CryoDistillMenu> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/processing/gui_cryodistill.png");
    private final CryoDistillEntity be;

    public CryoDistillGui(CryoDistillMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.be = pMenu.getEntity();
        this.imageWidth = 176;
        this.imageHeight = 238;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int j = (int) (be.power * 54 / CryoDistillEntity.MAX_POWER);
        gui.blit(TEXTURE, leftPos + 26, topPos + 70 - j, 176, 52 - j, 16, j);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 0xffffff);
    }
}
