package com.hbm.Inventory.recipe;

import com.google.gson.JsonObject;
import com.hbm.HBMKey;
import com.hbm.blockentity.machine.ChemplantEntity;
import com.hbm.datagen.recipe.ingredient.CountableIngredient;
import com.hbm.datagen.recipe.ingredient.FluidStackIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class ChemplantRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    final int duration;   //加工时间
//    final long power;
    public final List<ItemStack> resultItems;
    public final List<FluidStack> resultFLuids;
    final NonNullList<CountableIngredient> inputItems;
    final NonNullList<FluidStackIngredient> inputFluids;
    static final int MAX_SIZE = 4;
    static final Serializer SERIALIZER = new Serializer();
    public ChemplantRecipe(ResourceLocation id, int duration, List<ItemStack> resultItems, List<FluidStack> resultFLuids, NonNullList<CountableIngredient> inputItems, NonNullList<FluidStackIngredient> inputFluids) {
        this.id = id;
        this.duration = duration;
//        this.power = power;
        this.resultItems = resultItems;
        this.resultFLuids = resultFLuids;
        this.inputItems = inputItems;
        this.inputFluids = inputFluids;
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        if (pContainer instanceof ChemplantEntity chemplantEntity){
            return HBMRecipeMatcher.orderlessItemMatch(chemplantEntity.items.subList(12,16), inputItems)
                    && HBMRecipeMatcher.orderlessFluidMatch(chemplantEntity.getFluidTanks(null).subList(0,2), inputFluids);
        }
        return false;
    }
    /** 处理配方结果 */
    public void assemble(Container pContainer) {
        if (pContainer instanceof ChemplantEntity chemplantEntity){
            HBMRecipeMatcher.deductItems(chemplantEntity.items.subList(12,16), inputItems);
            HBMRecipeMatcher.deductFluids(chemplantEntity.getFluidTanks(null).subList(0,2), inputFluids);
            HBMRecipeMatcher.putResultItems(chemplantEntity.items.subList(4,8), resultItems);
            HBMRecipeMatcher.putResultFluids(chemplantEntity.getFluidTanks(null).subList(2,4), resultFLuids);
        }
    }
    // 无用，因为化工厂还会生成流体
    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
        return null;
    }
    // 只能检验物品是否放在物品格子里可以
    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= this.inputItems.size();
    }
    // 无用，因为可能生成多个结果物品
    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return null;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.CHEMPLANT.type().get();
    }

    public int getDuration(){return duration;}
//    public long getPower(){return power;}

    public static class Serializer implements RecipeSerializer<ChemplantRecipe>{
        @Override
        public ChemplantRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            NonNullList<CountableIngredient> inputItems = RecipeHelper.itemsFromJson(GsonHelper.getAsJsonArray(pSerializedRecipe, "inputItems"));
            NonNullList<FluidStackIngredient> inputFluids = RecipeHelper.fluidsFromJson(GsonHelper.getAsJsonArray(pSerializedRecipe, "inputFluids"));
            List<ItemStack> resultItems = RecipeHelper.itemListFromJson(GsonHelper.getAsJsonArray(pSerializedRecipe, "resultItems"));
            List<FluidStack> resultFluids = RecipeHelper.fluidListFromJson(GsonHelper.getAsJsonArray(pSerializedRecipe, "resultFluids"));
            int duration = GsonHelper.getAsInt(pSerializedRecipe, HBMKey.DURATION);
            return new ChemplantRecipe(pRecipeId, duration, resultItems, resultFluids, inputItems, inputFluids);
        }

        @Override
        public @Nullable ChemplantRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            int duration = pBuffer.readInt();
            long power = pBuffer.readLong();

            int len = pBuffer.readInt();
            NonNullList<CountableIngredient> inputItems = NonNullList.withSize(len, CountableIngredient.EMPTY);
            for (int i = 0; i < len; i++) {
                inputItems.set(i, CountableIngredient.Serializer.INSTANCE.parse(pBuffer));
            }

            len = pBuffer.readInt();
            NonNullList<FluidStackIngredient> inputFluids = NonNullList.withSize(len, FluidStackIngredient.EMPTY);
            for (int i = 0; i < len; i++) {
                inputFluids.set(i, FluidStackIngredient.fromNetwork(pBuffer));
            }

            len = pBuffer.readInt();
            List<ItemStack> resultItems = new java.util.ArrayList<>(List.of());
            for (int i = 0; i < len; i++) {
                resultItems.add(pBuffer.readItem());
            }

            len = pBuffer.readInt();
            List<FluidStack> resultFLuids = new java.util.ArrayList<>(List.of());
            for (int i = 0; i < len; i++) {
                resultFLuids.add(pBuffer.readFluidStack());
            }

            return new ChemplantRecipe(pRecipeId, duration, resultItems, resultFLuids, inputItems, inputFluids);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, ChemplantRecipe pRecipe) {
            pBuffer.writeInt(pRecipe.duration);
//            pBuffer.writeLong(pRecipe.power);
            pBuffer.writeInt(pRecipe.inputItems.size());
            for (CountableIngredient countableIngredient : pRecipe.inputItems) {
                CountableIngredient.Serializer.INSTANCE.write(pBuffer, countableIngredient);
            }
            pBuffer.writeInt(pRecipe.inputFluids.size());
            for (FluidStackIngredient fluidStackIngredient : pRecipe.inputFluids) {
                FluidStackIngredient.toNetwork(pBuffer, fluidStackIngredient);
            }
            pBuffer.writeInt(pRecipe.resultItems.size());
            for (ItemStack itemStack : pRecipe.resultItems) {
                pBuffer.writeItem(itemStack);
            }
            pBuffer.writeInt(pRecipe.resultFLuids.size());
            for (FluidStack fluidStack : pRecipe.resultFLuids) {
                pBuffer.writeFluidStack(fluidStack);
            }
        }
    }
}
