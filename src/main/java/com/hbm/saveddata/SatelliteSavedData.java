package com.hbm.saveddata;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.hbm.saveddata.satellites.Satellite;
import com.hbm.space.dim.CelestialBody;
import com.hbm.space.dim.orbit.OrbitalStation;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.server.ServerLifecycleHooks;

/**
 * Per-world saved data that stores all deployed satellites,
 * keyed by their communication frequency.
 *
 * Also maintains client-side caches for rendering.
 *
 * Ported from 1.7.10 WorldSavedData → 1.20.1 SavedData.
 */
public class SatelliteSavedData extends SavedData {

    public final HashMap<Integer, Satellite> sats = new HashMap<>();

    public SatelliteSavedData() {
        // default constructor for Factory
    }

    public static SatelliteSavedData load(CompoundTag nbt) {
        SatelliteSavedData data = new SatelliteSavedData();
        int satCount = nbt.getInt("satCount");

        for (int i = 0; i < satCount; i++) {
            Satellite sat = Satellite.create(nbt.getInt("sat_id_" + i));
            if (sat == null) continue; // skip unknown satellite types from older versions

            CompoundTag satNbt = nbt.getCompound("sat_data_" + i);
            sat.readFromNBT(satNbt);

            int freq = nbt.getInt("sat_freq_" + i);
            data.sats.put(freq, sat);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.putInt("satCount", sats.size());

        int i = 0;
        for (Map.Entry<Integer, Satellite> struct : sats.entrySet()) {
            CompoundTag data = new CompoundTag();
            struct.getValue().writeToNBT(data);

            nbt.putInt("sat_id_" + i, struct.getValue().getID());
            nbt.put("sat_data_" + i, data);
            nbt.putInt("sat_freq_" + i, struct.getKey());
            i++;
        }
        return nbt;
    }

    // === Queries ===

    public boolean isFreqTaken(int freq) {
        return getSatFromFreq(freq) != null;
    }

    public Satellite getSatFromFreq(int freq) {
        return sats.get(freq);
    }

    // === Accessors ===

    /**
     * Retrieve the satellite data for the given world.
     * @deprecated Will return invalid results in orbit — use
     *             {@link #getData(Level, int, int)} for position-aware lookup.
     */
    @Deprecated
    public static SatelliteSavedData getData(Level world) {
        if (world instanceof ServerLevel serverLevel) {
            return serverLevel.getDataStorage()
                .computeIfAbsent(SatelliteSavedData::load, SatelliteSavedData::new, "satellites");
        }
        throw new IllegalStateException("Cannot get SatelliteSavedData from client level");
    }

    /**
     * Position-aware data retrieval. If the caller is in orbit,
     * the lookup is redirected to the body being orbited.
     */
    public static SatelliteSavedData getData(Level world, int x, int z) {
        if (!world.isClientSide() && CelestialBody.inOrbit(world)) {
            OrbitalStation station = OrbitalStation.getStationFromPosition(x, z);
            if (station != null && station.orbiting != null && station.orbiting.dimension != null) {
                var server = ServerLifecycleHooks.getCurrentServer();
                if (server != null) {
                    ServerLevel orbitingWorld = server.getLevel(station.orbiting.dimension);
                    if (orbitingWorld != null) {
                        world = orbitingWorld;
                    }
                }
            }
        }
        return getData(world);
    }

    /**
     * Searches across all landable bodies for a satellite with the given frequency.
     * Falls back to the current world's data if not found.
     */
    public static SatelliteSavedData getDataFromFreq(Level world, int x, int z, int freq) {
        SatelliteSavedData data = getData(world, x, z);
        if (data.getSatFromFreq(freq) != null) {
            return data;
        }
        // Skip nether/end — satellites can't exist there
        ResourceKey<Level> dim = world.dimension();
        if (dim.equals(Level.NETHER) || dim.equals(Level.END)) {
            return data;
        }

        // Search all landable bodies
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            for (CelestialBody body : CelestialBody.getLandableBodies()) {
                if (body.dimension == null) continue;
                ServerLevel bodyWorld = server.getLevel(body.dimension);
                if (bodyWorld == null) continue;

                SatelliteSavedData bodyData = getData(bodyWorld);
                if (bodyData.getSatFromFreq(freq) != null) {
                    return bodyData;
                }
            }
        }

        return data;
    }

    // === Client Sync ===

    public static HashMap<Integer, Satellite> clientSats = new HashMap<>();
    public static HashMap<ResourceLocation, HashMap<Integer, Satellite>> clientSatsByDimension = new HashMap<>();

    @OnlyIn(Dist.CLIENT)
    public static void setClientSats(HashMap<Integer, Satellite> sats) {
        clientSats = sats;
    }

    @OnlyIn(Dist.CLIENT)
    public static HashMap<Integer, Satellite> getClientSats() {
        return clientSats;
    }

    @OnlyIn(Dist.CLIENT)
    public static void setClientSatsByDimension(
            HashMap<ResourceLocation, HashMap<Integer, Satellite>> satsByDimension) {
        clientSatsByDimension = satsByDimension != null
            ? satsByDimension
            : new HashMap<>();
    }

    @OnlyIn(Dist.CLIENT)
    public static Map<Integer, Satellite> getClientSats(ResourceLocation dimensionId) {
        HashMap<Integer, Satellite> sats = clientSatsByDimension.get(dimensionId);
        return sats != null ? sats : Collections.emptyMap();
    }
}
