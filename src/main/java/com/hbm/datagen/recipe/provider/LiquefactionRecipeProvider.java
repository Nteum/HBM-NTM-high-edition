package com.hbm.datagen.recipe.provider;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.Inventory.recipe.LiquefactionRecipe;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.Inventory.recipe.base.RecipeSerializerBuilder;
import com.hbm.core.contents.fluid.HBMFluids;
import com.hbm.datagen.recipe.ISubRecipeProvider;
import com.hbm.datagen.recipe.ingredient.CountableIngredient;
import com.hbm.registries.ModItems;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

/**
 * 液化机配方（象征性示例：煤/褐煤 → 煤油）。
 * 后续可在此补充更多液化配方。
 */
public class LiquefactionRecipeProvider implements ISubRecipeProvider {
    @Override
    public void addRecipes(Consumer<FinishedRecipe> consumer) {
        RecipeSerializerBuilder.AutoRecipeSerializer<LiquefactionRecipe> serializer =
                (RecipeSerializerBuilder.AutoRecipeSerializer<LiquefactionRecipe>) ModRecipes.LIQUEFACTOR.serializer().get();

        getBuilder(serializer, "coal_to_coaloil").setValue(
                CountableIngredient.of(Items.COAL),
                new FluidStack(HBMFluids.COALOIL.source().get(), 100),
                100
        ).save(consumer);

        getBuilder(serializer, "lignite_to_coaloil").setValue(
                CountableIngredient.of(ModItems.LIGNITE.get()),
                new FluidStack(HBMFluids.COALOIL.source().get(), 50),
                100
        ).save(consumer);
    }

    private static RecipeSerializerBuilder.AutoRecipeBuilder getBuilder(RecipeSerializerBuilder.AutoRecipeSerializer<?> serializer, String name){
        return serializer.getBuilder(HBM.rl(name).withPrefix("liquefactor/"));
    }
}
