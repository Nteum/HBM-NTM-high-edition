package com.hbm;

import com.hbm.api.text.ILangEntry;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public enum HBMLang implements ILangEntry {
    ITEMGROUP_ITEM("itemGroup","hbm_item"),
    ITEMGROUP_BLOCK("itemGroup","hbm_block"),
    ITEMGROUP_MACHINE("itemGroup","hbm_machine"),
    ITEMGROUP_TOOL("itemGroup","hbm_tool"),
    ITEMGROUP_WEAPON("itemGroup","hbm_weapon"),
    // handoverTexts
    ENERGY("item","battery.tooltip"),
    FLUID_CAPACITY("item","fluid_capacity"),
    // blockentity name
    ELECTRIC_FURNACE("container","electric_furnace"),
    BOILER("container","boiler"),
    ELECTRIC_BOILER("container","electric_boiler"),
    NUCLEAR_BOILER("container","nuclear_boiler"),
    CHEMPLANT("container", "chemplant"),
    BARREL("container", "barrel"),
    // command
    COMMAND_DEBUG("command","debug"),
    //Redstone Control
    REDSTONE_CONTROL_DISABLED("redstone_control", "disabled"),
    REDSTONE_CONTROL_HIGH("redstone_control", "high"),
    REDSTONE_CONTROL_LOW("redstone_control", "low"),
    REDSTONE_CONTROL_PULSE("redstone_control", "pulse"),
    // upgrade
    UPGRADE_RADIUS("upgrade","radius.tooltip"),
    UPGRADE_HEALTH("upgrade","health.tooltip"),
    UPGRADE_SMELTER("upgrade","smelter.tooltip"),
    UPGRADE_SHREDDER("upgrade","shredder.tooltip"),
    UPGRADE_CENTRIFUGE("upgrade","centrifuge.tooltip"),
    UPGRADE_CRYSTALLIZER("upgrade","crystallizer.tooltip"),
    UPGRADE_SCREAM("upgrade","scream.tooltip"),
    UPGRADE_NULLIFIER("upgrade","nullifier.tooltip"),
    UPGRADE_GC_SPEED("upgrade","gc_speed.tooltip"),
    ;

    private final String key;
    HBMLang(String type, String path){
        this(Util.makeDescriptionId(type,HBM.rl(path)));
    }
    HBMLang(String key){
        this.key = key;
    }
    public @NotNull String getTranslationKey(){
        return key;
    }

}
