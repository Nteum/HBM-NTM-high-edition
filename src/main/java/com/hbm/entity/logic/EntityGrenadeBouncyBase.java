package com.hbm.entity.logic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public abstract class EntityGrenadeBouncyBase extends Projectile {
    //爆炸倒计时
    public static final EntityDataAccessor<Integer> FUSE_TIME = SynchedEntityData.defineId(EntityGrenadeBouncyBase.class, EntityDataSerializers.INT);

    protected EntityGrenadeBouncyBase(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
//        this.xOld -= (double)(Mth.cos(this.yRotO / 180.0F * (float)Math.PI) * 0.16F);
//        this.yOld -= 0.10000000149011612D;
//        this.zOld -= (double)(Mth.sin(this.yRotO / 180.0F * (float)Math.PI) * 0.16F);
//        this.setPos(this.xOld, this.yOld, this.zOld);
//        this.yo = 0.0F;
//        float f = 0.4F;
//        double motionX = (double)(-Mth.sin(this.yRotO / 180.0F * (float)Math.PI) * Mth.cos(this.xRotO / 180.0F * (float)Math.PI) * f);
//        double motionZ = (double)(Mth.cos(this.yRotO / 180.0F * (float)Math.PI) * Mth.cos(this.xRotO / 180.0F * (float)Math.PI) * f);
//        double motionY = (double)(-Mth.sin((this.xRotO) / 180.0F * (float)Math.PI) * f);
//        this.shoot(motionX,motionY,motionZ,1.5F,1.0F);
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        System.out.println("on hit block");
    }

    @Override
    public void tick() {
        super.tick();

        int i = this.getFuse() - 1;
        this.setFuse(i);
        if (i <= 0){
            this.discard();
            if (!this.level().isClientSide){
                this.explode();
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(FUSE_TIME,getMaxTimer());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        pCompound.putInt("Fuse",this.getFuse());
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        this.setFuse(pCompound.getInt("Fuse"));
    }

    @Override
    public boolean shouldRender(double pX, double pY, double pZ) {
        return false;
    }

    public void setFuse(int burn_time){
        this.entityData.set(FUSE_TIME,burn_time);
    }

    public int getFuse(){
        return this.entityData.get(FUSE_TIME);
    }

    public abstract void explode();

    protected abstract int getMaxTimer();

    protected abstract double getBounceMod();
}
