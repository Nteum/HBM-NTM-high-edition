package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.blockentity.machine.RefineryEntity;
import com.hbm.gui.menu.RefineryMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 炼油厂 GUI。
 */
public class RefineryGui extends BaseMachineGui<RefineryMenu> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/processing/gui_refinery.png");
    private final RefineryEntity be;

    public RefineryGui(RefineryMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.be = pMenu.getEntity();
        this.imageWidth = 182;
        this.imageHeight = 240;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int power = (int) (be.power * 52 / be.MAX_POWER);
        gui.blit(TEXTURE, leftPos + 158, topPos + 70 - power, 176, 52 - power, 16, power);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 0xffffff);
    }
}
