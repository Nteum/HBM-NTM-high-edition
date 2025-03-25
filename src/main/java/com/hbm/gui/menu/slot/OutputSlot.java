package com.hbm.gui.menu.slot;

import com.hbm.registries.ModTags;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
//输出结果的slot，禁止放入任何物品。
public class OutputSlot extends Slot {
    public OutputSlot(Container pContainer, int pSlot, int pX, int pY) {
        super(pContainer, pSlot, pX, pY);
    }
    @Override
    public boolean mayPlace(ItemStack pStack) {
        return false;
    }
}
