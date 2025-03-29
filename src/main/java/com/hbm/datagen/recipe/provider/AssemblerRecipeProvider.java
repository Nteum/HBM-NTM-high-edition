package com.hbm.datagen.recipe.provider;

import com.hbm.datagen.recipe.ISubRecipeProvider;
import com.hbm.datagen.recipe.builder.ShapelessEnergyRecipeBuilder;
import com.hbm.registries.ModItems;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class AssemblerRecipeProvider implements ISubRecipeProvider {
    @Override
    public void addRecipes(Consumer<FinishedRecipe> consumer) {
        String basePath = "assembler/";
        ShapelessEnergyRecipeBuilder.assembler(ModItems.plate_iron.get(),2).requires(Items.IRON_INGOT,3).num(30).save(consumer,basePath+"plate_iron");
    }
}
