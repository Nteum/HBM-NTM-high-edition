package com.hbm.gui.menu.slot;

import com.hbm.registries.ModTags;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BatterySlot extends WhiteListSlot {
    public BatterySlot(Container pContainer, int pSlot, int pX, int pY) {
        super(pContainer, pSlot, pX, pY,stack -> stack.is(ModTags.Items.CHARGEABLE));
    }
}
