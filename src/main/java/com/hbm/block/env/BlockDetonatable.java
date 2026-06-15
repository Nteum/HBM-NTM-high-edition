package com.hbm.block.env;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public abstract class BlockDetonatable extends BlockFireCustom{
    int popFuse;
    protected boolean detonateOnCollision;
    protected boolean detonateOnShot;
    public BlockDetonatable(Properties pProperties, int encouragement, int flammability, int popFuse, boolean detonateOnCollision, boolean detonateOnShot) {
        super(pProperties, encouragement, flammability);
        this.detonateOnCollision = detonateOnCollision;
        this.detonateOnShot = detonateOnShot;
    }

    @Override
    public void wasExploded(Level pLevel, BlockPos pPos, Explosion pExplosion) {
        if(!pLevel.isClientSide) {
            Vec3 center = pPos.getCenter();
            PrimedTnt primedTnt = new PrimedTnt(pLevel, center.x, center.y, center.z, null);
            primedTnt.setFuse(popFuse <= 0 ? 0 : pLevel.random.nextInt(popFuse) + popFuse / 2);
            pLevel.addFreshEntity(primedTnt);
        }
    }

    @Override
    public boolean canDropFromExplosion(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        return false;
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        if(!pLevel.isClientSide && shouldIgnite(pLevel, pPos)) {
            pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 3);
            wasExploded(pLevel, pPos, null);
        }
    }
}
