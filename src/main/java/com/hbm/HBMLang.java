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
    DIFURNACE("container","difurnace"),
    CRUCIBLE("container","crucible"),
    ELECTRIC_FURNACE("container","electric_furnace"),
    BOILER("container","boiler"),
    ELECTRIC_BOILER("container","electric_boiler"),
    NUCLEAR_BOILER("container","nuclear_boiler"),
    CHEMPLANT("container", "chemplant"),
    BARREL("container", "barrel"),
    BATTERY("container", "battery"),
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
    // fluid
    FT_GASEOUS("fluid", "gaseous.tooltip"),
    FT_GASEOUS_ART("fluid", "gaseous_art.tooltip"),
    FT_LIQUID("fluid", "liquid.tooltip"),
    FT_VISCOUS("fluid", "viscous.tooltip"),
    FT_PLASMA("fluid", "plasma.tooltip"),
    FT_AMAT("fluid", "amat.tooltip"),
    FT_LEAD_CONTAINER("fluid", "lead_container.tooltip"),
    FT_DELICIOUS("fluid", "delicious.tooltip"),
    FT_UNSIPHONABLE("fluid", "unsiphonable.tooltip"),
    FT_FLAME("fluid", "flammable.tooltip"),
    FT_VENT_RADIATION("fluid", "vent_rad.tooltip"),
    FT_COMBUSTIBLE1("fluid", "combustible.tooltip1"),
    FT_COMBUSTIBLE2("fluid", "combustible.tooltip2"),
    FT_COMBUSTIBLE3("fluid", "combustible.tooltip3"),
    FT_THERMAL_CAPACITY("fluid","thermal_capacity.tooltip1"),
    FT_EFFICIENCY("fluid","efficiency.tooltip2"),
    FT_CORROSIVE1("fluid","corrosive.tooltip1"),
    FT_CORROSIVE2("fluid","corrosive.tooltip2"),
    FT_FLAMMABLE1("fluid","flammable.tooltip1"),
    FT_FLAMMABLE2("fluid","flammable.tooltip2"),
    FT_HEATABLE1("fluid","heatable.tooltip1"),
    FT_PHEROMONE1("fluid","pheromone.tooltip1"),
    FT_PHEROMONE2("fluid","pheromone.tooltip2"),
    FT_POISON("fluid","poison.tooltip"),
    FT_POLLUTION1("fluid","pollution.tooltip1"),
    FT_POLLUTION2("fluid","pollution.tooltip2"),
    FT_POLLUTION3("fluid","pollution.tooltip3"),
    FT_PER_MB("fluid","per_mb.tooltip"),
    FT_PWRMODERATOR("fluid","pwr_moderator.tooltip"),
    FT_CORE_FLUX("fluid","core_flux.tooltip"),
    FT_RADIOACTIVE("fluid","radioactive.tooltip"),
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
