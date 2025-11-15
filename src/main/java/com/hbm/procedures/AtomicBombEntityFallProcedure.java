package com.hbm.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

/**
 * Applies a flat burst of damage whenever the atomic bomb entity experiences a
 * fall tick. The original generated code used obfuscated helper calls; this
 * keeps the behaviour but expresses it using stable Mojang names.
 */
public final class AtomicBombEntityFallProcedure {

    private static final float DAMAGE = 30.0F;

    private AtomicBombEntityFallProcedure() {
    }

    public static void execute(LevelAccessor world, Entity entity) {
        if (world == null || entity == null) {
            return;
        }
        entity.hurt(entity.damageSources().generic(), DAMAGE);
    }
}
