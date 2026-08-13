package com.hbm.saveddata;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * Stores impact event data (meteor strike, etc.) — dust, fire, time,
 * and coordinates of the last impact.
 *
 * Ported from 1.7.10 WorldSavedData → 1.20.1 SavedData.
 */
public class TomSaveData extends SavedData {

    public static final String KEY = "impactData";

    public float dust;
    public float fire;
    public boolean impact;
    public long time;
    public long dtime;
    public int x;
    public int z;

    private static TomSaveData lastCachedUnsafe = null;

    public TomSaveData() {
        // default constructor for Factory
    }

    public static TomSaveData load(CompoundTag tag) {
        TomSaveData data = new TomSaveData();
        data.dust   = tag.getFloat("dust");
        data.fire   = tag.getFloat("fire");
        data.impact = tag.getBoolean("impact");
        data.time   = tag.getLong("time");
        data.dtime  = tag.getLong("dtime");
        data.x      = tag.getInt("x");
        data.z      = tag.getInt("z");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.putFloat("dust", dust);
        nbt.putFloat("fire", fire);
        nbt.putBoolean("impact", impact);
        nbt.putLong("time", time);
        nbt.putLong("dtime", dtime);
        nbt.putInt("x", x);
        nbt.putInt("z", z);
        return nbt;
    }

    // === Accessors ===

    /**
     * Retrieve or create the per-world TomSaveData.
     * No per-world caching is needed — Minecraft's save structure handles that.
     */
    public static TomSaveData forWorld(Level world) {
        if (world instanceof ServerLevel serverLevel) {
            TomSaveData result = serverLevel.getDataStorage()
                .computeIfAbsent(TomSaveData::load, TomSaveData::new, KEY);
            lastCachedUnsafe = result;
            return result;
        }
        // Client fallback — return cached or null
        return lastCachedUnsafe;
    }

    /**
     * Certain biome events do not have access to a world instance (very bad).
     * In those cases we rely on a possibly incorrect cached result.
     * However, since world gen invokes forWorld() quite a lot,
     * it's safe to say that in most cases we end up with the correct result.
     */
    public static TomSaveData getLastCachedOrNull() {
        return lastCachedUnsafe;
    }

    public static void resetLastCached() {
        lastCachedUnsafe = null;
    }
}
