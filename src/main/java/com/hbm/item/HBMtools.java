package com.hbm.item;

import com.hbm.item.tool.ItemGeigerCounter;
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

    public static RegistryObject<Item> DESIGNATOR;
//    // 流体桶
//    public static RegistryObject<Item> FLUID_BUCKET;
    public static void register(DeferredRegister<Item> ITEMS){
        UPGRADE_BASE = ITEMS.register("upgrade_template",()->new Item(new Item.Properties()));
        DEBUG_WAND = ITEMS.register("debug_wand",()->new ItemDebugWand(new Item.Properties()));
        GEIGER_COUNTER = ITEMS.register("geiger_counter",()->new ItemGeigerCounter(new Item.Properties()));

        DESIGNATOR = ITEMS.register("designator",()->new ItemDesignator(new Item.Properties().stacksTo(1)));
    }
    public static void creativeTab(CreativeModeTab.Output pOutput){
//        pOutput.accept(UPGRADE_BASE.get());
//        pOutput.accept(GEIGER_COUNTER.get());
//
//        pOutput.accept(DEBUG_WAND.get());
    }
    public static void genModel(ItemModelProvider provider){
        provider.basicItem(UPGRADE_BASE.get());
        provider.basicItem(GEIGER_COUNTER.get());
        provider.basicItem(DESIGNATOR.get());

        provider.basicItem(DEBUG_WAND.get());
    }
}
