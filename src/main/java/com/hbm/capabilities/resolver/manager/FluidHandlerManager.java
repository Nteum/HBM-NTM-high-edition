package com.hbm.capabilities.resolver.manager;

import com.hbm.api.fluid.mek.IExtendedFluidTank;
import com.hbm.api.fluid.mek.ISidedFluidHandler;
import com.hbm.capabilities.holder.fluid.IFluidTankHolder;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.hbm.capabilities.proxy.ProxyFluidHandler;

/**
 * Helper class to make reading instead of having as messy generics
 */
public class FluidHandlerManager extends CapabilityHandlerManager<IFluidTankHolder, IExtendedFluidTank, IFluidHandler, ISidedFluidHandler> {

    public FluidHandlerManager(@Nullable IFluidTankHolder holder, @NotNull ISidedFluidHandler baseHandler) {
        super(holder, baseHandler, ForgeCapabilities.FLUID_HANDLER, ProxyFluidHandler::new, IFluidTankHolder::getTanks);
    }
}