package com.hbm.Inventory.material;

import com.hbm.item.ItemEnums.*;
import com.hbm.registries.ModItems;
import com.hbm.registries.ModTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public class BurnUtils {
    public static EnumAshType getAshFromFuel(ItemStack stack) {
        if (stack.is(ModTags.Items.COKE) || stack.is(Items.COAL) || stack.is(ModItems.LIGNITE.get())) return EnumAshType.COAL;
        else if (stack.is(ItemTags.LOGS) || stack.is(ItemTags.SAPLINGS)) return EnumAshType.WOOD;
        else return EnumAshType.MISC;
    }

    public static ItemStack getAshPowder(EnumAshType type){
        return switch (type){
            case WOOD -> ModItems.POWDER_ASH_WOOD.get().getDefaultInstance();
            case COAL -> ModItems.POWDER_ASH_COAL.get().getDefaultInstance();
            case FLY -> ModItems.POWDER_ASH_FLY.get().getDefaultInstance();
            case SOOT -> ModItems.POWDER_ASH_SOOT.get().getDefaultInstance();
            case FULLERENE -> ModItems.POWDER_ASH_FULLERENE.get().getDefaultInstance();
            case MISC -> ModItems.POWDER_ASH_MISC.get().getDefaultInstance();
            default -> ItemStack.EMPTY;
        };
    }

    /** 物品判断方面（有些比较邪门，接口我认为可能变化） */
    public static boolean isFuel(ItemStack itemStack){
        return AbstractFurnaceBlockEntity.isFuel(itemStack);
    }
}
