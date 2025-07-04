package com.hbm.api.fluid;

import net.minecraft.core.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
/**
 * 1. 关于函数中的tank编号：对于没有tank编号的接口，实现方式是从第一个tank开始找到第一个符合标准的tank处理，
 * 对于有tank编号的接口，tank编号代表着绝对的编号，即使有些位置的tank编号不被允许操作。
 * 2. 关于方向direction：指明direction，则表明从外界的表面交互，如果方向为null，则是在内交互。
 * */
public interface ISidedFluidHandler extends IFluidHandler {
    enum FluidOperation{
        INPUT, OUTPUT;
        public boolean isFill(){return this==INPUT;}
        public boolean isDrain(){return this==OUTPUT;}
    }

    IExtendedFluidTank getFluidTank(int tank,@Nullable Direction side);
    default IExtendedFluidTank getFluidTank(int tank){
        return getFluidTank(tank,null);
    }

    void setFluidInTank(int tank, FluidStack stack, @Nullable Direction side);
    default void setFluidInTank(int tank, FluidStack stack){
        setFluidInTank(tank,stack,null);
    }

    @Override
    default int getTanks(){return getTanks(null);}

    int getTanks(@Nullable Direction side);

    @Override
    default @NotNull FluidStack getFluidInTank(int tank){
        return getFluidInTank(tank,null);
    }

    @NotNull FluidStack getFluidInTank(int tank, @Nullable Direction side);

    @Override
    default int getTankCapacity(int tank){
        return getTankCapacity(tank,null);
    }

    int getTankCapacity(int tank, @Nullable Direction side);

    boolean isFluidValid(int tank, @NotNull FluidStack stack, @Nullable Direction side);

    @Override
    default boolean isFluidValid(int tank, @NotNull FluidStack stack){
        return isFluidValid(tank, stack, null);
    }

    int fill(int tank, FluidStack resource, FluidAction action, @Nullable Direction side);

    default int fill(int tank, FluidStack resource, FluidAction action){
        return fill(tank, resource, action, null);
    }
    @Override
    default int fill(FluidStack resource, FluidAction action){
        return fill(0,resource,action);
    }

    FluidStack drain(int tank, int maxDrain, FluidAction action, @Nullable Direction side);

    default FluidStack drain(int tank, int maxDrain, FluidAction action){
        return drain(tank,maxDrain,action,null);
    }

    @Override
    default @NotNull FluidStack drain(int maxDrain, FluidAction action){
        return drain(0,maxDrain,action);
    }

    @Override
    default @NotNull FluidStack drain(FluidStack resource, FluidAction action){
        return isFluidValid(0,resource) ? drain(resource.getAmount(),action) : FluidStack.EMPTY;
    }
}
