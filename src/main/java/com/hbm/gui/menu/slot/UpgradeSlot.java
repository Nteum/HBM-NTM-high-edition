package com.hbm.gui.menu.slot;

import com.hbm.registries.ModTags;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.common.Tags;

public class UpgradeSlot extends WhiteListSlot {
    public UpgradeSlot(Container pContainer, int pSlot, int pX, int pY) {
        super(pContainer, pSlot, pX, pY,stack -> stack.is(ModTags.Items.UPGRADE));
    }
}
