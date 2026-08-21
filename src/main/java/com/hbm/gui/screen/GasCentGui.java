package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.blockentity.machine.GasCentEntity;
import com.hbm.gui.menu.GasCentMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 气体离心机 GUI。
 */
public class GasCentGui extends BaseMachineGui<GasCentMenu> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/processing/gui_centrifuge_gas.png");
    private final GasCentEntity be;

    public GasCentGui(GasCentMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.be = pMenu.getEntity();
        this.imageWidth = 206;
        this.imageHeight = 204;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int power = (int) (be.power * 52 / GasCentEntity.MAX_POWER);
        gui.blit(TEXTURE, leftPos + 182, topPos + 70 - power, 0, 204, 16, power);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 4210752);
    }
}
