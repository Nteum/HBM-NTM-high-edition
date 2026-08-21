package com.hbm.Inventory.recipe;

import com.hbm.HBMKey;
import com.hbm.Inventory.recipe.base.RecipeSerializerBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Function;

/**
 * 辐射裂解配方：流体 → 2 种流体。
 * 移植自旧版 RadiolysisRecipes：水 → 过氧化氢 + 氢气（象征性，其余按裂解配方可扩展）。
 * 字段：input（输入流体）、output1（输出1）、output2（输出2）
 */
public class RadiolysisRecipe extends RecipeSerializerBuilder.AutoRecipe {
    public FluidStack input;
    public FluidStack output1;
    public FluidStack output2;

    public static Function<RecipeType<RadiolysisRecipe>, RecipeSerializer<RadiolysisRecipe>> factory =
            type -> new RecipeSerializerBuilder()
                    .fluid(HBMKey.INPUT)
                    .fluid("output1")
                    .fluid("output2")
                    .build(type, RadiolysisRecipe::new);

    public <T extends RecipeSerializerBuilder.AutoRecipe> RadiolysisRecipe(ResourceLocation id, RecipeType<T> type, RecipeSerializer<T> serializer) {
        super(id, type, serializer);
    }

    @Override
    protected void onDataLoaded() {
        super.onDataLoaded();
        this.input = (FluidStack) getValue(HBMKey.INPUT);
        this.output1 = (FluidStack) getValue("output1");
        this.output2 = (FluidStack) getValue("output2");
    }

    public boolean matchesInput(FluidStack fluid){
        return fluid != null && !fluid.isEmpty() && fluid.getFluid().isSame(input.getFluid());
    }

    public int getInputAmount(){
        return input == null ? 100 : input.getAmount();
    }

    public FluidStack getOutput1(){
        return output1 == null ? FluidStack.EMPTY : output1.copy();
    }

    public FluidStack getOutput2(){
        return output2 == null ? FluidStack.EMPTY : output2.copy();
    }
}
