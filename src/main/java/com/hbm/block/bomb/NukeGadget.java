package com.hbm.block.bomb;

import com.hbm.blockentity.bomb.TileNukeGadget;
import com.hbm.core.contents.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;

public class NukeGadget extends NukeBomb{
    public NukeGadget(Properties pProperties, int range) {
        super(pProperties, range);
        this.shape = Shapes.box(-1, 0, -1, 1, 2, 1);
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
        return new TileNukeGadget(pPos, pState);
    }

    @Override
    public MultiblockData getMultiblockData() {
        if (MULTI_BLOCK == null) MULTI_BLOCK = new MultiblockData(0, 2, 1, 0, 1, 0);
        return MULTI_BLOCK;
    }
}
