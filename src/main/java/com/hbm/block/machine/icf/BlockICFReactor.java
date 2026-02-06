package com.hbm.block.machine.icf;

import com.hbm.block.base.BlockDummyable;
import com.hbm.blockentity.machine.icf.ICFReactorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockICFReactor extends BlockDummyable {

    public BlockICFReactor(Properties properties) {
        super(properties);
        this.shape = box(-32.0D, 0.0D, -32.0D, 32.0D, 64.0D, 32.0D);
    }

    @Override
    protected int placementOffset() {
        return 1;
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
        return new ICFReactorBlockEntity(pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return state.getValue(IS_CORE) ? RenderShape.MODEL : RenderShape.INVISIBLE;
    }
}
