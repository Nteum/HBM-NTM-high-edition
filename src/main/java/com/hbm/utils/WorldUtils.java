package com.hbm.utils;

import com.hbm.HBM;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorldUtils {
    @Contract("null, _ -> false")
    public static boolean isChunkLoaded(@Nullable LevelReader world, @NotNull BlockPos pos) {
        return isChunkLoaded(world, SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
    }
    @Contract("null, _ -> false")
    public static boolean isChunkLoaded(@Nullable LevelReader world, ChunkPos chunkPos) {
        return isChunkLoaded(world, chunkPos.x, chunkPos.z);
    }
    @Contract("null, _, _ -> false")
    public static boolean isChunkLoaded(@Nullable LevelReader world, int chunkX, int chunkZ) {
        if (world == null) {
            return false;
        } else if (world instanceof LevelAccessor accessor) {
            if (!(accessor instanceof Level level) || !level.isClientSide) {
                return accessor.hasChunk(chunkX, chunkZ);
            }
            //Don't allow the client level to just return true for all cases, as we actually care if it is present
            // and instead use the fallback logic that we have
        }
        return world.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false) != null;
    }
    @Contract("null, _ -> false")
    public static boolean isBlockLoaded(@Nullable BlockGetter world, @NotNull BlockPos pos) {
        if (world == null) {
            return false;
        } else if (world instanceof LevelReader reader) {
            if (reader instanceof Level level && !level.isInWorldBounds(pos)) {
                return false;
            }
            //TODO: If any cases come up where things are behaving oddly due to the change from reader.hasChunkAt(pos)
            // re-evaluate this and if the specific case is being handled properly
            return isChunkLoaded(reader, pos);
        }
        return true;
    }
    @Nullable
    @Contract("_, null, _, _ -> null")
    public static <T extends BlockEntity> T getTileEntity(@NotNull Class<T> clazz, @Nullable BlockGetter world, @NotNull BlockPos pos, boolean logWrongType) {
        BlockEntity tile = getTileEntity(world, pos);
        if (tile == null) {
            return null;
        }
        if (clazz.isInstance(tile)) {
            return clazz.cast(tile);
        } else if (logWrongType) {
            HBM.LOGGER.warn("Unexpected BlockEntity class at {}, expected {}, but found: {}", pos, clazz, tile.getClass());
        }
        return null;
    }
    @Nullable
    @Contract("null, _ -> null")
    public static BlockEntity getTileEntity(@Nullable BlockGetter world, @NotNull BlockPos pos) {
        if (!isBlockLoaded(world, pos)) {
            //If the world is null, or it is a world reader and the block is not loaded, return null
            return null;
        }
        return world.getBlockEntity(pos);
    }
}
