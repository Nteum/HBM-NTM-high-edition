package com.hbm.dim.moon;

import com.hbm.dim.SpaceSurfaceRules;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.RegistryHelper;
import com.hbm.world.feature.HBMConfigFeatures;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
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

import java.util.List;
import java.util.OptionalLong;

/**
* 关于和低版本的对应，
 */
public class WorldGenMun {
    public static final String id = "mun";
    public static final ResourceKey<LevelStem> KEY_STEM = ResourceKey.create(Registries.LEVEL_STEM, RegistryHelper.rl(id));
    public static final ResourceKey<DimensionType> KEY_DIMENSION_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, RegistryHelper.rl(id + "_dim_type"));
    public static final ResourceKey<NoiseGeneratorSettings> KEY_NOISE = ResourceKey.create(Registries.NOISE_SETTINGS, RegistryHelper.rl(id + "_noise"));
    // 生物群系的key需要在bootstrap biomes和bootstrap level stem中都注册，否则会报错。
    public static final ResourceKey<Biome> MOON_FLAT = ResourceKey.create(Registries.BIOME, RegistryHelper.rl("mun_flat"));
    public static final ResourceKey<Biome> MOON_HEIGHTLAND = ResourceKey.create(Registries.BIOME, RegistryHelper.rl("mun_heightland"));

    public static void genBiomes(BootstapContext<Biome> context){
        HolderGetter<PlacedFeature> featureHolder = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> carverHolder = context.lookup(Registries.CONFIGURED_CARVER);
        context.register(MOON_FLAT, new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(-1f)
                .downfall(0.9f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .fogColor(12638463).waterColor(4159204).waterFogColor(329011).skyColor(0).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build())
                .mobSpawnSettings(new MobSpawnSettings.Builder().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.COW, 4, 2, 10)).build())
                .generationSettings(new BiomeGenerationSettings.Builder(featureHolder,carverHolder)
                        .addCarver(GenerationStep.Carving.AIR, Carvers.CAVE)
                        .addCarver(GenerationStep.Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND)
                        .addFeature(GenerationStep.Decoration.LAKES, HBMConfigFeatures.METE_CREATOR_MOON.placedKey())
                        .build())
                .build()
        );
        context.register(MOON_HEIGHTLAND, new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(-1f)
                .downfall(0.9f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .fogColor(12638463).waterColor(4159204).waterFogColor(329011).skyColor(0).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).build())
                .mobSpawnSettings(new MobSpawnSettings.Builder().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.COW, 4, 2, 10)).build())
                .generationSettings(new BiomeGenerationSettings.Builder(featureHolder,carverHolder)
                        .addCarver(GenerationStep.Carving.AIR, Carvers.CAVE)
                        .addCarver(GenerationStep.Carving.AIR, Carvers.CAVE_EXTRA_UNDERGROUND)
                        .addFeature(GenerationStep.Decoration.LAKES, HBMConfigFeatures.METE_CREATOR_MOON.placedKey())
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
        HolderGetter<DensityFunction> densityFunctions = context.lookup(Registries.DENSITY_FUNCTION);

        // 注册月球专属设置
        context.register(KEY_NOISE, new NoiseGeneratorSettings(
                NoiseSettings.create(-64, 384, 1, 2),
                ModBlocks.moon_rock.get().defaultBlockState(), // 默认方块换成月岩
                Blocks.AIR.defaultBlockState(),               // 默认液体换成空气
                NoiseRouterData.overworld(densityFunctions, context.lookup(Registries.NOISE), false, false),
                SpaceSurfaceRules.moonSurfaceRules(),                  // 绑定你写的月球表面规则
                List.of(),
                -1,
                false, // 禁用怪物自然生成
                false,  // 启用含水层 (如果你想有地下岩浆或流体)
                false, // 禁用主世界矿脉 (用你自己的)
                false
        ));
    }

    public static void genDimension(BootstapContext<LevelStem> context){
        HolderGetter<DimensionType> types = context.lookup(Registries.DIMENSION_TYPE);
        HolderGetter<NoiseGeneratorSettings> settings = context.lookup(Registries.NOISE_SETTINGS);
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);

        // 使用 Multi-Noise 放置你的月球群系
        var biomeSource = MultiNoiseBiomeSource.createFromList(
                new Climate.ParameterList<>(List.of(
                        Pair.of(Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), biomes.getOrThrow(MOON_FLAT)),
                        Pair.of(Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), biomes.getOrThrow(MOON_HEIGHTLAND))
                ))
        );

        context.register(KEY_STEM, new LevelStem(
                types.getOrThrow(KEY_DIMENSION_TYPE),
                new NoiseBasedChunkGenerator(biomeSource, settings.getOrThrow(KEY_NOISE))
        ));
    }
}
