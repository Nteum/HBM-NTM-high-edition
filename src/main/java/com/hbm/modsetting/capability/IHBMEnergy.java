package com.hbm.modsetting.capability;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public interface IHBMEnergy {
    long getEnergy();
    void setEnergy(long energy);
    long receiveEnergy(long expectedReceive);
    long extractEnergy(long expectedExtract);
    long getMaxEnergy();
    void setMaxEnergy(long capacity);
    boolean canExtract();
    boolean canReceive();
}
