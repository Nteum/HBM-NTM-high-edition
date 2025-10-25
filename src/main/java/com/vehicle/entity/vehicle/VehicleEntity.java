package com.vehicle.entity.vehicle;

import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;

public class VehicleEntity extends Entity {
//    // 数据同步参数
//    private static final EntityDataAccessor<Float> ROTOR_SPEED = SynchedEntityData.defineId(HelicopterEntity.class, EntityDataSerializers.FLOAT);
//    private static final EntityDataAccessor<Float> TAIL_ROTOR_SPEED = SynchedEntityData.defineId(HelicopterEntity.class, EntityDataSerializers.FLOAT);
//    private static final EntityDataAccessor<Boolean> ENGINE_RUNNING = SynchedEntityData.defineId(HelicopterEntity.class, EntityDataSerializers.BOOLEAN);
//    private static final EntityDataAccessor<Integer> PASSENGERS = SynchedEntityData.defineId(HelicopterEntity.class, EntityDataSerializers.INT);

    // 飞行参数
    private float liftForce = 0.0f;
    private float forwardForce = 0.0f;
    private float sideForce = 0.0f;
    private float fuel = 1000.0f;
    private final float MAX_FUEL = 1000.0f;
    public VehicleEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }
}
