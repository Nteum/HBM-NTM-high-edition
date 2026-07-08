package com.hbm.registries;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Supplier;
// 伪fluidstack，用于涉及延迟加载的场景。
public class PseudoFluidStack {
    private Supplier<? extends Fluid> fluidSupplier;
    private int amount;
    public PseudoFluidStack(Supplier<? extends Fluid> fluidSupplier){
        this(fluidSupplier, 1000);
    }
    public PseudoFluidStack(Supplier<? extends Fluid> fluidSupplier, int amount){
        this.fluidSupplier = fluidSupplier;
        this.amount = amount;
    }
    public FluidStack get(){
        return new FluidStack(fluidSupplier.get(), amount);
    }
}
