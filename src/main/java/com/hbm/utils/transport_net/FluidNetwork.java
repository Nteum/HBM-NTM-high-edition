package com.hbm.utils.transport_net;

import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidType;

import java.util.ArrayList;
import java.util.List;

public class FluidNetwork {
    // 一般网络只允许同时存在一种流体类型
    public Fluid fluid;
    List<BlockEntity> machines;
    MutableGraph<BlockPos> transmitters;

    public FluidNetwork(){
        this(Fluids.EMPTY, null, null);
    }
    public FluidNetwork(BlockPos pos){
        this(Fluids.EMPTY, new ArrayList<>(), GraphBuilder.undirected().build());
        this.transmitters.addNode(pos);
    }
    public FluidNetwork(Fluid fluid, List<BlockEntity> machines, MutableGraph<BlockPos> transmitters){
        this.fluid = fluid;
        this.machines = machines;
        this.transmitters = transmitters;
    }

    public void tick(){

    }
}
