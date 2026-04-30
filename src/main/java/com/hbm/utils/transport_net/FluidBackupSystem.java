package com.hbm.utils.transport_net;

import com.hbm.blockentity.base.BasePipeBlockEntity;
import com.hbm.utils.WorldUtils;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;

public class FluidBackupSystem {
    public static Map<Level, FluidBackupSystem> INSTANCES = new HashMap<>();

    protected final Level level;
    // 所有需要的网络
    protected final Int2ObjectMap<NetWork> nets = new Int2ObjectOpenHashMap<>();
    // 所有节点的连接关系
    protected final Long2ObjectMap<NodeInfo> nodeMap = new Long2ObjectOpenHashMap<>();
    // 所有机器的数据，数据结构（标识符、机器所属网络集合，机器的电能handler）
    /**
     * 机器的加入：机器不会主动加入，在加入节点的过程中检测机器加入，并加入对应网络。
     * 机器的移除：1. 机器本身被破坏，可以给lazyoptional加listener，自身被移除的时候从代码中删除，但如何访问到机器总表？
     * 机器的网络归属：一个机器可以归属多个网络，它的不通的面可能对应不同的能力，这样似乎不能单纯用点位来对应机器，而应该用lazyoptional做主键，但如果这样，需要访问机器位置的情况应该怎么办？
     * 考虑情况：
     *  1). 机器入网，可以只用lazyoptioal
     *  2). 机器破坏，可以只用lazyoptional。监听事件，删除机器引用
     *  3). 网络合并，对应的机器加总，不需要访问位置
     *  4)。所连节点移除，需要判断所连节点是否被移除了，要么节点被移除的时候搜索连接方向是否有机器
     *  5). 网络分割，比较麻烦的情况，需要知道节点和lazyoptional的对应关系。
     * 目前考虑：不记录一个lazyoptional的总表，而是记录一个“节点 - 机器连接表”，主键是节点位置，值是节点不同方向连接的节点列表。
     * */
    protected final Long2ObjectMap<MachineAttachment[]> node2Machines = new Long2ObjectOpenHashMap<>();
    // 待加入网络的节点，用于延迟加载
    protected final LongSet tobeJoin = new LongOpenHashSet();
    // 待离开网络的节点
    protected final LongSet tobeLeave = new LongOpenHashSet();
    protected Set<int[]> readyToMerge = new HashSet<>();        //即将合并的网络
    protected Set<long[]> readyToLink = new HashSet<>();        //即将合并的网络合并处的节点表
    protected IntSet readyToSplit = new IntOpenHashSet();       //即将切分的网络

