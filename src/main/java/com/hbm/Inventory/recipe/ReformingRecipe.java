package com.hbm.Inventory.recipe;

import com.hbm.HBMKey;
import com.hbm.Inventory.recipe.base.RecipeSerializerBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Function;

/**
 * 催化重整配方：输入流体 → 3 种输出流体。
 * 移植自旧版 ReformingRecipes（象征性：加热油/石脑油 → 重整产物/石油/氢气）。
 */
public class ReformingRecipe extends RecipeSerializerBuilder.AutoRecipe {
    public FluidStack input;
    public FluidStack output1;
    public FluidStack output2;
    public FluidStack output3;

    public static Function<RecipeType<ReformingRecipe>, RecipeSerializer<ReformingRecipe>> factory =
            type -> new RecipeSerializerBuilder()
                    .fluid(HBMKey.INPUT)
                    .fluid("output1")
                    .fluid("output2")
                    .fluid("output3")
                    .build(type, ReformingRecipe::new);

    public <T extends RecipeSerializerBuilder.AutoRecipe> ReformingRecipe(ResourceLocation id, RecipeType<T> type, RecipeSerializer<T> serializer) {
        super(id, type, serializer);
    }

    @Override
    protected void onDataLoaded() {
        super.onDataLoaded();
        this.input = (FluidStack) getValue(HBMKey.INPUT);
        this.output1 = (FluidStack) getValue("output1");
        this.output2 = (FluidStack) getValue("output2");
        this.output3 = (FluidStack) getValue("output3");
    }

    public boolean matchesInput(FluidStack fluid){
        return fluid != null && !fluid.isEmpty() && fluid.getFluid().isSame(input.getFluid());
    }

    public int getInputAmount(){
        return input == null ? 100 : input.getAmount();
    }

    public FluidStack getOutput(int index){
        return switch (index){
            case 0 -> output1 == null ? FluidStack.EMPTY : output1.copy();
            case 1 -> output2 == null ? FluidStack.EMPTY : output2.copy();
            case 2 -> output3 == null ? FluidStack.EMPTY : output3.copy();
            default -> FluidStack.EMPTY;
        };
    }
}
