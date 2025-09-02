package com.hbm.datagen.tag;

import com.hbm.api.resource.OreType;
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
        this.tag(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.URANIUM_ORE.get());

        //矿石
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.URANIUM)).add(ModBlocks.URANIUM_ORE.get(),ModBlocks.DEEPSLATE_URANIUM_ORE.get(),ModBlocks.SCORCHED_URANIUM_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.TITANIUM)).add(ModBlocks.TITANIUM_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.THORIUM)).add(ModBlocks.THORIUM_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.NITER)).add(ModBlocks.NITER_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.TUNGSTNE)).add(ModBlocks.TUNGSTEN_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.ALUMINIUM)).add(ModBlocks.ALUMINIUM_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.FLUORITE)).add(ModBlocks.FLUORITE_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.LEAD)).add(ModBlocks.LEAD_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.BERYLLIUM)).add(ModBlocks.BERYLLIUM_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.SA326)).add(ModBlocks.SA326_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.ASBESTOS)).add(ModBlocks.ASBESTOS_ORE.get(),ModBlocks.BASALT_ASBESTOS_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.OIL)).add(ModBlocks.OIL_ORE.get(),ModBlocks.OIL_ORE_EMPTY.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.RARE_EARTH)).add(ModBlocks.RARE_EARTH_ORE.get(),ModBlocks.DEEPSLATE_RARE_EARTH_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.LITHIUM)).add(ModBlocks.LITHIUM_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.COBALT)).add(ModBlocks.COBALT_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.COLTAN)).add(ModBlocks.COLTAN_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.GAS)).add(ModBlocks.GENISS_GAS_ORE.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.SMOLDER)).add(ModBlocks.SMOLDER_ORE_NETHER.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.PLUTONIUM)).add(ModBlocks.PLUTONIUM_ORE_NETHER.get());
        this.tag(ModTags.Blocks.MOD_ORES.get(OreType.TIKITE)).add(ModBlocks.TIKITE_ORE_END.get());
        this.tag(Tags.Blocks.ORES_COAL).add(ModBlocks.FIRE_ORE_NETHER.get());
        ModTags.Blocks.MOD_ORES.values().forEach(oretag-> this.tag(Tags.Blocks.ORES).addTag(oretag));

        //机器
        this.tag(ModTags.Blocks.BATTERY).add(ModBlocks.machine_battery.get(),ModBlocks.machine_lithium_battery.get(),ModBlocks.machine_dineutronium_battery.get(),ModBlocks.machine_schrabidium_battery.get());
        this.tag(ModTags.Blocks.ANVIL).add(ModBlocks.anvil_iron.get(),ModBlocks.anvil_desh.get(),ModBlocks.anvil_bismuth.get());
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
