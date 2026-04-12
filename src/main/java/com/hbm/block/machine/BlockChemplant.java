package com.hbm.block.machine;

import com.hbm.block.base.BlockDummyable;
import com.hbm.blockentity.machine.ChemplantEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockChemplant extends BlockDummyable {
    public BlockChemplant(Properties pProperties) {
        super(pProperties);
        this.shape = Block.box(-32.0,0.0D,-32.0D,32.0D,48.0D,32.0D);
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
        return new ChemplantEntity(pPos,pState);
    }
}
