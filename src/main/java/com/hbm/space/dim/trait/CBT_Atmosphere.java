package com.hbm.space.dim.trait;

import java.util.ArrayList;
import java.util.List;

import com.hbm.core.contents.fluid.HbmFluidType;
import com.hbm.core.contents.fluid.HBMFluids;
import com.hbm.core.contents.fluid.IFluidTrait;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

/**
 * Atmospheric composition of a celestial body — a list of (fluid, partial pressure) pairs.
 * The {@code Fluid} field identifies which fluid; trait data (colour, gaseous, corrosion, etc.)
 * is accessed via {@code ((HbmFluidType) fluid.getFluidType())} only when needed.
 */
public class CBT_Atmosphere extends CelestialBodyTrait {

    public ArrayList<FluidEntry> fluids;

    public static class FluidEntry {
        /** The fluid making up this atmospheric component */
        public Fluid fluid;
        /** Partial pressure in atmospheres */
        public double pressure;

        public FluidEntry(Fluid fluid, double pressure) {
            this.fluid = fluid;
            this.pressure = pressure;
        }
    }

    public CBT_Atmosphere() {
        fluids = new ArrayList<>();
    }

    public CBT_Atmosphere(Fluid fluid, double pressure) {
        fluids = new ArrayList<>();
        fluids.add(new FluidEntry(fluid, pressure));
    }

    public CBT_Atmosphere and(Fluid fluid, double pressure) {
        fluids.add(new FluidEntry(fluid, pressure));
        return this;
    }

    public CBT_Atmosphere clone() {
        CBT_Atmosphere clone = new CBT_Atmosphere();
        for (FluidEntry entry : fluids) {
            clone.fluids.add(new FluidEntry(entry.fluid, entry.pressure));
        }
        return clone;
    }

    /** Add to the pressure of an existing entry, or create a new one. */
    public void add(Fluid fluid, double pressure) {
        for (FluidEntry entry : fluids) {
            if (entry.fluid == fluid) {
                entry.pressure += pressure;
                return;
            }
        }
        fluids.add(new FluidEntry(fluid, pressure));
    }

    /** Proportionally reduce all partial pressures. */
    public void reduce(double pressure) {
        double total = getPressure();
        if (pressure >= total) { fluids = new ArrayList<>(); return; }
        double ratio = pressure / total;
        for (FluidEntry e : fluids) e.pressure *= ratio;
    }

    /** Fluid present above 0.1 millibar? */
    public boolean hasFluid(Fluid fluid) { return hasFluid(fluid, 0.0001); }

    public boolean hasFluid(Fluid fluid, double abovePressure) {
        for (FluidEntry e : fluids) if (e.fluid == fluid && e.pressure >= abovePressure) return true;
        return false;
    }

    // --- trait queries (delegated to HbmFluidType) ---

    public boolean hasTrait(Class<? extends IFluidTrait> trait) { return hasTrait(trait, 0.0001); }

    public boolean hasTrait(Class<? extends IFluidTrait> trait, double abovePressure) {
        for (FluidEntry e : fluids) {
            if (e.pressure >= abovePressure
                    && ((HbmFluidType) e.fluid.getFluidType()).hasTrait(trait))
                return true;
        }
        return false;
    }

    /** Highest-pressure fluid. Falls back to NONE if the atmosphere is empty. */
    public Fluid getMainFluid() {
        sortDescending();
        return fluids.isEmpty() ? HBMFluids.NONE.source().get() : fluids.get(0).fluid;
    }

    public void sortDescending() {
        fluids.sort((a, b) -> Double.compare(b.pressure, a.pressure));
    }

    /** Total atmospheric pressure (sum of all partial pressures). */
    public double getPressure() {
        double p = 0;
        for (FluidEntry e : fluids) p += e.pressure;
        return p;
    }

    public double getPressure(Fluid fluid) {
        for (FluidEntry e : fluids) if (e.fluid == fluid) return e.pressure;
        return 0;
    }

    /** Hex colours of all component fluids (for rendering). */
    public List<Integer> getFluidColors() {
        List<Integer> colors = new ArrayList<>();
        for (FluidEntry e : fluids)
            colors.add(((HbmFluidType) e.fluid.getFluidType()).getHbmColor());
        return colors;
    }

    // ======== Serialization ========

    @Override
    public void writeToNBT(CompoundTag nbt) {
        ListTag fluidList = new ListTag();
        for (FluidEntry e : fluids) {
            CompoundTag tag = new CompoundTag();
            tag.putString("type", BuiltInRegistries.FLUID.getKey(e.fluid).toString());
            tag.putDouble("percentage", e.pressure);
            fluidList.add(tag);
        }
        nbt.put("fluids", fluidList);
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
        fluids = new ArrayList<>();
        ListTag fluidList = nbt.getList("fluids", Tag.TAG_COMPOUND);
        for (int i = 0; i < fluidList.size(); i++) {
            CompoundTag tag = fluidList.getCompound(i);
            Fluid fluid = BuiltInRegistries.FLUID.get(
                ResourceLocation.tryParse(tag.getString("type")));
            if (fluid == null) continue;
            fluids.add(new FluidEntry(fluid, tag.getDouble("percentage")));
        }
    }

    @Override
    public void writeToBytes(FriendlyByteBuf buf) {
        buf.writeInt(fluids.size());
        for (FluidEntry e : fluids) {
            buf.writeUtf(BuiltInRegistries.FLUID.getKey(e.fluid).toString());
            buf.writeFloat((float) e.pressure);
        }
    }

    @Override
    public void readFromBytes(FriendlyByteBuf buf) {
        int size = buf.readInt();
        fluids = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Fluid fluid = BuiltInRegistries.FLUID.get(
                ResourceLocation.tryParse(buf.readUtf()));
            if (fluid == null) continue;
            fluids.add(new FluidEntry(fluid, buf.readFloat()));
        }
    }
}
