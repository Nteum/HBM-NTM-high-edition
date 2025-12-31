package com.hbm.utils.transport_net;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Extremely lightweight representation of a pipe graph. We only need to know which
 * transmitters participate and the fluid bound to the network. All topology-aware
 * operations happen inside {@link FluidNetworkSystem}.
 */
public class FluidNetwork {

    private final Set<BlockPos> transmitters = new HashSet<>();
    private Fluid fluid = Fluids.EMPTY;

    public FluidNetwork() {
    }

    public Fluid getFluid() {
        return fluid;
    }

    public void setFluid(final Fluid fluid) {
        this.fluid = fluid == null ? Fluids.EMPTY : fluid;
    }

    public void addTransmitter(final BlockPos pos) {
        transmitters.add(pos.immutable());
    }

    public void removeTransmitter(final BlockPos pos) {
        transmitters.remove(pos);
    }

    public boolean isEmpty() {
        return transmitters.isEmpty();
    }

    public boolean contains(final BlockPos pos) {
        return transmitters.contains(pos);
    }

    public Set<BlockPos> getTransmitters() {
        return Collections.unmodifiableSet(transmitters);
    }

    public void absorb(final FluidNetwork other) {
        if (other == null) {
            return;
        }
        other.transmitters.forEach(transmitters::add);
        if (this.fluid == Fluids.EMPTY) {
            this.fluid = other.fluid;
        }
    }

    public void replaceTransmitters(final Set<BlockPos> newMembers) {
        transmitters.clear();
        if (newMembers != null) {
            newMembers.stream().filter(Objects::nonNull).forEach(pos -> transmitters.add(pos.immutable()));
        }
    }

    public boolean canMergeWith(final FluidNetwork other) {
        if (other == null) {
            return false;
        }
        if (this.fluid == Fluids.EMPTY || other.fluid == Fluids.EMPTY) {
            return true;
        }
        return this.fluid == other.fluid;
    }

    public void tick() {
        // Placeholder for future fluid balancing. The Stage 2 goal is stability, so the
        // manager simply keeps networks coherent while higher-level handlers push/pull.
    }
}
