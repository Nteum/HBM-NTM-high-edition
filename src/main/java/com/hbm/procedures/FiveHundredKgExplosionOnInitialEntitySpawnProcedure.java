package net.mcreator.nuclearcraft.procedures;

import net.mcreator.nuclearcraft.BigExplosivesMod;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/procedures/FiveHundredKgExplosionOnInitialEntitySpawnProcedure.class */
public class FiveHundredKgExplosionOnInitialEntitySpawnProcedure {
    public static void execute(LevelAccessor world, Entity entity) {
        if (entity == null) {
            return;
        }
        BigExplosivesMod.queueServerWork(90, () -> {
            if (entity.m_6084_() && !entity.m_9236_().m_5776_()) {
                entity.m_146870_();
            }
        });
    }
}
