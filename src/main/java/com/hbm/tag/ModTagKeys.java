package com.hbm.tag;

import com.hbm.DalisFunMod;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
//模组自定义的key列表
public class ModTagKeys {
    //电池
    public static final TagKey<Item> batteryItem = TagKey.of(RegistryKeys.ITEM,new Identifier(DalisFunMod.MOD_ID,"battery_item"));
    public static void registerTag(){
        DalisFunMod.LOGGER.info("Tag load");
    }
}
