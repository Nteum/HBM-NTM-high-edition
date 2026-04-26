package com.hbm;

import com.hbm.api.text.ILangEntry;
import com.hbm.datagen.LanguageProvider;
import joptsimple.internal.Strings;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.inventory.FurnaceScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.SlabBlock;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public enum HBMLang implements ILangEntry {
    // 创造模式物品栏
    HBM_PARTS("itemGroup","hbm_parts", "NTM Resources and Parts"),
    HBM_CONTROL("itemGroup","hbm_control", "NTM Machine Items and Fuel"),
    HBM_TEMPLATE("itemGroup","hbm_template", "NTM Templates"),
    HBM_BLOCKS("itemGroup","hbm_blocks", "NTM Ores and Blocks"),
    HBM_MACHINE("itemGroup","hbm_machines", "NTM Machines"),
    HBM_NUKE("itemGroup","hbm_nuke", "NTM Bombs"),
    HBM_MISSILE("itemGroup","hbm_missile", "NTM Missiles and Satellites"),
    HBM_WEAPON("itemGroup","hbm_weapons", "NTM Weapons and Turrets"),
    HBM_CONSUMABLE("itemGroup","hbm_consumable", "NTM Consumables and Gear"),
    // 机器
    CONTAINER_PRESS("Burner Press"),
    DIFURNACE("container","difurnace"),
    CRUCIBLE("container","crucible"),
    ELECTRIC_FURNACE("container","electric_furnace"),
    BOILER("container","boiler"),
    ELECTRIC_BOILER("container","electric_boiler"),
    NUCLEAR_BOILER("container","nuclear_boiler"),
    CONTAINER_ASSEMBLER("Assembler"),
    CHEMPLANT("container", "chemplant"),
    BARREL("container", "barrel"),
    BATTERY("container", "battery"),
    CONTAINER_LAUNCHPAD("Launch Pad"),
    CONTAINER_WOOD_BURNER("Wood-Burning Generator"),
    SHREDDER("container","shredder","Shredder"),
    TOKAMAK("container", "tokamak","Tokamak Reactor"),
    RBMK("container", "rbmk","RBMK Reactor"),
    ICF("container", "icf","ICF Reactor"),
    ICF_CONTROLLER("container", "icf_controller", "ICF Laser Controller"),
    ICF_PRESS("container", "icf_press", "ICF Fuel Press"),
    RESEARCH_REACTOR("container", "research_reactor", "Research Reactor"),
    BREEDER_REACTOR("container", "reactor_breeder", "Breeder Reactor"),
    CONTAINER_SPACE_STATION_DOCKER("Space Station Docker"),
    CONTAINER_FIREBOX("Firebox"),
    CONTAINER_CRUCIBLE("Crucible"),
    CONTAINER_CONVEYOR_EXTRACTOR("Conveyor Extractor"),
    CONTAINER_CONVEYOR_INSERTER("Conveyor Inserter"),
    CONTAINER_CONVEYOR_ROUTER("Conveyor Router"),
    // GUI
    TOOLTIP_LEFT_TIME("gui","left_time.tooltip"),
    TOOLTIP_ENERGY("gui","stored_energy.tooltip"),
    GUI_TOOLTIP_PROGRESS("Progress: %s %%"),
    GUI_TOOLTIP_LEFT_TIME("Left time: %s s"),
    GUI_TOOLTIP_BURN_TIME("Burn time: %s s"),
    GUI_TOOLTIP_ENERGY("Energy: %s / %s HE"),
    GUI_TOOLTIP_FLUID("%s : %s mB"),
    GUI_TOOLTIP_NO_FLUID("No Fluid"),
    GUI_TOOLTIP_BURN_TIME_BONUS("Burn time bonus: %s"),
    GUI_TOOLTIP_BURN_HEAT_BONUS("Burn heat bonus: %s"),
    TOOLTIP_TANK_VOLUME("gui","volume.tooltip","%s : %s mB"),
    GUI_TOOLTIP_PARTIAL("%s / %s"),
    GUI_TOOLTIP_CRUCIBLE_CAPACITY("Capacity: %s mB"),
    GUI_TOOLTIP_CRUCIBLE_BUTTON1("Click this to show Recipe book."),
    GUI_TOOLTIP_CONVEYOR_ROUTER_BUTTON1("WHITELIST: Route if filter matches"),
    GUI_TOOLTIP_CONVEYOR_ROUTER_BUTTON2("BLACKLIST: Route if filter doesn't match"),
    GUI_TOOLTIP_CONVEYOR_ROUTER_BUTTON3("WILDCARD: Route if no other route is valid"),
    GUI_TOOLTIP_CONVEYOR_ROUTER_WARNING("FULL! Please set one side to WILDCARD!"),
    // Item description
    TOOLTIP_SHOW_DETAIL("Hold %s to display more info"),
    ITEM_INGOT_NEPTUNIUM_DESC("That one's my favourite!"),
    ITEM_INGOT_SCHRARANIUM_NAME_ALTER("Nikonium Ingot"),
    ITEM_BILLETGH336_DESC("Seaborgium's colleague."),
    ITEM_BILLETFLASHLEAD_DESC("The lattice decays, causing antimatter-matter$annihilation reactions, causing the release of$pions, decaying into muons, catalyzing fusion of$the nuclei, creating the new element.$Please try to keep up."),
    ITEM_INGOTASBESTOS_DESC("§o\"Filled with life, self-doubt and asbestos. That comes with the air.\"§r"),
    ITEM_INGOTCOMBINE_STEEL_DESC("*insert Civil Protection reference here*"),
    ITEM_DUST_DESC("I hate dust!"),
    ITEM_POWDER_FIRE_DESC("Used in multi purpose bombs:$Incendiary bombs are fun!"),
    ITEM_METEOR_REMOTE_DESC("Right click to summon a meteorite!"),
    BLOCK_FIREBOX_DESC("Burns solid fuel to produce heat."),
    // handoverTexts
    ENERGY("item","battery.tooltip"),
    FLUID_CAPACITY("item","fluid_capacity"),
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
    // 大世界tooltip
    LOOKTOOLTIP_CHEMPLANT("block","chemplant.looktooltip"),
    TOOLTIP_GEIGER0("geiger","title", "GEIGER COUNTER"),
    TOOLTIP_GEIGER1("geiger","chunk_rad", "Current chunk radiation:"),
    TOOLTIP_GEIGER2("geiger","envrad", "Total environmental radiation:"),
    TOOLTIP_GEIGER3("geiger","playerrad", "Player contamination:"),
    TOOLTIP_GEIGER4("geiger","playerres", "Player resistance:"),
    ITEM_MISSILE_TIER("Tier %s"),
    ITEM_MISSILE_DESC_NOTLAUNCHABLE("Not launchable!"),
    // debug
    CACHED_DATA("general","cached"),
    POS_DATA("general","data.pos"),
    CHUNK_DATA("general", "data.chunk"),
    BLOCK_STATE_LOSE("debug","debugwand.msg.block_lost"),
    BLOCK_STATE_INFO("debug","debugwand.msg.block_info"),
    // general （不用于特殊用途，仅仅作为文字）
    RECIPE("general","recipe"),
    FUEL("Fuel"),
    FUEL_CAPACITY("Fuel Capacity: %s mB"),
    EMPTY("Empty"),
    // effect
    EFFECT_RADIATION("effect","radiation"),
    // armor tooltip
    ARMOR_GEIGERSOUND("Auditory Geiger Counter"),
    ARMOR_GEIGERHUD("Built-In Geiger Counter HUD"),
    ARMOR_GLIDER("Sneak to glide"),
    ARMOR_VATS("Enemy HUD"),
    ARMOR_THERMAL("Thermal Sight"),
    ARMOR_HARDLANDING("Hard Landing"),
    ARMOR_STEPSIZE("Stepsize: %s"),
    ARMOR_DASH("Grants %s dashes"),
    ARMOR_FSB("Full Set Bonus:"),
    TOOLTIP_CHARGERATE("Charge: %s / %s"),
    ;

    private final String key;
    private String content = "";
    // 直接根据列表名称
    HBMLang(String content){
        String[] split = this.name().toLowerCase().split("_", 2);
        split[0] = split[0] + "." + HBM.MODID;
        this.key = Strings.join(split, ".");
        this.content = content;
    }
    HBMLang(String content, String descriptionId){
        String[] split = descriptionId.toLowerCase().split("_", 2);
        split[0] = split[0] + "." + HBM.MODID;
        this.key = Strings.join(split, ".");
        this.content = content;
    }
    HBMLang(String type, String path, String content){
        this.key = Util.makeDescriptionId(type,HBM.rl(path));
        this.content = content;
    }
    public @NotNull String key(){
        return key;
    }
    public String content(){
        return content;
    }
}
