package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.blockentity.machine.CombustionEngineEntity;
import com.hbm.gui.menu.CombustionEngineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 内燃机 GUI。
 */
public class CombustionEngineGui extends BaseMachineGui<CombustionEngineMenu> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/generators/gui_combustion.png");
    private final CombustionEngineEntity be;

    public CombustionEngineGui(CombustionEngineMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.be = pMenu.getEntity();
        this.imageWidth = 176;
        this.imageHeight = 203;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int power = (int) (be.power * 52 / CombustionEngineEntity.MAX_POWER);
        gui.blit(TEXTURE, leftPos + 143, topPos + 70 - power, 176, 52 - power, 16, power);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 4210752);
    }
}
