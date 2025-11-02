package com.hbm.block;

import com.hbm.HBM;
import com.hbm.block.decoriate.BlockOre;
import com.hbm.block.env.BedRockOre;
import com.hbm.block.env.WasteEarth;
import com.hbm.block.env.WasteLeaves;
import com.hbm.datagen.LanguageProvider;
import com.hbm.datagen.loot.BlockLootGen;
import com.hbm.datagen.model.BlockStateGen;
import com.hbm.registries.ModBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;
import java.util.function.Supplier;

/**
 * 各种原材料
 * */
public class HBMBlockComponent {
    private static final List<RegistryObject<Block>> matherialList = new ArrayList<>();
    private static final Set<RegistryObject<Block>> modelSpecialSet = new HashSet<>();
    
    public static RegistryObject<Block> STEEL_BLOCK;
    public static RegistryObject<Block> PLASTIC_BARREL;
    public static RegistryObject<Block> SELLAFIELD_SLAKED;
    //自然物
    //ores
    public static RegistryObject<Block> WAST_LEAVES;
    public static RegistryObject<Block> WAST_EARTH;
    public static RegistryObject<Block> URANIUM_ORE;
    public static RegistryObject<Block> DEEPSLATE_URANIUM_ORE;
    public static RegistryObject<Block> SCORCHED_URANIUM_ORE;
    public static RegistryObject<Block> TITANIUM_ORE;
    public static RegistryObject<Block> THORIUM_ORE;
    public static RegistryObject<Block> NITER_ORE;
    public static RegistryObject<Block> TUNGSTEN_ORE;
    public static RegistryObject<Block> ALUMINIUM_ORE;
    public static RegistryObject<Block> FLUORITE_ORE;
    public static RegistryObject<Block> LEAD_ORE;
    public static RegistryObject<Block> BERYLLIUM_ORE;
    public static RegistryObject<Block> SA326_ORE;
    public static RegistryObject<Block> ASBESTOS_BLOCK;
    public static RegistryObject<Block> ASBESTOS_ORE;
    public static RegistryObject<Block> BASALT_ASBESTOS_ORE;
    //oil
    public static RegistryObject<Block> OIL_ORE;
    public static RegistryObject<Block> OIL_ORE_EMPTY;
    public static RegistryObject<Block> OIL_ORE_SAND;
    //rare ore
    public static RegistryObject<Block> RARE_EARTH_ORE;
    public static RegistryObject<Block> DEEPSLATE_RARE_EARTH_ORE;
    public static RegistryObject<Block> LITHIUM_ORE;
    public static RegistryObject<Block> COBALT_ORE;
    public static RegistryObject<Block> COLTAN_ORE;
    //geniss ore
    public static RegistryObject<Block> GENISS_GAS_ORE;
    //nether ore
    public static RegistryObject<Block> SMOLDER_ORE_NETHER;
    public static RegistryObject<Block> PLUTONIUM_ORE_NETHER;
    public static RegistryObject<Block> FIRE_ORE_NETHER;
    //end ore
    public static RegistryObject<Block> TIKITE_ORE_END;
    //bedrock ore
    public static RegistryObject<Block> BEDROCK_ORE;
    public static RegistryObject<Block> DEPTH_STONE;

