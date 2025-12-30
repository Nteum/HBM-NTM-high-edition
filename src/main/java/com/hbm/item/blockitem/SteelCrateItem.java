package com.hbm.item.blockitem;

import net.minecraft.world.level.block.Block;

/**
 * Block item for the steel crate that exposes the 54-slot tooltip logic.
 */
public class SteelCrateItem extends IronCrateItem {

    private static final int STEEL_SLOTS = 54;

    public SteelCrateItem(Block block, Properties properties) {
        super(block, properties, STEEL_SLOTS);
    }
}
