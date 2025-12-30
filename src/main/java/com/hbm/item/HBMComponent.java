package com.hbm.item;

import com.hbm.datagen.LanguageProvider;
import com.hbm.item.env.ItemEggGlyphid;
import com.hbm.item.env.ItemEggGlyphidToBirth;
import com.hbm.item.misc.ItemCircuit;
import com.hbm.item.tool.BatteryItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

//一些零部件
public class HBMComponent {
    // 记录所用矿物的注册物品，进行批量处理。
    private static final List<RegistryObject<Item>> matherialList = new ArrayList<>();
    // 线圈等物品，明明逻辑是 物品类型 + 材料 ，游戏内的名称需要颠倒过来
    private static final List<RegistryObject<Item>> partList = new ArrayList<>();
    private static boolean initialized = false;
    public static RegistryObject<Item> LASER_CRYSTAL_DIGAMMA;
    //工业元件
    public static RegistryObject<Item> BATTERY_CREATIVE;
    public static RegistryObject<Item> BATTERY_GENERIC;
    public static RegistryObject<Item> BATTERY_ADVANCED;
    public static RegistryObject<Item> BATTERY_LITHIUM;

    public static RegistryObject<Item> EGG_GLYPHID;
    public static RegistryObject<Item> EGG_GLYPHID_TO_BIRTH;
    //==================元件 part======================
    public static RegistryObject<Item> CIRCUIT_BASIC;
    public static RegistryObject<Item> WIRE_FINE_ALUMINIUM;
    public static RegistryObject<Item> SHELL;
    public static RegistryObject<Item> DUCT_TAPE;
    //====================材料=============================
    public static RegistryObject<Item> STEEL_INGOT;
    public static RegistryObject<Item> STEEL_DUST;
    public static RegistryObject<Item> STEEL_PLATE;
    public static RegistryObject<Item> STEEL_SMALL_DUST;
    public static RegistryObject<Item> PLATE_TITANIUM;
    public static RegistryObject<Item> INGOT_TITANIUM;
    public static RegistryObject<Item> INGOT_COBALT;
    public static RegistryObject<Item> INGOT_STARMETAL;
    public static RegistryObject<Item> INGOT_BISMUTH;
    public static RegistryObject<Item> INGOT_ASBESTOS;
    public static RegistryObject<Item>     BALL_RESIN;
    public static RegistryObject<Item>     INGOT_DURA_STEEL;
    public static RegistryObject<Item>     INGOT_POLYMER;
    public static RegistryObject<Item>     INGOT_BAKELITE;
    public static RegistryObject<Item>     INGOT_BIORUBBER;
    public static RegistryObject<Item>     INGOT_RUBBER;
    public static RegistryObject<Item>     INGOT_PC;
    public static RegistryObject<Item>     INGOT_PVC;
    public static RegistryObject<Item>     INGOT_DESH;
    public static RegistryObject<Item>     NUGGET_DESH;
    public static RegistryObject<Item>     INGOT_DINEUTRONIUM;
    public static RegistryObject<Item>     NUGGET_DINEUTRONIUM;
    public static RegistryObject<Item>     POWDER_DINEUTRONIUM;
    public static RegistryObject<Item>     INGOT_GUNMETAL;
    public static RegistryObject<Item>     PLATE_GUNMETAL;
    public static RegistryObject<Item>     INGOT_WEAPONSTEEL;
    public static RegistryObject<Item>     PLATE_WEAPONSTEEL;
    public static RegistryObject<Item>     INGOT_SATURNITE;
    public static RegistryObject<Item>     PLATE_SATURNITE;
    public static RegistryObject<Item>     INGOT_FERROURANIUM;
    public static RegistryObject<Item>     INGOT_FIBERGLASS;
    public static RegistryObject<Item>     POWDER_ASBESTOS;
    public static RegistryObject<Item>     INGOT_ELECTRONIUM;
    public static RegistryObject<Item>     NUGGET_ZIRCONIUM;
    public static RegistryObject<Item>     NUGGET_MERCURY;
    public static RegistryObject<Item>     INGOT_MERCURY;
    public static RegistryObject<Item>     BOTTLE_MERCURY;
    public static RegistryObject<Item>     INGOT_CALCIUM;
    public static RegistryObject<Item>     POWDER_CALCIUM;
    public static RegistryObject<Item>     INGOT_CADMIUM;
    public static RegistryObject<Item>     POWDER_CADMIUM;
    public static RegistryObject<Item>     POWDER_BISMUTH;
    public static RegistryObject<Item>     INGOT_MUD;
    public static RegistryObject<Item>     INGOT_CFT;
    public static RegistryObject<Item>     INGOT_TH232;
    public static RegistryObject<Item>     INGOT_URANIUM;
    public static RegistryObject<Item>     INGOT_U233;
    public static RegistryObject<Item>     INGOT_U235;
    public static RegistryObject<Item>     INGOT_U238;
    public static RegistryObject<Item>     INGOT_U238M2;
    public static RegistryObject<Item>     INGOT_PLUTONIUM;
    public static RegistryObject<Item>     INGOT_PU238;
    public static RegistryObject<Item>     INGOT_PU239;
    public static RegistryObject<Item>     INGOT_PU240;
    public static RegistryObject<Item>     INGOT_PU241;
    public static RegistryObject<Item>     INGOT_PU_MIX;
    public static RegistryObject<Item>     INGOT_AM241;
    public static RegistryObject<Item>     INGOT_AM242;
    public static RegistryObject<Item>     INGOT_AM_MIX;
    public static RegistryObject<Item>     INGOT_NEPTUNIUM;
    public static RegistryObject<Item>     INGOT_POLONIUM;
    public static RegistryObject<Item>     INGOT_TECHNETIUM;
    public static RegistryObject<Item>     INGOT_CO60;
    public static RegistryObject<Item>     INGOT_SR90;
    public static RegistryObject<Item>     INGOT_AU198;
    public static RegistryObject<Item>     INGOT_PB209;
    public static RegistryObject<Item>     INGOT_RA226;
    public static RegistryObject<Item>     INGOT_BORON;
    public static RegistryObject<Item>     INGOT_GRAPHITE;
    public static RegistryObject<Item>     INGOT_FIREBRICK;
    public static RegistryObject<Item>     INGOT_SMORE;
    public static RegistryObject<Item>     SULFUR;
    public static RegistryObject<Item>     NITRA;
    public static RegistryObject<Item>     NITRA_SMALL;

    public static RegistryObject<Item>     INGOT_URANIUM_FUEL;
    public static RegistryObject<Item>     INGOT_PLUTONIUM_FUEL;
    public static RegistryObject<Item>     INGOT_NEPTUNIUM_FUEL;
    public static RegistryObject<Item>     INGOT_MOX_FUEL;
    public static RegistryObject<Item>     INGOT_AMERICIUM_FUEL;
    public static RegistryObject<Item>     INGOT_SCHRABIDIUM_FUEL;
    public static RegistryObject<Item>     INGOT_THORIUM_FUEL;
    public static RegistryObject<Item>     NUGGET_URANIUM_FUEL;
    public static RegistryObject<Item>     NUGGET_THORIUM_FUEL;
    public static RegistryObject<Item>     NUGGET_PLUTONIUM_FUEL;
    public static RegistryObject<Item>     NUGGET_NEPTUNIUM_FUEL;
    public static RegistryObject<Item>     NUGGET_MOX_FUEL;
    public static RegistryObject<Item>     NUGGET_AMERICIUM_FUEL;
    public static RegistryObject<Item>     NUGGET_SCHRABIDIUM_FUEL;
    public static RegistryObject<Item>     INGOT_ADVANCED_ALLOY;
    public static RegistryObject<Item>     INGOT_TCALLOY;
    public static RegistryObject<Item>     INGOT_CDALLOY;
    public static RegistryObject<Item>     INGOT_BISMUTH_BRONZE;
    public static RegistryObject<Item>     INGOT_ARSENIC_BRONZE;
    public static RegistryObject<Item>     INGOT_BSCCO;

    public static RegistryObject<Item>     NITER;
    public static RegistryObject<Item>     INGOT_COPPER;
    public static RegistryObject<Item>     INGOT_RED_COPPER;
    public static RegistryObject<Item>     INGOT_TUNGSTEN;
    public static RegistryObject<Item>     INGOT_ALUMINIUM;
    public static RegistryObject<Item>     FLUORITE;
    public static RegistryObject<Item>     INGOT_BERYLLIUM;
    public static RegistryObject<Item>     INGOT_STEEL;
    public static RegistryObject<Item>     PLATE_STEEL;
    public static RegistryObject<Item>     PLATE_IRON;
    public static RegistryObject<Item>     INGOT_LEAD;
    public static RegistryObject<Item>     PLATE_LEAD;
    public static RegistryObject<Item>     PLATE_DURA_STEEL;
    public static RegistryObject<Item>     INGOT_SCHRARANIUM;
    public static RegistryObject<Item>     INGOT_SCHRABIDIUM;
    public static RegistryObject<Item>     INGOT_SCHRABIDATE;
    public static RegistryObject<Item>     PLATE_SCHRABIDIUM;
    public static RegistryObject<Item>     PLATE_COPPER;
    public static RegistryObject<Item>     PLATE_GOLD;
    public static RegistryObject<Item>     PLATE_ADVANCED_ALLOY;
    public static RegistryObject<Item>     LITHIUM;
    public static RegistryObject<Item>     INGOT_ZIRCONIUM;
    public static RegistryObject<Item>     INGOT_SEMTEX;
    public static RegistryObject<Item>     INGOT_C4;
    public static RegistryObject<Item>     INGOT_PHOSPHORUS;
    public static RegistryObject<Item>     COIL_ADVANCED_ALLOY;
    public static RegistryObject<Item>     COIL_ADVANCED_TORUS;
    public static RegistryObject<Item>     INGOT_MAGNETIZED_TUNGSTEN;
    public static RegistryObject<Item>     INGOT_COMBINE_STEEL;
    public static RegistryObject<Item>     PLATE_MIXED;
    public static RegistryObject<Item>     PLATE_PAA;
    public static RegistryObject<Item>     PIPES_STEEL;
    public static RegistryObject<Item>     DRILL_TITANIUM;
    public static RegistryObject<Item>     PLATE_DALEKANIUM;
    public static RegistryObject<Item>     PLATE_EUPHEMIUM;
    public static RegistryObject<Item>     BOLT;
    public static RegistryObject<Item>     BOLT_SPIKE;
    public static RegistryObject<Item>     PLATE_POLYMER;
    public static RegistryObject<Item>     PLATE_KEVLAR;
    public static RegistryObject<Item>     PLATE_DINEUTRONIUM;
    public static RegistryObject<Item>     PLATE_DESH;
    public static RegistryObject<Item>     PLATE_BISMUTH;
    public static RegistryObject<Item>     INGOT_SOLINIUM;
    public static RegistryObject<Item>     NUGGET_SOLINIUM;
    public static RegistryObject<Item>     PHOTO_PANEL;
    public static RegistryObject<Item>     SAT_BASE;
    public static RegistryObject<Item>     THRUSTER_NUCLEAR;
    public static RegistryObject<Item>     SAFETY_FUSE;
    public static RegistryObject<Item>     PART_GENERIC;
    public static RegistryObject<Item>     ITEM_EXPENSIVE;
    public static RegistryObject<Item>     ITEM_SECRET;
    public static RegistryObject<Item>     INGOT_METAL;
    public static RegistryObject<Item>     CHEMICAL_DYE;
    public static RegistryObject<Item>     CRAYON;

    public static RegistryObject<Item>     BILLET_URANIUM;
    public static RegistryObject<Item>     BILLET_U233;
    public static RegistryObject<Item>     BILLET_U235;
    public static RegistryObject<Item>     BILLET_U238;
    public static RegistryObject<Item>     BILLET_TH232;
    public static RegistryObject<Item>     BILLET_PLUTONIUM;
    public static RegistryObject<Item>     BILLET_PU238;
    public static RegistryObject<Item>     BILLET_PU239;
    public static RegistryObject<Item>     BILLET_PU240;
    public static RegistryObject<Item>     BILLET_PU241;
    public static RegistryObject<Item>     BILLET_PU_MIX;
    public static RegistryObject<Item>     BILLET_AM241;
    public static RegistryObject<Item>     BILLET_AM242;
    public static RegistryObject<Item>     BILLET_AM_MIX;
    public static RegistryObject<Item>     BILLET_NEPTUNIUM;
    public static RegistryObject<Item>     BILLET_POLONIUM;
    public static RegistryObject<Item>     BILLET_TECHNETIUM;
    public static RegistryObject<Item>     BILLET_COBALT;
    public static RegistryObject<Item>     BILLET_CO60;
    public static RegistryObject<Item>     BILLET_SR90;
    public static RegistryObject<Item>     BILLET_AU198;
    public static RegistryObject<Item>     BILLET_PB209;
    public static RegistryObject<Item>     BILLET_RA226;
    public static RegistryObject<Item>     BILLET_ACTINIUM;
    public static RegistryObject<Item>     BILLET_SCHRABIDIUM;
    public static RegistryObject<Item>     BILLET_SOLINIUM;
    public static RegistryObject<Item>     BILLET_GH336;
    public static RegistryObject<Item>     BILLET_AUSTRALIUM;
    public static RegistryObject<Item>     BILLET_AUSTRALIUM_LESSER;
    public static RegistryObject<Item>     BILLET_AUSTRALIUM_GREATER;
    public static RegistryObject<Item>     BILLET_URANIUM_FUEL;
    public static RegistryObject<Item>     BILLET_THORIUM_FUEL;
    public static RegistryObject<Item>     BILLET_PLUTONIUM_FUEL;
    public static RegistryObject<Item>     BILLET_NEPTUNIUM_FUEL;
    public static RegistryObject<Item>     BILLET_MOX_FUEL;
    public static RegistryObject<Item>     BILLET_AMERICIUM_FUEL;
    public static RegistryObject<Item>     BILLET_LES;
    public static RegistryObject<Item>     BILLET_SCHRABIDIUM_FUEL;
    public static RegistryObject<Item>     BILLET_HES;
    public static RegistryObject<Item>     BILLET_PO210BE;
    public static RegistryObject<Item>     BILLET_RA226BE;
    public static RegistryObject<Item>     BILLET_PU238BE;
    public static RegistryObject<Item>     BILLET_BERYLLIUM;
    public static RegistryObject<Item>     BILLET_BISMUTH;
    public static RegistryObject<Item>     BILLET_ZIRCONIUM;
    public static RegistryObject<Item>     BILLET_YHARONITE;
    public static RegistryObject<Item>     BILLET_BALEFIRE_GOLD;
    public static RegistryObject<Item>     BILLET_FLASHLEAD;
    public static RegistryObject<Item>     BILLET_ZFB_BISMUTH;
    public static RegistryObject<Item>     BILLET_ZFB_PU241;
    public static RegistryObject<Item>     BILLET_ZFB_AM_MIX;
    public static RegistryObject<Item>     BILLET_NUCLEAR_WASTE;

