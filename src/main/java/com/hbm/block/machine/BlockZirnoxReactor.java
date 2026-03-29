package com.hbm.block.machine;

import com.hbm.block.base.BlockDummyable;
import com.hbm.blockentity.machine.ZirnoxReactorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockZirnoxReactor extends BlockDummyable {
    public BlockZirnoxReactor(Properties properties) {
        super(properties);
        this.shape = box(-32.0D, 0.0D, -32.0D, 48.0D, 80.0D, 48.0D);
    }

    @Override
    protected int placementOffset() {
        return -2;
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
        return new ZirnoxReactorBlockEntity(pPos, pState);
    }
}
