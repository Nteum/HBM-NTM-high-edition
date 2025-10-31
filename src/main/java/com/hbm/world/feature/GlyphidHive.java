package com.hbm.world.feature;

import com.hbm.block.HBMBlockProperties;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModTags;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.EndGatewayConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.Random;

public class GlyphidHive extends Feature<NoneFeatureConfiguration> {
    // 异虫巢平面图，未来应当用nbt文件代替
    public static final int[][][] schematicSmall = new int[][][] {
            {
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,1,1,1,0,0,0,0},
                    {0,0,0,0,1,1,1,0,0,0,0},
                    {0,0,0,0,1,1,1,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0},
            },
            {
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,1,1,1,0,0,0,0},
                    {0,0,0,1,1,1,1,1,0,0,0},
                    {0,0,1,1,1,1,1,1,1,0,0},
                    {0,0,1,1,1,1,1,1,1,0,0},
                    {0,0,1,1,1,1,1,1,1,0,0},
                    {0,0,0,1,1,1,1,1,0,0,0},
                    {0,0,0,0,1,1,1,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0},
            },
            {
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,1,1,1,0,0,0,0},
                    {0,0,1,1,1,1,1,1,1,0,0},
                    {0,0,1,1,1,1,1,1,1,0,0},
                    {0,1,1,1,3,3,3,1,1,1,0},
                    {0,1,1,1,3,3,3,1,1,1,0},
                    {0,1,1,1,3,3,3,1,1,1,0},
                    {0,0,1,1,1,1,1,1,1,0,0},
                    {0,0,1,1,1,1,1,1,1,0,0},
                    {0,0,0,0,1,1,1,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0},
            },
            {
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,1,1,1,0,0,0,0},
                    {0,0,1,1,1,1,1,1,1,0,0},
                    {0,0,1,1,2,2,2,1,1,0,0},
                    {0,1,1,2,2,2,2,2,1,1,0},
                    {0,1,1,2,2,2,2,2,1,1,0},
                    {0,1,1,2,2,2,2,2,1,1,0},
                    {0,0,1,1,2,2,2,1,1,0,0},
                    {0,0,1,1,1,1,1,1,1,0,0},
                    {0,0,0,0,1,1,1,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0},
            },
            {
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,1,1,1,0,0,0,0},
                    {0,0,1,1,1,1,1,1,1,0,0},
                    {0,0,1,1,1,1,1,1,1,0,0},
                    {0,1,1,1,1,1,1,1,1,1,0},
                    {0,1,1,1,1,1,1,1,1,1,0},
                    {0,1,1,1,1,1,1,1,1,1,0},
                    {0,0,1,1,1,1,1,1,1,0,0},
                    {0,0,1,1,1,1,1,1,1,0,0},
                    {0,0,0,0,1,1,1,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0},
            }
    };

    public GlyphidHive(Codec<NoneFeatureConfiguration> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> pContext) {
//        boolean loot = pContext.config().loot;
        BlockPos blockPos = pContext.origin();
        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();
        WorldGenLevel level = pContext.level();
        RandomSource rand = pContext.random();
        boolean infected = rand.nextInt(10) == 0;

        int overrideMeta = infected ? 1 : 0;

        for(int i = 0; i < 11; i++) {
            for(int j = 0; j < 5; j++) {
                for(int k = 0; k < 11; k++) {

                    int block = schematicSmall[4 - j][i][k];
                    int iX = x + i - 5;
                    int iY = y + j - 2;
                    int iZ = z + k - 5;
                    BlockPos pos = new BlockPos(x + i - 5, y + j - 2, z + k - 5);

                    switch(block) {
                        case 1: level.setBlock(pos, ModBlocks.GLYPHID_BLOCK.get().defaultBlockState().setValue(HBMBlockProperties.VARIANT3, overrideMeta), 2); break;
                        case 2: level.setBlock(pos, rand.nextInt(3) == 0 ? ModBlocks.GLYPHID_SPAWNER.get().defaultBlockState().setValue(HBMBlockProperties.VARIANT3, overrideMeta) :
                            ModBlocks.GLYPHID_BLOCK.get().defaultBlockState().setValue(HBMBlockProperties.VARIANT3, overrideMeta), 2); break;
                        case 3:
                            int r = rand.nextInt(3);
                            if(r == 0) {
                                BlockState state = Blocks.SKELETON_SKULL.defaultBlockState();
                                level.setBlock(pos, state.setValue(SkullBlock.ROTATION, rand.nextInt(16)), 1, 3);
                            }
//                            else if(r == 1) {
//                                level.setBlock(iX, iY, z + k - 5, ModBlocks.deco_loot, 0, 2);
//                                LootGenerator.lootBones(level, iX, iY, iZ);
//                            } else if(r == 2) {
//                                if(loot) {
//                                    level.setBlock(iX, iY, iZ, ModBlocks.deco_loot, 0, 2);
//                                    LootGenerator.lootGlyphidHive(level, iX, iY, iZ);
//                                } else {
//                                    level.setBlock(pos, ModBlocks.GLYPHID_BLOCK.get().defaultBlockState(), 2);
//                                }
//                            }
                            break;
                    }
                }
            }
        }

        return true;
    }

    // 参数infected暂时不需要了
    public static class GlyphidHiveFeatureConfiguration implements FeatureConfiguration {
        public static final Codec<GlyphidHiveFeatureConfiguration> CODEC = RecordCodecBuilder.create((p_67649_) -> p_67649_.group(
//                Codec.BOOL.fieldOf("infected").forGetter((p_160810_) -> p_160810_.infected),
                Codec.BOOL.fieldOf("exact").forGetter((p_160808_) -> p_160808_.loot))
                .apply(p_67649_, GlyphidHiveFeatureConfiguration::new));
//        public boolean infected;
        public boolean loot;
        public GlyphidHiveFeatureConfiguration(boolean loot){
//            this.infected = infected;
            this.loot = loot;
        }
    }
}
