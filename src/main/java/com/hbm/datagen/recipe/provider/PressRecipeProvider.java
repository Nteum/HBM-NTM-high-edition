package com.hbm.datagen.recipe.provider;

import com.hbm.datagen.recipe.ISubRecipeProvider;
import com.hbm.datagen.recipe.builder.RecipePressBuilder;
import com.hbm.item.tool.ItemStamp.*;
import com.hbm.registries.HBMMatters;
import com.hbm.registries.ModItems;
import com.hbm.registries.ModTags;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;

public class PressRecipeProvider implements ISubRecipeProvider {
    @Override
    public void addRecipes(Consumer<FinishedRecipe> consumer) {
        // 大板
        new RecipePressBuilder(StampType.FLAT, ModTags.Items.DUST_QUARTZ, Items.QUARTZ).save(consumer);
        new RecipePressBuilder(StampType.FLAT, Ingredient.of(ModTags.Items.DUST_LAPIS), new ItemStack(Items.BLUE_DYE, 4)).save(consumer);
        new RecipePressBuilder(StampType.FLAT, ModTags.Items.DUST_DIAMOND, Items.DIAMOND).save(consumer);
        new RecipePressBuilder(StampType.FLAT, ModTags.Items.DUST_EMERALD, Items.EMERALD).save(consumer);
        new RecipePressBuilder(StampType.FLAT, ModTags.Items.DUST_EMERALD, Items.EMERALD).save(consumer);
        new RecipePressBuilder(StampType.FLAT, ModTags.Items.BIOMASS, ModItems.BIOMASS_COMPRESSED.get()).save(consumer);
        new RecipePressBuilder(StampType.FLAT, ModTags.Items.COKE, ModItems.INGOT_GRAPHITE.get()).save(consumer);
//        new RecipePressBuilder(StampType.FLAT, ModItems.METEORITE_SWORD_REFORGED.get(), ModItems.METEORITE_SWORD_HARDENED.get()).save(consumer);
        new RecipePressBuilder(StampType.FLAT, ItemTags.LOGS, ModItems.BALL_RESIN.get()).save(consumer);
        new RecipePressBuilder(StampType.FLAT, ModTags.Items.DUST_COAL, ModItems.BRIQUETTE_COAL.get()).save(consumer);
        new RecipePressBuilder(StampType.FLAT, ModTags.Items.DUST_LIGNITE, ModItems.BRIQUETTE_LIGNITE.get()).save(consumer);
        new RecipePressBuilder(StampType.FLAT, ModTags.Items.SAWDUST, ModItems.BRIQUETTE_WOOD.get()).save(consumer);
        // 板子
        new RecipePressBuilder(StampType.PLATE, Items.IRON_INGOT,ModItems.PLATE_IRON.get()).save(consumer);
        new RecipePressBuilder(StampType.PLATE, Items.GOLD_INGOT,ModItems.PLATE_GOLD.get()).save(consumer);
        new RecipePressBuilder(StampType.PLATE, HBMMatters.TITANIUM.ingot(),ModItems.PLATE_TITANIUM.get()).save(consumer);
        new RecipePressBuilder(StampType.PLATE, ModTags.Items.INGOT_ALUMINIUM,ModItems.PLATE_ALUMINIUM.get()).save(consumer);
        new RecipePressBuilder(StampType.PLATE, ModTags.Items.INGOT_STEEL,ModItems.PLATE_STEEL.get()).save(consumer);
        new RecipePressBuilder(StampType.PLATE, ModTags.Items.INGOT_LEAD,ModItems.PLATE_LEAD.get()).save(consumer);
        new RecipePressBuilder(StampType.PLATE, Items.COPPER_INGOT,ModItems.PLATE_COPPER.get()).save(consumer);
        new RecipePressBuilder(StampType.PLATE, ModItems.INGOT_ADVANCED_ALLOY.get(),ModItems.PLATE_ADVANCED_ALLOY.get()).save(consumer);
        new RecipePressBuilder(StampType.PLATE, ModItems.INGOT_SCHRABIDIUM.get(),ModItems.PLATE_SCHRABIDIUM.get()).save(consumer);
        new RecipePressBuilder(StampType.PLATE, ModItems.INGOT_COMBINE_STEEL.get(),ModItems.PLATE_COMBINE_STEEL.get()).save(consumer);
        new RecipePressBuilder(StampType.PLATE, ModItems.INGOT_GUNMETAL.get(),ModItems.PLATE_GUNMETAL.get()).save(consumer);
        new RecipePressBuilder(StampType.PLATE, ModItems.INGOT_WEAPONSTEEL.get(), ModItems.PLATE_WEAPONSTEEL.get()).save(consumer);
        new RecipePressBuilder(StampType.PLATE, ModItems.INGOT_SATURNITE.get(), ModItems.PLATE_SATURNITE.get()).save(consumer);
        new RecipePressBuilder(StampType.PLATE, ModItems.INGOT_DURA_STEEL.get(),ModItems.PLATE_DURA_STEEL.get()).save(consumer);
        // 炸药
        new RecipePressBuilder(StampType.PLATE, Ingredient.of(ModItems.PLATE_GUNMETAL.get()), new ItemStack(ModItems.CASING_SMALL.get(), 4)).save(consumer);
        new RecipePressBuilder(StampType.PLATE, Ingredient.of(ModItems.PLATE_GUNMETAL.get()), new ItemStack(ModItems.CASING_LARGE.get(), 2)).save(consumer);
        new RecipePressBuilder(StampType.PLATE, Ingredient.of(ModItems.PLATE_WEAPONSTEEL.get()), new ItemStack(ModItems.CASING_SMALL_STEEL.get(), 4)).save(consumer);
        new RecipePressBuilder(StampType.PLATE, Ingredient.of(ModItems.PLATE_WEAPONSTEEL.get()), new ItemStack(ModItems.CASING_LARGE_STEEL.get(), 2)).save(consumer);
        // 金属线
        for (String mat : ModItems.WIRE_MAT) {
            if (ModItems.MAP_INGOT.containsKey(mat))
                new RecipePressBuilder(StampType.WIRE, Ingredient.of(ModItems.MAP_INGOT.get(mat).get()), new ItemStack(ModItems.WIRE_FINE.get(mat).get(), 8)).save(consumer);
        }

//        makeRecipe(StampType.CIRCUIT, new OreDictStack(SI.billet()),						DictFrame.fromOne(ModItems.circuit, EnumCircuitType.SILICON));
//        makeRecipe(StampType.CIRCUIT, new OreDictStack(GAAS.billet()),						DictFrame.fromOne(ModItems.circuit, EnumCircuitType.GAAS));
//
//        makeRecipe(StampType.PRINTING1, new ComparableStack(Items.paper), DictFrame.fromOne(ModItems.page_of_, EnumPages.PAGE1));
//        makeRecipe(StampType.PRINTING2, new ComparableStack(Items.paper), DictFrame.fromOne(ModItems.page_of_, EnumPages.PAGE2));
//        makeRecipe(StampType.PRINTING3, new ComparableStack(Items.paper), DictFrame.fromOne(ModItems.page_of_, EnumPages.PAGE3));
//        makeRecipe(StampType.PRINTING4, new ComparableStack(Items.paper), DictFrame.fromOne(ModItems.page_of_, EnumPages.PAGE4));
//        makeRecipe(StampType.PRINTING5, new ComparableStack(Items.paper), DictFrame.fromOne(ModItems.page_of_, EnumPages.PAGE5));
//        makeRecipe(StampType.PRINTING6, new ComparableStack(Items.paper), DictFrame.fromOne(ModItems.page_of_, EnumPages.PAGE6));
//        makeRecipe(StampType.PRINTING7, new ComparableStack(Items.paper), DictFrame.fromOne(ModItems.page_of_, EnumPages.PAGE7));
//        makeRecipe(StampType.PRINTING8, new ComparableStack(Items.paper), DictFrame.fromOne(ModItems.page_of_, EnumPages.PAGE8));
    }
}