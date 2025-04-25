package com.hbm.capabilities.energy;

import com.hbm.api.NBTConstants;
import com.hbm.api.energy.IEnergyContainer;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.energy.IEnergyStorage;

public class BasicEnergyContainer implements IEnergyContainer {
    long energy;
    long capacity;
    long output;
    long input;
    static long BASIC_INOUT = 10_000;

    public BasicEnergyContainer(long capacity){
        this(0,capacity,BASIC_INOUT,BASIC_INOUT);
    }
    public BasicEnergyContainer(long capacity, long inout){
        this(0,capacity,inout,inout);
    }
    public BasicEnergyContainer(long capacity, long output, long input){
        this(0,capacity,output,input);
    }
    public static BasicEnergyContainer in(long capacity,long output){return new BasicEnergyContainer(capacity,output,0);}
    public static BasicEnergyContainer out(long capacity,long input){return new BasicEnergyContainer(capacity,0,input);}
    public BasicEnergyContainer(long energy, long capacity, long output, long input) {
        this.energy = energy;
        this.capacity = capacity;
        this.output = output;
        this.input = input;
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
    public long insert(long expectedReceive) {
        if (expectedReceive <= 0)return 0;
        long amount = Math.min(getNeeded(),Math.min(expectedReceive,input));
        if (amount > 0){
            energy += amount;
        }
        return amount;
    }

    @Override
    public long extract(long expectedExtract, boolean isSim) {
        if (expectedExtract <= 0)return 0;
        long amount = Math.min(getEnergy(),Math.min(output,expectedExtract));
        if (amount > 0 && !isSim){
            energy -= amount;
        }
        return amount;
    }

    @Override
    public long getMaxEnergy() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return output != 0;
    }

    @Override
    public boolean canReceive() {
        return input != 0;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        if (!isEmpty()){
            nbt.putLong(NBTConstants.STORED,getEnergy());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt!=null&&nbt.contains(NBTConstants.STORED)){
            setEnergy(nbt.getLong(NBTConstants.STORED));
        }
    }
    public int getEnergyRate(){
        return (int) (getEnergy() / getMaxEnergy());
    }
}
