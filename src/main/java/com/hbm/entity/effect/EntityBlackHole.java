package com.hbm.entity.effect;

import com.hbm.entity.ModEntityType;
import com.hbm.registries.HBMDamage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class EntityBlackHole extends Entity {
    private static final EntityDataAccessor<Float> DATA_SIZE = SynchedEntityData.defineId(EntityBlackHole.class, EntityDataSerializers.FLOAT);
    private static final int MAX_LIFETIME_TICKS = 20 * 60;
    private static final int CHIP_DAMAGE_INTERVAL = 10;
    private int count = 0;
    public EntityBlackHole(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public EntityBlackHole(Level pLevel) {
        this(ModEntityType.ENTITY_BLACK_HOLE.get(), pLevel);
    }
    public EntityBlackHole(Level pLevel, float size) {
        this(ModEntityType.ENTITY_BLACK_HOLE.get(), pLevel);
        this.getEntityData().set(DATA_SIZE, size);
    }

    @Override
    public void tick() {
        super.tick();
        if (++count > MAX_LIFETIME_TICKS){
            this.discard();
            return;
        }
        if (this.level().isClientSide) {
            return;
        }

        float size = Math.max(0.5F, this.getSize());
        double pullRadius = 2.0D + size * 6.0D;
        Vec3 center = this.position();
        AABB bounds = new AABB(center, center).inflate(pullRadius);
        List<Entity> targets = this.level().getEntities(this, bounds, entity -> entity.isAlive() && !entity.isSpectator());

        for (Entity target : targets) {
            if (target == this) {
                continue;
            }
            if (target instanceof Player player && player.isCreative()) {
                continue;
            }

            Vec3 targetCenter = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
            Vec3 toCenter = center.subtract(targetCenter);
            double distance = toCenter.length();
            if (distance < 1.0E-4D || distance > pullRadius) {
                continue;
            }

            double strengthFactor = 1.0D - distance / pullRadius;
            double pullStrength = (0.08D + size * 0.04D) * strengthFactor * strengthFactor;
            target.setDeltaMovement(target.getDeltaMovement().add(toCenter.normalize().scale(pullStrength)));
            target.hurtMarked = true;

            if (distance < Math.max(1.0D, size * 1.2D)) {
                target.hurt(HBMDamage.get(HBMDamage.BLACKHOLE, this.level().registryAccess(), this, this), 2.0F + size * 1.5F);
            } else if (count % CHIP_DAMAGE_INTERVAL == 0 && target instanceof LivingEntity) {
                target.hurt(HBMDamage.get(HBMDamage.BLACKHOLE, this.level().registryAccess(), this, this), 1.0F);
            }

            if (distance < 0.65D && (target instanceof Projectile || target instanceof ItemEntity)) {
                target.discard();
            }
        }

    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_SIZE,0.5F);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        this.getEntityData().set(DATA_SIZE, pCompound.getFloat("size"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putFloat("size", this.getEntityData().get(DATA_SIZE));
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return pDistance < 25000;
    }

    public float getSize(){
        return this.getEntityData().get(DATA_SIZE);
    }
}