    public static void register(DeferredRegister<Block> BLOCKS){
        STEEL_BLOCK = registerWithItem("block_steel", ()->new Block(BlockBehaviour.Properties.of()));
        SELLAFIELD_SLAKED = registerWithItem("sellafield_slaked", ()->new Block(BlockBehaviour.Properties.of().explosionResistance(5.0f)));

        //自然物
        //ores
        WAST_LEAVES = registerWithItem("wast_leaves",()->new WasteLeaves(BlockBehaviour.Properties.copy(Blocks.ACACIA_LEAVES).noLootTable()), true);
        WAST_EARTH = registerWithItem("wast_earth",()->new WasteEarth(BlockBehaviour.Properties.copy(Blocks.DIRT)), true);
        URANIUM_ORE = registerWithItem("ore_uranium",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)), true);
        DEEPSLATE_URANIUM_ORE = registerWithItem("ore_deepslate_uranium",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE)), true);
        SCORCHED_URANIUM_ORE = registerWithItem("ore_scorched_uranium",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)), true);
        TITANIUM_ORE = registerWithItem("ore_titanium",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)), true);
        THORIUM_ORE = registerWithItem("ore_thorium",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)), true);
        NITER_ORE = registerWithItem("ore_niter",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)), true);
        TUNGSTEN_ORE = registerWithItem("ore_tungsten",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)), true);
        ALUMINIUM_ORE = registerWithItem("ore_aluminium",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)), true);
        FLUORITE_ORE = registerWithItem("ore_fluorite",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)), true);
        LEAD_ORE = registerWithItem("ore_lead",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)), true);
        BERYLLIUM_ORE = registerWithItem("ore_beryllium",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.STONE)), true);
        SA326_ORE = registerWithItem("ore_sa326",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_DIAMOND_ORE), 0.1f), true);
        ASBESTOS_BLOCK = registerWithItem("asbestos_block",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL)), true);
        ASBESTOS_ORE = registerWithItem("ore_asbestos",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.STONE)), true);
        BASALT_ASBESTOS_ORE = registerWithItem("ore_basalt_asbestos",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.BASALT)), true);
        //oil
        OIL_ORE = registerWithItem("ore_oil",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.GOLD_ORE)), true);
        OIL_ORE_EMPTY = registerWithItem("oil_ore_empty",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.GOLD_ORE)), true);
        OIL_ORE_SAND = registerWithItem("oil_ore_sand",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.SAND)), true);
        //rare ore
        RARE_EARTH_ORE = registerWithItem("rare_earth",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.GOLD_ORE)), true);
        DEEPSLATE_RARE_EARTH_ORE = registerWithItem("ore_deepslate_rare",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_GOLD_ORE)), true);
        LITHIUM_ORE = registerWithItem("ore_lithium",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)), true);
        COBALT_ORE = registerWithItem("ore_cobalt",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)), true);
        COLTAN_ORE = registerWithItem("ore_coltan",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)), true);
        //geniss ore
        GENISS_GAS_ORE = registerWithItem("ore_geniss_gas",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)), true);
        //nether ore
        SMOLDER_ORE_NETHER = registerWithItem("smolder_ore_nether",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.NETHER_QUARTZ_ORE)), true);
        PLUTONIUM_ORE_NETHER = registerWithItem("plutonium_ore_nether",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.NETHER_QUARTZ_ORE)), true);
        FIRE_ORE_NETHER = registerWithItem("fire_ore_nether",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.NETHER_BRICK_WALL)), true);
        //end ore
        TIKITE_ORE_END = registerWithItem("tikite_ore_end",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.END_STONE)), true);
        //bedrock ore
        BEDROCK_ORE = registerWithItem("ore_bedrock",()->new BedRockOre(BlockBehaviour.Properties.copy(Blocks.BEDROCK)), true);
        DEPTH_STONE = registerWithItem("depth_stone",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.REINFORCED_DEEPSLATE)), true);
    }
    private static RegistryObject<Block> registerWithItem(final String name, final Supplier<? extends Block> blocksup){
        return registerWithItem(name, blocksup, false);
    }
    private static RegistryObject<Block> registerWithItem(final String name, final Supplier<? extends Block> blocksup, boolean isModelSpecial){
        RegistryObject<Block> blockRegistryObject = HBMMachine.registerBlockWithItem(ModBlocks.BLOCKS, name, blocksup);
        matherialList.add(blockRegistryObject);
        if (isModelSpecial) modelSpecialSet.add(blockRegistryObject);
        return blockRegistryObject;
    }

    public static void creativeTab(CreativeModeTab.Output pOutput){
        matherialList.forEach(blockRegistryObject -> pOutput.accept(blockRegistryObject.get()));
    }

    public static void genModel(BlockStateGen provider){
        // 默认直接用simpleblock，所以名称和位置一定要放对
        matherialList.forEach(blockRegistryObject -> {
            if (!modelSpecialSet.contains(blockRegistryObject))
                provider.simpleBlock(blockRegistryObject.get());
        });
        provider.simpleBlockWithItem(WAST_EARTH.get(),provider.models().cubeBottomTop(provider.path(WAST_EARTH.get()), HBM.rl("block/env/waste_earth_side"), HBM.rl("block/env/waste_earth_bottom"), HBM.rl("block/env/waste_earth_top")));
        provider.simpleBlockWithItem(WAST_LEAVES.get(),provider.models().leaves(provider.path(WAST_LEAVES.get()), HBM.rl("block/env/waste_leaves")));
        provider.simpleBlockWithItem(URANIUM_ORE.get(),provider.models().cubeAll(provider.path(URANIUM_ORE.get()), HBM.rl("block/env/ore_uranium")));
        provider.simpleBlockWithItem(DEEPSLATE_URANIUM_ORE.get(),provider.models().cubeAll(provider.path(DEEPSLATE_URANIUM_ORE.get()), HBM.rl("block/env/ore_uranium_deepslate")));
        provider.addEnumStateBlock(BEDROCK_ORE.get(), BedRockOre.TYPE, (value)->provider.enumModelFileFunction_BedRockOreType((BedRockOre.BedRockOreType) value));
        provider.simpleBlockItem(BEDROCK_ORE.get(), provider.models().cubeAll(provider.path(BEDROCK_ORE.get()), new ResourceLocation("block/bedrock")));
        provider.simpleBlockWithItem(RARE_EARTH_ORE.get(),provider.models().cubeAll(provider.path(RARE_EARTH_ORE.get()), HBM.rl("block/env/ore_rare")));
        provider.simpleBlockWithItem(DEEPSLATE_RARE_EARTH_ORE.get(),provider.models().cubeAll(provider.path(DEEPSLATE_RARE_EARTH_ORE.get()), HBM.rl("block/env/ore_rare_deepslate")));
        provider.simpleBlockWithItem(ASBESTOS_BLOCK.get(),provider.models().cubeAll(provider.path(ASBESTOS_BLOCK.get()), HBM.rl("block/env/block_asbestos")));
        provider.simpleBlockWithItem(ASBESTOS_ORE.get(),provider.models().cubeAll(provider.path(ASBESTOS_ORE.get()), HBM.rl("block/env/ore_asbestos")));
        provider.simpleBlockWithItem(BASALT_ASBESTOS_ORE.get(),provider.models().cubeTop(provider.path(BASALT_ASBESTOS_ORE.get()), HBM.rl("block/env/ore_asbestos_basalt"), HBM.rl("block/env/ore_asbestos_basalt_top")));
        provider.simpleBlockWithItem(SA326_ORE.get(),provider.models().cubeAll(provider.path(SA326_ORE.get()), HBM.rl("block/env/ore_schrabidium")));
        provider.simpleBlockWithItem(LITHIUM_ORE.get(),provider.models().cubeAll(provider.path(LITHIUM_ORE.get()), HBM.rl("block/env/ore_lithium")));
        provider.simpleBlockWithItem(DEPTH_STONE.get(),provider.models().cubeAll(provider.path(DEPTH_STONE.get()), HBM.rl("block/env/stone_depth")));
    }

    public static void languageSupport(LanguageProvider provider){
        matherialList.forEach(blockRegistryObject -> provider.add(blockRegistryObject.get(), Arrays.stream(blockRegistryObject.getId().getPath().split("_")).map(s -> s.substring(0,1).toUpperCase() + s.substring(1)).reduce("",(r, id) -> r + " " + id)));
    }
    public static void lootable(BlockLootGen provider){
        provider.dropSelf(STEEL_BLOCK.get());
        provider.dropSelf(SELLAFIELD_SLAKED.get());

        //矿石
        provider.dropOther(HBMBlockComponent.WAST_EARTH.get(), Blocks.DIRT);
        provider.dropSelf(HBMBlockComponent.URANIUM_ORE.get());
        provider.dropSelf(HBMBlockComponent.DEEPSLATE_URANIUM_ORE.get());
        provider.dropSelf(HBMBlockComponent.SCORCHED_URANIUM_ORE.get());
        provider.dropSelf(HBMBlockComponent.TITANIUM_ORE.get());
        provider.dropSelf(HBMBlockComponent.THORIUM_ORE.get());
        provider.dropSelf(HBMBlockComponent.NITER_ORE.get());
        provider.dropSelf(HBMBlockComponent.TUNGSTEN_ORE.get());
        provider.dropSelf(HBMBlockComponent.ALUMINIUM_ORE.get());
        provider.dropSelf(HBMBlockComponent.FLUORITE_ORE.get());
        provider.dropSelf(HBMBlockComponent.LEAD_ORE.get());
        provider.dropSelf(HBMBlockComponent.BERYLLIUM_ORE.get());
        provider.dropSelf(HBMBlockComponent.SA326_ORE.get());
        provider.dropSelf(HBMBlockComponent.ASBESTOS_BLOCK.get());
        provider.dropSelf(HBMBlockComponent.ASBESTOS_ORE.get());
        provider.dropSelf(HBMBlockComponent.BASALT_ASBESTOS_ORE.get());
        provider.dropSelf(HBMBlockComponent.OIL_ORE.get());
        provider.dropSelf(HBMBlockComponent.OIL_ORE_EMPTY.get());
        provider.dropSelf(HBMBlockComponent.OIL_ORE_SAND.get());
        provider.dropSelf(HBMBlockComponent.RARE_EARTH_ORE.get());
        provider.dropSelf(HBMBlockComponent.DEEPSLATE_RARE_EARTH_ORE.get());
        provider.dropSelf(HBMBlockComponent.LITHIUM_ORE.get());
        provider.dropSelf(HBMBlockComponent.COBALT_ORE.get());
        provider.dropSelf(HBMBlockComponent.COLTAN_ORE.get());
        provider.dropSelf(HBMBlockComponent.GENISS_GAS_ORE.get());
        provider.dropSelf(HBMBlockComponent.SMOLDER_ORE_NETHER.get());
        provider.dropSelf(HBMBlockComponent.PLUTONIUM_ORE_NETHER.get());
        provider.dropSelf(HBMBlockComponent.FIRE_ORE_NETHER.get());
        provider.dropSelf(HBMBlockComponent.TIKITE_ORE_END.get());
        provider.dropSelf(HBMBlockComponent.BEDROCK_ORE.get());
        provider.dropSelf(HBMBlockComponent.DEPTH_STONE.get());
    }
}
