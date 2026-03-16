package com.hbm.Inventory.material;

import com.hbm.registries.ModItems;
import com.hbm.registries.ModTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BurnUtils {
    public static EnumAshType getAshFromFuel(ItemStack stack) {
        if (stack.is(ModTags.Items.COKE) || stack.is(Items.COAL) || stack.is(ModItems.LIGNITE.get())) return EnumAshType.COAL;
        else if (stack.is(ItemTags.LOGS) || stack.is(ItemTags.SAPLINGS)) return EnumAshType.WOOD;
        else return EnumAshType.MISC;
    }
    public enum EnumAshType { WOOD, COAL, MISC, FLY, SOOT, FULLERENE}
}
