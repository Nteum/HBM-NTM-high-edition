package com.hbm.entity.mob;

import com.hbm.HBMKey;
import com.hbm.api.entity.IResistanceProvider;
import com.hbm.config.MobConfig;
import com.hbm.entity.ModEntityType;
import com.hbm.main.ResourceManager;
import com.hbm.registries.HBMDamage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * 异虫
 * - 凋落物现在用数据生成配置
 * - 生物的AI行动逻辑通过设定对应的Goal实现
 * */
public class EntityGlyphid extends Monster implements IResistanceProvider {

	public BlockPos home = null;

	//used for digging, bigger glyphids have a longer reach
	public int blastSize = Math.min((int) (3 * (getScale())) / 2, 5);
	public int blastResToDig = Math.min((int) (50 * (getScale() * 2)), 150);
	public boolean shouldDig;

	//subtypes
	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_INFECTED = 1;
	public static final int TYPE_RADIOACTIVE = 2;

	//data watcher keys
	public static final EntityDataAccessor<Byte> DATA_WALL = SynchedEntityData.defineId(EntityGlyphid.class, EntityDataSerializers.BYTE);
	public static final EntityDataAccessor<Byte> DATA_ARMOR = SynchedEntityData.defineId(EntityGlyphid.class, EntityDataSerializers.BYTE);
	public static final EntityDataAccessor<Byte> DATA_SUBTYPE = SynchedEntityData.defineId(EntityGlyphid.class, EntityDataSerializers.BYTE);

