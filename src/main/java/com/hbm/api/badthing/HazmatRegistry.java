package com.hbm.api.badthing;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.HBM;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * 可以防辐射物品的注册类
 * */
public class HazmatRegistry {
	public static void initDefault() {
		//assuming coefficient of 10
		//real coefficient turned out to be 5
		//oops
		
		double helmet = 0.2D;
		double chest = 0.4D;
		double legs = 0.3D;
		double boots = 0.1D;

		double iron = 0.0225D; // 5%
		double gold = 0.0225D; // 5%
		double steel = 0.045D; // 10%
		double titanium = 0.045D; // 10%
		double alloy = 0.07D; // 15%
		double cobalt = 0.125D; // 25%
		
		double hazYellow = 0.6D; // 50%
		double hazRed = 1.0D; // 90%
		double hazGray = 2D; // 99%
		double paa = 1.7D; // 97%
		double liquidator = 2.4D; // 99.6%

		double t45 = 1D; // 90%
		double ajr = 1.3D; // 95%
		double bj = 1D; // 90%
		double env = 1.0D; // 99%
		double hev = 2.3D; // 99.5%
		double rpa = 2D; // 99%
		double trench = 1D; // 90%
		double fau = 4D; // 99.99%
		double dns = 5D; // 99.999%
		double security = 0.825D; // 85%
		double star = 1D; // 90%
		double cmb = 1.3D; // 95%
		double schrab = 3D; // 99.9%
		double euph = 10D; // <100%
		
//		HazmatRegistry.registerHazmat(ModItems.hazmat_helmet, hazYellow * helmet);
//		HazmatRegistry.registerHazmat(ModItems.hazmat_plate, hazYellow * chest);
//		HazmatRegistry.registerHazmat(ModItems.hazmat_legs, hazYellow * legs);
//		HazmatRegistry.registerHazmat(ModItems.hazmat_boots, hazYellow * boots);
//
//		HazmatRegistry.registerHazmat(ModItems.hazmat_helmet_red, hazRed * helmet);
//		HazmatRegistry.registerHazmat(ModItems.hazmat_plate_red, hazRed * chest);
//		HazmatRegistry.registerHazmat(ModItems.hazmat_legs_red, hazRed * legs);
//		HazmatRegistry.registerHazmat(ModItems.hazmat_boots_red, hazRed * boots);
//
//		HazmatRegistry.registerHazmat(ModItems.hazmat_helmet_grey, hazGray * helmet);
//		HazmatRegistry.registerHazmat(ModItems.hazmat_plate_grey, hazGray * chest);
//		HazmatRegistry.registerHazmat(ModItems.hazmat_legs_grey, hazGray * legs);
//		HazmatRegistry.registerHazmat(ModItems.hazmat_boots_grey, hazGray * boots);
//
//		HazmatRegistry.registerHazmat(ModItems.liquidator_helmet, liquidator * helmet);
//		HazmatRegistry.registerHazmat(ModItems.liquidator_plate, liquidator * chest);
//		HazmatRegistry.registerHazmat(ModItems.liquidator_legs, liquidator * legs);
//		HazmatRegistry.registerHazmat(ModItems.liquidator_boots, liquidator * boots);
//
//		HazmatRegistry.registerHazmat(ModItems.t45_helmet, t45 * helmet);
//		HazmatRegistry.registerHazmat(ModItems.t45_plate, t45 * chest);
//		HazmatRegistry.registerHazmat(ModItems.t45_legs, t45 * legs);
//		HazmatRegistry.registerHazmat(ModItems.t45_boots, t45 * boots);
//
//		HazmatRegistry.registerHazmat(ModItems.ajr_helmet, ajr * helmet);
//		HazmatRegistry.registerHazmat(ModItems.ajr_plate, ajr * chest);
//		HazmatRegistry.registerHazmat(ModItems.ajr_legs, ajr * legs);
//		HazmatRegistry.registerHazmat(ModItems.ajr_boots, ajr * boots);
//		HazmatRegistry.registerHazmat(ModItems.ajro_helmet, ajr * helmet);
//		HazmatRegistry.registerHazmat(ModItems.ajro_plate, ajr * chest);
//		HazmatRegistry.registerHazmat(ModItems.ajro_legs, ajr * legs);
//		HazmatRegistry.registerHazmat(ModItems.ajro_boots, ajr * boots);
//
//		HazmatRegistry.registerHazmat(ModItems.bj_helmet, bj * helmet);
//		HazmatRegistry.registerHazmat(ModItems.bj_plate, bj * chest);
//		HazmatRegistry.registerHazmat(ModItems.bj_plate_jetpack, bj * chest);
//		HazmatRegistry.registerHazmat(ModItems.bj_legs, bj * legs);
//		HazmatRegistry.registerHazmat(ModItems.bj_boots, bj * boots);
//
//		HazmatRegistry.registerHazmat(ModItems.steamsuit_helmet, 1.3 * helmet);
//		HazmatRegistry.registerHazmat(ModItems.steamsuit_plate, 1.3 * chest);
//		HazmatRegistry.registerHazmat(ModItems.steamsuit_legs, 1.3 * legs);
//		HazmatRegistry.registerHazmat(ModItems.steamsuit_boots, 1.3 * boots);
//
//		HazmatRegistry.registerHazmat(ModItems.envsuit_helmet, env * helmet);
//		HazmatRegistry.registerHazmat(ModItems.envsuit_plate, env * chest);
//		HazmatRegistry.registerHazmat(ModItems.envsuit_legs, env * legs);
//		HazmatRegistry.registerHazmat(ModItems.envsuit_boots, env * boots);
//
//		HazmatRegistry.registerHazmat(ModItems.hev_helmet, hev * helmet);
//		HazmatRegistry.registerHazmat(ModItems.hev_plate, hev * chest);
//		HazmatRegistry.registerHazmat(ModItems.hev_legs, hev * legs);
//		HazmatRegistry.registerHazmat(ModItems.hev_boots, hev * boots);
//
//		HazmatRegistry.registerHazmat(ModItems.rpa_helmet, rpa * helmet);
//		HazmatRegistry.registerHazmat(ModItems.rpa_plate, rpa * chest);
//		HazmatRegistry.registerHazmat(ModItems.rpa_legs, rpa * legs);
//		HazmatRegistry.registerHazmat(ModItems.rpa_boots, rpa * boots);
//
//		HazmatRegistry.registerHazmat(ModItems.trenchmaster_helmet, trench * helmet);
//		HazmatRegistry.registerHazmat(ModItems.trenchmaster_plate, trench * chest);
//		HazmatRegistry.registerHazmat(ModItems.trenchmaster_legs, trench * legs);
//		HazmatRegistry.registerHazmat(ModItems.trenchmaster_boots, trench * boots);
//
//		HazmatRegistry.registerHazmat(ModItems.fau_helmet, fau * helmet);
//		HazmatRegistry.registerHazmat(ModItems.fau_plate, fau * chest);
//		HazmatRegistry.registerHazmat(ModItems.fau_legs, fau * legs);
//		HazmatRegistry.registerHazmat(ModItems.fau_boots, fau * boots);
//
//		HazmatRegistry.registerHazmat(ModItems.dns_helmet, dns * helmet);
//		HazmatRegistry.registerHazmat(ModItems.dns_plate, dns * chest);
//		HazmatRegistry.registerHazmat(ModItems.dns_legs, dns * legs);
//		HazmatRegistry.registerHazmat(ModItems.dns_boots, dns * boots);
//
//		HazmatRegistry.registerHazmat(ModItems.paa_plate, paa * chest);
//		HazmatRegistry.registerHazmat(ModItems.paa_legs, paa * legs);
//		HazmatRegistry.registerHazmat(ModItems.paa_boots, paa * boots);
//
//		HazmatRegistry.registerHazmat(ModItems.hazmat_paa_helmet, paa * helmet);
//		HazmatRegistry.registerHazmat(ModItems.hazmat_paa_plate, paa * chest);
//		HazmatRegistry.registerHazmat(ModItems.hazmat_paa_legs, paa * legs);
//		HazmatRegistry.registerHazmat(ModItems.hazmat_paa_boots, paa * boots);
//
//		HazmatRegistry.registerHazmat(ModItems.security_helmet, security * helmet);
//		HazmatRegistry.registerHazmat(ModItems.security_plate, security * chest);
//		HazmatRegistry.registerHazmat(ModItems.security_legs, security * legs);
//		HazmatRegistry.registerHazmat(ModItems.security_boots, security * boots);
//
//		HazmatRegistry.registerHazmat(ModItems.starmetal_helmet, star * helmet);
//		HazmatRegistry.registerHazmat(ModItems.starmetal_plate, star * chest);
//		HazmatRegistry.registerHazmat(ModItems.starmetal_legs, star * legs);
//		HazmatRegistry.registerHazmat(ModItems.starmetal_boots, star * boots);
//
//		HazmatRegistry.registerHazmat(ModItems.jackt, 0.1);
//		HazmatRegistry.registerHazmat(ModItems.jackt2, 0.1);
//
//		HazmatRegistry.registerHazmat(ModItems.gas_mask, 0.07);
//		HazmatRegistry.registerHazmat(ModItems.gas_mask_m65, 0.095);
//
//		HazmatRegistry.registerHazmat(ModItems.steel_helmet, steel * helmet);
//		HazmatRegistry.registerHazmat(ModItems.steel_plate, steel * chest);
//		HazmatRegistry.registerHazmat(ModItems.steel_legs, steel * legs);
//		HazmatRegistry.registerHazmat(ModItems.steel_boots, steel * boots);
//
//		HazmatRegistry.registerHazmat(ModItems.titanium_helmet, titanium * helmet);
//		HazmatRegistry.registerHazmat(ModItems.titanium_plate, titanium * chest);
//		HazmatRegistry.registerHazmat(ModItems.titanium_legs, titanium * legs);
//		HazmatRegistry.registerHazmat(ModItems.titanium_boots, titanium * boots);
//
//		HazmatRegistry.registerHazmat(ModItems.cobalt_helmet, cobalt * helmet);
//		HazmatRegistry.registerHazmat(ModItems.cobalt_plate, cobalt * chest);
//		HazmatRegistry.registerHazmat(ModItems.cobalt_legs, cobalt * legs);
//		HazmatRegistry.registerHazmat(ModItems.cobalt_boots, cobalt * boots);
//
//		HazmatRegistry.registerHazmat(ModItems.alloy_helmet, alloy * helmet);
//		HazmatRegistry.registerHazmat(ModItems.alloy_plate, alloy * chest);
//		HazmatRegistry.registerHazmat(ModItems.alloy_legs, alloy * legs);
//		HazmatRegistry.registerHazmat(ModItems.alloy_boots, alloy * boots);
//
//		HazmatRegistry.registerHazmat(ModItems.cmb_helmet, cmb * helmet);
//		HazmatRegistry.registerHazmat(ModItems.cmb_plate, cmb * chest);
//		HazmatRegistry.registerHazmat(ModItems.cmb_legs, cmb * legs);
//		HazmatRegistry.registerHazmat(ModItems.cmb_boots, cmb * boots);
//
//		HazmatRegistry.registerHazmat(ModItems.schrabidium_helmet, schrab * helmet);
//		HazmatRegistry.registerHazmat(ModItems.schrabidium_plate, schrab * chest);
//		HazmatRegistry.registerHazmat(ModItems.schrabidium_legs, schrab * legs);
//		HazmatRegistry.registerHazmat(ModItems.schrabidium_boots, schrab * boots);
//
//		HazmatRegistry.registerHazmat(ModItems.euphemium_helmet, euph * helmet);
//		HazmatRegistry.registerHazmat(ModItems.euphemium_plate, euph * chest);
//		HazmatRegistry.registerHazmat(ModItems.euphemium_legs, euph * legs);
//		HazmatRegistry.registerHazmat(ModItems.euphemium_boots, euph * boots);

		HazmatRegistry.registerHazmat(Items.IRON_HELMET, iron * helmet);
		HazmatRegistry.registerHazmat(Items.IRON_CHESTPLATE, iron * chest);
		HazmatRegistry.registerHazmat(Items.IRON_LEGGINGS, iron * legs);
		HazmatRegistry.registerHazmat(Items.IRON_BOOTS, iron * boots);

		HazmatRegistry.registerHazmat(Items.GOLDEN_HELMET, gold * helmet);
		HazmatRegistry.registerHazmat(Items.GOLDEN_CHESTPLATE, gold * chest);
		HazmatRegistry.registerHazmat(Items.GOLDEN_LEGGINGS, gold * legs);
		HazmatRegistry.registerHazmat(Items.GOLDEN_BOOTS, gold * boots);

		/** 主要是联动模组的一些装备 */
//		Compat.registerCompatHazmat();
	}
	
