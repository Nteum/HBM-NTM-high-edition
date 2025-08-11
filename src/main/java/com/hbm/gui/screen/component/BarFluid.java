package com.hbm.gui.screen.component;

import com.hbm.HBMLang;
import com.hbm.Inventory.fluid.ExtendedFluidType;
import com.hbm.Inventory.fluid.ModFluids;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.event.TickEvent;

import java.util.Objects;

public class BarFluid extends BarProgress{
    public Fluid fluid;
    // 这个构造函数只是为了继承，其实不会用到
//    public BarFluid(int pX, int pY, int pWidth, int pHeight, int pU, int pV, ResourceLocation texture, Component pMessage) {
//        this(pX, pY, pWidth, pHeight, pU, pV,256,256, texture, pMessage,true);
//    }
    public BarFluid(int pX, int pY, int pWidth, int pHeight, Fluid fluid){
        this(pX, pY, pWidth, pHeight, fluid, Component.empty());
    }
    public BarFluid(int pX, int pY, int pWidth, int pHeight, Fluid fluid, Component pMessage) {
        super(pX, pY, pWidth, pHeight, 0, 0,16,16, getFluidTexture(fluid), pMessage,true);
        this.fluid = fluid;
        if (Objects.equals(pMessage, Component.empty())){
            this.setTooltip(Tooltip.create(Component.translatable(this.fluid.getFluidType().getDescriptionId()).append(Component.translatable(HBMLang.TOOLTIP_TANK_VOLUME.getTranslationKey(), progress))));
//            this.setMessage(Component.translatable(this.fluid.getFluidType().getDescriptionId()).append(Component.translatable(HBMLang.TOOLTIP_TANK_VOLUME.getTranslationKey(), progress)));
        }
    }

//    public BarFluid(int pX, int pY, int pWidth, int pHeight, int pU, int pV, int pTextWidth, int pTextHeight, ResourceLocation texture, Component pMessage, boolean isVertical) {
//        super(pX, pY, pWidth, pHeight, pU, pV, pTextWidth, pTextHeight, texture, pMessage, isVertical);
//    }


    @Override
    public void updateData() {
        this.texture = getFluidTexture(this.fluid);
        if (Objects.equals(this.getMessage(), Component.empty())){
            this.setTooltip(Tooltip.create(Component.translatable(HBMLang.TOOLTIP_TANK_VOLUME.getTranslationKey(),this.fluid.getFluidType().getDescriptionId(), progress)));
//            this.setTooltip(Tooltip.create(Component.translatable(this.fluid.getFluidType().getDescriptionId()).append(Component.translatable(HBMLang.TOOLTIP_TANK_VOLUME.getTranslationKey(), progress))));
        }
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (progress == 0.0 || this.fluid.isSame(Fluids.EMPTY))return;
        int barLen;

//        int tintColor = ((ExtendedFluidType) this.fluid.getFluidType()).tintColor;
        setColor(getFluidColor(this.fluid),pGuiGraphics);
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

    public static ResourceLocation getFluidTexture(Fluid fluid){
        if (fluid.getFluidType() instanceof ExtendedFluidType fluidType)
            return fluidType.getGUITexture();
        else if (fluid.isSame(Fluids.WATER))
            return ExtendedFluidType.GUI_WATER;
        else if (fluid.isSame(Fluids.LAVA))
            return ExtendedFluidType.GUI_LAVA;
        else
            return ExtendedFluidType.GUI_MILK;
    }

    public int getFluidColor(Fluid fluid){
        if (fluid.getFluidType() instanceof ExtendedFluidType fluidType)
            return fluidType.tintColor;
        else if (fluid.isSame(Fluids.WATER))
            return ModFluids.water.tintColor;
        else if (fluid.isSame(Fluids.LAVA))
            return ModFluids.lava.tintColor;
        else
            return ModFluids.milk.tintColor;
    }
}
