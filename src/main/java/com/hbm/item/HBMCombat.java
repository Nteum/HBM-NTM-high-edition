package com.hbm.item;

import com.hbm.datagen.LanguageProvider;
import com.hbm.datagen.model.ItemModelGen;
import com.hbm.effect.ModEffects;
import com.hbm.item.armor.*;
import net.minecraft.client.model.ArmorStandModel;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.*;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 战斗物品，注册在原版创造物品栏里战斗物品那块
 * */
public class HBMCombat extends HBMComponent{
    private static final List<RegistryObject<Item>> itemList = new ArrayList<>();
    private static final List<RegistryObject<Item>> standaloneModels = new ArrayList<>();
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
    public static Suit SCHRABIDIUM;

    public static Suit T45;
    public static Suit T51;
    
    public static Suit BISMUTH;

    public static void register(DeferredRegister<Item> ITEMS){
        // 一般盔甲
        STEEL = new Suit(register(itemList, "steel_helmet", () -> new ItemArmorFSB(HBMArmorMats.STEEL, ArmorItem.Type.HELMET, new Item.Properties())),
                register(itemList, "steel_plate", () -> new ItemArmorFSB(HBMArmorMats.STEEL, ArmorItem.Type.CHESTPLATE, new Item.Properties(), ()->STEEL).cloneStats((ItemArmorFSB) STEEL.HELMET.get())),
                register(itemList, "steel_legs", () -> new ItemArmorFSB(HBMArmorMats.STEEL, ArmorItem.Type.LEGGINGS, new Item.Properties()).cloneStats((ItemArmorFSB) STEEL.HELMET.get())),
                register(itemList, "steel_boots", () -> new ItemArmorFSB(HBMArmorMats.STEEL, ArmorItem.Type.BOOTS, new Item.Properties()).cloneStats((ItemArmorFSB) STEEL.HELMET.get())));
        TITANIUM = new Suit(register(itemList, "titanium_helmet", () -> new ItemArmorFSB(HBMArmorMats.TITANIUM, ArmorItem.Type.HELMET, new Item.Properties())),
                register(itemList, "titanium_plate", () -> new ItemArmorFSB(HBMArmorMats.TITANIUM, ArmorItem.Type.CHESTPLATE, new Item.Properties(), ()->TITANIUM).cloneStats((ItemArmorFSB) TITANIUM.HELMET.get())),
                register(itemList, "titanium_legs", () -> new ItemArmorFSB(HBMArmorMats.TITANIUM, ArmorItem.Type.LEGGINGS, new Item.Properties()).cloneStats((ItemArmorFSB) TITANIUM.HELMET.get())),
                register(itemList, "titanium_boots", () -> new ItemArmorFSB(HBMArmorMats.TITANIUM, ArmorItem.Type.BOOTS, new Item.Properties()).cloneStats((ItemArmorFSB) TITANIUM.HELMET.get())));
        ALLOY = new Suit(register(itemList, "alloy_helmet", () -> new ItemArmorFSB(HBMArmorMats.ALLOY, ArmorItem.Type.HELMET, new Item.Properties())
                    .addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 2))
                    .addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 20, 2))
                    .addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20, 4))),
                register(itemList, "alloy_plate", () -> new ItemArmorFSB(HBMArmorMats.ALLOY, ArmorItem.Type.CHESTPLATE, new Item.Properties(), ()->ALLOY).cloneStats((ItemArmorFSB) ALLOY.HELMET.get())),
                register(itemList, "alloy_legs", () -> new ItemArmorFSB(HBMArmorMats.ALLOY, ArmorItem.Type.LEGGINGS, new Item.Properties()).cloneStats((ItemArmorFSB) ALLOY.HELMET.get())),
                register(itemList, "alloy_boots", () -> new ItemArmorFSB(HBMArmorMats.ALLOY, ArmorItem.Type.BOOTS, new Item.Properties()).cloneStats((ItemArmorFSB) ALLOY.HELMET.get())));
        CMB = new Suit(register(itemList, "cmb_helmet", () -> new ItemArmorFSB(HBMArmorMats.CMB, ArmorItem.Type.HELMET, new Item.Properties())),
                register(itemList, "cmb_plate", () -> new ItemArmorFSB(HBMArmorMats.CMB, ArmorItem.Type.CHESTPLATE, new Item.Properties(), ()->CMB).cloneStats((ItemArmorFSB) CMB.HELMET.get())),
                register(itemList, "cmb_legs", () -> new ItemArmorFSB(HBMArmorMats.CMB, ArmorItem.Type.LEGGINGS, new Item.Properties()).cloneStats((ItemArmorFSB) CMB.HELMET.get())),
                register(itemList, "cmb_boots", () -> new ItemArmorFSB(HBMArmorMats.CMB, ArmorItem.Type.BOOTS, new Item.Properties()).cloneStats((ItemArmorFSB) CMB.HELMET.get())));
        PAA = new Suit(null,
                register(itemList, "paa_plate", () -> new ItemArmorFSB(HBMArmorMats.PAA, ArmorItem.Type.CHESTPLATE, new Item.Properties(), ()->PAA)
                        .addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 20, 0))),
                register(itemList, "paa_legs", () -> new ItemArmorFSB(HBMArmorMats.PAA, ArmorItem.Type.LEGGINGS, new Item.Properties()).cloneStats((ItemArmorFSB) PAA.PLATE.get())),
                register(itemList, "paa_boots", () -> new ItemArmorFSB(HBMArmorMats.PAA, ArmorItem.Type.BOOTS, new Item.Properties()).cloneStats((ItemArmorFSB) PAA.PLATE.get())));
        ASBESTOS = new Suit(register(itemList, "asbestos_helmet", () -> new ItemArmorFSB(HBMArmorMats.ASBESTOS, ArmorItem.Type.HELMET, new Item.Properties())),
                register(itemList, "asbestos_plate", () -> new ItemArmorFSB(HBMArmorMats.ASBESTOS, ArmorItem.Type.CHESTPLATE, new Item.Properties(), ()->ASBESTOS).cloneStats((ItemArmorFSB) ASBESTOS.HELMET.get())),
                register(itemList, "asbestos_legs", () -> new ItemArmorFSB(HBMArmorMats.ASBESTOS, ArmorItem.Type.LEGGINGS, new Item.Properties()).cloneStats((ItemArmorFSB) ASBESTOS.HELMET.get())),
                register(itemList, "asbestos_boots", () -> new ItemArmorFSB(HBMArmorMats.ASBESTOS, ArmorItem.Type.BOOTS, new Item.Properties()).cloneStats((ItemArmorFSB) ASBESTOS.HELMET.get())));
        SECURITY = new Suit(register(itemList, "security_helmet", () -> new ItemArmorFSB(HBMArmorMats.SECURITY, ArmorItem.Type.HELMET, new Item.Properties())),
                register(itemList, "security_plate", () -> new ItemArmorFSB(HBMArmorMats.SECURITY, ArmorItem.Type.CHESTPLATE, new Item.Properties(), ()->SECURITY).cloneStats((ItemArmorFSB) SECURITY.HELMET.get())),
                register(itemList, "security_legs", () -> new ItemArmorFSB(HBMArmorMats.SECURITY, ArmorItem.Type.LEGGINGS, new Item.Properties()).cloneStats((ItemArmorFSB) SECURITY.HELMET.get())),
                register(itemList, "security_boots", () -> new ItemArmorFSB(HBMArmorMats.SECURITY, ArmorItem.Type.BOOTS, new Item.Properties()).cloneStats((ItemArmorFSB) SECURITY.HELMET.get())));
        COBALT = new Suit(register(itemList, "cobalt_helmet", () -> new ItemArmorFSB(HBMArmorMats.COBALT, ArmorItem.Type.HELMET, new Item.Properties())),
                register(itemList, "cobalt_plate", () -> new ItemArmorFSB(HBMArmorMats.COBALT, ArmorItem.Type.CHESTPLATE, new Item.Properties(), ()->COBALT).cloneStats((ItemArmorFSB) COBALT.HELMET.get())),
                register(itemList, "cobalt_legs", () -> new ItemArmorFSB(HBMArmorMats.COBALT, ArmorItem.Type.LEGGINGS, new Item.Properties()).cloneStats((ItemArmorFSB) COBALT.HELMET.get())),
                register(itemList, "cobalt_boots", () -> new ItemArmorFSB(HBMArmorMats.COBALT, ArmorItem.Type.BOOTS, new Item.Properties()).cloneStats((ItemArmorFSB) COBALT.HELMET.get())));
        STARMETAL = new Suit(register(itemList, "starmetal_helmet", () -> new ItemArmorFSB(HBMArmorMats.STARMETAL, ArmorItem.Type.HELMET, new Item.Properties())),
                register(itemList, "starmetal_plate", () -> new ItemArmorFSB(HBMArmorMats.STARMETAL, ArmorItem.Type.CHESTPLATE, new Item.Properties(), ()->STARMETAL).cloneStats((ItemArmorFSB) STARMETAL.HELMET.get())),
                register(itemList, "starmetal_legs", () -> new ItemArmorFSB(HBMArmorMats.STARMETAL, ArmorItem.Type.LEGGINGS, new Item.Properties()).cloneStats((ItemArmorFSB) STARMETAL.HELMET.get())),
                register(itemList, "starmetal_boots", () -> new ItemArmorFSB(HBMArmorMats.STARMETAL, ArmorItem.Type.BOOTS, new Item.Properties()).cloneStats((ItemArmorFSB) STARMETAL.HELMET.get())));
        ROBES = new Suit(register(itemList, "robes_helmet", () -> new ItemArmorFSB(ArmorMaterials.CHAIN, ArmorItem.Type.HELMET, new Item.Properties())),
                register(itemList, "robes_plate", () -> new ItemArmorFSB(ArmorMaterials.CHAIN, ArmorItem.Type.CHESTPLATE, new Item.Properties(), ()->ROBES).cloneStats((ItemArmorFSB) ROBES.HELMET.get())),
                register(itemList, "robes_legs", () -> new ItemArmorFSB(ArmorMaterials.CHAIN, ArmorItem.Type.LEGGINGS, new Item.Properties()).cloneStats((ItemArmorFSB) ROBES.HELMET.get())),
                register(itemList, "robes_boots", () -> new ItemArmorFSB(ArmorMaterials.CHAIN, ArmorItem.Type.BOOTS, new Item.Properties()).cloneStats((ItemArmorFSB) ROBES.HELMET.get())));
        DNT = new Suit(register(itemList, "dnt_helmet", () -> new ItemArmorFSB(HBMArmorMats.DNT, ArmorItem.Type.HELMET, new Item.Properties())),
                register(itemList, "dnt_plate", () -> new ItemArmorFSB(HBMArmorMats.DNT, ArmorItem.Type.CHESTPLATE, new Item.Properties(), ()->DNT).cloneStats((ItemArmorFSB) DNT.HELMET.get())),
                register(itemList, "dnt_legs", () -> new ItemArmorFSB(HBMArmorMats.DNT, ArmorItem.Type.LEGGINGS, new Item.Properties()).cloneStats((ItemArmorFSB) DNT.HELMET.get())),
                register(itemList, "dnt_boots", () -> new ItemArmorFSB(HBMArmorMats.DNT, ArmorItem.Type.BOOTS, new Item.Properties()).cloneStats((ItemArmorFSB) DNT.HELMET.get())));
        ZIRCONIUM = new Suit(null, null,
                register(itemList, "zirconium_legs", () -> new ItemArmorFSB(HBMArmorMats.ZIRCONIUM, ArmorItem.Type.LEGGINGS, new Item.Properties())), null);
        SCHRABIDIUM = new Suit(register(itemList, "schrabidium_helmet", () -> new ItemArmorFSB(HBMArmorMats.SCHRABIDIUM, ArmorItem.Type.HELMET, new Item.Properties())
                    .addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 20, 2))
                    .addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20, 2))
                    .addEffect(new MobEffectInstance(MobEffects.JUMP, 20, 1))
                    .addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 2))),
                register(itemList, "schrabidium_plate", () -> new ItemArmorFSB(HBMArmorMats.SCHRABIDIUM, ArmorItem.Type.CHESTPLATE, new Item.Properties(), ()->SCHRABIDIUM).cloneStats((ItemArmorFSB) SCHRABIDIUM.HELMET.get())),
                register(itemList, "schrabidium_legs", () -> new ItemArmorFSB(HBMArmorMats.SCHRABIDIUM, ArmorItem.Type.LEGGINGS, new Item.Properties()).cloneStats((ItemArmorFSB) SCHRABIDIUM.HELMET.get())),
                register(itemList, "schrabidium_boots", () -> new ItemArmorFSB(HBMArmorMats.SCHRABIDIUM, ArmorItem.Type.BOOTS, new Item.Properties()).cloneStats((ItemArmorFSB) SCHRABIDIUM.HELMET.get())));
        // 特殊盔甲
        T51 = new Suit(register(standaloneModels, "t51_helmet", () -> new ItemArmorT51(HBMArmorMats.T45, ArmorItem.Type.HELMET, new Item.Properties(),1000000, 10000, 1000, 5)),
                register(standaloneModels, "t51_plate", () -> new ItemArmorT51(HBMArmorMats.T45, ArmorItem.Type.CHESTPLATE, new Item.Properties(), 1000000, 10000, 1000, 5).cloneStats((ItemArmorFSB) T51.HELMET.get())),
                register(standaloneModels, "t51_legs", () -> new ItemArmorT51(HBMArmorMats.T45, ArmorItem.Type.LEGGINGS, new Item.Properties(), 1000000, 10000, 1000, 5).cloneStats((ItemArmorFSB) T51.HELMET.get())),
                register(standaloneModels, "t51_boots", () -> new ItemArmorT51(HBMArmorMats.T45, ArmorItem.Type.BOOTS, new Item.Properties(), 1000000, 10000, 1000, 5).cloneStats((ItemArmorFSB) T51.HELMET.get())));
        BISMUTH = new Suit(register(standaloneModels, "bismuth_helmet", () -> new ItemArmorBismuth(HBMArmorMats.BISMUTH, ArmorItem.Type.HELMET, new Item.Properties())
                    .addEffect(new MobEffectInstance(MobEffects.JUMP, 20, 6))
                    .addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 6))
                    .addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, 1))
                    .addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15 * 20, 0))
                    .setDashCount(3)),
                register(standaloneModels, "bismuth_plate", () -> new ItemArmorBismuth(HBMArmorMats.BISMUTH, ArmorItem.Type.CHESTPLATE, new Item.Properties(), ()->BISMUTH).cloneStats((ItemArmorFSB) BISMUTH.HELMET.get())),
                register(standaloneModels, "bismuth_legs", () -> new ItemArmorBismuth(HBMArmorMats.BISMUTH, ArmorItem.Type.LEGGINGS, new Item.Properties()).cloneStats((ItemArmorFSB) BISMUTH.HELMET.get())),
                register(standaloneModels, "bismuth_boots", () -> new ItemArmorBismuth(HBMArmorMats.BISMUTH, ArmorItem.Type.BOOTS, new Item.Properties()).cloneStats((ItemArmorFSB) BISMUTH.HELMET.get())));
    }
    public static void creativeTab(MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries){
        itemList.forEach(itemRegistryObject -> entries.put(new ItemStack(itemRegistryObject.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS));
        standaloneModels.forEach(itemRegistryObject -> entries.put(new ItemStack(itemRegistryObject.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS));
    }
    public static void genModel(ItemModelGen provider){
        itemList.forEach(itemRegistryObject -> provider.basicItem(itemRegistryObject.get()));
        standaloneModels.forEach(itemRegistryObject -> provider.builtinModel(itemRegistryObject.get()));
    }
    public static void languageSupport(LanguageProvider provider){
        itemList.forEach(itemRegistryObject -> provider.add(itemRegistryObject.get(), generateOrderlyName(itemRegistryObject.getId().getPath())));
        standaloneModels.forEach(itemRegistryObject -> provider.add(itemRegistryObject.get(), generateOrderlyName(itemRegistryObject.getId().getPath())));
    }
    public record Suit(@Nullable RegistryObject<Item> HELMET, RegistryObject<Item> PLATE, RegistryObject<Item> LEGS, RegistryObject<Item> BOOT){
    }
}
