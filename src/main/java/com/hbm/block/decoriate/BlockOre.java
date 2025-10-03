package com.hbm.block.decoriate;

import com.hbm.addational_data.chunk.RadiationManager;
import com.hbm.handler.radiation.ChunkRadiationManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockOre extends Block {
    private float rad = 0.0F;
    public BlockOre(Properties pProperties){
        this(pProperties, false);
    }
    public BlockOre(Properties pProperties, float rad) {
        this(pProperties, true);
        this.rad = rad;
    }
    public BlockOre(Properties pProperties, boolean randomTick) {
        super(randomTick ? pProperties.randomTicks() : pProperties);
    }

    @Override
    public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
        super.stepOn(pLevel, pPos, pState, pEntity);
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        super.randomTick(pState, pLevel, pPos, pRandom);
        if(this.rad > 0) {
            RadiationManager.incrementRadiation(pLevel, pPos, this.rad);
            pLevel.scheduleTick(pPos, this, tickRate(pLevel));
        }
    }
    public int tickRate(Level world) {
        if(this.rad > 0) return 20;
        return 100;
    }
}
