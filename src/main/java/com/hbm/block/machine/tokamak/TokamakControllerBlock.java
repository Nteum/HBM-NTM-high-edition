package com.hbm.block.machine.tokamak;

import com.hbm.block.base.BaseMachineBlock;
import com.hbm.blockentity.machine.tokamak.TokamakControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 托卡马克主管理方块。
 * 负责打开界面并挂载控制器方块实体。
 */
public class TokamakControllerBlock extends BaseMachineBlock implements EntityBlock {

    public TokamakControllerBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TokamakControllerBlockEntity(pos, state);
    }
}
