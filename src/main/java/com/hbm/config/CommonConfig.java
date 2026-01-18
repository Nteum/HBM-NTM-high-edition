package com.hbm.config;

import com.hbm.HBM;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.List;
import java.util.Locale;

@Mod.EventBusSubscriber(modid = HBM.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonConfig {
	
	public static final String CATEGORY_GENERAL = "01_general";
	public static final String CATEGORY_ORES = "02_ores";
	public static final String CATEGORY_NUKES = "03_nukes";
	public static final String CATEGORY_DUNGEONS = "04_dungeons";
	public static final String CATEGORY_METEORS = "05_meteors";
	public static final String CATEGORY_EXPLOSIONS = "06_explosions";
	public static final String CATEGORY_MISSILE = "07_missile_machines";
	public static final String CATEGORY_POTION = "08_potion_effects";
	public static final String CATEGORY_MACHINES = "09_machines";
	public static final String CATEGORY_DROPS = "10_dangerous_drops";
	public static final String CATEGORY_TOOLS = "11_tools";
	public static final String CATEGORY_MOBS = "12_mobs";
	public static final String CATEGORY_RADIATION = "13_radiation";
	public static final String CATEGORY_HAZARD = "14_hazard";
	public static final String CATEGORY_STRUCTURES = "15_structures";
	public static final String CATEGORY_POLLUTION = "16_pollution";
	public static final String CATEGORY_BIOMES = "17_biomes";
	public static final String CATEGORY_WEAPONS = "18_weapons";
	public static final String CATEGORY_RBMK = "19_rbmk";

	public static final String CATEGORY_528 = "528";
	public static final String CATEGORY_LBSM = "LESS BULLSHIT MODE";

	public static final ForgeConfigSpec CONFIG_SPEC;

	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		ConfigGeneral.addConfig(builder);
		ConfigRBMK.addConfig(builder);

		Config528.addConfig(builder);
		ConfigLBSM.addConfig(builder);

		ConfigWorld.addConfig(builder);

		CONFIG_SPEC = builder.build();
	}

	@SubscribeEvent
	public static void onLoad(final ModConfigEvent event){
		ConfigGeneral.loadConfig(event);
		ConfigRBMK.loadConfig(event);

		Config528.loadConfig(event);
		ConfigLBSM.loadConfig(CATEGORY_LBSM, event);
	}

	public static void addBoolean(ForgeConfigSpec.Builder builder, String name, boolean defaultValue, String comment){
		builder.comment(comment).define(name, defaultValue);
	}
	public static void addInt(ForgeConfigSpec.Builder builder, String name, int defaultValue, String comment){
		addInt(builder, name, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE, comment);
	}
	public static void addInt(ForgeConfigSpec.Builder builder, String name, int defaultValue, int min, int max, String comment){
		builder.comment(comment).defineInRange(name, defaultValue, min, max);
	}
	public static void addStringList(ForgeConfigSpec.Builder builder, String name, List<String> defaultValue, String comment){
		builder.comment(comment).defineList(name, defaultValue, s -> s instanceof String);
	}

	public static int parseStructureFlag(String flag) {
		if(flag == null) flag = "";
		
		switch(flag.toLowerCase(Locale.US)) {
		case "true":
		case "on":
		case "yes":
			return 1;
		case "false":
		case "off":
		case "no":
			return 0;
		default:
			return 2;
		}
	}

}
