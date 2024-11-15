package com.hbm.entity.logic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class EntityNukeExplosionMK3 extends EntityExplosionChunkLoading{
    public EntityNukeExplosionMK3(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide)loadChunk(this.chunkPos.x,this.chunkPos.z);

//        if (){
//
//        }
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
