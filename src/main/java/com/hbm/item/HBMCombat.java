package com.hbm.item;

import com.hbm.datagen.LanguageProvider;
import com.hbm.item.armor.HBMArmorMats;
import com.hbm.item.armor.ItemArmorFSB;
import com.hbm.item.armor.ItemArmorFSBPowered;
import net.minecraft.client.model.ArmorStandModel;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.world.item.*;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 战斗物品，注册在原版创造物品栏里战斗物品那块
 * */
public class HBMCombat extends HBMComponent{
    private static final List<RegistryObject<Item>> itemList = new ArrayList<>();
    public static Suit STEEL;
    public static Suit TITANIUM;
    public static Suit ALLOY;
    public static Suit CMB;
    public static Suit PAA;
    public static Suit ASBESTOS;
    public static Suit SECURITY;
    public static Suit COBALT;
    public static Suit STARMETAL;
    public static Suit ROBES;
    public static Suit DNT;
    public static Suit ZIRCONIUM;

    public static Suit T45;
    
    public static Suit BISMUTH;

    public static void register(DeferredRegister<Item> ITEMS){
        STEEL = new Suit(register(itemList, "steel_helmet", () -> new ItemArmorFSB(HBMArmorMats.STEEL, ArmorItem.Type.HELMET, new Item.Properties())),
                register(itemList, "steel_plate", () -> new ItemArmorFSB(HBMArmorMats.STEEL, ArmorItem.Type.CHESTPLATE, new Item.Properties(), ()->STEEL)),
                register(itemList, "steel_legs", () -> new ItemArmorFSB(HBMArmorMats.STEEL, ArmorItem.Type.LEGGINGS, new Item.Properties())),
                register(itemList, "steel_boots", () -> new ItemArmorFSB(HBMArmorMats.STEEL, ArmorItem.Type.BOOTS, new Item.Properties())));
        TITANIUM = new Suit(register(itemList, "titanium_helmet", () -> new ItemArmorFSB(HBMArmorMats.TITANIUM, ArmorItem.Type.HELMET, new Item.Properties())),
                register(itemList, "titanium_plate", () -> new ItemArmorFSB(HBMArmorMats.TITANIUM, ArmorItem.Type.CHESTPLATE, new Item.Properties(), ()->TITANIUM)),
                register(itemList, "titanium_legs", () -> new ItemArmorFSB(HBMArmorMats.TITANIUM, ArmorItem.Type.LEGGINGS, new Item.Properties())),
                register(itemList, "titanium_boots", () -> new ItemArmorFSB(HBMArmorMats.TITANIUM, ArmorItem.Type.BOOTS, new Item.Properties())));
        ALLOY = new Suit(register(itemList, "alloy_helmet", () -> new ItemArmorFSB(HBMArmorMats.ALLOY, ArmorItem.Type.HELMET, new Item.Properties())),
                register(itemList, "alloy_plate", () -> new ItemArmorFSB(HBMArmorMats.ALLOY, ArmorItem.Type.CHESTPLATE, new Item.Properties(), ()->ALLOY)),
                register(itemList, "alloy_legs", () -> new ItemArmorFSB(HBMArmorMats.ALLOY, ArmorItem.Type.LEGGINGS, new Item.Properties())),
                register(itemList, "alloy_boots", () -> new ItemArmorFSB(HBMArmorMats.ALLOY, ArmorItem.Type.BOOTS, new Item.Properties())));
        CMB = new Suit(register(itemList, "cmb_helmet", () -> new ItemArmorFSB(HBMArmorMats.CMB, ArmorItem.Type.HELMET, new Item.Properties())),
                register(itemList, "cmb_plate", () -> new ItemArmorFSB(HBMArmorMats.CMB, ArmorItem.Type.CHESTPLATE, new Item.Properties(), ()->CMB)),
                register(itemList, "cmb_legs", () -> new ItemArmorFSB(HBMArmorMats.CMB, ArmorItem.Type.LEGGINGS, new Item.Properties())),
                register(itemList, "cmb_boots", () -> new ItemArmorFSB(HBMArmorMats.CMB, ArmorItem.Type.BOOTS, new Item.Properties())));
        PAA = new Suit(null,
                register(itemList, "paa_plate", () -> new ItemArmorFSB(HBMArmorMats.PAA, ArmorItem.Type.CHESTPLATE, new Item.Properties(), ()->PAA)),
                register(itemList, "paa_legs", () -> new ItemArmorFSB(HBMArmorMats.PAA, ArmorItem.Type.LEGGINGS, new Item.Properties())),
                register(itemList, "paa_boots", () -> new ItemArmorFSB(HBMArmorMats.PAA, ArmorItem.Type.BOOTS, new Item.Properties())));
        ASBESTOS = new Suit(register(itemList, "asbestos_helmet", () -> new ItemArmorFSB(HBMArmorMats.ASBESTOS, ArmorItem.Type.HELMET, new Item.Properties())),
                register(itemList, "asbestos_plate", () -> new ItemArmorFSB(HBMArmorMats.ASBESTOS, ArmorItem.Type.CHESTPLATE, new Item.Properties(), ()->ASBESTOS)),
                register(itemList, "asbestos_legs", () -> new ItemArmorFSB(HBMArmorMats.ASBESTOS, ArmorItem.Type.LEGGINGS, new Item.Properties())),
                register(itemList, "asbestos_boots", () -> new ItemArmorFSB(HBMArmorMats.ASBESTOS, ArmorItem.Type.BOOTS, new Item.Properties())));
        SECURITY = new Suit(register(itemList, "security_helmet", () -> new ItemArmorFSB(HBMArmorMats.SECURITY, ArmorItem.Type.HELMET, new Item.Properties())),
                register(itemList, "security_plate", () -> new ItemArmorFSB(HBMArmorMats.SECURITY, ArmorItem.Type.CHESTPLATE, new Item.Properties())),
                register(itemList, "security_legs", () -> new ItemArmorFSB(HBMArmorMats.SECURITY, ArmorItem.Type.LEGGINGS, new Item.Properties())),
                register(itemList, "security_boots", () -> new ItemArmorFSB(HBMArmorMats.SECURITY, ArmorItem.Type.BOOTS, new Item.Properties())));
        COBALT = new Suit(register(itemList, "cobalt_helmet", () -> new ItemArmorFSB(HBMArmorMats.COBALT, ArmorItem.Type.HELMET, new Item.Properties())),
                register(itemList, "cobalt_plate", () -> new ItemArmorFSB(HBMArmorMats.COBALT, ArmorItem.Type.CHESTPLATE, new Item.Properties())),
                register(itemList, "cobalt_legs", () -> new ItemArmorFSB(HBMArmorMats.COBALT, ArmorItem.Type.LEGGINGS, new Item.Properties())),
                register(itemList, "cobalt_boots", () -> new ItemArmorFSB(HBMArmorMats.COBALT, ArmorItem.Type.BOOTS, new Item.Properties())));
        STARMETAL = new Suit(register(itemList, "starmetal_helmet", () -> new ItemArmorFSB(HBMArmorMats.STARMETAL, ArmorItem.Type.HELMET, new Item.Properties())),
                register(itemList, "starmetal_plate", () -> new ItemArmorFSB(HBMArmorMats.STARMETAL, ArmorItem.Type.CHESTPLATE, new Item.Properties())),
                register(itemList, "starmetal_legs", () -> new ItemArmorFSB(HBMArmorMats.STARMETAL, ArmorItem.Type.LEGGINGS, new Item.Properties())),
                register(itemList, "starmetal_boots", () -> new ItemArmorFSB(HBMArmorMats.STARMETAL, ArmorItem.Type.BOOTS, new Item.Properties())));
        ROBES = new Suit(register(itemList, "robes_helmet", () -> new ItemArmorFSB(ArmorMaterials.CHAIN, ArmorItem.Type.HELMET, new Item.Properties())),
                register(itemList, "robes_plate", () -> new ItemArmorFSB(ArmorMaterials.CHAIN, ArmorItem.Type.CHESTPLATE, new Item.Properties())),
                register(itemList, "robes_legs", () -> new ItemArmorFSB(ArmorMaterials.CHAIN, ArmorItem.Type.LEGGINGS, new Item.Properties())),
                register(itemList, "robes_boots", () -> new ItemArmorFSB(ArmorMaterials.CHAIN, ArmorItem.Type.BOOTS, new Item.Properties())));
        DNT = new Suit(register(itemList, "dnt_helmet", () -> new ItemArmorFSB(HBMArmorMats.DNT, ArmorItem.Type.HELMET, new Item.Properties())),
                register(itemList, "dnt_plate", () -> new ItemArmorFSB(HBMArmorMats.DNT, ArmorItem.Type.CHESTPLATE, new Item.Properties())),
                register(itemList, "dnt_legs", () -> new ItemArmorFSB(HBMArmorMats.DNT, ArmorItem.Type.LEGGINGS, new Item.Properties())),
                register(itemList, "dnt_boots", () -> new ItemArmorFSB(HBMArmorMats.DNT, ArmorItem.Type.BOOTS, new Item.Properties())));
        ZIRCONIUM = new Suit(null, null,
                register(itemList, "zirconium_legs", () -> new ItemArmorFSB(HBMArmorMats.ZIRCONIUM, ArmorItem.Type.LEGGINGS, new Item.Properties())), null);
//        T45 = new Suit(register(itemList, "t45_helmet", () -> new ItemArmorT45(HBMArmorMats.T45, ArmorItem.Type.HELMET, new Item.Properties(),1000000, 10000, 1000, 5)),
//                register(itemList, "t45_plate", () -> new ItemArmorT45(HBMArmorMats.T45, ArmorItem.Type.CHESTPLATE, new Item.Properties(),1000000, 10000, 1000, 5)),
//                register(itemList, "t45_legs", () -> new ItemArmorT45(HBMArmorMats.T45, ArmorItem.Type.LEGGINGS, new Item.Properties(),1000000, 10000, 1000, 5)),
//                register(itemList, "t45_boots", () -> new ItemArmorT45(HBMArmorMats.T45, ArmorItem.Type.BOOTS, new Item.Properties(),1000000, 10000, 1000, 5)));
    }
    public static void creativeTab(MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries){
        itemList.forEach(itemRegistryObject -> entries.put(new ItemStack(itemRegistryObject.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS));
    }
    public static void genModel(ItemModelProvider provider){
        itemList.forEach(itemRegistryObject -> provider.basicItem(itemRegistryObject.get()));
    }
    public static void languageSupport(LanguageProvider provider){
        itemList.forEach(itemRegistryObject -> provider.add(itemRegistryObject.get(), generateOrderlyName(itemRegistryObject.getId().getPath())));
    }
    public record Suit(@Nullable RegistryObject<Item> HELMET, RegistryObject<Item> PLATE, RegistryObject<Item> LEGS, RegistryObject<Item> BOOT){
    }
}
