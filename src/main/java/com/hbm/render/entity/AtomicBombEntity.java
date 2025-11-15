package com.hbm.render.entity;

import com.hbm.init.BigExplosivesModEntities;
import com.hbm.procedures.AtomicBombDiesDuplicateProcedure;
import com.hbm.procedures.AtomicBombEntityFallProcedure;
import com.hbm.procedures.AtomicBombOnEntityTickUpdateProcedure;
import com.hbm.render.pipeline.GeoRenderKeys;
import com.hbm.render.pipeline.PipelineKeyProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AtomicBombEntity extends PathfinderMob implements GeoEntity, PipelineKeyProvider {
    public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(AtomicBombEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(AtomicBombEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(AtomicBombEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache cache;
    private boolean swinging;
    private boolean lastloop;
    private long lastSwing;
    public String animationprocedure;
    String prevAnim;

    public AtomicBombEntity(PlayMessages.SpawnEntity packet, Level world) {
        this((EntityType<AtomicBombEntity>) BigExplosivesModEntities.ATOMIC_BOMB.get(), world);
    }

    public AtomicBombEntity(EntityType<AtomicBombEntity> type, Level world) {
        super(type, world);
        this.cache = GeckoLibUtil.createInstanceCache(this);
        this.animationprocedure = "empty";
        this.prevAnim = "empty";
        this.xpReward = 0;
        setNoAi(false);
        setMaxUpStep(0.0f);
        setPersistenceRequired();
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SHOOT, false);
        this.entityData.define(ANIMATION, "undefined");
        this.entityData.define(TEXTURE, "atomicbomb");
    }

    public void setTexture(String texture) {
        this.entityData.set(TEXTURE, texture);
    }

    public String getTexture() {
        return (String) this.entityData.get(TEXTURE);
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, ServerPlayer.class, 6.0f));
    }

    public MobType getMobType() {
        return MobType.UNDEFINED;
    }

    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    public SoundEvent getHurtSound(DamageSource ds) {
        return (SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("intentionally_empty"));
    }

    public SoundEvent getDeathSound() {
        return (SoundEvent) ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("intentionally_empty"));
    }

    public boolean causeFallDamage(float l, float d, DamageSource source) {
        AtomicBombEntityFallProcedure.execute(level(), this);
        return super.causeFallDamage(l, d, source);
    }

    public void die(DamageSource source) {
        super.die(source);
        AtomicBombDiesDuplicateProcedure.execute(level(), getX(), getY(), getZ(), this);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Texture", getTexture());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Texture")) {
            setTexture(compound.getString("Texture"));
        }
    }

    public void baseTick() {
        super.baseTick();
        AtomicBombOnEntityTickUpdateProcedure.execute(level(), this);
        refreshDimensions();
    }

    public EntityDimensions getDimensions(Pose p_33597_) {
        return super.getDimensions(p_33597_).scale(1.0f);
    }

    public void aiStep() {
        super.aiStep();
        updateSwingTime();
    }

    @Override
    public ResourceLocation getPipelineKey() {
        return GeoRenderKeys.ATOMIC_BOMB;
    }

    @Override
    public String getPipelineTextureKey() {
        return getTexture();
    }

    public static void init() {
        SpawnPlacements.register((EntityType) BigExplosivesModEntities.ATOMIC_BOMB.get(), SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (v0, v1, v2, v3, v4) -> {
            return Mob.checkMobSpawnRules(v0, v1, v2, v3, v4);
        });
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        return builder.add(Attributes.MOVEMENT_SPEED, 0.0d).add(Attributes.MAX_HEALTH, 10.0d).add(Attributes.ARMOR, 0.0d).add(Attributes.ATTACK_DAMAGE, 3.0d).add(Attributes.FOLLOW_RANGE, 16.0d);
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("AtomicBomb.model.new "));
        }
        return PlayState.STOP;
    }

    private PlayState procedurePredicate(AnimationState event) {
        if ((!this.animationprocedure.equals("empty") && event.getController().getAnimationState() == AnimationController.State.STOPPED) || (!this.animationprocedure.equals(this.prevAnim) && !this.animationprocedure.equals("empty"))) {
            if (!this.animationprocedure.equals(this.prevAnim)) {
                event.getController().forceAnimationReset();
            }
            event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
            if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
                this.animationprocedure = "empty";
                event.getController().forceAnimationReset();
            }
        } else if (this.animationprocedure.equals("empty")) {
            this.prevAnim = "empty";
            return PlayState.STOP;
        }
        this.prevAnim = this.animationprocedure;
        return PlayState.CONTINUE;
    }

    protected void tickDeath() {
        this.deathTime++;
        if (this.deathTime == 1) {
            remove(Entity.RemovalReason.KILLED);
            dropExperience();
        }
    }

    public String getSyncedAnimation() {
        return (String) this.entityData.get(ANIMATION);
    }

    public void setAnimation(String animation) {
        this.entityData.set(ANIMATION, animation);
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController[]{new AnimationController(this, "movement", 4, this::movementPredicate)});
        data.add(new AnimationController[]{new AnimationController(this, "procedure", 4, this::procedurePredicate)});
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
