package com.hbm.datagen.recipe.provider;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.Inventory.recipe.VacuumRefineryRecipe;
import com.hbm.Inventory.recipe.base.RecipeSerializerBuilder;
import com.hbm.core.contents.fluid.HBMFluids;
import com.hbm.datagen.recipe.ISubRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

/**
 * 真空蒸馏配方（象征性：原油 → 4 种馏分）。
 */
public class VacuumRefineryRecipeProvider implements ISubRecipeProvider {
    @Override
    public void addRecipes(Consumer<FinishedRecipe> consumer) {
        RecipeSerializerBuilder.AutoRecipeSerializer<VacuumRefineryRecipe> serializer =
                (RecipeSerializerBuilder.AutoRecipeSerializer<VacuumRefineryRecipe>) ModRecipes.VACUUM_REFINERY.serializer().get();

        serializer.getBuilder(HBM.rl("oil_to_vacuum_fractions").withPrefix("vacuum_refinery/")).setValue(
                new FluidStack(HBMFluids.OIL.source().get(), 100),
                new FluidStack(HBMFluids.HEAVYOIL_VACUUM.source().get(), 40),
                new FluidStack(HBMFluids.REFORMATE.source().get(), 25),
                new FluidStack(HBMFluids.LIGHTOIL_VACUUM.source().get(), 20),
                new FluidStack(HBMFluids.SOURGAS.source().get(), 15)
        ).save(consumer);
    }
}
