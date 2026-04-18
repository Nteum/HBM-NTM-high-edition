package com.hbm.Inventory.filter;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

// 用于让itemhandler可以检测方块进入的方向，我试图让它更加泛用一点，但contentChanged并没有被用作一个通用的接口，因此只是局限于物品运输。
public class SidedItemManager {
    ItemStackHandler handler;
//    Deque<Direction> dirToUpdate;
    Direction dirToUpdate = null;
    Map<Direction, SidedWrapper> sides = new HashMap<>();

    public SidedItemManager(ItemStackHandler handler){
        this.handler = handler;
    }

    public ItemStackHandler content(){
        return handler;
    }

    public SidedWrapper get(Direction direction){
        return sides.computeIfAbsent(direction, direction1 -> new SidedWrapper(this.handler, this, direction));
    }

    public void putUpdate(Direction direction){
//        dirToUpdate.addLast(direction);
        this.dirToUpdate = direction;
    }

    public Direction sideUpdate(){
        Direction result = this.dirToUpdate;
        this.dirToUpdate = null;
        return result;
    }

    public void disable(Direction direction){
        if (sides.containsKey(direction)){
            sides.get(direction).setSleep(true);
        }
    }

    public void enable(Direction direction){
        if (sides.containsKey(direction)){
            sides.get(direction).setSleep(false);
        }
    }

    public static class SidedWrapper extends ItemStackHandler{
        SidedItemManager parent;
        Direction direction;
        ItemStackHandler handler;
        boolean sleep = false;
        public SidedWrapper(ItemStackHandler handler, SidedItemManager parent, Direction direction){
            this.handler = handler;
            this.parent = parent;
            this.direction = direction;
        }

        @Override
        public int getSlots() {
            return handler.getSlots();
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return handler.getStackInSlot(slot);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (sleep) return stack;
            ItemStack insertResult = handler.insertItem(slot, stack, simulate);
            if (!simulate && insertResult.getCount() != stack.getCount()){
                onContentsChanged(slot);
            }
            return insertResult;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (sleep) return ItemStack.EMPTY;
            ItemStack extractResult = handler.extractItem(slot, amount, simulate);
            if (!simulate && !extractResult.isEmpty()){
                onContentsChanged(slot);
            }
            return extractResult;
        }

        @Override
        public int getSlotLimit(int slot) {
            return handler.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return !sleep && handler.isItemValid(slot, stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            parent.putUpdate(direction);
        }

        public void setSleep(boolean sleep){
            this.sleep = sleep;
        }
    }
}
