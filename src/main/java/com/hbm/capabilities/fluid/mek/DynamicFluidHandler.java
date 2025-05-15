package com.hbm.capabilities.fluid.mek;

import com.hbm.api.IContentsListener;
import com.hbm.api.annotations.NothingNullByDefault;
import com.hbm.api.enums.Action;
import com.hbm.api.fluid.mek.IExtendedFluidTank;
import com.hbm.api.fluid.mek.IMekanismFluidHandler;
import com.hbm.blockentity.base.TransmitterBlockEntity.InteractPredicate;
import com.hbm.capabilities.DynamicHandler;
import net.minecraft.core.Direction;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

@NothingNullByDefault
public class DynamicFluidHandler extends DynamicHandler<IExtendedFluidTank> implements IMekanismFluidHandler {

    public DynamicFluidHandler(Function<Direction, List<IExtendedFluidTank>> tankSupplier, InteractPredicate canExtract, InteractPredicate canInsert,
                               @Nullable IContentsListener listener) {
        super(tankSupplier, canExtract, canInsert, listener);
    }

    @Override
    public List<IExtendedFluidTank> getFluidTanks(@Nullable Direction side) {
        return containerSupplier.apply(side);
    }

    @Override
    public FluidStack insertFluid(int tank, FluidStack stack, @Nullable Direction side, Action action) {
        //If we can insert into the specific tank from that side, try to. Otherwise exit
        return canInsert.test(tank, side) ? IMekanismFluidHandler.super.insertFluid(tank, stack, side, action) : stack;
    }

    @Override
    public FluidStack extractFluid(int tank, int amount, @Nullable Direction side, Action action) {
        //If we can extract from a specific tank from a given side, try to. Otherwise exit
        return canExtract.test(tank, side) ? IMekanismFluidHandler.super.extractFluid(tank, amount, side, action) : FluidStack.EMPTY;
    }
}