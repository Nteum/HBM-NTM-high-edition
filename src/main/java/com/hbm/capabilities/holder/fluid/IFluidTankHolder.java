package com.hbm.capabilities.holder.fluid;

import com.hbm.api.fluid.mek.IExtendedFluidTank;
import com.hbm.capabilities.holder.IHolder;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IFluidTankHolder extends IHolder {

    @NotNull
    List<IExtendedFluidTank> getTanks(@Nullable Direction side);
}