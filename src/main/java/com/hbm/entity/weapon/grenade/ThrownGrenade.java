package com.hbm.entity.weapon.grenade;

import com.hbm.registries.ModItems;
import com.hbm.registries.ModItems;;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
/** 手榴弹实体类
 * 主要参考throwableitemprojectile
 * （说实话如果不是需要弹跳逻辑，直接写成它的子类都可以。）
 * */
abstract public class ThrownGrenade extends Projectile implements ItemSupplier {
    private static final EntityDataAccessor<ItemStack> DATA_GRENADE_STACK = SynchedEntityData.defineId(ThrownGrenade.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Integer> DATA_FUSE_ID = SynchedEntityData.defineId(ThrownGrenade.class, EntityDataSerializers.INT);
    public ThrownGrenade(EntityType<?> pEntityType, Level pLevel) {
        super((EntityType<? extends ThrownGrenade>) pEntityType, pLevel);
    }
    public ThrownGrenade(EntityType<?> pEntityType, double pX, double pY, double pZ,Level pLevel) {
        this(pEntityType, pLevel);
        this.setPos(pX, pY, pZ);
    }
    public ThrownGrenade(EntityType<?> pEntityType, LivingEntity pShooter,Level pLevel) {
        this(pEntityType,pShooter.getX(), pShooter.getEyeY() - (double)0.1F, pShooter.getZ(),pLevel);
        this.setOwner(pShooter);
    }

    /** 击中物品和实体的效果
     * */
    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
    }

    @Override
    public void tick() {
        super.tick();
        boolean bounce = false;

        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        boolean flag = false;
        if (hitresult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockpos = ((BlockHitResult)hitresult).getBlockPos();
            BlockState blockstate = this.level().getBlockState(blockpos);
            if (blockstate.is(Blocks.NETHER_PORTAL)) {
                this.handleInsidePortal(blockpos);
                flag = true;
            } else if (blockstate.is(Blocks.END_GATEWAY)) {
                BlockEntity blockentity = this.level().getBlockEntity(blockpos);
                if (blockentity instanceof TheEndGatewayBlockEntity && TheEndGatewayBlockEntity.canEntityTeleport(this)) {
                    TheEndGatewayBlockEntity.teleportEntity(this.level(), blockpos, blockstate, this, (TheEndGatewayBlockEntity)blockentity);
                }

                flag = true;
            }
            bounce = !flag;
        }

        if (hitresult.getType() != HitResult.Type.MISS && !flag && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
            this.onHit(hitresult);
        }

        this.checkInsideBlocks();
        Vec3 vec3 = this.getDeltaMovement();
        double d0 = 0,d1 = 0,d2 = 0;
        if (!bounce){
            //不反弹
            d2 = this.getX() + vec3.x;
            d0 = this.getY() + vec3.y;
            d1 = this.getZ() + vec3.z;
        }else {
            //反弹（暂定只是碰到方块，还没考虑碰到实体如何反弹）
            BlockHitResult blockHitResult = (BlockHitResult) hitresult;
            d2 = this.getX() + (blockHitResult.getLocation().x - this.getX()) * 0.6;
            d0 = this.getY() + (blockHitResult.getLocation().y - this.getY()) * 0.6;
            d1 = this.getZ() + (blockHitResult.getLocation().z - this.getZ()) * 0.6;

            vec3 = switch (blockHitResult.getDirection()) {
                case DOWN, UP -> new Vec3(vec3.x, -1 * vec3.y, vec3.z);
                case NORTH, SOUTH -> new Vec3(vec3.x, vec3.y, -1 * vec3.z);
                case EAST, WEST -> new Vec3(-1 * vec3.x, vec3.y, vec3.z);
            };

            vec3 = vec3.scale(getBounceMod());
        }

        this.updateRotation();
        float f;
        if (this.isInWater()) {
            for(int i = 0; i < 4; ++i) {
                float f1 = 0.25F;
                this.level().addParticle(ParticleTypes.BUBBLE, d2 - vec3.x * 0.25D, d0 - vec3.y * 0.25D, d1 - vec3.z * 0.25D, vec3.x, vec3.y, vec3.z);
            }

            f = 0.8F;
        } else {
            f = 0.99F;
        }

        this.setDeltaMovement(vec3.scale((double)f));
        if (!this.isNoGravity() && !this.onGround()) {
            Vec3 vec31 = this.getDeltaMovement();
            this.setDeltaMovement(vec31.x, vec31.y - (double)this.getGravity(), vec31.z);
        }

        this.setPos(d2, d0, d1);

        countDown();
    }
    /**
     * 实体数据以及相应的getter和setter
     * */
    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_GRENADE_STACK, ItemStack.EMPTY);
        this.entityData.define(DATA_FUSE_ID,getDefaultFuseTime());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        ItemStack itemstack = ItemStack.of(pCompound.getCompound("Item"));
        this.setItem(itemstack);
        setFuse((int)pCompound.getShort("fuse"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        ItemStack itemstack = this.getItemRaw();
        if (!itemstack.isEmpty()) {
            pCompound.put("Item", itemstack.save(new CompoundTag()));
        }
        pCompound.putShort("fuse",(short) getFuse());
    }
    public void setItem(ItemStack pStack) {
        if (!pStack.is(this.getDefaultItem()) || pStack.hasTag()) {
            this.getEntityData().set(DATA_GRENADE_STACK, pStack.copyWithCount(1));
        }
    }
    protected ItemStack getItemRaw() {
        return this.getEntityData().get(DATA_GRENADE_STACK);
    }
    protected @NotNull Item getDefaultItem() {return ModItems.grenade_generic.get();}
    public @NotNull ItemStack getItem() {
        ItemStack itemstack = this.getItemRaw();
        return itemstack.isEmpty() ? new ItemStack(this.getDefaultItem()) : itemstack;
    }

    public void setFuse(int pLife) {
        this.entityData.set(DATA_FUSE_ID, pLife);
    }

    public int getFuse() {
        return this.entityData.get(DATA_FUSE_ID);
    }
    //判断实体在特定距离是否需要渲染（参考throwableprojectile）
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        double d0 = this.getBoundingBox().getSize() * 4.0D;
        if (Double.isNaN(d0)) {
            d0 = 4.0D;
        }

        d0 *= 64.0D;
        return pDistance < d0 * d0;
    }
    /*
    * projectile的参数
    * */
    protected float getGravity(){
        return 0.03F;
    };
    //回弹时候的回弹系数
    abstract public double getBounceMod();
    abstract public int getDefaultFuseTime();
    public void countDown(){
        int i = getFuse();
        if (i <= 0){
            if (!this.level().isClientSide) {
                explode();
            }
        }else {
            setFuse(i-1);
        }
    }
    abstract public void explode();
}
