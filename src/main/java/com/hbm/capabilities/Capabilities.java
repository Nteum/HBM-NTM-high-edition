package com.hbm.capabilities;

import com.hbm.api.energy.IEnergyHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class Capabilities {
    private Capabilities(){
    }
    public static final Capability<IEnergyHandler> LONG_ENERGY = CapabilityManager.get(new CapabilityToken<>() {});
}
