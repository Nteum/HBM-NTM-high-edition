package com.hbm.core.capability.item;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import java.util.function.Predicate;

public class MachineItemHandler extends ItemStackHandler {
    private final Predicate<ItemStack>[] slotFilters; // 每个槽位一个 filter

    public MachineItemHandler(int slots) {
        super(slots);
        this.slotFilters = new Predicate[slots];
    }

    public void setSlotFilter(int slot, Predicate<ItemStack> filter) {
        this.slotFilters[slot] = filter;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        Predicate<ItemStack> filter = slotFilters[slot];
        return filter == null || filter.test(stack);
    }

    /** 通知内容变化，供模块/外部触发保存 */
    public void notifyContentsChanged(int slot){
        this.onContentsChanged(slot);
    }
}
