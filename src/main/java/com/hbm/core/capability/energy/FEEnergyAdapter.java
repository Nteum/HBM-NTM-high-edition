package com.hbm.core.capability.energy;

import com.hbm.api.math.MathUtils;
import com.hbm.core.api.capability.HBMEnergyHandler;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * 将 Forge Energy (IEnergyStorage) 适配为 HBMEnergyHandler。
 * HBM 能量与 FE 的换算比例沿用旧体系 FEAdapter：4 HBM = 1 FE。
 */
public class FEEnergyAdapter implements HBMEnergyHandler {
    public static final int TRANS_RATE = 4;
    private final IEnergyStorage fe;

    public FEEnergyAdapter(IEnergyStorage storage){
        this.fe = storage;
    }

    @Override
    public long getEnergy() {
        return (long) this.fe.getEnergyStored() * TRANS_RATE;
    }

    @Override
    public long getCapacity() {
        return (long) this.fe.getMaxEnergyStored() * TRANS_RATE;
    }

    @Override
    public void setEnergy(long energy) {
    }

    @Override
    public boolean canReceive() {
        return this.fe.canReceive();
    }

    @Override
    public boolean canExtract() {
        return this.fe.canExtract();
    }

    @Override
    public long receive(long maxReceive, boolean simulate) {
        if (maxReceive <= 0 || !canReceive()) return 0;
        int feToReceive = MathUtils.clampToInt(maxReceive / TRANS_RATE);
        if (feToReceive <= 0) return 0;
        return (long) this.fe.receiveEnergy(feToReceive, simulate) * TRANS_RATE;
    }

    @Override
    public long extract(long maxExtract, boolean simulate) {
        if (maxExtract <= 0 || !canExtract()) return 0;
        int feToExtract = MathUtils.clampToInt(maxExtract / TRANS_RATE);
        if (feToExtract <= 0) return 0;
        return (long) this.fe.extractEnergy(feToExtract, simulate) * TRANS_RATE;
    }

    @Override
    public void onContentsChanged() {
    }
}
