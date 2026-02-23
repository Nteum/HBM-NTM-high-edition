package com.hbm.api.energy;

import com.hbm.api.IContentsListener;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public interface IEnergyHandler extends IContentsListener {
    IEnergyContainer getEnergyContainer();
    default long getStored(){
        return getEnergyContainer().getEnergy();
    }
    default long getCapacity(){
        return getEnergyContainer().getCapacity();
    }
    default boolean canReceive(){
        return getEnergyContainer().canReceive();
    }
    default boolean canExtract(){
        return getEnergyContainer().canExtract();
    }
    default long receive(long amount, boolean simulate){
        return getEnergyContainer().receive(amount, simulate);
    }
    default long extract(long amount, boolean simulate){
        return getEnergyContainer().extract(amount, simulate);
    }
    default void setEnergy(long amount){
        getEnergyContainer().setEnergy(amount);
    }
    default long getNeeded(){
        return getEnergyContainer().getNeeded();
    }
}
