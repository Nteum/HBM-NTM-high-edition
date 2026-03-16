package com.hbm.gui.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.resources.ResourceLocation;
//具有多个状态的按钮
public class MultiStateButton extends ImageButton {
    int stateNum = 2;
    int defaultState = 0;
    public int stateNow = defaultState;
    public MultiStateButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int stateNum, ResourceLocation pResourceLocation, OnPress pOnPress) {
        this(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart,stateNum,0, pResourceLocation, pOnPress);
    }
    public MultiStateButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int stateNum, int defaultState, ResourceLocation pResourceLocation, OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pResourceLocation, pOnPress);
        this.stateNum = stateNum;
        this.defaultState = defaultState;
        this.stateNow = defaultState;
    }
    public void updateData(int mode){
        this.stateNow = mode;
    }

    @Override
    public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int i = yTexStart + yDiffTex * stateNow;
//        if (!this.isActive()) {
//            i = yTexStart + yDiffTex * stateNow;
//        } else
//        if (this.isHovered()) {
//            i = yTexStart + yDiffTex * (stateNow+1==stateNum ? 0 : stateNow + 1);
//        }

        RenderSystem.enableDepthTest();
        pGuiGraphics.blit(resourceLocation, getX(), getY(), (float)xTexStart, (float)i, width, height, textureWidth, textureHeight);
    }
}
