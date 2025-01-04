package com.hbm.modsetting.capability;

public interface IHBMEnergy {
    long getEnergy();
    void setEnergy(long energy);
    long receiveEnergy(long expectedReceive);
    long extractEnergy(long expectedExtract);
    long getMaxEnergy();
    boolean canExtract();
    boolean canReceive();
}
