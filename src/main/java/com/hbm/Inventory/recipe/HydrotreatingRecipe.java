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
 * 加氢处理配方：输入流体 + 氢 → 脱硫产物 + 酸气。
 * 字段：input（输入油）、hydrogen（消耗氢）、output（脱硫油）、sourGas（酸气）
 */
public class HydrotreatingRecipe extends RecipeSerializerBuilder.AutoRecipe {
    public FluidStack input;
    public FluidStack hydrogen;
    public FluidStack output;
    public FluidStack sourGas;

    public static Function<RecipeType<HydrotreatingRecipe>, RecipeSerializer<HydrotreatingRecipe>> factory =
            type -> new RecipeSerializerBuilder()
                    .fluid(HBMKey.INPUT)
                    .fluid(HBMKey.HYDROGEN)
                    .fluid(HBMKey.OUTPUT)
                    .fluid(HBMKey.SOURGAS)
                    .build(type, HydrotreatingRecipe::new);

    public <T extends RecipeSerializerBuilder.AutoRecipe> HydrotreatingRecipe(ResourceLocation id, RecipeType<T> type, RecipeSerializer<T> serializer) {
        super(id, type, serializer);
    }

    @Override
    protected void onDataLoaded() {
        super.onDataLoaded();
        this.input = (FluidStack) getValue(HBMKey.INPUT);
        this.hydrogen = (FluidStack) getValue(HBMKey.HYDROGEN);
        this.output = (FluidStack) getValue(HBMKey.OUTPUT);
        this.sourGas = (FluidStack) getValue(HBMKey.SOURGAS);
    }

    public boolean matchesInput(FluidStack fluid){
        return fluid != null && !fluid.isEmpty() && fluid.getFluid().isSame(input.getFluid());
    }
}
