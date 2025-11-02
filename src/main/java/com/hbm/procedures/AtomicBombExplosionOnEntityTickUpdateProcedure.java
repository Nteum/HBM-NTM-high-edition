package net.mcreator.nuclearcraft.procedures;

import net.mcreator.nuclearcraft.BigExplosivesMod;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/procedures/AtomicBombExplosionOnEntityTickUpdateProcedure.class */
public class AtomicBombExplosionOnEntityTickUpdateProcedure {
    public static void execute(LevelAccessor world, Entity entity) {
        if (entity == null) {
            return;
        }
        if (entity instanceof LivingEntity) {
            LivingEntity _entity = (LivingEntity) entity;
            _entity.m_21219_();
        }
        BigExplosivesMod.queueServerWork(380, () -> {
            if (entity.m_6084_()) {
                if (!entity.m_9236_().m_5776_()) {
                    entity.m_146870_();
                }
                if (entity instanceof LivingEntity) {
                    LivingEntity _entity2 = (LivingEntity) entity;
                    _entity2.m_21219_();
                }
            }
        });
        if (entity instanceof LivingEntity) {
            LivingEntity _entity2 = (LivingEntity) entity;
            _entity2.m_21219_();
        }
    }
}
