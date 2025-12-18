package com.hbm.block.machine.tokamak;

import com.hbm.blockentity.machine.tokamak.TokamakInjectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * D/T 燃料注入器，实际燃料逻辑由控制器统一处理。
 */
public class TokamakInjectorBlock extends Block implements EntityBlock {
    public TokamakInjectorBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TokamakInjectorBlockEntity(pos, state);
    }
}
