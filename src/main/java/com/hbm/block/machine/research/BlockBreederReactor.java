package com.hbm.block.machine.research;

import com.hbm.block.base.BlockDummyable;
import com.hbm.blockentity.machine.research.BreederReactorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockBreederReactor extends BlockDummyable {

    public BlockBreederReactor(Properties properties) {
        super(properties);
        this.shape = box(0.0D, 0.0D, 0.0D, 16.0D, 48.0D, 16.0D);
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pos, BlockState state) {
        return new BreederReactorBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }
}
