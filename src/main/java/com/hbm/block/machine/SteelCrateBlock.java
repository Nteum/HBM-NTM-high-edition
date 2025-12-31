package com.hbm.block.machine;

import com.hbm.blockentity.machine.SteelCrateBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Steel-tier crate that reuses the Iron crate behavior but swaps the block entity.
 */
public class SteelCrateBlock extends IronCrateBlock {

    public SteelCrateBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SteelCrateBlockEntity(pos, state);
    }
}
