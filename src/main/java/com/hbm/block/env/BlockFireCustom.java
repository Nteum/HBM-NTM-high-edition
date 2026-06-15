package com.hbm.block.env;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
/**
 * 新加入的可燃性方块
 * */
public class BlockFireCustom extends Block {
    public int encouragement;
    public int flammability;
    public BlockFireCustom(Properties pProperties, int encouragement, int flammability) {
        super(pProperties);
        this.encouragement = encouragement;
        this.flammability = flammability;
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return true;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return flammability;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return encouragement;
    }

    public boolean shouldIgnite(Level world, BlockPos pos) {
        if(flammability == 0) return false;
        for (Direction dir : Direction.values()) {
            if (world.getBlockState(pos.relative(dir)).is(BlockTags.FIRE)) return true;
        }
        return false;
    }
}