    public static RegistryObject<Item>     ORE_BYPRODUCT;

    public static RegistryObject<Item>     ORE_BEDROCK;
    public static RegistryObject<Item>     ORE_CENTRIFUGED;
    public static RegistryObject<Item>     ORE_CLEANED;
    public static RegistryObject<Item>     ORE_SEPARATED;
    public static RegistryObject<Item>     ORE_PURIFIED;
    public static RegistryObject<Item>     ORE_NITRATED;
    public static RegistryObject<Item>     ORE_NITROCRYSTALLINE;
    public static RegistryObject<Item>     ORE_DEEPCLEANED;
    public static RegistryObject<Item>     ORE_SEARED;
    public static RegistryObject<Item>     ORE_ENRICHED;
    public static RegistryObject<Item>     BEDROCK_ORE_BASE;
    public static RegistryObject<Item>     BEDROCK_ORE;
    public static RegistryObject<Item>     BEDROCK_ORE_FRAGMENT;

    public static RegistryObject<Item>     INGOT_LANTHANIUM;
    public static RegistryObject<Item>     INGOT_ACTINIUM;

    public static RegistryObject<Item>     INGOT_METEORITE;
    public static RegistryObject<Item>     INGOT_METEORITE_FORGED;
    public static RegistryObject<Item>     BLADE_METEORITE;
    public static RegistryObject<Item>     INGOT_STEEL_DUSTED;
    public static RegistryObject<Item>     INGOT_CHAINSTEEL;

    public static RegistryObject<Item>     PLATE_ARMOR_TITANIUM;
    public static RegistryObject<Item>     PLATE_ARMOR_AJR;
    public static RegistryObject<Item>     PLATE_ARMOR_HEV;
    public static RegistryObject<Item>     PLATE_ARMOR_LUNAR;
    public static RegistryObject<Item>     PLATE_ARMOR_FAU;
    public static RegistryObject<Item>     PLATE_ARMOR_DNT;

    public static RegistryObject<Item>     OIL_TAR;
    public static RegistryObject<Item>     SOLID_FUEL;
    public static RegistryObject<Item>     SOLID_FUEL_PRESTO;
    public static RegistryObject<Item>     SOLID_FUEL_PRESTO_TRIPLET;
    public static RegistryObject<Item>     SOLID_FUEL_BF;
    public static RegistryObject<Item>     SOLID_FUEL_PRESTO_BF;
    public static RegistryObject<Item>     SOLID_FUEL_PRESTO_TRIPLET_BF;
    public static RegistryObject<Item>     ROCKET_FUEL;
    public static RegistryObject<Item>     COKE;
    public static RegistryObject<Item>     LIGNITE;
    public static RegistryObject<Item>     BRIQUETTE;
    public static RegistryObject<Item>     POWDER_LIGNITE;
    public static RegistryObject<Item>     COAL_INFERNAL;
    public static RegistryObject<Item>     CINNEBAR;
    public static RegistryObject<Item>     POWDER_ASH;
    public static RegistryObject<Item>     POWDER_LIMESTONE;
    public static RegistryObject<Item>     POWDER_CEMENT;

    public static RegistryObject<Item>     INGOT_GH336;
    public static RegistryObject<Item>     NUGGET_GH336;

    public static RegistryObject<Item>     INGOT_AUSTRALIUM;
    public static RegistryObject<Item>     NUGGET_AUSTRALIUM;
    public static RegistryObject<Item>     NUGGET_AUSTRALIUM_LESSER;
    public static RegistryObject<Item>     NUGGET_AUSTRALIUM_GREATER;

    public static RegistryObject<Item>     NUGGET_TH232;
    public static RegistryObject<Item>     NUGGET_URANIUM;
    public static RegistryObject<Item>     NUGGET_U233;
    public static RegistryObject<Item>     NUGGET_U235;
    public static RegistryObject<Item>     NUGGET_U238;
    public static RegistryObject<Item>     NUGGET_PLUTONIUM;
    public static RegistryObject<Item>     NUGGET_PU238;
    public static RegistryObject<Item>     NUGGET_PU239;
    public static RegistryObject<Item>     NUGGET_PU240;
    public static RegistryObject<Item>     NUGGET_PU241;
    public static RegistryObject<Item>     NUGGET_PU_MIX;
    public static RegistryObject<Item>     NUGGET_AM241;
    public static RegistryObject<Item>     NUGGET_AM242;
    public static RegistryObject<Item>     NUGGET_AM_MIX;
    public static RegistryObject<Item>     NUGGET_NEPTUNIUM;
    public static RegistryObject<Item>     NUGGET_POLONIUM;
    public static RegistryObject<Item>     NUGGET_TECHNETIUM;
    public static RegistryObject<Item>     NUGGET_COBALT;
    public static RegistryObject<Item>     NUGGET_CO60;
    public static RegistryObject<Item>     NUGGET_SR90;
    public static RegistryObject<Item>     NUGGET_AU198;
    public static RegistryObject<Item>     NUGGET_PB209;
    public static RegistryObject<Item>     NUGGET_RA226;
    public static RegistryObject<Item>     NUGGET_ACTINIUM;
    public static RegistryObject<Item>     PLATE_ALUMINIUM;
    public static RegistryObject<Item>     NEUTRON_REFLECTOR;
    public static RegistryObject<Item>     NUGGET_LEAD;
    public static RegistryObject<Item>     NUGGET_BISMUTH;
    public static RegistryObject<Item>     INGOT_ARSENIC;
    public static RegistryObject<Item>     NUGGET_ARSENIC;
    public static RegistryObject<Item>     INGOT_TANTALIUM;
    public static RegistryObject<Item>     NUGGET_TANTALIUM;
    public static RegistryObject<Item>     INGOT_SILICON;
    public static RegistryObject<Item>     BILLET_SILICON;
    public static RegistryObject<Item>     NUGGET_SILICON;
    public static RegistryObject<Item>     INGOT_NIOBIUM;
    public static RegistryObject<Item>     NUGGET_NIOBIUM;
    public static RegistryObject<Item>     INGOT_OSMIRIDIUM;
    public static RegistryObject<Item>     NUGGET_OSMIRIDIUM;
    public static RegistryObject<Item>     NUGGET_SCHRABIDIUM;
    public static RegistryObject<Item>     NUGGET_BERYLLIUM;
    public static RegistryObject<Item>     HAZMAT_CLOTH;
    public static RegistryObject<Item>     HAZMAT_CLOTH_RED;
    public static RegistryObject<Item>     HAZMAT_CLOTH_GREY;
    public static RegistryObject<Item>     ASBESTOS_CLOTH;
    public static RegistryObject<Item>     RAG;
    public static RegistryObject<Item>     RAG_DAMP;
    public static RegistryObject<Item>     RAG_PISS;
    public static RegistryObject<Item>     FILTER_COAL;
    public static RegistryObject<Item>     INGOT_HES;
    public static RegistryObject<Item>     INGOT_LES;
    public static RegistryObject<Item>     NUGGET_HES;
    public static RegistryObject<Item>     NUGGET_LES;
    public static RegistryObject<Item>     PLATE_COMBINE_STEEL;

    public static RegistryObject<Item>     CRYSTAL_COAL;
    public static RegistryObject<Item>     CRYSTAL_IRON;
    public static RegistryObject<Item>     CRYSTAL_GOLD;
    public static RegistryObject<Item>     CRYSTAL_REDSTONE;
    public static RegistryObject<Item>     CRYSTAL_LAPIS;
    public static RegistryObject<Item>     CRYSTAL_DIAMOND;
    public static RegistryObject<Item>     CRYSTAL_URANIUM;
    public static RegistryObject<Item>     CRYSTAL_THORIUM;
    public static RegistryObject<Item>     CRYSTAL_PLUTONIUM;
    public static RegistryObject<Item>     CRYSTAL_TITANIUM;
    public static RegistryObject<Item>     CRYSTAL_SULFUR;
    public static RegistryObject<Item>     CRYSTAL_NITER;
    public static RegistryObject<Item>     CRYSTAL_COPPER;
    public static RegistryObject<Item>     CRYSTAL_TUNGSTEN;
    public static RegistryObject<Item>     CRYSTAL_ALUMINIUM;
    public static RegistryObject<Item>     CRYSTAL_FLUORITE;
    public static RegistryObject<Item>     CRYSTAL_BERYLLIUM;
    public static RegistryObject<Item>     CRYSTAL_LEAD;
    public static RegistryObject<Item>     CRYSTAL_SCHRARANIUM;
    public static RegistryObject<Item>     CRYSTAL_SCHRABIDIUM;
    public static RegistryObject<Item>     CRYSTAL_RARE;
    public static RegistryObject<Item>     CRYSTAL_PHOSPHORUS;
    public static RegistryObject<Item>     CRYSTAL_LITHIUM;
    public static RegistryObject<Item>     CRYSTAL_COBALT;
    public static RegistryObject<Item>     CRYSTAL_STARMETAL;
    public static RegistryObject<Item>     CRYSTAL_CINNEBAR;
    public static RegistryObject<Item>     CRYSTAL_TRIXITE;
    public static RegistryObject<Item>     CRYSTAL_OSMIRIDIUM;
    public static RegistryObject<Item>     GEM_SODALITE;
    public static RegistryObject<Item>     GEM_TANTALIUM;
    public static RegistryObject<Item>     GEM_VOLCANIC;
    public static RegistryObject<Item>     GEM_RAD;
    public static RegistryObject<Item>     GEM_ALEXANDRITE;

    public static RegistryObject<Item>     POWDER_LEAD;
    public static RegistryObject<Item>     POWDER_TANTALIUM;
    public static RegistryObject<Item>     POWDER_NEPTUNIUM;
    public static RegistryObject<Item>     POWDER_POLONIUM;
    public static RegistryObject<Item>     POWDER_CO60;
    public static RegistryObject<Item>     POWDER_SR90;
    public static RegistryObject<Item>     POWDER_SR90_TINY;
    public static RegistryObject<Item>     POWDER_I131;
    public static RegistryObject<Item>     POWDER_I131_TINY;
    public static RegistryObject<Item>     POWDER_XE135;
    public static RegistryObject<Item>     POWDER_XE135_TINY;
    public static RegistryObject<Item>     POWDER_CS137;
    public static RegistryObject<Item>     POWDER_CS137_TINY;
    public static RegistryObject<Item>     POWDER_AU198;
    public static RegistryObject<Item>     POWDER_RA226;
    public static RegistryObject<Item>     POWDER_AT209;
    public static RegistryObject<Item>     POWDER_SCHRABIDIUM;
    public static RegistryObject<Item>     POWDER_SCHRABIDATE;
    public static RegistryObject<Item>     POWDER_ALUMINIUM;
    public static RegistryObject<Item>     POWDER_BERYLLIUM;
    public static RegistryObject<Item>     POWDER_COPPER;
    public static RegistryObject<Item>     POWDER_GOLD;
    public static RegistryObject<Item>     POWDER_IRON;
    public static RegistryObject<Item>     POWDER_TITANIUM;
    public static RegistryObject<Item>     POWDER_TUNGSTEN;
    public static RegistryObject<Item>     POWDER_URANIUM;
    public static RegistryObject<Item>     POWDER_PLUTONIUM;
    public static RegistryObject<Item>     DUST;
    public static RegistryObject<Item>     DUST_TINY;
    public static RegistryObject<Item>     FALLOUT;
    public static RegistryObject<Item>     POWDER_ADVANCED_ALLOY;
    public static RegistryObject<Item>     POWDER_TCALLOY;
    public static RegistryObject<Item>     POWDER_COAL;
    public static RegistryObject<Item>     POWDER_COAL_TINY;
    public static RegistryObject<Item>     POWDER_COMBINE_STEEL;
    public static RegistryObject<Item>     POWDER_DIAMOND;
    public static RegistryObject<Item>     POWDER_EMERALD;
    public static RegistryObject<Item>     POWDER_LAPIS;
    public static RegistryObject<Item>     POWDER_QUARTZ;
    public static RegistryObject<Item>     POWDER_MAGNETIZED_TUNGSTEN;
    public static RegistryObject<Item>     POWDER_CHLOROPHYTE;
    public static RegistryObject<Item>     POWDER_RED_COPPER;
    public static RegistryObject<Item>     POWDER_STEEL;
    public static RegistryObject<Item>     POWDER_LITHIUM;
    public static RegistryObject<Item>     POWDER_ZIRCONIUM;
    public static RegistryObject<Item>     POWDER_SODIUM;
    public static RegistryObject<Item>     POWDER_POWER;
    public static RegistryObject<Item>     POWDER_IODINE;
    public static RegistryObject<Item>     POWDER_THORIUM;
    public static RegistryObject<Item>     POWDER_NEODYMIUM;
    public static RegistryObject<Item>     POWDER_ASTATINE;
    public static RegistryObject<Item>     POWDER_CAESIUM;
    public static RegistryObject<Item>     POWDER_AUSTRALIUM;
    public static RegistryObject<Item>     POWDER_STRONTIUM;
    public static RegistryObject<Item>     POWDER_COBALT;
    public static RegistryObject<Item>     POWDER_BROMINE;
    public static RegistryObject<Item>     POWDER_NIOBIUM;
    public static RegistryObject<Item>     POWDER_TENNESSINE;
    public static RegistryObject<Item>     POWDER_CERIUM;
    public static RegistryObject<Item>     POWDER_DURA_STEEL;
    public static RegistryObject<Item>     POWDER_POLYMER;
    public static RegistryObject<Item>     POWDER_BAKELITE;
    public static RegistryObject<Item>     POWDER_EUPHEMIUM;
    public static RegistryObject<Item>     POWDER_METEORITE;
    public static RegistryObject<Item>     POWDER_LANTHANIUM;
    public static RegistryObject<Item>     POWDER_ACTINIUM;
    public static RegistryObject<Item>     POWDER_BORON;
    public static RegistryObject<Item>     POWDER_SEMTEX_MIX;
    public static RegistryObject<Item>     POWDER_DESH_MIX;
    public static RegistryObject<Item>     POWDER_DESH_READY;
    public static RegistryObject<Item>     POWDER_NITAN_MIX;
    public static RegistryObject<Item>     POWDER_SPARK_MIX;
    public static RegistryObject<Item>     POWDER_DESH;
    public static RegistryObject<Item>     POWDER_STEEL_TINY;
    public static RegistryObject<Item>     POWDER_LITHIUM_TINY;
    public static RegistryObject<Item>     POWDER_NEODYMIUM_TINY;
    public static RegistryObject<Item>     POWDER_COBALT_TINY;
    public static RegistryObject<Item>     POWDER_NIOBIUM_TINY;
    public static RegistryObject<Item>     POWDER_CERIUM_TINY;
    public static RegistryObject<Item>     POWDER_LANTHANIUM_TINY;
    public static RegistryObject<Item>     POWDER_ACTINIUM_TINY;
    public static RegistryObject<Item>     POWDER_BORON_TINY;
    public static RegistryObject<Item>     POWDER_METEORITE_TINY;
    public static RegistryObject<Item>     POWDER_YELLOWCAKE;
    public static RegistryObject<Item>     POWDER_MAGIC;
    public static RegistryObject<Item>     POWDER_BALEFIRE;
    public static RegistryObject<Item>     POWDER_SAWDUST;
    public static RegistryObject<Item>     POWDER_FLUX;
    public static RegistryObject<Item>     POWDER_FERTILIZER;
    public static RegistryObject<Item>     POWDER_COLTAN_ORE;
    public static RegistryObject<Item>     POWDER_COLTAN;
    public static RegistryObject<Item>     POWDER_TEKTITE;
    public static RegistryObject<Item>     POWDER_PALEOGENITE;
    public static RegistryObject<Item>     POWDER_PALEOGENITE_TINY;
    public static RegistryObject<Item>     POWDER_IMPURE_OSMIRIDIUM;
    public static RegistryObject<Item>     POWDER_BORAX;
    public static RegistryObject<Item>     POWDER_CHLOROCALCITE;
    public static RegistryObject<Item>     POWDER_MOLYSITE;

