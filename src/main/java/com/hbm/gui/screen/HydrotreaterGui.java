package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.blockentity.machine.HydrotreaterEntity;
import com.hbm.gui.menu.HydrotreaterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 加氢装置 GUI。
 */
public class HydrotreaterGui extends BaseMachineGui<HydrotreaterMenu> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/processing/gui_hydrotreater.png");
    private final HydrotreaterEntity be;

    public HydrotreaterGui(HydrotreaterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.be = pMenu.getEntity();
        this.imageWidth = 176;
        this.imageHeight = 238;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int power = (int) (be.power * 52 / be.MAX_POWER);
        gui.blit(TEXTURE, leftPos + 17, topPos + 70 - power, 176, 52 - power, 16, power);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 0xffffff);
    }
}
