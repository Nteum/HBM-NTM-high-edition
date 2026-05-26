package com.hbm.utils.transport_net;

import com.hbm.api.fluid.FluidUtils;
import com.hbm.blockentity.base.BasePipeBlockEntity;
import com.hbm.blockentity.machine.PipeEntity;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
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
        long loc;
        IntSet netsToMerge;
        Int2ObjectMap<IntSet> splitState = new Int2ObjectOpenHashMap<>();
        IntSet netsToUpdatePipe = new IntOpenHashSet();
        LongSet pipesToInit = new LongOpenHashSet(tobeJoin);
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
                int code = createNet(iterator.next());
                netsToUpdatePipe.add(code);
                splitState.get(id).add(code);
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
                pipesToInit.addAll(posCache);
            }else {
                netsToUpdatePipe.add(createNet(posCache, neoNetId));
            }
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
        // 更新管道的网络标记
        for (int id : netsToUpdatePipe) {
            if (nets.containsKey(id)) nets.get(id).assignNet();
        }
        for (long pipePos : pipesToInit) {
            if (!nodeMap.containsKey(pipePos)) continue;
            NetWork net = nets.get(nodeMap.get(pipePos).netId);
            if (net == null) continue;
            PipeEntity pipe = WorldUtils.getTileEntity(PipeEntity.class, level, BlockPos.of(pipePos));
            if (pipe == null) continue;
            pipe.network = net;
        }
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
        private Fluid fluid = Fluids.EMPTY;
        public NetWork(FluidBackupSystem parent, int id, LongSet nodes, Set<LazyOptional<IFluidHandler>> endpoints){
            this.parent = parent;
            this.id = id;
            this.nodes = nodes;
            this.endpoints = endpoints;

        }
        public Fluid getFluid(){
            return fluid;
        }
        public FluidBackupSystem getParent(){
            return this.parent;
        }
        public void setFluid(final Fluid fluid) {
            this.fluid = fluid == null ? Fluids.EMPTY : fluid;
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
//            assignNet(joins);
        }
        public void merge(NetWork other){
            if (other == null || other == this || (this.fluid != Fluids.EMPTY && other.fluid != Fluids.EMPTY && this.fluid != other.fluid)) return;
            if (this.fluid == Fluids.EMPTY) this.fluid = other.fluid;
            this.nodes.addAll(other.nodes);
            for (long node : other.nodes) {
                parent.nodeMap.get(node).netId = this.id;
            }
            this.endpoints.addAll(other.endpoints);
            assignNet(other.nodes);
        }
        public void assignNet(){
            this.assignNet(this.nodes);
        }
        public void assignNet(LongSet nodes){
            for (Long node : nodes) {
                PipeEntity pipeEntity = WorldUtils.getTileEntity(PipeEntity.class, this.parent.level, BlockPos.of(node));
                if (pipeEntity != null) pipeEntity.network = this;
            }
        }
        public void tick() {
            // 1. 类型仲裁 (保持你的设计)
            if (this.fluid == Fluids.EMPTY) {
                for (LazyOptional<IFluidHandler> endpoint : this.endpoints) {
                    if (endpoint.isPresent()) {
                        FluidStack fluidStack = endpoint.resolve().get().drain(1000, IFluidHandler.FluidAction.SIMULATE);
                        if (!fluidStack.isEmpty()) {
                            this.fluid = fluidStack.getFluid();
                            break;
                        }
                    }
                }
            }

            if (this.fluid != Fluids.EMPTY) {
                int size = this.endpoints.size();
                int[] maxDrained = new int[size];
                int[] maxFilled = new int[size];
                boolean[] isTwoWay = new boolean[size];

                long totalPureSupply = 0;  // 纯生产者的最大供应
                long totalPureDemand = 0;  // 纯消费者的最大需求
                long totalTwoWaySupply = 0; // 储罐能拿出来的最大量
                long totalTwoWayDemand = 0; // 储罐能装下的最大量

                // === 1. 统计阶段 ===
                int index = 0;
                for (LazyOptional<IFluidHandler> lazyOptional : this.endpoints) {
                    if (lazyOptional.isPresent()) {
                        IFluidHandler handler = lazyOptional.orElse(null);
                        if (handler != null) {
                            int drained = handler.drain(new FluidStack(this.fluid, Integer.MAX_VALUE), IFluidHandler.FluidAction.SIMULATE).getAmount();
                            int filled = handler.fill(new FluidStack(this.fluid, Integer.MAX_VALUE), IFluidHandler.FluidAction.SIMULATE);

                            maxDrained[index] = drained;
                            maxFilled[index] = filled;

                            if (drained > 0 && filled > 0) {
                                isTwoWay[index] = true;
                                totalTwoWaySupply += drained;
                                totalTwoWayDemand += filled;
                            } else {
                                totalPureSupply += drained;
                                totalPureDemand += filled;
                            }
                        }
                    }
                    index++;
                }

                // === 2. 仲裁与守恒计算 (核心数学模型) ===
                long actualDrainTwoWay = 0;
                long actualFillTwoWay = 0;
                long finalGlobalTraffic = 0; // 本次 tick 网络决定传输的总流体量

                // 判断刚性供需关系
                if (totalPureSupply >= totalPureDemand) {
                    // 供大于求：纯生产就能喂饱纯消费，多余的塞给双向储罐
                    long surplus = totalPureSupply - totalPureDemand;
                    actualFillTwoWay = Math.min(surplus, totalTwoWayDemand); // 储罐尽力吸收
                    finalGlobalTraffic = totalPureDemand + actualFillTwoWay; // 纯消费需要的 + 储罐吃掉的
                } else {
                    // 供不应求：纯生产不够，需要让双向储罐吐出来一部分补齐
                    long deficit = totalPureDemand - totalPureSupply;
                    actualDrainTwoWay = Math.min(deficit, totalTwoWaySupply); // 储罐尽力贡献
                    finalGlobalTraffic = totalPureSupply + actualDrainTwoWay; // 纯生产提供的 + 储罐吐出来的
                }

                // === 3. 分配与执行阶段 (安全防止除零) ===
                index = 0;
                long executedFill = 0; // 用于重置网络的空闲状态

                for (LazyOptional<IFluidHandler> lazyOptional : this.endpoints) {
                    if (lazyOptional.isPresent()) {
                        IFluidHandler handler = lazyOptional.orElse(null);
                        if (handler != null) {
                            if (isTwoWay[index]) {
                                // 双向储罐处理
                                if (actualDrainTwoWay > 0 && totalTwoWaySupply > 0) {
                                    int toDrain = (int) (maxDrained[index] * actualDrainTwoWay / totalTwoWaySupply);
                                    handler.drain(new FluidStack(this.fluid, toDrain), IFluidHandler.FluidAction.EXECUTE);
                                }
                                if (actualFillTwoWay > 0 && totalTwoWayDemand > 0) {
                                    int toFill = (int) (maxFilled[index] * actualFillTwoWay / totalTwoWayDemand);
                                    handler.fill(new FluidStack(this.fluid, toFill), IFluidHandler.FluidAction.EXECUTE);
                                    executedFill += toFill;
                                }
                            } else {
                                // 纯单向机器处理
                                if (maxDrained[index] > 0 && totalPureSupply > 0) {
                                    // 纯生产者分摊：如果总传输量小于总供应（储罐没装满），按比例减少输出
                                    int toDrain = (int) (maxDrained[index] * finalGlobalTraffic / totalPureSupply);
                                    // 安全兜底：不能超过其原本的 drained
                                    toDrain = Math.min(toDrain, maxDrained[index]);
                                    handler.drain(new FluidStack(this.fluid, toDrain), IFluidHandler.FluidAction.EXECUTE);
                                }
                                if (maxFilled[index] > 0 && totalPureDemand > 0) {
                                    // 纯消费者分摊：如果总传输量小于总需求（供应不足），按比例减少输入
                                    int toFill = (int) (maxFilled[index] * finalGlobalTraffic / totalPureDemand);
                                    toFill = Math.min(toFill, maxFilled[index]);
                                    handler.fill(new FluidStack(this.fluid, toFill), IFluidHandler.FluidAction.EXECUTE);
                                    executedFill += toFill;
                                }
                            }
                        }
                    }
                    index++;
                }

                // 4. 自动释放锁：如果本 tick 没有任何流体被成功灌注，且网络已经干涸，重置为空闲网络
                if (finalGlobalTraffic == 0 && executedFill == 0) {
                    this.fluid = Fluids.EMPTY;
                }
            }
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
