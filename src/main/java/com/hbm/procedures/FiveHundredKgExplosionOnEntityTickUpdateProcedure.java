package com.hbm.procedures;

import com.hbm.init.BigExplosivesModParticleTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;

/**
 * Emits smoke and explosion particles while the lingering explosion entity is
 * alive.
 */
public final class FiveHundredKgExplosionOnEntityTickUpdateProcedure {

    private FiveHundredKgExplosionOnEntityTickUpdateProcedure() {
    }

    public static void execute(LevelAccessor world, double x, double y, double z) {
        if (!(world instanceof ServerLevel serverLevel)) {
            return;
        }
        for (int i = 0; i < 3; i++) {
            serverLevel.sendParticles(BigExplosivesModParticleTypes.SMOKE.get(), x, y, z, 50, 4.0D, 4.0D, 4.0D, 0.3D);
            serverLevel.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 50, 4.0D, 4.0D, 4.0D, 0.3D);
        }
    }
}
