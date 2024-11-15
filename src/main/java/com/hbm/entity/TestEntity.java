package com.hbm.entity;

import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;


public class TestEntity extends Entity {
    //LOGGER的Logger对象，用于记录日志信息。
    private static final Logger LOGGER = LogUtils.getLogger();
    //COUNTER的实体数据访问器，用于存储实体的计数器数据。
    private static final EntityDataAccessor<Integer> COUNTER = SynchedEntityData.defineId(TestEntity.class, EntityDataSerializers.INT);
    @Override
    public void tick() {
        //检查当前是否为客户端，如果是，则从实体数据中获取计数器数据并记录日志信息。如果不是客户端，则从实体数据中获取计数器数据，记录日志信息，并将计数器数据加1。最后，调用父类的tick()方法。
        // 说的明白一些就是服务器将计数+1，然后进行数据的同步，在客户端打印出来。
//        if(this.level().isClientSide){
//            Integer i = this.entityData.get(COUNTER);
//            LOGGER.info(i.toString());
//        }
//        if(!this.level().isClientSide){
//            LOGGER.info(this.entityData.get(COUNTER).toString());
//            this.entityData.set(COUNTER,this.entityData.get(COUNTER)+1);
//        }
        super.tick();
    }
    //构造方法
    public TestEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    //defineSynchedData()：该方法用于定义实体的同步数据，在该方法中，将COUNTER实体数据访问器初始化为0。
    @Override
    protected void defineSynchedData() {
        this.entityData.define(COUNTER, 0);
    }
    //readAdditionalSaveData()：该方法用于从NBT标签中读取额外的保存数据，在该方法中，从NBT标签中读取计数器数据，并保存到实体数据中。
    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        this.entityData.set(COUNTER,pCompound.getInt("counter"));
    }
    //addAdditionalSaveData()：该方法用于向NBT标签中添加额外的保存数据，在该方法中，将计数器数据保存到NBT标签中。
    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putInt("counter",this.entityData.get(COUNTER));
    }
}
