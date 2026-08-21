package com.hbm.Inventory.recipe;

import com.hbm.HBMKey;
import com.hbm.Inventory.recipe.base.RecipeSerializerBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.function.Function;

/**
 * 焦化配方：流体 → 物品 + 副产物流体。
 * 字段：input（输入流体，含消耗量）、outputItem（输出物品）、byproduct（副产物流体）、duration
 */
public class CokerRecipe extends RecipeSerializerBuilder.AutoRecipe {
    public FluidStack input;
    public ItemStack outputItem;
    public FluidStack byproduct;
    public int duration;

    public static Function<RecipeType<CokerRecipe>, RecipeSerializer<CokerRecipe>> factory =
            type -> new RecipeSerializerBuilder()
                    .fluid(HBMKey.INPUT)
                    .list(HBMKey.OUTPUT_ITEM, RecipeSerializerBuilder.TYPE_STACK)
                    .fluid(HBMKey.OUTPUT_FLUID)
                    .integer(HBMKey.DURATION)
                    .build(type, CokerRecipe::new);

    public <T extends RecipeSerializerBuilder.AutoRecipe> CokerRecipe(ResourceLocation id, RecipeType<T> type, RecipeSerializer<T> serializer) {
        super(id, type, serializer);
    }

    @Override
    protected void onDataLoaded() {
        super.onDataLoaded();
        this.input = (FluidStack) getValue(HBMKey.INPUT);
        List<ItemStack> items = (List<ItemStack>) getValue(HBMKey.OUTPUT_ITEM);
        this.outputItem = items == null || items.isEmpty() ? ItemStack.EMPTY : items.get(0);
        this.byproduct = (FluidStack) getValue(HBMKey.OUTPUT_FLUID);
        this.duration = (int) getValue(HBMKey.DURATION);
    }

    public boolean matchesFluid(net.minecraftforge.fluids.FluidStack fluid){
        return fluid != null && !fluid.isEmpty() && fluid.getFluid().isSame(input.getFluid());
    }

    public int getInputAmount(){
        return input.getAmount();
    }

    public ItemStack getOutputItem(){
        return outputItem == null ? ItemStack.EMPTY : outputItem.copy();
    }

    public FluidStack getByproduct(){
        return byproduct == null ? FluidStack.EMPTY : byproduct.copy();
    }
}
