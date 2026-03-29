package com.hbm.block.weapon;

import com.hbm.blockentity.weapon.NukeBombFatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class NukeFat extends NukeBomb{
//    public static final VoxelShape this.shape = Block.box(-14,0,-16,14,24,30);
    public NukeFat(Properties pProperties,int range) {
        super(pProperties,range);
        this.shape = Block.box(-20,0,-16,32,24,16);
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
        return new NukeBombFatEntity(pPos,pState);
    }
//
//    @Override
//    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
//        return pState.getValue(IS_CORE) ? SHAPE : Shapes.block();
//    }
}
