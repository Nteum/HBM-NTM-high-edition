package com.hbm.datagen.recipe.provider;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.Inventory.recipe.RadiolysisRecipe;
import com.hbm.Inventory.recipe.base.RecipeSerializerBuilder;
import com.hbm.core.contents.fluid.HBMFluids;
import com.hbm.datagen.recipe.ISubRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

/**
 * 辐射裂解配方（象征性示例：水 → 过氧化氢 + 氢气）。
 */
public class RadiolysisRecipeProvider implements ISubRecipeProvider {
    @Override
    public void addRecipes(Consumer<FinishedRecipe> consumer) {
        RecipeSerializerBuilder.AutoRecipeSerializer<RadiolysisRecipe> serializer =
                (RecipeSerializerBuilder.AutoRecipeSerializer<RadiolysisRecipe>) ModRecipes.RADIOLYSIS.serializer().get();

        serializer.getBuilder(HBM.rl("water_to_peroxide_hydrogen").withPrefix("radiolysis/")).setValue(
                new FluidStack(HBMFluids.WATER.source().get(), 100),
                new FluidStack(HBMFluids.PEROXIDE.source().get(), 80),
                new FluidStack(HBMFluids.HYDROGEN.source().get(), 20)
        ).save(consumer);
    }
}
