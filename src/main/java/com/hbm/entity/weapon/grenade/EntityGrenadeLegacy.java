package com.hbm.entity.weapon.grenade;

import com.hbm.compat.ballistix.BallistixExplosiveType;
import com.hbm.compat.ballistix.BallistixExplosionHandlers;
import com.hbm.entity.ModEntityType;
import com.hbm.item.weapon.grenade.ItemGrenade;
import com.hbm.registries.HBMDamage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

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
            case IF_NULL -> this.level().playSound(null, pos.x, pos.y, pos.z, SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 1.1F, 0.8F);
            case IF_MYSTERY, ZOMG -> explodeMystery(grenadeType, pos, owner);
            default -> {
                if (!explodeMapped(grenadeType, pos, owner)) {
                    this.level().explode(this, pos.x, pos.y, pos.z, 4.0F, Level.ExplosionInteraction.TNT);
                }
            }
        }

        this.discard();
    }

    private void explodeTau(Vec3 pos, Entity owner) {
        this.level().explode(this, pos.x, pos.y, pos.z, 2.5F, Level.ExplosionInteraction.TNT);
        List<LivingEntity> victims = this.level().getEntitiesOfClass(LivingEntity.class, new AABB(pos, pos).inflate(6.0D), LivingEntity::isAlive);
        Entity attacker = owner != null ? owner : this;
        for (LivingEntity living : victims) {
            double distance = living.position().distanceTo(pos);
            double factor = Math.max(0.15D, 1.0D - distance / 6.0D);
            living.hurt(HBMDamage.get(HBMDamage.TAU_BLAST, this.level().registryAccess(), this, attacker), (float) (8.0D * factor));
            Vec3 push = living.position().subtract(pos);
            if (push.lengthSqr() < 1.0E-5D) {
                push = new Vec3(0.0D, 1.0D, 0.0D);
            } else {
                push = push.normalize();
            }
            living.push(push.x * factor * 1.2D, 0.2D + factor * 0.25D, push.z * factor * 1.2D);
            living.hurtMarked = true;
        }
    }

    private void explodeMystery(ItemGrenade.Type grenadeType, Vec3 pos, Entity owner) {
        BallistixExplosiveType[] pool = new BallistixExplosiveType[] {
                BallistixExplosiveType.CONDENSIVE,
                BallistixExplosiveType.INCENDIARY,
                BallistixExplosiveType.CHEMICAL,
                BallistixExplosiveType.EMP,
                BallistixExplosiveType.SONIC,
                BallistixExplosiveType.ANTIGRAVITY,
                BallistixExplosiveType.HYPERSONIC
        };
        BallistixExplosionHandlers.detonate(pool[this.random.nextInt(pool.length)], this.level(), pos, owner);
        if (grenadeType == ItemGrenade.Type.ZOMG && this.random.nextFloat() < 0.35F) {
            BallistixExplosionHandlers.detonate(BallistixExplosiveType.ANTIMATTER, this.level(), pos, owner);
        }
    }

    private boolean explodeMapped(ItemGrenade.Type grenadeType, Vec3 pos, Entity owner) {
        return switch (grenadeType) {
            case ASCHRAB -> detonate(BallistixExplosiveType.ANTIMATTER, pos, owner);
            case SCHRABIDIUM -> detonate(BallistixExplosiveType.LARGE_ANTIMATTER, pos, owner);
            case BREACH, IF_IMPACT -> detonate(BallistixExplosiveType.BREACHING, pos, owner);
            case BURST -> detonate(BallistixExplosiveType.FRAGMENTATION, pos, owner);
            case MIRV -> {
                detonate(BallistixExplosiveType.FRAGMENTATION, pos, owner);
                scatterSubmunitions(BallistixExplosiveType.CONDENSIVE, pos, owner, 3, 5.0D);
                yield true;
            }
            case CLOUD -> detonate(BallistixExplosiveType.DEBILITATION, pos, owner);
            case CLUSTER -> {
                detonate(BallistixExplosiveType.FRAGMENTATION, pos, owner);
                scatterSubmunitions(BallistixExplosiveType.SHRAPNEL, pos, owner, 2, 4.0D);
                yield true;
            }
            case ELECTRIC, IF_SPARK, PULSE -> detonate(BallistixExplosiveType.EMP, pos, owner);
            case FLARE, IF_INCENDIARY -> detonate(BallistixExplosiveType.INCENDIARY, pos, owner);
            case GAS -> detonate(BallistixExplosiveType.CHEMICAL, pos, owner);
            case GASCAN -> {
                detonate(BallistixExplosiveType.CHEMICAL, pos, owner);
                detonate(BallistixExplosiveType.CONTAGIOUS, pos, owner);
                yield true;
            }
            case IF_BOUNCY -> detonate(BallistixExplosiveType.REPULSIVE, pos, owner);
            case IF_BRIMSTONE, PLASMA -> detonate(BallistixExplosiveType.EXOTHERMIC, pos, owner);
            case IF_CONCUSSION -> detonate(BallistixExplosiveType.SONIC, pos, owner);
            case IF_GENERIC -> detonate(BallistixExplosiveType.CONDENSIVE, pos, owner);
            case IF_HE -> detonate(BallistixExplosiveType.THERMOBARIC, pos, owner);
            case IF_HOPWIRE -> detonate(BallistixExplosiveType.ANTIGRAVITY, pos, owner);
            case IF_STICKY -> detonate(BallistixExplosiveType.LANDMINE, pos, owner);
            case IF_TOXIC -> detonate(BallistixExplosiveType.CONTAGIOUS, pos, owner);
            case KIT -> detonate(BallistixExplosiveType.REJUVINATION, pos, owner);
            case KYIV -> {
                detonate(BallistixExplosiveType.HYPERSONIC, pos, owner);
                detonate(BallistixExplosiveType.SHRAPNEL, pos, owner);
                yield true;
            }
            case LEMON -> {
                detonate(BallistixExplosiveType.INCENDIARY, pos, owner);
                detonate(BallistixExplosiveType.DEBILITATION, pos, owner);
                yield true;
            }
            case MK2 -> {
                detonate(BallistixExplosiveType.THERMOBARIC, pos, owner);
                this.level().explode(this, pos.x, pos.y, pos.z, 6.5F, Level.ExplosionInteraction.TNT);
                yield true;
            }
            case NUCLEAR -> detonate(BallistixExplosiveType.NUCLEAR, pos, owner);
            case NUKE -> {
                detonate(BallistixExplosiveType.NUCLEAR, pos, owner);
                detonate(BallistixExplosiveType.BREACHING, pos, owner);
                yield true;
            }
            case PINK_CLOUD -> {
                detonate(BallistixExplosiveType.ENDER, pos, owner);
                detonate(BallistixExplosiveType.CONTAGIOUS, pos, owner);
                yield true;
            }
            case POISON -> {
                detonate(BallistixExplosiveType.CONTAGIOUS, pos, owner);
                detonate(BallistixExplosiveType.CHEMICAL, pos, owner);
                yield true;
            }
            case SHRAPNEL -> detonate(BallistixExplosiveType.SHRAPNEL, pos, owner);
            case SMART -> {
                detonate(BallistixExplosiveType.ATTRACTIVE, pos, owner);
                detonate(BallistixExplosiveType.HYPERSONIC, pos, owner);
                yield true;
            }
            default -> false;
        };
    }

    private boolean detonate(BallistixExplosiveType explosiveType, Vec3 pos, Entity owner) {
        BallistixExplosionHandlers.detonate(explosiveType, this.level(), pos, owner);
        return true;
    }

    private void scatterSubmunitions(BallistixExplosiveType explosiveType, Vec3 pos, Entity owner, int count, double spread) {
        for (int i = 0; i < count; i++) {
            Vec3 offset = pos.add(
                    (this.random.nextDouble() - 0.5D) * spread,
                    0.05D + this.random.nextDouble() * 0.35D,
                    (this.random.nextDouble() - 0.5D) * spread);
            BallistixExplosionHandlers.detonate(explosiveType, this.level(), offset, owner);
        }
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
