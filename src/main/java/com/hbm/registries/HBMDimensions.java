package com.hbm.registries;

import com.hbm.HBM;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class HBMDimensions {
    public static final ResourceKey<Level> MOON_KEY = ResourceKey.create(Registries.DIMENSION, HBM.rl("hbm_moon"));
    // 维度的 Key
    public static final ResourceKey<LevelStem> MOON_LEVEL_KEY = ResourceKey.create(Registries.LEVEL_STEM, HBM.rl("hbm_moon"));
    // 维度类型的 Key
    public static final ResourceKey<DimensionType> MOON_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, HBM.rl("hbm_moon_type"));
    // 噪声设置的 Key
    public static final ResourceKey<NoiseGeneratorSettings> MOON_NOISE_SETTINGS = ResourceKey.create(Registries.NOISE_SETTINGS, HBM.rl("moon_settings"));
}
