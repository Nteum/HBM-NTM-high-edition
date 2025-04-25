package com.hbm.blockentity.base;

import com.hbm.api.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BedLikeBlockEntity extends BaseMachineBlockEntity{
    public boolean flagFormed = false;
    protected final MultiblockData multiblockData = new MultiblockData();
    protected BedLikeBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

}
