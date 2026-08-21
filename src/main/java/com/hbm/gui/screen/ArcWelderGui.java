package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.blockentity.machine.ArcWelderEntity;
import com.hbm.gui.menu.ArcWelderMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 电弧焊机 GUI。
 * 显示能量条与加工进度。
 */
public class ArcWelderGui extends BaseMachineGui<ArcWelderMenu> {
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/processing/gui_arc_welder.png");
    private final ArcWelderEntity be;

    public ArcWelderGui(ArcWelderMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.be = pMenu.getEntity();
        this.imageWidth = 176;
        this.imageHeight = 204;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        gui.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int p = (int) (be.power * 52 / Math.max(be.consumption * 20, 1));
        p = Math.min(p, 52);
        gui.blit(TEXTURE, leftPos + 152, topPos + 70 - p, 176, 52 - p, 16, p);

        int i = be.processTime > 0 ? Math.min(be.progress * 33 / be.processTime, 33) : 0;
        gui.blit(TEXTURE, leftPos + 72, topPos + 37, 192, 0, i, 14);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(font, this.title, this.titleLabelX, this.titleLabelY, 4210752);
    }
}
