package net.mcreator.nuclearcraft.procedures;

import net.mcreator.nuclearcraft.init.BigExplosivesModMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/procedures/RadiationImmunityOnEffectActiveTickProcedure.class */
public class RadiationImmunityOnEffectActiveTickProcedure {
    public static void execute(Entity entity) {
        if (entity != null && (entity instanceof LivingEntity)) {
            LivingEntity _entity = (LivingEntity) entity;
            _entity.m_21195_((MobEffect) BigExplosivesModMobEffects.RADIATION_POISONING.get());
        }
    }
}
