package com.hbm.item.weapon;

import com.hbm.HBMLang;
import com.hbm.entity.weapon.missile.EntityMissile;
import com.hbm.entity.weapon.missile.EntityMissileAntiBallistic;
import com.hbm.registries.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.client.model.Model;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class ItemMissile extends ItemCustomLore {
	private static final String TAG_LAUNCH_MODE = "launch_mode";
	private static final double DIRECT_GUIDANCE_SPEED = 3.8D;
	private static final float ABM_DIRECT_SPEED = 3.8F;
	private static final float ABM_THROW_SPEED = 1.35F;
	private static final float MISSILE_THROW_SPEED = 1.35F;

	public final MissileFormFactor formFactor;
	public final MissileTier tier;
	public final MissileFuel fuel;
	public int fuelCap;
	public boolean launchable = true;
	public EntityMissile.MissileCreator missileCreator;
	public Supplier<Model> model = null;

	public ItemMissile(Properties pProperties, MissileFormFactor form, MissileTier tier, EntityMissile.MissileCreator missileCreator) {
		this(pProperties.stacksTo(1), form, tier, form.defaultFuel, missileCreator);
	}

	public ItemMissile(Properties pProperties, MissileFormFactor form, MissileTier tier, MissileFuel fuel, EntityMissile.MissileCreator missileCreator) {
		super(pProperties);
		this.formFactor = form;
		this.tier = tier;
		this.fuel = fuel;
		this.setFuelCap(this.fuel.defaultCap);
		this.missileCreator = missileCreator;
	}

	public ItemMissile setModel(Supplier<Model> model){
		this.model = model;
		return this;
	}

	public ItemMissile notLaunchable() {
		this.launchable = false;
		return this;
	}

	public ItemMissile setFuelCap(int fuelCap) {
		this.fuelCap = fuelCap;
		return this;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
		ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);

		if (!this.launchable) {
			return InteractionResultHolder.fail(itemStack);
		}
		// 需求：创造模式下可手持导弹直接发射，生存模式仍走发射平台流程。
		if (!pPlayer.getAbilities().instabuild) {
			return InteractionResultHolder.pass(itemStack);
		}

		LaunchMode launchMode = getLaunchMode(itemStack);
		Entity spawned = null;
		if (!pLevel.isClientSide) {
			Vec3 look = pPlayer.getLookAngle();
			Vec3 spawnPos = pPlayer.getEyePosition().add(look.scale(2.0D));
			BlockPos target = resolveAimTarget(pPlayer);

			if (this.formFactor == MissileFormFactor.ABM) {
				EntityMissileAntiBallistic abm = new EntityMissileAntiBallistic(pLevel);
				abm.setOwner(pPlayer);
				abm.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
				configureAbmLaunch(abm, pPlayer, launchMode);
				spawned = abm;
			} else if (this.missileCreator != null) {
				EntityMissile missile = this.missileCreator.create(
						pLevel,
						(float) spawnPos.x,
						(float) spawnPos.y,
						(float) spawnPos.z,
						target);
				if (missile != null) {
					missile.setOwner(pPlayer);
					configureMissileLaunch(missile, pPlayer, launchMode, target);
					spawned = missile;
				}
			}

			if (spawned == null) {
				return InteractionResultHolder.fail(itemStack);
			}
			pLevel.addFreshEntity(spawned);
		}

		pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(),
				ModSounds.WEAPON_MISSILE_TAKE_OFF.get(), SoundSource.PLAYERS, 1.8F, 1.0F);
		pPlayer.awardStat(Stats.ITEM_USED.get(this));
		return InteractionResultHolder.sidedSuccess(itemStack, pLevel.isClientSide);
	}

	private static void configureMissileLaunch(EntityMissile missile, Player player, LaunchMode launchMode, BlockPos target) {
		switch (launchMode) {
			case DIRECT -> missile.configureDirectFlight(target, DIRECT_GUIDANCE_SPEED);
			case THROWN -> {
				missile.configureThrownFlight();
				missile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, MISSILE_THROW_SPEED, 1.0F);
			}
			case BALLISTIC -> missile.configureBallisticFlight(target);
		}
	}

	private static void configureAbmLaunch(EntityMissileAntiBallistic abm, Player player, LaunchMode launchMode) {
		switch (launchMode) {
			case DIRECT -> abm.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, ABM_DIRECT_SPEED, 0.04F);
			case THROWN -> abm.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, ABM_THROW_SPEED, 1.0F);
			case BALLISTIC -> abm.shootFromRotation(player, player.getXRot() - 30.0F, player.getYRot(), 0.0F, 2.2F, 0.12F);
		}
	}

	private static BlockPos resolveAimTarget(Player player) {
		HitResult hitResult = player.pick(400.0D, 1.0F, false);
		Vec3 loc = hitResult.getLocation();
		return BlockPos.containing(loc.x, loc.y, loc.z);
	}

	public static LaunchMode getLaunchMode(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		int ordinal = tag != null ? tag.getInt(TAG_LAUNCH_MODE) : 0;
		if (ordinal < 0 || ordinal >= LaunchMode.values().length) {
			return LaunchMode.BALLISTIC;
		}
		return LaunchMode.values()[ordinal];
	}

	public static LaunchMode cycleLaunchMode(ItemStack stack, int direction) {
		LaunchMode current = getLaunchMode(stack);
		int next = Math.floorMod(current.ordinal() + Integer.signum(direction), LaunchMode.values().length);
		stack.getOrCreateTag().putInt(TAG_LAUNCH_MODE, next);
		return LaunchMode.values()[next];
	}

	public enum LaunchMode {
		BALLISTIC("抛物线"),
		DIRECT("直射"),
		THROWN("投掷");

		private final String display;

		LaunchMode(String display) {
			this.display = display;
		}

		public String display() {
			return this.display;
		}
	}

	@Override
	public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> list, TooltipFlag pIsAdvanced) {
		list.add(Component.translatable(HBMLang.ITEM_MISSILE_TIER.key(), this.tier.ordinal()).withStyle(ChatFormatting.ITALIC));
		if (!launchable){
			list.add(Component.translatable(HBMLang.ITEM_MISSILE_DESC_NOTLAUNCHABLE.key()).withStyle(ChatFormatting.RED));
		} else {
			list.add((Component.translatable(HBMLang.FUEL.key()).append(": ").append(this.fuel.getDisplay())).withStyle(ChatFormatting.RED));
			if (fuelCap > 0) list.add(Component.translatable(HBMLang.FUEL_CAPACITY.key(), fuelCap));
			LaunchMode launchMode = getLaunchMode(pStack);
			list.add(Component.literal("发射模式: " + launchMode.display()).withStyle(ChatFormatting.GRAY));
			list.add(Component.literal("Ctrl/Command + 滚轮切换模式").withStyle(ChatFormatting.DARK_GRAY));
			super.appendHoverText(pStack, pLevel, list, pIsAdvanced);
		}
	}

	public enum MissileFormFactor {
		ABM(MissileFuel.SOLID),
		MICRO(MissileFuel.SOLID),
		V2(MissileFuel.ETHANOL_PEROXIDE),
		STRONG(MissileFuel.KEROSENE_PEROXIDE),
		HUGE(MissileFuel.KEROSENE_LOXY),
		ATLAS(MissileFuel.JETFUEL_LOXY),
		OTHER(MissileFuel.KEROSENE_PEROXIDE);

		protected MissileFuel defaultFuel;

		private MissileFormFactor(MissileFuel defaultFuel) {
			this.defaultFuel = defaultFuel;
		}
	}

	public enum MissileTier {
		TIER0("Tier 0"),
		TIER1("Tier 1"),
		TIER2("Tier 2"),
		TIER3("Tier 3"),
		TIER4("Tier 4");

		public String display;

		private MissileTier(String display) {
			this.display = display;
		}
	}

	public enum MissileFuel {
		SOLID("item.missile.fuel.solid.prefueled", ChatFormatting.GOLD, 0),
		ETHANOL_PEROXIDE("item.missile.fuel.ethanol_peroxide", ChatFormatting.AQUA, 4_000),
		KEROSENE_PEROXIDE("item.missile.fuel.kerosene_peroxide", ChatFormatting.BLUE, 8_000),
		KEROSENE_LOXY("item.missile.fuel.kerosene_loxy", ChatFormatting.LIGHT_PURPLE, 12_000),
		JETFUEL_LOXY("item.missile.fuel.jetfuel_loxy", ChatFormatting.RED, 16_000);

		private final String key;
		public final ChatFormatting color;
		public final int defaultCap;

		private MissileFuel(String key, ChatFormatting color, int defaultCap) {
			this.key = key;
			this.color = color;
			this.defaultCap = defaultCap;
		}

		/** Returns a color localized string for display */
		public Component getDisplay() {
			return Component.translatable(key).withStyle(color);
		}
	}
}
