package com.hbm.datagen.loot;

import net.minecraft.data.loot.packs.VanillaFishingLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.BiConsumer;

public class FishLootGen extends VanillaFishingLoot {
    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> builder) {
        // 这里super的不能取消，否则就会出错
        super.generate(builder);
    }
}
