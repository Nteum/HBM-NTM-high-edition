package com.hbm.utils.transport_net;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.*;

/**
 * 流体网络系统
 * 所有流体管道组成流体网络，还在加载的流体网络会每tick轮询流体的供应和需求进行流体分配。流体管道只负责连接方块，暂时不考虑复现流体在管道内流动的具体过程。
 * <br>
 * 主要任务有两个：
 * 1. 维护流体网络的结构
 * 2. 实现流体传输
 * <br>
 * 网络结构的变更可能的情况：
 * 1. JOIN 新方块加入网络
 * 2. LEAVE 方块离开网络，可能是被破坏了
 * 3. LOAD 方块加载了，它和JOIN的区别在哪里？或许在于它一般是批量的
 * 4. UNLOAD 方块被卸载了
 * 5. CONNECT 两个网络连接在一起
 * 6. SPLIT 网络被切开了，这个只能是可能被切开了，因为一般无法根据单个方块的离开而判断网络其他地方也被切开了。
 * 7. CREATE 新建网络，可能是因为方块是单个的，没有加入网络
 * 处理顺序：LOAD->UNLOAD->JOIN->LEAVE->CONNECT->SPLIT->CREATE
 * */
public class FluidNetworkSystem {
    public static Map<Level, FluidNetworkSystem> INSTANCES = new HashMap<>();
    public List<FluidNetwork> networks;
    private Map<BlockPos, FluidNetwork> transmitterToAdd = new HashMap<>();
    private Map<BlockPos, FluidNetwork> transmitterToRemove = new HashMap<>();
    private List<BlockPos> singlePipes = new ArrayList<>();
    private List<List<FluidNetwork>> networkToConnect = new ArrayList<>();
    private Set<FluidNetwork> networkToSplit = new HashSet<>();
    private Map<ChunkPos, List<BlockPos>> chunkToLoad = new HashMap<>();
    private Map<ChunkPos, List<BlockPos>> chunkToUnload = new HashMap<>();

    public static FluidNetworkSystem getOrCreate(Level level){
        return INSTANCES.computeIfAbsent(level, world -> new FluidNetworkSystem());
    }
    /**
     * 每tick结束阶段集中运行，使用TickEvent.ServerTickEvent引用
     * */
    public void tick(){

        // 下面所有网络分配能量
        networks.forEach(FluidNetwork::tick);
    }

    public void join(BlockPos pos, FluidNetwork network){
        transmitterToAdd.put(pos, network);
    }
    public void leave(BlockPos pos, FluidNetwork network){
        transmitterToRemove.put(pos, network);
    }
    public void create(BlockPos pos){
        singlePipes.add(pos);
    }
    public void load(ChunkPos chunkPos, BlockPos pos){
        chunkToLoad.computeIfAbsent(chunkPos, chunk -> new ArrayList<>()).add(pos);
    }
    public void unload(ChunkPos chunkPos, BlockPos pos){
        chunkToUnload.computeIfAbsent(chunkPos, chunk -> new ArrayList<>()).add(pos);
    }
    public void connect(FluidNetwork ... networks){
        this.networkToConnect.add(Arrays.stream(networks).toList());
    }
    public void split(FluidNetwork network){
        this.networkToSplit.add(network);
    }
}
