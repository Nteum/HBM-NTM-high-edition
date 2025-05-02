package com.hbm.gui.screen.component;

import com.hbm.gui.screen.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

// 类似进度条的功能，也可以用于显示能量条、液体条
public abstract class ProgressBar extends AbstractWidget {
    int pU;
    int pV;
    int textureWidth;
    int textureHeight;
    double progress = 0;
    ResourceLocation texture;
    boolean isVertical;
    public ProgressBar(int pX, int pY, int pWidth, int pHeight, int pU, int pV, ResourceLocation texture, Component pMessage){
        this(pX,pY,pWidth,pHeight,pU,pV,pWidth,pHeight,texture,pMessage,true);
    }
    public ProgressBar(int pX, int pY, int pWidth, int pHeight, int pU, int pV,int pTextWidth,int pTextHeight, ResourceLocation texture, Component pMessage, boolean isVertical) {
        super(pX, pY, pWidth, pHeight, pMessage);
        this.pU = pU;
        this.pV = pV;
        this.textureWidth = pTextWidth;
        this.textureHeight = pTextHeight;
        this.texture = texture;
        this.isVertical = isVertical;
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (progress == 0.0)return;
        int barLen;
        int barLenUV;

        setColor(getFGColor(),pGuiGraphics);
        if (this.isVertical){
            barLen = (int) (height * progress);
            barLenUV = (int) (textureHeight * progress);
            pGuiGraphics.blit(texture,getX(),getY() + height - barLen,width,barLen,pU,pV+textureHeight-barLenUV,textureWidth,barLenUV,textureWidth,textureHeight);
        }
        else{
            barLen = (int) (width * progress);
            barLenUV = (int) (textureWidth * progress);
            pGuiGraphics.blit(texture,getX() + barLen,getY(),barLen,height,pU,pV,barLenUV,textureHeight,textureWidth,textureHeight);
        }
        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
    static void setColor(int color, GuiGraphics guiGraphics){
        float r = ((color & 0xff0000) >> 16) / 255F;
        float g = ((color & 0x00ff00) >> 8) / 255F;
        float b = ((color & 0x0000ff)) / 255F;
        guiGraphics.setColor(r,g,b, 1.0F);
    }
}
