package com.hbm.datagen.recipe.provider;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.Inventory.recipe.CryoRecipe;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.Inventory.recipe.base.RecipeSerializerBuilder;
import com.hbm.core.contents.fluid.HBMFluids;
import com.hbm.datagen.recipe.ISubRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

/**
 * 低温蒸馏配方（象征性：空气 → 氮气/氧气/氪气/二氧化碳）。
 */
public class CryoRecipeProvider implements ISubRecipeProvider {
    @Override
    public void addRecipes(Consumer<FinishedRecipe> consumer) {
        RecipeSerializerBuilder.AutoRecipeSerializer<CryoRecipe> serializer =
                (RecipeSerializerBuilder.AutoRecipeSerializer<CryoRecipe>) ModRecipes.CRYO.serializer().get();

        serializer.getBuilder(HBM.rl("air_to_components").withPrefix("cryo/")).setValue(
                new FluidStack(HBMFluids.EARTHAIR.source().get(), 100),
                new FluidStack(HBMFluids.NITROGEN.source().get(), 60),
                new FluidStack(HBMFluids.OXYGEN.source().get(), 25),
                new FluidStack(HBMFluids.KRYPTON.source().get(), 10),
                new FluidStack(HBMFluids.CARBONDIOXIDE.source().get(), 5)
        ).save(consumer);
    }
}
