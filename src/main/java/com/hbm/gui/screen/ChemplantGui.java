package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.gui.menu.ChemplantMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ChemplantGui extends BaseMachineGui<ChemplantMenu> {
    public static final ResourceLocation TEXTURE = HBM.rl("textures/gui/gui_chemplant");
    public ChemplantGui(ChemplantMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageHeight = 256;
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        showBgTexture(pGuiGraphics, TEXTURE);

    }
}
