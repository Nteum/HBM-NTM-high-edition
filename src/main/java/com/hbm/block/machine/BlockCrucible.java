package com.hbm.block.machine;

import com.hbm.blockentity.machine.CrucibleEntityBE;
import com.hbm.core.block.BlockDummyable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockCrucible extends BlockDummyable {
    public BlockCrucible(Properties pProperties) {
        super(pProperties);
        shape = Block.box(-16.0,0,-16.0,16.0,24.0,16.0);
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
        return new CrucibleEntityBE(pPos,pState);
    }
}
