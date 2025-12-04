package com.hbm.gui.screen.widget;

import com.hbm.HBMLang;
import com.hbm.Inventory.fluid.ExtendedFluidType;
import com.hbm.Inventory.fluid.ModFluids;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.Objects;
import java.util.function.IntSupplier;

public class BarFluid extends BarProgress{
    public Fluid fluid;
    IntSupplier xgetter = this::getX;
    IntSupplier ygetter = this::getY;

    public BarFluid(int pX, int pY, int pWidth, int pHeight, Fluid fluid){
        this(pX, pY, pWidth, pHeight, fluid, Component.empty());
    }
    /**
     * 主要的构造函数
     * 之所以传入的是supplier，是为了保证组件的位置跟随窗口大小变化。
     * 能量条和进度条直接传入数字并没问题，但不知为什么流体条就会出问题，只有通过supplier才能解决。
     * */
    public BarFluid(IntSupplier supplierX, IntSupplier supplierY, int pWidth, int pHeight, Fluid fluid){
        this(supplierX.getAsInt(), supplierY.getAsInt(), pWidth, pHeight, fluid, Component.empty());
        this.xgetter = supplierX;
        this.ygetter = supplierY;
    }
    public BarFluid(int pX, int pY, int pWidth, int pHeight, Fluid fluid, Component pMessage) {
        super(pX, pY, pWidth, pHeight, 0, 0,16,16, getFluidTexture(fluid), pMessage,true);
        this.fluid = fluid;
        if (Objects.equals(pMessage, Component.empty())){
            MutableComponent fluidName = Component.translatable(this.fluid.getFluidType().getDescriptionId());
            this.setTooltip(Tooltip.create(Component.translatable(HBMLang.GUI_TOOLTIP_FLUID.key(), fluidName, progress)));
//            this.setTooltip(Tooltip.create(Component.translatable(this.fluid.getFluidType().getDescriptionId()).append(Component.translatable(HBMLang.TOOLTIP_TANK_VOLUME.key(), progress))));
        }
    }

    @Override
    public void updateData() {
        this.texture = getFluidTexture(this.fluid);
        if (Objects.equals(this.getMessage(), Component.empty())){
            MutableComponent fluidName = Component.translatable(this.fluid.getFluidType().getDescriptionId());
            this.setTooltip(Tooltip.create(Component.translatable(HBMLang.GUI_TOOLTIP_FLUID.key(), fluidName, progress)));
//            this.setTooltip(Tooltip.create(Component.translatable(HBMLang.TOOLTIP_TANK_VOLUME.key(),this.fluid.getFluidType().getDescriptionId(), progress)));
        }
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (progress == 0.0 || this.fluid.isSame(Fluids.EMPTY))return;
        int barLen;

        setColor(getFluidColor(this.fluid),pGuiGraphics);
        if (this.isVertical){
            barLen = (int) (height * progress / maxProgress);
            pGuiGraphics.blit(texture, xgetter.getAsInt(),ygetter.getAsInt() + height - barLen,width,barLen,pU,pV+height-barLen,width,barLen,256,256);
        }
        else{
            barLen = (int) (width * progress / maxProgress);
            pGuiGraphics.blit(texture, xgetter.getAsInt(),ygetter.getAsInt(),barLen,height,pU,pV,barLen,height,256,256);
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
