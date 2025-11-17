package com.hbm.gui.menu.slot;

import com.hbm.registries.ModTags;
import net.minecraft.world.Container;

public class BladeSlot extends WhiteListSlot {
    public BladeSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y, stack -> stack.is(ModTags.Items.SHREDDER_BLADES));
    }
}
