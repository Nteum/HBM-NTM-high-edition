package com.hbm.core.capability.fluid;

import net.minecraft.core.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import static com.hbm.registries.RegistryHelper.contain;

public class SidedFluidWrapper implements IFluidHandler {
    private BasicFluidHandler fluidHandler;
    Direction side;
    private final int[] inputTanks;              // 允许从该面插入的槽位
    private final int[] outputTanks;             // 允许从该面提取的槽位
    public SidedFluidWrapper(BasicFluidHandler handler, Direction side, int[] inputSlots, int[] outputSlots){
        this.fluidHandler = handler;
        this.side = side;
        this.inputTanks = inputSlots;
        this.outputTanks = outputSlots;
    }
    @Override
    public int getTanks() {
        return this.fluidHandler.getTanks();
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return this.fluidHandler.getFluidInTank(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        return this.fluidHandler.getTankCapacity(tank);
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return contain(inputTanks, tank) && fluidHandler.isFluidValid(tank, stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        FluidStack fluidStack = resource.copy();
        int fill = 0;
        for (int inputTank : this.inputTanks) {
            fluidStack.setAmount(resource.getAmount() - fill);
            fill += this.fluidHandler.getFluidTank(inputTank).fill(fluidStack, action);
        }
        return fill;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        int drained = 0;
        FluidStack toDrain = resource.copy();
        for (int outputTank : this.outputTanks) {
            FluidStack result = this.fluidHandler.getFluidTank(outputTank).drain(toDrain, action);
            drained += result.getAmount();
            toDrain.shrink(result.getAmount());
        }
        if (drained <= 0) return FluidStack.EMPTY;
        FluidStack out = resource.copy();
        out.setAmount(drained);
        return out;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        int toDrain = maxDrain;
        FluidStack drained = FluidStack.EMPTY;
        for (int outputTank : this.outputTanks) {
            if (toDrain == 0) break;
            if (drained.isEmpty()){
                drained = this.fluidHandler.getFluidTank(outputTank).drain(toDrain, action);
            }else if (this.fluidHandler.getFluidInTank(outputTank).isFluidEqual(drained)){
                drained.grow(this.fluidHandler.getFluidTank(outputTank).drain(toDrain, action).getAmount());
            }
            toDrain = maxDrain - drained.getAmount();
        }
        return drained;
    }
}
