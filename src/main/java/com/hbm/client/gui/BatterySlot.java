package com.hbm.client.gui;

import com.hbm.tag.ModTagKeys;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
//电池槽
public class BatterySlot extends Slot {
    private final ElectricFurnaceScreenHandler handler; //槽所在GUI的screen handler，目前暂时给电炉用，未来可能会给不同的screen handler
    public BatterySlot(ElectricFurnaceScreenHandler handler,Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.handler = handler;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.isIn(ModTagKeys.batteryItem);
//        return super.canInsert(stack);
    }

    @Override
    public int getMaxItemCount(ItemStack stack) {
        return 1;   //一般电池槽只能放一个电池
    }
}
