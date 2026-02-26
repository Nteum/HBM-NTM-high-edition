package com.hbm.registries;

import com.hbm.datagen.tag.BlockTagsGen;
import com.hbm.datagen.tag.ItemTagsGen;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.hbm.registries.ModTags.Blocks;
import static com.hbm.registries.ModTags.Items.*;

/**
 * 你可能很奇怪我为什么把它放在这里了，这是因为我在写代码的过程中发现移植旧版的矿物辞典还是和tag生成更有关系
 * mc高版本为了数据驱动牺牲了太多，很多代码都要写得很别扭。
 * */
public class OreDictManager {
    // 存储矿物词典和物品的map
    public static final Map<TagKey<Item>, Supplier<? extends Item>> ORE_DICT_MAP = new HashMap<>();
    public static final Map<TagKey<Block>, Supplier<? extends Block>> ORE_BLOCK_MAP = new HashMap<>();

    public static final DictFrame WOOD = new DictFrame("wood");
    public static final DictFrame BONE = new DictFrame("bone");
    public static final DictFrame COAL = new DictFrame("coal");
    public static final DictFrame IRON = new DictFrame("iron");
    public static final DictFrame GOLD = new DictFrame("gold");
    public static final DictFrame LAPIS = new DictFrame("lapis");
    public static final DictFrame REDSTONE = new DictFrame("redstone");
    public static final DictFrame NETHERQUARTZ = new DictFrame("netherquartz");
    public static final DictFrame QUARTZ = new DictFrame("quartz");
    public static final DictFrame DIAMOND = new DictFrame("diamond");
    public static final DictFrame EMERALD = new DictFrame("emerald");
    /*
     * RADIOACTIVE
     */
    public static final DictFrame U = new DictFrame("uranium");
    public static final DictFrame U233 = new DictFrame("uranium_233");
    public static final DictFrame U235 = new DictFrame("uranium_235");
    public static final DictFrame U238 = new DictFrame("uranium_238");
    public static final DictFrame TH232 = new DictFrame("thorium");
    public static final DictFrame PU = new DictFrame("plutonium");
    public static final DictFrame PURG = new DictFrame("plutoniumrg");
    public static final DictFrame PU238 = new DictFrame("plutonium_238");
    public static final DictFrame PU239 = new DictFrame("plutonium_239");
    public static final DictFrame PU240 = new DictFrame("plutonium_240");
    public static final DictFrame PU241 = new DictFrame("plutonium_241");
    public static final DictFrame AM241 = new DictFrame("americium_241");
    public static final DictFrame AM242 = new DictFrame("americium_242");
    public static final DictFrame AMRG = new DictFrame("americiumrg");
    public static final DictFrame NP237 = new DictFrame("neptunium");
    public static final DictFrame PO210 = new DictFrame("polonium");
    public static final DictFrame TC99 = new DictFrame("technetium_99");
    public static final DictFrame RA226 = new DictFrame("radium_226");
    public static final DictFrame AC227 = new DictFrame("actinium_227");
    public static final DictFrame CO60 = new DictFrame("cobalt_60");
    public static final DictFrame AU198 = new DictFrame("gold_198");
    public static final DictFrame PB209 = new DictFrame("lead_209");
    public static final DictFrame SA326 = new DictFrame("schrabidium");
    public static final DictFrame SA327 = new DictFrame("solinium");
    public static final DictFrame SBD = new DictFrame("schrabidate");
    public static final DictFrame SRN = new DictFrame("schraranium");
    public static final DictFrame GH336 = new DictFrame("ghiorsium_336");
    public static final DictFrame MUD = new DictFrame("watzmud");
    /*
     * STABLE
     */
    /** TITANIUM */
    public static final DictFrame TI = new DictFrame("titanium");
    /** COPPER */
    public static final DictFrame CU = new DictFrame("copper");
    public static final DictFrame MINGRADE = new DictFrame("mingrade");
    public static final DictFrame ALLOY = new DictFrame("advancedalloy");
    /** TUNGSTEN */
    public static final DictFrame W = new DictFrame("tungsten");
    /** ALUMINUM */
    public static final DictFrame AL = new DictFrame("aluminum");
    public static final DictFrame STEEL = new DictFrame("steel");
    /** TECHNETIUM STEEL */
    public static final DictFrame TCALLOY = new DictFrame("tcalloy");
    /** CADMIUM STEEL */
    public static final DictFrame CDALLOY = new DictFrame("cdalloy");
    /** BISMUTH BRONZE */
    public static final DictFrame BBRONZE = new DictFrame("bismuthbronze");
    /** ARSENIC BRONZE */
    public static final DictFrame ABRONZE = new DictFrame("arsenicbronze");
    /** BISMUTH STRONTIUM CALCIUM COPPER OXIDE */
    public static final DictFrame BSCCO = new DictFrame("bscco");
    /** LEAD */
    public static final DictFrame PB = new DictFrame("lead");
    public static final DictFrame BI = new DictFrame("bismuth");
    public static final DictFrame AS = new DictFrame("arsenic");
    public static final DictFrame CA = new DictFrame("calcium");
    public static final DictFrame CD = new DictFrame("cadmium");
    /** TANTALUM */
    public static final DictFrame TA = new DictFrame("tantalum");
    public static final DictFrame COLTAN = new DictFrame("coltan");
    /** NIOBIUM */
    public static final DictFrame NB = new DictFrame("niobium");
    /** BERYLLIUM */
    public static final DictFrame BE = new DictFrame("beryllium");
    /** COBALT */
    public static final DictFrame CO = new DictFrame("cobalt");
    /** BORON */
    public static final DictFrame B = new DictFrame("boron");
    /** SILICON */
    public static final DictFrame SI = new DictFrame("silicon");
    public static final DictFrame GRAPHITE = new DictFrame("graphite");
    public static final DictFrame CARBON = new DictFrame("carbon");
    public static final DictFrame DURA = new DictFrame("durasteel");
    public static final DictFrame POLYMER = new DictFrame("polymer");
    public static final DictFrame BAKELITE = new DictFrame("bakelite");
    public static final DictFrame PET = new DictFrame("pet");
    public static final DictFrame PC = new DictFrame("polycarbonate");
    public static final DictFrame PVC = new DictFrame("pvc");
    public static final DictFrame LATEX = new DictFrame("latex");
    public static final DictFrame RUBBER = new DictFrame("rubber");
    public static final DictFrame MAGTUNG = new DictFrame("magnetizedtungsten");
    public static final DictFrame CMB = new DictFrame("cmbsteel");
    public static final DictFrame DESH = new DictFrame("workersalloy");
    public static final DictFrame STAR = new DictFrame("starmetal");
    public static final DictFrame GUNMETAL = new DictFrame("gunmetal");
    public static final DictFrame WEAPONSTEEL = new DictFrame("weaponsteel");
    public static final DictFrame BIGMT = new DictFrame("saturnite");
    public static final DictFrame FERRO = new DictFrame("ferrouranium");
    public static final DictFrame EUPH = new DictFrame("euphemium");
    public static final DictFrame DNT = new DictFrame("dineutronium");
    public static final DictFrame FIBER = new DictFrame("fiberglass");
    public static final DictFrame ASBESTOS = new DictFrame("asbestos");
    public static final DictFrame OSMIRIDIUM = new DictFrame("osmiridium");
    /*
     * DUST AND GEM ORES
     */
    /** SULFUR */
    public static final DictFrame S = new DictFrame("sulfur");
    /** SALTPETER/NITER */
    public static final DictFrame KNO = new DictFrame("saltpeter");
    /** FLUORITE */
    public static final DictFrame F = new DictFrame("fluorite");
    public static final DictFrame LIGNITE = new DictFrame("lignite");
    public static final DictFrame COALCOKE = new DictFrame("coalcoke");
    public static final DictFrame PETCOKE = new DictFrame("petcoke");
    public static final DictFrame LIGCOKE = new DictFrame("lignitecoke");
    public static final DictFrame CINNABAR = new DictFrame("cinnabar");
    public static final DictFrame BORAX = new DictFrame("borax");
    public static final DictFrame CHLOROCALCITE = new DictFrame("chlorocalcite");
    public static final DictFrame MOLYSITE = new DictFrame("molysite");
    public static final DictFrame SODALITE = new DictFrame("sodalite");
    public static final DictFrame VOLCANIC = new DictFrame("volcanic");
    public static final DictFrame HEMATITE = new DictFrame("hematite");
    public static final DictFrame MALACHITE = new DictFrame("malachite");
    public static final DictFrame LIMESTONE = new DictFrame("limestone");
    public static final DictFrame SLAG = new DictFrame("slag");
    public static final DictFrame BAUXITE = new DictFrame("bauxite");
    public static final DictFrame CRYOLITE = new DictFrame("cryolite");
    /*
     * HAZARDS, MISC
     */
    /** LITHIUM */
    public static final DictFrame LI = new DictFrame("lithium");
    /** SODIUM */
    public static final DictFrame NA = new DictFrame("sodium");
    /*
     * PHOSPHORUS
     */
    public static final DictFrame P_WHITE = new DictFrame("whitephosphorus");
    public static final DictFrame P_RED = new DictFrame("redphosphorus");
    /*
     * RARE METALS
     */
    public static final DictFrame AUSTRALIUM = new DictFrame("australium");
    public static final DictFrame REIIUM = new DictFrame("reiium");
    public static final DictFrame WEIDANIUM = new DictFrame("weidanium");
    public static final DictFrame UNOBTAINIUM = new DictFrame("unobtainium");
    public static final DictFrame VERTICIUM = new DictFrame("verticium");
    public static final DictFrame DAFFERGON = new DictFrame("daffergon");
    /*
     * RARE EARTHS
     */
    public static final DictFrame RAREEARTH = new DictFrame("rareearth");
    /** LANTHANUM */
    public static final DictFrame LA = new DictFrame("lanthanum");
    /** ZIRCONIUM */
    public static final DictFrame ZR = new DictFrame("zirconium");
    /** NEODYMIUM */
    public static final DictFrame ND = new DictFrame("neodymium");
    /** CERIUM */
    public static final DictFrame CE = new DictFrame("cerium");
    /*
     * NITAN
     */
    /** IODINE */
    public static final DictFrame I = new DictFrame("iodine");
    /** ASTATINE */
    public static final DictFrame AT = new DictFrame("astatine");
    /** CAESIUM */
    public static final DictFrame CS = new DictFrame("caesium");
    /** STRONTIUM */
    public static final DictFrame ST = new DictFrame("strontium");
    /** BROMINE */
    public static final DictFrame BR = new DictFrame("bromine");
    /** TENNESSINE */
    public static final DictFrame TS = new DictFrame("tennessine") ;
    /*
     * FISSION FRAGMENTS
     */
    public static final DictFrame SR = new DictFrame("strontium");
    public static final DictFrame SR90 = new DictFrame("strontium_90");
    public static final DictFrame I131 = new DictFrame("iodine_131");
    public static final DictFrame XE135 = new DictFrame("xenon_135");
    public static final DictFrame CS137 = new DictFrame("caesium_137");
    public static final DictFrame AT209 = new DictFrame("astatine_209");

