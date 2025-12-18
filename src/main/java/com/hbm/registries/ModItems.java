package com.hbm.registries;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.datagen.LanguageProvider;
import com.hbm.datagen.model.ItemModelGen;
import com.hbm.item.HBMCombat;
import com.hbm.item.HBMComponent;
import com.hbm.item.HBMWeapon;
import com.hbm.item.HBMtools;
import com.hbm.item.env.BedrockOreItem;
import com.hbm.item.misc.ItemLemon;
import com.hbm.item.rbmk.ItemRBMKFuelRod;
import com.hbm.item.rbmk.ItemRBMKLid;
import com.hbm.item.weapon.ItemMissilePart;
import com.hbm.item.weapon.grenade.ItemGrenade;
import com.hbm.registries.WrapperRegistry.WrappedItemRegistry;
import com.hbm.utils.debug.GunSuicide;
import com.hbm.reactor.rbmk.RBMKLidType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModItems {
    //物品注册表
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HBM.MODID);

    public static final List<WrappedItemRegistry> itemList = new ArrayList<>();
    static {
        HBMtools.register(ITEMS);
        HBMComponent.register(ITEMS);
        HBMWeapon.register(ITEMS);
        HBMCombat.register(ITEMS);
    }
    /* weapon */
    //armor
    //grenade
    public static final RegistryObject<Item> grenade_generic = ITEMS.register("grenade_generic",()->new ItemGrenade(new Item.Properties(), ItemGrenade.Type.GENERIC));
    public static final RegistryObject<Item> grenade_strong = ITEMS.register("grenade_strong",()->new ItemGrenade(new Item.Properties(), ItemGrenade.Type.STRONG));
    public static final RegistryObject<Item> grenade_fire = ITEMS.register("grenade_fire",()->new ItemGrenade(new Item.Properties(), ItemGrenade.Type.FIRE));
    public static final RegistryObject<Item> grenade_frag = ITEMS.register("grenade_frag",()->new ItemGrenade(new Item.Properties(), ItemGrenade.Type.FRAG));
    public static final RegistryObject<Item> grenade_black_hole = ITEMS.register("grenade_black_hole",()->new ItemGrenade(new Item.Properties(), ItemGrenade.Type.BLACK_HOLE));
    /* material */
    public static final RegistryObject<Item> ingot_red_copper = ITEMS.register("ingot_red_copper",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> ingot_tungsten = ITEMS.register("ingot_tungsten",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> ingot_aluminium = ITEMS.register("ingot_aluminium",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> ingot_lead = ITEMS.register("ingot_lead",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> ingot_zirconium = ITEMS.register("ingot_zirconium",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> ingot_magnetized_tungsten = ITEMS.register("ingot_magnetized_tungsten",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> ingot_solinium = ITEMS.register("ingot_solinium",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> ingot_advanced_alloy = ITEMS.register("ingot_advanced_alloy",()->new Item(new Item.Properties()));
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
		    public static final RegistryObject<Item> rbmk_lid = ITEMS.register("rbmk_lid", () -> new ItemRBMKLid(new Item.Properties(), RBMKLidType.SOLID));
		    public static final RegistryObject<Item> rbmk_lid_glass = ITEMS.register("rbmk_lid_glass", () -> new ItemRBMKLid(new Item.Properties(), RBMKLidType.GLASS));
		    // RBMK fuel rods (placeholder stats, real values will be wired in later).
		    public static final RegistryObject<Item> rbmk_fuel_base = ITEMS.register("rbmk_fuel_base", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 12.0D, 20 * 60 * 10));
		    public static final RegistryObject<Item> rbmk_fuel_lea = ITEMS.register("rbmk_fuel_lea", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 14.0D, 20 * 60 * 12));
		    public static final RegistryObject<Item> rbmk_fuel_leaus = ITEMS.register("rbmk_fuel_leaus", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 16.0D, 20 * 60 * 12));
		    public static final RegistryObject<Item> rbmk_fuel_lep = ITEMS.register("rbmk_fuel_lep", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 18.0D, 20 * 60 * 10));
		    public static final RegistryObject<Item> rbmk_fuel_les = ITEMS.register("rbmk_fuel_les", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 18.0D, 20 * 60 * 10));
		    public static final RegistryObject<Item> rbmk_fuel_mea = ITEMS.register("rbmk_fuel_mea", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 22.0D, 20 * 60 * 10));
		    public static final RegistryObject<Item> rbmk_fuel_men = ITEMS.register("rbmk_fuel_men", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 24.0D, 20 * 60 * 10));
		    public static final RegistryObject<Item> rbmk_fuel_mep = ITEMS.register("rbmk_fuel_mep", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 26.0D, 20 * 60 * 10));
		    public static final RegistryObject<Item> rbmk_fuel_mes = ITEMS.register("rbmk_fuel_mes", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 26.0D, 20 * 60 * 10));
		    public static final RegistryObject<Item> rbmk_fuel_meu = ITEMS.register("rbmk_fuel_meu", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 28.0D, 20 * 60 * 12));
		    public static final RegistryObject<Item> rbmk_fuel_ueu = ITEMS.register("rbmk_fuel_ueu", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 20.0D, 20 * 60 * 12));
		    public static final RegistryObject<Item> rbmk_fuel_mox = ITEMS.register("rbmk_fuel_mox", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 32.0D, 20 * 60 * 16));
		    public static final RegistryObject<Item> rbmk_fuel_heu233 = ITEMS.register("rbmk_fuel_heu233", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 38.0D, 20 * 60 * 14));
		    public static final RegistryObject<Item> rbmk_fuel_heu235 = ITEMS.register("rbmk_fuel_heu235", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 40.0D, 20 * 60 * 14));
		    public static final RegistryObject<Item> rbmk_fuel_heaus = ITEMS.register("rbmk_fuel_heaus", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 36.0D, 20 * 60 * 14));
		    public static final RegistryObject<Item> rbmk_fuel_hea241 = ITEMS.register("rbmk_fuel_hea241", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 42.0D, 20 * 60 * 12));
		    public static final RegistryObject<Item> rbmk_fuel_hea242 = ITEMS.register("rbmk_fuel_hea242", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 42.0D, 20 * 60 * 12));
		    public static final RegistryObject<Item> rbmk_fuel_hen = ITEMS.register("rbmk_fuel_hen", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 44.0D, 20 * 60 * 10));
		    public static final RegistryObject<Item> rbmk_fuel_hep = ITEMS.register("rbmk_fuel_hep", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 44.0D, 20 * 60 * 12));
		    public static final RegistryObject<Item> rbmk_fuel_hep241 = ITEMS.register("rbmk_fuel_hep241", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 46.0D, 20 * 60 * 12));
		    public static final RegistryObject<Item> rbmk_fuel_hes = ITEMS.register("rbmk_fuel_hes", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 44.0D, 20 * 60 * 10));
		    public static final RegistryObject<Item> rbmk_fuel_thmeu = ITEMS.register("rbmk_fuel_thmeu", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 30.0D, 20 * 60 * 16));
		    public static final RegistryObject<Item> rbmk_fuel_drx = ITEMS.register("rbmk_fuel_drx", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 55.0D, 20 * 60 * 6));
		    public static final RegistryObject<Item> rbmk_fuel_flashlead = ITEMS.register("rbmk_fuel_flashlead", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 50.0D, 20 * 60 * 8));
		    public static final RegistryObject<Item> rbmk_fuel_balefire = ITEMS.register("rbmk_fuel_balefire", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 80.0D, 20 * 60 * 4));
		    public static final RegistryObject<Item> rbmk_fuel_balefire_gold = ITEMS.register("rbmk_fuel_balefire_gold", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 90.0D, 20 * 60 * 4));
		    public static final RegistryObject<Item> rbmk_fuel_po210be = ITEMS.register("rbmk_fuel_po210be", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 5.0D, 20 * 60 * 20));
		    public static final RegistryObject<Item> rbmk_fuel_pu238be = ITEMS.register("rbmk_fuel_pu238be", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 6.0D, 20 * 60 * 20));
		    public static final RegistryObject<Item> rbmk_fuel_ra226be = ITEMS.register("rbmk_fuel_ra226be", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 4.0D, 20 * 60 * 20));
		    public static final RegistryObject<Item> rbmk_fuel_zfb_base = ITEMS.register("rbmk_fuel_zfb_base", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 24.0D, 20 * 60 * 10));
		    public static final RegistryObject<Item> rbmk_fuel_zfb_bismuth = ITEMS.register("rbmk_fuel_zfb_bismuth", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 26.0D, 20 * 60 * 10));
		    public static final RegistryObject<Item> rbmk_fuel_zfb_pu241 = ITEMS.register("rbmk_fuel_zfb_pu241", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 36.0D, 20 * 60 * 12));
		    public static final RegistryObject<Item> rbmk_fuel_zfb_am_mix = ITEMS.register("rbmk_fuel_zfb_am_mix", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 40.0D, 20 * 60 * 8));
		    public static final RegistryObject<Item> rbmk_fuel_test = ITEMS.register("rbmk_fuel_test", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 40.0D, 20 * 60));
		    public static final RegistryObject<Item> rbmk_fuel_empty = ITEMS.register("rbmk_fuel_empty", () -> new Item(new Item.Properties().stacksTo(1)));

