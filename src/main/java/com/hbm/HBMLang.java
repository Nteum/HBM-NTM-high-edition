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
    NUCLEAR_BOILER("container","assembler"),
    ASSEMBLER("container","nuclear_boiler"),
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
    // GUI
    TOOLTIP_LEFT_TIME("gui","left_time.tooltip"),
    TOOLTIP_TANK_VOLUME("gui","volume.tooltip"),
    TOOLTIP_ENERGY("gui","stored_energy.tooltip"),
    // 大世界tooltip
    LOOKTOOLTIP_CHEMPLANT("block","chemplant.looktooltip"),
    TOOLTIP_GEIGER0("geiger","title", "GEIGER COUNTER"),
    TOOLTIP_GEIGER1("geiger","chunk_rad", "Current chunk radiation:"),
    TOOLTIP_GEIGER2("geiger","envrad", "Total environmental radiation:"),
    TOOLTIP_GEIGER3("geiger","playerrad", "Player contamination:"),
    TOOLTIP_GEIGER4("geiger","playerres", "Player resistance:"),
    // debug
    CACHED_DATA("general","cached"),
    POS_DATA("general","data.pos"),
    CHUNK_DATA("general", "data.chunk"),
    BLOCK_STATE_LOSE("debug","debugwand.msg.block_lost"),
    BLOCK_STATE_INFO("debug","debugwand.msg.block_info"),
    // general （不用于特殊用途，仅仅作为文字）
    RECIPE("general","recipe"),
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
    public boolean autoAdd = false;
    // 直接根据列表名称
    HBMLang(String content){
        this(content,true);
    }
    HBMLang(String content, boolean autoAdd){
        String[] split = this.name().toLowerCase().split("_");
        split[0] = split[0] + "." + HBM.MODID;
        this.key = Strings.join(split, ".");
        this.content = content;
        this.autoAdd = autoAdd;
    }
    HBMLang(String type, String path){
        this(type, path, "");
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
