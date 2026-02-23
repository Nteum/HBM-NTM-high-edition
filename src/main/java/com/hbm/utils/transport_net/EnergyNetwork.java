package com.hbm.utils.transport_net;

import com.hbm.api.energy.IEnergyHandler;
import com.hbm.blockentity.interfaces.IConnector;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;

public class EnergyNetwork {
    public byte INDEX_FORBID = 0;
    public byte INDEX_IN = 1;
    public byte INDEX_OUT = 2;
    public byte INDEX_INOUT = 3;

    int code;
    private final EnergyNetworkSystem parent;
    protected LongSet transmitters = new LongOpenHashSet();
    protected LongSet machines = new LongOpenHashSet();

    private final Long2LongMap transNeed = new Long2LongOpenHashMap();

    public EnergyNetwork(EnergyNetworkSystem parent, int code){
        this.parent = parent;
        this.code = code;
    }
    protected void tick(){
        if (this.transmitters.size() <= this.machines.size()) removeNet();
        int pri;
        LazyOptional<IEnergyHandler> handlerLazyOptional;
        long sum, sum1, sum2, sum3, sum4, sumPri1, sumPri2, sumPri3, sumPri4, transNeeded, posL, last1, last2, last3, temp1, temp2;
        IEnergyHandler energyHandler;
        sum1 = sum2 = sum3 = sum4 = last1 = last2 = last3 = 0;
        sumPri1 = sumPri2 = sumPri3 = sumPri4 = 0;
        List<Triple<Long, Long, Long>> tempInOut = new ArrayList<>();
        LongIterator iterator = machines.longIterator();
        while (iterator.hasNext()){
            posL = iterator.nextLong();
            MutableTriple<Byte, IntSet, LazyOptional<IEnergyHandler>> triple = parent.machines.get(posL);
            Byte left = triple.getLeft();
            handlerLazyOptional= triple.getRight();
            if (handlerLazyOptional.isPresent() && handlerLazyOptional.resolve().isPresent()){
                energyHandler = handlerLazyOptional.resolve().get();
                pri = left >> 4;
                temp1 = energyHandler.receive(Long.MAX_VALUE, true);
                temp2 = energyHandler.extract(Long.MAX_VALUE, true);
                if (temp1 != 0 && temp2 != 0){  // 对于既可输入又可输出的机器，需要根据其他机器耗电状态判定它们是输电还是放电。
                    tempInOut.add(Triple.of(posL, temp1, temp2));
                    sum3 += temp1;sum4 += temp2;
                    sumPri3 += pri * temp1; sumPri4 += pri * temp2;
                    last3 = posL;
                }else {
                    transNeed.put(posL, temp1 - temp2);
                    if (temp1 > 0){
                        sum1 += temp1;
                        sumPri1 += pri * temp1;
                        last1 = posL;
                    }else if (temp2 > 0){
                        sum2 += temp2;
                        sumPri2 += pri * temp2;
                        last2 = posL;
                    }
                }
            }
        }
        sum = 0;
        if (sum1 < sum2){
            if (sum1 + sum3 >= sum2) sum = sum2 - sum1;
            else {
                sum2 = sum1 + sum3;
                sum = sum3;
            }
        }else if (sum1 > sum2){
            if (sum2 + sum4 >= sum1) sum = -(sum1 - sum2);
            else {
                sum1 = sum2 + sum4;
                sum = -sum4;
            }
        }
        for (Long2LongMap.Entry entry : transNeed.long2LongEntrySet()) {
            posL = entry.getLongKey();
            MutableTriple<Byte, IntSet, LazyOptional<IEnergyHandler>> triple = parent.machines.get(posL);
            LazyOptional<IEnergyHandler> right = triple.getRight();
            if (right.isPresent() && right.resolve().isPresent()){
                energyHandler = right.resolve().get();
                pri = triple.getLeft() >> 4;
                transNeeded = transNeed.get(posL);
                if (transNeeded > 0){
                    long tempReceive = posL != last1 ? sum1 * transNeeded * pri / sumPri1 : sum1;
                    energyHandler.receive(tempReceive, false);
                    sum1 -= tempReceive;
                }else if (transNeeded < 0){
                    long tempExtract = posL != last2 ? -sum2 * transNeeded * pri / sumPri2 : sum2;
                    energyHandler.extract(tempExtract, false);
                    sum2 -= tempExtract;
                }
            }
        }
        if (sum != 0){
            for (Triple<Long, Long, Long> triple : tempInOut) {
                posL = triple.getLeft();
                MutableTriple<Byte, IntSet, LazyOptional<IEnergyHandler>> triple2 = parent.machines.get(posL);
                LazyOptional<IEnergyHandler> right = triple2.getRight();
                if (right.isPresent() && right.resolve().isPresent()){
                    energyHandler = right.resolve().get();
                    pri = triple2.getLeft() >> 4;
                    if (sum > 0){
                        long tempReceive = posL != last3 ? sum * triple.getMiddle() / sumPri3 : sum;
                        sum -= tempReceive;
                        energyHandler.receive(tempReceive, false);
                    }else {
                        long tempExtract = posL != last3 ? -sum * triple.getRight() / sumPri4 : -sum;
                        sum += tempExtract;
                        energyHandler.extract(tempExtract, false);
                    }
                }
            }
        }
        transNeed.clear();
    }

    public void addTransmitter(final long l){
        transmitters.add(l);
        parent.nodeMap.getOrDefault(l, new MutablePair<>(-1, LongSet.of())).setLeft(code);
        if (parent.machines.containsKey(l)) this.addMachine(l);
        else setNetwork(l);
    }

    public void addTransmitters(final LongSet nodes){
        nodes.forEach(this::addTransmitter);
    }

    public void addMachine(final long l){
        machines.add(l);
        parent.machines.get(l).getMiddle().add(code);
    }
    public void merge(EnergyNetwork other){
        transmitters.addAll(other.transmitters);
        machines.addAll(other.machines);
        for (Long l : other.transmitters) {
            parent.nodeMap.get(l).setLeft(code);
            if (!parent.machines.containsKey(l)) setNetwork(l);
        }
        for (Long l : other.machines) {
            IntSet netSet = parent.machines.get(l).getMiddle();
            netSet.remove(other.code);
            netSet.add(code);
        }
    }
    public void replaceTransmitters(final LongSet transmitters){
        this.transmitters.clear();
        this.machines.clear();
        this.addTransmitters(transmitters);
    }
    void setNetwork(final long l){
        BlockEntity blockEntity = parent.level.getBlockEntity(BlockPos.of(l));
        if (blockEntity instanceof IConnector connector){
            connector.setNetwork(this);
        }
    }
    public void removeTransmitter(final long l){
        this.transmitters.remove(l);
        this.machines.remove(l);
        // 如果网络变成空的，则从系统中移除网络，由于machine也在transmitter中，暂时只能用这种下策。
        if (this.transmitters.size() <= this.machines.size()){
            if (!machines.isEmpty()){
                for (long machine : machines) {
                    parent.removeMachineLink(machine, code);
                }
            }
            parent.nets.remove(code);
        }
    }
    private void removeNet(){
        for (long l : this.transmitters) {
            parent.nodeMap.getOrDefault(l, new MutablePair<>(-1, LongSet.of())).setLeft(-1);
        }
        for (long l : this.machines) {
            parent.removeMachineLink(l, code);
        }
        parent.nets.remove(code);
    }
    static byte getIoState(byte b){
        return (byte) (b & 0x0F);
    }
    static byte getPriority(byte b){
        return (byte) (b >> 4);
    }
}
