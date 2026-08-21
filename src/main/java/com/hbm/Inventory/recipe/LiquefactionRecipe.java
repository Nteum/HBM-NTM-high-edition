package com.hbm.Inventory.recipe;

import com.hbm.HBMKey;
import com.hbm.Inventory.recipe.base.RecipeSerializerBuilder;
import com.hbm.datagen.recipe.ingredient.CountableIngredient;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Function;

/**
 * 液化配方：物品 → 流体。
 * 仿照 RecipeCentrifuge / RecipeCrystallizer，继承 AutoRecipe。
 * 字段：input（物品）、outputFluid（输出流体）、duration（加工时长）、productivity（副产物倍率）
 */
public class LiquefactionRecipe extends RecipeSerializerBuilder.AutoRecipe {
    public CountableIngredient input;
    public FluidStack outputFluid;
    public int duration;

    public static Function<RecipeType<LiquefactionRecipe>, RecipeSerializer<LiquefactionRecipe>> factory =
            type -> new RecipeSerializerBuilder()
                    .countableIngredient(HBMKey.INPUT)
                    .fluid(HBMKey.OUTPUT_FLUID)
                    .integer(HBMKey.DURATION)
                    .build(type, LiquefactionRecipe::new);

    public <T extends RecipeSerializerBuilder.AutoRecipe> LiquefactionRecipe(ResourceLocation id, RecipeType<T> type, RecipeSerializer<T> serializer) {
        super(id, type, serializer);
    }

    @Override
    protected void onDataLoaded() {
        super.onDataLoaded();
        this.input = (CountableIngredient) getValue(HBMKey.INPUT);
        this.outputFluid = (FluidStack) getValue(HBMKey.OUTPUT_FLUID);
        this.duration = (int) getValue(HBMKey.DURATION);
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        return this.input.test(pContainer.getItem(0));
    }

    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    /** 机器用：判断输入物品是否匹配 */
    public boolean matches(ItemStack stack){
        return this.input.test(stack);
    }

    /** 获取输出流体 */
    public FluidStack getOutputFluid(){
        return outputFluid.copy();
    }
}
