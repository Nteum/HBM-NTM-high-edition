package com.hbm.core.contents.transport_net;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 通用分配网络系统基类（每个 Level 一份）。
 *
 * 职责：
 * 1. 拓扑存储：nodeMap（节点位置 -> 所属网络id + 连接关系）
 * 2. 网络生命周期：合并（merge）/ 切分（split）/ 节点加入离开（join/leave/link/cut）
 * 3. 延迟队列：待加入、待离开、待合并、待切分
 *
 * 子类只需实现：
 * - createNet(int id) 创建具体资源网络
 * - onNodeJoin / onNodeLeave 钩子（挂载/卸载机器）
 * - tickNet 分发
 *
 * 相比旧 EnergyNetworkSystem / FluidBackupSystem 的重复实现，本基类统一了：
 * - BFS 连通分量算法（findParts）
 * - 网络合并/切分流程
 * - 节点连接关系维护
 */
public abstract class AbstractNetworkSystem<N extends AbstractNetwork> {
    protected final Level level;
    // 所有需要的网络
    protected final Int2ObjectMap<N> nets = new Int2ObjectOpenHashMap<>();
    // 所有节点的连接关系，key 为节点位置，value 为 (网络id, 连接节点集合)
    protected final Long2ObjectMap<MutableNodeData> nodeMap = new Long2ObjectOpenHashMap<>();
    // 待加入网络的节点，用于延迟加载
    protected final LongSet tobeJoin = new LongOpenHashSet();
    // 待离开网络的节点
    protected final LongSet tobeLeave = new LongOpenHashSet();
    // 即将合并的网络合并处的节点表
    protected final Set<long[]> readyToLink = new HashSet<>();
    // 即将切分的网络
    protected final IntSet readyToSplit = new IntOpenHashSet();

    public AbstractNetworkSystem(Level level){
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
    // 连接连上
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
    // 连接切断
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

    // 从更新队列中加入网络，由子类实现具体入网逻辑
    protected abstract void updateStructure();

    protected abstract N createNet(int netID);

    protected int createNetID(){
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
            queue.addAll(nodeMap.getOrDefault(poll.longValue(), MutableNodeData.EMPTY).connPos);
        }
        return nodes;
    }

    /** 将一个节点集合划入网络 */
    protected void spreadNet(final long root){
        LongSet nodes = new LongOpenHashSet();
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
                }else {
                    queue.add(l);
                }
            }
        }
        if (net < 0){
            net = createNetID();
            N network = createNet(net);
            nets.put(net, network);
            network.addNodes(nodes);
        }else {
            nets.get(net).addNodes(nodes);
        }
        assignNetNodes(net, nodes);
    }

    /** 网络合并 */
    protected N connect(final N... candidates) {
        if (candidates == null) return null;
        if (candidates.length == 1)return candidates[0];
        N primary = null;
        for (N candidate : candidates) {
            if (candidate == null) {
                continue;
            }
            if (primary == null) {
                primary = candidate;
                continue;
            }
            LongSet candidateNodes = new LongOpenHashSet(candidate.getNodes());
            primary.absorb(candidate);
            for (long l : candidateNodes) {
                nodeMap.getOrDefault(l, MutableNodeData.EMPTY).netId = primary.code;
            }
            nets.remove(candidate.code);
            onNetMerge(primary, candidate);
        }
        return primary;
    }

    /** 网络切分：将指定网络的节点按连通性拆分 */
    protected void splitNet(final N network){
        if (network == null || network.isEmpty()) return;
        List<LongSet> parts = findParts(network);
        if (parts.size() <= 1) return;
        java.util.Iterator<LongSet> iterator = parts.iterator();
        LongSet longSet = iterator.next();
        network.replaceNodes(longSet);
        assignNetNodes(network.code, longSet);
        while (iterator.hasNext()){
            longSet = iterator.next();
            int netID = createNetID();
            N newNetwork = createNet(netID);
            newNetwork.addNodes(longSet);
            nets.put(netID, newNetwork);
            assignNetNodes(netID, longSet);
        }
    }
    // 找出网络的连通分量
    protected List<LongSet> findParts(final N network){
        LongOpenHashSet remain = new LongOpenHashSet(network.getNodes());
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
                for (long l : nodeMap.getOrDefault(poll, MutableNodeData.EMPTY).connPos) {
                    if (!component.contains(l)) queue.add(l);
                }
            }
            result.add(component);
        }
        return result;
    }

    /** 将节点集合的网络 id 写回 nodeMap */
    protected void assignNetNodes(int netID, LongSet nodes){
        for (long l : nodes) {
            if (nodeMap.containsKey(l)) nodeMap.get(l).netId = netID;
        }
    }

    /** 子类可在网络合并后做一些收尾工作（如重新绑定机器） */
    protected void onNetMerge(N primary, N merged){}

    /** 节点连接数据结构 */
    public static class MutableNodeData {
        public static final MutableNodeData EMPTY = new MutableNodeData(-1, new LongOpenHashSet());
        int netId;
        LongSet connPos;
        public MutableNodeData(int netId, LongSet connPos){
            this.netId = netId;
            this.connPos = connPos;
        }
    }
}
