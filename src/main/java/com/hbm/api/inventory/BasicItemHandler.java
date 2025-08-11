package com.hbm.api.inventory;

import com.hbm.api.Mode;
import com.hbm.api.fluid.IExtendedFluidHandler;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BasicItemHandler implements IExtendedItemHandler, INBTSerializable<CompoundTag>, Container {
    NonNullList<ItemStack> items;
    List<Mode> slotModes;
    @Override
    public List<ItemStack> getItems() {
        return items;
    }

    @Override
    public Mode getMode(int slot) {
        return slotModes.get(slot);
    }

    @Override
    public CompoundTag serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        if (slot >= 0 && slot <= getSlots())
            this.items.set(slot, stack);
    }

    //====================Container================
    @Override
    public int getContainerSize() {
        return getSlots();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : this.items) {
            if (!itemStack.isEmpty())return false;
        }
        return false;
    }

    @Override
    public ItemStack getItem(int pSlot) {
        return getStackInSlot(pSlot);
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        return extractItem(pSlot, pAmount, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        return extractItem(pSlot, getStackInSlot(pSlot).getCount(), false);
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        setStackInSlot(pSlot, pStack);
    }

    @Override
    public void setChanged() {
        // blockentity.markdirty
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }
}
