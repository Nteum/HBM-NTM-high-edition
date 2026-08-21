package com.hbm.block.bomb;

import com.hbm.blockentity.bomb.NukeBombFatEntityBE;
import com.hbm.core.contents.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class NukeFat extends NukeBomb{
//    public static final VoxelShape this.shape = Block.box(-14,0,-16,14,24,30);
    public NukeFat(Properties pProperties,int range) {
        super(pProperties,range);
        this.shape = Block.box(-20,0,-16,32,24,16);
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
        return new NukeBombFatEntityBE(pPos,pState);
    }
    @Override
    public MultiblockData getMultiblockData() {
        if (MULTI_BLOCK == null) MULTI_BLOCK = new MultiblockData(1,0,0,1,1,1);
        return MULTI_BLOCK;
    }
}
