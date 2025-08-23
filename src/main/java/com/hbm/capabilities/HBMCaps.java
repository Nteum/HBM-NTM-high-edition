package com.hbm.capabilities;

import com.hbm.api.energy.IEnergyHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class HBMCaps {
    private HBMCaps(){
    }
    public static final Capability<IEnergyHandler> LONG_ENERGY = CapabilityManager.get(new CapabilityToken<>() {});
}