	private static HashMap<Item, Double> entries = new HashMap();
	
	public static void registerHazmat(Item item, double resistance) {
		entries.put(item, resistance);
	}
	/** 获取物品的辐射抗性 */
	public static double getResistance(ItemStack stack) {
		if(stack == null)
			return 0;
		
		double cladding = getCladding(stack);
		
		Double f = entries.get(stack.getItem());
		
		if(f != null)
			return f + cladding;
		
		return cladding;
	}
	
	public static double getCladding(ItemStack stack) {

		if(stack.hasTag() && stack.getTag().getFloat("hfr_cladding") > 0)
			return stack.getTag().getFloat("hfr_cladding");
		
//		if(ArmorModHandler.hasMods(stack)) {
//
//			ItemStack[] mods = ArmorModHandler.pryMods(stack);
//			ItemStack cladding = mods[ArmorModHandler.cladding];
//
//			if(cladding != null && cladding.getItem() instanceof ItemModCladding) {
//				return ((ItemModCladding)cladding.getItem()).rad;
//			}
//		}
		
		return 0;
	}
	/** 获取玩家的辐射抗性 */
	public static float getResistance(Player player) {
		
		float res = 0.0F;
		
//		if(player.getEncodeId().equals(ShadyUtil.Pu_238)) {
//			res += 0.4F;
//		}
		
		for(int i = 0; i < 4; i++) {
			res += (float) getResistance(player.getInventory().getArmor(i));
		}
		
//		if(player.isPotionActive(HbmPotion.radx))
//			res += 0.2F;
		
		return res;
		
	}

