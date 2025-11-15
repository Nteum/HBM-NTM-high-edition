package com.hbm.datagen.tag;

import com.hbm.item.HBMComponent;
import com.hbm.item.HBMtools;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import com.hbm.registries.ModTags;
import com.hbm.registries.OreDictManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ItemTagsGen extends ItemTagsProvider {

    public ItemTagsGen(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagLookup<Block>> pBlockTags, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, pBlockTags, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        OreDictManager.addItemTags(this);
        //电池
        this.tag(ModTags.Items.BATTERY).add(ModItems.BATTERY_CREATIVE.get(),ModItems.BATTERY_GENERIC.get(),ModItems.BATTERY_ADVANCED.get(),ModItems.BATTERY_LITHIUM.get(),
                ModBlocks.machine_battery.get().asItem(),ModBlocks.machine_lithium_battery.get().asItem(),
                ModBlocks.machine_schrabidium_battery.get().asItem(),ModBlocks.machine_dineutronium_battery.get().asItem());
        //可以被充电的
        this.tag(ModTags.Items.CHARGEABLE).addTag(ModTags.Items.BATTERY);
        this.tag(ModTags.Items.UPGRADE).add(HBMtools.UPGRADE_BASE.get());
        this.tag(ModTags.Items.MISSILE).add(ModItems.MISSILE_GENERIC.get());
    }

    @Override
    public IntrinsicTagAppender<Item> tag(TagKey<Item> pTag) {
        return super.tag(pTag);
    }
}
