package com.hbm.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

/**
 * Triggers the shared atomic explosion routine when the bomb entity dies.
 */
public final class AtomicBombEntityDiesProcedure {

    private AtomicBombEntityDiesProcedure() {
    }

    public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
        AtomicExplosionHelper.detonate(world, x, y, z, entity);
    }
}
