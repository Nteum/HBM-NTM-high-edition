package com.hbm.registries;

import com.hbm.main.HBMxx;
import com.hbm.modsetting.resource.ElementUtils;
import com.hbm.modsetting.resource.OreType;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
        public static final TagKey<Block> BATTERY = forgeTag("battery");

        //注册本模组tag，如果只限于本模组使用，请注册此tag
        private static TagKey<Block> tag(String pName) {
            return TagKey.create(Registries.BLOCK, HBMxx.hbm(pName));
        }
        //注册forge tag，如果希望兼容其他模组，请注册此tag
        private static TagKey<Block> forgeTag(String pName) {
            return TagKey.create(Registries.BLOCK, new ResourceLocation("forge",pName));
        }
    }
    public static class Items{
        public static final TagKey<Item> BATTERY = forgeTag("battery");
        private static TagKey<Item> tag(String pName) {
            return TagKey.create(Registries.ITEM,HBMxx.hbm(pName));
        }
        public static TagKey<Item> forgeTag(String pName) {
            return TagKey.create(Registries.ITEM, new ResourceLocation("forge",pName));
        }
    }
}
