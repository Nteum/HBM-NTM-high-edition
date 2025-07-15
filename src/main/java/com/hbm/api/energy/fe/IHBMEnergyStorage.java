package com.hbm.api.energy.fe;

import com.hbm.api.IContentsListener;
import net.minecraftforge.energy.IEnergyStorage;
/**
 * 1. 我在考虑要不要单纯用
 * */
public interface IHBMEnergyStorage extends IEnergyStorage , IContentsListener {
    void setEnergy(long energy);
    long receiveEnergy(long maxReceive, boolean simulate);
    long extractEnergy(long maxExtract, boolean simulate);
    long getLongStore();
    long getLongCapacity();
    //当前可输入的最大值，需要考虑当前容量剩余部分
    long getMaxInput();
    //当前可输出的最大值，需要考虑当前所存能量
    long getMaxOutput();
    void setListener(IContentsListener listener);
    default long getNeeded(){return getLongCapacity()-getLongStore();}
//    default int long2int(long num){return Math.min((int)num,Integer.MAX_VALUE);}
}