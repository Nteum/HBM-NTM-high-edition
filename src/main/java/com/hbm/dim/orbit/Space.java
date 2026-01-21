package com.hbm.dim.orbit;

import com.hbm.HBM;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;
import java.util.OptionalLong;
import java.util.Set;

/**
 * 太空维度
 * */
public class Space {
    public static final ResourceKey<Level> LEVEL_KEY = ResourceKey.create(Registries.DIMENSION, HBM.rl("hbm_space"));
    public static final ResourceKey<LevelStem> SPACE = ResourceKey.create(Registries.LEVEL_STEM, HBM.rl("hbm_space"));
    public static final ResourceKey<Biome> SPACE_BIOME = ResourceKey.create(Registries.BIOME, HBM.rl("hbm_space_biome"));
    public static final ResourceKey<DimensionType> SPACE_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, HBM.rl("hbm_space_type"));
    public static final ResourceKey<NoiseGeneratorSettings> SPACE_NOISE_SETTINGS = ResourceKey.create(Registries.NOISE_SETTINGS, HBM.rl("space_settings"));
    public static Set<CelestialBody> CELESTIAL_BODIES;
    public static void genBiomes(BootstapContext<Biome> context){
        HolderGetter<PlacedFeature> featureHolder = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> carverHolder = context.lookup(Registries.CONFIGURED_CARVER);
        context.register(SPACE_BIOME, new Biome.BiomeBuilder()
                .hasPrecipitation(false).temperature(0.5f).downfall(0.9f)
                .specialEffects(new BiomeSpecialEffects.Builder().skyColor(0).fogColor(0).waterColor(0).waterFogColor(0).build())
                .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                .generationSettings(new BiomeGenerationSettings.Builder(featureHolder,carverHolder).build())
                .build()
        );
    }

    public static void genDimensionType(BootstapContext<DimensionType> context){

        context.register(SPACE_TYPE, new DimensionType(
                OptionalLong.of(18000), // 固定时间
                true,  // 是否有天光 (月球可以设为 false 如果你想要黑暗天空)
                false, // 是否有天顶 (类似地狱)
                false, // 是否极热 (床爆炸)
                false,  // 是否天然 (指南针转动)
                1.0D,  // 坐标比例
                true,  // 是否支持床
                true, // 是否支持重生锚
                -64,   // 最小 Y
                384,   // 高度
                384,   // 逻辑高度
                BlockTags.INFINIBURN_OVERWORLD,
                HBM.rl("space_effects"), // 渲染效果
                0,  // 环境光照
                new DimensionType.MonsterSettings(false, false, ConstantInt.of(0), 0)
        ));
    }

    public static void genNoiseSetting(BootstapContext<NoiseGeneratorSettings> context){
        // 我们将 finalDensity 设为恒定的负数，这样就不会有任何方块产生
        NoiseRouter router = new NoiseRouter(
                DensityFunctions.zero(), // barrier
                DensityFunctions.zero(), // fluidLevelFloodedness
                DensityFunctions.zero(), // fluidLevelSpread
                DensityFunctions.zero(), // lava
                DensityFunctions.zero(), // veinRidge
                DensityFunctions.zero(), // veinGap
                DensityFunctions.zero(), // veinThick
                DensityFunctions.constant(-1.0), // finalDensity: 关键！负数代表没有物质
                DensityFunctions.constant(-1.0), // initialDensityWithoutJaggedness
                DensityFunctions.zero(), // depth
                DensityFunctions.zero(), // ridges
                DensityFunctions.zero(), // erosion
                DensityFunctions.zero(), // temperature
                DensityFunctions.zero(), // humidity
                DensityFunctions.zero()  // continents
        );

        context.register(SPACE_NOISE_SETTINGS, new NoiseGeneratorSettings(
                NoiseSettings.create(-64, 384, 1, 2),
                Blocks.AIR.defaultBlockState(),
                Blocks.AIR.defaultBlockState(),
                router,
                SurfaceRules.state(Blocks.AIR.defaultBlockState()),
                List.of(),
                -64,
                true,
                false,
                false,
                false
        ));
    }

    public static void genDimension(BootstapContext<LevelStem> context){
        HolderGetter<DimensionType> types = context.lookup(Registries.DIMENSION_TYPE);
        HolderGetter<NoiseGeneratorSettings> settings = context.lookup(Registries.NOISE_SETTINGS);
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);

        var biomeSource = MultiNoiseBiomeSource.createFromList(
                new Climate.ParameterList<>(List.of(
                        Pair.of(Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), biomes.getOrThrow(SPACE_BIOME))
                ))
        );

        context.register(SPACE, new LevelStem(
                types.getOrThrow(SPACE_TYPE),
                new NoiseBasedChunkGenerator(biomeSource, settings.getOrThrow(SPACE_NOISE_SETTINGS))
        ));
    }


}
