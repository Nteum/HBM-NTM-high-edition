package com.hbm.block.machine;

import com.hbm.block.base.BaseMachineBlock;
import com.hbm.blockentity.machine.ShredderEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockShredder extends BaseMachineBlock {
    public BlockShredder(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ShredderEntity(pos, state);
    }
}
