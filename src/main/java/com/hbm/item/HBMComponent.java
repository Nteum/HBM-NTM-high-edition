package com.hbm.item;

import com.hbm.datagen.LanguageProvider;
import com.hbm.registries.ModItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Compatibility bridge for legacy references to {@code HBMComponent}.
 */
@Deprecated
public class HBMComponent extends ModItems {

    private HBMComponent() {
    }

    // Legacy aliases kept for source compatibility with old branches.
    public static final RegistryObject<Item> BEDROCK_ORE_BASE = BEDROCK_ORE;
    public static final RegistryObject<Item> BEDROCK_ORE_FRAGMENT = legacy("bedrock_ore_fragment");
    public static final RegistryObject<Item> BILLET_BALEFIRE_GOLD = billet_balefire_gold;
    public static final RegistryObject<Item> BILLET_FLASHLEAD = billet_flashlead;
    public static final RegistryObject<Item> BIO_WAFER = legacy("bio_wafer");
    public static final RegistryObject<Item> BOLT = legacy("bolt");
    public static final RegistryObject<Item> BOLT_SPIKE = legacy("bolt_spike");
    public static final RegistryObject<Item> BOTTLE_MERCURY = legacy("bottle_mercury");
    public static final RegistryObject<Item> BRIQUETTE = legacy("briquette");
    public static final RegistryObject<Item> CASING = legacy("casing");
    public static final RegistryObject<Item> CHEMICAL_DYE = legacy("chemical_dye");
    public static final RegistryObject<Item> CHUNK_ORE = legacy("chunk_ore");
    public static final RegistryObject<Item> CIRCUIT = legacy("circuit");
    public static final RegistryObject<Item> CIRCUIT_STAR = legacy("circuit_star");
    public static final RegistryObject<Item> CIRCUIT_STAR_COMPONENT = legacy("circuit_star_component");
    public static final RegistryObject<Item> CIRCUIT_STAR_PIECE = legacy("circuit_star_piece");
    public static final RegistryObject<Item> COKE = legacy("coke");
    public static final RegistryObject<Item> CRAYON = legacy("crayon");
    public static final RegistryObject<Item> CRYSTAL_SCHRABIDIUM = legacy("crystal_schrabidium");
    public static final RegistryObject<Item> CRYSTAL_SCHRARANIUM = legacy("crystal_schraranium");
    public static final RegistryObject<Item> DRILLBIT = legacy("drillbit");
    public static final RegistryObject<Item> ENTANGLEMENT_KIT = legacy("entanglement_kit");
    public static final RegistryObject<Item> GEAR_LARGE = legacy("gear_large");
    public static final RegistryObject<Item> GEM_ALEXANDRITE = legacy("gem_alexandrite");
    public static final RegistryObject<Item> GEM_RAD = legacy("gem_rad");
    public static final RegistryObject<Item> GEM_SODALITE = legacy("gem_sodalite");
    public static final RegistryObject<Item> GEM_TANTALIUM = legacy("gem_tantalium");
    public static final RegistryObject<Item> GEM_VOLCANIC = legacy("gem_volcanic");
    public static final RegistryObject<Item> INGOT_ACTINIUM = legacy("ingot_actinium");
    public static final RegistryObject<Item> INGOT_ARSENIC = legacy("ingot_arsenic");
    public static final RegistryObject<Item> INGOT_BAKELITE = legacy("ingot_bakelite");
    public static final RegistryObject<Item> INGOT_BIORUBBER = legacy("ingot_biorubber");
    public static final RegistryObject<Item> INGOT_CHAINSTEEL = legacy("ingot_chainsteel");
    public static final RegistryObject<Item> INGOT_ELECTRONIUM = legacy("ingot_electronium");
    public static final RegistryObject<Item> INGOT_FERROURANIUM = legacy("ingot_ferrouranium");
    public static final RegistryObject<Item> INGOT_FIBERGLASS = legacy("ingot_fiberglass");
    public static final RegistryObject<Item> INGOT_GH336 = legacy("ingot_gh336");
    public static final RegistryObject<Item> INGOT_LANTHANIUM = legacy("ingot_lanthanium");
    public static final RegistryObject<Item> INGOT_MERCURY = legacy("nugget_mercury");
    public static final RegistryObject<Item> INGOT_METAL = legacy("ingot_metal");
    public static final RegistryObject<Item> INGOT_METEORITE = legacy("ingot_meteorite");
    public static final RegistryObject<Item> INGOT_METEORITE_FORGED = legacy("ingot_meteorite_forged");
    public static final RegistryObject<Item> INGOT_OSMIRIDIUM = legacy("ingot_osmiridium");
    public static final RegistryObject<Item> INGOT_PC = legacy("ingot_pc");
    public static final RegistryObject<Item> INGOT_POLYMER = legacy("ingot_polymer");
    public static final RegistryObject<Item> INGOT_PVC = legacy("ingot_pvc");
    public static final RegistryObject<Item> INGOT_RUBBER = legacy("ingot_rubber");
    public static final RegistryObject<Item> INGOT_SEMTEX = legacy("ingot_semtex");
    public static final RegistryObject<Item> INGOT_SMORE = ingot_smore;
    public static final RegistryObject<Item> INGOT_STEEL_DUSTED = legacy("ingot_steel_dusted");
    public static final RegistryObject<Item> INGOT_TANTALIUM = legacy("ingot_tantalium");
    public static final RegistryObject<Item> ITEM_EXPENSIVE = legacy("item_expensive");
    public static final RegistryObject<Item> ITEM_SECRET = legacy("item_secret");
    public static final RegistryObject<Item> NUGGET_AUSTRALIUM = legacy("nugget_australium");
    public static final RegistryObject<Item> NUGGET_AUSTRALIUM_GREATER = legacy("nugget_australium_greater");
    public static final RegistryObject<Item> NUGGET_AUSTRALIUM_LESSER = legacy("nugget_australium_lesser");
    public static final RegistryObject<Item> NUGGET_DESH = legacy("nugget_desh");
    public static final RegistryObject<Item> NUGGET_DINEUTRONIUM = legacy("nugget_dineutronium");
    public static final RegistryObject<Item> NUGGET_GH336 = legacy("nugget_gh336");
    public static final RegistryObject<Item> NUGGET_OSMIRIDIUM = legacy("nugget_osmiridium");
    public static final RegistryObject<Item> NUGGET_SCHRABIDIUM = legacy("nugget_schrabidium");
    public static final RegistryObject<Item> NUGGET_TANTALIUM = legacy("nugget_tantalium");
    public static final RegistryObject<Item> OIL_TAR = legacy("oil_tar");
    public static final RegistryObject<Item> ORE_BEDROCK = legacy("ore_bedrock");
    public static final RegistryObject<Item> ORE_BYPRODUCT = legacy("byproduct");
    public static final RegistryObject<Item> ORE_CENTRIFUGED = legacy("ore_centrifuged");
    public static final RegistryObject<Item> ORE_CLEANED = legacy("ore_cleaned");
    public static final RegistryObject<Item> ORE_DEEPCLEANED = legacy("ore_deepcleaned");
    public static final RegistryObject<Item> ORE_ENRICHED = legacy("ore_enriched");
    public static final RegistryObject<Item> ORE_NITRATED = legacy("ore_nitrated");
    public static final RegistryObject<Item> ORE_NITROCRYSTALLINE = legacy("ore_nitrocrystalline");
    public static final RegistryObject<Item> ORE_PURIFIED = legacy("ore_purified");
    public static final RegistryObject<Item> ORE_SEARED = legacy("ore_seared");
    public static final RegistryObject<Item> ORE_SEPARATED = legacy("ore_separated");
    public static final RegistryObject<Item> PARTS_LEGENDARY = legacy("parts_legendary");
    public static final RegistryObject<Item> PART_GENERIC = legacy("part_generic");
    public static final RegistryObject<Item> PELLET_RTG_ACTINIUM = legacy("pellet_rtg_actinium");
    public static final RegistryObject<Item> PELLET_RTG_AMERICIUM = legacy("pellet_rtg_americium");
    public static final RegistryObject<Item> PELLET_RTG_COBALT = legacy("pellet_rtg_cobalt");
    public static final RegistryObject<Item> PELLET_RTG_DEPLETED = legacy("pellet_rtg_depleted");
    public static final RegistryObject<Item> PELLET_RTG_GOLD = legacy("pellet_rtg_gold");
    public static final RegistryObject<Item> PELLET_RTG_LEAD = legacy("pellet_rtg_lead");
    public static final RegistryObject<Item> PELLET_RTG_POLONIUM = legacy("pellet_rtg_polonium");
    public static final RegistryObject<Item> PELLET_RTG_RADIUM = legacy("pellet_rtg_radium");
    public static final RegistryObject<Item> PELLET_RTG_STRONTIUM = legacy("pellet_rtg_strontium");
    public static final RegistryObject<Item> PELLET_RTG_WEAK = legacy("pellet_rtg_weak");
    public static final RegistryObject<Item> PIPE = legacy("pipe");
    public static final RegistryObject<Item> PLANT_ITEM = legacy("plant_item");
    public static final RegistryObject<Item> PLATE_BISMUTH = legacy("plate_bismuth");
    public static final RegistryObject<Item> POWDER_ACTINIUM = legacy("powder_actinium");
    public static final RegistryObject<Item> POWDER_ASBESTOS = legacy("powder_asbestos");
    public static final RegistryObject<Item> POWDER_ASH = legacy("powder_ash");
    public static final RegistryObject<Item> POWDER_ASTATINE = legacy("powder_astatine");
    public static final RegistryObject<Item> POWDER_AUSTRALIUM = legacy("powder_australium");
    public static final RegistryObject<Item> POWDER_BAKELITE = legacy("powder_bakelite");
    public static final RegistryObject<Item> POWDER_BORON = legacy("powder_boron");
    public static final RegistryObject<Item> POWDER_BROMINE = legacy("powder_bromine");
    public static final RegistryObject<Item> POWDER_CAESIUM = legacy("powder_caesium");
    public static final RegistryObject<Item> POWDER_CEMENT = legacy("powder_cement");
    public static final RegistryObject<Item> POWDER_CERIUM = legacy("powder_cerium");
    public static final RegistryObject<Item> POWDER_COBALT = legacy("powder_cobalt");
    public static final RegistryObject<Item> POWDER_DINEUTRONIUM = legacy("powder_dineutronium");
    public static final RegistryObject<Item> POWDER_DURA_STEEL = legacy("powder_dura_steel");
    public static final RegistryObject<Item> POWDER_EUPHEMIUM = legacy("powder_euphemium");
    public static final RegistryObject<Item> POWDER_FERTILIZER = legacy("powder_fertilizer");
    public static final RegistryObject<Item> POWDER_IODINE = legacy("powder_iodine");
    public static final RegistryObject<Item> POWDER_LANTHANIUM = legacy("powder_lanthanium");
    public static final RegistryObject<Item> POWDER_LIMESTONE = legacy("powder_limestone");
    public static final RegistryObject<Item> POWDER_NEODYMIUM = legacy("powder_neodymium");
    public static final RegistryObject<Item> POWDER_NIOBIUM = legacy("powder_niobium");
    public static final RegistryObject<Item> POWDER_POLYMER = legacy("powder_polymer");
    public static final RegistryObject<Item> POWDER_POWER = legacy("powder_energy_alt");
    public static final RegistryObject<Item> POWDER_SCHRABIDATE = legacy("powder_schrabidate");
    public static final RegistryObject<Item> POWDER_SCHRABIDIUM = legacy("powder_schrabidium");
    public static final RegistryObject<Item> POWDER_STRONTIUM = legacy("powder_strontium");
    public static final RegistryObject<Item> POWDER_TENNESSINE = legacy("powder_tennessine");
    public static final RegistryObject<Item> POWDER_THORIUM = legacy("powder_thorium");
    public static final RegistryObject<Item> RAG = legacy("rag");
    public static final RegistryObject<Item> STEEL_DUST = POWDER_STEEL;
    public static final RegistryObject<Item> STEEL_INGOT = INGOT_STEEL;
    public static final RegistryObject<Item> STEEL_PLATE = PLATE_STEEL;
    public static final RegistryObject<Item> STEEL_SMALL_DUST = POWDER_STEEL_TINY;

    public static synchronized void register(DeferredRegister<Item> items) {
        // Intentionally no-op: component items are already declared in ModItems.
    }

    public static void creativeTab(CreativeModeTab.Output output) {
        // Current creative-tab population is event-driven in ModItems/ModTabs.
    }

    public static void genModel(ItemModelProvider provider) {
        // Current datagen model path is centralized in ModItems.genModel(ItemModelGen).
    }

    public static void languageSupport(LanguageProvider provider) {
        ModItems.languageSupport(provider);
    }

    public static void singleTexture(ItemModelProvider provider, Item item, String textureName) {
        // Kept for legacy callsites; modern model generation is handled by wrappers.
        provider.basicItem(item);
    }

    private static RegistryObject<Item> legacy(String path) {
        return ITEMS.getEntries().stream()
                .filter(entry -> entry.getId().getPath().equals(path))
                .findFirst()
                .orElseGet(() -> ITEMS.register(path, () -> new Item(new Item.Properties())));
    }
}
