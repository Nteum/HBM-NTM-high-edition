package com.hbm.registries;

import com.hbm.item.BatteryItem;
import com.hbm.item.env.BedrockOreItem;
import com.hbm.HBM;
import com.hbm.fluid.ModFluids;
import com.hbm.item.weapon.ItemDetonator;
import com.hbm.item.weapon.grenade.ItemGrenade;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    //物品注册表
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HBM.MODID);
    /* weapon */
    //armor
    //grenade
    public static final RegistryObject<Item> grenade_generic = ITEMS.register("grenade_generic",()->new ItemGrenade(new Item.Properties(), ItemGrenade.Type.GENERIC));
    public static final RegistryObject<Item> grenade_strong = ITEMS.register("grenade_strong",()->new ItemGrenade(new Item.Properties(), ItemGrenade.Type.STRONG));
    public static final RegistryObject<Item> grenade_fire = ITEMS.register("grenade_fire",()->new ItemGrenade(new Item.Properties(), ItemGrenade.Type.FIRE));
    public static final RegistryObject<Item> grenade_frag = ITEMS.register("grenade_frag",()->new ItemGrenade(new Item.Properties(), ItemGrenade.Type.FRAG));
    public static final RegistryObject<Item> grenade_black_hole = ITEMS.register("grenade_black_hole",()->new ItemGrenade(new Item.Properties(), ItemGrenade.Type.BLACK_HOLE));
    /* material */
    public static final RegistryObject<Item> ingot_steel = ITEMS.register("ingot_steel",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> ingot_red_copper = ITEMS.register("ingot_red_copper",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> ingot_tungsten = ITEMS.register("ingot_tungsten",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> ingot_aluminium = ITEMS.register("ingot_aluminium",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> ingot_lead = ITEMS.register("ingot_lead",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> ingot_zirconium = ITEMS.register("ingot_zirconium",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> ingot_magnetized_tungsten = ITEMS.register("ingot_magnetized_tungsten",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> ingot_solinium = ITEMS.register("ingot_solinium",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> ingot_advanced_alloy = ITEMS.register("ingot_advanced_alloy",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> plate_steel = ITEMS.register("plate_steel",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> plate_iron = ITEMS.register("plate_iron",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> plate_advanced_alloy = ITEMS.register("plate_advanced_alloy",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> fluorite = ITEMS.register("fluorite",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> nugget_zirconium = ITEMS.register("nugget_zirconium",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> solid_fuel = ITEMS.register("solid_fuel",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> lignite = ITEMS.register("lignite",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> powder_coal = ITEMS.register("powder_coal",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> powder_lignite = ITEMS.register("powder_lignite",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> powder_coal_tiny = ITEMS.register("powder_coal_tiny",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> coke_coal = ITEMS.register("coke_coal",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> coke_lignite = ITEMS.register("coke_lignite",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> coke_petroleum = ITEMS.register("coke_petroleum",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> briquette_coal = ITEMS.register("briquette_coal",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> briquette_lignite = ITEMS.register("briquette_lignite",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> briquette_wood = ITEMS.register("briquette_wood",()->new Item(new Item.Properties()));

    public static final RegistryObject<Item> detonator = ITEMS.register("detonator",()->new ItemDetonator(new Item.Properties()));

    //流体桶
    public static final RegistryObject<Item> bucket_irradiated_water = ITEMS.register("bucket_irradiated_water",()->new BucketItem(ModFluids.IRRADIATED_WATER_SOURCE_BLOCK,new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final RegistryObject<Item> bucket_irradiated_polluted = ITEMS.register("bucket_irradiated_polluted",()->new BucketItem(ModFluids.IRRADIATED_POLLUTED_SOURCE_BLOCK,new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final RegistryObject<Item> bucket_sulfuric_acid = ITEMS.register("bucket_sulfuric_acid",()->new BucketItem(ModFluids.SULFURIC_ACID_SOURCE_BLOCK,new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    //part
    public static final RegistryObject<Item> overlay_my_fluid = ITEMS.register("overlay_my_fluid",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> crucible_template = ITEMS.register("crucible_template",()->new Item(new Item.Properties()));

    //矿物
    public static final RegistryObject<Item> BEDROCK_ORE = ITEMS.register("bedrock_ore_base",()->new BedrockOreItem(new Item.Properties()));

    //工业元件
    public static final RegistryObject<Item> BATTERY_CREATIVE = ITEMS.register("battery_creative",()->new BatteryItem(-1, 1_000_000L, 0, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BATTERY_GENERIC = ITEMS.register("battery_generic",()->new BatteryItem(5_000, 100, 100, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BATTERY_ADVANCED = ITEMS.register("battery_advanced",()->new BatteryItem(60_000, 500, 500, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BATTERY_LITHIUM = ITEMS.register("battery_lithium",()->new BatteryItem(250_000, 1000, 1000, new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> reacher = ITEMS.register("reacher",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> SCREWDRIVER = ITEMS.register("screwdriver",()->new Item(new Item.Properties()));
    //升级组件
    public static final RegistryObject<Item> UPGRADE_BASE = ITEMS.register("upgrade_base",()->new Item(new Item.Properties()));
    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
