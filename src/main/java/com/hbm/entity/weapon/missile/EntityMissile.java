package com.hbm.entity.weapon.missile;

import com.hbm.HBM;
import com.hbm.entity.IRadarDetectableNT;
import com.hbm.entity.projectile.EntityThrowableNT;
import com.hbm.item.weapon.ItemMissilePart;
import com.hbm.particle.ParticleSystem;
import com.hbm.utils.chunk.ChunkLoadHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.world.ForgeChunkManager;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityMissile extends EntityThrowableNT implements IRadarDetectableNT {
    public BlockPos start;
    public BlockPos target;
    public double velocity; //速度
    public double yV0;  //期望Y轴初速度（实际上一开始会从0开始加速）
    public double decelY;   //Y方向空气阻力
    public double accelXZ;  //水平方向加速度
    public boolean startAcc = true;    //初始加速是否结束
    public boolean isCluster = false;
    // 我也不知道这个是什么，反正办过来就行了
    public static final EntityDataAccessor<Byte> DATA_MISSILE_1 = SynchedEntityData.defineId(EntityMissile.class, EntityDataSerializers.BYTE);
    public int health = 50;
    private static final int FLIGHT_MODE_BALLISTIC = 0;
    private static final int FLIGHT_MODE_DIRECT = 1;
    private static final int FLIGHT_MODE_THROWN = 2;
    private int flightMode = FLIGHT_MODE_BALLISTIC;
    private double directSpeed = 3.4D;

    public EntityMissile(EntityType<? extends ThrowableProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        start = target = blockPosition();
        if (!this.level().isClientSide){
            ChunkLoadHelper.register(this);
        }
    }
    public EntityMissile(EntityType<? extends ThrowableProjectile> pEntityType, Level level, float x, float y,float z, BlockPos target){
        this(pEntityType,level);
        entityInit();   //低版本是forge加的内置方法，后面变成event了，这里暂时用函数实现
        this.setPos(x,y,z);
        this.start = new BlockPos((int) x, (int) y, (int) z);
        this.target = target;
        this.yV0 = getyV0(x, y, z, target);
//        this.setDeltaMovement(getDeltaMovement().x,2,getDeltaMovement().z);
        this.setDeltaMovement(getDeltaMovement().x,0,getDeltaMovement().z);

        Vec3 vector = new Vec3(target.getX()-start.getX(),0,target.getZ()-start.getZ());
        accelXZ = decelY = 1 / vector.length();
        velocity = 0;
//        this.setYRot((float) (Mth.atan2(target.getX()-start.getX(), target.getZ()-start.getZ()) * 180.0D / Math.PI));
        this.setSize(1.5F, 1.5F);
    }
    // 设定无论是否在视线内始终渲染
    @Override
    public boolean shouldRender(double pX, double pY, double pZ) {
        return true;
    }

    /** Auto-generates radar blip level and all that from the item */
    public abstract ItemStack getMissileItemForInfo();

    @Override
    public boolean canBeSeenBy(Object radar) {
        return true;
    }

    @Override
    public boolean paramsApplicable(RadarScanParams params) {
        return params.scanMissiles;
    }

    @Override
    public boolean suppliesRedstone(RadarScanParams params) {
        return params.smartMode && this.getDeltaMovement().y >= 0;
    }

    @Override
    protected double motionMult() {
        return velocity;
    }

    @Override
    public boolean doesImpactEntities() {
        return false;
    }

    @Override
    public void tick() {
        if (velocity < 4 && !startAcc && this.flightMode == FLIGHT_MODE_BALLISTIC) velocity += Mth.clamp(tickCount / 60D * 0.05d,0,0.05);

        if (!level().isClientSide){
            switch (this.flightMode) {
                case FLIGHT_MODE_DIRECT -> tickDirectFlight();
                case FLIGHT_MODE_THROWN -> tickThrownFlight();
                default -> {
                    if (!tickBallisticFlight()) {
                        return;
                    }
                }
            }
        }

        super.tick();

        if (level().isClientSide){
            this.spawnContrail();
        }

        loadNeighboringChunks((int) Math.floor(getX() / 16), (int) Math.floor(getZ() / 16));
    }

    private boolean tickBallisticFlight() {
        double motionX = this.getDeltaMovement().x;
        double motionY = this.getDeltaMovement().y;
        double motionZ = this.getDeltaMovement().z;
        if (hasPropulsion()){
            if (startAcc){
                // 一开始火箭从0开始加速
                motionY += 0.05;
                if (motionY >= yV0) startAcc = false;
            }else {
                motionY -= decelY * velocity;
                double f = 2 * accelXZ * velocity;
                motionX = (target.getCenter().x - getX()) * f;
                motionZ = (target.getCenter().z - getZ()) * f;
            }
        }else {
            if (motionY > -1.5) motionY -= 0.2;
        }

        if(motionY < -velocity && this.isCluster) {
            cluster();
            this.setDead();
            return false;
        }
        this.setDeltaMovement(motionX, motionY, motionZ);
        return true;
    }

    private void tickDirectFlight() {
        Vec3 toTarget = this.target.getCenter().subtract(this.position());
        if (toTarget.lengthSqr() < 1.0E-4D) {
            return;
        }
        Vec3 direction = toTarget.normalize();
        this.setDeltaMovement(direction.scale(this.directSpeed));
    }

    private void tickThrownFlight() {
        Vec3 motion = this.getDeltaMovement().scale(0.985D);
        if (motion.y > -1.5D) {
            motion = motion.add(0.0D, -0.045D, 0.0D);
        }
        this.setDeltaMovement(motion);
    }

    public void configureDirectFlight(BlockPos target, double speed) {
        this.target = target;
        this.flightMode = FLIGHT_MODE_DIRECT;
        this.directSpeed = Math.max(0.8D, speed);
        this.startAcc = false;
        this.velocity = this.directSpeed;
    }

    public void configureThrownFlight() {
        this.flightMode = FLIGHT_MODE_THROWN;
        this.startAcc = false;
        this.velocity = 0.0D;
    }

    public void configureBallisticFlight(BlockPos target) {
        this.target = target;
        this.flightMode = FLIGHT_MODE_BALLISTIC;
        this.startAcc = true;
    }

    public boolean hasPropulsion() {
        return true;
    }

    public double getyV0(float x, float y,float z, BlockPos target){
        float dist0 = Mth.sqrt((float) target.distToCenterSqr(x,y,z));
        return Mth.clamp(dist0 / 100, 1, 3.5);
    }

    protected void spawnContrail() {
        this.spawnContraolWithOffset(0, 0, 0);
    }

    protected void spawnContraolWithOffset(double offsetX, double offsetY, double offsetZ) {
        Vec3 vec = this.position().subtract(xOld, yOld, zOld);
        double len = vec.length();
        vec = vec.normalize();
        Vec3 thrust = new Vec3(0, 1, 0);
        thrust.xRot((this.getXRot() - 90) * (float) Math.PI / 180F);
        thrust.yRot(this.getYRot() * (float) Math.PI / 180F);

        for(int i = 0; i < Math.max(Math.min(len, 10), 1); i++) {
            double j = i - len;
            ParticleSystem.addRocketFlame(position().x - vec.x * j + offsetX, position().y - vec.y * j + offsetY, position().z - vec.z * j + offsetZ,
                    -thrust.x, -thrust.y, -thrust.z, getContrailScale(), 60 + level().getRandom().nextInt(20));
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        decelY = nbt.getDouble("decel");
        accelXZ = nbt.getDouble("accel");
        // 没有用到y轴高度，不用存，加载的时候直接设成0即可
        this.target = new BlockPos(nbt.getInt("tX"), 0, nbt.getInt("tZ"));
        this.start = new BlockPos(nbt.getInt("sX"), 0, nbt.getInt("sZ"));
        velocity = nbt.getDouble("veloc");
        this.flightMode = nbt.contains("flightMode") ? nbt.getInt("flightMode") : FLIGHT_MODE_BALLISTIC;
        this.directSpeed = nbt.contains("directSpeed") ? nbt.getDouble("directSpeed") : 3.4D;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putDouble("decel", decelY);
        nbt.putDouble("accel", accelXZ);
        nbt.putInt("tX", target.getX());
        nbt.putInt("tZ", target.getZ());
        nbt.putInt("sX", start.getX());
        nbt.putInt("sZ", start.getZ());
        nbt.putDouble("veloc", velocity);
        nbt.putInt("flightMode", this.flightMode);
        nbt.putDouble("directSpeed", this.directSpeed);
    }

    protected float getContrailScale() {
        return 1F;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.isInvulnerable()) return false;
        else {
            if (this.health > 0 && !this.level().isClientSide){
                health -= (int) pAmount;
                if (this.health <= 0) this.killMissile();
            }
            return true;
        }
    }

    protected void killMissile() {
        if(!this.isAlive()) {
            this.setDead();
//            ExplosionLarge.explode(worldObj, posX, posY, posZ, 5, true, false, true);
//            ExplosionLarge.spawnShrapnelShower(worldObj, posX, posY, posZ, motionX, motionY, motionZ, 15, 0.075);
//            ExplosionLarge.spawnMissileDebris(worldObj, posX, posY, posZ, motionX, motionY, motionZ, 0.25, getDebris(), getDebrisRareDrop());
        }
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return true;
    }

    @Override
    protected void onHit(HitResult pResult) {
        // 打印击中位置
        if (getOwner() != null)  this.getOwner().sendSystemMessage(Component.translatable("msg.hbm.hit_on", pResult.getLocation().toString()));
        if (pResult.getType() == HitResult.Type.BLOCK){
            onMissileImpact(pResult);
            this.setDead();
        }
    }

    public abstract void onMissileImpact(HitResult pResult);
    /**
     * 火箭残骸
     * */
    public abstract List<ItemStack> getDebris();
    public abstract ItemStack getDebrisRareDrop();
    public void cluster() { }

    @Override
    protected float getGravity() {
        return 0.0F;
    }

    @Override
    protected float getAirDrag() {
        return 1F;
    }

    @Override
    protected float getWaterDrag() {
        return 1F;
    }

    public void setDead(){
        this.remove(RemovalReason.DISCARDED);
        if (!this.level().isClientSide())
            ChunkLoadHelper.unRegister(this);
    }

    /** 当实体被移除的时候，也一并移除它强制加载的区块。 */
    @Override
    public void onRemovedFromWorld() {
        if (!this.level().isClientSide())
            ChunkLoadHelper.unRegister(this);
        super.onRemovedFromWorld();
    }

    private void setSize(double width, double height){
        this.setBoundingBox(AABB.ofSize(new Vec3(0.5,0.5,0.5),width,height,width));
    }


    protected void entityInit() {
        if (!level().isClientSide()){
            ForgeChunkManager.forceChunk((ServerLevel) level(), HBM.MODID,blockPosition(),chunkPosition().x, chunkPosition().z, true,true);
        }
    }

    List<ChunkPos> loadedChunks = new ArrayList<ChunkPos>();

    public void explodeStandard(float strength, int resolution, boolean fire) {
//        ExplosionVNT xnt = new ExplosionVNT(level(), position().x, position().y, position().z, strength);
//        xnt.setBlockAllocator(new BlockAllocatorStandard(resolution));
////        xnt.setBlockProcessor(new BlockProcessorStandard().setNoDrop().withBlockEffect(fire ? new BlockMutatorFire() : null));
////        xnt.setEntityProcessor(new EntityProcessorCross(7.5D).withRangeMod(2));
//        xnt.setPlayerProcessor(new PlayerProcessorStandard());
//        xnt.explode();
    }

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


    public void clearChunkLoader() {
        if(!level().isClientSide()) {
            for(ChunkPos chunk : loadedChunks) {
                ForgeChunkManager.forceChunk((ServerLevel) level(),HBM.MODID,this,chunk.x,chunk.z,false,false);
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_MISSILE_1, Byte.valueOf("5"));
    }

    @Override
    public String getUnlocalizedName() {
        ItemStack item = this.getMissileItemForInfo();
        if(item != null && item.getItem() instanceof ItemMissilePart) {
            ItemMissilePart missile = (ItemMissilePart) item.getItem();
            switch(missile.tier) {
                case TIER0: return "radar.target.tier0";
                case TIER1: return "radar.target.tier1";
                case TIER2: return "radar.target.tier2";
                case TIER3: return "radar.target.tier3";
                case TIER4: return "radar.target.tier4";
                default: return "Unknown";
            }
        }

        return "Unknown";
    }
    @Override
    public int getBlipLevel() {
        ItemStack item = this.getMissileItemForInfo();
        if(item != null && item.getItem() instanceof ItemMissilePart) {
            ItemMissilePart missile = (ItemMissilePart) item.getItem();
            switch(missile.tier) {
                case TIER0: return IRadarDetectableNT.TIER0;
                case TIER1: return IRadarDetectableNT.TIER1;
                case TIER2: return IRadarDetectableNT.TIER2;
                case TIER3: return IRadarDetectableNT.TIER3;
                case TIER4: return IRadarDetectableNT.TIER4;
                default: return IRadarDetectableNT.SPECIAL;
            }
        }

        return IRadarDetectableNT.SPECIAL;
    }

    @FunctionalInterface
    public interface MissileCreator<T extends EntityMissile>{
        T create(Level level, float x, float y, float z, BlockPos target);
    }
}
