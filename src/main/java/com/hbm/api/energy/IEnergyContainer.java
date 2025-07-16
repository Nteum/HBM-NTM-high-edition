package com.hbm.api.energy;

import com.hbm.HBMKey;
import com.hbm.api.IContentsListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;
import org.jline.utils.Log;

/**
 * 我想几乎不存在机器需要两个能量槽，所以能量不像物品和流体操作那样，能量槽的功能直接作为能量系统的功能。
 * */
public interface IEnergyContainer extends IContentsListener, INBTSerializable<CompoundTag> {
    long getEnergy();
    long getCapacity();
    void setEnergy(long energy);
    void setCapacity(long capacity);
    default long getInputLimit(){return Long.MAX_VALUE;}
    default long getOutputLimit(){return Long.MAX_VALUE;
    }

    default boolean canReceive() {
        return getInputLimit() > 0;
    }

    default boolean canExtract() {
        return getOutputLimit() > 0;
    }

    // 返回被接收的能量
    default long receive(long maxReceive, boolean simulate){
        if (maxReceive <= 0 || !canReceive()) return 0;
        long amount = Math.min(maxReceive, Math.min(getInputLimit(), getNeeded()));
        if (!simulate){
            setEnergy(getEnergy() + amount);
            onContentsChanged();
        }
        return amount;
    }
    // 返回被抽取的能量
    default long extract(long maxExtract, boolean simulate){
        if (maxExtract <= 0 || !canExtract()) return 0;
        long amount = Math.min(maxExtract, Math.min(getOutputLimit(), getEnergy()));
        if (!simulate){
            setEnergy(getEnergy() - amount);
            onContentsChanged();
        }
        return amount;
    }
    default long getNeeded(){return getCapacity()-getEnergy();}
    default double getPercent(){
        return (double) getEnergy() / getCapacity();
    }
    @Override
    default CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.put(HBMKey.STORED, LongTag.valueOf(getEnergy()));
        return nbt;
    }

    @Override
    default void deserializeNBT(CompoundTag nbt) {
        if (nbt == null)
            throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
        setEnergy(nbt.getLong(HBMKey.STORED));
    }
}
