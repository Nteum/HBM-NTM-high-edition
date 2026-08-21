package com.hbm.core.capability.energy;

import com.hbm.core.api.capability.HBMEnergyHandler;
// 创造模式电池
public class InfiniteEnergyHandler implements HBMEnergyHandler {
    @Override
    public void onContentsChanged() {

    }

    @Override
    public long getEnergy() {
        return Long.MAX_VALUE;
    }

    @Override
    public long getCapacity() {
        return Long.MAX_VALUE;
    }

    @Override
    public boolean canReceive() {
        return false;
    }

    @Override
    public long extract(long maxExtract, boolean simulate) {
        return maxExtract;
    }

    @Override
    public void setEnergy(long energy) { }
}
