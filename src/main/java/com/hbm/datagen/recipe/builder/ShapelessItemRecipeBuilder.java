package com.hbm.datagen.recipe.builder;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hbm.HBM;
import com.hbm.datagen.recipe.ingredient.CountableIngredient;
import com.hbm.Inventory.recipe.ModRecipes;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
/**
 * 主要用于装配机等【不需要液体】【不产生副产物】的机器的配方。
 * 和原版的ShapelessRecipe很像，但机器需要能量，所以额外加一个能量参数。
 * 虽然名字带energy，但这个属性可以兼用来表示 能量/热量/工作时间 等可以转换成整数的标准。
 * */
public class ShapelessItemRecipeBuilder implements RecipeBuilder {
    protected final Item result;
    protected final int count;
    /** 虽然名字是power，实际上可以用来表示 能量/热量/工作时间 等用整数表示的概念，反正它们一般都不会一起用 */
    private long number;
    private RecipeCategory category = RecipeCategory.MISC;
    private final List<CountableIngredient> ingredients = Lists.newArrayList();
    @Nullable
    private String group = "";
    protected ShapelessItemRecipeBuilder(RecipeCategory pCategory, ItemLike result, int count) {
        this.result = result.asItem();
        this.count = count;
    }
    public static ShapelessItemRecipeBuilder assembler(ItemLike pResult) {
        return new ShapelessItemRecipeBuilder(RecipeCategory.MISC, pResult, 1);
    }
    public static ShapelessItemRecipeBuilder assembler(ItemLike pResult, int pCount) {
        return new ShapelessItemRecipeBuilder(RecipeCategory.MISC, pResult, pCount);
    }
    public ShapelessItemRecipeBuilder num(long number){
        this.number = number;
        return this;
    }
    public ShapelessItemRecipeBuilder requires(TagKey<Item> pTag) {
        return this.requires(CountableIngredient.of(pTag));
    }
    public ShapelessItemRecipeBuilder requires(TagKey<Item> pTag, int count) {
        return this.requires(CountableIngredient.of(pTag,count));
    }
    public ShapelessItemRecipeBuilder requires(ItemLike pItem) {
        return this.requires(pItem, 1);
    }

    public ShapelessItemRecipeBuilder requires(ItemLike pItem, int pQuantity) {
        this.requires(CountableIngredient.of(pItem,pQuantity));

        return this;
    }

    public ShapelessItemRecipeBuilder requires(CountableIngredient pIngredient) {
        this.ingredients.add(pIngredient);
        return this;
    }

    /**
     * Adds an ingredient multiple times.
     */
//    public ShapelessEnergyRecipeBuilder requires(Ingredient pIngredient, int pQuantity) {
//        for(int i = 0; i < pQuantity; ++i) {
//            this.ingredients.add(pIngredient);
//        }
//        return this;
//    }
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
    public @NotNull Item getResult() {
        return result;
    }
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, String path) {
        pFinishedRecipeConsumer.accept(new Result(HBM.rl(path),result,count,group,category,ingredients,number));
    }
    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        pFinishedRecipeConsumer.accept(new Result(pRecipeId,result,count,group,category,ingredients,number));
    }
    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final Item result;
        private final int count;
        private final long number;
        private final String group;
        private final List<CountableIngredient> ingredients;

        public Result(ResourceLocation pId, Item pResult, int pCount, String pGroup, RecipeCategory pCategory, List<CountableIngredient> pIngredients, long number) {
            this.id = pId;
            this.result = pResult;
            this.count = pCount;
            this.group = pGroup;
            this.ingredients = pIngredients;
            this.number = number;
        }

        public void serializeRecipeData(JsonObject pJson) {
            if (!this.group.isEmpty()) {
                pJson.addProperty("group", this.group);
            }

            JsonArray jsonarray = new JsonArray();
            for(CountableIngredient ingredient : this.ingredients) {
                jsonarray.add(ingredient.toJson());
            }
            pJson.add("ingredients", jsonarray);

            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("item", BuiltInRegistries.ITEM.getKey(this.result).toString());
            if (this.count > 1) {
                jsonobject.addProperty("count", this.count);
            }
            pJson.add("result", jsonobject);

            pJson.addProperty("number",this.number);
        }

        public RecipeSerializer<?> getType() {
            return ModRecipes.ASSEMBLER.serializer().get();
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
