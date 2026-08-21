package com.hbm.item.misc;

import net.minecraft.world.item.Item;

/**
 * RTG 燃料棒。
 * 移植自旧版 ItemRTGPellet，简化：仅保留热量值。
 */
public class ItemRTGPellet extends Item {
    private final short heat;

    public ItemRTGPellet(Properties pProperties, int heat) {
        super(pProperties);
        this.heat = (short) heat;
    }

    public short getHeat() {
        return heat;
    }
}
