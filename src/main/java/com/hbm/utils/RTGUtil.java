package com.hbm.utils;

import com.hbm.item.misc.ItemRTGPellet;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

/**
 * RTG 辅助工具。
 * 移植自旧版 com.hbm.util.RTGUtil：计算 RTG 燃料棒的热量。
 * 简化：移除衰变/配置系统，仅保留热量计算。
 */
public class RTGUtil {
    public static boolean hasHeat(IItemHandler itemHandler, int[] rtgSlots) {
        ItemStack[] itemStacks = new ItemStack[itemHandler.getSlots()];
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            itemStacks[i] = itemHandler.getStackInSlot(i);
        }
        return hasHeat(itemStacks, rtgSlots);
    }

    public static boolean hasHeat(ItemStack[] inventory, int[] rtgSlots) {
        for(int slot : rtgSlots) {
            if(inventory[slot] == null || inventory[slot].isEmpty())
                continue;
            if(inventory[slot].getItem() instanceof ItemRTGPellet)
                return true;
        }
        return false;
    }

    public static int updateRTGs(IItemHandler itemHandler, int[] rtgSlots) {
        ItemStack[] itemStacks = new ItemStack[itemHandler.getSlots()];
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            itemStacks[i] = itemHandler.getStackInSlot(i);
        }
        return updateRTGs(itemStacks, rtgSlots);
    }

    public static int updateRTGs(ItemStack[] inventory, int[] rtgSlots) {
        int newHeat = 0;
        for(int slot : rtgSlots) {
            if(inventory[slot] == null || inventory[slot].isEmpty())
                continue;
            if(inventory[slot].getItem() instanceof ItemRTGPellet pellet) {
                newHeat += pellet.getHeat();
            }
        }
        return newHeat;
    }
}
