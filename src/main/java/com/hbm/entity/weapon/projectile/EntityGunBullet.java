package com.hbm.entity.weapon.projectile;

import com.hbm.entity.ModEntityType;
import com.hbm.registries.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class EntityGunBullet extends Projectile {
    private static final int MAX_LIFE_TICKS = 50;
    private static final double AIR_DRAG = 0.995D;

    private float damage = 7.0F;
    private int lifeTicks;

    public EntityGunBullet(EntityType<? extends EntityGunBullet> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noCulling = true;
    }

    public EntityGunBullet(Level level, LivingEntity shooter, float damage) {
        this(ModEntityType.ENTITY_GUN_BULLET.get(), level);
        this.setOwner(shooter);
        this.damage = damage;
        this.setPos(shooter.getX(), shooter.getEyeY() - 0.1D, shooter.getZ());
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void tick() {
        super.tick();

        if (this.lifeTicks++ > MAX_LIFE_TICKS) {
            this.discard();
            return;
        }

        Vec3 motion = this.getDeltaMovement();
        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);

        if (hitResult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitResult)) {
            this.onHit(hitResult);
            if (!this.isAlive()) {
                return;
            }
        }

        this.setPos(this.getX() + motion.x, this.getY() + motion.y, this.getZ() + motion.z);
        this.updateRotationFromMotion();

        if (this.level() instanceof ServerLevel server && (this.tickCount % 2 == 0)) {
            server.sendParticles(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 1, 0.005D, 0.005D, 0.005D, 0.0D);
        }

        this.setDeltaMovement(motion.scale(AIR_DRAG));

        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
        }

        this.checkInsideBlocks();
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        Entity target = pResult.getEntity();
        Entity owner = this.getOwner();
        if (!this.level().isClientSide) {
            target.hurt(this.damageSources().thrown(this, owner), this.damage);
            Vec3 knockback = this.getDeltaMovement().normalize().scale(0.35D);
            target.push(knockback.x, 0.02D + Mth.clamp(knockback.y, 0.0D, 0.2D), knockback.z);
            target.hurtMarked = true;
        }
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        if (this.level() instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 4, 0.04D, 0.04D, 0.04D, 0.0D);
        }
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                ModSounds.WEAPON_RICOCHET.get(), SoundSource.PLAYERS, 0.6F, 1.2F + this.random.nextFloat() * 0.3F);
        this.discard();
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return pDistance < 4096.0D;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        this.damage = pCompound.getFloat("damage");
        this.lifeTicks = pCompound.getInt("lifeTicks");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putFloat("damage", this.damage);
        pCompound.putInt("lifeTicks", this.lifeTicks);
    }

    private void updateRotationFromMotion() {
        Vec3 vec = this.getDeltaMovement();
        if (vec.lengthSqr() < 1.0E-7D) {
            return;
        }
        float horizontal = Mth.sqrt((float) (vec.x * vec.x + vec.z * vec.z));
        this.setYRot((float) (Mth.atan2(vec.x, vec.z) * (180F / Math.PI)));
        this.setXRot((float) (Mth.atan2(vec.y, horizontal) * (180F / Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }
}
