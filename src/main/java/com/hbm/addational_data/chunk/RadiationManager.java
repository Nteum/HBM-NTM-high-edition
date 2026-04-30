package com.hbm.addational_data.chunk;

import com.hbm.addational_data.AdditionalDataManager;
import com.hbm.addational_data.DataEntry;
import com.hbm.config.RadiationConfig;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

// 控制环境辐射的更新
public class RadiationManager {
    public static final float worldDestructionThreshold = 10;
    public static float getRadiation(Level level, BlockPos blockPos){
        ChunkPos chunkPos = new ChunkPos(blockPos);
        LevelChunk chunk = getLoadedChunk(level, chunkPos.x, chunkPos.z);
        return chunk == null ? 0f : AdditionalDataManager.getChunkData(chunk, DataEntry.RADIATION).map(o -> (float) o).orElse(0f);
    }

    public static void incrementRadiation(Level level, BlockPos blockPos, float rad){
        LevelChunk chunk = getLoadedChunk(level, blockPos.getX() >> 4, blockPos.getZ() >> 4);
        if (chunk != null){
            Float radOld = AdditionalDataManager.getChunkData(chunk, DataEntry.RADIATION).map(o -> (float) o).orElse(0f);
            AdditionalDataManager.setChunkData(chunk, DataEntry.RADIATION, radOld + rad);
        }
    }
    public static void decrementRadiation(Level level, BlockPos blockPos, float radToSubtract){
        incrementRadiation(level, blockPos, -radToSubtract);
    }
    public static void updateRadiation(ServerLevel level, LevelChunk chunk, IChunkAdditionalData chunkData){
        if (level.random.nextInt(20) != 0) return;
        ChunkPos pos = chunk.getPos();
        float rad_add = 0f;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                LevelChunk neighbourChunk = getLoadedChunk(level, pos.x + i, pos.z + j);
                if (neighbourChunk != null){
                    int dist = Math.abs(i) + Math.abs(j);
                    Float rad = AdditionalDataManager.getChunkData(neighbourChunk, DataEntry.RADIATION).map(o -> (float) o).orElse(0f);
                    rad_add += rad * (dist == 1 ? 0.075F : 0.025F);
                }
            }
        }
        float newRad = chunkData.getData(DataEntry.RADIATION).map(o -> (float) o).orElse(0f) * 0.6f;
        if (rad_add > 0){
            if (chunkData.contains(DataEntry.RADIATION)) {
                newRad += rad_add;
            }
            chunkData.setData(DataEntry.RADIATION, newRad);
        }
        // 添加辐射雾

        // 添加环境破坏效果
        if (RadiationConfig.worldRadEffects && newRad > worldDestructionThreshold){
            for(int a = 0; a < 16; a++) {
                for(int b = 0; b < 16; b++) {
                    if(level.random.nextInt(3) != 0) continue;

                    int x = pos.getMiddleBlockX() - 8 + a;
                    int z = pos.getMiddleBlockZ() - 8 + b;
                    int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - level.random.nextInt(2);

                    BlockState blockState = level.getBlockState(new BlockPos(x, y, z));
                    if (blockState.isAir())continue;
                    else if(blockState.is(Blocks.GRASS_BLOCK)) {
                        level.setBlock(new BlockPos(x,y,z), ModBlocks.WASTE_EARTH.get().defaultBlockState(),3);
                    } else if(blockState.is(Blocks.TALL_GRASS)) {
                        level.setBlock(new BlockPos(x,y,z), Blocks.AIR.defaultBlockState(),11);
                    } else if(blockState.is(BlockTags.LEAVES) && !(blockState.is(ModBlocks.WASTE_LEAVES.get()))) {
                        if(level.random.nextInt(7) <= 5) {
                            level.setBlock(new BlockPos(x,y,z), ModBlocks.WASTE_LEAVES.get().defaultBlockState(),3);
                        } else {
                            level.setBlock(new BlockPos(x,y,z), Blocks.AIR.defaultBlockState(),11);
                        }
                    }
                }
            }
        }
    }

    private static LevelChunk getLoadedChunk(Level level, int chunkX, int chunkZ) {
        if (!level.hasChunk(chunkX, chunkZ)) {
            return null;
        }
        if (level instanceof ServerLevel server) {
            return server.getChunkSource().getChunkNow(chunkX, chunkZ);
        }
        return level.getChunk(chunkX, chunkZ);
    }
}
