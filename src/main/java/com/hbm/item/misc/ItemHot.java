package com.hbm.item.misc;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemHot extends Item {
    protected int heat;
    public ItemHot(int heat, Properties pProperties) {
        super(pProperties);
        this.heat = heat;
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
        if (pStack.getItem() instanceof ItemHot && getHeat(pStack) > 0){
            setHeat(pStack, getHeat(pStack) - 1);
        }
    }

    public int getMaxHeat(){
        return this.heat;
    }

    public static int getHeat(ItemStack stack){
        if (!(stack.getItem() instanceof ItemHot)) return 0;
        if (!stack.getOrCreateTag().contains("heat")) stack.getTag().putInt("heat", ((ItemHot) stack.getItem()).heat);
        return stack.getOrCreateTag().getInt("heat");
    }

    public static void setHeat(ItemStack stack, int heat){
        stack.getOrCreateTag().putInt("heat", heat);
    }

    public static boolean isCoolDown(ItemStack itemStack){
        return getHeat(itemStack) == 0;
    }
}
