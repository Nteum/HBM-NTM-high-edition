package com.hbm.saveddata.satellites;

import com.hbm.space.dim.CelestialBody;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

/**
 * Base class for war satellites. Provides fire/cooldown/interp
 * infrastructure. Rendering delegated to subclasses — stubbed
 * until the render system is ported.
 */
public class SatelliteWar extends Satellite {

    public long lastOp;
    public float interp;
    public int cooldown;

    public SatelliteWar() {
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

    @Override
    public void onClick(Level world, int x, int z) {
    }

    /** Called each tick while the weapon is firing */
    public void fire() {
    }

    public void setTarget(CelestialBody body) {
    }

    public void fireAtTarget(CelestialBody body) {
    }

    // TODO(port): rendering — playsound and getModel use Minecraft.getMinecraft()/client APIs
    // public void playsound() { ... }
    // public int magSize() { return 0; }

    @Override
    public void serialize(FriendlyByteBuf buf) {
        super.serialize(buf);
        buf.writeFloat(interp);
    }

    @Override
    public void deserialize(FriendlyByteBuf buf) {
        super.deserialize(buf);
        this.interp = buf.readFloat();
    }
}
