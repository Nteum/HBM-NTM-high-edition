package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.blockentity.machine.EPressEntityBE;
import com.hbm.gui.menu.EPressMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 电动锻压机 GUI。
 * 显示能量条与锻压进度。
 */
public class EPressGui extends BaseMachineGui<EPressMenu> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/gui_epress.png");
    private final EPressEntityBE be;

    public EPressGui(EPressMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.be = pMenu.getEntity();
        this.imageWidth = 176;
        this.imageHeight = 186;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int power = (int) be.getPowerScaled(34);
        gui.blit(TEXTURE, leftPos + 152, topPos + 52 - power, 176, 34 - power, 16, power);

        int k = (int) (be.renderPress * 16 / EPressEntityBE.MAX_PRESS);
        gui.blit(TEXTURE, leftPos + 18, topPos + 33, 192, 0, 18, k);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 4210752);
    }
}
