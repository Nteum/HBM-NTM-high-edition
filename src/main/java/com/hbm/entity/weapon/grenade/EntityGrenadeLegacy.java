package com.hbm.entity.weapon.grenade;

import com.hbm.config.ConfigBomb;
import com.hbm.entity.ModEntityType;
import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.hbm.explosion.ExplosionChaos;
import com.hbm.item.weapon.grenade.ItemGrenade;
import com.hbm.registries.HBMDamage;
import com.hbm.registries.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Consumer;

public class EntityGrenadeLegacy extends ThrownGrenade {
    private static final EntityDataAccessor<Integer> DATA_LEGACY_TYPE = SynchedEntityData.defineId(EntityGrenadeLegacy.class, EntityDataSerializers.INT);

    public EntityGrenadeLegacy(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public EntityGrenadeLegacy(double pX, double pY, double pZ, Level pLevel, ItemGrenade.Type grenadeType) {
        super(ModEntityType.ENTITY_GRENADE_LEGACY.get(), pX, pY, pZ, pLevel);
        setGrenadeType(grenadeType);
        setFuse(grenadeType.getLegacyFuseTicks());
    }

    public EntityGrenadeLegacy(LivingEntity pShooter, Level pLevel, ItemGrenade.Type grenadeType) {
        super(ModEntityType.ENTITY_GRENADE_LEGACY.get(), pShooter, pLevel);
        setGrenadeType(grenadeType);
        setFuse(grenadeType.getLegacyFuseTicks());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_LEGACY_TYPE, ItemGrenade.Type.IF_GENERIC.ordinal());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("LegacyGrenadeType")) {
            try {
                setGrenadeType(ItemGrenade.Type.valueOf(pCompound.getString("LegacyGrenadeType")));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putString("LegacyGrenadeType", getGrenadeType().name());
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (!this.level().isClientSide && getGrenadeType().isImpactFuse()) {
            explode();
        }
    }

    @Override
    public double getBounceMod() {
        return getGrenadeType().getLegacyBounceMod();
    }

    @Override
    public int getDefaultFuseTime() {
        return ItemGrenade.Type.IF_GENERIC.getLegacyFuseTicks();
    }

    @Override
    public void explode() {
        if (this.level().isClientSide) {
            return;
        }

        ItemGrenade.Type grenadeType = getGrenadeType();
        Vec3 pos = this.position();
        Entity owner = this.getOwner();

        switch (grenadeType) {
            case TAU -> explodeTau(pos, owner);
            case ASCHRAB -> explodeAntimatter(pos, false);
            case BREACH -> explodeBreach(pos, 2.5F, 8.0F);
            case BURST -> explodeBurst(pos, owner);
            case CLOUD -> explodeCloud(pos, 0xD7E0D7, 4.6F, 220,
                    new MobEffectInstance(MobEffects.CONFUSION, 140, 1),
                    new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 2),
                    new MobEffectInstance(MobEffects.WEAKNESS, 120, 0));
            case CLUSTER -> explodeCluster(pos, owner);
            case ELECTRIC -> explodeElectric(pos, 2.0F, 5.0D, 10.0F, 2, false);
            case FLARE -> explodeFlare(pos);
            case GAS -> explodeGas(pos, false);
            case GASCAN -> explodeGas(pos, true);
            case IF_BOUNCY -> explodeRepulsive(pos, 5.5D, 0.35D, 2.5F);
            case IF_BRIMSTONE -> explodeBrimstone(pos, owner);
            case IF_CONCUSSION -> explodeConcussion(pos, 8.0D, 14.0F, 1.8D);
            case IF_GENERIC -> explodeConventional(pos, 3.0F, false, ExplosionInteraction.TNT, 5.0D, 8.0F, 0.7D, 0, null);
            case IF_HE -> explodeHighExplosive(pos, owner, 5.0F, 14.0F, 18);
            case IF_HOPWIRE -> explodeHopwire(pos);
            case IF_IMPACT -> explodeBreach(pos, 3.2F, 11.0F);
            case IF_INCENDIARY -> explodeIncendiary(pos, 3.5F, 10.0F, 6);
            case IF_MYSTERY -> explodeMystery(false, pos, owner);
            case IF_NULL -> explodeDud(pos);
            case IF_SPARK -> explodeElectric(pos, 1.2F, 4.0D, 7.0F, 1, true);
            case IF_STICKY -> explodeSticky(pos);
            case IF_TOXIC -> explodeToxic(pos);
            case KIT -> explodeRejuvenation(pos);
            case KYIV -> explodeKyiv(pos, owner);
            case LEMON -> explodeLemon(pos);
            case MIRV -> explodeMirv(pos, owner);
            case MK2 -> explodeMk2(pos, owner);
            case NUCLEAR -> explodeNuclear(pos, false);
            case NUKE -> explodeNuclear(pos, true);
            case PINK_CLOUD -> explodePinkCloud(pos);
            case PLASMA -> explodePlasma(pos);
            case POISON -> explodePoison(pos);
            case PULSE -> explodePulse(pos);
            case SCHRABIDIUM -> explodeAntimatter(pos, true);
            case SHRAPNEL -> explodeShrapnel(pos, owner, 34, 14.0F);
            case SMART -> explodeSmart(pos);
            case ZOMG -> explodeMystery(true, pos, owner);
            default -> explodeConventional(pos, 4.0F, false, ExplosionInteraction.TNT, 5.5D, 10.0F, 0.8D, 0, null);
        }

        this.discard();
    }

