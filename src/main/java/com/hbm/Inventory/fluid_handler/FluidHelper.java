package com.hbm.Inventory.fluid_handler;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class FluidHelper {

    /**
     * 判断某个 ItemStack 是否包含可以被提取出来的流体
     */
    public static boolean isDrainable(ItemStack stack) {
        if (stack.isEmpty()) return false;
        // 1. 尝试获取物品的流体 Capability
        return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).map(fluidHandler -> {
            // 2. 模拟提取 1mB 的任意流体 (SIMULATE 模式绝对不会消耗/改变原物品)
            FluidStack drained = fluidHandler.drain(1, IFluidHandlerItem.FluidAction.SIMULATE);
            // 3. 如果提取出来的流体不为空，说明这个物品确实能抽走流体！
            return !drained.isEmpty();
        }).orElse(false); // 如果物品根本不支持流体 Cap，直接返回 false
    }
}