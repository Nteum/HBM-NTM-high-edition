package com.hbm.gui.screen.component;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * 实现的功能和ImageButton类似，但原版ImageButton无法指定渲染的横向位置，因此使用一个额外的button
 * */
public class ExtendedButton extends Button {
    protected ResourceLocation texture;
    protected int texWidth;
    protected int texHeight;
    protected int[] texPos1;
    protected int[] texPos2;
    // 记录按钮是否被按下去
    public boolean isOn = false;
    public ExtendedButton(int pX, int pY, int pWidth, int pHeight, int xTex, int yTex, int xTex1, int yTex1, ResourceLocation tex, OnPress pOnPress) {
        this(pX, pY, pWidth, pHeight, xTex, yTex, xTex1, yTex1, 256, 256, tex, pOnPress);
    }
    public ExtendedButton(int pX, int pY, int pWidth, int pHeight, int xTex, int yTex, int xTex1, int yTex1, int texWidth, int texHeight, ResourceLocation tex, OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight, CommonComponents.EMPTY, pOnPress, DEFAULT_NARRATION);
        texture = tex;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
        this.texPos1 = new int[]{xTex, yTex};
        this.texPos2 = new int[]{xTex1, yTex1};
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int[] texPos = !isOn ? texPos1 : texPos2;
        this.renderTexture(pGuiGraphics, texture, this.getX(), this.getY(), texPos[0], texPos[1], 0, this.width, this.height, texWidth, texHeight);
    }

    public boolean toggle(){
        isOn = !isOn;
        return isOn;
    }
}
