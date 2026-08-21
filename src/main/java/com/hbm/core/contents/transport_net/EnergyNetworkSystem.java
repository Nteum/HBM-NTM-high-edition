package com.hbm.core.contents.transport_net;

import com.hbm.api.energy.IEnergyHandler;
import com.hbm.api.energy.TransmitUtils;
import com.hbm.blockentity.interfaces.IConnector;
import com.hbm.blockentity.interfaces.IPower;
import com.hbm.blockentity.machine.BatteryEntityBE;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * 能量分配网络系统，继承 AbstractNetworkSystem。
 * 相比旧版：
 * 1. nodeMap 从 MutablePair<Integer, LongSet> 迁移到基类的 MutableNodeData
 * 2. 网络的合并/切分/连通分量算法复用基类
 * 3. 保留能量特有的机器挂载（含电池传播）逻辑
 */
public class EnergyNetworkSystem extends AbstractNetworkSystem<EnergyNetwork> {
    public static Map<Level, EnergyNetworkSystem> INSTANCES = new HashMap<>();
    public static MutablePair<Integer, LongSet> EMPTY_NODE_DATA = new MutablePair<>(-1, new LongOpenHashSet());
    // 所有机器的数据，数据结构（标识符、机器所属网络集合，机器的电能handler）
    protected Long2ObjectMap<MutableTriple<Byte, IntSet, LazyOptional<IEnergyHandler>>> machines = new Long2ObjectOpenHashMap<>();
    // 机器对应的状态
    protected final Map<LazyOptional<IEnergyHandler>, Boolean> machineTickFlag = new HashMap<>();

    public static boolean has(final Level level){
        return INSTANCES.containsKey(level);
    }
    public static EnergyNetworkSystem getOrCreate(final Level level){
        if (level.isClientSide) return null;
        return INSTANCES.computeIfAbsent(level, EnergyNetworkSystem::new);
    }
    protected EnergyNetworkSystem(final Level level){
        super(level);
    }

    @Override
    protected EnergyNetwork createNet(int netID){
        return new EnergyNetwork(this, netID);
    }

    public void refreshNeighbour(final BlockPos pos, final Direction ... directions){
        long posL = pos.asLong();
        for (Direction direction : directions) {
            addMachine(pos, direction);
        }
    }

    // 从更新队列中加入网络
    @Override
    protected void updateStructure(){
        LongSet posCache = new LongOpenHashSet();
        BlockPos blockPos;
        BlockEntity be, be1;
        int net, temp1, temp2;
        IntSet netMarkedChange = new IntOpenHashSet();  // 标记为变化了的网络，用于缩小更新网络结构的范围
        EnergyNetwork network;
        // 加入网络节点
        for (long l : tobeJoin) {
            blockPos = BlockPos.of(l);
            be = this.level.getBlockEntity(blockPos);
            if (be == null) continue;
            if (be instanceof IConnector connector){
                // 连接网络-1表示连接器有待添加到网络中
                LongSet linkedSet = nodeMap.computeIfAbsent(l, num -> new MutableNodeData(-1, new LongOpenHashSet())).connPos;
                for (BlockPos connPos : connector.getConnected()) {
                    long posL = connPos.asLong();
                    linkedSet.add(posL);
                    if (!nodeMap.containsKey(posL)) posCache.add(posL);
                }
                for (Direction direction : connector.getAttached()) {
                    addMachine(blockPos, direction);
                }
            }
        }
        // 移除网络节点
        for (Long l : tobeLeave) {
            MutableNodeData pair = nodeMap.remove(l.longValue());
            if (pair == null) continue;
            machines.remove(l.longValue());
            EnergyNetwork network1 = nets.get(pair.netId);
            if (network1 != null) network1.removeTransmitter(l);
            netMarkedChange.add(pair.netId);
            for (Long connedL : pair.connPos) {
                nodeMap.getOrDefault(connedL.longValue(), MutableNodeData.EMPTY).connPos.remove(l.longValue());
            }
        }
        // 更新网络划分（先入网再切割）
        // 给所有尚未入网的节点入网
        for (Long2ObjectMap.Entry<MutableNodeData> entry : nodeMap.long2ObjectEntrySet()) {
            if ((net = entry.getValue().netId) == -1){                     // 待连接的导线
                spreadNet(entry.getLongKey());
            }
        }
        // 所有被标记需要破坏的网络离网。
        for (int netID : netMarkedChange) {
            splitNet(nets.get(netID));
        }
        // 收尾工作
        tobeJoin.clear();
        tobeLeave.clear();
        tobeJoin.addAll(posCache);
    }

