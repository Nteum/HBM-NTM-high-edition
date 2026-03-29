package com.hbm.registries;

import com.hbm.Inventory.material.HBMMatForm;
import com.hbm.Inventory.material.HBMMatter;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hbm.registries.ModTags.Items.*;
import static net.minecraftforge.common.Tags.Items.*;

public class HBMMatters {
    private static final List<HBMMatter> ALL_MATTERS = new ArrayList<>();
    // 反向查找表：根据 Item 找到对应的 Matter
    public static final Map<Item, HBMMatter> ITEM_TO_MATTER = new HashMap<>();
    // 根据 Item 找到对应的形状（是锭、是粉还是粒）
    private static final Map<Item, TagKey<Item>> ITEM_TO_SHAPE = new HashMap<>();
    private static final Map<Fluid, HBMMatter> FLUID_TO_MATTER = new HashMap<>();

    // 材料列表
    //Vanilla and vanilla-like
    public static final HBMMatter WOOD = register(new HBMMatter("wood",0x896727, 0x281E0B, 0x896727).shapes(STOCK, GRIP).gen(entry -> entry.addKeyOut(NORMAL).addKeyIn(ItemTags.LOGS, Tags.Items.BARRELS_WOODEN)));
    public static final HBMMatter BONE = register(new HBMMatter("bone", 0xFFFEEE, 0x797870, 0xEDEBCA).shapes(GRIP).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter CARBON = register(new HBMMatter("carbon", 0x363636, 0x030303, 0x404040).shapes(WIRE, Tags.Items.STORAGE_BLOCKS).toFluid(4).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter STONE = register(new HBMMatter("stone", 0x7F7F7F, 0x353535, 0x4D2F23).toFluid(1).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter COAL = register(new HBMMatter("coal", 0x363636, 0x030303, 0x404040).convert(CARBON, 2, 1).shapes(FRAGMENT)
            .shape(Tags.Items.GEMS, ItemTags.COALS).shape(STORAGE_BLOCKS, STORAGE_BLOCKS_COAL).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter LIGNITE = register(new HBMMatter("lignite", 0x542D0F, 0x261508, 0x472913).convert(CARBON, 3, 1).shapes(FRAGMENT).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter COAL_COKE = register(new HBMMatter("coal_coke").convert(CARBON, 4, 3).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter PET_COKE = register(new HBMMatter("pet_coke").convert(CARBON, 4, 3).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter LIG_COKE = register(new HBMMatter("lig_coke").convert(CARBON, 4, 3).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter GRAPHITE = register(new HBMMatter("graphite").convert(CARBON, 1, 1).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter DIAMOND = register(new HBMMatter("diamond", 0xFFFFFF, 0x1B7B6B, 0x8CF4E2).convert(CARBON, 1, 1).shapes(FRAGMENT).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter IRON = register(new HBMMatter("iron", 0xFFFFFF, 0x353535, 0xFFA259)
            .shape(Tags.Items.INGOTS, INGOTS_IRON).shape(Tags.Items.NUGGETS, NUGGETS_IRON)
            .shapes(PLATE, FRAGMENT, DUST, PIPE, CASTPLATE, WELDEDPLATE, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter WROUGHT_IRON = register(new HBMMatter("wrought_iron", 0xFAAB89, 0xFAAB89, 0xFAAB89).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter PIG_IRON = register(new HBMMatter("pig_iron", 0xFF8B59, 0xFF8B59, 0xFF8B59).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter METEORIC_IRON = register(new HBMMatter("meteoric_iron", 0x715347, 0x715347, 0x715347).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter GOLD = register(new HBMMatter("gold", 0xFFFF8B, 0xC26E00, 0xE8D754).shapes(FRAGMENT, WIRE, Tags.Items.NUGGETS, DUST, DENSEWIRE, CASTPLATE, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter REDSTONE = register(new HBMMatter("redstone", 0xE3260C, 0x700E06, 0xFF1000).shapes(FRAGMENT).toFluid(1).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter OBSIDIAN = register(new HBMMatter("obsidain", 0x3D234D, 0x3D234D, 0x3D234D).toFluid(1).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter GLOWSTONE = register(new HBMMatter("glowstone", 0xFFFF00, 0x535300, 0xFFFF00).shapes(FRAGMENT).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter HEMATITE = register(new HBMMatter("hematite", 0xDFB7AE, 0x5F372E, 0x6E463D).toFluid(4).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter BAUXITE = register(new HBMMatter("bauxite", 0xF4BA30, 0xAA320A, 0xE2560F).shapes(FRAGMENT).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter MALACHITE = register(new HBMMatter("malachite", 0xA2F0C8, 0x227048, 0x61AF87).toFluid(4).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter CRYOLITE = register(new HBMMatter("cryolite", 0xCBC2A4, 0x8B711F, 0x8B701A).shapes(FRAGMENT).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter CONGLOMERATE = register(new HBMMatter("conglomerate", 0x797979, 0x797979, 0x797979).toFluid(4).shapes(FRAGMENT).gen(entry -> entry.addKeyOut(NORMAL)));
    //Radioactive
    public static final HBMMatter URANIUM = register(new HBMMatter("uranium", 0xC1C7BD, 0x2B3227, 0x9AA196).shapes(FRAGMENT, Tags.Items.NUGGETS, BILLET, DUST, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter U233 = register(new HBMMatter("u233", 0xC1C7BD, 0x2B3227, 0x9AA196).shapes(FRAGMENT, Tags.Items.NUGGETS, BILLET, DUST, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter U235 = register(new HBMMatter("u235", 0xC1C7BD, 0x2B3227, 0x9AA196).shapes(FRAGMENT, Tags.Items.NUGGETS, BILLET, DUST, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter U238 = register(new HBMMatter("u238", 0xC1C7BD, 0x2B3227, 0x9AA196).shapes(FRAGMENT, Tags.Items.NUGGETS, BILLET, DUST, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter THORIUM = register(new HBMMatter("thorium", 0xBF825F, 0x1C0000, 0xBF825F).shapes(FRAGMENT, Tags.Items.NUGGETS, BILLET, DUST, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter PLUTONIUM = register(new HBMMatter("plutonium", 0x9AA3A0, 0x111A17, 0x78817E).shapes(FRAGMENT, Tags.Items.NUGGETS, BILLET, DUST, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter RGP = register(new HBMMatter("rgp", 0x9AA3A0, 0x111A17, 0x78817E).shapes(Tags.Items.NUGGETS, BILLET, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter PU238 = register(new HBMMatter("pu238", 0xFFBC59, 0xFF8E2B, 0x78817E).shapes(Tags.Items.NUGGETS, BILLET, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter PU239 = register(new HBMMatter("pu239", 0x9AA3A0, 0x111A17, 0x78817E).shapes(FRAGMENT, Tags.Items.NUGGETS, BILLET, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter PU240 = register(new HBMMatter("pu240", 0x9AA3A0, 0x111A17, 0x78817E).shapes(Tags.Items.NUGGETS, BILLET, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter PU241 = register(new HBMMatter("pu241", 0x9AA3A0, 0x111A17, 0x78817E).shapes(Tags.Items.NUGGETS, BILLET, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter RGA = register(new HBMMatter("rga", 0xCEB3B9, 0x3A1C21, 0x93767B).shapes(Tags.Items.NUGGETS, BILLET, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter AM241 = register(new HBMMatter("am241", 0xCEB3B9, 0x3A1C21, 0x93767B).shapes(Tags.Items.NUGGETS, BILLET, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter AM242 = register(new HBMMatter("am242", 0xCEB3B9, 0x3A1C21, 0x93767B).shapes(Tags.Items.NUGGETS, BILLET, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter NEPTUNIUM = register(new HBMMatter("neptunium", 0xA6B2A6, 0x030F03, 0x647064).shapes(FRAGMENT, Tags.Items.NUGGETS, BILLET, DUST, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter POLONIUM = register(new HBMMatter("polonium", 0x968779, 0x3D1509, 0x715E4A).shapes(FRAGMENT, Tags.Items.NUGGETS, BILLET, DUST, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter TECHNETIUM = register(new HBMMatter("technetium", 0xFAFFFF, 0x576C6C, 0xCADFDF).shapes(FRAGMENT, Tags.Items.NUGGETS, BILLET, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter RADIUM = register(new HBMMatter("radium", 0xFCFCFC, 0xADBFBA, 0xE9FAF6).shapes(FRAGMENT, Tags.Items.NUGGETS, BILLET, DUST, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter ACTINIUM = register(new HBMMatter("actinium", 0xECE0E0, 0x221616, 0x958989).shapes(Tags.Items.NUGGETS, BILLET).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter CO60 = register(new HBMMatter("co60", 0xC2D1EE, 0x353554, 0x8F72AE).shapes(FRAGMENT, Tags.Items.NUGGETS, BILLET, DUST).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter AU198 = register(new HBMMatter("au198", 0xFFFF8B, 0xC26E00, 0xE8D754).shapes(FRAGMENT, Tags.Items.NUGGETS, BILLET, DUST).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter PB209 = register(new HBMMatter("pb209", 0xB38A94, 0x12020E, 0x7B535D).shapes(FRAGMENT, Tags.Items.NUGGETS, BILLET, DUST).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter SCHRABIDIUM = register(new HBMMatter("schrabidium", 0x32FFFF, 0x005C5C, 0x32FFFF).shapes(FRAGMENT, Tags.Items.NUGGETS, WIRE, BILLET, DUST, DENSEWIRE, PLATE, CASTPLATE, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter SOLINIUM = register(new HBMMatter("solinium", 0xA2E6E0, 0x00433D, 0x72B6B0).shapes(FRAGMENT, Tags.Items.NUGGETS, BILLET, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter SCHRABIDATE = register(new HBMMatter("schrabidate", 0x77C0D7, 0x39005E, 0x6589B4).shapes(DUST, DENSEWIRE, CASTPLATE, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter SCHRARANIUM = register(new HBMMatter("schraranium", 0x2B3227, 0x2B3227, 0x24AFAC).shapes(Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter GHIORSIUM = register(new HBMMatter("ghiorsium", 0xF4EFE1, 0x2A3306, 0xC6C6A1).shapes(FRAGMENT, Tags.Items.NUGGETS, BILLET, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter AUSTRALIUM = register(new HBMMatter("australium", 0xFFFF00).shapes(FRAGMENT).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter TASMANITE = register(new HBMMatter("tasmanite", 0xFFFF00).shapes(FRAGMENT).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter AYERITE = register(new HBMMatter("ayerite", 0xFFFF00).shapes(FRAGMENT).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    //Base metals
    public static final HBMMatter TITANIUM = register(new HBMMatter("titanium", 0xF7F3F2, 0x4F4C4B, 0xA99E79).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS).shapes(FRAGMENT, DUST, PLATE, DENSEWIRE, CASTPLATE, WELDEDPLATE, SHELL, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter COPPER = register(new HBMMatter("copper", 0xFDCA88, 0x601E0D, 0xC18336).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, FRAGMENT, WIRE, DUST, PLATE, DENSEWIRE, CASTPLATE, WELDEDPLATE, SHELL, PIPE, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter TUNGSTEN = register(new HBMMatter("tungsten", 0x868686, 0x000000, 0x977474).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, FRAGMENT, WIRE, BOLT, DUST, DENSEWIRE, CASTPLATE, WELDEDPLATE, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter ALUMINIUM = register(new HBMMatter("aluminium", 0xFFFFFF, 0x344550, 0xD0B8EB).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, FRAGMENT, WIRE, DUST, PLATE, CASTPLATE, WELDEDPLATE, SHELL, PIPE, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter LEAD = register(new HBMMatter("lead", 0xA6A6B2, 0x03030F, 0x646470).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, FRAGMENT, Tags.Items.NUGGETS, WIRE, BOLT, DUST, PLATE, CASTPLATE, PIPE, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter BISMUTH = register(new HBMMatter("bismuth", 0xB200FF).shapes(FRAGMENT, Tags.Items.NUGGETS, Tags.Items.INGOTS, DUST, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter ARSENIC = register(new HBMMatter("arsenic", 0x6CBABA, 0x242525, 0x558080).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, FRAGMENT, Tags.Items.NUGGETS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter TANTALIUM = register(new HBMMatter("tantalium", 0xFFFFFF, 0x1D1D36, 0xA89B74).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, FRAGMENT, Tags.Items.NUGGETS, DUST, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter NEODYMIUM = register(new HBMMatter("neodymium", 0xE6E6B6, 0x1C1C00, 0x8F8F5F).shapes(FRAGMENT, Tags.Items.NUGGETS, SMALL_DUST, Tags.Items.INGOTS, DUST, DENSEWIRE, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter NIOBIUM = register(new HBMMatter("niobium", 0xB76EC9, 0x2F2D42, 0xD576B1).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, FRAGMENT, Tags.Items.NUGGETS, SMALL_DUST, DUST, DENSEWIRE, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter BERYLLIUM = register(new HBMMatter("beryllium", 0xB2B2A6, 0x0F0F03, 0xAE9572).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, FRAGMENT, Tags.Items.NUGGETS, DUST, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter EMERALD = register(new HBMMatter("emerald", 0xBAFFD4, 0x003900, 0x17DD62).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, FRAGMENT, DUST, Tags.Items.GEMS, Tags.Items.STORAGE_BLOCKS).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter COBALT = register(new HBMMatter("cobalt", 0xC2D1EE, 0x353554, 0x8F72AE).shapes(FRAGMENT, Tags.Items.NUGGETS, SMALL_DUST, Tags.Items.INGOTS, DUST, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter BORON = register(new HBMMatter("boron", 0xBDC8D2, 0x29343E, 0xAD72AE).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, FRAGMENT, SMALL_DUST, DUST, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter BORAX = register(new HBMMatter("borax", 0xFFFFFF, 0x946E23, 0xFFECC6).shapes(FRAGMENT, Tags.Items.INGOTS, DUST).toFluid(1).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter LANTHANIUM = register(new HBMMatter("lanthanium", 0xC8E0E0, 0x3B5353, 0xA1B9B9).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, FRAGMENT, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter ZIRCONIUM = register(new HBMMatter("zirconium", 0xE3DCBE, 0x3E3719, 0xADA688).shapes(FRAGMENT, Tags.Items.NUGGETS, WIRE, SMALL_DUST, Tags.Items.INGOTS, DUST, CASTPLATE, WELDEDPLATE, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter SODIUM = register(new HBMMatter("sodium", 0xD3BF9E, 0x3A5A6B, 0x7E9493).shapes(FRAGMENT, Tags.Items.INGOTS, DUST).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter SODALITE = register(new HBMMatter("sodalite", 0xDCE5F6, 0x4927B4, 0x96A7E6).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, FRAGMENT, Tags.Items.GEMS).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter STRONTIUM = register(new HBMMatter("strontium", 0xF1E8BA, 0x271E00, 0xCAC193).shapes(FRAGMENT, Tags.Items.INGOTS, DUST).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter CALCIUM = register(new HBMMatter("calcium", 0xCFCFA6, 0x747F6E, 0xB7B784).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, DUST).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter LITHIUM = register(new HBMMatter("lithium", 0xFFFFFF, 0x818181, 0xD6D6D6).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, FRAGMENT, DUST, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter SULFUR = register(new HBMMatter("sulfur", 0xFCEE80, 0xBDA022, 0xF1DF68).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, FRAGMENT, DUST, Tags.Items.STORAGE_BLOCKS).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter KNO = register(new HBMMatter("kno", 0xD4D4D4, 0x969696, 0xC9C9C9).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, FRAGMENT, DUST, Tags.Items.STORAGE_BLOCKS).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter FLUORITE = register(new HBMMatter("fluorite", 0xFFFFFF, 0xB0A192, 0xE1DBD4).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, FRAGMENT, DUST, Tags.Items.STORAGE_BLOCKS).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter PHOSPHORUS = register(new HBMMatter("phosphorus", 0xCB0213, 0x600006, 0xBA0615).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, FRAGMENT, DUST, Tags.Items.STORAGE_BLOCKS).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter PHOSPHORUS_W = register(new HBMMatter("phosphorus_w", 0xF5F5ED, 0xC4BD9A, 0xC4BD9A).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, FRAGMENT, DUST).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter CHLOROCALCITE = register(new HBMMatter("chlorocalcite", 0xF7E761, 0x475B46, 0xB8B963).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, FRAGMENT, DUST).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter MOLYSITE = register(new HBMMatter("molysite", 0xF9E97B, 0x216E00, 0xD0D264).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, FRAGMENT, DUST).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter CINNABAR = register(new HBMMatter("cinnabar", 0xD87070, 0x993030, 0xBF4E4E).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, FRAGMENT, Tags.Items.GEMS).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter CADMIUM = register(new HBMMatter("cadmium", 0xFFFADE, 0x350000, 0xA85600).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, FRAGMENT, DUST).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter SILICON = register(new HBMMatter("silicon", 0xD1D7DF, 0x1A1A3D, 0x878B9E).shapes(FRAGMENT, Tags.Items.NUGGETS, Tags.Items.INGOTS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter ASBESTOS = register(new HBMMatter("asbestos", 0xD8D9CF, 0x616258, 0xB0B3A8).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, FRAGMENT, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter QUARTZ = register(new HBMMatter("quartz", 0xF7F5F2, 0x6F5D5A, 0xF7F5F2).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, FRAGMENT).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter OSMIRIDIUM = register(new HBMMatter("osmiridium", 0xDBE3EF, 0x7891BE, 0xACBDD9).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, Tags.Items.NUGGETS, CASTPLATE, WELDEDPLATE).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter NICKEL = register(new HBMMatter("nickel", 0xE8D1C7, 0x87756E, 0xAE9572).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, FRAGMENT, Tags.Items.NUGGETS, DUST, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter GALLIUM = register(new HBMMatter("gallium", 0x52687F).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, FRAGMENT, Tags.Items.NUGGETS, DUST, SMALL_DUST).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter ZINC = register(new HBMMatter("zinc", 0xD7CBDA, 0x7A7277, 0xA79DA8).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, FRAGMENT, Tags.Items.NUGGETS, DUST, WIRE).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter BROMINE = register(new HBMMatter("bromine", 0xFF642B, 0x720000, 0xFF642B).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, FRAGMENT).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter IODINE = register(new HBMMatter("iodine", 0x7A8796, 0x3F3049, 0x7A8796).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, FRAGMENT).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter HAFNIUM = register(new HBMMatter("hafnium", 0xFFF8C7, 0x2E1600, 0xFFF8C7).shapes(Tags.Items.INGOTS, Tags.Items.NUGGETS, FRAGMENT, DUST).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter IRIDIUM = register(new HBMMatter("iridium", 0xB8D0FF).shapes(Tags.Items.INGOTS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    //Alloys
    public static final HBMMatter STEEL = register(new HBMMatter("steel", 0xAFAFAF, 0x0F0F0F, 0x4A4A4A).shapes(SMALL_DUST, BOLT, WIRE, DUST, PLATE, CASTPLATE, WELDEDPLATE, SHELL, PIPE, Tags.Items.STORAGE_BLOCKS, LIGHTBARREL, HEAVYBARREL, LIGHTRECEIVER, GRIP).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter MINGRADE = register(new HBMMatter("mingrade", 0xFFBA7D, 0xAF1700, 0xE44C0F).shapes(WIRE, DUST, DENSEWIRE, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter ALLOY = register(new HBMMatter("alloy", 0xFF8330, 0x700000, 0xFF7318).shapes(WIRE, DUST, DENSEWIRE, PLATE, CASTPLATE, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter DURA = register(new HBMMatter("dura", 0x82A59C, 0x06281E, 0x42665C).shapes(BOLT, DUST, PLATE, CASTPLATE, PIPE, Tags.Items.STORAGE_BLOCKS, LIGHTBARREL, HEAVYBARREL, LIGHTRECEIVER, HEAVYRECEIVER, GRIP).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter DESH = register(new HBMMatter("desh", 0xFF6D6D, 0x720000, 0xF22929).shapes(DUST, CASTPLATE, Tags.Items.STORAGE_BLOCKS, LIGHTBARREL, HEAVYBARREL, LIGHTRECEIVER, STOCK, GRIP).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter STAR = register(new HBMMatter("star", 0xCCCCEA, 0x11111A, 0xA5A5D3).shapes(DUST, DENSEWIRE, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter FERRO = register(new HBMMatter("ferro", 0xB7B7C9, 0x101022, 0x6B6B8B).shapes(CASTPLATE, HEAVYBARREL, HEAVYRECEIVER).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter TCALLOY = register(new HBMMatter("tcalloy", 0xD4D6D6, 0x323D3D, 0x9CA6A6).shapes(DUST, CASTPLATE, WELDEDPLATE, LIGHTBARREL, HEAVYBARREL, LIGHTRECEIVER, HEAVYRECEIVER).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter CDALLOY = register(new HBMMatter("cdalloy", 0xF7DF8F, 0x604308, 0xFBD368).shapes(CASTPLATE, WELDEDPLATE, LIGHTBARREL, HEAVYBARREL, LIGHTRECEIVER, HEAVYRECEIVER).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter BBRONZE = register(new HBMMatter("bbronze", 0xE19A69, 0x485353, 0x987D65).shapes(CASTPLATE, LIGHTBARREL, LIGHTRECEIVER, HEAVYRECEIVER).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter ABRONZE = register(new HBMMatter("abronze", 0xDB9462, 0x203331, 0x77644D).shapes(CASTPLATE, LIGHTBARREL, LIGHTRECEIVER, HEAVYRECEIVER).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter BSCCO = register(new HBMMatter("bscco", 0x767BF1, 0x000000, 0x5E62C0).shapes(DENSEWIRE).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter MAGTUNG = register(new HBMMatter("magtung", 0x22A2A2, 0x0F0F0F, 0x22A2A2).shapes(WIRE, DUST, DENSEWIRE, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter CMB = register(new HBMMatter("cmb", 0x6F6FB4, 0x000011, 0x6F6FB4).shapes(DUST, PLATE, CASTPLATE, WELDEDPLATE, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter DNT = register(new HBMMatter("dnt", 0x7582B9, 0x16000E, 0x455289).shapes(DUST, DENSEWIRE, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter FLUX = register(new HBMMatter("flux", 0xF1E0BB, 0x6F6256, 0xDECCAD).toFluid(4).shapes(DUST).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter SLAG = register(new HBMMatter("slag", 0x554940, 0x34281F, 0x6C6562).shapes(Tags.Items.INGOTS, Tags.Items.STORAGE_BLOCKS).toFluid(1).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter MUD = register(new HBMMatter("mud", 0xBCB5A9, 0x481213, 0x96783B).toFluid(1).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter GUNMETAL = register(new HBMMatter("gunmetal", 0xFFEF3F, 0xAD3600, 0xF9C62C).shapes(LIGHTBARREL, HEAVYBARREL, LIGHTRECEIVER, HEAVYRECEIVER, MECHANISM, STOCK, GRIP).toFluid(1).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter WEAPONSTEEL = register(new HBMMatter("weaponsteel", 0xA0A0A0, 0x000000, 0x808080).shapes(CASTPLATE, SHELL, LIGHTBARREL, HEAVYBARREL, LIGHTRECEIVER, HEAVYRECEIVER, MECHANISM, STOCK, GRIP).toFluid(1).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter SATURN = register(new HBMMatter("saturn", 0x3AC4DA, 0x09282C, 0x30A4B7).shapes(PLATE, CASTPLATE, SHELL, Tags.Items.STORAGE_BLOCKS, LIGHTBARREL, HEAVYBARREL, LIGHTRECEIVER, HEAVYRECEIVER, MECHANISM, STOCK, GRIP).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    //Space extension alloys
    public static final HBMMatter GAAS = register(new HBMMatter("gaas", 0x6F4A57).shapes(Tags.Items.NUGGETS, BILLET).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter STAINLESS = register(new HBMMatter("stainless", 0xD8D8D8, 0x474747, 0x4A4A4A).shapes(PLATE, WELDEDPLATE, CASTPLATE).toFluid(1).gen(entry -> entry.addKeyOut(METAL)));
    public static final HBMMatter RICH_MAGMA = register(new HBMMatter("rich_magma", 0x7F7F7F, 0x353555, 0xFF6212).toFluid(1).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter SEMTEX = register(new HBMMatter("semtex", 0xEDAA28, 0x825D16, 0xF0B090).shapes(FRAGMENT).gen(entry -> entry.addKeyOut(NORMAL)));
    //Extension
    public static final HBMMatter RAREEARTH = register(new HBMMatter("rareearth", 0xC1BDBD, 0x384646, 0x7B7F7F).shapes(FRAGMENT).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter POLYMER = register(new HBMMatter("polymer", 0x363636, 0x040404, 0x272727).shapes(FRAGMENT, STOCK, GRIP).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter BAKELITE = register(new HBMMatter("bakelite", 0xF28086, 0x2B0608, 0xC93940).shapes(STOCK, GRIP).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter RUBBER = register(new HBMMatter("rubber", 0x817F75, 0x0F0D03, 0x4B4A3F).shapes(FRAGMENT, DUST, PIPE, GRIP).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter HARDPLASTIC = register(new HBMMatter("hardplastic", 0xEDE7C4, 0x908A67, 0xE1DBB8).shapes(STOCK, GRIP).gen(entry -> entry.addKeyOut(NORMAL)));
    public static final HBMMatter PVC = register(new HBMMatter("pvc", 0xFCFCFC, 0x9F9F9F, 0xF0F0F0).shapes(FRAGMENT, DUST, STOCK, GRIP).gen(entry -> entry.addKeyOut(NORMAL)));
    public static HBMMatter register(HBMMatter matter){
        ALL_MATTERS.add(matter);
        return matter;
    }
    public static void buildCache() {
        ITEM_TO_MATTER.clear();
        // 遍历你之前注册的所有 HBMMatter
        for (HBMMatter matter : ALL_MATTERS) {
            ForgeRegistries.ITEMS.tags().getTag(matter.key()).forEach(holder -> ITEM_TO_MATTER.put(holder.asItem(), matter));
            for (Map.Entry<TagKey<Item>, TagKey<Item>> entry : matter.getShapes().entrySet()) {
                TagKey<Item> shapeType = entry.getKey(); // 例如 BILLET
                TagKey<Item> specificTag = entry.getValue(); // 例如 forge:ingots/titanium

                // 关键：获取这个 Tag 下的所有物品并建立映射
                ForgeRegistries.ITEMS.tags().getTag(specificTag).forEach(holder -> {
                    Item item = holder.asItem();
                    ITEM_TO_MATTER.put(item, matter);
                    ITEM_TO_SHAPE.put(item, shapeType);
                });
            }
            if (matter.fluid() != null) FLUID_TO_MATTER.put(matter.fluid(), matter);
        }
    }

    public static boolean canSmelt(ItemStack stack) {
        if (stack.isEmpty()) return false;

        Item item = stack.getItem();
        HBMMatter matter = ITEM_TO_MATTER.get(item);

        // 如果查到了 Matter，说明它是你定义的材料之一
        if (matter != null) {
            return matter.getConvertMat() != null ? matter.getConvertMat().canMolten() : matter.canMolten();
        }

        return false;
    }

    public static FluidStack getMoltenMatter(ItemStack stack){
        if (stack.isEmpty()) return FluidStack.EMPTY;
        Item item = stack.getItem();
        HBMMatter matter = ITEM_TO_MATTER.get(item);
        HBMMatter convertMat = matter.getConvertMat();
        HBMMatForm matterFormat = HBMMatForm.MATTER_FORMATS.get(ITEM_TO_SHAPE.get(item));
        if (matter != null && (matter.canMolten() || convertMat.canMolten()) && matterFormat != null) {
            if (convertMat == null)
                return new FluidStack(matter.fluid(), matterFormat.quantity);
            else
                return new FluidStack(convertMat.fluid(), matterFormat.quantity * matter.convOut / matter.convIn);
        }
        return FluidStack.EMPTY;
    }

    public static HBMMatter getMatterFromFluid(FluidStack fluidStack){
        return FLUID_TO_MATTER.get(fluidStack.getFluid());
    }

    public static Item getItemWithMatForm(FluidStack fluidStack, HBMMatForm matForm){
        HBMMatter matter = getMatterFromFluid(fluidStack);
        TagKey<Item> format = matForm.getFormat();
        for (Map.Entry<Item, TagKey<Item>> entry : ITEM_TO_SHAPE.entrySet()) {
            if (ITEM_TO_MATTER.get(entry.getKey()) == matter && entry.getValue() == format){
                return entry.getKey();
            }
        }
        return null;
    }

    public static boolean doFluidValidToForm(FluidStack fluidStack, HBMMatForm matForm){
        HBMMatter matter = getMatterFromFluid(fluidStack);
        if (matter == null || matForm == null) return false;
        for (TagKey<Item> tagKey : matter.getShapes().keySet()) {
            if (tagKey.equals(matForm.getFormat())) return true;
        }
        return false;
    }
}
