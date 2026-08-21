package com.hbm.datagen.recipe.provider;

import com.hbm.HBM;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.Inventory.recipe.RefineryRecipe;
import com.hbm.Inventory.recipe.base.RecipeSerializerBuilder;
import com.hbm.core.contents.fluid.HBMFluids;
import com.hbm.datagen.recipe.ISubRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.function.Consumer;

/**
 * 炼油配方（象征性示例：热油 → 重油/石脑油/轻油/石油气）。
 */
public class RefineryRecipeProvider implements ISubRecipeProvider {
    @Override
    public void addRecipes(Consumer<FinishedRecipe> consumer) {
        RecipeSerializerBuilder.AutoRecipeSerializer<RefineryRecipe> serializer =
                (RecipeSerializerBuilder.AutoRecipeSerializer<RefineryRecipe>) ModRecipes.REFINERY.serializer().get();

        getBuilder(serializer, "hotoil_to_fractions").setValue(
                new FluidStack(HBMFluids.HOTOIL.source().get(), 1000),
                List.of(
                        new FluidStack(HBMFluids.HEAVYOIL.source().get(), 400),
                        new FluidStack(HBMFluids.NAPHTHA.source().get(), 200),
                        new FluidStack(HBMFluids.LIGHTOIL.source().get(), 200),
                        new FluidStack(HBMFluids.PETROLEUM.source().get(), 200)
                )
        ).save(consumer);
    }

    private static RecipeSerializerBuilder.AutoRecipeBuilder getBuilder(RecipeSerializerBuilder.AutoRecipeSerializer<?> serializer, String name){
        return serializer.getBuilder(HBM.rl(name).withPrefix("refinery/"));
    }
}
