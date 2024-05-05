package com.hbm.item.armor;

import com.hbm.item.ModItems;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

//EP装甲材料，HBM中最高级的装甲材料，暂时各项指标复制钻石指标
public class EuphemiumArmorMaterial implements ArmorMaterial {
    //取值排列：头盔、护腿、胸甲、靴子
    private static final int[] BASE_DURABILITY = new int[] {13, 15, 16, 11};
    private static final int[] PROTECTION_VALUES = new int[] {3, 6, 8, 3};
    @Override
    public int getDurability(ArmorItem.Type type) {
        return BASE_DURABILITY[type.getEquipmentSlot().getArmorStandSlotId() - 1] * 33;
    }

    @Override
    public int getProtection(ArmorItem.Type type) {
        return PROTECTION_VALUES[type.getEquipmentSlot().getArmorStandSlotId() - 1];
    }

    @Override
    public int getEnchantability() {
        return 10;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(ModItems.plate_euphemium);
    }

    @Override
    public String getName() {
        return "euphemium";
    }

    @Override
    public float getToughness() {
        return 0.0f;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.0f;
    }
}
