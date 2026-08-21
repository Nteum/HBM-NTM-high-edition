package com.hbm.Inventory.recipe;

import com.hbm.HBMKey;
import com.hbm.Inventory.recipe.base.RecipeSerializerBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.function.Function;

/**
 * 固化配方：流体 → 物品。仿照 LiquefactionRecipe 逆向。
 * 字段：inputFluid（输入流体，含消耗量）、output（输出物品）、duration（加工时长）
 */
public class SolidificationRecipe extends RecipeSerializerBuilder.AutoRecipe {
    public FluidStack inputFluid;
    public ItemStack output;
    public int duration;

    public static Function<RecipeType<SolidificationRecipe>, RecipeSerializer<SolidificationRecipe>> factory =
            type -> new RecipeSerializerBuilder()
                    .fluid(HBMKey.INPUT)
                    .list(HBMKey.OUTPUT, RecipeSerializerBuilder.TYPE_STACK)
                    .integer(HBMKey.DURATION)
                    .build(type, SolidificationRecipe::new);

    public <T extends RecipeSerializerBuilder.AutoRecipe> SolidificationRecipe(ResourceLocation id, RecipeType<T> type, RecipeSerializer<T> serializer) {
        super(id, type, serializer);
    }

    @Override
    protected void onDataLoaded() {
        super.onDataLoaded();
        this.inputFluid = (FluidStack) getValue(HBMKey.INPUT);
        List<ItemStack> outputs = (List<ItemStack>) getValue(HBMKey.OUTPUT);
        this.output = outputs.isEmpty() ? ItemStack.EMPTY : outputs.get(0);
        this.duration = (int) getValue(HBMKey.DURATION);
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        return true;
    }

    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    /** 判断输入流体是否匹配 */
    public boolean matches(FluidStack fluid){
        return fluid != null && !fluid.isEmpty() && fluid.getFluid().isSame(inputFluid.getFluid());
    }

    /** 获取输出物品 */
    public ItemStack getOutput(){
        return output.copy();
    }

    /** 获取消耗的流体量 */
    public int getInputAmount(){
        return inputFluid.getAmount();
    }
}
