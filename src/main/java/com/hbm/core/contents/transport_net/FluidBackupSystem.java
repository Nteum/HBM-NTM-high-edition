package com.hbm.core.contents.transport_net;

import com.hbm.blockentity.base.BasePipeBlockEntity;
import com.hbm.blockentity.logistic.PipeEntity;
import com.hbm.utils.DirectionUtils;
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
    // 所有节点的连接关系，连接节点包括其他管道，以及连接的机器
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
    protected Set<long[]> readyToLink = new HashSet<>();        //即将合并的网络合并处的节点表
    protected IntSet readyToSplit = new IntOpenHashSet();       //即将切分的网络
    // 记录待注销的监听器绑定信息
    protected final Queue<MachineAttachment> listenersToUnregister = new ArrayDeque<>();

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
    // 更新节点的状态
    public void refresh(@Nullable final BlockEntity be){
        if (be == null) return;
        long pos = be.getBlockPos().asLong();
        nodeMap.remove(pos);
        tobeJoin.add(pos);
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
        }else {
            if (!nodeMap.containsKey(l1)) join(level.getBlockEntity(pos1));
            if (!nodeMap.containsKey(l2)) join(level.getBlockEntity(pos2));
        }
    }
    //连接切断
    public void cut(final BlockPos pos1, final BlockPos pos2){
        long l1 = pos1.asLong();
        long l2 = pos2.asLong();
        int netId = -1;
        if (nodeMap.containsKey(l1)){
            nodeMap.get(l1).connPos.remove(l2);
            netId = nodeMap.get(l1).netId;
        }
        if (nodeMap.containsKey(l2)){
            nodeMap.get(l2).connPos.remove(l1);
            netId = nodeMap.get(l2).netId;
        }
        if (netId >= 0) readyToSplit.add(netId);
    }

    // 从更新队列中加入网络
    // 从更新队列中加入网络
    private void updateStructure() {
        LongSet posCache;
        BlockPos blockPos, pos, pos1;
        BlockEntity be;
        NodeInfo nodeInfo;
        NetWork netWork;
        Queue<Pair<BlockPos, Direction>> queue;
        IntSet netsToMerge;
        IntSet netsToUpdatePipe = new IntOpenHashSet();
        LongSet pipesToInit = new LongOpenHashSet(tobeJoin);
        int netId;
        LazyOptional<IFluidHandler> fluidHandlerLazyOptional;

        // 1. 节点离开 (保持原样，安全)
        for (long l : tobeLeave) {
            nodeInfo = nodeMap.get(l);
            if (nodeInfo != null){
                // 将对应节点从网络中去除。
                if ((netWork = nets.get(nodeInfo.netId)) != null) netWork.removeNode(l);
                // 删除相连节点的连接记录
                for (long conn : nodeInfo.connPos) {
                    if (nodeMap.containsKey(conn)) nodeMap.get(conn).connPos.remove(l);
                }
                // 如果连接的节点不止一个，则需要考虑网络切断
                if (nodeInfo.connPos.size() > 1) readyToSplit.add(nodeInfo.netId);
            }
            nodeMap.remove(l);
        }

        // 2. 网络拆分 (保持原样，安全)
        for (int id : readyToSplit) {
            if (!nets.containsKey(id)) {
                continue;
            }
            if ((netWork = nets.get(id)) == null || netWork.isEmpty()) {
                nets.remove(id);
                continue;
            }
            List<LongSet> parts = findParts(netWork);   // 返回的坐标既包括管道也包括机器
            if (parts.size() == 1) {
                continue;
            }
            Iterator<LongSet> iterator = parts.iterator();
            netWork.rebuild(iterator.next());
            while (iterator.hasNext()){
                int code = createNet(iterator.next());
                netsToUpdatePipe.add(code);
            }
        }

        // 3. 节点加入 (重构：物理连通双向建立 + 统一借用 ID 策略)
        for (long l : tobeJoin) {
            if (nodeMap.containsKey(l)) continue;

            blockPos = BlockPos.of(l);
            be = WorldUtils.getTileEntity(level, blockPos);

            if (be instanceof BasePipeBlockEntity pipe) {
                // 初始化起始点的 NodeInfo，先挂一个缺省占位 ID -1
                nodeMap.put(l, new NodeInfo(-1, new LongOpenHashSet()));
                posCache = new LongOpenHashSet(List.of(l));     // 所有新加入的节点
                netsToMerge = new IntOpenHashSet();             // 需要合并的网络
                if (!pipe.getAttached().isEmpty()){
                    queue = new ArrayDeque<>();
                    queue.add(Pair.of(blockPos, null));
                    BlockPos checkPos, neighbourPos;
                    long checkPosL, fromPosL, neighbourPosL;
                    Direction fromDir;
                    while (!queue.isEmpty()) {
                        Pair<BlockPos, Direction> poll = queue.poll();
                        checkPos = poll.getLeft();
                        fromDir = poll.getRight();
                        checkPosL = checkPos.asLong();
                        fromPosL = fromDir == null ? checkPosL : checkPos.relative(fromDir.getOpposite()).asLong();
                        if ((be = WorldUtils.getTileEntity(level, checkPos)) instanceof BasePipeBlockEntity pipeTile){
                            // 如果不是起始点，需要初始化 NodeInfo
                            if (fromDir != null) {
                                if (!nodeMap.containsKey(checkPosL)) {
                                    nodeMap.put(checkPosL, new NodeInfo(-1, new LongOpenHashSet()));
                                }
                                // 【修复 3】: 建立完整的双向物理连接
                                nodeMap.get(fromPosL).connPos.add(checkPosL);
                                nodeMap.get(checkPosL).connPos.add(fromPosL);
                            }
                            for (Direction toDir : pipeTile.getAttached()) {
                                if (toDir == null || fromDir != null && toDir == fromDir.getOpposite()) continue;
                                neighbourPos = checkPos.relative(toDir);
                                neighbourPosL = neighbourPos.asLong();
                                if (posCache.contains(neighbourPosL)) continue;
                                else if (nodeMap.containsKey(neighbourPosL)) {
                                    // 探测到了老管道或已处理管道
                                    netId = nodeMap.get(neighbourPosL).netId;
                                    if (netId != -1) {
                                        netsToMerge.add(netId);
                                        readyToLink.add(new long[]{checkPosL, neighbourPosL});
                                        continue;
                                    }
                                    // 【修复 3】：哪怕遇到了老管道，物理上的双向连通图也必须在当下接起来
                                    nodeMap.get(checkPosL).connPos.add(neighbourPosL);
                                    nodeMap.get(neighbourPosL).connPos.add(checkPosL);
                                }
                                queue.add(Pair.of(neighbourPos, toDir));
                                posCache.add(neighbourPosL);
                            }
                        }else {
                            posCache.remove(checkPosL); //从posCache中删除非管道的方块
                            if (fromDir != null){
                                nodeMap.get(fromPosL).connPos.add(checkPosL);
                                // 外部机器连接逻辑 (保持原样)
                                LazyOptional<IFluidHandler> lazyOptional = be.getCapability(ForgeCapabilities.FLUID_HANDLER, fromDir.getOpposite());
                                if (lazyOptional.isPresent()) {
                                    if (!node2Machines.containsKey(checkPosL)) node2Machines.put(checkPosL, new MachineAttachment[6]);
                                    InvalidationHandler listener = new InvalidationHandler(this, checkPos, fromDir.getOpposite());
                                    lazyOptional.addListener(listener);
                                    // 需要把listener单独记录以便在将机器移出网络的事后删掉监听器，否则机器反复进出网络会导致监听器越来越多
                                    node2Machines.get(checkPosL)[fromDir.getOpposite().get3DDataValue()] = new MachineAttachment(fromDir.getOpposite(), lazyOptional, listener);
                                }
                            }
                        }
                    }
                }

                // 决定如何挂载网络 Map
                int finalNetId;
                if (netsToMerge.isEmpty()) {
                    // 四面八方完全是一个孤立新放的网络，直接完全新建
                    finalNetId = createNet(posCache);
                    netsToUpdatePipe.add(finalNetId);
                } else {
                    // 至少靠着一个老网。我们捡起第一个老网的 ID 作为基础
                    finalNetId = netsToMerge.iterator().nextInt();
                    if (nets.containsKey(finalNetId)) {
                        nets.get(finalNetId).addNodes(posCache);
                        // 【修复 2】：必须同步回写到 nodeMap，纠正刚才占位的 -1
                        for (long cachedPos : posCache) {
                            if (nodeMap.containsKey(cachedPos)) {
                                nodeMap.get(cachedPos).netId = finalNetId;
                            }
                        }
                        pipesToInit.addAll(posCache);
                    }
                }
            }else if (be != null){
                // 如果添加的是机器，则搜索邻接管道并注册。
                for (Direction direction : Direction.values()) {
                    fluidHandlerLazyOptional = be.getCapability(ForgeCapabilities.FLUID_HANDLER, direction);
                    if (fluidHandlerLazyOptional.isPresent()){
                        if (!node2Machines.containsKey(l)) node2Machines.put(l, new MachineAttachment[6]);
                        long neighbourPosL = blockPos.relative(direction.getOpposite()).asLong();
                        if (nodeMap.containsKey(neighbourPosL)){
                            nodeMap.get(neighbourPosL).connPos.add(l);
                            InvalidationHandler listener = new InvalidationHandler(this, blockPos, direction);
                            fluidHandlerLazyOptional.addListener(listener);
                            // 需要把listener单独记录以便在将机器移出网络的事后删掉监听器，否则机器反复进出网络会导致监听器越来越多
                            node2Machines.get(l)[direction.get3DDataValue()] = new MachineAttachment(direction, fluidHandlerLazyOptional, listener);
                        }
                    }
                }
            }
        }

        // 4. 网络合并 (重构：真正踢掉死去的网络)
        netsToMerge = new IntOpenHashSet();
        for (long[] mergeNodes : readyToLink) {
            netsToMerge.clear();
            for (long mergeNode : mergeNodes) {
                if (nodeMap.containsKey(mergeNode) && nets.containsKey(netId = nodeMap.get(mergeNode).netId)) netsToMerge.add(netId);
            }
            if (netsToMerge.size() < 2) continue;

            IntIterator iterator = netsToMerge.iterator();
            int mainNetId = iterator.nextInt();
            netWork = nets.get(mainNetId);

            while (iterator.hasNext()) {
                int deadNetId = iterator.nextInt();

                NetWork deadNet = nets.get(deadNetId);
                if (deadNet != null) {
                    netWork.merge(deadNet);
                    // 【修复 1】：致命伤修复，把被合并死掉的网络彻底从大表摘除！
                    nets.remove(deadNetId);
                }
            }
        }

        // 5. 更新管道的网络标记 (后续收尾保持完整)
        for (int id : netsToUpdatePipe) {
            if (nets.containsKey(id)) nets.get(id).assignNet();
        }
        for (long pipePos : pipesToInit) {
            if (!nodeMap.containsKey(pipePos)) continue;
            NodeInfo nodeinfo = nodeMap.get(pipePos);
            NetWork net = nets.get(nodeinfo.netId);
            if (net == null) continue;
            PipeEntity pipe = WorldUtils.getTileEntity(PipeEntity.class, level, BlockPos.of(pipePos));
            if (pipe == null) continue;
            pipe.network = net;
        }

        // 6. 安全注销监听器，规避并发修改异常
        while (!listenersToUnregister.isEmpty()) {
            MachineAttachment attachment = listenersToUnregister.poll();
            if (attachment != null && attachment.handler != null && attachment.listener != null) {
                attachment.handler.removeListener(attachment.listener);
            }
        }

        // 清理缓存队列
        tobeJoin.clear();
        tobeLeave.clear();
        readyToSplit.clear();
        readyToLink.clear();
        listenersToUnregister.clear();
    }

    protected List<LongSet> findParts(final NetWork network){
        LongOpenHashSet remain = new LongOpenHashSet(network.nodes);
        List<LongSet> result = new ArrayList<>();
        while (!remain.isEmpty()){
            long start = remain.iterator().nextLong();
            LongSet component = new LongOpenHashSet();
            ArrayDeque<Long> queue = new ArrayDeque<>();
            queue.offerFirst(start);
            while (!queue.isEmpty()){
                long poll = queue.pollFirst();
                component.add(poll);
                remain.remove(poll);
                NodeInfo nodeInfo = nodeMap.get(poll);
                if (nodeInfo == null) continue;
                for (long l : nodeInfo.connPos) {
                    if (!component.contains(l) && nodeMap.containsKey(l)) queue.add(l);
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
        NetWork netWork = new NetWork(this, netID, new LongOpenHashSet(), new HashSet<>());
        netWork.rebuild(longSet);
        nets.put(netID, netWork);
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
        public void addNodes(LongSet points){
            for (long point : points) {
                if (!this.parent.nodeMap.containsKey(point)) continue;
                this.nodes.add(point);
                NodeInfo nodeInfo = this.parent.nodeMap.get(point);
                for (long conn : nodeInfo.connPos) {
                    if (this.parent.node2Machines.containsKey(conn)){
                        Direction direction = DirectionUtils.posToDirection(BlockPos.of(point), BlockPos.of(conn));
                        if (direction != null){
                            MachineAttachment machineAttachment = this.parent.node2Machines.get(conn)[direction.getOpposite().get3DDataValue()];
                            if (machineAttachment != null && machineAttachment.handler != null) this.endpoints.add(machineAttachment.handler);
                        }
                    }
                }
                nodeInfo.netId = this.id;
            }
        }
        public void rebuild(LongSet points){
            this.nodes.clear();
            this.endpoints.clear();
            addNodes(points);
        }
        public void removeNode(long node){
            this.nodes.remove(node);
            NodeInfo nodeInfo = this.parent.nodeMap.get(node);
            if (nodeInfo != null){
                for (long conn : nodeInfo.connPos) {
                    if (this.parent.node2Machines.containsKey(conn)){
                        Direction direction = DirectionUtils.posToDirection(BlockPos.of(node), BlockPos.of(conn));
                        if (direction != null){
                            MachineAttachment machineAttachment = this.parent.node2Machines.get(conn)[direction.getOpposite().get3DDataValue()];
                            this.endpoints.remove(machineAttachment.handler);
                            this.parent.removeMachineSide(conn, direction.getOpposite());
                        }
                    }
                }
            }
            // 如果网络已经空了，则移除网络。
            if (this.nodes.isEmpty()){
                this.parent.nets.remove(this.id);
            }
        }
        public void merge(NetWork other){
            if (other == null || other == this || (this.fluid != Fluids.EMPTY && other.fluid != Fluids.EMPTY && this.fluid != other.fluid)) return;
            if (this.fluid == Fluids.EMPTY) this.fluid = other.fluid;
            this.nodes.addAll(other.nodes);
            for (long node : other.nodes) {
                if (parent.nodeMap.containsKey(node)) parent.nodeMap.get(node).netId = this.id;
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
                if (parent.nodeMap.containsKey(node)) parent.nodeMap.get(node).netId = this.id;
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
                            // 流体一旦更新则更新管道流体到客户端去。
                            for (long node : this.nodes) {
                                PipeEntity pipeEntity = WorldUtils.getTileEntity(PipeEntity.class, parent.level, BlockPos.of(node));
                                if (pipeEntity != null) pipeEntity.syncToClient();
                            }
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

    protected record MachineAttachment(Direction side, LazyOptional<IFluidHandler> handler, InvalidationHandler listener) {}
    protected void removeMachineSide(long pos, Direction face){
        if (!node2Machines.containsKey(pos) || face == null) return;
        MachineAttachment machineAttachment = node2Machines.get(pos)[face.get3DDataValue()];
        node2Machines.get(pos)[face.get3DDataValue()] = null;

        if (machineAttachment != null && machineAttachment.listener != null) {
            // 挂起，不当场注销，扔进队列
            listenersToUnregister.add(machineAttachment);
        }
    }

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
            NetWork netWork = nets.get(parent.nodeMap.get(this.nodePos.relative(this.side).asLong()).netId);
            if (netWork != null) netWork.endpoints.remove(handle);
            else {
                for (NetWork netWork1 : nets.values()) {
                    netWork1.endpoints.remove(handle);
                }
            }
            parent.removeMachineSide(nodePos.asLong(), side);
        }
    }
}
