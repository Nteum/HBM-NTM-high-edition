package com.hbm.api.fluid;

import com.hbm.HBMKey;
import com.hbm.api.interferences.IDefaultFacing;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
/**
 * 1. 关于函数中的tank编号：对于没有tank编号的接口，实现方式是从第一个tank开始找到第一个符合标准的tank处理，
 * 对于有tank编号的接口，tank编号代表着绝对的编号，即使有些位置的tank编号不被允许操作。
 * 2. 关于方向direction：指明direction，则表明从外界的表面交互，如果方向为null，则是在内交互。
 * */
public interface ISidedFluidHandler<T extends IExtendedFluidTank> extends IFluidHandler, IDefaultFacing {

    default boolean canHandleFluid() {
        return true;
    }
    /**
     * 获取某个面可以提供的FluidTank列表
     * 1. 返回的FluidTank可以被修改
     * 2. 后面其他函数也需要参数slot查询和修改对应的FluidTank，因此slot不应该是个相对于某个面的相对值，因此返回的列表长度应该始终都是总tank数量
     * 因此在某个面不能提供服务的tank，它不是不放入列表，而是对应的位置设为null
     * */
    List<T> getFluidTanks(@Nullable Direction side);

    @Override
    default int getTanks() {
        return getFluidTanks(null).size();
    }
    @Nullable
    default IExtendedFluidTank getFluidTank(int tank, @Nullable Direction side){
        List<T> tanks = getFluidTanks(side);
        return tank >= 0 && tank < tanks.size() ? tanks.get(tank) : null;
    }

    default @NotNull FluidStack getFluidInTank(int tank, @Nullable Direction side){
        List<T> tanks = getFluidTanks(side);
        return tank >= 0 && tank < tanks.size() ? tanks.get(tank).getFluid() : FluidStack.EMPTY;
    }

    @Override
    default @NotNull FluidStack getFluidInTank(int tank){
        return getFluidInTank(tank,null);
    }

    default void setFluidInTank(int tank, FluidStack stack, @Nullable Direction side){
        IExtendedFluidTank fluidTank = getFluidTank(tank, side);
        if (fluidTank != null)fluidTank.setStack(stack);
    }
    default void setFluidInTank(int tank, FluidStack stack){
        setFluidInTank(tank,stack,null);
    }

    default int getTankCapacity(int tank, @Nullable Direction side){
        IExtendedFluidTank fluidTank = getFluidTank(tank, side);
        return fluidTank != null ? fluidTank.getCapacity() : 0;
    }

    @Override
    default int getTankCapacity(int tank){
        return getTankCapacity(tank,null);
    }

    default boolean isFluidValid(int tank, @NotNull FluidStack stack, @Nullable Direction side){
        IExtendedFluidTank fluidTank = getFluidTank(tank, side);
        return fluidTank != null && fluidTank.isFluidValid(stack);
    }

    @Override
    default boolean isFluidValid(int tank, @NotNull FluidStack stack){
        return isFluidValid(tank, stack, null);
    }

    default int fill(int tank, FluidStack resource, FluidAction action, @Nullable Direction side){
        IExtendedFluidTank fluidTank = getFluidTank(tank, side);
        return fluidTank == null ? 0 : fluidTank.fill(resource, action);
    }

    default int fill(int tank, FluidStack resource, FluidAction action){
        return fill(tank, resource, action, null);
    }
    // 不指明哪个tank向内输入流体，就找任何可以输入的tank输入，直到预计的量输入完，或者遍历所有tank
    // 优先向液体内容和注入液体类型相同的tank中输入
    // 输入的resource可以被改变
    @Override
    default int fill(FluidStack resource, FluidAction action){
        int amount = resource.getAmount();
        List<T> tanks = getFluidTanks(null);
        IntList typeMatchTanks = new IntArrayList();
        IntList emptyTanks = new IntArrayList();
        for (int i = 0; i < tanks.size(); i++) {
            IExtendedFluidTank tank = tanks.get(i);
            if (tank != null)
                if (tank.getFluid().isEmpty())
                    emptyTanks.add(i);
                else if (tank.isFluidValid(resource)) {
                    typeMatchTanks.add(i);
                }
        }
        for (Integer tank : typeMatchTanks) {
            if (!resource.isEmpty())
                resource.shrink(tanks.get(tank).fill(resource,action));
        }
        for (Integer tank : emptyTanks) {
            if (!resource.isEmpty())
                resource.shrink(tanks.get(tank).fill(resource,action));
        }
        return amount - resource.getAmount();
    }

    default FluidStack drain(int tank, int maxDrain, FluidAction action, @Nullable Direction side){
        IExtendedFluidTank fluidTank = getFluidTank(tank, side);
        return fluidTank == null ? FluidStack.EMPTY : fluidTank.drain(maxDrain,action);
    }

    default FluidStack drain(int tank, int maxDrain, FluidAction action){
        return drain(tank,maxDrain,action,null);
    }
    // 不指定哪个tank吸取流体，方案是找到第一个有流体的tank，以其中的流体为目标挨个tank吸收流体
    // 不过这个功能非常的迷惑，感觉几乎是不可能被使用的，谁会在不知道吸出什么液体的情况下盲吸？
    @Override
    default @NotNull FluidStack drain(int maxDrain, FluidAction action){
        FluidStack resultStack = FluidStack.EMPTY;
        List<T> tanks = getFluidTanks(null);
        for (IExtendedFluidTank tank : tanks) {
            if (tank != null && !tank.getFluid().isEmpty() && (resultStack.isEmpty() || tank.isFluidValid(resultStack))){
                FluidStack drainStack = tank.drain(maxDrain, action);
                if (resultStack.isEmpty())
                    resultStack = drainStack;
                else
                    resultStack.grow(drainStack.getAmount());
                maxDrain -= drainStack.getAmount();
                if (maxDrain == 0)break;
            }
        }
        return resultStack;
    }
    // 指定流体类型的输出，也是逐个匹配
    @Override
    default @NotNull FluidStack drain(FluidStack resource, FluidAction action){
        if (resource.isEmpty())return FluidStack.EMPTY;
        int drainAmount = 0;
        List<T> tanks = getFluidTanks(null);
        for (IExtendedFluidTank tank : tanks) {
            if (tank != null && tank.isFluidValid(resource)){
                drainAmount += tank.drain(resource.getAmount() - drainAmount,action).getAmount();
            }
            if (drainAmount == resource.getAmount())break;
        }
        FluidStack resultStack = resource.copy();
        resultStack.setAmount(drainAmount);
        return resultStack;
    }
}
