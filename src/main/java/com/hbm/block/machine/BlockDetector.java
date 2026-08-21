package com.hbm.block.machine;

import com.hbm.block.base.BlockMachineBase;
import com.hbm.blockentity.machine.DetectorEntityBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;

/**
 * 能量检测器。
 * 移植自旧版 PowerDetector：收到电网能量时点亮并输出红石强信号。
 * 1.20.1 用 LIT blockstate 替代旧版 meta。
 */
public class BlockDetector extends BlockMachineBase {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public BlockDetector(Properties pProperties){
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(LIT);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new DetectorEntityBE(pPos, pState);
    }

    @Override
    public boolean isSignalSource(BlockState pState) {
        return true;
    }

    @Override
    public int getDirectSignal(BlockState pState, BlockGetter pLevel, BlockPos pPos, Direction pDirection) {
        return pState.getValue(LIT) ? 15 : 0;
    }

    @Override
    public int getSignal(BlockState pState, BlockGetter pLevel, BlockPos pPos, Direction pDirection) {
        return pState.getValue(LIT) ? 15 : 0;
    }
}
