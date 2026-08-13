package com.hbm.space.dim;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.hbm.config.SpaceConfig;
import com.hbm.space.dim.orbit.OrbitalStation;
import com.hbm.space.dim.orbit.OrbitalStation.StationState;
import com.hbm.space.dim.trait.CelestialBodyTrait;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.server.ServerLifecycleHooks;

/**
 * Persistent world saved data for the solar system.
 * Stores trait overrides for celestial bodies and orbital station data.
 *
 * Similar to CelestialBodyWorldSavedData but for anything that must be
 * visible from other bodies, like atmospheric data or UTTER ANNIHILATION.
 *
 * Ported from 1.7.10 com.hbm.dim.SolarSystemWorldSavedData to 1.20.1.
 */
public class SolarSystemWorldSavedData extends SavedData {

    private static final String DATA_NAME = "SolarSystemData";

    private final Random rand = new Random();

    /** body name → trait class → trait instance */
    private final HashMap<String, HashMap<Class<? extends CelestialBodyTrait>, CelestialBodyTrait>>
        traitMap = new HashMap<>();

    /** ChunkPos → OrbitalStation */
    private final HashMap<ChunkPos, OrbitalStation> stations = new HashMap<>();

    // Constructor for new data
    public SolarSystemWorldSavedData() {
        // empty
    }

    // Constructor for loading from NBT
    private SolarSystemWorldSavedData(CompoundTag nbt) {
        loadStatic(nbt, this);
    }

    // === Accessors ===

