package com.hbm.space.dim.trait;

import com.hbm.space.dim.CelestialBody;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Denotes that a celestial body has been destroyed (planet-killer weapon).
 * Tracks the visual progress of the destruction animation.
 */
public class CBT_Destroyed extends CelestialBodyTrait {

    public float destProgress;

    public CBT_Destroyed() {}

    public CBT_Destroyed(float destProgress) {
        this.destProgress = destProgress;
    }

    @Override
    public void update(boolean isRemote, CelestialBody body) {
        if (isRemote) {
            destProgress = Math.min(201.0f, destProgress + 0.0025f * (201.0f - destProgress) * 0.15f);
            if (destProgress >= 200) {
                destProgress = 0;
            }
        }
    }

    @Override
    public void writeToNBT(CompoundTag nbt) {
        nbt.putFloat("destProgress", destProgress);
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
        destProgress = nbt.getFloat("destProgress");
    }

    @Override
    public void writeToBytes(FriendlyByteBuf buf) {
        // Intentionally empty: destruction animation is client-side
    }

    @Override
    public void readFromBytes(FriendlyByteBuf buf) {
        // Intentionally empty
    }
}
