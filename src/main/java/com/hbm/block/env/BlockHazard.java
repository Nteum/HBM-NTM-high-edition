package com.hbm.block.env;

import com.hbm.particle.ParticleSystem;
import net.minecraft.client.particle.DustColorTransitionParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BlockHazard extends Block {
    ExtDisplayEffect extEffect;
    public BlockHazard(Properties pProperties) {
        super(pProperties);
    }

    public BlockHazard setDisplayEffect(ExtDisplayEffect extEffect) {
        this.extEffect = extEffect;
        return this;
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource rand) {
        super.animateTick(pState, pLevel, pPos, rand);
        if(extEffect == null)
            return;

        switch(extEffect) {
            case RADFOG:
            case SCHRAB:
            case FLAMES:
                for (Direction direction : Direction.values()) {
                    if (direction == Direction.DOWN && this.extEffect == ExtDisplayEffect.FLAMES) continue;
                    if (pLevel.getBlockState(pPos.relative(direction)).isAir()){
                        Vec3i normal = direction.getNormal();
                        Vec3 vec = pPos.getCenter().add(
                                normal.getX() * 0.5 + normal.getX() != 0 ? (rand.nextDouble() * normal.getX()) : (rand.nextDouble() * 3 - 1.5),
                                normal.getY() * 0.5 + normal.getY() != 0 ? (rand.nextDouble() * normal.getY()) : (rand.nextDouble() * 3 - 1.5),
                                normal.getZ() * 0.5 + normal.getZ() != 0 ? (rand.nextDouble() * normal.getZ()) : (rand.nextDouble() * 3 - 1.5)
                        );
                        if (extEffect == ExtDisplayEffect.RADFOG){
                            pLevel.addParticle(ParticleTypes.MYCELIUM, vec.x, vec.y, vec.z, 0,0,0);
                        }else if (extEffect == ExtDisplayEffect.SCHRAB){
                            DustParticleOptions dustParticleOptions = new DustParticleOptions(Vec3.fromRGB24(FastColor.ARGB32.color(255, 0, 255, 255)).toVector3f(), 1);
                            pLevel.addParticle(dustParticleOptions, vec.x, vec.y, vec.z, 0,0,0);
                        }else if (extEffect == ExtDisplayEffect.FLAMES){
                            pLevel.addParticle(ParticleTypes.FLAME, vec.x, vec.y, vec.z, 0,0,0);
                            pLevel.addParticle(ParticleTypes.SMOKE, vec.x, vec.y, vec.z, 0,0,0);
                            pLevel.addParticle(ParticleTypes.SMOKE, vec.x, vec.y, vec.z, 0,0.1,0);
                        }
                    }
                }
                break;

            case SPARKS:
                break;

            case LAVAPOP:
                pLevel.addParticle(ParticleTypes.LAVA, pPos.getX() + rand.nextFloat(), pPos.getY() + 1.1f, pPos.getZ() + rand.nextFloat(), 0,0,0);
                break;

            default: break;
        }
    }

    public enum ExtDisplayEffect {
        RADFOG,
        SPARKS,
        SCHRAB,
        FLAMES,
        LAVAPOP
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        super.randomTick(pState, pLevel, pPos, pRandom);
    }
}
