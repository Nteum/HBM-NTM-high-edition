package com.hbm.registries;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.Inventory.material.HBMMatForm;
import com.hbm.block.interfaces.ToolType;
import com.hbm.compat.legacy.LegacyItems;
import com.hbm.item.HBMComponent;
import com.hbm.config.ConfigLBSM;
import com.hbm.datagen.LanguageProvider;
import com.hbm.datagen.model.ItemModelGen;
import com.hbm.datagen.tag.ItemTagsGen;
import com.hbm.item.HBMCombat;
import com.hbm.item.HBMWeapon;
import com.hbm.item.consumable.LegacyConsumableItem;
import com.hbm.item.consumable.TemFlakesItem;
import com.hbm.item.env.BedrockOreItem;
import com.hbm.item.env.ItemEggGlyphid;
import com.hbm.item.env.ItemEggGlyphidToBirth;
import com.hbm.item.icf.ItemICFPellet;
import com.hbm.item.machine.ItemMachineUpgrade;
import com.hbm.item.machine.ItemMachineUpgrade.UpgradeType;
import com.hbm.item.misc.*;
import com.hbm.item.pwr.ItemPWRFuel;
import com.hbm.item.rbmk.ItemRBMKControlRod;
import com.hbm.item.rbmk.ItemRBMKFuelRod;
import com.hbm.item.rbmk.ItemRBMKLid;
import com.hbm.item.research.ItemBreedingRod;
import com.hbm.item.research.ItemPileRod;
import com.hbm.item.research.ItemResearchFuelPlate;
import com.hbm.item.special.ItemUnstable;
import com.hbm.item.tool.*;
import com.hbm.item.weapon.*;
import com.hbm.item.weapon.grenade.ItemGrenade;
import com.hbm.item.zirnox.ItemZirnoxRod;
import com.hbm.reactor.rbmk.RBMKLidType;
import com.hbm.registries.WrapperRegistry.WrappedItemRegistry;
import com.hbm.render.model.Models;
import com.hbm.entity.weapon.missile.EntityMissileTier0;
import com.hbm.debug.GunSuicide;
import com.hbm.debug.ItemDebugWand;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HBM.MODID);
    public static final List<WrappedItemRegistry> itemList = new ArrayList<>();
    private static boolean registeredToBus = false;

    public static final String[] WIRE_MAT = new String[]{HBMKey.ALUMINIUM, HBMKey.COPPER, HBMKey.RED_COPPER, HBMKey.GOLD, HBMKey.TUNGSTEN, HBMKey.ADVANCED_ALLOY, HBMKey.SCHRABIDIUM, HBMKey.ZINC, HBMKey.MAGNETIZED_TUNGSTEN};

    static {
        HBMWeapon.register(ITEMS);
        HBMCombat.register(ITEMS);
    }

    public static final RegistryObject<Item> INGOT_DURA_STEEL = parts("ingot_dura_steel", ()->new Item(new Item.Properties()), "High-Speed Steel Ingot");
    // HBM物品
    public static final RegistryObject<Item> REDSTONE_SWORD = new WrapperRegistry.ItemBuilder("redstone_sword", ()->new RedstoneSword(Tiers.STONE, 3, -2.4F, new Item.Properties())).tab(CreativeModeTabs.COMBAT).build();
    public static final RegistryObject<Item> BIG_SWORD = new WrapperRegistry.ItemBuilder("big_sword", ()->new BigSword(Tiers.GOLD, 3, -2.4F, new Item.Properties())).tab(CreativeModeTabs.COMBAT).build();
    /* material */
    public static final RegistryObject<Item> INGOT_TH232 = new WrapperRegistry.ItemBuilder("ingot_th232", ()->new Item(new Item.Properties())).tab(ModTabs.PARTS.getKey()).loc(HBMKey.ORDERLY_GEN_EXCEPT_FIRST).build();
    public static final RegistryObject<Item> INGOT_URANIUM = parts("ingot_uranium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, ModTags.Items.INGOT_URANIUM);
    public static final RegistryObject<Item> INGOT_U233 = parts("ingot_u233", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_U235 = parts("ingot_u235", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_U238 = parts("ingot_u238", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_U238M2 = parts("ingot_u238m2", ()->new ItemUnstable(350, 200, new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    //    ingot_u238m2 = new ItemUnstable(350, 200).setUnlocalizedName("ingot_u238m2").setCreativeTab(null).setTextureName(RefStrings.MODID + ":ingot_u238m2");
    public static final RegistryObject<Item> INGOT_PLUTONIUM = parts("ingot_plutonium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_PU238 = parts("ingot_pu238", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_PU239 = parts("ingot_pu239", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_PU240 = parts("ingot_pu240", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_PU241 = parts("ingot_pu241", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_PU_MIX = parts("ingot_pu_mix", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_AM241 = parts("ingot_am241", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_AM242 = parts("ingot_am242", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_AM_MIX = parts("ingot_am_mix", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_NEPTUNIUM = parts("ingot_neptunium", ()->new Item(new Item.Properties()),HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_POLONIUM = parts("ingot_polonium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_TECHNETIUM = parts("ingot_technetium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_CO60 = parts("ingot_co60", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_SR90 = parts("ingot_sr90", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_AU198 = parts("ingot_au198", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_PB209 = parts("ingot_pb209", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_RA226 = parts("ingot_ra226", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_TITANIUM = parts("ingot_titanium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.TITANIUM.ingot());
    public static final RegistryObject<Item> INGOT_COBALT = parts("ingot_cobalt", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_BORON = parts("ingot_boron", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_GRAPHITE = parts("ingot_graphite", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_FIREBRICK = parts("ingot_firebrick", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> ingot_smore = parts("ingot_smore", ()->new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationMod(20f).build())), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SULFUR = parts("sulfur", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NITRA = parts("nitra", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NITRA_SMALL = parts("nitra_small", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

    public static final RegistryObject<Item> PELLET_RTG = control("pellet_rtg", () -> new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> ASSEMBLY_TEMPLATE = template("assembly_template", () -> new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> MISSILE_NUCLEAR = new WrapperRegistry.ItemBuilder("missile_nuclear", () -> new ItemMissile(new Item.Properties(), ItemMissile.MissileFormFactor.ATLAS, ItemMissile.MissileTier.TIER4,
            (level, x, y, z, target) -> EntityMissileTier0.EntityMissileTest.create(level, x, y, z, target, EntityMissileTier0.EntityMissileTest.Payload.NUCLEAR)).setModel(() -> Models.getEntityModel(Models.MISSILE_NUKE)))
            .model(HBMKey.MODEL_EXISTING_FILE).build();
    public static final RegistryObject<Item> GUN_RIFLE = new WrapperRegistry.ItemBuilder("gun_maresleg", () -> new ItemGun(new Item.Properties())).model(HBMKey.MODEL_EXISTING_FILE).tab(ModTabs.WEAPON.getKey()).loc("Lever Action Shotgun").build();
    public static final RegistryObject<Item> BOTTLE_NUKA = consumable("bottle_nuka", () -> new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> LEMON = consumable("lemon",
            () -> LegacyConsumableItem.builder(3, 0.5F)
                    .tooltip("item.hbm.lemon.desc")
                    .build(),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> LOOPS = consumable("loops",
            () -> LegacyConsumableItem.builder(4, 0.25F)
                    .tooltip("item.hbm.loops.desc")
                    .build(),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> LOOP_STEW = consumable("loop_stew",
            () -> LegacyConsumableItem.builder(10, 0.5F)
                    .stacksTo(1)
                    .container(() -> Items.BOWL)
                    .tooltip("item.hbm.loop_stew.desc")
                    .effect(() -> MobEffects.REGENERATION, 20 * 20, 1, 1.0F)
                    .effect(() -> MobEffects.DAMAGE_RESISTANCE, 60 * 20, 2, 1.0F)
                    .effect(() -> MobEffects.MOVEMENT_SPEED, 60 * 20, 1, 1.0F)
                    .effect(() -> MobEffects.DAMAGE_BOOST, 20 * 20, 2, 1.0F)
                    .build(),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> TWINKIE = consumable("twinkie",
            () -> LegacyConsumableItem.builder(3, 0.25F)
                    .tooltip("item.hbm.twinkie.desc")
                    .build(),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PUDDING = consumable("pudding",
            () -> LegacyConsumableItem.builder(6, 1.0F)
                    .tooltip("item.hbm.pudding.desc1", "item.hbm.pudding.desc2", "item.hbm.pudding.desc3")
                    .build(),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET = consumable("nugget",
            () -> LegacyConsumableItem.builder(200, 1.0F).build(),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> GLYPHID_MEAT = consumable("glyphid_meat",
            () -> LegacyConsumableItem.builder(3, 0.5F)
                    .meat()
                    .build(),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> GLYPHID_MEAT_GRILLED = consumable("glyphid_meat_grilled",
            () -> LegacyConsumableItem.builder(8, 0.75F)
                    .meat()
                    .effect(() -> MobEffects.DAMAGE_BOOST, 180, 1, 1.0F)
                    .build(),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PANCAKE = consumable("pancake",
            () -> LegacyConsumableItem.builder(20, 20.0F).build(),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> MUCHO_MANGO = consumable("mucho_mango",
            () -> LegacyConsumableItem.builder(10, 0.6F)
                    .alwaysEat()
                    .useAnimation(UseAnim.DRINK)
                    .useDuration(200)
                    .tooltip("item.hbm.mucho_mango.desc")
                    .effect(() -> MobEffects.MOVEMENT_SPEED, 200, 0, 1.0F)
                    .build(),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> DEFINITELYFOOD = consumable("definitelyfood",
            () -> LegacyConsumableItem.builder(3, 0.5F).build(),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SPONGEBOB_MACARONI = consumable("spongebob_macaroni",
            () -> LegacyConsumableItem.builder(5, 1.0F).build(),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FOODITEM = consumable("fooditem",
            () -> LegacyConsumableItem.builder(2, 5.0F).build(),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> STATIC_SANDWICH = consumable("static_sandwich",
            () -> LegacyConsumableItem.builder(6, 1.0F).build(),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CHEESE = consumable("cheese",
            () -> LegacyConsumableItem.builder(5, 0.75F).build(),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> QUESADILLA = consumable("quesadilla",
            () -> LegacyConsumableItem.builder(8, 1.0F).build(),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> COTTON_CANDY = consumable("cotton_candy",
            () -> LegacyConsumableItem.builder(5, 0.6F)
                    .alwaysEat()
                    .effect(() -> MobEffects.POISON, 15 * 20, 0, 1.0F)
                    .effect(() -> MobEffects.WITHER, 5 * 20, 0, 1.0F)
                    .effect(() -> MobEffects.WEAKNESS, 25 * 20, 2, 1.0F)
                    .effect(() -> MobEffects.MOVEMENT_SPEED, 25 * 20, 2, 1.0F)
                    .effect(() -> MobEffects.DAMAGE_RESISTANCE, 30 * 20, 4, 1.0F)
                    .build(),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SCHNITZEL_VEGAN = consumable("schnitzel_vegan",
            () -> LegacyConsumableItem.builder(0, 0.6F)
                    .effect(() -> MobEffects.BLINDNESS, 10 * 20, 0, 1.0F)
                    .effect(() -> MobEffects.CONFUSION, 30 * 20, 0, 1.0F)
                    .effect(() -> MobEffects.HUNGER, 3 * 60 * 20, 4, 1.0F)
                    .effect(() -> MobEffects.WITHER, 3 * 20, 0, 1.0F)
                    .customAction((level, entity) -> {
                        entity.setSecondsOnFire(5);
                        entity.push(0.0D, 2.0D, 0.0D);
                    })
                    .build(),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> GLOWING_STEW = consumable("glowing_stew",
            () -> LegacyConsumableItem.builder(6, 0.6F)
                    .container(() -> Items.BOWL)
                    .build(),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BALEFIRE_SCRAMBLED = consumable("balefire_scrambled",
            () -> LegacyConsumableItem.builder(6, 0.6F)
                    .container(() -> Items.BOWL)
                    .build(),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BALEFIRE_AND_HAM = consumable("balefire_and_ham",
            () -> LegacyConsumableItem.builder(6, 0.6F)
                    .container(() -> Items.BOWL)
                    .build(),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> TEM_FLAKES = consumable("tem_flakes",
            TemFlakesItem::new,
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    /* weapon */
    //armor
    //grenade
    public static final RegistryObject<Item> grenade_generic = ITEMS.register("grenade_generic",()->new ItemGrenade(new Item.Properties(), ItemGrenade.Type.GENERIC));
    public static final RegistryObject<Item> grenade_strong = ITEMS.register("grenade_strong",()->new ItemGrenade(new Item.Properties(), ItemGrenade.Type.STRONG));
    public static final RegistryObject<Item> grenade_fire = ITEMS.register("grenade_fire",()->new ItemGrenade(new Item.Properties(), ItemGrenade.Type.FIRE));
    public static final RegistryObject<Item> grenade_frag = ITEMS.register("grenade_frag",()->new ItemGrenade(new Item.Properties(), ItemGrenade.Type.FRAG));
    public static final RegistryObject<Item> grenade_black_hole = ITEMS.register("grenade_black_hole",()->new ItemGrenade(new Item.Properties(), ItemGrenade.Type.BLACK_HOLE));
    // 1.7.10 兼容手榴弹全量补全
    public static final RegistryObject<Item> GRENADE_ASCHRAB = legacyGrenade("grenade_aschrab", ItemGrenade.Type.ASCHRAB);
    public static final RegistryObject<Item> GRENADE_BREACH = legacyGrenade("grenade_breach", ItemGrenade.Type.BREACH);
    public static final RegistryObject<Item> GRENADE_BURST = legacyGrenade("grenade_burst", ItemGrenade.Type.BURST);
    public static final RegistryObject<Item> GRENADE_CLOUD = legacyGrenade("grenade_cloud", ItemGrenade.Type.CLOUD);
    public static final RegistryObject<Item> GRENADE_CLUSTER = legacyGrenade("grenade_cluster", ItemGrenade.Type.CLUSTER);
    public static final RegistryObject<Item> GRENADE_ELECTRIC = legacyGrenade("grenade_electric", ItemGrenade.Type.ELECTRIC);
    public static final RegistryObject<Item> GRENADE_FLARE = legacyGrenade("grenade_flare", ItemGrenade.Type.FLARE);
    public static final RegistryObject<Item> GRENADE_GAS = legacyGrenade("grenade_gas", ItemGrenade.Type.GAS);
    public static final RegistryObject<Item> GRENADE_GASCAN = legacyGrenade("grenade_gascan", ItemGrenade.Type.GASCAN);
    public static final RegistryObject<Item> GRENADE_IF_BOUNCY = legacyGrenade("grenade_if_bouncy", ItemGrenade.Type.IF_BOUNCY);
    public static final RegistryObject<Item> GRENADE_IF_BRIMSTONE = legacyGrenade("grenade_if_brimstone", ItemGrenade.Type.IF_BRIMSTONE);
    public static final RegistryObject<Item> GRENADE_IF_CONCUSSION = legacyGrenade("grenade_if_concussion", ItemGrenade.Type.IF_CONCUSSION);
    public static final RegistryObject<Item> GRENADE_IF_GENERIC = legacyGrenade("grenade_if_generic", ItemGrenade.Type.IF_GENERIC);
    public static final RegistryObject<Item> GRENADE_IF_HE = legacyGrenade("grenade_if_he", ItemGrenade.Type.IF_HE);
    public static final RegistryObject<Item> GRENADE_IF_HOPWIRE = legacyGrenade("grenade_if_hopwire", ItemGrenade.Type.IF_HOPWIRE);
    public static final RegistryObject<Item> GRENADE_IF_IMPACT = legacyGrenade("grenade_if_impact", ItemGrenade.Type.IF_IMPACT);
    public static final RegistryObject<Item> GRENADE_IF_INCENDIARY = legacyGrenade("grenade_if_incendiary", ItemGrenade.Type.IF_INCENDIARY);
    public static final RegistryObject<Item> GRENADE_IF_MYSTERY = legacyGrenade("grenade_if_mystery", ItemGrenade.Type.IF_MYSTERY);
    public static final RegistryObject<Item> GRENADE_IF_NULL = legacyGrenade("grenade_if_null", ItemGrenade.Type.IF_NULL);
    public static final RegistryObject<Item> GRENADE_IF_SPARK = legacyGrenade("grenade_if_spark", ItemGrenade.Type.IF_SPARK);
    public static final RegistryObject<Item> GRENADE_IF_STICKY = legacyGrenade("grenade_if_sticky", ItemGrenade.Type.IF_STICKY);
    public static final RegistryObject<Item> GRENADE_IF_TOXIC = legacyGrenade("grenade_if_toxic", ItemGrenade.Type.IF_TOXIC);
    public static final RegistryObject<Item> GRENADE_KIT = legacyGrenade("grenade_kit", ItemGrenade.Type.KIT);
    public static final RegistryObject<Item> GRENADE_KYIV = legacyGrenade("grenade_kyiv", ItemGrenade.Type.KYIV);
    public static final RegistryObject<Item> GRENADE_LEMON = legacyGrenade("grenade_lemon", ItemGrenade.Type.LEMON);
    public static final RegistryObject<Item> GRENADE_MIRV = legacyGrenade("grenade_mirv", ItemGrenade.Type.MIRV);
    public static final RegistryObject<Item> GRENADE_MK2 = legacyGrenade("grenade_mk2", ItemGrenade.Type.MK2);
    public static final RegistryObject<Item> GRENADE_NUCLEAR = legacyGrenade("grenade_nuclear", ItemGrenade.Type.NUCLEAR);
    public static final RegistryObject<Item> GRENADE_NUKE = legacyGrenade("grenade_nuke", ItemGrenade.Type.NUKE);
    public static final RegistryObject<Item> GRENADE_PINK_CLOUD = legacyGrenade("grenade_pink_cloud", ItemGrenade.Type.PINK_CLOUD);
    public static final RegistryObject<Item> GRENADE_PLASMA = legacyGrenade("grenade_plasma", ItemGrenade.Type.PLASMA);
    public static final RegistryObject<Item> GRENADE_POISON = legacyGrenade("grenade_poison", ItemGrenade.Type.POISON);
    public static final RegistryObject<Item> GRENADE_PULSE = legacyGrenade("grenade_pulse", ItemGrenade.Type.PULSE);
    public static final RegistryObject<Item> GRENADE_SCHRABIDIUM = legacyGrenade("grenade_schrabidium", ItemGrenade.Type.SCHRABIDIUM);
    public static final RegistryObject<Item> GRENADE_SHRAPNEL = legacyGrenade("grenade_shrapnel", ItemGrenade.Type.SHRAPNEL);
    public static final RegistryObject<Item> GRENADE_SMART = legacyGrenade("grenade_smart", ItemGrenade.Type.SMART);
    public static final RegistryObject<Item> GRENADE_TAU = legacyGrenade("grenade_tau", ItemGrenade.Type.TAU);
    public static final RegistryObject<Item> GRENADE_ZOMG = legacyGrenade("grenade_zomg", ItemGrenade.Type.ZOMG);
    // 旧版武器条目可用化
    public static final RegistryObject<Item> GUN_B92 = gun("gun_b92", () -> new ItemGun(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> GUN_B92_AMMO = gun("gun_b92_ammo", () -> new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> STICK_TNT = legacyGrenade("stick_tnt", ItemGrenade.Type.IF_HE);
    public static final RegistryObject<Item> STICK_C4 = legacyGrenade("stick_c4", ItemGrenade.Type.BREACH);
    public static final RegistryObject<Item> STICK_DYNAMITE = legacyGrenade("stick_dynamite", ItemGrenade.Type.BURST);
    public static final RegistryObject<Item> STICK_DYNAMITE_FISHING = legacyGrenade("stick_dynamite_fishing", ItemGrenade.Type.BURST);
    public static final RegistryObject<Item> STICK_SEMTEX = legacyGrenade("stick_semtex", ItemGrenade.Type.IF_STICKY);
    public static final RegistryObject<Item> DISPERSER_CANISTER = legacyGrenade("disperser_canister", ItemGrenade.Type.GASCAN);
    public static final RegistryObject<Item> DISPERSER_CANISTER_EMPTY = legacyGrenade("disperser_canister_empty", ItemGrenade.Type.IF_NULL);
    public static final RegistryObject<Item> GLYPHID_GLAND = legacyGrenade("glyphid_gland", ItemGrenade.Type.IF_TOXIC);
    public static final RegistryObject<Item> GLYPHID_GLAND_EMPTY = legacyGrenade("glyphid_gland_empty", ItemGrenade.Type.IF_NULL);
    public static final RegistryObject<Item> NUCLEAR_WASTE_PEARL = legacyGrenade("nuclear_waste_pearl", ItemGrenade.Type.NUCLEAR);
    public static final RegistryObject<Item> ULLAPOOL_CABER = legacyGrenade("ullapool_caber", ItemGrenade.Type.BURST);
    public static final RegistryObject<Item> WEAPONIZED_STARBLASTER_CELL = legacyGrenade("weaponized_starblaster_cell", ItemGrenade.Type.PLASMA);



    public static final RegistryObject<Item> INGOT_URANIUM_FUEL = parts("ingot_uranium_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_PLUTONIUM_FUEL = parts("ingot_plutonium_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_NEPTUNIUM_FUEL = parts("ingot_neptunium_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_MOX_FUEL = parts("ingot_mox_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_AMERICIUM_FUEL = parts("ingot_americium_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_SCHRABIDIUM_FUEL = parts("ingot_schrabidium_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_THORIUM_FUEL = parts("ingot_thorium_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_URANIUM_FUEL = parts("nugget_uranium_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_THORIUM_FUEL = parts("nugget_thorium_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_PLUTONIUM_FUEL = parts("nugget_plutonium_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_NEPTUNIUM_FUEL = parts("nugget_neptunium_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_MOX_FUEL = parts("nugget_mox_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_AMERICIUM_FUEL = parts("nugget_americium_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_SCHRABIDIUM_FUEL = parts("nugget_schrabidium_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_ADVANCED_ALLOY = parts("ingot_advanced_alloy", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_TCALLOY = parts("ingot_tcalloy", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_CDALLOY = parts("ingot_cdalloy", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_BISMUTH_BRONZE = parts("ingot_bismuth_bronze", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_ARSENIC_BRONZE = parts("ingot_arsenic_bronze", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_BSCCO = parts("ingot_bscco", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

    public static final RegistryObject<Item> NITER = parts("salpeter", ()->new Item(new Item.Properties()), "Niter");
    public static final RegistryObject<Item> INGOT_COPPER = parts("ingot_copper", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_RED_COPPER = parts("ingot_red_copper", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_TUNGSTEN = parts("ingot_tungsten", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_ALUMINIUM = parts("ingot_aluminium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, ModTags.Items.INGOT_ALUMINIUM);
    public static final RegistryObject<Item> FLUORITE = parts("fluorite", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_BERYLLIUM = parts("ingot_beryllium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_STEEL = parts("ingot_steel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, ModTags.Items.INGOT_STEEL);
    public static final RegistryObject<Item> PLATE_STEEL = parts("plate_steel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, ModTags.Items.PLATE_STEEL);
    public static final RegistryObject<Item> PLATE_IRON = parts("plate_iron", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.IRON.plate());
    public static final RegistryObject<Item> INGOT_LEAD = parts("ingot_lead", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, ModTags.Items.INGOT_LEAD);
    public static final RegistryObject<Item> PLATE_LEAD = parts("plate_lead", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.LEAD.plate());
    public static final RegistryObject<Item> PLATE_DURA_STEEL = parts("plate_dura_steel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_SCHRARANIUM = new WrapperRegistry.ItemBuilder("ingot_schraranium", ()->new Item(new Item.Properties()){
        @Override
        public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
            super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
            if (ConfigLBSM.enableLBSM && ConfigLBSM.enableLBSMFullSchrab) pTooltipComponents.add(Component.literal("pankæk"));
        }
        @Override
        public Component getName(ItemStack pStack) {
            if (ConfigLBSM.enableLBSM && ConfigLBSM.enableLBSMFullSchrab) return HBMLang.ITEM_INGOT_SCHRARANIUM_NAME_ALTER.translate();
            else return super.getName(pStack);
        }
    }).tab(ModTabs.PARTS.getKey()).loc(HBMKey.REVERSE_GEN)
            .withConfigTexture("condition_state", () -> ConfigLBSM.enableLBSM && ConfigLBSM.enableLBSMFullSchrab).model(itemModelGen -> {
                // 我的评价是没办法，需要的参数太多了，一个一个传非常麻烦，只能手动了。
                ResourceLocation rl = HBM.rl("ingot_schraranium");
                itemModelGen.getBuilder(rl.toString()).parent(new ModelFile.UncheckedModelFile("item/generated"))
                        .texture("layer0", new ResourceLocation(rl.getNamespace(), "item/" + rl.getPath()))
                        .override().predicate(new ResourceLocation("condition_state"), 1.0f).model(itemModelGen.basicItem(HBM.rl("ingot_nikonium"))).end();
            }).build();
    public static final RegistryObject<Item> INGOT_SCHRABIDIUM = parts("ingot_schrabidium", ()->new Item(new Item.Properties().rarity(Rarity.RARE)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_SCHRABIDATE = parts("ingot_schrabidate", ()->new Item(new Item.Properties().rarity(Rarity.RARE)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_SCHRABIDIUM = parts("plate_schrabidium", ()->new Item(new Item.Properties().rarity(Rarity.RARE)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_COPPER = parts("plate_copper", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_GOLD = parts("plate_gold", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_ADVANCED_ALLOY = parts("plate_advanced_alloy", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> LITHIUM = parts("lithium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_ZIRCONIUM = parts("ingot_zirconium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

    //    ingot_semtex = new ItemLemon(4, 5, true).setUnlocalizedName("ingot_semtex").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ingot_semtex");
    public static final RegistryObject<Item> INGOT_C4 = parts("ingot_c4", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_PHOSPHORUS = parts("ingot_phosphorus", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> COIL_ADVANCED_ALLOY = parts("coil_advanced_alloy", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> COIL_ADVANCED_TORUS = parts("coil_advanced_torus", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_MAGNETIZED_TUNGSTEN = parts("ingot_magnetized_tungsten", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_COMBINE_STEEL = parts("ingot_combine_steel", ()->new ItemCustomInfo(new Item.Properties(), HBMLang.ITEM_INGOTCOMBINE_STEEL_DESC.translate()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_MIXED = parts("plate_mixed", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

    public static final RegistryObject<Item> PLATE_PAA = parts("plate_paa", ()->new ItemCustomInfo(new Item.Properties().rarity(Rarity.UNCOMMON)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PIPES_STEEL = parts("pipes_steel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> DRILL_TITANIUM = parts("drill_titanium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_DALEKANIUM = parts("plate_dalekanium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_EUPHEMIUM = parts("plate_euphemium", ()->new ItemCustomInfo(new Item.Properties().rarity(Rarity.EPIC)), "Euphemium Compound Plate");
    //    bolt = new ItemAutogen(MaterialShapes.BOLT).oun("boltntm").setUnlocalizedName("bolt").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":bolt");
//    bolt_spike = new ItemCustomLore().setUnlocalizedName("bolt_spike").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":bolt_spike");
    public static final RegistryObject<Item> PLATE_POLYMER = parts("plate_polymer", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_KEVLAR = parts("plate_kevlar", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_DINEUTRONIUM = parts("plate_dineutronium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_DESH = parts("plate_desh", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    //    plate_bismuth = new ItemCustomLore().setUnlocalizedName("plate_bismuth").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":plate_bismuth");
    public static final RegistryObject<Item> INGOT_SOLINIUM = parts("ingot_solinium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_SOLINIUM = parts("nugget_solinium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PHOTO_PANEL = parts("photo_panel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SAT_BASE = parts("sat_base", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> THRUSTER_NUCLEAR = parts("thruster_nuclear", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SAFETY_FUSE = parts("safety_fuse", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
//    part_generic = new ItemGenericPart().setUnlocalizedName("part_generic").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":part_generic");
//    item_expensive = new ItemExpensive().setUnlocalizedName("item_expensive").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":item_expensive");
//    item_secret = new ItemEnumMulti(EnumSecretType.class, true, true).setUnlocalizedName("item_secret").setCreativeTab(null).setTextureName(RefStrings.MODID + ":item_secret");
//    ingot_metal = new ItemEnumMulti(EnumIngotMetal.class, true, true).setUnlocalizedName("ingot_metal").setCreativeTab(null).setTextureName(RefStrings.MODID + ":ingot_metal");
//    chemical_dye = new ItemChemicalDye().setUnlocalizedName("chemical_dye").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":chemical_dye");
//    crayon = new ItemCrayon().setUnlocalizedName("crayon").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":crayon");

//    public static final RegistryObject<Item> ingot_red_copper = ITEMS.register("ingot_red_copper",()->new Item(new Item.Properties()));
//    public static final RegistryObject<Item> ingot_tungsten = ITEMS.register("ingot_tungsten",()->new Item(new Item.Properties()));
//    public static final RegistryObject<Item> ingot_aluminium = ITEMS.register("ingot_aluminium",()->new Item(new Item.Properties()));
//    public static final RegistryObject<Item> ingot_lead = ITEMS.register("ingot_lead",()->new Item(new Item.Properties()));
//    public static final RegistryObject<Item> ingot_zirconium = ITEMS.register("ingot_zirconium",()->new Item(new Item.Properties()));
//    public static final RegistryObject<Item> ingot_magnetized_tungsten = ITEMS.register("ingot_magnetized_tungsten",()->new Item(new Item.Properties()));
//    public static final RegistryObject<Item> ingot_solinium = ITEMS.register("ingot_solinium",()->new Item(new Item.Properties()));
//    public static final RegistryObject<Item> ingot_advanced_alloy = ITEMS.register("ingot_advanced_alloy",()->new Item(new Item.Properties()));
//    public static final RegistryObject<Item> plate_iron = ITEMS.register("plate_iron",()->new Item(new Item.Properties()));
//    public static final RegistryObject<Item> plate_advanced_alloy = ITEMS.register("plate_advanced_alloy",()->new Item(new Item.Properties()));
//    public static final RegistryObject<Item> fluorite = ITEMS.register("fluorite",()->new Item(new Item.Properties()));
//    public static final RegistryObject<Item> nugget_zirconium = ITEMS.register("nugget_zirconium",()->new Item(new Item.Properties()));
//    public static final RegistryObject<Item> solid_fuel = ITEMS.register("solid_fuel",()->new Item(new Item.Properties()));
//    public static final RegistryObject<Item> lignite = ITEMS.register("lignite",()->new Item(new Item.Properties()));
//    public static final RegistryObject<Item> powder_coal = ITEMS.register("powder_coal",()->new Item(new Item.Properties()));
//    public static final RegistryObject<Item> powder_lignite = ITEMS.register("powder_lignite",()->new Item(new Item.Properties()));
//    public static final RegistryObject<Item> powder_coal_tiny = ITEMS.register("powder_coal_tiny",()->new Item(new Item.Properties()));
//    public static final RegistryObject<Item> coke_coal = ITEMS.register("coke_coal",()->new Item(new Item.Properties()));
//    public static final RegistryObject<Item> coke_lignite = ITEMS.register("coke_lignite",()->new Item(new Item.Properties()));
//    public static final RegistryObject<Item> coke_petroleum = ITEMS.register("coke_petroleum",()->new Item(new Item.Properties()));


//    undefined = new ItemCustomLore().setUnlocalizedName("undefined").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":undefined");

    public static final RegistryObject<Item> BILLET_URANIUM = parts("billet_uranium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_U233 = parts("billet_u233", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_U235 = parts("billet_u235", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_U238 = parts("billet_u238", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_TH232 = parts("billet_th232", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_PLUTONIUM = parts("billet_plutonium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_PU238 = parts("billet_pu238", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_PU239 = parts("billet_pu239", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_PU240 = parts("billet_pu240", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_PU241 = parts("billet_pu241", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_PU_MIX = parts("billet_pu_mix", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_AM241 = parts("billet_am241", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_AM242 = parts("billet_am242", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_AM_MIX = parts("billet_am_mix", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_NEPTUNIUM = parts("billet_neptunium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_POLONIUM = parts("billet_polonium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_TECHNETIUM = parts("billet_technetium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_COBALT = parts("billet_cobalt", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_CO60 = parts("billet_co60", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_SR90 = parts("billet_sr90", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_AU198 = parts("billet_au198", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_PB209 = parts("billet_pb209", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_RA226 = parts("billet_ra226", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_ACTINIUM = parts("billet_actinium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_SCHRABIDIUM = parts("billet_schrabidium", ()->new Item(new Item.Properties().rarity(Rarity.RARE)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_SOLINIUM = parts("billet_solinium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_GH336 = parts("billet_gh336", ()->new ItemCustomInfo(new Item.Properties().rarity(Rarity.EPIC), HBMLang.ITEM_BILLETGH336_DESC.translate()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_AUSTRALIUM = parts("billet_australium", ()->new Item(new Item.Properties().rarity(Rarity.UNCOMMON)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_AUSTRALIUM_LESSER = parts("billet_australium_lesser", ()->new Item(new Item.Properties().rarity(Rarity.UNCOMMON)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_AUSTRALIUM_GREATER = parts("billet_australium_greater", ()->new Item(new Item.Properties().rarity(Rarity.UNCOMMON)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_URANIUM_FUEL = parts("billet_uranium_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_THORIUM_FUEL = parts("billet_thorium_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_PLUTONIUM_FUEL = parts("billet_plutonium_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_NEPTUNIUM_FUEL = parts("billet_neptunium_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_MOX_FUEL = parts("billet_mox_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_AMERICIUM_FUEL = parts("billet_americium_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_LES = parts("billet_les", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_SCHRABIDIUM_FUEL = parts("billet_schrabidium_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_HES = parts("billet_hes", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_PO210BE = parts("billet_po210be", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_RA226BE = parts("billet_ra226be", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_PU238BE = parts("billet_pu238be", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_BERYLLIUM = parts("billet_beryllium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_BISMUTH = parts("billet_bismuth", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_ZIRCONIUM = parts("billet_zirconium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_YHARONITE = parts("billet_yharonite", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> billet_balefire_gold = parts("billet_balefire_gold", ()->new Item(new Item.Properties().rarity(Rarity.UNCOMMON)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> billet_flashlead = parts("billet_flashlead", ()->new ItemCustomInfo(new Item.Properties().rarity(Rarity.UNCOMMON),HBMLang.ITEM_BILLETFLASHLEAD_DESC.translate()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_ZFB_BISMUTH = parts("billet_zfb_bismuth", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_ZFB_PU241 = parts("billet_zfb_pu241", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_ZFB_AM_MIX = parts("billet_zfb_am_mix", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_NUCLEAR_WASTE = parts("billet_nuclear_waste", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BALL_RESIN = parts("ball_resin", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
//    ingot_dura_steel = new ItemCustomLore().setUnlocalizedName("ingot_dura_steel").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ingot_dura_steel");
//    ingot_polymer = new ItemCustomLore().setUnlocalizedName("ingot_polymer").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ingot_polymer");
//    ingot_bakelite = new ItemCustomLore().setUnlocalizedName("ingot_bakelite").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ingot_bakelite");
//    ingot_biorubber = new ItemCustomLore().setUnlocalizedName("ingot_biorubber").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ingot_biorubber");
//    ingot_rubber = new ItemCustomLore().setUnlocalizedName("ingot_rubber").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ingot_rubber");
//    ingot_pc = new ItemCustomLore().setUnlocalizedName("ingot_pc").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ingot_pc");
//    ingot_pvc = new ItemCustomLore().setUnlocalizedName("ingot_pvc").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ingot_pvc");
    public static final RegistryObject<Item> INGOT_DESH = parts("ingot_desh", ()->new ItemCustomInfo(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    //    nugget_desh = new ItemCustomLore().setUnlocalizedName("nugget_desh").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":nugget_desh");
    public static final RegistryObject<Item> INGOT_DINEUTRONIUM = parts("ingot_dineutronium", ()->new ItemCustomInfo(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    //    nugget_dineutronium = new ItemCustomLore().setUnlocalizedName("nugget_dineutronium").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":nugget_dineutronium");
//    powder_dineutronium = new ItemCustomLore().setUnlocalizedName("powder_dineutronium").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":powder_dineutronium");
    public static final RegistryObject<Item> INGOT_STARMETAL = parts("ingot_starmetal", ()->new ItemStarmetal(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_GUNMETAL = parts("ingot_gunmetal", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_GUNMETAL = parts("plate_gunmetal", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_WEAPONSTEEL = parts("ingot_weaponsteel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_WEAPONSTEEL = parts("plate_weaponsteel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_SATURNITE = parts("ingot_saturnite", ()->new Item(new Item.Properties().rarity(Rarity.RARE)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_SATURNITE = parts("plate_saturnite", ()->new Item(new Item.Properties().rarity(Rarity.RARE)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
//    ingot_ferrouranium = new ItemCustomLore().setUnlocalizedName("ingot_ferrouranium").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ingot_ferrouranium");
//    ingot_fiberglass = new ItemCustomLore().setUnlocalizedName("ingot_fiberglass").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ingot_fiberglass");
    public static final RegistryObject<Item> INGOT_ASBESTOS = parts("ingot_asbestos", ()->new ItemCustomInfo(new Item.Properties(), HBMLang.ITEM_INGOTASBESTOS_DESC.translate()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    //    powder_asbestos = new ItemCustomLore().setUnlocalizedName("powder_asbestos").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":powder_asbestos");
//    ingot_electronium = new ItemCustomLore().setUnlocalizedName("ingot_electronium").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ingot_electronium");
    public static final RegistryObject<Item> NUGGET_ZIRCONIUM = add("nugget_zirconium", ()->new Item(new Item.Properties()), ModTabs.PARTS.getKey(), "Zirconium Splinter");
    public static final RegistryObject<Item> NUGGET_MERCURY = parts("nugget_mercury_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    //    ingot_mercury = new ItemCustomLore().setUnlocalizedName("nugget_mercury").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":nugget_mercury");
//    bottle_mercury = new ItemCustomLore().setUnlocalizedName("bottle_mercury").setContainerItem(Items.glass_bottle).setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":bottle_mercury");
    public static final RegistryObject<Item> INGOT_CALCIUM = parts("ingot_calcium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_CALCIUM = parts("powder_calcium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_CADMIUM = parts("ingot_cadmium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_CADMIUM = parts("powder_cadmium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_BISMUTH = parts("powder_bismuth", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_MUD = parts("ingot_mud", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_CFT = parts("ingot_cft", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

//    ore_byproduct = new ItemByproduct().setUnlocalizedName("ore_byproduct").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":byproduct");
//
//    ore_bedrock = new ItemBedrockOre().setUnlocalizedName("ore_bedrock").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ore_bedrock");
//    ore_centrifuged = new ItemBedrockOre().setUnlocalizedName("ore_centrifuged").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ore_centrifuged");
//    ore_cleaned = new ItemBedrockOre().setUnlocalizedName("ore_cleaned").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ore_cleaned");
//    ore_separated = new ItemBedrockOre().setUnlocalizedName("ore_separated").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ore_separated");
//    ore_purified = new ItemBedrockOre().setUnlocalizedName("ore_purified").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ore_purified");
//    ore_nitrated = new ItemBedrockOre().setUnlocalizedName("ore_nitrated").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ore_nitrated");
//    ore_nitrocrystalline = new ItemBedrockOre().setUnlocalizedName("ore_nitrocrystalline").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ore_nitrocrystalline");
//    ore_deepcleaned = new ItemBedrockOre().setUnlocalizedName("ore_deepcleaned").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ore_deepcleaned");
//    ore_seared = new ItemBedrockOre().setUnlocalizedName("ore_seared").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ore_seared");
//    ore_enriched = new ItemBedrockOre().setUnlocalizedName("ore_enriched").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ore_enriched");
//    bedrock_ore_base = new ItemBedrockOreBase().setUnlocalizedName("bedrock_ore_base").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":bedrock_ore_new");
//    bedrock_ore = new ItemBedrockOreNew().setUnlocalizedName("bedrock_ore").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":bedrock_ore_new");
//    bedrock_ore_fragment = new ItemAutogen(MaterialShapes.FRAGMENT).aot(Mats.MAT_BISMUTH, "bedrock_ore_fragment_bismuth").setUnlocalizedName("bedrock_ore_fragment").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":bedrock_ore_fragment");
//
//    ingot_lanthanium = new ItemCustomLore().setUnlocalizedName("ingot_lanthanium").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ingot_lanthanium");
//    ingot_actinium = new ItemCustomLore().setUnlocalizedName("ingot_actinium").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ingot_actinium");
//
//    ingot_meteorite = new ItemHot(200).setUnlocalizedName("ingot_meteorite").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ingot_meteorite");
//    ingot_meteorite_forged = new ItemHot(200).setUnlocalizedName("ingot_meteorite_forged").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ingot_meteorite_forged");
//    blade_meteorite = new ItemHot(200).setUnlocalizedName("blade_meteorite").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":blade_meteorite");
//    ingot_steel_dusted = new ItemHotDusted(200).setUnlocalizedName("ingot_steel_dusted").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ingot_steel_dusted");
//    ingot_chainsteel = new ItemHot(100).setUnlocalizedName("ingot_chainsteel").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ingot_chainsteel");

    public static final RegistryObject<Item> BLADE_METEORITE = parts("blade_meteorite", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

    public static final RegistryObject<Item> PLATE_ARMOR_TITANIUM = parts("plate_armor_titanium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_ARMOR_AJR = parts("plate_armor_ajr", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_ARMOR_HEV = parts("plate_armor_hev", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_ARMOR_LUNAR = parts("plate_armor_lunar", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_ARMOR_FAU = parts("plate_armor_fau", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_ARMOR_DNT = parts("plate_armor_dnt", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

    //    oil_tar = new ItemEnumMulti(EnumTarType.class, true, true).setUnlocalizedName("oil_tar").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":oil_tar");
    public static final RegistryObject<Item> SOLID_FUEL = parts("solid_fuel", ()->new ItemFuel(new Item.Properties(), 200 * 16), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SOLID_FUEL_PRESTO = parts("solid_fuel_presto", ()->new ItemFuel(new Item.Properties(), 200 * 40), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SOLID_FUEL_PRESTO_TRIPLET = parts("solid_fuel_presto_triplet", ()->new ItemFuel(new Item.Properties(), 200 * 200), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SOLID_FUEL_BF = parts("solid_fuel_bf", ()->new ItemFuel(new Item.Properties(), 200 * 160), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SOLID_FUEL_PRESTO_BF = parts("solid_fuel_presto_bf", ()->new ItemFuel(new Item.Properties(), 200 * 400), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SOLID_FUEL_PRESTO_TRIPLET_BF = parts("solid_fuel_presto_triplet_bf", ()->new ItemFuel(new Item.Properties(), 200 * 2000), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> ROCKET_FUEL = parts("rocket_fuel", ()->new ItemFuel(new Item.Properties(), 200 * 32), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> COKE_COAL = parts("coke_coal", ()->new ItemFuel(new Item.Properties(), 200 * 16), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, ModTags.Items.COKE);
    public static final RegistryObject<Item> COKE_LIGNITE = parts("coke_lignite", ()->new ItemFuel(new Item.Properties(), 200 * 16), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, ModTags.Items.COKE);
    public static final RegistryObject<Item> COKE_PETROLEUM = parts("coke_petroleum", ()->new ItemFuel(new Item.Properties(), 200 * 16), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, ModTags.Items.COKE);
    public static final RegistryObject<Item> LIGNITE = parts("lignite", ()->new ItemFuel(new Item.Properties(), 1200), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BRIQUETTE_COAL = parts("briquette_coal", ()->new ItemFuel(new Item.Properties(), 200 * 10), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BRIQUETTE_LIGNITE = parts("briquette_lignite", ()->new ItemFuel(new Item.Properties(), 200 * 8), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BRIQUETTE_WOOD = parts("briquette_wood", ()->new ItemFuel(new Item.Properties(), 200 * 2), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_LIGNITE = parts("powder_lignite", ()->new ItemFuel(new Item.Properties(), 1200), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, ModTags.Items.DUST_LIGNITE);
    public static final RegistryObject<Item> COAL_INFERNAL = parts("coal_infernal", ()->new ItemFuel(new Item.Properties(), 4800), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CINNEBAR = parts("cinnebar", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

    public static final RegistryObject<Item> POWDER_ASH_COAL = parts("powder_ash_coal", ()->new ItemFuel(new Item.Properties(), 200), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> POWDER_ASH_FLY = parts("powder_ash_fly", ()->new ItemFuel(new Item.Properties(), 200), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> POWDER_ASH_FULLERENE = parts("powder_ash_fullerene", ()->new ItemFuel(new Item.Properties(), 200), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> POWDER_ASH_MISC = parts("powder_ash_misc", ()->new ItemFuel(new Item.Properties(), 200 / 2), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> POWDER_ASH_SOOT = parts("powder_ash_soot", ()->new ItemFuel(new Item.Properties(), 200 / 2), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> POWDER_ASH_WOOD = parts("powder_ash_wood", ()->new ItemFuel(new Item.Properties(), 200 / 2), HBMKey.REVERSE_GEN);
//    powder_limestone = new Item().setUnlocalizedName("powder_limestone").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":powder_limestone");
//    powder_cement = new ItemLemon(2, 0.5F, false).setUnlocalizedName("powder_cement").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":powder_cement");
//
//    ingot_gh336 = new ItemCustomLore().setRarity(EnumRarity.epic).setUnlocalizedName("ingot_gh336").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ingot_gh336");
//    nugget_gh336 = new ItemCustomLore().setRarity(EnumRarity.epic).setUnlocalizedName("nugget_gh336").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":nugget_gh336");

    public static final RegistryObject<Item> INGOT_AUSTRALIUM = parts("ingot_australium", ()->new ItemCustomInfo(new Item.Properties().rarity(Rarity.UNCOMMON)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
//    nugget_australium = new ItemCustomLore().setRarity(EnumRarity.uncommon).setUnlocalizedName("nugget_australium").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":nugget_australium");
//    nugget_australium_lesser = new ItemCustomLore().setRarity(EnumRarity.uncommon).setUnlocalizedName("nugget_australium_lesser").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":nugget_australium_lesser");
//    nugget_australium_greater = new ItemCustomLore().setRarity(EnumRarity.uncommon).setUnlocalizedName("nugget_australium_greater").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":nugget_australium_greater");

    public static final RegistryObject<Item> NUGGET_TH232 = parts("nugget_th232", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_URANIUM = parts("nugget_uranium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_U233 = parts("nugget_u233", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_U235 = parts("nugget_u235", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_U238 = parts("nugget_u238", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_PLUTONIUM = parts("nugget_plutonium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_PU238 = parts("nugget_pu238", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_PU239 = parts("nugget_pu239", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_PU240 = parts("nugget_pu240", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_PU241 = parts("nugget_pu241", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_PU_MIX = parts("nugget_pu_mix", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_AM241 = parts("nugget_am241", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_AM242 = parts("nugget_am242", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_AM_MIX = parts("nugget_am_mix", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_NEPTUNIUM = parts("nugget_neptunium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_POLONIUM = parts("nugget_polonium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_TECHNETIUM = parts("nugget_technetium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_COBALT = parts("nugget_cobalt", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_CO60 = parts("nugget_co60", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_SR90 = parts("nugget_sr90", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_AU198 = parts("nugget_au198", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_PB209 = parts("nugget_pb209", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_RA226 = parts("nugget_ra226", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_ACTINIUM = parts("nugget_actinium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_TITANIUM = parts("plate_titanium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.TITANIUM.plate());
    public static final RegistryObject<Item> PLATE_ALUMINIUM = parts("plate_aluminium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NEUTRON_REFLECTOR = parts("neutron_reflector", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_LEAD = parts("nugget_lead", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_BISMUTH = parts("ingot_bismuth", ()->new ItemCustomInfo(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_BISMUTH = parts("nugget_bismuth", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    //    ingot_arsenic = new ItemCustomLore().setUnlocalizedName("ingot_arsenic").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ingot_arsenic");
    public static final RegistryObject<Item> NUGGET_ARSENIC = parts("nugget_arsenic", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

    //    ingot_tantalium = new ItemCustomLore().setUnlocalizedName("ingot_tantalium").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ingot_tantalium");
//    nugget_tantalium = new ItemCustomLore().setUnlocalizedName("nugget_tantalium").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":nugget_tantalium");
    public static final RegistryObject<Item> INGOT_SILICON = parts("ingot_silicon", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_SILICON = parts("billet_silicon", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_SILICON = parts("nugget_silicon", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_NIOBIUM = parts("ingot_niobium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_NIOBIUM = parts("nugget_niobium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    //    ingot_osmiridium = new ItemCustomLore().setRarity(EnumRarity.rare).setUnlocalizedName("ingot_osmiridium").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":ingot_osmiridium");
//    nugget_osmiridium = new ItemCustomLore().setRarity(EnumRarity.rare).setUnlocalizedName("nugget_osmiridium").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":nugget_osmiridium");
//    nugget_schrabidium = new ItemCustomLore().setRarity(EnumRarity.rare).setUnlocalizedName("nugget_schrabidium").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":nugget_schrabidium");
    public static final RegistryObject<Item> NUGGET_BERYLLIUM = parts("nugget_beryllium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> HAZMAT_CLOTH = parts("hazmat_cloth", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> HAZMAT_CLOTH_RED = parts("hazmat_cloth_red", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> HAZMAT_CLOTH_GREY = parts("hazmat_cloth_grey", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> ASBESTOS_CLOTH = parts("asbestos_cloth", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> WOOD_ASH_POWDER = parts("wood_ash_powder", () -> new Item(new Item.Properties()), HBMKey.GEN_STANDALONE);
    //    rag = new ItemRag().setUnlocalizedName("rag").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":rag");
    public static final RegistryObject<Item> RAG_DAMP = parts("rag_damp", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> RAG_PISS = parts("rag_piss", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FILTER_COAL = parts("filter_coal", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_HES = parts("ingot_hes", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_LES = parts("ingot_les", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_HES = parts("nugget_hes", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_LES = parts("nugget_les", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_COMBINE_STEEL = parts("plate_combine_steel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

    public static final RegistryObject<Item> CRYSTAL_COAL = parts("crystal_coal", ()->new ItemFuel(new Item.Properties(), 6400), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_IRON = parts("crystal_iron", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_GOLD = parts("crystal_gold", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_REDSTONE = parts("crystal_redstone", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_LAPIS = parts("crystal_lapis", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_DIAMOND = parts("crystal_diamond", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_URANIUM = parts("crystal_uranium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_THORIUM = parts("crystal_thorium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_PLUTONIUM = parts("crystal_plutonium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_TITANIUM = parts("crystal_titanium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_SULFUR = parts("crystal_sulfur", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_NITER = parts("crystal_niter", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_COPPER = parts("crystal_copper", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_TUNGSTEN = parts("crystal_tungsten", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_ALUMINIUM = parts("crystal_aluminium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_FLUORITE = parts("crystal_fluorite", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_BERYLLIUM = parts("crystal_beryllium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_LEAD = parts("crystal_lead", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    //    crystal_schraranium = new ItemCustomLore().setRarity(EnumRarity.rare).setUnlocalizedName("crystal_schraranium").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":crystal_schraranium");
//    crystal_schrabidium = new ItemCustomLore().setRarity(EnumRarity.rare).setUnlocalizedName("crystal_schrabidium").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":crystal_schrabidium");
    public static final RegistryObject<Item> CRYSTAL_RARE = parts("crystal_rare", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_PHOSPHORUS = parts("crystal_phosphorus", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_LITHIUM = parts("crystal_lithium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_COBALT = parts("crystal_cobalt", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_STARMETAL = parts("crystal_starmetal", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_CINNEBAR = parts("crystal_cinnebar", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_TRIXITE = parts("crystal_trixite", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_OSMIRIDIUM = parts("crystal_osmiridium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
//    gem_sodalite = new ItemCustomLore().setUnlocalizedName("gem_sodalite").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":gem_sodalite");
//    gem_tantalium = new ItemCustomLore().setUnlocalizedName("gem_tantalium").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":gem_tantalium");
//    gem_volcanic = new ItemCustomLore().setRarity(EnumRarity.uncommon).setUnlocalizedName("gem_volcanic").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":gem_volcanic");
//    gem_rad = new ItemCustomLore().setRarity(EnumRarity.uncommon).setUnlocalizedName("gem_rad").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":gem_rad");
//    gem_alexandrite = new ItemAlexandrite().setUnlocalizedName("gem_alexandrite").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":gem_alexandrite");

    public static final RegistryObject<Item> POWDER_LEAD = parts("powder_lead", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_TANTALIUM = parts("powder_tantalium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_NEPTUNIUM = parts("powder_neptunium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_POLONIUM = parts("powder_polonium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_CO60 = parts("powder_co60", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_SR90 = parts("powder_sr90", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_SR90_TINY = parts("powder_sr90_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_I131 = parts("powder_i131", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_I131_TINY = parts("powder_i131_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_XE135 = parts("powder_xe135", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_XE135_TINY = parts("powder_xe135_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_CS137 = parts("powder_cs137", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_CS137_TINY = parts("powder_cs137_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_AU198 = parts("powder_au198", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_RA226 = parts("powder_ra226", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_AT209 = parts("powder_at209", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    //    powder_schrabidium = new ItemCustomLore().setRarity(EnumRarity.rare).setUnlocalizedName("powder_schrabidium").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":powder_schrabidium");
//    powder_schrabidate = new ItemCustomLore().setRarity(EnumRarity.rare).setUnlocalizedName("powder_schrabidate").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":powder_schrabidate");
    public static final RegistryObject<Item> POWDER_ALUMINIUM = parts("powder_aluminium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_BERYLLIUM = parts("powder_beryllium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_COPPER = parts("powder_copper", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_GOLD = parts("powder_gold", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_IRON = parts("powder_iron", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_TITANIUM = parts("powder_titanium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_TUNGSTEN = parts("powder_tungsten", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_URANIUM = parts("powder_uranium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_PLUTONIUM = parts("powder_plutonium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    //    dust = new ItemCustomLore().setUnlocalizedName("dust").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":dust");
    public static final RegistryObject<Item> DUST = parts("dust", ()->new ItemFuel(new Item.Properties(), 200 / 8), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> DUST_TINY = parts("dust_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FALLOUT = parts("fallout", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_ADVANCED_ALLOY = parts("powder_advanced_alloy", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_TCALLOY = parts("powder_tcalloy", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_COAL = parts("powder_coal", ()->new ItemFuel(new Item.Properties(), 200 * 8), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, ModTags.Items.DUST_COAL);
    public static final RegistryObject<Item> POWDER_COAL_TINY = parts("powder_coal_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_COMBINE_STEEL = parts("powder_combine_steel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_DIAMOND = parts("powder_diamond", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, ModTags.Items.DUST_DIAMOND);
    public static final RegistryObject<Item> POWDER_EMERALD = parts("powder_emerald", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, ModTags.Items.DUST_EMERALD);
    public static final RegistryObject<Item> POWDER_LAPIS = parts("powder_lapis", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, ModTags.Items.DUST_LAPIS);
    public static final RegistryObject<Item> POWDER_QUARTZ = parts("powder_quartz", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, ModTags.Items.DUST_QUARTZ);
    public static final RegistryObject<Item> POWDER_MAGNETIZED_TUNGSTEN = parts("powder_magnetized_tungsten", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_CHLOROPHYTE = parts("powder_chlorophyte", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_RED_COPPER = parts("powder_red_copper", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_STEEL = parts("powder_steel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, ModTags.Items.DUST_STEEL);
    public static final RegistryObject<Item> POWDER_LITHIUM = parts("powder_lithium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_ZIRCONIUM = parts("powder_zirconium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_SODIUM = parts("powder_sodium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    //    powder_power = new ItemCustomLore().setRarity(EnumRarity.uncommon).setUnlocalizedName("powder_power").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":powder_energy_alt");
//    powder_iodine = new ItemCustomLore().setRarity(EnumRarity.epic).setUnlocalizedName("powder_iodine").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":powder_iodine");
//    powder_thorium = new ItemCustomLore().setRarity(EnumRarity.uncommon).setUnlocalizedName("powder_thorium").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":powder_thorium");
//    powder_neodymium = new ItemCustomLore().setRarity(EnumRarity.epic).setUnlocalizedName("powder_neodymium").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":powder_neodymium");
//    powder_astatine = new ItemCustomLore().setRarity(EnumRarity.epic).setUnlocalizedName("powder_astatine").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":powder_astatine");
//    powder_caesium = new ItemCustomLore().setRarity(EnumRarity.epic).setUnlocalizedName("powder_caesium").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":powder_caesium");
//    powder_australium = new ItemCustomLore().setRarity(EnumRarity.uncommon).setUnlocalizedName("powder_australium").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":powder_australium");
//    powder_strontium = new ItemCustomLore().setRarity(EnumRarity.epic).setUnlocalizedName("powder_strontium").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":powder_strontium");
//    powder_cobalt = new ItemCustomLore().setRarity(EnumRarity.epic).setUnlocalizedName("powder_cobalt").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":powder_cobalt");
//    powder_bromine = new ItemCustomLore().setRarity(EnumRarity.epic).setUnlocalizedName("powder_bromine").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":powder_bromine");
//    powder_niobium = new ItemCustomLore().setRarity(EnumRarity.epic).setUnlocalizedName("powder_niobium").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":powder_niobium");
//    powder_tennessine = new ItemCustomLore().setRarity(EnumRarity.epic).setUnlocalizedName("powder_tennessine").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":powder_tennessine");
//    powder_cerium = new ItemCustomLore().setRarity(EnumRarity.epic).setUnlocalizedName("powder_cerium").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":powder_cerium");
//    powder_dura_steel = new ItemCustomLore().setUnlocalizedName("powder_dura_steel").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":powder_dura_steel");
//    powder_polymer = new ItemCustomLore().setUnlocalizedName("powder_polymer").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":powder_polymer");
//    powder_bakelite = new ItemCustomLore().setUnlocalizedName("powder_bakelite").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":powder_bakelite");
//    powder_euphemium = new ItemCustomLore().setRarity(EnumRarity.epic).setUnlocalizedName("powder_euphemium").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":powder_euphemium");
//    public static final RegistryObject<Item> POWDER_METEORITE = parts("powder_meteorite", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
//    powder_lanthanium = new ItemCustomLore().setRarity(EnumRarity.epic).setUnlocalizedName("powder_lanthanium").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":powder_lanthanium");
//    powder_actinium = new ItemCustomLore().setRarity(EnumRarity.epic).setUnlocalizedName("powder_actinium").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":powder_actinium");
//    powder_boron = new ItemCustomLore().setRarity(EnumRarity.epic).setUnlocalizedName("powder_boron").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":powder_boron");
    public static final RegistryObject<Item> POWDER_SEMTEX_MIX = parts("powder_semtex_mix", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_DESH_MIX = parts("powder_desh_mix", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_DESH_READY = parts("powder_desh_ready", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_NITAN_MIX = parts("powder_nitan_mix", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_SPARK_MIX = parts("powder_spark_mix", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_DESH = parts("powder_desh", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_STEEL_TINY = parts("powder_steel_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, ModTags.Items.SMALL_DUST_STEEL);
    public static final RegistryObject<Item> POWDER_LITHIUM_TINY = parts("powder_lithium_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_NEODYMIUM_TINY = parts("powder_neodymium_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_COBALT_TINY = parts("powder_cobalt_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_NIOBIUM_TINY = parts("powder_niobium_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_CERIUM_TINY = parts("powder_cerium_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_LANTHANIUM_TINY = parts("powder_lanthanium_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_ACTINIUM_TINY = parts("powder_actinium_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_BORON_TINY = parts("powder_boron_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_METEORITE_TINY = parts("powder_meteorite_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_YELLOWCAKE = parts("powder_yellowcake", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_MAGIC = parts("powder_magic", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_BALEFIRE = parts("powder_balefire", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_SAWDUST = parts("powder_sawdust", ()->new ItemFuel(new Item.Properties(), 200 / 2), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, ModTags.Items.SAWDUST);
    public static final RegistryObject<Item> POWDER_FLUX = parts("powder_flux", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    //    powder_fertilizer = new ItemFertilizer().setUnlocalizedName("powder_fertilizer").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":powder_fertilizer");
    public static final RegistryObject<Item> POWDER_COLTAN_ORE = parts("powder_coltan_ore", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_COLTAN = parts("powder_coltan", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_TEKTITE = parts("powder_tektite", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_PALEOGENITE = parts("powder_paleogenite", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_PALEOGENITE_TINY = parts("powder_paleogenite_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_IMPURE_OSMIRIDIUM = parts("powder_impure_osmiridium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_BORAX = parts("powder_borax", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_CHLOROCALCITE = parts("powder_chlorocalcite", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_MOLYSITE = parts("powder_molysite", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

    public static final RegistryObject<Item> FRAGMENT_NEODYMIUM = parts("fragment_neodymium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FRAGMENT_COBALT = parts("fragment_cobalt", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FRAGMENT_NIOBIUM = parts("fragment_niobium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FRAGMENT_CERIUM = parts("fragment_cerium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FRAGMENT_LANTHANIUM = parts("fragment_lanthanium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FRAGMENT_ACTINIUM = parts("fragment_actinium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FRAGMENT_BORON = parts("fragment_boron", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FRAGMENT_METEORITE = parts("fragment_meteorite", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FRAGMENT_COLTAN = parts("fragment_coltan", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
//    chunk_ore = new ItemEnumMulti(EnumChunkType.class, true, true).setUnlocalizedName("chunk_ore").setCreativeTab(MainRegistry.partsTab);

    public static final RegistryObject<Item> BIOMASS = parts("biomass", ()->new ItemFuel(new Item.Properties(), 200 * 2), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, ModTags.Items.BIOMASS);
    public static final RegistryObject<Item> BIOMASS_COMPRESSED = parts("biomass_compressed", ()->new ItemFuel(new Item.Properties(), 200 * 4), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
//    bio_wafer = new ItemLemon(4, 2F, false).setUnlocalizedName("bio_wafer").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":bio_wafer");
//    plant_item = new ItemEnumMulti(EnumPlantType.class, true, true).setUnlocalizedName("plant_item").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":plant_item");

    public static final RegistryObject<Item> COIL_COPPER = parts("coil_copper", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> COIL_COPPER_TORUS = parts("coil_copper_torus", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> COIL_TUNGSTEN = parts("coil_tungsten", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> TANK_STEEL = parts("tank_steel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> MOTOR = parts("motor", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> MOTOR_DESH = parts("motor_desh", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> MOTOR_BISMUTH = parts("motor_bismuth", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CENTRIFUGE_ELEMENT = parts("centrifuge_element", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> REACTOR_CORE = parts("reactor_core", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> RTG_UNIT = parts("rtg_unit", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> COIL_MAGNETIZED_TUNGSTEN = parts("coil_magnetized_tungsten", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> COIL_GOLD = parts("coil_gold", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> COIL_GOLD_TORUS = parts("coil_gold_torus", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CHLORINE_PINWHEEL = parts("chlorine_pinwheel", ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN);
    //    chlorine_pinwheel = new ItemInfiniteFluid(Fluids.CHLORINE, 1, 2).setUnlocalizedName("chlorine_pinwheel").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":chlorine_pinwheel");
//		FluidTank.noDualUnload.add(chlorine_pinwheel);public static final RegistryObject<Item> RING_STARMETAL = parts("ring_starmetal", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FLYWHEEL_BERYLLIUM = parts("flywheel_beryllium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> DEUTERIUM_FILTER = parts("deuterium_filter", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

    //    parts_legendary = new ItemEnumMulti(EnumLegendaryType.class, false, true).setUnlocalizedName("parts_legendary").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":parts_legendary");
//
//    gear_large = new ItemGear().setUnlocalizedName("gear_large").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":gear_large");
    public static final RegistryObject<Item> SAWBLADE = parts("sawblade", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);


    //    shell = new ItemAutogen(MaterialShapes.SHELL).oun("shellntm").setUnlocalizedName("shell").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":shell");
//    pipe = new ItemAutogen(MaterialShapes.PIPE).oun("pipentm").setUnlocalizedName("pipe").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":pipe");
    public static final RegistryObject<Item> FINS_FLAT = parts("fins_flat", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FINS_SMALL_STEEL = parts("fins_small_steel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FINS_BIG_STEEL = parts("fins_big_steel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FINS_TRI_STEEL = parts("fins_tri_steel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FINS_QUAD_TITANIUM = parts("fins_quad_titanium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SPHERE_STEEL = parts("sphere_steel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PEDESTAL_STEEL = parts("pedestal_steel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> DYSFUNCTIONAL_REACTOR = parts("dysfunctional_reactor", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BLADE_TITANIUM = parts("blade_titanium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> TURBINE_TITANIUM = parts("turbine_titanium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BLADE_TUNGSTEN = parts("blade_tungsten", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> TURBINE_TUNGSTEN = parts("turbine_tungsten", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);


    public static final RegistryObject<Item> TOOTHPICKS = parts("toothpicks", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> DUCTTAPE = parts("ducttape", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CATALYST_CLAY = parts("catalyst_clay", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

    public static final RegistryObject<Item> WARHEAD_GENERIC_SMALL = parts("warhead_generic_small", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> WARHEAD_GENERIC_MEDIUM = parts("warhead_generic_medium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> WARHEAD_GENERIC_LARGE = parts("warhead_generic_large", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> WARHEAD_INCENDIARY_SMALL = parts("warhead_incendiary_small", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> WARHEAD_INCENDIARY_MEDIUM = parts("warhead_incendiary_medium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> WARHEAD_INCENDIARY_LARGE = parts("warhead_incendiary_large", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> WARHEAD_CLUSTER_SMALL = parts("warhead_cluster_small", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> WARHEAD_CLUSTER_MEDIUM = parts("warhead_cluster_medium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> WARHEAD_CLUSTER_LARGE = parts("warhead_cluster_large", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> WARHEAD_BUSTER_SMALL = parts("warhead_buster_small", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> WARHEAD_BUSTER_MEDIUM = parts("warhead_buster_medium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> WARHEAD_BUSTER_LARGE = parts("warhead_buster_large", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> WARHEAD_NUCLEAR = parts("warhead_nuclear", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> WARHEAD_MIRV = parts("warhead_mirv", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> WARHEAD_VOLCANO = parts("warhead_volcano", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

    public static final RegistryObject<Item> FUEL_TANK_SMALL = parts("fuel_tank_small", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FUEL_TANK_MEDIUM = parts("fuel_tank_medium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FUEL_TANK_LARGE = parts("fuel_tank_large", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> THRUSTER_SMALL = parts("thruster_small", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> THRUSTER_MEDIUM = parts("thruster_medium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> THRUSTER_LARGE = parts("thruster_large", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SAT_HEAD_MAPPER = parts("sat_head_mapper", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SAT_HEAD_SCANNER = parts("sat_head_scanner", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SAT_HEAD_RADAR = parts("sat_head_radar", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SAT_HEAD_LASER = parts("sat_head_laser", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SAT_HEAD_RESONATOR = parts("sat_head_resonator", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SEG_10 = parts("seg_10", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SEG_15 = parts("seg_15", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SEG_20 = parts("seg_20", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> COMBINE_SCRAP = parts("combine_scrap", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SHIMMER_HEAD = parts("shimmer_head", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SHIMMER_AXE_HEAD = parts("shimmer_axe_head", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SHIMMER_HANDLE = parts("shimmer_handle", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

    public static final RegistryObject<Item> POWDER_FIRE = parts("powder_red_phosphorus", ()->new ItemFuel(new Item.Properties(), 6400), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

    public static final RegistryObject<Item> GUIDE_BOOK = consumable("book_guide", ()->new WritableBookItem(new Item.Properties()), HBMKey.REVERSE_GEN);

//    entanglement_kit = new ItemCustomLore().setUnlocalizedName("entanglement_kit").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":entanglement_kit");
//
//    circuit = new ItemCircuit().setUnlocalizedName("circuit").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":circuit");public static final RegistryObject<Item> CRT_DISPLAY = parts("crt_display", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
//
//    circuit_star_piece = (ItemEnumMulti) new ItemEnumMulti(ScrapType.class, true, true).setUnlocalizedName("circuit_star_piece").setCreativeTab(null);
//    circuit_star_component = (ItemEnumMulti) new ItemCircuitStarComponent().setUnlocalizedName("circuit_star_component").setCreativeTab(null);
//    circuit_star = new ItemCustomLore().setRarity(EnumRarity.uncommon).setUnlocalizedName("circuit_star").setCreativeTab(null).setTextureName(RefStrings.MODID + ":circuit_star");public static final RegistryObject<Item> ASSEMBLY_NUKE = parts("assembly_nuke", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
//
//    casing = new ItemEnumMulti(ItemEnums.EnumCasingType.class, true, true).setUnlocalizedName("casing").setCreativeTab(MainRegistry.partsTab).setTextureName(RefStrings.MODID + ":casing");
//
    public static final RegistryObject<Item> WIRING_RED_COPPER = parts("wiring_red_copper", ()->new ItemWiring(new Item.Properties().stacksTo(1).durability(20)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
//
//    pellet_rtg_depleted = new ItemRTGPelletDepleted().setContainerItem(plate_iron).setUnlocalizedName("pellet_rtg_depleted").setCreativeTab(MainRegistry.controlTab);
//
//    pellet_rtg_radium = new ItemRTGPellet(3).setDecays(DepletedRTGMaterial.LEAD, (long) (RTGUtil.getLifespan(16.0F, HalfLifeType.LONG, false) * 1.5)).setUnlocalizedName("pellet_rtg_radium").setCreativeTab(MainRegistry.controlTab).setMaxStackSize(1).setTextureName(RefStrings.MODID + ":pellet_rtg_radium");
//    pellet_rtg_weak = new ItemRTGPellet(5).setDecays(DepletedRTGMaterial.LEAD, (long) (RTGUtil.getLifespan(1.0F, HalfLifeType.LONG, false) * 1.5)).setUnlocalizedName("pellet_rtg_weak").setCreativeTab(MainRegistry.controlTab).setMaxStackSize(1).setTextureName(RefStrings.MODID + ":pellet_rtg_weak");
//    pellet_rtg = new ItemRTGPellet(10).setDecays(DepletedRTGMaterial.LEAD, (long) (RTGUtil.getLifespan(87.7F, HalfLifeType.MEDIUM, false) * 1.5)).setUnlocalizedName("pellet_rtg").setCreativeTab(MainRegistry.controlTab).setMaxStackSize(1).setTextureName(RefStrings.MODID + ":pellet_rtg");
//    pellet_rtg_strontium = new ItemRTGPellet(15).setDecays(DepletedRTGMaterial.ZIRCONIUM, (long) (RTGUtil.getLifespan(29.0F, HalfLifeType.MEDIUM, false) * 1.5)).setUnlocalizedName("pellet_rtg_strontium").setCreativeTab(MainRegistry.controlTab).setTextureName(RefStrings.MODID + ":pellet_rtg_strontium");
//    pellet_rtg_cobalt = new ItemRTGPellet(15).setDecays(DepletedRTGMaterial.NICKEL, (long) (RTGUtil.getLifespan(5.3F, HalfLifeType.MEDIUM, false) * 1.5)).setUnlocalizedName("pellet_rtg_cobalt").setCreativeTab(MainRegistry.controlTab).setTextureName(RefStrings.MODID + ":pellet_rtg_cobalt");
//    pellet_rtg_actinium = new ItemRTGPellet(20).setDecays(DepletedRTGMaterial.LEAD, (long) (RTGUtil.getLifespan(21.8F, HalfLifeType.MEDIUM, false) * 1.5)).setUnlocalizedName("pellet_rtg_actinium").setCreativeTab(MainRegistry.controlTab).setTextureName(RefStrings.MODID + ":pellet_rtg_actinium");
//    pellet_rtg_americium = new ItemRTGPellet(20).setDecays(DepletedRTGMaterial.NEPTUNIUM, (long) (RTGUtil.getLifespan(4.7F, HalfLifeType.LONG, false) * 1.5)).setUnlocalizedName("pellet_rtg_americium").setCreativeTab(MainRegistry.controlTab).setMaxStackSize(1).setTextureName(RefStrings.MODID + ":pellet_rtg_americium");
//    pellet_rtg_polonium = new ItemRTGPellet(50).setDecays(DepletedRTGMaterial.LEAD, (long) (RTGUtil.getLifespan(138.0F, HalfLifeType.SHORT, false) * 1.5)).setUnlocalizedName("pellet_rtg_polonium").setCreativeTab(MainRegistry.controlTab).setMaxStackSize(1).setTextureName(RefStrings.MODID + ":pellet_rtg_polonium");
//    pellet_rtg_gold = new ItemRTGPellet(VersatileConfig.rtgDecay() ? 200 : 100).setDecays(DepletedRTGMaterial.MERCURY, (long) (RTGUtil.getLifespan(2.7F, HalfLifeType.SHORT, false) * 1.5)).setUnlocalizedName("pellet_rtg_gold").setCreativeTab(MainRegistry.controlTab).setMaxStackSize(1).setTextureName(RefStrings.MODID + ":pellet_rtg_gold");
//    pellet_rtg_lead = new ItemRTGPellet(VersatileConfig.rtgDecay() ? 600 : 200).setDecays(DepletedRTGMaterial.BISMUTH, (long) (RTGUtil.getLifespan(0.3F, HalfLifeType.SHORT, false) * 1.5)).setUnlocalizedName("pellet_rtg_lead").setCreativeTab(MainRegistry.controlTab).setTextureName(RefStrings.MODID + ":pellet_rtg_lead");

    // 锻压机压印版
    public static final RegistryObject<Item> STAMP_STONE_FLAT = new WrapperRegistry.ItemBuilder("stamp_stone_flat", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(32), ItemStamp.StampType.FLAT)).tab(ModTabs.CONTROL.getKey()).loc("Flat Stamp (Stone)").build();
    public static final RegistryObject<Item> STAMP_STONE_PLATE = new WrapperRegistry.ItemBuilder("stamp_stone_plate", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(32), ItemStamp.StampType.PLATE)).tab(ModTabs.CONTROL.getKey()).loc("Plate Stamp (Stone)").build();
    public static final RegistryObject<Item> STAMP_STONE_WIRE = new WrapperRegistry.ItemBuilder("stamp_stone_wire", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(32), ItemStamp.StampType.WIRE)).tab(ModTabs.CONTROL.getKey()).loc("Wire Stamp (Stone)").build();
    public static final RegistryObject<Item> STAMP_STONE_CIRCUIT = new WrapperRegistry.ItemBuilder("stamp_stone_circuit", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(32), ItemStamp.StampType.CIRCUIT)).tab(ModTabs.CONTROL.getKey()).loc("Circuit Stamp (Stone)").build();
    public static final RegistryObject<Item> STAMP_IRON_FLAT = new WrapperRegistry.ItemBuilder("stamp_iron_flat", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(64), ItemStamp.StampType.FLAT)).tab(ModTabs.CONTROL.getKey()).loc("Flat Stamp (Iron)").build();
    public static final RegistryObject<Item> STAMP_IRON_PLATE = new WrapperRegistry.ItemBuilder("stamp_iron_plate", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(64), ItemStamp.StampType.PLATE)).tab(ModTabs.CONTROL.getKey()).loc("Plate Stamp (Iron)").build();
    public static final RegistryObject<Item> STAMP_IRON_WIRE = new WrapperRegistry.ItemBuilder("stamp_iron_wire", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(64), ItemStamp.StampType.WIRE)).tab(ModTabs.CONTROL.getKey()).loc("Wire Stamp (Iron)").build();
    public static final RegistryObject<Item> STAMP_IRON_CIRCUIT = new WrapperRegistry.ItemBuilder("stamp_iron_circuit", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(64), ItemStamp.StampType.CIRCUIT)).tab(ModTabs.CONTROL.getKey()).loc("Circuit Stamp (Iron)").build();
    public static final RegistryObject<Item> STAMP_STEEL_FLAT = new WrapperRegistry.ItemBuilder("stamp_steel_flat", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(192), ItemStamp.StampType.FLAT)).tab(ModTabs.CONTROL.getKey()).loc("Flat Stamp (Steel)").build();
    public static final RegistryObject<Item> STAMP_STEEL_PLATE = new WrapperRegistry.ItemBuilder("stamp_steel_plate", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(192), ItemStamp.StampType.PLATE)).tab(ModTabs.CONTROL.getKey()).loc("Plate Stamp (Steel)").build();
    public static final RegistryObject<Item> STAMP_STEEL_WIRE = new WrapperRegistry.ItemBuilder("stamp_steel_wire", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(192), ItemStamp.StampType.WIRE)).tab(ModTabs.CONTROL.getKey()).loc("Wire Stamp (Steel)").build();
    public static final RegistryObject<Item> STAMP_STEEL_CIRCUIT = new WrapperRegistry.ItemBuilder("stamp_steel_circuit", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(192), ItemStamp.StampType.CIRCUIT)).tab(ModTabs.CONTROL.getKey()).loc("Circuit Stamp (Steel)").build();
    public static final RegistryObject<Item> STAMP_TITANIUM_FLAT = new WrapperRegistry.ItemBuilder("stamp_titanium_flat", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(256), ItemStamp.StampType.FLAT)).tab(ModTabs.CONTROL.getKey()).loc("Flat Stamp (Titanium)").build();
    public static final RegistryObject<Item> STAMP_TITANIUM_PLATE = new WrapperRegistry.ItemBuilder("stamp_titanium_plate", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(256), ItemStamp.StampType.PLATE)).tab(ModTabs.CONTROL.getKey()).loc("Plate Stamp (Titanium)").build();
    public static final RegistryObject<Item> STAMP_TITANIUM_WIRE = new WrapperRegistry.ItemBuilder("stamp_titanium_wire", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(256), ItemStamp.StampType.WIRE)).tab(ModTabs.CONTROL.getKey()).loc("Wire Stamp (Titanium)").build();
    public static final RegistryObject<Item> STAMP_TITANIUM_CIRCUIT = new WrapperRegistry.ItemBuilder("stamp_titanium_circuit", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(256), ItemStamp.StampType.CIRCUIT)).tab(ModTabs.CONTROL.getKey()).loc("Circuit Stamp (Titanium)").build();
    public static final RegistryObject<Item> STAMP_OBSIDIAN_FLAT = new WrapperRegistry.ItemBuilder("stamp_obsidian_flat", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(512), ItemStamp.StampType.FLAT)).tab(ModTabs.CONTROL.getKey()).loc("Flat Stamp (Obsidian)").build();
    public static final RegistryObject<Item> STAMP_OBSIDIAN_PLATE = new WrapperRegistry.ItemBuilder("stamp_obsidian_plate", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(512), ItemStamp.StampType.PLATE)).tab(ModTabs.CONTROL.getKey()).loc("Plate Stamp (Obsidian)").build();
    public static final RegistryObject<Item> STAMP_OBSIDIAN_WIRE = new WrapperRegistry.ItemBuilder("stamp_obsidian_wire", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(512), ItemStamp.StampType.WIRE)).tab(ModTabs.CONTROL.getKey()).loc("Wire Stamp (Obsidian)").build();
    public static final RegistryObject<Item> STAMP_OBSIDIAN_CIRCUIT = new WrapperRegistry.ItemBuilder("stamp_obsidian_circuit", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(512), ItemStamp.StampType.CIRCUIT)).tab(ModTabs.CONTROL.getKey()).loc("Circuit Stamp (Obsidian)").build();
    public static final RegistryObject<Item> STAMP_DESH_FLAT = new WrapperRegistry.ItemBuilder("stamp_desh_flat", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(0), ItemStamp.StampType.FLAT)).tab(ModTabs.CONTROL.getKey()).loc("Flat Stamp (Desh)").build();
    public static final RegistryObject<Item> STAMP_DESH_PLATE = new WrapperRegistry.ItemBuilder("stamp_desh_plate", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(0), ItemStamp.StampType.PLATE)).tab(ModTabs.CONTROL.getKey()).loc("Plate Stamp (Desh)").build();
    public static final RegistryObject<Item> STAMP_DESH_WIRE = new WrapperRegistry.ItemBuilder("stamp_desh_wire", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(0), ItemStamp.StampType.WIRE)).tab(ModTabs.CONTROL.getKey()).loc("Wire Stamp (Desh)").build();
    public static final RegistryObject<Item> STAMP_DESH_CIRCUIT = new WrapperRegistry.ItemBuilder("stamp_desh_circuit", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(0), ItemStamp.StampType.CIRCUIT)).tab(ModTabs.CONTROL.getKey()).loc("Circuit Stamp (Desh)").build();
    public static final RegistryObject<Item> STAMP_357 = new WrapperRegistry.ItemBuilder("stamp_357", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(1000), ItemStamp.StampType.C357)).tab(ModTabs.CONTROL.getKey()).loc(".357 Magnum Stamp").build();
    public static final RegistryObject<Item> STAMP_44 = new WrapperRegistry.ItemBuilder("stamp_44", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(1000), ItemStamp.StampType.C44)).tab(ModTabs.CONTROL.getKey()).loc(".44 Magnum Stamp").build();
    public static final RegistryObject<Item> STAMP_9 = new WrapperRegistry.ItemBuilder("stamp_9", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(1000), ItemStamp.StampType.C9)).tab(ModTabs.CONTROL.getKey()).loc("Small Caliber Stamp").build();
    public static final RegistryObject<Item> STAMP_50 = new WrapperRegistry.ItemBuilder("stamp_50", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(1000), ItemStamp.StampType.C50)).tab(ModTabs.CONTROL.getKey()).loc("Large Caliber Stamp").build();
    public static final RegistryObject<Item> STAMP_DESH_357 = new WrapperRegistry.ItemBuilder("stamp_357_desh", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(0), ItemStamp.StampType.C357)).tab(ModTabs.CONTROL.getKey()).loc(".357 Magnum Stamp (Desh)").build();
    public static final RegistryObject<Item> STAMP_DESH_44 = new WrapperRegistry.ItemBuilder("stamp_44_desh", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(0), ItemStamp.StampType.C44)).tab(ModTabs.CONTROL.getKey()).loc(".44 Magnum Stamp (Desh)").build();
    public static final RegistryObject<Item> STAMP_DESH_9 = new WrapperRegistry.ItemBuilder("stamp_9_desh", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(0), ItemStamp.StampType.C9)).tab(ModTabs.CONTROL.getKey()).loc("Small Caliber Stamp (Desh)").build();
    public static final RegistryObject<Item> STAMP_DESH_50 = new WrapperRegistry.ItemBuilder("stamp_50_desh", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(0), ItemStamp.StampType.C50)).tab(ModTabs.CONTROL.getKey()).loc("Large Caliber Stamp (Desh)").build();

    // 电池
    public static final RegistryObject<Item> BATTERY_CREATIVE = control("battery_creative",()->new BatteryItem(-1, 1_000_000L, new Item.Properties().stacksTo(1)), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> BATTERY_GENERIC = control("battery_generic",()->new BatteryItem(false,5_000, 100, new Item.Properties().stacksTo(1)), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> BATTERY_ADVANCED = control("battery_advanced",()->new BatteryItem(false,60_000, 500, new Item.Properties().stacksTo(1)), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> BATTERY_LITHIUM = control("battery_lithium",()->new BatteryItem(false,250_000, 1000, new Item.Properties().stacksTo(1)), HBMKey.REVERSE_GEN);

    // 其他hbmcomponent的东西，暂时放一下
    public static final RegistryObject<Item> LASER_CRYSTAL_DIGAMMA = control("laser_crystal_digamma", ()->new ItemFELCrystal(new Item.Properties(), ItemFELCrystal.EnumWavelengths.DRX), "Digamma Laser Crystal");
    public static final RegistryObject<Item> CIRCUIT_BASIC = parts("circuit_basic", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> EGG_GLYPHID = parts("egg_glyphid_base",()->new ItemEggGlyphid(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> EGG_GLYPHID_TO_BIRTH = parts("egg_glyphid",()->new ItemEggGlyphidToBirth(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> WIRE_FINE_ALUMINIUM = parts("wire_aluminium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SHELL = parts("shell", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> DUCT_TAPE = parts("duct_tape", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

    public static final RegistryObject<Item> SCRAP = parts("scrap", () -> new ItemFuel(new Item.Properties(), 200 / 4), HBMKey.ORDERLY_GEN);



    //    public static final RegistryObject<Item> detonator = ITEMS.register("detonator",()->new ItemDetonator(new Item.Properties()));
    public static final RegistryObject<Item> DETONATOR = add("detonator", ()->new ItemDetonator(new Item.Properties()), ModTabs.NUKE.getKey(), HBMKey.BASIC_MODEL, HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> GUN_SUICIDE = add("gun_suicide", ()->new GunSuicide(new Item.Properties()), ModTabs.WEAPON.getKey(), HBMKey.BASIC_MODEL, HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    //    public static RegistryObject<Item> GLYPHID_SPAWN_EGG;
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
    public static final RegistryObject<Item> SCREWDRIVER = control("screwdriver", ()->new ItemTooling(new Item.Properties().stacksTo(1).durability(100).setNoRepair(), ToolType.SCREWDRIVER), "Screw");
    //升级组件
    public static final RegistryObject<Item> UPGRADE_BASE = ITEMS.register("upgrade_base",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> UPGRADE_SPEED_1 = control("upgrade_speed_1", ()->new ItemMachineUpgrade(UpgradeType.SPEED, 1), "Speed Upgrade Tiler 1");
    public static final RegistryObject<Item> UPGRADE_SPEED_2 = control("upgrade_speed_2", () -> new ItemMachineUpgrade(UpgradeType.SPEED, 2), "Speed Upgrade Tier 2");
    public static final RegistryObject<Item> UPGRADE_SPEED_3 = control("upgrade_speed_3", () -> new ItemMachineUpgrade(UpgradeType.SPEED, 3), "Speed Upgrade Tier 3");
    public static final RegistryObject<Item> UPGRADE_EFFECT_1 = control("upgrade_effect_1", () -> new ItemMachineUpgrade(UpgradeType.EFFECT, 1), "Effect Upgrade Tier 1");
    public static final RegistryObject<Item> UPGRADE_EFFECT_2 = control("upgrade_effect_2", () -> new ItemMachineUpgrade(UpgradeType.EFFECT, 2), "Effect Upgrade Tier 2");
    public static final RegistryObject<Item> UPGRADE_EFFECT_3 = control("upgrade_effect_3", () -> new ItemMachineUpgrade(UpgradeType.EFFECT, 3), "Effect Upgrade Tier 3");
    public static final RegistryObject<Item> UPGRADE_POWER_1 = control("upgrade_power_1", () -> new ItemMachineUpgrade(UpgradeType.POWER, 1), "Power Upgrade Tier 1");
    public static final RegistryObject<Item> UPGRADE_POWER_2 = control("upgrade_power_2", () -> new ItemMachineUpgrade(UpgradeType.POWER, 2), "Power Upgrade Tier 2");
    public static final RegistryObject<Item> UPGRADE_POWER_3 = control("upgrade_power_3", () -> new ItemMachineUpgrade(UpgradeType.POWER, 3), "Power Upgrade Tier 3");
    public static final RegistryObject<Item> UPGRADE_FORTUNE_1 = control("upgrade_fortune_1", () -> new ItemMachineUpgrade(UpgradeType.FORTUNE, 1), "Fortune Upgrade Tier 1");
    public static final RegistryObject<Item> UPGRADE_FORTUNE_2 = control("upgrade_fortune_2", () -> new ItemMachineUpgrade(UpgradeType.FORTUNE, 2), "Fortune Upgrade Tier 2");
    public static final RegistryObject<Item> UPGRADE_FORTUNE_3 = control("upgrade_fortune_3", () -> new ItemMachineUpgrade(UpgradeType.FORTUNE, 3), "Fortune Upgrade Tier 3");
    public static final RegistryObject<Item> UPGRADE_AFTERBURN_1 = control("upgrade_afterburn_1", () -> new ItemMachineUpgrade(UpgradeType.AFTERBURN, 1), "Afterburn Upgrade Tier 1");
    public static final RegistryObject<Item> UPGRADE_AFTERBURN_2 = control("upgrade_afterburn_2", () -> new ItemMachineUpgrade(UpgradeType.AFTERBURN, 2), "Afterburn Upgrade Tier 2");
    public static final RegistryObject<Item> UPGRADE_AFTERBURN_3 = control("upgrade_afterburn_3", () -> new ItemMachineUpgrade(UpgradeType.AFTERBURN, 3), "Afterburn Upgrade Tier 3");
    public static final RegistryObject<Item> UPGRADE_OVERDRIVE_1 = control("upgrade_overdrive_1", () -> new ItemMachineUpgrade(UpgradeType.OVERDRIVE, 1), "Overdrive Upgrade Tier 1");
    public static final RegistryObject<Item> UPGRADE_OVERDRIVE_2 = control("upgrade_overdrive_2", () -> new ItemMachineUpgrade(UpgradeType.OVERDRIVE, 2), "Overdrive Upgrade Tier 2");
    public static final RegistryObject<Item> UPGRADE_OVERDRIVE_3 = control("upgrade_overdrive_3", () -> new ItemMachineUpgrade(UpgradeType.OVERDRIVE, 3), "Overdrive Upgrade Tier 3");
    public static final RegistryObject<Item> UPGRADE_RADIUS = control("upgrade_radius", () -> new ItemMachineUpgrade(new Item.Properties().stacksTo(16)), "Radius Upgrade");
    public static final RegistryObject<Item> UPGRADE_HEALTH = control("upgrade_health", () -> new ItemMachineUpgrade(new Item.Properties().stacksTo(16)), "Health Upgrade");
    public static final RegistryObject<Item> UPGRADE_SMELTER = control("upgrade_smelter", ItemMachineUpgrade::new, "Smelter Upgrade");
    public static final RegistryObject<Item> UPGRADE_SHREDDER = control("upgrade_shredder", ItemMachineUpgrade::new, "Shredder Upgrade");
    public static final RegistryObject<Item> UPGRADE_CENTRIFUGE = control("upgrade_centrifuge", ItemMachineUpgrade::new, "Centrifuge Upgrade");
    public static final RegistryObject<Item> UPGRADE_CRYSTALLIZER = control("upgrade_crystallizer", ItemMachineUpgrade::new, "Crystallizer Upgrade");
    public static final RegistryObject<Item> UPGRADE_NULLIFIER = control("upgrade_nullifier", ItemMachineUpgrade::new, "Nullifier Upgrade");
    public static final RegistryObject<Item> UPGRADE_SCREM = control("upgrade_screm", ItemMachineUpgrade::new, "Screm Upgrade");
    public static final RegistryObject<Item> UPGRADE_GC_SPEED = control("upgrade_gc_speed", ItemMachineUpgrade::new, "GC Speed Upgrade");
    public static final RegistryObject<Item> UPGRADE_5G = control("upgrade_5g", ItemMachineUpgrade::new, "5G Upgrade");
    // 这里tier设为0是暂时留空，通过HBMUpgrade#setUpgrade设置等级。
    public static final RegistryObject<Item> UPGRADE_STACK = control("upgrade_stack", () -> new ItemMachineUpgrade(UpgradeType.SPECIAL, 0), "Stack Upgrade");
    public static final RegistryObject<Item> UPGRADE_EJECTOR = control("upgrade_ejector", () -> new ItemMachineUpgrade(UpgradeType.SPECIAL, 0), "Ejector Upgrade");

    //导弹
//    public static final RegistryObject<Item> DESIGNATOR = ITEMS.register("designator",()->new ItemDesignator(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MISSILE_GENERIC = ITEMS.register("missile_generic",()->new ItemMissile(new Item.Properties(), ItemMissile.MissileFormFactor.V2, ItemMissile.MissileTier.TIER1,
            (level, x, y, z, target) -> EntityMissileTier0.EntityMissileTest.create(level, x, y, z, target, EntityMissileTier0.EntityMissileTest.Payload.GENERIC)));
    // RBMK
    public static final RegistryObject<Item> rbmk_lid = machine("rbmk_lid", () -> new ItemRBMKLid(new Item.Properties(), RBMKLidType.SOLID));
    public static final RegistryObject<Item> rbmk_lid_glass = machine("rbmk_lid_glass", () -> new ItemRBMKLid(new Item.Properties(), RBMKLidType.GLASS));
    public static final RegistryObject<Item> rbmk_control_rod = machine("rbmk_control_rod", () -> new ItemRBMKControlRod(new Item.Properties().stacksTo(1)), HBMKey.GEN_STANDALONE);
    // RBMK fuel rods (placeholder stats, real values will be wired in later).
    public static final RegistryObject<Item> rbmk_fuel_base = machine("rbmk_fuel_base", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 12.0D, 20 * 60 * 10));
    public static final RegistryObject<Item> rbmk_fuel_lea = machine("rbmk_fuel_lea", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 14.0D, 20 * 60 * 12));
    public static final RegistryObject<Item> rbmk_fuel_leaus = machine("rbmk_fuel_leaus", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 16.0D, 20 * 60 * 12));
    public static final RegistryObject<Item> rbmk_fuel_lep = machine("rbmk_fuel_lep", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 18.0D, 20 * 60 * 10));
    public static final RegistryObject<Item> rbmk_fuel_les = machine("rbmk_fuel_les", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 18.0D, 20 * 60 * 10));
    public static final RegistryObject<Item> rbmk_fuel_mea = machine("rbmk_fuel_mea", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 22.0D, 20 * 60 * 10));
    public static final RegistryObject<Item> rbmk_fuel_men = machine("rbmk_fuel_men", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 24.0D, 20 * 60 * 10));
    public static final RegistryObject<Item> rbmk_fuel_mep = machine("rbmk_fuel_mep", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 26.0D, 20 * 60 * 10));
    public static final RegistryObject<Item> rbmk_fuel_mes = machine("rbmk_fuel_mes", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 26.0D, 20 * 60 * 10));
    public static final RegistryObject<Item> rbmk_fuel_meu = machine("rbmk_fuel_meu", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 28.0D, 20 * 60 * 12));
    public static final RegistryObject<Item> rbmk_fuel_ueu = machine("rbmk_fuel_ueu", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 20.0D, 20 * 60 * 12));
    public static final RegistryObject<Item> rbmk_fuel_mox = machine("rbmk_fuel_mox", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 32.0D, 20 * 60 * 16));
    public static final RegistryObject<Item> rbmk_fuel_heu233 = machine("rbmk_fuel_heu233", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 38.0D, 20 * 60 * 14));
    public static final RegistryObject<Item> rbmk_fuel_heu235 = machine("rbmk_fuel_heu235", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 40.0D, 20 * 60 * 14));
    public static final RegistryObject<Item> rbmk_fuel_heaus = machine("rbmk_fuel_heaus", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 36.0D, 20 * 60 * 14));
    public static final RegistryObject<Item> rbmk_fuel_hea241 = machine("rbmk_fuel_hea241", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 42.0D, 20 * 60 * 12));
    public static final RegistryObject<Item> rbmk_fuel_hea242 = machine("rbmk_fuel_hea242", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 42.0D, 20 * 60 * 12));
    public static final RegistryObject<Item> rbmk_fuel_hen = machine("rbmk_fuel_hen", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 44.0D, 20 * 60 * 10));
    public static final RegistryObject<Item> rbmk_fuel_hep = machine("rbmk_fuel_hep", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 44.0D, 20 * 60 * 12));
    public static final RegistryObject<Item> rbmk_fuel_hep241 = machine("rbmk_fuel_hep241", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 46.0D, 20 * 60 * 12));
    public static final RegistryObject<Item> rbmk_fuel_hes = machine("rbmk_fuel_hes", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 44.0D, 20 * 60 * 10));
    public static final RegistryObject<Item> rbmk_fuel_thmeu = machine("rbmk_fuel_thmeu", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 30.0D, 20 * 60 * 16));
    public static final RegistryObject<Item> rbmk_fuel_drx = machine("rbmk_fuel_drx", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 55.0D, 20 * 60 * 6));
    public static final RegistryObject<Item> rbmk_fuel_flashlead = machine("rbmk_fuel_flashlead", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 50.0D, 20 * 60 * 8));
    public static final RegistryObject<Item> rbmk_fuel_balefire = machine("rbmk_fuel_balefire", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 80.0D, 20 * 60 * 4));
    public static final RegistryObject<Item> rbmk_fuel_balefire_gold = machine("rbmk_fuel_balefire_gold", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 90.0D, 20 * 60 * 4));
    public static final RegistryObject<Item> rbmk_fuel_po210be = machine("rbmk_fuel_po210be", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 5.0D, 20 * 60 * 20));
    public static final RegistryObject<Item> rbmk_fuel_pu238be = machine("rbmk_fuel_pu238be", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 6.0D, 20 * 60 * 20));
    public static final RegistryObject<Item> rbmk_fuel_ra226be = machine("rbmk_fuel_ra226be", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 4.0D, 20 * 60 * 20));
    public static final RegistryObject<Item> rbmk_fuel_zfb_base = machine("rbmk_fuel_zfb_base", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 24.0D, 20 * 60 * 10));
    public static final RegistryObject<Item> rbmk_fuel_zfb_bismuth = machine("rbmk_fuel_zfb_bismuth", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 26.0D, 20 * 60 * 10));
    public static final RegistryObject<Item> rbmk_fuel_zfb_pu241 = machine("rbmk_fuel_zfb_pu241", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 36.0D, 20 * 60 * 12));
    public static final RegistryObject<Item> rbmk_fuel_zfb_am_mix = machine("rbmk_fuel_zfb_am_mix", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 40.0D, 20 * 60 * 8));
    public static final RegistryObject<Item> rbmk_fuel_test = machine("rbmk_fuel_test", () -> new ItemRBMKFuelRod(new Item.Properties().stacksTo(1), 40.0D, 20 * 60));
    public static final RegistryObject<Item> rbmk_fuel_empty = machine("rbmk_fuel_empty", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> icf_pellet = control("icf_pellet", () -> new ItemICFPellet(new Item.Properties()), HBMKey.GEN_STANDALONE);
    public static final RegistryObject<Item> icf_pellet_depleted = control("icf_pellet_depleted", () -> new Item(new Item.Properties().stacksTo(1)), HBMKey.GEN_STANDALONE);
    public static final RegistryObject<Item> icf_pellet_empty = control("icf_pellet_empty", () -> new Item(new Item.Properties().stacksTo(1)), HBMKey.GEN_STANDALONE);
    public static final RegistryObject<Item> plate_fuel_u233 = control("plate_fuel_u233", () -> new ItemResearchFuelPlate(new Item.Properties(), 2_200_000, ItemResearchFuelPlate.FunctionType.SQUARE_ROOT, 50), HBMKey.GEN_STANDALONE);
    public static final RegistryObject<Item> plate_fuel_u235 = control("plate_fuel_u235", () -> new ItemResearchFuelPlate(new Item.Properties(), 2_200_000, ItemResearchFuelPlate.FunctionType.SQUARE_ROOT, 40), HBMKey.GEN_STANDALONE);
    public static final RegistryObject<Item> plate_fuel_mox = control("plate_fuel_mox", () -> new ItemResearchFuelPlate(new Item.Properties(), 2_400_000, ItemResearchFuelPlate.FunctionType.LOGARITHM, 50), HBMKey.GEN_STANDALONE);
    public static final RegistryObject<Item> plate_fuel_pu239 = control("plate_fuel_pu239", () -> new ItemResearchFuelPlate(new Item.Properties(), 2_000_000, ItemResearchFuelPlate.FunctionType.NEGATIVE_QUADRATIC, 50), HBMKey.GEN_STANDALONE);
    public static final RegistryObject<Item> plate_fuel_sa326 = control("plate_fuel_sa326", () -> new ItemResearchFuelPlate(new Item.Properties(), 2_000_000, ItemResearchFuelPlate.FunctionType.LINEAR, 80), HBMKey.GEN_STANDALONE);
    public static final RegistryObject<Item> plate_fuel_ra226be = control("plate_fuel_ra226be", () -> new ItemResearchFuelPlate(new Item.Properties(), 1_300_000, ItemResearchFuelPlate.FunctionType.PASSIVE, 30), HBMKey.GEN_STANDALONE);
    public static final RegistryObject<Item> plate_fuel_pu238be = control("plate_fuel_pu238be", () -> new ItemResearchFuelPlate(new Item.Properties(), 1_000_000, ItemResearchFuelPlate.FunctionType.PASSIVE, 50), HBMKey.GEN_STANDALONE);
    public static final RegistryObject<Item> waste_plate_u233 = control("waste_plate_u233", () -> new Item(new Item.Properties().stacksTo(1)), HBMKey.GEN_STANDALONE);
    public static final RegistryObject<Item> waste_plate_u235 = control("waste_plate_u235", () -> new Item(new Item.Properties().stacksTo(1)), HBMKey.GEN_STANDALONE);
    public static final RegistryObject<Item> waste_plate_mox = control("waste_plate_mox", () -> new Item(new Item.Properties().stacksTo(1)), HBMKey.GEN_STANDALONE);
    public static final RegistryObject<Item> waste_plate_pu239 = control("waste_plate_pu239", () -> new Item(new Item.Properties().stacksTo(1)), HBMKey.GEN_STANDALONE);
    public static final RegistryObject<Item> waste_plate_sa326 = control("waste_plate_sa326", () -> new Item(new Item.Properties().stacksTo(1)), HBMKey.GEN_STANDALONE);
    public static final RegistryObject<Item> waste_plate_ra226be = control("waste_plate_ra226be", () -> new Item(new Item.Properties().stacksTo(1)), HBMKey.GEN_STANDALONE);
    public static final RegistryObject<Item> waste_plate_pu238be = control("waste_plate_pu238be", () -> new Item(new Item.Properties().stacksTo(1)), HBMKey.GEN_STANDALONE);

    public static final RegistryObject<Item> pwr_fuel = control("pwr_fuel", () -> new ItemPWRFuel(new Item.Properties(), ItemPWRFuel.FuelState.FRESH), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> pwr_fuel_hot = control("pwr_fuel_hot", () -> new ItemPWRFuel(new Item.Properties(), ItemPWRFuel.FuelState.HOT), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> pwr_fuel_depleted = control("pwr_fuel_depleted", () -> new ItemPWRFuel(new Item.Properties(), ItemPWRFuel.FuelState.DEPLETED), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> pwr_printer = control("pwr_printer", () -> new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

    public static final RegistryObject<Item> PILE_ROD_URANIUM = control("pile_rod_uranium",
            () -> new ItemPileRod(new Item.Properties().stacksTo(1), ItemPileRod.Spec.fuel(0.8D, 0.25D, 50_000, 1)),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PILE_ROD_PU239 = control("pile_rod_pu239",
            () -> new ItemPileRod(new Item.Properties().stacksTo(1), ItemPileRod.Spec.fuel(1.0D, 0.3D, 40_000, 1)),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PILE_ROD_PLUTONIUM = control("pile_rod_plutonium",
            () -> new ItemPileRod(new Item.Properties().stacksTo(1), ItemPileRod.Spec.source(2.0D, 0.0D, 0, 1)),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PILE_ROD_SOURCE = control("pile_rod_source",
            () -> new ItemPileRod(new Item.Properties().stacksTo(1), ItemPileRod.Spec.source(1.0D, 0.0D, 0, 1)),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PILE_ROD_BORON = control("pile_rod_boron",
            () -> new ItemPileRod(new Item.Properties().stacksTo(1), ItemPileRod.Spec.control(1.0D, 0, 1)),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PILE_ROD_LITHIUM = control("pile_rod_lithium",
            () -> new ItemPileRod(new Item.Properties().stacksTo(1), ItemPileRod.Spec.breeder(30_000)),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PILE_ROD_DETECTOR = control("pile_rod_detector",
            () -> new ItemPileRod(new Item.Properties().stacksTo(1), ItemPileRod.Spec.control(0.25D, 0, 1)),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> HAND_DRILL = control("hand_drill",
            () -> new HandDrillItem(100),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> HAND_DRILL_DESH = control("hand_drill_desh",
            () -> new HandDrillItem(0),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> rod_zirnox_empty = control("rod_zirnox_empty", () -> new Item(new Item.Properties().stacksTo(64)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> rod_zirnox_tritium = control("rod_zirnox_tritium", () -> new Item(new Item.Properties().stacksTo(1)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> rod_zirnox = control("rod_zirnox", () -> new ItemZirnoxRod(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> rod_zirnox_natural_uranium_fuel_depleted = control("rod_zirnox_natural_uranium_fuel_depleted", () -> new Item(new Item.Properties().stacksTo(1)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> rod_zirnox_uranium_fuel_depleted = control("rod_zirnox_uranium_fuel_depleted", () -> new Item(new Item.Properties().stacksTo(1)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> rod_zirnox_thorium_fuel_depleted = control("rod_zirnox_thorium_fuel_depleted", () -> new Item(new Item.Properties().stacksTo(1)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> rod_zirnox_mox_fuel_depleted = control("rod_zirnox_mox_fuel_depleted", () -> new Item(new Item.Properties().stacksTo(1)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> rod_zirnox_plutonium_fuel_depleted = control("rod_zirnox_plutonium_fuel_depleted", () -> new Item(new Item.Properties().stacksTo(1)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> rod_zirnox_u233_fuel_depleted = control("rod_zirnox_u233_fuel_depleted", () -> new Item(new Item.Properties().stacksTo(1)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> rod_zirnox_u235_fuel_depleted = control("rod_zirnox_u235_fuel_depleted", () -> new Item(new Item.Properties().stacksTo(1)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> rod_zirnox_les_fuel_depleted = control("rod_zirnox_les_fuel_depleted", () -> new Item(new Item.Properties().stacksTo(1)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> rod_zirnox_zfb_mox_depleted = control("rod_zirnox_zfb_mox_depleted", () -> new Item(new Item.Properties().stacksTo(1)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> rod_empty = control("rod_empty", () -> new Item(new Item.Properties()), HBMKey.GEN_STANDALONE);
    public static final RegistryObject<Item> rod_dual_empty = control("rod_dual_empty", () -> new Item(new Item.Properties()), HBMKey.GEN_STANDALONE);
    public static final RegistryObject<Item> rod_quad_empty = control("rod_quad_empty", () -> new Item(new Item.Properties()), HBMKey.GEN_STANDALONE);
    public static final RegistryObject<Item> rod_breeder_single = control("rod_breeder_single", () -> new ItemBreedingRod(new Item.Properties(), ItemBreedingRod.RodForm.SINGLE), HBMKey.GEN_STANDALONE);
    public static final RegistryObject<Item> rod_breeder_dual = control("rod_breeder_dual", () -> new ItemBreedingRod(new Item.Properties(), ItemBreedingRod.RodForm.DUAL), HBMKey.GEN_STANDALONE);
    public static final RegistryObject<Item> rod_breeder_quad = control("rod_breeder_quad", () -> new ItemBreedingRod(new Item.Properties(), ItemBreedingRod.RodForm.QUAD), HBMKey.GEN_STANDALONE);

    // 临时增补
    public static final RegistryObject<Item> CELL_DEUTERIUM = parts("cell_deuterium", ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> CELL_TRITIUM = parts("cell_tritium", ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> CELL_EMPTY = parts("cell_empty", ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN);
    /**
     * 控制类物品
     * */
    public static final RegistryObject<Item> GEIGER_COUNTER = control("geiger_counter_hand", ()->new ItemGeigerCounter(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> DEBUG_WAND = control("debug_wand", ()->new ItemDebugWand(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> METEOR_REMOTE = control("meteor_remote", ()->new ItemMeteorRemote(new Item.Properties().durability(2)), "Meteorite Remote");
    public static final RegistryObject<Item> BUILD_WAND = control("wand", () -> new ItemBuildWand(new Item.Properties().stacksTo(1)), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> RBMK_TOOL = control("rbmk_tool", () -> new ItemRBMKTool(new Item.Properties()), HBMKey.GEN_STANDALONE);
    public static final RegistryObject<Item> DOSIMETER = control("dosimeter", () -> new ItemDosimeter(new Item.Properties()), HBMKey.GEN_STANDALONE);
    public static final RegistryObject<Item> DIGAMMA_DIAGNOSTIC = control("digamma_diagnostic", () -> new ItemDigammaDiagnostic(new Item.Properties()), HBMKey.GEN_STANDALONE);
    public static final RegistryObject<Item> POLLUTION_DETECTOR = control("pollution_detector", () -> new PollutionDetectorItem(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> ORE_CANNER = control("ore_density_scanner", () -> new OreScannerItem(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> DESIGNATOR = control("designator",()->new ItemDesignator(new Item.Properties().stacksTo(1)), HBMKey.ORDERLY_GEN);
    // 从旧HBMComponet中迁移来的
    public static final RegistryObject<Item> TRITIUM_DEUTERIUM_CAKE = parts("tritium_deuterium_cake", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> PISTON_SELENIUM = parts("piston_selenium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> PISTON_SET = parts("piston_set", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);

    public static final RegistryObject<Item> RUNE_BLANK = parts("rune_blank", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> RUNE_ISA = parts("rune_isa", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> RUNE_DAGAZ = parts("rune_dagaz", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> RUNE_HAGALAZ = parts("rune_hagalaz", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> RUNE_JERA = parts("rune_jera", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> RUNE_THURISAZ = parts("rune_thurisaz", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);

    public static final RegistryObject<Item> AMS_CATALYST_BLANK = parts("ams_catalyst_blank", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> AMS_CATALYST_ALUMINIUM = parts("ams_catalyst_aluminium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> AMS_CATALYST_BERYLLIUM = parts("ams_catalyst_beryllium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> AMS_CATALYST_CAESIUM = parts("ams_catalyst_caesium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> AMS_CATALYST_CERIUM = parts("ams_catalyst_cerium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> AMS_CATALYST_COBALT = parts("ams_catalyst_cobalt", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> AMS_CATALYST_COPPER = parts("ams_catalyst_copper", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> AMS_CATALYST_DINEUTRONIUM = parts("ams_catalyst_dineutronium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> AMS_CATALYST_EUPHEMIUM = parts("ams_catalyst_euphemium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> AMS_CATALYST_IRON = parts("ams_catalyst_iron", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> AMS_CATALYST_LITHIUM = parts("ams_catalyst_lithium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> AMS_CATALYST_NIOBIUM = parts("ams_catalyst_niobium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> AMS_CATALYST_SCHRABIDIUM = parts("ams_catalyst_schrabidium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> AMS_CATALYST_STRONTIUM = parts("ams_catalyst_strontium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> AMS_CATALYST_THORIUM = parts("ams_catalyst_thorium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> AMS_CATALYST_TUNGSTEN = parts("ams_catalyst_tungsten", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);

    public static final RegistryObject<Item> CELL_UF6 = parts("cell_uf6", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> CELL_PUF6 = parts("cell_puf6", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> CELL_ANTIMATTER = parts("cell_antimatter", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> CELL_SAS3 = parts("cell_sas3", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> CELL_ANTI_SCHRABIDIUM = parts("cell_anti_schrabidium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> CELL_BALEFIRE = parts("cell_balefire", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);

    public static final RegistryObject<Item> DEMON_CORE_OPEN = parts("demon_core_open", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> DEMON_CORE_CLOSED = parts("demon_core_closed", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);

//            public static final RegistryObject<Item> PA_COIL = parts("pa_coil", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);

    public static final RegistryObject<Item> PARTICLE_EMPTY = parts("particle_empty", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> PARTICLE_HYDROGEN = parts("particle_hydrogen", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> PARTICLE_COPPER = parts("particle_copper", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> PARTICLE_LEAD = parts("particle_lead", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> PARTICLE_APROTON = parts("particle_aproton", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> PARTICLE_AELECTRON = parts("particle_aelectron", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> PARTICLE_AMAT = parts("particle_amat", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> PARTICLE_ASCHRAB = parts("particle_aschrab", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> PARTICLE_HIGGS = parts("particle_higgs", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> PARTICLE_MUON = parts("particle_muon", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> PARTICLE_TACHYON = parts("particle_tachyon", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> PARTICLE_STRANGE = parts("particle_strange", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> PARTICLE_DARK = parts("particle_dark", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> PARTICLE_SPARKTICLE = parts("particle_sparkticle", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> PARTICLE_DIGAMMA = parts("particle_digamma", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> PARTICLE_LUTECE = parts("particle_lutece", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);

    public static final RegistryObject<Item> CASING_SMALL = parts("casing.small", ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> CASING_LARGE = parts("casing.large", ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> CASING_SMALL_STEEL = parts("casing.small_steel", ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> CASING_LARGE_STEEL = parts("casing.large_steel", ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> CASING_SHOTSHELL = parts("casing.shotshell", ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> CASING_BUCKSHOT = parts("casing.buckshot", ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> CASING_BUCKSHOT_ADVANCED = parts("casing.buckshot_advanced", ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN);
    public static final Map<String, RegistryObject<Item>> WIRE_FINE = Arrays.stream(WIRE_MAT).collect(Collectors.toMap(key -> key, value -> parts("wire_"+value, ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN, ModTags.Items.WIRE)));
    public static final Map<String, Supplier<Item>> MAP_INGOT = Map.of(
            HBMKey.ALUMINIUM,INGOT_ALUMINIUM, HBMKey.COPPER, () -> Items.COPPER_INGOT, HBMKey.RED_COPPER, INGOT_RED_COPPER, HBMKey.GOLD, () -> Items.GOLD_INGOT, HBMKey.TUNGSTEN, INGOT_TUNGSTEN,
            HBMKey.ADVANCED_ALLOY, INGOT_ADVANCED_ALLOY, HBMKey.SCHRABIDIUM, INGOT_SCHRABIDIUM, HBMKey.ZINC,INGOT_ZIRCONIUM, HBMKey.MAGNETIZED_TUNGSTEN, INGOT_MAGNETIZED_TUNGSTEN
    );

    public static final RegistryObject<Item> MOLD_NUGGET = parts("mold_nugget", () -> new ItemMold(new Item.Properties(), HBMMatForm.NUGGET), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> MOLD_BILLET = parts("mold_billet", () -> new ItemMold(new Item.Properties(), HBMMatForm.BILLET), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> MOLD_INGOT = parts("mold_ingot", () -> new ItemMold(new Item.Properties(), HBMMatForm.INGOT), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> MOLD_PLATE = parts("mold_plate", () -> new ItemMold(new Item.Properties(), HBMMatForm.PLATE), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> MOLD_WIRE = parts("mold_wire", () -> new ItemMold(new Item.Properties(), HBMMatForm.WIRE), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> MOLD_PLATE_CAST = parts("mold_plate_cast", () -> new ItemMold(new Item.Properties(), HBMMatForm.CASTPLATE), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> MOLD_WIRE_DENSE = parts("mold_wire_dense", () -> new ItemMold(new Item.Properties(), HBMMatForm.DENSEWIRE), HBMKey.REVERSE_GEN);

    public static final RegistryObject<Item> MOLD_SHELL = parts("mold_shell", () -> new ItemMold(new Item.Properties(), HBMMatForm.SHELL), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> MOLD_PIPE = parts("mold_pipe", () -> new ItemMold(new Item.Properties(), HBMMatForm.PIPE), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> MOLD_INGOTS = parts("mold_ingots", () -> new ItemMold(new Item.Properties(), HBMMatForm.INGOT, 9), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> MOLD_PLATES = parts("mold_plates", () -> new ItemMold(new Item.Properties(), HBMMatForm.PLATE, 9), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> MOLD_WIRES_DENSE = parts("mold_wires_dense", () -> new ItemMold(new Item.Properties(), HBMMatForm.DENSEWIRE, 9), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> MOLD_BLOCK = parts("mold_block", () -> new ItemMold(new Item.Properties(), HBMMatForm.BLOCK), HBMKey.REVERSE_GEN);
    // 填充物品，游戏内无法获得，用于避免物品被匹配上
    public static final RegistryObject<Item> DUMMY_ITEM = ITEMS.register("dummy_item", ()->new Item(new Item.Properties()));
    /**
     *
     *
     * 以下为功能函数
     * */
    public static synchronized void register(IEventBus eventBus){
        if (registeredToBus) {
            return;
        }
        registeredToBus = true;
        // Force block registration classes to populate their BlockItems first so
        // legacy placeholders only backfill truly missing ids.
        ModBlocks.BLOCKS.getEntries();
        try {
            Class.forName(HBMComponent.class.getName());
        } catch (ClassNotFoundException ignored) {
        }
        LegacyItems.registerLegacy();
        ITEMS.register(eventBus);
    }
    public static RegistryObject<Item> machine(final String name, final Supplier<? extends Item> sup){
        return machine(name, sup, HBMKey.REVERSE_GEN);
    }
    public static RegistryObject<Item> machine(final String name, final Supplier<? extends Item> sup, String genNameWay){
        return add(name, sup, ModTabs.MACHINE.getKey(), genNameWay);
    }

    public static RegistryObject<Item> missile(final String name, final Supplier<? extends Item> sup, String genNameWay){
        return add(name, sup, ModTabs.MISSILE.getKey(), genNameWay);
    }
    public static RegistryObject<Item> gun(final String name, final Supplier<? extends Item> sup, String genNameWay){
        return add(name, sup, ModTabs.WEAPON.getKey(), genNameWay);
    }
    public static RegistryObject<Item> legacyGrenade(final String name, final ItemGrenade.Type type){
        return gun(name, () -> new ItemGrenade(new Item.Properties(), type), HBMKey.ORDERLY_GEN);
    }
    public static RegistryObject<Item> template(final String name, final Supplier<? extends Item> sup, String genNameWay){
        return add(name, sup, ModTabs.TEMPLATE.getKey(), genNameWay);
    }

    public static RegistryObject<Item> consumable(final String name, final Supplier<? extends Item> sup, String genNameWay){
        return add(name, sup, ModTabs.CONSUMABLE.getKey(), genNameWay);
    }
    public static RegistryObject<Item> parts(final String name, final Supplier<? extends Item> sup, String genNameWay){
        return add(name, sup, ModTabs.PARTS.getKey(), genNameWay);
    }
    // 待改
    public static RegistryObject<Item> parts(final String name, final Supplier<? extends Item> sup, String genNameWay, TagKey<Item> tag){
        return add(name, sup, ModTabs.PARTS.getKey(), HBMKey.BASIC_MODEL, genNameWay, tag);
    }
    public static RegistryObject<Item> control(final String name, final Supplier<? extends Item> sup, String genNameWay){
        return add(name, sup, ModTabs.CONTROL.getKey(), genNameWay);
    }

    public static RegistryObject<Item> add(final String name, final Supplier<? extends Item> sup, ResourceKey<CreativeModeTab> tabKey, String genNameWay){
        return add(name, sup, tabKey, HBMKey.BASIC_MODEL, genNameWay);
    }
    public static RegistryObject<Item> add(final String name, final Supplier<? extends Item> sup, ResourceKey<CreativeModeTab> tabKey, String genModelWay, String genNameWay){
        return new WrapperRegistry.ItemBuilder(name, sup).tab(tabKey).model(genModelWay).loc(genNameWay).build();
    }
    public static RegistryObject<Item> add(final String name, final Supplier<? extends Item> sup, ResourceKey<CreativeModeTab> tabKey, String genModelWay, String genNameWay, TagKey<Item> tag){
        return new WrapperRegistry.ItemBuilder(name, sup).tab(tabKey).tags(tag).model(genModelWay).loc(genNameWay).build();
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
    public static void tagSupport(ItemTagsGen provider){
        for (WrappedItemRegistry itemRegistry : itemList) {
            itemRegistry.tagSupport(provider);
        }
    }

}
