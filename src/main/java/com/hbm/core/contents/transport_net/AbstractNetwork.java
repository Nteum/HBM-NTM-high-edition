package com.hbm.core.contents.transport_net;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

/**
 * 通用分配网络基类（单个连通分量）。
 * 只负责持有节点集合与合并/切分语义，资源分配（能量/流体）由子类在 tick 中实现。
 * 对应旧 EnergyNetwork / FluidBackupSystem.NetWork。
 */
public abstract class AbstractNetwork {
    protected int code;
    protected final LongSet nodes = new LongOpenHashSet();

    public AbstractNetwork(int code){
        this.code = code;
    }

    public int getCode(){
        return code;
    }

    /** 每个 tick 的网络资源分配逻辑，由具体资源网络实现 */
    public abstract void tick();

    public boolean isEmpty(){
        return nodes.isEmpty();
    }

    public boolean contains(long pos){
        return nodes.contains(pos);
    }

    public void addNode(long pos){
        nodes.add(pos);
    }

    public void addNodes(LongSet positions){
        nodes.addAll(positions);
    }

    public void removeNode(long pos){
        nodes.remove(pos);
    }

    public void replaceNodes(LongSet positions){
        nodes.clear();
        nodes.addAll(positions);
    }

    public LongSet getNodes(){
        return nodes;
    }

    /** 合并另一个网络的所有节点（不关心对方是否同类型资源，由调用方决定） */
    public void absorb(AbstractNetwork other){
        if (other == null || other == this) return;
        nodes.addAll(other.nodes);
        other.nodes.clear();
    }
}
