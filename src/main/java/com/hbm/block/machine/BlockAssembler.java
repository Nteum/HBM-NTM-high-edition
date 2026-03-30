package com.hbm.block.machine;

import com.hbm.block.base.BlockDummyable;
import com.hbm.blockentity.machine.AssemblerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockAssembler extends BlockDummyable {
    public BlockAssembler(Properties pProperties) {
        super(pProperties);
        shape = Block.box(-32.0,0.0D,-32.0D,32.0D,32.0D,32.0D);
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
        return new AssemblerEntity(pPos,pState);
    }
}
