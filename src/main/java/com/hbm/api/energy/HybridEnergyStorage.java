package com.hbm.api.energy;

import net.minecraftforge.energy.IEnergyStorage;

/**
 * Simple wrapper that exposes an {@link IEnergyContainer} as a Forge {@link IEnergyStorage}.
 */
public final class HybridEnergyStorage implements IEnergyStorage, IEnergyHandler {

    private final IEnergyContainer delegate;
    private final int ratio;

    public HybridEnergyStorage(IEnergyContainer delegate) {
        this(delegate, FEAdapter.transRate);
    }

    public HybridEnergyStorage(IEnergyContainer delegate, int ratio) {
        this.delegate = delegate;
        this.ratio = Math.max(1, ratio);
    }

    private long toLong(int fe) {
        return (long) fe * ratio;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive() || maxReceive <= 0) {
            return 0;
        }
        long accepted = delegate.receive(toLong(maxReceive), simulate);
        return (int) Math.min(Integer.MAX_VALUE, accepted / ratio);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract() || maxExtract <= 0) {
            return 0;
        }
        long extracted = delegate.extract(toLong(maxExtract), simulate);
        return (int) Math.min(Integer.MAX_VALUE, extracted / ratio);
    }

    @Override
    public int getEnergyStored() {
        return (int) Math.min(Integer.MAX_VALUE, delegate.getEnergy() / ratio);
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) Math.min(Integer.MAX_VALUE, delegate.getCapacity() / ratio);
    }

    @Override
    public boolean canExtract() {
        return delegate.canExtract();
    }

    @Override
    public IEnergyContainer getEnergyContainer() {
        return delegate;
    }

    @Override
    public boolean canReceive() {
        return delegate.canReceive();
    }

    @Override
    public void onContentsChanged() {

    }
}
