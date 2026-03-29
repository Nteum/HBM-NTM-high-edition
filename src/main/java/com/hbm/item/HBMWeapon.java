package com.hbm.item;

import com.hbm.entity.weapon.missile.EntityMissileTier0;
import com.hbm.item.weapon.ItemMissile;
import com.hbm.item.weapon.ItemMissilePart;
import com.hbm.render.model.Models;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class HBMWeapon{
    protected static final List<RegistryObject<Item>> weaponList = new ArrayList<>();
    public static RegistryObject<Item> MP_WARHEAD_15_BALEFIRE;
    public static RegistryObject<Item> MISSILE_ANTI_BALLISTIC;
    public static RegistryObject<Item> MISSILE_TEST;
    // Armor
    public static void register(DeferredRegister<Item> ITEMS){
        MP_WARHEAD_15_BALEFIRE = ITEMS.register("mp_warhead_15_balefire",()->new ItemMissilePart(new Item.Properties(), ItemMissilePart.MissileTier.TIER1));
        MISSILE_ANTI_BALLISTIC = ITEMS.register("missile_anti_ballistic",()->new ItemMissile(new Item.Properties(), ItemMissile.MissileFormFactor.ABM, ItemMissile.MissileTier.TIER1, null));
        MISSILE_TEST = ITEMS.register("missile_test",()->new ItemMissile(new Item.Properties(), ItemMissile.MissileFormFactor.MICRO, ItemMissile.MissileTier.TIER0, EntityMissileTier0.EntityMissileTest::new).setModel(()->Models.getEntityModel(Models.MISSILE_TEST)));

//        MISSILE_TEST = register(weaponList, "missile_test", ()->new ItemMissile(new Item.Properties(), ItemMissile.MissileTier.TIER1));
    }
    public static void creativeTab(CreativeModeTab.Output pOutput){
        pOutput.accept(MP_WARHEAD_15_BALEFIRE.get());
        pOutput.accept(MISSILE_TEST.get());

        weaponList.forEach(itemRegistryObject -> pOutput.accept(itemRegistryObject.get()));
    }
    public static void genModel(ItemModelProvider provider){
        provider.basicItem(MISSILE_TEST.get());

        weaponList.forEach(itemRegistryObject -> provider.basicItem(itemRegistryObject.get()));
    }
}
