package com.hbm.Inventory.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.datagen.recipe.ingredient.CountableIngredient;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

/**
 * Single-input shredder recipe turning one item (optionally using tags) into an output stack.
 */
public class ShredderRecipe implements Recipe<Container> {

    private final ResourceLocation id;
    private final CountableIngredient ingredient;
    private final ItemStack result;
    private final int processTime;

    public ShredderRecipe(ResourceLocation id, CountableIngredient ingredient, ItemStack result, int processTime) {
        this.id = id;
        this.ingredient = ingredient;
        this.result = result;
        this.processTime = processTime;
    }

    public CountableIngredient getIngredient() {
        return ingredient;
    }

    public int getProcessTime() {
        return processTime;
    }

    /**
     * Returns true when the supplied stack matches this recipe's ingredient.
     */
    public boolean matches(ItemStack stack) {
        return !stack.isEmpty() && ingredient.test(stack) && stack.getCount() >= ingredient.value.count;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return matches(container.getItem(0));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return result;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.SHREDDER.type().get();
    }

    public ItemStack getOutput() {
        return result.copy();
    }

    public int getIngredientCount() {
        return ingredient.value.count;
    }

    public static class Serializer implements RecipeSerializer<ShredderRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public ShredderRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            if (!json.has(HBMKey.ITEM) && !json.has(HBMKey.TAG) && !json.has("ingredient")) {
                throw new JsonParseException("Missing ingredient for shredder recipe '" + recipeId + "'");
            }
            CountableIngredient ingredient;
            if (json.has("ingredient")) {
                ingredient = CountableIngredient.Serializer.INSTANCE.parse(GsonHelper.getAsJsonObject(json, "ingredient"));
            } else {
                // legacy style support: allow direct item object at root
                JsonObject obj = new JsonObject();
                if (json.has(HBMKey.ITEM)) {
                    obj.add(HBMKey.ITEM, json.get(HBMKey.ITEM));
                }
                if (json.has(HBMKey.TAG)) {
                    obj.add(HBMKey.TAG, json.get(HBMKey.TAG));
                }
                obj.addProperty(HBMKey.COUNT, GsonHelper.getAsInt(json, HBMKey.COUNT, 1));
                ingredient = CountableIngredient.Serializer.INSTANCE.parse(obj);
            }
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            int time = GsonHelper.getAsInt(json, HBMKey.DURATION, 60);
            return new ShredderRecipe(recipeId, ingredient, result, time);
        }

        @Override
        public ShredderRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            CountableIngredient ingredient = CountableIngredient.Serializer.INSTANCE.parse(buffer);
            ItemStack result = buffer.readItem();
            int time = buffer.readVarInt();
            return new ShredderRecipe(recipeId, ingredient, result, time);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ShredderRecipe recipe) {
            CountableIngredient.Serializer.INSTANCE.write(buffer, recipe.ingredient);
            buffer.writeItem(recipe.result);
            buffer.writeVarInt(recipe.processTime);
        }
    }
}
