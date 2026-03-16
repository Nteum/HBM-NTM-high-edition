package com.hbm.gui.screen;

import com.hbm.HBM;
import com.hbm.HBMLang;
import com.hbm.blockentity.machine.PressEntity;
import com.hbm.gui.menu.PressMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class PressGui extends BaseMachineGui<PressMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(HBM.MODID , "textures/gui/gui_press.png");
    private static final ResourceLocation GAUGE = new ResourceLocation(HBM.MODID , "textures/gui/gauges/small_bow.png");

    public PressGui(PressMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics,pMouseX,pMouseY,pPartialTick);
        renderTooltip(pGuiGraphics,pMouseX,pMouseY);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        showBgTexture(pGuiGraphics, TEXTURE);

        renderGauge(pGuiGraphics, GAUGE, this.leftPos + 25, topPos + 16, 18, 18, (menu.getSpeed()) / PressEntity.MAX_SPEED);

        int k = (int) (menu.pressEntity.renderPress * 16 / PressEntity.MAX_PRESS);
        pGuiGraphics.blit(TEXTURE, leftPos + 79, topPos + 35, 194, 0, 18, k);

        if (menu.getBurnTime() >= 20){
            pGuiGraphics.blit(TEXTURE, leftPos + 27, topPos + 36, 176, 0, 14, 14);
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics pGuiGraphics, int pX, int pY) {
        List<Component> tooltips = new ArrayList<>();
        if (isHovering(25, 16, 18, 18, pX, pY)){
            tooltips.add(HBMLang.GUI_TOOLTIP_PROGRESS.translate(String.format("%.2f", menu.getSpeed() * 100 / PressEntity.MAX_SPEED)));
        }else if (isHovering(25, 34, 18, 18, pX, pY)){
            tooltips.add(HBMLang.GUI_TOOLTIP_LEFT_TIME.translate(String.format("%.2f", menu.getBurnTime() / 200)));
        }

        if (!tooltips.isEmpty()) pGuiGraphics.renderComponentTooltip(font, tooltips, pX, pY);
        super.renderTooltip(pGuiGraphics, pX, pY);
    }

    protected void renderGauge(GuiGraphics pGuiGraphics, ResourceLocation TEXTURE, int minX, int minY, int width, int height, float value){
        int numStage = 13;
        float lenInterval = (float) 1 / (numStage - 1);
        int stage = (int) (value / lenInterval);
        pGuiGraphics.blit(TEXTURE, minX, minY, 0, 0, stage * height, width, height, 18, 234);
    }
}