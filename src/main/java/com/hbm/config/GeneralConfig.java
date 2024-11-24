package com.hbm.config;

import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

/** 游戏的总体配置 */
public class GeneralConfig {
	public static boolean enableThermosPreventer = true;

	public static boolean enableDebugMode = true;
	public static boolean enableMycelium = false;
	public static boolean enablePlutoniumOre = false;
	public static CommonConfig.StructureFlag enableDungeons = CommonConfig.StructureFlag.UNDEFINE;
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

	public static boolean enable528 = false;
	public static boolean enable528ReasimBoilers = true;
	public static boolean enable528ColtanDeposit = true;
	public static boolean enable528ColtanSpawn = false;
	public static boolean enable528BedrockDeposit = true;
	public static boolean enable528BedrockSpawn = false;
	public static boolean enable528BosniaSimulator = true;
	public static boolean enable528BedrockReplacement = true;
	public static boolean enable528NetherBurn = true;
	public static int coltanRate = 2;
	public static int bedrockRate = 50;

	public static boolean enableLBSM = false;
	public static boolean enableLBSMFullSchrab = true;
	public static boolean enableLBSMShorterDecay = true;
	public static boolean enableLBSMSimpleArmorRecipes = true;
	public static boolean enableLBSMSimpleToolRecipes = true;
	public static boolean enableLBSMSimpleAlloy = true;
	public static boolean enableLBSMSimpleChemsitry = true;
	public static boolean enableLBSMSimpleCentrifuge = true;
	public static boolean enableLBSMUnlockAnvil = true;
	public static boolean enableLBSMSimpleCrafting = true;
	public static boolean enableLBSMSimpleMedicineRecipes = true;
	public static boolean enableLBSMSafeCrates = true;
	public static boolean enableLBSMSafeMEDrives = true;
	public static boolean enableLBSMIGen = true;
	public static int schrabRate = 20;
	
	public static BooleanValue _enableThermosPreventer ;
	public static BooleanValue _enableDebugMode ;
	public static BooleanValue _enableMycelium ;
	public static BooleanValue _enablePlutoniumOre ;
	public static EnumValue _enableDungeons;
	public static BooleanValue _enableMDOres ;
	public static BooleanValue _enableMines ;
	public static BooleanValue _enableRad ;
	public static BooleanValue _enableNITAN ;
	public static BooleanValue _enableBomberShortMode ;
	public static BooleanValue _enableVaults ;
	public static BooleanValue _enableCataclysm ;
	public static BooleanValue _enableExtendedLogging ;
	public static BooleanValue _enableHardcoreTaint ;
	public static BooleanValue _enableGuns ;
	public static BooleanValue _enableVirus ;
	public static BooleanValue _enableCrosshairs ;
	public static BooleanValue _enableReflectorCompat ;
	public static BooleanValue _enableRenderDistCheck ;
	public static BooleanValue _enableReEval ;
	public static BooleanValue _enableSilentCompStackErrors ;
	public static BooleanValue _enableSkyboxes ;
	public static BooleanValue _enableImpactWorldProvider ;
	public static BooleanValue _enableStatReRegistering ;
	public static BooleanValue _enableKeybindOverlap ;
	public static BooleanValue _enableFluidContainerCompat ;
	public static BooleanValue _enableMOTD ;
	public static BooleanValue _enableGuideBook ;
	public static BooleanValue _enableSteamParticles ;
	public static BooleanValue _enableSoundExtension ;
	public static BooleanValue _enableMekanismChanges ;
	public static IntValue _normalSoundChannels;
	public static IntValue _hintPos;
	public static BooleanValue _enableExpensiveMode ;
	public static BooleanValue _enable528 ;
	public static BooleanValue _enable528ReasimBoilers ;
	public static BooleanValue _enable528ColtanDeposit ;
	public static BooleanValue _enable528ColtanSpawn ;
	public static BooleanValue _enable528BedrockDeposit ;
	public static BooleanValue _enable528BedrockSpawn ;
	public static BooleanValue _enable528BosniaSimulator ;
	public static BooleanValue _enable528BedrockReplacement ;
	public static BooleanValue _enable528NetherBurn ;
	public static IntValue _coltanRate;
	public static IntValue _bedrockRate;
	public static BooleanValue _enableLBSM ;
	public static BooleanValue _enableLBSMFullSchrab ;
	public static BooleanValue _enableLBSMShorterDecay ;
	public static BooleanValue _enableLBSMSimpleArmorRecipes ;
	public static BooleanValue _enableLBSMSimpleToolRecipes ;
	public static BooleanValue _enableLBSMSimpleAlloy ;
	public static BooleanValue _enableLBSMSimpleChemsitry ;
	public static BooleanValue _enableLBSMSimpleCentrifuge ;
	public static BooleanValue _enableLBSMUnlockAnvil ;
	public static BooleanValue _enableLBSMSimpleCrafting ;
	public static BooleanValue _enableLBSMSimpleMedicineRecipes ;
	public static BooleanValue _enableLBSMSafeCrates ;
	public static BooleanValue _enableLBSMSafeMEDrives ;
	public static BooleanValue _enableLBSMIGen ;
	public static IntValue _schrabRate;
	public static final ForgeConfigSpec CONFIG_SPEC;

	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.push(CommonConfig.CATEGORY_GENERAL);

