package com.hbm.core.api.capability;


import com.hbm.HBMKey;
import com.hbm.api.IContentsListener;
import com.hbm.api.energy.IEnergyContainer;
import com.hbm.api.energy.IEnergyHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongTag;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * 尽量复刻IEnergyStorage的逻辑，不用forge能量只是因为传递能量的格式为long
 */
public interface HBMEnergyHandler extends IEnergyHandler, IContentsListener, INBTSerializable<CompoundTag> {
    long getEnergy();
    long getCapacity();
    void setEnergy(long energy);

    /**
     * 能量限幅这一块，forge也干了，我就跟着干了
     */
    default long getInputLimit(){return Long.MAX_VALUE;}
    default long getOutputLimit(){return Long.MAX_VALUE;}
    default boolean canReceive() {
        return getInputLimit() > 0;
    }
    default boolean canExtract() {
        return getOutputLimit() > 0;
    }

    default long getNeeded(){return getCapacity()-getEnergy();}

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

    @Override
    default CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.put(HBMKey.ENERGY, LongTag.valueOf(getEnergy()));
        return nbt;
    }

    @Override
    default void deserializeNBT(CompoundTag nbt) {
        if (nbt == null)
            throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
        setEnergy(nbt.getLong(HBMKey.ENERGY));
    }

    /**
     * 兼容IEnergyHandler用
     */
    @Override
    default IEnergyContainer getEnergyContainer() {
        return null;
    }
}
