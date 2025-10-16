package com.hbm.item.weapon;

import com.hbm.HBMLang;
import com.hbm.entity.weapon.missile.EntityMissile;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.Model;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class ItemMissile extends ItemCustomLore {

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
	public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> list, TooltipFlag pIsAdvanced) {
		list.add(Component.translatable(HBMLang.ITEM_MISSILE_TIER.key(), this.tier.ordinal()).withStyle(ChatFormatting.ITALIC));
		if (!launchable){
			list.add(Component.translatable(HBMLang.ITEM_MISSILE_DESC_NOTLAUNCHABLE.key()).withStyle(ChatFormatting.RED));
		} else {
			list.add((Component.translatable(HBMLang.FUEL.key()).append(": ").append(this.fuel.getDisplay())).withStyle(ChatFormatting.RED));
			if (fuelCap > 0) list.add(Component.translatable(HBMLang.FUEL_CAPACITY.key(), fuelCap));
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
