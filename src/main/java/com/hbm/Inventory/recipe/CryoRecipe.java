package com.hbm.Inventory.recipe;

import com.hbm.HBMKey;
import com.hbm.Inventory.recipe.base.RecipeSerializerBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Function;

/**
 * 低温蒸馏配方：输入气体 → 4 种输出气体。
 * 移植自旧版 CryoRecipes（象征性：大气/行星气体分离）。
 */
public class CryoRecipe extends RecipeSerializerBuilder.AutoRecipe {
    public FluidStack input;
    public FluidStack output1;
    public FluidStack output2;
    public FluidStack output3;
    public FluidStack output4;

    public static Function<RecipeType<CryoRecipe>, RecipeSerializer<CryoRecipe>> factory =
            type -> new RecipeSerializerBuilder()
                    .fluid(HBMKey.INPUT)
                    .fluid("output1")
                    .fluid("output2")
                    .fluid("output3")
                    .fluid("output4")
                    .build(type, CryoRecipe::new);

    public <T extends RecipeSerializerBuilder.AutoRecipe> CryoRecipe(ResourceLocation id, RecipeType<T> type, RecipeSerializer<T> serializer) {
        super(id, type, serializer);
    }

    @Override
    protected void onDataLoaded() {
        super.onDataLoaded();
        this.input = (FluidStack) getValue(HBMKey.INPUT);
        this.output1 = (FluidStack) getValue("output1");
        this.output2 = (FluidStack) getValue("output2");
        this.output3 = (FluidStack) getValue("output3");
        this.output4 = (FluidStack) getValue("output4");
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
            case 3 -> output4 == null ? FluidStack.EMPTY : output4.copy();
            default -> FluidStack.EMPTY;
        };
    }
}
