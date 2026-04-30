package com.hbm.utils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A snapshot of a sub-chunks.
 *
 * @author mlbv
 */
public class SubChunkSnapshot {
    /**
     * A section that contains only air.
     */
    public static final SubChunkSnapshot EMPTY = new SubChunkSnapshot(new Block[]{Blocks.AIR}, null);

    private final Block[] palette;
    private final short[] data;

    private SubChunkSnapshot(Block[] p, short[] d) {
        this.palette = p;
        this.data = d;
    }

    /**
     * Creates a SubChunkSnapshot.
     *
     * @param level           The ServerLevel instance from which to retrieve the chunk.
     * @param key             The SubChunkKey identifying the section.
     * @param allowGeneration Whether to generate chunks. If false, attempting to retrieve a snapshot of a chunk that doesn't exist would return {@link SubChunkSnapshot#EMPTY}.
     * @return A SubChunkSnapshot containing the palette and block data for the section, or {@link SubChunkSnapshot#EMPTY} if the region contains only air.
     */
    public static SubChunkSnapshot getSnapshot(ServerLevel level, SubChunkKey key, boolean allowGeneration) {
        LevelChunk chunk = allowGeneration
                ? (LevelChunk) level.getChunk(key.getChunkX(), key.getChunkZ(), ChunkStatus.FULL, true)
                : level.getChunkSource().getChunkNow(key.getChunkX(), key.getChunkZ());
        if (chunk == null) return SubChunkSnapshot.EMPTY;
        int lowestSectionIndex = level.getSectionIndex(level.getMinBuildHeight());
        int arrayIndex = key.getSectionY() - lowestSectionIndex;
        LevelChunkSection section = chunk.getSection(arrayIndex);
        if (section.hasOnlyAir()) return SubChunkSnapshot.EMPTY;

        short[] data = new short[16 * 16 * 16];
        List<Block> palette = new ArrayList<>();
        palette.add(Blocks.AIR);
        Map<Block, Short> idxMap = new HashMap<>();
        idxMap.put(Blocks.AIR, (short) 0);

        for (int ly = 0; ly < 16; ly++) {
            for (int lz = 0; lz < 16; lz++) {
                for (int lx = 0; lx < 16; lx++) {
                    Block block = section.getBlockState(lx, ly, lz).getBlock();
                    short idx;
                    if (block == Blocks.AIR) idx = 0;
                    else {
                        Short e = idxMap.get(block);
                        if (e == null) {
                            idx = (short) palette.size();
                            idxMap.put(block, idx);
                            palette.add(block);
                        } else idx = e;
                    }
                    data[(ly << 8) | (lz << 4) | lx] = idx;
                }
            }
        }
        return new SubChunkSnapshot(palette.toArray(new Block[0]), data);
    }

    /**
     * Retrieves the Block at the specified local coordinates within this sub-chunk snapshot.
     *
     * @param x The local x-coordinate within the section (0-15).
     * @param y The local y-coordinate within the section (0-15).
     * @param z The local z-coordinate within the section (0-15).
     * @return The Block instance at the given position.
     */
    public Block getBlock(int x, int y, int z) {
        if (this == EMPTY || data == null) return Blocks.AIR;
        short idx = data[(y << 8) | (z << 4) | x];
        return (idx >= 0 && idx < palette.length) ? palette[idx] : Blocks.AIR;
    }
}
