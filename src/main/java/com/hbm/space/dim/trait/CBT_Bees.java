package com.hbm.space.dim.trait;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Tracks the bee population on a celestial body.
 * 1.0 == ~100 bees.
 */
public class CBT_Bees extends CelestialBodyTrait {

    /** Amount of bees in a dimension */
    public float bees;

    public CBT_Bees() {}

    public CBT_Bees(float bees) {
        this.bees = bees;
    }

    @Override
    public void writeToNBT(CompoundTag nbt) {
        nbt.putFloat("bees", bees);
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
        bees = nbt.getFloat("bees");
    }

    @Override
    public void writeToBytes(FriendlyByteBuf buf) {
        buf.writeFloat(bees);
    }

    @Override
    public void readFromBytes(FriendlyByteBuf buf) {
        bees = buf.readFloat();
    }
}
