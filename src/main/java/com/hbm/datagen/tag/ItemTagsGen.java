package com.hbm.datagen.tag;

import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import com.hbm.registries.ModTags;
import com.hbm.registries.OreDictManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

;

public class ItemTagsGen extends ItemTagsProvider {

    public ItemTagsGen(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagLookup<Block>> pBlockTags, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, pBlockTags, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        //电池
        this.tag(ModTags.Items.BATTERY).add(ModItems.BATTERY_CREATIVE.get(),ModItems.BATTERY_GENERIC.get(),ModItems.BATTERY_ADVANCED.get(),ModItems.BATTERY_LITHIUM.get(),
                ModBlocks.BATTERY.get().asItem(),ModBlocks.BATTERY_LITHIUM.get().asItem(),
                ModBlocks.BATTERY_SCHRABIDIUM.get().asItem(),ModBlocks.BATTERY_DINEUTRONIUM.get().asItem());
        //可以被充电的
        this.tag(ModTags.Items.CHARGEABLE).addTag(ModTags.Items.BATTERY);
        this.tag(ModTags.Items.UPGRADE).add(ModItems.UPGRADE_BASE.get());
        this.tag(ModTags.Items.MISSILE).add(ModItems.MISSILE_GENERIC.get());
        this.tag(ModTags.Items.SHREDDER_BLADES).add(
                ModItems.BLADE_METEORITE.get(),
                ModItems.BLADE_TITANIUM.get(),
                ModItems.BLADE_TUNGSTEN.get(),
                ModItems.SAWBLADE.get()
        );
        ModItems.tagSupport(this);

        for (ModTags.TagGenEntry<Item> tagGenEntry : ModTags.Items.LIST_TAG_GEN_REQ) {
            TagKey<Item> key = tagGenEntry.key;
            if (tagGenEntry.keyOut != null)
                for (TagKey<Item> keyToJoin : tagGenEntry.keyOut) {
                    this.tag(keyToJoin).addTag(key);
                }
            if (tagGenEntry.keyIn != null)
                for (TagKey<Item> keyToContain : tagGenEntry.keyIn) {
                    this.tag(key).addTag(keyToContain);
                }
            if (tagGenEntry.keyAutoGen != null)
                for (TagKey<Item> keyToJoin : tagGenEntry.keyAutoGen) {
                    this.tag(keyToJoin).addTag(TagKey.create(Registries.ITEM, keyToJoin.location().withSuffix("/" + key.location().getPath())));
                }
        }
        ModTags.Items.LIST_TAG_GEN_REQ.clear();

        for (Map.Entry<TagKey<Block>, TagKey<Item>> entry : ModTags.Items.BLOCK_ITEM_TRANS.entrySet()) {
            this.copy(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public IntrinsicTagAppender<Item> tag(TagKey<Item> pTag) {
        return super.tag(pTag);
    }
}
