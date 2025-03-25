package com.hbm.capabilities;

import com.hbm.api.energy.IEnergyContainer;
import com.hbm.api.energy.IStrictEnergyHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class Capabilities {
    private Capabilities(){
    }
    public static final Capability<IEnergyContainer> ENERGY = CapabilityManager.get(new CapabilityToken<>() {});
}
