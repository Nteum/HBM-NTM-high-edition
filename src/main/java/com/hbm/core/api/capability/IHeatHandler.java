package com.hbm.core.api.capability;

import com.hbm.api.IContentsListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IHeatHandler extends INBTSerializable<CompoundTag>, IContentsListener {
    int getHeat();
    int getMaxHeat();
    boolean canExtract();
    boolean canReceive();
    int extractHeat(int maxExtract, boolean simulate);
    int receiveHeat(int maxReceive, boolean simulate);
}
