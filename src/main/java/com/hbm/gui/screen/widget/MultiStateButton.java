package com.hbm.gui.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.resources.ResourceLocation;
//具有多个状态的按钮
public class MultiStateButton extends ImageButton {
    int stateNum;
    int order;          // 按钮的序号，可以作为额外的信息，用于在按扭外寻址到按钮。
    public int stateNow;
    public MultiStateButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int stateNum, ResourceLocation pResourceLocation, OnPress pOnPress) {
        this(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart,stateNum,0, pResourceLocation, pOnPress);
    }
    public MultiStateButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int stateNum, int order, ResourceLocation pResourceLocation, OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pResourceLocation, pOnPress);
        this.stateNum = stateNum;
        this.order = order;
        this.stateNow = 0;
    }
    public void updateData(int mode){
        this.stateNow = mode;
    }

    @Override
    public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int i = yTexStart + yDiffTex * stateNow;

        RenderSystem.enableDepthTest();
        pGuiGraphics.blit(resourceLocation, getX(), getY(), (float)xTexStart, (float)i, width, height, textureWidth, textureHeight);
    }

    // 按顺序变换状态
    public int changeState(){
        this.stateNow = (this.stateNow + 1) % this.stateNum;
        return this.stateNow;
    }

    public void setState(int state){
        this.stateNow = state;
    }

    public int getOrder(){
        return this.order;
    }

    public boolean isHover(int x, int y){
        return x > this.getX() && x <= this.getX() + width && y > this.getY() && y <= this.getY() + height;
    }
}