		_enableThermosPreventer = builder.comment("When set to true, will prevent the mod to launch on Thermos servers. Only disable this if you understand what \"tileentities.yml\" is, and how it severely cripples the mod.").define("enableThermosPreventer", true);
		_enableDebugMode = builder.comment("Enable debugging mode").define("enableDebugMode", true);
		_enableMycelium = builder.comment("Allows glowing mycelium to spread").define("enableMycelium", true);
		_enablePlutoniumOre = builder.comment("Enables plutonium ore generation in the nether").define("enablePlutoniumOre", true);
		_enableDungeons = builder.comment("Allows structures and dungeons to spawn.").defineEnum("enableDungeons", CommonConfig.StructureFlag.UNDEFINE);


		builder.pop();
		CONFIG_SPEC = builder.build();
	}

	@SubscribeEvent
	static void onLoad(final ModConfigEvent event)
	{
		enableThermosPreventer = _enableThermosPreventer.get();

		enableDebugMode = _enableDebugMode.get();
		enableMycelium = _enableMycelium.get();
		enablePlutoniumOre = _enablePlutoniumOre.get();
		enableDungeons = (CommonConfig.StructureFlag) _enableDungeons.get();
		enableMDOres = _enableMDOres.get();
		enableMines = _enableMines.get();
		enableRad = _enableRad.get();
		enableNITAN = _enableNITAN.get();
		enableBomberShortMode = _enableBomberShortMode.get();
		enableVaults = _enableVaults.get();
		enableCataclysm = _enableCataclysm.get();
		enableExtendedLogging = _enableExtendedLogging.get();
		enableHardcoreTaint = _enableHardcoreTaint.get();
		enableGuns = _enableGuns.get();
		enableVirus = _enableVirus.get();
		enableCrosshairs = _enableCrosshairs.get();
		enableReflectorCompat = _enableReflectorCompat.get();
		enableRenderDistCheck = _enableRenderDistCheck.get();
		enableReEval = _enableReEval.get();
		enableSilentCompStackErrors = _enableSilentCompStackErrors.get();
		enableSkyboxes = _enableSkyboxes.get();
		enableImpactWorldProvider = _enableImpactWorldProvider.get();
		enableStatReRegistering = _enableStatReRegistering.get();
		enableKeybindOverlap = _enableKeybindOverlap.get();
		enableFluidContainerCompat = _enableFluidContainerCompat.get();
		enableMOTD = _enableMOTD.get();
		enableGuideBook = _enableGuideBook.get();
		enableSteamParticles = _enableSteamParticles.get();
		enableSoundExtension = _enableSoundExtension.get();
		enableMekanismChanges = _enableMekanismChanges.get();
		normalSoundChannels = _normalSoundChannels.get();
		hintPos = _hintPos.get();
		enableExpensiveMode = _enableExpensiveMode.get();
		enable528 = _enable528.get();
		enable528ReasimBoilers = _enable528ReasimBoilers.get();
		enable528ColtanDeposit = _enable528ColtanDeposit.get();
		enable528ColtanSpawn = _enable528ColtanSpawn.get();
		enable528BedrockDeposit = _enable528BedrockDeposit.get();
		enable528BedrockSpawn = _enable528BedrockSpawn.get();
		enable528BosniaSimulator = _enable528BosniaSimulator.get();
		enable528BedrockReplacement = _enable528BedrockReplacement.get();
		enable528NetherBurn = _enable528NetherBurn.get();
		coltanRate = _coltanRate.get();
		bedrockRate = _bedrockRate.get();
		enableLBSM = _enableLBSM.get();
		enableLBSMFullSchrab = _enableLBSMFullSchrab.get();
		enableLBSMShorterDecay = _enableLBSMShorterDecay.get();
		enableLBSMSimpleArmorRecipes = _enableLBSMSimpleArmorRecipes.get();
		enableLBSMSimpleToolRecipes = _enableLBSMSimpleToolRecipes.get();
		enableLBSMSimpleAlloy = _enableLBSMSimpleAlloy.get();
		enableLBSMSimpleChemsitry = _enableLBSMSimpleChemsitry.get();
		enableLBSMSimpleCentrifuge = _enableLBSMSimpleCentrifuge.get();
		enableLBSMUnlockAnvil = _enableLBSMUnlockAnvil.get();
		enableLBSMSimpleCrafting = _enableLBSMSimpleCrafting.get();
		enableLBSMSimpleMedicineRecipes = _enableLBSMSimpleMedicineRecipes.get();
		enableLBSMSafeCrates = _enableLBSMSafeCrates.get();
		enableLBSMSafeMEDrives = _enableLBSMSafeMEDrives.get();
		enableLBSMIGen = _enableLBSMIGen.get();
		schrabRate = _schrabRate.get();
	}
	
