package com.hbm.api.fluid.mek;

import com.hbm.api.enums.Action;
import com.hbm.api.annotations.NothingNullByDefault;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

@NothingNullByDefault
public interface IExtendedFluidHandler extends IFluidHandler {

    void setFluidInTank(int tank, FluidStack stack);

    //向tank输入对应的fluidstack
    //返回尚未被输入的部分，如果完全被输入，则返回空的fluidstadk
    FluidStack insertFluid(int tank, FluidStack stack, Action action);

    //从tank获取amount量的流体
    //返回抽取的流体量，如果没有抽取则反馈空的fluidstack
    FluidStack extractFluid(int tank, int amount, Action action);

    default FluidStack insertFluid(FluidStack stack, Action action) {
        return ExtendedFluidHandlerUtils.insert(stack, action, this::getTanks, this::getFluidInTank, this::insertFluid);
    }

    default FluidStack extractFluid(int amount, Action action) {
        return ExtendedFluidHandlerUtils.extract(amount, action, this::getTanks, this::getFluidInTank, this::extractFluid);
    }

    default FluidStack extractFluid(FluidStack stack, Action action) {
        return ExtendedFluidHandlerUtils.extract(stack, action, this::getTanks, this::getFluidInTank, this::extractFluid);
    }

    /**
     * {@inheritDoc}
     *
     * Wrapped to properly use our method declarations
     */
    @Override
    @Deprecated
    default int fill(FluidStack stack, FluidAction action) {
        return stack.getAmount() - insertFluid(stack, Action.fromFluidAction(action)).getAmount();
    }

    /**
     * {@inheritDoc}
     *
     * Wrapped to properly use our method declarations
     */
    @Override
    @Deprecated
    default FluidStack drain(FluidStack stack, FluidAction action) {
        return extractFluid(stack, Action.fromFluidAction(action));
    }

    /**
     * {@inheritDoc}
     *
     * Wrapped to properly use our method declarations
     */
    @Override
    @Deprecated
    default FluidStack drain(int amount, FluidAction action) {
        return extractFluid(amount, Action.fromFluidAction(action));
    }
}