package net.mcreator.nuclearcraft.entity;

import java.io.IOException;
import net.mcreator.nuclearcraft.init.BigExplosivesModEntities;
import net.mcreator.nuclearcraft.procedures.FiveBombEntityDiesProcedure;
import net.mcreator.nuclearcraft.procedures.FiveBombWaterExplodeProcedure;
import net.mcreator.nuclearcraft.procedures.FiveHundredKgBombEntityDiesProcedure;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/entity/FiveBombEntity.class */
public class FiveBombEntity extends PathfinderMob implements GeoEntity {
    public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.m_135353_(FiveBombEntity.class, EntityDataSerializers.f_135035_);
    public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.m_135353_(FiveBombEntity.class, EntityDataSerializers.f_135030_);
    public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.m_135353_(FiveBombEntity.class, EntityDataSerializers.f_135030_);
    private final AnimatableInstanceCache cache;
    private boolean swinging;
    private boolean lastloop;
    private long lastSwing;
    public String animationprocedure;
    String prevAnim;

    public FiveBombEntity(PlayMessages.SpawnEntity packet, Level world) {
        this((EntityType<FiveBombEntity>) BigExplosivesModEntities.FIVE_BOMB.get(), world);
    }

    public FiveBombEntity(EntityType<FiveBombEntity> type, Level world) {
        super(type, world);
        this.cache = GeckoLibUtil.createInstanceCache(this);
        this.animationprocedure = "empty";
        this.prevAnim = "empty";
        this.f_21364_ = 0;
        m_21557_(false);
        m_274367_(0.6f);
        m_21530_();
    }

    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(SHOOT, false);
        this.f_19804_.m_135372_(ANIMATION, "undefined");
        this.f_19804_.m_135372_(TEXTURE, "500kg");
    }

    public void setTexture(String texture) {
        this.f_19804_.m_135381_(TEXTURE, texture);
    }

    public String getTexture() {
        return (String) this.f_19804_.m_135370_(TEXTURE);
    }

    public Packet<ClientGamePacketListener> m_5654_() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    protected void m_8099_() {
        super.m_8099_();
    }

    public MobType m_6336_() {
        return MobType.f_21640_;
    }

    public boolean m_6785_(double distanceToClosestPlayer) {
        return false;
    }

    public boolean m_142535_(float l, float d, DamageSource source) throws IOException {
        FiveHundredKgBombEntityDiesProcedure.execute(m_9236_(), m_20185_(), m_20186_(), m_20189_());
        return super.m_142535_(l, d, source);
    }

    public void m_6667_(DamageSource source) {
        super.m_6667_(source);
        FiveBombEntityDiesProcedure.execute(m_9236_(), m_20185_(), m_20186_(), m_20189_());
    }

    public void m_7380_(CompoundTag compound) {
        super.m_7380_(compound);
        compound.m_128359_("Texture", getTexture());
    }

    public void m_7378_(CompoundTag compound) {
        super.m_7378_(compound);
        if (compound.m_128441_("Texture")) {
            setTexture(compound.m_128461_("Texture"));
        }
    }

    public void m_6075_() {
        super.m_6075_();
        FiveBombWaterExplodeProcedure.execute(m_9236_(), m_20185_(), m_20186_(), m_20189_(), this);
        m_6210_();
    }

    public EntityDimensions m_6972_(Pose p_33597_) {
        return super.m_6972_(p_33597_).m_20388_(1.0f);
    }

    public void m_8107_() {
        super.m_8107_();
        m_21203_();
    }

    public static void init() {
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.m_21552_();
        return builder.m_22268_(Attributes.f_22279_, 0.0d).m_22268_(Attributes.f_22276_, 10.0d).m_22268_(Attributes.f_22284_, 0.0d).m_22268_(Attributes.f_22281_, 3.0d).m_22268_(Attributes.f_22277_, 16.0d);
    }

    private PlayState movementPredicate(AnimationState event) {
        if (this.animationprocedure.equals("empty")) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("500kg.model.new"));
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

    protected void m_6153_() {
        this.f_20919_++;
        if (this.f_20919_ == 1) {
            m_142687_(Entity.RemovalReason.KILLED);
            m_21226_();
        }
    }

    public String getSyncedAnimation() {
        return (String) this.f_19804_.m_135370_(ANIMATION);
    }

    public void setAnimation(String animation) {
        this.f_19804_.m_135381_(ANIMATION, animation);
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController[]{new AnimationController(this, "movement", 4, this::movementPredicate)});
        data.add(new AnimationController[]{new AnimationController(this, "procedure", 4, this::procedurePredicate)});
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