    static {
        IRON.ingot(Items.IRON_INGOT.builtInRegistryHolder());
        ALLOY.ingot(ModItems.INGOT_ADVANCED_ALLOY);
        U.nugget(ModItems.NUGGET_ZIRCONIUM);
        STEEL.ingot(ModItems.INGOT_STEEL).dust(ModItems.POWDER_STEEL).dustSmall(ModItems.POWDER_STEEL_TINY).plate(ModItems.PLATE_STEEL);
    }

    /**
     * 添加需要的物品tag
     * */
    public static void addItemTags(ItemTagsGen provider){
        ORE_DICT_MAP.forEach((key, value) -> {
            if (value.get() != null) provider.tag(key).add(value.get());
        });
        SERIALIZE_MAP.forEach((key,value) -> {
            if (!value.isEmpty()) provider.tag(key).addTags(value.values().toArray(TagKey[]::new));
        });
    }
    /**
     * 添加需要的方块tag
     * 其实我原本想弄一个统一的函数加tag，但真没办法保证ItemTagGen和BlockTagGen它们注册的时间相同，只能在它们注册的时候分别调用。
     * */
    public static void addBlockTags(BlockTagsGen provider){
        ORE_BLOCK_MAP.forEach((key, value) -> {
            if (value.get() != null) provider.tag(key).add(value.get());
        });
        Blocks.SERIALIZE_MAP.forEach((key,value) -> {
            if (!value.isEmpty()) provider.tag(key).addTags(value.values().toArray(TagKey[]::new));
        });
    }

