/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.hbm.recipe;

import com.hbm.datagen.recipe.ingredient.CountableIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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


}
