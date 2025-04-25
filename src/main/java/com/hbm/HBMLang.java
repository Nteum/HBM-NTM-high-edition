package com.hbm;

import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public enum HBMLang {
    ITEMGROUP_ITEM("itemGroup","hbm_item"),
    ITEMGROUP_BLOCK("itemGroup","hbm_block"),
    ITEMGROUP_MACHINE("itemGroup","hbm_machine"),
    ITEMGROUP_TOOL("itemGroup","hbm_tool"),
    ITEMGROUP_WEAPON("itemGroup","hbm_weapon"),
    // handoverTexts
    ENERGY("item","battery.tooltip"),
    ;
    private final String key;
    HBMLang(String type, String path){
        this(Util.makeDescriptionId(type,HBM.rl(path)));
    }
    HBMLang(String key){
        this.key = key;
    }
    public String getTranslationKey(){
        return key;
    }

}
