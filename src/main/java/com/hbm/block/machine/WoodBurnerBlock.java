package com.hbm.block.machine;

import com.hbm.blockentity.machine.WoodBurnerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WoodBurnerBlock extends BlockLitSingleBlockMachine {

    public WoodBurnerBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WoodBurnerBlockEntity(pos, state);
    }
}
