package com.hbm.Inventory.recipe;

import com.hbm.HBMKey;
import com.hbm.Inventory.recipe.base.RecipeSerializerBuilder;
import com.hbm.datagen.recipe.ingredient.CountableIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Function;

/**
 * 混合机配方：至多 2 种输入流体 + 可选固体 → 输出流体。
 * 移植自旧版 MixerRecipes（简化：象征性配方，字段结构与旧版一致）。
 */
public class MixerRecipe extends RecipeSerializerBuilder.AutoRecipe {
    public FluidStack input1;
    public FluidStack input2;
    public CountableIngredient solidInput;
    public int processTime;
    public int outputAmount;

    public static Function<RecipeType<MixerRecipe>, RecipeSerializer<MixerRecipe>> factory =
            type -> new RecipeSerializerBuilder()
                    .fluid("input1")
                    .fluid("input2")
                    .countableIngredient("solidInput")
                    .integer("outputAmount")
                    .integer(HBMKey.DURATION)
                    .build(type, MixerRecipe::new);

    public <T extends RecipeSerializerBuilder.AutoRecipe> MixerRecipe(ResourceLocation id, RecipeType<T> type, RecipeSerializer<T> serializer) {
        super(id, type, serializer);
    }

    @Override
    protected void onDataLoaded() {
        super.onDataLoaded();
        this.input1 = (FluidStack) getValue("input1");
        this.input2 = (FluidStack) getValue("input2");
        this.solidInput = (CountableIngredient) getValue("solidInput");
        this.outputAmount = (int) getValue("outputAmount");
        this.processTime = (int) getValue(HBMKey.DURATION);
    }

    public int getOutputAmount(){
        return outputAmount;
    }
}
