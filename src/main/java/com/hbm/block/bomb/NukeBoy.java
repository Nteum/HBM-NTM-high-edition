package com.hbm.block.bomb;

import com.hbm.blockentity.bomb.NukeBombBoyEntityBE;
import com.hbm.core.contents.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class NukeBoy extends NukeBomb{
//    public static final VoxelShape this.shape = Block.box(-30,0,0,24,16,16);
    public NukeBoy(Properties pProperties, int range) {
        super(pProperties, range);
        this.shape = Block.box(-24,0,0,16,16,16);
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
        return new NukeBombBoyEntityBE(pPos,pState);
    }

    @Override
    public MultiblockData getMultiblockData() {
        if (MULTI_BLOCK == null) MULTI_BLOCK = new MultiblockData(0,0,0,0,1,1);
        return MULTI_BLOCK;
    }
}
