package com.hbm.datagen.recipe.provider;

import com.hbm.datagen.recipe.ISubRecipeProvider;
import com.hbm.datagen.recipe.builder.RecipePressBuilder;
import com.hbm.item.tool.ItemStamp.*;
import com.hbm.registries.ModItems;
import com.hbm.registries.OreDictManager;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;

public class PressRecipeProvider implements ISubRecipeProvider {
    @Override
    public void addRecipes(Consumer<FinishedRecipe> consumer) {
        new RecipePressBuilder(StampType.PLATE, OreDictManager.IRON.ingot(),ModItems.PLATE_IRON.get()).save(consumer);
//        new RecipePressBuilder(StampType.PLATE, OreDictManager.GOLD.ingot(),ModItems.plate_gold).save(consumer);
//        new RecipePressBuilder(StampType.PLATE, OreDictManager.TI.ingot(),ModItems.plate_titanium).save(consumer);
//        new RecipePressBuilder(StampType.PLATE, OreDictManager.AL.ingot(),ModItems.plate_aluminium).save(consumer);
//        new RecipePressBuilder(StampType.PLATE, OreDictManager.STEEL.ingot(),ModItems.plate_steel).save(consumer);
//        new RecipePressBuilder(StampType.PLATE, OreDictManager.PB.ingot(),ModItems.plate_lead).save(consumer);
//        new RecipePressBuilder(StampType.PLATE, OreDictManager.CU.ingot(),ModItems.plate_copper).save(consumer);
        new RecipePressBuilder(StampType.PLATE, OreDictManager.ALLOY.ingot(),ModItems.PLATE_ADVANCED_ALLOY.get()).save(consumer);
//        new RecipePressBuilder(StampType.PLATE, OreDictManager.SA326.ingot(),ModItems.plate_schrabidium).save(consumer);
//        new RecipePressBuilder(StampType.PLATE, OreDictManager.CMB.ingot(),ModItems.plate_combine_steel).save(consumer);
//        new RecipePressBuilder(StampType.PLATE, OreDictManager.GUNMETAL.ingot(),ModItems.plate_gunmetal).save(consumer);
//        new RecipePressBuilder(StampType.PLATE, OreDictManager.WEAPONSTEEL.ingot(),ModItems.plate_weaponsteel).save(consumer);
//        new RecipePressBuilder(StampType.PLATE, OreDictManager.BIGMT.ingot(),ModItems.plate_saturnite).save(consumer);
//        new RecipePressBuilder(StampType.PLATE, OreDictManager.DURA.ingot(),ModItems.plate_dura_steel).save(consumer);
    }
}