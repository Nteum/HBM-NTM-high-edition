package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.gui.menu.MenuArcFurnace;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GuiArcFurnace extends BaseMachineGui<MenuArcFurnace>{
    public static final ResourceLocation TEXTURE = HBM.rl("textures/gui/processing/gui_arc_furnace.png");
    public GuiArcFurnace(MenuArcFurnace pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void firstInit() {
        super.firstInit();
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        super.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY);
        showBgTexture(pGuiGraphics, TEXTURE);
    }

    @Override
    protected void renderTooltip(GuiGraphics pGuiGraphics, int pX, int pY) {
        super.renderTooltip(pGuiGraphics, pX, pY);
    }
}
