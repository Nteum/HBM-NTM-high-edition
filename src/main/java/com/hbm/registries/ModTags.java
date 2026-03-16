package com.hbm.registries;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.api.resource.OreType;
import com.hbm.registries.OreDictManager.DictFrame;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 模组特有的tag
 * 有的tag是放在原版和forge的tag里的，这里不代表模组物品的所有tag
 * */
public class ModTags {
    public static class Blocks{
        public static final Map<OreType, TagKey<Block>> MOD_ORES = new HashMap<>();
        static {
            for (OreType type : OreType.values()) {
                MOD_ORES.put(type,forgeTag("ores/"+type.key));
            }
        }
        // 包含同类矿物所有对应tag的tag
        public static final Map<String, TagKey<Block>> ORES = new HashMap<>();
        public static final Map<String, TagKey<Block>> STONE_ORES = new HashMap<>();
        public static final Map<String, TagKey<Block>> NETHER_ORES = new HashMap<>();
        public static final Map<String, TagKey<Block>> DEEPSLATE_ORES = new HashMap<>();
        public static final Map<String, TagKey<Block>> STORAGE_BLOCKS = new HashMap<>();
        public static final TagKey<Block> STORAGE_BLOCK  = forgeTag("storage_blocks");
        public static final Map<TagKey<Block>, Map<String, TagKey<Block>>> SERIALIZE_MAP = Map.of(Tags.Blocks.ORES,ORES, Tags.Blocks.ORES_IN_GROUND_STONE,STONE_ORES,
                Tags.Blocks.ORES_IN_GROUND_DEEPSLATE,DEEPSLATE_ORES,  Tags.Blocks.ORES_IN_GROUND_NETHERRACK,NETHER_ORES, STORAGE_BLOCK,STORAGE_BLOCKS);

        public static final TagKey<Block> BATTERY = forgeTag("battery");
        public static final TagKey<Block> ANVIL = tag("anvil");
        public static final TagKey<Block> MACHINE = forgeTag("machine");
        public static final TagKey<Block> ENERGY_TRANSMITTER = tag("energy_transmitter");

        public static final Set<TagKey<Block>> BLOCK_SHAPES = Set.of(Tags.Blocks.ORES, Tags.Blocks.STORAGE_BLOCKS);
        public static final List<TagGenEntry<Block>> LIST_TAG_GEN_REQ = new ArrayList<>();

        //注册本模组tag，如果只限于本模组使用，请注册此tag
        private static TagKey<Block> tag(String pName) {
            return TagKey.create(Registries.BLOCK, HBM.rl(pName));
        }
        //注册forge tag，如果希望兼容其他模组，请注册此tag
        public static TagKey<Block> forgeTag(String pName) {
            return TagKey.create(Registries.BLOCK, new ResourceLocation("forge",pName));
        }
        protected static TagKey<Block> convertToBlockTag(TagKey<Item> itemTag) {
            return BlockTags.create(itemTag.location());
        }

        protected static TagKey<Block> subBlockTag(TagKey<Block> base, TagKey<Item> matter) {
            return BlockTags.create(new ResourceLocation(base.location().getNamespace(),
                    base.location().getPath() + "/" + matter.location().getPath()));
        }
        public static TagGenEntry<Block> make(TagKey<Item> key){
            TagGenEntry<Block> entry = new TagGenEntry<>(convertToBlockTag(key));
            LIST_TAG_GEN_REQ.add(entry);
            return entry;
        }
    }
    public static class Items{
        public static final TagKey<Item> RAW_MATERIAL  = forgeTag("raw_materials");
        public static final Map<String, TagKey<Item>> NUGGETS = new HashMap<>();
        public static final TagKey<Item> NUGGET  = forgeTag("nuggets");
        public static final Map<String, TagKey<Item>> INGOTS = new HashMap<>();
        public static final Map<String, TagKey<Item>> DUSTS = new HashMap<>();
        public static final Map<String, TagKey<Item>> SMALL_DUSTS = new HashMap<>();
        public static final TagKey<Item> SMALL_DUST  = forgeTag("small_dusts");
        public static final Map<String, TagKey<Item>> GEMS = new HashMap<>();
        public static final Map<String, TagKey<Item>> CRYSTALS = new HashMap<>();
        public static final TagKey<Item> CRYSTAL  = forgeTag("crystals");
        public static final Map<String, TagKey<Item>> PLATES = new HashMap<>();
        public static final TagKey<Item> PLATE  = forgeTag("plates");
        public static final Map<String, TagKey<Item>> CAST_PLATES = new HashMap<>();
        public static final TagKey<Item> CAST_PLATE  = forgeTag("cast_plates");
        public static final Map<String, TagKey<Item>> BILLETS = new HashMap<>();
        public static final TagKey<Item> BILLET  = forgeTag("billets");
        public static final Map<TagKey<Item>, Map<String, TagKey<Item>>> SERIALIZE_MAP = Map.of(NUGGET,NUGGETS,  Tags.Items.INGOTS,INGOTS,
                Tags.Items.DUSTS,DUSTS,  SMALL_DUST,SMALL_DUSTS,  Tags.Items.GEMS,GEMS,  CRYSTAL,CRYSTALS,  PLATE,PLATES,  CAST_PLATE, CAST_PLATES,  BILLET, BILLETS);

