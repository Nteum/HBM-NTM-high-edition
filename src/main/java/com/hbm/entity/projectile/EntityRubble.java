package com.hbm.entity.projectile;

import com.hbm.entity.ModEntityType;
import com.hbm.entity.TestEntity;
import com.hbm.entity.effect.EntityMeteor;
import com.hbm.entity.projectile.EntityThrowableNT;
import com.hbm.registries.HBMDamage;
import com.hbm.registries.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * 爆炸飞溅出的方块效果
 * */
public class EntityRubble extends EntityThrowableNT {
    public static final EntityDataAccessor<BlockState> DATA_BLOCK = SynchedEntityData.defineId(EntityRubble.class, EntityDataSerializers.BLOCK_STATE);
    public EntityRubble(EntityType<? extends ThrowableProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public EntityRubble(Level pLevel, Vec3 pos) {
        this(ModEntityType.ENTITY_RUBBLE.get(), pLevel);
        this.setPos(pos);
    }



    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_BLOCK, Blocks.AIR.defaultBlockState());
    }

    public void setDataBlock(BlockState blockState){
        this.entityData.set(DATA_BLOCK, blockState);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide){
            this.tickCount++;
        }
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (this.tickCount > 2){
            this.discard();
            level().playSound(null, getOnPos(), ModSounds.BLOCK_DEBRIS.get(), SoundSource.RECORDS, 1.5f, 1.0f);
            if (!level().isClientSide && level() instanceof ServerLevel serverLevel){
                serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, getBlock()), this.getX(), this.getY(), this.getZ(), 20, 0.5, 0.5, 0.5, 0.1);
            }
        }
    }

    public BlockState getBlock(){
        return this.entityData.get(DATA_BLOCK);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        pResult.getEntity().hurt(HBMDamage.get(HBMDamage.METEORITE, level().registryAccess(), this, null), 15);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.put("block", NbtUtils.writeBlockState(this.entityData.get(DATA_BLOCK)));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.entityData.set(DATA_BLOCK, NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), pCompound.getCompound("block")));
    }
}
