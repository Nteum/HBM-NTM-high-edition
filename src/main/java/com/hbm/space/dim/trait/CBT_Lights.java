package com.hbm.space.dim.trait;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Tracks light pollution / civilization level on a celestial body.
 * Used to render city lights visible from orbit.
 */
public class CBT_Lights extends CelestialBodyTrait {

    public int lights;
    public boolean isCivilized;

    public CBT_Lights() {}

    public CBT_Lights(int light) {
        this.lights = light;
    }

    /**
     * Returns an intensity level for rendering:
     * 0 = none, 1 = >400, 2 = >2000, 3 = >10000
     */
    public int getIntensity() {
        if (lights > 10000) return 3;
        if (lights > 2000) return 2;
        if (lights > 400) return 1;
        return 0;
    }

    public void addLight(BlockState state, int x, int y, int z) {
        lights += state.getLightEmission();
    }

    public void removeLight(BlockState state, int x, int y, int z) {
        lights -= state.getLightEmission();
        if (lights < 0) lights = 0;
    }

    @Override
    public void writeToNBT(CompoundTag nbt) {
        nbt.putInt("lights", lights);
        nbt.putBoolean("isCiv", isCivilized);
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
        lights = nbt.getInt("lights");
        isCivilized = nbt.getBoolean("isCiv");
    }

    @Override
    public void writeToBytes(FriendlyByteBuf buf) {
        buf.writeInt(lights);
        buf.writeBoolean(isCivilized);
    }

    @Override
    public void readFromBytes(FriendlyByteBuf buf) {
        lights = buf.readInt();
        isCivilized = buf.readBoolean();
    }
}
