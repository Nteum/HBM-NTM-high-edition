package com.hbm.space.dim.ike;

import com.hbm.registries.ModBlocks;
import com.hbm.registries.RegistryHelper;
import com.hbm.space.dim.SpaceNoise;
import com.hbm.space.dim.SpaceSurfaceRules;
import com.hbm.world.feature.HBMConfigFeatures;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.NoiseData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.CubicSpline;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.List;
import java.util.OptionalLong;

public class WorldGenIke {
    public static final String id = "ike";
    public static final ResourceKey<LevelStem> KEY_STEM = ResourceKey.create(Registries.LEVEL_STEM, RegistryHelper.rl(id));
    public static final ResourceKey<DimensionType> KEY_DIMENSION_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, RegistryHelper.rl(id + "_dim_type"));
    public static final ResourceKey<NoiseGeneratorSettings> KEY_NOISE = ResourceKey.create(Registries.NOISE_SETTINGS, RegistryHelper.rl(id + "_noise"));
    // 生物群系的key需要在bootstrap biomes和bootstrap level stem中都注册，否则会报错。
    public static final ResourceKey<Biome> IKE_FLAT = ResourceKey.create(Registries.BIOME, RegistryHelper.rl("ike_flat"));

    public static void genBiomes(BootstapContext<Biome> context){
        HolderGetter<PlacedFeature> featureHolder = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> carverHolder = context.lookup(Registries.CONFIGURED_CARVER);
        context.register(IKE_FLAT, new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(-1f)
                .downfall(0.9f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .fogColor(12638463).waterColor(4159204).waterFogColor(329011).skyColor(0).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build())
                .mobSpawnSettings(new MobSpawnSettings.Builder().build())
                .generationSettings(new BiomeGenerationSettings.Builder(featureHolder,carverHolder)
                        .addCarver(GenerationStep.Carving.AIR, Carvers.CAVE)
                        .addCarver(GenerationStep.Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND)
                        .build())
                .build()
        );
    }

    public static void genDimensionType(BootstapContext<DimensionType> context){
        context.register(KEY_DIMENSION_TYPE, new DimensionType(
                OptionalLong.empty(), // 固定时间
                false,  // 是否有天光 (月球可以设为 false 如果你想要黑暗天空)
                false, // 是否有天顶 (类似地狱)
                false, // 是否极热 (床爆炸)
                true,  // 是否天然 (指南针转动)
                1.0D,  // 坐标比例
                true,  // 是否支持床
                false, // 是否支持重生锚
                -64,   // 最小 Y
                384,   // 高度
                384,   // 逻辑高度
                BlockTags.INFINIBURN_OVERWORLD,
                BuiltinDimensionTypes.END_EFFECTS, // 渲染效果
                0.1f,  // 环境光照
                new DimensionType.MonsterSettings(false, false, ConstantInt.of(7), 3)
        ));
    }

    public static void genNoiseSetting(BootstapContext<NoiseGeneratorSettings> context){
        // 获取主世界的默认设置作为模板
        HolderGetter<NoiseGeneratorSettings> settings = context.lookup(Registries.NOISE_SETTINGS);
        Holder.Reference<NoiseGeneratorSettings> overworld = settings.getOrThrow(NoiseGeneratorSettings.OVERWORLD);
        HolderGetter<DensityFunction> functions = context.lookup(Registries.DENSITY_FUNCTION);
        HolderGetter<NormalNoise.NoiseParameters> noises = context.lookup(Registries.NOISE);

        // 获取我们上面写好的陨石坑地形密度
        DensityFunction myMoonTerrain = SpaceNoise.createCraterTerrain(context);

        NoiseRouter router = new NoiseRouter(
                DensityFunctions.zero(), // barrierNoise
                DensityFunctions.zero(), // fluidLevelFloodedness
                DensityFunctions.zero(), // fluidLevelSpread
                DensityFunctions.zero(), // lava
                DensityFunctions.noise(noises.getOrThrow(Noises.TEMPERATURE)),
                DensityFunctions.noise(noises.getOrThrow(Noises.VEGETATION)),
                SpaceNoise.getFunction(functions, NoiseRouterData.CONTINENTS),
                SpaceNoise.getFunction(functions, NoiseRouterData.EROSION),
                SpaceNoise.getFunction(functions, NoiseRouterData.DEPTH),
                SpaceNoise.getFunction(functions, NoiseRouterData.RIDGES),
                // ---------------------------------------------------------------------
                // 关键部分：用我们自定义的地形同时赋值给 initialDensity 和 finalDensity
                myMoonTerrain, // initialDensityWithoutJaggedness
                myMoonTerrain, // finalDensity (真正决定渲染实体的密度场)
                // ---------------------------------------------------------------------
                DensityFunctions.zero(), // veinToggle (矿脉)
                DensityFunctions.zero(), // veinRidges
                DensityFunctions.zero()  // veinGap
        );

        NoiseGeneratorSettings generatorSettings = new NoiseGeneratorSettings(
                NoiseSettings.create(0, 384, 1, 2), // 构建标准高度区间
                Blocks.STONE.defaultBlockState(),     // 替换为你月球的基岩（如 Moon Rock）
                Blocks.AIR.defaultBlockState(),       // 默认没有流体海洋（填空气）
                router,
                SpaceSurfaceRules.ikeSurfaceRules(),        // 加上之前写的 SurfaceRules (地表替换 Moon Turf)
                List.of(),                           // Spawn targets
                64,                                  // 海平面高度（如果有液体的话）
                false,                               // disableMobGeneration
                true,                                // aquifersEnabled (水层/洞穴含水层)
                false,                               // oreVeinsEnabled
                false                                // useLegacyRandomSource
        );

        context.register(KEY_NOISE, generatorSettings);
    }

    public static void genDimension(BootstapContext<LevelStem> context){
        HolderGetter<DimensionType> types = context.lookup(Registries.DIMENSION_TYPE);
        HolderGetter<NoiseGeneratorSettings> settings = context.lookup(Registries.NOISE_SETTINGS);
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);

        // 群系的生成条件
        var biomeSource = MultiNoiseBiomeSource.createFromList(
                new Climate.ParameterList<>(List.of(
                        Pair.of(Climate.parameters(
                                Climate.Parameter.point(0),
                                Climate.Parameter.point(0),
                                Climate.Parameter.point(0),
                                Climate.Parameter.point(0),
                                Climate.Parameter.point(0),
                                Climate.Parameter.point(0),
                                0f
                        ), biomes.getOrThrow(IKE_FLAT))
                ))
        );

        context.register(KEY_STEM, new LevelStem(
                types.getOrThrow(KEY_DIMENSION_TYPE),
                new NoiseBasedChunkGenerator(biomeSource, settings.getOrThrow(KEY_NOISE))
        ));
    }


}
