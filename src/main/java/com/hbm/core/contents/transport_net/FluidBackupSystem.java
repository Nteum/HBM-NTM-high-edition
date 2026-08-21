package com.hbm.core.contents.transport_net;

import com.hbm.core.blockentity.BEPipeBase;
import com.hbm.blockentity.logistic.PipeEntityBEPipeBase;
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

import javax.annotation.Nonnull;
import java.util.*;

/**
 * 流体分配网络系统，继承 AbstractNetworkSystem。
 * 相比旧版：
 * 1. nodeMap/NodeInfo 迁移到基类的 MutableNodeData
 * 2. 网络拓扑（合并/切分/连通分量）复用基类
 * 3. 保留流体特有的机器-节点连接表 node2Machines、流体类型仲裁、供需分配逻辑
 */
public class FluidBackupSystem extends AbstractNetworkSystem<FluidBackupSystem.FluidNetwork> {
    public static Map<Level, FluidBackupSystem> INSTANCES = new HashMap<>();

    // 所有机器的数据，"节点 - 机器连接表"，主键是节点位置，值是节点不同方向连接的机器
    protected final Long2ObjectMap<MachineAttachment[]> node2Machines = new Long2ObjectOpenHashMap<>();
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
        super(level);
    }

    @Override
    protected FluidNetwork createNet(int netID){
        return new FluidNetwork(this, netID, new LongOpenHashSet(), new HashSet<>());
    }
    /** 用节点集合创建一个新网络并注册到 nets，返回该网络 */
    private FluidNetwork createNetWithNodes(LongSet longSet){
        if (longSet.isEmpty()) return null;
        int netID = createNetID();
        FluidNetwork netWork = new FluidNetwork(this, netID, new LongOpenHashSet(), new HashSet<>());
        netWork.rebuild(longSet);
        nets.put(netID, netWork);
        return netWork;
    }

    // 从更新队列中加入网络
    @Override
    protected void updateStructure() {
        LongSet posCache;
        BlockPos blockPos, pos, pos1;
        BlockEntity be;
        MutableNodeData nodeInfo;
        FluidNetwork netWork;
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
                FluidNetwork newNet = createNetWithNodes(iterator.next());
                if (newNet != null) netsToUpdatePipe.add(newNet.code);
            }
        }

        // 3. 节点加入 (重构：物理连通双向建立 + 统一借用 ID 策略)
        for (long l : tobeJoin) {
            if (nodeMap.containsKey(l)) continue;

            blockPos = BlockPos.of(l);
            be = WorldUtils.getTileEntity(level, blockPos);

            if (be instanceof BEPipeBase pipe) {
                // 初始化起始点的 NodeInfo，先挂一个缺省占位 ID -1
                nodeMap.put(l, new MutableNodeData(-1, new LongOpenHashSet()));
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
                        if ((be = WorldUtils.getTileEntity(level, checkPos)) instanceof BEPipeBase pipeTile){
                            // 如果不是起始点，需要初始化 NodeInfo
                            if (fromDir != null) {
                                if (!nodeMap.containsKey(checkPosL)) {
                                    nodeMap.put(checkPosL, new MutableNodeData(-1, new LongOpenHashSet()));
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
                    FluidNetwork created = createNetWithNodes(posCache);
                    finalNetId = created == null ? -1 : created.code;
                    if (created != null) netsToUpdatePipe.add(finalNetId);
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

                FluidNetwork deadNet = nets.get(deadNetId);
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
            MutableNodeData nodeinfo = nodeMap.get(pipePos);
            FluidNetwork net = nets.get(nodeinfo.netId);
            if (net == null) continue;
            PipeEntityBEPipeBase pipe = WorldUtils.getTileEntity(PipeEntityBEPipeBase.class, level, BlockPos.of(pipePos));
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

    public static class FluidNetwork extends AbstractNetwork {
        FluidBackupSystem parent;
        LongSet nodes;
        Set<LazyOptional<IFluidHandler>> endpoints;
        private Fluid fluid = Fluids.EMPTY;
        public FluidNetwork(FluidBackupSystem parent, int id, LongSet nodes, Set<LazyOptional<IFluidHandler>> endpoints){
            super(id);
            this.parent = parent;
            this.nodes = nodes;
            this.endpoints = endpoints;
        }        public Fluid getFluid(){
            return fluid;
        }
        public FluidBackupSystem getParent(){
            return this.parent;
        }
        public void setFluid(final Fluid fluid) {
            this.fluid = fluid == null ? Fluids.EMPTY : fluid;
        }
        @Override
        public boolean isEmpty(){
            return nodes.isEmpty() && endpoints.isEmpty();
        }
        @Override
        public void addNodes(LongSet points){
            for (long point : points) {
                if (!this.parent.nodeMap.containsKey(point)) continue;
                this.nodes.add(point);
                MutableNodeData nodeInfo = this.parent.nodeMap.get(point);
                for (long conn : nodeInfo.connPos) {
                    if (this.parent.node2Machines.containsKey(conn)){
                        Direction direction = DirectionUtils.posToDirection(BlockPos.of(point), BlockPos.of(conn));
                        if (direction != null){
                            MachineAttachment machineAttachment = this.parent.node2Machines.get(conn)[direction.getOpposite().get3DDataValue()];
                            if (machineAttachment != null && machineAttachment.handler != null) this.endpoints.add(machineAttachment.handler);
                        }
                    }
                }
                nodeInfo.netId = this.code;
            }
        }
        @Override
        public void addNode(long point){
            LongSet single = new LongOpenHashSet();
            single.add(point);
            addNodes(single);
        }
        public void rebuild(LongSet points){
            this.nodes.clear();
            this.endpoints.clear();
            addNodes(points);
        }
        @Override
        public void removeNode(long node){
            this.nodes.remove(node);
            MutableNodeData nodeInfo = this.parent.nodeMap.get(node);
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
                this.parent.nets.remove(this.code);
            }
        }
        public void merge(FluidNetwork other){
            if (other == null || other == this || (this.fluid != Fluids.EMPTY && other.fluid != Fluids.EMPTY && this.fluid != other.fluid)) return;
            if (this.fluid == Fluids.EMPTY) this.fluid = other.fluid;
            this.nodes.addAll(other.nodes);
            for (long node : other.nodes) {
                if (parent.nodeMap.containsKey(node)) parent.nodeMap.get(node).netId = this.code;
            }
            this.endpoints.addAll(other.endpoints);
            assignNet(other.nodes);
        }
        public void assignNet(){
            this.assignNet(this.nodes);
        }
        public void assignNet(LongSet nodes){
            for (Long node : nodes) {
                PipeEntityBEPipeBase pipeEntity = WorldUtils.getTileEntity(PipeEntityBEPipeBase.class, this.parent.level, BlockPos.of(node));
                if (pipeEntity != null) pipeEntity.network = this;
                if (parent.nodeMap.containsKey(node)) parent.nodeMap.get(node).netId = this.code;
            }
        }
        @Override
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
                                PipeEntityBEPipeBase pipeEntity = WorldUtils.getTileEntity(PipeEntityBEPipeBase.class, parent.level, BlockPos.of(node));
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
            MutableNodeData nodeData = parent.nodeMap.get(this.nodePos.relative(this.side).asLong());
            if (nodeData != null){
                FluidNetwork netWork = nets.get(nodeData.netId);
                if (netWork != null) netWork.endpoints.remove(handle);
            }else {
                for (FluidNetwork netWork1 : nets.values()) {
                    netWork1.endpoints.remove(handle);
                }
            }
            parent.removeMachineSide(nodePos.asLong(), side);
        }
    }

    protected static class MachineAttachment {
        Direction side;
        LazyOptional<IFluidHandler> handler;
        InvalidationHandler listener;
        MachineAttachment(Direction side, LazyOptional<IFluidHandler> handler, InvalidationHandler listener){
            this.side = side;
            this.handler = handler;
            this.listener = listener;
        }
    }
}
