package com.hbm.client.gui;

import com.hbm.item.machine.MachineUpgrade;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
//升级槽
public class UpgradeSlot extends Slot {
    public UpgradeSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.getItem() instanceof MachineUpgrade;
    }

    @Override
    public int getMaxItemCount(ItemStack stack) {
        return 1;
    }
}
