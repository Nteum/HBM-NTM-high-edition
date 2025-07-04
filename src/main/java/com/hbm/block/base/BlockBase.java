package com.hbm.block.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BeaconBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

//HBM所有专属方块的父类
public class BlockBase extends Block {
    //是否可用作信标基座
    private boolean beaconable = false;
    //生物是否可在上面生成
    private boolean canSpawn = true;
    public BlockBase(){
        super(BlockBehaviour.Properties.copy(Blocks.STONE));
    }
    public BlockBase(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean isValidSpawn(BlockState state, BlockGetter level, BlockPos pos, SpawnPlacements.Type type, EntityType<?> entityType) {
        return this.canSpawn ? super.isValidSpawn(state, level, pos, type, entityType) : false;
    }
}
