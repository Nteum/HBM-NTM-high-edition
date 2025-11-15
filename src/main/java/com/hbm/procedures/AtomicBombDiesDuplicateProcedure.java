package com.hbm.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

/**
 * Legacy duplicate hook routed to the shared atomic explosion implementation.
 */
public final class AtomicBombDiesDuplicateProcedure {

    private AtomicBombDiesDuplicateProcedure() {
    }

    public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
        AtomicExplosionHelper.detonate(world, x, y, z, entity);
    }
}
