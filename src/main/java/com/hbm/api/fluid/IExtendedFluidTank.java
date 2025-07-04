package com.hbm.api.fluid;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

public interface IExtendedFluidTank extends IFluidTank {
    void setStack(FluidStack stack);
}