    public static class DictFrame {
        public String name;
        public IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> holder;

        public DictFrame(String name){
            this.name = name;
        }
        public TagKey<Item> nugget(){
            return NUGGETS.get(this.name);
        }
        public TagKey<Item> tiny(){
            return NUGGETS.get(this.name);
        }
        public TagKey<Item> bolt(){
            return NUGGETS.get(this.name);
        }
        public TagKey<Item> ingot(){
            return INGOTS.get(this.name);
        }
        public TagKey<Item> smallDusts(){
            return SMALL_DUSTS.get(this.name);
        }
        public TagKey<Item> dust(){
            return DUSTS.get(this.name);
        }
        public TagKey<Item> gem(){
            return GEMS.get(this.name);
        }
        public TagKey<Item> crystal(){
            return CRYSTALS.get(this.name);
        }
        public TagKey<Item> plate(){
            return PLATES.get(this.name);
        }
        public TagKey<Item> plateCast(){
            return CAST_PLATES.get(this.name);
        }
        public TagKey<Item> plateWelded(){
            return NUGGETS.get(this.name);
        }
        public TagKey<Item> heavyComp(){
            return NUGGETS.get(this.name);
        }
        public TagKey<Item> wireFine(){
            return NUGGETS.get(this.name);
        }
        public TagKey<Item> wireDense(){
            return NUGGETS.get(this.name);
        }
        public TagKey<Item> shell(){
            return NUGGETS.get(this.name);
        }
        public TagKey<Item> pipe(){
            return NUGGETS.get(this.name);
        }
        public TagKey<Item> billet(){
            return BILLETS.get(this.name);
        }
        public TagKey<Block> block(){
            return Blocks.STORAGE_BLOCKS.get(this.name);
        }
        public TagKey<Block> ore(){
            return Blocks.ORES.get(this.name);
        }
        public TagKey<Item> fragment(){
            return NUGGETS.get(this.name);
        }
        public TagKey<Item> lightBarrel(){
            return NUGGETS.get(this.name);
        }
        public TagKey<Item> heavyBarrel(){
            return NUGGETS.get(this.name);
        }
        public TagKey<Item> lightReceiver(){
            return NUGGETS.get(this.name);
        }
        public TagKey<Item> heavyReceiver(){
            return NUGGETS.get(this.name);
        }
        public TagKey<Item> mechanism(){
            return NUGGETS.get(this.name);
        }
        public TagKey<Item> stock(){
            return NUGGETS.get(this.name);
        }
        public TagKey<Item> grip(){
            return NUGGETS.get(this.name);
        }
        public TagKey<Item> all(){
            return NUGGETS.get(this.name);
        }
        public <I extends Item>DictFrame nugget(Supplier<I> nugget) {
            TagKey<Item> tag = forgeTag("nuggets/" + name);
            ORE_DICT_MAP.put(tag, nugget);
            NUGGETS.put(this.name, tag);
            return this;
        }
        public <I extends Item>DictFrame ingot(Supplier<I> ingot) {
            TagKey<Item> tag = forgeTag("ingots/" + name);
            ORE_DICT_MAP.put(tag, ingot);
            INGOTS.put(this.name, tag);
            return this;
        }
        public <I extends Item>DictFrame dustSmall(Supplier<I> dustSmall) {
            TagKey<Item> tag = forgeTag("small_dusts/" + name);
            ORE_DICT_MAP.put(tag, dustSmall);
            SMALL_DUSTS.put(this.name, tag);
            return this;
        }
        public <I extends Item>DictFrame dust(Supplier<I> dust) {
            TagKey<Item> tag = forgeTag("dusts/" + name);
            ORE_DICT_MAP.put(tag, dust);
            DUSTS.put(this.name, tag);
            return this;
        }
        public <I extends Item>DictFrame gem(Supplier<I> gem) {
            TagKey<Item> tag = forgeTag("gems/" + name);
            ORE_DICT_MAP.put(tag, gem);
            GEMS.put(this.name, tag);
            return this;
        }
        public <I extends Item>DictFrame crystal(Supplier<I> crystal) {
            TagKey<Item> tag = forgeTag("crystals/" + name);
            ORE_DICT_MAP.put(tag, crystal);
            CRYSTALS.put(this.name, tag);
            return this;
        }
        public <I extends Item>DictFrame plate(Supplier<I> plate) {
            TagKey<Item> tag = forgeTag("plates/" + name);
            ORE_DICT_MAP.put(tag, plate);
            PLATES.put(this.name, tag);
            return this;
        }
        public <I extends Item>DictFrame plateCast(Supplier<I> plate) {
            TagKey<Item> tag = forgeTag("cast_plates/" + name);
            ORE_DICT_MAP.put(tag, plate);
            CAST_PLATES.put(this.name, tag);
            return this;
        }
        public <I extends Item>DictFrame billet(Supplier<I> billet) {
            TagKey<Item> tag = forgeTag("billets/" + name);
            ORE_DICT_MAP.put(tag, billet);
            BILLETS.put(this.name, tag);
            return this;
        }

