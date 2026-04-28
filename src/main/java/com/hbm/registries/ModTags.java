package com.hbm.registries;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.Inventory.material.HBMMatForm;
import com.hbm.api.resource.OreType;
import com.hbm.registries.OreDictManager.DictFrame;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
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
        public static final TagKey<Block> BLOCK_COKE = forgeTag("block_coke");
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
        public static TagKey<Block> convertToBlockTag(TagKey<Item> itemTag) {
            return BlockTags.create(itemTag.location());
        }

        public static TagKey<Block> subBlockTag(TagKey<Block> base, TagKey<Item> matter) {
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
        public static final TagKey<Item> INGOT_ALUMINIUM = forgeTag(HBMKey.link(HBMKey.INGOTS, HBMKey.ALUMINIUM));
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

//        public static final TagKey<Item> WIRE_FINE = tag(HBMKey.WIRE);

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
//        // 材料形状
//        static Map<TagKey<Item>, HBMMatForm> MATTER_FORMATS = new HashMap<>();
        // 所有材质依赖的东西
        public static final TagKey<Item> ANY = forgeTag("any");
        public static final TagKey<Item> QUANTUM = forgeTag("quantum");
        public static final TagKey<Item> TINY = forgeTag("tiny");
        public static final TagKey<Item> DUST = forgeTag("dust");
        public static final TagKey<Item> SMALL_DUST  = forgeTag("small_dusts");
        public static final TagKey<Item> FRAGMENT = forgeTag("fragment");
        public static final TagKey<Item> QUART = forgeTag("quart");
        public static final TagKey<Item> CRYSTAL  = forgeTag("crystals");
        public static final TagKey<Item> WIRE = forgeTag("wires/fine");
        public static final TagKey<Item> DENSEWIRE = forgeTag("wires/dense");
        public static final TagKey<Item> BOLT = forgeTag("bolts");
        public static final TagKey<Item> PLATE = forgeTag("plates");
        public static final TagKey<Item> CASTPLATE = forgeTag("plates/cast");
        public static final TagKey<Item> WELDEDPLATE = forgeTag("plates/welded");
        public static final TagKey<Item> PLATES_TRIPLE = forgeTag("plates/triple");
        public static final TagKey<Item> PLATES_SEXTUPLE = forgeTag("plates/sextuple");
        public static final TagKey<Item> SHELL = forgeTag("shells");
        public static final TagKey<Item> PIPE = forgeTag("pipes");
        public static final TagKey<Item> BILLET  = forgeTag("billets");
        public static final TagKey<Item> LIGHTBARREL = forgeTag("barrels/light");
        public static final TagKey<Item> HEAVYBARREL = forgeTag("barrels/heavy");
        public static final TagKey<Item> LIGHTRECEIVER = forgeTag("receivers/light");
        public static final TagKey<Item> HEAVYRECEIVER = forgeTag("receivers/heavy");
        public static final TagKey<Item> MECHANISM = forgeTag("mechanisms/gun");
        public static final TagKey<Item> STOCK = forgeTag("stocks");
        public static final TagKey<Item> GRIP = forgeTag("grips");

        // 旧oredict的东西，早晚要清理
        public static final Map<String, TagKey<Item>> NUGGETS = new HashMap<>();
        public static final Map<String, TagKey<Item>> INGOTS = new HashMap<>();
        public static final Map<String, TagKey<Item>> DUSTS = new HashMap<>();
        public static final Map<String, TagKey<Item>> SMALL_DUSTS = new HashMap<>();
        public static final Map<String, TagKey<Item>> GEMS = new HashMap<>();
        public static final Map<String, TagKey<Item>> CRYSTALS = new HashMap<>();
        public static final Map<String, TagKey<Item>> PLATES = new HashMap<>();
        public static final Map<String, TagKey<Item>> CAST_PLATES = new HashMap<>();
        public static final Map<String, TagKey<Item>> BILLETS = new HashMap<>();
        public static final Map<TagKey<Item>, Map<String, TagKey<Item>>> SERIALIZE_MAP = Map.of(Tags.Items.NUGGETS,NUGGETS,  Tags.Items.INGOTS,INGOTS,
                Tags.Items.DUSTS,DUSTS,  SMALL_DUST,SMALL_DUSTS,  Tags.Items.GEMS,GEMS,  CRYSTAL,CRYSTALS,  PLATE,PLATES,  CASTPLATE, CAST_PLATES,  BILLET, BILLETS);

        public static final Map<TagKey<Block>, TagKey<Item>> BLOCK_ITEM_TRANS = Map.of(Tags.Blocks.ORES, Tags.Items.ORES, Tags.Blocks.STORAGE_BLOCKS,Tags.Items.STORAGE_BLOCKS);
        // 待生成tag的列表
        public static TagGenEntry<Item> make(TagKey<Item> key){
            TagGenEntry<Item> entry = new TagGenEntry<>(key);
            LIST_TAG_GEN_REQ.add(entry);
            return entry;
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

    public static class Fluids{
        public static TagKey<Fluid> MOLTEN = tag("molten");
        public static TagKey<Fluid> tag(String pName) {
            return TagKey.create(Registries.FLUID, HBM.rl(pName));
        }
        public static TagKey<Fluid> forgeTag(String pName) {
            return TagKey.create(Registries.FLUID, new ResourceLocation("forge",pName));
        }
    }
}
