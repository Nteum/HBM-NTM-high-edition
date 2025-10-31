package com.hbm.entity.effect;

import com.hbm.entity.ModEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Client-visible holder entity that plays the imported Geckolib explosion animations for core events.
 * It is purely visual – no physics, no collision, no damage – and self-destructs after the animation finishes.
 */
public class EntityCoreExplosion extends Entity implements GeoEntity {
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(EntityCoreExplosion.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache animCache = GeckoLibUtil.createInstanceCache(this);
    private int lifeTicks;

    public EntityCoreExplosion(EntityType<? extends EntityCoreExplosion> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public static @Nullable EntityCoreExplosion spawn(Level level, Vec3 position, Variant variant) {
        EntityCoreExplosion entity = ModEntityType.ENTITY_CORE_EXPLOSION.get().create(level);
        if (entity != null) {
            entity.setVariant(variant);
            entity.moveTo(position.x, position.y, position.z, 0.0F, 0.0F);
        }
        return entity;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_VARIANT, Variant.SMALL.ordinal());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("Variant")) {
            setVariant(Variant.byName(tag.getString("Variant")));
        }
        this.lifeTicks = tag.getInt("Life");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putString("Variant", getVariant().name());
        tag.putInt("Life", this.lifeTicks);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            setDeltaMovement(Vec3.ZERO);
        }
        this.lifeTicks++;
        if (this.lifeTicks >= getVariant().lifetimeTicks()) {
            discard();
        }
    }

    @Override
    public void move(MoverType type, Vec3 movement) {
        // Visual entity – ignore movement requests.
    }

    @Override
    protected void doWaterSplashEffect() {
        // Prevent splash sounds for the purely visual entity.
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    protected MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    @Override
    public double getBaseYawRotationSpeed() {
        return 0;
    }

    @Override
    public float getEyeHeight(Pose pose, EntityDimensions size) {
        return 0.0F; // 纯视觉实体，无需真实视线高度
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        // 注意：实体尺寸保持很小，真正的可视大小交由渲染器进行缩放处理，
        // 避免在世界加载/区块构建阶段出现超大 AABB 带来的卡死或进度停滞。
        return getVariant().dimensions();
    }

    public Variant getVariant() {
        return Variant.byId(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(Variant variant) {
        this.entityData.set(DATA_VARIANT, variant.ordinal());
        refreshDimensions();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_VARIANT.equals(key)) {
            refreshDimensions();
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "core_explosion", 0, this::animationPredicate));
    }

    private PlayState animationPredicate(AnimationState<EntityCoreExplosion> state) {
        state.setAndContinue(getVariant().animation());
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animCache;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double range = (getVariant() == Variant.LARGE) ? 256.0D : 192.0D;
        return distance < range * range;
    }

    public enum Variant {
        SMALL("core_explosion_small", "mediumexplosion.model.new", 90, 0.0F, EntityDimensions.fixed(1.0F, 1.0F)),
        LARGE("core_explosion_large", "AtomicBombExplosion.model.new", 420, 2.0F, EntityDimensions.fixed(2.0F, 2.0F));

        private static final Variant[] VALUES = values();
        private final String resourceKey;
        private final RawAnimation animation;
        private final int lifetimeTicks;
        private final float shadowRadius;
        private final EntityDimensions dimensions;

        Variant(String resourceKey, String animationName, int lifetimeTicks, float shadowRadius, EntityDimensions dimensions) {
            this.resourceKey = resourceKey;
            this.animation = RawAnimation.begin().thenLoop(animationName);
            this.lifetimeTicks = lifetimeTicks;
            this.shadowRadius = shadowRadius;
            this.dimensions = dimensions;
        }

        public String resourceKey() {
            return resourceKey;
        }

        public RawAnimation animation() {
            return animation;
        }

        public int lifetimeTicks() {
            return lifetimeTicks;
        }

        public float shadowRadius() {
            return shadowRadius;
        }

        public EntityDimensions dimensions() {
            return dimensions;
        }

        public static Variant byName(String name) {
            for (Variant variant : VALUES) {
                if (variant.name().equalsIgnoreCase(name)) {
                    return variant;
                }
            }
            return SMALL;
        }

        public static Variant byId(int id) {
            if (id < 0 || id >= VALUES.length) {
                return SMALL;
            }
            return VALUES[id];
        }
    }
}
