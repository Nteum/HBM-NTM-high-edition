package com.hbm.reactor.pile;

import com.hbm.reactor.pile.NeutronNodeWorld.StreamWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Represents a single neutron ray travelling through the pile. Streams are
 * short lived and consumed during the same tick they are spawned.
 */
public abstract class NeutronStream {

    protected final NeutronNode origin;
    protected final Vec3 direction;
    protected double fluxQuantity;

    protected NeutronStream(NeutronNode origin, Vec3 direction, double fluxQuantity) {
        this.origin = origin;
        this.direction = direction;
        this.fluxQuantity = fluxQuantity;
    }

    /**
     * Executes the stream logic and applies flux to all receivers along
     * the ray path.
     */
    public abstract void run(Level level, StreamWorld streamWorld);

    protected BlockPos originPos() {
        return origin.pos();
    }

    protected boolean isDead() {
        return fluxQuantity <= 0.0D;
    }
}
