package com.hbm.datagen.recipe.provider;

import com.hbm.datagen.recipe.ISubRecipeProvider;
import com.hbm.datagen.recipe.builder.SingleItemRecipeBuilder;
import com.hbm.registries.ModItems;
;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class ShredderRecipeProvider implements ISubRecipeProvider {
    @Override
    public void addRecipes(Consumer<FinishedRecipe> consumer) {
        String base = "shredder/";

        SingleItemRecipeBuilder.shredder(ModItems.POWDER_COAL.get(), 2)
                .requires(Items.COAL)
                .duration(40)
                .save(consumer, base + "coal_to_powder");

        SingleItemRecipeBuilder.shredder(ModItems.POWDER_IRON.get())
                .requires(Tags.Items.INGOTS_IRON)
                .duration(60)
                .save(consumer, base + "iron_ingot");

        SingleItemRecipeBuilder.shredder(ModItems.POWDER_GOLD.get())
                .requires(Tags.Items.INGOTS_GOLD)
                .duration(60)
                .save(consumer, base + "gold_ingot");

        SingleItemRecipeBuilder.shredder(ModItems.POWDER_COPPER.get())
                .requires(Tags.Items.INGOTS_COPPER)
                .duration(60)
                .save(consumer, base + "copper_ingot");
    }
}
