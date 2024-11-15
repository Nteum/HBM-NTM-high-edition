package com.hbm.entity.effect;

import com.hbm.entity.ModEntityType;
import com.hbm.entity.weapon.grenade.ThrownGrenade;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class EntityBlackHole extends Entity {
    private static final EntityDataAccessor<Float> DATA_SIZE = SynchedEntityData.defineId(EntityBlackHole.class, EntityDataSerializers.FLOAT);
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
        if (++count > 1200){
            this.discard();
        }
//        Float size = this.getEntityData().get(DATA_SIZE);

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
