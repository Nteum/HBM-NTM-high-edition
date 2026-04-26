package com.hbm.utils.transport_net;

import com.hbm.api.energy.IEnergyHandler;
import com.hbm.api.energy.TransmitUtils;
import com.hbm.blockentity.interfaces.IConnector;
import com.hbm.blockentity.interfaces.IPower;
import com.hbm.blockentity.machine.BatteryEntity;
import com.hbm.utils.EnumUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.junit.experimental.max.MaxHistory;

import java.util.*;

/**
 * - 数据存储问题
 * - 同步问题（是否需要同步）
 * - 区块加载问题
 * */
public class EnergyNetworkSystem {
    public static Map<Level, EnergyNetworkSystem> INSTANCES = new HashMap<>();
    public static MutablePair<Integer, LongSet> EMPTY_NODE_DATA = new MutablePair<>(-1, new LongOpenHashSet());
    protected final Level level;
    // 所有需要的网络
    protected final Int2ObjectMap<EnergyNetwork> nets = new Int2ObjectOpenHashMap<>();
    // 所有节点的连接关系
    protected final Long2ObjectMap<MutablePair<Integer, LongSet>> nodeMap = new Long2ObjectOpenHashMap<>();
    // 所有机器的数据，数据结构（标识符、机器所属网络集合，机器的电能handler）
    protected Long2ObjectMap<MutableTriple<Byte, IntSet, LazyOptional<IEnergyHandler>>> machines = new Long2ObjectOpenHashMap<>();
    // 机器对应的状态
    protected final Map<LazyOptional<IEnergyHandler>, Boolean> machineTickFlag = new HashMap<>();
    // 待加入网络的节点，用于延迟加载
    protected final LongSet tobeJoin = new LongOpenHashSet();
    // 待离开网络的节点
    protected final LongSet tobeLeave = new LongOpenHashSet();

    public static boolean has(final Level level){
        return INSTANCES.containsKey(level);
    }
    public static EnergyNetworkSystem getOrCreate(final Level level){
        if (level.isClientSide) return null;
        return INSTANCES.computeIfAbsent(level, EnergyNetworkSystem::new);
    }
    protected EnergyNetworkSystem(final Level level){
        this.level = level;
    }

