package com.hbm.blockentity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

// ref:mek
public abstract class UpdatableBlockEntity extends BlockEntity {
    public UpdatableBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }
    @NotNull
    public CompoundTag getReducedUpdateTag() {
        //Add the base update tag information
        return super.getUpdateTag();
    }
}
