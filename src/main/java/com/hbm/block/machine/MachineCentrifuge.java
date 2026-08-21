package com.hbm.block.machine;

import com.hbm.core.block.BlockDummyable;
import com.hbm.blockentity.machine.TileMachineCentrifuge;
import com.hbm.core.contents.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;

// 离心机
public class MachineCentrifuge extends BlockDummyable {
    public static String id = "machine_centrifuge";
    public static MultiblockData MULTIBLOCK_DATA;
    public MachineCentrifuge(Properties pProperties) {
        super(pProperties);
        shape = Shapes.or(
                Shapes.box(0, 0D, 0, 1, 1D, 1),
                Shapes.box(0.125, 1D, 0.125, 0.875D, 4D, 0.875D)
        );
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
        return new TileMachineCentrifuge(pPos, pState);
    }

    @Override
    public MultiblockData getMultiblockData() {
        if (MULTIBLOCK_DATA == null) MULTIBLOCK_DATA = new MultiblockData(3, 0, 0, 0, 0, 0);
        return MULTIBLOCK_DATA;
    }
}
