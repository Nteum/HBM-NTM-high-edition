package com.hbm.entity.weapon.missile;

import com.hbm.HBM;
import com.hbm.entity.ModEntityType;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.hbm.entity.weapon.grenade.ThrownGrenade;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.world.ForgeChunkManager;

import javax.swing.plaf.basic.BasicSliderUI;
import java.util.ArrayList;
import java.util.List;

public abstract class EntityMissile extends ThrowableProjectile {
    public BlockPos start;
    public BlockPos target;
    public double velocity; //速度
    public double decelY;
    public double accelXZ;  //水平方向加速度
    public boolean isCluster = false;
    private static final EntityDataAccessor<Integer> DATA_FUSE_ID = SynchedEntityData.defineId(ThrownGrenade.class, EntityDataSerializers.INT);

    public EntityMissile(EntityType<? extends ThrowableProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        start = target = blockPosition();
    }
    public EntityMissile(EntityType<? extends ThrowableProjectile> pEntityType, Level level, double x, double y,double z, BlockPos target){
        this(pEntityType,level);
        entityInit();   //低版本是forge加的内置方法，后面变成event了，这里暂时用函数实现
        this.setPos(x,y,z);
        this.setRot(0,0);
        this.start = new BlockPos((int) x, (int) y, (int) z);
        this.target = target;
        this.setDeltaMovement(this.getDeltaMovement().x,2,this.getDeltaMovement().z);

        Vec3 vector = new Vec3(target.getX()-start.getX(),0,target.getZ()-start.getZ());
        accelXZ = decelY = 1 / vector.length();
        decelY *= 2;
        velocity = 0;
        this.setSize(1.5F, 1.5F);
    }
    /** Auto-generates radar blip level and all that from the item */
    public abstract ItemStack getMissileItemForInfo();

    private void setSize(double width, double height){
        this.setBoundingBox(AABB.ofSize(new Vec3(0.5,0.5,0.5),width,height,width));
    }
    //============from IRadarDetectableEntity=============
//    @Override
//    public boolean canBeSeenBy(Object radar) {
//        return true;
//    }
//
//    @Override
//    public boolean paramsApplicable(RadarScanParams params) {
//        if(!params.scanMissiles) return false;
//        return true;
//    }
//
//    @Override
//    public boolean suppliesRedstone(RadarScanParams params) {
//        return !params.smartMode || !(this.getDeltaMovement().y >= 0);
//    }
    //=============from EntityMissileThrowableNT=================
    protected void entityInit() {
        if (!level().isClientSide()){
            ForgeChunkManager.forceChunk((ServerLevel) level(), HBM.MODID,blockPosition(),chunkPosition().x, chunkPosition().z, true,true);
        }
    }
    protected double motionMult() {
        return velocity;
    }
    public boolean doesImpactEntities() {
        return false;
    }
    //==========================================

    @Override
    public void tick() {
        setOldPosAndRot();
        super.tick();
        if (velocity < 4)velocity += Mth.clamp(tickCount / 60D * 0.05d,0,0.05);
        if(!level().isClientSide()) {
            double motionX = this.getDeltaMovement().x;
            double motionY = this.getDeltaMovement().y;
            double motionZ = this.getDeltaMovement().z;
            if(hasPropulsion()) {
                motionY -= decelY * velocity;

                Vec3 vector = new Vec3(target.getX() - start.getX(), 0, target.getZ() - start.getZ());
                vector = vector.normalize();
                double f = accelXZ * velocity;
                if (motionY < 0)f = -f;
                vector = vector.scale(f);
                motionX += vector.x;
                motionZ += vector.z;

            } else {
                motionX *= 0.99;
                motionZ *= 0.99;

                if(motionY > -1.5)
                    motionY -= 0.05;
            }

            if(motionY < -velocity && this.isCluster) {
                cluster();
                this.setDead();
                return;
            }

            updateRotation();
            //这里更新tracker，新版没有找到对应代码

            loadNeighboringChunks((int) Math.floor(getX() / 16), (int) Math.floor(getZ() / 16));
        } else {
            this.spawnContrail();
        }
    }

    protected void spawnContrail() {
        this.spawnContraolWithOffset(0, 0, 0);
    }

    protected void spawnContraolWithOffset(double offsetX, double offsetY, double offsetZ) {
//        Vec3 vec = new Vec3(this.lastTickPosX - this.posX, this.lastTickPosY - this.posY, this.lastTickPosZ - this.posZ);
//        double len = vec.lengthVector();
//        vec = vec.normalize();
//        Vec3 thrust = Vec3.createVectorHelper(0, 1, 0);
//        thrust.rotateAroundZ(this.rotationPitch * (float) Math.PI / 180F);
//        thrust.rotateAroundY((this.rotationYaw + 90) * (float) Math.PI / 180F);
//
//        for(int i = 0; i < Math.max(Math.min(len, 10), 1); i++) {
//            double j = i - len;
//            NBTTagCompound data = new NBTTagCompound();
//            data.setDouble("posX", posX - vec.xCoord * j + offsetX);
//            data.setDouble("posY", posY - vec.yCoord * j + offsetY);
//            data.setDouble("posZ", posZ - vec.zCoord * j + offsetZ);
//            data.setString("type", "missileContrail");
//            data.setFloat("scale", this.getContrailScale());
//            data.setDouble("moX", -thrust.xCoord);
//            data.setDouble("moY", -thrust.yCoord);
//            data.setDouble("moZ", -thrust.zCoord);
//            data.setInteger("maxAge", 60 + rand.nextInt(20));
//            MainRegistry.proxy.effectNT(data);
//            level().addParticle();
//        }
    }

    List<ChunkPos> loadedChunks = new ArrayList<ChunkPos>();

    public void loadNeighboringChunks(int newChunkX, int newChunkZ){
        if(!level().isClientSide()) {

            clearChunkLoader();

            loadedChunks.clear();
            loadedChunks.add(new ChunkPos(newChunkX, newChunkZ));

            for(ChunkPos chunk : loadedChunks) {
                ForgeChunkManager.forceChunk((ServerLevel) level(),HBM.MODID,this,chunk.x,chunk.z,true,true);
            }
        }
    }
    // 本来是1.7.10Entity内置方法，现在似乎没法继承，只能暂时如此
    public void setDead() {
        super.discard();
        this.clearChunkLoader();
    }

    public void clearChunkLoader() {
        if(!level().isClientSide()) {
            for(ChunkPos chunk : loadedChunks) {
                ForgeChunkManager.forceChunk((ServerLevel) level(),HBM.MODID,this,chunk.x,chunk.z,false,false);
            }
        }
    }

    public boolean hasPropulsion() {
        return true;
    }
    @Override
    protected void defineSynchedData() {

    }

    public void cluster(){

    }
}
