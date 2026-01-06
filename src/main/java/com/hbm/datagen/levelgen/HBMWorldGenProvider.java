package com.hbm.datagen.levelgen;

import com.hbm.HBM;
import com.hbm.registries.HBMBiomes;
import com.hbm.registries.HBMDimensions;
import com.hbm.registries.ModBlocks;
import com.hbm.world.biome.HBMSurfaceRules;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.*;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.List;
import java.util.OptionalLong;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class HBMWorldGenProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            // 1. 注册维度类型
            .add(Registries.DIMENSION_TYPE, HBMWorldGenProvider::bootstrapType)
            // 2. 注册噪声设置 (Surface Rules 绑定在这里)
            .add(Registries.NOISE_SETTINGS, HBMWorldGenProvider::bootstrapNoise)
            // 3. 注册维度实例
//            .add(Registries.LEVEL_STEM, HBMWorldGenProvider::bootstrapDimension)
            ;
    public HBMWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(HBM.MODID));
    }
    // --- 步骤 1: 定义维度属性 ---
    private static void bootstrapType(BootstapContext<DimensionType> context) {
        context.register(HBMDimensions.MOON_TYPE, new DimensionType(
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
                new DimensionType.MonsterSettings(false, false, ConstantInt.of(0), 0)
        ));
    }

    // --- 步骤 2: 定义地形和表面规则 ---
    private static void bootstrapNoise(BootstapContext<NoiseGeneratorSettings> context) {
        // 获取主世界的默认设置作为模板
        HolderGetter<NoiseGeneratorSettings> settings = context.lookup(Registries.NOISE_SETTINGS);
//        NoiseGeneratorSettings overworld = settings.getOrThrow(NoiseGeneratorSettings.OVERWORLD).value();
        Holder.Reference<NoiseGeneratorSettings> overworld = settings.getOrThrow(NoiseGeneratorSettings.OVERWORLD);
        HolderGetter<DensityFunction> densityFunctions = context.lookup(Registries.DENSITY_FUNCTION);

        // 注册月球专属设置
        context.register(HBMDimensions.MOON_NOISE_SETTINGS, new NoiseGeneratorSettings(
                NoiseSettings.create(-64, 384, 1, 2),
                ModBlocks.moon_rock.get().defaultBlockState(), // 默认方块换成月岩
                Blocks.AIR.defaultBlockState(),               // 默认液体换成空气
                NoiseRouterData.overworld(densityFunctions, context.lookup(Registries.NOISE), false, false),
                HBMSurfaceRules.moonRules(),                  // 绑定你写的月球表面规则
                List.of(),
                -64,   // 月球没有海，海平面设为最底层
                false, // 禁用怪物自然生成
                true,  // 启用含水层 (如果你想有地下岩浆或流体)
                false, // 禁用主世界矿脉 (用你自己的)
                false
        ));
    }

    // --- 步骤 3: 组合维度 ---
    private static void bootstrapDimension(BootstapContext<LevelStem> context) {
        HolderGetter<DimensionType> types = context.lookup(Registries.DIMENSION_TYPE);
        HolderGetter<NoiseGeneratorSettings> settings = context.lookup(Registries.NOISE_SETTINGS);
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);

        // 使用 Multi-Noise 放置你的月球群系
        var biomeSource = MultiNoiseBiomeSource.createFromList(
                new Climate.ParameterList<>(List.of(
                        Pair.of(Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F),
                                biomes.getOrThrow(HBMBiomes.MUN))
                ))
        );

        context.register(HBMDimensions.MOON_LEVEL_KEY, new LevelStem(
                types.getOrThrow(HBMDimensions.MOON_TYPE),
                new NoiseBasedChunkGenerator(biomeSource, settings.getOrThrow(HBMDimensions.MOON_NOISE_SETTINGS))
        ));
    }
}
