package com.hbm.item;

import com.hbm.datagen.LanguageProvider;
import com.hbm.item.tool.BatteryItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

//一些零部件
public class HBMComponent {
    public static RegistryObject<Item> LASER_CRYSTAL_DIGAMMA;
    //工业元件
    public static RegistryObject<Item> BATTERY_CREATIVE;
    public static RegistryObject<Item> BATTERY_GENERIC;
    public static RegistryObject<Item> BATTERY_ADVANCED;
    public static RegistryObject<Item> BATTERY_LITHIUM;
    public static void register(DeferredRegister<Item> ITEMS){
        LASER_CRYSTAL_DIGAMMA = ITEMS.register("laser_crystal_digamma",()->new Item(new Item.Properties()));
        BATTERY_CREATIVE = ITEMS.register("battery_creative",()->new BatteryItem(-1, 1_000_000L, new Item.Properties().stacksTo(1)));
        BATTERY_GENERIC = ITEMS.register("battery_generic",()->new BatteryItem(true,5_000, 100, new Item.Properties()));
        BATTERY_ADVANCED = ITEMS.register("battery_advanced",()->new BatteryItem(60_000, 500, new Item.Properties()));
        BATTERY_LITHIUM = ITEMS.register("battery_lithium",()->new BatteryItem(250_000, 1000, new Item.Properties()));
    }
    public static void creativeTab(CreativeModeTab.Output pOutput){
        pOutput.accept(LASER_CRYSTAL_DIGAMMA.get());
        pOutput.accept(BATTERY_CREATIVE.get());
//        ItemStack itemStack = new ItemStack(BATTERY_GENERIC.get());
//        itemStack.setDamageValue(itemStack.getMaxDamage());
//        pOutput.accept(itemStack);
        pOutput.accept(BATTERY_GENERIC.get());
        pOutput.accept(BATTERY_ADVANCED.get());
        pOutput.accept(BATTERY_LITHIUM.get());
    }
    public static void genModel(ItemModelProvider provider){
        provider.basicItem(LASER_CRYSTAL_DIGAMMA.get());
        provider.basicItem(BATTERY_CREATIVE.get());
        provider.basicItem(BATTERY_GENERIC.get());
        provider.basicItem(BATTERY_ADVANCED.get());
        provider.basicItem(BATTERY_LITHIUM.get());
    }
    public static void languageSupport(LanguageProvider provider){
        provider.add(LASER_CRYSTAL_DIGAMMA.get(),"Layser crystal digamma");
        provider.add(BATTERY_CREATIVE.get(),"Creative battery");
        provider.add(BATTERY_GENERIC.get(),"Genetic battery");
        provider.add(BATTERY_ADVANCED.get(),"Advanced battery");
        provider.add(BATTERY_LITHIUM.get(),"Lithium battery");
    }
}
