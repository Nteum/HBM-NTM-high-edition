package com.hbm.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.hbm.HBM;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.List;

import static com.hbm.config.CommonConfig.*;

/** 游戏的总体配置 */

public class ConfigGeneral {
	public static boolean enableThermosPreventer = true;

	public static boolean enableDebugMode = true;
	public static boolean enableMycelium = false;
	public static boolean enablePlutoniumOre = false;
	public static TriFlag enableDungeons = TriFlag.UNDEFINE;
	public static boolean enableMDOres = true;
	public static boolean enableMines = true;
	public static boolean enableRad = true;
	public static boolean enableNITAN = true;
	public static boolean enableBomberShortMode = false;
	public static boolean enableVaults = true;
	public static boolean enableCataclysm = false;
	public static boolean enableExtendedLogging = false;
	public static boolean enableHardcoreTaint = false;
	public static boolean enableGuns = true;
	public static boolean enableVirus = true;
	public static boolean enableCrosshairs = true;
	public static boolean enableReflectorCompat = false;
	public static boolean enableRenderDistCheck = true;
	public static boolean enableReEval = true;
	public static boolean enableSilentCompStackErrors = true;
	public static boolean enableSkyboxes = true;
	public static boolean enableImpactWorldProvider = true;
	public static boolean enableStatReRegistering = true;
	public static boolean enableKeybindOverlap = true;
	public static boolean enableFluidContainerCompat = true;
	public static boolean enableMOTD = true;
	public static boolean enableGuideBook = true;
	public static boolean enableSteamParticles = true;
	public static boolean enableSoundExtension = true;
	public static boolean enableMekanismChanges = true;
	public static int normalSoundChannels = 200;
	public static int hintPos = 0;

	public static boolean enableExpensiveMode = false;

	public static void addConfig(ForgeConfigSpec.Builder builder){
		builder.push(CommonConfig.CATEGORY_GENERAL);

		builder.comment("When set to true, will prevent the mod to launch on Thermos servers. Only disable this if you understand what \"tileentities.yml\" is, and how it severely cripples the mod.").define("enableThermosPreventer", true);
		addBoolean(builder, "enablePacketThreading", true, "Enables creation of a separate thread to increase packet processing speed on servers. Disable this if you are having anomalous crashes related to memory connections.");
		addInt(builder, "packetThreadingCoreCount", 1, "Number of core threads to create for packets (recommended 1).");
		addInt(builder, "packetThreadingMaxCount", 1, "Maximum number of threads to create for packet threading. Must be greater than or equal to packetThreadingCoreCount.");
		addBoolean(builder, "packetThreadingErrorBypass", false, "Forces the bypassing of most packet threading errors, only enable this if directed to or if you know what you're doing.");
		addBoolean(builder, "enableServerRecipeSync", false, "Syncs any recipes customised via JSON to clients connecting to the server.");
		addBoolean(builder, "enableDebugMode", false, "Enable debugging mode");
		addBoolean(builder, "enableMyceliumSpread", false, "Allows glowing mycelium to spread");
		addBoolean(builder, "enablePlutoniumNetherOre", false, "Enables plutonium ore generation in the nether");
		builder.comment("Allows structures and dungeons to spawn.").defineEnum("enableDungeons", TriFlag.UNDEFINE);
		addBoolean(builder, "enableOresInModdedDimensions", true, "Allows NTM ores to generate in modded dimensions");
		addBoolean(builder, "enableLandmineSpawn", true, "Allows landmines to generate");
		addBoolean(builder, "enableRadHotspotSpawn", true, "Allows radiation hotspots to generate");
		addBoolean(builder, "enableNITANChestSpawn", true, "Allows chests to spawn at specific coordinates full of powders");
		addBoolean(builder, "enableBomberShortMode", false, "Has bomber planes spawn in closer to the target for use with smaller render distances");
		addBoolean(builder, "enableVaultSpawn", true, "Allows locked safes to spawn");
		addBoolean(builder, "enableCataclysm", false, "Causes satellites to fall whenever a mob dies");
		addBoolean(builder, "enableExtendedLogging", false, "Logs uses of the detonator, nuclear explosions, missile launches, grenades, etc.");
		addBoolean(builder, "enableGuns", true, "Prevents new system guns to be fired");
		addBoolean(builder, "enableVirus", false, "Allows virus blocks to spread");
		addBoolean(builder, "enableCrosshairs", true, "Shows custom crosshairs when an NTM gun is being held");
		addBoolean(builder,  "enableReflectorCompat", false, "Enable old reflector oredict name (\"plateDenseLead\") instead of new \"plateTungCar\"");
		addBoolean(builder,  "enableRenderDistCheck", true, "Check invalid render distances (over 16, without OptiFine) and fix it");
		addBoolean(builder, "enableSilentCompStackErrors", false, "Enabling this will disable log spam created by unregistered items in ComparableStack instances.");
		addBoolean(builder, "enableSkyboxes", true, "If enabled, will try to use NTM's custom skyboxes.");
		addBoolean(builder, "enableImpactWorldProvider", true, "If enabled, registers custom world provider which modifies lighting and sky colors for post impact effects.");
		addBoolean(builder, "enableStatReRegistering", true, "If enabled, will re-register item crafting/breaking/usage stats in order to fix a forge bug where modded items just won't show up.");
		addBoolean(builder,  "enableKeybindOverlap", true, "If enabled, will handle keybinds that would otherwise be ignored due to overlapping.");
		addBoolean(builder, "enableFluidContainerCompat", true, "If enabled, fluid containers will be oredicted and interchangable in recipes with other mods' containers, as well as TrainCraft's diesel being considered a valid diesel canister.");
		addBoolean(builder, "enableMOTD", true, "If enabled, shows the 'Loaded mod!' chat message as well as update notifications when joining a world");
		addBoolean(builder, "enableGuideBook", true, "If enabled, gives players the guide book when joining the world for the first time");
		addBoolean(builder, "enableSoundExtension", true, "If enabled, will change the limit for how many sounds can play at once.");
		addBoolean(builder, "enableMekanismChanges", true, "If enabled, will change some of Mekanism's recipes.");
		addInt(builder, "normalSoundChannels", 100, "The amount of channels to create while enableSoundExtension is enabled.\n" +
						"Note that a value below 28 or above 200 can cause buggy sounds and issues with other mods running out of sound memory.");
		addStringList(builder, "preferredOutputMod", List.of(HBM.MODID), "The mod which is preferred as output when certain machines autogenerate recipes. Currently used for the shredder");
		addBoolean(builder, "enableLoadScreenReplacement", true, "Tries to replace the vanilla load screen with the 'tip of the day' one, may clash with other mods trying to do the same.");
		addBoolean(builder, "enableExpensiveMode", false, "It does what the name implies.");
		builder.comment("Enables plutonium ore generation in the nether").define("enablePlutoniumOre", true);

		builder.pop();
	}
	public static void loadConfig(final ModConfigEvent event){
		CommentedConfig configData = event.getConfig().getConfigData();
		CommentedConfig config;
		if (configData.contains(CommonConfig.CATEGORY_GENERAL) && (config = configData.get(CommonConfig.CATEGORY_GENERAL)) instanceof CommentedConfig){
			enableThermosPreventer = config.get("enableThermosPreventer");
			enableDebugMode = config.get("enableDebugMode");
			enableMycelium = config.get("enableMyceliumSpread");
			enablePlutoniumOre = config.get("enablePlutoniumOre");
			enableDungeons = config.getEnum("enableDungeons", TriFlag.class);
		}
	}
}
