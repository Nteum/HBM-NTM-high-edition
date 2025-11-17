package com.hbm.datagen.recipe.builder;

import com.google.gson.JsonObject;
import com.hbm.HBM;
import com.hbm.datagen.recipe.ingredient.CountableIngredient;
import com.hbm.Inventory.recipe.ModRecipes;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Simple builder for one-input machine recipes (e.g., shredder).
 */
public class SingleItemRecipeBuilder implements RecipeBuilder {

    private final Item result;
    private final int count;
    private final RecipeSerializer<?> serializer;
    private CountableIngredient ingredient;
    private int duration = 60;

    protected SingleItemRecipeBuilder(ItemLike result, int count, RecipeSerializer<?> serializer) {
        this.result = result.asItem();
        this.count = count;
        this.serializer = serializer;
    }

    public static SingleItemRecipeBuilder shredder(ItemLike result) {
        return new SingleItemRecipeBuilder(result, 1, ModRecipes.SHREDDER.serializer().get());
    }

    public static SingleItemRecipeBuilder shredder(ItemLike result, int count) {
        return new SingleItemRecipeBuilder(result, count, ModRecipes.SHREDDER.serializer().get());
    }

    public SingleItemRecipeBuilder requires(ItemLike item) {
        return requires(item, 1);
    }

    public SingleItemRecipeBuilder requires(ItemLike item, int count) {
        return requires(CountableIngredient.of(item, count));
    }

    public SingleItemRecipeBuilder requires(TagKey<Item> tag) {
        return requires(tag, 1);
    }

    public SingleItemRecipeBuilder requires(TagKey<Item> tag, int count) {
        return requires(CountableIngredient.of(tag, count));
    }

    public SingleItemRecipeBuilder requires(CountableIngredient ingredient) {
        this.ingredient = ingredient;
        return this;
    }

    public SingleItemRecipeBuilder duration(int ticks) {
        this.duration = ticks;
        return this;
    }

    @Override
    public RecipeBuilder unlockedBy(String name, CriterionTriggerInstance trigger) {
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String groupName) {
        return this;
    }

    @Override
    public @NotNull Item getResult() {
        return result;
    }

    public void save(Consumer<FinishedRecipe> consumer, String path) {
        save(consumer, new ResourceLocation(HBM.MODID, path));
    }

    @Override
    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        if (ingredient == null) {
            throw new IllegalStateException("Missing ingredient for recipe " + id);
        }
        consumer.accept(new Result(id, ingredient, result, count, duration, serializer));
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final CountableIngredient ingredient;
        private final Item result;
        private final int count;
        private final int duration;
        private final RecipeSerializer<?> serializer;

        private Result(ResourceLocation id, CountableIngredient ingredient, Item result, int count, int duration, RecipeSerializer<?> serializer) {
            this.id = id;
            this.ingredient = ingredient;
            this.result = result;
            this.count = count;
            this.duration = duration;
            this.serializer = serializer;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("ingredient", ingredient.toJson());
            JsonObject resultObj = new JsonObject();
            resultObj.addProperty("item", BuiltInRegistries.ITEM.getKey(result).toString());
            if (count > 1) {
                resultObj.addProperty("count", count);
            }
            json.add("result", resultObj);
            json.addProperty("duration", duration);
        }

        @Override
        public RecipeSerializer<?> getType() {
            return serializer;
        }

        @Override
        public ResourceLocation getId() {
            return id;
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
    }
}
