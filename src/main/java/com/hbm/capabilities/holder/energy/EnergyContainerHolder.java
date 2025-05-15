package com.hbm.capabilities.holder.energy;

import com.hbm.api.RelativeSide;
import com.hbm.api.energy.fe.IHBMEnergyStorage;
import com.hbm.capabilities.holder.BasicHolder;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class EnergyContainerHolder extends BasicHolder<IHBMEnergyStorage> implements IEnergyContainerHolder {

    EnergyContainerHolder(Supplier<Direction> facingSupplier) {
        super(facingSupplier);
    }

    void addContainer(@NotNull IHBMEnergyStorage container, RelativeSide... sides) {
        addSlotInternal(container, sides);
    }

    @NotNull
    @Override
    public List<IHBMEnergyStorage> getEnergyContainers(@Nullable Direction direction) {
        return getSlots(direction);
    }
}