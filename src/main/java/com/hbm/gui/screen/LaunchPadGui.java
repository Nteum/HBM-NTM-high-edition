package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.gui.menu.LaunchPadMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class LaunchPadGui extends BaseMachineGui<LaunchPadMenu>{
    public static final ResourceLocation TEXTURE = HBM.rl("textures/gui/gui_launch_pad_large.png");
    public LaunchPadGui(LaunchPadMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageHeight = 236;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        showBgTexture(pGuiGraphics, TEXTURE);
    }
}
