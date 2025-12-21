package com.hbm.item.tool;

import net.minecraft.world.item.Item;

public class ItemStamp extends Item {
    private StampType type;
    public ItemStamp(Properties pProperties, StampType type) {
        super(pProperties);
        this.type = type;
    }

    public StampType getType(){
        return type;
    }

    public enum StampType {
        FLAT,
        PLATE,
        WIRE,
        CIRCUIT,
        C357,
        C44,
        C50,
        C9,
        PRINTING1,
        PRINTING2,
        PRINTING3,
        PRINTING4,
        PRINTING5,
        PRINTING6,
        PRINTING7,
        PRINTING8;
    }
}
