package com.hbm.core.capability.fluid;

import com.hbm.HBMKey;
import com.hbm.api.IContentsListener;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class BasicFluidHandler implements IFluidHandler, IContentsListener, INBTSerializable<CompoundTag> {
    List<FluidTank> tanks;
    private final Predicate<FluidStack>[] fluidFilters;

    public BasicFluidHandler(int tankNum, int capacity){
        tanks = new ArrayList<>(tankNum);
        for (int i = 0; i < tankNum; i++) {
            tanks.add(new FluidTank(capacity));
        }
        fluidFilters = new Predicate[tankNum];
    }
    @Override
    public int getTanks() {
        return tanks.size();
    }
    // 单独获得tank以便单独操作
    public FluidTank getFluidTank(int tank){
        return tank >= 0 && tank < getTanks() ? tanks.get(tank) : null;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return tank >= 0 && tank < getTanks() ? tanks.get(tank).getFluid() : FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        return tank >= 0 && tank < getTanks() ? tanks.get(tank).getCapacity() : 0;
    }

    public void setFluidFilter(int slot, Predicate<FluidStack> filter) {
        this.fluidFilters[slot] = filter;
    }
    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        Predicate<FluidStack> filter = fluidFilters[tank];
        return filter == null || filter.test(stack);
    }

    /**
     * 注入流体，默认依次匹配注入。如果需要单注入一个tank，可以获取FluidTank单独注入。
     */

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        FluidStack input = resource.copy();

        IntList typeMatchedTanks = new IntArrayList();
        IntList emptyTanks = new IntArrayList();
        for (int i = 0; i < tanks.size(); i++) {
            if (!isFluidValid(i, input)) continue;
            if (tanks.get(i).isEmpty()) emptyTanks.add(i);
            else if (tanks.get(i).isFluidValid(input)) typeMatchedTanks.add(i);
        }
        for (Integer tank : typeMatchedTanks) {
            if (!input.isEmpty()) input.shrink(tanks.get(tank).fill(input,action));
        }
        for (Integer tank : emptyTanks) {
            if (!input.isEmpty()) input.shrink(tanks.get(tank).fill(input,action));
        }
        if (action.execute() && input.getAmount() != resource.getAmount())
            onContentsChanged();
        return resource.getAmount() - input.getAmount();
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return FluidStack.EMPTY;
        int drainAmount = 0;
        for (FluidTank tank : tanks) {
            if (tank != null && tank.getFluid().isFluidEqual(resource)) {
                drainAmount += tank.drain(resource.getAmount() - drainAmount, action).getAmount();
            }
            if (drainAmount == resource.getAmount()) break;
        }
        FluidStack resultStack = resource.copy();
        resultStack.setAmount(drainAmount);
        if (action.execute() && drainAmount > 0)
            onContentsChanged();
        return resultStack;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        FluidStack resultStack = FluidStack.EMPTY;
        for (int i = 0; i < tanks.size(); i++) {
            FluidTank tank = tanks.get(i);
            if (tank != null && !tank.isEmpty() && (resultStack.isEmpty() || tank.getFluid().isFluidEqual(resultStack))){
                FluidStack drainStack = tank.drain(maxDrain, action);
                if (resultStack.isEmpty())
                    resultStack = drainStack;
                else
                    resultStack.grow(drainStack.getAmount());
                maxDrain -= drainStack.getAmount();
                if (maxDrain == 0)break;
            }
        }
        if (action.execute() && !resultStack.isEmpty())
            onContentsChanged();
        return resultStack;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        int length = tanks.size();
        compoundTag.putInt(HBMKey.NUM, length);
        for (int i = 0; i < length; i++) {
            CompoundTag tag = tanks.get(i).writeToNBT(new CompoundTag());
            compoundTag.put(String.valueOf(i),tag);
        }
        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        int length = nbt.getInt(HBMKey.NUM);
        for (int i = 0; i < length; i++) {
            CompoundTag tag = nbt.getCompound(String.valueOf(i));
            tanks.get(i).readFromNBT(tag);
        }
    }

    @Override
    public void onContentsChanged() {

    }
}
