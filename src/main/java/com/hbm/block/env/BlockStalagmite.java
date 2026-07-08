package com.hbm.block.env;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStalagmite extends Block {
    public BlockStalagmite(Properties pProperties) {
        super(pProperties);
    }

    /**
     * 🔥 核心一：判定当前位置能否允许该方块生存（比如玩家放置时、或者世界生成时）
     */
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);

        // 🎯 示例逻辑：这个方块必须放在石头（或者你模组里的基岩矿、多孔岩）上
        // 如果你想允许放在任何坚固表面上，可以用 belowState.isFaceSturdy(level, belowPos, Direction.UP)
        return belowState.isFaceSturdy(level, belowPos, Direction.UP);
    }

    /**
     * 🔥 核心二：当周围的方块发生改变时（比如地下的支撑方块被玩家挖掉了）
     * 游戏会触发这个方法。如果检测到无法生存，直接原地变成空气并销毁，触发掉落。
     */
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        // 如果改变的方向是下方（Direction.DOWN），且新的状态导致自己无法生存
        if (direction == Direction.DOWN && !this.canSurvive(state, level, currentPos)) {
            // 💡 返回空气（代表把自己销毁）。
            // 原版系统在检测到 updateShape 返回空气时，会自动根据战利品表触发该方块的掉落物生成！
            return Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }
}
