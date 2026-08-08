package com.hbm.datagen.recipe.provider;

import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.Inventory.recipe.RecipeCentrifuge;
import com.hbm.Inventory.recipe.RecipeCrystallizer;
import com.hbm.Inventory.recipe.base.RecipeSerializerBuilder;
import com.hbm.datagen.recipe.ISubRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

public class RecipeCrysrtallizerProvider implements ISubRecipeProvider {

    @Override
    public void addRecipes(Consumer<FinishedRecipe> consumer) {
        RecipeSerializerBuilder.AutoRecipeSerializer<RecipeCrystallizer> serializer = (RecipeSerializerBuilder.AutoRecipeSerializer<RecipeCrystallizer>) ModRecipes.CRYSTALLIZER.serializer().get();


    }
}
