package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.blockentity.machine.AshpitEntityBE;
import com.hbm.gui.menu.AshpitMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 灰烬收集器 GUI。
 */
public class AshpitGui extends BaseMachineGui<AshpitMenu> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/machine/gui_ashpit.png");
    private final AshpitEntityBE be;

    public AshpitGui(AshpitMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.be = pMenu.getEntity();
        this.imageWidth = 176;
        this.imageHeight = 168;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 4210752);
    }
}
