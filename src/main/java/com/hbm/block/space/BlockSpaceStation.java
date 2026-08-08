package com.hbm.block.space;

import com.hbm.block.base.BlockDummyable;
import com.hbm.blockentity.machine.TileSpaceStation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

// 航天版作 OrbitalStation
public class BlockSpaceStation extends BlockDummyable {
    public BlockSpaceStation(Properties pProperties) {
        super(pProperties);
        shape = Block.box(-40, 0, -40, 32, 40, 40);
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
        return new TileSpaceStation(pPos, pState);
    }
}
