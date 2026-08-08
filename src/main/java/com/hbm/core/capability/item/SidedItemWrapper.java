package com.hbm.core.capability.item;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import static com.hbm.registries.RegistryHelper.contain;

public class SidedItemWrapper implements IItemHandler {
    private final ItemStackHandler storage;     // 相同的存储对象
    private final Direction side;
    private final int[] inputSlots;              // 允许从该面插入的槽位
    private final int[] outputSlots;             // 允许从该面提取的槽位

    public SidedItemWrapper(ItemStackHandler handler, Direction side, int[] inputSlots, int[] outputSlots){
        this.storage = handler;
        this.side = side;
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (!contain(inputSlots, slot)) return stack;   // 门控
        return storage.insertItem(slot, stack, simulate);      // 委托
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!contain(outputSlots, slot)) return ItemStack.EMPTY;  // 门控
        return storage.extractItem(slot, amount, simulate);              // 委托
    }

    @Override
    public int getSlotLimit(int slot) {
        return storage.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return contain(inputSlots, slot) && storage.isItemValid(slot, stack);
    }

    @Override
    public int getSlots() { return storage.getSlots(); }

    @Override
    public ItemStack getStackInSlot(int slot) {
        // 可选：按面隐藏内容（如 GregTech 那样）
        if (!contain(inputSlots, slot) && !contain(outputSlots, slot))
            return ItemStack.EMPTY;
        return storage.getStackInSlot(slot);
    }
}