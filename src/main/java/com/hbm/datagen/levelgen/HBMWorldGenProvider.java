package com.hbm.datagen.levelgen;

import com.hbm.HBM;
import com.hbm.space.dim.moon.WorldGenMun;
import com.hbm.space.dim.orbit.Space;
import com.hbm.world.feature.HBMConfigFeatures;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class HBMWorldGenProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            // 1. 地物配置
            .add(Registries.CONFIGURED_FEATURE, HBMConfigFeatures::bootstrapCF)
            // 2. 地物放置
            .add(Registries.PLACED_FEATURE, HBMConfigFeatures::bootstrapPF)
            // 3. 生物群系
            .add(Registries.BIOME, HBMWorldGenProvider::bootstrapBiomes)
            // 4. 维度类型
            .add(Registries.DIMENSION_TYPE, HBMWorldGenProvider::bootstrapType)
            // 5. 噪声设置 (Surface Rules 绑定在这里)
            .add(Registries.NOISE_SETTINGS, HBMWorldGenProvider::bootstrapNoise)
            // 6. 注册维度实例
            .add(Registries.LEVEL_STEM, HBMWorldGenProvider::bootstrapDimension)
            ;
    public HBMWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(HBM.MODID));
    }
    private static void bootstrapBiomes(BootstapContext<Biome> context) {
        WorldGenMun.genBiomes(context);
        Space.genBiomes(context);
    }
    // --- 步骤 1: 定义维度属性 ---
    private static void bootstrapType(BootstapContext<DimensionType> context) {
        WorldGenMun.genDimensionType(context);
        Space.genDimensionType(context);
    }

    // --- 步骤 2: 定义地形和表面规则 ---
    private static void bootstrapNoise(BootstapContext<NoiseGeneratorSettings> context) {
        WorldGenMun.genNoiseSetting(context);
        Space.genNoiseSetting(context);
    }

    // --- 步骤 3: 组合维度 ---
    private static void bootstrapDimension(BootstapContext<LevelStem> context) {
        WorldGenMun.genDimension(context);
        Space.genDimension(context);
    }
}
