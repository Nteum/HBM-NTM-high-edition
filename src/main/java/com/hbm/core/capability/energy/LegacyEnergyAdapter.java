package com.hbm.core.capability.energy;

import com.hbm.api.energy.IEnergyHandler;
import com.hbm.core.api.capability.HBMEnergyHandler;

/**
 * 将旧体系能量接口 IEnergyHandler（HBMCaps.LONG_ENERGY）适配为新的 HBMEnergyHandler。
 * 用于兼容已移植的旧机器。
 */
public class LegacyEnergyAdapter implements HBMEnergyHandler {
    private final IEnergyHandler legacy;

    public LegacyEnergyAdapter(IEnergyHandler legacy){
        this.legacy = legacy;
    }

    @Override
    public long getEnergy() {
        return legacy.getStored();
    }

    @Override
    public long getCapacity() {
        return legacy.getCapacity();
    }

    @Override
    public void setEnergy(long energy) {
        legacy.setEnergy(energy);
    }

    @Override
    public long getInputLimit() {
        return legacy.getEnergyContainer().getInputLimit();
    }

    @Override
    public long getOutputLimit() {
        return legacy.getEnergyContainer().getOutputLimit();
    }

    @Override
    public long receive(long maxReceive, boolean simulate) {
        return legacy.receive(maxReceive, simulate);
    }

    @Override
    public long extract(long maxExtract, boolean simulate) {
        return legacy.extract(maxExtract, simulate);
    }

    @Override
    public void onContentsChanged() {
        legacy.onContentsChanged();
    }
}
