package com.hbm.block.machine.icf;

import com.hbm.block.base.BaseMachineBlock;
import com.hbm.blockentity.machine.icf.ICFPressBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockICFPress extends BaseMachineBlock implements EntityBlock {

    public BlockICFPress(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ICFPressBlockEntity(pos, state);
    }
}
