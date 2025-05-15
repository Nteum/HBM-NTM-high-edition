package com.hbm.capabilities.holder.energy;

import com.hbm.api.energy.fe.IHBMEnergyStorage;
import com.hbm.capabilities.holder.IHolder;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
/**
 * energy的holder，从mek参考的，这里改成返回IHBMEnergyStorage
 * */
public interface IEnergyContainerHolder extends IHolder {

    @NotNull
    List<IHBMEnergyStorage> getEnergyContainers(@Nullable Direction side);
}