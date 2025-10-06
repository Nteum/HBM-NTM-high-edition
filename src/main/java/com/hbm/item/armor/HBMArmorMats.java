package com.hbm.item.armor;

import com.hbm.HBM;
import com.hbm.item.HBMComponent;
import com.hbm.registries.ModItems;
import cpw.mods.modlauncher.EnumerationHelper;
import net.minecraft.Util;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fml.common.asm.RuntimeEnumExtender;

import java.util.EnumMap;
import java.util.function.Supplier;

// 盔甲材料
// 模仿ArmorMaterials
public enum HBMArmorMats implements StringRepresentable, ArmorMaterial {
    STEEL("steel", 20, new int[] { 2, 6, 5, 2 }, 5, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, ()->Ingredient.of(HBMComponent.STEEL_INGOT.get())),
    TITANIUM("titanium", 25, new int[] { 3, 8, 6, 3 }, 9, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, ()->Ingredient.of(HBMComponent.INGOT_TITANIUM.get())),
    ALLOY("alloy", 40, new int[] { 3, 8, 6, 3 }, 12, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, ()->Ingredient.of(ModItems.ingot_advanced_alloy.get())),
    COBALT("cobalt", 70, new int[] { 3, 8, 6, 3 }, 60, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, ()->Ingredient.of(HBMComponent.INGOT_COBALT.get())),
    STARMETAL("starmetal", 150, new int[] { 3, 8, 6, 3 }, 100, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, ()->Ingredient.of(HBMComponent.INGOT_STARMETAL.get())),
    BISMUTH("bismuth", 100, new int[] { 3, 8, 6, 3 }, 100, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, ()->Ingredient.of(HBMComponent.INGOT_BISMUTH.get())),
    ASBESTOS("asbestos", 20, new int[] { 1, 4, 3, 1 }, 5, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, ()->Ingredient.of(HBMComponent.INGOT_ASBESTOS.get())),
    PAA("paa", 75, new int[] { 3, 8, 6, 3 }, 25, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, ()->Ingredient.of(HBMComponent.PLATE_PAA.get())),
    CMB("cmb", 60, new int[] { 3, 8, 6, 3 }, 50, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, ()->Ingredient.of(HBMComponent.INGOT_COMBINE_STEEL.get())),
    AUS3("aus3", 375, new int[] { 2, 6, 5, 2 }, 0, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, ()->Ingredient.of(HBMComponent.INGOT_AUSTRALIUM.get())),
    HAZMAT("hazmat", 60, new int[] { 2, 5, 4, 1 }, 5, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, ()->Ingredient.of(HBMComponent.HAZMAT_CLOTH.get())),
    HAZMAT2("hazmat2", 60, new int[] { 2, 5, 4, 1 }, 5, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, ()->Ingredient.of(HBMComponent.HAZMAT_CLOTH_RED.get())),
    HAZMAT3("hazmat3", 60, new int[] { 2, 5, 4, 1 }, 5, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, ()->Ingredient.of(HBMComponent.HAZMAT_CLOTH_GREY.get())),
    SECURITY("security", 100, new int[] { 3, 8, 6, 3 }, 15, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, ()->Ingredient.of(ModItems.DUMMY_ITEM.get())),
    EUPHEMIUM("euphemium", 20, new int[] { 2, 6, 5, 2 }, 5, SoundEvents.ARMOR_EQUIP_NETHERITE, 0.0F, 0.0F, ()->Ingredient.of(HBMComponent.PLATE_EUPHEMIUM.get())),
    SCHRABIDIUM("schrabidium", 15000000, new int[] { 3, 8, 6, 3 }, 100, SoundEvents.ARMOR_EQUIP_DIAMOND, 0.0F, 0.0F, ()->Ingredient.of(HBMComponent.INGOT_SCHRABIDIUM.get())),
    DNT("dnt", 3, new int[] { 1, 1, 1, 1 }, 0, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, ()->Ingredient.of(HBMComponent.INGOT_DINEUTRONIUM.get())),
    ZIRCONIUM("zirconium", 1000, new int[] { 2, 5, 3, 1 }, 100, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, ()->Ingredient.of(HBMComponent.INGOT_ZIRCONIUM.get())),
    T45("t45", 150, new int[] { 3, 8, 6, 3 }, 0, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, ()->Ingredient.of(ModItems.DUMMY_ITEM.get())),
    ;

    private static final EnumMap<ArmorItem.Type, Integer> HEALTH_FUNCTION_FOR_TYPE = Util.make(new EnumMap<>(ArmorItem.Type.class), (p_266653_) -> {
        p_266653_.put(ArmorItem.Type.BOOTS, 13);
        p_266653_.put(ArmorItem.Type.LEGGINGS, 15);
        p_266653_.put(ArmorItem.Type.CHESTPLATE, 16);
        p_266653_.put(ArmorItem.Type.HELMET, 11);
    });
    private final String name;
    private final int durabilityMultiplier;
    private final EnumMap<ArmorItem.Type, Integer> protectionFunctionForType;
    private final int enchantmentValue;
    private final SoundEvent sound;
    private final float toughness;
    private final float knockbackResistance;
    private final LazyLoadedValue<Ingredient> repairIngredient;

    private HBMArmorMats(String pName, int pDurabilityMultiplier, int[] protection, int pEnchantmentValue, SoundEvent pSound, float pToughness, float pKnockbackResistance, Supplier<Ingredient> pRepairIngredient) {
        this.name = pName;
        this.durabilityMultiplier = pDurabilityMultiplier;
        this.protectionFunctionForType = Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
            map.put(ArmorItem.Type.HELMET, protection[0]);map.put(ArmorItem.Type.CHESTPLATE, protection[1]);map.put(ArmorItem.Type.LEGGINGS, protection[2]);map.put(ArmorItem.Type.BOOTS, protection[3]);
        });
        this.enchantmentValue = pEnchantmentValue;
        this.sound = pSound;
        this.toughness = pToughness;
        this.knockbackResistance = pKnockbackResistance;
        this.repairIngredient = new LazyLoadedValue<>(pRepairIngredient);
    }

    public int getDurabilityForType(ArmorItem.Type pType) {
        return HEALTH_FUNCTION_FOR_TYPE.get(pType) * this.durabilityMultiplier;
    }

    public int getDefenseForType(ArmorItem.Type pType) {
        return this.protectionFunctionForType.get(pType);
    }

    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    public SoundEvent getEquipSound() {
        return this.sound;
    }

    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    public String getName() {
        return HBM.rl(this.name).toString();
    }

    public float getToughness() {
        return this.toughness;
    }

    /**
     * Gets the percentage of knockback resistance provided by armor of the material.
     */
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }

    public String getSerializedName() {
        return this.name;
    }
}
