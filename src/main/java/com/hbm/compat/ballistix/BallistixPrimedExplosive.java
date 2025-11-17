package com.hbm.compat.ballistix;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class BallistixPrimedExplosive extends Entity {

    private static final EntityDataAccessor<Integer> DATA_FUSE =
            SynchedEntityData.defineId(BallistixPrimedExplosive.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TYPE =
            SynchedEntityData.defineId(BallistixPrimedExplosive.class, EntityDataSerializers.INT);

    @Nullable
    private LivingEntity owner;

    public BallistixPrimedExplosive(final EntityType<? extends BallistixPrimedExplosive> type, final Level level) {
        super(type, level);
        this.blocksBuilding = true;
    }

    public BallistixPrimedExplosive(final Level level, final double x, final double y, final double z,
                                    @Nullable final LivingEntity owner, final BallistixExplosiveType explosiveType) {
        this(BallistixEntities.PRIMED_EXPLOSIVE.get(), level);
        setPos(x, y, z);
        final double variance = level.random.nextDouble() * 0.2 - 0.1;
        setDeltaMovement(variance, 0.2D, variance);
        this.owner = owner;
        setExplosiveType(explosiveType);
        setFuse(explosiveType.defaultFuse());
    }

    public static BallistixPrimedExplosive spawn(Level level, BlockPos pos, BallistixExplosiveType type, @Nullable LivingEntity owner) {
        BallistixPrimedExplosive entity = new BallistixPrimedExplosive(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, owner, type);
        level.addFreshEntity(entity);
        return entity;
    }

    static void init() {
        // nothing to wire yet – hook preserved for symmetry with other compat modules
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(DATA_FUSE, 80);
        entityData.define(DATA_TYPE, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        setFuse(tag.getShort("Fuse"));
        if (tag.contains("ExplosiveType")) {
            String id = tag.getString("ExplosiveType");
            for (BallistixExplosiveType candidate : BallistixExplosiveType.values()) {
                if (candidate.id().equals(id)) {
                    setExplosiveType(candidate);
                    break;
                }
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putShort("Fuse", (short) getFuse());
        tag.putString("ExplosiveType", getExplosiveType().id());
    }

    @Override
    public void tick() {
        if (!level().isClientSide && !(level() instanceof ServerLevel)) {
            // Defensive: do not tick on a non-server logical level
            discard();
            return;
        }
        Vec3 motion = getDeltaMovement();
        if (!isNoGravity()) {
            setDeltaMovement(motion.x, motion.y - 0.04D, motion.z);
        }
        move(MoverType.SELF, getDeltaMovement());
        setDeltaMovement(getDeltaMovement().scale(0.98D));
        if (onGround()) {
            setDeltaMovement(getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
        }

        final int fuse = getFuse() - 1;
        setFuse(fuse);
        if (fuse <= 0) {
            discard();
            if (!level().isClientSide) {
                BallistixExplosionHandlers.detonate(getExplosiveType(), level(), position(), owner);
            }
        } else {
            if (level().isClientSide) {
                level().addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE, getX(), getY() + 0.2D, getZ(), 0.0D, 0.0D, 0.0D);
            } else if (fuse % 5 == 0) {
                level().playSound(null, blockPosition(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 0.2F, 1.0F);
            }
        }
    }

    public void setFuse(int fuse) {
        entityData.set(DATA_FUSE, fuse);
    }

    public int getFuse() {
        return entityData.get(DATA_FUSE);
    }

    public BallistixExplosiveType getExplosiveType() {
        final int ordinal = entityData.get(DATA_TYPE);
        BallistixExplosiveType[] values = BallistixExplosiveType.values();
        return values[Math.max(0, Math.min(values.length - 1, ordinal))];
    }

    public void setExplosiveType(BallistixExplosiveType type) {
        entityData.set(DATA_TYPE, type.ordinal());
    }

    @Nullable
    public LivingEntity getOwner() {
        return owner;
    }

    public BlockState getRenderState() {
        return BallistixBlocks.EXPLOSIVES.get(getExplosiveType()).get().defaultBlockState();
    }

    @Override
    public boolean isPickable() {
        return !isRemoved();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
