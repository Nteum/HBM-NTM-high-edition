package com.hbm.Inventory.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.hbm.HBM;
import com.hbm.blockentity.machine.AssemblerEntity;
import com.hbm.datagen.recipe.ingredient.CountableIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * ref:vanilla - shapelessRecipe
 * */
public class AssemblerRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    final int processingTime;   //加工时间
    public final ItemStack result;
    public final NonNullList<CountableIngredient> ingredients;
    static final int MAX_SIZE = 12;
    public AssemblerRecipe(ResourceLocation id, ItemStack result, NonNullList<CountableIngredient> ingredients){
        this(id,100_000,result,ingredients);
    }
    public AssemblerRecipe(ResourceLocation id,int processingTime, ItemStack result, NonNullList<CountableIngredient> ingredients) {
        this.id = id;
        this.processingTime = processingTime;
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
        return ModRecipes.ASSEMBLER.type().get();
    }
    public int getProcessingTime(){return processingTime;}

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        //比较复杂，还需要根据原版的RecipeMatcher修改
        if (pContainer instanceof AssemblerEntity entity){
            int cnt = 0;
            List<ItemStack> inputs = new ArrayList<>();
            for (int i : AssemblerEntity.ASSEMBLE_SLOTS) {
                if (!entity.getItemHandler().getStackInSlot(i).isEmpty())inputs.add(entity.getItemHandler().getStackInSlot(i));
            }
            return inputs.size() >= ingredients.size()
                    && HBMRecipeMatcher.orderlessMatch(inputs,this.ingredients);
//                    && RecipeMatcher.findMatches(inputs,this.ingredients)!=null;
        }
        return false;
    }
    public boolean checkItem(ItemStack itemStack){
        for (CountableIngredient ingredient : this.ingredients) {
            if (ingredient.test(itemStack))return true;
        }
        return false;
    }

    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
        if (pContainer instanceof AssemblerEntity entity){
            for (CountableIngredient ingredient : this.ingredients) {
                int tempCount = ingredient.value.count;
                for (int i : AssemblerEntity.ASSEMBLE_SLOTS) {
                    ItemStack itemStack = entity.getItemHandler().getStackInSlot(i);
                    if (ingredient.value.flagTag && itemStack.is(ingredient.value.tagKey)
                            || !ingredient.value.flagTag && itemStack.is(ingredient.value.itemStack.getItem())){
                        int subsCount = Math.min(tempCount, itemStack.getCount());
                        itemStack.shrink(subsCount);
                        if (itemStack.isEmpty())entity.getItemHandler().setStackInSlot(i,ItemStack.EMPTY);
                        else entity.getItemHandler().setStackInSlot(i,itemStack);
                        tempCount -= subsCount;
                        if (tempCount == 0)break;
                    }
                }
            }
        }
        return this.result.copy();
    }

    public static class Serializer implements RecipeSerializer<AssemblerRecipe> {
        public static final AssemblerRecipe.Serializer INSTANCE = new AssemblerRecipe.Serializer();
        private static final ResourceLocation NAME = HBM.rl("assembler_recipe");
        public AssemblerRecipe fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
            NonNullList<CountableIngredient> nonnulllist = RecipeHelper.itemsFromJson(GsonHelper.getAsJsonArray(pJson, "ingredients"));
            if (nonnulllist.isEmpty()) {
                throw new JsonParseException("No ingredients for assembler recipe");
            } else if (nonnulllist.size() > AssemblerRecipe.MAX_SIZE) {
                throw new JsonParseException("Too many ingredients for assembler recipe. The maximum is " + AssemblerRecipe.MAX_SIZE);
            } else {
                ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pJson, "result"));
                int processingTime = GsonHelper.getAsInt(pJson,"number");
                return new AssemblerRecipe(pRecipeId,processingTime, itemstack, nonnulllist);
            }
        }

        public AssemblerRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            int i = pBuffer.readVarInt();
            NonNullList<CountableIngredient> nonnulllist = NonNullList.withSize(i, CountableIngredient.EMPTY);

            for(int j = 0; j < nonnulllist.size(); ++j) {
                nonnulllist.set(j, CountableIngredient.Serializer.INSTANCE.parse(pBuffer));
            }

            ItemStack itemstack = pBuffer.readItem();
            int processingTime = pBuffer.readInt();
            return new AssemblerRecipe(pRecipeId,processingTime, itemstack, nonnulllist);
        }

        public void toNetwork(FriendlyByteBuf pBuffer, AssemblerRecipe pRecipe) {
            pBuffer.writeVarInt(pRecipe.ingredients.size());

            for(CountableIngredient ingredient : pRecipe.ingredients) {
                CountableIngredient.Serializer.INSTANCE.write(pBuffer,ingredient);
            }

            pBuffer.writeItem(pRecipe.result);
            pBuffer.writeInt(pRecipe.processingTime);
        }
    }
}
