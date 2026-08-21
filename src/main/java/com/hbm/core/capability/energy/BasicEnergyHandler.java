package com.hbm.core.capability.energy;

import com.hbm.api.IContentsListener;
import com.hbm.core.api.capability.HBMEnergyHandler;

/**
 * 最基础的能量系统，我觉得它基本够应对99%的情况了
 */
public class BasicEnergyHandler implements HBMEnergyHandler {
    public static final int FORBIDDEN = 0;
    public static final int INPUT = 1;
    public static final int OUTPUT = 2;
    public static final int BOTH = 3;
    protected long energy;
    protected long capacity;
    protected long maxReceive;
    protected long maxExtract;
    IContentsListener listener;

    public BasicEnergyHandler(long capacity)
    {
        this(capacity, capacity, capacity, 0);
    }

    public BasicEnergyHandler(long capacity, long maxTransfer)
    {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public BasicEnergyHandler(long capacity, long maxReceive, long maxExtract)
    {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public BasicEnergyHandler(long capacity, long maxReceive, long maxExtract, long energy)
    {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = Math.max(0 , Math.min(capacity, energy));
    }
    public BasicEnergyHandler setListener(IContentsListener listener){
        this.listener = listener;
        return this;
    }
    public BasicEnergyHandler setIO(int IO){
        if (IO == 0) return setIO(false, false);
        else if (IO == 1) return setIO(true, false);
        else if (IO == 2) return setIO(false, true);
        else return this;
    }
    public BasicEnergyHandler setIO(boolean canReceive, boolean canExtract){
        if (!canExtract) maxExtract = 0;
        if (!canReceive) maxReceive = 0;
        return this;
    }

    @Override
    public long getEnergy() {
        return energy;
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    @Override
    public void setEnergy(long energy) {
        if (energy >= 0 && energy <= capacity)
            this.energy = energy;
    }

    @Override
    public long getInputLimit() {
        return maxReceive;
    }

    @Override
    public long getOutputLimit() {
        return maxExtract;
    }
    // 有一说一这玩意我也搞不明白放在这里有啥用，因为什么参数也传不了，但毕竟继承了，就空着吧
    // 实际上应该是在blockentity里定义的内容，它们继承Ienergycontainer，并在这个函数里存储变化的数值。
    @Override
    public void onContentsChanged() {
        if (this.listener != null)
            this.listener.onContentsChanged();
    }
}
