package com.hbm.entity.logic;

import com.hbm.api.badthing.ContaminationUtil;
import com.hbm.config.ConfigBomb;
import com.hbm.entity.ModEntityType;
import com.hbm.explosion.ExplosionNukeGeneric;
import com.hbm.explosion.ExplosionNukeRayBatched;
import com.hbm.explosion.ExplosionNukeRayParallelized;
import com.hbm.explosion.IExplosionRay;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class EntityNukeExplosionMK5 extends EntityExplosionChunkLoading{
    private static final int default_explode_strength = 100;
    //爆炸的强度
    public static final EntityDataAccessor<Integer> EXPLODE_STRENGTH = SynchedEntityData.defineId(EntityNukeExplosionMK5.class, EntityDataSerializers.INT);
    //爆炸半径
    public static final EntityDataAccessor<Integer> EXPLODE_RADIUS = SynchedEntityData.defineId(EntityNukeExplosionMK5.class, EntityDataSerializers.INT);
    //辐射蔓延速度
    public static final EntityDataAccessor<Integer> RADIATION_SPEED = SynchedEntityData.defineId(EntityNukeExplosionMK5.class, EntityDataSerializers.INT);
    //负责爆炸的主要类
    IExplosionRay explosion;
    public EntityNukeExplosionMK5(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public EntityNukeExplosionMK5(Level pLevel) {
        super(ModEntityType.ENTITY_NUKE_EXPLOSION_MK5.get(), pLevel);
    }
    public EntityNukeExplosionMK5(Level pLevel, Vec3 location, int strength, int radius, int speed) {
        super(ModEntityType.ENTITY_NUKE_EXPLOSION_MK5.get(), pLevel);
        setStrength(strength);
        setRadius(radius);
        setSpeed(speed);
        this.setPos(location);
    }

    @Override
    public void tick() {
        super.tick();
        if (getStrength() == 0){
            this.clearChunkLoader();
            this.discard();
            return;
        }
        if (!this.level().isClientSide){
//            if (tickCount % 100 == 0) HBM.LOGGER.info("mk5 exist tick: " + this.tickCount);
            loadChunk((int) Math.floor(position().x / 16D), (int) Math.floor(position().z / 16D));
            int damageInterval = getDamageInterval();
            if (tickCount % damageInterval == 0) {
                ExplosionNukeGeneric.dealDamage(level(), position(), getRadius());
            }

            int radiationInterval = getRadiationInterval();
            if (tickCount % radiationInterval == radiationInterval / 2) {
                radiate((2_500_000F / (this.tickCount * 5 + 1)) * radiationInterval, this.getRadius() * 2, getRadiationSampleStep());
            }

            if(explosion == null) {
                if (ConfigBomb.explosionAlgorithm == 0) {
                    explosion = new ExplosionNukeRayBatched(level(), blockPosition(),
                            getStrength(), getSpeed(),getRadius());
                } else {
                    explosion = new ExplosionNukeRayParallelized((ServerLevel) level(), blockPosition(),
                            getStrength(),getRadius());
                }
            }
            if(!explosion.isComplete()) {
                int tickBudget = Math.max(1, ConfigBomb.mk5);
                int cacheBudget = Math.max(1, tickBudget / 2);
                int destructionBudget = Math.max(1, tickBudget - cacheBudget);
                explosion.cacheChunksTick(cacheBudget);
                explosion.destructionTick(destructionBudget);
            }
//            else if(fallout) {
//
//                EntityFalloutRain fallout = new EntityFalloutRain(this.worldObj);
//                fallout.posX = this.posX;
//                fallout.posY = this.posY;
//                fallout.posZ = this.posZ;
//                fallout.setScale((int)(this.length * 2.5 + falloutAdd) * BombConfig.falloutRange / 100);
//
//                this.worldObj.spawnEntityInWorld(fallout);
//
//                this.clearChunkLoader();
//                this.setDead();
//            }
            else {
                this.clearChunkLoader();
                this.discard();
            }
        }
    }
    //爆炸产生的辐射
    private void radiate(float rads, double range, int sampleStep){
        List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class, new AABB(position().x, position().y, position().z, position().x, position().y, position().z).inflate(range, range, range));
        if (entities.isEmpty()) {
            return;
        }

        MutableBlockPos cursor = new MutableBlockPos();
        for(LivingEntity e : entities) {

            Vec3 vec = new Vec3(e.position().x - position().x, (e.position().y + e.getEyeHeight()) - position().y, e.position().z - position().z);
            double len = vec.length();
            if (len < 1.0E-3D) {
                continue;
            }
            vec = vec.normalize();

            float res = 0;

            for(int i = 1; i < len; i += sampleStep) {

                int ix = (int)Math.floor(position().x + vec.x * i);
                int iy = (int)Math.floor(position().y + vec.y * i);
                int iz = (int)Math.floor(position().z + vec.z * i);

                cursor.set(ix, iy, iz);
                res += level().getBlockState(cursor).getBlock().getExplosionResistance() * sampleStep;
            }

            if(res < 1)
                res = 1;

            float eRads = rads;
            eRads /= (float)res;
            eRads /= (float)(len * len);

            ContaminationUtil.contaminate(e, ContaminationUtil.HazardType.RADIATION, ContaminationUtil.ContaminationType.RAD_BYPASS, eRads);
        }
    }

    private int getDamageInterval() {
        int radius = getRadius();
        if (radius >= 384) return 8;
        if (radius >= 256) return 6;
        if (radius >= 160) return 4;
        if (radius >= 96) return 2;
        return 1;
    }

    private int getRadiationInterval() {
        int radius = getRadius();
        if (radius >= 384) return 16;
        if (radius >= 256) return 12;
        if (radius >= 160) return 8;
        if (radius >= 96) return 4;
        return 2;
    }

    private int getRadiationSampleStep() {
        int radius = getRadius();
        if (radius >= 384) return 6;
        if (radius >= 256) return 4;
        if (radius >= 160) return 3;
        if (radius >= 96) return 2;
        return 1;
    }

    public static EntityNukeExplosionMK5 statFac(Level level, int r, Vec3 location) {
        r = r == 0 ? 25 : 2 * r;
        int strength = r;
        int speed = (int)Math.ceil((double) 10_0000 / strength);
        int radius = strength / 2;
        return new EntityNukeExplosionMK5(level,location,strength,radius,speed);
    }

    @Override
    public void remove(RemovalReason pReason){
        if (explosion != null) explosion.cancel();
        super.remove(pReason);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(EXPLODE_STRENGTH,default_explode_strength);
        this.entityData.define(EXPLODE_RADIUS,100);
        this.entityData.define(RADIATION_SPEED,0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putInt("strength",getStrength());
        pCompound.putInt("radius",getRadius());
        pCompound.putInt("speed",getSpeed());
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        this.setStrength(pCompound.getInt("strength"));
        this.setRadius(pCompound.getInt("radius"));
        this.setSpeed(pCompound.getInt("speed"));
    }

    protected int getStrength(){
        return this.entityData.get(EXPLODE_STRENGTH);
    }
    protected void setStrength(int strength){
        this.entityData.set(EXPLODE_STRENGTH,strength);
    }
    protected int getRadius(){
        return this.entityData.get(EXPLODE_RADIUS);
    }
    protected void setRadius(int radius){
        this.entityData.set(EXPLODE_RADIUS,radius);
    }
    protected int getSpeed(){
        return this.entityData.get(RADIATION_SPEED);
    }
    protected void setSpeed(int speed){
        this.entityData.set(RADIATION_SPEED,speed);
    }
}
