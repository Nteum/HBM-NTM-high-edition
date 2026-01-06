package com.hbm.dim;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
/**
 * 区块生成的基本流程：
 * - ChunkGenerator#fillFromNoise - 根据噪声生成一个仅有石头和水的区块
 * - ChunkGenerator#buildSurface - 替换表面岩石等物品，表面规则 surface rule 会在这里应用
 * - ChunkGenerator#applyCarvers - 雕刻：生成洞穴和峡谷，确保它们的连通性
 * - ChunkGenerator#applyBiomeDecoration - 地物生成：生成湖泊、矿物、植物等（有默认实现）
 * 1.7.10 NTM Space 对应的 ChunkProviderCelestial 用于所有外星地形生成的父类，大量代码搬运自原版 ChunkProviderGenerate ，
 * 这要做主要是为了增加一些自定义的设定，其中大部分在高版本已经原生支持自定义：
 * - 陆地填充物的类型，默认为石头
 * - 海洋填充物类型，默认为水
 * - 噪声频率，一共四种噪声的参数，似乎是为了调整生成的地面平滑度
 * - replaceBlocksForBiome 函数中取消了对 forge 事件的支持，疑似是避免其他人的地形生成影响他的逻辑
 * - provideChunk 函数移除了关于洞穴生成以及村庄等主世界地表物生成的代码
 * - populate （用于生成地物）函数移除了村庄、岩浆、冰山等主世界地物生成的代码
 * */
public abstract class ChunkProviderCelestial extends ChunkGenerator {
//    public static final Codec<ChunkProviderCelestial> CODEC = RecordCodecBuilder.create(instance ->
//            instance.group(
//                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(gen -> gen.biomeSource),
//                    Codec.INT.fieldOf("base_height").forGetter(gen -> gen.baseHeight) // 示例自定义参数
//            ).apply(instance, ChunkProviderCelestial::new)
//    );
    public ChunkProviderCelestial(BiomeSource pBiomeSource) {
        super(pBiomeSource);
    }
    // 1. 定义 Codec：这允许游戏将 JSON 配置解析为这个 Java 类
    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void applyCarvers(WorldGenRegion pLevel, long pSeed, RandomState pRandom, BiomeManager pBiomeManager, StructureManager pStructureManager, ChunkAccess pChunk, GenerationStep.Carving pStep) {

    }

    @Override
    public void buildSurface(WorldGenRegion pLevel, StructureManager pStructureManager, RandomState pRandom, ChunkAccess pChunk) {

    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion pLevel) {

    }

    @Override
    public int getGenDepth() {
        return 0;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor pExecutor, Blender pBlender, RandomState pRandom, StructureManager pStructureManager, ChunkAccess pChunk) {
        return null;
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getBaseHeight(int pX, int pZ, Heightmap.Types pType, LevelHeightAccessor pLevel, RandomState pRandom) {
        return 0;
    }

    @Override
    public NoiseColumn getBaseColumn(int pX, int pZ, LevelHeightAccessor pHeight, RandomState pRandom) {
        return null;
    }

    @Override
    public void addDebugScreenInfo(List<String> pInfo, RandomState pRandom, BlockPos pPos) {

    }
}
