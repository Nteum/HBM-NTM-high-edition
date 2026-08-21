package com.hbm.registries;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.Inventory.material.HBMMatForm;
import com.hbm.Inventory.material.HBMMatter;
import com.hbm.block.interfaces.ToolType;
import com.hbm.compat.legacy.LegacyItems;
import com.hbm.config.ConfigLBSM;
import com.hbm.core.contents.fluid.HBMFluids;
import com.hbm.core.item.ItemBattery;
import com.hbm.core.item.ItemBatteryCreative;
import com.hbm.datagen.LanguageProvider;
import com.hbm.datagen.model.ItemModelGen;
import com.hbm.datagen.tag.ItemTagsGen;
import com.hbm.item.HBMCombat;
import com.hbm.item.HBMWeapon;
import com.hbm.item.ItemEnums;
import com.hbm.item.armor.ItemAshGlass;
import com.hbm.item.consumable.LegacyConsumableItem;
import com.hbm.item.consumable.TemFlakesItem;
import com.hbm.item.env.*;
import com.hbm.item.icf.ItemICFPellet;
import com.hbm.item.machine.ItemBatterySC;
import com.hbm.item.machine.ItemMachineUpgrade;
import com.hbm.item.machine.ItemMachineUpgrade.UpgradeType;
import com.hbm.item.misc.*;
import com.hbm.item.misc.ItemCustomLore;
import com.hbm.item.pwr.ItemPWRFuel;
import com.hbm.item.rbmk.ItemRBMKControlRod;
import com.hbm.item.rbmk.ItemRBMKFuelRod;
import com.hbm.item.rbmk.ItemRBMKLid;
import com.hbm.item.research.ItemBreedingRod;
import com.hbm.item.research.ItemPileRod;
import com.hbm.item.research.ItemResearchFuelPlate;
import com.hbm.item.special.ItemPotatos;
import com.hbm.item.tool.*;
import com.hbm.item.weapon.*;
import com.hbm.item.weapon.grenade.ItemGrenade;
import com.hbm.item.zirnox.ItemZirnoxRod;
import com.hbm.reactor.rbmk.RBMKLidType;
import com.hbm.registries.WrappedRegistryBuilder.*;
import com.hbm.render.hud.HUDBedrockOreScanner;
import com.hbm.render.model.Models;
import com.hbm.entity.weapon.missile.EntityMissileTier0;
import com.hbm.debug.GunSuicide;
import com.hbm.debug.ItemDebugWand;
import com.hbm.utils.data.NBTHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HBM.MODID);
    public static final List<WrappedItemRegistryBuilder> itemList = new ArrayList<>();
    private static boolean registeredToBus = false;

    public static final String[] WIRE_MAT = new String[]{HBMKey.ALUMINIUM, HBMKey.COPPER, HBMKey.RED_COPPER, HBMKey.GOLD, HBMKey.TUNGSTEN, HBMKey.ADVANCED_ALLOY, HBMKey.SCHRABIDIUM, HBMKey.ZINC, HBMKey.MAGNETIZED_TUNGSTEN};

    static {
        HBMFluids.registerItem(ITEMS);
        HBMWeapon.register(ITEMS);
        HBMCombat.register(ITEMS);
    }

    public static RegistryObject<Item> REDSTONE_SWORD = new WrappedItemRegistryBuilder("redstone_sword", ()->new RedstoneSword(Tiers.STONE, 3, -2.4F, new Item.Properties())).tab(CreativeModeTabs.COMBAT).build();
    public static final RegistryObject<Item> BIG_SWORD = new WrappedItemRegistryBuilder("big_sword", ()->new BigSword(Tiers.GOLD, 3, -2.4F, new Item.Properties())).tab(CreativeModeTabs.COMBAT).build();
    public static RegistryObject<Item> BUTTER_SWORD;


        public static final RegistryObject<Item> INGOT_TH232 = new WrappedItemRegistryBuilder("ingot_th232", ()->new Item(new Item.Properties())).tab(ModTabs.PARTS.getKey()).loc(HBMKey.ORDERLY_GEN_EXCEPT_FIRST).tags(HBMMatters.THORIUM.ingot()).build();
        public static final RegistryObject<Item> INGOT_URANIUM = parts("ingot_uranium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.URANIUM.ingot());
        public static final RegistryObject<Item> INGOT_U233 = parts("ingot_u233", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.U233.ingot());
        public static final RegistryObject<Item> INGOT_U235 = parts("ingot_u235", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.U235.ingot());
        public static final RegistryObject<Item> INGOT_U238 = parts("ingot_u238", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.U238.ingot());
        public static final RegistryObject<Item> INGOT_U238M2 = parts("ingot_u238m2", ()->new ItemUnstable(350, 200, new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.U238.ingot());
        public static final RegistryObject<Item> INGOT_PLUTONIUM = parts("ingot_plutonium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.PLUTONIUM.ingot());
        public static final RegistryObject<Item> INGOT_PU238 = parts("ingot_pu238", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.PU238.ingot());
        public static final RegistryObject<Item> INGOT_PU239 = parts("ingot_pu239", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.PU239.ingot());
        public static final RegistryObject<Item> INGOT_PU240 = parts("ingot_pu240", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.PU240.ingot());
        public static final RegistryObject<Item> INGOT_PU241 = parts("ingot_pu241", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.PU241.ingot());
        public static final RegistryObject<Item> INGOT_PU_MIX = parts("ingot_pu_mix", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.PLUTONIUM.ingot());
        public static final RegistryObject<Item> INGOT_AM241 = parts("ingot_am241", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.AM241.ingot());
        public static final RegistryObject<Item> INGOT_AM242 = parts("ingot_am242", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.AM242.ingot());
        public static final RegistryObject<Item> INGOT_AM_MIX = parts("ingot_am_mix", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, Tags.Items.INGOTS);
    public static RegistryObject<Item> INGOT_CM242;
    public static RegistryObject<Item> INGOT_CM243;
    public static RegistryObject<Item> INGOT_CM244;
    public static RegistryObject<Item> INGOT_CM245;
    public static RegistryObject<Item> INGOT_CM246;
    public static RegistryObject<Item> INGOT_CM247;
    public static RegistryObject<Item> INGOT_CM_FUEL;

    public static RegistryObject<Item> INGOT_CM_MIX;

    public static RegistryObject<Item> INGOT_BK247;

    public static RegistryObject<Item> INGOT_CF251;

    public static RegistryObject<Item> INGOT_ES253;
    public static RegistryObject<Item> INGOT_ES255;

        public static final RegistryObject<Item> INGOT_NEPTUNIUM = parts("ingot_neptunium", ()->new Item(new Item.Properties()),HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.NEPTUNIUM.ingot());
    public static final RegistryObject<Item> INGOT_POLONIUM = parts("ingot_polonium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.POLONIUM.ingot());
    public static final RegistryObject<Item> INGOT_TECHNETIUM = parts("ingot_technetium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.TECHNETIUM.ingot());
    public static final RegistryObject<Item> INGOT_CO60 = parts("ingot_co60", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.CO60.ingot());
    public static final RegistryObject<Item> INGOT_SR90 = parts("ingot_sr90", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, Tags.Items.INGOTS);
    public static final RegistryObject<Item> INGOT_AU198 = parts("ingot_au198", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.AU198.ingot());
    public static final RegistryObject<Item> INGOT_PB209 = parts("ingot_pb209", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.PB209.ingot());
    public static final RegistryObject<Item> INGOT_RA226 = parts("ingot_ra226", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.RADIUM.ingot());
    public static final RegistryObject<Item> INGOT_TITANIUM = parts("ingot_titanium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.TITANIUM.ingot());
    public static RegistryObject<Item> INGOT_NICKEL;
    public static final RegistryObject<Item> INGOT_COBALT = parts("ingot_cobalt", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.COBALT.ingot());
    public static final RegistryObject<Item> INGOT_GALLIUM = parts("ingot_gallium", ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN, HBMMatters.GALLIUM.ingot());
    public static RegistryObject<Item> INGOT_ZINC;
    public static RegistryObject<Item> INGOT_GAAS;
    public static RegistryObject<Item> INGOT_IRIDIUM;
    public static final RegistryObject<Item> SULFUR = parts("sulfur", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static RegistryObject<Item> INGOT_PALLADIUM;

    public static final RegistryObject<Item> NITRA = parts("nitra", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NITRA_SMALL = parts("nitra_small", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

    public static RegistryObject<Item> COKE;
    public static RegistryObject<Item> POWDER_COKE;
    public static final RegistryObject<Item> LIGNITE = parts("lignite", ()->new ItemFuel(new Item.Properties(), 1200), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_LIGNITE = parts("powder_lignite", ()->new ItemFuel(new Item.Properties(), 1200), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, ModTags.Items.DUST_LIGNITE);
    public static RegistryObject<Item> BRIQUETTE;
    public static final RegistryObject<Item> COAL_INFERNAL = parts("coal_infernal", ()->new ItemFuel(new Item.Properties(), 4800), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static RegistryObject<Item> COAL_ETERNAL;
    public static final RegistryObject<Item> CINNEBAR = parts("cinnebar", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

    public static RegistryObject<Item> WOODEMIUM_BRIQUETTE;

    public static RegistryObject<Item> BUTTER; //////////////////////////////////////////////
    public static RegistryObject<Item> S_CREAM;
    public static RegistryObject<Item> MIN_CREAM;
    public static RegistryObject<Item> POWDER_ASH;
    public static RegistryObject<Item> POWDER_LIMESTONE;
    public static RegistryObject<Item> POWDER_CEMENT;

    public static final RegistryObject<Item> NITER = parts("salpeter", ()->new Item(new Item.Properties()), "Niter");
    public static final RegistryObject<Item> INGOT_COPPER = parts("ingot_copper", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.COPPER.ingot());
    public static final RegistryObject<Item> INGOT_RED_COPPER = parts("ingot_red_copper", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, Tags.Items.INGOTS);
    public static final RegistryObject<Item> INGOT_TUNGSTEN = parts("ingot_tungsten", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.TUNGSTEN.ingot());
    public static RegistryObject<Item> INGOT_TUNGSTEN_CARBIDE;
    public static final RegistryObject<Item> INGOT_ALUMINIUM = parts("ingot_aluminium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.ALUMINIUM.ingot());
    public static final RegistryObject<Item> FLUORITE = parts("fluorite", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_BERYLLIUM = parts("ingot_beryllium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.BERYLLIUM.ingot());
    public static final RegistryObject<Item> INGOT_SCHRARANIUM = new WrappedItemRegistryBuilder("ingot_schraranium", ()->new Item(new Item.Properties()){
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
    }).tab(ModTabs.PARTS.getKey()).loc(HBMKey.REVERSE_GEN).tags(HBMMatters.SCHRARANIUM.ingot())
            .withConfigTexture("condition_state", () -> ConfigLBSM.enableLBSM && ConfigLBSM.enableLBSMFullSchrab).model(itemModelGen -> {
                // 我的评价是没办法，需要的参数太多了，一个一个传非常麻烦，只能手动了。
                ResourceLocation rl = HBM.rl("ingot_schraranium");
                itemModelGen.getBuilder(rl.toString()).parent(new ModelFile.UncheckedModelFile("item/generated"))
                        .texture("layer0", ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), "item/" + rl.getPath()))
                        .override().predicate(ResourceLocation.parse("condition_state"), 1.0f).model(itemModelGen.basicItem(HBM.rl("ingot_nikonium"))).end();
            }).build();
    public static final RegistryObject<Item> INGOT_SCHRABIDIUM = parts("ingot_schrabidium", ()->new Item(new Item.Properties().rarity(Rarity.RARE)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.SCHRABIDATE.ingot());
    public static final RegistryObject<Item> INGOT_SCHRABIDATE = parts("ingot_schrabidate", ()->new Item(new Item.Properties().rarity(Rarity.RARE)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.SCHRABIDATE.ingot());
    public static final RegistryObject<Item> INGOT_PLUTONIUM_FUEL = parts("ingot_plutonium_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, Tags.Items.INGOTS);
    public static final RegistryObject<Item> INGOT_NEPTUNIUM_FUEL = parts("ingot_neptunium_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, Tags.Items.INGOTS);
    public static final RegistryObject<Item> INGOT_URANIUM_FUEL = parts("ingot_uranium_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, Tags.Items.INGOTS);
    public static final RegistryObject<Item> INGOT_MOX_FUEL = parts("ingot_mox_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, Tags.Items.INGOTS);
    public static final RegistryObject<Item> INGOT_AMERICIUM_FUEL = parts("ingot_americium_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, Tags.Items.INGOTS);
    public static final RegistryObject<Item> INGOT_SCHRABIDIUM_FUEL = parts("ingot_schrabidium_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, Tags.Items.INGOTS);
    public static final RegistryObject<Item> INGOT_THORIUM_FUEL = parts("ingot_thorium_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, Tags.Items.INGOTS);
    public static final RegistryObject<Item> NUGGET_URANIUM_FUEL = parts("nugget_uranium_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, Tags.Items.NUGGETS);
    public static final RegistryObject<Item> NUGGET_THORIUM_FUEL = parts("nugget_thorium_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, Tags.Items.NUGGETS);
    public static final RegistryObject<Item> NUGGET_PLUTONIUM_FUEL = parts("nugget_plutonium_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, Tags.Items.NUGGETS);
    public static final RegistryObject<Item> NUGGET_NEPTUNIUM_FUEL = parts("nugget_neptunium_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, Tags.Items.NUGGETS);
    public static final RegistryObject<Item> NUGGET_MOX_FUEL = parts("nugget_mox_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, Tags.Items.NUGGETS);
    public static final RegistryObject<Item> NUGGET_AMERICIUM_FUEL = parts("nugget_americium_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, Tags.Items.NUGGETS);
    public static final RegistryObject<Item> NUGGET_SCHRABIDIUM_FUEL = parts("nugget_schrabidium_fuel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, Tags.Items.NUGGETS);
    public static final RegistryObject<Item> INGOT_TCALLOY = parts("ingot_tcalloy", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.TCALLOY.ingot());
    public static final RegistryObject<Item> INGOT_CDALLOY = parts("ingot_cdalloy", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.CDALLOY.ingot());
    public static final RegistryObject<Item> INGOT_BISMUTH_BRONZE = parts("ingot_bismuth_bronze", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.BISMUTH.ingot());
    public static final RegistryObject<Item> INGOT_ARSENIC_BRONZE = parts("ingot_arsenic_bronze", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.ARSENIC.ingot());
    public static final RegistryObject<Item> INGOT_BSCCO = parts("ingot_bscco", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.BSCCO.ingot());
    public static final RegistryObject<Item> LITHIUM = parts("lithium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_ZIRCONIUM = parts("ingot_zirconium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.ZIRCONIUM.ingot());
    public static final RegistryObject<Item> INGOT_HES = parts("ingot_hes", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_LES = parts("ingot_les", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_HES = parts("nugget_hes", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, Tags.Items.NUGGETS);
    public static final RegistryObject<Item> NUGGET_LES = parts("nugget_les", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, Tags.Items.NUGGETS);
    public static final RegistryObject<Item> INGOT_MAGNETIZED_TUNGSTEN = parts("ingot_magnetized_tungsten", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.MAGTUNG.ingot());
    public static final RegistryObject<Item> INGOT_COMBINE_STEEL = parts("ingot_combine_steel", ()->new ItemCustomInfo(new Item.Properties(), HBMLang.ITEM_INGOTCOMBINE_STEEL_DESC.translate()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.CMB.ingot());
    public static final RegistryObject<Item> INGOT_SOLINIUM = parts("ingot_solinium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.SOLINIUM.ingot());
    public static final RegistryObject<Item> NUGGET_SOLINIUM = parts("nugget_solinium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.SOLINIUM.nugget());
    public static final RegistryObject<Item> INGOT_PHOSPHORUS = parts("ingot_phosphorus", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.PHOSPHORUS.ingot());
    public static final RegistryObject<Item> INGOT_SEMTEX = new WrappedItemRegistryBuilder("ingot_semtex", ()->new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationMod(5).build())))
            .tab(ModTabs.PARTS.getKey()).loc("Bar of Semtex", "Semtex H Plastic Explosive$Performant explosive for many applications.$Edible").build();
    public static final RegistryObject<Item> INGOT_C4 = parts("ingot_c4", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, Tags.Items.INGOTS);
    public static final RegistryObject<Item> INGOT_BORON = parts("ingot_boron", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.BORON.ingot());
    public static final RegistryObject<Item> INGOT_GRAPHITE = parts("ingot_graphite", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.GRAPHITE.ingot());
    public static final RegistryObject<Item> INGOT_FIREBRICK = parts("ingot_firebrick", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, Tags.Items.INGOTS);
    public static final RegistryObject<Item> INGOT_SMORE = parts("ingot_smore", ()->new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationMod(20f).build())), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, Tags.Items.INGOTS);

    public static final RegistryObject<Item> INGOT_GH336 = new WrappedItemRegistryBuilder("ingot_gh336", ()->new Item(new Item.Properties())).tab(ModTabs.PARTS.getKey())
        .loc(HBMKey.ORDERLY_GEN_EXCEPT_FIRST, "Seaborgium's colleague.").tags(HBMMatters.GHIORSIUM.nugget()).build();
    public static final RegistryObject<Item> NUGGET_GH336 = new WrappedItemRegistryBuilder("nugget_gh336", ()->new Item(new Item.Properties().rarity(Rarity.EPIC)))
        .tab(ModTabs.PARTS.getKey()).loc(HBMKey.REVERSE_GEN, "Seaborgium's colleague.").tags(HBMMatters.GHIORSIUM.nugget()).build();

    public static RegistryObject<Item> INGOT_CN989;

    public static final RegistryObject<Item> INGOT_AUSTRALIUM = parts("ingot_australium", ()->new ItemCustomInfo(new Item.Properties().rarity(Rarity.UNCOMMON)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.AUSTRALIUM.ingot());
    public static RegistryObject<Item> INGOT_AUSTRALIUM_LESSER;
    public static RegistryObject<Item> INGOT_AUSTRALIUM_GREATER;
    public static final RegistryObject<Item> NUGGET_AUSTRALIUM = parts("nugget_australium", ()->new ItemCustomInfo(new Item.Properties().rarity(Rarity.UNCOMMON)), HBMKey.REVERSE_GEN, HBMMatters.AUSTRALIUM.nugget());
    public static final RegistryObject<Item> NUGGET_AUSTRALIUM_LESSER = parts("nugget_australium_lesser", ()->new ItemCustomInfo(new Item.Properties().rarity(Rarity.UNCOMMON)), HBMKey.REVERSE_GEN, HBMMatters.AUSTRALIUM.nugget());
    public static final RegistryObject<Item> NUGGET_AUSTRALIUM_GREATER = parts("nugget_australium_greater", ()->new ItemCustomInfo(new Item.Properties().rarity(Rarity.UNCOMMON)), HBMKey.REVERSE_GEN, HBMMatters.AUSTRALIUM.nugget());

    public static final RegistryObject<Item> INGOT_DESH = parts("ingot_desh", ()->new ItemCustomInfo(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.DESH.ingot());
    public static final RegistryObject<Item> NUGGET_DESH = parts("nugget_desh", ()->new ItemCustomLore(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.DESH.nugget());
    public static final RegistryObject<Item> INGOT_DINEUTRONIUM = parts("ingot_dineutronium", ()->new ItemCustomInfo(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, Tags.Items.INGOTS);
    public static final RegistryObject<Item> NUGGET_DINEUTRONIUM = parts("nugget_dineutronium", ()->new ItemCustomLore(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, Tags.Items.NUGGETS);
    public static final RegistryObject<Item> POWDER_DINEUTRONIUM = parts("powder_dineutronium", ()->new ItemStarmetal(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, Tags.Items.DUSTS);
    public static RegistryObject<Item> INGOT_TETRANEUTRONIUM;
    public static RegistryObject<Item> NUGGET_TETRANEUTRONIUM;
    public static RegistryObject<Item> POWDER_TETRANEUTRONIUM;
    public static final RegistryObject<Item> INGOT_STARMETAL = parts("ingot_starmetal", ()->new ItemStarmetal(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.STAR.ingot());
    public static final RegistryObject<Item> INGOT_GUNMETAL = parts("ingot_gunmetal", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.GUNMETAL.ingot());
    public static final RegistryObject<Item> PLATE_GUNMETAL = parts("plate_gunmetal", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_WEAPONSTEEL = parts("ingot_weaponsteel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.WEAPONSTEEL.ingot());
    public static final RegistryObject<Item> PLATE_WEAPONSTEEL = parts("plate_weaponsteel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_SATURNITE = parts("ingot_saturnite", ()->new Item(new Item.Properties().rarity(Rarity.RARE)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.SATURN.ingot());
    public static final RegistryObject<Item> PLATE_SATURNITE = parts("plate_saturnite", ()->new Item(new Item.Properties().rarity(Rarity.RARE)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.SATURN.plate());
    public static final RegistryObject<Item> INGOT_FERROURANIUM = parts("ingot_ferrouranium", ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN, HBMMatters.FERRO.ingot());
    public static final RegistryObject<Item> INGOT_ELECTRONIUM = parts("ingot_electronium", ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN, Tags.Items.INGOTS);
    public static RegistryObject<Item> INGOT_GWENIUM;
    public static final RegistryObject<Item> NUGGET_ZIRCONIUM = add("nugget_zirconium", ()->new Item(new Item.Properties()), ModTabs.PARTS.getKey(), "Zirconium Splinter", HBMMatters.ZIRCONIUM.nugget());
    public static RegistryObject<Item> NUGGET_NICKEL;
    public static RegistryObject<Item> NUGGET_ZINC;
    public static final RegistryObject<Item> NUGGET_GALLIUM = parts("nugget_gallium", ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN, HBMMatters.GALLIUM.nugget());
    public static RegistryObject<Item> NUGGET_GAAS;
    public static final RegistryObject<Item> NUGGET_MERCURY = parts("nugget_mercury_tiny", ()->new Item(new Item.Properties()), "Tiny Drop of Mercury", Tags.Items.NUGGETS);
    public static final RegistryObject<Item> INGOT_MERCURY = parts("nugget_mercury", ()->new Item(new Item.Properties()), "Drop of Mercury");
    public static RegistryObject<Item> INGOT_MENTHOL; //this is correct but i want to call it menthol_crystals so bad
    public static RegistryObject<Item> NUGGET_MENTHOL;
    public static final RegistryObject<Item> BOTTLE_MERCURY = parts("bottle_mercury", ()->new Item(new Item.Properties()), "Drop of Mercury");

    public static RegistryObject<Item> INGOT_TT;
    public static RegistryObject<Item> INGOT_TTAS;

    @Deprecated public static RegistryObject<Item> ORE_BYPRODUCT;

    @Deprecated public static RegistryObject<Item> ORE_BEDROCK;
    public static final RegistryObject<Item> ORE_CENTRIFUGED = new WrappedItemRegistryBuilder("ore_centrifuged", ()->new ItemBedrockOre(new Item.Properties()))
            .tab(ModTabs.PARTS.getKey()).loc(HBMKey.REVERSE_GEN).model(HBMKey.MODEL_ITEM_OVERLAY, HBM.rl("ore_overlay"))
            .color((ItemStack stack, int tintIndex) -> tintIndex == 1 ? ItemBedrockOre.getColor(stack) : -1).build();
    public static final RegistryObject<Item> ORE_CLEANED = new WrappedItemRegistryBuilder("ore_cleaned", ()->new ItemBedrockOre(new Item.Properties()))
            .tab(ModTabs.PARTS.getKey()).loc(HBMKey.REVERSE_GEN).model(HBMKey.MODEL_ITEM_OVERLAY, HBM.rl("ore_overlay"))
            .color((ItemStack stack, int tintIndex) -> tintIndex == 1 ? ItemBedrockOre.getColor(stack) : -1).build();
    public static final RegistryObject<Item> ORE_SEPARATED = new WrappedItemRegistryBuilder("ore_separated", ()->new ItemBedrockOre(new Item.Properties()))
            .tab(ModTabs.PARTS.getKey()).loc(HBMKey.REVERSE_GEN).model(HBMKey.MODEL_ITEM_OVERLAY, HBM.rl("ore_overlay"))
            .color((ItemStack stack, int tintIndex) -> tintIndex == 1 ? ItemBedrockOre.getColor(stack) : -1).build();
    public static final RegistryObject<Item> ORE_PURIFIED = new WrappedItemRegistryBuilder("ore_purified", ()->new ItemBedrockOre(new Item.Properties()))
            .tab(ModTabs.PARTS.getKey()).loc(HBMKey.REVERSE_GEN).model(HBMKey.MODEL_ITEM_OVERLAY, HBM.rl("ore_overlay"))
            .color((ItemStack stack, int tintIndex) -> tintIndex == 1 ? ItemBedrockOre.getColor(stack) : -1).build();
    public static final RegistryObject<Item> ORE_NITRATED = new WrappedItemRegistryBuilder("ore_nitrated", ()->new ItemBedrockOre(new Item.Properties()))
            .tab(ModTabs.PARTS.getKey()).loc(HBMKey.REVERSE_GEN).model(HBMKey.MODEL_ITEM_OVERLAY, HBM.rl("ore_overlay"))
            .color((ItemStack stack, int tintIndex) -> tintIndex == 1 ? ItemBedrockOre.getColor(stack) : -1).build();
    public static final RegistryObject<Item> ORE_NITROCRYSTALLINE = new WrappedItemRegistryBuilder("ore_nitrocrystalline", ()->new ItemBedrockOre(new Item.Properties()))
            .tab(ModTabs.PARTS.getKey()).loc(HBMKey.REVERSE_GEN).model(HBMKey.MODEL_ITEM_OVERLAY, HBM.rl("ore_overlay"))
            .color((ItemStack stack, int tintIndex) -> tintIndex == 1 ? ItemBedrockOre.getColor(stack) : -1).build();
    public static final RegistryObject<Item> ORE_DEEPCLEANED = new WrappedItemRegistryBuilder("ore_deepcleaned", ()->new ItemBedrockOre(new Item.Properties()))
            .tab(ModTabs.PARTS.getKey()).loc(HBMKey.REVERSE_GEN).model(HBMKey.MODEL_ITEM_OVERLAY, HBM.rl("ore_overlay"))
            .color((ItemStack stack, int tintIndex) -> tintIndex == 1 ? ItemBedrockOre.getColor(stack) : -1).build();
    public static final RegistryObject<Item> ORE_SEARED = new WrappedItemRegistryBuilder("ore_seared", ()->new ItemBedrockOre(new Item.Properties()))
            .tab(ModTabs.PARTS.getKey()).loc(HBMKey.REVERSE_GEN).model(HBMKey.MODEL_ITEM_OVERLAY, HBM.rl("ore_overlay"))
            .color((ItemStack stack, int tintIndex) -> tintIndex == 1 ? ItemBedrockOre.getColor(stack) : -1).build();
    public static final RegistryObject<Item> ORE_ENRICHED = new WrappedItemRegistryBuilder("ore_enriched", ()->new ItemBedrockOre(new Item.Properties()))
            .tab(ModTabs.PARTS.getKey()).loc(HBMKey.REVERSE_GEN).model(HBMKey.MODEL_ITEM_OVERLAY, HBM.rl("ore_overlay"))
            .color((ItemStack stack, int tintIndex) -> tintIndex == 1 ? ItemBedrockOre.getColor(stack) : -1).build();

    public static RegistryObject<Item> BEDROCK_ORE_BASE;
    public static final RegistryObject<Item> BEDROCK_ORE = new WrappedItemRegistryBuilder("piece_ore_bedrock", ()->new ItemBedrockOre(new Item.Properties()))
            .tab(ModTabs.PARTS.getKey()).loc(HBMKey.REVERSE_GEN).model(HBMKey.MODEL_ITEM_OVERLAY, HBM.rl("ore_overlay"))
            .color((ItemStack stack, int tintIndex) -> tintIndex == 1 ? ItemBedrockOre.getColor(stack) : -1).build();
    public static RegistryObject<Item> BEDROCK_ORE_FRAGMENT;

    public static RegistryObject<Item> CRYSTAL_MINERAL;
    public static RegistryObject<Item> CRYSTAL_CLEANED;
    public static RegistryObject<Item> MINERAL_DUST;
    public static RegistryObject<Item> MINERAL_FRAGMENT;

    public static final RegistryObject<Item> BILLET_URANIUM = parts("billet_uranium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_U233 = parts("billet_u233", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_U235 = parts("billet_u235", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_U238 = parts("billet_u238", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static RegistryObject<Item> BILLET_UZH;
    public static final RegistryObject<Item> BILLET_TH232 = parts("billet_th232", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_PLUTONIUM = parts("billet_plutonium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_PU238 = parts("billet_pu238", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_PU239 = parts("billet_pu239", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_PU240 = parts("billet_pu240", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_PU241 = parts("billet_pu241", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_PU_MIX = parts("billet_pu_mix", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_AM241 = parts("billet_am241", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_AM242 = parts("billet_am242", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static RegistryObject<Item> BILLET_BK247;
    public static RegistryObject<Item> BILLET_CM242;
    public static RegistryObject<Item> BILLET_CM243;
    public static RegistryObject<Item> BILLET_CM244;
    public static RegistryObject<Item> BILLET_CM245;
    public static RegistryObject<Item> BILLET_CM246;
    public static RegistryObject<Item> BILLET_CM247;
    public static RegistryObject<Item> BILLET_CN989;
    public static RegistryObject<Item> BILLET_CM_FUEL;
    public static RegistryObject<Item> BILLET_CF252;
    public static RegistryObject<Item> BILLET_CF251;
    public static RegistryObject<Item> INGOT_CF252;
    public static RegistryObject<Item> BILLET_CM_MIX;
    public static RegistryObject<Item> BILLET_ES253;
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
    public static final RegistryObject<Item> BILLET_YHARONITE = parts("billet_yharonite", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static RegistryObject<Item> BILLET_BALEFIRE_GOLD;
    public static RegistryObject<Item> BILLET_FLASHLEAD;
    public static final RegistryObject<Item> BILLET_ZFB_BISMUTH = parts("billet_zfb_bismuth", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_ZFB_PU241 = parts("billet_zfb_pu241", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_ZFB_AM_MIX = parts("billet_zfb_am_mix", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_BERYLLIUM = parts("billet_beryllium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_BISMUTH = parts("billet_bismuth", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BILLET_ZIRCONIUM = parts("billet_zirconium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static RegistryObject<Item> BILLET_GAAS;
    public static final RegistryObject<Item> BILLET_NUCLEAR_WASTE = parts("billet_nuclear_waste", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static RegistryObject<Item> BILLET_RED_COPPER;
    public static RegistryObject<Item> BILLET_MENTHOL;
    public static RegistryObject<Item> CHOCOLATE_MINT_BILLET;

    public static final RegistryObject<Item> NUGGET_TH232 = parts("nugget_th232", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.THORIUM.nugget());
    public static final RegistryObject<Item> NUGGET_URANIUM = parts("nugget_uranium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.URANIUM.nugget());
    public static final RegistryObject<Item> NUGGET_U233 = parts("nugget_u233", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.U233.nugget());
    public static final RegistryObject<Item> NUGGET_U235 = parts("nugget_u235", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.U235.nugget());
    public static final RegistryObject<Item> NUGGET_U238 = parts("nugget_u238", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.U238.nugget());
    public static final RegistryObject<Item> NUGGET_PLUTONIUM = parts("nugget_plutonium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.PLUTONIUM.nugget());
    public static final RegistryObject<Item> NUGGET_PU238 = parts("nugget_pu238", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.PU238.nugget());
    public static final RegistryObject<Item> NUGGET_PU239 = parts("nugget_pu239", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.PU239.nugget());
    public static final RegistryObject<Item> NUGGET_PU240 = parts("nugget_pu240", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.PU240.nugget());
    public static final RegistryObject<Item> NUGGET_PU241 = parts("nugget_pu241", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.PU241.nugget());
    public static final RegistryObject<Item> NUGGET_PU_MIX = parts("nugget_pu_mix", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.PLUTONIUM.nugget());
    public static final RegistryObject<Item> NUGGET_AM241 = parts("nugget_am241", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.AM241.nugget());
    public static final RegistryObject<Item> NUGGET_AM242 = parts("nugget_am242", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.AM242.nugget());
    public static RegistryObject<Item> NUGGET_BK247;
    public static RegistryObject<Item> NUGGET_CM242;
    public static RegistryObject<Item> NUGGET_CM243;
    public static RegistryObject<Item> NUGGET_CM244;
    public static RegistryObject<Item> NUGGET_CM245;
    public static RegistryObject<Item> NUGGET_CM246;
    public static RegistryObject<Item> NUGGET_CM247;
    public static RegistryObject<Item> NUGGET_CN989;
    public static RegistryObject<Item> NUGGET_CM_FUEL;
    public static RegistryObject<Item> NUGGET_CF251;
    public static RegistryObject<Item> NUGGET_CF252;
    public static RegistryObject<Item> NUGGET_CM_MIX;
    public static RegistryObject<Item> NUGGET_ES253;
    public static final RegistryObject<Item> NUGGET_AM_MIX = parts("nugget_am_mix", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, Tags.Items.NUGGETS);
    public static final RegistryObject<Item> NUGGET_NEPTUNIUM = parts("nugget_neptunium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.NEPTUNIUM.nugget());
    public static final RegistryObject<Item> NUGGET_POLONIUM = parts("nugget_polonium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.POLONIUM.nugget());
    public static final RegistryObject<Item> NUGGET_TECHNETIUM = parts("nugget_technetium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.TECHNETIUM.nugget());
    public static final RegistryObject<Item> NUGGET_COBALT = parts("nugget_cobalt", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.COBALT.nugget());
    public static final RegistryObject<Item> NUGGET_CO60 = parts("nugget_co60", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.CO60.nugget());
    public static final RegistryObject<Item> NUGGET_SR90 = parts("nugget_sr90", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, Tags.Items.NUGGETS);
    public static final RegistryObject<Item> NUGGET_AU198 = parts("nugget_au198", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.AU198.nugget());
    public static final RegistryObject<Item> NUGGET_PB209 = parts("nugget_pb209", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.PB209.nugget());
    public static final RegistryObject<Item> NUGGET_RA226 = parts("nugget_ra226", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.RADIUM.nugget());
    public static final RegistryObject<Item> NUGGET_ACTINIUM = parts("nugget_actinium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.ACTINIUM.nugget());
    public static final RegistryObject<Item> PLATE_TITANIUM = parts("plate_titanium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.TITANIUM.plate());
    public static RegistryObject<Item> PLATE_NICKEL;
    public static final RegistryObject<Item> PLATE_ALUMINIUM = parts("plate_aluminium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.ALUMINIUM.plate());
    public static final RegistryObject<Item> NEUTRON_REFLECTOR = parts("neutron_reflector", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_STEEL = parts("ingot_steel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.STEEL.ingot());
    public static final RegistryObject<Item> INGOT_STAINLESS = parts("ingot_stainless", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.STAINLESS.ingot());
    public static final RegistryObject<Item> PLATE_STAINLESS = parts("plate_stainless", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.STAINLESS.plate());
    public static final RegistryObject<Item> PLATE_STEEL = parts("plate_steel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, ModTags.Items.PLATE_STEEL);
    public static final RegistryObject<Item> PLATE_IRON = parts("plate_iron", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.IRON.plate());
    public static RegistryObject<Item> POWDER_CN989;
    public static RegistryObject<Item> PLATE_CN989;
    public static final RegistryObject<Item> INGOT_LEAD = parts("ingot_lead", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.LEAD.ingot());
    public static final RegistryObject<Item> NUGGET_LEAD = parts("nugget_lead", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.LEAD.nugget());
    public static final RegistryObject<Item> INGOT_BISMUTH = parts("ingot_bismuth", ()->new ItemCustomInfo(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.BISMUTH.ingot());
    public static final RegistryObject<Item> NUGGET_BISMUTH = parts("nugget_bismuth", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.BISMUTH.nugget());
    public static RegistryObject<Item> INGOT_ARSENIC;
    public static final RegistryObject<Item> NUGGET_ARSENIC = parts("nugget_arsenic", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.ARSENIC.nugget());
    public static final RegistryObject<Item> INGOT_TANTALIUM = new WrappedItemRegistryBuilder("ingot_tantalium", ()->new Item(new Item.Properties())).tab(ModTabs.PARTS.getKey())
            .loc(HBMKey.ORDERLY_GEN_EXCEPT_FIRST, "'Tantalum'", "AKA Tantalum.").tags(HBMMatters.TANTALIUM.ingot()).build();
    public static final RegistryObject<Item> NUGGET_TANTALIUM = new WrappedItemRegistryBuilder("nugget_tantalium", ()->new Item(new Item.Properties())).tab(ModTabs.PARTS.getKey())
                    .loc(HBMKey.ORDERLY_GEN_EXCEPT_FIRST).tags(HBMMatters.TANTALIUM.nugget()).build();
    public static final RegistryObject<Item> INGOT_SILICON = parts("ingot_silicon", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.SILICON.ingot());
    public static final RegistryObject<Item> BILLET_SILICON = parts("billet_silicon", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET_SILICON = parts("nugget_silicon", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.SILICON.nugget());
    public static final RegistryObject<Item> INGOT_NIOBIUM = parts("ingot_niobium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.NIOBIUM.ingot());
    public static final RegistryObject<Item> NUGGET_NIOBIUM = parts("nugget_niobium", ()->new Item(new Item.Properties().rarity(Rarity.RARE)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.NIOBIUM.nugget());
    public static final RegistryObject<Item> INGOT_OSMIRIDIUM = parts("ingot_osmiridium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.OSMIRIDIUM.ingot());
    public static final RegistryObject<Item> NUGGET_OSMIRIDIUM = parts("nugget_osmiridium", ()->new Item(new Item.Properties().rarity(Rarity.RARE)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.OSMIRIDIUM.nugget());
    public static RegistryObject<Item> INGOT_HAFNIUM;
    public static RegistryObject<Item> NUGGET_HAFNIUM;
    public static final RegistryObject<Item> PLATE_LEAD = parts("plate_lead", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.LEAD.plate());
    public static final RegistryObject<Item> PLATE_DURA_STEEL = parts("plate_dura_steel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.DURA.plate());
    public static final RegistryObject<Item> NUGGET_SCHRABIDIUM = parts("nugget_schrabidium", ()->new Item(new Item.Properties().rarity(Rarity.RARE)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.SCHRABIDIUM.nugget());
    public static final RegistryObject<Item> PLATE_SCHRABIDIUM = parts("plate_schrabidium", ()->new Item(new Item.Properties().rarity(Rarity.RARE)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_COPPER = parts("plate_copper", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.COPPER.plate());
    public static final RegistryObject<Item> NUGGET_BERYLLIUM = parts("nugget_beryllium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.BERYLLIUM.nugget());
    public static final RegistryObject<Item> PLATE_GOLD = parts("plate_gold", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> HAZMAT_CLOTH = parts("hazmat_cloth", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> HAZMAT_CLOTH_RED = parts("hazmat_cloth_red", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> HAZMAT_CLOTH_GREY = parts("hazmat_cloth_grey", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> ASBESTOS_CLOTH = parts("asbestos_cloth", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static RegistryObject<Item> RAG;
    public static final RegistryObject<Item> RAG_DAMP = parts("rag_damp", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> RAG_PISS = parts("rag_piss", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static RegistryObject<Item> RAG_BLOOD;
    public static final RegistryObject<Item> FILTER_COAL = parts("filter_coal", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_COMBINE_STEEL = parts("plate_combine_steel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.CMB.plate());
    public static final RegistryObject<Item> PLATE_MIXED = parts("plate_mixed", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_PAA = parts("plate_paa", ()->new ItemCustomInfo(new Item.Properties().rarity(Rarity.UNCOMMON)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PIPES_STEEL = parts("pipes_steel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> DRILL_TITANIUM = parts("drill_titanium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_DALEKANIUM = parts("plate_dalekanium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_EUPHEMIUM = parts("plate_euphemium", ()->new ItemCustomInfo(new Item.Properties().rarity(Rarity.EPIC)), "Euphemium Compound Plate");
    public static RegistryObject<Item> BOLT;
    public static final RegistryObject<Item> BOLT_SPIKE = new WrappedItemRegistryBuilder("bolt_spike", ()->new Item(new Item.Properties()))
            .tab(ModTabs.PARTS.getKey()).loc(HBMKey.REVERSE_GEN).loc("Railroad Spike", "Radiates a threatening aura, somehow").build();
    public static final RegistryObject<Item> PLATE_POLYMER = parts("plate_polymer", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_KEVLAR = parts("plate_kevlar", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_DINEUTRONIUM = parts("plate_dineutronium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_DESH = parts("plate_desh", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_BISMUTH = new WrappedItemRegistryBuilder("plate_bismuth", ()->new Item(new Item.Properties()))
            .tab(ModTabs.PARTS.getKey()).loc(HBMKey.ORDERLY_GEN_EXCEPT_FIRST).loc("Bismuth Compound Plate", "Guys, It's Bismuth's alchemical symbol, I swear.").build();
    public static final RegistryObject<Item> PHOTO_PANEL = parts("photo_panel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static RegistryObject<Item> BERYLLIUM_MIRROR;
    public static final RegistryObject<Item> THRUSTER_NUCLEAR = parts("thruster_nuclear", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SAFETY_FUSE = parts("safety_fuse", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PART_GENERIC = parts("part_generic", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegisterObjectCollection<Item, ItemEnums.EnumExpensiveType> ITEM_EXPENSIVE = new RegisterObjectCollection<>(ItemEnums.EnumExpensiveType.class,
            type -> new WrappedItemRegistryBuilder("item_expensive." + type.name().toLowerCase(), ()->new Item(new Item.Properties()))
                    .tab(ModTabs.PARTS.getKey()).loc(HBMKey.ORDERLY_GEN_EXCEPT_FIRST).loc(HBMKey.REVERSE_GEN, "Expensive mode item")
                    .build());
    public static final RegisterObjectCollection<Item, ItemEnums.EnumSecretType> ITEM_SECRET = new RegisterObjectCollection<>(ItemEnums.EnumSecretType.class,
            type -> parts("item_secret." + type.name().toLowerCase(), ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN));
    // 这个ingot何意味？我觉得不用把它放进普通的ingot tag里
    public static final RegisterObjectCollection<Item, ItemEnums.EnumIngotMetal> INGOT_METAL = new RegisterObjectCollection<>(ItemEnums.EnumIngotMetal.class,
            type -> parts("ingot_metal." + type.name().toLowerCase(), ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN));
    public static final RegistryObject<Item> CHEMICAL_DYE = new WrappedItemRegistryBuilder("chemical_dye", ()->new ItemColorEnum(new Item.Properties()))
            .tab(ModTabs.PARTS.getKey()).loc(HBMKey.REVERSE_GEN).model(HBMKey.MODEL_ITEM_OVERLAY, HBM.rl("chemical_dye_overlay"))
            .color((ItemStack stack, int tintIndex) -> tintIndex == 1 ? NBTHelper.getInt(stack, HBMKey.COLOR, -1) : -1)
            .build();
    public static final RegistryObject<Item> CRAYON = new WrappedItemRegistryBuilder("crayon", ()->new ItemColorEnum(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).build())))
            .tab(ModTabs.PARTS.getKey()).loc(HBMKey.REVERSE_GEN).tags(Tags.Items.INGOTS).model(HBMKey.MODEL_ITEM_OVERLAY, HBM.rl("crayon_overlay"))
            .color((ItemStack stack, int tintIndex) -> tintIndex == 1 ? NBTHelper.getInt(stack, HBMKey.COLOR, -1) : -1)
            .build();
    public static RegistryObject<Item> DIVINE_SHARD;

    public static RegistryObject<Item> SCUTTERTAIL;
    public static RegistryObject<Item> SALTLEAF;
    public static RegistryObject<Item> LEAF_RUBBER;
    public static RegistryObject<Item> LEAF_PET;

    public static final RegistryObject<Item> UNDEFINED = new WrappedItemRegistryBuilder("undefined", ()->new Item(new Item.Properties())).tab(ModTabs.PARTS.getKey()).build();

    public static final RegistryObject<Item> BALL_RESIN = parts("ball_resin", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_DURA_STEEL = parts("ingot_dura_steel", ()->new Item(new Item.Properties()), "High-Speed Steel Ingot", HBMMatters.DURA.ingot());
    public static final RegistryObject<Item> INGOT_POLYMER = parts("ingot_polymer", ()->new Item(new Item.Properties()), "Polymer Bar", HBMMatters.POLYMER.ingot());
    public static final RegistryObject<Item> INGOT_BAKELITE = parts("ingot_bakelite", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.BAKELITE.ingot());
    public static final RegistryObject<Item> INGOT_BIORUBBER = parts("ingot_biorubber", ()->new Item(new Item.Properties()), "Latex Bar", Tags.Items.INGOTS);
    public static final RegistryObject<Item> INGOT_RUBBER = parts("ingot_rubber", ()->new Item(new Item.Properties()), "Rubber Bar", HBMMatters.RUBBER.ingot());
    public static RegistryObject<Item> INGOT_PET;
    public static final RegistryObject<Item> INGOT_PC = parts("ingot_pc", ()->new Item(new Item.Properties()), "Hard Plastic Bar", Tags.Items.INGOTS);
    public static final RegistryObject<Item> INGOT_PVC = parts("ingot_pvc", ()->new Item(new Item.Properties()), "PVC Bar", HBMMatters.PVC.ingot());
    public static RegistryObject<Item> STICK_PVC;
    public static RegistryObject<Item> STICK_VINYL;

    public static final RegistryObject<Item> INGOT_FIBERGLASS = new WrappedItemRegistryBuilder("ingot_fiberglass", ()->new Item(new Item.Properties())).tab(ModTabs.PARTS.getKey())
        .loc("Fiberglass Bar", "High in fiber, high in glass. Everything the body needs.").tags(Tags.Items.INGOTS).build();
    public static final RegistryObject<Item> INGOT_ASBESTOS = parts("ingot_asbestos", ()->new ItemCustomInfo(new Item.Properties(), HBMLang.ITEM_INGOTASBESTOS_DESC.translate()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.ASBESTOS.ingot());
    public static final RegistryObject<Item> POWDER_ASBESTOS = parts("powder_asbestos", ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN, HBMMatters.ASBESTOS.dust());
    public static final RegistryObject<Item> INGOT_CALCIUM = parts("ingot_calcium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.CALCIUM.ingot());
    public static final RegistryObject<Item> POWDER_CALCIUM = parts("powder_calcium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_CADMIUM = parts("ingot_cadmium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.CADMIUM.ingot());
    public static final RegistryObject<Item> POWDER_CADMIUM = parts("powder_cadmium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.CALCIUM.dust());
    public static final RegistryObject<Item> POWDER_BISMUTH = parts("powder_bismuth", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_MUD = parts("ingot_mud", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.MUD.ingot());
    public static RegistryObject<Item> INGOT_MAGMA;
    public static final RegistryObject<Item> INGOT_CFT = parts("ingot_cft", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, Tags.Items.INGOTS);

    public static final RegistryObject<Item> INGOT_LANTHANIUM = parts("ingot_lanthanium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.LANTHANIUM.ingot());
    public static RegistryObject<Item> NUGGET_LANTHANIUM;
    public static final RegistryObject<Item> INGOT_ACTINIUM = parts("ingot_actinium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.ACTINIUM.ingot());

    public static final RegistryObject<Item> INGOT_METEORITE = parts("ingot_meteorite", ()->new ItemHot(200, new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.METEORIC_IRON.ingot());
    public static final RegistryObject<Item> INGOT_METEORITE_FORGED = parts("ingot_meteorite_forged", ()->new ItemHot(200, new Item.Properties()), HBMKey.REVERSE_GEN, HBMMatters.METEORIC_IRON.ingot());
    public static final RegistryObject<Item> BLADE_METEORITE = parts("blade_meteorite", ()->new ItemHot(200, new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> INGOT_STEEL_DUSTED = new WrappedItemRegistryBuilder("ingot_steel_dusted", ()->new ItemHot(100, new Item.Properties()))
        .tab(ModTabs.PARTS.getKey()).loc(HBMKey.REVERSE_GEN).tags(Tags.Items.INGOTS).model(HBMKey.MODEL_ITEM_PROPERTY)
        .itemProperties(HBM.rl("state"), (stack, level, entity, seed) -> ItemHot.isCoolDown(stack) ? 1 : 0, HBM.rl("ingot_steel_dusted_hot"))
        .build();
    public static final RegistryObject<Item> INGOT_CHAINSTEEL = new WrappedItemRegistryBuilder("ingot_chainsteel", ()->new ItemHot(100, new Item.Properties()))
        .tab(ModTabs.PARTS.getKey()).loc(HBMKey.REVERSE_GEN).tags(Tags.Items.INGOTS).model(HBMKey.MODEL_ITEM_PROPERTY)
        .itemProperties(HBM.rl("state"), (stack, level, entity, seed) -> ItemHot.isCoolDown(stack) ? 1 : 0, HBM.rl("ingot_chainsteel_hot"))
        .build();

    public static final RegistryObject<Item> PLATE_ARMOR_TITANIUM = parts("plate_armor_titanium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_ARMOR_AJR = parts("plate_armor_ajr", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_ARMOR_HEV = parts("plate_armor_hev", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_ARMOR_LUNAR = parts("plate_armor_lunar", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_ARMOR_FAU = parts("plate_armor_fau", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PLATE_ARMOR_DNT = parts("plate_armor_dnt", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

    public static RegistryObject<Item> OIL_TAR;
    public static final RegistryObject<Item> SOLID_FUEL = parts("solid_fuel", ()->new ItemFuel(new Item.Properties(), 200 * 16), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SOLID_FUEL_PRESTO = parts("solid_fuel_presto", ()->new ItemFuel(new Item.Properties(), 200 * 40), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SOLID_FUEL_PRESTO_TRIPLET = parts("solid_fuel_presto_triplet", ()->new ItemFuel(new Item.Properties(), 200 * 200), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SOLID_FUEL_BF = parts("solid_fuel_bf", ()->new ItemFuel(new Item.Properties(), 200 * 160), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SOLID_FUEL_PRESTO_BF = parts("solid_fuel_presto_bf", ()->new ItemFuel(new Item.Properties(), 200 * 400), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SOLID_FUEL_PRESTO_TRIPLET_BF = parts("solid_fuel_presto_triplet_bf", ()->new ItemFuel(new Item.Properties(), 200 * 2000), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> ROCKET_FUEL = parts("rocket_fuel", ()->new ItemFuel(new Item.Properties(), 200 * 32), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

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
    public static RegistryObject<Item> CRYSTAL_SCHRARANIUM;
    public static RegistryObject<Item> CRYSTAL_SCHRABIDIUM;
    public static final RegistryObject<Item> CRYSTAL_RARE = parts("crystal_rare", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_PHOSPHORUS = parts("crystal_phosphorus", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_LITHIUM = parts("crystal_lithium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_COBALT = parts("crystal_cobalt", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_STARMETAL = parts("crystal_starmetal", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_CINNEBAR = parts("crystal_cinnebar", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_TRIXITE = parts("crystal_trixite", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> CRYSTAL_OSMIRIDIUM = parts("crystal_osmiridium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static RegistryObject<Item> CRYSTAL_NICKEL;
    public static RegistryObject<Item> CRYSTAL_NIOBIUM;
    //my name is seven, i made the zinc dispatcher
    //it was difficult, to dispatch the zinc() together
    //but unforutnatley something went so chopped chin wrong
    //now i cant do anything but sing this stupid song!!!!!!!!!!
    public static RegistryObject<Item> CRYSTAL_ZINC;

    public static RegistryObject<Item> NICKEL_SALTS;


    public static RegistryObject<Item> GEM_SODALITE;
    public static RegistryObject<Item> GEM_TANTALIUM;
    public static RegistryObject<Item> GEM_VOLCANIC;
    public static RegistryObject<Item> GEM_RAD;
    public static RegistryObject<Item> GEM_ALEXANDRITE;

    public static final RegistryObject<Item> POWDER_LEAD = parts("powder_lead", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_TANTALIUM = parts("powder_tantalium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_NEPTUNIUM = parts("powder_neptunium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_POLONIUM = parts("powder_polonium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_CO60 = parts("powder_co60", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_SR90 = parts("powder_sr90", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_SR90_TINY = parts("powder_sr90_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_AU198 = parts("powder_au198", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_RA226 = parts("powder_ra226", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_I131 = parts("powder_i131", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_I131_TINY = parts("powder_i131_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_XE135 = parts("powder_xe135", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_XE135_TINY = parts("powder_xe135_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_CS137 = parts("powder_cs137", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_CS137_TINY = parts("powder_cs137_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_AT209 = parts("powder_at209", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static RegistryObject<Item> POWDER_SCHRABIDIUM;
    public static RegistryObject<Item> POWDER_SCHRABIDATE;

    public static final RegistryObject<Item> POWDER_ALUMINIUM = parts("powder_aluminium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_BERYLLIUM = parts("powder_beryllium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_COPPER = parts("powder_copper", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_GOLD = parts("powder_gold", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_IRON = parts("powder_iron", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_TITANIUM = parts("powder_titanium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static RegistryObject<Item> POWDER_NICKEL;
    public static final RegistryObject<Item> POWDER_GALLIUM_TINY = parts("powder_gallium_tiny", ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN, HBMMatters.GALLIUM.small_dust());
    public static final RegistryObject<Item> POWDER_GALLIUM = parts("powder_gallium", ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN, HBMMatters.GALLIUM.dust());
    public static RegistryObject<Item> POWDER_ZINC;
    public static final RegistryObject<Item> POWDER_TUNGSTEN = parts("powder_tungsten", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_URANIUM = parts("powder_uranium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_PLUTONIUM = parts("powder_plutonium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> DUST = parts("dust", ()->new ItemFuel(new Item.Properties(), 200 / 8), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> DUST_TINY = parts("dust_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FALLOUT = parts("fallout", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static RegistryObject<Item> POWDER_POWER;

    public static RegistryObject<Item> POWDER_THORIUM;
    public static RegistryObject<Item> POWDER_IODINE;
    public static RegistryObject<Item> POWDER_NEODYMIUM;
    public static RegistryObject<Item> POWDER_ASTATINE;
    public static RegistryObject<Item> POWDER_CAESIUM;

    public static RegistryObject<Item> POWDER_STRONTIUM;
    public static RegistryObject<Item> POWDER_COBALT;
    public static RegistryObject<Item> POWDER_BROMINE;
    public static RegistryObject<Item> POWDER_NIOBIUM;
    public static RegistryObject<Item> POWDER_TENNESSINE;
    public static RegistryObject<Item> POWDER_CERIUM;

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

    public static RegistryObject<Item> POWDER_AUSTRALIUM;

    public static RegistryObject<Item> POWDER_DURA_STEEL;
    public static RegistryObject<Item> POWDER_POLYMER;
    public static RegistryObject<Item> POWDER_BAKELITE;
    public static RegistryObject<Item> POWDER_RUBBER;
    public static RegistryObject<Item> POWDER_PVC;
    public static RegistryObject<Item> POWDER_EUPHEMIUM;
    public static RegistryObject<Item> POWDER_METEORITE;

    public static RegistryObject<Item> POWDER_WD2004_TINY;
    public static RegistryObject<Item> POWDER_WD2004;

    public static final RegistryObject<Item> POWDER_STEEL_TINY = parts("powder_steel_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.STEEL.small_dust());
    public static final RegistryObject<Item> POWDER_LITHIUM_TINY = parts("powder_lithium_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.LITHIUM.small_dust());
    public static final RegistryObject<Item> POWDER_NEODYMIUM_TINY = parts("powder_neodymium_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.NEODYMIUM.small_dust());
    public static final RegistryObject<Item> POWDER_COBALT_TINY = parts("powder_cobalt_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.COBALT.small_dust());
    public static final RegistryObject<Item> POWDER_NIOBIUM_TINY = parts("powder_niobium_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.NIOBIUM.small_dust());
    public static final RegistryObject<Item> POWDER_CERIUM_TINY = parts("powder_cerium_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.CERIUM.small_dust());
    public static final RegistryObject<Item> POWDER_LANTHANIUM_TINY = parts("powder_lanthanium_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.LANTHANIUM.small_dust());
    public static final RegistryObject<Item> POWDER_ACTINIUM_TINY = parts("powder_actinium_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.ACTINIUM.small_dust());
    public static final RegistryObject<Item> POWDER_BORON_TINY = parts("powder_boron_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.BORON.small_dust());
    public static final RegistryObject<Item> POWDER_METEORITE_TINY = parts("powder_meteorite_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, ModTags.Items.SMALL_DUST);

    public static final RegistryObject<Item> POWDER_COLTAN_ORE = parts("powder_coltan_ore", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_COLTAN = parts("powder_coltan", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_TEKTITE = parts("powder_tektite", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_PALEOGENITE = parts("powder_paleogenite", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_PALEOGENITE_TINY = parts("powder_paleogenite_tiny", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, ModTags.Items.SMALL_DUST);
    public static final RegistryObject<Item> POWDER_IMPURE_OSMIRIDIUM = parts("powder_impure_osmiridium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_BORAX = parts("powder_borax", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_CHLOROCALCITE = parts("powder_chlorocalcite", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_MOLYSITE = parts("powder_molysite", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

    public static RegistryObject<Item> POWDER_LANTHANIUM;
    public static RegistryObject<Item> POWDER_ACTINIUM;
    public static RegistryObject<Item> POWDER_BORON;
    public static final RegistryObject<Item> POWDER_DESH = parts("powder_desh", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_SEMTEX_MIX = parts("powder_semtex_mix", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_DESH_MIX = parts("powder_desh_mix", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_DESH_READY = parts("powder_desh_ready", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_NITAN_MIX = parts("powder_nitan_mix", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_SPARK_MIX = parts("powder_spark_mix", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_YELLOWCAKE = parts("powder_yellowcake", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_MAGIC = parts("powder_magic", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_BALEFIRE = parts("powder_balefire", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> POWDER_SAWDUST = parts("powder_sawdust", ()->new ItemFuel(new Item.Properties(), 200 / 2), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, ModTags.Items.SAWDUST);
    public static final RegistryObject<Item> POWDER_FLUX = parts("powder_flux", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static RegistryObject<Item> POWDER_FERTILIZER;

    public static final RegistryObject<Item> FRAGMENT_NEODYMIUM = parts("fragment_neodymium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FRAGMENT_COBALT = parts("fragment_cobalt", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FRAGMENT_NIOBIUM = parts("fragment_niobium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FRAGMENT_CERIUM = parts("fragment_cerium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FRAGMENT_LANTHANIUM = parts("fragment_lanthanium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FRAGMENT_ACTINIUM = parts("fragment_actinium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FRAGMENT_BORON = parts("fragment_boron", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FRAGMENT_METEORITE = parts("fragment_meteorite", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FRAGMENT_COLTAN = parts("fragment_coltan", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegisterObjectCollection<Item, ItemEnums.EnumChunkType> CHUNK_ORE = new RegisterObjectCollection<>(ItemEnums.EnumChunkType.class, type -> new WrappedItemRegistryBuilder("chunk_ore." + type.toString().toLowerCase(), ()->new Item(new Item.Properties()))
            .tab(ModTabs.PARTS.getKey()).loc(HBMKey.REVERSE_GEN).tags(Tags.Items.ORES).build());

    public static final RegistryObject<Item> BIOMASS = parts("biomass", ()->new ItemFuel(new Item.Properties(), 200 * 2), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, ModTags.Items.BIOMASS);
    public static final RegistryObject<Item> FLESH = parts("flesh", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static RegistryObject<Item> FLESH_WAFER;
    public static RegistryObject<Item> GRILLED_FLESH;
    public static RegistryObject<Item> FLESH_BURGER;
    public static final RegistryObject<Item> BIOMASS_COMPRESSED = parts("biomass_compressed", ()->new ItemFuel(new Item.Properties(), 200 * 4), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static RegistryObject<Item> BIO_WAFER;
    public static RegistryObject<Item> PLANT_ITEM;

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
    public static RegistryObject<Item> OXY_PINWHEEL;
    public static final RegistryObject<Item> DEUTERIUM_FILTER = parts("deuterium_filter", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

    public static RegistryObject<Item> PARTS_LEGENDARY;

    public static RegistryObject<Item> CIRCUIT;

    public static RegistryObject<Item> CRT_DISPLAY;
//    public static ItemEnumMulti circuit_star_piece;
//    public static ItemEnumMulti circuit_star_component;
    public static RegistryObject<Item> CIRCUIT_STAR;

    public static RegistryObject<Item> ASSEMBLY_NUKE;

    public static RegistryObject<Item> CASING;

    public static final RegistryObject<Item> WIRING_RED_COPPER = parts("wiring_red_copper", ()->new ItemWiring(new Item.Properties().stacksTo(1).durability(20)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static RegistryObject<Item> WRENCH;

    public static final RegistryObject<Item> SHELL = parts("shell", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static RegistryObject<Item> PIPE;
    public static final RegistryObject<Item> FINS_FLAT = parts("fins_flat", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FINS_SMALL_STEEL = parts("fins_small_steel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FINS_BIG_STEEL = parts("fins_big_steel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FINS_TRI_STEEL = parts("fins_tri_steel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FINS_QUAD_TITANIUM = parts("fins_quad_titanium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SPHERE_STEEL = parts("sphere_steel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PEDESTAL_STEEL = parts("pedestal_steel", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> DYSFUNCTIONAL_REACTOR = parts("dysfunctional_reactor", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BLADE_TITANIUM = parts("blade_titanium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static RegistryObject<Item> BLADE_SYNGAS;
    public static final RegistryObject<Item> TURBINE_TITANIUM = parts("turbine_titanium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BLADE_TUNGSTEN = parts("blade_tungsten", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> TURBINE_TUNGSTEN = parts("turbine_tungsten", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static RegistryObject<Item> TURBINE_SYNGAS;
    public static RegistryObject<Item> RING_STARMETAL;
    public static final RegistryObject<Item> FLYWHEEL_BERYLLIUM = parts("flywheel_beryllium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

    public static RegistryObject<Item> GEAR_LARGE;
    public static final RegistryObject<Item> SAWBLADE = parts("sawblade", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

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

    public static final RegistryObject<Item> SEG_10 = parts("seg_10", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SEG_15 = parts("seg_15", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SEG_20 = parts("seg_20", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

    public static final RegistryObject<Item> COMBINE_SCRAP = parts("combine_scrap", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

    public static final RegistryObject<Item> SHIMMER_HEAD = parts("shimmer_head", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SHIMMER_AXE_HEAD = parts("shimmer_axe_head", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SHIMMER_HANDLE = parts("shimmer_handle", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

    //public static RegistryObject<Item> TELEPAD;
    public static RegistryObject<Item> ENTANGLEMENT_KIT;

    public static final RegistryObject<Item> STAMP_STONE_FLAT = new WrappedItemRegistryBuilder("stamp_stone_flat", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(32), ItemStamp.StampType.FLAT)).tab(ModTabs.CONTROL.getKey()).loc("Flat Stamp (Stone)").build();
    public static final RegistryObject<Item> STAMP_STONE_PLATE = new WrappedItemRegistryBuilder("stamp_stone_plate", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(32), ItemStamp.StampType.PLATE)).tab(ModTabs.CONTROL.getKey()).loc("Plate Stamp (Stone)").build();
    public static final RegistryObject<Item> STAMP_STONE_WIRE = new WrappedItemRegistryBuilder("stamp_stone_wire", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(32), ItemStamp.StampType.WIRE)).tab(ModTabs.CONTROL.getKey()).loc("Wire Stamp (Stone)").build();
    public static final RegistryObject<Item> STAMP_STONE_CIRCUIT = new WrappedItemRegistryBuilder("stamp_stone_circuit", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(32), ItemStamp.StampType.CIRCUIT)).tab(ModTabs.CONTROL.getKey()).loc("Circuit Stamp (Stone)").build();
    public static final RegistryObject<Item> STAMP_IRON_FLAT = new WrappedItemRegistryBuilder("stamp_iron_flat", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(64), ItemStamp.StampType.FLAT)).tab(ModTabs.CONTROL.getKey()).loc("Flat Stamp (Iron)").build();
    public static final RegistryObject<Item> STAMP_IRON_PLATE = new WrappedItemRegistryBuilder("stamp_iron_plate", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(64), ItemStamp.StampType.PLATE)).tab(ModTabs.CONTROL.getKey()).loc("Plate Stamp (Iron)").build();
    public static final RegistryObject<Item> STAMP_IRON_WIRE = new WrappedItemRegistryBuilder("stamp_iron_wire", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(64), ItemStamp.StampType.WIRE)).tab(ModTabs.CONTROL.getKey()).loc("Wire Stamp (Iron)").build();
    public static final RegistryObject<Item> STAMP_IRON_CIRCUIT = new WrappedItemRegistryBuilder("stamp_iron_circuit", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(64), ItemStamp.StampType.CIRCUIT)).tab(ModTabs.CONTROL.getKey()).loc("Circuit Stamp (Iron)").build();
    public static final RegistryObject<Item> STAMP_STEEL_FLAT = new WrappedItemRegistryBuilder("stamp_steel_flat", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(192), ItemStamp.StampType.FLAT)).tab(ModTabs.CONTROL.getKey()).loc("Flat Stamp (Steel)").build();
    public static final RegistryObject<Item> STAMP_STEEL_PLATE = new WrappedItemRegistryBuilder("stamp_steel_plate", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(192), ItemStamp.StampType.PLATE)).tab(ModTabs.CONTROL.getKey()).loc("Plate Stamp (Steel)").build();
    public static final RegistryObject<Item> STAMP_STEEL_WIRE = new WrappedItemRegistryBuilder("stamp_steel_wire", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(192), ItemStamp.StampType.WIRE)).tab(ModTabs.CONTROL.getKey()).loc("Wire Stamp (Steel)").build();
    public static final RegistryObject<Item> STAMP_STEEL_CIRCUIT = new WrappedItemRegistryBuilder("stamp_steel_circuit", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(192), ItemStamp.StampType.CIRCUIT)).tab(ModTabs.CONTROL.getKey()).loc("Circuit Stamp (Steel)").build();
    public static final RegistryObject<Item> STAMP_TITANIUM_FLAT = new WrappedItemRegistryBuilder("stamp_titanium_flat", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(256), ItemStamp.StampType.FLAT)).tab(ModTabs.CONTROL.getKey()).loc("Flat Stamp (Titanium)").build();
    public static final RegistryObject<Item> STAMP_TITANIUM_PLATE = new WrappedItemRegistryBuilder("stamp_titanium_plate", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(256), ItemStamp.StampType.PLATE)).tab(ModTabs.CONTROL.getKey()).loc("Plate Stamp (Titanium)").build();
    public static final RegistryObject<Item> STAMP_TITANIUM_WIRE = new WrappedItemRegistryBuilder("stamp_titanium_wire", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(256), ItemStamp.StampType.WIRE)).tab(ModTabs.CONTROL.getKey()).loc("Wire Stamp (Titanium)").build();
    public static final RegistryObject<Item> STAMP_TITANIUM_CIRCUIT = new WrappedItemRegistryBuilder("stamp_titanium_circuit", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(256), ItemStamp.StampType.CIRCUIT)).tab(ModTabs.CONTROL.getKey()).loc("Circuit Stamp (Titanium)").build();
    public static final RegistryObject<Item> STAMP_OBSIDIAN_FLAT = new WrappedItemRegistryBuilder("stamp_obsidian_flat", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(512), ItemStamp.StampType.FLAT)).tab(ModTabs.CONTROL.getKey()).loc("Flat Stamp (Obsidian)").build();
    public static final RegistryObject<Item> STAMP_OBSIDIAN_PLATE = new WrappedItemRegistryBuilder("stamp_obsidian_plate", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(512), ItemStamp.StampType.PLATE)).tab(ModTabs.CONTROL.getKey()).loc("Plate Stamp (Obsidian)").build();
    public static final RegistryObject<Item> STAMP_OBSIDIAN_WIRE = new WrappedItemRegistryBuilder("stamp_obsidian_wire", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(512), ItemStamp.StampType.WIRE)).tab(ModTabs.CONTROL.getKey()).loc("Wire Stamp (Obsidian)").build();
    public static final RegistryObject<Item> STAMP_OBSIDIAN_CIRCUIT = new WrappedItemRegistryBuilder("stamp_obsidian_circuit", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(512), ItemStamp.StampType.CIRCUIT)).tab(ModTabs.CONTROL.getKey()).loc("Circuit Stamp (Obsidian)").build();
    public static final RegistryObject<Item> STAMP_DESH_FLAT = new WrappedItemRegistryBuilder("stamp_desh_flat", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(0), ItemStamp.StampType.FLAT)).tab(ModTabs.CONTROL.getKey()).loc("Flat Stamp (Desh)").build();
    public static final RegistryObject<Item> STAMP_DESH_PLATE = new WrappedItemRegistryBuilder("stamp_desh_plate", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(0), ItemStamp.StampType.PLATE)).tab(ModTabs.CONTROL.getKey()).loc("Plate Stamp (Desh)").build();
    public static final RegistryObject<Item> STAMP_DESH_WIRE = new WrappedItemRegistryBuilder("stamp_desh_wire", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(0), ItemStamp.StampType.WIRE)).tab(ModTabs.CONTROL.getKey()).loc("Wire Stamp (Desh)").build();
    public static final RegistryObject<Item> STAMP_DESH_CIRCUIT = new WrappedItemRegistryBuilder("stamp_desh_circuit", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(0), ItemStamp.StampType.CIRCUIT)).tab(ModTabs.CONTROL.getKey()).loc("Circuit Stamp (Desh)").build();
    public static RegistryObject<Item> STAMP_BOOK;

    public static final RegistryObject<Item> STAMP_357 = new WrappedItemRegistryBuilder("stamp_357", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(1000), ItemStamp.StampType.C357)).tab(ModTabs.CONTROL.getKey()).loc(".357 Magnum Stamp").build();
    public static final RegistryObject<Item> STAMP_44 = new WrappedItemRegistryBuilder("stamp_44", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(1000), ItemStamp.StampType.C44)).tab(ModTabs.CONTROL.getKey()).loc(".44 Magnum Stamp").build();
    public static final RegistryObject<Item> STAMP_9 = new WrappedItemRegistryBuilder("stamp_9", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(1000), ItemStamp.StampType.C9)).tab(ModTabs.CONTROL.getKey()).loc("Small Caliber Stamp").build();
    public static final RegistryObject<Item> STAMP_50 = new WrappedItemRegistryBuilder("stamp_50", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(1000), ItemStamp.StampType.C50)).tab(ModTabs.CONTROL.getKey()).loc("Large Caliber Stamp").build();

    public static final RegistryObject<Item> STAMP_DESH_357 = new WrappedItemRegistryBuilder("stamp_357_desh", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(0), ItemStamp.StampType.C357)).tab(ModTabs.CONTROL.getKey()).loc(".357 Magnum Stamp (Desh)").build();
    public static final RegistryObject<Item> STAMP_DESH_44 = new WrappedItemRegistryBuilder("stamp_44_desh", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(0), ItemStamp.StampType.C44)).tab(ModTabs.CONTROL.getKey()).loc(".44 Magnum Stamp (Desh)").build();
    public static final RegistryObject<Item> STAMP_DESH_9 = new WrappedItemRegistryBuilder("stamp_9_desh", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(0), ItemStamp.StampType.C9)).tab(ModTabs.CONTROL.getKey()).loc("Small Caliber Stamp (Desh)").build();
    public static final RegistryObject<Item> STAMP_DESH_50 = new WrappedItemRegistryBuilder("stamp_50_desh", ()->new ItemStamp(new Item.Properties().stacksTo(1).durability(0), ItemStamp.StampType.C50)).tab(ModTabs.CONTROL.getKey()).loc("Large Caliber Stamp (Desh)").build();

    public static RegistryObject<Item> BLADES_STEEL;
    public static RegistryObject<Item> BLADES_TITANIUM;
    public static RegistryObject<Item> BLADES_DESH;

    public static RegistryObject<Item> MOLD_BASE;
    public static RegistryObject<Item> MOLD;
    public static RegistryObject<Item> SCRAPS;
    public static RegistryObject<Item> INGOT_RAW;
    public static RegistryObject<Item> PLATE_CAST;
    public static RegistryObject<Item> PLATE_WELDED;
    public static final RegisterObjectCollection<Item, HBMMatter> WIRE_FINE = new RegisterObjectCollection<>(HBMMatters.ALL_MATTERS, matter -> new WrappedItemRegistryBuilder("wire_" + matter.name(), ()->new Item(new Item.Properties()))
            .tab(ModTabs.PARTS.getKey()).loc(HBMKey.ORDERLY_GEN_EXCEPT_FIRST).model(itemModelGen -> itemModelGen.basicItem("wire_" + matter.name(), HBM.rl("wire_" + matter.name()))).tags(matter.grip())
            .color((stack, idx) -> matter.solidColorLight).build(), matter -> matter.wire() != null);
    public static RegistryObject<Item> WIRE_DENSE;
    public static final RegisterObjectCollection<Item, HBMMatter> PART_BARREL_LIGHT = new RegisterObjectCollection<>(HBMMatters.ALL_MATTERS, matter -> new WrappedItemRegistryBuilder("part_barrel_light_" + matter.name(), ()->new Item(new Item.Properties()))
            .tab(ModTabs.PARTS.getKey()).loc(HBMKey.ORDERLY_GEN_EXCEPT_FIRST).model(itemModelGen -> itemModelGen.basicItem("part_barrel_light_" + matter.name(), HBM.rl("part_barrel_light"))).tags(matter.barrel_light())
            .color((stack, idx) -> matter.solidColorLight).build(), matter -> matter.barrel_light() != null);
    public static final RegisterObjectCollection<Item, HBMMatter> PART_BARREL_HEAVY = new RegisterObjectCollection<>(HBMMatters.ALL_MATTERS, matter -> new WrappedItemRegistryBuilder("part_barrel_heavy_" + matter.name(), ()->new Item(new Item.Properties()))
            .tab(ModTabs.PARTS.getKey()).loc(HBMKey.ORDERLY_GEN_EXCEPT_FIRST).model(itemModelGen -> itemModelGen.basicItem("part_barrel_heavy_" + matter.name(), HBM.rl("part_barrel_heavy"))).tags(matter.barrel_heavy())
            .color((stack, idx) -> matter.solidColorLight).build(), matter -> matter.barrel_heavy() != null);
    public static final RegisterObjectCollection<Item, HBMMatter> PART_RECEIVER_HEAVY = new RegisterObjectCollection<>(HBMMatters.ALL_MATTERS, matter -> new WrappedItemRegistryBuilder("part_receiver_heavy_" + matter.name(), ()->new Item(new Item.Properties()))
            .tab(ModTabs.PARTS.getKey()).loc(HBMKey.ORDERLY_GEN_EXCEPT_FIRST).model(itemModelGen -> itemModelGen.basicItem("part_receiver_heavy_" + matter.name(), HBM.rl("part_receiver_heavy"))).tags(matter.receiver_heavy())
            .color((stack, idx) -> matter.solidColorLight).build(), matter -> matter.receiver_heavy() != null);
    public static final RegisterObjectCollection<Item, HBMMatter> PART_RECEIVER_LIGHT = new RegisterObjectCollection<>(HBMMatters.ALL_MATTERS, matter -> new WrappedItemRegistryBuilder("part_receiver_light_" + matter.name(), ()->new Item(new Item.Properties()))
            .tab(ModTabs.PARTS.getKey()).loc(HBMKey.ORDERLY_GEN_EXCEPT_FIRST).model(itemModelGen -> itemModelGen.basicItem("part_receiver_light_" + matter.name(), HBM.rl("part_receiver_light"))).tags(matter.receiver_light())
            .color((stack, idx) -> matter.solidColorLight).build(), matter -> matter.receiver_light() != null);
    public static final RegisterObjectCollection<Item, HBMMatter> PART_MECHANISM = new RegisterObjectCollection<>(HBMMatters.ALL_MATTERS, matter -> new WrappedItemRegistryBuilder("part_mechanism_" + matter.name(), ()->new Item(new Item.Properties()))
            .tab(ModTabs.PARTS.getKey()).loc(HBMKey.ORDERLY_GEN_EXCEPT_FIRST).model(itemModelGen -> itemModelGen.basicItem("part_mechanism_" + matter.name(), HBM.rl("part_mechanism"))).tags(matter.mechanism())
            .color((stack, idx) -> matter.solidColorLight).build(), matter -> matter.mechanism() != null);
    public static final RegisterObjectCollection<Item, HBMMatter> PART_STOCK = new RegisterObjectCollection<>(HBMMatters.ALL_MATTERS, matter -> new WrappedItemRegistryBuilder("part_stock_" + matter.name(), ()->new Item(new Item.Properties()))
            .tab(ModTabs.PARTS.getKey()).loc(HBMKey.ORDERLY_GEN_EXCEPT_FIRST).model(itemModelGen -> itemModelGen.basicItem("part_stock_" + matter.name(), HBM.rl("part_stock"))).tags(matter.stock())
            .color((stack, idx) -> matter.solidColorLight).build(), matter -> matter.stock() != null);
    public static final RegisterObjectCollection<Item, HBMMatter> PART_GRIP = new RegisterObjectCollection<>(HBMMatters.ALL_MATTERS, matter -> new WrappedItemRegistryBuilder("part_grip_" + matter.name(), ()->new Item(new Item.Properties()))
            .tab(ModTabs.PARTS.getKey()).loc(HBMKey.ORDERLY_GEN_EXCEPT_FIRST).model(itemModelGen -> itemModelGen.basicItem("part_grip_" + matter.name(), HBM.rl("part_grip"))).tags(matter.grip())
            .color((stack, idx) -> matter.solidColorLight).build(), matter -> matter.grip() != null);

    public static RegistryObject<Item> PART_LITHIUM;
    public static RegistryObject<Item> PART_BERYLLIUM;
    public static RegistryObject<Item> PART_CARBON;
    public static RegistryObject<Item> PART_COPPER;
    public static RegistryObject<Item> PART_PLUTONIUM;

    public static RegistryObject<Item> LASER_CRYSTAL_CO2;
    public static RegistryObject<Item> LASER_CRYSTAL_BISMUTH;
    public static RegistryObject<Item> LASER_CRYSTAL_CMB;
    public static RegistryObject<Item> LASER_CRYSTAL_IRON;
    public static RegistryObject<Item> LASER_CRYSTAL_DNT;
    public static final RegistryObject<Item> LASER_CRYSTAL_DIGAMMA = control("laser_crystal_digamma", ()->new ItemFELCrystal(new Item.Properties(), ItemFELCrystal.EnumWavelengths.DRX), "Digamma Laser Crystal");

    public static RegistryObject<Item> THERMO_ELEMENT;

    public static final RegistryObject<Item> CATALYTIC_CONVERTER = parts("catalytic_converter", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static RegistryObject<Item> CRACKPIPE;

    public static final RegistryObject<Item> ASSEMBLY_TEMPLATE = template("assembly_template", () -> new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> GUN_RIFLE = new WrappedItemRegistryBuilder("gun_maresleg", () -> new ItemGun(new Item.Properties())).model(HBMKey.MODEL_EXISTING_FILE).tab(ModTabs.WEAPON.getKey()).loc("Lever Action Shotgun").build();

    public static RegistryObject<Item> PELLET_RTG_DEPLETED;

    public static RegistryObject<Item> PELLET_RTG_RADIUM;
    public static RegistryObject<Item> PELLET_RTG_WEAK;
    public static final RegistryObject<Item> PELLET_RTG = control("pellet_rtg", () -> new ItemRTGPellet(new Item.Properties().stacksTo(1), 10), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static RegistryObject<Item> PELLET_RTG_STRONTIUM;
    public static RegistryObject<Item> PELLET_RTG_COBALT;
    public static RegistryObject<Item> PELLET_RTG_ACTINIUM;
    public static RegistryObject<Item> PELLET_RTG_POLONIUM;
    public static RegistryObject<Item> PELLET_RTG_AMERICIUM;
    public static RegistryObject<Item> PELLET_RTG_BERKELIUM;
    public static RegistryObject<Item> PELLET_RTG_GOLD;
    public static RegistryObject<Item> PELLET_RTG_LEAD;
    public static RegistryObject<Item> PELLET_RTG_CF251;
    public static RegistryObject<Item> PELLET_RTG_CF252;

    public static final RegistryObject<Item> PISTON_SELENIUM = parts("piston_selenium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> PISTON_SET = parts("piston_set", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static RegistryObject<Item> DRILLBIT;

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

    public static RegistryObject<Item> AMS_LENS;

    public static RegistryObject<Item> AMS_CORE_SING;
    public static RegistryObject<Item> AMS_CORE_WORMHOLE;
    public static RegistryObject<Item> AMS_CORE_EYEOFHARMONY;
    public static RegistryObject<Item> AMS_CORE_THINGY;

    @Deprecated public static RegistryObject<Item> FUSION_SHIELD_TUNGSTEN;
    @Deprecated public static RegistryObject<Item> FUSION_SHIELD_DESH;
    @Deprecated public static RegistryObject<Item> FUSION_SHIELD_CHLOROPHYTE;
    @Deprecated public static RegistryObject<Item> FUSION_SHIELD_VAPORWAVE;

    public static final RegistryObject<Item> CELL_EMPTY = parts("cell_empty", ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> CELL_UF6 = parts("cell_uf6", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> CELL_PUF6 = parts("cell_puf6", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> CELL_DEUTERIUM = parts("cell_deuterium", ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> CELL_TRITIUM = parts("cell_tritium", ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> CELL_SAS3 = parts("cell_sas3", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> CELL_ANTIMATTER = parts("cell_antimatter", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> CELL_ANTI_SCHRABIDIUM = parts("cell_anti_schrabidium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> CELL_BALEFIRE = parts("cell_balefire", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);

    public static final RegistryObject<Item> DEMON_CORE_OPEN = parts("demon_core_open", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> DEMON_CORE_CLOSED = parts("demon_core_closed", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);

    public static RegistryObject<Item> PA_COIL;

    public static final RegistryObject<Item> PARTICLE_EMPTY = parts("particle_empty", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> PARTICLE_HYDROGEN = parts("particle_hydrogen", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> PARTICLE_COPPER = parts("particle_copper", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> PARTICLE_LEAD = parts("particle_lead", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
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

    public static RegistryObject<Item> PELLET_ANTIMATTER;
    public static RegistryObject<Item> SINGULARITY;
    public static RegistryObject<Item> SINGULARITY_COUNTER_RESONANT;
    public static RegistryObject<Item> SINGULARITY_SUPER_HEATED;
    public static RegistryObject<Item> BLACK_HOLE;
    public static RegistryObject<Item> SINGULARITY_SPARK;
    public static RegistryObject<Item> CRYSTAL_XEN;
    public static RegistryObject<Item> INF_WATER;
    public static RegistryObject<Item> INF_WATER_MK2;

    public static RegistryObject<Item> FUEL_ADDITIVE;

    public static RegistryObject<Item> CANISTER_EMPTY;
    public static RegistryObject<Item> CANISTER_FULL;
    public static RegistryObject<Item> CANISTER_NAPALM;

    public static RegistryObject<Item> GAS_EMPTY;
    public static RegistryObject<Item> GAS_FULL;

    public static RegistryObject<Item> FLUID_TANK_FULL;
    public static RegistryObject<Item> FLUID_TANK_EMPTY;
    public static RegistryObject<Item> FLUID_TANK_LEAD_FULL;
    public static RegistryObject<Item> FLUID_TANK_LEAD_EMPTY;
    public static RegistryObject<Item> FLUID_BARREL_FULL;
    public static RegistryObject<Item> FLUID_BARREL_EMPTY;
    public static RegistryObject<Item> FLUID_BARREL_INFINITE;
    public static RegistryObject<Item> FLUID_PACK_FULL;
    public static RegistryObject<Item> FLUID_PACK_EMPTY;
    public static RegistryObject<Item> PIPETTE;
    public static RegistryObject<Item> PIPETTE_BORON;
    public static RegistryObject<Item> PIPETTE_LABORATORY;
    public static RegistryObject<Item> SIPHON;

    public static final RegistryObject<Item> DISPERSER_CANISTER_EMPTY = legacyGrenade("disperser_canister_empty", ItemGrenade.Type.IF_NULL);
    public static final RegistryObject<Item> DISPERSER_CANISTER = legacyGrenade("disperser_canister", ItemGrenade.Type.GASCAN);
    public static final RegistryObject<Item> GLYPHID_GLAND = legacyGrenade("glyphid_gland", ItemGrenade.Type.IF_TOXIC);
    public static final RegistryObject<Item> GLYPHID_GLAND_EMPTY = legacyGrenade("glyphid_gland_empty", ItemGrenade.Type.IF_NULL);

    public static RegistryObject<Item> SYRINGE_EMPTY;
    public static RegistryObject<Item> SYRINGE_ANTIDOTE;
    public static RegistryObject<Item> SYRINGE_POISON;
    public static RegistryObject<Item> SYRINGE_AWESOME;
    public static RegistryObject<Item> SYRINGE_METAL_EMPTY;
    public static RegistryObject<Item> SYRINGE_METAL_STIMPAK;
    public static RegistryObject<Item> SYRINGE_METAL_MEDX;
    public static RegistryObject<Item> SYRINGE_METAL_PSYCHO;
    public static RegistryObject<Item> SYRINGE_METAL_SUPER;
    public static RegistryObject<Item> SYRINGE_TAINT;
    public static RegistryObject<Item> SYRINGE_MKUNICORN;
    public static RegistryObject<Item> IV_EMPTY;
    public static RegistryObject<Item> IV_BLOOD;
    public static RegistryObject<Item> IV_XP_EMPTY;
    public static RegistryObject<Item> IV_XP;
    public static RegistryObject<Item> RADAWAY;
    public static RegistryObject<Item> RADAWAY_STRONG;
    public static RegistryObject<Item> RADAWAY_FLUSH;
    public static RegistryObject<Item> RADX;
    public static RegistryObject<Item> SIOX;
    public static RegistryObject<Item> PILL_HERBAL;
    public static RegistryObject<Item> XANAX;
    public static RegistryObject<Item> FMN;
    public static RegistryObject<Item> FIVE_HTP;
    public static RegistryObject<Item> MED_BAG;
    public static RegistryObject<Item> PILL_IODINE;
    public static RegistryObject<Item> PLAN_C;
    public static RegistryObject<Item> PILL_RED;
    public static RegistryObject<Item> STEALTH_BOY;
    public static RegistryObject<Item> GAS_MASK_FILTER;
    public static RegistryObject<Item> GAS_MASK_FILTER_MONO;
    public static RegistryObject<Item> GAS_MASK_FILTER_COMBO;
    public static RegistryObject<Item> GAS_MASK_FILTER_RAG;
    public static RegistryObject<Item> GAS_MASK_FILTER_PISS;
    public static RegistryObject<Item> JETPACK_TANK;
    public static RegistryObject<Item> LOX_TANK;
    public static RegistryObject<Item> GUN_KIT_1;
    public static RegistryObject<Item> GUN_KIT_2;
    public static RegistryObject<Item> CBT_DEVICE;
    public static RegistryObject<Item> CIGARETTE;

    public static RegistryObject<Item> ANIMAN;

    public static RegistryObject<Item> CAN_EMPTY;
    public static RegistryObject<Item> CAN_SMART;
    public static RegistryObject<Item> CAN_CREATURE;
    public static RegistryObject<Item> CAN_REDBOMB;
    public static RegistryObject<Item> CAN_MRSUGAR;
    public static RegistryObject<Item> CAN_OVERCHARGE;
    public static RegistryObject<Item> CAN_LUNA;
    public static RegistryObject<Item> CAN_BEPIS;
    public static RegistryObject<Item> CAN_BREEN;
    public static RegistryObject<Item> CAN_MUG;
    public static final RegistryObject<Item> MUCHO_MANGO = consumable("mucho_mango",
            () -> LegacyConsumableItem.builder(10, 0.6F)
                    .alwaysEat()
                    .useAnimation(UseAnim.DRINK)
                    .useDuration(200)
                    .tooltip("item.hbm.mucho_mango.desc")
                    .effect(() -> MobEffects.MOVEMENT_SPEED, 200, 0, 1.0F)
                    .build(),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static RegistryObject<Item> BOTTLE_EMPTY;
    public static final RegistryObject<Item> BOTTLE_NUKA = consumable("bottle_nuka", () -> new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static RegistryObject<Item> BOTTLE_CHERRY;
    public static RegistryObject<Item> BOTTLE_QUANTUM;
    public static RegistryObject<Item> BOTTLE_SPARKLE;
    public static RegistryObject<Item> BOTTLE_RAD;
    public static RegistryObject<Item> BOTTLE2_EMPTY;
    public static RegistryObject<Item> BOTTLE2_KORL;
    public static RegistryObject<Item> BOTTLE2_FRITZ;
    public static RegistryObject<Item> FLASK_EMPTY;
    public static RegistryObject<Item> FLASK_INFUSION;
    public static RegistryObject<Item> CHOCOLATE_MILK;
    public static RegistryObject<Item> COFFEE;
    public static RegistryObject<Item> COFFEE_RADIUM;
    public static RegistryObject<Item> CHOCOLATE;
    public static RegistryObject<Item> CAP_NUKA;
    public static RegistryObject<Item> CAP_QUANTUM;
    public static RegistryObject<Item> CAP_SPARKLE;
    public static RegistryObject<Item> CAP_RAD;
    public static RegistryObject<Item> CAP_KORL;
    public static RegistryObject<Item> CAP_FRITZ;
    public static RegistryObject<Item> RING_PULL;
    public static RegistryObject<Item> BDCL;
//    public static ItemEnumMulti canned_conserve;
    public static RegistryObject<Item> CAN_KEY;

    public static RegistryObject<Item> GLASS_EMPTY;
    public static RegistryObject<Item> GLASS_SMILK;
    public static RegistryObject<Item> STRAWBERRY;
    public static RegistryObject<Item> MINT_LEAVES;
    public static RegistryObject<Item> TEASEEDS;
    public static RegistryObject<Item> TEA_LEAF;
    public static RegistryObject<Item> BEAN_RAW;
    public static RegistryObject<Item> BEAN_ROAST;
    public static RegistryObject<Item> POWDER_COFFEE;
    public static RegistryObject<Item> CMUG_EMPTY;
    public static RegistryObject<Item> TEACUP;
    public static RegistryObject<Item> TEACUP_EMPTY;
    public static RegistryObject<Item> BOTTLE_HONEY;
    public static RegistryObject<Item> PARAFFIN_SEEDS;


    public static RegistryObject<Item> BOAT_RUBBER;
    public static RegistryObject<Item> CART;
    public static RegistryObject<Item> TRAIN;
    public static RegistryObject<Item> DRONE;

    public static RegistryObject<Item> COIN_CREEPER;
    public static RegistryObject<Item> COIN_RADIATION;
    public static RegistryObject<Item> COIN_MASKMAN;
    public static RegistryObject<Item> COIN_WORM;
    public static RegistryObject<Item> COIN_UFO;
    public static RegistryObject<Item> COIN_AIRLINER;
    public static RegistryObject<Item> COIN_TOKEN;
    //public static RegistryObject<Item> COIN_SIEGE;
    //public static RegistryObject<Item> SOURCE;

    public static RegistryObject<Item> ROD_EMPTY;
    public static RegistryObject<Item> ROD;
    public static RegistryObject<Item> ROD_DUAL_EMPTY;
    public static RegistryObject<Item> ROD_DUAL;
    public static RegistryObject<Item> ROD_QUAD_EMPTY;
    public static RegistryObject<Item> ROD_QUAD;

    public static RegistryObject<Item> ROD_ZIRNOX_EMPTY;
    public static RegistryObject<Item> ROD_ZIRNOX_TRITIUM;
    public static final RegistryObject<Item> rod_zirnox = control("rod_zirnox", () -> new ItemZirnoxRod(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

//    public static ItemEnumMulti full_drive;
    public static RegistryObject<Item> HARD_DRIVE;

    public static RegistryObject<Item> ROD_ZIRNOX_NATURAL_URANIUM_FUEL_DEPLETED;
    public static RegistryObject<Item> ROD_ZIRNOX_URANIUM_FUEL_DEPLETED;
    public static RegistryObject<Item> ROD_ZIRNOX_THORIUM_FUEL_DEPLETED;
    public static RegistryObject<Item> ROD_ZIRNOX_MOX_FUEL_DEPLETED;
    public static RegistryObject<Item> ROD_ZIRNOX_PLUTONIUM_FUEL_DEPLETED;
    public static RegistryObject<Item> ROD_ZIRNOX_U233_FUEL_DEPLETED;
    public static RegistryObject<Item> ROD_ZIRNOX_U235_FUEL_DEPLETED;
    public static RegistryObject<Item> ROD_ZIRNOX_LES_FUEL_DEPLETED;
    public static RegistryObject<Item> ROD_ZIRNOX_ZFB_MOX_DEPLETED;

    public static RegistryObject<Item> WASTE_NATURAL_URANIUM;
    public static RegistryObject<Item> WASTE_URANIUM;
    public static RegistryObject<Item> WASTE_THORIUM;
    public static RegistryObject<Item> WASTE_MOX;
    public static RegistryObject<Item> WASTE_PLUTONIUM;
    public static RegistryObject<Item> WASTE_U233;
    public static RegistryObject<Item> WASTE_U235;
    public static RegistryObject<Item> WASTE_SCHRABIDIUM;
    public static RegistryObject<Item> WASTE_ZFB_MOX;

    @Deprecated public static RegistryObject<Item> WASTE_PLATE_U233;
    @Deprecated public static RegistryObject<Item> WASTE_PLATE_U235;
    @Deprecated public static RegistryObject<Item> WASTE_PLATE_MOX;
    @Deprecated public static RegistryObject<Item> WASTE_PLATE_PU239;
    @Deprecated public static RegistryObject<Item> WASTE_PLATE_SA326;
    @Deprecated public static RegistryObject<Item> WASTE_PLATE_RA226BE;
    @Deprecated public static RegistryObject<Item> WASTE_PLATE_PU238BE;

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

    public static RegistryObject<Item> PILE_ROD;

    @Deprecated public static RegistryObject<Item> PLATE_FUEL_U233;
    @Deprecated public static RegistryObject<Item> PLATE_FUEL_U235;
    @Deprecated public static RegistryObject<Item> PLATE_FUEL_MOX;
    @Deprecated public static RegistryObject<Item> PLATE_FUEL_PU239;
    @Deprecated public static RegistryObject<Item> PLATE_FUEL_SA326;
    @Deprecated public static RegistryObject<Item> PLATE_FUEL_RA226BE;
    @Deprecated public static RegistryObject<Item> PLATE_FUEL_PU238BE;

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

    public static RegistryObject<Item> PWR_FUEL;
    public static RegistryObject<Item> PWR_FUEL_HOT;
    public static RegistryObject<Item> PWR_FUEL_DEPLETED;
    public static RegistryObject<Item> PWR_PRINTER;

    public static RegistryObject<Item> RBMK_LID;
    public static RegistryObject<Item> RBMK_LID_GLASS;
    public static RegistryObject<Item> RBMK_FUEL_EMPTY;
//    public static ItemRBMKRod rbmk_fuel_lecm;
//    public static ItemRBMKRod rbmk_fuel_mecm;
//    public static ItemRBMKRod rbmk_fuel_hecm;
//    public static ItemRBMKPellet rbmk_pellet_ueu;
//    public static ItemRBMKPellet rbmk_pellet_meu;
//    public static ItemRBMKPellet rbmk_pellet_heu233;
//    public static ItemRBMKPellet rbmk_pellet_heu235;
//    public static ItemRBMKPellet rbmk_pellet_uzh;
//    public static ItemRBMKPellet rbmk_pellet_thmeu;
//    public static ItemRBMKPellet rbmk_pellet_lep;
//    public static ItemRBMKPellet rbmk_pellet_mep;
//    public static ItemRBMKPellet rbmk_pellet_hep239;
//    public static ItemRBMKPellet rbmk_pellet_hep241;
//    public static ItemRBMKPellet rbmk_pellet_lea;
//    public static ItemRBMKPellet rbmk_pellet_mea;
//    public static ItemRBMKPellet rbmk_pellet_hea241;
//    public static ItemRBMKPellet rbmk_pellet_hea242;
//    public static ItemRBMKPellet rbmk_pellet_bk247;
//    public static ItemRBMKPellet rbmk_pellet_men;
//    public static ItemRBMKPellet rbmk_pellet_hen;
//    public static ItemRBMKPellet rbmk_pellet_mox;
//    public static ItemRBMKPellet rbmk_pellet_les;
//    public static ItemRBMKPellet rbmk_pellet_mes;
//    public static ItemRBMKPellet rbmk_pellet_hes;
//    public static ItemRBMKPellet rbmk_pellet_leaus;
//    public static ItemRBMKPellet rbmk_pellet_heaus;
//    public static ItemRBMKPellet rbmk_pellet_po210be;
//    public static ItemRBMKPellet rbmk_pellet_ra226be;
//    public static ItemRBMKPellet rbmk_pellet_pu238be;
//    public static ItemRBMKPellet rbmk_pellet_balefire_gold;
//    public static ItemRBMKPellet rbmk_pellet_flashlead;
//    public static ItemRBMKPellet rbmk_pellet_balefire;
//    public static ItemRBMKPellet rbmk_pellet_zfb_bismuth;
//    public static ItemRBMKPellet rbmk_pellet_zfb_pu241;
//    public static ItemRBMKPellet rbmk_pellet_zfb_am_mix;
//    public static ItemRBMKPellet rbmk_pellet_drx;
//    public static ItemRBMKPellet rbmk_pellet_lecm;
//    public static ItemRBMKPellet rbmk_pellet_mecm;
//    public static ItemRBMKPellet rbmk_pellet_hecm;
//    public static ItemRBMKPellet rbmk_pellet_lecf;
//    public static ItemRBMKPellet rbmk_pellet_mecf;
//    public static ItemRBMKPellet rbmk_pellet_hecf;

    public static RegistryObject<Item> WATZ_PELLET;
    public static RegistryObject<Item> WATZ_PELLET_DEPLETED;

    public static RegistryObject<Item> ICF_PELLET_EMPTY;
    public static RegistryObject<Item> ICF_PELLET;
    public static RegistryObject<Item> ICF_PELLET_DEPLETED;

    public static RegistryObject<Item> SCRAP_PLASTIC;
    public static final RegistryObject<Item> SCRAP = parts("scrap", () -> new ItemFuel(new Item.Properties(), 200 / 4), HBMKey.ORDERLY_GEN);
    public static RegistryObject<Item> SCRAP_OIL;
    public static RegistryObject<Item> SCRAP_NUCLEAR;
    public static RegistryObject<Item> TRINITITE;
    public static RegistryObject<Item> NUCLEAR_WASTE_LONG;
    public static RegistryObject<Item> NUCLEAR_WASTE_LONG_TINY;
    public static RegistryObject<Item> NUCLEAR_WASTE_SHORT;
    public static RegistryObject<Item> NUCLEAR_WASTE_SHORT_TINY;
    public static RegistryObject<Item> NUCLEAR_WASTE_LONG_DEPLETED;
    public static RegistryObject<Item> NUCLEAR_WASTE_LONG_DEPLETED_TINY;
    public static RegistryObject<Item> NUCLEAR_WASTE_SHORT_DEPLETED;
    public static RegistryObject<Item> NUCLEAR_WASTE_SHORT_DEPLETED_TINY;
    public static RegistryObject<Item> NUCLEAR_WASTE;
    public static RegistryObject<Item> NUCLEAR_WASTE_TINY;
    public static RegistryObject<Item> NUCLEAR_WASTE_VITRIFIED;
    public static RegistryObject<Item> NUCLEAR_WASTE_VITRIFIED_TINY;

    public static RegistryObject<Item> DEBRIS_GRAPHITE;
    public static RegistryObject<Item> DEBRIS_METAL;
    public static RegistryObject<Item> DEBRIS_FUEL;
    public static RegistryObject<Item> DEBRIS_CONCRETE;
    public static RegistryObject<Item> DEBRIS_EXCHANGER;
    public static RegistryObject<Item> DEBRIS_SHRAPNEL;
    public static RegistryObject<Item> DEBRIS_ELEMENT;

    public static RegistryObject<Item> CONTAINMENT_BOX;
    public static RegistryObject<Item> PLASTIC_BAG;

    public static RegistryObject<Item> AMMO_BAG;
    public static RegistryObject<Item> AMMO_BAG_INFINITE;
    public static RegistryObject<Item> CASING_BAG;

    public static RegistryObject<Item> CORDITE;
    public static RegistryObject<Item> BALLISTITE;
    public static RegistryObject<Item> BALL_DYNAMITE;
    public static RegistryObject<Item> BALL_TNT;
    public static RegistryObject<Item> BALL_TATB;
    public static RegistryObject<Item> BALL_FIRECLAY;
    public static RegistryObject<Item> AMMONIUM_NITRATE;
    public static RegistryObject<Item> BALL_FERRIC_CLAY;

    public static RegistryObject<Item> PELLET_CLUSTER;
    public static final RegistryObject<Item> POWDER_FIRE = parts("powder_red_phosphorus", ()->new ItemFuel(new Item.Properties(), 6400), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static RegistryObject<Item> POWDER_ICE;
    public static RegistryObject<Item> POWDER_POISON;
    public static RegistryObject<Item> POWDER_THERMITE;
    public static RegistryObject<Item> PELLET_GAS;
    public static RegistryObject<Item> MAGNETRON;
    public static RegistryObject<Item> PELLET_BUCKSHOT;
    public static RegistryObject<Item> PELLET_CHARGED;

    public static RegistryObject<Item> RANGEFINDER;
    public static final RegistryObject<Item> DESIGNATOR = control("designator",()->new ItemDesignator(new Item.Properties().stacksTo(1)), HBMKey.ORDERLY_GEN);
    public static RegistryObject<Item> DESIGNATOR_RANGE;
    public static RegistryObject<Item> DESIGNATOR_MANUAL;
    public static RegistryObject<Item> DESIGNATOR_ARTY_RANGE;
    public static RegistryObject<Item> LINKER;
    public static RegistryObject<Item> REACTOR_SENSOR;
    public static RegistryObject<Item> OIL_DETECTOR;
    public static final RegistryObject<Item> DOSIMETER = control("dosimeter", () -> new ItemDosimeter(new Item.Properties()), HBMKey.GEN_STANDALONE);
    public static final RegistryObject<Item> GEIGER_COUNTER = control("geiger_counter_hand", ()->new ItemGeigerCounter(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> DIGAMMA_DIAGNOSTIC = control("digamma_diagnostic", () -> new ItemDigammaDiagnostic(new Item.Properties()), HBMKey.GEN_STANDALONE);
    public static final RegistryObject<Item> POLLUTION_DETECTOR = control("pollution_detector", () -> new PollutionDetectorItem(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static RegistryObject<Item> ORE_DENSITY_SCANNER;
    public static RegistryObject<Item> SURVEY_SCANNER;
    public static RegistryObject<Item> MIRROR_TOOL = consumable("mirror_tool", ()->new ItemMirrorTool(new Item.Properties().stacksTo(1)), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> RBMK_TOOL = control("rbmk_tool", () -> new ItemRBMKTool(new Item.Properties()), HBMKey.GEN_STANDALONE);
    public static RegistryObject<Item> COLTAN_TOOL;
    public static RegistryObject<Item> POWER_NET_TOOL;
    public static RegistryObject<Item> ANALYSIS_TOOL;
    public static RegistryObject<Item> COUPLING_TOOL;
    public static RegistryObject<Item> DRONE_LINKER;
    public static RegistryObject<Item> RADAR_LINKER;
    public static RegistryObject<Item> SETTINGS_TOOL;
    public static RegistryObject<Item> ATMOSPHERE_SCANNER;
    public static RegistryObject<Item> RTTY_PAGER;

    public static RegistryObject<Item> BLUEPRINTS;
    public static RegistryObject<Item> BLUEPRINT_FOLDER;
    public static RegistryObject<Item> FLUID_IDENTIFIER_MULTI;
    public static RegistryObject<Item> FLUID_ICON;
    public static RegistryObject<Item> SIREN_TRACK;
    public static RegistryObject<Item> FLUID_DUCT;

    public static RegistryObject<Item> BOBMAZON;
    public static RegistryObject<Item> BOBMAZON_HIDDEN;

    public static RegistryObject<Item> LAUNCH_CODE_PIECE;
    public static RegistryObject<Item> LAUNCH_CODE;
    public static RegistryObject<Item> LAUNCH_KEY;

    public static RegistryObject<Item> TRANSPORTER_LINKER;

    public static RegistryObject<Item> MISSILE_ASSEMBLY;
    public static final RegistryObject<Item> MISSILE_GENERIC = ITEMS.register("missile_generic",()->new ItemMissile(new Item.Properties(), ItemMissile.MissileFormFactor.V2, ItemMissile.MissileTier.TIER1,
            (level, x, y, z, target) -> EntityMissileTier0.EntityMissileTest.create(level, x, y, z, target, EntityMissileTier0.EntityMissileTest.Payload.GENERIC)));
    public static RegistryObject<Item> MISSILE_ANTI_BALLISTIC;
    public static RegistryObject<Item> MISSILE_INCENDIARY;
    public static RegistryObject<Item> MISSILE_CLUSTER;
    public static RegistryObject<Item> MISSILE_BUSTER;
    public static RegistryObject<Item> MISSILE_DECOY;
    public static RegistryObject<Item> MISSILE_STRONG;
    public static RegistryObject<Item> MISSILE_INCENDIARY_STRONG;
    public static RegistryObject<Item> MISSILE_CLUSTER_STRONG;
    public static RegistryObject<Item> MISSILE_BUSTER_STRONG;
    public static RegistryObject<Item> MISSILE_EMP_STRONG;
    public static RegistryObject<Item> MISSILE_BURST;
    public static RegistryObject<Item> MISSILE_INFERNO;
    public static RegistryObject<Item> MISSILE_RAIN;
    public static RegistryObject<Item> MISSILE_DRILL;
    public static final RegistryObject<Item> MISSILE_NUCLEAR = new WrappedItemRegistryBuilder("missile_nuclear", () -> new ItemMissile(new Item.Properties(), ItemMissile.MissileFormFactor.ATLAS, ItemMissile.MissileTier.TIER4,
            (level, x, y, z, target) -> EntityMissileTier0.EntityMissileTest.create(level, x, y, z, target, EntityMissileTier0.EntityMissileTest.Payload.NUCLEAR)).setModel(() -> Models.getEntityModel(Models.MISSILE_NUKE)))
            .model(HBMKey.MODEL_EXISTING_FILE).build();
    public static RegistryObject<Item> MISSILE_NUCLEAR_CLUSTER;
    public static RegistryObject<Item> MISSILE_VOLCANO;
    public static RegistryObject<Item> MISSILE_DOOMSDAY;
    public static RegistryObject<Item> MISSILE_DOOMSDAY_RUSTED;
    public static RegistryObject<Item> MISSILE_TAINT;
    public static RegistryObject<Item> MISSILE_MICRO;
    public static RegistryObject<Item> MISSILE_BHOLE;
    public static RegistryObject<Item> MISSILE_SCHRABIDIUM;
    public static RegistryObject<Item> MISSILE_EMP;
    public static RegistryObject<Item> MISSILE_SHUTTLE;
    public static RegistryObject<Item> MISSILE_STEALTH;
    public static RegistryObject<Item> MISSILE_TEST;

    public static RegistryObject<Item> MP_THRUSTER_10_KEROSENE;
    public static RegistryObject<Item> MP_THRUSTER_10_SOLID;
    public static RegistryObject<Item> MP_THRUSTER_10_HYDRAZINE;
    public static RegistryObject<Item> MP_THRUSTER_10_XENON;
    public static RegistryObject<Item> MP_THRUSTER_15_KEROSENE;
    public static RegistryObject<Item> MP_THRUSTER_15_KEROSENE_DUAL;
    public static RegistryObject<Item> MP_THRUSTER_15_KEROSENE_TRIPLE;
    public static RegistryObject<Item> MP_THRUSTER_15_SOLID;
    public static RegistryObject<Item> MP_THRUSTER_15_SOLID_HEXDECUPLE;
    public static RegistryObject<Item> MP_THRUSTER_15_HYDROGEN;
    public static RegistryObject<Item> MP_THRUSTER_15_HYDROGEN_DUAL;
    public static RegistryObject<Item> MP_THRUSTER_15_BALEFIRE_SHORT;
    public static RegistryObject<Item> MP_THRUSTER_15_BALEFIRE;
    public static RegistryObject<Item> MP_THRUSTER_15_BALEFIRE_LARGE;
    public static RegistryObject<Item> MP_THRUSTER_15_BALEFIRE_LARGE_RAD;
    public static RegistryObject<Item> MP_THRUSTER_20_KEROSENE;
    public static RegistryObject<Item> MP_THRUSTER_20_KEROSENE_DUAL;
    public static RegistryObject<Item> MP_THRUSTER_20_KEROSENE_TRIPLE;
    public static RegistryObject<Item> MP_THRUSTER_20_METHALOX;
    public static RegistryObject<Item> MP_THRUSTER_20_METHALOX_DUAL;
    public static RegistryObject<Item> MP_THRUSTER_20_METHALOX_TRIPLE;
    public static RegistryObject<Item> MP_THRUSTER_20_HYDROGEN;
    public static RegistryObject<Item> MP_THRUSTER_20_HYDROGEN_DUAL;
    public static RegistryObject<Item> MP_THRUSTER_20_HYDROGEN_TRIPLE;
    public static RegistryObject<Item> MP_THRUSTER_20_SOLID;
    public static RegistryObject<Item> MP_THRUSTER_20_SOLID_MULTI;
    public static RegistryObject<Item> MP_THRUSTER_20_SOLID_MULTIER;
    public static RegistryObject<Item> MP_THRUSTER_20_HYDRAZINE;

    public static RegistryObject<Item> MP_STABILITY_10_FLAT;
    public static RegistryObject<Item> MP_STABILITY_10_CRUISE;
    public static RegistryObject<Item> MP_STABILITY_10_SPACE;
    public static RegistryObject<Item> MP_STABILITY_15_FLAT;
    public static RegistryObject<Item> MP_STABILITY_15_THIN;
    public static RegistryObject<Item> MP_STABILITY_15_SOYUZ;
    public static RegistryObject<Item> MP_STABILITY_20_FLAT;

    public static RegistryObject<Item> MP_FUSELAGE_10_KEROSENE;
    public static RegistryObject<Item> MP_FUSELAGE_10_KEROSENE_CAMO;
    public static RegistryObject<Item> MP_FUSELAGE_10_KEROSENE_DESERT;
    public static RegistryObject<Item> MP_FUSELAGE_10_KEROSENE_SKY;
    public static RegistryObject<Item> MP_FUSELAGE_10_KEROSENE_FLAMES;
    public static RegistryObject<Item> MP_FUSELAGE_10_KEROSENE_INSULATION;
    public static RegistryObject<Item> MP_FUSELAGE_10_KEROSENE_SLEEK;
    public static RegistryObject<Item> MP_FUSELAGE_10_KEROSENE_METAL;
    public static RegistryObject<Item> MP_FUSELAGE_10_KEROSENE_TAINT;

    public static RegistryObject<Item> MP_FUSELAGE_10_SOLID;
    public static RegistryObject<Item> MP_FUSELAGE_10_SOLID_FLAMES;
    public static RegistryObject<Item> MP_FUSELAGE_10_SOLID_INSULATION;
    public static RegistryObject<Item> MP_FUSELAGE_10_SOLID_SLEEK;
    public static RegistryObject<Item> MP_FUSELAGE_10_SOLID_SOVIET_GLORY;
    public static RegistryObject<Item> MP_FUSELAGE_10_SOLID_CATHEDRAL;
    public static RegistryObject<Item> MP_FUSELAGE_10_SOLID_MOONLIT;
    public static RegistryObject<Item> MP_FUSELAGE_10_SOLID_BATTERY;
    public static RegistryObject<Item> MP_FUSELAGE_10_SOLID_DURACELL;

    public static RegistryObject<Item> MP_FUSELAGE_10_HYDRAZINE;

    public static RegistryObject<Item> MP_FUSELAGE_10_XENON;
    public static RegistryObject<Item> MP_FUSELAGE_10_XENON_BHOLE;

    public static RegistryObject<Item> MP_FUSELAGE_10_LONG_KEROSENE;
    public static RegistryObject<Item> MP_FUSELAGE_10_LONG_KEROSENE_CAMO;
    public static RegistryObject<Item> MP_FUSELAGE_10_LONG_KEROSENE_DESERT;
    public static RegistryObject<Item> MP_FUSELAGE_10_LONG_KEROSENE_SKY;
    public static RegistryObject<Item> MP_FUSELAGE_10_LONG_KEROSENE_FLAMES;
    public static RegistryObject<Item> MP_FUSELAGE_10_LONG_KEROSENE_INSULATION;
    public static RegistryObject<Item> MP_FUSELAGE_10_LONG_KEROSENE_SLEEK;
    public static RegistryObject<Item> MP_FUSELAGE_10_LONG_KEROSENE_METAL;
    public static RegistryObject<Item> MP_FUSELAGE_10_LONG_KEROSENE_TAINT;
    public static RegistryObject<Item> MP_FUSELAGE_10_LONG_KEROSENE_DASH;
    public static RegistryObject<Item> MP_FUSELAGE_10_LONG_KEROSENE_VAP;

    public static RegistryObject<Item> MP_FUSELAGE_10_LONG_SOLID;
    public static RegistryObject<Item> MP_FUSELAGE_10_LONG_SOLID_FLAMES;
    public static RegistryObject<Item> MP_FUSELAGE_10_LONG_SOLID_INSULATION;
    public static RegistryObject<Item> MP_FUSELAGE_10_LONG_SOLID_SLEEK;
    public static RegistryObject<Item> MP_FUSELAGE_10_LONG_SOLID_SOVIET_GLORY;
    public static RegistryObject<Item> MP_FUSELAGE_10_LONG_SOLID_BULLET;
    public static RegistryObject<Item> MP_FUSELAGE_10_LONG_SOLID_SILVERMOONLIGHT;

    public static RegistryObject<Item> MP_FUSELAGE_10_15_KEROSENE;
    public static RegistryObject<Item> MP_FUSELAGE_10_15_SOLID;
    public static RegistryObject<Item> MP_FUSELAGE_10_15_HYDROGEN;
    public static RegistryObject<Item> MP_FUSELAGE_10_15_BALEFIRE;

    public static RegistryObject<Item> MP_FUSELAGE_15_KEROSENE;
    public static RegistryObject<Item> MP_FUSELAGE_15_KEROSENE_CAMO;
    public static RegistryObject<Item> MP_FUSELAGE_15_KEROSENE_DESERT;
    public static RegistryObject<Item> MP_FUSELAGE_15_KEROSENE_SKY;
    public static RegistryObject<Item> MP_FUSELAGE_15_KEROSENE_INSULATION;
    public static RegistryObject<Item> MP_FUSELAGE_15_KEROSENE_METAL;
    public static RegistryObject<Item> MP_FUSELAGE_15_KEROSENE_DECORATED;
    public static RegistryObject<Item> MP_FUSELAGE_15_KEROSENE_STEAMPUNK;
    public static RegistryObject<Item> MP_FUSELAGE_15_KEROSENE_POLITE;
    public static RegistryObject<Item> MP_FUSELAGE_15_KEROSENE_BLACKJACK;
    public static RegistryObject<Item> MP_FUSELAGE_15_KEROSENE_LAMBDA;
    public static RegistryObject<Item> MP_FUSELAGE_15_KEROSENE_MINUTEMAN;
    public static RegistryObject<Item> MP_FUSELAGE_15_KEROSENE_PIP;
    public static RegistryObject<Item> MP_FUSELAGE_15_KEROSENE_TAINT;
    public static RegistryObject<Item> MP_FUSELAGE_15_KEROSENE_YUCK;

    public static RegistryObject<Item> MP_FUSELAGE_15_SOLID;
    public static RegistryObject<Item> MP_FUSELAGE_15_SOLID_INSULATION;
    public static RegistryObject<Item> MP_FUSELAGE_15_SOLID_DESH;
    public static RegistryObject<Item> MP_FUSELAGE_15_SOLID_SOVIET_GLORY;
    public static RegistryObject<Item> MP_FUSELAGE_15_SOLID_SOVIET_STANK;
    public static RegistryObject<Item> MP_FUSELAGE_15_SOLID_FAUST;
    public static RegistryObject<Item> MP_FUSELAGE_15_SOLID_SILVERMOONLIGHT;
    public static RegistryObject<Item> MP_FUSELAGE_15_SOLID_SNOWY;
    public static RegistryObject<Item> MP_FUSELAGE_15_SOLID_PANORAMA;
    public static RegistryObject<Item> MP_FUSELAGE_15_SOLID_ROSES;
    public static RegistryObject<Item> MP_FUSELAGE_15_SOLID_MIMI;

    public static RegistryObject<Item> MP_FUSELAGE_15_HYDROGEN;
    public static RegistryObject<Item> MP_FUSELAGE_15_HYDROGEN_CATHEDRAL;

    public static RegistryObject<Item> MP_FUSELAGE_15_BALEFIRE;

    public static RegistryObject<Item> MP_FUSELAGE_15_20_KEROSENE;
    public static RegistryObject<Item> MP_FUSELAGE_15_20_KEROSENE_MAGNUSSON;
    public static RegistryObject<Item> MP_FUSELAGE_15_20_SOLID;

    public static RegistryObject<Item> MP_WARHEAD_10_HE;
    public static RegistryObject<Item> MP_WARHEAD_10_INCENDIARY;
    public static RegistryObject<Item> MP_WARHEAD_10_BUSTER;
    public static RegistryObject<Item> MP_WARHEAD_10_NUCLEAR;
    public static RegistryObject<Item> MP_WARHEAD_10_NUCLEAR_LARGE;
    public static RegistryObject<Item> MP_WARHEAD_10_TAINT;
    public static RegistryObject<Item> MP_WARHEAD_10_CLOUD;
    public static RegistryObject<Item> MP_WARHEAD_15_HE;
    public static RegistryObject<Item> MP_WARHEAD_15_INCENDIARY;
    public static RegistryObject<Item> MP_WARHEAD_15_NUCLEAR;
    public static RegistryObject<Item> MP_WARHEAD_15_NUCLEAR_SHARK;
    public static RegistryObject<Item> MP_WARHEAD_15_NUCLEAR_MIMI;
    public static RegistryObject<Item> MP_WARHEAD_15_BOXCAR;
    public static RegistryObject<Item> MP_WARHEAD_15_N2;
    public static RegistryObject<Item> MP_WARHEAD_15_BALEFIRE;
    public static RegistryObject<Item> MP_WARHEAD_15_TURBINE;

    public static RegistryObject<Item> MP_CHIP_1;
    public static RegistryObject<Item> MP_CHIP_2;
    public static RegistryObject<Item> MP_CHIP_3;
    public static RegistryObject<Item> MP_CHIP_4;
    public static RegistryObject<Item> MP_CHIP_5;

    public static RegistryObject<Item> RP_FUSELAGE_20_12;
    public static RegistryObject<Item> RP_FUSELAGE_20_6;
    public static RegistryObject<Item> RP_FUSELAGE_20_3;
    public static RegistryObject<Item> RP_FUSELAGE_20_1;
    public static RegistryObject<Item> RP_FUSELAGE_20_12_HYDRAZINE;

    public static RegistryObject<Item> RP_LEGS_20;

    public static RegistryObject<Item> RP_CAPSULE_20;
    public static RegistryObject<Item> RP_STATION_CORE_20;
    public static RegistryObject<Item> RP_POD_20;

    public static RegistryObject<Item> MISSILE_CUSTOM;
    public static RegistryObject<Item> ROCKET_CUSTOM;

    public static RegistryObject<Item> MISSILE_SOYUZ;
    public static RegistryObject<Item> MISSILE_SOYUZ_LANDER;
    // public static RegistryObject<Item> SATELLITE;
    public static RegistryObject<Item> SAT_CHIP;
    public static RegistryObject<Item> SAT_MAPPER;
    public static RegistryObject<Item> SAT_SCANNER;
    public static RegistryObject<Item> SAT_RADAR;
    public static RegistryObject<Item> SAT_LASER;
    public static RegistryObject<Item> SAT_FOEQ;
    public static RegistryObject<Item> SAT_RESONATOR;
    public static RegistryObject<Item> SAT_MINER;
    public static RegistryObject<Item> SAT_LUNAR_MINER;
    public static RegistryObject<Item> SAT_GERALD;
    public static RegistryObject<Item> SAT_DYSON_RELAY;
    public static RegistryObject<Item> SAT_WAR;
    public static RegistryObject<Item> SAT_PRECISION_LASER;
    public static RegistryObject<Item> SAT_DETECTOR;
    public static RegistryObject<Item> SAT_RAY_SCAN;

    public static RegistryObject<Item> SAT_COORD;
    public static RegistryObject<Item> SAT_DESIGNATOR;
    public static RegistryObject<Item> SAT_RELAY;

//    public static ItemEnumMulti ammo_misc;
//    public static ItemEnumMulti ammo_shell;
//    public static ItemEnumMulti ammo_fireext;

    public static RegistryObject<Item> AMMO_DGK;
    public static RegistryObject<Item> AMMO_ARTY;
    public static RegistryObject<Item> AMMO_HIMARS;

    public static final RegistryObject<Item> GUN_B92 = gun("gun_b92", () -> new ItemGun(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> GUN_B92_AMMO = gun("gun_b92_ammo", () -> new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static RegistryObject<Item> GUN_FIREEXT;

    public static RegistryObject<Item> GUN_DEBUG;
    public static RegistryObject<Item> AMMO_DEBUG;

    public static RegistryObject<Item> GUN_PEPPERBOX;
    public static RegistryObject<Item> GUN_LIGHT_REVOLVER;
    public static RegistryObject<Item> GUN_LIGHT_REVOLVER_ATLAS;
    public static RegistryObject<Item> GUN_LIGHT_REVOLVER_DANI;
    public static RegistryObject<Item> GUN_HENRY;
    public static RegistryObject<Item> GUN_HENRY_LINCOLN;
    public static RegistryObject<Item> GUN_GREASEGUN;
    public static RegistryObject<Item> GUN_MARESLEG;
    public static RegistryObject<Item> GUN_MARESLEG_AKIMBO;
    public static RegistryObject<Item> GUN_MARESLEG_BROKEN;
    public static RegistryObject<Item> GUN_FLAREGUN;
    public static RegistryObject<Item> GUN_HEAVY_REVOLVER;
    public static RegistryObject<Item> GUN_HEAVY_REVOLVER_LILMAC;
    public static RegistryObject<Item> GUN_HEAVY_REVOLVER_PROTEGE;
    public static RegistryObject<Item> GUN_CARBINE;
    public static RegistryObject<Item> GUN_AM180;
    public static RegistryObject<Item> GUN_LIBERATOR;
    public static RegistryObject<Item> GUN_CONGOLAKE;
    public static RegistryObject<Item> GUN_FLAMER;
    public static RegistryObject<Item> GUN_FLAMER_TOPAZ;
    public static RegistryObject<Item> GUN_FLAMER_DAYBREAKER;
    public static RegistryObject<Item> GUN_UZI;
    public static RegistryObject<Item> GUN_UZI_AKIMBO;
    public static RegistryObject<Item> GUN_SPAS12;
    public static RegistryObject<Item> GUN_PANZERSCHRECK;
    public static RegistryObject<Item> GUN_STAR_F;
    public static RegistryObject<Item> GUN_STAR_F_AKIMBO;
    public static RegistryObject<Item> GUN_G3;
    public static RegistryObject<Item> GUN_G3_ZEBRA;
    public static RegistryObject<Item> GUN_STINGER;
    public static RegistryObject<Item> GUN_MK108;
    public static RegistryObject<Item> GUN_CHEMTHROWER;
    public static RegistryObject<Item> GUN_AMAT;
    public static RegistryObject<Item> GUN_AMAT_SUBTLETY;
    public static RegistryObject<Item> GUN_AMAT_PENANCE;
    public static RegistryObject<Item> GUN_M2;
    public static RegistryObject<Item> GUN_AUTOSHOTGUN;
    public static RegistryObject<Item> GUN_AUTOSHOTGUN_SHREDDER;
    public static RegistryObject<Item> GUN_AUTOSHOTGUN_SEXY;
    public static RegistryObject<Item> GUN_AUTOSHOTGUN_HERETIC;
    public static RegistryObject<Item> GUN_QUADRO;
    public static RegistryObject<Item> GUN_LAG;
    public static RegistryObject<Item> GUN_MINIGUN;
    public static RegistryObject<Item> GUN_MINIGUN_DUAL;
    public static RegistryObject<Item> GUN_MINIGUN_LACUNAE;
    public static RegistryObject<Item> GUN_MISSILE_LAUNCHER;
    public static RegistryObject<Item> GUN_TESLA_CANNON;
    public static RegistryObject<Item> GUN_LASER_PISTOL;
    public static RegistryObject<Item> GUN_LASER_PISTOL_PEW_PEW;
    public static RegistryObject<Item> GUN_LASER_PISTOL_MORNING_GLORY;
    public static RegistryObject<Item> GUN_STG77;
    public static RegistryObject<Item> GUN_TAU;
    public static RegistryObject<Item> GUN_FATMAN;
    public static RegistryObject<Item> GUN_LASRIFLE;
    public static RegistryObject<Item> GUN_COILGUN;
    public static RegistryObject<Item> GUN_HANGMAN;
    public static RegistryObject<Item> GUN_MAS36;
    public static RegistryObject<Item> GUN_BOLTER;
    public static RegistryObject<Item> GUN_FOLLY;
    public static RegistryObject<Item> GUN_ABERRATOR;
    public static RegistryObject<Item> GUN_ABERRATOR_EOTT;
    public static RegistryObject<Item> GUN_DOUBLE_BARREL;
    public static RegistryObject<Item> GUN_DOUBLE_BARREL_SACRED_DRAGON;
    public static RegistryObject<Item> GUN_N_I_4_N_I;
    public static RegistryObject<Item> GUN_CHARGE_THROWER;
    public static RegistryObject<Item> GUN_DRILL;
    public static RegistryObject<Item> GUN_PA_MELEE;
    public static RegistryObject<Item> GUN_PA_RANGED;

    public static RegistryObject<Item> AMMO_STANDARD;
    public static RegistryObject<Item> AMMO_SECRET;

    public static RegistryObject<Item> WEAPON_MOD_TEST;
    public static RegistryObject<Item> WEAPON_MOD_GENERIC;
    public static RegistryObject<Item> WEAPON_MOD_SPECIAL;
    public static RegistryObject<Item> WEAPON_MOD_CALIBER;

    public static RegistryObject<Item> CRUCIBLE;

    public static final RegistryObject<Item> STICK_DYNAMITE = legacyGrenade("stick_dynamite", ItemGrenade.Type.BURST);
    public static final RegistryObject<Item> STICK_DYNAMITE_FISHING = legacyGrenade("stick_dynamite_fishing", ItemGrenade.Type.BURST);
    public static final RegistryObject<Item> STICK_TNT = legacyGrenade("stick_tnt", ItemGrenade.Type.IF_HE);
    public static final RegistryObject<Item> STICK_SEMTEX = legacyGrenade("stick_semtex", ItemGrenade.Type.IF_STICKY);
    public static final RegistryObject<Item> STICK_C4 = legacyGrenade("stick_c4", ItemGrenade.Type.BREACH);

    public static RegistryObject<Item> GRENADE_SHELL;
    public static RegistryObject<Item> GRENADE_FILLING;
    public static RegistryObject<Item> GRENADE_FUZE;
    public static RegistryObject<Item> GRENADE_EXTRA;
    public static RegistryObject<Item> GRENADE_UNIVERSAL;

    public static final RegistryObject<Item> ULLAPOOL_CABER = legacyGrenade("ullapool_caber", ItemGrenade.Type.BURST);

    public static final RegistryObject<Item> WEAPONIZED_STARBLASTER_CELL = legacyGrenade("weaponized_starblaster_cell", ItemGrenade.Type.PLASMA);

    public static RegistryObject<Item> BOMB_WAFFLE;
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
    public static RegistryObject<Item> APPLE_LEAD;
    public static RegistryObject<Item> APPLE_SCHRABIDIUM;
    public static final RegistryObject<Item> TEM_FLAKES = consumable("tem_flakes",
            TemFlakesItem::new,
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
    public static final RegistryObject<Item> LEMON = consumable("lemon",
            () -> LegacyConsumableItem.builder(3, 0.5F)
                    .tooltip("item.hbm.lemon.desc")
                    .build(),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> DEFINITELYFOOD = consumable("definitelyfood", () -> LegacyConsumableItem.builder(3, 0.5F).build(), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
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
    public static final RegistryObject<Item> SPONGEBOB_MACARONI = consumable("spongebob_macaroni", () -> LegacyConsumableItem.builder(5, 1.0F).build(), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> FOODITEM = consumable("fooditem", () -> LegacyConsumableItem.builder(2, 5.0F).build(), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> TWINKIE = consumable("twinkie",
            () -> LegacyConsumableItem.builder(3, 0.25F)
                    .tooltip("item.hbm.twinkie.desc")
                    .build(),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> STATIC_SANDWICH = consumable("static_sandwich",
            () -> LegacyConsumableItem.builder(6, 1.0F).build(),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PUDDING = consumable("pudding",
            () -> LegacyConsumableItem.builder(6, 1.0F)
                    .tooltip("item.hbm.pudding.desc1", "item.hbm.pudding.desc2", "item.hbm.pudding.desc3")
                    .build(),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> PANCAKE = consumable("pancake", () -> LegacyConsumableItem.builder(20, 20.0F).build(), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> NUGGET = consumable("nugget", () -> LegacyConsumableItem.builder(200, 1.0F).build(), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static RegistryObject<Item> PEAS;
    public static RegistryObject<Item> MARSHMALLOW;
    public static final RegistryObject<Item> CHEESE = consumable("cheese",
            () -> LegacyConsumableItem.builder(5, 0.75F).build(),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> QUESADILLA = consumable("quesadilla",
            () -> LegacyConsumableItem.builder(8, 1.0F).build(),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> GLYPHID_MEAT = consumable("glyphid_meat", () -> LegacyConsumableItem.builder(3, 0.5F).meat().build(), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> GLYPHID_MEAT_GRILLED = consumable("glyphid_meat_grilled", () -> LegacyConsumableItem.builder(8, 0.75F).meat().effect(() -> MobEffects.DAMAGE_BOOST, 180, 1, 1.0F).build(), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> EGG_GLYPHID = parts("egg_glyphid_base",()->new ItemEggGlyphid(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static RegistryObject<Item> FLOUR;

    public static RegistryObject<Item> MED_IPECAC;
    public static RegistryObject<Item> MED_PTSD;
    public static RegistryObject<Item> MED_SCHIZOPHRENIA;

    public static RegistryObject<Item> CANTEEN_VODKA;

    public static RegistryObject<Item> DEFUSER;
    public static RegistryObject<Item> REACHER;
    public static RegistryObject<Item> BISMUTH_TOOL;
    public static RegistryObject<Item> MELTDOWN_TOOL;

    public static RegistryObject<Item> FLAME_PONY;
    public static RegistryObject<Item> FLAME_CONSPIRACY;
    public static RegistryObject<Item> FLAME_POLITICS;
    public static RegistryObject<Item> FLAME_OPINION;

    //public static RegistryObject<Item> GADGET_EXPLOSIVE;
    public static RegistryObject<Item> EARLY_EXPLOSIVE_LENSES;
    public static RegistryObject<Item> EXPLOSIVE_LENSES;
    public static RegistryObject<Item> GADGET_WIREING;
    public static RegistryObject<Item> GADGET_CORE;
    public static RegistryObject<Item> BOY_IGNITER;
    public static RegistryObject<Item> BOY_PROPELLANT;
    public static RegistryObject<Item> BOY_BULLET;
    public static RegistryObject<Item> BOY_TARGET;
    public static RegistryObject<Item> BOY_SHIELDING;
    //public static RegistryObject<Item> MAN_EXPLOSIVE;
    public static RegistryObject<Item> MAN_IGNITER;
    public static RegistryObject<Item> MAN_CORE;
    public static RegistryObject<Item> MIKE_CORE;
    public static RegistryObject<Item> MIKE_DEUT;
    public static RegistryObject<Item> MIKE_COOLING_UNIT;
    public static RegistryObject<Item> TSAR_CORE;
    public static RegistryObject<Item> FLEIJA_IGNITER;
    public static RegistryObject<Item> FLEIJA_PROPELLANT;
    public static RegistryObject<Item> FLEIJA_CORE;
    public static RegistryObject<Item> SOLINIUM_IGNITER;
    public static RegistryObject<Item> SOLINIUM_PROPELLANT;
    public static RegistryObject<Item> SOLINIUM_CORE;
    public static RegistryObject<Item> N2_CHARGE;
    public static RegistryObject<Item> EGG_BALEFIRE_SHARD;
    public static RegistryObject<Item> EGG_BALEFIRE;

    public static RegistryObject<Item> CUSTOM_TNT;
    public static RegistryObject<Item> CUSTOM_NUKE;
    public static RegistryObject<Item> CUSTOM_HYDRO;
    public static RegistryObject<Item> CUSTOM_AMAT;
    public static RegistryObject<Item> CUSTOM_DIRTY;
    public static RegistryObject<Item> CUSTOM_SCHRAB;
    public static RegistryObject<Item> CUSTOM_FALL;
    public static RegistryObject<Item> BATTERY_SPARK = nuke("battery_spark", ()->new Item(new Item.Properties().stacksTo(1)), HBMKey.REVERSE_GEN);
    public static RegistryObject<Item> BATTERY_TRIXITE= nuke("battery_trixite", ()->new Item(new Item.Properties().stacksTo(1)), HBMKey.REVERSE_GEN);

    public static RegistryObject<Item> BATTERY_PACK;
    public static final RegistryObject<Item> BATTERY_CREATIVE = control("battery_creative",()->new ItemBatteryCreative(new Item.Properties().stacksTo(1)), HBMKey.REVERSE_GEN, ModTags.Items.BATTERY);
    public static RegistryObject<Item> CUBE_POWER;
    public static RegistryObject<Item> CORE_ANGEL;

    public static RegisterObjectCollection<Item, ItemBatterySC.EnumBatterySC> BATTERY_SC = new RegisterObjectCollection<>(ItemBatterySC.EnumBatterySC.class, type ->
            new WrappedItemRegistryBuilder("battery_sc." + type.toString().toLowerCase(), ()->new ItemBatterySC(type, new Item.Properties().stacksTo(1)))
            .tab(ModTabs.CONTROL.getKey()).loc(HBMKey.ORDERLY_GEN_EXCEPT_FIRST).tags(ModTags.Items.BATTERY).build());

    public static RegistryObject<Item> BATTERY_POTATO = control("battery_potato", ()->new ItemBattery(1000, 0, 100, new Item.Properties().stacksTo(1)), HBMKey.REVERSE_GEN, ModTags.Items.BATTERY);
    public static RegistryObject<Item> BATTERY_POTATOS = control("battery_potatos", ()->new ItemPotatos(500000, 0, 100, new Item.Properties().stacksTo(1)), HBMKey.REVERSE_GEN, ModTags.Items.BATTERY);
    public static RegistryObject<Item> HEV_BATTERY;
    public static RegistryObject<Item> FUSION_CORE;
    public static RegistryObject<Item> FUSE;
    //by using these in crafting table recipes, i'm running the risk of making my recipes too greg-ian (which i don't like)
    //in the event that i forget about the meaning of the word "sparingly", please throw a brick at my head
    public static final RegistryObject<Item> SCREWDRIVER = control("screwdriver", ()->new ItemTooling(new Item.Properties().stacksTo(1).durability(100).setNoRepair(), ToolType.SCREWDRIVER), "Screw");
    public static RegistryObject<Item> SCREWDRIVER_DESH;
    public static final RegistryObject<Item> HAND_DRILL = control("hand_drill",
            () -> new HandDrillItem(100),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> HAND_DRILL_DESH = control("hand_drill_desh",
            () -> new HandDrillItem(0),
            HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static RegistryObject<Item> WRENCH_ARCHINEER;
    public static RegistryObject<Item> CHEMISTRY_SET;
    public static RegistryObject<Item> CHEMISTRY_SET_BORON;
    public static RegistryObject<Item> BLOWTORCH;
    public static RegistryObject<Item> ACETYLENE_TORCH;
    public static RegistryObject<Item> BOLTGUN;

    public static final RegisterObjectCollection<Item, ItemEnums.EnumElectrodeType> ARC_ELECTRODE
            = new RegisterObjectCollection<>(ItemEnums.EnumElectrodeType.class, type -> new WrappedItemRegistryBuilder("arc_electrode_" + type.toString().toLowerCase(), ()->new ItemElectrode(new Item.Properties().durability(type.durability)))
            .tab(ModTabs.PARTS.getKey()).loc(HBMKey.ORDERLY_GEN_EXCEPT_FIRST).model(itemModelGen -> itemModelGen.basicItem("arc_electrode_" + type.toString().toLowerCase(), HBM.rl("arc_electrode." + type.toString().toLowerCase()))).build());
    public static RegistryObject<Item> ARC_ELECTRODE_BURNT;

    public static RegistryObject<Item> UPGRADE_MUFFLER;

    public static RegistryObject<Item> UPGRADE_TEMPLATE;
    public static final RegistryObject<Item> UPGRADE_SPEED_1 = control("upgrade_speed_1", ()->new ItemMachineUpgrade(UpgradeType.SPEED, 1), "Speed Upgrade Tiler 1", ModTags.Items.UPGRADE);
    public static final RegistryObject<Item> UPGRADE_SPEED_2 = control("upgrade_speed_2", () -> new ItemMachineUpgrade(UpgradeType.SPEED, 2), "Speed Upgrade Tier 2", ModTags.Items.UPGRADE);
    public static final RegistryObject<Item> UPGRADE_SPEED_3 = control("upgrade_speed_3", () -> new ItemMachineUpgrade(UpgradeType.SPEED, 3), "Speed Upgrade Tier 3", ModTags.Items.UPGRADE);
    public static final RegistryObject<Item> UPGRADE_EFFECT_1 = control("upgrade_effect_1", () -> new ItemMachineUpgrade(UpgradeType.EFFECT, 1), "Effect Upgrade Tier 1", ModTags.Items.UPGRADE);
    public static final RegistryObject<Item> UPGRADE_EFFECT_2 = control("upgrade_effect_2", () -> new ItemMachineUpgrade(UpgradeType.EFFECT, 2), "Effect Upgrade Tier 2", ModTags.Items.UPGRADE);
    public static final RegistryObject<Item> UPGRADE_EFFECT_3 = control("upgrade_effect_3", () -> new ItemMachineUpgrade(UpgradeType.EFFECT, 3), "Effect Upgrade Tier 3", ModTags.Items.UPGRADE);
    public static final RegistryObject<Item> UPGRADE_POWER_1 = control("upgrade_power_1", () -> new ItemMachineUpgrade(UpgradeType.POWER, 1), "Power Upgrade Tier 1", ModTags.Items.UPGRADE);
    public static final RegistryObject<Item> UPGRADE_POWER_2 = control("upgrade_power_2", () -> new ItemMachineUpgrade(UpgradeType.POWER, 2), "Power Upgrade Tier 2", ModTags.Items.UPGRADE);
    public static final RegistryObject<Item> UPGRADE_POWER_3 = control("upgrade_power_3", () -> new ItemMachineUpgrade(UpgradeType.POWER, 3), "Power Upgrade Tier 3", ModTags.Items.UPGRADE);
    public static final RegistryObject<Item> UPGRADE_FORTUNE_1 = control("upgrade_fortune_1", () -> new ItemMachineUpgrade(UpgradeType.FORTUNE, 1), "Fortune Upgrade Tier 1", ModTags.Items.UPGRADE);
    public static final RegistryObject<Item> UPGRADE_FORTUNE_2 = control("upgrade_fortune_2", () -> new ItemMachineUpgrade(UpgradeType.FORTUNE, 2), "Fortune Upgrade Tier 2", ModTags.Items.UPGRADE);
    public static final RegistryObject<Item> UPGRADE_FORTUNE_3 = control("upgrade_fortune_3", () -> new ItemMachineUpgrade(UpgradeType.FORTUNE, 3), "Fortune Upgrade Tier 3", ModTags.Items.UPGRADE);
    public static final RegistryObject<Item> UPGRADE_AFTERBURN_1 = control("upgrade_afterburn_1", () -> new ItemMachineUpgrade(UpgradeType.AFTERBURN, 1), "Afterburn Upgrade Tier 1", ModTags.Items.UPGRADE);
    public static final RegistryObject<Item> UPGRADE_AFTERBURN_2 = control("upgrade_afterburn_2", () -> new ItemMachineUpgrade(UpgradeType.AFTERBURN, 2), "Afterburn Upgrade Tier 2", ModTags.Items.UPGRADE);
    public static final RegistryObject<Item> UPGRADE_AFTERBURN_3 = control("upgrade_afterburn_3", () -> new ItemMachineUpgrade(UpgradeType.AFTERBURN, 3), "Afterburn Upgrade Tier 3", ModTags.Items.UPGRADE);
    public static final RegistryObject<Item> UPGRADE_OVERDRIVE_1 = control("upgrade_overdrive_1", () -> new ItemMachineUpgrade(UpgradeType.OVERDRIVE, 1), "Overdrive Upgrade Tier 1", ModTags.Items.UPGRADE);
    public static final RegistryObject<Item> UPGRADE_OVERDRIVE_2 = control("upgrade_overdrive_2", () -> new ItemMachineUpgrade(UpgradeType.OVERDRIVE, 2), "Overdrive Upgrade Tier 2", ModTags.Items.UPGRADE);
    public static final RegistryObject<Item> UPGRADE_OVERDRIVE_3 = control("upgrade_overdrive_3", () -> new ItemMachineUpgrade(UpgradeType.OVERDRIVE, 3), "Overdrive Upgrade Tier 3", ModTags.Items.UPGRADE);
    public static final RegistryObject<Item> UPGRADE_RADIUS = control("upgrade_radius", () -> new ItemMachineUpgrade(new Item.Properties().stacksTo(16)), "Radius Upgrade", ModTags.Items.UPGRADE);
    public static final RegistryObject<Item> UPGRADE_HEALTH = control("upgrade_health", () -> new ItemMachineUpgrade(new Item.Properties().stacksTo(16)), "Health Upgrade", ModTags.Items.UPGRADE);
    public static final RegistryObject<Item> UPGRADE_SMELTER = control("upgrade_smelter", ItemMachineUpgrade::new, "Smelter Upgrade", ModTags.Items.UPGRADE);
    public static final RegistryObject<Item> UPGRADE_SHREDDER = control("upgrade_shredder", ItemMachineUpgrade::new, "Shredder Upgrade", ModTags.Items.UPGRADE);
    public static final RegistryObject<Item> UPGRADE_CENTRIFUGE = control("upgrade_centrifuge", ItemMachineUpgrade::new, "Centrifuge Upgrade", ModTags.Items.UPGRADE);
    public static final RegistryObject<Item> UPGRADE_CRYSTALLIZER = control("upgrade_crystallizer", ItemMachineUpgrade::new, "Crystallizer Upgrade", ModTags.Items.UPGRADE);
    public static final RegistryObject<Item> UPGRADE_NULLIFIER = control("upgrade_nullifier", ItemMachineUpgrade::new, "Nullifier Upgrade", ModTags.Items.UPGRADE);
    public static final RegistryObject<Item> UPGRADE_SCREM = control("upgrade_screm", ItemMachineUpgrade::new, "Screm Upgrade", ModTags.Items.UPGRADE);
    public static final RegistryObject<Item> UPGRADE_GC_SPEED = control("upgrade_gc_speed", ItemMachineUpgrade::new, "GC Speed Upgrade", ModTags.Items.UPGRADE);
    public static final RegistryObject<Item> UPGRADE_5G = control("upgrade_5g", ItemMachineUpgrade::new, "5G Upgrade", ModTags.Items.UPGRADE);
    public static final RegistryObject<Item> UPGRADE_STACK = control("upgrade_stack", () -> new ItemMachineUpgrade(UpgradeType.SPECIAL, 0), "Stack Upgrade", ModTags.Items.UPGRADE);
    public static final RegistryObject<Item> UPGRADE_EJECTOR = control("upgrade_ejector", () -> new ItemMachineUpgrade(UpgradeType.SPECIAL, 0), "Ejector Upgrade", ModTags.Items.UPGRADE);

    public static RegistryObject<Item> INGOT_EUPHEMIUM;
    public static RegistryObject<Item> NUGGET_EUPHEMIUM;
    public static RegistryObject<Item> EUPHEMIUM_HELMET;
    public static RegistryObject<Item> EUPHEMIUM_PLATE;
    public static RegistryObject<Item> EUPHEMIUM_LEGS;
    public static RegistryObject<Item> EUPHEMIUM_BOOTS;
    public static RegistryObject<Item> APPLE_EUPHEMIUM;
    public static RegistryObject<Item> WATCH;

    public static RegistryObject<Item> GOGGLES;
    public static RegistryObject<Item> ASHGLASSES;
    public static RegistryObject<Item> GAS_MASK;
    public static RegistryObject<Item> GAS_MASK_M65;
    public static RegistryObject<Item> GAS_MASK_MONO;
    public static RegistryObject<Item> GAS_MASK_OLDE;
    public static RegistryObject<Item> MASK_RAG;
    public static RegistryObject<Item> MASK_PISS;
    public static RegistryObject<Item> HAT;
    public static RegistryObject<Item> BETA;
    public static RegistryObject<Item> NO9;

    public static RegistryObject<Item> T51_HELMET;
    public static RegistryObject<Item> T51_PLATE;
    public static RegistryObject<Item> T51_LEGS;
    public static RegistryObject<Item> T51_BOOTS;
    public static RegistryObject<Item> STEAMSUIT_HELMET;
    public static RegistryObject<Item> STEAMSUIT_PLATE;
    public static RegistryObject<Item> STEAMSUIT_LEGS;
    public static RegistryObject<Item> STEAMSUIT_BOOTS;
    public static RegistryObject<Item> DIESELSUIT_HELMET;
    public static RegistryObject<Item> DIESELSUIT_PLATE;
    public static RegistryObject<Item> DIESELSUIT_LEGS;
    public static RegistryObject<Item> DIESELSUIT_BOOTS;

    public static RegistryObject<Item> CHAINSAW;

    public static RegistryObject<Item> SCHRABIDIUM_HELMET;
    public static RegistryObject<Item> SCHRABIDIUM_PLATE;
    public static RegistryObject<Item> SCHRABIDIUM_LEGS;
    public static RegistryObject<Item> SCHRABIDIUM_BOOTS;
    public static RegistryObject<Item> TITANIUM_HELMET;
    public static RegistryObject<Item> TITANIUM_PLATE;
    public static RegistryObject<Item> TITANIUM_LEGS;
    public static RegistryObject<Item> TITANIUM_BOOTS;
    public static RegistryObject<Item> STEEL_HELMET;
    public static RegistryObject<Item> STEEL_PLATE;
    public static RegistryObject<Item> STEEL_LEGS;
    public static RegistryObject<Item> STEEL_BOOTS;
    @Deprecated public static RegistryObject<Item> ALLOY_HELMET;
    @Deprecated public static RegistryObject<Item> ALLOY_PLATE;
    @Deprecated public static RegistryObject<Item> ALLOY_LEGS;
    @Deprecated public static RegistryObject<Item> ALLOY_BOOTS;
    public static RegistryObject<Item> CMB_HELMET;
    public static RegistryObject<Item> CMB_PLATE;
    public static RegistryObject<Item> CMB_LEGS;
    public static RegistryObject<Item> CMB_BOOTS;
    public static RegistryObject<Item> PAA_PLATE;
    public static RegistryObject<Item> PAA_LEGS;
    public static RegistryObject<Item> PAA_BOOTS;
    public static RegistryObject<Item> ASBESTOS_HELMET;
    public static RegistryObject<Item> ASBESTOS_PLATE;
    public static RegistryObject<Item> ASBESTOS_LEGS;
    public static RegistryObject<Item> ASBESTOS_BOOTS;
    public static RegistryObject<Item> SECURITY_HELMET;
    public static RegistryObject<Item> SECURITY_PLATE;
    public static RegistryObject<Item> SECURITY_LEGS;
    public static RegistryObject<Item> SECURITY_BOOTS;
    public static RegistryObject<Item> COBALT_HELMET;
    public static RegistryObject<Item> COBALT_PLATE;
    public static RegistryObject<Item> COBALT_LEGS;
    public static RegistryObject<Item> COBALT_BOOTS;
    public static RegistryObject<Item> STARMETAL_HELMET;
    public static RegistryObject<Item> STARMETAL_PLATE;
    public static RegistryObject<Item> STARMETAL_LEGS;
    public static RegistryObject<Item> STARMETAL_BOOTS;
    public static RegistryObject<Item> DNT_HELMET;
    public static RegistryObject<Item> DNT_PLATE;
    public static RegistryObject<Item> DNT_LEGS;
    public static RegistryObject<Item> DNT_BOOTS;
    public static RegistryObject<Item> AJR_HELMET;
    public static RegistryObject<Item> AJR_PLATE;
    public static RegistryObject<Item> AJR_LEGS;
    public static RegistryObject<Item> AJR_BOOTS;
    public static RegistryObject<Item> AJRO_HELMET;
    public static RegistryObject<Item> AJRO_PLATE;
    public static RegistryObject<Item> AJRO_LEGS;
    public static RegistryObject<Item> AJRO_BOOTS;
    public static RegistryObject<Item> RPA_HELMET;
    public static RegistryObject<Item> RPA_PLATE;
    public static RegistryObject<Item> RPA_LEGS;
    public static RegistryObject<Item> RPA_BOOTS;
    public static RegistryObject<Item> NCRPA_HELMET;
    public static RegistryObject<Item> NCRPA_PLATE;
    public static RegistryObject<Item> NCRPA_LEGS;
    public static RegistryObject<Item> NCRPA_BOOTS;
    public static RegistryObject<Item> BISMUTH_HELMET;
    public static RegistryObject<Item> BISMUTH_PLATE;
    public static RegistryObject<Item> BISMUTH_LEGS;
    public static RegistryObject<Item> BISMUTH_BOOTS;
    public static RegistryObject<Item> BJ_HELMET;
    public static RegistryObject<Item> BJ_PLATE;
    public static RegistryObject<Item> BJ_PLATE_JETPACK;
    public static RegistryObject<Item> BJ_LEGS;
    public static RegistryObject<Item> BJ_BOOTS;
    public static RegistryObject<Item> ENVSUIT_HELMET;
    public static RegistryObject<Item> ENVSUIT_PLATE;
    public static RegistryObject<Item> ENVSUIT_LEGS;
    public static RegistryObject<Item> ENVSUIT_BOOTS;
    public static RegistryObject<Item> HEV_HELMET;
    public static RegistryObject<Item> HEV_PLATE;
    public static RegistryObject<Item> HEV_LEGS;
    public static RegistryObject<Item> HEV_BOOTS;
    public static RegistryObject<Item> FAU_HELMET;
    public static RegistryObject<Item> FAU_PLATE;
    public static RegistryObject<Item> FAU_LEGS;
    public static RegistryObject<Item> FAU_BOOTS;
    public static RegistryObject<Item> DNS_HELMET;
    public static RegistryObject<Item> DNS_PLATE;
    public static RegistryObject<Item> DNS_LEGS;
    public static RegistryObject<Item> DNS_BOOTS;
    public static RegistryObject<Item> TAURUN_HELMET;
    public static RegistryObject<Item> TAURUN_PLATE;
    public static RegistryObject<Item> TAURUN_LEGS;
    public static RegistryObject<Item> TAURUN_BOOTS;
    public static RegistryObject<Item> TRENCHMASTER_HELMET;
    public static RegistryObject<Item> TRENCHMASTER_PLATE;
    public static RegistryObject<Item> TRENCHMASTER_LEGS;
    public static RegistryObject<Item> TRENCHMASTER_BOOTS;
    public static RegistryObject<Item> ZIRCONIUM_LEGS;
    public static RegistryObject<Item> ROBES_HELMET;
    public static RegistryObject<Item> ROBES_PLATE;
    public static RegistryObject<Item> ROBES_LEGS;
    public static RegistryObject<Item> ROBES_BOOTS;

    public static RegistryObject<Item> JETPACK_BOOST;
    public static RegistryObject<Item> JETPACK_BREAK;
    public static RegistryObject<Item> JETPACK_FLY;
    public static RegistryObject<Item> JETPACK_VECTOR;
    public static RegistryObject<Item> WINGS_LIMP;
    public static RegistryObject<Item> WINGS_MURK;
    public static RegistryObject<Item> OXY_PLSS;

    public static RegistryObject<Item> JACKT;
    public static RegistryObject<Item> JACKT2;

    public static RegistryObject<Item> SCHRABIDIUM_SWORD;
    public static RegistryObject<Item> SCHRABIDIUM_PICKAXE;
    public static RegistryObject<Item> SCHRABIDIUM_AXE;
    public static RegistryObject<Item> SCHRABIDIUM_SHOVEL;
    public static RegistryObject<Item> SCHRABIDIUM_HOE;
    public static RegistryObject<Item> TITANIUM_SWORD;
    public static RegistryObject<Item> TITANIUM_PICKAXE;
    public static RegistryObject<Item> TITANIUM_AXE;
    public static RegistryObject<Item> TITANIUM_SHOVEL;
    public static RegistryObject<Item> TITANIUM_HOE;
    public static RegistryObject<Item> STEEL_SWORD;
    public static RegistryObject<Item> STEEL_PICKAXE;
    public static RegistryObject<Item> STEEL_AXE;
    public static RegistryObject<Item> STEEL_SHOVEL;
    public static RegistryObject<Item> STEEL_HOE;
    @Deprecated public static RegistryObject<Item> ALLOY_SWORD;
    @Deprecated public static RegistryObject<Item> ALLOY_PICKAXE;
    @Deprecated public static RegistryObject<Item> ALLOY_AXE;
    @Deprecated public static RegistryObject<Item> ALLOY_SHOVEL;
    @Deprecated public static RegistryObject<Item> ALLOY_HOE;
    public static RegistryObject<Item> CMB_SWORD;
    public static RegistryObject<Item> CMB_PICKAXE;
    public static RegistryObject<Item> CMB_AXE;
    public static RegistryObject<Item> CMB_SHOVEL;
    public static RegistryObject<Item> CMB_HOE;
    public static RegistryObject<Item> ELEC_SWORD;
    public static RegistryObject<Item> ELEC_PICKAXE;
    public static RegistryObject<Item> ELEC_AXE;
    public static RegistryObject<Item> ELEC_SHOVEL;
    public static RegistryObject<Item> DESH_SWORD;
    public static RegistryObject<Item> DESH_PICKAXE;
    public static RegistryObject<Item> DESH_AXE;
    public static RegistryObject<Item> DESH_SHOVEL;
    public static RegistryObject<Item> DESH_HOE;
    public static RegistryObject<Item> COBALT_SWORD;
    public static RegistryObject<Item> COBALT_PICKAXE;
    public static RegistryObject<Item> COBALT_AXE;
    public static RegistryObject<Item> COBALT_SHOVEL;
    public static RegistryObject<Item> COBALT_HOE;
    public static RegistryObject<Item> COBALT_DECORATED_SWORD;
    public static RegistryObject<Item> COBALT_DECORATED_PICKAXE;
    public static RegistryObject<Item> COBALT_DECORATED_AXE;
    public static RegistryObject<Item> COBALT_DECORATED_SHOVEL;
    public static RegistryObject<Item> COBALT_DECORATED_HOE;
    public static RegistryObject<Item> STARMETAL_SWORD;
    public static RegistryObject<Item> STARMETAL_PICKAXE;
    public static RegistryObject<Item> STARMETAL_AXE;
    public static RegistryObject<Item> STARMETAL_SHOVEL;
    public static RegistryObject<Item> STARMETAL_HOE;
    public static RegistryObject<Item> SMASHING_HAMMER;
    public static RegistryObject<Item> CENTRI_STICK;
    public static RegistryObject<Item> BISMUTH_PICKAXE;
    public static RegistryObject<Item> BISMUTH_AXE;
    public static RegistryObject<Item> VOLCANIC_PICKAXE;
    public static RegistryObject<Item> VOLCANIC_AXE;
    public static RegistryObject<Item> CHLOROPHYTE_PICKAXE;
    public static RegistryObject<Item> CHLOROPHYTE_AXE;
    public static RegistryObject<Item> MESE_PICKAXE;
    public static RegistryObject<Item> MESE_AXE;
    public static RegistryObject<Item> DNT_SWORD;
    public static RegistryObject<Item> DWARVEN_PICKAXE;

    public static RegistryObject<Item> METEORITE_SWORD;
    public static RegistryObject<Item> METEORITE_SWORD_SEARED;
    public static RegistryObject<Item> METEORITE_SWORD_REFORGED;
    public static RegistryObject<Item> METEORITE_SWORD_HARDENED;
    public static RegistryObject<Item> METEORITE_SWORD_ALLOYED;
    public static RegistryObject<Item> METEORITE_SWORD_MACHINED;
    public static RegistryObject<Item> METEORITE_SWORD_TREATED;
    public static RegistryObject<Item> METEORITE_SWORD_ETCHED;
    public static RegistryObject<Item> METEORITE_SWORD_BRED;
    public static RegistryObject<Item> METEORITE_SWORD_IRRADIATED;
    public static RegistryObject<Item> METEORITE_SWORD_FUSED;
    public static RegistryObject<Item> METEORITE_SWORD_BALEFUL;

    public static RegistryObject<Item> MATCHSTICK;
    public static RegistryObject<Item> BALEFIRE_AND_STEEL;

    public static RegistryObject<Item> MASK_OF_INFAMY;

    public static RegistryObject<Item> SCHRABIDIUM_HAMMER;
    public static RegistryObject<Item> SHIMMER_SLEDGE;
    public static RegistryObject<Item> SHIMMER_AXE;
    public static RegistryObject<Item> BOTTLE_OPENER;
    public static RegistryObject<Item> PCH; //for compat please do not hit me
    public static RegistryObject<Item> WOOD_GAVEL;
    public static RegistryObject<Item> LEAD_GAVEL;
    public static RegistryObject<Item> DIAMOND_GAVEL;
    public static RegistryObject<Item> MESE_GAVEL;

    public static RegistryObject<Item> CROWBAR;

    public static RegistryObject<Item> WRENCH_FLIPPED;
    public static RegistryObject<Item> MEMESPOON;

    public static RegistryObject<Item> PIPE_LEAD;
    public static RegistryObject<Item> REER_GRAAR;
    public static RegistryObject<Item> STOPSIGN;
    public static RegistryObject<Item> SOPSIGN;
    public static RegistryObject<Item> CHERNOBYLSIGN;

    public static RegistryObject<Item> CRYSTAL_HORN;
    public static RegistryObject<Item> CRYSTAL_CHARRED;

    public static RegistryObject<Item> ATTACHMENT_MASK;
    public static RegistryObject<Item> ATTACHMENT_MASK_MONO;
    public static RegistryObject<Item> BACK_TESLA;
    public static RegistryObject<Item> SERVO_SET;
    public static RegistryObject<Item> SERVO_SET_DESH;
    public static RegistryObject<Item> PADS_RUBBER;
    public static RegistryObject<Item> PADS_SLIME;
    public static RegistryObject<Item> PADS_STATIC;
    public static RegistryObject<Item> CLADDING_PAINT;
    public static RegistryObject<Item> CLADDING_RUBBER;
    public static RegistryObject<Item> CLADDING_LEAD;
    public static RegistryObject<Item> CLADDING_DESH;
    public static RegistryObject<Item> CLADDING_GHIORSIUM;
    public static RegistryObject<Item> CLADDING_IRON;
    public static RegistryObject<Item> CLADDING_OBSIDIAN;
    public static RegistryObject<Item> INSERT_KEVLAR;
    public static RegistryObject<Item> INSERT_SAPI;
    public static RegistryObject<Item> INSERT_ESAPI;
    public static RegistryObject<Item> INSERT_XSAPI;
    public static RegistryObject<Item> INSERT_STEEL;
    public static RegistryObject<Item> INSERT_DU;
    public static RegistryObject<Item> INSERT_POLONIUM;
    public static RegistryObject<Item> INSERT_GHIORSIUM;
    public static RegistryObject<Item> INSERT_CMB;
    public static RegistryObject<Item> INSERT_ERA;
    public static RegistryObject<Item> INSERT_YHARONITE;
    public static RegistryObject<Item> INSERT_DOXIUM;
    public static RegistryObject<Item> ARMOR_POLISH;
    public static RegistryObject<Item> BANDAID;
    public static RegistryObject<Item> SERUM;
    public static RegistryObject<Item> QUARTZ_PLUTONIUM;
    public static RegistryObject<Item> MORNING_GLORY;
    public static RegistryObject<Item> LODESTONE;
    public static RegistryObject<Item> HORSESHOE_MAGNET;
    public static RegistryObject<Item> INDUSTRIAL_MAGNET;
    public static RegistryObject<Item> BATHWATER;
    public static RegistryObject<Item> BATHWATER_MK2;
    public static RegistryObject<Item> SPIDER_MILK;
    public static RegistryObject<Item> INK;
    public static RegistryObject<Item> HEART_PIECE;
    public static RegistryObject<Item> HEART_CONTAINER;
    public static RegistryObject<Item> HEART_BOOSTER;
    public static RegistryObject<Item> HEART_FAB;
    public static RegistryObject<Item> BLACK_DIAMOND;
    public static RegistryObject<Item> WD40;
    public static RegistryObject<Item> SCRUMPY;
    public static RegistryObject<Item> WILD_P;
    public static RegistryObject<Item> SHACKLES;
    public static RegistryObject<Item> INJECTOR_5HTP;
    public static RegistryObject<Item> INJECTOR_KNIFE;
    public static RegistryObject<Item> MEDAL_LIQUIDATOR;
    public static RegistryObject<Item> V1;
    public static RegistryObject<Item> PROTECTION_CHARM;
    public static RegistryObject<Item> METEOR_CHARM;
    public static RegistryObject<Item> NEUTRINO_LENS;
    public static RegistryObject<Item> GAS_TESTER;
    public static RegistryObject<Item> DEFUSER_GOLD;
    public static RegistryObject<Item> BALLISTIC_GAUNTLET;
    public static RegistryObject<Item> NIGHT_VISION;
    public static RegistryObject<Item> CARD_AOS;
    public static RegistryObject<Item> CARD_QOS;
    public static RegistryObject<Item> AUSTRALIUM_III;
    public static RegistryObject<Item> ARMOR_BATTERY;
    public static RegistryObject<Item> ARMOR_BATTERY_MK2;
    public static RegistryObject<Item> ARMOR_BATTERY_MK3;
    public static RegistryObject<Item> FLIPPERS;
    public static RegistryObject<Item> HEAVY_BOOTS;

    public static RegistryObject<Item> HAZMAT_HELMET;
    public static RegistryObject<Item> HAZMAT_PLATE;
    public static RegistryObject<Item> HAZMAT_LEGS;
    public static RegistryObject<Item> HAZMAT_BOOTS;
    public static RegistryObject<Item> HAZMAT_HELMET_RED;
    public static RegistryObject<Item> HAZMAT_PLATE_RED;
    public static RegistryObject<Item> HAZMAT_LEGS_RED;
    public static RegistryObject<Item> HAZMAT_BOOTS_RED;
    public static RegistryObject<Item> HAZMAT_HELMET_GREY;
    public static RegistryObject<Item> HAZMAT_PLATE_GREY;
    public static RegistryObject<Item> HAZMAT_LEGS_GREY;
    public static RegistryObject<Item> HAZMAT_BOOTS_GREY;
    public static RegistryObject<Item> LIQUIDATOR_HELMET;
    public static RegistryObject<Item> LIQUIDATOR_PLATE;
    public static RegistryObject<Item> LIQUIDATOR_LEGS;
    public static RegistryObject<Item> LIQUIDATOR_BOOTS;

    public static RegistryObject<Item> HAZMAT_PAA_HELMET;
    public static RegistryObject<Item> HAZMAT_PAA_PLATE;
    public static RegistryObject<Item> HAZMAT_PAA_LEGS;
    public static RegistryObject<Item> HAZMAT_PAA_BOOTS;

    public static RegistryObject<Item> REBAR_PLACER;

    public static RegistryObject<Item> WAND;
    public static RegistryObject<Item> WAND_S;
    public static RegistryObject<Item> WAND_D;
    public static RegistryObject<Item> WAND_TIME;

    public static RegistryObject<Item> STRUCTURE_SINGLE;
    public static RegistryObject<Item> STRUCTURE_SOLID;
    public static RegistryObject<Item> STRUCTURE_PATTERN;
    public static RegistryObject<Item> STRUCTURE_RANDOMIZED;
    public static RegistryObject<Item> STRUCTURE_RANDOMLY;
    public static RegistryObject<Item> STRUCTURE_CUSTOMMACHINE;

    public static RegistryObject<Item> ROD_OF_DISCORD;

    @Deprecated public static RegistryObject<Item> CAPE_RADIATION;
    @Deprecated public static RegistryObject<Item> CAPE_GASMASK;
    @Deprecated public static RegistryObject<Item> CAPE_SCHRABIDIUM;
    @Deprecated public static RegistryObject<Item> CAPE_HIDDEN;

    public static RegistryObject<Item> NUKE_STARTER_KIT;
    public static RegistryObject<Item> NUKE_ADVANCED_KIT;
    public static RegistryObject<Item> NUKE_COMMERCIALLY_KIT;
    public static RegistryObject<Item> NUKE_ELECTRIC_KIT;
    public static RegistryObject<Item> GADGET_KIT;
    public static RegistryObject<Item> BOY_KIT;
    public static RegistryObject<Item> MAN_KIT;
    public static RegistryObject<Item> MIKE_KIT;
    public static RegistryObject<Item> TSAR_KIT;
    public static RegistryObject<Item> MULTI_KIT;
    public static RegistryObject<Item> CUSTOM_KIT;
    public static RegistryObject<Item> FLEIJA_KIT;
    public static RegistryObject<Item> PROTOTYPE_KIT;
    public static RegistryObject<Item> MISSILE_KIT;
    public static RegistryObject<Item> EUPHEMIUM_KIT;
    public static RegistryObject<Item> SOLINIUM_KIT;
    public static RegistryObject<Item> HAZMAT_KIT;
    public static RegistryObject<Item> HAZMAT_RED_KIT;
    public static RegistryObject<Item> HAZMAT_GREY_KIT;
    public static RegistryObject<Item> KIT_CUSTOM;

    public static RegistryObject<Item> TOOLBOX;

    public static RegistryObject<Item> LOOT_10;
    public static RegistryObject<Item> LOOT_15;
    public static RegistryObject<Item> LOOT_MISC;

    public static RegistryObject<Item> AMMO_CONTAINER;

    public static RegistryObject<Item> IGNITER;
    public static final RegistryObject<Item> DETONATOR = add("detonator", ()->new ItemDetonator(new Item.Properties()), ModTabs.NUKE.getKey(), HBMKey.BASIC_MODEL, HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static RegistryObject<Item> DETONATOR_MULTI;
    public static RegistryObject<Item> DETONATOR_LASER;
    public static RegistryObject<Item> DETONATOR_DEADMAN;
    public static RegistryObject<Item> DETONATOR_DE;
    public static RegistryObject<Item> BOMB_CALLER;
    public static final RegistryObject<Item> METEOR_REMOTE = control("meteor_remote", ()->new ItemMeteorRemote(new Item.Properties().durability(2)), "Meteorite Remote");
    public static RegistryObject<Item> ANCHOR_REMOTE;
    public static RegistryObject<Item> REMOTE;
    //public static RegistryObject<Item> TURRET_CONTROL;
    public static RegistryObject<Item> TURRET_CHIP;
    //public static RegistryObject<Item> TURRET_BIOMETRY;

    public static RegistryObject<Item> SPAWN_CHOPPER;
    public static RegistryObject<Item> SPAWN_WORM;
    public static RegistryObject<Item> SPAWN_UFO;
    public static RegistryObject<Item> SPAWN_DUCK;
    public static RegistryObject<Item> SPAWN_ANGEL;

    public static final RegistryObject<Item> KEY = new WrappedItemRegistryBuilder("key", ()->new com.hbm.item.tool.ItemKey(new Item.Properties().stacksTo(1))).tab(ModTabs.PARTS.getKey()).loc(HBMKey.REVERSE_GEN).build();
    public static RegistryObject<Item> KEY_RED;
    public static RegistryObject<Item> KEY_RED_CRACKED;
    public static RegistryObject<Item> KEY_KIT;
    public static RegistryObject<Item> KEY_FAKE;
    public static RegistryObject<Item> PIN;
    public static RegistryObject<Item> PADLOCK_RUSTY;
    public static RegistryObject<Item> PADLOCK;
    public static RegistryObject<Item> PADLOCK_REINFORCED;
    public static RegistryObject<Item> PADLOCK_UNBREAKABLE;

    public static RegistryObject<Item> MECH_KEY;

    public static RegistryObject<Item> BUCKET_MUD;
    public static RegistryObject<Item> BUCKET_ACID;
    public static RegistryObject<Item> BUCKET_TOXIC;
    public static RegistryObject<Item> BUCKET_SCHRABIDIC_ACID;
    public static RegistryObject<Item> BUCKET_SULFURIC_ACID;
    public static RegistryObject<Item> BUCKET_MERCURY;
    public static RegistryObject<Item> BUCKET_BROMINE;
    public static RegistryObject<Item> BUCKET_CCL;

    public static RegistryObject<Item> DOOR_METAL;
    public static RegistryObject<Item> DOOR_OFFICE;
    public static RegistryObject<Item> DOOR_BUNKER;
    public static RegistryObject<Item> DOOR_RED;

    public static RegistryObject<Item> FENCE_GATE;

    public static RegistryObject<Item> RECORD_LC;
    public static RegistryObject<Item> RECORD_SS;
    public static RegistryObject<Item> RECORD_VC;
    public static RegistryObject<Item> RECORD_GS;
    public static RegistryObject<Item> RECORD_GP;
    public static RegistryObject<Item> RECORD_EL;
    public static RegistryObject<Item> RECORD_GLASS;

    public static RegistryObject<Item> BOOK_GUIDE;
    public static RegistryObject<Item> BOOK_LORE;
    public static RegistryObject<Item> HOLOTAPE_IMAGE;
    public static RegistryObject<Item> HOLOTAPE_DAMAGED;
    public static RegistryObject<Item> CLAY_TABLET;

    public static RegistryObject<Item> POLAROID;
    public static RegistryObject<Item> GLITCH;
    public static RegistryObject<Item> BOOK_SECRET;
    public static RegistryObject<Item> BOOK_OF_;
    public static RegistryObject<Item> PAGE_OF_;
    public static RegistryObject<Item> BOOK_LEMEGETON;
    public static RegistryObject<Item> BURNT_BARK;

    public static RegistryObject<Item> CHLORINE1;
    public static RegistryObject<Item> CHLORINE2;
    public static RegistryObject<Item> CHLORINE3;
    public static RegistryObject<Item> CHLORINE4;
    public static RegistryObject<Item> CHLORINE5;
    public static RegistryObject<Item> CHLORINE6;
    public static RegistryObject<Item> CHLORINE7;
    public static RegistryObject<Item> CHLORINE8;
    public static RegistryObject<Item> PC1;
    public static RegistryObject<Item> PC2;
    public static RegistryObject<Item> PC3;
    public static RegistryObject<Item> PC4;
    public static RegistryObject<Item> PC5;
    public static RegistryObject<Item> PC6;
    public static RegistryObject<Item> PC7;
    public static RegistryObject<Item> PC8;
    public static RegistryObject<Item> CLOUD1;
    public static RegistryObject<Item> CLOUD2;
    public static RegistryObject<Item> CLOUD3;
    public static RegistryObject<Item> CLOUD4;
    public static RegistryObject<Item> CLOUD5;
    public static RegistryObject<Item> CLOUD6;
    public static RegistryObject<Item> CLOUD7;
    public static RegistryObject<Item> CLOUD8;
    public static RegistryObject<Item> ORANGE1;
    public static RegistryObject<Item> ORANGE2;
    public static RegistryObject<Item> ORANGE3;
    public static RegistryObject<Item> ORANGE4;
    public static RegistryObject<Item> ORANGE5;
    public static RegistryObject<Item> ORANGE6;
    public static RegistryObject<Item> ORANGE7;
    public static RegistryObject<Item> ORANGE8;

    public static RegistryObject<Item> TEMPLATE_FOLDER;
    public static RegistryObject<Item> NOTHING;
    public static RegistryObject<Item> BROKEN_ITEM;

    public static RegistryObject<Item> ACHIEVEMENT_ICON;

    public static RegistryObject<Item> MYSTERYSHOVEL;
    public static RegistryObject<Item> MEMORY;

    public static RegistryObject<Item> CONVEYOR_WAND;

    public static RegistryObject<Item> SWARM_MEMBER;
    
    
    //=================================================================
    static void point(){}

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
    public static final RegistryObject<Item> NUCLEAR_WASTE_PEARL = legacyGrenade("nuclear_waste_pearl", ItemGrenade.Type.NUCLEAR);

    public static final RegistryObject<Item> INGOT_ADVANCED_ALLOY = parts("ingot_advanced_alloy", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.ALLOY.ingot());

    public static final RegistryObject<Item> PLATE_ADVANCED_ALLOY = parts("plate_advanced_alloy", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMMatters.ALLOY.plate());
    public static final RegistryObject<Item> COIL_ADVANCED_ALLOY = parts("coil_advanced_alloy", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> COIL_ADVANCED_TORUS = parts("coil_advanced_torus", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

    public static final RegisterObjectCollection<Item, HBMMatter> BOLTS = new RegisterObjectCollection<>(HBMMatters.ALL_MATTERS, matter -> new WrappedItemRegistryBuilder("bolt_" + matter.name(), ()->new Item(new Item.Properties()))
            .tab(ModTabs.PARTS.getKey()).loc(HBMKey.ORDERLY_GEN_EXCEPT_FIRST).model(itemModelGen -> itemModelGen.basicItem("bolt_" + matter.name(), HBM.rl("bolt"))).tags(matter.bolt())
            .color((stack, idx) -> matter.solidColorLight).build(), matter -> matter.bolt() != null);
    public static final RegistryObject<Item> SAT_BASE = parts("sat_base", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> billet_balefire_gold = parts("billet_balefire_gold", ()->new Item(new Item.Properties().rarity(Rarity.UNCOMMON)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> billet_flashlead = parts("billet_flashlead", ()->new ItemCustomInfo(new Item.Properties().rarity(Rarity.UNCOMMON),HBMLang.ITEM_BILLETFLASHLEAD_DESC.translate()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

    public static final RegistryObject<Item> BYPRODUCT = new WrappedItemRegistryBuilder("byproduct", ()->new ItemBedrockOre(new Item.Properties()))
            .tab(ModTabs.PARTS.getKey()).color((ItemStack stack, int tintIndex) -> tintIndex == 1 ? ItemBedrockOre.getColor(stack) : -1).build();
    // 基岩原矿
    public static final RegistryObject<Item> ORE_BEDROCK_RAW = new WrappedItemRegistryBuilder("ore_bedrock_raw", ()->new ItemBedrockOreRaw(new Item.Properties()))
            .tab(ModTabs.PARTS.getKey()).model(HBMKey.MODEL_ITEM_SINGLE, HBM.rl("item/bedrock_ore_new")).loc(HBMKey.REVERSE_GEN).build();
    public static final RegistryObject<Item> ORE_BEDROCK_SCANNER = new WrappedItemRegistryBuilder("bedrock_ore_scanner", ()->new ItemBedrockOreScanner(new Item.Properties()))
            .tab(ModTabs.PARTS.getKey()).model(HBMKey.MODEL_ITEM_SINGLE, HBM.rl("item/ore_density_scanner"))
            .hud(event -> event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), ItemBedrockOreScanner.id.getPath(), new HUDBedrockOreScanner())).build();
    public static final RegistryObject<Item> BEDROCK_ORE_COMPLEX = new WrappedItemRegistryBuilder("bedrock_ore", ()->new ItemBedrockOreCombine(new Item.Properties()))
            .tab(ModTabs.PARTS.getKey())
            .itemProperties(ItemModelGen.property_stage, false, ItemBedrockOreCombine::getGradeProperty, null)
            .itemProperties(ItemModelGen.property_type, false, ItemBedrockOreCombine::getTypeProperty, null)
            .model(ItemBedrockOreCombine::genModel) // 单独生成模型，不根据物品属性自动生成模型
            .color((ItemStack stack, int tintIndex) -> tintIndex == 0 ? ItemBedrockOreCombine.getColor(stack) : -1)
            .build();

    public static final RegistryObject<Item> COKE_COAL = parts("coke_coal", ()->new ItemFuel(new Item.Properties(), 200 * 16), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, ModTags.Items.COKE);
    public static final RegistryObject<Item> COKE_LIGNITE = parts("coke_lignite", ()->new ItemFuel(new Item.Properties(), 200 * 16), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, ModTags.Items.COKE);
    public static final RegistryObject<Item> COKE_PETROLEUM = parts("coke_petroleum", ()->new ItemFuel(new Item.Properties(), 200 * 16), HBMKey.ORDERLY_GEN_EXCEPT_FIRST, ModTags.Items.COKE);
    public static final RegistryObject<Item> BRIQUETTE_COAL = parts("briquette_coal", ()->new ItemFuel(new Item.Properties(), 200 * 10), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BRIQUETTE_LIGNITE = parts("briquette_lignite", ()->new ItemFuel(new Item.Properties(), 200 * 8), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> BRIQUETTE_WOOD = parts("briquette_wood", ()->new ItemFuel(new Item.Properties(), 200 * 2), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

    public static final RegistryObject<Item> POWDER_ASH_COAL = parts("powder_ash_coal", ()->new ItemFuel(new Item.Properties(), 200), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> POWDER_ASH_FLY = parts("powder_ash_fly", ()->new ItemFuel(new Item.Properties(), 200), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> POWDER_ASH_FULLERENE = parts("powder_ash_fullerene", ()->new ItemFuel(new Item.Properties(), 200), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> POWDER_ASH_MISC = parts("powder_ash_misc", ()->new ItemFuel(new Item.Properties(), 200 / 2), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> POWDER_ASH_SOOT = parts("powder_ash_soot", ()->new ItemFuel(new Item.Properties(), 200 / 2), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> POWDER_ASH_WOOD = parts("powder_ash_wood", ()->new ItemFuel(new Item.Properties(), 200 / 2), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> WOOD_ASH_POWDER = parts("wood_ash_powder", () -> new Item(new Item.Properties()), HBMKey.GEN_STANDALONE);
    public static final RegistryObject<Item> POWDER_ADVANCED_ALLOY = parts("powder_advanced_alloy", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> TOOTHPICKS = parts("toothpicks", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SAT_HEAD_MAPPER = parts("sat_head_mapper", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SAT_HEAD_SCANNER = parts("sat_head_scanner", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SAT_HEAD_RADAR = parts("sat_head_radar", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SAT_HEAD_LASER = parts("sat_head_laser", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> SAT_HEAD_RESONATOR = parts("sat_head_resonator", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);


    public static final RegistryObject<Item> GUIDE_BOOK = consumable("book_guide", ()->new WritableBookItem(new Item.Properties()), HBMKey.REVERSE_GEN);
    // 电池
    public static final RegistryObject<Item> BATTERY_GENERIC = control("battery_generic",()->new ItemBattery(false,5_000, 100, new Item.Properties().stacksTo(1)), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> BATTERY_ADVANCED = control("battery_advanced",()->new ItemBattery(false,60_000, 500, new Item.Properties().stacksTo(1)), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> BATTERY_LITHIUM = control("battery_lithium",()->new ItemBattery(false,250_000, 1000, new Item.Properties().stacksTo(1)), HBMKey.REVERSE_GEN);

    // 其他hbmcomponent的东西，暂时放一下
    public static final RegistryObject<Item> CIRCUIT_BASIC = parts("circuit_basic", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> EGG_GLYPHID_TO_BIRTH = parts("egg_glyphid",()->new ItemEggGlyphidToBirth(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> WIRE_FINE_ALUMINIUM = parts("wire_aluminium", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> DUCT_TAPE = parts("duct_tape", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> GUN_SUICIDE = add("gun_suicide", ()->new GunSuicide(new Item.Properties()), ModTabs.WEAPON.getKey(), HBMKey.BASIC_MODEL, HBMKey.ORDERLY_GEN_EXCEPT_FIRST);

    public static final RegistryObject<Item> overlay_my_fluid = ITEMS.register("overlay_my_fluid",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> crucible_template = ITEMS.register("crucible_template",()->new Item(new Item.Properties()));

    public static final RegistryObject<Item> reacher = ITEMS.register("reacher",()->new Item(new Item.Properties()));
    //升级组件
    public static final RegistryObject<Item> UPGRADE_BASE = ITEMS.register("upgrade_base",()->new Item(new Item.Properties()));
    // RBMK
    public static final RegistryObject<Item> rbmk_lid = machine("rbmk_lid", () -> new ItemRBMKLid(new Item.Properties(), RBMKLidType.SOLID));
    public static final RegistryObject<Item> rbmk_lid_glass = machine("rbmk_lid_glass", () -> new ItemRBMKLid(new Item.Properties(), RBMKLidType.GLASS));
    public static final RegistryObject<Item> rbmk_control_rod = machine("rbmk_control_rod", () -> new ItemRBMKControlRod(new Item.Properties().stacksTo(1)), HBMKey.GEN_STANDALONE);
    // RBMK fuel rods (placeholder stats, real values will be wired in later).
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

    public static final RegistryObject<Item> rod_zirnox_empty = control("rod_zirnox_empty", () -> new Item(new Item.Properties().stacksTo(64)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
    public static final RegistryObject<Item> rod_zirnox_tritium = control("rod_zirnox_tritium", () -> new Item(new Item.Properties().stacksTo(1)), HBMKey.ORDERLY_GEN_EXCEPT_FIRST);
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
    /**
     * 控制类物品
     * */
    public static final RegistryObject<Item> DEBUG_WAND = control("debug_wand", ()->new ItemDebugWand(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> BUILD_WAND = control("wand", () -> new ItemBuildWand(new Item.Properties().stacksTo(1)), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> ORE_CANNER = control("ore_density_scanner", () -> new OreScannerItem(new Item.Properties()), HBMKey.ORDERLY_GEN);
    // 从旧HBMComponet中迁移来的
    public static final RegistryObject<Item> TRITIUM_DEUTERIUM_CAKE = parts("tritium_deuterium_cake", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);

    public static final RegistryObject<Item> PARTICLE_APROTON = parts("particle_aproton", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);
    public static final RegistryObject<Item> PARTICLE_AELECTRON = parts("particle_aelectron", ()->new Item(new Item.Properties()), HBMKey.ORDERLY_GEN);

    public static final RegistryObject<Item> CASING_SMALL = parts("casing.small", ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> CASING_LARGE = parts("casing.large", ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> CASING_SMALL_STEEL = parts("casing.small_steel", ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> CASING_LARGE_STEEL = parts("casing.large_steel", ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> CASING_SHOTSHELL = parts("casing.shotshell", ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> CASING_BUCKSHOT = parts("casing.buckshot", ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> CASING_BUCKSHOT_ADVANCED = parts("casing.buckshot_advanced", ()->new Item(new Item.Properties()), HBMKey.REVERSE_GEN);

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


    // 大型采矿机钻头
    public static final RegistryObject<Item> DRILLBIT_STEEL = parts("drillbit_steel", () -> new ItemDrillbit(ItemDrillbit.EnumDrillType.STEEL, new Item.Properties().stacksTo(1)), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> DRILLBIT_STEEL_DIAMOND = parts("drillbit_steel_diamond", () -> new ItemDrillbit(ItemDrillbit.EnumDrillType.STEEL_DIAMOND, new Item.Properties().stacksTo(1)), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> DRILLBIT_HSS = parts("drillbit_hss", () -> new ItemDrillbit(ItemDrillbit.EnumDrillType.HSS, new Item.Properties().stacksTo(1)), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> DRILLBIT_HSS_DIAMOND = parts("drillbit_hss_diamond", () -> new ItemDrillbit(ItemDrillbit.EnumDrillType.HSS_DIAMOND, new Item.Properties().stacksTo(1)), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> DRILLBIT_DESH = parts("drillbit_desh", () -> new ItemDrillbit(ItemDrillbit.EnumDrillType.DESH, new Item.Properties().stacksTo(1)), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> DRILLBIT_DESH_DIAMOND = parts("drillbit_desh_diamond", () -> new ItemDrillbit(ItemDrillbit.EnumDrillType.DESH_DIAMOND, new Item.Properties().stacksTo(1)), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> DRILLBIT_TCALLOY = parts("drillbit_tcalloy", () -> new ItemDrillbit(ItemDrillbit.EnumDrillType.TCALLOY, new Item.Properties().stacksTo(1)), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> DRILLBIT_TCALLOY_DIAMOND = parts("drillbit_tcalloy_diamond", () -> new ItemDrillbit(ItemDrillbit.EnumDrillType.TCALLOY_DIAMOND, new Item.Properties().stacksTo(1)), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> DRILLBIT_FERRO = parts("drillbit_ferro", () -> new ItemDrillbit(ItemDrillbit.EnumDrillType.FERRO, new Item.Properties().stacksTo(1)), HBMKey.REVERSE_GEN);
    public static final RegistryObject<Item> DRILLBIT_FERRO_DIAMOND = parts("drillbit_ferro_diamond", () -> new ItemDrillbit(ItemDrillbit.EnumDrillType.FERRO_DIAMOND, new Item.Properties().stacksTo(1)), HBMKey.REVERSE_GEN);

    public static final RegistryObject<Item> DUMMY_ITEM = ITEMS.register("dummy_item", ()->new Item(new Item.Properties()));
    // 武器配件
    public static final RegistryObject<Item> ASH_GLASS = parts("ash_glass", () -> new ItemAshGlass(ArmorMaterials.IRON, ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1)), HBMKey.REVERSE_GEN);
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
    public static RegistryObject<Item> parts(final String name, final Supplier<? extends Item> sup, String genNameWay, TagKey<Item> ... tag){
        return add(name, sup, ModTabs.PARTS.getKey(), HBMKey.BASIC_MODEL, genNameWay, tag);
    }
    public static RegistryObject<Item> control(final String name, final Supplier<? extends Item> sup, String genNameWay, TagKey<Item> ... tag){
        return add(name, sup, ModTabs.CONTROL.getKey(), genNameWay, tag);
    }

    public static RegistryObject<Item> nuke(final String name, final Supplier<? extends Item> sup, String genNameWay, TagKey<Item> ... tag){
        return add(name, sup, ModTabs.NUKE.getKey(), genNameWay, tag);
    }

    public static RegistryObject<Item> add(final String name, final Supplier<? extends Item> sup, ResourceKey<CreativeModeTab> tabKey, String genNameWay, TagKey<Item> ... tag){
        return add(name, sup, tabKey, HBMKey.BASIC_MODEL, genNameWay, tag);
    }
//    public static RegistryObject<Item> add(final String name, final Supplier<? extends Item> sup, ResourceKey<CreativeModeTab> tabKey, String genModelWay, String genNameWay){
//        return new WrapperRegistry.ItemBuilder(name, sup).tab(tabKey).model(genModelWay).loc(genNameWay).build();
//    }
    public static RegistryObject<Item> add(final String name, final Supplier<? extends Item> sup, ResourceKey<CreativeModeTab> tabKey, String genModelWay, String genNameWay, TagKey<Item> ... tag){
        return new WrappedItemRegistryBuilder(name, sup).tab(tabKey).tags(tag).model(genModelWay).loc(genNameWay).build();
    }

    public static void creativeTab(BuildCreativeModeTabContentsEvent event){
        for (WrappedItemRegistryBuilder itemRegistry : itemList) {
            itemRegistry.creativeTabSupport(event);
        }
    }

    public static void genModel(ItemModelGen provider){
        for (WrappedItemRegistryBuilder itemRegistry : itemList) {
            itemRegistry.modelSupport(provider);
        }
    }
    public static final RegistryObject<Item> KEY_PIN = new WrappedItemRegistryBuilder("key_pin", ()->new com.hbm.item.tool.ItemKeyPin(new Item.Properties().stacksTo(1))).tab(ModTabs.PARTS.getKey()).loc(HBMKey.REVERSE_GEN).build();

    public static void languageSupport(LanguageProvider provider){
        for (WrappedItemRegistryBuilder itemRegistry : itemList) {
            itemRegistry.languageSupport(provider);
        }
    }
    public static void tagSupport(ItemTagsGen provider){
        for (WrappedItemRegistryBuilder itemRegistry : itemList) {
            itemRegistry.tagSupport(provider);
        }
    }
    public static void itemPropertiesSupport(){
        for (WrappedItemRegistryBuilder itemRegistry : itemList) {
            itemRegistry.itemPropertiesSupport();
        }
    }

    public static void itemColorSupport(RegisterColorHandlersEvent.Item event){
        for (WrappedItemRegistryBuilder itemRegistry : itemList) {
            itemRegistry.itemColorSupport(event);
        }
    }

    public static void hudSupport(RegisterGuiOverlaysEvent event){
        for (WrappedItemRegistryBuilder itemRegistry : itemList) {
            itemRegistry.hudSupport(event);
        }
    }
}
