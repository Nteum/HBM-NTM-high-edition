package com.hbm.datagen.recipe.provider;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.Inventory.recipe.FractionRecipe;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.Inventory.recipe.base.RecipeSerializerBuilder;
import com.hbm.core.contents.fluid.HBMFluids;
import com.hbm.datagen.recipe.ISubRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

/**
 * 分馏塔配方（象征性：重油 → 沥青/粘稠油）。
 */
public class FractionRecipeProvider implements ISubRecipeProvider {
    @Override
    public void addRecipes(Consumer<FinishedRecipe> consumer) {
        RecipeSerializerBuilder.AutoRecipeSerializer<FractionRecipe> serializer =
                (RecipeSerializerBuilder.AutoRecipeSerializer<FractionRecipe>) ModRecipes.FRACTION.serializer().get();

        serializer.getBuilder(HBM.rl("heavyoil_to_fractions").withPrefix("fraction/")).setValue(
                new FluidStack(HBMFluids.HEAVYOIL.source().get(), 100),
                new FluidStack(HBMFluids.BITUMEN.source().get(), 30),
                new FluidStack(HBMFluids.SMEAR.source().get(), 70)
        ).save(consumer);

        serializer.getBuilder(HBM.rl("smear_to_fractions").withPrefix("fraction/")).setValue(
                new FluidStack(HBMFluids.SMEAR.source().get(), 100),
                new FluidStack(HBMFluids.HEATINGOIL.source().get(), 60),
                new FluidStack(HBMFluids.LUBRICANT.source().get(), 40)
        ).save(consumer);
    }
}
