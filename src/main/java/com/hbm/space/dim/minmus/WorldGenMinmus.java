package com.hbm.space.dim.minmus;

import com.hbm.registries.RegistryHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class WorldGenMinmus {
    public static final String id = "minmus";
    public static final ResourceKey<Level> KEY_LEVEL = ResourceKey.create(Registries.DIMENSION, RegistryHelper.rl(id));
    public static final ResourceKey<LevelStem> KEY_STEM = ResourceKey.create(Registries.LEVEL_STEM, RegistryHelper.rl(id));
    public static final ResourceKey<DimensionType> KEY_DIMENSION_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, RegistryHelper.rl(id + "_dim_type"));
    public static final ResourceKey<NoiseGeneratorSettings> KEY_NOISE = ResourceKey.create(Registries.NOISE_SETTINGS, RegistryHelper.rl(id + "_noise"));
}