    /**
     * Get the saved data from the overworld.
     */
    public static SolarSystemWorldSavedData get() {
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null)
            throw new IllegalStateException("Cannot get SolarSystemWorldSavedData without a server");
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (overworld == null)
            throw new IllegalStateException("Overworld is null");
        return overworld.getDataStorage().computeIfAbsent(SolarSystemWorldSavedData::load, SolarSystemWorldSavedData::new, DATA_NAME);
    }

    /**
     * Get the saved data using the given level's world storage.
     */
    public static SolarSystemWorldSavedData get(Level world) {
        if (world instanceof ServerLevel serverLevel) {
            return serverLevel.getDataStorage().computeIfAbsent(SolarSystemWorldSavedData::load, SolarSystemWorldSavedData::new, DATA_NAME);
        }
        // Fallback: try overworld
        return get();
    }

    // === NBT Serialization ===

    public static SolarSystemWorldSavedData load(CompoundTag nbt) {
        SolarSystemWorldSavedData data = new SolarSystemWorldSavedData();
        loadStatic(nbt, data);
        return data;
    }

    private static void loadStatic(CompoundTag nbt, SolarSystemWorldSavedData data) {
        for (CelestialBody body : CelestialBody.getAllBodies()) {
            if (nbt.contains("b_" + body.name)) {
                CompoundTag bodyData = nbt.getCompound("b_" + body.name);
                HashMap<Class<? extends CelestialBodyTrait>, CelestialBodyTrait> traits =
                    new HashMap<>();

                for (Map.Entry<String, Class<? extends CelestialBodyTrait>> entry :
                        CelestialBodyTrait.traitMap.entrySet()) {
                    if (bodyData.contains(entry.getKey())) {
                        try {
                            CelestialBodyTrait trait =
                                entry.getValue().getDeclaredConstructor().newInstance();
                            trait.readFromNBT(bodyData.getCompound(entry.getKey()));
                            traits.put(trait.getClass(), trait);
                        } catch (Exception ex) {
                            // Skip traits that fail to deserialize
                        }
                    }
                }

                data.traitMap.put(body.name, traits);
            }
        }

        data.stations.clear();
        ListTag stationList = nbt.getList("stations", Tag.TAG_COMPOUND);
        for (int i = 0; i < stationList.size(); i++) {
            CompoundTag stationTag = stationList.getCompound(i);
            int x = stationTag.getInt("x");
            int z = stationTag.getInt("z");
            CelestialBody orbiting = CelestialBody.getBody(stationTag.getString("orbiting"));
            CelestialBody target = CelestialBody.getBody(stationTag.getString("target"));
            StationState state = StationState.values()[stationTag.getInt("state")];
            int stateTimer = stationTag.getInt("stateTimer");
            int maxStateTimer = stationTag.getInt("maxStateTimer");
            boolean hasStation = stationTag.getBoolean("hasStation");
            String name = stationTag.getString("name");
            float gravityMultiplier = stationTag.contains("gravity")
                ? stationTag.getFloat("gravity") : 1;

            ChunkPos pos = new ChunkPos(x, z);
            OrbitalStation station = new OrbitalStation(orbiting, x, z);
            station.target = target;
            station.state = state;
            station.stateTimer = stateTimer;
            station.maxStateTimer = maxStateTimer;
            station.hasStation = hasStation;
            station.name = name;
            station.gravityMultiplier = gravityMultiplier;

            data.stations.put(pos, station);
        }
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        for (Map.Entry<String, HashMap<Class<? extends CelestialBodyTrait>, CelestialBodyTrait>>
                bodyEntry : traitMap.entrySet()) {
            CompoundTag bodyData = new CompoundTag();

            for (CelestialBodyTrait trait : bodyEntry.getValue().values()) {
                String traitName = CelestialBodyTrait.traitMap.inverse().get(trait.getClass());
                CompoundTag traitData = new CompoundTag();
                trait.writeToNBT(traitData);
                bodyData.put(traitName, traitData);
            }

            nbt.put("b_" + bodyEntry.getKey(), bodyData);
        }

        ListTag stationList = new ListTag();
        for (OrbitalStation station : stations.values()) {
            CompoundTag stationTag = new CompoundTag();
            stationTag.putInt("x", station.dX);
            stationTag.putInt("z", station.dZ);
            stationTag.putString("orbiting", station.orbiting.name);
            stationTag.putString("target", station.target.name);
            stationTag.putInt("state", station.state.ordinal());
            stationTag.putInt("stateTimer", station.stateTimer);
            stationTag.putInt("maxStateTimer", station.maxStateTimer);
            stationTag.putBoolean("hasStation", station.hasStation);
            stationTag.putString("name", station.name);
            stationTag.putFloat("gravity", station.gravityMultiplier);

            stationList.add(stationTag);
        }
        nbt.put("stations", stationList);

        return nbt;
    }

    // === Trait Management ===

    public void setTraits(String bodyName, CelestialBodyTrait... traits) {
        if (traits.length == 0) {
            clearTraits(bodyName);
            return;
        }

        HashMap<Class<? extends CelestialBodyTrait>, CelestialBodyTrait> newTraits =
            new HashMap<>();
        for (CelestialBodyTrait trait : traits) {
            newTraits.put(trait.getClass(), trait);
        }

        this.traitMap.put(bodyName, newTraits);
        setDirty();
    }

    public void clearTraits(String bodyName) {
        this.traitMap.remove(bodyName);
        setDirty();
    }

    public HashMap<Class<? extends CelestialBodyTrait>, CelestialBodyTrait>
            getTraits(String bodyName) {
        return traitMap.get(bodyName);
    }

    // === Station Management ===

    public HashMap<ChunkPos, OrbitalStation> getStations() {
        return stations;
    }

    /** Grabs an existing station */
    public OrbitalStation getStationFromPosition(int x, int z) {
        ChunkPos pos = new ChunkPos(
            Mth.floor((float) x / OrbitalStation.STATION_SIZE),
            Mth.floor((float) z / OrbitalStation.STATION_SIZE)
        );
        return stations.get(pos);
    }

    /** Find an unoccupied space for a new station */
    public ChunkPos findFreeSpace() {
        int size = SpaceConfig.maxStationDistance / OrbitalStation.STATION_SIZE;

        ChunkPos pos = null;
        for (int i = 0; i < 512; i++) {
            pos = new ChunkPos(
                rand.nextInt(size * 2) - size,
                rand.nextInt(size * 2) - size
            );
            if (!stations.containsKey(pos)) return pos;
            if (i > 256 && !stations.get(pos).hasStation) return pos;
        }

        return pos;
    }

    /** Finds an unoccupied space and adds a new station */
    public OrbitalStation addStation(CelestialBody orbiting) {
        ChunkPos pos = findFreeSpace();
        return addStation(pos.x, pos.z, orbiting);
    }

    /** Adds a station at given coordinates (used for debug stations). Won't overwrite existing. */
    public OrbitalStation addStation(int x, int z, CelestialBody orbiting) {
        ChunkPos pos = new ChunkPos(x, z);
        OrbitalStation station = stations.get(pos);

        if (station == null) {
            station = new OrbitalStation(orbiting, x, z);
            stations.put(pos, station);
        }

        setDirty();
        return station;
    }

    public void removeStation(OrbitalStation station) {
        removeStation(station.dX, station.dZ);
    }

    public void removeStation(int x, int z) {
        ChunkPos pos = new ChunkPos(x, z);

        // Don't remove stations with launched cores, only temporary stations!
        OrbitalStation station = stations.get(pos);
        if (station == null || station.hasStation) return;

        stations.remove(pos);
    }

    // === Client Sync ===

    /** Client-side trait cache — updated via network packets */
    public static HashMap<String, HashMap<Class<? extends CelestialBodyTrait>, CelestialBodyTrait>>
        clientTraits = new HashMap<>();

    public static void updateClientTraits(
            HashMap<String, HashMap<Class<? extends CelestialBodyTrait>, CelestialBodyTrait>>
            traits) {
        clientTraits = traits;
        if (clientTraits == null)
            clientTraits = new HashMap<>();
    }

    public static HashMap<Class<? extends CelestialBodyTrait>, CelestialBodyTrait>
            getClientTraits(String bodyName) {
        return clientTraits.get(bodyName);
    }
}
