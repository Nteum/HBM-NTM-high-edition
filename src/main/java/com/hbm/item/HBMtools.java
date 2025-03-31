package com.hbm.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class HBMtools {
    public static RegistryObject<Item> UPGRADE_BASE;
    public static void register(DeferredRegister<Item> ITEMS){
        UPGRADE_BASE = ITEMS.register("upgrade_template",()->new Item(new Item.Properties()));
    }
    public static void creativeTab(CreativeModeTab.Output pOutput){
        pOutput.accept(UPGRADE_BASE.get());
    }
    public static void genModel(ItemModelProvider provider){
        provider.basicItem(UPGRADE_BASE.get());
    }
}
