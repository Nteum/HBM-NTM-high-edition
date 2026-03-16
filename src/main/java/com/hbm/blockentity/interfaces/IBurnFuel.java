package com.hbm.blockentity.interfaces;

import com.hbm.dim.orbit.Space;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;

public interface IBurnFuel extends ITakeAir{
    default int getBurnTime(ItemStack stack){
        return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING);
    }
    default int getBurnHeat(int base, ItemStack stack){
        return base;
    }
}
