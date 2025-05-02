package com.hbm.gui.screen.component;

import com.hbm.gui.screen.RenderUtils;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.Objects;

public class FluidBar extends ProgressBar{
    Fluid fluid = Fluids.EMPTY;
    int fluidAmount = 0;

    public FluidBar(int pX, int pY, int pWidth, int pHeight, int pU, int pV, ResourceLocation texture, Component pMessage) {
        super(pX, pY, pWidth, pHeight, pU, pV, texture, pMessage);
    }
    public FluidBar(int pX, int pY, int pWidth, int pHeight, int pU, int pV,int pTextWidth,int pTextHeight, ResourceLocation texture, Component pMessage, boolean isVertical){
        super(pX,pY,pWidth,pHeight,pU,pV,pTextWidth,pTextHeight,texture,pMessage,isVertical);
        if (Objects.equals(pMessage, Component.empty())){
//            this.setMessage(Component.translatable(fluid.getFluidType().getDescriptionId()+" : "+fluidAmount+" mB"));
            this.setMessage(Component.translatable("%1$s : %2$s mB",fluid.getFluidType().getDescription(),fluidAmount));
        }
    }


    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
//        pNarrationElementOutput.add(NarratedElementType.HINT, Component.translatable("%1$s : %2$s mB",fluid.getFluidType().getDescription(),fluidAmount));
    }

    public void updateFluidTank(IFluidHandler tank, int num){
        FluidStack fluidStack = tank.getFluidInTank(num);
        int capacity = tank.getTankCapacity(num);
        fluid = fluidStack.getFluid();
        fluidAmount = fluidStack.getAmount();
        this.progress = (double) fluidAmount / capacity;
        this.packedFGColor = RenderUtils.fluidColor.get(fluid);
//        this.setTooltip(Tooltip.create(Component.translatable(fluid.getFluidType().getDescription()+" : "+fluidAmount+" mB")));
    }
}
