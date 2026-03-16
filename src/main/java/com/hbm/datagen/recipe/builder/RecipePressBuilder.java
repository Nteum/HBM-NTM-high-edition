package com.hbm.datagen.recipe.builder;

import com.google.gson.JsonObject;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.item.tool.ItemStamp;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class RecipePressBuilder implements FinishedRecipe {
    private static int counter = 0;
    private ResourceLocation id;
    Ingredient input;
    ItemStamp.StampType stamp;
    ItemStack result;
    public RecipePressBuilder(ItemStamp.StampType stamp, TagKey<Item> input, Item result){
        this(stamp, Ingredient.of(input), result.getDefaultInstance());
    }
    public RecipePressBuilder(ItemStamp.StampType stamp, Ingredient input, ItemStack result){
        this(BuiltInRegistries.ITEM.getKey(result.getItem()).withPrefix("press/").withSuffix("_" + counter), result, input, stamp);
    }
    public RecipePressBuilder(ItemStamp.StampType stamp, Item input, Item result){
        this(BuiltInRegistries.ITEM.getKey(result).withPrefix("press/").withSuffix("_" + counter), result.getDefaultInstance(), Ingredient.of(input), stamp);
    }
    public RecipePressBuilder(ResourceLocation id, ItemStack result, Ingredient input, ItemStamp.StampType stamp){
        this.result = result;
        this.input = input;
        this.stamp = stamp;
        this.id = id;
    }
    @Override
    public void serializeRecipeData(JsonObject pJson) {
        pJson.add("input", input.toJson());
        pJson.addProperty("stamp", stamp.name());
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("item", BuiltInRegistries.ITEM.getKey(this.result.getItem()).toString());
        if (this.result.getCount() > 1) jsonobject.addProperty("count", this.result.getCount());
        pJson.add("result", jsonobject);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getType() {
        return ModRecipes.PRESS.serializer().get();
    }

    @Nullable
    @Override
    public JsonObject serializeAdvancement() {
        return null;
    }

    @Nullable
    @Override
    public ResourceLocation getAdvancementId() {
        return null;
    }
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        pFinishedRecipeConsumer.accept(this);
        counter ++;
    }
}
