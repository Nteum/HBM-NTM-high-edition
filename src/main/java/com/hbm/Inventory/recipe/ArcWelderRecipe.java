package com.hbm.Inventory.recipe;

import com.hbm.HBMKey;
import com.hbm.Inventory.recipe.base.RecipeSerializerBuilder;
import com.hbm.datagen.recipe.ingredient.CountableIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.function.Function;

/**
 * 电弧焊配方：最多 3 种物品 + 可选流体 → 物品。
 * 移植自旧版 ArcWelderRecipes（简化：象征性配方，字段结构与旧版一致）。
 * 字段：input（3 个可空输入物品）、fluid（可选输入流体）、output、duration、consumption
 */
public class ArcWelderRecipe extends RecipeSerializerBuilder.AutoRecipe {
    public List<CountableIngredient> inputs;
    public FluidStack fluid;
    public ItemStack resultItem;
    public int duration;
    public long consumption;

    public static Function<RecipeType<ArcWelderRecipe>, RecipeSerializer<ArcWelderRecipe>> factory =
            type -> new RecipeSerializerBuilder()
                    .list("inputs", RecipeSerializerBuilder.TYPE_COUNTABLE_INGREDIENT)
                    .fluid("fluid")
                    .list(HBMKey.OUTPUT, RecipeSerializerBuilder.TYPE_STACK)
                    .integer(HBMKey.DURATION)
                    .integer("consumption")
                    .build(type, ArcWelderRecipe::new);

    public <T extends RecipeSerializerBuilder.AutoRecipe> ArcWelderRecipe(ResourceLocation id, RecipeType<T> type, RecipeSerializer<T> serializer) {
        super(id, type, serializer);
    }

    @Override
    protected void onDataLoaded() {
        super.onDataLoaded();
        this.inputs = (List<CountableIngredient>) getValue("inputs");
        this.fluid = (FluidStack) getValue("fluid");
        List<ItemStack> outList = (List<ItemStack>) getValue(HBMKey.OUTPUT);
        this.resultItem = outList == null || outList.isEmpty() ? ItemStack.EMPTY : outList.get(0);
        this.duration = (int) getValue(HBMKey.DURATION);
        this.consumption = (int) getValue("consumption");
    }

    public ItemStack getOutputItem(){
        return resultItem == null ? ItemStack.EMPTY : resultItem.copy();
    }
}
