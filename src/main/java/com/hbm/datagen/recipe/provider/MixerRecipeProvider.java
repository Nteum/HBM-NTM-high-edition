package com.hbm.datagen.recipe.provider;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.Inventory.recipe.MixerRecipe;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.Inventory.recipe.base.RecipeSerializerBuilder;
import com.hbm.core.contents.fluid.HBMFluids;
import com.hbm.datagen.recipe.ISubRecipeProvider;
import com.hbm.datagen.recipe.ingredient.CountableIngredient;
import com.hbm.registries.ModItems;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

/**
 * 混合机配方（象征性：水 + 硫磺 → 硫酸）。
 */
public class MixerRecipeProvider implements ISubRecipeProvider {
    @Override
    public void addRecipes(Consumer<FinishedRecipe> consumer) {
        RecipeSerializerBuilder.AutoRecipeSerializer<MixerRecipe> serializer =
                (RecipeSerializerBuilder.AutoRecipeSerializer<MixerRecipe>) ModRecipes.MIXER.serializer().get();

        serializer.getBuilder(HBM.rl("water_sulfur_to_sulfuric").withPrefix("mixer/")).setValue(
                new FluidStack(HBMFluids.WATER.source().get(), 800),
                null,
                CountableIngredient.of(ModItems.POWDER_ASH_COAL.get(), 1),
                500,
                50
        ).save(consumer);

        serializer.getBuilder(HBM.rl("hydrogen_oxygen_to_oxyhydrogen").withPrefix("mixer/")).setValue(
                new FluidStack(HBMFluids.HYDROGEN.source().get(), 500),
                new FluidStack(HBMFluids.OXYGEN.source().get(), 500),
                null,
                1000,
                50
        ).save(consumer);
    }
}
