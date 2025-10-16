package com.hbm.registries;

import com.hbm.HBMKey;
import com.hbm.datagen.LanguageProvider;
import com.hbm.datagen.model.ItemModelGen;
import com.hbm.item.HBMCombat;
import com.hbm.item.HBMComponent;
import com.hbm.item.HBMWeapon;
import com.hbm.item.HBMtools;
import com.hbm.item.env.BedrockOreItem;
import com.hbm.HBM;
import com.hbm.item.weapon.ItemDesignator;
import com.hbm.item.weapon.ItemDetonator;
import com.hbm.item.weapon.ItemMissilePart;
import com.hbm.item.weapon.grenade.ItemGrenade;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

//    public static final RegistryObject<Item> detonator = ITEMS.register("detonator",()->new ItemDetonator(new Item.Properties()));
    public static final WrappedItemRegistry DETONATOR = add("billet_schrabidium_fuel", ()->new Item(new Item.Properties()), ModCreativeModeTab.HBM_TOOL.getKey(), HBMKey.BASIC_MODEL, HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

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

    public static class WrappedItemRegistry{
        RegistryObject<Item> registryObject;
        String localizedName;
        String genNameWay = HBMKey.ORDERLY_GEN;
        ResourceKey<CreativeModeTab> creativeKey;
        String genModelWay = HBMKey.BASIC_MODEL;

        public Item get(){
            return registryObject.get();
        }
        public ResourceLocation getId()
        {
            return registryObject.getId();
        }
        @Nullable
        public ResourceKey<Item> getKey()
        {
            return registryObject.getKey();
        }

        public void languageSupport(LanguageProvider provider){
            switch (genNameWay){
                case HBMKey.LITERALLY -> provider.add(get(), localizedName);
                case HBMKey.ORDERLY_GEN -> provider.add(get(), RegistryHelper.generateOrderlyName(getId().getPath()));
                case HBMKey.REVERSE_GEN -> provider.add(get(), RegistryHelper.generateReversedName(getId().getPath()));
                case HBMKey.ORDERLY_GEN_EXCEPT_FIRST -> provider.add(get(), RegistryHelper.generateOrderlyExceptFirstName(getId().getPath()));
                default -> provider.add(get(), getId().toLanguageKey());
            }
        }

        public void creativeTabSupport(BuildCreativeModeTabContentsEvent event){
            if (event.getTabKey() == this.creativeKey){
                event.getEntries().put(new ItemStack(get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
        }

        public void modelSupport(ItemModelGen provider){
            if (genModelWay.equals(HBMKey.BASIC_MODEL)){
                provider.basicItem(get());
            }
        }
    }
}
