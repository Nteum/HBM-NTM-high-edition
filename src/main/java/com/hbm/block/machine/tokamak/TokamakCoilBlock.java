package com.hbm.block.machine.tokamak;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/**
 * 托卡马克环向磁场线圈。
 * 使用强度属性代表送入的线圈电流，用于计算 B 场。
 */
public class TokamakCoilBlock extends Block {
    public static final IntegerProperty STRENGTH = IntegerProperty.create("strength", 0, 4);

    public TokamakCoilBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(STRENGTH, 2));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STRENGTH);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        // 简化：根据红石信号切换线圈强度，玩家可用比较器/红石充当电源调节
        if (!level.isClientSide) {
            int signal = level.getBestNeighborSignal(pos);
            int mapped = Math.min(4, Math.max(0, signal / 4)); // 0~15 -> 0~3/4
            if (mapped != state.getValue(STRENGTH)) {
                level.setBlock(pos, state.setValue(STRENGTH, mapped), 3);
            }
        }
    }
}
