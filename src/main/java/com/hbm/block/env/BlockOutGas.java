package com.hbm.block.env;

import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

// 被破坏会释放气体的方块
public class BlockOutGas extends Block {
    boolean randomTick;
    int rate;
    // 方块被破坏时的气体替换的半径
    int replaceWhenBreak;

    public BlockOutGas(boolean randomTick, int rate, int onBreak, Properties pProperties) {
        super(pProperties);
        this.randomTick = randomTick;
        this.rate = rate;
        this.replaceWhenBreak = onBreak;
    }
    public int tickRate(Level level) {
        return rate;
    }
    public Block getGas(){
        if (this == ModBlocks.ANCIENT_SCRAP.get() || this == ModBlocks.BLOCK_CORIUM_COBBLE.get()) return ModBlocks.GAS_RADON.get();
        if (this == ModBlocks.BLOCK_ASBESTOS.get()) return ModBlocks.GAS_RADON_ASBESTOS.get();

        return Blocks.AIR;
    }

    @Override
    public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
        super.stepOn(pLevel, pPos, pState, pEntity);
        if(this.randomTick && getGas() == ModBlocks.GAS_RADON_ASBESTOS.get()) {
            if(pLevel.getBlockState(pPos.above()).isAir()) {
                if (pLevel.random.nextInt(10) == 0) pLevel.setBlock(pPos.above(), ModBlocks.GAS_RADON_ASBESTOS.get().defaultBlockState(), 3);
                for (int i = 0; i < 5; i++) {
                    pLevel.addParticle(ParticleTypes.MYCELIUM, pPos.getX() + pLevel.random.nextFloat(), pPos.getY() + 1.1, pPos.getZ() + pLevel.random.nextFloat(), 0,0,0);
                }
            }
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return this.randomTick;
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource rand) {
        super.tick(pState, pLevel, pPos, rand);
        Direction dir = Direction.from3DDataValue(rand.nextInt(6));
        if (!pLevel.getBlockState(pPos.relative(dir)).isAir()) pLevel.setBlock(pPos.relative(dir), getGas().defaultBlockState(), 3);
    }

    @Override
    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
        replaceWithGas(pLevel, pPos);
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        super.onBlockExploded(state, level, pos, explosion);
        replaceWithGas(level, pos);
    }
    private void replaceWithGas(Level level, BlockPos pos){
        if (replaceWhenBreak == 1){
            level.setBlock(pos, getGas().defaultBlockState(), 3);
        }else {
            for (int ix = -replaceWhenBreak; ix <= replaceWhenBreak; ix++)
                for (int iy = -replaceWhenBreak; iy <= replaceWhenBreak; iy++)
                    for (int iz = -replaceWhenBreak; iz <= replaceWhenBreak; iz++)
                        if (Math.abs(ix + iy + iz) < 5 && Math.abs(ix + iy + iz) > 0 && level.getBlockState(pos.offset(ix, iy, iz)).isAir())
                            level.setBlock(pos.offset(ix, iy, iz), getGas().defaultBlockState(), 3);
        }
    }
}
