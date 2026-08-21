package com.hbm.datagen.recipe.provider;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.Inventory.recipe.ArcWelderRecipe;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.Inventory.recipe.base.RecipeSerializerBuilder;
import com.hbm.datagen.recipe.ISubRecipeProvider;
import com.hbm.datagen.recipe.ingredient.CountableIngredient;
import com.hbm.registries.ModItems;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.function.Consumer;

/**
 * 电弧焊配方（象征性示例：2 铁板 → 铁板焊缝）。
 */
public class ArcWelderRecipeProvider implements ISubRecipeProvider {
    @Override
    public void addRecipes(Consumer<FinishedRecipe> consumer) {
        RecipeSerializerBuilder.AutoRecipeSerializer<ArcWelderRecipe> serializer =
                (RecipeSerializerBuilder.AutoRecipeSerializer<ArcWelderRecipe>) ModRecipes.ARC_WELDER.serializer().get();

        serializer.getBuilder(HBM.rl("plate_welded_iron").withPrefix("arc_welder/")).setValue(
                List.of(CountableIngredient.of(ModItems.PLATE_IRON.get(), 2)),
                null,
                List.of(new ItemStack(ModItems.PLATE_IRON.get(), 1)),
                100,
                100
        ).save(consumer);

        serializer.getBuilder(HBM.rl("plate_welded_steel").withPrefix("arc_welder/")).setValue(
                List.of(CountableIngredient.of(ModItems.PLATE_STEEL.get(), 2)),
                null,
                List.of(new ItemStack(ModItems.PLATE_STEEL.get(), 1)),
                100,
                500
        ).save(consumer);
    }
}
