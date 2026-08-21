package com.hbm.block.machine;

import com.hbm.blockentity.machine.CokerEntity;
import com.hbm.core.block.BlockDummyable;
import com.hbm.core.contents.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 焦化装置（多方块）。移植自旧版 MachineCoker（22 格高）。
 */
public class BlockCoker extends BlockDummyable {
    public static MultiblockData MULTIBLOCK_DATA;
    public BlockCoker(Properties pProperties){
        super(pProperties);
        this.shape = Block.box(-16.0D, 0.0D, -16.0D, 32.0D, 352.0D, 16.0D);
    }

    @Nullable
    @Override
    protected BlockEntity mainBlockEntity(BlockPos pos, BlockState state) {
        return new CokerEntity(pos, state);
    }

    @Override
    public MultiblockData getMultiblockData() {
        if (MULTIBLOCK_DATA == null){
            MULTIBLOCK_DATA = new MultiblockData(22, 0, 1, 1, 1, 1);
        }
        return MULTIBLOCK_DATA;
    }
}
