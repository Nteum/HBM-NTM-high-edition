package com.hbm.api.fluid;

import com.hbm.HBMKey;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
//基础流体系统，里面包含多个FluidTank
public class BaseFluidHandler implements IFluidHandler, INBTSerializable<CompoundTag> {
    FluidTank[] tanks;
    public BaseFluidHandler(int tankNum, int capacity){
        tanks = new FluidTank[tankNum];
        for (int i = 0; i < tankNum; i++) {
            tanks[i] = new FluidTank(capacity);
        }
    }
    @Override
    public int getTanks() {
        return tanks.length;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return tanks[tank].getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        return tanks[tank].getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return tanks[tank].isFluidValid(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        int filled = 0;
        for (FluidTank tank : tanks) {
            filled = tank.fill(resource,action);
            if (filled > 0)break;
        }
        return filled;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        FluidStack fluidStack = FluidStack.EMPTY;
        for (FluidTank tank : tanks) {
            fluidStack = tank.drain(resource, action);
            if (fluidStack != FluidStack.EMPTY)break;
        }
        return fluidStack;
    }
    // 这个函数是直接从FluidTank中取流体的，然而本类有多个流体槽，在不指定哪个流体槽的前提下难以判断
    // 这个函数指从第一个tank中获取
    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        return drain(0, maxDrain, action);
    }
    //指定流体槽位抽取流体
    public @NotNull FluidStack drain(int tank, int maxDrain, FluidAction action) {
        return tanks[tank].drain(maxDrain,action);
    }
    public void setTankByNbt(int tank, CompoundTag nbt){
        this.tanks[tank].readFromNBT(nbt);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        int length = tanks.length;
        compoundTag.putInt(HBMKey.NUM, length);
        for (int i = 0; i < length; i++) {
            compoundTag.put(String.valueOf(i),tanks[i].writeToNBT(new CompoundTag()));
        }
        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        int length = nbt.getInt(HBMKey.NUM);
        for (int i = 0; i < length; i++) {
            this.tanks[i].readFromNBT(nbt.getCompound(String.valueOf(i)));
        }
    }
}
