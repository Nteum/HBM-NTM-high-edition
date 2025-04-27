package com.hbm.api.energy.fe;

import com.hbm.HBMKey;
import com.hbm.api.IContentsListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongTag;
import net.minecraftforge.common.util.INBTSerializable;

/** 用于机器内部的存储 */
public class HBMEnergyStorage implements IHBMEnergyStorage, INBTSerializable<CompoundTag> {
    long longEnergy;
    long longCapacity;
    long output;
    long input;
    static long BASIC_INOUT = 10_000;
    IContentsListener listener;
    public HBMEnergyStorage(long capacity){this(capacity,BASIC_INOUT);}
    public HBMEnergyStorage(long capacity, long inout){
        this(0,capacity,inout,inout);
    }
    public HBMEnergyStorage(long capacity, long output, long input){
        this(0,capacity,output,input);
    }
    public HBMEnergyStorage(long energy, long capacity, long output, long input) {
        this.longEnergy = energy;
        this.longCapacity = capacity;
        this.output = output;
        this.input = input;
    }
    public static HBMEnergyStorage input(long capacity){
        return new HBMEnergyStorage(0,capacity,0,BASIC_INOUT);
    }
    public static HBMEnergyStorage output(long capacity){
        return new HBMEnergyStorage(0,capacity,BASIC_INOUT,0);
    }
    @Override
    public void setListener(IContentsListener listener) {
        this.listener = listener;
    }

    @Override
    public void setEnergy(long energy) {
        this.longEnergy = energy;
        onContentsChanged();
    }

    @Override
    public long receiveEnergy(long maxReceive, boolean simulate) {
        if (!canReceive())
            return 0;
        if (isCreative())
            return 0;

        long energyReceived = Math.min(longCapacity - longEnergy, Math.min(this.input, maxReceive));
        if (!simulate){
            longEnergy += energyReceived;
            onContentsChanged();
        }
        return energyReceived;
    }
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return (int) receiveEnergy((long) maxReceive,simulate);
    }

    @Override
    public long extractEnergy(long maxExtract, boolean simulate) {
        if (!canExtract())
            return 0;
        if (isCreative())
            return maxExtract;

        long energyExtracted = Math.min(longEnergy, Math.min(this.output, maxExtract));
        if (!simulate){
            longEnergy -= energyExtracted;
            onContentsChanged();
        }
        return energyExtracted;
    }
    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return (int) extractEnergy((long) maxExtract,simulate);
    }
    //为什么要用这个呢？因为为了避免机器的能量向外流动，我机器都设置输出为0，然而实际操作中处理配方又需要提取能量，只能出此下策
    public long recipeExtract(long maxExtract, boolean simulate){
        long temp = this.output;
        this.output = this.input;
        long result = extractEnergy(maxExtract,simulate);
        this.output = temp;
        return result;
    }

    @Override
    public long getLongStore() {
        return this.longEnergy;
    }

    @Override
    public int getEnergyStored() {
        return long2int(this.longEnergy);
    }

    @Override
    public long getLongCapacity() {
        return this.longCapacity;
    }

    @Override
    public long getMaxInput() {
        return Math.min(input,getNeeded());
    }

    @Override
    public long getMaxOutput() {
        return Math.min(output,getLongStore());
    }

    @Override
    public int getMaxEnergyStored() {
        return long2int(this.longCapacity);
    }

    @Override
    public boolean canExtract() {
        return this.output > 0;
    }

    @Override
    public boolean canReceive() {
        return this.input > 0;
    }

    static int long2int(long num){return Math.min((int)num,Integer.MAX_VALUE);}

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.put(HBMKey.STORED,LongTag.valueOf(this.longEnergy));
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt == null)
            throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
        this.longEnergy = nbt.getLong(HBMKey.STORED);
    }

    @Override
    public void onContentsChanged() {
        if (listener != null)listener.onContentsChanged();
    }
    public double getPercent(){
        return (double) this.longEnergy / this.longCapacity;
    }
    public boolean isCreative(){return this.longCapacity < 0;}
}
