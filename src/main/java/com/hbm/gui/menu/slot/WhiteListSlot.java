package com.hbm.gui.menu.slot;

import com.hbm.registries.ModTags;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class WhiteListSlot extends Slot {
    Predicate<ItemStack> allowInsert;
    public WhiteListSlot(Container pContainer, int pSlot, int pX, int pY, Predicate<ItemStack> allowInsert) {
        super(pContainer, pSlot, pX, pY);
        this.allowInsert = allowInsert;
    }
    @Override
    public boolean mayPlace(ItemStack pStack) {
        return allowInsert.test(pStack);
    }
}
