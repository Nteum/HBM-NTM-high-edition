package com.hbm.datagen.tag;

import com.hbm.modsetting.resource.ElementUtils;
import com.hbm.modsetting.resource.OreType;
import com.hbm.registries.ModTags;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/** 方块tag生成器，生成方法请参考 ForgeBlockTagsProvider */
public class BlockTagsGen extends BlockTagsProvider {
    public BlockTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }
    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        //工具
        this.tag(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.URANIUM_ORE.get());

        //矿石
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.URANIUM)).add(ModBlocks.URANIUM_ORE.get(),ModBlocks.DEEPSLATE_URANIUM_ORE.get(),ModBlocks.SCORCHED_URANIUM_ORE.get());

        //机器
        this.tag(ModTags.Blocks.BATTERY).add(ModBlocks.machine_battery.get(),ModBlocks.machine_lithium_battery.get(),ModBlocks.machine_dineutronium_battery.get(),ModBlocks.machine_schrabidium_battery.get());
    }
}
