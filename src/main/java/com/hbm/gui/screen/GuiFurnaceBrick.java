package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.core.client.gui.GuiMachineBase;
import com.hbm.gui.menu.MenuFurnaceBrick;
import com.hbm.registries.ModTags;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.Blocks;

public class GuiFurnaceBrick extends GuiMachineBase<MenuFurnaceBrick> {
    private static ResourceLocation TEXTURE = HBM.rl("textures/gui/processing/gui_furnace_brick.png");
    public GuiFurnaceBrick(MenuFurnaceBrick pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        showBgTexture(guiGraphics, TEXTURE);

        if(this.menu.getBurnTime() > 0) {
            int b = menu.getBurnTime() * 13 / menu.getMaxBurnTime();
            guiGraphics.blit(TEXTURE, leftPos + 62, topPos + 54 + 12 - b, 176, 12 - b, 14, b + 1);
            int p = menu.getProgress() * 24 / 200;
            guiGraphics.blit(TEXTURE, leftPos + 85, topPos + 34, 176, 14, p + 1, 16);
        }
    }
}
