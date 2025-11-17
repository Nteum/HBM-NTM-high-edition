package com.hbm.procedures;

import com.hbm.compat.bigexplosives.BigExplosivesMod;
import com.hbm.render.entity.AtomicBombExplosionEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;

/**
 * Keeps the lingering explosion entity around long enough for its animation to
 * play, then discards it on the server. While alive we continually purge any
 * potion effects so shader mods cannot tint the flash.
 */
public final class AtomicBombExplosionOnEntityTickUpdateProcedure {

    private AtomicBombExplosionOnEntityTickUpdateProcedure() {
    }

    public static void execute(LevelAccessor world, Entity entity) {
        if (world == null || entity == null) {
            return;
        }

        if (entity instanceof LivingEntity living) {
            living.removeAllEffects();
        }

        BigExplosivesMod.queueServerWork(AtomicBombExplosionEntity.LIFETIME_TICKS, () -> {
            if (!entity.isAlive()) {
                return;
            }
            if (!entity.level().isClientSide()) {
                entity.discard();
            }
            if (entity instanceof LivingEntity living) {
                living.removeAllEffects();
            }
        });
    }
}
