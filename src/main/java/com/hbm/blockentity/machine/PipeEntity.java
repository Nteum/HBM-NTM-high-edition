package com.hbm.blockentity.machine;

import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BasePipeBlockEntity;
import com.hbm.utils.transport_net.FluidNetwork;
import com.hbm.utils.transport_net.FluidNetworkSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.obj.ObjLoader;
import org.jetbrains.annotations.NotNull;

public class PipeEntity extends BasePipeBlockEntity {
    public FluidNetwork network;
    int oldColor = -1;

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

    // 获取流体颜色
    public int getFluidColor() {
        Fluid fluid = getFluid();
        int color = fluid == Fluids.EMPTY ? -1 : IClientFluidTypeExtensions.of(fluid).getTintColor();
        if (color != oldColor){ // 颜色变化就更新一下
            this.oldColor = color;
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_CLIENTS);
        }
        return color;
    }
}
