package com.hbm.blockentity.machine;

import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BasePipeBlockEntity;
import com.hbm.utils.transport_net.FluidNetwork;
import com.hbm.utils.transport_net.FluidNetworkSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class PipeEntity extends BasePipeBlockEntity {
    public FluidNetwork network;

    public PipeEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.PIPE_ENTITY.get(), pPos, pBlockState);
    }

    public Fluid getFluid(){
        return network != null ? network.getFluid() : Fluids.EMPTY;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.hasLevel() && !this.getLevel().isClientSide()) {
            FluidNetworkSystem.getOrCreate(this.getLevel()).load(this.getLevel().getChunk(this.worldPosition).getPos(), this.worldPosition);
        }
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if (this.hasLevel() && !this.getLevel().isClientSide()) {
            FluidNetworkSystem.getOrCreate(this.getLevel()).unload(this.getLevel().getChunk(this.worldPosition).getPos(), this.worldPosition);
        }
    }
}
