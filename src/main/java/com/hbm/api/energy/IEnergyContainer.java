package com.hbm.api.energy;

import com.hbm.api.Action;
import com.hbm.api.AutomationType;
import com.hbm.api.IContentsListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.INBTSerializable;

@AutoRegisterCapability
public interface IEnergyContainer  extends INBTSerializable<CompoundTag> {
    long getEnergy();
    void setEnergy(long energy);
    long insert(long expectedReceive);
    long extract(long expectedExtract,boolean isSim);
    long getMaxEnergy();
//    void setMaxEnergy(long capacity);
    boolean canExtract();
    boolean canReceive();
    default boolean isEmpty(){
        return getEnergy()==0;
    }
    default void setEmpty(){setEnergy(0);}
    default long getNeeded(){return getMaxEnergy()-getEnergy();}
}