        public <B extends Block>DictFrame block(Supplier<B> block) {
            TagKey<Block> tag = Blocks.forgeTag("storage_blocks/" + name);
            ORE_BLOCK_MAP.put(tag, block);
            Blocks.STORAGE_BLOCKS.put(this.name, tag);
            return this;
        }
        public <B extends Block>DictFrame oreStone(Supplier<B> ore) {
            Blocks.ORES.putIfAbsent(this.name, Blocks.forgeTag("ores/" + name));
            TagKey<Block> tag = Blocks.forgeTag(Tags.Blocks.ORE_BEARING_GROUND_STONE.location().getPath() + "/" + name);
            ORE_BLOCK_MAP.put(tag, ore);
            Blocks.STONE_ORES.put(this.name, tag);
            return this;
        }
        public <B extends Block>DictFrame oreDeepSlate(Supplier<B> ore) {
            Blocks.ORES.putIfAbsent(this.name, Blocks.forgeTag("ores/" + name));
            TagKey<Block> tag = Blocks.forgeTag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE.location().getPath() + "/" + name);
            ORE_BLOCK_MAP.put(tag, ore);
            Blocks.DEEPSLATE_ORES.put(this.name, tag);
            return this;
        }
        public <B extends Block>DictFrame oreNether(Supplier<B> ore) {
            Blocks.ORES.putIfAbsent(this.name, Blocks.forgeTag("ores/" + name));
            TagKey<Block> tag = Blocks.forgeTag(Tags.Blocks.ORES_IN_GROUND_NETHERRACK.location().getPath() + "/" + name);
            ORE_BLOCK_MAP.put(tag, ore);
            Blocks.NETHER_ORES.put(this.name, tag);
            return this;
        }
    }
}
