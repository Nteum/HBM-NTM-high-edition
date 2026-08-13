package com.hbm.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import static com.hbm.config.CommonConfig.addBoolean;
import static com.hbm.config.CommonConfig.addInt;


public class SpaceConfig {
	// Dimension ID limit is over 4 million, so go crazy
	public static int moonDimension = 413_015;
	public static int dunaDimension = 413_016;
	public static int ikeDimension = 413_017;
	public static int eveDimension = 413_018;
	public static int dresDimension = 413_019;
	public static int mohoDimension = 413_020;
	public static int minmusDimension = 413_021;
	public static int laytheDimension = 413_022;
	public static int orbitDimension = 413_023;
	public static int tektoDimension = 413_024;
	public static int thatmoDimension = 413_025;

	// Biome ID limit is 255

	// NOTE: some popular mod biome IDs, try to avoid colliding with these

	// Vanilla biomes: 0-39
	// Vanilla mutated variants: 128-167
	// Biomes O' Plenty: 41-124 (oof)
	// Aether Legacy: 127
	// Galacticraft: 102+ (variable, ugh, just assume up to 110 I guess)
	// Advanced Rocketry: "maxBiomes = 512" - ...that isn't even valid but okay
	// Witchery: needs investigating
	// Thaumcraft: needs investigating
	// The Twilight Forest: 40-58
	// ExtraBiomesXL: needs investigating
	// NTM upstream: 80-82 for craters

	// Most mods start at 40 and go up, so easiest way to avoid conflicts is to count backwards!

	public static int orbitBiome = 126;

	public static int moonBiome = 125;
	public static int minmusBiome = 124;
	public static int minmusBasinBiome = 123;

	public static int dunaBiome = 122;
	public static int dunaLowlandsBiome = 121;
	public static int dunaPolarBiome = 120;
	public static int dunaHillsBiome = 119;
	public static int dunaPolarHillsBiome = 118;

	public static int eveBiome = 117;
	public static int eveMountainsBiome = 116;
	public static int eveOceanBiome = 115;
	public static int eveSeismicBiome = 114;
	public static int eveRiverBiome = 113;

	public static int dresBiome = 112;
	public static int dresBasinBiome = 111;

	public static int mohoBiome = 101;
	public static int mohoBasaltBiome = 100;
	public static int mohoLavaBiome = 99;
	public static int mohoPlateauBiome = 98;

	public static int laytheBiome = 97;
	public static int laytheOceanBiome = 96;
	public static int laythePolarBiome = 95;
	public static int laytheCoastBiome = 94;

	public static int ikeBiome = 93;

	public static int tektoPolyvinylBiome = 92;
	public static int tektoHalogenHillBiome = 91;
	public static int tektoRiverBiome = 90;
	public static int tektoForestBiome = 89;
	public static int tektoVinylIslandBiome = 88;

	public static int thatmoBiome = 87;

	public static boolean allowNetherPortals = false;

	public static boolean enableVolcanoGen = true;

	public static boolean crashOnBiomeConflict = true;

	public static boolean showOreLocations = true;

	public static int maxProbeDistance = 32_000;
	public static int maxStationDistance = 32_000;
	
	public static boolean combatPodDespawn = false;

	public static void addToConfig(final ForgeConfigSpec.Builder builder) {
		builder.push(CommonConfig.CATEGORY_DIMS);
		addBoolean(builder, "17.00_allowNetherPortals", "Should Nether portals function on other celestial bodies?", allowNetherPortals);
		builder.pop();

		builder.push(CommonConfig.CATEGORY_GENERAL);
		addInt(builder, "1.90_maxProbeDistance", "How far from the center of the dimension can probes generate landing coordinates", maxProbeDistance);
		addInt(builder, "1.93_maxStationDistance", "How far from the center of the dimension can orbital stations be generated", maxStationDistance, 1024, Integer.MAX_VALUE);
		addBoolean(builder, "1.91_enableVolcanoGen", "Should volcanoes be active when spawning, disabling will prevent natural volcanoes from spewing lava and growing", enableVolcanoGen);
		addBoolean(builder, "1.92_crashOnBiomeConflict", "To avoid biome ID collisions, the game will crash if one occurs, and give instructions on how to fix. Only disable this if you know what you're doing!", crashOnBiomeConflict);
		addBoolean(builder, "1.93_showOreLocations", "Should ores indicate which planets they can be found on.", showOreLocations);
		addBoolean(builder, "1.94_combatPodDespawn", "wether combat pods should despawn after a certian amount of time.", combatPodDespawn);
		// 用于适应EndlessIDs的措施，高版本不用考虑这些
		int defaultBiomeOffset = 0;
		builder.pop();
	}

	public static void onLoad(final ModConfigEvent event){
		CommentedConfig configData = event.getConfig().getConfigData();
		CommentedConfig config;
		if (configData.contains(CommonConfig.CATEGORY_DIMS) && (config = configData.get(CommonConfig.CATEGORY_DIMS)) instanceof CommentedConfig){
			allowNetherPortals = config.get("17.00_allowNetherPortals");
		}
		if (configData.contains(CommonConfig.CATEGORY_GENERAL) && (config = configData.get(CommonConfig.CATEGORY_GENERAL)) instanceof CommentedConfig){
			maxProbeDistance = config.get("1.90_maxProbeDistance");
			maxStationDistance = config.get("1.93_maxStationDistance");
			enableVolcanoGen = config.get("1.91_enableVolcanoGen");
			crashOnBiomeConflict = config.get("1.92_crashOnBiomeConflict");
			showOreLocations = config.get("1.93_showOreLocations");
			combatPodDespawn = config.get("1.94_combatPodDespawn");
		}
	}
}
