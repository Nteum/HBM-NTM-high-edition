package com.hbm.capabilities.holder.slot;

import com.hbm.api.inventory.IInventorySlot;
import com.hbm.capabilities.holder.IHolder;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IInventorySlotHolder extends IHolder {

    @NotNull
    List<IInventorySlot> getInventorySlots(@Nullable Direction side);
}