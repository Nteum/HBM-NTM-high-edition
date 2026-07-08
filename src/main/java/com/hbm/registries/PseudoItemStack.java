package com.hbm.registries;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Supplier;

public class PseudoItemStack {
    private Supplier<? extends ItemLike> itemSupplier;
    private int count;
    public PseudoItemStack(Supplier<? extends ItemLike> itemSupplier){
        this(itemSupplier, 1);
    }
    public PseudoItemStack(Supplier<? extends ItemLike> itemSupplier, int count){
        this.itemSupplier = itemSupplier;
        this.count = count;
    }
    public ItemStack get(){
        return new ItemStack(itemSupplier.get(), count);
    }
}
