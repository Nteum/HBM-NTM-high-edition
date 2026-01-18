package com.hbm.world.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/**
 * 陨石坑
 * */
public class MeteCreator extends Feature<MeteCreator.CreatorConfiguration> {
    public MeteCreator(Codec<CreatorConfiguration> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<CreatorConfiguration> pContext) {
        BlockPos blockpos = pContext.origin();
        WorldGenLevel worldgenlevel = pContext.level();
        RandomSource rand = pContext.random();
        CreatorConfiguration configuration = pContext.config();

        int height = worldgenlevel.getHeight(Heightmap.Types.WORLD_SURFACE_WG, blockpos.getX(), blockpos.getZ());
        int radius = rand.nextInt(configuration.maxSize - configuration.minSize) + configuration.minSize;
        double depth = radius * 0.35D;

        blockpos = new BlockPos(blockpos.getX(), height, blockpos.getZ());
        if (height - depth <= worldgenlevel.getMinBuildHeight() + 4 || worldgenlevel.getBlockState(blockpos.below((int) depth)).canBeReplaced()) return false;

        int y,dep,dep2,dep3;
        BlockPos tempPos;
        for (int i = -radius; i < radius; i++) {
            for (int i1 = -radius; i1 < radius; i1++) {
                y = height;
                float r = Mth.sqrt(i * i + i1 * i1);

                if (r - rand.nextInt(3) < radius){
                    dep = (int) Mth.clamp(depthFunc(r, radius, depth), 0, y-1);
                    // 清空陨石坑内部和上方一定距离的方块
                    for (int i2 = -radius; i2 < dep; i2++) {
                        worldgenlevel.setBlock(blockpos.offset(i, -i2, i1), Blocks.AIR.defaultBlockState(), 2);
                    }
                    dep2 = Math.min(3, y - 1);
                    // 判断陨石坑下面是否有跳空，较小的跳空找到它对应的深度进行岩石覆盖，更深的跳空不需要覆盖
                    boolean flag = false;
                    dep3 = 0;
                    for (int i2 = 0; i2 < 5; i2++) {
                        if (!worldgenlevel.getBlockState(blockpos.offset(i, -dep-i2, i1)).canBeReplaced()){
                            flag = true;
                            dep3 = i2;
                            break;
                        }
                    }
                    // 填充陨石坑底的方块
                    if (flag) {
                        if(r + rand.nextInt(3) <= radius / 3D) {
                            for(int j = 0; j < dep2; j++) {
                                worldgenlevel.setBlock(blockpos.offset(i, -dep-dep3-j, i1), configuration.coreTarget, 2);
                            }
                        } else {
                            for(int j = 0; j < dep2; j++) {
                                worldgenlevel.setBlock(blockpos.offset(i, -dep-dep3-j, i1), configuration.target, 2);
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    private double depthFunc(double x, double rad, double depth) {
        return -Math.pow(x, 4) / Math.pow(rad, 4) * depth + depth;
    }

    public static class CreatorConfiguration implements FeatureConfiguration{
        public static final Codec<CreatorConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    BlockState.CODEC.fieldOf("target").forGetter(o -> o.target),
                    BlockState.CODEC.fieldOf("core_target").forGetter(o -> o.coreTarget),
                    Codec.intRange(4, 32).fieldOf("min_size").forGetter(o -> o.minSize),
                    Codec.intRange(4, 32).fieldOf("max_size").forGetter(o -> o.maxSize)
                ).apply(instance, CreatorConfiguration::new)
        );
        public int minSize;
        public int maxSize;
        public BlockState target;
        public BlockState coreTarget;
        public CreatorConfiguration(BlockState target, BlockState coreTarget, int minSize, int maxSize){
            this.target = target;
            this.coreTarget = coreTarget;
            this.minSize = minSize;
            this.maxSize = maxSize;
        }

    }
}
