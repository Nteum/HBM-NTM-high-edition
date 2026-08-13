package com.hbm.space.dim;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * Per-dimension saved data storing the local time for each celestial body.
 *
 * In 1.7.10 this was attached to WorldProviderCelestial; in 1.20.1 it's a
 * standalone SavedData keyed by dimension ResourceLocation.
 *
 * Ported from 1.7.10 WorldSavedData → 1.20.1 SavedData.
 */
public class CelestialBodyWorldSavedData extends SavedData {

    private static final String DATA_NAME = "CelestialBodyData";

    private long localTime;

    public CelestialBodyWorldSavedData() {
    }

    // === SavedData infrastructure ===

    public static CelestialBodyWorldSavedData load(CompoundTag nbt) {
        CelestialBodyWorldSavedData data = new CelestialBodyWorldSavedData();
        data.localTime = nbt.getLong("time");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.putLong("time", localTime);
        return nbt;
    }

    /**
     * Retrieve or create the saved data for the given dimension.
     */
    public static CelestialBodyWorldSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
            CelestialBodyWorldSavedData::load,
            CelestialBodyWorldSavedData::new,
            DATA_NAME
        );
    }

    // === Time accessors ===

    public long getLocalTime() {
        return localTime;
    }

    public void setLocalTime(long time) {
        localTime = time;
        setDirty();
    }
}
