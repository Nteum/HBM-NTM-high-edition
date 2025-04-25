package com.hbm.api.energy.fe;

import com.hbm.api.IContentsListener;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.Map;

public interface IHBMEnergyStorage extends IEnergyStorage , IContentsListener {
    void setEnergy(long energy);
    long receiveEnergy(long maxReceive, boolean simulate);
    long extractEnergy(long maxExtract, boolean simulate);
    long getLongStore();
    long getLongCapacity();
    void setListener(IContentsListener listener);
    default long getNeeded(){return getLongCapacity()-getLongStore();}
//    default int long2int(long num){return Math.min((int)num,Integer.MAX_VALUE);}
}