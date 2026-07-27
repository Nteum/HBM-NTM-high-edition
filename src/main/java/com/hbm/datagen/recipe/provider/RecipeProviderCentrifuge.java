package com.hbm.datagen.recipe.provider;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.Inventory.recipe.RecipeCentrifuge;
import com.hbm.Inventory.recipe.base.RecipeSerializerBuilder;
import com.hbm.datagen.recipe.ISubRecipeProvider;
import com.hbm.datagen.recipe.ingredient.CountableIngredient;
import com.hbm.item.ItemEnums;
import com.hbm.registries.HBMMatters;
import com.hbm.registries.ModItems;
import com.hbm.registries.ModTags;
import com.hbm.registries.RegistryHelper;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RecipeProviderCentrifuge implements ISubRecipeProvider {
    @Override
    public void addRecipes(Consumer<FinishedRecipe> consumer) {
        RecipeSerializerBuilder.AutoRecipeSerializer<RecipeCentrifuge> serializer = (RecipeSerializerBuilder.AutoRecipeSerializer<RecipeCentrifuge>) ModRecipes.CENTRIFUGE.serializer().get();

        getBuilder(serializer, ModItems.CHUNK_ORE.get(ItemEnums.EnumChunkType.RARE)).setValue(
                CountableIngredient.of(ModItems.CHUNK_ORE.get(ItemEnums.EnumChunkType.RARE).get()),
                List.of(new ItemStack(ModItems.POWDER_COBALT_TINY.get(), 2),
                        new ItemStack(ModItems.POWDER_BORON_TINY.get(), 2),
                        new ItemStack(ModItems.POWDER_NIOBIUM_TINY.get(), 2),
                        new ItemStack(ModItems.NUGGET_ZIRCONIUM.get(), 3))
        ).save(consumer);

        getBuilder(serializer, Blocks.COAL_ORE::asItem).setValue(
                CountableIngredient.of(ItemTags.COAL_ORES),
                List.of(new ItemStack(ModItems.POWDER_COAL.get(), 2),
                        new ItemStack(ModItems.POWDER_COAL.get(), 2),
                        new ItemStack(ModItems.POWDER_COAL.get(), 2),
                        new ItemStack(Blocks.GRAVEL))
        ).save(consumer);

        getBuilder(serializer, ModItems.LIGNITE).setValue(
                CountableIngredient.of(ModItems.LIGNITE.get()),
                List.of(new ItemStack(ModItems.POWDER_LIGNITE.get(), 2),
                        new ItemStack(ModItems.POWDER_LIGNITE.get(), 2),
                        new ItemStack(ModItems.POWDER_LIGNITE.get(), 2),
                        new ItemStack(Blocks.GRAVEL))
        ).save(consumer);

        getBuilder(serializer, Blocks.IRON_ORE::asItem).setValue(
                CountableIngredient.of(ItemTags.IRON_ORES),
                List.of(new ItemStack(ModItems.POWDER_IRON.get(), 2),
                        new ItemStack(ModItems.POWDER_IRON.get(), 2),
                        new ItemStack(ModItems.POWDER_IRON.get(), 2),
                        new ItemStack(Blocks.GRAVEL))
        ).save(consumer);
    }

    private static RecipeSerializerBuilder.AutoRecipeBuilder getBuilder(RecipeSerializerBuilder.AutoRecipeSerializer<?> serializer, Supplier<Item> itemSupplier){
        return serializer.getBuilder(HBM.rl(RegistryHelper.itemRL(itemSupplier.get()).getPath()).withPrefix("centrifuge/"));
    }
}
