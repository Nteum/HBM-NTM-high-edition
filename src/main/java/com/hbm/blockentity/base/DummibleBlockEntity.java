package com.hbm.blockentity.base;

import com.hbm.blockentity.ModBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DummibleBlockEntity extends BlockEntity {
    public BlockPos corePos;
    public DummibleBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.DUMMIBLEBLOCK.get(), pPos, pBlockState);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putIntArray("core_pos",new int[]{corePos.getX(),corePos.getY(),corePos.getZ()});
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        int[] pos = pTag.getIntArray("core_pos");
        corePos = new BlockPos(pos[0],pos[1],pos[2]);
    }
}
