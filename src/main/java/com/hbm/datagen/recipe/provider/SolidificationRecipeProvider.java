package com.hbm.datagen.recipe.provider;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.Inventory.recipe.SolidificationRecipe;
import com.hbm.Inventory.recipe.base.RecipeSerializerBuilder;
import com.hbm.datagen.recipe.ISubRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.function.Consumer;

/**
 * 固化机配方（象征性示例：岩浆→黑曜石、水→冰）。
 * 后续可在此补充更多固化配方。
 */
public class SolidificationRecipeProvider implements ISubRecipeProvider {
    @Override
    public void addRecipes(Consumer<FinishedRecipe> consumer) {
        RecipeSerializerBuilder.AutoRecipeSerializer<SolidificationRecipe> serializer =
                (RecipeSerializerBuilder.AutoRecipeSerializer<SolidificationRecipe>) ModRecipes.SOLIDIFIER.serializer().get();

        getBuilder(serializer, "lava_to_obsidian").setValue(
                new FluidStack(Fluids.LAVA, 1000),
                List.of(new ItemStack(Items.OBSIDIAN)),
                100
        ).save(consumer);

        getBuilder(serializer, "water_to_ice").setValue(
                new FluidStack(Fluids.WATER, 1000),
                List.of(new ItemStack(Items.ICE)),
                100
        ).save(consumer);
    }

    private static RecipeSerializerBuilder.AutoRecipeBuilder getBuilder(RecipeSerializerBuilder.AutoRecipeSerializer<?> serializer, String name){
        return serializer.getBuilder(HBM.rl(name).withPrefix("solidifier/"));
    }
}
