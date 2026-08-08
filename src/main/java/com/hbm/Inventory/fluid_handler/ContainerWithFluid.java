package com.hbm.Inventory.fluid_handler;

import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;
// 这么做主要用于那些只能向cachedcheck传入Container进行查找，却需要判断流体状态的配方
public class ContainerWithFluid extends RecipeWrapper {
    public IFluidHandler fluidHandler;
    public ContainerWithFluid(IItemHandlerModifiable inv) {
        super(inv);
    }
    public ContainerWithFluid(IItemHandlerModifiable inv, IFluidHandler iFluidHandler) {
        super(inv);
        fluidHandler = iFluidHandler;
    }
}
