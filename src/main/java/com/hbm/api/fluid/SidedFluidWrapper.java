package com.hbm.api.fluid;

import com.hbm.capabilities.resolver.SidedCapabilityWrapper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.List;

public class SidedFluidWrapper extends SidedCapabilityWrapper<IFluidHandler> {
    public SidedFluidWrapper(IFluidHandler content) {
        super(content);
    }

    @Override
    public List<Capability<?>> getSupportedCapabilities() {
        return List.of(ForgeCapabilities.FLUID_HANDLER);
    }
}
