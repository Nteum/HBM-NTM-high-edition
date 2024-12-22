package com.hbm.blockentity.machine;

import com.hbm.blockentity.ModBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CableEntity extends BlockEntity {
    public CableEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.CABLE_ENTITY.get(), pPos, pBlockState);
    }
}
