package com.hbm.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.hbm.HBM;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
/**
 * ref:vinilla - shapelessRecipe
 * */
public class AssemblerRecipe implements Recipe<CraftingContainer> {
    private final ResourceLocation id;
    final int energyNeeded;
    final ItemStack result;
    final NonNullList<Ingredient> ingredients;
    static final int MAX_SIZE = 12;
    public AssemblerRecipe(ResourceLocation id, ItemStack result, NonNullList<Ingredient> ingredients){
        this(id,100_000,result,ingredients);
    }
    public AssemblerRecipe(ResourceLocation id,int energyNeeded, ItemStack result, NonNullList<Ingredient> ingredients) {
        this.id = id;
        this.energyNeeded = energyNeeded;
        this.result = result;
        this.ingredients = ingredients;
    }
    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return this.result;
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
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= this.ingredients.size();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeType.ASSEMBLER.get();
    }

    @Override
    public boolean matches(CraftingContainer pContainer, Level pLevel) {
        //比较复杂，还需要根据原版的RecipeMatcher修改
        return false;
    }

    @Override
    public ItemStack assemble(CraftingContainer pContainer, RegistryAccess pRegistryAccess) {
        return this.result.copy();
    }

    public static class Serializer implements RecipeSerializer<AssemblerRecipe> {
        public static final AssemblerRecipe.Serializer INSTANCE = new AssemblerRecipe.Serializer();
        private static final ResourceLocation NAME = HBM.rl("assembler_recipe");
        public AssemblerRecipe fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
            NonNullList<Ingredient> nonnulllist = itemsFromJson(GsonHelper.getAsJsonArray(pJson, "ingredients"));
            if (nonnulllist.isEmpty()) {
                throw new JsonParseException("No ingredients for assembler recipe");
            } else if (nonnulllist.size() > AssemblerRecipe.MAX_SIZE) {
                throw new JsonParseException("Too many ingredients for assembler recipe. The maximum is " + AssemblerRecipe.MAX_SIZE);
            } else {
                ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pJson, "result"));
                int energy = GsonHelper.getAsInt(pJson,"energyNeeded");
                return new AssemblerRecipe(pRecipeId,energy, itemstack, nonnulllist);
            }
        }

        private static NonNullList<Ingredient> itemsFromJson(JsonArray pIngredientArray) {
            NonNullList<Ingredient> nonnulllist = NonNullList.create();

            for(int i = 0; i < pIngredientArray.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(pIngredientArray.get(i), false);
                if (true || !ingredient.isEmpty()) { // FORGE: Skip checking if an ingredient is empty during shapeless recipe deserialization to prevent complex ingredients from caching tags too early. Can not be done using a config value due to sync issues.
                    nonnulllist.add(ingredient);
                }
            }

            return nonnulllist;
        }

        public AssemblerRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            int i = pBuffer.readVarInt();
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

            for(int j = 0; j < nonnulllist.size(); ++j) {
                nonnulllist.set(j, Ingredient.fromNetwork(pBuffer));
            }

            ItemStack itemstack = pBuffer.readItem();
            int energy = pBuffer.readInt();
            return new AssemblerRecipe(pRecipeId,energy, itemstack, nonnulllist);
        }

        public void toNetwork(FriendlyByteBuf pBuffer, AssemblerRecipe pRecipe) {
            pBuffer.writeVarInt(pRecipe.ingredients.size());

            for(Ingredient ingredient : pRecipe.ingredients) {
                ingredient.toNetwork(pBuffer);
            }

            pBuffer.writeItem(pRecipe.result);
            pBuffer.writeInt(pRecipe.energyNeeded);
        }
    }
}
