package com.hbm.gui.screen.widget;

import com.hbm.HBMLang;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

// 类似进度条的功能，也可以用于显示能量条、液体条
public class BarProgress extends AbstractWidget {
    int pU;
    int pV;
    int color = 0xFFFFFFFF;
    int textureWidth;
    int textureHeight;
    public double progress = 0;
    public int maxProgress;
    ResourceLocation texture;
    boolean isVertical;
    public BarProgress(int pX, int pY, int pWidth, int pHeight, int pU, int pV, ResourceLocation texture, Component pMessage){
        this(pX,pY,pWidth,pHeight,pU,pV, 256, 256, texture,pMessage,true);
    }
    public BarProgress(int pX, int pY, int pWidth, int pHeight, int pU, int pV, int pTextWidth, int pTextHeight, ResourceLocation texture, Component pMessage, boolean isVertical) {
        super(pX, pY, pWidth, pHeight, pMessage);
        this.pU = pU;
        this.pV = pV;
        this.textureWidth = pTextWidth;
        this.textureHeight = pTextHeight;
        this.texture = texture;
        this.isVertical = isVertical;
        if (Objects.equals(pMessage, Component.empty())){
            this.setTooltip(Tooltip.create(Component.translatable(HBMLang.TOOLTIP_LEFT_TIME.key(), (int)(maxProgress - progress / 20))));
        }
    }
    public void updateData(){
        // 更新tooltip
        if (Objects.equals(this.getMessage(), Component.empty())){
            this.setTooltip(Tooltip.create(Component.translatable(HBMLang.TOOLTIP_LEFT_TIME.key(), ((maxProgress - progress) / 20))));
        }
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (progress == 0.0)return;
        int barLen;

        setColor(getFGColor(),pGuiGraphics);
        if (this.isVertical){
            barLen = (int) (height * progress / maxProgress);
            pGuiGraphics.blit(texture,getX(),getY() + height - barLen,width,barLen,pU,pV+height-barLen,width,barLen,textureWidth,textureHeight);
        }
        else{
            barLen = (int) (width * progress / maxProgress);
            pGuiGraphics.blit(texture,getX(),getY(),barLen,height,pU,pV,barLen,height,textureWidth,textureHeight);
        }
        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }



    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
        pNarrationElementOutput.add(NarratedElementType.TITLE, this.getMessage());
    }

    static void setColor(int color, GuiGraphics guiGraphics){
        float r = ((color & 0xff0000) >> 16) / 255F;
        float g = ((color & 0x00ff00) >> 8) / 255F;
        float b = ((color & 0x0000ff)) / 255F;
        guiGraphics.setColor(r,g,b, 1.0F);
    }
}