    public static boolean has(final Level level){
        return INSTANCES.containsKey(level);
    }
    public static FluidBackupSystem getOrCreate(final Level level){
        if (level.isClientSide) return null;
        return INSTANCES.computeIfAbsent(level, FluidBackupSystem::new);
    }
    protected FluidBackupSystem(final Level level){
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
            nodeMap.get(l1).connPos.add(l2);
            nodeMap.get(l2).connPos.add(l1);
            int net1 = nodeMap.get(l1).netId;
            int net2 = nodeMap.get(l2).netId;
            if (net1 != net2) readyToLink.add(new long[]{l1, l2});
//            if (net1 != net2) readyToMerge.add(new int[]{net1, net2});
        }else {
            if (!nodeMap.containsKey(l1)) join(level.getBlockEntity(pos1));
            if (!nodeMap.containsKey(l2)) join(level.getBlockEntity(pos2));
        }
    }
    //连接切断
    public void cut(final BlockPos pos1, final BlockPos pos2){
        long l1 = pos1.asLong();
        long l2 = pos2.asLong();
        if (!(nodeMap.containsKey(l1) && nodeMap.containsKey(l2))) return;
        nodeMap.get(l1).connPos.remove(l2);
        nodeMap.get(l2).connPos.remove(l1);
        readyToSplit.add(nodeMap.get(l1).netId);
    }

    // 从更新队列中加入网络
    private void updateStructure(){
        LongSet posCache;
        BlockPos blockPos, pos, pos1;
        BlockEntity be;
        BasePipeBlockEntity pipeEntity;
        NodeInfo nodeInfo;
        NetWork netWork;
        MachineAttachment[] machineAttachments;
        Queue<Pair<BlockPos, Direction>> queue;
        Queue<Integer> netQueue;
        Queue<Long> nodeQueue;
        long loc;
        IntSet netsToMerge;
        Int2ObjectMap<IntSet> splitState = new Int2ObjectOpenHashMap<>();
        int neoNetId, netId;
        // 1. 节点离开
        for (long l : tobeLeave) {
            nodeInfo = nodeMap.remove(l);
            machineAttachments = node2Machines.remove(l);
            if (nodeInfo == null) continue;
            if ((netWork = nets.get(nodeInfo.netId)) != null) netWork.nodes.remove(l);
            for (long conn : nodeInfo.connPos) {
                if (nodeMap.containsKey(conn)) nodeMap.get(conn).connPos.remove(l);
            }
            if (machineAttachments != null){
                for (MachineAttachment machineAttachment : machineAttachments) {
                    if (netWork != null && machineAttachment != null) netWork.endpoints.remove(machineAttachment.handler);
                }
            }
            if (nodeInfo.connPos.size() > 1) readyToSplit.add(nodeInfo.netId);
        }
        // 2. 网络拆分
        for (int id : readyToSplit) {
            if (!nets.containsKey(id)) {
                splitState.put(id, IntSet.of());
                continue;
            }
            if ((netWork = nets.get(id)) == null || netWork.isEmpty()) {
                nets.remove(id);
                splitState.put(id, IntSet.of());
                continue;
            }
            List<LongSet> parts = findParts(netWork);
            if (parts.size() == 1) {
                splitState.put(id, IntSet.of(id));
                continue;
            }
            splitState.put(id, new IntOpenHashSet());
            Iterator<LongSet> iterator = parts.iterator();
            netWork.nodes = iterator.next();
            while (iterator.hasNext()){
                splitState.get(id).add(createNet(iterator.next()));
            }
        }
        // 3. 节点加入
        for (long l : tobeJoin) {
            blockPos = BlockPos.of(l);
            if (nodeMap.containsKey(l) || (pipeEntity = WorldUtils.getTileEntity(BasePipeBlockEntity.class, level, blockPos)) == null) continue;
            posCache = new LongOpenHashSet();
            posCache.add(l);
            netsToMerge = new IntOpenHashSet();
            queue = new ArrayDeque<>();
            queue.add(Pair.of(blockPos, null));
            neoNetId = createNetID();   // 先赋予一个新的id，以便和其他有待加入的网络区分
//            nodeMap.put(l, new NodeInfo(-1, new LongOpenHashSet()));
//            for (Direction direction : pipeEntity.getAttached()) {
//                pos = blockPos.relative(direction);
//                if (nodeMap.containsKey(pos.asLong())) {
//                    netsToMerge.add(nodeMap.get(pos.asLong()).netId);
//                    continue;
//                }
//                queue.add(Pair.of(pos, direction));
//                posCache.add(pos.asLong());
//            }
            while (!queue.isEmpty()) {
                Pair<BlockPos, Direction> poll = queue.poll();
                pos = poll.getLeft();
                Direction dir = poll.getRight();
                be = WorldUtils.getTileEntity(level, pos);
                if (be == null) continue;
                loc = pos.asLong();
                long l1 = dir == null ? loc : pos.relative(dir.getOpposite()).asLong();
                if (be instanceof BasePipeBlockEntity basePipeBlockEntity){
                    nodeMap.put(loc, new NodeInfo(neoNetId, new LongOpenHashSet()));
                    if (dir != null) nodeMap.get(l1).connPos.add(loc);
                    for (Direction direction : basePipeBlockEntity.getAttached()) {
                        if (dir != null && direction == dir.getOpposite()) continue;
                        pos1 = pos.relative(direction);
                        long l2 = pos1.asLong();
                        if (nodeMap.containsKey(l2)){
                            if ((netId = nodeMap.get(l2).netId) != neoNetId){
                                netsToMerge.add(netId);
                                readyToLink.add(new long[]{l2, loc});
                            }
                            continue;
                        }
                        queue.add(Pair.of(pos1, direction));
                        posCache.add(l2);
                    }
                }else if (dir != null){
                    LazyOptional<IFluidHandler> lazyOptional = be.getCapability(ForgeCapabilities.FLUID_HANDLER, dir.getOpposite());
                    if (lazyOptional.isPresent()){
                        if (!node2Machines.containsKey(l1)) node2Machines.put(l1, new MachineAttachment[6]);
                        lazyOptional.addListener(new InvalidationHandler(this, pos.relative(dir.getOpposite()), dir.getOpposite()));
                        node2Machines.get(l1)[dir.getOpposite().get3DDataValue()] = new MachineAttachment(dir.getOpposite(), lazyOptional);
                    }
                }
            }

            if (netsToMerge.size() == 1 && nets.containsKey(netsToMerge.iterator().nextInt())){
                nets.get(netsToMerge.iterator().nextInt()).addNodes(posCache);
            }else {
                createNet(posCache, neoNetId);
            }
//            if (netsToMerge.size() > 1){
//                netsToMerge.add(createNet(posCache));
//                readyToMerge.add(netsToMerge.toIntArray());
//            }else if (netsToMerge.size() == 1 && nets.containsKey(netsToMerge.iterator().nextInt())){
//                nets.get(netsToMerge.iterator().nextInt()).addNodes(posCache);
//            }else{
//                createNet(posCache);
//            }
        }
        // 4. 网络合并
        netsToMerge = new IntOpenHashSet();
        for (long[] mergeNodes : readyToLink) {
            netsToMerge.clear();
            for (long mergeNode : mergeNodes) {
                netId = nodeMap.get(mergeNode).netId;
                if (nets.containsKey(netId)) netsToMerge.add(netId);
            }
            if (netsToMerge.size() < 2) continue;
            IntIterator iterator = netsToMerge.iterator();
            netWork = nets.get(iterator.nextInt());
            while (iterator.hasNext()){
                netWork.merge(nets.get(iterator.nextInt()));
            }
        }
//        for (int[] mergeNets : readyToMerge) {
//            if (mergeNets.length <= 1) continue;
//            boolean shouldIterate = false;
//            IntSet split = new IntOpenHashSet();
//            for (int mergeNet : mergeNets) {
//                if (readyToSplit.contains(mergeNet)) {
//                    split.addAll(splitState.get(mergeNet));
//                    shouldIterate = true;
//                }else {
//                    split.add(mergeNet);
//                }
//            }
//            if (shouldIterate){
//                netQueue = new ArrayDeque<>(split);
//                while (!netQueue.isEmpty()){
//                    int poll = netQueue.poll();
//                    if (!nets.containsKey(poll)) continue;
//                    if ((netWork = nets.get(poll)).isEmpty()) {
//                        nets.remove(poll);
//                        continue;
//                    }
//                    nodeQueue = new ArrayDeque<>(netWork.nodes);
//                    while (!nodeQueue.isEmpty()){
//                        long nodePoll = nodeQueue.poll();
//                        if (!nodeMap.containsKey(nodePoll)) continue;
//                        for (long conn : nodeMap.get(poll).connPos) {
//                            netId = nodeMap.get(conn).netId;
//                            if (netId != poll){
//                                NetWork netWork1 = nets.get(netId);
//                                nodeQueue.addAll(netWork1.nodes);
//                                netWork.merge(netWork1);
//                                nets.remove(netId);
//                            }
//                        }
//                    }
//                }
//            }else {
//                NetWork net1 = nets.get(mergeNets[0]);
//                for (int i = 1; i < mergeNets.length; i++) {
//                    net1.merge(nets.get(mergeNets[i]));
//                }
//            }
//        }
        // 收尾工作
        tobeJoin.clear();
        tobeLeave.clear();
        readyToSplit.clear();
        readyToMerge.clear();
        readyToLink.clear();
    }

    protected List<LongSet> findParts(final NetWork network){
        LongOpenHashSet remain = new LongOpenHashSet(network.nodes);
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
                for (long l : nodeMap.get(poll).connPos) {
                    if (!component.contains(l)) queue.add(l);
                }
            }
            result.add(component);
        }
        return result;
    }

    private int createNetID(){
        int id;
        while (nets.containsKey(id = this.level.random.nextInt(Integer.MAX_VALUE))) ;
        return id;
    }

    private int createNet(LongSet longSet){
        return createNet(longSet, -1);
    }
    private int createNet(LongSet longSet, int id){
        if (longSet.isEmpty()) return -1;
        int netID = id < 0 ? createNetID() : id;
        Set<LazyOptional<IFluidHandler>> endpoints = new HashSet<>();
        for (long l : longSet) {
            if (!node2Machines.containsKey(l)) continue;
            for (MachineAttachment machineAttachment : node2Machines.get(l)) {
                if (machineAttachment != null) endpoints.add(machineAttachment.handler);
            }
            nodeMap.get(l).netId = netID;
        }
        nets.put(netID, new NetWork(this, netID, longSet, endpoints));
        return netID;
    }

    public static class NetWork{
        FluidBackupSystem parent;
        int id;
        LongSet nodes;
        Set<LazyOptional<IFluidHandler>> endpoints;
        public NetWork(FluidBackupSystem parent, int id, LongSet nodes, Set<LazyOptional<IFluidHandler>> endpoints){
            this.parent = parent;
            this.id = id;
            this.nodes = nodes;
            this.endpoints = endpoints;
        }
        public boolean isEmpty(){
            return nodes.isEmpty() && endpoints.isEmpty();
        }
        public void addNodes(LongSet joins){
            nodes.addAll(joins);
            for (long l : joins) {
                if (!parent.node2Machines.containsKey(l)) continue;
                endpoints.addAll(Arrays.stream(parent.node2Machines.get(l)).filter(Objects::nonNull).map(MachineAttachment::handler).toList());
            }
        }
        public void merge(NetWork other){
            if (other == null || other == this) return;
            this.nodes.addAll(other.nodes);
            for (long node : other.nodes) {
                parent.nodeMap.get(node).netId = this.id;
            }
            this.endpoints.addAll(other.endpoints);
        }
        public void tick(){
            // 还没来得及写，暂时留空
        }
    }

    protected static class NodeInfo{
        int netId;
        LongSet connPos;
        public NodeInfo(int netId, LongSet connPos){
            this.netId = netId;
            this.connPos = connPos;
        }
    }

    protected record MachineAttachment(Direction side, LazyOptional<IFluidHandler> handler) {}

    public class InvalidationHandler implements NonNullConsumer<LazyOptional<IFluidHandler>> {
        private final FluidBackupSystem parent;
        private final BlockPos nodePos;
        private final Direction side;

        public InvalidationHandler(FluidBackupSystem parent,  BlockPos nodePos, Direction side) {
            this.parent = parent;
            this.nodePos = nodePos;
            this.side = side;
        }

        @Override
        public void accept(@Nonnull LazyOptional<IFluidHandler> handle) {
            parent.node2Machines.get(nodePos.asLong())[side.get3DDataValue()] = null;
            NetWork netWork = nets.get(parent.nodeMap.get(this.nodePos.relative(this.side).asLong()).netId);
            if (netWork != null) netWork.endpoints.remove(handle);
            else {
                for (NetWork netWork1 : nets.values()) {
                    netWork1.endpoints.remove(handle);
                }
            }
        }
    }
}
