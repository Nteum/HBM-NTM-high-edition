package com.hbm.registries;

import com.hbm.HBM;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HBMDimensions {
    public static final Set<ResourceKey<Level>> LEVELS = new HashSet<>(List.of(Level.OVERWORLD, Level.NETHER, Level.END));
//    public static final ResourceKey<Level> MOON_KEY = register(ResourceKey.create(Registries.DIMENSION, HBM.rl("mun")));
//    // 维度的 Key
//    public static final ResourceKey<LevelStem> MOON_LEVEL_KEY = ResourceKey.create(Registries.LEVEL_STEM, HBM.rl("hbm_moon"));
//    // 维度类型的 Key
//    public static final ResourceKey<DimensionType> MOON_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, HBM.rl("hbm_moon_type"));
//    // 噪声设置的 Key
//    public static final ResourceKey<NoiseGeneratorSettings> MOON_NOISE_SETTINGS = ResourceKey.create(Registries.NOISE_SETTINGS, HBM.rl("moon_settings"));

    public static final ResourceKey<Level> KERBIN = register(ResourceKey.create(Registries.DIMENSION, HBM.rl("kerbin")));
    public static final ResourceKey<Level> MINMUS = register(ResourceKey.create(Registries.DIMENSION, HBM.rl("minmus")));
    public static final ResourceKey<Level> DUNA = register(ResourceKey.create(Registries.DIMENSION, HBM.rl("duna")));
    public static final ResourceKey<Level> MOHO = register(ResourceKey.create(Registries.DIMENSION, HBM.rl("moho")));
    public static final ResourceKey<Level> DRES = register(ResourceKey.create(Registries.DIMENSION, HBM.rl("dres")));
    public static final ResourceKey<Level> EVE = register(ResourceKey.create(Registries.DIMENSION, HBM.rl("eve")));
    public static final ResourceKey<Level> IKE = register(ResourceKey.create(Registries.DIMENSION, HBM.rl("ike")));
    public static final ResourceKey<Level> LAYTHE = register(ResourceKey.create(Registries.DIMENSION, HBM.rl("laythe")));
    public static final ResourceKey<Level> TEKTO = register(ResourceKey.create(Registries.DIMENSION, HBM.rl("tekto")));
    
    private static ResourceKey<Level> register(ResourceKey<Level> resourceKey){
        LEVELS.add(resourceKey);
        return resourceKey;
    }
}
