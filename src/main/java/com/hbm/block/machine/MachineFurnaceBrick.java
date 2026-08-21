package com.hbm.block.machine;

import com.hbm.blockentity.machine.TileEntityFurnaceBrick;
import com.hbm.core.block.BlockFurnaceBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MachineFurnaceBrick extends BlockFurnaceBase {
    public MachineFurnaceBrick(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TileEntityFurnaceBrick(blockPos, blockState);
    }
}
