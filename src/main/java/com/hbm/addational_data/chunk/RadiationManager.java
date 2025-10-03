package com.hbm.addational_data.chunk;

import com.hbm.addational_data.AdditionalDataManager;
import com.hbm.addational_data.DataEntry;
import com.hbm.block.HBMBlockComponent;
import com.hbm.config.RadiationConfig;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Optional;

// 控制环境辐射的更新
public class RadiationManager {
    public static final float worldDestructionThreshold = 10;
    public static float getRadiation(Level level, BlockPos blockPos){
        ChunkPos chunkPos = new ChunkPos(blockPos);
        if (!level.hasChunk(chunkPos.x, chunkPos.z)) return 0f;
        ChunkAccess chunk = level.getChunk(blockPos);
        if (chunk instanceof LevelChunk levelChunk){
            return AdditionalDataManager.getChunkData(levelChunk, DataEntry.RADIATION).map(o -> (float) o).orElse(0f);
        }
        return 0;
    }

    public static void incrementRadiation(Level level, BlockPos blockPos, float rad){
        if (level.hasChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4) && (level.getChunk(blockPos) instanceof LevelChunk chunk)){
            Float radOld = AdditionalDataManager.getChunkData(chunk, DataEntry.RADIATION).map(o -> (float) o).orElse(0f);
            AdditionalDataManager.setChunkData(chunk, DataEntry.RADIATION, radOld + rad);
        }
    }
    public static void decrementRadiation(Level level, BlockPos blockPos, float radToSubtract){
        incrementRadiation(level, blockPos, -radToSubtract);
    }
    public static void updateRadiation(ServerLevel level, LevelChunk chunk, IChunkAdditionalData chunkData){
        ChunkPos pos = chunk.getPos();
        float rad_add = 0f;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (level.hasChunk(pos.x + i, pos.z + j) && i + j != 0){
                    int dist = i + j;
                    LevelChunk neighbourChunk = level.getChunk(pos.x + i, pos.z + j);
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
                        level.setBlock(new BlockPos(x,y,z), HBMBlockComponent.WAST_EARTH.get().defaultBlockState(),3);
                    } else if(blockState.is(Blocks.TALL_GRASS)) {
                        level.setBlock(new BlockPos(x,y,z), Blocks.AIR.defaultBlockState(),11);
                    } else if(blockState.is(BlockTags.LEAVES) && !(blockState.is(HBMBlockComponent.WAST_LEAVES.get()))) {
                        if(level.random.nextInt(7) <= 5) {
                            level.setBlock(new BlockPos(x,y,z), HBMBlockComponent.WAST_LEAVES.get().defaultBlockState(),3);
                        } else {
                            level.setBlock(new BlockPos(x,y,z), Blocks.AIR.defaultBlockState(),11);
                        }
                    }
                }
            }
        }
    }
}
