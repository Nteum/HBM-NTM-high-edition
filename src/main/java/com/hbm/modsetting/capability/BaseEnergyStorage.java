package com.hbm.modsetting.capability;

import net.minecraftforge.energy.EnergyStorage;
//基础的能量类
public abstract class BaseEnergyStorage extends EnergyStorage {
    public BaseEnergyStorage(int capacity) {
        super(capacity);
    }
    public BaseEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extractedEnergy = super.extractEnergy(maxExtract, simulate);
        if (extractedEnergy != 0){
            onEnergyChange();
        }
        return extractedEnergy;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int receivedEnergy = super.receiveEnergy(maxReceive, simulate);
        if (receivedEnergy != 0){
            onEnergyChange();
        }
        return receivedEnergy;
    }

    public int setEnergy(int energy){
        this.energy = energy;
        return energy;
    }
    //能量变化时做一些回调操作
    public abstract void onEnergyChange();
}
