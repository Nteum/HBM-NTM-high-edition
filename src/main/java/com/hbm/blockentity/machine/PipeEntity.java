package com.hbm.blockentity.machine;

import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BasePipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class PipeEntity extends BasePipeBlockEntity {
    public PipeEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.PIPE_ENTITY.get(), pPos, pBlockState);
//        capabilitiesCache.addCapabilityResolver(new SidedEnergyWrapper(new HBMEnergyStorage(10_000)));
    }
}
