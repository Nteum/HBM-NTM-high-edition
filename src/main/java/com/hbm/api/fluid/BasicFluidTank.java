package com.hbm.api.fluid;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.function.Predicate;

public class BasicFluidTank extends FluidTank implements IExtendedFluidTank{
    int inputLimit;
    int outputLimit;
    public BasicFluidTank(int capacity) {
        super(capacity);
    }
    public BasicFluidTank(int capacity, int inputLimit, int outputLimit){
        this(capacity);
        this.inputLimit = inputLimit;
        this.outputLimit = outputLimit;
    }

    public BasicFluidTank(int capacity, Predicate<FluidStack> validator) {
        super(capacity, validator);
    }

    @Override
    public void setStack(FluidStack stack) {
        this.fluid = stack.copy();
    }
}
