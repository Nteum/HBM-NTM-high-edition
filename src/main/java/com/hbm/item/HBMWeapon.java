package com.hbm.item;

import com.hbm.item.weapon.ItemMissle;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class HBMWeapon {
    public static RegistryObject<Item> MP_WARHEAD_15_BALEFIRE;
    public static void register(DeferredRegister<Item> ITEMS){
        MP_WARHEAD_15_BALEFIRE = ITEMS.register("mp_warhead_15_balefire",()->new ItemMissle(new Item.Properties()));
    }
    public static void creativeTab(CreativeModeTab.Output pOutput){
        pOutput.accept(MP_WARHEAD_15_BALEFIRE.get());
    }
    public static void genModel(ItemModelProvider provider){

    }
}
