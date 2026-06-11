package com.hbm.debug;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;
import java.util.stream.Collectors;

public class TagDebugUtils {

    public static void checkItemTagLoading(Level level, ResourceLocation myTagLocation) {
        if (level.isClientSide) return; // 建议在服务端检查，因为服务器是 Data 的权威实体

        // 1. 获取 1.20.1 完备的内置物品 Registry
        var itemRegistry = level.registryAccess().registryOrThrow(Registries.ITEM);

        // 2. 检查你的 Tag 是否存在于 Registry 的 Tag 集合中
        boolean tagExists = itemRegistry.getTags().anyMatch(tagKey -> tagKey.getFirst().location().equals(myTagLocation));

        System.out.println("================ TAG DEBUG ================");
        System.out.println("目标检查的 Tag: " + myTagLocation);
        System.out.println("该 Tag 是否成功被注册/加载: " + (tagExists ? "【已加载】" : "【未加载！游戏压根没认它】"));

        if (tagExists) {
            // 如果加载了，顺便看看里面到底捆绑了哪些物品
            var tagKey = net.minecraft.tags.TagKey.create(Registries.ITEM, myTagLocation);
            Optional<HolderSet.Named<Item>> optionalNamedTag = itemRegistry.getTag(tagKey);

            optionalNamedTag.ifPresent(holders -> {
                System.out.println("该 Tag 内部包含的物品数量: " + holders.size());
                holders.forEach(holder -> {
                    System.out.println(" -> 包含物品: " + holder.value());
                });
            });
        }
        System.out.println("===========================================");
    }
}