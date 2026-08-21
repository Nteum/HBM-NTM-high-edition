package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.blockentity.machine.RTGEntityBE;
import com.hbm.gui.menu.RTGMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * RTG 发电机 GUI。
 * 显示热量条与能量条。
 */
public class RTGGui extends BaseMachineGui<RTGMenu> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/gui_rtg.png");
    private final RTGEntityBE be;

    public RTGGui(RTGMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.be = pMenu.getEntity();
        this.imageWidth = 176;
        this.imageHeight = 188;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        if (be.hasHeat()) {
            int heat = be.getHeatScaled(51);
            gui.blit(TEXTURE, leftPos + 124, topPos + 61 - heat, 176, 10 + (51 - heat), 16, heat);
        }

        if (be.hasPower()) {
            int power = (int) be.getPowerScaled(51);
            gui.blit(TEXTURE, leftPos + 146, topPos + 61 - power, 192, 10 + (51 - power), 16, power);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 10925486);
    }
}
