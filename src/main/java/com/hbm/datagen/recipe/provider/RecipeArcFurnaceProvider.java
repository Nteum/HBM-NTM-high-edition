package com.hbm.datagen.recipe.provider;

import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.Inventory.recipe.RecipeArcFurnace;
import com.hbm.Inventory.recipe.RecipeCrystallizer;
import com.hbm.Inventory.recipe.base.RecipeSerializerBuilder;
import com.hbm.datagen.recipe.ISubRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

public class RecipeArcFurnaceProvider implements ISubRecipeProvider {

    @Override
    public void addRecipes(Consumer<FinishedRecipe> consumer) {
        RecipeSerializerBuilder.AutoRecipeSerializer<RecipeArcFurnace> serializer = (RecipeSerializerBuilder.AutoRecipeSerializer<RecipeArcFurnace>) ModRecipes.ARC_FURNACE.serializer().get();
    }
}
