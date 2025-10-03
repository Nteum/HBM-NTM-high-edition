package com.hbm.item;

import com.hbm.item.weapon.ItemMissile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class HBMWeapon extends HBMComponent{
    protected static final List<RegistryObject<Item>> weaponList = new ArrayList<>();
    public static RegistryObject<Item> MP_WARHEAD_15_BALEFIRE;
    // Armor
    public static void register(DeferredRegister<Item> ITEMS){
        MP_WARHEAD_15_BALEFIRE = ITEMS.register("mp_warhead_15_balefire",()->new ItemMissile(new Item.Properties(), ItemMissile.MissileTier.TIER1));

//        MISSILE_TEST = register(weaponList, "missile_test", ()->new ItemMissile(new Item.Properties(), ItemMissile.MissileTier.TIER1));
    }
    public static void creativeTab(CreativeModeTab.Output pOutput){
        pOutput.accept(MP_WARHEAD_15_BALEFIRE.get());

        weaponList.forEach(itemRegistryObject -> pOutput.accept(itemRegistryObject.get()));
    }
    public static void genModel(ItemModelProvider provider){
        weaponList.forEach(itemRegistryObject -> provider.basicItem(itemRegistryObject.get()));
    }
}
