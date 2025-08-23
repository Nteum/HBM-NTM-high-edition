package com.hbm.entity.projectile;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class EntityThrowableNT extends ThrowableProjectile {
    public int ticksInGround;
    protected EntityThrowableNT(EntityType<? extends ThrowableProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public EntityThrowableNT(EntityType<? extends ThrowableProjectile> pEntityType, Level level, LivingEntity thrower){
        this(pEntityType,level);
        this.setOwner(thrower);
        this.setBoundingBox(setSize(0.25,0.25));
        double deltaX = Mth.cos(this.getXRot() * ((float) Math.PI / 180F) * 0.16F);
        double deltaY = 0.1D;
        double deltaZ = Mth.sin(this.getXRot() * ((float) Math.PI / 180F) * 0.16F);
        this.position().subtract(deltaX,deltaY,deltaZ);
        this.yo = 0.0F;
        float velocity = 0.4F;
        this.shootFromRotation(getOwner(),1.0F,1.0F,1.0F,throwForce(), (float) (0.0172275D / headingForceMult()));
        ticksInGround = 0;
    }
    public EntityThrowableNT(EntityType<? extends ThrowableProjectile> pEntityType, Level level, double x, double y, double z){
        this(pEntityType,level);
        this.setPos(x,y,z);
        this.setBoundingBox(setSize(0.25,0.25));
        this.yo = 0.0F;
        this.ticksInGround = 0;
    }

    protected float throwForce() {
        return 1.5F;
    }

    protected double headingForceMult() {
        return 0.0075D;
    }

    protected float throwAngle() {
        return 0.0F;
    }

    protected double motionMult() {
        return 1.0D;
    }

    private AABB setSize(double width, double height){
        return AABB.ofSize(new Vec3(0.5,0.5,0.5),width,height,width);
    }

    @Override
    public void setDeltaMovement(double pX, double pY, double pZ) {
        super.setDeltaMovement(pX, pY, pZ);

        if (this.yRotO == 0 && this.xRotO == 0){
            float hyp = Mth.sqrt((float) (pX * pX + pZ * pZ));
            this.yRotO = (float) (Math.atan2(pX,pZ) * 180.0D / Math.PI);
            this.setYRot(yRotO);
            this.xRotO = (float) (Math.atan2(pY, (double) hyp) * 180.0D / Math.PI);
            this.setXRot(xRotO);
        }
    }

    public boolean doesImpactEntities() {
        return true;
    }

    public boolean doesPenetrate() {
        return false;
    }

    public boolean isSpectral() {
        return false;
    }

    public int selfDamageDelay() {
        return 5;
    }

    /* ================================== Additional Getters =====================================*/

    protected float getAirDrag() {
        return 0.99F;
    }

    protected float getWaterDrag() {
        return 0.8F;
    }

    protected int groundDespawn() {
        return 1200;
    }
}
