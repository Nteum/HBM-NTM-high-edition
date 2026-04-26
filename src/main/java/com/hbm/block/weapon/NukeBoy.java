package com.hbm.block.weapon;

import com.hbm.blockentity.weapon.NukeBombBoyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class NukeBoy extends NukeBomb{
//    public static final VoxelShape this.shape = Block.box(-30,0,0,24,16,16);
    public NukeBoy(Properties pProperties, int range) {
        super(pProperties, range);
        this.shape = Block.box(-24,0,0,16,16,16);
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
        return new NukeBombBoyEntity(pPos,pState);
    }
}
