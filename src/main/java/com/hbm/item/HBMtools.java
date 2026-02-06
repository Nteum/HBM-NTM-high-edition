package com.hbm.item;

import com.hbm.item.tool.ItemBuildWand;
import com.hbm.item.tool.ItemDigammaDiagnostic;
import com.hbm.item.tool.ItemDosimeter;
import com.hbm.item.tool.ItemGeigerCounter;
import com.hbm.item.tool.ItemRBMKTool;
import com.hbm.item.tool.OreScannerItem;
import com.hbm.item.tool.PollutionDetectorItem;
import com.hbm.item.weapon.ItemDesignator;
import com.hbm.utils.debug.ItemDebugWand;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class HBMtools {

    // 升级
    public static RegistryObject<Item> UPGRADE_BASE;
    public static RegistryObject<Item> DEBUG_WAND;
    public static RegistryObject<Item> GEIGER_COUNTER;
    public static RegistryObject<Item> BUILD_WAND;
    public static RegistryObject<Item> RBMK_TOOL;
    public static RegistryObject<Item> DOSIMETER;
    public static RegistryObject<Item> DIGAMMA_DIAGNOSTIC;
    public static RegistryObject<Item> POLLUTION_DETECTOR;
    public static RegistryObject<Item> ORE_SCANNER;

    public static RegistryObject<Item> DESIGNATOR;

    // // 流体桶
    // public static RegistryObject<Item> FLUID_BUCKET;
    public static void register(DeferredRegister<Item> ITEMS) {
        UPGRADE_BASE = ITEMS.register("upgrade_template", () -> new Item(new Item.Properties()));
        BUILD_WAND = ITEMS.register("wand", () -> new ItemBuildWand(new Item.Properties().stacksTo(1)));
        DEBUG_WAND = ITEMS.register("debug_wand", () -> new ItemDebugWand(new Item.Properties()));
        GEIGER_COUNTER = ITEMS.register("geiger_counter", () -> new ItemGeigerCounter(new Item.Properties()));
        RBMK_TOOL = ITEMS.register("rbmk_tool", () -> new ItemRBMKTool(new Item.Properties()));
        DOSIMETER = ITEMS.register("dosimeter", () -> new ItemDosimeter(new Item.Properties()));
        DIGAMMA_DIAGNOSTIC = ITEMS.register("digamma_diagnostic",
                () -> new ItemDigammaDiagnostic(new Item.Properties()));
        POLLUTION_DETECTOR = ITEMS.register("pollution_detector",
                () -> new PollutionDetectorItem(new Item.Properties()));
        ORE_SCANNER = ITEMS.register("ore_density_scanner", () -> new OreScannerItem(new Item.Properties()));

        DESIGNATOR = ITEMS.register("designator", () -> new ItemDesignator(new Item.Properties().stacksTo(1)));
    }

    public static void creativeTab(CreativeModeTab.Output pOutput) {
        pOutput.accept(UPGRADE_BASE.get());
        pOutput.accept(GEIGER_COUNTER.get());

        pOutput.accept(BUILD_WAND.get());
        pOutput.accept(DEBUG_WAND.get());
        pOutput.accept(DESIGNATOR.get());
        pOutput.accept(RBMK_TOOL.get());
        pOutput.accept(DOSIMETER.get());
        pOutput.accept(DIGAMMA_DIAGNOSTIC.get());
        pOutput.accept(POLLUTION_DETECTOR.get());
        pOutput.accept(ORE_SCANNER.get());
        pOutput.accept(HBMItems.HAND_DRILL.get());
        pOutput.accept(HBMItems.HAND_DRILL_DESH.get());
        pOutput.accept(HBMItems.SCREWDRIVER.get());
        pOutput.accept(HBMItems.reacher.get());
    }

    public static void genModel(ItemModelProvider provider) {
        provider.basicItem(UPGRADE_BASE.get());
        provider.basicItem(GEIGER_COUNTER.get());
        provider.basicItem(DESIGNATOR.get());
        provider.basicItem(RBMK_TOOL.get());
        provider.basicItem(DOSIMETER.get());
        provider.basicItem(DIGAMMA_DIAGNOSTIC.get());
        provider.basicItem(POLLUTION_DETECTOR.get());
        provider.basicItem(ORE_SCANNER.get());

        provider.basicItem(BUILD_WAND.get());
        provider.basicItem(DEBUG_WAND.get());
    }
}
