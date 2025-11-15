package com.hbm.procedures;

import com.hbm.compat.bigexplosives.BigExplosivesMod;
import com.hbm.init.BigExplosivesModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

/**
 * Spawns the lingering 500kg explosion entity shortly after the Five Bomb dies.
 */
public final class FiveBombEntityDiesProcedure {

    private static final int SPAWN_DELAY_TICKS = 2;

    private FiveBombEntityDiesProcedure() {
    }

    public static void execute(LevelAccessor world, double x, double y, double z) {
        BigExplosivesMod.queueServerWork(SPAWN_DELAY_TICKS, () -> {
            if (!(world instanceof ServerLevel serverLevel)) {
                return;
            }
            Entity explosion = BigExplosivesModEntities.FIVE_HUNDRED_KG_EXPLOSION.get().create(serverLevel);
            if (explosion == null) {
                return;
            }
            explosion.moveTo(x, y, z, explosion.getYRot(), explosion.getXRot());
            explosion.setDeltaMovement(0.0D, 0.0D, 0.0D);
            serverLevel.addFreshEntity(explosion);
        });
    }
}
