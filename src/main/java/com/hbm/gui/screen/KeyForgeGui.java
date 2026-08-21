package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.gui.menu.KeyForgeMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 钥匙锻造台 GUI。
 */
public class KeyForgeGui extends BaseMachineGui<KeyForgeMenu> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/gui_keyforge.png");

    public KeyForgeGui(KeyForgeMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 176;
        this.imageHeight = 186;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        drawInfoPanel(gui, leftPos + 12, topPos + 28, 2);
        drawInfoPanel(gui, leftPos + 12, topPos + 44, 3);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 0xffffff);
    }
}
