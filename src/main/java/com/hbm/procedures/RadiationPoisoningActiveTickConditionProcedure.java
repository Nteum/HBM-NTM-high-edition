package net.mcreator.nuclearcraft.procedures;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/procedures/RadiationPoisoningActiveTickConditionProcedure.class */
public class RadiationPoisoningActiveTickConditionProcedure {
    public static void execute(LevelAccessor world, Entity entity) {
        if (entity == null) {
            return;
        }
        if (Math.random() < 1.0d) {
            if (entity instanceof ServerPlayer) {
                ServerPlayer _player = (ServerPlayer) entity;
                Advancement _adv = _player.f_8924_.m_129889_().m_136041_(new ResourceLocation("big_explosives:acute_radiation_syndrome"));
                AdvancementProgress _ap = _player.m_8960_().m_135996_(_adv);
                if (!_ap.m_8193_()) {
                    for (String criteria : _ap.m_8219_()) {
                        _player.m_8960_().m_135988_(_adv, criteria);
                    }
                }
            }
            if (entity instanceof LivingEntity) {
                LivingEntity _entity = (LivingEntity) entity;
                if (!_entity.m_9236_().m_5776_()) {
                    _entity.m_7292_(new MobEffectInstance(MobEffects.f_19619_, 200, 0));
                }
            }
            if (entity instanceof Player) {
                ((Player) entity).m_36399_(0.01f);
            }
        }
        if (Math.random() < 0.005d && (entity instanceof LivingEntity)) {
            LivingEntity _entity2 = (LivingEntity) entity;
            if (!_entity2.m_9236_().m_5776_()) {
                _entity2.m_7292_(new MobEffectInstance(MobEffects.f_19614_, (int) Mth.m_216263_(RandomSource.m_216327_(), 60.0d, 120.0d), 0, false, false));
            }
        }
        if (Math.random() < 5.0E-4d) {
            entity.m_6469_(new DamageSource(world.m_9598_().m_175515_(Registries.f_268580_).m_246971_(ResourceKey.m_135785_(Registries.f_268580_, new ResourceLocation("big_explosives:radiation")))), 4.0f);
        }
        if (Math.random() < 0.01d && (entity instanceof LivingEntity)) {
            LivingEntity _entity3 = (LivingEntity) entity;
            if (!_entity3.m_9236_().m_5776_()) {
                _entity3.m_7292_(new MobEffectInstance(MobEffects.f_19604_, (int) Mth.m_216263_(RandomSource.m_216327_(), 100.0d, 200.0d), 0, false, false));
            }
        }
        if (Math.random() < 0.002d && (entity instanceof LivingEntity)) {
            LivingEntity _entity4 = (LivingEntity) entity;
            if (!_entity4.m_9236_().m_5776_()) {
                _entity4.m_7292_(new MobEffectInstance(MobEffects.f_19610_, (int) Mth.m_216263_(RandomSource.m_216327_(), 150.0d, 250.0d), 0, false, false));
            }
        }
        if (Math.random() >= 0.005d || !(entity instanceof LivingEntity)) {
            return;
        }
        LivingEntity _entity5 = (LivingEntity) entity;
        if (!_entity5.m_9236_().m_5776_()) {
            _entity5.m_7292_(new MobEffectInstance(MobEffects.f_19613_, (int) Mth.m_216263_(RandomSource.m_216327_(), 100.0d, 200.0d), 0, false, false));
        }
    }
}
