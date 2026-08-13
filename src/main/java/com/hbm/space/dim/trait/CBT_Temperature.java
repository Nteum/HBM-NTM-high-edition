package com.hbm.space.dim.trait;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Defines the surface temperature of a celestial body.
 */
public class CBT_Temperature extends CelestialBodyTrait {

    /** Temperature in Celsius (SI units) */
    public float degrees;

    public CBT_Temperature() {
        this.degrees = 20;
    }

    public CBT_Temperature(float degrees) {
        this.degrees = degrees;
    }

    @Override
    public void writeToNBT(CompoundTag nbt) {
        nbt.putFloat("c", degrees);
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
        degrees = nbt.getFloat("c");
    }

    @Override
    public void writeToBytes(FriendlyByteBuf buf) {
        buf.writeFloat(degrees);
    }

    @Override
    public void readFromBytes(FriendlyByteBuf buf) {
        degrees = buf.readFloat();
    }
}
