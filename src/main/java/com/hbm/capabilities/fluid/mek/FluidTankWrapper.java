package com.hbm.capabilities.fluid.mek;

import com.hbm.api.annotations.NothingNullByDefault;
import com.hbm.api.enums.Action;
import com.hbm.api.enums.AutomationType;
import com.hbm.api.fluid.mek.IExtendedFluidTank;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;
//其实用处不大，mek中用于和化学元素相结合。
@NothingNullByDefault
public class FluidTankWrapper implements IExtendedFluidTank {

    private final IExtendedFluidTank internal;
    private final BooleanSupplier insertCheck;
//    private final MergedTank mergedTank;

    public FluidTankWrapper(IExtendedFluidTank internal, BooleanSupplier insertCheck) {
        //TODO: Do we want to short circuit it so that if we are not empty it allows for inserting before checking the insertCheck
//        this.mergedTank = mergedTank;
        this.internal = internal;
        this.insertCheck = insertCheck;
    }

//    public MergedTank getMergedTank() {
//        return mergedTank;
//    }

    @Override
    public void setStack(FluidStack stack) {
        internal.setStack(stack);
    }

    @Override
    public void setStackUnchecked(FluidStack stack) {
        internal.setStackUnchecked(stack);
    }

    @Override
    public FluidStack insert(FluidStack stack, Action action, AutomationType automationType) {
        //Only allow inserting if we pass the check
        return insertCheck.getAsBoolean() ? internal.insert(stack, action, automationType) : stack;
    }

    @Override
    public FluidStack extract(int amount, Action action, AutomationType automationType) {
        return internal.extract(amount, action, automationType);
    }

    @Override
    public void onContentsChanged() {
        internal.onContentsChanged();
    }

    @Override
    public int setStackSize(int amount, Action action) {
        return internal.setStackSize(amount, action);
    }

    @Override
    public int growStack(int amount, Action action) {
        return internal.growStack(amount, action);
    }

    @Override
    public int shrinkStack(int amount, Action action) {
        return internal.shrinkStack(amount, action);
    }

    @Override
    public boolean isEmpty() {
        return internal.isEmpty();
    }

    @Override
    public void setEmpty() {
        internal.setEmpty();
    }

    @Override
    public boolean isFluidEqual(FluidStack other) {
        return internal.isFluidEqual(other);
    }

    @Override
    public int getNeeded() {
        return internal.getNeeded();
    }

    @Override
    public CompoundTag serializeNBT() {
        return internal.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        internal.deserializeNBT(nbt);
    }

    @NotNull
    @Override
    public FluidStack getFluid() {
        return internal.getFluid();
    }

    @Override
    public int getFluidAmount() {
        return internal.getFluidAmount();
    }

    @Override
    public int getCapacity() {
        return internal.getCapacity();
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return internal.isFluidValid(stack);
    }
}