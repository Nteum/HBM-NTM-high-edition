package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.blockentity.machine.MilkReformerEntity;
import com.hbm.gui.menu.MilkReformerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 牛奶改质器 GUI。
 * 显示能量条。
 */
public class MilkReformerGui extends BaseMachineGui<MilkReformerMenu> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/processing/gui_milk_reformer.png");
    private final MilkReformerEntity be;

    public MilkReformerGui(MilkReformerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.be = pMenu.getEntity();
        this.imageWidth = 176;
        this.imageHeight = 238;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int j = (int) (be.power * 63 / MilkReformerEntity.MAX_POWER);
        gui.blit(TEXTURE, leftPos + 98, topPos + 13, 176, 6, j, 6);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 4210752);
    }
}