    public void tick(){
        updateStructure();
        if (nets.isEmpty()) return;
        nets.forEach((code, net) -> net.tick());
    }
    // 节点加入
    public void join(@Nullable final BlockEntity be){
        if (be == null) return;
        long pos = be.getBlockPos().asLong();
        if (!nodeMap.containsKey(pos)) tobeJoin.add(pos);
    }
    // 节点离开
    public void leave(@Nullable final BlockEntity be){
        if (be == null) return;
        long pos = be.getBlockPos().asLong();
        if (nodeMap.containsKey(pos)) tobeLeave.add(pos);
    }
    //连接连上
    public void link(final BlockPos pos1, final BlockPos pos2){
        long l1 = pos1.asLong();
        long l2 = pos2.asLong();
        if (nodeMap.containsKey(l1) && nodeMap.containsKey(l2)){
            nodeMap.get(l1).getRight().add(l2);
            nodeMap.get(l2).getRight().add(l1);
            int net1 = nodeMap.get(l1).getLeft();
            int net2 = nodeMap.get(l2).getLeft();
            if (net1 != net2){
                connect(nets.get(net1), nets.get(net2));
            }
        }else {
            if (!nodeMap.containsKey(l1)) join(level.getBlockEntity(pos1));
            if (!nodeMap.containsKey(l2)) join(level.getBlockEntity(pos2));
        }
    }
    //连接切断
    public void cut(final BlockPos pos1, final BlockPos pos2){
        long l1 = pos1.asLong();
        long l2 = pos2.asLong();
        nodeMap.getOrDefault(l1, EMPTY_NODE_DATA).getRight().remove(l2);
        nodeMap.getOrDefault(l2, EMPTY_NODE_DATA).getRight().remove(l1);
        int net = nodeMap.get(l1).getLeft();
        splitNet(nets.get(net));
    }
    //
    public void refreshNeighbour(final BlockPos pos, final Direction ... directions){
        long posL = pos.asLong();
        for (Direction direction : directions) {
            addMachine(pos, direction);
        }
    }
    // 从更新队列中加入网络
    private void updateStructure(){
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
                LongSet linkedSet = nodeMap.computeIfAbsent(l, num -> new MutablePair<>(-1, new LongOpenHashSet())).getRight();
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
            MutablePair<Integer, LongSet> pair = nodeMap.remove(l.longValue());
            if (pair == null) continue;
            machines.remove(l.longValue());
            EnergyNetwork network1 = nets.get(pair.getLeft());
            if (network1 != null) network1.removeTransmitter(l);
            netMarkedChange.add(pair.getLeft().intValue());
            for (Long connedL : pair.getRight()) {
                nodeMap.getOrDefault(connedL.longValue(), EMPTY_NODE_DATA).getRight().remove(l.longValue());
            }
        }
        // 更新网络划分（先入网再切割）
        // 给所有尚未入网的节点入网
        for (Long2ObjectMap.Entry<MutablePair<Integer, LongSet>> entry : nodeMap.long2ObjectEntrySet()) {
            if ((net = entry.getValue().getLeft()) == -1){                     // 待连接的导线
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
                if (be instanceof BatteryEntity) {
                    connFlag = -1;
                    flag += 1 << 2; // 标记为电池

                    // 如果是电池，将周围 6 个方向加入扫描队列，实现“传播”
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
                nodeMap.putIfAbsent(posLong, new MutablePair<>(connFlag, new LongOpenHashSet()));
                BlockPos fromPos = currentPos.relative(fromDir.getOpposite());
                nodeMap.get(posLong).getRight().add(fromPos.asLong());
                MutablePair<Integer, LongSet> fromPosPair = nodeMap.getOrDefault(fromPos.asLong(), EMPTY_NODE_DATA);
                fromPosPair.getRight().add(posLong);
                if (fromPosPair.getLeft() >= 0) nets.get(fromPosPair.getLeft()).addTransmitter(posLong);
                // --- 安全机制：监听失效 ---
                energyOptional.addListener(opt -> {
                    // 当机器被拆除或失效时，自动触发离开逻辑
                    this.leave(be);
                });
            }
        }
    }
//    private void addMachine(BlockPos blockPos, Direction direction){
//        addMachine(blockPos, direction, 0);
//    }
//    private void addMachine(BlockPos blockPos, Direction direction, int time){
//        if (time >= 2) return;
//        BlockEntity be1;
//        LazyOptional<IEnergyHandler> energyOptional;
//        BlockPos neighbourPos = blockPos.relative(direction);
//        if (nodeMap.containsKey(neighbourPos.asLong()) || tobeJoin.contains(neighbourPos.asLong())) return;
//        be1 = level.getBlockEntity(neighbourPos);
//        if ((energyOptional = TransmitUtils.getEnergyOptional(be1, direction)).isPresent()){
//            IEnergyHandler energyHandler = energyOptional.resolve().get();
//            byte flag = (byte)(energyHandler.canExtract() ? 0 : 1 + (energyHandler.canReceive() ? 0 : 1) * 2);  //低两位表示输入输出模式
//            int priority = 1;
//            int connFlag = -2;                      //-3表示作为机器接入，且尚未整合入网；-2表示机器已经整合入网，连接数据在machines里，不要在nodeMap中更改网络连接
//            if (be1 instanceof IPower) priority = 2;
//            if (be1 instanceof BatteryEntity){
//                connFlag = -1;
//                flag += 1 << 2; //第三位表示是否是电池
//                for (Direction dir : EnumUtils.DIRECTIONS) {
//                    if (dir != direction.getOpposite()) addMachine(neighbourPos.relative(dir), dir, time + 1);
//                }
//            }
//            nodeMap.putIfAbsent(neighbourPos.asLong(), new MutablePair<>(connFlag, new LongOpenHashSet()));
//            nodeMap.get(neighbourPos.asLong()).getRight().add(blockPos.asLong());
//            flag += (byte) (priority << 4);     //高四位表示优先级（0~15）
//            machines.putIfAbsent(neighbourPos.asLong(), new MutableTriple<>(flag, new IntOpenHashSet(), energyOptional));
//        }
//    }
    private int getNetID(){
        int id;
        while (nets.containsKey(id = this.level.random.nextInt(Integer.MAX_VALUE))) ;
        return id;
    }
    // 广度优先搜索和源节点相连的节点
    protected LongSet bfs(final long root){
        LongSet nodes = new LongOpenHashSet();
        ArrayDeque<Long> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()){
            Long poll = queue.poll();
            nodes.add(poll.longValue());
            queue.addAll(nodeMap.getOrDefault(poll.longValue(), EMPTY_NODE_DATA).getValue());
        }
        return nodes;
    }
    private EnergyNetwork spreadNet(final long root){
        LongSet nodes = new LongOpenHashSet();
        EnergyNetwork network;
        int net = -1, temp;
        ArrayDeque<Long> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()){
            long poll = queue.poll();
            nodes.add(poll);
            MutablePair<Integer, LongSet> pair = nodeMap.getOrDefault(poll, EMPTY_NODE_DATA);
            for (long l : pair.getRight()) {
                if (nodes.contains(l)) continue;
                MutablePair<Integer, LongSet> pair1 = nodeMap.getOrDefault(l, EMPTY_NODE_DATA);
                if ((temp = pair1.getLeft()) >= 0){
                    net = temp;
                }else if (temp == -1){
                    queue.add(l);
                }else if (temp == -2){
                    queue.add(l);
                    MutableTriple<Byte, IntSet, LazyOptional<IEnergyHandler>> triple = this.machines.get(l);
                    boolean isBattery = (triple.getLeft() & 0x04 >> 2) == 1;
                    if (isBattery){
                        IntSet connNet = triple.getMiddle();
                        if (!connNet.isEmpty()) net = connNet.toIntArray()[0];
                    }
                }
            }
        }
        if (net < 0){
            net = getNetID();
            network = new EnergyNetwork(this, net);
            nets.put(net, network);
        }else {
            network = nets.get(net);
        }
        network.addTransmitters(nodes);
        return network;
    }

    private EnergyNetwork connect(final EnergyNetwork... candidates) {
        if (candidates == null) return null;
        if (candidates.length == 1)return candidates[0];
        EnergyNetwork primary = null;
        for (EnergyNetwork candidate : candidates) {
            if (candidate == null) {
                continue;
            }
            if (primary == null) {
                primary = candidate;
                continue;
            }
            primary.merge(candidate);
            nets.remove(candidate.code);
        }
        return primary;
    }
    private void splitNet(final EnergyNetwork network){
        if (network == null || network.transmitters.isEmpty()) return;
        List<LongSet> parts = findParts(network);
        if (parts.size() <= 1) return;
        Iterator<LongSet> iterator = parts.iterator();
        LongSet longSet = iterator.next();
        network.replaceTransmitters(longSet);
        while (iterator.hasNext()){
            longSet = iterator.next();
            int netID = getNetID();
            EnergyNetwork newNetwork = new EnergyNetwork(this, netID);
            newNetwork.addTransmitters(longSet);
            nets.put(netID, newNetwork);
        }
    }
    protected List<LongSet> findParts(final EnergyNetwork network){
        LongOpenHashSet remain = new LongOpenHashSet(network.transmitters);
        List<LongSet> result = new ArrayList<>();
        while (!remain.isEmpty()){
            long start = remain.iterator().nextLong();
            LongSet component = new LongOpenHashSet();
            ArrayDeque<Long> queue = new ArrayDeque<>();
            queue.add(start);
            while (!queue.isEmpty()){
                long poll = queue.poll();
                component.add(poll);
                remain.remove(poll);
                for (long l : nodeMap.getOrDefault(poll, EMPTY_NODE_DATA).getRight()) {
                    if (!component.contains(l)) queue.add(l);
                }
            }
            result.add(component);
        }
        return result;
    }

    protected void removeMachineLink(final long machine, final int netID){
        MutableTriple<Byte, IntSet, LazyOptional<IEnergyHandler>> triple = this.machines.get(machine);
        if (triple != null){
            triple.getMiddle().remove(netID);
            if (triple.getMiddle().isEmpty()){
                this.machines.remove(machine);
                this.nodeMap.remove(machine);
            }
        }
    }

    public void load(final ChunkPos chunkPos, final BlockPos pos){
    }

    public void unload(final ChunkPos chunkPos, final BlockPos pos){
    }
}
