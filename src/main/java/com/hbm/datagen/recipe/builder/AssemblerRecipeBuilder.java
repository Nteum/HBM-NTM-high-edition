package com.hbm.datagen.recipe.builder;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hbm.HBM;
import com.hbm.recipe.ModRecipes;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class AssemblerRecipeBuilder implements RecipeBuilder {
    protected final Item result;
    protected final int count;
    /** 虽然名字是power，实际上可以用来表示 能量/热量/工作时间 等用整数表示的概念，反正它们一般都不会一起用 */
    private long powerConsume;
    private RecipeCategory category = RecipeCategory.MISC;
    private final List<Ingredient> ingredients = Lists.newArrayList();
    @Nullable
    private String group = "";
    protected AssemblerRecipeBuilder(RecipeCategory pCategory, ItemLike result, int count) {
//        super(RegistryUtils.getName(serializer));
        this.result = result.asItem();
        this.count = count;
    }
    /**
     * Creates a new builder for a shapeless recipe.
     */
    public static AssemblerRecipeBuilder assembler(ItemLike pResult) {
        return new AssemblerRecipeBuilder(RecipeCategory.MISC, pResult, 1);
    }

    /**
     * Creates a new builder for a shapeless recipe.
     */
    public static AssemblerRecipeBuilder assembler(ItemLike pResult, int pCount) {
        return new AssemblerRecipeBuilder(RecipeCategory.MISC, pResult, pCount);
    }
    public AssemblerRecipeBuilder power(long power){
        this.powerConsume = power;
        return this;
    }
    /**
     * Adds an ingredient that can be any item in the given tag.
     */
    public AssemblerRecipeBuilder requires(TagKey<Item> pTag) {
        return this.requires(Ingredient.of(pTag));
    }

    /**
     * Adds an ingredient of the given item.
     */
    public AssemblerRecipeBuilder requires(ItemLike pItem) {
        return this.requires(pItem, 1);
    }

    /**
     * Adds the given ingredient multiple times.
     */
    public AssemblerRecipeBuilder requires(ItemLike pItem, int pQuantity) {
        for(int i = 0; i < pQuantity; ++i) {
            this.requires(Ingredient.of(pItem));
        }

        return this;
    }

    /**
     * Adds an ingredient.
     */
    public AssemblerRecipeBuilder requires(Ingredient pIngredient) {
        return this.requires(pIngredient, 1);
    }

    /**
     * Adds an ingredient multiple times.
     */
    public AssemblerRecipeBuilder requires(Ingredient pIngredient, int pQuantity) {
        for(int i = 0; i < pQuantity; ++i) {
            this.ingredients.add(pIngredient);
        }

        return this;
    }
    @Override
    public RecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String pGroupName) {
        group = pGroupName;
        return this;
    }

    @Override
    public Item getResult() {
        return result;
    }
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, String path) {
        pFinishedRecipeConsumer.accept(new Result(HBM.rl(path),result,count,group,category,ingredients,powerConsume));
    }
    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        pFinishedRecipeConsumer.accept(new Result(pRecipeId,result,count,group,category,ingredients,powerConsume));
    }
    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final Item result;
        private final int count;
        private final long powerConsume;
        private final String group;
        private final List<Ingredient> ingredients;

        public Result(ResourceLocation pId, Item pResult, int pCount, String pGroup, RecipeCategory pCategory, List<Ingredient> pIngredients, long power) {
            this.id = pId;
            this.result = pResult;
            this.count = pCount;
            this.group = pGroup;
            this.ingredients = pIngredients;
            this.powerConsume = power;
        }

        public void serializeRecipeData(JsonObject pJson) {
            if (!this.group.isEmpty()) {
                pJson.addProperty("group", this.group);
            }

            JsonArray jsonarray = new JsonArray();
            for(Ingredient ingredient : this.ingredients) {
                jsonarray.add(ingredient.toJson());
            }
            pJson.add("ingredients", jsonarray);

            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("item", BuiltInRegistries.ITEM.getKey(this.result).toString());
            if (this.count > 1) {
                jsonobject.addProperty("count", this.count);
            }
            pJson.add("result", jsonobject);

            pJson.addProperty("power",this.powerConsume);
        }

        public RecipeSerializer<?> getType() {
            return ModRecipes.ASSEMBLER_SERIALIZER.get();
        }

        /**
         * Gets the ID for the recipe.
         */
        public ResourceLocation getId() {
            return this.id;
        }

        /**
         * Gets the JSON for the advancement that unlocks this recipe. Null if there is no advancement.
         */
        @javax.annotation.Nullable
        public JsonObject serializeAdvancement() {
            return null;
        }

        /**
         * Gets the ID for the advancement associated with this recipe. Should not be null if @link #getAdvancementJson
         * is non-null.
         */
        @javax.annotation.Nullable
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }
}
