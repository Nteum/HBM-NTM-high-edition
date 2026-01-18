package com.hbm.datagen.recipe.provider;

import com.hbm.datagen.recipe.ISubRecipeProvider;
import com.hbm.datagen.recipe.builder.ShapelessItemRecipeBuilder;
import com.hbm.registries.ModItems;
;
import com.hbm.registries.ModTags;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class AssemblerRecipeProvider implements ISubRecipeProvider {
    @Override
    public void addRecipes(Consumer<FinishedRecipe> consumer) {
        String basePath = "assembler/";
        ShapelessItemRecipeBuilder.assembler(ModItems.PLATE_IRON.get(),2).requires(Items.IRON_INGOT,3).num(30).save(consumer,basePath+"plate_iron");
        ShapelessItemRecipeBuilder.assembler(ModItems.BATTERY_CREATIVE.get(),1).requires(ModTags.Items.BATTERY,3).num(30).save(consumer,basePath+"battery_creative");
    }
}
