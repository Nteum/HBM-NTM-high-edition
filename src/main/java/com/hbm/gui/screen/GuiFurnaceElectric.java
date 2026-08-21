package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.blockentity.machine.TileEntityMachineElectricFurnace;
import com.hbm.core.client.gui.GuiMachineBase;
import com.hbm.gui.menu.MenuFurnaceElectric;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class GuiFurnaceElectric extends GuiMachineBase<MenuFurnaceElectric> {
    private static ResourceLocation TEXTURE = HBM.rl("textures/gui/processing/gui_electric_furnace.png");

    @Override
    protected void firstInit() {
        this.imageHeight = 186;
        super.firstInit();
    }

    public GuiFurnaceElectric(MenuFurnaceElectric pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        showBgTexture(pGuiGraphics, TEXTURE);
        int p = (int) (menu.getPower() * 34f / TileEntityMachineElectricFurnace.maxPower);
        if (p > 0) pGuiGraphics.blit(TEXTURE, leftPos + 152, topPos + 52 - p, 176, 64 - p, 16, p);

        if (menu.getProgress() > 0){
            pGuiGraphics.blit(TEXTURE, leftPos + 45, topPos + 20, 192, 12, 18, 16);
            pGuiGraphics.blit(TEXTURE, leftPos + 46, topPos + 47, 192, 28, 18, 16);
        }

        p = (int) (menu.getProgress() * 28f / menu.getMaxProgress());
        if (p > 0) pGuiGraphics.blit(TEXTURE, leftPos + 43, topPos + 36, 176, 0, p, 12);

        this.drawInfoPanel(pGuiGraphics, leftPos + 115, topPos + 19, 8);
    }

    @Override
    protected void renderTooltip(GuiGraphics pGuiGraphics, int pX, int pY) {
        super.renderTooltip(pGuiGraphics, pX, pY);
        List<Component> tooltips = new ArrayList<>();
        if (isHovering(152, 18, 16, 34, pX, pY)){
            tooltips.add(HBMLang.GUI_TOOLTIP_ENERGY.translate(menu.getPower(), TileEntityMachineElectricFurnace.maxPower));
        }else if (isHovering(leftPos + 115, topPos + 19, 8, 8, pX, pY)){
            tooltips.add(HBMLang.GUI_DESC_UPGRADE.translate());
            tooltips.add(HBMLang.GUI_DESC_UPGRADE_SPEED.translate());
            tooltips.add(HBMLang.GUI_DESC_UPGRADE_POWER.translate());
        }

        pGuiGraphics.renderComponentTooltip(this.font, tooltips, pX, pY);
    }
}