//    public static final RegistryObject<Item> detonator = ITEMS.register("detonator",()->new ItemDetonator(new Item.Properties()));
		    public static final WrappedItemRegistry DETONATOR = add("billet_schrabidium_fuel", ()->new Item(new Item.Properties()), ModCreativeModeTab.HBM_TOOL.getKey(), HBMKey.BASIC_MODEL, HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
	    public static final WrappedItemRegistry GUN_SUICIDE = add("gun_suicide", ()->new GunSuicide(new Item.Properties()), ModCreativeModeTab.HBM_TOOL.getKey(), HBMKey.BASIC_MODEL, HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final WrappedItemRegistry GLYPHID_MEAT_GRILLED = add("glyphid_meat_grilled", ()->new ItemLemon(new Item.Properties().food(Foods.ROTTEN_FLESH)), CreativeModeTabs.FOOD_AND_DRINKS, HBMKey.BASIC_MODEL, HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final WrappedItemRegistry GLYPHID_MEAT = add("glyphid_meat", ()->new ItemLemon(new Item.Properties().food(Foods.MUTTON)), CreativeModeTabs.FOOD_AND_DRINKS, HBMKey.BASIC_MODEL, HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
//    public static WrappedItemRegistry GLYPHID_SPAWN_EGG;
    //流体桶
//    public static final RegistryObject<Item> bucket_irradiated_water = ITEMS.register("bucket_irradiated_water",()->new BucketItem(ModFluids.IRRADIATED_WATER_SOURCE_BLOCK,new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
//    public static final RegistryObject<Item> bucket_irradiated_polluted = ITEMS.register("bucket_irradiated_polluted",()->new BucketItem(ModFluids.IRRADIATED_POLLUTED_SOURCE_BLOCK,new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
//    public static final RegistryObject<Item> bucket_sulfuric_acid = ITEMS.register("bucket_sulfuric_acid",()->new BucketItem(ModFluids.SULFURIC_ACID_SOURCE_BLOCK,new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    //part
    public static final RegistryObject<Item> overlay_my_fluid = ITEMS.register("overlay_my_fluid",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> crucible_template = ITEMS.register("crucible_template",()->new Item(new Item.Properties()));

    //矿物
    public static final RegistryObject<Item> BEDROCK_ORE = ITEMS.register("bedrock_ore_base",()->new BedrockOreItem(new Item.Properties()));

    public static final RegistryObject<Item> reacher = ITEMS.register("reacher",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> SCREWDRIVER = ITEMS.register("screwdriver",()->new Item(new Item.Properties()));
    //升级组件
//    public static final RegistryObject<Item> UPGRADE_BASE = ITEMS.register("upgrade_base",()->new Item(new Item.Properties()));
    //导弹
//    public static final RegistryObject<Item> DESIGNATOR = ITEMS.register("designator",()->new ItemDesignator(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MISSILE_GENERIC = ITEMS.register("missile_generic",()->new ItemMissilePart(new Item.Properties().stacksTo(1), ItemMissilePart.MissileTier.TIER1));
    // 填充物品，游戏内无法获得，用于避免物品被匹配上
    public static final RegistryObject<Item> DUMMY_ITEM = ITEMS.register("dummy_item", ()->new Item(new Item.Properties()));
    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }

    public static void creativeTab(BuildCreativeModeTabContentsEvent event){
        for (WrappedItemRegistry itemRegistry : itemList) {
            itemRegistry.creativeTabSupport(event);
        }
    }

    public static void genModel(ItemModelGen provider){
        for (WrappedItemRegistry itemRegistry : itemList) {
            itemRegistry.modelSupport(provider);
        }
    }
    public static void languageSupport(LanguageProvider provider){
        for (WrappedItemRegistry itemRegistry : itemList) {
            itemRegistry.languageSupport(provider);
        }
    }

    public static WrappedItemRegistry add(final String name, final Supplier<? extends Item> sup, ResourceKey<CreativeModeTab> tabKey, String genNameWay){
        return add(name, sup, tabKey, HBMKey.BASIC_MODEL, genNameWay, null);
    }
    public static WrappedItemRegistry add(final String name, final Supplier<? extends Item> sup, ResourceKey<CreativeModeTab> tabKey, String genModelWay, String genNameWay){
        return add(name, sup, tabKey, genModelWay, genNameWay, null);
    }
    public static WrappedItemRegistry add(final String name, final Supplier<? extends Item> sup, ResourceKey<CreativeModeTab> tabKey, String genModelWay, String genNameWay, String localizedName){
        WrappedItemRegistry itemRegistry = new WrappedItemRegistry();
        itemRegistry.registryObject = ITEMS.register(name, sup);
        itemRegistry.creativeKey = tabKey;
        itemRegistry.genModelWay = genModelWay;
        itemRegistry.genNameWay = genNameWay;
        if (itemRegistry.genNameWay!= null && itemRegistry.genNameWay.equals(HBMKey.LITERALLY) && localizedName!=null)
            itemRegistry.localizedName = localizedName;
        itemList.add(itemRegistry);
        return itemRegistry;
    }

//    public static class WrappedItemRegistry{
//        RegistryObject<Item> registryObject;
//        String localizedName;
//        String genNameWay = HBMKey.ORDERLY_GEN;
//        ResourceKey<CreativeModeTab> creativeKey;
//        String genModelWay = HBMKey.BASIC_MODEL;
//
//        public Item get(){
//            return registryObject.get();
//        }
//        public ResourceLocation getId()
//        {
//            return registryObject.getId();
//        }
//        @Nullable
//        public ResourceKey<Item> getKey()
//        {
//            return registryObject.getKey();
//        }
//
//        public void languageSupport(LanguageProvider provider){
//            switch (genNameWay){
//                case HBMKey.LITERALLY -> provider.add(get(), localizedName);
//                case HBMKey.ORDERLY_GEN -> provider.add(get(), RegistryHelper.generateOrderlyName(getId().getPath()));
//                case HBMKey.REVERSE_GEN -> provider.add(get(), RegistryHelper.generateReversedName(getId().getPath()));
//                case HBMKey.ORDERLY_GEN_EXCEPT_FIRST -> provider.add(get(), RegistryHelper.generateOrderlyExceptFirstName(getId().getPath()));
//                default -> provider.add(get(), getId().toLanguageKey());
//            }
//        }
//
//        public void creativeTabSupport(BuildCreativeModeTabContentsEvent event){
//            if (event.getTabKey() == this.creativeKey){
//                event.getEntries().put(new ItemStack(get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
//            }
//        }
//
//        public void modelSupport(ItemModelGen provider){
//            if (genModelWay.equals(HBMKey.BASIC_MODEL)){
//                provider.basicItem(get());
//            }else if (genModelWay.equals(HBMKey.SPAWN_EGG_MODEL)){
//                provider.withExistingParent(localizedName, "minecraft:item/template_spawn_egg");
//            }
//        }
//    }
}
