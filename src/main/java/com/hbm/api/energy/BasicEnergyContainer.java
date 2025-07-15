package com.hbm.api.energy;

import com.hbm.api.IContentsListener;

public class BasicEnergyContainer implements IEnergyContainer{
    protected long energy;
    protected long capacity;
    protected long maxReceive;
    protected long maxExtract;
    IContentsListener listener;

    public BasicEnergyContainer(long capacity)
    {
        this(capacity, capacity, capacity, 0);
    }

    public BasicEnergyContainer(long capacity, long maxTransfer)
    {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public BasicEnergyContainer(long capacity, long maxReceive, long maxExtract)
    {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public BasicEnergyContainer(long capacity, long maxReceive, long maxExtract, long energy)
    {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = Math.max(0 , Math.min(capacity, energy));
    }
    public void setListener(IContentsListener listener){
        this.listener = listener;
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
    public void setCapacity(long capacity) {
        if (capacity >= 0)this.capacity = capacity;
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