    // 用于添加机器的递归函数
    private void addMachine(BlockPos startPos, Direction startDir) {
        // 待扫描队列：存储需要检查的方块位置及其进入的方向
        Queue<Pair<BlockPos, Direction>> queue = new ArrayDeque<>();
        queue.add(Pair.of(startPos.relative(startDir), startDir));

        while (!queue.isEmpty()) {
            Pair<BlockPos, Direction> current = queue.poll();
            BlockPos currentPos = current.getLeft();
            Direction fromDir = current.getRight();
            long posLong = currentPos.asLong();
            // 1. 基础校验：如果已经处理过，或者是电缆节点，跳过
            if (nodeMap.containsKey(posLong) || tobeJoin.contains(posLong)) continue;

            BlockEntity be = level.getBlockEntity(currentPos);
            LazyOptional<IEnergyHandler> energyOptional = TransmitUtils.getEnergyOptional(be, fromDir.getOpposite());

            if (energyOptional.isPresent()) {
                IEnergyHandler handler = energyOptional.resolve().get();

                // --- 数据提取逻辑 ---
                byte flag = (byte) (handler.canExtract() ? 0 : 1 + (handler.canReceive() ? 0 : 1) * 2);
                int priority = (be instanceof IPower) ? 2 : 1;
                int connFlag = -2;

                // --- 电池/多方块传播逻辑 ---
                if (be instanceof BatteryEntityBE) {
                    connFlag = -1;
                    flag += 1 << 2; // 标记为电池

                    // 如果是电池，将周围 6 个方向加入扫描队列，实现"传播"
                    for (Direction nextDir : Direction.values()) {
                        // 避免往回跳，虽然 containsKey 能挡住，但这样更高效
                        if (nextDir != fromDir.getOpposite()) {
                            queue.add(Pair.of(currentPos.relative(nextDir), nextDir));
                        }
                    }
                }

                // --- 存储与失效监听 ---
                flag += (byte) (priority << 4);

                // 存入机器列表
                machines.putIfAbsent(posLong, new MutableTriple<>(flag, new IntOpenHashSet(), energyOptional));
                nodeMap.putIfAbsent(posLong, new MutableNodeData(connFlag, new LongOpenHashSet()));
                BlockPos fromPos = currentPos.relative(fromDir.getOpposite());
                nodeMap.get(posLong).connPos.add(fromPos.asLong());
                MutableNodeData fromPosPair = nodeMap.getOrDefault(fromPos.asLong(), MutableNodeData.EMPTY);
                fromPosPair.connPos.add(posLong);
                if (fromPosPair.netId >= 0) nets.get(fromPosPair.netId).addNode(posLong);
                // --- 安全机制：监听失效 ---
                energyOptional.addListener(opt -> {
                    // 当机器被拆除或失效时，自动触发离开逻辑
                    this.leave(be);
                });
            }
        }
    }

    private void removeMachineLink(final long machine, final int netID){
        MutableTriple<Byte, IntSet, LazyOptional<IEnergyHandler>> triple = this.machines.get(machine);
        if (triple != null){
            triple.getMiddle().remove(netID);
            if (triple.getMiddle().isEmpty()){
                this.machines.remove(machine);
                this.nodeMap.remove(machine);
            }
        }
    }

    // 重写 spreadNet：能量网络需要处理机器（netId=-2）与电池（netId=-1，会传播到多个网络）的特殊情况
    @Override
    protected void spreadNet(final long root){
        LongSet nodes = new LongOpenHashSet();
        EnergyNetwork network;
        int net = -1, temp;
        ArrayDeque<Long> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()){
            long poll = queue.poll();
            nodes.add(poll);
            MutableNodeData pair = nodeMap.getOrDefault(poll, MutableNodeData.EMPTY);
            for (long l : pair.connPos) {
                if (nodes.contains(l)) continue;
                MutableNodeData pair1 = nodeMap.getOrDefault(l, MutableNodeData.EMPTY);
                if ((temp = pair1.netId) >= 0){
                    net = temp;
                }else if (temp == -1){
                    queue.add(l);
                }else if (temp == -2){
                    queue.add(l);
                    MutableTriple<Byte, IntSet, LazyOptional<IEnergyHandler>> triple = this.machines.get(l);
                    if (triple != null){
                        boolean isBattery = ((triple.getLeft() & 0x04) >> 2) == 1;
                        if (isBattery){
                            IntSet connNet = triple.getMiddle();
                            if (!connNet.isEmpty()) net = connNet.toIntArray()[0];
                        }
                    }
                }
            }
        }
        if (net < 0){
            net = createNetID();
            network = createNet(net);
            nets.put(net, network);
        }else {
            network = nets.get(net);
        }
        network.addNodes(nodes);
        for (long l : nodes) {
            if (nodeMap.containsKey(l)) nodeMap.get(l).netId = net;
            if (machines.containsKey(l)) network.addMachine(l);
        }
    }

    @Override
    protected void onNetMerge(EnergyNetwork primary, EnergyNetwork merged){
        for (long l : merged.machines) {
            if (machines.containsKey(l)){
                IntSet netSet = machines.get(l).getMiddle();
                netSet.remove(merged.code);
                netSet.add(primary.code);
            }
        }
    }

    public void load(final ChunkPos chunkPos, final BlockPos pos){
    }

    public void unload(final ChunkPos chunkPos, final BlockPos pos){
    }
}
