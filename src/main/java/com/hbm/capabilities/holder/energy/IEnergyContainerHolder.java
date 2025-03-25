package com.hbm.capabilities.holder.energy;

import com.hbm.api.energy.IEnergyContainer;
import com.hbm.capabilities.holder.IHolder;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IEnergyContainerHolder extends IHolder {

    @NotNull
    List<IEnergyContainer> getEnergyContainers(@Nullable Direction side);
}