    public static RegistryObject<Item>     FRAGMENT_NEODYMIUM;
    public static RegistryObject<Item>     FRAGMENT_COBALT;
    public static RegistryObject<Item>     FRAGMENT_NIOBIUM;
    public static RegistryObject<Item>     FRAGMENT_CERIUM;
    public static RegistryObject<Item>     FRAGMENT_LANTHANIUM;
    public static RegistryObject<Item>     FRAGMENT_ACTINIUM;
    public static RegistryObject<Item>     FRAGMENT_BORON;
    public static RegistryObject<Item>     FRAGMENT_METEORITE;
    public static RegistryObject<Item>     FRAGMENT_COLTAN;
    public static RegistryObject<Item>     CHUNK_ORE;

    public static RegistryObject<Item>     BIOMASS;
    public static RegistryObject<Item>     BIOMASS_COMPRESSED;
    public static RegistryObject<Item>     BIO_WAFER;
    public static RegistryObject<Item>     PLANT_ITEM;

    public static RegistryObject<Item>     COIL_COPPER;
    public static RegistryObject<Item>     COIL_COPPER_TORUS;
    public static RegistryObject<Item>     COIL_TUNGSTEN;
    public static RegistryObject<Item>     TANK_STEEL;
    public static RegistryObject<Item>     MOTOR;
    public static RegistryObject<Item>     MOTOR_DESH;
    public static RegistryObject<Item>     MOTOR_BISMUTH;
    public static RegistryObject<Item>     CENTRIFUGE_ELEMENT;
    public static RegistryObject<Item>     REACTOR_CORE;
    public static RegistryObject<Item>     RTG_UNIT;
    public static RegistryObject<Item>     COIL_MAGNETIZED_TUNGSTEN;
    public static RegistryObject<Item>     COIL_GOLD;
    public static RegistryObject<Item>     COIL_GOLD_TORUS;
    public static RegistryObject<Item>     CHLORINE_PINWHEEL;
    public static RegistryObject<Item>     RING_STARMETAL;
    public static RegistryObject<Item>     FLYWHEEL_BERYLLIUM;
    public static RegistryObject<Item>     DEUTERIUM_FILTER;
    public static RegistryObject<Item>     PARTS_LEGENDARY;

    public static RegistryObject<Item>     GEAR_LARGE;
    public static RegistryObject<Item>     SAWBLADE;

    public static RegistryObject<Item>     PIPE;
    public static RegistryObject<Item>     FINS_FLAT;
    public static RegistryObject<Item>     FINS_SMALL_STEEL;
    public static RegistryObject<Item>     FINS_BIG_STEEL;
    public static RegistryObject<Item>     FINS_TRI_STEEL;
    public static RegistryObject<Item>     FINS_QUAD_TITANIUM;
    public static RegistryObject<Item>     SPHERE_STEEL;
    public static RegistryObject<Item>     PEDESTAL_STEEL;
    public static RegistryObject<Item>     DYSFUNCTIONAL_REACTOR;
    public static RegistryObject<Item>     BLADE_TITANIUM;
    public static RegistryObject<Item>     TURBINE_TITANIUM;
    public static RegistryObject<Item>     BLADE_TUNGSTEN;
    public static RegistryObject<Item>     TURBINE_TUNGSTEN;

    public static RegistryObject<Item>     TOOTHPICKS;
    public static RegistryObject<Item>     DUCTTAPE;
    public static RegistryObject<Item>     CATALYST_CLAY;

    public static RegistryObject<Item>     WARHEAD_GENERIC_SMALL;
    public static RegistryObject<Item>     WARHEAD_GENERIC_MEDIUM;
    public static RegistryObject<Item>     WARHEAD_GENERIC_LARGE;
    public static RegistryObject<Item>     WARHEAD_INCENDIARY_SMALL;
    public static RegistryObject<Item>     WARHEAD_INCENDIARY_MEDIUM;
    public static RegistryObject<Item>     WARHEAD_INCENDIARY_LARGE;
    public static RegistryObject<Item>     WARHEAD_CLUSTER_SMALL;
    public static RegistryObject<Item>     WARHEAD_CLUSTER_MEDIUM;
    public static RegistryObject<Item>     WARHEAD_CLUSTER_LARGE;
    public static RegistryObject<Item>     WARHEAD_BUSTER_SMALL;
    public static RegistryObject<Item>     WARHEAD_BUSTER_MEDIUM;
    public static RegistryObject<Item>     WARHEAD_BUSTER_LARGE;
    public static RegistryObject<Item>     WARHEAD_NUCLEAR;
    public static RegistryObject<Item>     WARHEAD_MIRV;
    public static RegistryObject<Item>     WARHEAD_VOLCANO;

    public static RegistryObject<Item>     FUEL_TANK_SMALL;
    public static RegistryObject<Item>     FUEL_TANK_MEDIUM;
    public static RegistryObject<Item>     FUEL_TANK_LARGE;

    public static RegistryObject<Item>     THRUSTER_SMALL;
    public static RegistryObject<Item>     THRUSTER_MEDIUM;
    public static RegistryObject<Item>     THRUSTER_LARGE;

    public static RegistryObject<Item>     SAT_HEAD_MAPPER;
    public static RegistryObject<Item>     SAT_HEAD_SCANNER;
    public static RegistryObject<Item>     SAT_HEAD_RADAR;
    public static RegistryObject<Item>     SAT_HEAD_LASER;
    public static RegistryObject<Item>     SAT_HEAD_RESONATOR;

    public static RegistryObject<Item>     SEG_10;
    public static RegistryObject<Item>     SEG_15;
    public static RegistryObject<Item>     SEG_20;

    public static RegistryObject<Item>     COMBINE_SCRAP;

    public static RegistryObject<Item>     SHIMMER_HEAD;
    public static RegistryObject<Item>     SHIMMER_AXE_HEAD;
    public static RegistryObject<Item>     SHIMMER_HANDLE;

    public static RegistryObject<Item>     ENTANGLEMENT_KIT;

    public static RegistryObject<Item>     CIRCUIT;
    public static RegistryObject<Item>     CRT_DISPLAY;
    public static RegistryObject<Item>     CIRCUIT_STAR_PIECE;
    public static RegistryObject<Item>     CIRCUIT_STAR_COMPONENT;
    public static RegistryObject<Item>     CIRCUIT_STAR;
    public static RegistryObject<Item>     ASSEMBLY_NUKE;
    public static RegistryObject<Item>     CASING;

    public static RegistryObject<Item>     WIRING_RED_COPPER;

    public static RegistryObject<Item>     PELLET_RTG_DEPLETED;

    public static RegistryObject<Item>     PELLET_RTG_RADIUM;
    public static RegistryObject<Item>     PELLET_RTG_WEAK;
    public static RegistryObject<Item>     PELLET_RTG;
    public static RegistryObject<Item>     PELLET_RTG_STRONTIUM;
    public static RegistryObject<Item>     PELLET_RTG_COBALT;
    public static RegistryObject<Item>     PELLET_RTG_ACTINIUM;
    public static RegistryObject<Item>     PELLET_RTG_AMERICIUM;
    public static RegistryObject<Item>     PELLET_RTG_POLONIUM;
    public static RegistryObject<Item>     PELLET_RTG_GOLD;
    public static RegistryObject<Item>     PELLET_RTG_LEAD;

    public static RegistryObject<Item>     TRITIUM_DEUTERIUM_CAKE;

    public static RegistryObject<Item>     PISTON_SELENIUM;
    public static RegistryObject<Item>     PISTON_SET;
    public static RegistryObject<Item>     DRILLBIT;

    public static RegistryObject<Item>     RUNE_BLANK;
    public static RegistryObject<Item>     RUNE_ISA;
    public static RegistryObject<Item>     RUNE_DAGAZ;
    public static RegistryObject<Item>     RUNE_HAGALAZ;
    public static RegistryObject<Item>     RUNE_JERA;
    public static RegistryObject<Item>     RUNE_THURISAZ;

    public static RegistryObject<Item>     AMS_CATALYST_BLANK;
    public static RegistryObject<Item>     AMS_CATALYST_ALUMINIUM;
    public static RegistryObject<Item>     AMS_CATALYST_BERYLLIUM;
    public static RegistryObject<Item>     AMS_CATALYST_CAESIUM;
    public static RegistryObject<Item>     AMS_CATALYST_CERIUM;
    public static RegistryObject<Item>     AMS_CATALYST_COBALT;
    public static RegistryObject<Item>     AMS_CATALYST_COPPER;
    public static RegistryObject<Item>     AMS_CATALYST_DINEUTRONIUM;
    public static RegistryObject<Item>     AMS_CATALYST_EUPHEMIUM;
    public static RegistryObject<Item>     AMS_CATALYST_IRON;
    public static RegistryObject<Item>     AMS_CATALYST_LITHIUM;
    public static RegistryObject<Item>     AMS_CATALYST_NIOBIUM;
    public static RegistryObject<Item>     AMS_CATALYST_SCHRABIDIUM;
    public static RegistryObject<Item>     AMS_CATALYST_STRONTIUM;
    public static RegistryObject<Item>     AMS_CATALYST_THORIUM;
    public static RegistryObject<Item>     AMS_CATALYST_TUNGSTEN;

    public static RegistryObject<Item>     CELL_EMPTY;
    public static RegistryObject<Item>     CELL_UF6;
    public static RegistryObject<Item>     CELL_PUF6;
    public static RegistryObject<Item>     CELL_ANTIMATTER;
    public static RegistryObject<Item>     CELL_DEUTERIUM;
    public static RegistryObject<Item>     CELL_TRITIUM;
    public static RegistryObject<Item>     CELL_SAS3;
    public static RegistryObject<Item>     CELL_ANTI_SCHRABIDIUM;
    public static RegistryObject<Item>     CELL_BALEFIRE;

    public static RegistryObject<Item>     DEMON_CORE_OPEN;
    public static RegistryObject<Item>     DEMON_CORE_CLOSED;

    public static RegistryObject<Item>     PA_COIL;