        public static final TagKey<Item> BATTERY = forgeTag("battery");
        public static final TagKey<Item> CHARGEABLE = forgeTag("chargeable");
        public static final TagKey<Item> UPGRADE = tag("upgrade");
        public static final TagKey<Item> MISSILE = tag("missile");
        public static final TagKey<Item> SHREDDER_BLADES = tag("shredder_blades");
        // 需要和其他mod兼容的材料，用于替代旧版的OreDiction
        public static final TagKey<Item> INGOT_STEEL = forgeTag(HBMKey.link(HBMKey.INGOTS, HBMKey.STEEL));
        public static final TagKey<Item> DUST_STEEL = forgeTag(HBMKey.link(HBMKey.DUSTS, HBMKey.STEEL));
        public static final TagKey<Item> SMALL_DUST_STEEL = forgeTag(HBMKey.link(HBMKey.SMALL_DUSTS, HBMKey.STEEL));
        public static final TagKey<Item> PLATE_STEEL = forgeTag(HBMKey.link(HBMKey.PLATES, HBMKey.STEEL));
        public static final TagKey<Item> INGOT_URANIUM = forgeTag(HBMKey.link(HBMKey.INGOTS, HBMKey.URANIUM));
//        public static final TagKey<Item> INGOT_TITANIUM = forgeTag(HBMKey.link(HBMKey.INGOTS, HBMKey.TITANIUM));
        public static final TagKey<Item> INGOT_ALUMINIUM = forgeTag(HBMKey.link(HBMKey.INGOTS, HBMKey.TITANIUM));
        public static final TagKey<Item> INGOT_LEAD = forgeTag(HBMKey.link(HBMKey.INGOTS, HBMKey.LEAD));
        public static final TagKey<Item> DUST_QUARTZ = forgeTag(HBMKey.link(HBMKey.DUSTS, HBMKey.QUARTZ));
        public static final TagKey<Item> DUST_LAPIS = forgeTag(HBMKey.link(HBMKey.DUSTS, HBMKey.LAPIS));
        public static final TagKey<Item> DUST_DIAMOND = forgeTag(HBMKey.link(HBMKey.DUSTS, HBMKey.DIAMOND));
        public static final TagKey<Item> DUST_EMERALD = forgeTag(HBMKey.link(HBMKey.DUSTS, HBMKey.EMERALD));
        public static final TagKey<Item> BIOMASS = forgeTag(HBMKey.BIOMASS);
        public static final TagKey<Item> COKE = forgeTag(HBMKey.COKE);
        public static final TagKey<Item> DUST_COAL = forgeTag(HBMKey.link(HBMKey.DUSTS, HBMKey.COAL));
        public static final TagKey<Item> DUST_LIGNITE = forgeTag(HBMKey.link(HBMKey.DUSTS, HBMKey.LIGNITE));
        public static final TagKey<Item> SAWDUST = forgeTag(HBMKey.SAWDUST);

        public static final TagKey<Item> WIRE_FINE = tag(HBMKey.WIRE);

        public static TagKey<Item> tag(String pName) {
            return TagKey.create(Registries.ITEM, HBM.rl(pName));
        }
        public static TagKey<Item> forgeTag(String pName) {
            return TagKey.create(Registries.ITEM, new ResourceLocation("forge",pName));
        }
        public static TagKey<Item> subTag(TagKey<Item> parent, TagKey<Item> child){
            return TagKey.create(Registries.ITEM, parent.location().withSuffix("/" + child.location().getPath()));
        }
        /**
         * 在游戏启动的时候加载相应的物品tag进来放到一些map里，这是因为不同材料的tag太多了，一个个列tag太麻烦，把它们放在一个map或许是比较好的选择。
         * */
        public static void loadTag(){
            BuiltInRegistries.ITEM.getTags().forEach(pair -> {
                TagKey<Item> first = pair.getFirst();
                if (first.location().getNamespace().equals(HBM.MODID)){
                    first.location().getPath().contains("nuggets");
                }
            });
        }

