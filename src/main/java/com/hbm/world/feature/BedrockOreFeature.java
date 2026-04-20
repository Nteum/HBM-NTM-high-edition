package com.hbm.world.feature;

import com.hbm.block.env.BedRockOre;
import com.hbm.registries.ModBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** 自然生成基岩矿的地物 */
public class BedrockOreFeature extends Feature<NoneFeatureConfiguration> {
    public static final int LOWEST_Y_OVERWORLD = -64;
    public static final int HIGHEST_BEDROCK_Y_OVERWORLD = -59;
    public static final int MAX_NUM = 4;

    public BedrockOreFeature(Codec<NoneFeatureConfiguration> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos blockPos = context.origin().atY(LOWEST_Y_OVERWORLD);
        RandomSource random = context.random();

        int num = random.nextInt(1, MAX_NUM);
        //生成基岩矿
        for (int i = 0; i < num; i++) {
            BlockPos bedrock_pos = blockPos.offset(random.nextInt(2), 0, random.nextInt(2));
            BlockState blockState = ModBlocks.BEDROCK_ORE.get().defaultBlockState();
            blockState = blockState.setValue(BedRockOre.TYPE, BedRockOre.BedRockOreType.IRON);
            level.setBlock(bedrock_pos,blockState,11);
        }
        //生成深层石头覆层
        for (int i = LOWEST_Y_OVERWORLD+1; i <= HIGHEST_BEDROCK_Y_OVERWORLD; i++) {
            for (int j = blockPos.getX() - 3; j < blockPos.getX() + 4; j++) {
                for (int k = blockPos.getZ() - 3; k < blockPos.getZ() + 4; k++) {
                    if (blockPos.distSqr(new Vec3i(j,i,k)) < 4){
                        BlockPos cover_pos = new BlockPos(j, i, k);
                        this.setBlock(level,cover_pos,ModBlocks.DEPTH_STONE.get().defaultBlockState());
                    }
                }
            }
        }

        return true;
    }
}
