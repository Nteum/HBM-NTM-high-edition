package com.hbm.api.energy;

import com.hbm.api.math.MathUtils;
import net.minecraftforge.energy.IEnergyStorage;
// forge能量的适配器，将forge能量转换成hbm能量。
public class FEAdapter implements IEnergyContainer{
    // HBM能量和FE以4:1比例转换
    public static final int transRate = 4;
    public IEnergyStorage FEContainer;
    public FEAdapter(IEnergyStorage storage){
        this.FEContainer = storage;
    }
    @Override
    public long getEnergy() {
        return (long) this.FEContainer.getEnergyStored() * transRate;
    }

    @Override
    public long getCapacity() {
        return (long) this.FEContainer.getMaxEnergyStored() * transRate;
    }

    @Override
    public boolean canReceive() {
        return this.FEContainer.canReceive();
    }

    @Override
    public boolean canExtract() {
        return this.FEContainer.canExtract();
    }

    @Override
    public long receive(long maxReceive, boolean simulate) {
        return MathUtils.clampToLong(this.FEContainer.receiveEnergy(MathUtils.clampToInt(maxReceive / transRate) * transRate,simulate));
    }

    @Override
    public long extract(long maxExtract, boolean simulate) {
        return MathUtils.clampToLong(this.FEContainer.extractEnergy(MathUtils.clampToInt(maxExtract / transRate) * transRate,simulate));
    }

    @Override
    public void setEnergy(long energy) {
    }

    @Override
    public void setCapacity(long capacity) {
    }
    @Override
    public void onContentsChanged() {

    }
}
