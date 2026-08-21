package com.hbm.datagen.recipe.provider;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.Inventory.recipe.ReformingRecipe;
import com.hbm.Inventory.recipe.base.RecipeSerializerBuilder;
import com.hbm.core.contents.fluid.HBMFluids;
import com.hbm.datagen.recipe.ISubRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

/**
 * 催化重整配方（象征性：石脑油 → 重整产物/石油/氢气）。
 */
public class ReformingRecipeProvider implements ISubRecipeProvider {
    @Override
    public void addRecipes(Consumer<FinishedRecipe> consumer) {
        RecipeSerializerBuilder.AutoRecipeSerializer<ReformingRecipe> serializer =
                (RecipeSerializerBuilder.AutoRecipeSerializer<ReformingRecipe>) ModRecipes.REFORMING.serializer().get();

        serializer.getBuilder(HBM.rl("naphtha_to_reformate").withPrefix("reforming/")).setValue(
                new FluidStack(HBMFluids.NAPHTHA.source().get(), 100),
                new FluidStack(HBMFluids.REFORMATE.source().get(), 50),
                new FluidStack(HBMFluids.PETROLEUM.source().get(), 15),
                new FluidStack(HBMFluids.HYDROGEN.source().get(), 10)
        ).save(consumer);
    }
}