    public static RegistryObject<Item>     PARTICLE_EMPTY;
    public static RegistryObject<Item>     PARTICLE_HYDROGEN;
    public static RegistryObject<Item>     PARTICLE_COPPER;
    public static RegistryObject<Item>     PARTICLE_LEAD;
    public static RegistryObject<Item>     PARTICLE_APROTON;
    public static RegistryObject<Item>     PARTICLE_AELECTRON;
    public static RegistryObject<Item>     PARTICLE_AMAT;
    public static RegistryObject<Item>     PARTICLE_ASCHRAB;
    public static RegistryObject<Item>     PARTICLE_HIGGS;
    public static RegistryObject<Item>     PARTICLE_MUON;
    public static RegistryObject<Item>     PARTICLE_TACHYON;
    public static RegistryObject<Item>     PARTICLE_STRANGE;
    public static RegistryObject<Item>     PARTICLE_DARK;
    public static RegistryObject<Item>     PARTICLE_SPARKTICLE;
    public static RegistryObject<Item>     PARTICLE_DIGAMMA;
    public static RegistryObject<Item>     PARTICLE_LUTECE;
    public static synchronized void register(DeferredRegister<Item> ITEMS){
        if(initialized){
            return;
        }
        initialized = true;
        LASER_CRYSTAL_DIGAMMA = getOrRegister("laser_crystal_digamma",()->new Item(new Item.Properties()));
        BATTERY_CREATIVE = getOrRegister("battery_creative",()->new BatteryItem(-1, 1_000_000L, new Item.Properties().stacksTo(1)));
        BATTERY_GENERIC = getOrRegister("battery_generic",()->new BatteryItem(false,5_000, 100, new Item.Properties()));
        BATTERY_ADVANCED = getOrRegister("battery_advanced",()->new BatteryItem(false,60_000, 500, new Item.Properties()));
        BATTERY_LITHIUM = getOrRegister("battery_lithium",()->new BatteryItem(false,250_000, 1000, new Item.Properties()));

        EGG_GLYPHID = getOrRegister("egg_glyphid",()->new ItemEggGlyphid(new Item.Properties()));
        EGG_GLYPHID_TO_BIRTH = getOrRegister("egg_glyphid_to_birth",()->new ItemEggGlyphidToBirth(new Item.Properties()));
        //==================元件 part======================
        CIRCUIT_BASIC = register(partList, "circuit_basic",()->new Item(new Item.Properties()));
        WIRE_FINE_ALUMINIUM = register(partList, "wire_aluminium",()->new Item(new Item.Properties()));
        SHELL = register(partList, "shell",()->new Item(new Item.Properties()));
        DUCT_TAPE = register(partList, "duct_tape",()->new Item(new Item.Properties()));
        //==================材料 material===================
        STEEL_SMALL_DUST = register(matherialList, "dust_small_steel", ()-> new Item(new Item.Properties()));
        STEEL_DUST = register(matherialList, "dust_steel", ()-> new Item(new Item.Properties()));
        STEEL_PLATE = register(matherialList, "plate_steel", ()-> new Item(new Item.Properties()));
        STEEL_INGOT = register(matherialList, "ingot_steel", ()-> new Item(new Item.Properties()));
        PLATE_TITANIUM = register(matherialList, "plate_titanium", ()-> new Item(new Item.Properties()));
        INGOT_TITANIUM = register(matherialList, "ingot_titanium", ()-> new Item(new Item.Properties()));
        INGOT_COBALT = register(matherialList, "ingot_cobalt", ()-> new Item(new Item.Properties()));
        INGOT_STARMETAL = register(matherialList, "ingot_starmetal", ()-> new Item(new Item.Properties()));
        INGOT_BISMUTH = register(matherialList, "ingot_bismuth", ()-> new Item(new Item.Properties()));
        INGOT_ASBESTOS = register(matherialList, "ingot_asbestos", ()-> new Item(new Item.Properties()));
        BALL_RESIN = register(matherialList, "ball_resin", ()->new Item(new Item.Properties()));
        INGOT_DURA_STEEL = register(matherialList, "ingot_dura_steel", ()->new Item(new Item.Properties()));
        INGOT_POLYMER = register(matherialList, "ingot_polymer", ()->new Item(new Item.Properties()));
        INGOT_BAKELITE = register(matherialList, "ingot_bakelite", ()->new Item(new Item.Properties()));
        INGOT_BIORUBBER = register(matherialList, "ingot_biorubber", ()->new Item(new Item.Properties()));
        INGOT_RUBBER = register(matherialList, "ingot_rubber", ()->new Item(new Item.Properties()));
        INGOT_PC = register(matherialList, "ingot_pc", ()->new Item(new Item.Properties()));
        INGOT_PVC = register(matherialList, "ingot_pvc", ()->new Item(new Item.Properties()));
        INGOT_DESH = register(matherialList, "ingot_desh", ()->new Item(new Item.Properties()));
        NUGGET_DESH = register(matherialList, "nugget_desh", ()->new Item(new Item.Properties()));
        INGOT_DINEUTRONIUM = register(matherialList, "ingot_dineutronium", ()->new Item(new Item.Properties()));
        NUGGET_DINEUTRONIUM = register(matherialList, "nugget_dineutronium", ()->new Item(new Item.Properties()));
        POWDER_DINEUTRONIUM = register(matherialList, "powder_dineutronium", ()->new Item(new Item.Properties()));
        INGOT_GUNMETAL = register(matherialList, "ingot_gunmetal", ()->new Item(new Item.Properties()));
        PLATE_GUNMETAL = register(matherialList, "plate_gunmetal", ()->new Item(new Item.Properties()));
        INGOT_WEAPONSTEEL = register(matherialList, "ingot_gunsteel", ()->new Item(new Item.Properties()));
        PLATE_WEAPONSTEEL = register(matherialList, "plate_gunsteel", ()->new Item(new Item.Properties()));
        INGOT_SATURNITE = register(matherialList, "ingot_saturnite", ()->new Item(new Item.Properties()));
        PLATE_SATURNITE = register(matherialList, "plate_saturnite", ()->new Item(new Item.Properties()));
        INGOT_FERROURANIUM = register(matherialList, "ingot_ferrouranium", ()->new Item(new Item.Properties()));
        INGOT_FIBERGLASS = register(matherialList, "ingot_fiberglass", ()->new Item(new Item.Properties()));
        POWDER_ASBESTOS = register(matherialList, "powder_asbestos", ()->new Item(new Item.Properties()));
        INGOT_ELECTRONIUM = register(matherialList, "ingot_electronium", ()->new Item(new Item.Properties()));
        NUGGET_MERCURY = register(matherialList, "nugget_mercury_tiny", ()->new Item(new Item.Properties()));
        INGOT_MERCURY = register(matherialList, "nugget_mercury", ()->new Item(new Item.Properties()));
        BOTTLE_MERCURY = register(matherialList, "bottle_mercury", ()->new Item(new Item.Properties()));
        INGOT_CALCIUM = register(matherialList, "ingot_calcium", ()->new Item(new Item.Properties()));
        POWDER_CALCIUM = register(matherialList, "powder_calcium", ()->new Item(new Item.Properties()));
        INGOT_CADMIUM = register(matherialList, "ingot_cadmium", ()->new Item(new Item.Properties()));
        POWDER_CADMIUM = register(matherialList, "powder_cadmium", ()->new Item(new Item.Properties()));
        POWDER_BISMUTH = register(matherialList, "powder_bismuth", ()->new Item(new Item.Properties()));
        INGOT_MUD = register(matherialList, "ingot_mud", ()->new Item(new Item.Properties()));
        INGOT_CFT = register(matherialList, "ingot_cft", ()->new Item(new Item.Properties()));
        INGOT_TH232 = register(matherialList, "ingot_th232", ()->new Item(new Item.Properties()));
        INGOT_URANIUM = register(matherialList, "ingot_uranium", ()->new Item(new Item.Properties()));
        INGOT_U233 = register(matherialList, "ingot_u233", ()->new Item(new Item.Properties()));
        INGOT_U235 = register(matherialList, "ingot_u235", ()->new Item(new Item.Properties()));
        INGOT_U238 = register(matherialList, "ingot_u238", ()->new Item(new Item.Properties()));
        INGOT_U238M2 = register(matherialList, "ingot_u238m2", ()->new Item(new Item.Properties()));
        INGOT_PLUTONIUM = register(matherialList, "ingot_plutonium", ()->new Item(new Item.Properties()));
        INGOT_PU238 = register(matherialList, "ingot_pu238", ()->new Item(new Item.Properties()));
        INGOT_PU239 = register(matherialList, "ingot_pu239", ()->new Item(new Item.Properties()));
        INGOT_PU240 = register(matherialList, "ingot_pu240", ()->new Item(new Item.Properties()));
        INGOT_PU241 = register(matherialList, "ingot_pu241", ()->new Item(new Item.Properties()));
        INGOT_PU_MIX = register(matherialList, "ingot_pu_mix", ()->new Item(new Item.Properties()));
        INGOT_AM241 = register(matherialList, "ingot_am241", ()->new Item(new Item.Properties()));
        INGOT_AM242 = register(matherialList, "ingot_am242", ()->new Item(new Item.Properties()));
        INGOT_AM_MIX = register(matherialList, "ingot_am_mix", ()->new Item(new Item.Properties()));
        INGOT_NEPTUNIUM = register(matherialList, "ingot_neptunium", ()->new Item(new Item.Properties()));
        INGOT_POLONIUM = register(matherialList, "ingot_polonium", ()->new Item(new Item.Properties()));
        INGOT_TECHNETIUM = register(matherialList, "ingot_technetium", ()->new Item(new Item.Properties()));
        INGOT_CO60 = register(matherialList, "ingot_co60", ()->new Item(new Item.Properties()));
        INGOT_SR90 = register(matherialList, "ingot_sr90", ()->new Item(new Item.Properties()));
        INGOT_AU198 = register(matherialList, "ingot_au198", ()->new Item(new Item.Properties()));
        INGOT_PB209 = register(matherialList, "ingot_pb209", ()->new Item(new Item.Properties()));
        INGOT_RA226 = register(matherialList, "ingot_ra226", ()->new Item(new Item.Properties()));
        INGOT_BORON = register(matherialList, "ingot_boron", ()->new Item(new Item.Properties()));
        INGOT_GRAPHITE = register(matherialList, "ingot_graphite", ()->new Item(new Item.Properties()));
        INGOT_FIREBRICK = register(matherialList, "ingot_firebrick", ()->new Item(new Item.Properties()));
        INGOT_SMORE = register(matherialList, "ingot_smore", ()->new Item(new Item.Properties()));
        SULFUR = register(matherialList, "sulfur", ()->new Item(new Item.Properties()));
        NITRA = register(matherialList, "nitra", ()->new Item(new Item.Properties()));
        NITRA_SMALL = register(matherialList, "nitra_small", ()->new Item(new Item.Properties()));
        
        INGOT_URANIUM_FUEL = register(matherialList, "ingot_uranium_fuel", ()->new Item(new Item.Properties()));
        INGOT_PLUTONIUM_FUEL = register(matherialList, "ingot_plutonium_fuel", ()->new Item(new Item.Properties()));
        INGOT_NEPTUNIUM_FUEL = register(matherialList, "ingot_neptunium_fuel", ()->new Item(new Item.Properties()));
        INGOT_MOX_FUEL = register(matherialList, "ingot_mox_fuel", ()->new Item(new Item.Properties()));
        INGOT_AMERICIUM_FUEL = register(matherialList, "ingot_americium_fuel", ()->new Item(new Item.Properties()));
        INGOT_SCHRABIDIUM_FUEL = register(matherialList, "ingot_schrabidium_fuel", ()->new Item(new Item.Properties()));
        INGOT_THORIUM_FUEL = register(matherialList, "ingot_thorium_fuel", ()->new Item(new Item.Properties()));
        NUGGET_URANIUM_FUEL = register(matherialList, "nugget_uranium_fuel", ()->new Item(new Item.Properties()));
        NUGGET_THORIUM_FUEL = register(matherialList, "nugget_thorium_fuel", ()->new Item(new Item.Properties()));
        NUGGET_PLUTONIUM_FUEL = register(matherialList, "nugget_plutonium_fuel", ()->new Item(new Item.Properties()));
        NUGGET_NEPTUNIUM_FUEL = register(matherialList, "nugget_neptunium_fuel", ()->new Item(new Item.Properties()));
        NUGGET_MOX_FUEL = register(matherialList, "nugget_mox_fuel", ()->new Item(new Item.Properties()));
        NUGGET_AMERICIUM_FUEL = register(matherialList, "nugget_americium_fuel", ()->new Item(new Item.Properties()));
        NUGGET_SCHRABIDIUM_FUEL = register(matherialList, "nugget_schrabidium_fuel", ()->new Item(new Item.Properties()));
        INGOT_TCALLOY = register(matherialList, "ingot_tcalloy", ()->new Item(new Item.Properties()));
        INGOT_CDALLOY = register(matherialList, "ingot_cdalloy", ()->new Item(new Item.Properties()));
        INGOT_BISMUTH_BRONZE = register(matherialList, "ingot_bismuth_bronze", ()->new Item(new Item.Properties()));
        INGOT_ARSENIC_BRONZE = register(matherialList, "ingot_arsenic_bronze", ()->new Item(new Item.Properties()));
        INGOT_BSCCO = register(matherialList, "ingot_bscco", ()->new Item(new Item.Properties()));
        
        NITER = register(matherialList, "salpeter", ()->new Item(new Item.Properties()));
        INGOT_COPPER = register(matherialList, "ingot_copper", ()->new Item(new Item.Properties()));
        INGOT_BERYLLIUM = register(matherialList, "ingot_beryllium", ()->new Item(new Item.Properties()));
        PLATE_LEAD = register(matherialList, "plate_lead", ()->new Item(new Item.Properties()));
        PLATE_DURA_STEEL = register(matherialList, "plate_dura_steel", ()->new Item(new Item.Properties()));
        INGOT_SCHRARANIUM = register(matherialList, "ingot_schraranium", ()->new Item(new Item.Properties()));
        INGOT_SCHRABIDIUM = register(matherialList, "ingot_schrabidium", ()->new Item(new Item.Properties()));
        INGOT_SCHRABIDATE = register(matherialList, "ingot_schrabidate", ()->new Item(new Item.Properties()));
        PLATE_SCHRABIDIUM = register(matherialList, "plate_schrabidium", ()->new Item(new Item.Properties()));
        PLATE_COPPER = register(matherialList, "plate_copper", ()->new Item(new Item.Properties()));
        PLATE_GOLD = register(matherialList, "plate_gold", ()->new Item(new Item.Properties()));
        LITHIUM = register(matherialList, "lithium", ()->new Item(new Item.Properties()));
        INGOT_SEMTEX = register(matherialList, "ingot_semtex", ()->new Item(new Item.Properties()));
        INGOT_C4 = register(matherialList, "ingot_c4", ()->new Item(new Item.Properties()));
        INGOT_PHOSPHORUS = register(matherialList, "ingot_phosphorus", ()->new Item(new Item.Properties()));
        COIL_ADVANCED_ALLOY = register(matherialList, "coil_advanced_alloy", ()->new Item(new Item.Properties()));
        COIL_ADVANCED_TORUS = register(matherialList, "coil_advanced_torus", ()->new Item(new Item.Properties()));
        INGOT_COMBINE_STEEL = register(matherialList, "ingot_combine_steel", ()->new Item(new Item.Properties()));
        PLATE_MIXED = register(matherialList, "plate_mixed", ()->new Item(new Item.Properties()));
        PLATE_PAA = register(matherialList, "plate_paa", ()->new Item(new Item.Properties()));
        PIPES_STEEL = register(matherialList, "pipes_steel", ()->new Item(new Item.Properties()));
        DRILL_TITANIUM = register(matherialList, "drill_titanium", ()->new Item(new Item.Properties()));
        PLATE_DALEKANIUM = register(matherialList, "plate_dalekanium", ()->new Item(new Item.Properties()));
        PLATE_EUPHEMIUM = register(matherialList, "plate_euphemium", ()->new Item(new Item.Properties()));
        BOLT = register(matherialList, "bolt", ()->new Item(new Item.Properties()));
        BOLT_SPIKE = register(matherialList, "bolt_spike", ()->new Item(new Item.Properties()));
        PLATE_POLYMER = register(matherialList, "plate_polymer", ()->new Item(new Item.Properties()));
        PLATE_KEVLAR = register(matherialList, "plate_kevlar", ()->new Item(new Item.Properties()));
        PLATE_DINEUTRONIUM = register(matherialList, "plate_dineutronium", ()->new Item(new Item.Properties()));
        PLATE_DESH = register(matherialList, "plate_desh", ()->new Item(new Item.Properties()));
        PLATE_BISMUTH = register(matherialList, "plate_bismuth", ()->new Item(new Item.Properties()));
        NUGGET_SOLINIUM = register(matherialList, "nugget_solinium", ()->new Item(new Item.Properties()));
        PHOTO_PANEL = register(matherialList, "photo_panel", ()->new Item(new Item.Properties()));
        SAT_BASE = register(matherialList, "sat_base", ()->new Item(new Item.Properties()));
        THRUSTER_NUCLEAR = register(matherialList, "thruster_nuclear", ()->new Item(new Item.Properties()));
        SAFETY_FUSE = register(matherialList, "safety_fuse", ()->new Item(new Item.Properties()));
//        PART_GENERIC = register(matherialList, "part_generic", ()->new Item(new Item.Properties()));
//        ITEM_EXPENSIVE = register(matherialList, "item_expensive", ()->new Item(new Item.Properties()));
//        ITEM_SECRET = register(matherialList, "item_secret", ()->new Item(new Item.Properties()));
//        INGOT_METAL = register(matherialList, "ingot_metal", ()->new Item(new Item.Properties()));
        CHEMICAL_DYE = register(matherialList, "chemical_dye", ()->new Item(new Item.Properties()));
        CRAYON = register(matherialList, "crayon", ()->new Item(new Item.Properties()));
        
        BILLET_URANIUM = register(matherialList, "billet_uranium", ()->new Item(new Item.Properties()));
        BILLET_U233 = register(matherialList, "billet_u233", ()->new Item(new Item.Properties()));
        BILLET_U235 = register(matherialList, "billet_u235", ()->new Item(new Item.Properties()));
        BILLET_U238 = register(matherialList, "billet_u238", ()->new Item(new Item.Properties()));
        BILLET_TH232 = register(matherialList, "billet_th232", ()->new Item(new Item.Properties()));
        BILLET_PLUTONIUM = register(matherialList, "billet_plutonium", ()->new Item(new Item.Properties()));
        BILLET_PU238 = register(matherialList, "billet_pu238", ()->new Item(new Item.Properties()));
        BILLET_PU239 = register(matherialList, "billet_pu239", ()->new Item(new Item.Properties()));
        BILLET_PU240 = register(matherialList, "billet_pu240", ()->new Item(new Item.Properties()));
        BILLET_PU241 = register(matherialList, "billet_pu241", ()->new Item(new Item.Properties()));
        BILLET_PU_MIX = register(matherialList, "billet_pu_mix", ()->new Item(new Item.Properties()));
        BILLET_AM241 = register(matherialList, "billet_am241", ()->new Item(new Item.Properties()));
        BILLET_AM242 = register(matherialList, "billet_am242", ()->new Item(new Item.Properties()));
        BILLET_AM_MIX = register(matherialList, "billet_am_mix", ()->new Item(new Item.Properties()));
        BILLET_NEPTUNIUM = register(matherialList, "billet_neptunium", ()->new Item(new Item.Properties()));
        BILLET_POLONIUM = register(matherialList, "billet_polonium", ()->new Item(new Item.Properties()));
        BILLET_TECHNETIUM = register(matherialList, "billet_technetium", ()->new Item(new Item.Properties()));
        BILLET_COBALT = register(matherialList, "billet_cobalt", ()->new Item(new Item.Properties()));
        BILLET_CO60 = register(matherialList, "billet_co60", ()->new Item(new Item.Properties()));
        BILLET_SR90 = register(matherialList, "billet_sr90", ()->new Item(new Item.Properties()));
        BILLET_AU198 = register(matherialList, "billet_au198", ()->new Item(new Item.Properties()));
        BILLET_PB209 = register(matherialList, "billet_pb209", ()->new Item(new Item.Properties()));
        BILLET_RA226 = register(matherialList, "billet_ra226", ()->new Item(new Item.Properties()));
        BILLET_ACTINIUM = register(matherialList, "billet_actinium", ()->new Item(new Item.Properties()));
        BILLET_SCHRABIDIUM = register(matherialList, "billet_schrabidium", ()->new Item(new Item.Properties()));
        BILLET_SOLINIUM = register(matherialList, "billet_solinium", ()->new Item(new Item.Properties()));
        BILLET_GH336 = register(matherialList, "billet_gh336", ()->new Item(new Item.Properties()));
        BILLET_AUSTRALIUM = register(matherialList, "billet_australium", ()->new Item(new Item.Properties()));
        BILLET_AUSTRALIUM_LESSER = register(matherialList, "billet_australium_lesser", ()->new Item(new Item.Properties()));
        BILLET_AUSTRALIUM_GREATER = register(matherialList, "billet_australium_greater", ()->new Item(new Item.Properties()));
        BILLET_URANIUM_FUEL = register(matherialList, "billet_uranium_fuel", ()->new Item(new Item.Properties()));
        BILLET_THORIUM_FUEL = register(matherialList, "billet_thorium_fuel", ()->new Item(new Item.Properties()));
        BILLET_PLUTONIUM_FUEL = register(matherialList, "billet_plutonium_fuel", ()->new Item(new Item.Properties()));
        BILLET_NEPTUNIUM_FUEL = register(matherialList, "billet_neptunium_fuel", ()->new Item(new Item.Properties()));
        BILLET_MOX_FUEL = register(matherialList, "billet_mox_fuel", ()->new Item(new Item.Properties()));
        BILLET_AMERICIUM_FUEL = register(matherialList, "billet_americium_fuel", ()->new Item(new Item.Properties()));
        BILLET_LES = register(matherialList, "billet_les", ()->new Item(new Item.Properties()));
//        BILLET_SCHRABIDIUM_FUEL = register(matherialList, "billet_schrabidium_fuel", ()->new Item(new Item.Properties()));
        BILLET_HES = register(matherialList, "billet_hes", ()->new Item(new Item.Properties()));
        BILLET_PO210BE = register(matherialList, "billet_po210be", ()->new Item(new Item.Properties()));
        BILLET_RA226BE = register(matherialList, "billet_ra226be", ()->new Item(new Item.Properties()));
        BILLET_PU238BE = register(matherialList, "billet_pu238be", ()->new Item(new Item.Properties()));
        BILLET_BERYLLIUM = register(matherialList, "billet_beryllium", ()->new Item(new Item.Properties()));
        BILLET_BISMUTH = register(matherialList, "billet_bismuth", ()->new Item(new Item.Properties()));
        BILLET_ZIRCONIUM = register(matherialList, "billet_zirconium", ()->new Item(new Item.Properties()));
        BILLET_YHARONITE = register(matherialList, "billet_yharonite", ()->new Item(new Item.Properties()));
        BILLET_BALEFIRE_GOLD = register(matherialList, "billet_balefire_gold", ()->new Item(new Item.Properties()));
        BILLET_FLASHLEAD = register(matherialList, "billet_flashlead", ()->new Item(new Item.Properties()));
        BILLET_ZFB_BISMUTH = register(matherialList, "billet_zfb_bismuth", ()->new Item(new Item.Properties()));
        BILLET_ZFB_PU241 = register(matherialList, "billet_zfb_pu241", ()->new Item(new Item.Properties()));
        BILLET_ZFB_AM_MIX = register(matherialList, "billet_zfb_am_mix", ()->new Item(new Item.Properties()));
        BILLET_NUCLEAR_WASTE = register(matherialList, "billet_nuclear_waste", ()->new Item(new Item.Properties()));
        
        ORE_BYPRODUCT = register(matherialList, "byproduct", ()->new Item(new Item.Properties()));

        ORE_CENTRIFUGED = register(matherialList, "ore_centrifuged", ()->new Item(new Item.Properties()));
        ORE_CLEANED = register(matherialList, "ore_cleaned", ()->new Item(new Item.Properties()));
        ORE_SEPARATED = register(matherialList, "ore_separated", ()->new Item(new Item.Properties()));
        ORE_PURIFIED = register(matherialList, "ore_purified", ()->new Item(new Item.Properties()));
        ORE_NITRATED = register(matherialList, "ore_nitrated", ()->new Item(new Item.Properties()));
        ORE_NITROCRYSTALLINE = register(matherialList, "ore_nitrocrystalline", ()->new Item(new Item.Properties()));
        ORE_DEEPCLEANED = register(matherialList, "ore_deepcleaned", ()->new Item(new Item.Properties()));
        ORE_SEARED = register(matherialList, "ore_seared", ()->new Item(new Item.Properties()));
        ORE_ENRICHED = register(matherialList, "ore_enriched", ()->new Item(new Item.Properties()));
        BEDROCK_ORE = register(matherialList, "bedrock_ore", ()->new Item(new Item.Properties()));
        BEDROCK_ORE_FRAGMENT = register(matherialList, "bedrock_ore_fragment", ()->new Item(new Item.Properties()));
        
        INGOT_LANTHANIUM = register(matherialList, "ingot_lanthanium", ()->new Item(new Item.Properties()));
        INGOT_ACTINIUM = register(matherialList, "ingot_actinium", ()->new Item(new Item.Properties()));
        
        INGOT_METEORITE = register(matherialList, "ingot_meteorite", ()->new Item(new Item.Properties()));
        INGOT_METEORITE_FORGED = register(matherialList, "ingot_meteorite_forged", ()->new Item(new Item.Properties()));
        BLADE_METEORITE = register(matherialList, "blade_meteorite", ()->new Item(new Item.Properties()));
        INGOT_STEEL_DUSTED = register(matherialList, "ingot_steel_dusted", ()->new Item(new Item.Properties()));
        INGOT_CHAINSTEEL = register(matherialList, "ingot_chainsteel", ()->new Item(new Item.Properties()));
        
        PLATE_ARMOR_TITANIUM = register(matherialList, "plate_armor_titanium", ()->new Item(new Item.Properties()));
        PLATE_ARMOR_AJR = register(matherialList, "plate_armor_ajr", ()->new Item(new Item.Properties()));
        PLATE_ARMOR_HEV = register(matherialList, "plate_armor_hev", ()->new Item(new Item.Properties()));
        PLATE_ARMOR_LUNAR = register(matherialList, "plate_armor_lunar", ()->new Item(new Item.Properties()));
        PLATE_ARMOR_FAU = register(matherialList, "plate_armor_fau", ()->new Item(new Item.Properties()));
        PLATE_ARMOR_DNT = register(matherialList, "plate_armor_dnt", ()->new Item(new Item.Properties()));
        
//        OIL_TAR = register(matherialList, "oil_tar", ()->new Item(new Item.Properties()));
        SOLID_FUEL_PRESTO = register(matherialList, "solid_fuel_presto", ()->new Item(new Item.Properties()));
        SOLID_FUEL_PRESTO_TRIPLET = register(matherialList, "solid_fuel_presto_triplet", ()->new Item(new Item.Properties()));
        SOLID_FUEL_BF = register(matherialList, "solid_fuel_bf", ()->new Item(new Item.Properties()));
        SOLID_FUEL_PRESTO_BF = register(matherialList, "solid_fuel_presto_bf", ()->new Item(new Item.Properties()));
        SOLID_FUEL_PRESTO_TRIPLET_BF = register(matherialList, "solid_fuel_presto_triplet_bf", ()->new Item(new Item.Properties()));
        ROCKET_FUEL = register(matherialList, "rocket_fuel", ()->new Item(new Item.Properties()));
//        COKE = register(matherialList, "coke", ()->new Item(new Item.Properties()));
//        BRIQUETTE = register(matherialList, "briquette", ()->new Item(new Item.Properties()));
        COAL_INFERNAL = register(matherialList, "coal_infernal", ()->new Item(new Item.Properties()));
        CINNEBAR = register(matherialList, "cinnebar", ()->new Item(new Item.Properties()));
//        POWDER_ASH = register(matherialList, "powder_ash", ()->new Item(new Item.Properties()));
        POWDER_LIMESTONE = register(matherialList, "powder_limestone", ()->new Item(new Item.Properties()));
        POWDER_CEMENT = register(matherialList, "powder_cement", ()->new Item(new Item.Properties()));
        
        INGOT_GH336 = register(matherialList, "ingot_gh336", ()->new Item(new Item.Properties()));
        NUGGET_GH336 = register(matherialList, "nugget_gh336", ()->new Item(new Item.Properties()));
        
        INGOT_AUSTRALIUM = register(matherialList, "ingot_australium", ()->new Item(new Item.Properties()));
        NUGGET_AUSTRALIUM = register(matherialList, "nugget_australium", ()->new Item(new Item.Properties()));
        NUGGET_AUSTRALIUM_LESSER = register(matherialList, "nugget_australium_lesser", ()->new Item(new Item.Properties()));
        NUGGET_AUSTRALIUM_GREATER = register(matherialList, "nugget_australium_greater", ()->new Item(new Item.Properties()));
        
        NUGGET_TH232 = register(matherialList, "nugget_th232", ()->new Item(new Item.Properties()));
        NUGGET_URANIUM = register(matherialList, "nugget_uranium", ()->new Item(new Item.Properties()));
        NUGGET_U233 = register(matherialList, "nugget_u233", ()->new Item(new Item.Properties()));
        NUGGET_U235 = register(matherialList, "nugget_u235", ()->new Item(new Item.Properties()));
        NUGGET_U238 = register(matherialList, "nugget_u238", ()->new Item(new Item.Properties()));
        NUGGET_PLUTONIUM = register(matherialList, "nugget_plutonium", ()->new Item(new Item.Properties()));
        NUGGET_PU238 = register(matherialList, "nugget_pu238", ()->new Item(new Item.Properties()));
        NUGGET_PU239 = register(matherialList, "nugget_pu239", ()->new Item(new Item.Properties()));
        NUGGET_PU240 = register(matherialList, "nugget_pu240", ()->new Item(new Item.Properties()));
        NUGGET_PU241 = register(matherialList, "nugget_pu241", ()->new Item(new Item.Properties()));
        NUGGET_PU_MIX = register(matherialList, "nugget_pu_mix", ()->new Item(new Item.Properties()));
        NUGGET_AM241 = register(matherialList, "nugget_am241", ()->new Item(new Item.Properties()));
        NUGGET_AM242 = register(matherialList, "nugget_am242", ()->new Item(new Item.Properties()));
        NUGGET_AM_MIX = register(matherialList, "nugget_am_mix", ()->new Item(new Item.Properties()));
        NUGGET_NEPTUNIUM = register(matherialList, "nugget_neptunium", ()->new Item(new Item.Properties()));
        NUGGET_POLONIUM = register(matherialList, "nugget_polonium", ()->new Item(new Item.Properties()));
        NUGGET_TECHNETIUM = register(matherialList, "nugget_technetium", ()->new Item(new Item.Properties()));
        NUGGET_COBALT = register(matherialList, "nugget_cobalt", ()->new Item(new Item.Properties()));
        NUGGET_CO60 = register(matherialList, "nugget_co60", ()->new Item(new Item.Properties()));
        NUGGET_SR90 = register(matherialList, "nugget_sr90", ()->new Item(new Item.Properties()));
        NUGGET_AU198 = register(matherialList, "nugget_au198", ()->new Item(new Item.Properties()));
        NUGGET_PB209 = register(matherialList, "nugget_pb209", ()->new Item(new Item.Properties()));
        NUGGET_RA226 = register(matherialList, "nugget_ra226", ()->new Item(new Item.Properties()));
        NUGGET_ACTINIUM = register(matherialList, "nugget_actinium", ()->new Item(new Item.Properties()));
        PLATE_ALUMINIUM = register(matherialList, "plate_aluminium", ()->new Item(new Item.Properties()));
        NEUTRON_REFLECTOR = register(matherialList, "neutron_reflector", ()->new Item(new Item.Properties()));
        NUGGET_LEAD = register(matherialList, "nugget_lead", ()->new Item(new Item.Properties()));
        NUGGET_BISMUTH = register(matherialList, "nugget_bismuth", ()->new Item(new Item.Properties()));
        INGOT_ARSENIC = register(matherialList, "ingot_arsenic", ()->new Item(new Item.Properties()));
        NUGGET_ARSENIC = register(matherialList, "nugget_arsenic", ()->new Item(new Item.Properties()));
        INGOT_TANTALIUM = register(matherialList, "ingot_tantalium", ()->new Item(new Item.Properties()));
        NUGGET_TANTALIUM = register(matherialList, "nugget_tantalium", ()->new Item(new Item.Properties()));
        INGOT_SILICON = register(matherialList, "ingot_silicon", ()->new Item(new Item.Properties()));
        BILLET_SILICON = register(matherialList, "billet_silicon", ()->new Item(new Item.Properties()));
        NUGGET_SILICON = register(matherialList, "nugget_silicon", ()->new Item(new Item.Properties()));
        INGOT_NIOBIUM = register(matherialList, "ingot_niobium", ()->new Item(new Item.Properties()));
        NUGGET_NIOBIUM = register(matherialList, "nugget_niobium", ()->new Item(new Item.Properties()));
        INGOT_OSMIRIDIUM = register(matherialList, "ingot_osmiridium", ()->new Item(new Item.Properties()));
        NUGGET_OSMIRIDIUM = register(matherialList, "nugget_osmiridium", ()->new Item(new Item.Properties()));
        NUGGET_SCHRABIDIUM = register(matherialList, "nugget_schrabidium", ()->new Item(new Item.Properties()));
        NUGGET_BERYLLIUM = register(matherialList, "nugget_beryllium", ()->new Item(new Item.Properties()));
        HAZMAT_CLOTH = register(matherialList, "hazmat_cloth", ()->new Item(new Item.Properties()));
        HAZMAT_CLOTH_RED = register(matherialList, "hazmat_cloth_red", ()->new Item(new Item.Properties()));
        HAZMAT_CLOTH_GREY = register(matherialList, "hazmat_cloth_grey", ()->new Item(new Item.Properties()));
        ASBESTOS_CLOTH = register(matherialList, "asbestos_cloth", ()->new Item(new Item.Properties()));
        RAG = register(matherialList, "rag", ()->new Item(new Item.Properties()));
        RAG_DAMP = register(matherialList, "rag_damp", ()->new Item(new Item.Properties()));
        RAG_PISS = register(matherialList, "rag_piss", ()->new Item(new Item.Properties()));
        FILTER_COAL = register(matherialList, "filter_coal", ()->new Item(new Item.Properties()));
        INGOT_HES = register(matherialList, "ingot_hes", ()->new Item(new Item.Properties()));
        INGOT_LES = register(matherialList, "ingot_les", ()->new Item(new Item.Properties()));
        NUGGET_HES = register(matherialList, "nugget_hes", ()->new Item(new Item.Properties()));
        NUGGET_LES = register(matherialList, "nugget_les", ()->new Item(new Item.Properties()));
        PLATE_COMBINE_STEEL = register(matherialList, "plate_combine_steel", ()->new Item(new Item.Properties()));
        
        CRYSTAL_COAL = register(matherialList, "crystal_coal", ()->new Item(new Item.Properties()));
        CRYSTAL_IRON = register(matherialList, "crystal_iron", ()->new Item(new Item.Properties()));
        CRYSTAL_GOLD = register(matherialList, "crystal_gold", ()->new Item(new Item.Properties()));
        CRYSTAL_REDSTONE = register(matherialList, "crystal_redstone", ()->new Item(new Item.Properties()));
        CRYSTAL_LAPIS = register(matherialList, "crystal_lapis", ()->new Item(new Item.Properties()));
        CRYSTAL_DIAMOND = register(matherialList, "crystal_diamond", ()->new Item(new Item.Properties()));
        CRYSTAL_URANIUM = register(matherialList, "crystal_uranium", ()->new Item(new Item.Properties()));
        CRYSTAL_THORIUM = register(matherialList, "crystal_thorium", ()->new Item(new Item.Properties()));
        CRYSTAL_PLUTONIUM = register(matherialList, "crystal_plutonium", ()->new Item(new Item.Properties()));
        CRYSTAL_TITANIUM = register(matherialList, "crystal_titanium", ()->new Item(new Item.Properties()));
        CRYSTAL_SULFUR = register(matherialList, "crystal_sulfur", ()->new Item(new Item.Properties()));
        CRYSTAL_NITER = register(matherialList, "crystal_niter", ()->new Item(new Item.Properties()));
        CRYSTAL_COPPER = register(matherialList, "crystal_copper", ()->new Item(new Item.Properties()));
        CRYSTAL_TUNGSTEN = register(matherialList, "crystal_tungsten", ()->new Item(new Item.Properties()));
        CRYSTAL_ALUMINIUM = register(matherialList, "crystal_aluminium", ()->new Item(new Item.Properties()));
        CRYSTAL_FLUORITE = register(matherialList, "crystal_fluorite", ()->new Item(new Item.Properties()));
        CRYSTAL_BERYLLIUM = register(matherialList, "crystal_beryllium", ()->new Item(new Item.Properties()));
        CRYSTAL_LEAD = register(matherialList, "crystal_lead", ()->new Item(new Item.Properties()));
        CRYSTAL_SCHRARANIUM = register(matherialList, "crystal_schraranium", ()->new Item(new Item.Properties()));
        CRYSTAL_SCHRABIDIUM = register(matherialList, "crystal_schrabidium", ()->new Item(new Item.Properties()));
        CRYSTAL_RARE = register(matherialList, "crystal_rare", ()->new Item(new Item.Properties()));
        CRYSTAL_PHOSPHORUS = register(matherialList, "crystal_phosphorus", ()->new Item(new Item.Properties()));
        CRYSTAL_LITHIUM = register(matherialList, "crystal_lithium", ()->new Item(new Item.Properties()));
        CRYSTAL_COBALT = register(matherialList, "crystal_cobalt", ()->new Item(new Item.Properties()));
        CRYSTAL_STARMETAL = register(matherialList, "crystal_starmetal", ()->new Item(new Item.Properties()));
        CRYSTAL_CINNEBAR = register(matherialList, "crystal_cinnebar", ()->new Item(new Item.Properties()));
        CRYSTAL_TRIXITE = register(matherialList, "crystal_trixite", ()->new Item(new Item.Properties()));
        CRYSTAL_OSMIRIDIUM = register(matherialList, "crystal_osmiridium", ()->new Item(new Item.Properties()));
        GEM_SODALITE = register(matherialList, "gem_sodalite", ()->new Item(new Item.Properties()));
        GEM_TANTALIUM = register(matherialList, "gem_tantalium", ()->new Item(new Item.Properties()));
        GEM_VOLCANIC = register(matherialList, "gem_volcanic", ()->new Item(new Item.Properties()));
        GEM_RAD = register(matherialList, "gem_rad", ()->new Item(new Item.Properties()));
        GEM_ALEXANDRITE = register(matherialList, "gem_alexandrite", ()->new Item(new Item.Properties()));
        
        POWDER_LEAD = register(matherialList, "powder_lead", ()->new Item(new Item.Properties()));
        POWDER_TANTALIUM = register(matherialList, "powder_tantalium", ()->new Item(new Item.Properties()));
        POWDER_NEPTUNIUM = register(matherialList, "powder_neptunium", ()->new Item(new Item.Properties()));
        POWDER_POLONIUM = register(matherialList, "powder_polonium", ()->new Item(new Item.Properties()));
        POWDER_CO60 = register(matherialList, "powder_co60", ()->new Item(new Item.Properties()));
        POWDER_SR90 = register(matherialList, "powder_sr90", ()->new Item(new Item.Properties()));
        POWDER_SR90_TINY = register(matherialList, "powder_sr90_tiny", ()->new Item(new Item.Properties()));
        POWDER_I131 = register(matherialList, "powder_i131", ()->new Item(new Item.Properties()));
        POWDER_I131_TINY = register(matherialList, "powder_i131_tiny", ()->new Item(new Item.Properties()));
        POWDER_XE135 = register(matherialList, "powder_xe135", ()->new Item(new Item.Properties()));
        POWDER_XE135_TINY = register(matherialList, "powder_xe135_tiny", ()->new Item(new Item.Properties()));
        POWDER_CS137 = register(matherialList, "powder_cs137", ()->new Item(new Item.Properties()));
        POWDER_CS137_TINY = register(matherialList, "powder_cs137_tiny", ()->new Item(new Item.Properties()));
        POWDER_AU198 = register(matherialList, "powder_au198", ()->new Item(new Item.Properties()));
        POWDER_RA226 = register(matherialList, "powder_ra226", ()->new Item(new Item.Properties()));
        POWDER_AT209 = register(matherialList, "powder_at209", ()->new Item(new Item.Properties()));
        POWDER_SCHRABIDIUM = register(matherialList, "powder_schrabidium", ()->new Item(new Item.Properties()));
        POWDER_SCHRABIDATE = register(matherialList, "powder_schrabidate", ()->new Item(new Item.Properties()));
        POWDER_ALUMINIUM = register(matherialList, "powder_aluminium", ()->new Item(new Item.Properties()));
        POWDER_BERYLLIUM = register(matherialList, "powder_beryllium", ()->new Item(new Item.Properties()));
        POWDER_COPPER = register(matherialList, "powder_copper", ()->new Item(new Item.Properties()));
        POWDER_GOLD = register(matherialList, "powder_gold", ()->new Item(new Item.Properties()));
        POWDER_IRON = register(matherialList, "powder_iron", ()->new Item(new Item.Properties()));
        POWDER_TITANIUM = register(matherialList, "powder_titanium", ()->new Item(new Item.Properties()));
        POWDER_TUNGSTEN = register(matherialList, "powder_tungsten", ()->new Item(new Item.Properties()));
        POWDER_URANIUM = register(matherialList, "powder_uranium", ()->new Item(new Item.Properties()));
        POWDER_PLUTONIUM = register(matherialList, "powder_plutonium", ()->new Item(new Item.Properties()));
        DUST = register(matherialList, "dust", ()->new Item(new Item.Properties()));
        DUST_TINY = register(matherialList, "dust_tiny", ()->new Item(new Item.Properties()));
        FALLOUT = register(matherialList, "fallout", ()->new Item(new Item.Properties()));
        POWDER_ADVANCED_ALLOY = register(matherialList, "powder_advanced_alloy", ()->new Item(new Item.Properties()));
        POWDER_TCALLOY = register(matherialList, "powder_tcalloy", ()->new Item(new Item.Properties()));
        POWDER_COMBINE_STEEL = register(matherialList, "powder_combine_steel", ()->new Item(new Item.Properties()));
        POWDER_DIAMOND = register(matherialList, "powder_diamond", ()->new Item(new Item.Properties()));
        POWDER_EMERALD = register(matherialList, "powder_emerald", ()->new Item(new Item.Properties()));
        POWDER_LAPIS = register(matherialList, "powder_lapis", ()->new Item(new Item.Properties()));
        POWDER_QUARTZ = register(matherialList, "powder_quartz", ()->new Item(new Item.Properties()));
        POWDER_MAGNETIZED_TUNGSTEN = register(matherialList, "powder_magnetized_tungsten", ()->new Item(new Item.Properties()));
        POWDER_CHLOROPHYTE = register(matherialList, "powder_chlorophyte", ()->new Item(new Item.Properties()));
        POWDER_RED_COPPER = register(matherialList, "powder_red_copper", ()->new Item(new Item.Properties()));
        POWDER_STEEL = register(matherialList, "powder_steel", ()->new Item(new Item.Properties()));
        POWDER_LITHIUM = register(matherialList, "powder_lithium", ()->new Item(new Item.Properties()));
        POWDER_ZIRCONIUM = register(matherialList, "powder_zirconium", ()->new Item(new Item.Properties()));
        POWDER_SODIUM = register(matherialList, "powder_sodium", ()->new Item(new Item.Properties()));
        POWDER_POWER = register(matherialList, "powder_energy_alt", ()->new Item(new Item.Properties()));
        POWDER_IODINE = register(matherialList, "powder_iodine", ()->new Item(new Item.Properties()));
        POWDER_THORIUM = register(matherialList, "powder_thorium", ()->new Item(new Item.Properties()));
        POWDER_NEODYMIUM = register(matherialList, "powder_neodymium", ()->new Item(new Item.Properties()));
        POWDER_ASTATINE = register(matherialList, "powder_astatine", ()->new Item(new Item.Properties()));
        POWDER_CAESIUM = register(matherialList, "powder_caesium", ()->new Item(new Item.Properties()));
        POWDER_AUSTRALIUM = register(matherialList, "powder_australium", ()->new Item(new Item.Properties()));
        POWDER_STRONTIUM = register(matherialList, "powder_strontium", ()->new Item(new Item.Properties()));
        POWDER_COBALT = register(matherialList, "powder_cobalt", ()->new Item(new Item.Properties()));
        POWDER_BROMINE = register(matherialList, "powder_bromine", ()->new Item(new Item.Properties()));
        POWDER_NIOBIUM = register(matherialList, "powder_niobium", ()->new Item(new Item.Properties()));
        POWDER_TENNESSINE = register(matherialList, "powder_tennessine", ()->new Item(new Item.Properties()));
        POWDER_CERIUM = register(matherialList, "powder_cerium", ()->new Item(new Item.Properties()));
        POWDER_DURA_STEEL = register(matherialList, "powder_dura_steel", ()->new Item(new Item.Properties()));
        POWDER_POLYMER = register(matherialList, "powder_polymer", ()->new Item(new Item.Properties()));
        POWDER_BAKELITE = register(matherialList, "powder_bakelite", ()->new Item(new Item.Properties()));
        POWDER_EUPHEMIUM = register(matherialList, "powder_euphemium", ()->new Item(new Item.Properties()));
        POWDER_METEORITE = register(matherialList, "powder_meteorite", ()->new Item(new Item.Properties()));
        POWDER_LANTHANIUM = register(matherialList, "powder_lanthanium", ()->new Item(new Item.Properties()));
        POWDER_ACTINIUM = register(matherialList, "powder_actinium", ()->new Item(new Item.Properties()));
        POWDER_BORON = register(matherialList, "powder_boron", ()->new Item(new Item.Properties()));
        POWDER_SEMTEX_MIX = register(matherialList, "powder_semtex_mix", ()->new Item(new Item.Properties()));
        POWDER_DESH_MIX = register(matherialList, "powder_desh_mix", ()->new Item(new Item.Properties()));
        POWDER_DESH_READY = register(matherialList, "powder_desh_ready", ()->new Item(new Item.Properties()));
        POWDER_NITAN_MIX = register(matherialList, "powder_nitan_mix", ()->new Item(new Item.Properties()));
        POWDER_SPARK_MIX = register(matherialList, "powder_spark_mix", ()->new Item(new Item.Properties()));
        POWDER_DESH = register(matherialList, "powder_desh", ()->new Item(new Item.Properties()));
        POWDER_STEEL_TINY = register(matherialList, "powder_steel_tiny", ()->new Item(new Item.Properties()));
        POWDER_LITHIUM_TINY = register(matherialList, "powder_lithium_tiny", ()->new Item(new Item.Properties()));
        POWDER_NEODYMIUM_TINY = register(matherialList, "powder_neodymium_tiny", ()->new Item(new Item.Properties()));
        POWDER_COBALT_TINY = register(matherialList, "powder_cobalt_tiny", ()->new Item(new Item.Properties()));
        POWDER_NIOBIUM_TINY = register(matherialList, "powder_niobium_tiny", ()->new Item(new Item.Properties()));
        POWDER_CERIUM_TINY = register(matherialList, "powder_cerium_tiny", ()->new Item(new Item.Properties()));
        POWDER_LANTHANIUM_TINY = register(matherialList, "powder_lanthanium_tiny", ()->new Item(new Item.Properties()));
        POWDER_ACTINIUM_TINY = register(matherialList, "powder_actinium_tiny", ()->new Item(new Item.Properties()));
        POWDER_BORON_TINY = register(matherialList, "powder_boron_tiny", ()->new Item(new Item.Properties()));
        POWDER_METEORITE_TINY = register(matherialList, "powder_meteorite_tiny", ()->new Item(new Item.Properties()));
        POWDER_YELLOWCAKE = register(matherialList, "powder_yellowcake", ()->new Item(new Item.Properties()));
        POWDER_MAGIC = register(matherialList, "powder_magic", ()->new Item(new Item.Properties()));
        POWDER_BALEFIRE = register(matherialList, "powder_balefire", ()->new Item(new Item.Properties()));
        POWDER_SAWDUST = register(matherialList, "powder_sawdust", ()->new Item(new Item.Properties()));
        POWDER_FLUX = register(matherialList, "powder_flux", ()->new Item(new Item.Properties()));
        POWDER_FERTILIZER = register(matherialList, "powder_fertilizer", ()->new Item(new Item.Properties()));
        POWDER_COLTAN_ORE = register(matherialList, "powder_coltan_ore", ()->new Item(new Item.Properties()));
        POWDER_COLTAN = register(matherialList, "powder_coltan", ()->new Item(new Item.Properties()));
        POWDER_TEKTITE = register(matherialList, "powder_tektite", ()->new Item(new Item.Properties()));
        POWDER_PALEOGENITE = register(matherialList, "powder_paleogenite", ()->new Item(new Item.Properties()));
        POWDER_PALEOGENITE_TINY = register(matherialList, "powder_paleogenite_tiny", ()->new Item(new Item.Properties()));
        POWDER_IMPURE_OSMIRIDIUM = register(matherialList, "powder_impure_osmiridium", ()->new Item(new Item.Properties()));
        POWDER_BORAX = register(matherialList, "powder_borax", ()->new Item(new Item.Properties()));
        POWDER_CHLOROCALCITE = register(matherialList, "powder_chlorocalcite", ()->new Item(new Item.Properties()));
        POWDER_MOLYSITE = register(matherialList, "powder_molysite", ()->new Item(new Item.Properties()));
        
        FRAGMENT_NEODYMIUM = register(matherialList, "fragment_neodymium", ()->new Item(new Item.Properties()));
        FRAGMENT_COBALT = register(matherialList, "fragment_cobalt", ()->new Item(new Item.Properties()));
        FRAGMENT_NIOBIUM = register(matherialList, "fragment_niobium", ()->new Item(new Item.Properties()));
        FRAGMENT_CERIUM = register(matherialList, "fragment_cerium", ()->new Item(new Item.Properties()));
        FRAGMENT_LANTHANIUM = register(matherialList, "fragment_lanthanium", ()->new Item(new Item.Properties()));
        FRAGMENT_ACTINIUM = register(matherialList, "fragment_actinium", ()->new Item(new Item.Properties()));
        FRAGMENT_BORON = register(matherialList, "fragment_boron", ()->new Item(new Item.Properties()));
        FRAGMENT_METEORITE = register(matherialList, "fragment_meteorite", ()->new Item(new Item.Properties()));
        FRAGMENT_COLTAN = register(matherialList, "fragment_coltan", ()->new Item(new Item.Properties()));
//        CHUNK_ORE = register(matherialList, "chunk_ore", ()->new Item(new Item.Properties()));
        
        BIOMASS = register(matherialList, "biomass", ()->new Item(new Item.Properties()));
        BIOMASS_COMPRESSED = register(matherialList, "biomass_compressed", ()->new Item(new Item.Properties()));
        BIO_WAFER = register(matherialList, "bio_wafer", ()->new Item(new Item.Properties()));
//        PLANT_ITEM = register(matherialList, "plant_item", ()->new Item(new Item.Properties()));
        
        COIL_COPPER = register(matherialList, "coil_copper", ()->new Item(new Item.Properties()));
        COIL_COPPER_TORUS = register(matherialList, "coil_copper_torus", ()->new Item(new Item.Properties()));
        COIL_TUNGSTEN = register(matherialList, "coil_tungsten", ()->new Item(new Item.Properties()));
        TANK_STEEL = register(matherialList, "tank_steel", ()->new Item(new Item.Properties()));
        MOTOR = register(matherialList, "motor", ()->new Item(new Item.Properties()));
        MOTOR_DESH = register(matherialList, "motor_desh", ()->new Item(new Item.Properties()));
        MOTOR_BISMUTH = register(matherialList, "motor_bismuth", ()->new Item(new Item.Properties()));
        CENTRIFUGE_ELEMENT = register(matherialList, "centrifuge_element", ()->new Item(new Item.Properties()));
        REACTOR_CORE = register(matherialList, "reactor_core", ()->new Item(new Item.Properties()));
        RTG_UNIT = register(matherialList, "rtg_unit", ()->new Item(new Item.Properties()));
        COIL_MAGNETIZED_TUNGSTEN = register(matherialList, "coil_magnetized_tungsten", ()->new Item(new Item.Properties()));
        COIL_GOLD = register(matherialList, "coil_gold", ()->new Item(new Item.Properties()));
        COIL_GOLD_TORUS = register(matherialList, "coil_gold_torus", ()->new Item(new Item.Properties()));
        CHLORINE_PINWHEEL = register(matherialList, "chlorine_pinwheel", ()->new Item(new Item.Properties()));
        RING_STARMETAL = register(matherialList, "ring_starmetal", ()->new Item(new Item.Properties()));
        FLYWHEEL_BERYLLIUM = register(matherialList, "flywheel_beryllium", ()->new Item(new Item.Properties()));
        DEUTERIUM_FILTER = register(matherialList, "deuterium_filter", ()->new Item(new Item.Properties()));
//        PARTS_LEGENDARY = register(matherialList, "parts_legendary", ()->new Item(new Item.Properties()));
        
        GEAR_LARGE = register(matherialList, "gear_large", ()->new Item(new Item.Properties()));
        SAWBLADE = register(matherialList, "sawblade", ()->new Item(new Item.Properties()));
        
        PIPE = register(matherialList, "pipe", ()->new Item(new Item.Properties()));
        FINS_FLAT = register(matherialList, "fins_flat", ()->new Item(new Item.Properties()));
        FINS_SMALL_STEEL = register(matherialList, "fins_small_steel", ()->new Item(new Item.Properties()));
        FINS_BIG_STEEL = register(matherialList, "fins_big_steel", ()->new Item(new Item.Properties()));
        FINS_TRI_STEEL = register(matherialList, "fins_tri_steel", ()->new Item(new Item.Properties()));
        FINS_QUAD_TITANIUM = register(matherialList, "fins_quad_titanium", ()->new Item(new Item.Properties()));
        SPHERE_STEEL = register(matherialList, "sphere_steel", ()->new Item(new Item.Properties()));
        PEDESTAL_STEEL = register(matherialList, "pedestal_steel", ()->new Item(new Item.Properties()));
        DYSFUNCTIONAL_REACTOR = register(matherialList, "dysfunctional_reactor", ()->new Item(new Item.Properties()));
        BLADE_TITANIUM = register(matherialList, "blade_titanium", ()->new Item(new Item.Properties()));
        TURBINE_TITANIUM = register(matherialList, "turbine_titanium", ()->new Item(new Item.Properties()));
        BLADE_TUNGSTEN = register(matherialList, "blade_tungsten", ()->new Item(new Item.Properties()));
        TURBINE_TUNGSTEN = register(matherialList, "turbine_tungsten", ()->new Item(new Item.Properties()));
        
        TOOTHPICKS = register(matherialList, "toothpicks", ()->new Item(new Item.Properties()));
        DUCTTAPE = register(matherialList, "ducttape", ()->new Item(new Item.Properties()));
        CATALYST_CLAY = register(matherialList, "catalyst_clay", ()->new Item(new Item.Properties()));
        
        WARHEAD_GENERIC_SMALL = register(matherialList, "warhead_generic_small", ()->new Item(new Item.Properties()));
        WARHEAD_GENERIC_MEDIUM = register(matherialList, "warhead_generic_medium", ()->new Item(new Item.Properties()));
        WARHEAD_GENERIC_LARGE = register(matherialList, "warhead_generic_large", ()->new Item(new Item.Properties()));
        WARHEAD_INCENDIARY_SMALL = register(matherialList, "warhead_incendiary_small", ()->new Item(new Item.Properties()));
        WARHEAD_INCENDIARY_MEDIUM = register(matherialList, "warhead_incendiary_medium", ()->new Item(new Item.Properties()));
        WARHEAD_INCENDIARY_LARGE = register(matherialList, "warhead_incendiary_large", ()->new Item(new Item.Properties()));
        WARHEAD_CLUSTER_SMALL = register(matherialList, "warhead_cluster_small", ()->new Item(new Item.Properties()));
        WARHEAD_CLUSTER_MEDIUM = register(matherialList, "warhead_cluster_medium", ()->new Item(new Item.Properties()));
        WARHEAD_CLUSTER_LARGE = register(matherialList, "warhead_cluster_large", ()->new Item(new Item.Properties()));
        WARHEAD_BUSTER_SMALL = register(matherialList, "warhead_buster_small", ()->new Item(new Item.Properties()));
        WARHEAD_BUSTER_MEDIUM = register(matherialList, "warhead_buster_medium", ()->new Item(new Item.Properties()));
        WARHEAD_BUSTER_LARGE = register(matherialList, "warhead_buster_large", ()->new Item(new Item.Properties()));
        WARHEAD_NUCLEAR = register(matherialList, "warhead_nuclear", ()->new Item(new Item.Properties()));
        WARHEAD_MIRV = register(matherialList, "warhead_mirv", ()->new Item(new Item.Properties()));
        WARHEAD_VOLCANO = register(matherialList, "warhead_volcano", ()->new Item(new Item.Properties()));
        
        FUEL_TANK_SMALL = register(matherialList, "fuel_tank_small", ()->new Item(new Item.Properties()));
        FUEL_TANK_MEDIUM = register(matherialList, "fuel_tank_medium", ()->new Item(new Item.Properties()));
        FUEL_TANK_LARGE = register(matherialList, "fuel_tank_large", ()->new Item(new Item.Properties()));
        
        THRUSTER_SMALL = register(matherialList, "thruster_small", ()->new Item(new Item.Properties()));
        THRUSTER_MEDIUM = register(matherialList, "thruster_medium", ()->new Item(new Item.Properties()));
        THRUSTER_LARGE = register(matherialList, "thruster_large", ()->new Item(new Item.Properties()));
        
        SAT_HEAD_MAPPER = register(matherialList, "sat_head_mapper", ()->new Item(new Item.Properties()));
        SAT_HEAD_SCANNER = register(matherialList, "sat_head_scanner", ()->new Item(new Item.Properties()));
        SAT_HEAD_RADAR = register(matherialList, "sat_head_radar", ()->new Item(new Item.Properties()));
        SAT_HEAD_LASER = register(matherialList, "sat_head_laser", ()->new Item(new Item.Properties()));
        SAT_HEAD_RESONATOR = register(matherialList, "sat_head_resonator", ()->new Item(new Item.Properties()));
        
        SEG_10 = register(matherialList, "seg_10", ()->new Item(new Item.Properties()));
        SEG_15 = register(matherialList, "seg_15", ()->new Item(new Item.Properties()));
        SEG_20 = register(matherialList, "seg_20", ()->new Item(new Item.Properties()));
        
        COMBINE_SCRAP = register(matherialList, "combine_scrap", ()->new Item(new Item.Properties()));
        
        SHIMMER_HEAD = register(matherialList, "shimmer_head", ()->new Item(new Item.Properties()));
        SHIMMER_AXE_HEAD = register(matherialList, "shimmer_axe_head", ()->new Item(new Item.Properties()));
        SHIMMER_HANDLE = register(matherialList, "shimmer_handle", ()->new Item(new Item.Properties()));
        
        ENTANGLEMENT_KIT = register(matherialList, "entanglement_kit", ()->new Item(new Item.Properties()));
        
        CIRCUIT = register(matherialList, "circuit", ()->new Item(new Item.Properties()));
        CRT_DISPLAY = register(matherialList, "crt_display", ()->new Item(new Item.Properties()));
//        CIRCUIT_STAR_PIECE = register(matherialList, "circuit_star_piece", ()->new Item(new Item.Properties()));
//        CIRCUIT_STAR_COMPONENT = register(matherialList, "circuit_star_component", ()->new Item(new Item.Properties()));
        CIRCUIT_STAR = register(matherialList, "circuit_star", ()->new Item(new Item.Properties()));
        ASSEMBLY_NUKE = register(matherialList, "assembly_nuke", ()->new Item(new Item.Properties()));
//        CASING = register(matherialList, "casing", ()->new Item(new Item.Properties()));
        
        WIRING_RED_COPPER = register(matherialList, "wiring_red_copper", ()->new Item(new Item.Properties()));
        
//        PELLET_RTG_DEPLETED = register(matherialList, "pellet_rtg_depleted", ()->new Item(new Item.Properties()));
        
        PELLET_RTG_RADIUM = register(matherialList, "pellet_rtg_radium", ()->new Item(new Item.Properties()));
        PELLET_RTG_WEAK = register(matherialList, "pellet_rtg_weak", ()->new Item(new Item.Properties()));
        PELLET_RTG = register(matherialList, "pellet_rtg", ()->new Item(new Item.Properties()));
        PELLET_RTG_STRONTIUM = register(matherialList, "pellet_rtg_strontium", ()->new Item(new Item.Properties()));
        PELLET_RTG_COBALT = register(matherialList, "pellet_rtg_cobalt", ()->new Item(new Item.Properties()));
        PELLET_RTG_ACTINIUM = register(matherialList, "pellet_rtg_actinium", ()->new Item(new Item.Properties()));
        PELLET_RTG_AMERICIUM = register(matherialList, "pellet_rtg_americium", ()->new Item(new Item.Properties()));
        PELLET_RTG_POLONIUM = register(matherialList, "pellet_rtg_polonium", ()->new Item(new Item.Properties()));
        PELLET_RTG_GOLD = register(matherialList, "pellet_rtg_gold", ()->new Item(new Item.Properties()));
        PELLET_RTG_LEAD = register(matherialList, "pellet_rtg_lead", ()->new Item(new Item.Properties()));
        
        TRITIUM_DEUTERIUM_CAKE = register(matherialList, "tritium_deuterium_cake", ()->new Item(new Item.Properties()));
        
        PISTON_SELENIUM = register(matherialList, "piston_selenium", ()->new Item(new Item.Properties()));
        PISTON_SET = register(matherialList, "piston_set", ()->new Item(new Item.Properties()));
//        DRILLBIT = register(matherialList, "drillbit", ()->new Item(new Item.Properties()));
        
        RUNE_BLANK = register(matherialList, "rune_blank", ()->new Item(new Item.Properties()));
        RUNE_ISA = register(matherialList, "rune_isa", ()->new Item(new Item.Properties()));
        RUNE_DAGAZ = register(matherialList, "rune_dagaz", ()->new Item(new Item.Properties()));
        RUNE_HAGALAZ = register(matherialList, "rune_hagalaz", ()->new Item(new Item.Properties()));
        RUNE_JERA = register(matherialList, "rune_jera", ()->new Item(new Item.Properties()));
        RUNE_THURISAZ = register(matherialList, "rune_thurisaz", ()->new Item(new Item.Properties()));
        
        AMS_CATALYST_BLANK = register(matherialList, "ams_catalyst_blank", ()->new Item(new Item.Properties()));
        AMS_CATALYST_ALUMINIUM = register(matherialList, "ams_catalyst_aluminium", ()->new Item(new Item.Properties()));
        AMS_CATALYST_BERYLLIUM = register(matherialList, "ams_catalyst_beryllium", ()->new Item(new Item.Properties()));
        AMS_CATALYST_CAESIUM = register(matherialList, "ams_catalyst_caesium", ()->new Item(new Item.Properties()));
        AMS_CATALYST_CERIUM = register(matherialList, "ams_catalyst_cerium", ()->new Item(new Item.Properties()));
        AMS_CATALYST_COBALT = register(matherialList, "ams_catalyst_cobalt", ()->new Item(new Item.Properties()));
        AMS_CATALYST_COPPER = register(matherialList, "ams_catalyst_copper", ()->new Item(new Item.Properties()));
        AMS_CATALYST_DINEUTRONIUM = register(matherialList, "ams_catalyst_dineutronium", ()->new Item(new Item.Properties()));
        AMS_CATALYST_EUPHEMIUM = register(matherialList, "ams_catalyst_euphemium", ()->new Item(new Item.Properties()));
        AMS_CATALYST_IRON = register(matherialList, "ams_catalyst_iron", ()->new Item(new Item.Properties()));
        AMS_CATALYST_LITHIUM = register(matherialList, "ams_catalyst_lithium", ()->new Item(new Item.Properties()));
        AMS_CATALYST_NIOBIUM = register(matherialList, "ams_catalyst_niobium", ()->new Item(new Item.Properties()));
        AMS_CATALYST_SCHRABIDIUM = register(matherialList, "ams_catalyst_schrabidium", ()->new Item(new Item.Properties()));
        AMS_CATALYST_STRONTIUM = register(matherialList, "ams_catalyst_strontium", ()->new Item(new Item.Properties()));
        AMS_CATALYST_THORIUM = register(matherialList, "ams_catalyst_thorium", ()->new Item(new Item.Properties()));
        AMS_CATALYST_TUNGSTEN = register(matherialList, "ams_catalyst_tungsten", ()->new Item(new Item.Properties()));
        
        CELL_EMPTY = register(matherialList, "cell_empty", ()->new Item(new Item.Properties()));
        CELL_UF6 = register(matherialList, "cell_uf6", ()->new Item(new Item.Properties()));
        CELL_PUF6 = register(matherialList, "cell_puf6", ()->new Item(new Item.Properties()));
        CELL_ANTIMATTER = register(matherialList, "cell_antimatter", ()->new Item(new Item.Properties()));
        CELL_DEUTERIUM = register(matherialList, "cell_deuterium", ()->new Item(new Item.Properties()));
        CELL_TRITIUM = register(matherialList, "cell_tritium", ()->new Item(new Item.Properties()));
        CELL_SAS3 = register(matherialList, "cell_sas3", ()->new Item(new Item.Properties()));
        CELL_ANTI_SCHRABIDIUM = register(matherialList, "cell_anti_schrabidium", ()->new Item(new Item.Properties()));
        CELL_BALEFIRE = register(matherialList, "cell_balefire", ()->new Item(new Item.Properties()));
        
        DEMON_CORE_OPEN = register(matherialList, "demon_core_open", ()->new Item(new Item.Properties()));
        DEMON_CORE_CLOSED = register(matherialList, "demon_core_closed", ()->new Item(new Item.Properties()));
        
//        PA_COIL = register(matherialList, "pa_coil", ()->new Item(new Item.Properties()));
        
        PARTICLE_EMPTY = register(matherialList, "particle_empty", ()->new Item(new Item.Properties()));
        PARTICLE_HYDROGEN = register(matherialList, "particle_hydrogen", ()->new Item(new Item.Properties()));
        PARTICLE_COPPER = register(matherialList, "particle_copper", ()->new Item(new Item.Properties()));
        PARTICLE_LEAD = register(matherialList, "particle_lead", ()->new Item(new Item.Properties()));
        PARTICLE_APROTON = register(matherialList, "particle_aproton", ()->new Item(new Item.Properties()));
        PARTICLE_AELECTRON = register(matherialList, "particle_aelectron", ()->new Item(new Item.Properties()));
        PARTICLE_AMAT = register(matherialList, "particle_amat", ()->new Item(new Item.Properties()));
        PARTICLE_ASCHRAB = register(matherialList, "particle_aschrab", ()->new Item(new Item.Properties()));
        PARTICLE_HIGGS = register(matherialList, "particle_higgs", ()->new Item(new Item.Properties()));
        PARTICLE_MUON = register(matherialList, "particle_muon", ()->new Item(new Item.Properties()));
        PARTICLE_TACHYON = register(matherialList, "particle_tachyon", ()->new Item(new Item.Properties()));
        PARTICLE_STRANGE = register(matherialList, "particle_strange", ()->new Item(new Item.Properties()));
        PARTICLE_DARK = register(matherialList, "particle_dark", ()->new Item(new Item.Properties()));
        PARTICLE_SPARKTICLE = register(matherialList, "particle_sparkticle", ()->new Item(new Item.Properties()));
        PARTICLE_DIGAMMA = register(matherialList, "particle_digamma", ()->new Item(new Item.Properties()));
        PARTICLE_LUTECE = register(matherialList, "particle_lutece", ()->new Item(new Item.Properties()));
    }
    public static void creativeTab(CreativeModeTab.Output pOutput){
        pOutput.accept(LASER_CRYSTAL_DIGAMMA.get());
        pOutput.accept(BATTERY_CREATIVE.get());
        pOutput.accept(BATTERY_GENERIC.get());
        pOutput.accept(BATTERY_ADVANCED.get());
        pOutput.accept(BATTERY_LITHIUM.get());

        pOutput.accept(EGG_GLYPHID.get());
        pOutput.accept(EGG_GLYPHID_TO_BIRTH.get());
        
        matherialList.forEach(itemRegistryObject -> pOutput.accept(itemRegistryObject.get()));
        partList.forEach(itemRegistryObject -> pOutput.accept(itemRegistryObject.get()));
    }
    public static void genModel(ItemModelProvider provider){
        provider.basicItem(LASER_CRYSTAL_DIGAMMA.get());
        provider.basicItem(BATTERY_CREATIVE.get());
        provider.basicItem(BATTERY_GENERIC.get());
        provider.basicItem(BATTERY_ADVANCED.get());
        provider.basicItem(BATTERY_LITHIUM.get());

        singleTexture(provider, EGG_GLYPHID.get(), "egg_glyphid_base");
        singleTexture(provider, EGG_GLYPHID_TO_BIRTH.get(), "egg_glyphid");

        matherialList.forEach(itemRegistryObject -> provider.basicItem(itemRegistryObject.get()));
        partList.forEach(itemRegistryObject -> provider.basicItem(itemRegistryObject.get()));
    }
    public static void languageSupport(LanguageProvider provider){
        provider.add(LASER_CRYSTAL_DIGAMMA.get(),"Layser crystal digamma");
        provider.add(BATTERY_CREATIVE.get(),"Creative battery");
        provider.add(BATTERY_GENERIC.get(),"Genetic battery");
        provider.add(BATTERY_ADVANCED.get(),"Advanced battery");
        provider.add(BATTERY_LITHIUM.get(),"Lithium battery");

        provider.add(EGG_GLYPHID.get(),"Glyphid Egg");
        provider.add(EGG_GLYPHID_TO_BIRTH.get(),"Glypid Egg will birth");

        // 所有物料的名称就是它们内部名称
        matherialList.forEach(itemRegistryObject -> provider.add(itemRegistryObject.get(), generateReversedName(itemRegistryObject.getId().getPath())));
        // 所有零件的名称是颠倒的名称
        partList.forEach(itemRegistryObject -> provider.add(itemRegistryObject.get(), generateReversedName(itemRegistryObject.getId().getPath())));
    }

