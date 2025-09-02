package com.hbm.datagen.recipe.provider;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.datagen.recipe.ISubRecipeProvider;
import com.hbm.datagen.recipe.builder.ItemFluidRecipeBuilder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

public class ChemplantRecipeProvider implements ISubRecipeProvider {
    public static final String ROOT = HBMKey.CHEMPLANT + "/";
    @Override
    public void addRecipes(Consumer<FinishedRecipe> consumer) {
        ItemFluidRecipeBuilder.of(HBM.rl(ROOT+"hydrogen")).duration(20).requireItems(new ItemStack(Items.COAL,1)).requireFluids(new FluidStack(Fluids.WATER, 8000))
                .resultFluids(new FluidStack(ModFluids.HYDROGEN.source().get(), 500)).save(consumer);
    }
}
