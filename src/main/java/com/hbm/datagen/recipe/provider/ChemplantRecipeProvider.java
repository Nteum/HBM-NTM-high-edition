package com.hbm.datagen.recipe.provider;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.datagen.recipe.ISubRecipeProvider;
import com.hbm.datagen.recipe.builder.ItemFluidRecipeBuilder;
import com.hbm.registries.ModItems;
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
        ItemFluidRecipeBuilder.of(HBM.rl(ROOT+"oxygen")).duration(20).requireFluids(new FluidStack(Fluids.WATER, 8000))
                .resultFluids(new FluidStack(ModFluids.OXYGEN.source().get(), 400)).save(consumer);
        ItemFluidRecipeBuilder.of(HBM.rl(ROOT+"deuterium")).duration(60)
                .requireItems(new ItemStack(ModItems.DEUTERIUM_FILTER.get()))
                .requireFluids(new FluidStack(ModFluids.HYDROGEN.source().get(), 4000))
                .resultFluids(new FluidStack(ModFluids.DEUTERIUM.source().get(), 250)).save(consumer);
        ItemFluidRecipeBuilder.of(HBM.rl(ROOT+"tritium")).duration(80)
                .requireItems(new ItemStack(ModItems.TRITIUM_DEUTERIUM_CAKE.get()))
                .requireFluids(new FluidStack(ModFluids.DEUTERIUM.source().get(), 2000))
                .resultFluids(new FluidStack(ModFluids.TRITIUM.source().get(), 250)).save(consumer);
        ItemFluidRecipeBuilder.of(HBM.rl(ROOT+"helium3")).duration(100)
                .requireItems(new ItemStack(Items.END_STONE, 2))
                .requireFluids(new FluidStack(Fluids.WATER, 1000))
                .resultFluids(new FluidStack(ModFluids.HELIUM3.source().get(), 125)).save(consumer);
        ItemFluidRecipeBuilder.of(HBM.rl(ROOT+"helium4")).duration(60)
                .requireFluids(new FluidStack(ModFluids.HELIUM3.source().get(), 500), new FluidStack(ModFluids.HYDROGEN.source().get(), 500))
                .resultFluids(new FluidStack(ModFluids.HELIUM4.source().get(), 500)).save(consumer);
        ItemFluidRecipeBuilder.of(HBM.rl(ROOT+"chlorine")).duration(40)
                .requireItems(new ItemStack(Items.PRISMARINE_CRYSTALS, 4))
                .requireFluids(new FluidStack(Fluids.WATER, 4000))
                .resultFluids(new FluidStack(ModFluids.CHLORINE.source().get(), 250)).save(consumer);
    }
}