        /**
         * 自动生成的尝试
         * */
        public static final List<TagGenEntry<Item>> LIST_TAG_GEN_REQ = new ArrayList<>();

        public static final TagKey<Item> NORMAL = tag("normal");
        public static final TagKey<Item> METAL = tag("metal");
        // 材料形状
        static Map<TagKey<Item>, MatterFormat> MATTER_FORMATS = new HashMap<>();
        private static final int SIZE_NUGGIT = 16;              // 物质形式遵循匠魂的数值，一个粒相当于16mb
        private static final int SIZE_INGOT = SIZE_NUGGIT * 9;
        public static final TagKey<Item> QUANTUM = addMatterFormat(forgeTag("quantum"), false, 1);
        public static final TagKey<Item> STOCK = addMatterFormat(forgeTag("stock"), false, SIZE_INGOT * 4);
        public static final TagKey<Item> GRIP =	addMatterFormat(forgeTag("grip"), false, SIZE_INGOT * 2);

        public static final TagKey<Item> FRAGMENT =	addMatterFormat(forgeTag("fragment"), false, SIZE_INGOT * 2);
        public static final TagKey<Item> DUST =	addMatterFormat(forgeTag("dust"), false, SIZE_INGOT * 2);
//        public static final TagKey<Item> PLATE =	addMatterFormat(forgeTag("plate"), false, SIZE_INGOT * 2);
        public static final TagKey<Item> DENSEWIRE =	addMatterFormat(forgeTag("densewire"), false, SIZE_INGOT * 2);
        public static final TagKey<Item> CASTPLATE =	addMatterFormat(forgeTag("castplate"), false, SIZE_INGOT * 2);
        public static final TagKey<Item> WELDEDPLATE =	addMatterFormat(forgeTag("weldedplate"), false, SIZE_INGOT * 2);
        public static final TagKey<Item> SHELL =	addMatterFormat(forgeTag("shell"), false, SIZE_INGOT * 2);

        public static final Map<TagKey<Block>, TagKey<Item>> BLOCK_ITEM_TRANS = Map.of(Tags.Blocks.ORES, Tags.Items.ORES, Tags.Blocks.STORAGE_BLOCKS,Tags.Items.STORAGE_BLOCKS);
        // 待生成tag的列表
        public static TagGenEntry<Item> make(TagKey<Item> key){
            TagGenEntry<Item> entry = new TagGenEntry<>(key);
            LIST_TAG_GEN_REQ.add(entry);
            return entry;
        }

