package com.hbm.registries;

import com.hbm.HBM;
import com.hbm.api.resource.OreType;
import com.hbm.registries.OreDictManager.DictFrame;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;

import java.util.HashMap;
import java.util.Map;

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
        public static final TagKey<Block> BLOCK_COKE = tag("block_coke");

        //注册本模组tag，如果只限于本模组使用，请注册此tag
        private static TagKey<Block> tag(String pName) {
            return TagKey.create(Registries.BLOCK, HBM.rl(pName));
        }
        //注册forge tag，如果希望兼容其他模组，请注册此tag
        public static TagKey<Block> forgeTag(String pName) {
            return TagKey.create(Registries.BLOCK, new ResourceLocation("forge",pName));
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

        public static final TagKey<Item> COKE = forgeTag("coke");
        public static final TagKey<Item> LIGNITE = forgeTag("lighnite");
        public static final TagKey<Item> WOOD = forgeTag("wood");
        public static final TagKey<Item> SAPLING = forgeTag("sapling");

        public static final TagKey<Item> BLOCK_COKE = ItemTags.create(Blocks.BLOCK_COKE.location());
        public static TagKey<Item> tag(String pName) {
            return TagKey.create(Registries.ITEM, HBM.rl(pName));
        }
        public static TagKey<Item> forgeTag(String pName) {
            return TagKey.create(Registries.ITEM, new ResourceLocation("forge",pName));
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
    }
}
