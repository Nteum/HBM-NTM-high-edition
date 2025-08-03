/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.hbm.Inventory.recipe;

import com.hbm.datagen.recipe.ingredient.CountableIngredient;
import com.hbm.datagen.recipe.ingredient.FluidStackIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import java.util.*;
import java.util.function.Predicate;

public class HBMRecipeMatcher
{
    /** 用于匹配装配机等无序可堆叠配方
     * 原本的配方匹配算法在{@RecipeMatcher}上，但它的算法太复杂了，我实在无法理解，只能优先以完成功能为重点，效率低一点也是无可奈何地
     */
    public static <T extends ItemStack> boolean orderlessMatch(List<T> inputs, List<? extends CountableIngredient> tests)
    {
        int inputSize = inputs.size();
        int count;
        BitSet bitSet = new BitSet(inputSize);
        for (CountableIngredient test : tests) {
            count = test.value.count;
            for (int i = 0; i < inputSize; i++) {
                if (bitSet.get(i))continue;
                T input = inputs.get(i);
                if (!test.test(input))continue;
                else {
                    bitSet.set(i);
                    count -= input.getCount();
                }
                if (count <= 0)break;
            }
            if (count > 0)return false;
        }
        return true;
    }
    /** 采用另一种方式匹配 */
    public static <T extends ItemStack> boolean orderlessItemMatch(List<T> inputs, List<? extends CountableIngredient> tests){
        HashMap<ItemLike, Integer> countAll = new HashMap<>();
        for (T input : inputs) {
            countAll.compute(input.getItem(), (k,v) -> (v==null) ? input.getCount() : input.getCount() + v);
        }
        for (CountableIngredient ingredient : tests) {
            if (!ingredient.value.flagTag){
                if (!countAll.containsKey(ingredient.value.itemStack.getItem()) || ingredient.value.count > countAll.get(ingredient.value.itemStack.getItem()))
                    return false;
            }else {
                // 在使用tag的情况下统计数量
                Integer cnt = countAll.keySet().stream().reduce(0, (sum, item) -> ingredient.test(new ItemStack(item)) ? sum + countAll.get(item) : sum, Integer::sum);
                if (cnt < ingredient.value.count) return false;
            }
        }
        return true;
    }

    public static <T extends IFluidTank> boolean orderlessFluidMatch(List<T> inputs, List<? extends FluidStackIngredient> tests){
        HashMap<Fluid, Integer> countAll = new HashMap<>();
        for (T input : inputs) {
            FluidStack fluid = input.getFluid();
            countAll.compute(fluid.getFluid(), (k, v) -> (v==null) ? fluid.getAmount() : fluid.getAmount() + v);
        }
        for (FluidStackIngredient ingredient : tests) {
            if (!ingredient.flagTag){
                if (!countAll.containsKey(ingredient.fluidStack.getFluid()) || ingredient.volume > countAll.get(ingredient.fluidStack.getFluid()))
                    return false;
            }else {
                // 在使用tag的情况下统计数量
                Integer cnt = countAll.keySet().stream().reduce(0, (sum, fluid) -> ingredient.test(new FluidStack(fluid,1)) ? sum + countAll.get(fluid) : sum, Integer::sum);
                if (cnt < ingredient.volume) return false;
            }
        }
        return true;
    }
}
