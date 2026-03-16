package com.hbm.Inventory.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.hbm.HBMKey;
import com.hbm.datagen.recipe.ingredient.CountableIngredient;
import com.hbm.datagen.recipe.ingredient.FluidStackIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeHelper {
    // 从json中读取物品，变成CountableIngredient列表
    public static NonNullList<CountableIngredient> itemsFromJson(JsonArray pIngredientArray) {
        NonNullList<CountableIngredient> nonnulllist = NonNullList.create();
        for(int i = 0; i < pIngredientArray.size(); ++i) {
            if (pIngredientArray.get(i).isJsonObject()){
                CountableIngredient ingredient = CountableIngredient.Serializer.INSTANCE.parse(pIngredientArray.get(i).getAsJsonObject());
                if (!ingredient.isEmpty()){
                    nonnulllist.add(ingredient);
                }else {
                    throw new JsonSyntaxException("Parse wrong : Ingredient is empty.");
                }
            }else {
                throw new JsonSyntaxException("Parse wrong : CountableIngredient must be read from JsonObject.");
            }
        }
        return nonnulllist;
    }
    // 从json中读取流体，
    public static NonNullList<FluidStackIngredient> fluidsFromJson(JsonArray pIngredientArray) {
        NonNullList<FluidStackIngredient> nonnulllist = NonNullList.create();
        for(int i = 0; i < pIngredientArray.size(); ++i) {
            if (pIngredientArray.get(i).isJsonObject()){
                FluidStackIngredient ingredient = FluidStackIngredient.fromJson(pIngredientArray.get(i).getAsJsonObject());
                if (!ingredient.isEmpty()){
                    nonnulllist.add(ingredient);
                }else {
                    throw new JsonSyntaxException("Parse wrong : Ingredient is empty.");
                }
            }else {
                throw new JsonSyntaxException("Parse wrong : FluidStackIngredient must be read from JsonObject.");
            }
        }
        return nonnulllist;
    }
    public static List<ItemStack> itemListFromJson(JsonArray pIngredientArray) {
        List<ItemStack> result = new ArrayList<>();
        for(int i = 0; i < pIngredientArray.size(); ++i) {
            if (pIngredientArray.get(i).isJsonObject()){
                JsonObject jsonObject = pIngredientArray.get(i).getAsJsonObject();
                String s = GsonHelper.getAsString(jsonObject, HBMKey.ITEM);
                Item item = BuiltInRegistries.ITEM.getOptional(ResourceLocation.tryParse(s)).orElseThrow(() -> {
                    return new JsonSyntaxException("Unknown item '" + s + "'");
                });
                int count = GsonHelper.getAsInt(jsonObject, HBMKey.COUNT, 1);

                if (item != null){
                    result.add(new ItemStack(item, count));
                }else {
                    throw new JsonSyntaxException("Parse wrong : Ingredient is empty.");
                }
            }else {
                throw new JsonSyntaxException("Parse wrong : CountableIngredient must be read from JsonObject.");
            }
        }
        return result;
    }
    public static List<FluidStack> fluidListFromJson(JsonArray pIngredientArray) {
        List<FluidStack> result = new ArrayList<>();
        for(int i = 0; i < pIngredientArray.size(); ++i) {
            if (pIngredientArray.get(i).isJsonObject()){
                JsonObject jsonObject = pIngredientArray.get(i).getAsJsonObject();
                String s = GsonHelper.getAsString(jsonObject, HBMKey.FLUIDS);
                Fluid fluid = BuiltInRegistries.FLUID.getOptional(ResourceLocation.tryParse(s)).orElseThrow(() -> {
                    return new JsonSyntaxException("Unknown item '" + s + "'");
                });
                int volume = GsonHelper.getAsInt(jsonObject, HBMKey.VOLUME, 1000);

                if (fluid != null){
                    result.add(new FluidStack(fluid, volume));
                }else {
                    throw new JsonSyntaxException("Parse wrong : Ingredient is empty.");
                }
            }else {
                throw new JsonSyntaxException("Parse wrong : CountableIngredient must be read from JsonObject.");
            }
        }
        return result;
    }
    // 从jsonobject中读取单独的流体类型
    public static Fluid fluidFromJson(JsonObject json){
        String s = GsonHelper.getAsString(json, HBMKey.FLUIDS);
        Fluid fluid = BuiltInRegistries.FLUID.getOptional(ResourceLocation.tryParse(s)).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown fluid '" + s + "'");
        });
        if (fluid == Fluids.EMPTY) {
            throw new JsonSyntaxException("Empty ingredient not allowed here");
        } else {
            return fluid;
        }
    }
    // 展示配方的内容
    public static List<Component> genTooltip(Recipe recipe, RegistryAccess reg){
        List<Component> result = new ArrayList<>();
        if (recipe.getType() == ModRecipes.ASSEMBLER.type().get()){
            AssemblerRecipe assemblerRecipe = (AssemblerRecipe) recipe;
            ItemStack resultItem = assemblerRecipe.getResultItem(reg);
            result.add(Component.translatable(resultItem.getDescriptionId()).append(" ×" + resultItem.getCount()));
            for (CountableIngredient ingredient : assemblerRecipe.ingredients) {
                result.add(Component.literal(" - ").append(genIngredientTooltip(ingredient)));
            }
            result.add(Component.literal("Processing Time : " + assemblerRecipe.processingTime));
        }
        return result;
    }

    private static Component genItemStackTooltip(ItemStack itemStack){
        return Component.translatable(itemStack.getDescriptionId()).append(" ×" + itemStack.getCount());
    }

    private static Component genIngredientTooltip(Ingredient ingredient){
        ItemStack itemStack = ingredient.getItems()[0];
        MutableComponent result = Component.translatable(itemStack.getDescriptionId());
        if (ingredient instanceof CountableIngredient countableIngredient){
            result.append(" ×" + countableIngredient.value.count);
        }
        return result;
    }
}
