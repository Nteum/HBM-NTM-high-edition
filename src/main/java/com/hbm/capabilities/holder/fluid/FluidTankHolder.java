package com.hbm.capabilities.holder.fluid;

import com.hbm.api.RelativeSide;
import com.hbm.api.fluid.mek.IExtendedFluidTank;
import com.hbm.capabilities.holder.BasicHolder;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class FluidTankHolder extends BasicHolder<IExtendedFluidTank> implements IFluidTankHolder {

    FluidTankHolder(Supplier<Direction> facingSupplier) {
        super(facingSupplier);
    }

    void addTank(@NotNull IExtendedFluidTank tank, RelativeSide... sides) {
        addSlotInternal(tank, sides);
    }

    @NotNull
    @Override
    public List<IExtendedFluidTank> getTanks(@Nullable Direction direction) {
        return getSlots(direction);
    }
}