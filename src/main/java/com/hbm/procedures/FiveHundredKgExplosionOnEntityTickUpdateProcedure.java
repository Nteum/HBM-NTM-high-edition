package net.mcreator.nuclearcraft.procedures;

import net.mcreator.nuclearcraft.init.BigExplosivesModParticleTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/procedures/FiveHundredKgExplosionOnEntityTickUpdateProcedure.class */
public class FiveHundredKgExplosionOnEntityTickUpdateProcedure {
    public static void execute(LevelAccessor world, double x, double y, double z) {
        for (int index0 = 0; index0 < 3; index0++) {
            if (world instanceof ServerLevel) {
                ServerLevel _level = (ServerLevel) world;
                _level.m_8767_((SimpleParticleType) BigExplosivesModParticleTypes.SMOKE.get(), x, y, z, 50, 4.0d, 4.0d, 4.0d, 0.3d);
            }
            if (world instanceof ServerLevel) {
                ServerLevel _level2 = (ServerLevel) world;
                _level2.m_8767_(ParticleTypes.f_123778_, x, y, z, 50, 4.0d, 4.0d, 4.0d, 0.3d);
            }
        }
    }
}