//	public static void loadFromConfig(Configuration config) {
//
//		final String CATEGORY_GENERAL = CommonConfig.CATEGORY_GENERAL;
//		enableThermosPreventer = config.get(CATEGORY_GENERAL, "0.00_crashOnThermos", true, "When set to true, will prevent the mod to launch on Thermos servers. Only disable this if you understand what \"tileentities.yml\" is, and how it severely cripples the mod.").getBooleanValue(true);
//		enableDebugMode = config.get(CATEGORY_GENERAL, "1.00_enableDebugMode", false, "Enable debugging mode").getBooleanValue(false);
//		enableMycelium = config.get(CATEGORY_GENERAL, "1.01_enableMyceliumSpread", false, "Allows glowing mycelium to spread").getBooleanValue(false);
//		enablePlutoniumOre = config.get(CATEGORY_GENERAL, "1.02_enablePlutoniumNetherOre", false, "Enables plutonium ore generation in the nether").getBooleanValue(false);
//
//		String unparsedDungeonFlag = config.get(CATEGORY_GENERAL, "1.03_enableDungeonSpawn", "flag", "Allows structures and dungeons to spawn. Valid values are true|false|flag - flag will respect the \"Generate Structures\" world flag.").getString();
//		enableDungeons = CommonConfig.parseStructureFlag(unparsedDungeonFlag);
//
//		enableMDOres = config.get(CATEGORY_GENERAL, "1.04_enableOresInModdedDimensions", true, "Allows NTM ores to generate in modded dimensions").getBooleanValue(true);
//		enableMines = config.get(CATEGORY_GENERAL, "1.05_enableLandmineSpawn", true, "Allows landmines to generate").getBooleanValue(true);
//		enableRad = config.get(CATEGORY_GENERAL, "1.06_enableRadHotspotSpawn", true, "Allows radiation hotspots to generate").getBooleanValue(true);
//		enableNITAN = config.get(CATEGORY_GENERAL, "1.07_enableNITANChestSpawn", true, "Allows chests to spawn at specific coordinates full of powders").getBooleanValue(true);
//		enableBomberShortMode = config.get(CATEGORY_GENERAL, "1.14_enableBomberShortMode", false, "Has bomber planes spawn in closer to the target for use with smaller render distances").getBooleanValue(false);
//		enableVaults = config.get(CATEGORY_GENERAL, "1.15_enableVaultSpawn", true, "Allows locked safes to spawn").getBooleanValue(true);
//		enableCataclysm = config.get(CATEGORY_GENERAL, "1.17_enableCataclysm", false, "Causes satellites to fall whenever a mob dies").getBooleanValue(false);
//		enableExtendedLogging = config.get(CATEGORY_GENERAL, "1.18_enableExtendedLogging", false, "Logs uses of the detonator, nuclear explosions, missile launches, grenades, etc.").getBooleanValue(false);
//		enableHardcoreTaint = config.get(CATEGORY_GENERAL, "1.19_enableHardcoreTaint", false, "Allows tainted mobs to spread taint").getBooleanValue(false);
//		enableGuns = config.get(CATEGORY_GENERAL, "1.20_enableGuns", true, "Prevents new system guns to be fired").getBooleanValue(true);
//		enableVirus = config.get(CATEGORY_GENERAL, "1.21_enableVirus", false, "Allows virus blocks to spread").getBooleanValue(false);
//		enableCrosshairs = config.get(CATEGORY_GENERAL, "1.22_enableCrosshairs", true, "Shows custom crosshairs when an NTM gun is being held").getBooleanValue(true);
//		enableReflectorCompat = config.get(CATEGORY_GENERAL, "1.24_enableReflectorCompat", false, "Enable old reflector oredict name (\"plateDenseLead\") instead of new \"plateTungCar\"").getBooleanValue(false);
//		enableRenderDistCheck = config.get(CATEGORY_GENERAL, "1.25_enableRenderDistCheck", true, "Check invalid render distances (over 16, without OptiFine) and fix it").getBooleanValue(true);
//		enableReEval = config.get(CATEGORY_GENERAL, "1.27_enableReEval", true, "Allows re-evaluating power networks on link remove instead of destroying and recreating").getBooleanValue(true);
//		enableSilentCompStackErrors = config.get(CATEGORY_GENERAL, "1.28_enableSilentCompStackErrors", false, "Enabling this will disable log spam created by unregistered items in ComparableStack instances.").getBooleanValue(false);
//		hintPos = CommonConfig.createConfigInt(config, CATEGORY_GENERAL, "1.29_hudOverlayPosition", "0: Top left\n1: Top right\n2: Center right\n3: Center Left", 0);
//		enableSkyboxes = config.get(CATEGORY_GENERAL, "1.31_enableSkyboxes", true, "If enabled, will try to use NTM's custom skyboxes.").getBooleanValue(true);
//		enableImpactWorldProvider = config.get(CATEGORY_GENERAL, "1.32_enableImpactWorldProvider", true, "If enabled, registers custom world provider which modifies lighting and sky colors for post impact effects.").getBooleanValue(true);
//		enableStatReRegistering = config.get(CATEGORY_GENERAL, "1.33_enableStatReRegistering", true, "If enabled, will re-register item crafting/breaking/usage stats in order to fix a forge bug where modded items just won't show up.").getBooleanValue(true);
//		enableKeybindOverlap = config.get(CATEGORY_GENERAL, "1.34_enableKeybindOverlap", true, "If enabled, will handle keybinds that would otherwise be ignored due to overlapping.").getBooleanValue(true);
//		enableFluidContainerCompat = config.get(CATEGORY_GENERAL, "1.35_enableFluidContainerCompat", true, "If enabled, fluid containers will be oredicted and interchangable in recipes with other mods' containers, as well as TrainCraft's diesel being considered a valid diesel canister.").getBooleanValue(true);
//		enableMOTD = config.get(CATEGORY_GENERAL, "1.36_enableMOTD", true, "If enabled, shows the 'Loaded mod!' chat message as well as update notifications when joining a world").getBooleanValue(true);
//		enableGuideBook = config.get(CATEGORY_GENERAL, "1.37_enableGuideBook", true, "If enabled, gives players the guide book when joining the world for the first time").getBooleanValue(true);
//		enableSteamParticles = config.get(CATEGORY_GENERAL, "1.38_enableSteamParticles", true, "If disabled, auxiliary cooling towers and large cooling towers will not emit steam particles when in use.").getBooleanValue(true);
//		enableSoundExtension = config.get(CATEGORY_GENERAL, "1.39_enableSoundExtension", true, "If enabled, will change the limit for how many sounds can play at once.").getBooleanValue(true);
//		enableMekanismChanges = config.get(CATEGORY_GENERAL, "1.40_enableMekanismChanges", true, "If enabled, will change some of Mekanism's recipes.").getBooleanValue(true);
//		normalSoundChannels = CommonConfig.createConfigInt(config, CATEGORY_GENERAL, "1.41_normalSoundChannels",
//				"The amount of channels to create while 1.39_enableSoundExtension is enabled.\n" +
//				"Note that a value below 28 or above 200 can cause buggy sounds and issues with other mods running out of sound memory.", 100);
//
//		enableExpensiveMode = config.get(CATEGORY_GENERAL, "1.99_enableExpensiveMode", false, "It does what the name implies.").getBooleanValue(false);
//
//		final String CATEGORY_528 = CommonConfig.CATEGORY_528;
//
//		config.addCustomCategoryComment(CATEGORY_528, "CAUTION\n"
//				+ "528 Mode: Please proceed with caution!\n"
//				+ "528-Modus: Lassen Sie Vorsicht walten!\n"
//				+ "способ-528: действовать с осторожностью!");
//
//		enable528 = CommonConfig.createConfigBool(config, CATEGORY_528, "enable528Mode", "The central toggle for 528 mode.", false);
//		enable528ReasimBoilers = CommonConfig.createConfigBool(config, CATEGORY_528, "X528_forceReasimBoilers", "Keeps the RBMK dial for ReaSim boilers on, preventing use of non-ReaSim boiler columns and forcing the use of steam in-/outlets", true);
//		enable528ColtanDeposit = CommonConfig.createConfigBool(config, CATEGORY_528, "X528_enableColtanDepsoit", "Enables the coltan deposit. A large amount of coltan will spawn around a single random location in the world.", true);
//		enable528ColtanSpawn = CommonConfig.createConfigBool(config, CATEGORY_528, "X528_enableColtanSpawning", "Enables coltan ore as a random spawn in the world. Unlike the deposit option, coltan will not just spawn in one central location.", false);
//		enable528BedrockDeposit = CommonConfig.createConfigBool(config, CATEGORY_528, "X528_enableBedrockDepsoit", "Enables bedrock coltan ores in the coltan deposit. These ores can be drilled to extract infinite coltan, albeit slowly.", true);
//		enable528BedrockSpawn = CommonConfig.createConfigBool(config, CATEGORY_528, "X528_enableBedrockSpawning", "Enables the bedrock coltan ores as a rare spawn. These will be rarely found anywhere in the world.", false);
//		enable528BosniaSimulator = CommonConfig.createConfigBool(config, CATEGORY_528, "X528_enableBosniaSimulator", "Enables anti tank mines spawning all over the world.", true);
//		enable528BedrockReplacement = CommonConfig.createConfigBool(config, CATEGORY_528, "X528_enable528BedrockReplacement", "Replaces certain bedrock ores with ones that require additional processing.", true);
//		enable528NetherBurn = CommonConfig.createConfigBool(config, CATEGORY_528, "X528_enable528NetherBurn", "Whether players burn in the nether", true);
//		coltanRate = CommonConfig.createConfigInt(config, CATEGORY_528, "X528_oreColtanFrequency", "Determines how many coltan ore veins are to be expected in a chunk. These values do not affect the frequency in deposits, and only apply if random coltan spanwing is enabled.", 2);
//		bedrockRate = CommonConfig.createConfigInt(config, CATEGORY_528, "X528_bedrockColtanFrequency", "Determines how often (1 in X) bedrock coltan ores spawn. Applies for both the bedrock ores in the coltan deposit (if applicable) and the random bedrock ores (if applicable)", 50);
//
//
//		final String CATEGORY_LBSM = CommonConfig.CATEGORY_LBSM;
//
//		config.addCustomCategoryComment(CATEGORY_LBSM,
//				"Will most likely break standard progression!\n"
//				+ "However, the game gets generally easier and more enjoyable for casual players.\n"
//				+ "Progression-braking recipes are usually not too severe, so the mode is generally server-friendly!");
//
//		enableLBSM = CommonConfig.createConfigBool(config, CATEGORY_LBSM, "enableLessBullshitMode", "The central toggle for LBS mode. Forced OFF when 528 is enabled!", false);
//		enableLBSMFullSchrab = CommonConfig.createConfigBool(config, CATEGORY_LBSM, "LBSM_fullSchrab", "When enabled, this will replace schraranium with full schrabidium ingots in the transmutator's output", true);
//		enableLBSMShorterDecay = CommonConfig.createConfigBool(config, CATEGORY_LBSM, "LBSM_shortDecay", "When enabled, this will highly accelerate the speed at which nuclear waste disposal drums decay their contents. 60x faster than 528 mode and 5-12x faster than on normal mode.", true);
//		enableLBSMSimpleArmorRecipes = CommonConfig.createConfigBool(config, CATEGORY_LBSM, "LBSM_recipeSimpleArmor", "When enabled, simplifies the recipe for armor sets like starmetal or schrabidium.", true);
//		enableLBSMSimpleToolRecipes = CommonConfig.createConfigBool(config, CATEGORY_LBSM, "LBSM_recipeSimpleTool", "When enabled, simplifies the recipe for tool sets like starmetal or scrhabidium", true);
//		enableLBSMSimpleAlloy = CommonConfig.createConfigBool(config, CATEGORY_LBSM, "LBSM_recipeSimpleAlloy", "When enabled, adds some blast furnace recipes to make certain things cheaper", true);
//		enableLBSMSimpleChemsitry = CommonConfig.createConfigBool(config, CATEGORY_LBSM, "LBSM_recipeSimpleChemistry", "When enabled, simplifies some chemical plant recipes", true);
//		enableLBSMSimpleCentrifuge = CommonConfig.createConfigBool(config, CATEGORY_LBSM, "LBSM_recipeSimpleCentrifuge", "When enabled, enhances centrifuge outputs to make rare materials more common", true);
//		enableLBSMUnlockAnvil = CommonConfig.createConfigBool(config, CATEGORY_LBSM, "LBSM_recipeUnlockAnvil", "When enabled, all anvil recipes are available at tier 1", true);
//		enableLBSMSimpleCrafting = CommonConfig.createConfigBool(config, CATEGORY_LBSM, "LBSM_recipeSimpleCrafting", "When enabled, some uncraftable or more expansive items get simple crafting recipes. Scorched uranium also becomes washable", true);
//		enableLBSMSimpleMedicineRecipes = CommonConfig.createConfigBool(config, CATEGORY_LBSM, "LBSM_recipeSimpleMedicine", "When enabled, makes some medicine recipes (like ones that require bismuth) much more affordable", true);
//		enableLBSMSafeCrates = CommonConfig.createConfigBool(config, CATEGORY_LBSM, "LBSM_safeCrates", "When enabled, prevents crates from becoming radioactive", true);
//		enableLBSMSafeMEDrives = CommonConfig.createConfigBool(config, CATEGORY_LBSM, "LBSM_safeMEDrives", "When enabled, prevents ME Drives and Portable Cells from becoming radioactive", true);
//		enableLBSMIGen = CommonConfig.createConfigBool(config, CATEGORY_LBSM, "LBSM_iGen", "When enabled, restores the industrial generator to pre-nerf power", true);
//		schrabRate = CommonConfig.createConfigInt(config, CATEGORY_LBSM, "LBSM_schrabOreRate", "Changes the amount of uranium ore needed on average to create one schrabidium ore using nukes. Standard mode value is 100", 20);
//
//		if(enable528) enableLBSM ;
//	}
}
