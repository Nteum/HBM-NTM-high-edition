package com.hbm.space.dim.trait;

import com.hbm.core.contents.fluid.HBMFluids;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

/**
 * Surface liquid (oceans/lakes) of a celestial body.
 * Doesn't have to be water — can be any fluid.
 */
public class CBT_Water extends CelestialBodyTrait {

    public Fluid fluid;

    public CBT_Water() {
        fluid = HBMFluids.WATER.source().get();
    }

    public CBT_Water(Fluid fluid) {
        this.fluid = fluid;
    }

    @Override
    public void writeToNBT(CompoundTag nbt) {
        nbt.putString("fluid", BuiltInRegistries.FLUID.getKey(fluid).toString());
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
        String raw = nbt.contains("fluid") ? nbt.getString("fluid") : "hbm:water";
        Fluid resolved = BuiltInRegistries.FLUID.get(ResourceLocation.tryParse(raw));
        fluid = resolved != null ? resolved : HBMFluids.WATER.source().get();
    }

    @Override
    public void writeToBytes(FriendlyByteBuf buf) {
        buf.writeUtf(BuiltInRegistries.FLUID.getKey(fluid).toString());
    }

    @Override
    public void readFromBytes(FriendlyByteBuf buf) {
        Fluid resolved = BuiltInRegistries.FLUID.get(
            ResourceLocation.tryParse(buf.readUtf()));
        fluid = resolved != null ? resolved : HBMFluids.WATER.source().get();
    }
}
