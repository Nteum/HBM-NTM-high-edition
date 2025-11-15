package com.hbm.datagen.tag;

import com.hbm.api.resource.OreType;
import com.hbm.block.HBMBlockComponent;
import com.hbm.block.HBMMachine;
import com.hbm.registries.ModTags;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.OreDictManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
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
        OreDictManager.addBlockTags(this);
        //工具
        this.tag(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.Ores.ORE_URANIUM.get());

        //矿石
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.URANIUM)).add(ModBlocks.Ores.ORE_URANIUM.get(),HBMBlockComponent.DEEPSLATE_URANIUM_ORE.get(),HBMBlockComponent.SCORCHED_URANIUM_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.TITANIUM)).add(HBMBlockComponent.TITANIUM_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.THORIUM)).add(HBMBlockComponent.THORIUM_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.NITER)).add(HBMBlockComponent.NITER_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.TUNGSTNE)).add(HBMBlockComponent.TUNGSTEN_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.ALUMINIUM)).add(HBMBlockComponent.ALUMINIUM_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.FLUORITE)).add(HBMBlockComponent.FLUORITE_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.LEAD)).add(HBMBlockComponent.LEAD_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.BERYLLIUM)).add(HBMBlockComponent.BERYLLIUM_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.SA326)).add(HBMBlockComponent.SA326_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.ASBESTOS)).add(HBMBlockComponent.ASBESTOS_ORE.get(),HBMBlockComponent.BASALT_ASBESTOS_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.OIL)).add(HBMBlockComponent.OIL_ORE.get(),HBMBlockComponent.OIL_ORE_EMPTY.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.RARE_EARTH)).add(HBMBlockComponent.RARE_EARTH_ORE.get(),HBMBlockComponent.DEEPSLATE_RARE_EARTH_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.LITHIUM)).add(HBMBlockComponent.LITHIUM_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.COBALT)).add(HBMBlockComponent.COBALT_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.COLTAN)).add(HBMBlockComponent.COLTAN_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.GAS)).add(HBMBlockComponent.GENISS_GAS_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.SMOLDER)).add(HBMBlockComponent.SMOLDER_ORE_NETHER.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.PLUTONIUM)).add(HBMBlockComponent.PLUTONIUM_ORE_NETHER.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.TIKITE)).add(HBMBlockComponent.TIKITE_ORE_END.get());
        this.tag(Tags.Blocks.ORES_COAL).add(HBMBlockComponent.FIRE_ORE_NETHER.get());
        ModTags.Blocks.MOD_ORES.values().forEach(oretag-> this.tag(Tags.Blocks.ORES).addTag(oretag));

        //机器
        this.tag(ModTags.Blocks.BATTERY).add(ModBlocks.machine_battery.get(),ModBlocks.machine_lithium_battery.get(),ModBlocks.machine_dineutronium_battery.get(),ModBlocks.machine_schrabidium_battery.get());
        this.tag(ModTags.Blocks.ANVIL).add(ModBlocks.Machine.ANVIL_IRON.get(),ModBlocks.Machine.ANVIL_DESH.get(),ModBlocks.Machine.ANVIL_BISMUTH.get());
        this.tag(ModTags.Blocks.MACHINE).add(ModBlocks.machine_assembler.get(),ModBlocks.RED_CABLE.get(),HBMMachine.CHEMPLANT.get());
//                .addTag(ModTags.Blocks.BATTERY);

        /** 原版tag */
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.DUMMIBLE.get()).addTag(ModTags.Blocks.MACHINE).addTag(Tags.Blocks.ORES);
    }

    @Override
    public IntrinsicTagAppender<Block> tag(TagKey<Block> pTag) {
        return super.tag(pTag);
    }
}
