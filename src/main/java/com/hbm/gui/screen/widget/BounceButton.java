package com.hbm.gui.screen.widget;

import net.minecraft.resources.ResourceLocation;

/**
 * 有“按下去”效果的按钮，过一定时间内弹起，按下去状态下再按不会有效果
 * 用于突出反馈效果，以及避免过于频繁地按动
 * */
public class BounceButton extends MultiStateButton{
    final int bounceTime;
    int counter = 0;
    public BounceButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int bounceTime, ResourceLocation pResourceLocation, OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, 2, pResourceLocation, pOnPress);
        this.bounceTime = bounceTime;
    }

    @Override
    public void onPress() {
        if (this.counter == 0)
            this.onPress.onPress(this);
        this.stateNow = 1;
    }

    public void tick(){
        if (this.counter == 0 && this.stateNow == 1 || this.counter > 0) this.counter ++;
        if (this.counter >= bounceTime) {
            this.counter = 0;
            this.stateNow = 0;
        }
    }
}