	public static final Gson gson = new Gson();
	/** 注册所有内置的防辐射功能的物品，整个类的初始化类，需要在模组加载时调用。 */
	public static void registerHazmats() {
//		File folder = MainRegistry.configHbmDir;
//
//		//玩家可自行配置的配置文件
//		File config = new File(folder.getAbsolutePath() + File.separatorChar + "hbmRadResist.json");
//		//默认配置文件，用于存放系统内定的配置
//		File template = new File(folder.getAbsolutePath() + File.separatorChar + "_hbmRadResist.json");
//
//		initDefault();
//
//		if(!config.exists()) {
//			writeDefault(template);
//		} else {
//			HashMap<Item, Double> conf = readConfig(config);
//
//			if(conf != null) {
//				entries.clear();
//				entries.putAll(conf);
//			}
//		}
	}
	/** 将默认配置写入配置文件中 */
	private static void writeDefault(File file) {
		try {
			JsonWriter writer = new JsonWriter(new FileWriter(file));
			writer.setIndent("  ");					//pretty formatting
			writer.beginObject();					//initial '{'
			writer.name("comment").value("Template file, remove the underscore ('_') from the name to enable the config.");
			writer.name("entries").beginArray();	//all recipes are stored in an array called "entries"
			
			for(Entry<Item, Double> entry : entries.entrySet()) {
				writer.beginObject();				//begin object for a single recipe
//				writer.name("item").value(Item.itemRegistry.getNameForObject(entry.getKey()));
				writer.name("resistance").value(entry.getValue());
				writer.endObject();					//end recipe object
			}
			
			writer.endArray();						//end recipe array
			writer.endObject();						//final '}'
			writer.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	/** 从配置文件读取配置 */
	private static HashMap<Item, Double> readConfig(File config) {
		try {
			JsonObject json = gson.fromJson(new FileReader(config), JsonObject.class);
			JsonArray array = json.get("entries").getAsJsonArray();
			HashMap<Item, Double> conf = new HashMap();
			
			for(JsonElement element : array) {
				JsonObject object = (JsonObject) element;
				
				try {
					String name = object.get("item").getAsString();
//					Item item = (Item) Item.itemRegistry.getObject(name);
//					double resistance = object.get("resistance").getAsDouble();
//					if(item != null) {
//						conf.put(item, resistance);
//					} else {
//						HBMxx.LOGGER.error("Tried loading unknown item " + name + " for hazmat entry.");
//					}
				} catch(Exception ex) {
					HBM.LOGGER.error("Encountered " + ex + " trying to read hazmat entry " + element.toString());
				}
			}
			return conf;
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
