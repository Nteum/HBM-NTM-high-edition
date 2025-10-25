package com.hbm.api.fluid;

import com.hbm.api.IContentsListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.function.Predicate;

public class BasicFluidTank extends FluidTank implements IExtendedFluidTank, INBTSerializable<CompoundTag> {
    int inputLimit;
    int outputLimit;
    IContentsListener listener = null;
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

    public void setListener(IContentsListener listener){
        this.listener = listener;
    }

    @Override
    public void setStack(FluidStack stack) {
        this.fluid = stack.copy();
    }

    public float getPercent(){
        return (float) this.getFluidAmount() / this.getCapacity();
    }

    @Override
    public CompoundTag serializeNBT() {
        return this.writeToNBT(new CompoundTag());
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.readFromNBT(nbt);
    }

    @Override
    protected void onContentsChanged() {
        if (listener != null) this.listener.onContentsChanged();
    }
}
