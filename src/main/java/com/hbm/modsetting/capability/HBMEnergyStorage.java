package com.hbm.modsetting.capability;

import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

//基础的能量类
public class HBMEnergyStorage implements IHBMEnergy, INBTSerializable<Tag> {
    protected long energy;
    protected long capacity;
    protected long maxReceive;
    protected long maxExtract;

    public HBMEnergyStorage(long capacity)
    {
        this(capacity, capacity, capacity, 0);
    }

    public HBMEnergyStorage(long capacity, long maxTransfer)
    {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public HBMEnergyStorage(long capacity, long maxReceive, long maxExtract)
    {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public HBMEnergyStorage(long capacity, long maxReceive, long maxExtract, long energy)
    {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = Math.max(0 , Math.min(capacity, energy));
    }

    @Override
    public long getEnergy() {
        return energy;
    }

    @Override
    public void setEnergy(long energy) {
        this.energy = energy;
    }

    @Override
    public long receiveEnergy(long expectedReceive) {
        if (!canReceive())return 0;
        long received = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        energy += received;
        return received;
    }

    @Override
    public long extractEnergy(long expectedExtract) {
        if (!canExtract())return 0;
        long extracted = Math.min(Math.min(expectedExtract,maxExtract),energy);
        this.capacity -= extracted;
        return extracted;
    }

    @Override
    public long getMaxEnergy() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return maxExtract > 0;
    }

    @Override
    public boolean canReceive() {
        return maxReceive > 0;
    }

    @Override
    public Tag serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(Tag nbt) {

    }
}
