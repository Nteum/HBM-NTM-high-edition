package com.hbm.procedures;

import com.hbm.compat.bigexplosives.BigExplosivesMod;
import com.hbm.render.entity.FiveHundredKgExplosionEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

/**
 * Ensures the lingering explosion despawns after its animation completes.
 */
public final class FiveHundredKgExplosionOnInitialEntitySpawnProcedure {

    private FiveHundredKgExplosionOnInitialEntitySpawnProcedure() {
    }

    public static void execute(LevelAccessor world, Entity entity) {
        if (world == null || entity == null) {
            return;
        }
        BigExplosivesMod.queueServerWork(FiveHundredKgExplosionEntity.LIFETIME_TICKS, () -> {
            if (entity.isAlive() && !entity.level().isClientSide()) {
                entity.discard();
            }
        });
    }
}
