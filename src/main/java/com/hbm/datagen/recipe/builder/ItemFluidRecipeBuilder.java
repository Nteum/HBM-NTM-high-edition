package com.hbm.datagen.recipe.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hbm.HBM;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.datagen.recipe.ingredient.CountableIngredient;
import com.hbm.datagen.recipe.ingredient.FluidStackIngredient;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * 物品 - 流体混合配方
 * 1. 所需物品
 * 2. 所需流体
 * 3. 一个数字，可以记录能量或热量
 * 4. 产生物品
 * 5. 产生流体
 * 直接实现FinishedRecipe，干嘛用recipeBuilder
 * */
public class ItemFluidRecipeBuilder implements FinishedRecipe {
    private ResourceLocation id = null;
    protected List<ItemStack> resultItems = new ArrayList<>();
    protected List<FluidStack> resultFluids = new ArrayList<>();
    protected List<CountableIngredient> rawItems = new ArrayList<>();
    protected List<FluidStackIngredient> rawFluids = new ArrayList<>();
    // 怎么说呢，沟槽的bob，他设置的化工厂配方需要两个数：（时间，功率）
    long number = 0;
    int number2 = 0;
    private RecipeCategory category = RecipeCategory.MISC;
    private String group = "";
    private static int counter = 0;
    public static ItemFluidRecipeBuilder of(ResourceLocation id){
        ItemFluidRecipeBuilder builder = new ItemFluidRecipeBuilder();
        builder.id = id;
        return builder;
    }
    public static ItemFluidRecipeBuilder of(RecipeCategory category){
        ItemFluidRecipeBuilder builder = new ItemFluidRecipeBuilder();
        builder.category = category;
        return builder;
    }

    public ItemFluidRecipeBuilder resultItems(ItemStack ... itemStacks){
        this.resultItems = Arrays.stream(itemStacks).toList();
        return this;
    }
    public ItemFluidRecipeBuilder resultFluids(FluidStack ... fluidStacks){
        this.resultFluids = Arrays.stream(fluidStacks).toList();
        return this;
    }
    public ItemFluidRecipeBuilder requireItems(ItemStack ... itemStacks){
        Arrays.stream(itemStacks).forEach(itemStack -> this.rawItems.add(CountableIngredient.of(itemStack)));
        return this;
    }
    public ItemFluidRecipeBuilder requireItemTag(TagKey<Item> tag, int count){
        this.rawItems.add(CountableIngredient.of(tag, count));
        return this;
    }
    public ItemFluidRecipeBuilder requireFluids(FluidStack ... fluidStacks){
        Arrays.stream(fluidStacks).forEach(fluidStack -> this.rawFluids.add(FluidStackIngredient.of(fluidStack)));
        return this;
    }
    public ItemFluidRecipeBuilder requireFluidTag(TagKey<Fluid> tag, int amount){
        this.rawFluids.add(FluidStackIngredient.of(tag, amount));
        return this;
    }
    public ItemFluidRecipeBuilder num(long number){
        this.number = number;
        return this;
    }
    public ItemFluidRecipeBuilder num2(int number){
        this.number2 = number;
        return this;
    }
    public ItemFluidRecipeBuilder group(String group){
        this.group = group;
        return this;
    }
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        if (id == null)
            this.id = HBM.rl("chemplant_recipe_"+counter);
        pFinishedRecipeConsumer.accept(this);
        counter ++;
    }
    @Override
    public void serializeRecipeData(JsonObject pJson) {
        if (!this.group.isEmpty()) {
            pJson.addProperty("group", this.group);
        }

        JsonArray jsonarray = new JsonArray();
        for(CountableIngredient ingredient : this.rawItems) {
            jsonarray.add(ingredient.toJson());
        }
        pJson.add("inputItems", jsonarray);

        JsonArray jsonarray1 = new JsonArray();
        for (FluidStackIngredient rawFluid : this.rawFluids) {
            jsonarray1.add(rawFluid.toJson());
        }
        pJson.add("inputFluids", jsonarray1);

        JsonObject jsonobject2;
        JsonArray jsonArray2 = new JsonArray();
        for (ItemStack resultItem : this.resultItems) {
            jsonobject2 = new JsonObject();
            jsonobject2.addProperty("item", BuiltInRegistries.ITEM.getKey(resultItem.getItem()).toString());
            if (resultItem.getCount() > 1) {
                jsonobject2.addProperty("count", resultItem.getCount());
            }
            jsonArray2.add(jsonobject2.deepCopy());
        }
        pJson.add("resultItems", jsonArray2.deepCopy());

        for (FluidStack fluidStack : this.resultFluids) {
            jsonobject2 = new JsonObject();
            jsonobject2.addProperty("fluids", BuiltInRegistries.FLUID.getKey(fluidStack.getFluid()).toString());
            jsonobject2.addProperty("amount", fluidStack.getAmount());
            jsonArray2.add(jsonobject2.deepCopy());
        }
        pJson.add("resultFluids", jsonArray2);

        pJson.addProperty("number",this.number);
        pJson.addProperty("number2",this.number2);
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getType() {
        return ModRecipes.CHEMPLANT.serializer().get();
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
