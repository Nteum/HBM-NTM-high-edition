package com.hbm.item;

import com.hbm.registries.ModTags;
import net.minecraft.world.item.Item;

public class BatteryItem extends Item {
    public final long capacity;
    public BatteryItem(Properties pProperties, long capacity) {
        super(pProperties);
        this.capacity = capacity;
    }


}