    private void explodeTau(Vec3 pos, Entity owner) {
        playSound(ModSounds.WEAPON_TAU_SHOOT.get(), pos, 2.2F, 0.92F);
        particleBurst(ParticleTypes.ELECTRIC_SPARK, pos, 75, 1.8D, 0.08D);
        particleBurst(ParticleTypes.CRIT, pos, 80, 1.6D, 0.3D);
        this.level().explode(this, pos.x, pos.y, pos.z, 2.5F, ExplosionInteraction.TNT);
        damageAndPush(pos, 6.0D, 8.0F, damageSource(HBMDamage.TAU_BLAST, owner), 1.2D, 0.25D, 0, null);
    }

    private void explodeAntimatter(Vec3 pos, boolean schrabidium) {
        float power = schrabidium ? 8.5F : 6.0F;
        float damage = schrabidium ? 22.0F : 15.0F;
        double radius = schrabidium ? 8.5D : 6.5D;
        playSound(ModSounds.WEAPON_MUKE_EXPLOSION.get(), pos, schrabidium ? 4.6F : 3.6F, schrabidium ? 0.78F : 0.88F);
        particleBurst(ParticleTypes.REVERSE_PORTAL, pos, schrabidium ? 220 : 150, schrabidium ? 3.6D : 2.6D, 0.15D);
        particleBurst(ParticleTypes.FLASH, pos, schrabidium ? 6 : 4, 0.35D, 0.0D);
        this.level().explode(this, pos.x, pos.y, pos.z, power, true, ExplosionInteraction.BLOCK);
        pullEntities(pos, radius, schrabidium ? 0.22D : 0.16D, false);
        damageAndPush(pos, radius, damage, this.level().damageSources().explosion(null), 1.2D, 0.3D, 0,
                living -> {
                    living.addEffect(new MobEffectInstance(MobEffects.WITHER, schrabidium ? 140 : 90, schrabidium ? 1 : 0));
                    living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 120, 1));
                });
    }

    private void explodeBreach(Vec3 pos, float power, float maxDamage) {
        playSound(ModSounds.WEAPON_EXPLOSION_SMALL_NEAR.get(), pos, 2.1F, 1.05F);
        particleBurst(ParticleTypes.CLOUD, pos, 45, 1.2D, 0.02D);
        particleBurst(ParticleTypes.CRIT, pos, 55, 1.0D, 0.24D);
        this.level().explode(this, pos.x, pos.y, pos.z, power, false, ExplosionInteraction.BLOCK);
        damageAndPush(pos, 4.5D, maxDamage, this.level().damageSources().explosion(null), 0.8D, 0.16D, 0, null);
    }

    private void explodeBurst(Vec3 pos, Entity owner) {
        playSound(ModSounds.WEAPON_EXPLOSION_MEDIUM.get(), pos, 2.5F, 1.0F);
        particleBurst(ParticleTypes.CLOUD, pos, 40, 1.1D, 0.03D);
        for (int i = 0; i < 8; i++) {
            Vec3 offset = randomOffset(pos, 1.8D, 0.4D);
            this.level().explode(this, offset.x, offset.y, offset.z, 2.25F, false, ExplosionInteraction.BLOCK);
        }
        ExplosionChaos.frag(this.level(), (int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z), 12, false, owner);
        damageAndPush(pos, 5.5D, 9.0F, damageSource(HBMDamage.SHRAPNEL, owner), 0.7D, 0.12D, 0, null);
    }

    private void explodeCluster(Vec3 pos, Entity owner) {
        playSound(ModSounds.WEAPON_EXPLOSION_MEDIUM.get(), pos, 2.7F, 0.95F);
        particleBurst(ParticleTypes.EXPLOSION, pos, 26, 1.1D, 0.0D);
        this.level().explode(this, pos.x, pos.y, pos.z, 1.6F, true, ExplosionInteraction.TNT);
        for (int i = 0; i < 6; i++) {
            Vec3 offset = randomOffset(pos, 3.6D, 0.6D);
            this.level().explode(this, offset.x, offset.y, offset.z, 1.75F, false, ExplosionInteraction.TNT);
            particleBurst(ParticleTypes.CRIT, offset, 14, 0.7D, 0.18D);
        }
        ExplosionChaos.frag(this.level(), (int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z), 18, false, owner);
        damageAndPush(pos, 6.0D, 10.0F, damageSource(HBMDamage.SHRAPNEL, owner), 0.75D, 0.14D, 0, null);
    }

    private void explodeElectric(Vec3 pos, float power, double radius, float maxDamage, int bolts, boolean minor) {
        playSound(ModSounds.WEAPON_TESLA_SHOOT.get(), pos, minor ? 1.8F : 2.6F, minor ? 1.2F : 0.95F);
        particleBurst(ParticleTypes.ELECTRIC_SPARK, pos, minor ? 70 : 130, minor ? 1.6D : 2.8D, 0.08D);
        if (power > 0.0F) {
            this.level().explode(this, pos.x, pos.y, pos.z, power, false, ExplosionInteraction.NONE);
        }
        spawnLightning(pos, bolts);
        damageAndPush(pos, radius, maxDamage, damageSource(HBMDamage.ELECTRICITY, this), 0.65D, 0.08D, 0,
                living -> {
                    living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, minor ? 60 : 120, 1));
                    living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, minor ? 80 : 140, minor ? 0 : 1));
                });
    }

    private void explodeFlare(Vec3 pos) {
        playSound(ModSounds.WEAPON_FLAMETHROWER_IGNITE.get(), pos, 1.6F, 1.1F);
        particleBurst(ParticleTypes.FLAME, pos, 120, 1.6D, 0.05D);
        particleBurst(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, pos, 90, 1.8D, 0.03D);
        igniteArea(pos, 4, 0.45F);
        damageAndPush(pos, 4.0D, 4.0F, this.level().damageSources().inFire(), 0.35D, 0.08D, 6, null);
    }

    private void explodeGas(Vec3 pos, boolean canister) {
        playSound(ModSounds.ITEM_SPRAY.get(), pos, canister ? 2.4F : 1.6F, canister ? 0.85F : 1.05F);
        particleBurst(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos, canister ? 110 : 75, canister ? 2.6D : 2.0D, 0.01D);
        particleBurst(ParticleTypes.SPORE_BLOSSOM_AIR, pos, canister ? 95 : 65, canister ? 2.4D : 1.8D, 0.0D);
        if (canister) {
            this.level().explode(this, pos.x, pos.y, pos.z, 4.5F, false, ExplosionInteraction.TNT);
            damageAndPush(pos, 5.5D, 7.0F, this.level().damageSources().explosion(null), 0.55D, 0.1D, 0, null);
        }
        spawnCloud(pos, canister ? 5.5F : 4.0F, canister ? 260 : 180, 0x7CCF4C,
                new MobEffectInstance(MobEffects.CONFUSION, 180, 1),
                new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 180, 1),
                new MobEffectInstance(MobEffects.WEAKNESS, 180, 0),
                new MobEffectInstance(MobEffects.POISON, canister ? 100 : 60, 0));
        damageAndPush(pos, canister ? 5.5D : 4.5D, canister ? 4.0F : 2.0F, this.level().damageSources().magic(), 0.0D, 0.0D, 0, null);
    }

    private void explodeRepulsive(Vec3 pos, double radius, double strength, float damage) {
        playSound(ModSounds.WEAPON_SING_FLYBY.get(), pos, 2.0F, 1.15F);
        particleBurst(ParticleTypes.CLOUD, pos, 80, 2.0D, 0.03D);
        this.level().explode(this, pos.x, pos.y, pos.z, 1.0F, false, ExplosionInteraction.NONE);
        pullEntities(pos, radius, strength, true);
        damageAndPush(pos, radius, damage, this.level().damageSources().explosion(null), 0.25D, 0.2D, 0, null);
    }

    private void explodeBrimstone(Vec3 pos, Entity owner) {
        playSound(ModSounds.WEAPON_FLAMETHROWER_IGNITE.get(), pos, 2.6F, 0.92F);
        particleBurst(ParticleTypes.FLAME, pos, 140, 2.2D, 0.05D);
        this.level().explode(this, pos.x, pos.y, pos.z, 4.0F, true, ExplosionInteraction.TNT);
        ExplosionChaos.frag(this.level(), (int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z), 22, true, owner);
        ExplosionChaos.flameDeath(this.level(), (int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z), 7);
        damageAndPush(pos, 6.5D, 13.0F, damageSource(HBMDamage.SHRAPNEL, owner), 0.9D, 0.15D, 8, null);
    }

    private void explodeConcussion(Vec3 pos, double radius, float maxDamage, double pushStrength) {
        playSound(ModSounds.BLOCK_SONAR_PING.get(), pos, 2.8F, 0.9F);
        particleBurst(ParticleTypes.SONIC_BOOM, pos, 1, 0.1D, 0.0D);
        particleBurst(ParticleTypes.CLOUD, pos, 100, 2.6D, 0.04D);
        this.level().explode(this, pos.x, pos.y, pos.z, 0.8F, false, ExplosionInteraction.NONE);
        pullEntities(pos, radius, pushStrength * 0.12D, true);
        damageAndPush(pos, radius, maxDamage, this.level().damageSources().explosion(null), pushStrength, 0.25D, 0,
                living -> living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 1)));
    }

    private void explodeConventional(Vec3 pos, float power, boolean fire, ExplosionInteraction interaction, double radius, float maxDamage, double knockback, int fireTicks, Consumer<LivingEntity> extra) {
        SoundEvent sound = power >= 6.0F ? ModSounds.WEAPON_EXPLOSION_LARGE_NEAR.get() : power >= 4.0F ? ModSounds.WEAPON_EXPLOSION_MEDIUM.get() : ModSounds.WEAPON_EXPLOSION_SMALL_NEAR.get();
        playSound(sound, pos, power >= 6.0F ? 3.3F : 2.2F, 1.0F);
        particleBurst(ParticleTypes.EXPLOSION, pos, power >= 6.0F ? 30 : 18, 1.1D + power * 0.12D, 0.0D);
        particleBurst(ParticleTypes.SMOKE, pos, 45, 1.4D + power * 0.12D, 0.01D);
        this.level().explode(this, pos.x, pos.y, pos.z, power, fire, interaction);
        damageAndPush(pos, radius, maxDamage, this.level().damageSources().explosion(null), knockback, 0.16D, fireTicks, extra);
    }

    private void explodeHighExplosive(Vec3 pos, Entity owner, float power, float maxDamage, int fragCount) {
        explodeConventional(pos, power, false, ExplosionInteraction.TNT, 7.0D, maxDamage, 1.05D, 0, null);
        ExplosionChaos.frag(this.level(), (int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z), fragCount, false, owner);
        damageAndPush(pos, 7.5D, maxDamage * 0.65F, damageSource(HBMDamage.SHRAPNEL, owner), 0.45D, 0.08D, 0, null);
    }

    private void explodeHopwire(Vec3 pos) {
        playSound(ModSounds.WEAPON_SING_FLYBY.get(), pos, 2.4F, 0.85F);
        particleBurst(ParticleTypes.REVERSE_PORTAL, pos, 110, 2.4D, 0.12D);
        this.level().explode(this, pos.x, pos.y, pos.z, 0.8F, false, ExplosionInteraction.NONE);
        pullEntities(pos, 6.5D, 0.18D, false);
        damageAndPush(pos, 5.5D, 5.0F, this.level().damageSources().magic(), 0.0D, 0.0D, 0,
                living -> living.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 70, 1)));
    }

    private void explodeIncendiary(Vec3 pos, float power, float damage, int fireRadius) {
        explodeConventional(pos, power, true, ExplosionInteraction.TNT, 5.5D, damage, 0.6D, 8, null);
        particleBurst(ParticleTypes.FLAME, pos, 100, 2.0D, 0.05D);
        ExplosionChaos.burn(this.level(), (int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z), fireRadius);
    }

    private void explodeDud(Vec3 pos) {
        playSound(SoundEvents.FIRE_EXTINGUISH, pos, 1.1F, 0.8F);
        particleBurst(ParticleTypes.SMOKE, pos, 22, 0.8D, 0.01D);
    }

    private void explodeSticky(Vec3 pos) {
        playSound(ModSounds.WEAPON_EXPLOSION_MEDIUM.get(), pos, 2.4F, 0.9F);
        particleBurst(ParticleTypes.CLOUD, pos, 55, 1.4D, 0.03D);
        this.level().explode(this, pos.x, pos.y, pos.z, 5.0F, true, ExplosionInteraction.BLOCK);
        damageAndPush(pos, 6.0D, 12.0F, this.level().damageSources().explosion(null), 0.95D, 0.18D, 6, null);
    }

    private void explodeToxic(Vec3 pos) {
        explodeConventional(pos, 2.0F, false, ExplosionInteraction.TNT, 4.5D, 6.0F, 0.4D, 0, null);
        particleBurst(ParticleTypes.MYCELIUM, pos, 65, 1.8D, 0.01D);
        spawnCloud(pos, 4.4F, 220, 0x689E49,
                new MobEffectInstance(MobEffects.POISON, 140, 1),
                new MobEffectInstance(MobEffects.WEAKNESS, 120, 1),
                new MobEffectInstance(MobEffects.HUNGER, 180, 0));
        damageAndPush(pos, 4.8D, 4.5F, this.level().damageSources().magic(), 0.0D, 0.0D, 0, null);
    }

    private void explodeRejuvenation(Vec3 pos) {
        playSound(ModSounds.ITEM_RADAWAY.get(), pos, 1.5F, 1.1F);
        particleBurst(ParticleTypes.HAPPY_VILLAGER, pos, 70, 1.6D, 0.02D);
        spawnCloud(pos, 4.0F, 140, 0x87FF87,
                new MobEffectInstance(MobEffects.REGENERATION, 100, 1),
                new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 80, 0));
        for (LivingEntity living : livingEntities(pos, 5.0D)) {
            living.heal(6.0F);
            living.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 120, 1));
        }
    }

    private void explodeKyiv(Vec3 pos, Entity owner) {
        playSound(ModSounds.BLOCK_SONAR_PING.get(), pos, 2.8F, 1.0F);
        particleBurst(ParticleTypes.SONIC_BOOM, pos, 1, 0.1D, 0.0D);
        explodeHighExplosive(pos, owner, 4.5F, 13.0F, 24);
        pullEntities(pos, 7.0D, 0.12D, true);
    }

    private void explodeLemon(Vec3 pos) {
        explodeConventional(pos, 5.0F, true, ExplosionInteraction.TNT, 6.5D, 14.0F, 0.8D, 10, null);
        particleBurst(ParticleTypes.FLAME, pos, 80, 2.2D, 0.05D);
        ExplosionChaos.flameDeath(this.level(), (int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z), 6);
    }

    private void explodeMirv(Vec3 pos, Entity owner) {
        playSound(ModSounds.WEAPON_EXPLOSION_MEDIUM.get(), pos, 3.0F, 0.92F);
        this.level().explode(this, pos.x, pos.y, pos.z, 2.8F, false, ExplosionInteraction.TNT);
        for (int i = 0; i < 5; i++) {
            Vec3 offset = randomOffset(pos, 5.0D, 0.9D);
            this.level().explode(this, offset.x, offset.y, offset.z, 3.2F, false, ExplosionInteraction.TNT);
            particleBurst(ParticleTypes.EXPLOSION, offset, 8, 0.8D, 0.0D);
        }
        ExplosionChaos.frag(this.level(), (int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z), 20, false, owner);
        damageAndPush(pos, 8.0D, 15.0F, damageSource(HBMDamage.SHRAPNEL, owner), 0.9D, 0.18D, 0, null);
    }

    private void explodeMk2(Vec3 pos, Entity owner) {
        explodeHighExplosive(pos, owner, 7.5F, 20.0F, 28);
    }

    private void explodeNuclear(Vec3 pos, boolean large) {
        int nukeRadius = large ? Math.max(ConfigBomb.fatmanRadius, ConfigBomb.nukaRadius) : ConfigBomb.nukaRadius;
        double radius = large ? Math.max(12.0D, nukeRadius * 0.55D) : Math.max(8.0D, nukeRadius * 0.45D);
        float maxDamage = large ? 28.0F : 18.0F;
        playSound(ModSounds.WEAPON_NUCLEAR_EXPLOSION.get(), pos, large ? 5.0F : 3.6F, large ? 0.78F : 0.88F);
        particleBurst(ParticleTypes.FLASH, pos, large ? 6 : 4, 0.2D, 0.0D);
        particleBurst(ParticleTypes.EXPLOSION_EMITTER, pos, large ? 8 : 5, 0.8D, 0.0D);
        particleBurst(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, pos, large ? 180 : 120, large ? 3.2D : 2.1D, 0.03D);
        if (ConfigBomb.allowNukes) {
            this.level().addFreshEntity(EntityNukeExplosionMK5.statFac(this.level(), nukeRadius, pos));
            this.level().addFreshEntity(new EntityNukeTorex(this.level(), pos.add(0.0D, 4.5D, 0.0D), nukeRadius));
        } else {
            this.level().explode(this, pos.x, pos.y, pos.z, large ? 6.0F : 4.0F, true, ExplosionInteraction.TNT);
        }
        damageAndPush(pos, radius, maxDamage, damageSource(HBMDamage.NUKE, this), 1.15D, 0.25D, 10,
                living -> {
                    living.addEffect(new MobEffectInstance(MobEffects.POISON, large ? 320 : 220, large ? 1 : 0));
                    living.addEffect(new MobEffectInstance(MobEffects.WITHER, large ? 140 : 90, large ? 1 : 0));
                });
    }

    private void explodePinkCloud(Vec3 pos) {
        playSound(ModSounds.ITEM_SPRAY.get(), pos, 1.8F, 1.15F);
        particleBurst(ParticleTypes.DRAGON_BREATH, pos, 110, 2.0D, 0.02D);
        spawnCloud(pos, 4.8F, 240, 0xFF71D1,
                new MobEffectInstance(MobEffects.BLINDNESS, 120, 0),
                new MobEffectInstance(MobEffects.POISON, 120, 0),
                new MobEffectInstance(MobEffects.CONFUSION, 140, 1));
        damageAndPush(pos, 5.0D, 3.5F, this.level().damageSources().magic(), 0.0D, 0.0D, 0, null);
    }

    private void explodePlasma(Vec3 pos) {
        playSound(ModSounds.WEAPON_EXPLOSION_LARGE_NEAR.get(), pos, 3.2F, 1.05F);
        particleBurst(ParticleTypes.FLAME, pos, 150, 2.5D, 0.05D);
        particleBurst(ParticleTypes.LAVA, pos, 40, 1.6D, 0.03D);
        this.level().explode(this, pos.x, pos.y, pos.z, 2.0F, true, ExplosionInteraction.TNT);
        ExplosionChaos.burn(this.level(), (int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z), 7);
        damageAndPush(pos, 6.0D, 12.0F, this.level().damageSources().magic(), 0.55D, 0.12D, 8, null);
    }

    private void explodePoison(Vec3 pos) {
        explodeConventional(pos, 2.0F, false, ExplosionInteraction.TNT, 4.5D, 6.0F, 0.3D, 0, null);
        particleBurst(ParticleTypes.MYCELIUM, pos, 60, 1.5D, 0.01D);
        spawnCloud(pos, 4.6F, 220, 0x7C8B39,
                new MobEffectInstance(MobEffects.POISON, 140, 1),
                new MobEffectInstance(MobEffects.WITHER, 70, 0),
                new MobEffectInstance(MobEffects.HUNGER, 180, 1));
        damageAndPush(pos, 4.5D, 5.0F, this.level().damageSources().magic(), 0.0D, 0.0D, 0, null);
    }

    private void explodePulse(Vec3 pos) {
        playSound(ModSounds.BLOCK_SONAR_PING.get(), pos, 3.0F, 0.85F);
        particleBurst(ParticleTypes.SONIC_BOOM, pos, 1, 0.1D, 0.0D);
        particleBurst(ParticleTypes.ELECTRIC_SPARK, pos, 80, 2.2D, 0.07D);
        this.level().explode(this, pos.x, pos.y, pos.z, 1.4F, false, ExplosionInteraction.NONE);
        pullEntities(pos, 7.5D, 0.24D, true);
        damageAndPush(pos, 7.0D, 9.0F, damageSource(HBMDamage.ELECTRICITY, this), 1.1D, 0.18D, 0,
                living -> {
                    living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0));
                    living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 120, 1));
                });
    }

    private void explodeShrapnel(Vec3 pos, Entity owner, int fragments, float maxDamage) {
        playSound(ModSounds.WEAPON_EXPLOSION_SMALL_NEAR.get(), pos, 2.0F, 1.1F);
        particleBurst(ParticleTypes.CRIT, pos, 150, 2.0D, 0.45D);
        this.level().explode(this, pos.x, pos.y, pos.z, 2.0F, false, ExplosionInteraction.TNT);
        ExplosionChaos.frag(this.level(), (int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z), fragments, false, owner);
        damageAndPush(pos, 7.0D, maxDamage, damageSource(HBMDamage.SHRAPNEL, owner), 0.7D, 0.14D, 0, null);
    }

    private void explodeSmart(Vec3 pos) {
        playSound(ModSounds.WEAPON_SING_FLYBY.get(), pos, 2.0F, 1.05F);
        particleBurst(ParticleTypes.END_ROD, pos, 50, 1.4D, 0.02D);
        pullEntities(pos, 5.5D, 0.12D, false);
        explodeConventional(pos, 4.5F, false, ExplosionInteraction.TNT, 5.5D, 12.0F, 0.85D, 0, null);
    }

    private void explodeCloud(Vec3 pos, int color, float radius, int duration, MobEffectInstance... effects) {
        playSound(ModSounds.ITEM_SPRAY.get(), pos, 1.8F, 1.0F);
        particleBurst(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos, 80, 2.0D, 0.01D);
        spawnCloud(pos, radius, duration, color, effects);
        damageAndPush(pos, radius + 0.3D, 2.5F, this.level().damageSources().magic(), 0.0D, 0.0D, 0, null);
    }

    private void explodeMystery(boolean zomg, Vec3 pos, Entity owner) {
        switch (this.random.nextInt(zomg ? 7 : 6)) {
            case 0 -> explodeElectric(pos, 1.4F, 4.5D, 8.0F, 1, false);
            case 1 -> explodeGas(pos, false);
            case 2 -> explodePulse(pos);
            case 3 -> explodeIncendiary(pos, 3.0F, 9.0F, 5);
            case 4 -> explodePinkCloud(pos);
            case 5 -> explodeCluster(pos, owner);
            default -> explodeAntimatter(pos, false);
        }
        if (zomg) {
            particleBurst(ParticleTypes.FLASH, pos, 3, 0.2D, 0.0D);
        }
    }

    private void spawnCloud(Vec3 pos, float radius, int duration, int color, MobEffectInstance... effects) {
        AreaEffectCloud cloud = new AreaEffectCloud(this.level(), pos.x, pos.y + 0.1D, pos.z);
        cloud.setParticle(ParticleTypes.ENTITY_EFFECT);
        cloud.setFixedColor(color);
        cloud.setRadius(radius);
        cloud.setDuration(duration);
        cloud.setWaitTime(0);
        cloud.setRadiusPerTick(-radius / duration);
        for (MobEffectInstance effect : effects) {
            cloud.addEffect(new MobEffectInstance(effect));
        }
        this.level().addFreshEntity(cloud);
    }

    private void spawnLightning(Vec3 pos, int count) {
        if (!(this.level() instanceof ServerLevel server)) {
            return;
        }
        for (int i = 0; i < count; i++) {
            LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(server);
            if (bolt == null) {
                continue;
            }
            Vec3 offset = i == 0 ? pos : randomOffset(pos, 1.8D, 0.0D);
            bolt.moveTo(offset.x, offset.y, offset.z);
            bolt.setVisualOnly(i > 0);
            server.addFreshEntity(bolt);
        }
    }

    private void particleBurst(ParticleOptions particle, Vec3 pos, int count, double spread, double speed) {
        if (this.level() instanceof ServerLevel server) {
            server.sendParticles(particle, pos.x, pos.y, pos.z, count, spread, spread * 0.45D, spread, speed);
        }
    }

    private void playSound(SoundEvent sound, Vec3 pos, float volume, float pitch) {
        this.level().playSound(null, pos.x, pos.y, pos.z, sound, SoundSource.HOSTILE, volume, pitch);
    }

    private void damageAndPush(Vec3 pos, double radius, float maxDamage, DamageSource source, double knockback, double yBoost, int fireSeconds, Consumer<LivingEntity> extra) {
        for (LivingEntity living : livingEntities(pos, radius)) {
            double distance = living.position().add(0.0D, living.getBbHeight() * 0.5D, 0.0D).distanceTo(pos);
            double factor = Math.max(0.0D, 1.0D - distance / radius);
            if (factor <= 0.0D) {
                continue;
            }
            float damage = (float) Math.max(1.0D, maxDamage * factor);
            living.hurt(source, damage);
            if (knockback > 0.0D) {
                Vec3 push = living.position().subtract(pos);
                if (push.lengthSqr() < 1.0E-5D) {
                    push = new Vec3(0.0D, 1.0D, 0.0D);
                } else {
                    push = push.normalize();
                }
                living.push(push.x * factor * knockback, yBoost + factor * yBoost, push.z * factor * knockback);
                living.hurtMarked = true;
            }
            if (fireSeconds > 0) {
                living.setSecondsOnFire(fireSeconds);
            }
            if (extra != null) {
                extra.accept(living);
            }
        }
    }

    private void pullEntities(Vec3 pos, double radius, double strength, boolean outward) {
        for (LivingEntity living : livingEntities(pos, radius)) {
            Vec3 delta = living.position().add(0.0D, living.getBbHeight() * 0.5D, 0.0D).subtract(pos);
            double distance = delta.length();
            if (distance < 1.0E-4D || distance > radius) {
                continue;
            }
            Vec3 direction = delta.normalize();
            if (!outward) {
                direction = direction.scale(-1.0D);
            }
            double factor = 1.0D - distance / radius;
            living.push(direction.x * strength * factor, 0.04D + strength * 0.1D * factor, direction.z * strength * factor);
            living.hurtMarked = true;
        }
    }

    private void igniteArea(Vec3 pos, int radius, float chance) {
        BlockPos center = BlockPos.containing(pos);
        for (BlockPos target : BlockPos.betweenClosed(center.offset(-radius, -1, -radius), center.offset(radius, 1, radius))) {
            if (center.distSqr(target) > radius * radius) {
                continue;
            }
            if (this.random.nextFloat() > chance) {
                continue;
            }
            if (this.level().getBlockState(target).isAir() && this.level().getBlockState(target.below()).isSolid()) {
                this.level().setBlock(target, Blocks.FIRE.defaultBlockState(), 11);
            }
        }
    }

    private List<LivingEntity> livingEntities(Vec3 pos, double radius) {
        return this.level().getEntitiesOfClass(LivingEntity.class, new AABB(pos, pos).inflate(radius), LivingEntity::isAlive);
    }

    private Vec3 randomOffset(Vec3 pos, double spread, double upSpread) {
        return pos.add(
                (this.random.nextDouble() - 0.5D) * spread,
                upSpread <= 0.0D ? 0.0D : this.random.nextDouble() * upSpread,
                (this.random.nextDouble() - 0.5D) * spread);
    }

    private DamageSource damageSource(ResourceKey<DamageType> key, Entity attacker) {
        return HBMDamage.get(key, this.level().registryAccess(), this, attacker != null ? attacker : this);
    }

    private void setGrenadeType(ItemGrenade.Type grenadeType) {
        this.entityData.set(DATA_LEGACY_TYPE, grenadeType.ordinal());
    }

    private ItemGrenade.Type getGrenadeType() {
        int index = this.entityData.get(DATA_LEGACY_TYPE);
        ItemGrenade.Type[] values = ItemGrenade.Type.values();
        if (index < 0 || index >= values.length) {
            return ItemGrenade.Type.IF_GENERIC;
        }
        return values[index];
    }
}
