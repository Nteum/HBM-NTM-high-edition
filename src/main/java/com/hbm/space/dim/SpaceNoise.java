package com.hbm.space.dim;

import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseRouterData;

/**
 * 航空版和噪音相关的内容放到这里
 * <br>
 * 旧HBM相关类说明：<br>
 * - PerlinNoiseSampler 基本复制的高版本 ImprovedNoise ，直接替代<br>
 * - SimplexNoiseSampler 基本复制自高版本 SimplexNoise ，直接替代<br>
 * - OctavePerlinNoiseSampler 基本复制自高版本 PerlinNoise ，直接替代，但pUseNewFactory == false<br>
 * - DoublePerlinNoiseSampler 基本复制自 NormalNoise ，直接替代<br>
 */
public class SpaceNoise {
    /**
     * 对应 NoiseCaveGenerator 生成的内容：
     *<br> - pillarNoise - Noises.PILLAR
     *<br> - pillarFalloffNoise - Noises.PILLAR_RARENESS
     *<br> - pillarScaleNoise - Noises.PILLAR_THICKNESS
     *<br> - caveNoise - Noises.SPAGHETTI_2D
     *<br> - horizontalCaveNoise - Noises.SPAGHETTI_2D_ELEVATION
     *<br> - caveScaleNoise - Noises.SPAGHETTI_2D_MODULATOR
     *<br> - caveFalloffNoise - Noises.SPAGHETTI_2D_THICKNESS
     *<br> - tunnelNoise1 - Noises.SPAGHETTI_3D_1
     *<br> - tunnelNoise2 - Noises.SPAGHETTI_3D_2
     *<br> - tunnelScaleNoise - Noises.SPAGHETTI_3D_RARITY
     *<br> - tunnelFalloffNoise - Noises.SPAGHETTI_3D_THICKNESS
     *<br> - offsetNoise - Noises.SPAGHETTI_ROUGHNESS
     *<br> - offsetScaleNoise - Noises.SPAGHETTI_ROUGHNESS_MODULATOR
     *<br> - terrainAdditionNoise - Noises.CAVE_LAYER
     *<br> - caveDensityNoise - Noises.CAVE_CHEESE
     */
    public static DensityFunction cave(){
        return null;
    }
}