	public EntityGlyphid(EntityType<? extends Monster> entityType, Level level) {
		this(level);
	}
	public EntityGlyphid(Level level) {
		super(ModEntityType.GLYPHID.get(), level);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_WALL, (byte) 0);		//wall climbing
		this.entityData.define(DATA_ARMOR, (byte) 0b11111);	//armor
		this.entityData.define(DATA_SUBTYPE, (byte) 0);		//subtype (i.e. normal, infected, etc)
	}

	public static AttributeSupplier.@NotNull Builder createMobAttributes() {
		return Monster.createMobAttributes()
				.add(Attributes.MAX_HEALTH, GlyphidStatus.GRUND.health)
				.add(Attributes.MOVEMENT_SPEED, GlyphidStatus.GRUND.speed)
				.add(Attributes.ATTACK_DAMAGE, GlyphidStatus.GRUND.damage);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		// 流体中漂浮
		this.goalSelector.addGoal(1, new FloatGoal(this));
		// 向目标跳跃
		this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
		// 追踪并攻击玩家
		this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0f, false));
		// 随机移动并避免踏入水中
		this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8D));
		// 看向玩家
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
		// 随意地向四处看
		this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
		// 归巢
		this.goalSelector.addGoal(5, new GoHomeGoal(this, 0.6d));
		// 受到目标生物伤害
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		// 寻找最近可攻击的玩家
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	public ResourceLocation getSkin() {
		return ResourceManager.glyphid_tex;
	}

	@Override
	public float getScale() {
		// 默认就是1
		return super.getScale();
	}
	/**
	 * 异虫的状态
	 * */
	public GlyphidStatus getStats() {
		return GlyphidStatus.GRUND;
	}

	@Override
	public float[] getCurrentDTDR(DamageSource damage, float amount, float pierceDT, float pierce) {
		if (damage.is(DamageTypes.STARVE)) return new float[] {0F, 0F};
		GlyphidStatus stats = this.getStats();
		float threshold = stats.thresholdMultForArmor * getGlyphidArmor() / 5F;

		if(damage.is(DamageTypeTags.IS_FIRE)) return new float[] {0F, stats.resistanceMult * 0.2F}; //fire ignores DT and most DR
		if(damage.is(DamageTypeTags.IS_EXPLOSION)) return new float[] {threshold * 0.5F, stats.resistanceMult * 0.35F}; //explosions  are still subject to DT and reduce DR by a fair amount

		if(damage.is(HBMDamage.NUKE)) return new float[] {threshold * 0.25F, 0F}; // nukes shred shrough glyphids
		if(damage.type().equals(level().registryAccess().registry(Registries.DAMAGE_TYPE).get().getHolderOrThrow(HBMDamage.LASER).getType())) return new float[] {threshold * 0.5F, stats.resistanceMult * 0.5F}; //lasers are quite powerful too
		if(damage.type().equals(level().registryAccess().registry(Registries.DAMAGE_TYPE).get().getHolderOrThrow(HBMDamage.ELECTRIC).getType())) return new float[] {threshold * 0.25F, stats.resistanceMult * 0.25F}; //electricity even more so
		if(damage.type().equals(level().registryAccess().registry(Registries.DAMAGE_TYPE).get().getHolderOrThrow(HBMDamage.SUBAUTOMIC).getType())) return new float[] {0F, stats.resistanceMult * 0.1F}; //and particles are almsot commpletely unaffected

		return new float[] {threshold, stats.resistanceMult};
	}

	@Override
	public void onDamageDealt(DamageSource damage, float amount) {
		if(this.isArmorBroken(amount)) this.breakOffArmor();
	}

	// 处理伤害数据似乎需要改写hurt函数
	@Override
	public boolean hurt(DamageSource pSource, float pAmount) {
		return super.hurt(pSource, pAmount);
	}

	protected boolean canDig() {
		return MobConfig.rampantDig;
	}

	@Override
	public void tick() {
		super.tick();

		if (!level().isClientSide()){
			if (home == null) home = this.blockPosition();
		}
	}
	// 序列化数据
	@Override
	public void addAdditionalSaveData(CompoundTag pCompound) {
		super.addAdditionalSaveData(pCompound);
		if (this.home != null)
			pCompound.put(HBMKey.POSITION, NbtUtils.writeBlockPos(this.home));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag pCompound) {
		super.readAdditionalSaveData(pCompound);
		if (pCompound.contains(HBMKey.POSITION, Tag.TAG_COMPOUND))
			this.home = NbtUtils.readBlockPos(pCompound.getCompound(HBMKey.POSITION));
	}

	public boolean isArmorBroken(float amount) {
		return this.random.nextInt(100) <= Math.min(Math.pow(amount * 0.6, 2), 100);
	}

	public void breakOffArmor() {
		byte armor = this.entityData.get(DATA_ARMOR);
		List<Integer> indices = Arrays.asList(0, 1, 2, 3, 4);
		Collections.shuffle(indices);

		for(Integer i : indices) {
			byte bit = (byte) (1 << i);
			if((armor & bit) > 0) {
				armor &= ~bit;
				armor = (byte) (armor & 0b11111);
				this.entityData.set(DATA_ARMOR, armor);
				this.level().playSound(null, getOnPos(), SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 1.0f, 1.25f);
				break;
			}
		}
	}

	public int getGlyphidArmor() {
		int total = 0;
		byte armor = this.entityData.get(DATA_ARMOR);
		List<Integer> indices = Arrays.asList(0, 1, 2, 3, 4);
		for(Integer i : indices) {
			total += (armor & (1 << i)) != 0 ? 1 : 0;
		}
		return total;
	}

	/**
	 * 异虫归巢
	 * */
	public static class GoHomeGoal extends Goal{
		public static final int DEFAULT_INTERVAL = 120;
		protected final PathfinderMob mob;
		protected final double speedModifier;
		protected Vec3 target = null;
		public GoHomeGoal(PathfinderMob pMob, double pSpeedModifier) {
			this.mob = pMob;
			this.speedModifier = pSpeedModifier;
			this.setFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		@Override
		public boolean canUse() {
			if (this.mob.isVehicle()) {
				return false;
			} else {
				if (this.mob instanceof EntityGlyphid glyphid){
					this.target = glyphid.home == null ? null : glyphid.home.getCenter();
				}

				if (this.target != null){
					// 距离出生点64格外开始归巢，距离0.5以内视为归巢完成
					double dist2Home = this.mob.position().distanceTo(target);
					return dist2Home > 64;
				}
			}
			return false;
		}

		@Override
		public boolean canContinueToUse() {
			if (this.mob.isVehicle()){
				return false;
			} else {
				if (this.target != null){
					// 距离出生点64格外开始归巢，距离0.5以内视为归巢完成
					double dist2Home = this.mob.position().distanceTo(target);
					return dist2Home > 15;
				}
			}
			return false;
		}

		@Override
		public void start() {
			this.mob.getNavigation().moveTo(target.x, target.y, target.z, speedModifier);
		}

		@Override
		public void stop() {
			this.mob.getNavigation().stop();
			super.stop();
		}
	}

	public static class BuildHiveGoal extends Goal{

		@Override
		public boolean canUse() {
			return false;
		}
	}

	public static class CommunicateGoal extends Goal{

		@Override
		public boolean canUse() {
			return false;
		}
	}

	public static class TerraformGoal extends Goal{

		@Override
		public boolean canUse() {
			return false;
		}
	}

	public static class DigableChaseGoal extends HurtByTargetGoal{

		public DigableChaseGoal(PathfinderMob pMob, Class<?>... pToIgnoreDamage) {
			super(pMob, pToIgnoreDamage);
		}
	}

	public boolean getCanSpawnHere(){
		if (level().getBlockState(this.blockPosition()).canBeReplaced()){
			return true;
		}
		return false;
	}
}
