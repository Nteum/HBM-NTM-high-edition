package com.hbm.utils.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class NoiseHelper {
    public static long getDimensionBoundSeed(ServerLevel level, BlockPos pos) {
        ChunkGenerator generator = level.getChunkSource().getGenerator();

        // 💡 1. 拿到当前维度特有的 BiomeSource 哈希值。
        // 不同的维度（主世界、下界、末地、自定义维度）其内部的 Biome 组合和序列完全不同，
        // 这个 hashCode 是天然且绝对固定的“维度特化盐（Salt）”。
        int dimensionSalt = generator.getBiomeSource().hashCode();

        // 2. 将坐标混合进去进行高位和低位混淆（借鉴了原版区块随机数的经典混淆算法）
        long l = (long)pos.getX() * 3129871L ^ (long)pos.getZ() * 116129781L ^ (long)pos.getY();
        l = l * l * 42317861L + l * 11L;

        // 3. 混合维度特化盐，确保同一个坐标在主世界和下界算出来的结果截然不同
        return l ^ dimensionSalt;
    }
}
