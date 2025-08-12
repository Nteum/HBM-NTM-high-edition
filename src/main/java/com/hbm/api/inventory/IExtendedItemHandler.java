package com.hbm.api.inventory;

import com.hbm.api.Mode;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 物品处理规则
 * <br>
 * 原则：
 * 1. 与流体和能量不同，由于物品控制较为频繁，因此尽量让方块实体类继承这个接口，而不是做成独立的wrapper
 * */
public interface IExtendedItemHandler extends IItemHandlerModifiable {
    List<ItemStack> getItems();
    Mode getMode(int tank);

    @Override
    default int getSlots(){ return getItems().size();}
    /** 仅考虑slot本身是否允许输入，未考虑流体本身是否适合输入，要和isFluidValid结合使用 */
    default boolean allowInput(int slot){
        Mode mode = getMode(slot);
        return slot >= 0 && slot < getSlots() && (mode == Mode.INPUT || mode == Mode.BOTH);
    }
    default boolean allowOutput(int slot){
        Mode mode = getMode(slot);
        return slot >= 0 && slot < getSlots() && (mode == Mode.OUTPUT || mode == Mode.BOTH);
    }

    @Override
    default @NotNull ItemStack getStackInSlot(int slot){
        return slot >= 0 && slot < getSlots() ? getItems().get(slot) : ItemStack.EMPTY;
    }

    @Override
    default void setStackInSlot(int slot, @NotNull ItemStack stack){
        if (slot >= 0 && slot < getSlots()){
            getItems().set(slot, stack);
        }
    }

    @Override
    default int getSlotLimit(int slot){
        return getStackInSlot(slot).getMaxStackSize();
    }

    @Override
    default boolean isItemValid(int slot, @NotNull ItemStack stack){
//        if (allowInput(slot)) return false;
        ItemStack itemStack = getItems().get(slot);
        return itemStack.isEmpty() || ItemStack.isSameItemSameTags(itemStack, stack) && itemStack.getCount() < getSlotLimit(slot);
    }
    /**
     * 注意：返回的是尚未被插入的物品，如果插入失败则返回输入的stack
     * */
    @Override
    default @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate){
//        if (!isItemValid(slot, stack)) return stack;
        return insertNoCheck(slot,stack,simulate);
    }
    /**
     * 注意：返回的是成功拉取的物品，如果拉取失败应返回empty
     * */
    @Override
    default @NotNull ItemStack extractItem(int slot, int amount, boolean simulate){
//        if (!allowOutput(slot)) return ItemStack.EMPTY;
        return extractNoCheck(slot, amount, simulate);
    }

    default @NotNull ItemStack insertNoCheck(int slot, @NotNull ItemStack stack, boolean simulate){
        if (!isItemValid(slot, stack)) return stack;
        ItemStack beforeStack = getStackInSlot(slot);

        int toAdd = Math.min(stack.getCount(), getSlotLimit(slot) - beforeStack.getCount());
        if (!simulate) {
            if (beforeStack.isEmpty())
                beforeStack = stack;
            else
                beforeStack.grow(toAdd);
            setStackInSlot(slot, beforeStack);
        }
        return stack.getCount() - toAdd == 0 ? ItemStack.EMPTY : stack.copyWithCount(stack.getCount() - toAdd);
    }

    default @NotNull ItemStack extractNoCheck(int slot, int amount, boolean simulate){
        if (getStackInSlot(slot).isEmpty()) return ItemStack.EMPTY;
        ItemStack beforeStack = getStackInSlot(slot);

        int toSubtract = Math.min(amount, beforeStack.getCount());
        if (!simulate){
            beforeStack.shrink(toSubtract);
        }
        return toSubtract == 0 ? ItemStack.EMPTY : beforeStack.copyWithCount(toSubtract);
    }
}
