package com.hbm.saveddata.satellites;

import java.util.HashMap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;

/**
 * Mining satellite that periodically delivers loot from a weighted pool.
 * Each SatelliteMiner subclass can register its own cargo pool key.
 */
public class SatelliteMiner extends Satellite {

    /** Maps miner satellite class → item pool key */
    private static final HashMap<Class<? extends SatelliteMiner>, String> CARGO = new HashMap<>();

    public long lastOp;

    public SatelliteMiner() {
        this.satIface = Interfaces.NONE;
    }

    @Override
    public void writeToNBT(CompoundTag nbt) {
        super.writeToNBT(nbt);
        nbt.putLong("lastOp", lastOp);
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
        super.readFromNBT(nbt);
        lastOp = nbt.getLong("lastOp");
    }

    /**
     * Register the cargo pool for a miner satellite class.
     * @param minerSatelliteClass The SatelliteMiner subclass
     * @param cargo Item pool key string
     */
    public static void registerCargo(Class<? extends SatelliteMiner> minerSatelliteClass,
                                     String cargo) {
        CARGO.put(minerSatelliteClass, cargo);
    }

    /** Get the item pool key for this miner's cargo */
    public String getCargo() {
        return CARGO.get(getClass());
    }

    /**
     * Gets the cargo key for a satellite item.
     * Returns null if the item is not a mining satellite.
     */
    public static String getCargoForItem(Item satelliteItem) {
        Class<? extends Satellite> satelliteClass = itemToClass.getOrDefault(satelliteItem, null);
        return satelliteClass != null ? CARGO.getOrDefault(satelliteClass, null) : null;
    }

    static {
        // TODO(port): ItemPoolsSatellite.POOL_SAT_MINER not yet ported
        // registerCargo(SatelliteMiner.class, ItemPoolsSatellite.POOL_SAT_MINER);
    }
}
