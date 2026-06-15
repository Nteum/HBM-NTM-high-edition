package com.hbm.block.env;

import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class BlockHazardNuke extends BlockHazard{
    public BlockHazardNuke(Properties pProperties) {
        super(pProperties);
    }
    // 是否允许通过tick计时，返回true，tick才能生效
    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return true;
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource rand) {
        Direction dir = Direction.from3DDataValue(rand.nextInt(6));
        if (rand.nextInt(2) == 0 && pLevel.getBlockState(pPos.relative(dir)).isAir()){
            pLevel.setBlock(pPos.relative(dir), ModBlocks.GAS_RADON_DENSE.get().defaultBlockState(), 3);
        }
        super.tick(pState, pLevel, pPos, rand);
    }
}
