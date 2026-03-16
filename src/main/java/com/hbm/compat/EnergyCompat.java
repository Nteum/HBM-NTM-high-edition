package com.hbm.compat;

import com.hbm.registries.HBMCaps;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class EnergyCompat {
    public static boolean support(ICapabilityProvider capabilityProvider) {
        if (capabilityProvider == null) return false;
        else return capabilityProvider.getCapability(HBMCaps.LONG_ENERGY).isPresent() || capabilityProvider.getCapability(ForgeCapabilities.ENERGY).isPresent();
    }
}
