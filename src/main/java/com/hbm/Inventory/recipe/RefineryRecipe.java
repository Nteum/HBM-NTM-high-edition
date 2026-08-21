package com.hbm.Inventory.recipe;

import com.hbm.HBMKey;
import com.hbm.Inventory.recipe.base.RecipeSerializerBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.function.Function;

/**
 * 炼油配方：输入流体（热油）→ 4 种馏分流体。
 * 字段：input（输入热油）、outputs（4 种输出流体）
 */
public class RefineryRecipe extends RecipeSerializerBuilder.AutoRecipe {
    public FluidStack input;
    public List<FluidStack> outputs;

    public static Function<RecipeType<RefineryRecipe>, RecipeSerializer<RefineryRecipe>> factory =
            type -> new RecipeSerializerBuilder()
                    .fluid(HBMKey.INPUT)
                    .list(HBMKey.OUTPUT, RecipeSerializerBuilder.TYPE_FLUID_STACK)
                    .build(type, RefineryRecipe::new);

    public <T extends RecipeSerializerBuilder.AutoRecipe> RefineryRecipe(ResourceLocation id, RecipeType<T> type, RecipeSerializer<T> serializer) {
        super(id, type, serializer);
    }

    @Override
    protected void onDataLoaded() {
        super.onDataLoaded();
        this.input = (FluidStack) getValue(HBMKey.INPUT);
        this.outputs = (List<FluidStack>) getValue(HBMKey.OUTPUT);
    }

    public boolean matchesInput(FluidStack fluid){
        return fluid != null && !fluid.isEmpty() && fluid.getFluid().isSame(input.getFluid());
    }

    public FluidStack getOutput(int index){
        if (outputs == null || index >= outputs.size()) return FluidStack.EMPTY;
        FluidStack stack = outputs.get(index);
        return stack == null ? FluidStack.EMPTY : stack.copy();
    }

    public int getInputAmount(){
        return input == null ? 100 : input.getAmount();
    }
}
