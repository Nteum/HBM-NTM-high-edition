package com.hbm.space.dim.trait;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import com.hbm.space.dim.CelestialBody;
import com.hbm.space.dim.CelestialBody;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Base class for celestial body traits.
 * Traits define properties of a celestial body that can be modified
 * at runtime (e.g., terraforming, atmosphere changes, war damage).
 *
 * Similarly to fluid traits, we have classes and instance members.
 * For simple traits, we init both here rather than two places.
 */
public abstract class CelestialBodyTrait {

    // === Simple Singleton Traits ===

    public static class CBT_BATTLEFIELD extends CelestialBodyTrait { }
    public static final CBT_BATTLEFIELD BATTLE = new CBT_BATTLEFIELD();

    public static class CBT_COMPROMISED extends CelestialBodyTrait { }
    public static final CBT_COMPROMISED COMP = new CBT_COMPROMISED();

    // === Trait Registry ===

    public static final List<Class<? extends CelestialBodyTrait>> traitList = new ArrayList<>();
    public static final BiMap<String, Class<? extends CelestialBodyTrait>> traitMap = HashBiMap.create();

    static {
        registerTrait("atmosphere", CBT_Atmosphere.class);
        registerTrait("temperature", CBT_Temperature.class);
        registerTrait("bees", CBT_Bees.class);
        registerTrait("war", CBT_War.class);
        registerTrait("destroyed", CBT_Destroyed.class);
        registerTrait("water", CBT_Water.class);
        registerTrait("weather", CBT_Weather.class);
        registerTrait("battle", CBT_BATTLEFIELD.class);
        registerTrait("infected", CBT_COMPROMISED.class);
        registerTrait("dyson", CBT_Dyson.class);
        registerTrait("impact", CBT_Impact.class);
        registerTrait("lights", CBT_Lights.class);
        registerTrait("invasion", CBT_Invasion.class);
    }

    private static void registerTrait(String name, Class<? extends CelestialBodyTrait> clazz) {
        traitList.add(clazz);
        traitMap.put(name, clazz);
    }

    // === Serialization ===

    public void readFromNBT(CompoundTag nbt) { }
    public void writeToNBT(CompoundTag nbt) { }

    public void readFromBytes(FriendlyByteBuf buf) { }
    public void writeToBytes(FriendlyByteBuf buf) { }

    /**
     * Called each tick to allow traits to update their state.
     * @param isRemote true if on the logical client side
     * @param body the celestial body this trait belongs to
     */
    public void update(boolean isRemote, CelestialBody body) { }
}
