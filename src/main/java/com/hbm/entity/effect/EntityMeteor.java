package com.hbm.entity.effect;

import com.hbm.config.ConfigWorld;
import com.hbm.entity.ModEntityType;
import com.hbm.particle.ParticleSystem;
import net.minecraft.nbt.CompoundTag;
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
                CompoundTag data = new CompoundTag();
                data.putString("type", "exhaust");
                data.putString("mode", "meteor");
                data.putInt("count", 10);
                data.putDouble("width", 1);
                data.putDouble("x", getX() - getDeltaMovement().x);
                data.putDouble("y", getY() - getDeltaMovement().y);
                data.putDouble("z", getZ() - getDeltaMovement().z);

                ParticleSystem.handleParticleCombo(data);
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
