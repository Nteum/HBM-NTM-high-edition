package com.hbm.item.tool;

import com.hbm.item.ModItems;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Lazy;

import java.util.function.Supplier;

public enum ModToolMaterials implements ToolMaterial {
    SCHRABIDIUM(3, 10000, 50.0F, 100.0F, 200,()->Ingredient.ofItems(ModItems.ingot_schrabidium)),
    SCHRABIDIUMHAMMER(3, 0, 50.0F, 999999996F, 200,()->Ingredient.ofItems(ModItems.ingot_schrabidium)),
    CHAINSAW(3, 1500, 50.0F, 22.0F, 0,()->Ingredient.ofItems(ModItems.ingot_schrabidium)),
    HBM_STEEL(2, 500, 7.5F, 2.0F, 10,()->Ingredient.ofItems(ModItems.ingot_schrabidium)),
    HBM_TITANIUM(3, 750, 9.0F, 2.5F, 15,()->Ingredient.ofItems(ModItems.ingot_schrabidium)),
    HBM_ALLOY(3, 2000, 15.0F, 5.0F, 5,()->Ingredient.ofItems(ModItems.ingot_schrabidium)),
    HBM_CMB(3, 8500, 40.0F, 55F, 100,()->Ingredient.ofItems(ModItems.ingot_schrabidium)),
    HBM_ELEC(3, 0, 30.0F, 12.0F, 2,()->Ingredient.ofItems(ModItems.ingot_schrabidium)),
    HBM_DESH(2, 0, 7.5F, 2.0F, 10,()->Ingredient.ofItems(ModItems.ingot_schrabidium)),
    HBM_COBALT(3, 750, 9.0F, 2.5F, 60,()->Ingredient.ofItems(ModItems.ingot_schrabidium)),
    SAW(2, 750, 2.0F, 3.5F, 25,()->Ingredient.ofItems(ModItems.ingot_schrabidium)),
    BAT(0, 500, 1.5F, 3F, 25,()->Ingredient.ofItems(ModItems.ingot_schrabidium)),
    BATNAIL(0, 450, 1.0F, 4F, 25,()->Ingredient.ofItems(ModItems.ingot_schrabidium)),
    GOLFCLUB(1, 1000, 2.0F, 5F, 25,()->Ingredient.ofItems(ModItems.ingot_schrabidium)),
    PIPERUSTY(1, 350, 1.5F, 4.5F, 25,()->Ingredient.ofItems(ModItems.ingot_schrabidium)),
    PIPELEAD(1, 250, 1.5F, 5.5F, 25,()->Ingredient.ofItems(ModItems.ingot_schrabidium)),
    OPENER(1, 250, 1.5F, 0.5F, 200,()->Ingredient.ofItems(ModItems.ingot_schrabidium)),
    SHIMMERSLEDGE(1, 0, 25.0F, 26F, 200,()->Ingredient.ofItems(ModItems.ingot_schrabidium)),
    MULTITOOL(3, 5000, 25F, 5.5F, 25,()->Ingredient.ofItems(ModItems.ingot_schrabidium));
    private final int miningLevel;
    private final int itemDurability;
    private final float miningSpeed;
    private final float attackDamage;
    private final int enchantability;
    private final Lazy<Ingredient> repairIngredient;

    private ModToolMaterials(int miningLevel, int itemDurability, float miningSpeed, float attackDamage, int enchantability, Supplier<Ingredient> repairIngredient) {
        this.miningLevel = miningLevel;
        this.itemDurability = itemDurability;
        this.miningSpeed = miningSpeed;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairIngredient = new Lazy<Ingredient>(repairIngredient);
    }

    @Override
    public int getDurability() {
        return this.itemDurability;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return this.miningSpeed;
    }

    @Override
    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public int getMiningLevel() {
        return this.miningLevel;
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}
