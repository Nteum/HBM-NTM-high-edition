package com.hbm.utils;

import net.minecraft.world.level.ChunkPos;

/**
 * Unique identifier for sub-chunks.
 *
 * @author mlbv
 */
public class SubChunkKey {

    private int chunkX;
    private int chunkZ;
    private int sectionY;
    private int hash;

    public SubChunkKey(int cx, int cz, int sy) {
        this.update(cx, cz, sy);
    }

    public SubChunkKey(ChunkPos pos, int sy) {
        this.update(pos.x, pos.z, sy);
    }

    public SubChunkKey update(int cx, int cz, int sy) {
        this.chunkX = cx;
        this.chunkZ = cz;
        this.sectionY = sy;
        int result = sectionY;
        result = 31 * result + cx;
        result = 31 * result + cz;
        this.hash = result;
        return this;
    }

    @Override
    public final int hashCode() {
        return this.hash;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubChunkKey k)) return false;
        return this.sectionY == k.sectionY && this.chunkX == k.chunkX && this.chunkZ == k.chunkZ;
    }

    public int getSectionY() {
        return sectionY;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public ChunkPos getPos() {
        return new ChunkPos(this.chunkX, this.chunkZ);
    }
}
