/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.hbm.Inventory.recipe;

import com.hbm.datagen.recipe.ingredient.CountableIngredient;
import com.hbm.datagen.recipe.ingredient.FluidStackIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
    /**
     * 完成配方后从物品槽中扣除原料。这个函数默认之前已检验被扣除的物品槽数量是够的，因此内部不额外检验
     * */
    public static <T extends ItemStack> void deductItems(List<T> inputSlots, List<? extends CountableIngredient> toDeduct){
        for (CountableIngredient ingredient : toDeduct) {
            int tempCount = ingredient.value.count;
            for (int i = 0; i < inputSlots.size(); i++) {
                T slot = inputSlots.get(i);
                if (ingredient.value.flagTag && slot.is(ingredient.value.tagKey)
                        || !ingredient.value.flagTag && slot.is(ingredient.value.itemStack.getItem())){
                    int subsCount = Math.min(tempCount, slot.getCount());
                    slot.shrink(subsCount);
                    if (slot.isEmpty()) slot = (T) ItemStack.EMPTY;
                    inputSlots.set(i, slot);
                    tempCount -= subsCount;
                    if (tempCount == 0)break;
                }
            }
        }
    }
    /** 检查配方结果是否可以放入机器结果槽里 */
    public static <T extends ItemStack> boolean checkOutputSlots(List<T> outputSlots, List<T> resultItems){
        int emptySlotNum = 0;
        Map<ItemLike, Integer> countAll = new HashMap<>();
        Map<ItemLike, Integer> targetMap = new HashMap<>();
        resultItems.forEach(stack -> targetMap.compute(stack.getItem(), (k, v) -> (v==null) ? 0 : v + stack.getCount()));
        for (T stack : outputSlots) {
            if (!stack.isEmpty())
                countAll.compute(stack.getItem(), (k, v) -> (v == null) ? stack.getMaxStackSize() - stack.getCount() : v + stack.getMaxStackSize() - stack.getCount());
            else
                emptySlotNum ++;
        }
        for (Map.Entry<ItemLike, Integer> entry : targetMap.entrySet()) {
            ItemLike item = entry.getKey();
            Integer count = entry.getValue();
            count -= Math.min(count, countAll.getOrDefault(item, 0));
            if (count > 0){
                int maxStackSize = item.asItem().getMaxStackSize();
                emptySlotNum -= count / maxStackSize + 1;
                if (emptySlotNum < 0) return false;
            }
            targetMap.put(item, count);
        }
        return true;
    }
    /**
     * 完成配方后向结果输出物品槽。这个函数默认之前已检验被扣除的物品槽数量是够的，因此内部不额外检验
     * 吐槽：貌似这个泛型也没啥意义，不过如果使用的ItemStack的继承类中实现了相应的copyWithCount函数，那也可继续使用。
     * */
    public static <T extends ItemStack> void putResultItems(final List<T> outputSlots, final List<T> resultItems){
        for (T resultItem : resultItems) {
            int temp = resultItem.getCount();
            for (int i = 0; i < outputSlots.size(); i++) {
                T slot = outputSlots.get(i);
                if (slot.is(resultItem.getItem())){
                    int insertNum = Math.min(resultItem.getMaxStackSize() - slot.getCount(), temp);
                    slot.grow(insertNum);
                    temp -= insertNum;
                }else if (slot.isEmpty()){
                    int insertNum = Math.min(resultItem.getMaxStackSize(), temp);
                    outputSlots.set(i, (T) resultItem.copyWithCount(insertNum));
                    temp -= insertNum;
                }
                if (temp==0) break;
            }
        }
    }

    /**
     * 完成配方后从物品槽中扣除原流体。这个函数默认之前已检验被扣除的物品槽数量是够的，因此内部不额外检验
     * */
    public static <T extends IFluidTank> void deductFluids(final List<T> inputSlots, final List<? extends FluidStackIngredient> toDeduct){
        for (FluidStackIngredient ingredient : toDeduct) {
            int tempCount = ingredient.volume;
            for (int i = 0; i < inputSlots.size(); i++) {
                FluidStack slot = inputSlots.get(i).getFluid();
                if (ingredient.flagTag && slot.getFluid().is(ingredient.tagKey)
                        || !ingredient.flagTag && slot.isFluidEqual(ingredient.fluidStack)){
                    int subsCount = Math.min(tempCount, slot.getAmount());
                    inputSlots.get(i).drain(subsCount, IFluidHandler.FluidAction.EXECUTE);
                    tempCount -= subsCount;
                    if (tempCount == 0)break;
                }
            }
        }
    }
    /** 检查输出流体槽是否可以成功输出 */
    public static <T extends IFluidTank> boolean checkOutputTanks(List<T> outputTanks, List<FluidStack> resultFluids){
        Map<Fluid, Integer> countAll = new HashMap<>();
        Map<Fluid, Integer> targetMap = new HashMap<>();
        // 用栈记录空的tank，判断空tank是否可以容纳流体时依次判断，暂不考虑根据tank的相对大小容纳更多种类的流体。一方面是避免过于复杂的
        // 算法，另一方面是因为大部分方块实体内部的流体容器本来就是一样大的。
        Deque<IFluidTank> emptyTanks = new LinkedList<>();
        resultFluids.forEach(stack -> targetMap.compute(stack.getFluid(), (k, v) -> (v==null) ? 0 : v + stack.getAmount()));
        for (T tank : outputTanks) {
            if (!tank.getFluid().isEmpty())
                countAll.compute(tank.getFluid().getFluid(), (k, v) -> (v == null) ? tank.getCapacity() - tank.getFluidAmount() : v + tank.getCapacity() - tank.getFluidAmount());
            else
                emptyTanks.addFirst(tank);
        }
        for (Map.Entry<Fluid, Integer> entry : targetMap.entrySet()) {
            Fluid fluid = entry.getKey();
            Integer count = entry.getValue();
            count -= Math.min(count, countAll.getOrDefault(fluid, 0));
            while (count > 0){
                IFluidTank first = emptyTanks.peekFirst();
                if (first == null) return false;
                count -= Math.min(count, first.getCapacity());
                emptyTanks.pollFirst();
            }
            targetMap.put(fluid, count);
        }
        return true;
    }
    /**
     * 完成配方后向结束。这个函数默认之前已检验被扣除的物品槽数量是够的，因此内部不额外检验
     * */
    public static <T extends IFluidTank> void putResultFluids(List<T> outputTanks, List<FluidStack> resultFluids){
        int insertVol;
        for (FluidStack resultFluid : resultFluids) {
            int temp = resultFluid.getAmount();
            for (int i = 0; i < outputTanks.size(); i++) {
                T tank = outputTanks.get(i);
                if (tank.getFluid().isFluidEqual(resultFluid) || tank.getFluid().isEmpty()){
                    insertVol = Math.min(tank.getCapacity() - tank.getFluidAmount(), temp);
                    tank.fill(new FluidStack(resultFluid, insertVol), IFluidHandler.FluidAction.EXECUTE);
                    temp -= insertVol;
                }
                if (temp == 0) break;
            }
        }
    }
}
