package com.hbm.capabilities.resolver.manager;

import com.hbm.api.inventory.IInventorySlot;
import com.hbm.api.inventory.ISidedItemHandler;
import com.hbm.capabilities.holder.slot.IInventorySlotHolder;
import com.hbm.capabilities.proxy.ProxyItemHandler;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Helper class to make reading instead of having as messy generics
 */
public class ItemHandlerManager extends CapabilityHandlerManager<IInventorySlotHolder, IInventorySlot, IItemHandler, ISidedItemHandler> {

    public ItemHandlerManager(@Nullable IInventorySlotHolder holder, @NotNull ISidedItemHandler baseHandler) {
        super(holder, baseHandler, ForgeCapabilities.ITEM_HANDLER, ProxyItemHandler::new, IInventorySlotHolder::getInventorySlots);
    }
}