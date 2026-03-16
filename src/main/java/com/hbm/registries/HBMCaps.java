package com.hbm.registries;

import com.hbm.api.energy.IEnergyHandler;
import com.hbm.api.heat.IHeatHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class HBMCaps {
    private HBMCaps(){
    }
    public static final Capability<IEnergyHandler> LONG_ENERGY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<IHeatHandler> HEAT = CapabilityManager.get(new CapabilityToken<>() {});
}
