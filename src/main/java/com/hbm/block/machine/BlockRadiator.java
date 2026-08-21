package com.hbm.block.machine;

import com.hbm.blockentity.machine.RadiatorEntityBE;
import com.hbm.core.block.BlockDummyable;
import com.hbm.core.contents.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 散热器（多方块）。移植自旧版 MachineRadiator。
 */
public class BlockRadiator extends BlockDummyable {
    public static MultiblockData MULTIBLOCK_DATA;
    public BlockRadiator(Properties pProperties){
        super(pProperties);
        this.shape = Block.box(-32.0D, 0.0D, -32.0D, 32.0D, 16.0D, 32.0D);
    }

    @Nullable
    @Override
    protected BlockEntity mainBlockEntity(BlockPos pos, BlockState state) {
        return new RadiatorEntityBE(pos, state);
    }

    @Override
    public MultiblockData getMultiblockData() {
        if (MULTIBLOCK_DATA == null){
            MULTIBLOCK_DATA = new MultiblockData(0, 0, 2, 2, 0, 0);
        }
        return MULTIBLOCK_DATA;
    }
}
