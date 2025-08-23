package com.hbm.explosion;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ExplosionChaos {
    //frag：手雷破片
    //这里用产生火箭代替
    public static void frag(Level level, int x, int y, int z, int count, boolean flame, Entity shooter) {
        double d1 = 0;
        double d2 = 0;
        double d3 = 0;
        Arrow fragment;
        RandomSource rand = level.getRandom();

        for (int i = 0; i < count; i++) {
            d1 = Math.min(rand.nextDouble(),0.5);
            d2 = Math.min(rand.nextDouble(),0.5);
            d3 = Math.min(rand.nextDouble(),0.5);

            if (rand.nextInt(2) == 0) {
                d1 *= -1;
            }

            if (rand.nextInt(2) == 0) {
                d3 *= -1;
            }

            fragment = new Arrow(level, x, y, z);

            fragment.setDeltaMovement(d1,d2,d3);

            fragment.setOwner(shooter);

//            fragment.setIsCritical(true);
            fragment.setCritArrow(true);
            if (flame) {
                fragment.setSecondsOnFire(1000);
            }

            fragment.setBaseDamage(2.5);

            level.addFreshEntity(fragment);
        }
    }
    /**
     * 点燃球形范围内的可燃物
     */
    public static void flameDeath(Level level, int x, int y, int z, int bound) {

        int r = bound;
        int r2 = r * r;
        int r22 = r2 / 2;
        for (int xx = -r; xx < r; xx++) {
            int X = xx + x;
            int XX = xx * xx;
            for (int yy = -r; yy < r; yy++) {
                int Y = yy + y;
                int YY = XX + yy * yy;
                for (int zz = -r; zz < r; zz++) {
                    int Z = zz + z;
                    int ZZ = YY + zz * zz;
                    if (ZZ < r22) {
                        BlockPos pos = new BlockPos(X,Y,Z);
                        if (level.getBlockState(pos).isFlammable(level, pos, Direction.UP)
                                && level.getBlockState(pos.above()).isAir()) {
                            BlockState blockstate1 = BaseFireBlock.getState(level, pos.above());
                            level.setBlock(pos, blockstate1, 11);
//                            level.setBlock(pos.above(),Blocks.FIRE.defaultBlockState(),512);
                        }
                    }
                }
            }
        }

    }

    /**
     * 点燃球形范围
     */
    public static void burn(Level level, int x, int y, int z, int bound) {

        int r = bound;
        int r2 = r * r;
        int r22 = r2 / 2;
        for (int xx = -r; xx < r; xx++) {
            int X = xx + x;
            int XX = xx * xx;
            for (int yy = -r; yy < r; yy++) {
                int Y = yy + y;
                int YY = XX + yy * yy;
                for (int zz = -r; zz < r; zz++) {
                    int Z = zz + z;
                    int ZZ = YY + zz * zz;
                    if (ZZ < r22) {
                        BlockPos pos = new BlockPos(X,Y,Z).above();
                        if (level.getBlockState(pos).isAir() || level.getBlockState(pos).is(Blocks.SNOW)) {
                            BlockState blockstate1 = BaseFireBlock.getState(level, pos);
                            level.setBlock(pos, blockstate1, 11);
                        }
                    }
                }
            }
        }

    }
}
