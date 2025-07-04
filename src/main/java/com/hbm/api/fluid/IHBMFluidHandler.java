package com.hbm.api.fluid;

import net.minecraft.core.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface IHBMFluidHandler extends ISidedFluidHandler{
    //流体功能是否可用，如不可用，则无法进行操作
    default boolean canHandleFluid() {
        return true;
    }
    //存储的tank
    List<IExtendedFluidTank> getFluidTanks(@Nullable Direction side);

    @Override
    default int getTanks(@Nullable Direction side){
        return getFluidTanks(side).size();
    }

    @Override
    default IExtendedFluidTank getFluidTank(int tank, @Nullable Direction side){
        List<IExtendedFluidTank> tanks = getFluidTanks(side);
        return tank >= 0 && tank < tanks.size() ? tanks.get(tank) : null;
    }

    @Override
    default @NotNull FluidStack getFluidInTank(int tank, @Nullable Direction side){
        IExtendedFluidTank fluidTank = getFluidTank(tank, side);
        return fluidTank == null ? FluidStack.EMPTY : fluidTank.getFluid();
    }

    @Override
    default void setFluidInTank(int tank, FluidStack stack, @Nullable Direction side){
        IExtendedFluidTank fluidTank = getFluidTank(tank, side);
        if (fluidTank != null) {
            fluidTank.setStack(stack);
        }
    }

    @Override
    default int getTankCapacity(int tank, @Nullable Direction side){
        IExtendedFluidTank fluidTank = getFluidTank(tank, side);
        return fluidTank == null ? 0 : fluidTank.getCapacity();
    }

    @Override
    default boolean isFluidValid(int tank, @NotNull FluidStack stack, @Nullable Direction side){
        IExtendedFluidTank fluidTank = getFluidTank(tank, side);
        return fluidTank != null && fluidTank.isFluidValid(stack);
    }

    @Override
    default int fill(int tank, FluidStack resource, FluidAction action, @Nullable Direction side){
        IExtendedFluidTank fluidTank = getFluidTank(tank, side);
        return fluidTank==null ? 0 : fluidTank.fill(resource,action);
    }

    @Override
    default FluidStack drain(int tank, int maxDrain, FluidAction action, @Nullable Direction side){
        IExtendedFluidTank fluidTank = getFluidTank(tank, side);
        return fluidTank==null ? FluidStack.EMPTY : fluidTank.drain(maxDrain,action);
    }
}
