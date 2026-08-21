package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.blockentity.machine.BERtgFurnace;
import com.hbm.core.client.gui.GuiMachineBase;
import com.hbm.gui.menu.MenuRtgFurnace;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GuiRtgFurnace extends GuiMachineBase<MenuRtgFurnace> {
    private static ResourceLocation TEXTURE = HBM.rl("textures/gui/rtgfurnace.png");
    public GuiRtgFurnace(MenuRtgFurnace pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        showBgTexture(pGuiGraphics, TEXTURE);
        int j;
        if (menu.hasHeat()) pGuiGraphics.blit(TEXTURE, leftPos + 55, topPos + 35, 176, 0, 18, 16);
        if ((j = (int) (menu.getCookTime() * 24f / BERtgFurnace.processingSpeed)) > 0)
            pGuiGraphics.blit(TEXTURE, leftPos + 79, topPos + 34, 176, 16, j + 1, 17);
    }
}
