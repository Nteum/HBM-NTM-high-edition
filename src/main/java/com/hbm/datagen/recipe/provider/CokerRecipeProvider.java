package com.hbm.datagen.recipe.provider;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.Inventory.recipe.CokerRecipe;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.Inventory.recipe.base.RecipeSerializerBuilder;
import com.hbm.core.contents.fluid.HBMFluids;
import com.hbm.datagen.recipe.ISubRecipeProvider;
import com.hbm.registries.ModItems;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.function.Consumer;

/**
 * 焦化配方（象征性示例：重油 → 石油焦 + 油焦）。
 */
public class CokerRecipeProvider implements ISubRecipeProvider {
    @Override
    public void addRecipes(Consumer<FinishedRecipe> consumer) {
        RecipeSerializerBuilder.AutoRecipeSerializer<CokerRecipe> serializer =
                (RecipeSerializerBuilder.AutoRecipeSerializer<CokerRecipe>) ModRecipes.COKER.serializer().get();

        getBuilder(serializer, "heavyoil_to_coker").setValue(
                new FluidStack(HBMFluids.HEAVYOIL.source().get(), 1000),
                List.of(new ItemStack(ModItems.COKE_PETROLEUM.get(), 1)),
                new FluidStack(HBMFluids.OIL_COKER.source().get(), 100),
                100
        ).save(consumer);
    }

    private static RecipeSerializerBuilder.AutoRecipeBuilder getBuilder(RecipeSerializerBuilder.AutoRecipeSerializer<?> serializer, String name){
        return serializer.getBuilder(HBM.rl(name).withPrefix("coker/"));
    }
}