    public static void singleTexture(ItemModelProvider provider, Item item, String textureName){
        ResourceLocation itemRL = ForgeRegistries.ITEMS.getKey(item);
        provider.getBuilder(itemRL.toString())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", new ResourceLocation(itemRL.getNamespace(), "item/" + textureName));
    }
    protected static String generateOrderlyName(String itemId){
        return Arrays.stream(itemId.split("_")).map(s -> s.substring(0,1).toUpperCase() + s.substring(1)).reduce("",(r,id) -> r + (r.isEmpty() ? "": " ") + id);
    }
    protected static String generateReversedName(String itemId){
        List<String> strings = Arrays.stream(itemId.split("_")).map(s -> s.substring(0, 1).toUpperCase() + s.substring(1)).toList();
        return strings.subList(0, strings.size() - 1).stream().reduce(strings.get(strings.size() - 1), (s, s1) -> s + " " + s1);
    }
    protected static RegistryObject<Item> register(List<RegistryObject<Item>> list, final String name, final Supplier<? extends Item> sup){
        RegistryObject<Item> registryObject = getOrRegister(name, sup);
        list.add(registryObject);
        return registryObject;
    }

    private static RegistryObject<Item> getOrRegister(final String name, final Supplier<? extends Item> sup){
        return HBMItems.ITEMS.getEntries().stream()
                .filter(ro -> ro.getId() != null && ro.getId().getPath().equals(name))
                .findFirst()
                .orElseGet(() -> HBMItems.ITEMS.register(name, sup));
    }

    //===============enum==========================
    public enum EnumCircuitType {
        VACUUM_TUBE,
        CAPACITOR,
        CAPACITOR_TANTALIUM,
        PCB,
        SILICON,
        CHIP,
        CHIP_BISMOID,
        ANALOG,
        BASIC,
        ADVANCED,
        CAPACITOR_BOARD,
        BISMOID,
        CONTROLLER_CHASSIS,
        CONTROLLER,
        CONTROLLER_ADVANCED,
        QUANTUM,
        CHIP_QUANTUM,
        CONTROLLER_QUANTUM,
        ATOMIC_CLOCK,
    }
}
