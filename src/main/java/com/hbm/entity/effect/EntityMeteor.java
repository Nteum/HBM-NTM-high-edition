package com.hbm.entity.effect;

import com.hbm.config.ConfigWorld;
import com.hbm.entity.ModEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class EntityMeteor extends Entity {
    public boolean safe = false;
    public EntityMeteor(Level pLevel) {
        this(ModEntityType.ENTITY_METEOR.get(), pLevel);
    }

    public EntityMeteor(EntityType<EntityMeteor> entityType, Level level) {
        super(entityType, level);
        this.fireImmune();
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            tickCount ++;
            if (ConfigWorld.enableMeteorTails.get()){

            }
        }
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean("safe", safe);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        safe = tag.getBoolean("safe");
    }
}
