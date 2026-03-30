package com.hbm.block.machine;

import com.hbm.block.base.BlockDummyable;
import com.hbm.blockentity.machine.TileFireBox;
import com.hbm.blockentity.machine.TileFireboxBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;

public class BlockFireBox extends BlockDummyable {
    public BlockFireBox(Properties pProperties) {
        super(pProperties);
//        shape = Block.box(-16.0, 0.0, -16.0, 32.0, 16.0, 32.0);
//        shape = Block.box(0, 0, 0, 16, 16, 16);
        shape = Shapes.block();
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
        return new TileFireBox(pPos, pState);
    }
}
