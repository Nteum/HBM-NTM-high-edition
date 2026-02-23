package com.hbm.block.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;

// 可以贴在某个方向上的方块
public interface IFaceAttach {
    default boolean canAttach(LevelReader pReader, BlockPos pPos, Direction pDirection) {
        BlockPos blockpos = pPos.relative(pDirection);
        return pReader.getBlockState(blockpos).isFaceSturdy(pReader, blockpos, pDirection.getOpposite());
    }
    default Direction getAttachedDirection(BlockPlaceContext pContext){
        for (Direction direction : pContext.getNearestLookingDirections()) {
            if (canAttach(pContext.getLevel(), pContext.getClickedPos(), direction))
                return direction;
        }
        return null;
    }
}