        private static TagKey<Item> addMatterFormat(TagKey<Item> key, boolean autoGen, int quantity){
            MATTER_FORMATS.put(key, new MatterFormat(autoGen, quantity));
            return key;
        }
    }
    public static class MatterFormat{
        boolean autoGen = true; // 是否自动生成
        int quantity = 72;      // 等效物质的量，默认8为一个粒，72为一个锭
        Set<TagKey<Item>> content;
        public MatterFormat(boolean autoGen, int quantity){
            this.autoGen = autoGen;
            this.quantity = quantity;
        }
        public MatterFormat add(TagKey<Item> ... keys){
            if (content == null) content = new HashSet<>();
            content.addAll(Arrays.stream(keys).toList());
            return this;
        }
    }
    public static class HBMMatter{
        String name;
        TagKey<Item> matterKey;
        Map<TagKey<Item>, TagKey<Item>> shapes;         // 物品形状
        Map<TagKey<Block>, TagKey<Block>> blockShapes;  // 方块形状
        TagGenEntry<Item> genEntry;
        TagGenEntry<Block> blockGen;
        public int solidColorLight = 0xFF4A00;
        public int solidColorDark = 0x802000;
        public int moltenColor = 0xFF4A00;
        // 0 - 不可融化；1 - 可被融化
        public byte smeltProperty = 0;
        // 融化后的流体，用于兼容匠魂系统，不是所有matter都可融化
        RegistryObject<FluidType> fluidType;
        RegistryObject<Fluid> source;
        public HBMMatter(String name){
            this.matterKey = Items.forgeTag(name);
        }
        public HBMMatter(String name, int solidColorLight, int solidColorDark, int moltenColor){
            this(name);
            color(solidColorLight, solidColorDark, moltenColor);
        }
        public HBMMatter color(int solidColorLight, int solidColorDark, int moltenColor){
            this.solidColorLight = solidColorLight;
            this.solidColorDark = solidColorDark;
            this.moltenColor = moltenColor;
            return this;
        }
        @SafeVarargs
        public final HBMMatter shapes(TagKey<Item>... shapes){
            if (this.shapes == null) this.shapes = new HashMap<>();
            List<TagKey<Item>> itemTags = new ArrayList<>();
            List<TagKey<Block>> blockTags = new ArrayList<>();
            for (TagKey<Item> shape : shapes) {
                this.shapes.put(shape, Items.subTag(shape, matterKey));
                if (isPhysicallyPresent(shape)) {
                    if (this.blockShapes == null) this.blockShapes = new HashMap<>();
                    TagKey<Block> blockBase = Blocks.convertToBlockTag(shape);
                    this.blockShapes.put(blockBase, Blocks.subBlockTag(blockBase, matterKey));
                    blockTags.add(blockBase);
                }else {
                    itemTags.add(shape);
                }
            }
            if (genEntry == null) genEntry = Items.make(matterKey);
            genEntry.addKeyAutogen(itemTags.toArray(TagKey[]::new));
            if (blockGen == null) blockGen = Blocks.make(matterKey);
            blockGen.addKeyAutogen(blockTags.toArray(TagKey[]::new));
            return this;
        }
        private boolean isPhysicallyPresent(TagKey<Item> shape) {
            return Blocks.BLOCK_SHAPES.contains(Blocks.convertToBlockTag(shape));
        }
        public TagKey<Item> key(){
            return matterKey;
        }
        public HBMMatter gen(Consumer<TagGenEntry<Item>> consumer){
            consumer.accept(genEntry);
            return this;
        }
        public TagKey<Item> getShape(TagKey<Item> shape){
            return this.shapes.get(shape);
        }
        public HBMMatter toFluid() {
            // 为每种金属生成唯一的 FluidType（用于区分颜色）
            // 这里可以巧妙地把颜色作为温度参考
            this.fluidType = ModFluids.FLUID_TYPES.register(name + "_type", () -> new FluidType(FluidType.Properties.create().temperature(moltenColor).descriptionId("fluid." + HBM.MODID + "." + name)));
            this.source = ModFluids.FLUIDS.register("molten_" + name, () -> new ForgeFlowingFluid.Source(new ForgeFlowingFluid.Properties(fluidType, null, null)));
            return this;
        }
        public Fluid fluid(){
            return this.source.get();
        }
        public FluidType fluidType(){
            return this.fluidType.get();
        }
        public boolean canMolten(){
            return smeltProperty != 0 && this.source != null && this.fluidType != null;
        }
        // 锭的tag
        public TagKey<Item> ingot(){
            return getShape(Tags.Items.INGOTS);
        }
        // 粒的tag
        public TagKey<Item> nugget(){
            return getShape(Tags.Items.NUGGETS);
        }
        // 板
        public TagKey<Item> plate(){
            return getShape(Items.PLATE);
        }
    }
    public static class TagGenEntry<T>{
        public TagKey<T> key;
        public Set<TagKey<T>> keyIn;        // 需要加入这个key的key
        public Set<TagKey<T>> keyOut;       // 这个key需要加入的key
        public Set<TagKey<T>> keyAutoGen;   // 这个key不仅需要加入还需要自动生成下级条目的key
        public TagGenEntry(TagKey<T> key){
            this.key = key;
        }
        @SafeVarargs
        public final TagGenEntry<T> addKeyIn(TagKey<T> ... keys){
            if (keyIn == null) keyIn = new HashSet<>();
            keyIn.addAll(List.of(keys));
            return this;
        }
        @SafeVarargs
        public final TagGenEntry<T> addKeyOut(TagKey<T> ... keys){
            if (keyOut == null) keyOut = new HashSet<>();
            keyOut.addAll(List.of(keys));
            return this;
        }
        @SafeVarargs
        public final TagGenEntry<T> addKeyAutogen(TagKey<T> ... keys){
            if (keyAutoGen == null) keyAutoGen = new HashSet<>();
            keyAutoGen.addAll(List.of(keys));
            return this;
        }
        public TagKey<T> key(){
            return this.key;
        }
    }
}
