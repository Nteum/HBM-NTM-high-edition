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
 * 气体离心机配方（简化版）。
 * 移植自旧版 GasCentrifugeRecipes（去掉 PseudoFluidType 链式富集，改为流体 → 物品列表）。
 */
public class GasCentrifugeRecipe extends RecipeSerializerBuilder.AutoRecipe {
    public FluidStack input;
    public List<ItemStack> outputs;
    public int processingSpeed;

    public static Function<RecipeType<GasCentrifugeRecipe>, RecipeSerializer<GasCentrifugeRecipe>> factory =
            type -> new RecipeSerializerBuilder()
                    .fluid(HBMKey.INPUT)
                    .list("outputs", RecipeSerializerBuilder.TYPE_STACK)
                    .integer(HBMKey.DURATION)
                    .build(type, GasCentrifugeRecipe::new);

    public <T extends RecipeSerializerBuilder.AutoRecipe> GasCentrifugeRecipe(ResourceLocation id, RecipeType<T> type, RecipeSerializer<T> serializer) {
        super(id, type, serializer);
    }

    @Override
    protected void onDataLoaded() {
        super.onDataLoaded();
        this.input = (FluidStack) getValue(HBMKey.INPUT);
        this.outputs = (List<ItemStack>) getValue("outputs");
        this.processingSpeed = (int) getValue(HBMKey.DURATION);
    }

    public boolean matchesInput(FluidStack fluid){
        return fluid != null && !fluid.isEmpty() && fluid.getFluid().isSame(input.getFluid());
    }

    public int getInputAmount(){
        return input == null ? 1000 : input.getAmount();
    }

    public List<ItemStack> getOutputs(){
        return outputs == null ? List.of() : outputs;
    }
}
