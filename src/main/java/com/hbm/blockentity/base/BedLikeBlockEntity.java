package com.hbm.blockentity.base;

import com.hbm.utils.multiblock.HBMMultiData;
import com.hbm.blockentity.base2.BaseMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BedLikeBlockEntity extends BaseMachineBlockEntity {
    public boolean flagFormed = false;
    protected final HBMMultiData multiblockData = new HBMMultiData();
    protected BedLikeBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

}
