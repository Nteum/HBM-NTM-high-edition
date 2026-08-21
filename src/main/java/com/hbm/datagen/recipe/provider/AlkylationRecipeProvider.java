package com.hbm.datagen.recipe.provider;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.Inventory.recipe.AlkylationRecipe;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.Inventory.recipe.base.RecipeSerializerBuilder;
import com.hbm.core.contents.fluid.HBMFluids;
import com.hbm.datagen.recipe.ISubRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

/**
 * 烷基化配方（象征性：氯甲烷 → 不饱和烃/氯气）。
 */
public class AlkylationRecipeProvider implements ISubRecipeProvider {
    @Override
    public void addRecipes(Consumer<FinishedRecipe> consumer) {
        RecipeSerializerBuilder.AutoRecipeSerializer<AlkylationRecipe> serializer =
                (RecipeSerializerBuilder.AutoRecipeSerializer<AlkylationRecipe>) ModRecipes.ALKYLATION.serializer().get();

        serializer.getBuilder(HBM.rl("chloromethane_to_products").withPrefix("alkylation/")).setValue(
                new FluidStack(HBMFluids.CHLOROMETHANE.source().get(), 100),
                null,
                new FluidStack(HBMFluids.UNSATURATEDS.source().get(), 75),
                new FluidStack(HBMFluids.CHLORINE.source().get(), 25)
        ).save(consumer);

        serializer.getBuilder(HBM.rl("unsaturates_to_aromatics").withPrefix("alkylation/")).setValue(
                new FluidStack(HBMFluids.UNSATURATEDS.source().get(), 100),
                null,
                new FluidStack(HBMFluids.AROMATICS.source().get(), 70),
                new FluidStack(HBMFluids.PETROLEUM.source().get(), 30)
        ).save(consumer);
    }
}
