package com.hbm.block.machine;

import com.hbm.core.block.BlockDummyable;
import com.hbm.blockentity.machine.TileMinerLarge;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;

// 大型采矿机
public class BlockMinerLarge extends BlockDummyable {
    public BlockMinerLarge(Properties pProperties) {
        super(pProperties);
        shape = Shapes.or(
                Block.box(-48,-16,-48,64,32,64),
                Block.box(-48, -64, 32, 64, 0, 64),
                Block.box(-48, -64, -48, -16, 0, -16),
                Block.box(16, -64, -48, 64, 0, -16)
        );
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
        return new TileMinerLarge(pPos, pState);
    }
}
