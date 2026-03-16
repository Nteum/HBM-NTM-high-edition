package com.hbm.Inventory.recipe;

import com.google.gson.JsonObject;
import com.hbm.blockentity.machine.PressEntity;
import com.hbm.item.tool.ItemStamp;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class RecipePress implements Recipe<Container> {
    private final ResourceLocation id;
    public Ingredient input;
    ItemStamp.StampType stamp;
    ItemStack result;
    public RecipePress(ResourceLocation id, ItemStack result, Ingredient input, ItemStamp.StampType stamp){
        this.id = id;
        this.result = result;
        this.stamp = stamp;
        this.input = input;
    }
    @Override
    public boolean matches(Container pContainer, Level pLevel) {
//        if (!(pContainer instanceof PressEntity)) return false;
        ItemStack stampItem = pContainer.getItem(1);
        ItemStack inputItem = pContainer.getItem(2);
        return input.test(inputItem) && stampItem.getItem() instanceof ItemStamp && ((ItemStamp) stampItem.getItem()).getType() == stamp;
    }

    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return this.result;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.PRESS.type().get();
    }

    public static class Serializer implements RecipeSerializer<RecipePress>{
        public static final RecipePress.Serializer INSTANCE = new RecipePress.Serializer();
        @Override
        public RecipePress fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
            ItemStamp.StampType type = ItemStamp.StampType.valueOf(GsonHelper.getAsString(pJson, "stamp", ""));
            Ingredient input = Ingredient.fromJson(pJson.get("input"));
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pJson, "result"));
            return new RecipePress(pRecipeId, result, input, type);
        }

        @Override
        public @Nullable RecipePress fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            return new RecipePress(pRecipeId, pBuffer.readItem(), Ingredient.fromNetwork(pBuffer), pBuffer.readEnum(ItemStamp.StampType.class));
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, RecipePress pRecipe) {
            pBuffer.writeItem(pRecipe.result);
            pRecipe.input.toNetwork(pBuffer);
            pBuffer.writeEnum(pRecipe.stamp);
        }
    }
}