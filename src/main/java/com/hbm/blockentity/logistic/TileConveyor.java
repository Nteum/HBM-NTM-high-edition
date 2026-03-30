package com.hbm.blockentity.logistic;

import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base2.CapabilityBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileConveyor extends CapabilityBlockEntity {
    public TileConveyor(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.TILE_CONVEYOR.get(), pPos, pBlockState);
    }
}
