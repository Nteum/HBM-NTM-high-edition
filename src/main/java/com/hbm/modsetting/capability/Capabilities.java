package com.hbm.modsetting.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class Capabilities {
    private Capabilities(){
    }
    public static final Capability<IHBMEnergy> ENERGY = CapabilityManager.get(new CapabilityToken<>() {});
}
