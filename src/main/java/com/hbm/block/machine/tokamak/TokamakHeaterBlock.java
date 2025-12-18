package com.hbm.block.machine.tokamak;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * 加热器方块，提供欧姆/射频加热功率。
 * 通过红石信号决定是否工作，实际功率在控制器里统计。
 */
public class TokamakHeaterBlock extends Block {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public TokamakHeaterBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(ACTIVE, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (!level.isClientSide) {
            boolean powered = level.hasNeighborSignal(pos);
            if (powered != state.getValue(ACTIVE)) {
                level.setBlock(pos, state.setValue(ACTIVE, powered), 3);
            }
        }
    }
}
