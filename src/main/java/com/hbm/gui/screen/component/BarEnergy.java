package com.hbm.gui.screen.component;

import com.hbm.HBMLang;
import com.hbm.utils.BobMathUtil;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.TickEvent;

public class BarEnergy extends BarProgress {
    public long energy = 0;
    public BarEnergy(int pX, int pY, int pWidth, int pHeight, int pU, int pV, ResourceLocation texture, Component pMessage) {
        this(pX, pY, pWidth, pHeight, pU, pV, 256, 256, texture, pMessage,true);
    }
    public BarEnergy(int pX, int pY, int pWidth, int pHeight, int pU, int pV, int pTextWidth, int pTextHeight, ResourceLocation texture, Component pMessage, boolean isVertical) {
        super(pX, pY, pWidth, pHeight, pU, pV, pTextWidth, pTextHeight, texture, pMessage, isVertical);
        if (pMessage.equals(Component.empty())){
            this.setTooltip(Tooltip.create(Component.translatable(HBMLang.TOOLTIP_ENERGY.getTranslationKey(), BobMathUtil.getShortNumber((long) progress))));
//            this.setMessage(Component.translatable(HBMLang.TOOLTIP_ENERGY.getTranslationKey(), BobMathUtil.getShortNumber(energy)));
        }
    }

    @Override
    public void updateData() {
        if (this.getMessage().equals(Component.empty())){
            this.setTooltip(Tooltip.create(Component.translatable(HBMLang.TOOLTIP_ENERGY.getTranslationKey(), BobMathUtil.getShortNumber((long) progress))));
        }
    }
}
