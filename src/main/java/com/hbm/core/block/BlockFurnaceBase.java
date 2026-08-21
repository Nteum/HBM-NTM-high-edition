package com.hbm.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;

public abstract class BlockFurnaceBase extends BlockMachineBase{
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public BlockFurnaceBase(Properties pProperties) {
        super(pProperties);
        //设置状态的初始值
        this.registerDefaultState(
                this.getStateDefinition().any().setValue(LIT,Boolean.FALSE)
        );
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(LIT);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource rand) {
        super.animateTick(pState, pLevel, pPos, rand);
        if (pState.getValue(LIT)){
            float f = pPos.getX() + 0.5F;
            float f1 = pPos.getY() + 0.25F + rand.nextFloat() * 6.0F / 16.0F;
            float f2 = pPos.getZ() + 0.5F;
            float f3 = 0.52F;
            float f4 = rand.nextFloat() * 0.6F - 0.3F;

            if (rand.nextDouble() < 0.1D) {
                pLevel.playLocalSound(f, f1, f2, SoundEvents.SMOKER_SMOKE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }

            Direction facing = pState.getValue(BlockStateProperties.HORIZONTAL_FACING);
            if(facing == Direction.WEST) {
                pLevel.addParticle(ParticleTypes.SMOKE, f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
                pLevel.addParticle(ParticleTypes.FLAME, f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
            } else if(facing == Direction.EAST) {
                pLevel.addParticle(ParticleTypes.SMOKE, f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
                pLevel.addParticle(ParticleTypes.FLAME, f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
            } else if(facing == Direction.NORTH) {
                pLevel.addParticle(ParticleTypes.SMOKE,  f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
                pLevel.addParticle(ParticleTypes.FLAME,  f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
            } else if(facing == Direction.SOUTH) {
                pLevel.addParticle(ParticleTypes.SMOKE, f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
                pLevel.addParticle(ParticleTypes.FLAME, f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}
