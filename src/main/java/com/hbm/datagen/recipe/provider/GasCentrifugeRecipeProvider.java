package com.hbm.datagen.recipe.provider;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.Inventory.recipe.GasCentrifugeRecipe;
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
 * 气体离心机配方（象征性：UF6 → 铀/氟化物品）。
 */
public class GasCentrifugeRecipeProvider implements ISubRecipeProvider {
    @Override
    public void addRecipes(Consumer<FinishedRecipe> consumer) {
        RecipeSerializerBuilder.AutoRecipeSerializer<GasCentrifugeRecipe> serializer =
                (RecipeSerializerBuilder.AutoRecipeSerializer<GasCentrifugeRecipe>) ModRecipes.GAS_CENTRIFUGE.serializer().get();

        serializer.getBuilder(HBM.rl("uf6_to_nuggets").withPrefix("gas_centrifuge/")).setValue(
                new FluidStack(HBMFluids.UF6.source().get(), 1200),
                List.of(
                        new ItemStack(ModItems.NUGGET_U238.get(), 11),
                        new ItemStack(ModItems.NUGGET_U235.get(), 1),
                        new ItemStack(ModItems.FLUORITE.get(), 4)
                ),
                150
        ).save(consumer);
    }
}
