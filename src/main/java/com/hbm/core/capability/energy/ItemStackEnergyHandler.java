package com.hbm.core.capability.energy;

import com.hbm.registries.HBMCaps;
import net.minecraft.world.item.ItemStack;

public class ItemStackEnergyHandler extends BasicEnergyHandler {
    private ItemStack innerStack;
    public ItemStackEnergyHandler(ItemStack stack, long capacity){
        this(stack, capacity, Long.MAX_VALUE, true);
    }
    public ItemStackEnergyHandler(ItemStack stack, long capacity, long inout, boolean isEmpty){
        this(stack, capacity, inout, inout, isEmpty);
    }
    public ItemStackEnergyHandler(ItemStack stack, long capacity, long input, long output, boolean isEmpty){
        super(capacity, input, output, isEmpty ? 0 : capacity);
        this.innerStack = stack;
    }

    @Override
    public void onContentsChanged() {
        this.innerStack.getOrCreateTag().put(HBMCaps.LONG_ENERGY.getName(), serializeNBT());
//        ItemDataUtils.writeContainers(innerStack, HBMKey.ENERGY, this);
        saveDamageValue();
    }
    private void saveDamageValue(){
        if (innerStack.isDamageableItem())
            innerStack.setDamageValue((int) ((1 - (float) energy / capacity) * innerStack.getMaxDamage()));
    }
}

