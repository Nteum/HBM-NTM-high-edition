package net.mcreator.nuclearcraft.procedures;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/procedures/DamageTestPlayerCollidesWithThisEntityProcedure.class */
public class DamageTestPlayerCollidesWithThisEntityProcedure {
    public static void execute(LevelAccessor world, Entity entity) {
        if (entity == null) {
            return;
        }
        for (int index0 = 0; index0 < 100; index0++) {
            entity.m_6469_(new DamageSource(world.m_9598_().m_175515_(Registries.f_268580_).m_246971_(DamageTypes.f_268433_)), 1.0f);
        }
    }
}
