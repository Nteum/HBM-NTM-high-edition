package com.hbm.space.dim.trait;

import java.util.HashMap;
import java.util.Map;

import com.hbm.space.dim.CelestialBody;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

/**
 * Dyson swarm trait — manages satellite swarms around a star.
 * Each swarm is identified by an integer ID and tracks member count
 * and consumer count for power distribution.
 *
 * Swarms attenuate over time (satellite failures), encouraging
 * continuous automation.
 */
public class CBT_Dyson extends CelestialBodyTrait {

    /** Correlates an ID with a swarm */
    private HashMap<Integer, Swarm> swarms = new HashMap<>();

    private static class Swarm {

        int members;
        int consumers;

        /** Incremented whenever another consumer is added;
         *  copied to consumers at the start of a tick. */
        private int addedConsumers;

        public Swarm(int members) {
            this.members = members;
        }
    }

    /**
     * Launch a single satellite member into a swarm.
     */
    public static void launch(Level world, int id) {
        launch(world, id, 1);
    }

    /**
     * Launch a specified number of satellite members into a swarm.
     */
    public static void launch(Level world, int id, int amount) {
        CelestialBody star = CelestialBody.getStar(world);
        CBT_Dyson dyson = star.getTrait(CBT_Dyson.class);
        if (dyson == null) dyson = new CBT_Dyson();

        Swarm swarm = dyson.swarms.get(id);
        if (swarm == null) {
            swarm = new Swarm(0);
            dyson.swarms.put(id, swarm);
        }

        swarm.members += amount;

        star.modifyTraits(dyson);
    }

    /**
     * Get the total member count for a specific swarm.
     */
    public static int count(Level world, int id) {
        CelestialBody star = CelestialBody.getStar(world);
        CBT_Dyson dyson = star.getTrait(CBT_Dyson.class);
        if (dyson == null) return 0;

        Swarm swarm = dyson.swarms.get(id);
        if (swarm == null) return 0;

        return swarm.members;
    }

    /**
     * Register a consumer and return the current consumer count.
     * Each call increments the consumer count for the next tick.
     */
    public static int consumers(Level world, int id) {
        CelestialBody star = CelestialBody.getStar(world);
        CBT_Dyson dyson = star.getTrait(CBT_Dyson.class);
        if (dyson == null) return 0;

        Swarm swarm = dyson.swarms.get(id);
        if (swarm == null) return 0;

        swarm.addedConsumers++;

        return swarm.consumers;
    }

    /**
     * Total members across all swarms.
     */
    public int size() {
        int size = 0;
        for (Swarm swarm : swarms.values()) {
            size += swarm.members;
        }
        return size;
    }

    /**
     * Called once per tick to lower swarm counts from satellite failures.
     * Based on total across all swarms — players on servers are encouraged
     * to either annihilate other launchers or work together.
     */
    public void attenuate() {
        for (Swarm swarm : swarms.values()) {
            swarm.consumers = swarm.addedConsumers;
            swarm.addedConsumers = 0;

            if (swarm.members <= 0) continue;

            double decayChance = (double) size() / (1024 * 5 * 20);
            if (Math.random() < decayChance)
                swarm.members--;
        }
    }

    @Override
    public void writeToNBT(CompoundTag nbt) {
        int[] swarmData = new int[swarms.size() * 2];
        int i = 0;
        for (Map.Entry<Integer, Swarm> entry : swarms.entrySet()) {
            swarmData[i] = entry.getKey();
            swarmData[i + 1] = entry.getValue().members;
            i += 2;
        }
        nbt.putIntArray("swarm", swarmData);
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
        int[] swarmData = nbt.getIntArray("swarm");
        swarms = new HashMap<>();
        for (int i = 0; i < swarmData.length; i += 2) {
            swarms.put(swarmData[i], new Swarm(swarmData[i + 1]));
        }
    }

    @Override
    public void writeToBytes(FriendlyByteBuf buf) {
        buf.writeInt(swarms.size() * 2);
        for (Map.Entry<Integer, Swarm> entry : swarms.entrySet()) {
            buf.writeShort(entry.getKey());
            buf.writeInt(entry.getValue().members);
        }
    }

    @Override
    public void readFromBytes(FriendlyByteBuf buf) {
        int count = buf.readInt();
        swarms = new HashMap<>();
        for (int i = 0; i < count; i += 2) {
            int id = buf.readShort();
            int members = buf.readInt();
            swarms.put(id, new Swarm(members));
        }
    }
}
