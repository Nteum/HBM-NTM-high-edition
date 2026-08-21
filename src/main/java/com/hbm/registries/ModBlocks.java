package com.hbm.registries;

import com.hbm.HBM;
import com.hbm.HBMKey;

import com.hbm.block.BlockEnums;
import com.hbm.block.HBMBlockProperties;
import com.hbm.block.base.BlockBase;
import com.hbm.block.bomb.NukeGadget;
import com.hbm.block.decoriate.BlockMolten;
import com.hbm.block.decoriate.BlockOre;
import com.hbm.block.decoriate.BlockTest12;
import com.hbm.block.enums.EnumCapBlock;
import com.hbm.block.env.*;
import com.hbm.block.generic.BlockSpeedy;
import com.hbm.block.logistic.*;
import com.hbm.block.machine.*;
import com.hbm.block.machine.icf.BlockICFController;
import com.hbm.block.machine.icf.BlockICFPress;
import com.hbm.block.machine.icf.BlockICFReactor;
import com.hbm.block.machine.pile.*;
import com.hbm.block.machine.research.BlockBreederReactor;
import com.hbm.block.machine.tokamak.*;
import com.hbm.block.machine.rbmk.BlockRBMKBase;
import com.hbm.block.machine.rbmk.BlockRBMKColumn;
import com.hbm.block.machine.rbmk.BlockRBMKFuelChannel;
import com.hbm.block.machine.rbmk.BlockRBMKHeater;
import com.hbm.block.machine.rbmk.BlockRBMKControlRod;
import com.hbm.block.machine.rbmk.BlockRBMKDisplay;
import com.hbm.block.machine.rbmk.BlockRBMKGauge;
import com.hbm.block.machine.rbmk.BlockRBMKGraph;
import com.hbm.block.machine.rbmk.BlockRBMKKeypad;
import com.hbm.block.machine.rbmk.BlockRBMKNumitron;
import com.hbm.block.machine.rbmk.BlockRBMKPeripheral;
import com.hbm.block.machine.rbmk.BlockRBMKPeripheralLarge;
import com.hbm.block.machine.rbmk.BlockRBMKRadioController;
import com.hbm.block.machine.rbmk.BlockRBMKSteamPort;
import com.hbm.block.tools.GeigerCounter;
import com.hbm.block.weapon.LaunchPad;
import com.hbm.blockentity.bomb.NukeBombBoyEntityBE;
import com.hbm.blockentity.bomb.NukeBombCustomEntityBE;
import com.hbm.blockentity.bomb.NukeBombFatEntityBE;
import com.hbm.blockentity.bomb.TileNukeGadget;
import com.hbm.blockentity.logistic.PipeEntityBEPipeBase;
import com.hbm.blockentity.machine.*;
import com.hbm.blockentity.machine.rbmk.*;
import com.hbm.blockentity.machine.rbmk.RBMKOutgasserEntityBE;
import com.hbm.block.machine.research.BlockResearchReactor;
import com.hbm.block.machine.generator.BlockPWR;
import com.hbm.block.machine.generator.BlockPWRController;
import com.hbm.block.machine.generator.BlockPWRPillar;
import com.hbm.block.machine.generator.BlockGenericPWR;
import com.hbm.block.space.BlockSpaceStation;
import com.hbm.block.tools.FoundryMold;
import com.hbm.block.bomb.NukeBoy;
import com.hbm.block.bomb.NukeCustom;
import com.hbm.block.bomb.NukeFat;
import com.hbm.blockentity.machine.rbmk.RBMKStorageEntityBE;
import com.hbm.blockentity.tools.TileEntityGeigerBE;
import com.hbm.config.ConfigBomb;
import com.hbm.core.client.render.RendererBlockNaked;
import com.hbm.core.contents.fluid.HBMFluids;
import com.hbm.datagen.json.HBMJsonProvider;
import com.hbm.datagen.LanguageProvider;
import com.hbm.datagen.loot.BlockLootGen;
import com.hbm.datagen.model.BlockStateGen;
import com.hbm.datagen.tag.BlockTagsGen;
import com.hbm.gui.menu.*;
import com.hbm.gui.screen.*;
import com.hbm.core.item.BlockItemDummyable;
import com.hbm.item.blockitem.IronCrateItem;
import com.hbm.item.blockitem.ItemPosModify;
import com.hbm.item.blockitem.SteelCrateItem;
import com.hbm.core.item.BlockItemBattery;
import com.hbm.reactor.rbmk.RBMKPeripheralType;
import com.hbm.registries.WrappedRegistryBuilder.WrappedBlockRegistryBuilder;
import com.hbm.debug.BlockDebug;
import com.hbm.render.blockentity.*;
import com.hbm.world.feature.BedrockOreDefinition;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import static com.hbm.HBM.MODID;

public class ModBlocks {
    //方块注册表
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final List<WrappedBlockRegistryBuilder> blockList = new ArrayList<>();
    private static final List<RegistryObject<Block>> legacyMachineTagBlocks = new ArrayList<>();

    static {
        HBMFluids.registerBlock(BLOCKS);
    }

    private static final TagKey<Block>[] TAG_MACHINE = new TagKey[]{BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL};

    public static RegistryObject<Block> TEST_RENDER;
    public static RegistryObject<Block> TEST_BOMB;
    public static RegistryObject<Block> TEST_BOMB_ADVANCED;
    public static RegistryObject<Block> TEST_NUKE;
    public static RegistryObject<Block> EVENT_TESTER;
    public static RegistryObject<Block> OBJ_TESTER;
    public static RegistryObject<Block> TEST_CORE;
    public static RegistryObject<Block> TEST_CHARGE;
    public static RegistryObject<Block> TEST_PIPE;
    public static RegistryObject<Block> TEST_CT;
    public static RegistryObject<Block> TEST_RAIL;
    public static RegistryObject<Block> STRUCTURE_ANCHOR;
    public static RegistryObject<Block> WAND_AIR = block("wand_air", ()->new Block(Properties.of().noCollission().noOcclusion()));
    public static RegistryObject<Block> WAND_LOOT = block("wand_loot", ()->new Block(Properties.of().noCollission().noOcclusion()));
    public static RegistryObject<Block> WAND_JIGSAW = block("wand_jigsaw", ()->new Block(Properties.of().noCollission().noOcclusion()));
    public static RegistryObject<Block> WAND_LOGIC = block("wand_logic", ()->new Block(Properties.of().noCollission().noOcclusion()));
    public static RegistryObject<Block> WAND_TANDEM = block("wand_tandem", ()->new Block(Properties.of().noCollission().noOcclusion()));

    // Vanilla ores for other planets
    public static RegistryObject<Block> ORE_IRON;
    public static RegistryObject<Block> ORE_GOLD;
    public static RegistryObject<Block> ORE_REDSTONE;
    public static RegistryObject<Block> ORE_LAPIS;
    public static RegistryObject<Block> ORE_EMERALD;
    public static RegistryObject<Block> ORE_QUARTZ;
    public static RegistryObject<Block> ORE_DIAMOND;

    public static RegistryObject<Block> ORE_URANIUM;
    public static RegistryObject<Block> ORE_URANIUM_SCORCHED;
    public static RegistryObject<Block> ORE_TITANIUM;
    public static RegistryObject<Block> ORE_SULFUR;
    public static RegistryObject<Block> ORE_THORIUM;
    public static RegistryObject<Block> ORE_MORKITE;
    public static RegistryObject<Block> ORE_NITER;
    public static RegistryObject<Block> ORE_COPPER;
    public static RegistryObject<Block> ORE_NICKEL;
    public static RegistryObject<Block> ORE_TUNGSTEN;
    public static RegistryObject<Block> ORE_ALUMINIUM;
    public static RegistryObject<Block> ORE_FLUORITE;
    public static RegistryObject<Block> ORE_LEAD;
    public static RegistryObject<Block> ORE_SCHRABIDIUM;
    public static RegistryObject<Block> ORE_BERYLLIUM;
    public static RegistryObject<Block> ORE_AUSTRALIUM;
    public static RegistryObject<Block> ORE_WEIDANIUM;
    public static RegistryObject<Block> ORE_REIIUM;
    public static RegistryObject<Block> ORE_UNOBTAINIUM;
    public static RegistryObject<Block> ORE_DAFFERGON;
    public static RegistryObject<Block> ORE_VERTICIUM;
    public static RegistryObject<Block> ORE_RARE;
    public static RegistryObject<Block> ORE_COBALT;
    public static RegistryObject<Block> ORE_CINNEBAR;
    public static RegistryObject<Block> ORE_COLTAN;
    public static RegistryObject<Block> ORE_ALEXANDRITE;
    public static RegistryObject<Block> ORE_MINERAL;
    public static RegistryObject<Block> ORE_ZINC;
    public static final RegistryObject<Block> BLOCK_OSMIRIDIUM = block("block_osmiridium",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 10.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.OSMIRIDIUM.storage_block());


    public static RegistryObject<Block> ORE_RANDOM;
    public static RegistryObject<Block> ORE_BEDROCK;
    public static RegistryObject<Block> ORE_VOLCANO;

    public static RegistryObject<Block> ORE_NETHER_COAL;
    public static RegistryObject<Block> ORE_NETHER_SMOLDERING;
    public static RegistryObject<Block> ORE_NETHER_URANIUM;
    public static RegistryObject<Block> ORE_NETHER_URANIUM_SCORCHED;
    public static RegistryObject<Block> ORE_NETHER_PLUTONIUM;
    public static RegistryObject<Block> ORE_NETHER_TUNGSTEN;
    public static RegistryObject<Block> ORE_NETHER_SULFUR;
    public static RegistryObject<Block> ORE_NETHER_FIRE;
    public static RegistryObject<Block> ORE_NETHER_COBALT;
    public static RegistryObject<Block> ORE_NETHER_SCHRABIDIUM;

    public static RegistryObject<Block> ORE_METEOR_URANIUM;
    public static RegistryObject<Block> ORE_METEOR_THORIUM;
    public static RegistryObject<Block> ORE_METEOR_TITANIUM;
    public static RegistryObject<Block> ORE_METEOR_SULFUR;
    public static final RegistryObject<Block> ORE_METEOR_COPPER = add("ore_meteor_copper", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF);
    public static RegistryObject<Block> ORE_METEOR_TUNGSTEN;
    public static final RegistryObject<Block> ORE_METEOR_ALUMINIUM = add("ore_meteor_aluminium", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF);
    public static RegistryObject<Block> ORE_METEOR_LEAD;
    public static RegistryObject<Block> ORE_METEOR_LITHIUM;
    public static RegistryObject<Block> ORE_METEOR_STARMETAL;

    public static RegistryObject<Block> STONE_GNEISS;
    public static RegistryObject<Block> ORE_GNEISS_IRON;
    public static final RegistryObject<Block> ORE_METEOR_IRON = add("ore_meteor_iron", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF);
    public static RegistryObject<Block> ORE_GNEISS_GOLD;
    public static RegistryObject<Block> ORE_GNEISS_URANIUM;
    public static RegistryObject<Block> ORE_GNEISS_URANIUM_SCORCHED;
    public static RegistryObject<Block> ORE_GNEISS_COPPER;
    public static RegistryObject<Block> ORE_GNEISS_ASBESTOS;
    public static RegistryObject<Block> ORE_GNEISS_LITHIUM;
    public static RegistryObject<Block> ORE_GNEISS_SCHRABIDIUM;
    public static RegistryObject<Block> ORE_GNEISS_RARE;
    public static RegistryObject<Block> ORE_GNEISS_GAS;

    public static RegistryObject<Block> GNEISS_BRICK;
    public static RegistryObject<Block> GNEISS_TILE;
    public static RegistryObject<Block> GNEISS_CHISELED;

    public static RegistryObject<Block> STONE_DEPTH;
    public static RegistryObject<Block> ORE_DEPTH_CINNEBAR;
    public static RegistryObject<Block> ORE_DEPTH_ZIRCONIUM;
    public static RegistryObject<Block> ORE_DEPTH_BORAX;
    public static RegistryObject<Block> CLUSTER_DEPTH_IRON;
    public static RegistryObject<Block> CLUSTER_DEPTH_TITANIUM;
    public static RegistryObject<Block> CLUSTER_DEPTH_TUNGSTEN;

    public static RegistryObject<Block> STONE_KEYHOLE;

    public static RegistryObject<Block> STONE_DEPTH_NETHER;
    public static RegistryObject<Block> ORE_DEPTH_NETHER_NEODYMIUM;

    // 基岩矿
    public static final RegistryObject<Block> STONE_POROUS = new WrappedBlockRegistryBuilder("stone_porous",()->new Block(BlockBehaviour.Properties.copy(Blocks.STONE))).model(HBMKey.MODEL_STANDALONE).loc(HBMKey.REVERSE_GEN).loot(HBMKey.DROP_STANDALONE).tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, BlockTags.STONE_ORE_REPLACEABLES).build();
    public static final WrappedRegistryBuilder.RegisterObjectCollection<Block, BlockEnums.EnumStoneType> STONE_RESOURCE = new WrappedRegistryBuilder.RegisterObjectCollection<>(BlockEnums.EnumStoneType.class,
            type -> add("stone_resource." + type.name().toLowerCase(), ()->new Block(Properties.of().strength(5, 10)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, type == BlockEnums.EnumStoneType.MALACHITE ? HBMKey.DROP_STANDALONE : HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL));
    public static final WrappedRegistryBuilder.RegisterObjectCollection<Block, BlockEnums.EnumStalagmiteType> STALAGMITE = new WrappedRegistryBuilder.RegisterObjectCollection<>(BlockEnums.EnumStalagmiteType.class,
            type -> new WrappedBlockRegistryBuilder("stalagmite." + type.name().toLowerCase(), ()->new BlockStalagmite(Properties.of().strength(0.5f, 2))).model(HBMKey.MODEL_STANDALONE).loot(HBMKey.DROP_STANDALONE).loc(HBMKey.REVERSE_GEN).build());
    public static final WrappedRegistryBuilder.RegisterObjectCollection<Block, BlockEnums.EnumStalagmiteType> STALACTITE = new WrappedRegistryBuilder.RegisterObjectCollection<>(BlockEnums.EnumStalagmiteType.class,
            type -> new WrappedBlockRegistryBuilder("stalactite." + type.name().toLowerCase(), ()->new BlockStalagmite(Properties.of().strength(0.5f, 2))).model(HBMKey.MODEL_STANDALONE).loot(HBMKey.DROP_STANDALONE).loc(HBMKey.REVERSE_GEN).build());
    public static RegistryObject<Block> STONE_BIOME;

    public static final RegistryObject<Block> DEPTH_BRICK = new WrappedBlockRegistryBuilder("depth_brick",()->new BlockOre(BlockBehaviour.Properties.of().destroyTime(-1.0F).explosionResistance(10))).tab(ModTabs.BLOCKS.getKey()).loc("Depth Bricks").build();
    public static final RegistryObject<Block> DEPTH_TILES = new WrappedBlockRegistryBuilder("depth_tiles",()->new BlockOre(BlockBehaviour.Properties.of().destroyTime(-1.0F).explosionResistance(10))).tab(ModTabs.BLOCKS.getKey()).loc("Depth Tiles").build();
    public static final RegistryObject<Block> DEPTH_NETHER_BRICK = new WrappedBlockRegistryBuilder("depth_nether_brick",()->new BlockOre(BlockBehaviour.Properties.of().destroyTime(-1.0F).explosionResistance(10))).tab(ModTabs.BLOCKS.getKey()).loc("Nether Depth Bricks").build();
    public static final RegistryObject<Block> DEPTH_NETHER_TILES = new WrappedBlockRegistryBuilder("depth_nether_tiles",()->new BlockOre(BlockBehaviour.Properties.of().destroyTime(-1.0F).explosionResistance(10))).tab(ModTabs.BLOCKS.getKey()).loc("Nether Depth Tiles").build();
    public static final RegistryObject<Block> DEPTH_DNT = new WrappedBlockRegistryBuilder("depth_dnt",()->new BlockOre(BlockBehaviour.Properties.of().destroyTime(-1.0F).explosionResistance(60000))).tab(ModTabs.BLOCKS.getKey()).loc("DNT-Reinforced Depth Bricks").tags(HBMMatters.DNT.storage_block()).build();

    public static RegistryObject<Block> BASALT;
    public static RegistryObject<Block> ORE_BASALT;
    @Deprecated public static RegistryObject<Block> BASALT_SULFUR;
    @Deprecated public static RegistryObject<Block> BASALT_FLUORITE;
    @Deprecated public static RegistryObject<Block> BASALT_ASBESTOS;
    @Deprecated public static RegistryObject<Block> BASALT_GEM;
    public static RegistryObject<Block> BASALT_SMOOTH;
    public static RegistryObject<Block> BASALT_BRICK;
    public static RegistryObject<Block> BASALT_POLISHED;
    public static RegistryObject<Block> BASALT_TILES;

    public static RegistryObject<Block> CLUSTER_IRON;
    public static RegistryObject<Block> CLUSTER_TITANIUM;
    public static RegistryObject<Block> CLUSTER_ALUMINIUM;
    public static RegistryObject<Block> CLUSTER_COPPER;

    public static RegistryObject<Block> ORE_OIL;
    public static RegistryObject<Block> ORE_OIL_EMPTY;
    public static RegistryObject<Block> ORE_OIL_SAND;
    public static RegistryObject<Block> ORE_BEDROCK_OIL;
    public static RegistryObject<Block> ORE_LIGNITE;
    public static RegistryObject<Block> ORE_ASBESTOS;
    public static RegistryObject<Block> ORE_COAL_OIL = block("ore_coal_oil", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 120)));
    public static RegistryObject<Block> ORE_COAL_OIL_BURNING;

    public static RegistryObject<Block> ORE_TIKITE;

    public static RegistryObject<Block> CRYSTAL_POWER;
    public static RegistryObject<Block> CRYSTAL_ENERGY;
    public static RegistryObject<Block> CRYSTAL_ROBUST;
    public static RegistryObject<Block> CRYSTAL_TRIXITE;

    // 资源方块
    // ==========================================
    // 铀、钍、镎、钋 系列核材料方块 (BlockHazard & BlockHotHazard)
    // ==========================================
    public static final RegistryObject<Block> BLOCK_THORIUM = block("block_thorium",
            () -> new BlockHazard(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 50.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.THORIUM.storage_block());

    public static final RegistryObject<Block> BLOCK_THORIUM_FUEL = block("block_thorium_fuel",
            () -> new BlockHazard(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 50.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, Tags.Blocks.STORAGE_BLOCKS);
    public static final RegistryObject<Block> BLOCK_URANIUM = block("block_uranium", () -> new BlockHazard(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5, 50)), BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.URANIUM.storage_block());
    public static final RegistryObject<Block> BLOCK_U233 = block("block_u233", () -> new BlockHazard(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5, 50)), BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.U233.storage_block());
    public static final RegistryObject<Block> BLOCK_U235 = block("block_u235",
            () -> new BlockHazard(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 50.0F).sound(SoundType.METAL)).setDisplayEffect(BlockHazard.ExtDisplayEffect.RADFOG),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.U235.storage_block());

    public static final RegistryObject<Block> BLOCK_U238 = block("block_u238",
            () -> new BlockHazard(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 50.0F).sound(SoundType.METAL)).setDisplayEffect(BlockHazard.ExtDisplayEffect.RADFOG),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.U238.storage_block());

    public static final RegistryObject<Block> BLOCK_URANIUM_FUEL = block("block_uranium_fuel",
            () -> new BlockHazard(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 50.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, Tags.Blocks.STORAGE_BLOCKS);

    public static final RegistryObject<Block> BLOCK_NEPTUNIUM = block("block_neptunium",
            () -> new BlockHazard(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 60.0F).sound(SoundType.METAL)).setDisplayEffect(BlockHazard.ExtDisplayEffect.RADFOG),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.NEPTUNIUM.storage_block());

    public static final RegistryObject<Block> BLOCK_POLONIUM = block("block_polonium",
            () -> new BlockHazardHot(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 50.0F).sound(SoundType.METAL)).setDisplayEffect(BlockHazard.ExtDisplayEffect.RADFOG),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.POLONIUM.storage_block());

    public static final RegistryObject<Block> BLOCK_MOX_FUEL = block("block_mox_fuel",
            () -> new BlockHazard(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 50.0F).sound(SoundType.METAL)).setDisplayEffect(BlockHazard.ExtDisplayEffect.RADFOG),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, Tags.Blocks.STORAGE_BLOCKS);

    public static final RegistryObject<Block> BLOCK_PLUTONIUM = block("block_plutonium",
            () -> new BlockHazard(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 50.0F).sound(SoundType.METAL)).setDisplayEffect(BlockHazard.ExtDisplayEffect.RADFOG),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.PLUTONIUM.storage_block());

    public static final RegistryObject<Block> BLOCK_PU238 = block("block_pu238",
            () -> new BlockHazardHot(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 50.0F).sound(SoundType.METAL).lightLevel(state -> 5)).setDisplayEffect(BlockHazard.ExtDisplayEffect.RADFOG),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.PU238.storage_block());

    public static final RegistryObject<Block> BLOCK_PU239 = block("block_pu239",
            () -> new BlockHazard(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 50.0F).sound(SoundType.METAL)).setDisplayEffect(BlockHazard.ExtDisplayEffect.RADFOG),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.PU239.storage_block());

    public static final RegistryObject<Block> BLOCK_PU240 = block("block_pu240",
            () -> new BlockHazard(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 50.0F).sound(SoundType.METAL)).setDisplayEffect(BlockHazard.ExtDisplayEffect.RADFOG),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.PU240.storage_block());

    public static final RegistryObject<Block> BLOCK_PU_MIX = block("block_pu_mix",
            () -> new BlockHazard(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 50.0F).sound(SoundType.METAL)).setDisplayEffect(BlockHazard.ExtDisplayEffect.RADFOG),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, Tags.Blocks.STORAGE_BLOCKS);

    public static final RegistryObject<Block> BLOCK_PLUTONIUM_FUEL = block("block_plutonium_fuel",
            () -> new BlockHazard(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 50.0F).sound(SoundType.METAL)).setDisplayEffect(BlockHazard.ExtDisplayEffect.RADFOG),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, Tags.Blocks.STORAGE_BLOCKS);

    // ==========================================
    // 基础工业金属与合金方块 (Block)
    // ==========================================
    public static final RegistryObject<Block> BLOCK_TITANIUM = block("block_titanium",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 50.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.TITANIUM.storage_block());

    public static final RegistryObject<Block> BLOCK_SULFUR = block("block_sulfur",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 10.0F)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.SULFUR.storage_block());

    public static final RegistryObject<Block> BLOCK_NITER = block("block_niter",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 10.0F)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, Tags.Blocks.STORAGE_BLOCKS);

    public static RegistryObject<Block> BLOCK_NITER_REINFORCED;
    public static RegistryObject<Block> BLOCK_COPPER;

    public static final RegistryObject<Block> BLOCK_NICKEL = block("block_nickel",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 10.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.NICKEL.storage_block());

    public static final RegistryObject<Block> BLOCK_RED_COPPER = block("block_red_copper",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 25.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, Tags.Blocks.STORAGE_BLOCKS);

    public static final RegistryObject<Block> BLOCK_TUNGSTEN = block("block_tungsten",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 20.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.TUNGSTEN.storage_block());

    public static final RegistryObject<Block> BLOCK_ALUMINIUM = block("block_aluminium",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 20.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.ALUMINIUM.storage_block());

    public static final RegistryObject<Block> BLOCK_FLUORITE = block("block_fluorite",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 10.0F)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.FLUORITE.storage_block());

    public static final RegistryObject<Block> BLOCK_STEEL = block("block_steel",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 50.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.STEEL.storage_block());

    public static final RegistryObject<Block> BLOCK_TCALLOY = block("block_tcalloy",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 70.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.TCALLOY.storage_block());

    public static final RegistryObject<Block> BLOCK_CDALLOY = block("block_cdalloy",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 70.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.CDALLOY.storage_block());

    public static final RegistryObject<Block> BLOCK_LEAD = block("block_lead",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 50.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.LEAD.storage_block());

    public static final RegistryObject<Block> BLOCK_BISMUTH = block("block_bismuth",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 90.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.BISMUTH.storage_block());

    public static final RegistryObject<Block> BLOCK_CADMIUM = block("block_cadmium",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 90.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.CADMIUM.storage_block());

    public static final RegistryObject<Block> BLOCK_COLTAN = block("block_coltan",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 50.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, Tags.Blocks.STORAGE_BLOCKS);

    public static final RegistryObject<Block> BLOCK_TANTALIUM = block("block_tantalium",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 50.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.TANTALIUM.storage_block());

    public static final RegistryObject<Block> BLOCK_NIOBIUM = block("block_niobium",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 50.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.NIOBIUM.storage_block());

    // ==========================================
    // 核废料与特殊产物系列 (BlockNuclearWaste, BlockOutgas)
    // ==========================================
    public static final RegistryObject<Block> BLOCK_TRINITITE = block("block_trinitite",
            () -> new BlockHazard(BlockBehaviour.Properties.copy(Blocks.STONE).strength(5.0F, 10.0F)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, Tags.Blocks.STORAGE_BLOCKS);

    public static final RegistryObject<Block> BLOCK_WASTE = block("block_waste",
            () -> new BlockHazardNuke(BlockBehaviour.Properties.copy(Blocks.STONE).strength(5.0F, 10.0F)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);

    public static final RegistryObject<Block> BLOCK_WASTE_PAINTED = block("block_waste_painted",
            () -> new BlockHazardNuke(BlockBehaviour.Properties.copy(Blocks.STONE).strength(5.0F, 10.0F)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);

    public static final RegistryObject<Block> BLOCK_WASTE_VITRIFIED = block("block_waste_vitrified",
            () -> new BlockHazardNuke(BlockBehaviour.Properties.copy(Blocks.STONE).strength(5.0F, 10.0F)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);

    public static final RegistryObject<Block> ANCIENT_SCRAP = block("ancient_scrap",
            () -> new BlockOutGas(true, 1, 1, BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(100.0F, 6000.0F)),
            BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL);

    public static final RegistryObject<Block> BLOCK_CORIUM = block("block_corium",
            () -> new BlockHazard(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(100.0F, 6000.0F)),
            BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL, Tags.Blocks.STORAGE_BLOCKS);

    public static final RegistryObject<Block> BLOCK_CORIUM_COBBLE = block("block_corium_cobble",
            () -> new BlockOutGas(true, 1, 1, BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(100.0F, 6000.0F)),
            BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL, Tags.Blocks.STORAGE_BLOCKS);

    // ==========================================
    // 重力受控下落方块 (BlockFalling & BlockHazardFalling)
    // ==========================================
    public static final RegistryObject<Block> BLOCK_SCRAP = block("block_scrap",
            () -> new FallingBlock(BlockBehaviour.Properties.copy(Blocks.GRAVEL).strength(2.5F, 5.0F).sound(SoundType.GRAVEL)),
            BlockTags.MINEABLE_WITH_SHOVEL, Tags.Blocks.STORAGE_BLOCKS);

    public static final RegistryObject<Block> BLOCK_ELECTRICAL_SCRAP = block("block_electrical_scrap",
            () -> new FallingBlock(BlockBehaviour.Properties.copy(Blocks.ANVIL).strength(2.5F, 5.0F).sound(SoundType.METAL)),
            BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, Tags.Blocks.STORAGE_BLOCKS);

    public static final RegistryObject<Block> BLOCK_YELLOWCAKE = block("block_yellowcake",
            () -> new BlockHazard(BlockBehaviour.Properties.copy(Blocks.SAND).strength(5.0F, 10.0F).sound(SoundType.SAND)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_SHOVEL, Tags.Blocks.STORAGE_BLOCKS);

    public static final RegistryObject<Block> BLOCK_RED_PHOSPHORUS = block("block_red_phosphorus",
            () -> new BlockHazard(BlockBehaviour.Properties.copy(Blocks.SAND).strength(5.0F, 10.0F).sound(SoundType.SAND)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_SHOVEL, HBMMatters.PHOSPHORUS.storage_block());

    public static final RegistryObject<Block> BLOCK_FALLOUT = block("block_fallout",
            () -> new BlockHazard(BlockBehaviour.Properties.copy(Blocks.SAND).strength(0.2F, 0.2F).sound(SoundType.GRAVEL)),
            BlockTags.MINEABLE_WITH_SHOVEL, Tags.Blocks.STORAGE_BLOCKS);

    // ==========================================
    // 超限/高级无序工业材料
    // ==========================================
    public static final RegistryObject<Block> BLOCK_BERYLLIUM = block("block_beryllium",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 20.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.BERYLLIUM.storage_block());

    public static final RegistryObject<Block> BLOCK_SCHRARANIUM = block("block_schraranium",
            () -> new BlockHazard(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 250.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL, HBMMatters.SCHRARANIUM.storage_block());

    public static final RegistryObject<Block> BLOCK_SCHRABIDIUM = block("block_schrabidium",
            () -> new BlockHazard(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 600.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL, HBMMatters.SCHRABIDIUM.storage_block());

    public static final RegistryObject<Block> BLOCK_SCHRABIDATE = block("block_schrabidate",
            () -> new BlockHazard(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 600.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL, HBMMatters.SCHRABIDATE.storage_block());

    public static final RegistryObject<Block> BLOCK_SOLINIUM = block("block_solinium",
            () -> new BlockHazard(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 600.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL, HBMMatters.SOLINIUM.storage_block());

    public static final RegistryObject<Block> BLOCK_SCHRABIDIUM_FUEL = block("block_schrabidium_fuel",
            () -> new BlockHazard(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 600.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL, Tags.Blocks.STORAGE_BLOCKS);

    public static final RegistryObject<Block> BLOCK_EUPHEMIUM = block("block_euphemium",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 60000.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL, Tags.Blocks.STORAGE_BLOCKS);

    public static final RegistryObject<Block> BLOCK_DINEUTRONIUM = block("block_dineutronium",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 60000.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL, Tags.Blocks.STORAGE_BLOCKS);

    public static RegistryObject<Block> BLOCK_ADVANCED_ALLOY;

    public static final RegistryObject<Block> BLOCK_MAGNETIZED_TUNGSTEN = block("block_magnetized_tungsten",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 75.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.MAGTUNG.storage_block());

    public static final RegistryObject<Block> BLOCK_COMBINE_STEEL = block("block_combine_steel",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 600.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL, HBMMatters.CMB.storage_block());

    public static final RegistryObject<Block> BLOCK_DESH = block("block_desh",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 300.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.DESH.storage_block());

    public static final RegistryObject<Block> BLOCK_DURA_STEEL = block("block_dura_steel",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 200.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.DURA.storage_block());

    public static final RegistryObject<Block> BLOCK_STARMETAL = block("block_starmetal",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 400.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL, HBMMatters.STAR.storage_block());

    public static final RegistryObject<Block> BLOCK_COBALT = block("block_cobalt",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 50.0F)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.COBALT.storage_block());

    public static final RegistryObject<Block> BLOCK_LITHIUM = block("block_lithium",
            () -> new BlockLithium(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 10.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.LITHIUM.storage_block());

    public static final RegistryObject<Block> BLOCK_ZIRCONIUM = block("block_zirconium",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 30.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.ZIRCONIUM.storage_block());

    public static final RegistryObject<Block> BLOCK_WHITE_PHOSPHORUS = block("block_white_phosphorus",
            () -> new BlockHazard(BlockBehaviour.Properties.copy(Blocks.STONE).strength(5.0F, 10.0F)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, Tags.Blocks.STORAGE_BLOCKS);

    // ==========================================
    // 聚合物、绝缘体与特殊石质/布质轴向方块 (BlockRotatablePillar / BlockPillar)
    // ==========================================
    public static final RegistryObject<Block> BLOCK_POLYMER = block("block_polymer",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE).strength(3.0F, 10.0F).sound(SoundType.NETHERITE_BLOCK)),
            BlockTags.MINEABLE_WITH_PICKAXE, Tags.Blocks.STORAGE_BLOCKS);

    public static final RegistryObject<Block> BLOCK_BAKELITE = block("block_bakelite",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE).strength(3.0F, 5.0F).sound(SoundType.NETHERITE_BLOCK)),
            BlockTags.MINEABLE_WITH_PICKAXE, HBMMatters.BAKELITE.storage_block());

    public static final RegistryObject<Block> BLOCK_RUBBER = block("block_rubber",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE).strength(3.0F, 15.0F).sound(SoundType.NETHERITE_BLOCK)),
            BlockTags.MINEABLE_WITH_PICKAXE, HBMMatters.RUBBER.storage_block());

    public static final RegistryObject<Block> BLOCK_INSULATOR = add("block_insulator",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).strength(5.0F, 10.0F).sound(SoundType.WOOL)),
            ModTabs.BLOCKS.getKey(), HBMKey.MODEL_PILLAR, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF,
            BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL, Tags.Blocks.STORAGE_BLOCKS);

    public static final RegistryObject<Block> BLOCK_FIBERGLASS = add("block_fiberglass",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).strength(5.0F, 15.0F).sound(SoundType.WOOL)),
            ModTabs.BLOCKS.getKey(), HBMKey.MODEL_PILLAR, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF,
            BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL);

    public static final RegistryObject<Block> BLOCK_ASBESTOS = block("block_asbestos",
            () -> new BlockOutGas(true, 5, 1, BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).strength(5.0F, 15.0F).sound(SoundType.WOOL)),
            BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL, HBMMatters.ASBESTOS.storage_block());

    public static final RegistryObject<Block> BLOCK_SCHRABIDIUM_CLUSTER = new WrappedBlockRegistryBuilder("block_schrabidium_cluster",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STONE).strength(5.0F, 60000.0F)))
            .tab(ModTabs.BLOCKS.getKey()).model(HBMKey.MODEL_PILLAR).loc(HBMKey.ORDERLY_GEN_EXCEPT_FIRST).loot(HBMKey.DROP_SELF).tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL, Tags.Blocks.STORAGE_BLOCKS)
            .item(block -> new BlockItem(block, new Item.Properties().rarity(Rarity.RARE))).build();

    public static final RegistryObject<Block> BLOCK_EUPHEMIUM_CLUSTER = add("block_euphemium_cluster",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STONE).strength(5.0F, 60000.0F)),
            ModTabs.BLOCKS.getKey(), HBMKey.MODEL_PILLAR, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF,
            BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL, Tags.Blocks.STORAGE_BLOCKS);

    // ==========================================
    // 反应堆石墨群与低硬度杂项方块
    // ==========================================
    public static final RegistryObject<Block> BLOCK_FOAM = block("block_foam",
            () -> new BlockBase(BlockBehaviour.Properties.copy(Blocks.SNOW_BLOCK).strength(0.5F, 0.0F).sound(SoundType.SNOW)),
            BlockTags.MINEABLE_WITH_SHOVEL);
    public static RegistryObject<Block> BLOCK_COKE;
    public static RegistryObject<Block> BLOCK_GRAPHITE;
    public static RegistryObject<Block> BLOCK_GRAPHITE_DRILLED;
    public static RegistryObject<Block> BLOCK_GRAPHITE_FUEL;
    public static RegistryObject<Block> BLOCK_GRAPHITE_PLUTONIUM;
    public static RegistryObject<Block> BLOCK_GRAPHITE_ROD;
    public static RegistryObject<Block> BLOCK_GRAPHITE_SOURCE;
    public static RegistryObject<Block> BLOCK_GRAPHITE_LITHIUM;
    public static RegistryObject<Block> BLOCK_GRAPHITE_TRITIUM;
    public static RegistryObject<Block> BLOCK_GRAPHITE_DETECTOR;

    public static final RegistryObject<Block> BLOCK_BORON = block("block_boron",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 10.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.BORON.storage_block());

    public static final RegistryObject<Block> BLOCK_LANTHANIUM = block("block_lanthanium",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 10.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.LANTHANIUM.storage_block());

    public static final RegistryObject<Block> BLOCK_RA226 = block("block_ra226",
            () -> new BlockHazard(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 10.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);

    public static final RegistryObject<Block> BLOCK_ACTINIUM = block("block_actinium",
            () -> new BlockHazard(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 10.0F).sound(SoundType.METAL)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);

    public static final RegistryObject<Block> BLOCK_TRITIUM = add("block_tritium",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.GLASS).strength(3.0F, 2.0F).sound(SoundType.GLASS)),
            ModTabs.BLOCKS.getKey(), HBMKey.MODEL_PILLAR, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF,
            BlockTags.MINEABLE_WITH_PICKAXE);

    public static final RegistryObject<Block> BLOCK_SEMTEX = add("block_semtex",
            () -> new BlockPlasticExplosive(BlockBehaviour.Properties.copy(Blocks.TNT).strength(2.0F, 2.0F).sound(SoundType.METAL)),
            ModTabs.BLOCKS.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF,
            BlockTags.MINEABLE_WITH_PICKAXE);

    public static final RegistryObject<Block> BLOCK_C4 = add("block_c4",
            () -> new BlockPlasticExplosive(BlockBehaviour.Properties.copy(Blocks.TNT).strength(2.0F, 2.0F).sound(SoundType.METAL)),
            ModTabs.BLOCKS.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF,
            BlockTags.MINEABLE_WITH_PICKAXE);

    public static final RegistryObject<Block> BLOCK_SLAG = add("block_slag",
            () -> new BlockSlag(BlockBehaviour.Properties.copy(Blocks.STONE).strength(2.0F, 2.0F).sound(SoundType.STONE)),
            ModTabs.BLOCKS.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, HBMMatters.SLAG.storage_block(),
            BlockTags.MINEABLE_WITH_PICKAXE);

    public static final RegistryObject<Block> BLOCK_SMORE = add("block_smore",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STONE).strength(15.0F, 600.0F)),
            ModTabs.BLOCKS.getKey(), HBMKey.MODEL_PILLAR, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF,
            BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, Tags.Blocks.STORAGE_BLOCKS);

    public static final RegistryObject<Block> BLOCK_AUSTRALIUM = block("block_australium",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(5.0F, 10.0F)),
            BlockTags.BEACON_BASE_BLOCKS, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.AUSTRALIUM.storage_block());
    public static RegistryObject<Block> BLOCK_WEIDANIUM;
    public static RegistryObject<Block> BLOCK_REIIUM;
    public static RegistryObject<Block> BLOCK_UNOBTAINIUM;
    public static RegistryObject<Block> BLOCK_DAFFERGON;
    public static RegistryObject<Block> BLOCK_VERTICIUM;

    public static RegistryObject<Block> BLOCK_CAP_NUKA;
    public static RegistryObject<Block> BLOCK_CAP_QUANTUM;
    public static RegistryObject<Block> BLOCK_CAP_RAD;
    public static RegistryObject<Block> BLOCK_CAP_SPARKLE;
    public static RegistryObject<Block> BLOCK_CAP_KORL;
    public static RegistryObject<Block> BLOCK_CAP_FRITZ;
    public static RegistryObject<Block> BLOCK_CAP_SUNSET;
    public static RegistryObject<Block> BLOCK_CAP_STAR;

    public static RegistryObject<Block> DECO_TITANIUM = block("deco_titanium", ()->new Block(Properties.copy(Blocks.STONE).strength(5, 30)));
    public static RegistryObject<Block> DECO_RED_COPPER = block("deco_red_copper", ()->new Block(Properties.copy(Blocks.STONE).strength(5, 30)));
    public static RegistryObject<Block> DECO_TUNGSTEN = block("deco_tungsten", ()->new Block(Properties.copy(Blocks.STONE).strength(5, 30)));
    public static RegistryObject<Block> DECO_ALUMINIUM = block("deco_aluminium", ()->new Block(Properties.copy(Blocks.STONE).strength(5, 30)));
    public static RegistryObject<Block> DECO_STEEL = block("deco_steel", ()->new Block(Properties.copy(Blocks.STONE).strength(5, 30)));
    public static RegistryObject<Block> DECO_LEAD = block("deco_lead", ()->new Block(Properties.copy(Blocks.STONE).strength(5, 30)));
    public static RegistryObject<Block> DECO_BERYLLIUM = block("deco_beryllium", ()->new Block(Properties.copy(Blocks.STONE).strength(5, 30)));
    public static RegistryObject<Block> DECO_ASBESTOS = block("deco_asbestos", ()->new Block(Properties.copy(Blocks.STONE).strength(5, 30)));
    public static RegistryObject<Block> DECO_RBMK = new WrappedBlockRegistryBuilder("deco_rbmk", ()->new Block(Properties.copy(Blocks.STONE).strength(5, 100))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "rbmk/rbmk_top").build();
    public static RegistryObject<Block> DECO_RBMK_SMOOTH = new WrappedBlockRegistryBuilder("deco_rbmk_smooth", ()->new Block(Properties.copy(Blocks.STONE).strength(5, 100))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "rbmk/rbmk_blank_top").build();

    public static RegistryObject<Block> DECO_EMITTER;
    public static RegistryObject<Block> PART_EMITTER;
    public static RegistryObject<Block> DECO_LOOT = new WrappedBlockRegistryBuilder("deco_loot", ()->new Block(Properties.of().instabreak().noCollission().noOcclusion())).mSmp(HBMKey.MODEL_CUBE_ALL, "block_steel").build();
    public static RegistryObject<Block> PEDESTAL = block("pedestal", ()->new Block(Properties.copy(Blocks.STONE).strength(2, 8)));
    public static RegistryObject<Block> BOBBLEHEAD = block("bobblehead", ()->new Block(Properties.copy(Blocks.IRON_BLOCK).strength(1, 5)));
    public static RegistryObject<Block> SNOWGLOBE;

    public static RegistryObject<Block> HAZMAT;

    public static RegistryObject<Block> GRAVEL_OBSIDIAN = block("gravel_obsidian", ()->new Block(Properties.copy(Blocks.STONE).strength(2, 10)));
    public static RegistryObject<Block> GRAVEL_DIAMOND;
    public static RegistryObject<Block> ASPHALT = new WrappedBlockRegistryBuilder("asphalt", () -> new BlockSpeedy(Properties.copy(Blocks.STONE).strength(15, 120), 3)).tab(ModTabs.BLOCKS.getKey()).build();
    public static RegistryObject<Block> ASPHALT_LIGHT;

    public static RegistryObject<Block> REINFORCED_BRICK = block("reinforced_brick", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 100)));
    public static RegistryObject<Block> REINFORCED_DUCRETE;
    public static RegistryObject<Block> REINFORCED_GLASS = block("reinforced_glass", ()->new Block(Properties.copy(Blocks.GLASS).strength(5, 100)));
    public static RegistryObject<Block> REINFORCED_GLASS_PANE = block("reinforced_glass_pane", ()->new Block(Properties.copy(Blocks.IRON_BARS).strength(5, 100)));
    public static RegistryObject<Block> REINFORCED_LIGHT;
    public static RegistryObject<Block> REINFORCED_SAND = block("reinforced_sand", ()->new Block(Properties.copy(Blocks.SANDSTONE).strength(15, 100)));
    public static RegistryObject<Block> REINFORCED_LAMP_OFF;
    public static RegistryObject<Block> REINFORCED_LAMP_ON;
    public static RegistryObject<Block> REINFORCED_LAMINATE;
    public static RegistryObject<Block> REINFORCED_LAMINATE_PANE;

    public static RegistryObject<Block> LAMP_TRITIUM_GREEN_OFF;
    public static RegistryObject<Block> LAMP_TRITIUM_GREEN_ON;
    public static RegistryObject<Block> LAMP_TRITIUM_BLUE_OFF;
    public static RegistryObject<Block> LAMP_TRITIUM_BLUE_ON;

    public static RegistryObject<Block> LAMP_UV_OFF;
    public static RegistryObject<Block> LAMP_UV_ON;
    public static RegistryObject<Block> LAMP_DEMON;

    public static RegistryObject<Block> LANTERN;
    public static RegistryObject<Block> LANTERN_BEHEMOTH;

    public static RegistryObject<Block> REINFORCED_STONE = block("reinforced_stone", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 100)));
    public static RegistryObject<Block> CONCRETE_SMOOTH = new WrappedBlockRegistryBuilder("concrete_smooth", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 140))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "concrete").build();
    public static RegistryObject<Block> CONCRETE_COLORED;
    public static RegistryObject<Block> CONCRETE_COLORED_EXT;
    public static RegistryObject<Block> CONCRETE = new WrappedBlockRegistryBuilder("concrete", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 140))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "concrete_tile").build();
    public static RegistryObject<Block> CONCRETE_ASBESTOS = block("concrete_asbestos", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 140)));
    public static RegistryObject<Block> CONCRETE_SUPER = block("concrete_super", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 1000)));
    public static RegistryObject<Block> CONCRETE_SUPER_BROKEN = block("concrete_super_broken", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 1000)));
    public static RegistryObject<Block> DUCRETE_SMOOTH;
    public static RegistryObject<Block> DUCRETE;
    public static RegistryObject<Block> CONCRETE_PILLAR = new WrappedBlockRegistryBuilder("concrete_pillar", ()->new RotatedPillarBlock(Properties.copy(Blocks.STONE).strength(15, 180))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "concrete_pillar_side").build();
    public static RegistryObject<Block> BRICK_CONCRETE = block("brick_concrete", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 160)));
    public static RegistryObject<Block> BRICK_CONCRETE_MOSSY = block("brick_concrete_mossy", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 160)));
    public static RegistryObject<Block> BRICK_CONCRETE_CRACKED = block("brick_concrete_cracked", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 160)));
    public static RegistryObject<Block> BRICK_CONCRETE_BROKEN = block("brick_concrete_broken", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 160)));
    public static RegistryObject<Block> BRICK_CONCRETE_MARKED = block("brick_concrete_marked", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 160)));
    public static RegistryObject<Block> BRICK_DUCRETE;
    public static RegistryObject<Block> BRICK_OBSIDIAN = block("brick_obsidian", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 120)));
    public static RegistryObject<Block> BRICK_LIGHT = block("brick_light", ()->new Block(Properties.copy(Blocks.STONE).strength(5, 20)));
    public static RegistryObject<Block> BRICK_COMPOUND = block("brick_compound", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 100)));
    public static RegistryObject<Block> BRICK_ASBESTOS = block("brick_asbestos", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 100)));
    public static RegistryObject<Block> BRICK_FIRE = block("brick_fire", ()->new Block(Properties.copy(Blocks.STONE).strength(5, 35)));

    public static RegistryObject<Block> CONCRETE_SLAB = new WrappedBlockRegistryBuilder("concrete_slab", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 140))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "concrete").build();
    public static RegistryObject<Block> CONCRETE_DOUBLE_SLAB = new WrappedBlockRegistryBuilder("concrete_double_slab", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 140))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "concrete").build();
    public static RegistryObject<Block> CONCRETE_BRICK_SLAB = new WrappedBlockRegistryBuilder("concrete_brick_slab", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 160))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "brick_concrete").build();
    public static RegistryObject<Block> CONCRETE_BRICK_DOUBLE_SLAB = new WrappedBlockRegistryBuilder("concrete_brick_double_slab", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 160))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "brick_concrete").build();
    public static RegistryObject<Block> BRICK_SLAB = new WrappedBlockRegistryBuilder("brick_slab", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 100))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "reinforced_brick").build();
    public static RegistryObject<Block> BRICK_DOUBLE_SLAB;

    public static RegistryObject<Block> CONCRETE_SMOOTH_STAIRS = new WrappedBlockRegistryBuilder("concrete_smooth_stairs", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 140))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "concrete").build();
    public static RegistryObject<Block> CONCRETE_STAIRS = new WrappedBlockRegistryBuilder("concrete_stairs", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 140))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "concrete_tile").build();
    public static RegistryObject<Block> CONCRETE_ASBESTOS_STAIRS = new WrappedBlockRegistryBuilder("concrete_asbestos_stairs", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 140))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "concrete_asbestos").build();
    public static RegistryObject<Block> DUCRETE_SMOOTH_STAIRS;
    public static RegistryObject<Block> DUCRETE_STAIRS;
    public static RegistryObject<Block> BRICK_CONCRETE_STAIRS = new WrappedBlockRegistryBuilder("brick_concrete_stairs", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 160))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "brick_concrete").build();
    public static RegistryObject<Block> BRICK_CONCRETE_MOSSY_STAIRS = new WrappedBlockRegistryBuilder("brick_concrete_mossy_stairs", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 160))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "brick_concrete_mossy").build();
    public static RegistryObject<Block> BRICK_CONCRETE_CRACKED_STAIRS = new WrappedBlockRegistryBuilder("brick_concrete_cracked_stairs", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 160))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "brick_concrete_cracked").build();
    public static RegistryObject<Block> BRICK_CONCRETE_BROKEN_STAIRS = new WrappedBlockRegistryBuilder("brick_concrete_broken_stairs", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 160))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "brick_concrete_broken").build();
    public static RegistryObject<Block> BRICK_DUCRETE_STAIRS;
    public static RegistryObject<Block> REINFORCED_STONE_STAIRS = new WrappedBlockRegistryBuilder("reinforced_stone_stairs", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 100))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "reinforced_stone").build();
    public static RegistryObject<Block> REINFORCED_BRICK_STAIRS = new WrappedBlockRegistryBuilder("reinforced_brick_stairs", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 100))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "reinforced_brick").build();
    public static RegistryObject<Block> BRICK_OBSIDIAN_STAIRS = new WrappedBlockRegistryBuilder("brick_obsidian_stairs", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 120))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "brick_obsidian").build();
    public static RegistryObject<Block> BRICK_LIGHT_STAIRS = new WrappedBlockRegistryBuilder("brick_light_stairs", ()->new Block(Properties.copy(Blocks.STONE).strength(5, 20))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "brick_light").build();
    public static RegistryObject<Block> BRICK_COMPOUND_STAIRS = new WrappedBlockRegistryBuilder("brick_compound_stairs", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 100))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "brick_compound").build();
    public static RegistryObject<Block> BRICK_ASBESTOS_STAIRS = new WrappedBlockRegistryBuilder("brick_asbestos_stairs", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 100))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "brick_asbestos").build();
    public static RegistryObject<Block> BRICK_FIRE_STAIRS = new WrappedBlockRegistryBuilder("brick_fire_stairs", ()->new Block(Properties.copy(Blocks.STONE).strength(5, 35))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "brick_fire").build();

    public static RegistryObject<Block> CMB_BRICK;
    public static RegistryObject<Block> CMB_BRICK_REINFORCED;

    public static RegistryObject<Block> VINYL_TILE;

    public static RegistryObject<Block> TILE_LAB;
    public static RegistryObject<Block> TILE_LAB_CRACKED;
    public static RegistryObject<Block> TILE_LAB_BROKEN;

    public static RegistryObject<Block> SIEGE_SHIELD;
    public static RegistryObject<Block> SIEGE_INTERNAL;
    public static RegistryObject<Block> SIEGE_CIRCUIT;
    public static RegistryObject<Block> SIEGE_EMERGENCY;
    public static RegistryObject<Block> SIEGE_HOLE;

    public static final RegistryObject<Block> BLOCK_METEOR = add("block_meteor", () -> new Block(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), "Meteorite Block");
    public static final RegistryObject<Block> BLOCK_METEOR_COBBLE = add("block_meteor_cobble", () -> new Block(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, "Meteorite Cobblestone", HBMKey.DROP_STANDALONE);
    public static final RegistryObject<Block> BLOCK_METEOR_BROKEN = add("block_meteor_broken", () -> new Block(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, "Broken Meteorite Block", HBMKey.DROP_STANDALONE);
    public static final RegistryObject<Block> BLOCK_METEOR_MOLTEN = add("block_meteor_molten", () -> new BlockMolten(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, "Hot Meteorite Cobblestone", HBMKey.DROP_NONE);
    public static final RegistryObject<Block> BLOCK_METEOR_TREASURE = add("block_meteor_treasure", () -> new Block(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, "Broken Meteorite Block", HBMKey.DROP_STANDALONE);
    public static final RegistryObject<Block> METEOR_POLISHED = add("meteor_polished", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), "Polished Meteor Block");
    public static final RegistryObject<Block> METEOR_BRICK = block("meteor_brick", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)));
    public static final RegistryObject<Block> METEOR_BRICK_MOSSY = add("meteor_brick_mossy", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), "Mossy Meteor Bricks");
    public static final RegistryObject<Block> METEOR_BRICK_CRACKED = add("meteor_brick_cracked", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), "Cracked Meteor Bricks");
    public static final RegistryObject<Block> METEOR_BRICK_CHISELED = add("meteor_brick_chiseled", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), "Chiseled Meteor Bricks");
    public static final RegistryObject<Block> METEOR_PILLAR = add("meteor_pillar", () -> new RotatedPillarBlock(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_PILLAR, HBMKey.ORDERLY_GEN, HBMKey.DROP_SELF);
    public static RegistryObject<Block> METEOR_SPAWNER;
    public static RegistryObject<Block> METEOR_BATTERY;

    public static RegistryObject<Block> MOON_TURF;

    public static RegistryObject<Block> BRICK_JUNGLE;
    public static RegistryObject<Block> BRICK_JUNGLE_CRACKED;
    public static RegistryObject<Block> BRICK_JUNGLE_FRAGILE;
    public static RegistryObject<Block> BRICK_JUNGLE_LAVA;
    public static RegistryObject<Block> BRICK_JUNGLE_OOZE;
    public static RegistryObject<Block> BRICK_JUNGLE_MYSTIC;
    public static RegistryObject<Block> BRICK_JUNGLE_TRAP;
    public static RegistryObject<Block> BRICK_JUNGLE_GLYPH;
    public static RegistryObject<Block> BRICK_JUNGLE_CIRCLE;

    public static RegistryObject<Block> BRICK_FORGOTTEN;
    public static RegistryObject<Block> BRICK_RED;

    public static RegistryObject<Block> DECO_COMPUTER = block("deco_computer", ()->new Block(Properties.copy(Blocks.STONE).strength(2, 8)));
    public static RegistryObject<Block> DECO_CRT = block("deco_crt", ()->new Block(Properties.copy(Blocks.STONE).strength(2, 8)));
    public static RegistryObject<Block> DECO_RUSTY_STEEL = block("deco_rusty_steel", ()->new Block(Properties.copy(Blocks.STONE).strength(5, 30)));
    public static RegistryObject<Block> DECO_TOASTER = block("deco_toaster", ()->new Block(Properties.copy(Blocks.STONE).strength(2, 8)));

    public static RegistryObject<Block> FILING_CABINET = block("filing_cabinet", ()->new Block(Properties.copy(Blocks.IRON_BLOCK).strength(2, 8)));

    public static RegistryObject<Block> TAPE_RECORDER = block("tape_recorder", ()->new Block(Properties.copy(Blocks.IRON_BLOCK).strength(2, 8)));
    public static RegistryObject<Block> STEEL_POLES = block("steel_poles", ()->new Block(Properties.copy(Blocks.STONE).strength(5, 30)));
    public static RegistryObject<Block> POLE_TOP = block("pole_top", ()->new Block(Properties.copy(Blocks.STONE).strength(5, 30)));
    public static RegistryObject<Block> POLE_SATELLITE_RECEIVER = block("pole_satellite_receiver", ()->new Block(Properties.copy(Blocks.STONE).strength(5, 30)));
    public static RegistryObject<Block> STEEL_WALL = block("steel_wall", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 100)));
    public static RegistryObject<Block> STEEL_CORNER = block("steel_corner", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 100)));
    public static RegistryObject<Block> STEEL_ROOF = block("steel_roof", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 100)));
    public static RegistryObject<Block> STEEL_BEAM = block("steel_beam", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 100)));
    public static RegistryObject<Block> STEEL_SCAFFOLD = block("steel_scaffold", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 100)));
    public static RegistryObject<Block> STEEL_GRATE = block("steel_grate", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 100)));
    public static RegistryObject<Block> STEEL_GRATE_WIDE = block("steel_grate_wide", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 100)));

    public static RegistryObject<Block> DECO_PIPE = new WrappedBlockRegistryBuilder("deco_pipe", ()->new Block(Properties.copy(Blocks.STONE).strength(2, 5))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "pipe_top").build();
    public static RegistryObject<Block> DECO_PIPE_RUSTED = new WrappedBlockRegistryBuilder("deco_pipe_rusted", ()->new Block(Properties.copy(Blocks.STONE).strength(2, 5))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "pipe_top_rusty").build();
    public static RegistryObject<Block> DECO_PIPE_GREEN = new WrappedBlockRegistryBuilder("deco_pipe_green", ()->new Block(Properties.copy(Blocks.STONE).strength(2, 5))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "pipe_top_green").build();
    public static RegistryObject<Block> DECO_PIPE_GREEN_RUSTED = new WrappedBlockRegistryBuilder("deco_pipe_green_rusted", ()->new Block(Properties.copy(Blocks.STONE).strength(2, 5))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "pipe_top_green_rusty").build();
    public static RegistryObject<Block> DECO_PIPE_RED = new WrappedBlockRegistryBuilder("deco_pipe_red", ()->new Block(Properties.copy(Blocks.STONE).strength(2, 5))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "pipe_top_red").build();
    public static RegistryObject<Block> DECO_PIPE_MARKED = new WrappedBlockRegistryBuilder("deco_pipe_marked", ()->new Block(Properties.copy(Blocks.STONE).strength(2, 5))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "pipe_top_marked").build();
    public static RegistryObject<Block> DECO_PIPE_RIM = new WrappedBlockRegistryBuilder("deco_pipe_rim", ()->new Block(Properties.copy(Blocks.STONE).strength(2, 5))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "pipe_top").build();
    public static RegistryObject<Block> DECO_PIPE_RIM_RUSTED = new WrappedBlockRegistryBuilder("deco_pipe_rim_rusted", ()->new Block(Properties.copy(Blocks.STONE).strength(2, 5))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "pipe_top_rusty").build();
    public static RegistryObject<Block> DECO_PIPE_RIM_GREEN = new WrappedBlockRegistryBuilder("deco_pipe_rim_green", ()->new Block(Properties.copy(Blocks.STONE).strength(2, 5))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "pipe_top_green").build();
    public static RegistryObject<Block> DECO_PIPE_RIM_GREEN_RUSTED = new WrappedBlockRegistryBuilder("deco_pipe_rim_green_rusted", ()->new Block(Properties.copy(Blocks.STONE).strength(2, 5))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "pipe_top_green_rusty").build();
    public static RegistryObject<Block> DECO_PIPE_RIM_RED = new WrappedBlockRegistryBuilder("deco_pipe_rim_red", ()->new Block(Properties.copy(Blocks.STONE).strength(2, 5))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "pipe_top_red").build();
    public static RegistryObject<Block> DECO_PIPE_RIM_MARKED = new WrappedBlockRegistryBuilder("deco_pipe_rim_marked", ()->new Block(Properties.copy(Blocks.STONE).strength(2, 5))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "pipe_top_marked").build();
    public static RegistryObject<Block> DECO_PIPE_FRAMED = new WrappedBlockRegistryBuilder("deco_pipe_framed", ()->new Block(Properties.copy(Blocks.STONE).strength(2, 5))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "pipe_frame").build();
    public static RegistryObject<Block> DECO_PIPE_FRAMED_RUSTED = new WrappedBlockRegistryBuilder("deco_pipe_framed_rusted", ()->new Block(Properties.copy(Blocks.STONE).strength(2, 5))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "pipe_frame").build();
    public static RegistryObject<Block> DECO_PIPE_FRAMED_GREEN = new WrappedBlockRegistryBuilder("deco_pipe_framed_green", ()->new Block(Properties.copy(Blocks.STONE).strength(2, 5))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "pipe_frame").build();
    public static RegistryObject<Block> DECO_PIPE_FRAMED_GREEN_RUSTED = new WrappedBlockRegistryBuilder("deco_pipe_framed_green_rusted", ()->new Block(Properties.copy(Blocks.STONE).strength(2, 5))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "pipe_frame").build();
    public static RegistryObject<Block> DECO_PIPE_FRAMED_RED = new WrappedBlockRegistryBuilder("deco_pipe_framed_red", ()->new Block(Properties.copy(Blocks.STONE).strength(2, 5))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "pipe_frame").build();
    public static RegistryObject<Block> DECO_PIPE_FRAMED_MARKED = new WrappedBlockRegistryBuilder("deco_pipe_framed_marked", ()->new Block(Properties.copy(Blocks.STONE).strength(2, 5))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "pipe_frame").build();
    public static RegistryObject<Block> DECO_PIPE_QUAD = new WrappedBlockRegistryBuilder("deco_pipe_quad", ()->new Block(Properties.copy(Blocks.STONE).strength(2, 5))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "pipe_top").build();
    public static RegistryObject<Block> DECO_PIPE_QUAD_RUSTED = new WrappedBlockRegistryBuilder("deco_pipe_quad_rusted", ()->new Block(Properties.copy(Blocks.STONE).strength(2, 5))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "pipe_top_rusty").build();
    public static RegistryObject<Block> DECO_PIPE_QUAD_GREEN = new WrappedBlockRegistryBuilder("deco_pipe_quad_green", ()->new Block(Properties.copy(Blocks.STONE).strength(2, 5))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "pipe_top_green").build();
    public static RegistryObject<Block> DECO_PIPE_QUAD_GREEN_RUSTED = new WrappedBlockRegistryBuilder("deco_pipe_quad_green_rusted", ()->new Block(Properties.copy(Blocks.STONE).strength(2, 5))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "pipe_top_green_rusty").build();
    public static RegistryObject<Block> DECO_PIPE_QUAD_RED = new WrappedBlockRegistryBuilder("deco_pipe_quad_red", ()->new Block(Properties.copy(Blocks.STONE).strength(2, 5))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "pipe_top_red").build();
    public static RegistryObject<Block> DECO_PIPE_QUAD_MARKED = new WrappedBlockRegistryBuilder("deco_pipe_quad_marked", ()->new Block(Properties.copy(Blocks.STONE).strength(2, 5))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "pipe_top_marked").build();

    public static RegistryObject<Block> BROADCASTER_PC;
    public static RegistryObject<Block> GEIGER = new WrappedBlockRegistryBuilder("geiger", ()->new GeigerCounter(BlockBehaviour.Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey()).loc(HBMKey.REVERSE_GEN).obj("block/geiger", "block/geiger", 1).tags(TAG_MACHINE)
            .tile(TileEntityGeigerBE::new)
            .build();
    public static RegistryObject<Block> HEV_BATTERY = block("hev_battery", ()->new Block(Properties.copy(Blocks.IRON_BLOCK).strength(2, 8)));

    public static final RegistryObject<Block> MACHINE_DECON = new WrappedBlockRegistryBuilder("machine_decon", ()->new com.hbm.block.machine.BlockDecon(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .mSmp(HBMKey.MODEL_CUBE_BOTTOM_TOP, "decon_side", "decon_top")
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.DeconEntity::new)
            .build();

    public static RegistryObject<Block> FENCE_METAL = block("fence_metal", ()->new Block(Properties.copy(Blocks.IRON_BARS).strength(5, 30)));

    public static RegistryObject<Block> SAND_BORON;
    public static RegistryObject<Block> SAND_LEAD;
    public static RegistryObject<Block> SAND_URANIUM;
    public static RegistryObject<Block> SAND_POLONIUM;
    public static RegistryObject<Block> SAND_QUARTZ;
    public static RegistryObject<Block> SAND_GOLD;
    public static RegistryObject<Block> SAND_GOLD198;
    public static RegistryObject<Block> ASH_DIGAMMA;
    public static RegistryObject<Block> GLASS_BORON;
    public static RegistryObject<Block> GLASS_LEAD;
    public static RegistryObject<Block> GLASS_URANIUM;
    public static RegistryObject<Block> GLASS_TRINITITE;
    public static RegistryObject<Block> GLASS_POLONIUM;
    public static RegistryObject<Block> GLASS_ASH;
    public static RegistryObject<Block> GLASS_QUARTZ;

    public static RegistryObject<Block> MUSH;
    public static RegistryObject<Block> MUSH_BLOCK;
    public static RegistryObject<Block> MUSH_BLOCK_STEM;

    public static RegistryObject<Block> GLYPHID_BASE;
    public static final RegistryObject<Block> GLYPHID_SPAWNER = add("glyphid_spawner", ()->new GlyphidSpawner(Properties.copy(Blocks.STONE).pushReaction(PushReaction.IGNORE).explosionResistance(0.5f)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.ORDERLY_GEN, HBMKey.DROP_NONE);

    public static RegistryObject<Block> PLANT_FLOWER;
    public static RegistryObject<Block> PLANT_TALL;
    public static final WrappedRegistryBuilder.RegisterObjectCollection<Block, BlockEnums.EnumDeadPlantType> PLANT_DEAD = new WrappedRegistryBuilder.RegisterObjectCollection(BlockEnums.EnumDeadPlantType.class,
            type -> add("plant_dead." + type.toString().toLowerCase(), ()->new DeadBushBlock(BlockBehaviour.Properties.copy(Blocks.DEAD_BUSH)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CROSS, HBMKey.REVERSE_GEN, HBMKey.DROP_NONE));    public static RegistryObject<Block> REEDS;
    public static RegistryObject<Block> CROP_STRAWBERRY;
    public static RegistryObject<Block> CROP_COFFEE;
    public static RegistryObject<Block> CROP_TEA;

    public static final RegistryObject<Block> WASTE_EARTH = add("waste_earth",()->new WasteEarth(BlockBehaviour.Properties.copy(Blocks.DIRT)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_BOTTOM_TOP, HBMKey.ORDERLY_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static RegistryObject<Block> WASTE_MYCELIUM;
    public static RegistryObject<Block> WASTE_TRINITITE;
    public static RegistryObject<Block> WASTE_TRINITITE_RED;
    public static RegistryObject<Block> WASTE_LOG;
    public static final RegistryObject<Block> WASTE_LEAVES = add("waste_leaves",()->new WasteLeaves(BlockBehaviour.Properties.copy(Blocks.ACACIA_LEAVES).noLootTable()), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_LEAVES, HBMKey.ORDERLY_GEN, HBMKey.DROP_NONE);
    public static RegistryObject<Block> WASTE_PLANKS;
    public static RegistryObject<Block> FROZEN_DIRT;
    public static RegistryObject<Block> FROZEN_GRASS;
    public static RegistryObject<Block> FROZEN_LOG;
    public static RegistryObject<Block> FROZEN_PLANKS;
    public static final RegistryObject<Block> DIRT_DEAD = add("dirt_dead",()->new FallingBlock(BlockBehaviour.Properties.copy(Blocks.GRAVEL)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_SHOVEL);
    public static final RegistryObject<Block> DIRT_OILY = add("dirt_oily",()->new FallingBlock(BlockBehaviour.Properties.copy(Blocks.GRAVEL)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_SHOVEL);
    public static final RegistryObject<Block> SAND_DIRTY = add("sand_dirty",()->new FallingBlock(BlockBehaviour.Properties.copy(Blocks.SAND)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_SHOVEL);
    public static final RegistryObject<Block> SAND_DIRTY_RED = add("sand_dirty_red",()->new FallingBlock(BlockBehaviour.Properties.copy(Blocks.SAND)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_SHOVEL);
    public static final RegistryObject<Block> STONE_CRACKED = add("stone_cracked",()->new FallingBlock(BlockBehaviour.Properties.copy(Blocks.STONE)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE);
    public static RegistryObject<Block> BURNING_EARTH;
    public static RegistryObject<Block> TEKTITE;
    public static RegistryObject<Block> ORE_TEKTITE_OSMIRIDIUM;
    public static RegistryObject<Block> IMPACT_DIRT;

    public static RegistryObject<Block> FALLOUT;
    public static RegistryObject<Block> SALTED_FALLOUT;
    public static RegistryObject<Block> FOAM_LAYER;
    public static RegistryObject<Block> SAND_BORON_LAYER;
    public static RegistryObject<Block> LEAVES_LAYER;

    public static final RegistryObject<Block> SELLAFIELD_SLAKED = block("sellafield_slaked", ()->new Block(BlockBehaviour.Properties.of().explosionResistance(5.0f)), BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static RegistryObject<Block> SELLAFIELD;
    public static RegistryObject<Block> ORE_SELLAFIELD_DIAMOND;
    public static RegistryObject<Block> ORE_SELLAFIELD_EMERALD;
    public static RegistryObject<Block> ORE_SELLAFIELD_URANIUM_SCORCHED;
    public static RegistryObject<Block> ORE_SELLAFIELD_SCHRABIDIUM;
    public static RegistryObject<Block> ORE_SELLAFIELD_RADGEM;

    public static RegistryObject<Block> GEYSIR_WATER;
    public static RegistryObject<Block> GEYSIR_CHLORINE;
    public static RegistryObject<Block> GEYSIR_VAPOR;
    public static RegistryObject<Block> GEYSIR_NETHER;

    public static RegistryObject<Block> OBSERVER_OFF;
    public static RegistryObject<Block> OBSERVER_ON;

    public static RegistryObject<Block> FLAME_WAR;
    public static RegistryObject<Block> FLOAT_BOMB;
    public static RegistryObject<Block> THERM_ENDO;
    public static RegistryObject<Block> THERM_EXO;
    public static RegistryObject<Block> EMP_BOMB;
    public static RegistryObject<Block> DET_CORD;
    public static RegistryObject<Block> DET_CHARGE = block("det_charge", ()->new Block(Properties.copy(Blocks.IRON_BLOCK).strength(2, 8)));
    public static RegistryObject<Block> DET_NUKE;
    public static RegistryObject<Block> DET_SALT;
    public static RegistryObject<Block> DET_MINER;
    public static RegistryObject<Block> RED_BARREL = block("red_barrel", ()->new Block(Properties.copy(Blocks.IRON_BLOCK).strength(0.5F, 2.5F)));
    public static RegistryObject<Block> PINK_BARREL = new WrappedBlockRegistryBuilder("pink_barrel", ()->new Block(Properties.copy(Blocks.IRON_BLOCK).strength(0.5F, 2.5F))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "barrel_pink").build();
    public static RegistryObject<Block> VITRIFIED_BARREL = new WrappedBlockRegistryBuilder("vitrified_barrel", ()->new Block(Properties.copy(Blocks.IRON_BLOCK).strength(0.5F, 2.5F))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "barrel_vitrified").build();
    public static RegistryObject<Block> LOX_BARREL;
    public static RegistryObject<Block> TAINT_BARREL;
    public static RegistryObject<Block> OIL_BARREL;
    public static RegistryObject<Block> CRASHED_BALEFIRE;
    public static RegistryObject<Block> REJUVINATOR;
    public static RegistryObject<Block> FIREWORKS;
    public static RegistryObject<Block> DYNAMITE;
    public static RegistryObject<Block> TNT;
    public static RegistryObject<Block> SEMTEX;
    public static RegistryObject<Block> C4;
    public static RegistryObject<Block> FISSURE_BOMB;

    public static RegistryObject<Block> CHARGE_DYNAMITE;
    public static RegistryObject<Block> CHARGE_MINER;
    public static RegistryObject<Block> CHARGE_C4;
    public static RegistryObject<Block> CHARGE_SEMTEX;

    public static RegistryObject<Block> MINE_AP;
    public static RegistryObject<Block> MINE_HE;
    public static RegistryObject<Block> MINE_SHRAP;
    public static RegistryObject<Block> MINE_FAT;

    public static RegistryObject<Block> CRATE = block("crate", ()->new Block(Properties.copy(Blocks.OAK_PLANKS).strength(1, 2.5F)));
    public static RegistryObject<Block> CRATE_WEAPON = block("crate_weapon", ()->new Block(Properties.copy(Blocks.OAK_PLANKS).strength(1, 2.5F)));
    public static RegistryObject<Block> CRATE_LEAD = block("crate_lead", ()->new Block(Properties.copy(Blocks.OAK_PLANKS).strength(1, 2.5F)));
    public static RegistryObject<Block> CRATE_METAL = block("crate_metal", ()->new Block(Properties.copy(Blocks.OAK_PLANKS).strength(1, 2.5F)));
    public static RegistryObject<Block> CRATE_RED = block("crate_red", ()->new Block(Properties.copy(Blocks.OAK_PLANKS).strength(1, 2.5F)));
    public static RegistryObject<Block> CRATE_CAN = block("crate_can", ()->new Block(Properties.copy(Blocks.OAK_PLANKS).strength(1.0F, 2.5F)));
    public static RegistryObject<Block> CRATE_AMMO;
    public static RegistryObject<Block> CRATE_JUNGLE;

    public static RegistryObject<Block> BOXCAR;
    public static RegistryObject<Block> BOAT;

    public static RegistryObject<Block> SEAL_FRAME;
    public static RegistryObject<Block> SEAL_CONTROLLER;
    public static RegistryObject<Block> SEAL_HATCH;

    public static RegistryObject<Block> VAULT_DOOR;
    public static RegistryObject<Block> BLAST_DOOR;
    public static RegistryObject<Block> SLIDING_BLAST_DOOR;
    public static RegistryObject<Block> FIRE_DOOR;
    public static RegistryObject<Block> TRANSITION_SEAL;
    public static RegistryObject<Block> SILO_HATCH;

    // 1.12.2 Doors
    public static RegistryObject<Block> SECURE_ACCESS_DOOR;
    public static RegistryObject<Block> LARGE_VEHICLE_DOOR;
    public static RegistryObject<Block> QE_CONTAINMENT;
    public static RegistryObject<Block> QE_SLIDING_DOOR;
    public static RegistryObject<Block> ROUND_AIRLOCK_DOOR;
    public static RegistryObject<Block> SLIDING_SEAL_DOOR;
    public static RegistryObject<Block> WATER_DOOR;

    public static RegistryObject<Block> DOOR_METAL = new WrappedBlockRegistryBuilder("door_metal", ()->new Block(Properties.copy(Blocks.IRON_DOOR).strength(5, 5))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "door_metal_lower").build();
    public static RegistryObject<Block> DOOR_OFFICE = new WrappedBlockRegistryBuilder("door_office", ()->new Block(Properties.copy(Blocks.IRON_DOOR).strength(10, 10))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "door_office_lower").build();
    public static RegistryObject<Block> DOOR_BUNKER = new WrappedBlockRegistryBuilder("door_bunker", ()->new Block(Properties.copy(Blocks.IRON_DOOR).strength(10, 100))).tab(ModTabs.BLOCKS.getKey()).mSmp(HBMKey.MODEL_CUBE_ALL, "door_bunker_lower").build();
    public static RegistryObject<Block> DOOR_RED;

    public static RegistryObject<Block> BARBED_WIRE = block("barbed_wire", ()->new Block(Properties.copy(Blocks.IRON_BARS).strength(1, 5)));
    public static RegistryObject<Block> BARBED_WIRE_FIRE;
    public static RegistryObject<Block> BARBED_WIRE_POISON;
    public static RegistryObject<Block> BARBED_WIRE_ACID;
    public static RegistryObject<Block> BARBED_WIRE_WITHER;
    public static RegistryObject<Block> BARBED_WIRE_ULTRADEATH;
    public static RegistryObject<Block> SPIKES = block("spikes", ()->new Block(Properties.copy(Blocks.IRON_BARS).strength(2, 8)));

    public static RegistryObject<Block> CHARGER;

    public static RegistryObject<Block> TESLA;
    public static RegistryObject<Block> AA_BATTERY;

    public static RegistryObject<Block> SAT_MAPPER;
    public static RegistryObject<Block> SAT_SCANNER;
    public static RegistryObject<Block> SAT_RADAR;
    public static RegistryObject<Block> SAT_LASER;
    public static RegistryObject<Block> SAT_FOEQ;
    public static RegistryObject<Block> SAT_RESONATOR;

    public static RegistryObject<Block> SAT_DOCK;

    public static RegistryObject<Block> SOYUZ_CAPSULE;

    public static RegistryObject<Block> CRATE_IRON;
    public static RegistryObject<Block> CRATE_STEEL;
    public static RegistryObject<Block> CRATE_DESH;
    public static RegistryObject<Block> CRATE_TUNGSTEN;
    public static RegistryObject<Block> CRATE_TEMPLATE;
    public static RegistryObject<Block> SAFE;
    public static RegistryObject<Block> MASS_STORAGE;

    private static final Properties PROPERTIES_NUKE = Properties.copy(Blocks.IRON_BLOCK).strength(5, 200);
    public static RegistryObject<Block> NUKE_GADGET = new WrappedBlockRegistryBuilder("nuke_gadget", ()->new NukeGadget(PROPERTIES_NUKE, ConfigBomb.gadgetRadius))
            .tab(ModTabs.NUKE.getKey()).obj("models/block/bomb/gadget", "models/bombs/gadget", 3).tags(TAG_MACHINE)
            .tile(TileNukeGadget::new, true).renderer(RendererBlockNaked::new)
            .build();
    public static RegistryObject<Block> NUKE_BOY = new WrappedBlockRegistryBuilder("nuke_boy", ()->new NukeBoy(PROPERTIES_NUKE, ConfigBomb.boyRadius))
            .tab(ModTabs.NUKE.getKey()).obj("models/block/bomb/boy", "bomb/boy", 2).tags(TAG_MACHINE)
            .tile(NukeBombBoyEntityBE::new, true).renderer(RendererBlockNaked::new)
            .build();
    public static RegistryObject<Block> NUKE_MAN = new WrappedBlockRegistryBuilder("nuke_man", ()->new NukeFat(PROPERTIES_NUKE, ConfigBomb.fatmanRadius))
            .tab(ModTabs.NUKE.getKey()).obj("models/block/bomb/fat_man", "bomb/fat_man", 3).tags(TAG_MACHINE)
            .tile(NukeBombFatEntityBE::new, true).renderer(RendererBlockNaked::new)
            .build();
    public static RegistryObject<Block> NUKE_MIKE;
    public static RegistryObject<Block> NUKE_TSAR;
    public static RegistryObject<Block> NUKE_FLEIJA;
    public static RegistryObject<Block> NUKE_PROTOTYPE;
    public static RegistryObject<Block> NUKE_CUSTOM = new WrappedBlockRegistryBuilder("nuke_custom", ()->new NukeCustom(PROPERTIES_NUKE))
            .tab(ModTabs.NUKE.getKey()).obj("models/block/bomb/boy", "bomb/custom_nuke", 2)
            .tile(NukeBombCustomEntityBE::new, true).renderer(RendererBlockNaked::new)
            .build();
    public static RegistryObject<Block> NUKE_SOLINIUM;
    public static RegistryObject<Block> NUKE_N2;
    public static RegistryObject<Block> NUKE_FSTBMB;
    public static RegistryObject<Block> NUKE_ANTIMATTER;
    public static RegistryObject<Block> BOMB_MULTI;

    public static RegistryObject<Block> PUMP_STEAM;
    public static RegistryObject<Block> PUMP_ELECTRIC;

    public static RegistryObject<Block> HEATER_FIREBOX = new WrappedBlockRegistryBuilder("firebox", ()->new BlockFireBox(Properties.copy(Blocks.IRON_BLOCK)))
            .tab(ModTabs.MACHINE.getKey()).obj("block/machine/firebox", "machine/firebox", 3)
            .tile(TileFireBox::new, true).menu(MenuFirebox::new).gui(GuiFirebox::new).renderer(RendererFirebox::new)
            .build();
    public static RegistryObject<Block> HEATER_OVEN;
    public static RegistryObject<Block> HEATER_OILBURNER;
    public static RegistryObject<Block> HEATER_ELECTRIC;
    public static RegistryObject<Block> HEATER_HEATEX;
    public static final RegistryObject<Block> MACHINE_ASHPIT = new WrappedBlockRegistryBuilder("machine_ashpit", ()->new com.hbm.block.machine.BlockAshpit(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .mSmp(HBMKey.MODEL_CUBE_ALL, "models/machines/ashpit")
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.AshpitEntityBE::new)
            .menu(com.hbm.gui.menu.AshpitMenu::new).gui(com.hbm.gui.screen.AshpitGui::new)
            .build();

    public static RegistryObject<Block> FURNACE_IRON;
    public static RegistryObject<Block> FURNACE_STEEL;
    public static RegistryObject<Block> FURNACE_COMBINATION;
    public static final RegistryObject<Block> MACHINE_STIRLING = new WrappedBlockRegistryBuilder("machine_stirling", ()->new com.hbm.block.machine.BlockStirling(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .obj("block/stirling/stirling", "models/machines/stirling", 2.0F)
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.StirlingEntity::new, true)
            .build();
    public static RegistryObject<Block> MACHINE_STIRLING_STEEL;
    public static RegistryObject<Block> MACHINE_STIRLING_CREATIVE;
    public static final RegistryObject<Block> MACHINE_SAWMILL = new WrappedBlockRegistryBuilder("machine_sawmill", ()->new com.hbm.block.machine.BlockSawmill(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .obj("block/sawmill/sawmill", "models/machines/sawmill", 2.0F)
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.SawmillEntity::new, true)
            .build();
    public static RegistryObject<Block> MACHINE_CRUCIBLE;
    public static RegistryObject<Block> MACHINE_BOILER = new WrappedBlockRegistryBuilder("machine_boiler", ()->new MachineHeatBoiler(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey()).obj("block/machines/boiler", "models/machines/boiler", 4.0F).modelRL(HBM.rl("block/machines/boiler_burst")).model(HBMKey.HORIZONTAL_BISTATE)
            .loot(HBMKey.DROP_SELF).loc(HBMKey.REVERSE_GEN).tags(TAG_MACHINE)
            .tile(TileEntityHeatBoiler::new, true).renderer(RendererBoiler::new)
            .build();
    public static RegistryObject<Block> MACHINE_INDUSTRIAL_BOILER;

    public static final RegistryObject<Block> FOUNDRY_MOLD = new WrappedBlockRegistryBuilder("foundry_mold", () -> new FoundryMold(Properties.copy(Blocks.STONE)))
            .tab(ModTabs.MACHINE.getKey()).model(HBMKey.MODEL_EXISTING).loc(HBMKey.ORDERLY_GEN).loot(HBMKey.DROP_SELF).tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL).build();
    public static RegistryObject<Block> FOUNDRY_BASIN;
    public static RegistryObject<Block> FOUNDRY_CHANNEL;
    public static RegistryObject<Block> FOUNDRY_TANK;
    public static RegistryObject<Block> FOUNDRY_OUTLET;
    public static RegistryObject<Block> MACHINE_STRAND_CASTER;
    public static RegistryObject<Block> FOUNDRY_SLAGTAP;
    public static RegistryObject<Block> SLAG;

    public static RegistryObject<Block> MACHINE_DIFURNACE = new WrappedBlockRegistryBuilder("difurnace", ()->new BlockDifurnace(Properties.of().lightLevel(litEmission(13))))
            .tab(ModTabs.MACHINE.getKey()).loc("Blast Furnace").model(HBMKey.STANDALONE)
            .tile(DifurnaceEntity::new).menu(DifurnaceMenu::new).gui(DifurnaceGui::new)
            .build();
    public static RegistryObject<Block> MACHINE_DIFURNACE_EXTENSION;
    public static RegistryObject<Block> MACHINE_DIFURNACE_RTG_OFF;
    public static RegistryObject<Block> MACHINE_DIFURNACE_RTG_ON;
    //public static final int guiID_test_difurnace = 1; historical

    public static RegistryObject<Block> MACHINE_CENTRIFUGE = new WrappedBlockRegistryBuilder(MachineCentrifuge.id, () -> new MachineCentrifuge(Properties.copy(Blocks.IRON_BLOCK).strength(5.0f, 10.0f)))
            .tab(ModTabs.MACHINE.getKey()).loc("Centrifuge")
            .model((block, provider) -> provider.horizontalBlockWithItem(block, provider.genSimpleModel(block, HBM.rl("block/machines/centrifuge.obj"), HBM.rl("block/machine/centrifuge"), 3)))
            .tile(TileMachineCentrifuge::new, true).renderer(RenderrerCentrifuge::new).menu(MenuCentrifuge::new).gui(GuiCentrifuge::new)
            .tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL).build();
    public static final RegistryObject<Block> MACHINE_GASCENT = new WrappedBlockRegistryBuilder("machine_gascent", ()->new com.hbm.block.machine.BlockGasCent(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .obj("block/gascent/gascent", "models/machines/gascent", 4.0F)
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.GasCentEntity::new, true)
            .menu(GasCentMenu::new).gui(GasCentGui::new)
            .build();

    public static RegistryObject<Block> MACHINE_FEL;
    public static RegistryObject<Block> MACHINE_SILEX;

    public static RegistryObject<Block> MACHINE_CRYSTALLIZER = new WrappedBlockRegistryBuilder(MachineCrystallizer.id, () -> new MachineCrystallizer(Properties.copy(Blocks.IRON_BLOCK).strength(5.0f, 10.0f)))
            .tab(ModTabs.MACHINE.getKey()).loc("Crystallizer")
            .model((block, provider) -> provider.horizontalBlockWithItem(block, provider.genSimpleModel(block, HBM.rl("block/machines/acidizer.obj"), HBM.rl("block/machine/acidizer"), 6)))
            .tile(TileCrystallizer::new, true).renderer(RenderCrystallizer::new).menu(MenuCrystallizer::new).gui(GuiCrystallizer::new)
            .tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL).build();

    public static final RegistryObject<Block> MACHINE_SOLAR = new WrappedBlockRegistryBuilder("machine_solar", ()->new com.hbm.block.machine.BlockSolarPanel(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .obj("block/solar/solar_panel", "solar/solar_panel", 2.0F)
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.SolarPanelEntity::new, true)
            .renderer(com.hbm.render.blockentity.SolarPanelRenderer::new)
            .build();

    public static final RegistryObject<Block> MACHINE_HYDROTREATER = new WrappedBlockRegistryBuilder("machine_hydrotreater", ()->new com.hbm.block.machine.BlockHydrotreater(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .obj("block/hydrotreater/hydrotreater", "hydrotreater/hydrotreater", 6.0F)
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.HydrotreaterEntity::new, true)
            .menu(HydrotreaterMenu::new).gui(HydrotreaterGui::new).build();

    public static final RegistryObject<Block> MACHINE_RADIATOR = new WrappedBlockRegistryBuilder("machine_radiator", ()->new com.hbm.block.machine.BlockRadiator(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .mSmp(HBMKey.MODEL_CUBE_ALL, "block_steel_machine")
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(RadiatorEntityBE::new, true)
            .build();

    public static final RegistryObject<Block> MACHINE_ORE_SLOPPER = new WrappedBlockRegistryBuilder("machine_ore_slopper", () -> new MachineOreSlopper(Properties.of().strength(5.0f, 10.0f)))
            .tab(ModTabs.MACHINE.getKey()).loc("Bedrock Ore Processor")
            .model((block, provider) -> provider.horizontalBlockWithItem(block, provider.genSimpleModel(block, HBM.rl("block/machines/ore_slopper.obj"), HBM.rl("block/machine/ore_slopper"), 7)))
            .tile(TileOreSloppper::new, true).renderer(RendererOreSlopper::new).menu(MenuOreSlopper::new).gui(GuiOreSlopper::new)
            .tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL).build();

    public static RegistryObject<Block> MACHINE_UF6_TANK;

    public static RegistryObject<Block> MACHINE_PUF6_TANK;

    public static final RegistryObject<Block> MACHINE_REACTOR_BREEDING = new WrappedBlockRegistryBuilder("machine_reactor", ()->new com.hbm.block.machine.BlockBreedingReactor(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .obj("block/breeder/breeder", "models/machines/breeder", 3.0F)
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.BreedingReactorEntity::new, true)
            .menu(BreedingReactorMenu::new).gui(BreedingReactorGui::new)
            .build();

    public static RegistryObject<Block> MACHINE_NUKE_FURNACE_OFF;
    public static RegistryObject<Block> MACHINE_NUKE_FURNACE_ON;
    public static RegistryObject<Block> MACHINE_FURNACE_BRICK = new WrappedBlockRegistryBuilder("machine_furnace_brick", ()->new MachineFurnaceBrick(Properties.copy(Blocks.BRICKS).strength(5, 10).lightLevel(litEmission(13))))
            .tab(ModTabs.MACHINE.getKey()).loc("Bricked Furnace").tags(TAG_MACHINE).model(HBMKey.HORIZONTAL_BISTATE).modelType(BlockStateGen.Type.ORIENTABLE_WITH_BOTTOM)
            .tile(TileEntityFurnaceBrick::new).menu(MenuFurnaceBrick::new).gui(GuiFurnaceBrick::new)
            .build();
    public static RegistryObject<Block> MACHINE_RTG_FURNACE = new WrappedBlockRegistryBuilder("machine_rtg_furnace", ()->new MachineFurnaceRtg(Properties.copy(Blocks.BRICKS).strength(5, 10).lightLevel(litEmission(13))))
            .tab(ModTabs.MACHINE.getKey()).loc("RTG Furnace").tags(TAG_MACHINE).model(HBMKey.HORIZONTAL_BISTATE).modelType(BlockStateGen.Type.ORIENTABLE).texSuf(new String[]{"_side_alt", "_alt", "_base_alt"})
            .tile(BERtgFurnace::new).menu(MenuRtgFurnace::new).gui(GuiRtgFurnace::new)
            .build();
    public static final RegistryObject<Block> MACHINE_BLAST_FURNACE = new WrappedBlockRegistryBuilder("machine_blast_furnace", ()->new com.hbm.block.machine.BlockBlastFurnace(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .obj("block/blast_furnace/blast_furnace", "models/machines/blast_furnace", 7.0F)
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.BlastFurnaceEntity::new, true)
            .menu(BlastFurnaceMenu::new).gui(BlastFurnaceGui::new)
            .build();

    public static RegistryObject<Block> MACHINE_GENERATOR;

    public static RegistryObject<Block> MACHINE_INDUSTRIAL_GENERATOR;

    public static RegistryObject<Block> MACHINE_CYCLOTRON;
    public static RegistryObject<Block> MACHINE_EXPOSURE_CHAMBER;

    public static RegistryObject<Block> HADRON_PLATING;
    public static RegistryObject<Block> HADRON_PLATING_BLUE;
    public static RegistryObject<Block> HADRON_PLATING_BLACK;
    public static RegistryObject<Block> HADRON_PLATING_YELLOW;
    public static RegistryObject<Block> HADRON_PLATING_STRIPED;
    public static RegistryObject<Block> HADRON_PLATING_VOLTZ;
    public static RegistryObject<Block> HADRON_PLATING_GLASS;
    public static RegistryObject<Block> HADRON_COIL_ALLOY;
    public static RegistryObject<Block> HADRON_COIL_GOLD;
    public static RegistryObject<Block> HADRON_COIL_NEODYMIUM;
    public static RegistryObject<Block> HADRON_COIL_MAGTUNG;
    public static RegistryObject<Block> HADRON_COIL_SCHRABIDIUM;
    public static RegistryObject<Block> HADRON_COIL_SCHRABIDATE;
    public static RegistryObject<Block> HADRON_COIL_STARMETAL;
    public static RegistryObject<Block> HADRON_COIL_CHLOROPHYTE;
    public static RegistryObject<Block> HADRON_COIL_MESE;
    public static RegistryObject<Block> HADRON_POWER;
    public static RegistryObject<Block> HADRON_POWER_10M;
    public static RegistryObject<Block> HADRON_POWER_100M;
    public static RegistryObject<Block> HADRON_POWER_1G;
    public static RegistryObject<Block> HADRON_POWER_10G;
    public static RegistryObject<Block> HADRON_DIODE;
    public static RegistryObject<Block> HADRON_ANALYSIS;
    public static RegistryObject<Block> HADRON_ANALYSIS_GLASS;
    public static RegistryObject<Block> HADRON_ACCESS;
    public static RegistryObject<Block> HADRON_CORE;
    public static RegistryObject<Block> HADRON_COOLER;

    public static RegistryObject<Block> MACHINE_ELECTRIC_FURNACE = new WrappedBlockRegistryBuilder("machine_electric_furnace", ()->new MachineFurnaceElectric(Properties.of().lightLevel(litEmission(13))))
            .tab(ModTabs.MACHINE.getKey()).loc("Electric Furnace").tags(TAG_MACHINE).model(HBMKey.HORIZONTAL_BISTATE).modelType(BlockStateGen.Type.ORIENTABLE_WITH_BOTTOM)
            .tile(TileEntityMachineElectricFurnace::new).menu(MenuFurnaceElectric::new).gui(GuiFurnaceElectric::new)
            .build();

    public static final RegistryObject<Block> MACHINE_MICROWAVE = new WrappedBlockRegistryBuilder("machine_microwave", ()->new com.hbm.block.machine.BlockMicrowave(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .mSmp(HBMKey.MODEL_CUBE_ALL, "machine_microwave")
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(MicrowaveEntityBE::new)
            .menu(MicrowaveMenu::new).gui(MicrowaveGui::new).build();

    public static RegistryObject<Block> MACHINE_ARC_FURNACE_OFF;
    public static RegistryObject<Block> MACHINE_ARC_FURNACE_ON;

    //public static RegistryObject<Block> MACHINE_DEUTERIUM;

    public static RegistryObject<Block> MACHINE_BATTERY_POTATO;
    public static RegistryObject<Block> MACHINE_BATTERY;
    public static RegistryObject<Block> MACHINE_LITHIUM_BATTERY;
    public static RegistryObject<Block> MACHINE_SCHRABIDIUM_BATTERY;
    public static RegistryObject<Block> MACHINE_DINEUTRONIUM_BATTERY;
    public static RegistryObject<Block> MACHINE_FENSU;
    public static final int guiID_machine_fensu = 99;

    public static RegistryObject<Block> CAPACITOR_BUS;
    public static RegistryObject<Block> CAPACITOR_COPPER;
    public static RegistryObject<Block> CAPACITOR_GOLD;
    public static RegistryObject<Block> CAPACITOR_NIOBIUM;
    public static RegistryObject<Block> CAPACITOR_TANTALIUM;
    public static RegistryObject<Block> CAPACITOR_COMPLEX;

    public static RegistryObject<Block> CAPACITOR_SCHRABIDATE;

    public static RegistryObject<Block> MACHINE_WOOD_BURNER;

    public static RegistryObject<Block> RED_WIRE_COATED = block("red_wire_coated", ()->new Block(Properties.copy(Blocks.STONE).strength(1, 5)));
    public static RegistryObject<Block> RED_CABLE = add("red_cable", ()->new BlockCable(Properties.copy(Blocks.STONE_BRICK_WALL)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.ORDERLY_GEN, HBMKey.DROP_SELF);
    public static RegistryObject<Block> RED_CABLE_CLASSIC;
    public static RegistryObject<Block> RED_CABLE_PAINTABLE;
    public static RegistryObject<Block> RED_CABLE_GAUGE;
    public static RegistryObject<Block> RED_CONNECTOR = block("red_connector", ()->new Block(Properties.copy(Blocks.STONE).strength(1, 5)));
    public static RegistryObject<Block> RED_PYLON;
    public static RegistryObject<Block> RED_PYLON_LARGE;
    public static RegistryObject<Block> SUBSTATION;
    public static RegistryObject<Block> CABLE_SWITCH;
    public static RegistryObject<Block> CABLE_DETECTOR;
    public static RegistryObject<Block> CABLE_DIODE;
    public static final RegistryObject<Block> MACHINE_DETECTOR = new WrappedBlockRegistryBuilder("machine_detector", ()->new com.hbm.block.machine.BlockDetector(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .mSmp(HBMKey.MODEL_CUBE_ALL, "machine_detector_off")
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(DetectorEntityBE::new)
            .build();
    public static RegistryObject<Block> FLUID_DUCT;
    public static RegistryObject<Block> FLUID_DUCT_SOLID;
    public static RegistryObject<Block> FLUID_DUCT_NEO = new WrappedBlockRegistryBuilder("fluid_duct_neo", ()->new BlockFluidPipe(BlockBehaviour.Properties.of().strength(5.0F).explosionResistance(10.0F).sound(net.minecraft.world.level.block.SoundType.METAL)))
            .tab(ModTabs.MACHINE.getKey()).model(HBMKey.MODEL_STANDALONE).loc("Universal Fluid Duct").tags(TAG_MACHINE)
            .tile(PipeEntityBEPipeBase::new).color(BlockFluidPipe::getColor)
            .build();

    public static RegistryObject<Block> FLUID_DUCT_BOX;
    public static RegistryObject<Block> FLUID_DUCT_PAINTABLE;
    public static RegistryObject<Block> FLUID_DUCT_GAUGE;
    public static RegistryObject<Block> FLUID_DUCT_EXHAUST;
    public static RegistryObject<Block> FLUID_VALVE;
    public static RegistryObject<Block> FLUID_SWITCH;
    public static RegistryObject<Block> RADIO_TORCH_SENDER;
    public static RegistryObject<Block> RADIO_TORCH_RECEIVER;
    public static RegistryObject<Block> RADIO_TORCH_COUNTER;
    public static RegistryObject<Block> RADIO_TELEX;

    public static RegistryObject<Block> CONVEYOR;
    public static RegistryObject<Block> CONVEYOR_EXPRESS;
    //public static RegistryObject<Block> CONVEYOR_CLASSIC;
    public static RegistryObject<Block> CONVEYOR_DOUBLE;
    public static RegistryObject<Block> CONVEYOR_TRIPLE;
    public static RegistryObject<Block> CONVEYOR_CHUTE;
    public static RegistryObject<Block> CONVEYOR_LIFT;
    public static RegistryObject<Block> CRANE_EXTRACTOR;
    public static RegistryObject<Block> CRANE_INSERTER;
    public static RegistryObject<Block> CRANE_GRABBER;
    public static RegistryObject<Block> CRANE_ROUTER;
    public static RegistryObject<Block> CRANE_BOXER;
    public static RegistryObject<Block> CRANE_UNBOXER;
    public static RegistryObject<Block> CRANE_SPLITTER;

    public static RegistryObject<Block> DRONE_WAYPOINT;
    public static RegistryObject<Block> DRONE_CRATE;
    public static RegistryObject<Block> DRONE_WAYPOINT_REQUEST;
    public static RegistryObject<Block> DRONE_DOCK;
    public static RegistryObject<Block> DRONE_CRATE_PROVIDER;
    public static RegistryObject<Block> DRONE_CRATE_REQUESTER;

    public static RegistryObject<Block> FAN;

    public static RegistryObject<Block> PISTON_INSERTER;

    public static RegistryObject<Block> CHAIN;

    public static RegistryObject<Block> LADDER_STURDY;
    public static RegistryObject<Block> LADDER_IRON;
    public static RegistryObject<Block> LADDER_GOLD;
    public static RegistryObject<Block> LADDER_COPPER;
    public static RegistryObject<Block> LADDER_TITANIUM;
    public static RegistryObject<Block> LADDER_LEAD;
    public static RegistryObject<Block> LADDER_COBALT;
    public static RegistryObject<Block> LADDER_ALUMINIUM = block("ladder_aluminium", ()->new Block(Properties.copy(Blocks.LADDER).strength(2, 8)));
    public static RegistryObject<Block> LADDER_STEEL = block("ladder_steel", ()->new Block(Properties.copy(Blocks.LADDER).strength(2, 8)));
    public static RegistryObject<Block> LADDER_TUNGSTEN = block("ladder_tungsten", ()->new Block(Properties.copy(Blocks.LADDER).strength(2, 8)));

    private static Properties PROPERTIES_BARREL = BlockBehaviour.Properties.of().strength(2.0F).explosionResistance(5.0F).requiresCorrectToolForDrops().sound(SoundType.METAL);
    public static RegistryObject<Block> BARREL_PLASTIC = add("barrel_plastic", ()->new BlockFluidBarrel(BlockBehaviour.Properties.of().strength(2.0F).explosionResistance(5.0F).requiresCorrectToolForDrops().sound(SoundType.STONE), BlockFluidBarrel.BarrelProperties.of().capacity(12000)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "Safe Barrel™", HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static RegistryObject<Block> BARREL_CORRODED = new WrappedBlockRegistryBuilder("barrel_corroded", ()->new BlockFluidBarrel(PROPERTIES_BARREL, BlockFluidBarrel.BarrelProperties.of().capacity(6000).hotResist()))
            .tab(ModTabs.MACHINE.getKey()).tags(TAG_MACHINE).loc(HBMKey.REVERSE_GEN).obj("models/block/barrel/barrel", "barrel_corroded", 1)
            .tile(BarrelEntityBE::new).menu(BarrelMenu::new).gui(BarrelGui::new)
            .build();
    public static RegistryObject<Block> BARREL_IRON = new WrappedBlockRegistryBuilder("barrel_iron", ()->new BlockFluidBarrel(PROPERTIES_BARREL, BlockFluidBarrel.BarrelProperties.of().capacity(8000).hotResist()))
            .tab(ModTabs.MACHINE.getKey()).tags(TAG_MACHINE).loc(HBMKey.REVERSE_GEN).obj("models/block/barrel/barrel", "barrel_iron", 1)
            .tile(BarrelEntityBE::new).menu(BarrelMenu::new).gui(BarrelGui::new)
            .build();
    public static RegistryObject<Block> BARREL_STEEL = new WrappedBlockRegistryBuilder("barrel_steel", ()->new BlockFluidBarrel(PROPERTIES_BARREL, BlockFluidBarrel.BarrelProperties.of().capacity(16000).hotResist()))
            .tab(ModTabs.MACHINE.getKey()).tags(TAG_MACHINE).loc(HBMKey.REVERSE_GEN).obj("models/block/barrel/barrel", "barrel_steel", 1)
            .tile(BarrelEntityBE::new).menu(BarrelMenu::new).gui(BarrelGui::new)
            .build();
    public static RegistryObject<Block> BARREL_TCALLOY = new WrappedBlockRegistryBuilder("barrel_tcalloy", ()->new BlockFluidBarrel(PROPERTIES_BARREL, BlockFluidBarrel.BarrelProperties.of().capacity(24000).hotResist()))
            .tab(ModTabs.MACHINE.getKey()).tags(TAG_MACHINE).loc("Technetium Steel Barrel").obj("models/block/barrel/barrel", "barrel_tcalloy", 1)
            .tile(BarrelEntityBE::new).menu(BarrelMenu::new).gui(BarrelGui::new)
            .build();
    public static RegistryObject<Block> BARREL_ANTIMATTER = new WrappedBlockRegistryBuilder("barrel_antimatter", ()->new BlockFluidBarrel(PROPERTIES_BARREL, BlockFluidBarrel.BarrelProperties.of().capacity(16000).hotResist()))
            .tab(ModTabs.MACHINE.getKey()).tags(TAG_MACHINE).loc("Magnetic Antimatter Container").obj("models/block/barrel/barrel", "barrel_antimatter", 1)
            .tile(BarrelEntityBE::new).menu(BarrelMenu::new).gui(BarrelGui::new)
            .build();
    public static RegistryObject<Block> MACHINE_TRANSFORMER;
    public static RegistryObject<Block> MACHINE_TRANSFORMER_20;
    public static RegistryObject<Block> MACHINE_TRANSFORMER_DNT;
    public static RegistryObject<Block> MACHINE_TRANSFORMER_DNT_20;

    public static RegistryObject<Block> BOMB_MULTI_LARGE;
    public static final int guiID_bomb_multi_large = 18;

    public static final RegistryObject<Block> MACHINE_SOLAR_BOILER = new WrappedBlockRegistryBuilder("machine_solar_boiler", ()->new com.hbm.block.machine.BlockSolarBoiler(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey()).obj("block/machines/solar_boiler", "models/machines/solar_boiler", 3.0F).loot(HBMKey.DROP_SELF).loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.SolarBoilerEntity::new, true).renderer(RendererSolarBoiler::new)
            .build();
    public static final int guiID_solar_boiler = 18;
    public static RegistryObject<Block> SOLAR_MIRROR = new WrappedBlockRegistryBuilder("solar_mirror", ()->new SolarMirror(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey()).obj("block/machines/solar_mirror", "models/machines/solar_mirror", 3.0F).model(HBMKey.SIMPLE).loot(HBMKey.DROP_SELF).loc(HBMKey.REVERSE_GEN)
            .tile(TileEntitySolarMirror::new).renderer(RendererSolarMirror::new)
            .build();

    public static RegistryObject<Block> STRUCT_LAUNCHER;
    public static RegistryObject<Block> STRUCT_SCAFFOLD;
    public static RegistryObject<Block> STRUCT_LAUNCHER_CORE;
    public static RegistryObject<Block> STRUCT_LAUNCHER_CORE_LARGE;
    public static RegistryObject<Block> STRUCT_SOYUZ_CORE;
    public static RegistryObject<Block> STRUCT_ITER_CORE;
    public static RegistryObject<Block> STRUCT_PLASMA_CORE;
    public static RegistryObject<Block> STRUCT_WATZ_CORE;

    public static RegistryObject<Block> FACTORY_TITANIUM_HULL;
    @Deprecated public static RegistryObject<Block> FACTORY_TITANIUM_FURNACE;
    @Deprecated public static RegistryObject<Block> FACTORY_TITANIUM_CONDUCTOR;

    public static RegistryObject<Block> FACTORY_ADVANCED_HULL;
    @Deprecated public static RegistryObject<Block> FACTORY_ADVANCED_FURNACE;
    @Deprecated public static RegistryObject<Block> FACTORY_ADVANCED_CONDUCTOR;

    public static RegistryObject<Block> CM_BLOCK;
    public static RegistryObject<Block> CM_SHEET;
    public static RegistryObject<Block> CM_ENGINE;
    public static RegistryObject<Block> CM_TANK;
    public static RegistryObject<Block> CM_CIRCUIT;
    public static RegistryObject<Block> CM_PORT;
    public static RegistryObject<Block> CUSTOM_MACHINE;
    public static RegistryObject<Block> CM_ANCHOR;

    public static RegistryObject<Block> PWR_FUEL;
    public static RegistryObject<Block> PWR_CONTROL;
    public static RegistryObject<Block> PWR_CHANNEL;
    public static RegistryObject<Block> PWR_HEATEX;
    public static RegistryObject<Block> PWR_NEUTRON_SOURCE;
    public static RegistryObject<Block> PWR_REFLECTOR;
    public static RegistryObject<Block> PWR_CASING;
    public static RegistryObject<Block> PWR_PORT;
    public static RegistryObject<Block> PWR_CONTROLLER;
    public static RegistryObject<Block> PWR_BLOCK;

    public static RegistryObject<Block> FUSION_CONDUCTOR;
    public static RegistryObject<Block> FUSION_CENTER;
    public static RegistryObject<Block> FUSION_MOTOR;
    public static RegistryObject<Block> FUSION_HEATER;
    public static RegistryObject<Block> FUSION_HATCH;
    //public static RegistryObject<Block> FUSION_CORE;
    public static RegistryObject<Block> PLASMA;

    public static RegistryObject<Block> ITER;
    public static RegistryObject<Block> PLASMA_HEATER;

    public static RegistryObject<Block> WATZ;
    public static RegistryObject<Block> WATZ_PUMP;

    public static RegistryObject<Block> WATZ_ELEMENT;
    public static RegistryObject<Block> WATZ_CONTROL;
    public static RegistryObject<Block> WATZ_COOLER;
    public static RegistryObject<Block> WATZ_END;
    public static RegistryObject<Block> WATZ_CONDUCTOR;

    public static RegistryObject<Block> FWATZ_CONDUCTOR;
    public static RegistryObject<Block> FWATZ_COOLER;
    public static RegistryObject<Block> FWATZ_TANK;
    public static RegistryObject<Block> FWATZ_SCAFFOLD;
    public static RegistryObject<Block> FWATZ_HATCH;
    public static RegistryObject<Block> FWATZ_COMPUTER;
    public static RegistryObject<Block> FWATZ_CORE;
    public static RegistryObject<Block> FWATZ_PLASMA;

    public static RegistryObject<Block> BALEFIRE = block("balefire", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 120)));
    public static RegistryObject<Block> FIRE_DIGAMMA;
    public static RegistryObject<Block> DIGAMMA_MATTER;

    public static RegistryObject<Block> AMS_BASE;
    public static RegistryObject<Block> AMS_EMITTER;
    public static RegistryObject<Block> AMS_LIMITER;

    public static RegistryObject<Block> DFC_EMITTER;
    public static RegistryObject<Block> DFC_INJECTOR;
    public static RegistryObject<Block> DFC_RECEIVER;
    public static RegistryObject<Block> DFC_STABILIZER;
    public static RegistryObject<Block> DFC_CORE;

    public static RegistryObject<Block> MACHINE_CONVERTER_HE_RF = new WrappedBlockRegistryBuilder("machine_converter_he_rf", ()->new com.hbm.block.machine.BlockConverterHeRf(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .mSmp(HBMKey.MODEL_CUBE_ALL, "machine_converter_he_rf")
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(ConverterHeRfEntityBE::new)
            .build();
    public static final int guiID_converter_he_rf = 28;
    public static RegistryObject<Block> MACHINE_CONVERTER_RF_HE = new WrappedBlockRegistryBuilder("machine_converter_rf_he", ()->new com.hbm.block.machine.BlockConverterRfHe(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .mSmp(HBMKey.MODEL_CUBE_ALL, "machine_converter_rf_he")
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(ConverterRfHeEntityBE::new)
            .build();

    public static RegistryObject<Block> MACHINE_SCHRABIDIUM_TRANSMUTATOR;

    public static RegistryObject<Block> MACHINE_DISCHARGER;


    public static final RegistryObject<Block> MACHINE_DIESEL = new WrappedBlockRegistryBuilder("machine_diesel", ()->new com.hbm.block.machine.BlockDiesel(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .obj("block/dieselgen/dieselgen", "dieselgen/dieselgen", 1.0F)
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(DieselEntityBE::new)
            .menu(DieselMenu::new).gui(DieselGui::new).build();
    public static final RegistryObject<Block> MACHINE_COMBUSTION_ENGINE = new WrappedBlockRegistryBuilder("machine_combustion_engine", ()->new com.hbm.block.machine.BlockCombustionEngine(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .obj("block/combustion_engine/combustion_engine", "models/machines/combustion_engine", 4.0F)
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.CombustionEngineEntity::new, true)
            .menu(CombustionEngineMenu::new).gui(CombustionEngineGui::new)
            .build();

    public static RegistryObject<Block> MACHINE_SHREDDER;

    public static RegistryObject<Block> MACHINE_SHREDDER_LARGE;
    public static final int guiID_machine_shredder_large = 76;

    public static RegistryObject<Block> MACHINE_TELEPORTER;
    public static RegistryObject<Block> TELEANCHOR;
    public static RegistryObject<Block> FIELD_DISTURBER;
    public static RegistryObject<Block> TROLL_DISTURBER;

    public static final RegistryObject<Block> MACHINE_RTG_GREY = new WrappedBlockRegistryBuilder("machine_rtg", ()->new com.hbm.block.machine.BlockRTG(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .mSmp(HBMKey.MODEL_CUBE_ALL, "rtg")
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.RTGEntityBE::new)
            .menu(com.hbm.gui.menu.RTGMenu::new).gui(com.hbm.gui.screen.RTGGui::new)
            .build();
    public static RegistryObject<Block> MACHINE_AMGEN;
    public static RegistryObject<Block> MACHINE_GEO;
    public static RegistryObject<Block> MACHINE_MINIRTG;
    public static RegistryObject<Block> MACHINE_POWERRTG;
    public static final RegistryObject<Block> MACHINE_RADIOLYSIS = new WrappedBlockRegistryBuilder("machine_radiolysis", ()->new com.hbm.block.machine.BlockRadiolysis(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .obj("block/radiolysis/radiolysis", "models/radiolysis", 1.0F)
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.RadiolysisEntity::new, true)
            .menu(RadiolysisMenu::new).gui(RadiolysisGui::new)
            .build();
    public static RegistryObject<Block> MACHINE_HEPHAESTUS;

    public static RegistryObject<Block> MACHINE_WELL;
    public static RegistryObject<Block> OIL_PIPE;
    public static RegistryObject<Block> MACHINE_PUMPJACK;
    public static RegistryObject<Block> MACHINE_FRACKING_TOWER;

    public static RegistryObject<Block> MACHINE_FLARE;
    public static RegistryObject<Block> CHIMNEY_BRICK;
    public static RegistryObject<Block> CHIMNEY_INDUSTRIAL;

    public static final RegistryObject<Block> MACHINE_REFINERY = new WrappedBlockRegistryBuilder("machine_refinery", ()->new com.hbm.block.machine.BlockRefinery(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .obj("block/refinery/refinery", "refinery/refinery", 8.0F)
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.RefineryEntity::new, true)
            .menu(RefineryMenu::new).gui(RefineryGui::new).build();
    public static final RegistryObject<Block> MACHINE_VACUUM_DISTILL = new WrappedBlockRegistryBuilder("machine_vacuum_distill", ()->new com.hbm.block.machine.BlockVacuumDistill(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .obj("block/vacuum_distill/vacuum_distill", "models/machines/vacuum_distill", 9.0F)
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.VacuumDistillEntity::new, true)
            .menu(VacuumDistillMenu::new).gui(VacuumDistillGui::new)
            .build();
    public static final RegistryObject<Block> MACHINE_FRACTION_TOWER = new WrappedBlockRegistryBuilder("machine_fraction_tower", ()->new com.hbm.block.machine.BlockFractionTower(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .obj("block/fraction_tower/fraction_tower", "models/machines/fraction_tower", 3.0F)
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.FractionTowerEntity::new, true)
            .build();
    public static RegistryObject<Block> FRACTION_SPACER;
    public static final RegistryObject<Block> MACHINE_CATALYTIC_CRACKER = new WrappedBlockRegistryBuilder("machine_catalytic_cracker", ()->new com.hbm.block.machine.BlockCatalyticCracker(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .obj("block/catalytic_cracker/catalytic_cracker", "models/machines/catalytic_cracker", 16.0F)
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.CatalyticCrackerEntity::new, true)
            .build();
    public static final RegistryObject<Block> MACHINE_CATALYTIC_REFORMER = new WrappedBlockRegistryBuilder("machine_catalytic_reformer", ()->new com.hbm.block.machine.BlockCatalyticReformer(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .obj("block/catalytic_reformer/catalytic_reformer", "models/machines/catalytic_reformer", 3.0F)
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.CatalyticReformerEntity::new, true)
            .menu(CatalyticReformerMenu::new).gui(CatalyticReformerGui::new)
            .build();
    public static final RegistryObject<Block> MACHINE_COKER = new WrappedBlockRegistryBuilder("machine_coker", ()->new com.hbm.block.machine.BlockCoker(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .obj("block/coker/coker", "coker/coker", 22.0F)
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.CokerEntity::new, true)
            .menu(CokerMenu::new).gui(CokerGui::new).build();
    public static final RegistryObject<Block> MACHINE_MILK_REFORMER = new WrappedBlockRegistryBuilder("machine_milk_reformer", ()->new com.hbm.block.machine.BlockMilkReformer(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .obj("block/milk_reformer/milk_reformer", "models/machines/milker", 7.0F)
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.MilkReformerEntity::new, true)
            .menu(MilkReformerMenu::new).gui(MilkReformerGui::new)
            .build();

    public static final RegistryObject<Block> MACHINE_CRYO_DISTILL = new WrappedBlockRegistryBuilder("machine_cryo_distill", ()->new com.hbm.block.machine.BlockCryoDistill(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .obj("block/cryo_distill/cryo_distill", "models/machines/cryo_distill", 8.0F)
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.CryoDistillEntity::new, true)
            .menu(CryoDistillMenu::new).gui(CryoDistillGui::new)
            .build();

    public static RegistryObject<Block> MACHINE_BOILER_OFF;
    public static RegistryObject<Block> MACHINE_BOILER_ON;
    public static final RegistryObject<Block> MACHINE_HEAT_BOILER = new WrappedBlockRegistryBuilder("machine_heat_boiler", ()->new com.hbm.block.machine.BlockHeatBoiler(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .obj("block/boiler/boiler", "models/machines/boiler", 4.0F)
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.HeatBoilerEntity::new, true)
            .build();

    public static RegistryObject<Block> MACHINE_BOILER_ELECTRIC_OFF;
    public static RegistryObject<Block> MACHINE_BOILER_ELECTRIC_ON;

    public static final RegistryObject<Block> MACHINE_STEAM_ENGINE = new WrappedBlockRegistryBuilder("machine_steam_engine", ()->new com.hbm.block.machine.BlockSteamEngine(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .obj("block/steam_engine/steam_engine", "models/machines/steam_engine", 6.0F)
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.SteamEngineEntity::new, true)
            .build();
    public static RegistryObject<Block> MACHINE_TURBINE;
    public static RegistryObject<Block> MACHINE_LARGE_TURBINE;

    public static final RegistryObject<Block> MACHINE_DEUTERIUM_EXTRACTOR = new WrappedBlockRegistryBuilder("machine_deuterium_extractor", ()->new com.hbm.block.machine.BlockDeuteriumExtractor(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .mSmp(HBMKey.MODEL_CUBE_BOTTOM_TOP, "deuterium_extractor_side", "deuterium_extractor_top_water")
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.DeuteriumExtractorEntity::new)
            .build();
    public static RegistryObject<Block> MACHINE_DEUTERIUM_TOWER;
    public static RegistryObject<Block> MACHINE_ATMO_TOWER;
    public static RegistryObject<Block> MACHINE_ATMO_VENT;

    public static final RegistryObject<Block> MACHINE_LIQUEFACTOR = new WrappedBlockRegistryBuilder("machine_liquefactor", ()->new com.hbm.block.machine.BlockLiquefactor(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .obj("block/liquefactor/liquefactor", "liquefactor/liquefactor", 3.0F)
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.LiquefactorEntity::new, true)
            .menu(LiquefactorMenu::new).gui(LiquefactorGui::new).build();
    public static final RegistryObject<Block> MACHINE_SOLIDIFIER = new WrappedBlockRegistryBuilder("machine_solidifier", ()->new com.hbm.block.machine.BlockSolidifier(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .obj("block/solidifier/solidifier", "solidifier/solidifier", 3.0F)
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.SolidifierEntity::new, true)
            .menu(SolidifierMenu::new).gui(SolidifierGui::new).build();
    public static RegistryObject<Block> MACHINE_COMPRESSOR;

    public static RegistryObject<Block> MACHINE_CHUNGUS;
    public static RegistryObject<Block> MACHINE_CONDENSER;
    public static RegistryObject<Block> MACHINE_TOWER_SMALL;
    public static RegistryObject<Block> MACHINE_TOWER_LARGE;
    public static RegistryObject<Block> MACHINE_CONDENSER_POWERED;

    public static RegistryObject<Block> MACHINE_ELECTROLYSER;

    public static RegistryObject<Block> MACHINE_DEAERATOR;
    public static final int guiID_machine_deaerator = 74;

    public static RegistryObject<Block> MACHINE_EXCAVATOR;
    public static RegistryObject<Block> MACHINE_AUTOSAW;

    public static RegistryObject<Block> MACHINE_MINING_LASER;
    public static RegistryObject<Block> BARRICADE; // a sand bag that drops nothing, for automated walling purposes

    public static RegistryObject<Block> MACHINE_ASSEMBLER;
    public static RegistryObject<Block> MACHINE_ASSEMFAC;
    public static final RegistryObject<Block> MACHINE_ARC_WELDER = new WrappedBlockRegistryBuilder("machine_arc_welder", ()->new com.hbm.block.machine.BlockArcWelder(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .obj("block/arc_welder/arc_welder", "models/machines/arc_welder", 1.0F)
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.ArcWelderEntity::new, true)
            .menu(ArcWelderMenu::new).gui(ArcWelderGui::new)
            .build();

    public static RegistryObject<Block> MACHINE_CHEMPLANT = add("chemplant", ()->new BlockChemplant(BlockBehaviour.Properties.of().strength(5.0F).explosionResistance(30.0F)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "Chemical Plant", HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static RegistryObject<Block> MACHINE_CHEMFAC;
    public static final RegistryObject<Block> MACHINE_MIXER = new WrappedBlockRegistryBuilder("machine_mixer", ()->new com.hbm.block.machine.BlockMixer(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .obj("block/mixer/mixer", "models/machines/mixer", 3.0F)
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.MixerEntity::new, true)
            .menu(MixerMenu::new).gui(MixerGui::new)
            .build();

    public static RegistryObject<Block> MACHINE_FLUIDTANK;
    public static RegistryObject<Block> MACHINE_BAT9000;
    public static RegistryObject<Block> MACHINE_ORBUS;
    public static final RegistryObject<Block> MACHINE_BIGASS_TANK = new WrappedBlockRegistryBuilder("machine_bigass_tank", ()->new com.hbm.block.machine.BlockBigAssTank(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .obj("block/bigasstank/bigasstank", "models/machines/bigasstank", 9.0F)
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.BigAssTankEntity::new, true)
            .build();
    public static final RegistryObject<Block> MACHINE_ALKYLATION = new WrappedBlockRegistryBuilder("machine_alkylation", ()->new com.hbm.block.machine.BlockAlkylation(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .obj("block/alkylation/alkylation", "models/machines/alkylation_unit", 4.0F)
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.AlkylationEntity::new, true)
            .build();

    public static RegistryObject<Block> LAUNCH_PAD = add("launch_pad", ()->new LaunchPad(BlockBehaviour.Properties.of().strength(5.0F).explosionResistance(10.0F)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "Launch Pad", HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);

    public static RegistryObject<Block> MACHINE_MISSILE_ASSEMBLY;

    public static RegistryObject<Block> COMPACT_LAUNCHER;

    public static RegistryObject<Block> LAUNCH_TABLE;

    public static RegistryObject<Block> SOYUZ_LAUNCHER;

    public static RegistryObject<Block> MACHINE_RADAR;
    public static RegistryObject<Block> MACHINE_RADAR_LARGE;
    public static RegistryObject<Block> RADAR_SCREEN;

    public static RegistryObject<Block> MACHINE_TURBOFAN;
    public static RegistryObject<Block> MACHINE_TURBINEGAS;


    public static RegistryObject<Block> MACHINE_SELENIUM;

    public static RegistryObject<Block> PRESS_PREHEATER = add("press_preheater", ()->new BlockBase(Properties.of()), ModTabs.MACHINE.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.ORDERLY_GEN, HBMKey.DROP_SELF);
    public static RegistryObject<Block> MACHINE_PRESS = new WrappedBlockRegistryBuilder("machine_press", ()->new BlockPress(Properties.of())).tab(ModTabs.MACHINE.getKey())
            .model(HBMKey.HORIZONTAL).modelType(BlockStateGen.Type.EXISTING).modelRL(HBM.rl( "block/press")).loc("Burner Press").tags(TAG_MACHINE)
            .tile(PressEntityBE::new).menu(PressMenu::new).gui(PressGui::new).renderer(PressRenderer::new)
            .build();
    public static final RegistryObject<Block> MACHINE_EPRESS = new WrappedBlockRegistryBuilder("machine_epress", ()->new com.hbm.block.machine.BlockEPress(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .mSmp(HBMKey.MODEL_CUBE_ALL, "machine_epress")
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.EPressEntityBE::new)
            .menu(com.hbm.gui.menu.EPressMenu::new).gui(com.hbm.gui.screen.EPressGui::new)
            .build();
    public static RegistryObject<Block> MACHINE_CONVEYOR_PRESS;

    public static final RegistryObject<Block> MACHINE_SIREN = new WrappedBlockRegistryBuilder("machine_siren", ()->new com.hbm.block.machine.BlockSiren(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .mSmp(HBMKey.MODEL_CUBE_ALL, "machine_siren")
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(com.hbm.blockentity.machine.SirenEntity::new)
            .build();

    public static RegistryObject<Block> MACHINE_RADGEN;

    public static RegistryObject<Block> MACHINE_SATLINKER;
    public static final RegistryObject<Block> MACHINE_KEY_FORGE = new WrappedBlockRegistryBuilder("machine_keyforge", ()->new com.hbm.block.machine.BlockKeyForge(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .mSmp(HBMKey.MODEL_CUBE_ALL, "machine_keyforge_side")
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(KeyForgeEntityBE::new)
            .menu(KeyForgeMenu::new).gui(KeyForgeGui::new).build();

    public static RegistryObject<Block> MACHINE_ARMOR_TABLE;

    public static RegistryObject<Block> REACTOR_RESEARCH;
    public static RegistryObject<Block> REACTOR_ZIRNOX;
    public static RegistryObject<Block> ZIRNOX_DESTROYED;

    public static RegistryObject<Block> MACHINE_CONTROLLER;

    public static RegistryObject<Block> MACHINE_SPP_BOTTOM;
    public static RegistryObject<Block> MACHINE_SPP_TOP;

    public static RegistryObject<Block> RADIOBOX;
    public static RegistryObject<Block> RADIOREC = block("radiorec", ()->new Block(Properties.copy(Blocks.IRON_BLOCK).strength(2, 8)));

    public static RegistryObject<Block> MACHINE_FORCEFIELD;

    public static RegistryObject<Block> MACHINE_WASTE_DRUM;
    public static RegistryObject<Block> MACHINE_STORAGE_DRUM;

    public static RegistryObject<Block> MACHINE_AUTOCRAFTER;
    public static final RegistryObject<Block> MACHINE_FUNNEL = new WrappedBlockRegistryBuilder("machine_funnel", ()->new com.hbm.block.machine.BlockFunnel(Properties.of().strength(5.0F).explosionResistance(10.0F)))
            .tab(ModTabs.MACHINE.getKey())
            .obj("block/funnel/funnel", "machine_funnel_side", 1.0F)
            .loot(HBMKey.DROP_SELF)
            .loc(HBMKey.REVERSE_GEN)
            .tile(FunnelEntityBE::new)
            .menu(FunnelMenu::new).gui(FunnelGui::new).build();

    public static RegistryObject<Block> ANVIL_IRON;
    public static RegistryObject<Block> ANVIL_LEAD;
    public static RegistryObject<Block> ANVIL_STEEL;
    public static RegistryObject<Block> ANVIL_METEORITE;
    public static RegistryObject<Block> ANVIL_STARMETAL;
    public static RegistryObject<Block> ANVIL_FERROURANIUM;
    public static RegistryObject<Block> ANVIL_BISMUTH;
    public static RegistryObject<Block> ANVIL_SCHRABIDATE;
    public static RegistryObject<Block> ANVIL_DNT;
    public static RegistryObject<Block> ANVIL_OSMIRIDIUM;
    public static RegistryObject<Block> ANVIL_MURKY;

    public static RegistryObject<Block> TURRET_CHEKHOV;
    public static RegistryObject<Block> TURRET_FRIENDLY;
    public static RegistryObject<Block> TURRET_JEREMY;
    public static RegistryObject<Block> TURRET_TAUON;
    public static RegistryObject<Block> TURRET_RICHARD;
    public static RegistryObject<Block> TURRET_HOWARD;
    public static RegistryObject<Block> TURRET_HOWARD_DAMAGED;
    public static RegistryObject<Block> TURRET_MAXWELL;
    public static RegistryObject<Block> TURRET_FRITZ;
    public static RegistryObject<Block> TURRET_BRANDON;
    public static RegistryObject<Block> TURRET_ARTY;
    public static RegistryObject<Block> TURRET_HIMARS;
    public static RegistryObject<Block> TURRET_SENTRY;

    public static RegistryObject<Block> RBMK_ROD;
    public static RegistryObject<Block> RBMK_ROD_MOD;
    public static RegistryObject<Block> RBMK_ROD_REASIM;
    public static RegistryObject<Block> RBMK_ROD_REASIM_MOD;
    public static RegistryObject<Block> RBMK_CONTROL;
    public static RegistryObject<Block> RBMK_CONTROL_MOD;
    public static RegistryObject<Block> RBMK_CONTROL_AUTO;
    public static RegistryObject<Block> RBMK_BLANK;
    public static RegistryObject<Block> RBMK_BOILER;
    public static RegistryObject<Block> RBMK_REFLECTOR;
    public static RegistryObject<Block> RBMK_ABSORBER;
    public static RegistryObject<Block> RBMK_MODERATOR;
    public static RegistryObject<Block> RBMK_OUTGASSER;
    public static RegistryObject<Block> RBMK_STORAGE;
    public static RegistryObject<Block> RBMK_COOLER;
    public static RegistryObject<Block> RBMK_BURNER;
    public static RegistryObject<Block> RBMK_HEATER;
    public static RegistryObject<Block> RBMK_CONSOLE;
    public static RegistryObject<Block> RBMK_CRANE_CONSOLE;
    public static RegistryObject<Block> RBMK_LOADER;
    public static RegistryObject<Block> RBMK_STEAM_INLET;
    public static RegistryObject<Block> RBMK_STEAM_OUTLET;
    public static RegistryObject<Block> RBMK_HEATEX;
    public static RegistryObject<Block> PRIBRIS;
    public static RegistryObject<Block> PRIBRIS_BURNING;
    public static RegistryObject<Block> PRIBRIS_RADIATING;
    public static RegistryObject<Block> PRIBRIS_DIGAMMA;

    public static RegistryObject<Block> BOOK_GUIDE;

    public static RegistryObject<Block> RAIL_WOOD;
    public static RegistryObject<Block> RAIL_NARROW = block("rail_narrow", ()->new Block(Properties.copy(Blocks.STONE).strength(15, 100)));
    public static RegistryObject<Block> RAIL_HIGHSPEED;
    public static RegistryObject<Block> RAIL_BOOSTER;

    public static RegistryObject<Block> RAIL_NARROW_STRAIGHT;
    public static RegistryObject<Block> RAIL_NARROW_CURVE;
    public static RegistryObject<Block> RAIL_LARGE_STRAIGHT;
    public static RegistryObject<Block> RAIL_LARGE_STRAIGHT_SHORT;
    public static RegistryObject<Block> RAIL_LARGE_CURVE;
    public static RegistryObject<Block> RAIL_LARGE_CURVE_7;
    public static RegistryObject<Block> RAIL_LARGE_CURVE_9;
    public static RegistryObject<Block> RAIL_LARGE_RAMP;
    public static RegistryObject<Block> RAIL_LARGE_BUFFER;
    public static RegistryObject<Block> RAIL_LARGE_SWITCH;
    public static RegistryObject<Block> RAIL_LARGE_SWITCH_FLIPPED;

    public static RegistryObject<Block> STATUE_ELB;
    public static RegistryObject<Block> STATUE_ELB_G;
    public static RegistryObject<Block> STATUE_ELB_W;
    public static RegistryObject<Block> STATUE_ELB_F;

    public static RegistryObject<Block> CHEATER_VIRUS;
    public static RegistryObject<Block> CHEATER_VIRUS_SEED;
    public static RegistryObject<Block> CRYSTAL_VIRUS;
    public static RegistryObject<Block> CRYSTAL_HARDENED;
    public static RegistryObject<Block> CRYSTAL_PULSAR;
    public static final RegistryObject<Block> TAINT = block("taint", () -> new Block(Properties.copy(Blocks.IRON_BLOCK)));
    public static RegistryObject<Block> RESIDUE;

    public static RegistryObject<Block> VENT_CHLORINE;
    public static RegistryObject<Block> VENT_CLOUD;
    public static RegistryObject<Block> VENT_PINK_CLOUD;
    public static RegistryObject<Block> VENT_CHLORINE_SEAL;
    public static RegistryObject<Block> CHLORINE_GAS;

    public static final RegistryObject<Block> GAS_RADON = add("gas_radon", ()->new BlockGasRadon(Properties.copy(Blocks.AIR)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.REVERSE_GEN, HBMKey.DROP_NONE);
    public static final RegistryObject<Block> GAS_RADON_DENSE = add("gas_radon_dense", ()->new BlockGasRadon(Properties.copy(Blocks.AIR)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.REVERSE_GEN, HBMKey.DROP_NONE);
    public static final RegistryObject<Block> GAS_RADON_TOMB = add("gas_radon_tomb", ()->new BlockGasRadon(Properties.copy(Blocks.AIR)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.REVERSE_GEN, HBMKey.DROP_NONE);
    public static RegistryObject<Block> GAS_MELTDOWN;
    public static RegistryObject<Block> GAS_MONOXIDE;
    public static RegistryObject<Block> GAS_ASBESTOS = block("gas_asbestos", ()->new Block(Properties.copy(Blocks.STONE).strength(2, 8)));
    public static RegistryObject<Block> GAS_COAL;
    public static RegistryObject<Block> GAS_FLAMMABLE;
    public static RegistryObject<Block> GAS_EXPLOSIVE;
    public static RegistryObject<Block> VACUUM;

    public static RegistryObject<Block> ABSORBER;
    public static RegistryObject<Block> ABSORBER_RED;
    public static RegistryObject<Block> ABSORBER_GREEN;
    public static RegistryObject<Block> ABSORBER_PINK;
    public static RegistryObject<Block> DECON;
    public static RegistryObject<Block> TRANSISSION_HATCH;
    // 流体方块，应当在流体注册中注册
    public static RegistryObject<Block> MUD_BLOCK;
    public static RegistryObject<Block> ACID_BLOCK;
    public static RegistryObject<Block> TOXIC_BLOCK;
    public static RegistryObject<Block> SCHRABIDIC_BLOCK;
    public static RegistryObject<Block> CORIUM_BLOCK;
    public static RegistryObject<Block> VOLCANIC_LAVA_BLOCK;
    public static RegistryObject<Block> RAD_LAVA_BLOCK;
    public static RegistryObject<Block> SULFURIC_ACID_BLOCK;

    public static RegistryObject<Block> CONCRETE_LIQUID;

    public static RegistryObject<Block> VOLCANO_CORE;
    public static RegistryObject<Block> VOLCANO_RAD_CORE;

    public static RegistryObject<Block> DUMMY_BLOCK_AMS_LIMITER;
    public static RegistryObject<Block> DUMMY_PORT_AMS_LIMITER;
    public static RegistryObject<Block> DUMMY_BLOCK_AMS_EMITTER;
    public static RegistryObject<Block> DUMMY_PORT_AMS_EMITTER;
    public static RegistryObject<Block> DUMMY_BLOCK_AMS_BASE;
    public static RegistryObject<Block> DUMMY_PORT_AMS_BASE;
    public static RegistryObject<Block> DUMMY_BLOCK_VAULT;
    public static RegistryObject<Block> DUMMY_BLOCK_BLAST;
    public static RegistryObject<Block> DUMMY_BLOCK_UF6;
    public static RegistryObject<Block> DUMMY_BLOCK_PUF6;
    public static RegistryObject<Block> DUMMY_PLATE_COMPACT_LAUNCHER;
    public static RegistryObject<Block> DUMMY_PORT_COMPACT_LAUNCHER;
    public static RegistryObject<Block> DUMMY_PLATE_LAUNCH_TABLE;
    public static RegistryObject<Block> DUMMY_PORT_LAUNCH_TABLE;
    public static RegistryObject<Block> DUMMY_PLATE_CARGO;

    public static RegistryObject<Block> NTM_DIRT;

    public static RegistryObject<Block> PINK_LOG;
    public static RegistryObject<Block> PINK_PLANKS;
    public static RegistryObject<Block> PINK_SLAB;
    public static RegistryObject<Block> PINK_DOUBLE_SLAB;
    public static RegistryObject<Block> PINK_STAIRS;

    public static RegistryObject<Block> BF_LOG;
    public static RegistryObject<Block> LATTICE_LOG;
    public static RegistryObject<Block> PRIMED_LOG;
    public static RegistryObject<Block> EU_LOG;

    public static RegistryObject<Block> FF;
    
    //机械
//    public static final RegistryObject<Block> CHEMPLANT = add("chemplant", ()->new BlockChemplant(BlockBehaviour.Properties.of().strength(5.0F).explosionResistance(30.0F)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "Chemical Plant", HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
//    public static final RegistryObject<Block> PLASTIC_BARREL = add("barrel_plastic", ()->new BlockFluidBarrel(BlockBehaviour.Properties.of().strength(2.0F).explosionResistance(5.0F).requiresCorrectToolForDrops().sound(SoundType.STONE), BlockFluidBarrel.BarrelProperties.of().capacity(12000)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "Safe Barrel™", HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
//    public static final RegistryObject<Block> CORRODED_BARREL = add("barrel_corroded", ()->new BlockFluidBarrel(BlockBehaviour.Properties.of().strength(2.0F).explosionResistance(5.0F).requiresCorrectToolForDrops().sound(SoundType.METAL),BlockFluidBarrel.BarrelProperties.of().capacity(6000).hotResist().corrosiveResistance().highCorroResist().leaky()), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "Corroded Barrel", HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
//    public static final RegistryObject<Block> IRON_BARREL = add("barrel_iron", ()->new BlockFluidBarrel(BlockBehaviour.Properties.of().strength(2.0F).explosionResistance(5.0F).requiresCorrectToolForDrops().sound(SoundType.METAL),BlockFluidBarrel.BarrelProperties.of().capacity(8000).hotResist()), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "Iron Barrel", HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
//    public static final RegistryObject<Block> STEEL_BARREL = add("barrel_steel", ()->new BlockFluidBarrel(BlockBehaviour.Properties.of().strength(2.0F).explosionResistance(5.0F).requiresCorrectToolForDrops().sound(SoundType.METAL),BlockFluidBarrel.BarrelProperties.of().capacity(16000).hotResist().corrosiveResistance()), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "Steel Barrel", HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
//    public static final RegistryObject<Block> TCALLOY_BARREL = add("barrel_tcalloy", ()->new BlockFluidBarrel(BlockBehaviour.Properties.of().strength(2.0F).explosionResistance(5.0F).requiresCorrectToolForDrops().sound(SoundType.METAL),BlockFluidBarrel.BarrelProperties.of().capacity(24000).hotResist().highCorroResist()), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "Technetium Steel Barrel", HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
//    public static final RegistryObject<Block> ANTIMATTER_BARREL = add("barrel_antimatter", ()->new BlockFluidBarrel(BlockBehaviour.Properties.of().strength(2.0F).explosionResistance(5.0F).requiresCorrectToolForDrops().sound(SoundType.METAL),BlockFluidBarrel.BarrelProperties.of().capacity(16000).hotResist().highCorroResist().antimatter()), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "Magnetic Antimatter Container", HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
//    public static final RegistryObject<Block> FLUID_PIPE = add("fluid_pipe", ()->new BlockFluidPipe(BlockBehaviour.Properties.of().strength(5.0F).explosionResistance(10.0F)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "Fluid Pipe", HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
//    public static final RegistryObject<Block> GEIGER_COUNTER = add("geiger", ()->new GeigerCounter(BlockBehaviour.Properties.of().strength(5.0F).explosionResistance(10.0F)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "Geiger Counter", HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
//    public static final RegistryObject<Block> LAUNCH_PAD = add("launch_pad", ()->new LaunchPad(BlockBehaviour.Properties.of().strength(5.0F).explosionResistance(10.0F)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "Launch Pad", HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
//    public static final RegistryObject<Block> machine_difurnace = registerMachineBlockWithItem("difurnace", ()->new BlockDifurnace(Properties.of().lightLevel(litEmission(13))));
//    public static final RegistryObject<Block> machine_converter_he_rf = new WrappedBlockRegistryBuilder("machine_converter_he_rf", ()->new com.hbm.block.machine.BlockConverterHeRf(Properties.of().strength(5.0F).explosionResistance(10.0F)))
//            .tab(ModTabs.MACHINE.getKey())
//            .mSmp(HBMKey.MODEL_CUBE_ALL, "machine_converter_he_rf")
//            .loot(HBMKey.DROP_SELF)
//            .loc(HBMKey.REVERSE_GEN)
//            .tile(ConverterHeRfEntityBE::new)
//            .build();
//    public static final RegistryObject<Block> machine_converter_rf_he = new WrappedBlockRegistryBuilder("machine_converter_rf_he", ()->new com.hbm.block.machine.BlockConverterRfHe(Properties.of().strength(5.0F).explosionResistance(10.0F)))
//            .tab(ModTabs.MACHINE.getKey())
//            .mSmp(HBMKey.MODEL_CUBE_ALL, "machine_converter_rf_he")
//            .loot(HBMKey.DROP_SELF)
//            .loc(HBMKey.REVERSE_GEN)
//            .tile(ConverterRfHeEntityBE::new)
//            .build();
//    public static final RegistryObject<Block> machine_electric_furnace = add("furnace_electric", ()->new BlockElectricFurnace(Properties.of().lightLevel(litEmission(13))), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> machine_boiler = add("boiler", ()->new BlockBoiler(Properties.of().lightLevel(litEmission(13))), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> machine_electric_boiler = add("boiler_electric", ()->new BlockElectricBoiler(Properties.of().lightLevel(litEmission(14))), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> machine_nuclear_boiler = add("boiler_nuclear", ()->new BlockNuclearBoiler(Properties.of().lightLevel(litEmission(15))), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
//    public static final RegistryObject<Block> machine_press = registerMachineBlockWithItem("machine_press", ()->new BlockPress(Properties.of()));
//    public static final RegistryObject<Block> PRESS_PREHEATER = add("press_preheater", ()->new BlockBase(Properties.of()), ModTabs.MACHINE.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.ORDERLY_GEN, HBMKey.DROP_SELF);
    public static final RegistryObject<Block> machine_shredder = registerMachineBlockWithItem("machine_shredder", ()->new BlockShredder(Properties.of()));
    public static final RegistryObject<Block> machine_wood_burner = registerMachineBlockWithItem("machine_wood_burner",
            () -> new WoodBurnerBlock(Properties.of().strength(3.0F).sound(SoundType.METAL)
                    .lightLevel(state -> state.getValue(WoodBurnerBlock.LIT) ? 13 : 0)));
    public static final RegistryObject<Block> MINER_LARGE = new WrappedBlockRegistryBuilder("miner_large", ()->new BlockMinerLarge(Properties.copy(Blocks.IRON_BLOCK).strength(5).explosionResistance(100)))
            .tab(ModTabs.MACHINE.getKey()).model(HBMKey.MODEL_STANDALONE).loc(HBMKey.REVERSE_GEN).loot(HBMKey.DROP_SELF).tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL).item(block -> new ItemPosModify(block, new Vec3i(0, 4, 0), new Item.Properties())).build();
//    public static final RegistryObject<Block> MACHINE_CENTRIFUGE = new WrappedBlockRegistryBuilder(MachineCentrifuge.id, () -> new MachineCentrifuge(Properties.copy(Blocks.IRON_BLOCK).strength(5.0f, 10.0f)))
//            .tab(ModTabs.MACHINE.getKey()).loc("Centrifuge")
//            .model((block, provider) -> provider.horizontalBlockWithItem(block, provider.genSimpleModel(block, HBM.rl("block/machines/centrifuge.obj"), HBM.rl("block/machine/centrifuge"), 3)))
//            .tile(TileMachineCentrifuge::new, true).renderer(RenderrerCentrifuge::new).menu(MenuCentrifuge::new).gui(GuiCentrifuge::new)
//            .tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL).build();
//    public static final RegistryObject<Block> MACHINE_CRYSTALLIZER = new WrappedBlockRegistryBuilder(MachineCrystallizer.id, () -> new MachineCrystallizer(Properties.copy(Blocks.IRON_BLOCK).strength(5.0f, 10.0f)))
//            .tab(ModTabs.MACHINE.getKey()).loc("Crystallizer")
//            .model((block, provider) -> provider.horizontalBlockWithItem(block, provider.genSimpleModel(block, HBM.rl("block/machines/acidizer.obj"), HBM.rl("block/machine/acidizer"), 6)))
//            .tile(TileCrystallizer::new, true).renderer(RenderCrystallizer::new).menu(MenuCrystallizer::new).gui(GuiCrystallizer::new)
//            .tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL).build();
    public static final RegistryObject<Block> MACHINE_ARC_FURNACE = new WrappedBlockRegistryBuilder(MachineArcFurnace.id, () -> new MachineArcFurnace(Properties.copy(Blocks.IRON_BLOCK).strength(5.0f, 10.0f)))
            .tab(ModTabs.MACHINE.getKey()).loc("Arc Furnace")
            .model((block, provider) -> provider.horizontalBlockWithItem(block, provider.genSimpleModel(block, HBM.rl("block/machines/arc_furnace.obj"), HBM.rl("block/machine/arc_furnace"), 5)))
            .tile(TileArcFurnace::new, true).renderer(RenderArcFurnace::new).menu(MenuArcFurnace::new).gui(GuiArcFurnace::new)
            .tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL).build();

    // Tokamak 聚变堆组件
    public static final RegistryObject<Block> tokamak_controller = registerMachineBlockWithItem("tokamak_controller", ()->new TokamakControllerBlock(Properties.of().strength(5.0F).lightLevel(state -> 8)));
    public static final RegistryObject<Block> tokamak_casing = registerMachineBlockWithItem("tokamak_casing", ()->new TokamakCasingBlock(Properties.of().strength(6.0F).explosionResistance(18.0F)));
    public static final RegistryObject<Block> tokamak_coil = registerMachineBlockWithItem("tokamak_coil", ()->new TokamakCoilBlock(Properties.of().strength(5.0F).lightLevel(state -> state.getValue(TokamakCoilBlock.STRENGTH) * 2)));
    public static final RegistryObject<Block> tokamak_heater = registerMachineBlockWithItem("tokamak_heater", ()->new TokamakHeaterBlock(Properties.of().strength(5.0F).lightLevel(state -> state.getValue(TokamakHeaterBlock.ACTIVE) ? 12 : 0)));
    public static final RegistryObject<Block> tokamak_injector = registerMachineBlockWithItem("tokamak_injector", ()->new TokamakInjectorBlock(Properties.of().strength(4.0F)));
    public static final RegistryObject<Block> tokamak_port = registerMachineBlockWithItem("tokamak_port", ()->new TokamakPortBlock(Properties.of().strength(4.0F)));
    public static final RegistryObject<Block> machine_icf = registerMachineBlockWithItem("machine_icf", () -> new BlockICFReactor(Properties.of().strength(5.0F).explosionResistance(40.0F)));
    public static final RegistryObject<Block> machine_icf_controller = registerMachineBlockWithItem("machine_icf_controller", () -> new BlockICFController(Properties.of().strength(4.0F).explosionResistance(20.0F)));
    public static final RegistryObject<Block> machine_icf_press = registerMachineBlockWithItem("machine_icf_press", () -> new BlockICFPress(Properties.of().strength(4.0F).explosionResistance(15.0F)));
    public static final RegistryObject<Block> machine_reactor_breeding = registerMachineBlockWithItem("machine_reactor_breeding", () -> new BlockBreederReactor(Properties.of().strength(5.0F).explosionResistance(20.0F)));
    public static final RegistryObject<Block> machine_research_reactor = registerMachineBlockWithItem("machine_research_reactor", () -> new BlockResearchReactor(Properties.of().strength(5.0F).explosionResistance(20.0F)));
    public static final RegistryObject<Block> pwr_controller = add("pwr_controller", ()->new BlockPWRController(Properties.of().strength(5.0F).explosionResistance(10.0F)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "PWR Controller", HBMKey.DROP_SELF);
    public static final RegistryObject<Block> pwr_casing = add("pwr_casing", () -> new BlockGenericPWR(Properties.of().strength(5.0F).explosionResistance(10.0F)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "PWR Casting", HBMKey.DROP_SELF);
    public static final RegistryObject<Block> pwr_port = add("pwr_port", () -> new BlockGenericPWR(Properties.of().strength(5.0F).explosionResistance(10.0F)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "PWR Port", HBMKey.DROP_SELF);
    public static final RegistryObject<Block> pwr_reflector = add("pwr_reflector", () -> new BlockGenericPWR(Properties.of().strength(5.0F).explosionResistance(10.0F)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "PWR Reflector", HBMKey.DROP_SELF);
    public static final RegistryObject<Block> pwr_fuel_block = add("pwr_fuel_block", () -> new BlockPWRPillar(Properties.of().strength(4.0F).explosionResistance(8.0F)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "PWR Fuel Block", HBMKey.DROP_SELF);
    public static final RegistryObject<Block> pwr_control = add("pwr_control", () -> new BlockPWRPillar(Properties.of().strength(4.0F).explosionResistance(8.0F)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "PWR Control", HBMKey.DROP_SELF);
    public static final RegistryObject<Block> pwr_channel = add("pwr_channel", () -> new BlockPWRPillar(Properties.of().strength(4.0F).explosionResistance(8.0F)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "PWR Channel", HBMKey.DROP_SELF);
    public static final RegistryObject<Block> pwr_heatex = add("pwr_heatex", () -> new BlockGenericPWR(Properties.of().strength(4.0F).explosionResistance(8.0F)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "PWR Heat Tex", HBMKey.DROP_SELF);
    public static final RegistryObject<Block> pwr_heatsink = add("pwr_heatsink", () -> new BlockGenericPWR(Properties.of().strength(4.0F).explosionResistance(8.0F)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "PWR Heat Sink", HBMKey.DROP_SELF);
    public static final RegistryObject<Block> pwr_neutron_source = add("pwr_neutron_source", () -> new BlockGenericPWR(Properties.of().strength(4.0F).explosionResistance(8.0F)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "PWR Neuron Source", HBMKey.DROP_SELF);
    public static final RegistryObject<Block> pwr_block = add("pwr_block", () -> new BlockPWR(Properties.of().strength(5.0F).explosionResistance(10.0F).noLootTable()), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "PWR Block", HBMKey.DROP_NONE);
    public static final RegistryObject<Block> machine_zirnox = add("machine_zirnox", () -> new BlockZirnoxReactor(Properties.of().strength(5.0F).explosionResistance(100.0F)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "Zirnox Reactor", HBMKey.DROP_SELF);
    public static final RegistryObject<Block> zirnox_destroyed = add("zirnox_destroyed", () -> new Block(Properties.of().strength(100.0F).explosionResistance(800.0F).noLootTable()), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "Zirnox Ruins", HBMKey.DROP_NONE);

    public static final RegistryObject<Block> machine_battery = registerMachineBattery("machine_battery",()->new BlockBattery(Properties.of(), BlockBattery.BatteryType.BASIC));
    public static final RegistryObject<Block> machine_lithium_battery = registerMachineBattery("battery_block_lithium",()->new BlockBattery(Properties.of(), BlockBattery.BatteryType.LITHIUM));
    public static final RegistryObject<Block> machine_schrabidium_battery = registerMachineBattery("battery_block_schrabidium",()->new BlockBattery(Properties.of(), BlockBattery.BatteryType.SCHRABIDIUM));
    public static final RegistryObject<Block> machine_dineutronium_battery = registerMachineBattery("battery_block_dineutronium",()->new BlockBattery(Properties.of(), BlockBattery.BatteryType.DINEUTRONIUM));
    public static final RegistryObject<Block> BATTERY = machine_battery;
    public static final RegistryObject<Block> BATTERY_LITHIUM = machine_lithium_battery;
//    public static final RegistryObject<Block> BATTERY_SCHRABIDIUM = machine_schrabidium_battery;
    public static final RegistryObject<Block> BATTERY_DINEUTRONIUM = machine_dineutronium_battery;
    public static final RegistryObject<Block> anvil_iron = registerMachineBlockWithItem("anvil_iron",()->new BlockAnvil(Properties.of()));
    public static final RegistryObject<Block> anvil_desh = registerMachineBlockWithItem("anvil_desh",()->new BlockAnvil(Properties.of()));
    public static final RegistryObject<Block> anvil_bismuth = registerMachineBlockWithItem("anvil_bismuth",()->new BlockAnvil(Properties.of()));
    public static final RegistryObject<Block> machine_cracking_tower = registerMachineBlockWithItem("machine_cracking_tower",()->new BlockCrackingTower(Properties.of()));
    public static final RegistryObject<Block> machine_condenser = registerMachineBlockWithItem("machine_condenser", () -> new CondenserBlock(Properties.of().strength(4.0F).sound(SoundType.METAL)));
    public static final RegistryObject<Block> machine_cooling_tower = registerMachineBlockWithItem("machine_cooling_tower", () -> new CoolingTowerBlock(Properties.of().strength(5.0F).sound(SoundType.METAL)));
    public static final RegistryObject<Block> machine_turbine_gas = machine("machine_turbine_gas", () -> new BlockTurbineGas(Properties.of().strength(6.0F).sound(SoundType.METAL)));
    public static final RegistryObject<Block> machine_assembler = registerMachineBlockWithItem("machine_assembler",()->new BlockAssembler(Properties.of()));
    public static final RegistryObject<Block> ASSEMBLER = machine_assembler;
    public static final RegistryObject<Block> machine_crucible = registerMachineBlockWithItem("machine_crucible",()->new BlockCrucible(Properties.of()));
    public static final RegistryObject<Block> machine_rbmk_base = registerMachineBlockWithItem("machine_rbmk_base", () -> new BlockRBMKBase(Properties.of().strength(6.0F).explosionResistance(30.0F)));
    public static final RegistryObject<Block> machine_rbmk_heater = registerMachineBlockWithItem("machine_rbmk_heater", () -> new BlockRBMKHeater(Properties.of().strength(4.0F).explosionResistance(12.0F).lightLevel(state -> state.getValue(BlockRBMKHeater.LIT) ? 8 : 0)));
    public static final RegistryObject<Block> machine_rbmk_fuel_channel = registerMachineBlockWithItem("machine_rbmk_fuel_channel", () -> new BlockRBMKFuelChannel(Properties.of().strength(4.0F).explosionResistance(12.0F)));
    public static final RegistryObject<Block> machine_rbmk_control_rod = registerMachineBlockWithItem("machine_rbmk_control_rod", () -> new BlockRBMKControlRod(Properties.of().strength(4.0F).explosionResistance(12.0F)));
    public static final RegistryObject<Block> machine_rbmk_control_auto = registerMachineBlockWithItem("machine_rbmk_control_auto", () -> new BlockRBMKControlRod(Properties.of().strength(4.0F).explosionResistance(12.0F)));
    public static final RegistryObject<Block> machine_rbmk_boiler = registerMachineBlockWithItem("machine_rbmk_boiler", () -> new BlockRBMKColumn(Properties.of().strength(4.0F).explosionResistance(12.0F), RBMKBoilerEntityBE::new));
    public static final RegistryObject<Block> machine_rbmk_moderator = registerMachineBlockWithItem("machine_rbmk_moderator", () -> new BlockRBMKColumn(Properties.of().strength(4.0F).explosionResistance(16.0F)));
    public static final RegistryObject<Block> machine_rbmk_absorber = registerMachineBlockWithItem("machine_rbmk_absorber", () -> new BlockRBMKColumn(Properties.of().strength(4.0F).explosionResistance(16.0F)));
    public static final RegistryObject<Block> machine_rbmk_outgasser = registerMachineBlockWithItem("machine_rbmk_outgasser", () -> new BlockRBMKColumn(Properties.of().strength(4.0F).explosionResistance(16.0F), RBMKOutgasserEntityBE::new));
    public static final RegistryObject<Block> machine_rbmk_storage = registerMachineBlockWithItem("machine_rbmk_storage", () -> new BlockRBMKColumn(Properties.of().strength(4.0F).explosionResistance(16.0F), RBMKStorageEntityBE::new));
    public static final RegistryObject<Block> machine_rbmk_cooler = registerMachineBlockWithItem("machine_rbmk_cooler", () -> new BlockRBMKColumn(Properties.of().strength(4.0F).explosionResistance(16.0F), RBMKCoolerEntityBE::new));
    public static final RegistryObject<Block> machine_rbmk_console = registerMachineBlockWithItem("machine_rbmk_console", () -> new BlockRBMKPeripheralLarge(Properties.of().strength(4.0F).explosionResistance(16.0F), RBMKPeripheralType.CONSOLE, Shapes.block(), true, 1));
    public static final RegistryObject<Block> machine_rbmk_display = registerMachineBlockWithItem("machine_rbmk_display", () -> new BlockRBMKDisplay(Properties.of().strength(2.0F).explosionResistance(6.0F).sound(SoundType.METAL).noOcclusion()));
    public static final RegistryObject<Block> machine_rbmk_graph = registerMachineBlockWithItem("machine_rbmk_graph", () -> new BlockRBMKGraph(Properties.of().strength(2.0F).explosionResistance(6.0F).sound(SoundType.METAL).noOcclusion()));
    public static final RegistryObject<Block> machine_rbmk_numitron = registerMachineBlockWithItem("machine_rbmk_numitron", () -> new BlockRBMKNumitron(Properties.of().strength(2.0F).explosionResistance(6.0F).sound(SoundType.METAL).noOcclusion()));
    public static final RegistryObject<Block> machine_rbmk_keypad = registerMachineBlockWithItem("machine_rbmk_keypad", () -> new BlockRBMKKeypad(Properties.of().strength(2.0F).explosionResistance(6.0F).sound(SoundType.METAL).noOcclusion()));
    public static final RegistryObject<Block> machine_rbmk_gauge = registerMachineBlockWithItem("machine_rbmk_gauge", () -> new BlockRBMKGauge(Properties.of().strength(2.0F).explosionResistance(6.0F).sound(SoundType.METAL).noOcclusion()));
    public static final RegistryObject<Block> radio_torch_controller = registerMachineBlockWithItem("radio_torch_controller", () -> new BlockRBMKRadioController(Properties.of().strength(2.0F).explosionResistance(6.0F).sound(SoundType.METAL).noOcclusion()));
    public static final RegistryObject<Block> machine_rbmk_element = registerMachineBlockWithItem("machine_rbmk_element", () -> new BlockRBMKColumn(Properties.of().strength(4.0F).explosionResistance(16.0F)));
    public static final RegistryObject<Block> machine_rbmk_reflector = registerMachineBlockWithItem("machine_rbmk_reflector", () -> new BlockRBMKColumn(Properties.of().strength(5.0F).explosionResistance(20.0F)));
    public static final RegistryObject<Block> machine_rbmk_debris = registerMachineBlockWithItem("machine_rbmk_debris", () -> new BlockRBMKPeripheral(Properties.of().strength(3.0F).explosionResistance(8.0F), RBMKPeripheralType.DEBRIS));
    public static final RegistryObject<Block> machine_rbmk_crane_console = registerMachineBlockWithItem("machine_rbmk_crane_console", () -> new BlockRBMKPeripheralLarge(Properties.of().strength(4.0F).explosionResistance(12.0F), RBMKPeripheralType.CRANE_CONSOLE, Shapes.block(), true, 1));
    public static final RegistryObject<Block> machine_rbmk_autoloader = registerMachineBlockWithItem("machine_rbmk_autoloader", () -> new BlockRBMKPeripheralLarge(Properties.of().strength(4.0F).explosionResistance(12.0F), RBMKPeripheralType.AUTOLOADER, Shapes.block(), true, 0));
    public static final RegistryObject<Block> rbmk_steam_inlet = registerMachineBlockWithItem("rbmk_steam_inlet", () -> new BlockRBMKSteamPort(Properties.of().strength(4.0F).explosionResistance(12.0F).sound(SoundType.METAL), true));
    public static final RegistryObject<Block> rbmk_steam_outlet = registerMachineBlockWithItem("rbmk_steam_outlet", () -> new BlockRBMKSteamPort(Properties.of().strength(4.0F).explosionResistance(12.0F).sound(SoundType.METAL), false));
    static {
        registerLegacyBlockItemAlias("rbmk_blank", machine_rbmk_base);
        registerLegacyBlockItemAlias("rbmk_boiler", machine_rbmk_boiler);
        registerLegacyBlockItemAlias("rbmk_console", machine_rbmk_console);
        registerLegacyBlockItemAlias("rbmk_control", machine_rbmk_control_rod);
        registerLegacyBlockItemAlias("rbmk_control_auto", machine_rbmk_control_auto);
        registerLegacyBlockItemAlias("rbmk_control_mod", machine_rbmk_control_rod); // best-effort until moderated control rod exists
        registerLegacyBlockItemAlias("rbmk_cooler", machine_rbmk_cooler);
        registerLegacyBlockItemAlias("rbmk_crane_console", machine_rbmk_crane_console);
        registerLegacyBlockItemAlias("rbmk_heater", machine_rbmk_heater);
        registerLegacyBlockItemAlias("rbmk_heatex", machine_rbmk_heater);
        registerLegacyBlockItemAlias("rbmk_loader", machine_rbmk_autoloader);
        registerLegacyBlockItemAlias("rbmk_moderator", machine_rbmk_moderator);
        registerLegacyBlockItemAlias("rbmk_absorber", machine_rbmk_absorber);
        registerLegacyBlockItemAlias("rbmk_outgasser", machine_rbmk_outgasser);
        registerLegacyBlockItemAlias("rbmk_storage", machine_rbmk_storage);
        registerLegacyBlockItemAlias("rbmk_reflector", machine_rbmk_reflector);
        registerLegacyBlockItemAlias("rbmk_element", machine_rbmk_element);
        registerLegacyBlockItemAlias("rbmk_display", machine_rbmk_display);
        registerLegacyBlockItemAlias("rbmk_display_blank", machine_rbmk_display);
        registerLegacyBlockItemAlias("rbmk_graph", machine_rbmk_graph);
        registerLegacyBlockItemAlias("rbmk_numitron", machine_rbmk_numitron);
        registerLegacyBlockItemAlias("rbmk_key_pad", machine_rbmk_keypad);
        registerLegacyBlockItemAlias("rbmk_gauge", machine_rbmk_gauge);
        registerLegacyBlockItemAlias("rbmk_rod", machine_rbmk_fuel_channel);
        registerLegacyBlockItemAlias("rbmk_rod_mod", machine_rbmk_fuel_channel);
        registerLegacyBlockItemAlias("rbmk_rod_reasim", machine_rbmk_fuel_channel);
        registerLegacyBlockItemAlias("rbmk_rod_reasim_mod", machine_rbmk_fuel_channel);
    }
//    public static final RegistryObject<Block> HEATER_FIREBOX = add("firebox", ()->new BlockFireBox(Properties.copy(Blocks.IRON_BLOCK)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.ORDERLY_GEN, HBMKey.DROP_SELF);
    //电力
//    public static final RegistryObject<Block> RED_CABLE = add("red_cable", ()->new BlockCable(Properties.copy(Blocks.STONE_BRICK_WALL)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.ORDERLY_GEN, HBMKey.DROP_SELF);
    public static final RegistryObject<Block> CONNECTOR = add("connector", ()->new BlockConnector(Properties.of()), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.ORDERLY_GEN, HBMKey.DROP_SELF);
    // 物流体系
    public static final RegistryObject<Block> conveyor = new WrappedBlockRegistryBuilder("conveyor", ()->new Conveyor(Properties.copy(Blocks.STONE))).tab(ModTabs.CONTROL.getKey()).model(HBMKey.MODEL_STANDALONE).loc(HBMKey.ORDERLY_GEN).loot(HBMKey.DROP_SELF).tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL).build();
    public static final RegistryObject<Block> CONVEYOR_INSERTER = new WrappedBlockRegistryBuilder("conveyor_inserter", ()->new ConveyorInserter(Properties.copy(Blocks.STONE))).tab(ModTabs.CONTROL.getKey()).model(HBMKey.MODEL_STANDALONE).loc(HBMKey.ORDERLY_GEN).loot(HBMKey.DROP_SELF).tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL).build();
    public static final RegistryObject<Block> CONVEYOR_EXTRACTOR = new WrappedBlockRegistryBuilder("conveyor_extractor", ()->new ConveyorExtractor(Properties.copy(Blocks.STONE))).tab(ModTabs.CONTROL.getKey()).model(HBMKey.MODEL_STANDALONE).loc(HBMKey.ORDERLY_GEN).loot(HBMKey.DROP_SELF).tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL).build();
    public static final RegistryObject<Block> CONVEYOR_ROUTER = new WrappedBlockRegistryBuilder("conveyor_router", ()->new ConveyorRouter(Properties.copy(Blocks.STONE))).tab(ModTabs.CONTROL.getKey()).model(HBMKey.MODEL_EXISTING).loc(HBMKey.ORDERLY_GEN).loot(HBMKey.DROP_SELF).tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL).build();
    public static final RegistryObject<Block> crate_iron =
            new WrappedBlockRegistryBuilder("crate_iron", () -> new IronCrateBlock(Properties.of().strength(3.0F).sound(SoundType.WOOD)))
                    .tab(ModTabs.MACHINE.getKey()).loc(HBMKey.REVERSE_GEN)
                    .item(block -> new IronCrateItem(block, new Item.Properties().stacksTo(1)))
                    .build();
    public static final RegistryObject<Block> crate_steel =
            new WrappedBlockRegistryBuilder("crate_steel", () -> new SteelCrateBlock(Properties.of().strength(4.0F).sound(SoundType.METAL)))
                    .tab(ModTabs.MACHINE.getKey()).loc(HBMKey.REVERSE_GEN)
                    .item(block -> new SteelCrateItem(block, new Item.Properties().stacksTo(1))).loc(HBMKey.REVERSE_GEN)
                    .build();
    //炸弹
//    public static final RegistryObject<Block> bomb_boy = registerBlockWithItem("bomb_boy",()->new NukeBoy(Properties.of(), ConfigBomb.boyRadius));
//    public static final RegistryObject<Block> bomb_fat_man = registerBlockWithItem("bomb_fat_man",()->new NukeFat(Properties.of(), ConfigBomb.manRadius));
//    public static final RegistryObject<Block> bomb_custom = registerBlockWithItem("bomb_custom",()->new NukeCustom(Properties.of(), ConfigBomb.manRadius));
//    public static final RegistryObject<Block> BOMB_FAT_MAN = bomb_fat_man;
    //发射台
    //装饰
    public static final RegistryObject<Block> TEST12 = registerBlockWithItem("test12",()->new BlockTest12(Properties.of()));
    // glyphid
    public static final RegistryObject<Block> GLYPHID_BLOCK = add("glyphid_block", ()->new GlyphidBlock(Properties.copy(Blocks.STONE).pushReaction(PushReaction.IGNORE).explosionResistance(0.5f)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.ORDERLY_GEN, HBMKey.DROP_NONE);
//    public static final RegistryObject<Block> GLYPHID_SPAWNER = add("glyphid_spawner", ()->new GlyphidSpawner(Properties.copy(Blocks.STONE).pushReaction(PushReaction.IGNORE).explosionResistance(0.5f)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.ORDERLY_GEN, HBMKey.DROP_NONE);
    // 陨石
//    public static final RegistryObject<Block> BLOCK_METEOR = add("block_meteor", () -> new Block(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), "Meteorite Block");
//    public static final RegistryObject<Block> BLOCK_METEOR_COBBLE = add("block_meteor_cobble", () -> new Block(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, "Meteorite Cobblestone", HBMKey.DROP_STANDALONE);
//    public static final RegistryObject<Block> BLOCK_METEOR_BROKEN = add("block_meteor_broken", () -> new Block(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, "Broken Meteorite Block", HBMKey.DROP_STANDALONE);
//    public static final RegistryObject<Block> BLOCK_METEOR_MOLTEN = add("block_meteor_molten", () -> new BlockMolten(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, "Hot Meteorite Cobblestone", HBMKey.DROP_NONE);
//    public static final RegistryObject<Block> BLOCK_METEOR_TREASURE = add("block_meteor_treasure", () -> new Block(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, "Broken Meteorite Block", HBMKey.DROP_STANDALONE);
//    public static final RegistryObject<Block> METEOR_POLISHED = add("meteor_polished", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), "Polished Meteor Block");
//    public static final RegistryObject<Block> METEOR_BRICK = block("meteor_brick", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)));
//    public static final RegistryObject<Block> METEOR_BRICK_MOSSY = add("meteor_brick_mossy", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), "Mossy Meteor Bricks");
//    public static final RegistryObject<Block> METEOR_BRICK_CRACKED = add("meteor_brick_cracked", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), "Cracked Meteor Bricks");
//    public static final RegistryObject<Block> METEOR_BRICK_CHISELED = add("meteor_brick_chiseled", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), "Chiseled Meteor Bricks");
//    public static final RegistryObject<Block> METEOR_PILLAR = add("meteor_pillar", () -> new RotatedPillarBlock(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_PILLAR, HBMKey.ORDERLY_GEN, HBMKey.DROP_SELF);
//    public static final RegistryObject<Block> ORE_METEOR_IRON = add("ore_meteor_iron", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF);
//    public static final RegistryObject<Block> ORE_METEOR_COPPER = add("ore_meteor_copper", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF);
//    public static final RegistryObject<Block> ORE_METEOR_ALUMINIUM = add("ore_meteor_aluminium", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF);
    public static final RegistryObject<Block> ORE_METEOR_RAREEARTH = add("ore_meteor_rareearth", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF);
    public static final RegistryObject<Block> ORE_METEOR_COBALT = add("ore_meteor_cobalt", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF);

//    public static final RegistryObject<Block> TAINT = block("taint", () -> new Block(Properties.copy(Blocks.IRON_BLOCK)));
//    public static final RegistryObject<Block> WASTE_GRASS = add("waste_grass", () -> new WasteEarth(Properties.copy(Blocks.DIRT)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_DIFURNACE, HBMKey.ORDERLY_GEN, HBMKey.DROP_STANDALONE);
    // casting
//    public static final RegistryObject<Block> FOUNDRY_MOLD = new WrappedBlockRegistryBuilder("foundry_mold", () -> new FoundryMold(Properties.copy(Blocks.STONE)))
//            .tab(ModTabs.MACHINE.getKey()).model(HBMKey.MODEL_EXISTING).loc(HBMKey.ORDERLY_GEN).loot(HBMKey.DROP_SELF).tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL).build();
    // 方块
//    public static final RegistryObject<Block> SELLAFIELD_SLAKED = block("sellafield_slaked", ()->new Block(BlockBehaviour.Properties.of().explosionResistance(5.0f)), BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
//    public static final RegistryObject<Block> WASTE_LEAVES = add("waste_leaves",()->new WasteLeaves(BlockBehaviour.Properties.copy(Blocks.ACACIA_LEAVES).noLootTable()), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_LEAVES, HBMKey.ORDERLY_GEN, HBMKey.DROP_NONE);
//    public static final RegistryObject<Block> WASTE_EARTH = add("waste_earth",()->new WasteEarth(BlockBehaviour.Properties.copy(Blocks.DIRT)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_BOTTOM_TOP, HBMKey.ORDERLY_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> URANIUM_ORE = add("ore_uranium",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.ORDERLY_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> DEEPSLATE_URANIUM_ORE = add("ore_uranium_deepslate",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> SCORCHED_URANIUM_ORE = add("ore_uranium_scorched",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> TITANIUM_ORE = add("ore_titanium",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> THORIUM_ORE = add("ore_thorium",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> NITER_ORE = add("ore_niter",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> TUNGSTEN_ORE = add("ore_tungsten",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> ALUMINIUM_ORE = add("ore_aluminium",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> FLUORITE_ORE = add("ore_fluorite",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> LEAD_ORE = add("ore_lead",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> BERYLLIUM_ORE = add("ore_beryllium",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.STONE)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> SA326_ORE = add("ore_schrabidium",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_DIAMOND_ORE), 0.1f), ModTabs.BLOCKS.getKey(),HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> ASBESTOS_ORE = add("ore_asbestos",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.STONE)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> BASALT_ASBESTOS_ORE = add("ore_basalt_asbestos",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.BASALT)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> OIL_ORE = add("ore_oil",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.GOLD_ORE)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> OIL_ORE_EMPTY = add("ore_oil_empty",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.GOLD_ORE)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> OIL_ORE_SAND = add("ore_oil_sand",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.SAND)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> RARE_EARTH_ORE = add("ore_rare",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.GOLD_ORE)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> DEEPSLATE_RARE_EARTH_ORE = add("ore_rare_deepslate",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_GOLD_ORE)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> LITHIUM_ORE = add("ore_lithium",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> COBALT_ORE = add("ore_cobalt",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> COLTAN_ORE = add("ore_coltan",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> GENISS_GAS_ORE = add("ore_gneiss_gas",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> SMOLDER_ORE_NETHER = add("ore_nether_smoldering",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.NETHER_QUARTZ_ORE)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> PLUTONIUM_ORE_NETHER = add("ore_nether_plutonium",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.NETHER_QUARTZ_ORE)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> FIRE_ORE_NETHER = add("ore_nether_fire",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.NETHER_BRICK_WALL)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> TIKITE_ORE_END = add("ore_tikite",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.END_STONE)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> SAND_BAG = add("sand_bag", ()->new BlockSandBag(BlockBehaviour.Properties.of().strength(1, 2.5f).lightLevel(litEmission(11))), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.ORDERLY_GEN, HBMKey.DROP_NONE);

//    public static final RegistryObject<Block> DIRT_DEAD = add("dirt_dead",()->new FallingBlock(BlockBehaviour.Properties.copy(Blocks.GRAVEL)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_SHOVEL);
//    public static final RegistryObject<Block> DIRT_OILY = add("dirt_oily",()->new FallingBlock(BlockBehaviour.Properties.copy(Blocks.GRAVEL)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_SHOVEL);
//    public static final RegistryObject<Block> SAND_DIRTY = add("sand_dirty",()->new FallingBlock(BlockBehaviour.Properties.copy(Blocks.SAND)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_SHOVEL);
//    public static final RegistryObject<Block> SAND_DIRTY_RED = add("sand_dirty_red",()->new FallingBlock(BlockBehaviour.Properties.copy(Blocks.SAND)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_SHOVEL);
//    public static final RegistryObject<Block> STONE_CRACKED = add("stone_cracked",()->new FallingBlock(BlockBehaviour.Properties.copy(Blocks.STONE)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE);
//    public static final WrappedRegistryBuilder.RegisterObjectCollection<Block, BlockEnums.EnumDeadPlantType> PLANT_DEAD = new WrappedRegistryBuilder.RegisterObjectCollection(BlockEnums.EnumDeadPlantType.class,
//            type -> add("plant_dead." + type.toString().toLowerCase(), ()->new DeadBushBlock(BlockBehaviour.Properties.copy(Blocks.DEAD_BUSH)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CROSS, HBMKey.REVERSE_GEN, HBMKey.DROP_NONE));
    // todo
    public static final RegistryObject<Block> ORE_BRINE = add("ore_brine",()->new Block(BlockBehaviour.Properties.copy(Blocks.STONE)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE);
    // todo
    public static final RegistryObject<Block> OIL_SPILL = add("oil_spill",()->new Block(BlockBehaviour.Properties.copy(Blocks.GRAVEL).sound(SoundType.SNOW).strength(0.1f)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF);
    // 气体
//    public static final RegistryObject<Block> GAS_RADON = add("gas_radon", ()->new BlockGasRadon(Properties.copy(Blocks.AIR)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.REVERSE_GEN, HBMKey.DROP_NONE);
//    public static final RegistryObject<Block> GAS_RADON_DENSE = add("gas_radon_dense", ()->new BlockGasRadon(Properties.copy(Blocks.AIR)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.REVERSE_GEN, HBMKey.DROP_NONE);
    public static final RegistryObject<Block> GAS_RADON_ASBESTOS = add("gas_radon_asbestos", ()->new BlockGasRadon(Properties.copy(Blocks.AIR)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.REVERSE_GEN, HBMKey.DROP_NONE);
//    public static final RegistryObject<Block> GAS_RADON_TOMB = add("gas_radon_tomb", ()->new BlockGasRadon(Properties.copy(Blocks.AIR)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.REVERSE_GEN, HBMKey.DROP_NONE);




    public static final RegistryObject<Block> BLOCK_COKE_COAL = block("block_coke_coal", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COAL_BLOCK)), BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.CARBON.storage_block());
    public static final RegistryObject<Block> BLOCK_COKE_LIGNITE = block("block_coke_lignite", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COAL_BLOCK)), BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.CARBON.storage_block());
    public static final RegistryObject<Block> BLOCK_COKE_PETROLEUM = block("block_coke_petroleum", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COAL_BLOCK)), BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, HBMMatters.CARBON.storage_block());

    // Chicago Pile components
    public static final RegistryObject<Block> chicago_graphite_block = new WrappedBlockRegistryBuilder("chicago_graphite_block",
            () -> new ChicagoGraphiteBlock(BlockBehaviour.Properties.of().strength(4.0F).explosionResistance(10.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()))
            .tab(ModTabs.MACHINE.getKey()).tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL)
            .build();
    public static final RegistryObject<Block> chicago_graphite_drilled = new WrappedBlockRegistryBuilder("chicago_graphite_drilled",
            () -> new ChicagoGraphiteDrilledBlock(BlockBehaviour.Properties.of().strength(4.0F).explosionResistance(10.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()))
            .tab(ModTabs.MACHINE.getKey()).tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL)
            .build();
    public static final RegistryObject<Block> chicago_graphite_rod = new WrappedBlockRegistryBuilder("chicago_graphite_rod",
            () -> new ChicagoGraphiteRodBlock(BlockBehaviour.Properties.of().strength(4.0F).explosionResistance(10.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()))
            .tab(ModTabs.MACHINE.getKey()).tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL)
            .build();
    public static final RegistryObject<Block> chicago_graphite_fuel = new WrappedBlockRegistryBuilder("chicago_graphite_fuel",
            () -> new ChicagoGraphiteFuelBlock(BlockBehaviour.Properties.of().strength(4.0F).explosionResistance(10.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()))
            .tab(ModTabs.MACHINE.getKey()).tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL)
            .build();
    public static final RegistryObject<Block> chicago_graphite_source = new WrappedBlockRegistryBuilder("chicago_graphite_source",
            () -> new ChicagoGraphiteSourceBlock(BlockBehaviour.Properties.of().strength(4.0F).explosionResistance(10.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()))
            .tab(ModTabs.MACHINE.getKey()).tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL)
            .build();
    public static final RegistryObject<Block> chicago_graphite_breeder = new WrappedBlockRegistryBuilder("chicago_graphite_breeder",
            () -> new ChicagoGraphiteBreederBlock(BlockBehaviour.Properties.of().strength(4.0F).explosionResistance(10.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()))
            .tab(ModTabs.MACHINE.getKey()).tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL)
            .build();
    public static final RegistryObject<Block> chicago_graphite_detector = new WrappedBlockRegistryBuilder("chicago_graphite_detector",
            () -> new ChicagoGraphiteDetectorBlock(BlockBehaviour.Properties.of().strength(4.0F).explosionResistance(10.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().lightLevel(state -> state.getValue(ChicagoPileStateProperties.TRIGGERED) ? 4 : 0)))
            .tab(ModTabs.MACHINE.getKey()).tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL)
            .build();
    public static final RegistryObject<Block> chicago_graphite_tritium = new WrappedBlockRegistryBuilder("chicago_graphite_tritium",
            () -> new ChicagoGraphiteTritiumBlock(BlockBehaviour.Properties.of().strength(4.0F).explosionResistance(10.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()))
            .tab(ModTabs.MACHINE.getKey()).tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL)
            .build();

//    public static final RegistryObject<Block> BLOCK_CAP = block("block_cap",
//            () -> new BlockCap(BlockBehaviour.Properties.copy(Blocks.STONE).strength(5.0F, 10.0F)),
//            BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final WrappedRegistryBuilder.RegisterObjectCollection<Block, EnumCapBlock> BLOCK_CAP = new WrappedRegistryBuilder.RegisterObjectCollection<>(EnumCapBlock.class,
                type -> add("block_cap_" + type.name().toLowerCase(), ()->new RotatedPillarBlock(Properties.copy(Blocks.STONE).strength(5.0F, 10.0F)),
                        ModTabs.BLOCKS.getKey(), HBMKey.MODEL_PILLAR, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF,
                        BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL));
    // 基岩矿
//    public static final RegistryObject<Block> STONE_POROUS = new WrappedBlockRegistryBuilder("stone_porous",()->new Block(BlockBehaviour.Properties.copy(Blocks.STONE))).model(HBMKey.MODEL_STANDALONE).loc(HBMKey.REVERSE_GEN).loot(HBMKey.DROP_STANDALONE).tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, BlockTags.STONE_ORE_REPLACEABLES).build();
//    public static final WrappedRegistryBuilder.RegisterObjectCollection<Block, BlockEnums.EnumStoneType> STONE_RESOURCE = new WrappedRegistryBuilder.RegisterObjectCollection<>(BlockEnums.EnumStoneType.class,
//            type -> add("stone_resource." + type.name().toLowerCase(), ()->new Block(Properties.of().strength(5, 10)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, type == BlockEnums.EnumStoneType.MALACHITE ? HBMKey.DROP_STANDALONE : HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL));
//    public static final WrappedRegistryBuilder.RegisterObjectCollection<Block, BlockEnums.EnumStalagmiteType> STALAGMITE = new WrappedRegistryBuilder.RegisterObjectCollection<>(BlockEnums.EnumStalagmiteType.class,
//            type -> new WrappedBlockRegistryBuilder("stalagmite." + type.name().toLowerCase(), ()->new BlockStalagmite(Properties.of().strength(0.5f, 2))).model(HBMKey.MODEL_STANDALONE).loot(HBMKey.DROP_STANDALONE).loc(HBMKey.REVERSE_GEN).build());
//    public static final WrappedRegistryBuilder.RegisterObjectCollection<Block, BlockEnums.EnumStalagmiteType> STALACTITE = new WrappedRegistryBuilder.RegisterObjectCollection<>(BlockEnums.EnumStalagmiteType.class,
//            type -> new WrappedBlockRegistryBuilder("stalactite." + type.name().toLowerCase(), ()->new BlockStalagmite(Properties.of().strength(0.5f, 2))).model(HBMKey.MODEL_STANDALONE).loot(HBMKey.DROP_STANDALONE).loc(HBMKey.REVERSE_GEN).build());
    //    stalagmite = new BlockStalagmite().setBlockName("stalagmite").setCreativeTab(MainRegistry.blockTab).setHardness(0.5F).setResistance(2.0F);
//    stalactite = new BlockStalagmite().setBlockName("stalactite").setCreativeTab(MainRegistry.blockTab).setHardness(0.5F).setResistance(2.0F);
//    stone_biome = new BlockBiomeStone().setBlockName("stone_biome").setCreativeTab(MainRegistry.blockTab).setHardness(5.0F).setResistance(10.0F);

//    public static final RegistryObject<Block> BEDROCK_ORE = add("ore_bedrock",()->new BedRockOre(BlockBehaviour.Properties.copy(Blocks.BEDROCK)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.REVERSE_GEN, HBMKey.DROP_NONE);
    public static final RegistryObject<Block> DEPTH_STONE = add("depth_stone",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.REINFORCED_DEEPSLATE)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.ORDERLY_GEN, HBMKey.DROP_SELF);
//    public static final RegistryObject<Block> DEPTH_BRICK = new WrappedBlockRegistryBuilder("depth_brick",()->new BlockOre(BlockBehaviour.Properties.of().destroyTime(-1.0F).explosionResistance(10))).tab(ModTabs.BLOCKS.getKey()).loc("Depth Bricks").build();
//    public static final RegistryObject<Block> DEPTH_TILES = new WrappedBlockRegistryBuilder("depth_tiles",()->new BlockOre(BlockBehaviour.Properties.of().destroyTime(-1.0F).explosionResistance(10))).tab(ModTabs.BLOCKS.getKey()).loc("Depth Tiles").build();
//    public static final RegistryObject<Block> DEPTH_NETHER_BRICK = new WrappedBlockRegistryBuilder("depth_nether_brick",()->new BlockOre(BlockBehaviour.Properties.of().destroyTime(-1.0F).explosionResistance(10))).tab(ModTabs.BLOCKS.getKey()).loc("Nether Depth Bricks").build();
//    public static final RegistryObject<Block> DEPTH_NETHER_TILES = new WrappedBlockRegistryBuilder("depth_nether_tiles",()->new BlockOre(BlockBehaviour.Properties.of().destroyTime(-1.0F).explosionResistance(10))).tab(ModTabs.BLOCKS.getKey()).loc("Nether Depth Tiles").build();
//    public static final RegistryObject<Block> DEPTH_DNT = new WrappedBlockRegistryBuilder("depth_dnt",()->new BlockOre(BlockBehaviour.Properties.of().destroyTime(-1.0F).explosionResistance(60000))).tab(ModTabs.BLOCKS.getKey()).loc("DNT-Reinforced Depth Bricks").tags(HBMMatters.DNT.storage_block()).build();
    public static final WrappedRegistryBuilder.RegisterObjectCollection<Block, BedrockOreDefinition> BEDROCK_ORE = new WrappedRegistryBuilder.RegisterObjectCollection<>(BedrockOreDefinition.DEFINITIONS.values(),
            definition -> new WrappedBlockRegistryBuilder("ore_bedrock_" + definition.id.toLowerCase(), ()->new BedRockOre(definition, Properties.copy(Blocks.BEDROCK)))
                    .model(HBMKey.MODEL_STANDALONE).color((state, level, pos, tintIndex) -> tintIndex != 0 ? 0xFFFFFFFF : definition.color).loc(HBMKey.REVERSE_GEN).loot(HBMKey.DROP_NONE).build());
//            definition -> add("ore_bedrock_" + definition.id.toLowerCase(), ()->new BedRockOre(definition, Properties.copy(Blocks.BEDROCK)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.REVERSE_GEN, HBMKey.DROP_NONE));

    /**
     * 航天版方块
     * */
    public static final RegistryObject<Block> moon_rock = block("moon_rock", ()->new Block(Properties.of().sound(SoundType.STONE).strength(1.5f, 10f)));
    public static final RegistryObject<Block> moon_turf = block("moon_turf", ()->new FallingBlock(Properties.of().sound(SoundType.SAND).strength(0.5f)));
    public static final RegistryObject<Block> SPACE_STATION_BASE = new WrappedBlockRegistryBuilder("space_station_base", ()->new BlockSpaceStation(Properties.of().sound(SoundType.SAND).strength(0.5f)))
            .tab(ModTabs.MACHINE.getKey()).model(HBMKey.MODEL_STANDALONE).item(block -> new BlockItemDummyable(block, new Item.Properties()))
            .build();
    public static final RegistryObject<Block> DRES_ROCK = add("dres_rock",()->new Block(BlockBehaviour.Properties.copy(Blocks.STONE)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE);

//            add("space_station_base", ()->new BlockSpaceStation(Properties.of().sound(SoundType.SAND).strength(0.5f)),
//                    ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.ORDERLY_GEN, HBMKey.DROP_SELF);
    /**
     * 调试方块
     * */
    public static final RegistryObject<Block> DEBUG_BLOCK = block("debug_block", ()->new BlockDebug(BlockBehaviour.Properties.copy(Blocks.STONE)));


    /**
     * ===========================以下是注册函数部分=============================
     *
     * */
    public static ToIntFunction<BlockState> litEmission(int value){
        return state -> state.getValue(BlockStateProperties.LIT)?value:0;
    }

    public static RegistryObject<Block> registerBattery(final String name, final Supplier<? extends Block> blocksup){
        RegistryObject<Block> block = BLOCKS.register(name,blocksup);
        ModItems.ITEMS.register(name,()->new BlockItemBattery(block.get(),new Item.Properties()));
        return block;
    }
    private static RegistryObject<Block> registerMachineBattery(final String name, final Supplier<? extends Block> blocksup){
        RegistryObject<Block> block = registerBattery(name, blocksup);
        trackLegacyMachineTagBlock(block);
        return block;
    }
    public static RegistryObject<Block> registerBlockWithItem(final String name, final Supplier<? extends Block> blocksup){
        RegistryObject<Block> block = BLOCKS.register(name,blocksup);
        ModItems.ITEMS.register(name,()->new BlockItem(block.get(),new Item.Properties()));
        return block;
    }
    private static void registerLegacyBlockItemAlias(final String alias, final RegistryObject<Block> block){
        ModItems.ITEMS.register(alias, () -> new BlockItem(block.get(), new Item.Properties()));
    }
    private static RegistryObject<Block> registerMachineBlockWithItem(final String name, final Supplier<? extends Block> blocksup){
        RegistryObject<Block> block = registerBlockWithItem(name, blocksup);
        trackLegacyMachineTagBlock(block);
        return block;
    }

    static boolean isMachineTab(ResourceKey<CreativeModeTab> tabKey) {
        return ModTabs.MACHINE.getKey().equals(tabKey);
    }

    public static void trackLegacyMachineTagBlock(RegistryObject<Block> block) {
        if (!legacyMachineTagBlocks.contains(block)) {
            legacyMachineTagBlocks.add(block);
        }
    }

    public static void machineTagSupport(BlockTagsGen provider){
        for (RegistryObject<Block> block : legacyMachineTagBlocks) {
            provider.tag(ModTags.Blocks.MACHINE).add(block.get());
        }
    }
    public static void register(IEventBus modEventBus){
        BLOCKS.register(modEventBus);
    }

    @SafeVarargs
    protected static RegistryObject<Block> block(final String name, final Supplier<? extends Block> sup, TagKey<Block> ... keys){
        return ModBlocks.add(name, sup, ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.ORDERLY_GEN, HBMKey.DROP_SELF, keys);
    }

    @SafeVarargs
    protected static RegistryObject<Block> machine(final String name, final Supplier<? extends Block> sup, TagKey<Block> ... keys){
        return ModBlocks.add(name, sup, ModTabs.MACHINE.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.ORDERLY_GEN, HBMKey.DROP_SELF, keys);
    }

    protected static RegistryObject<Block> add(final String name, final Supplier<? extends Block> sup, ResourceKey<CreativeModeTab> tabKey, String genNameWay){
        return add(name, sup, tabKey, HBMKey.MODEL_CUBE_ALL, genNameWay, HBMKey.DROP_SELF);
    }
    @SafeVarargs
    protected static RegistryObject<Block> add(final String name, final Supplier<? extends Block> sup, ResourceKey<CreativeModeTab> tabKey, String genModelWay, String genNameWay, String lootWay, TagKey<Block> ... keys){
        return new WrappedBlockRegistryBuilder(name, sup).tab(tabKey).model(genModelWay).loc(genNameWay).loot(lootWay).tags(keys).build();
    }

    /**
     * 其他属性注册的钩子
     * */
    public static void creativeTab(BuildCreativeModeTabContentsEvent event){
        for (WrappedBlockRegistryBuilder blockRegistry : blockList) {
            blockRegistry.creativeTabSupport(event);
        }
    }

    public static void genModel(BlockStateGen provider){
        for (WrappedBlockRegistryBuilder blockRegistry : blockList) {
            blockRegistry.modelSupport(provider);
        }
        provider.addIntStateCubeAllBlock(ModBlocks.GLYPHID_BLOCK.get(), HBMBlockProperties.VARIANT3);
        provider.addIntStateCubeAllBlock(ModBlocks.GLYPHID_SPAWNER.get(), HBMBlockProperties.VARIANT3);
    }
    public static void languageSupport(LanguageProvider provider){
        for (WrappedBlockRegistryBuilder blockRegistry : blockList) {
            blockRegistry.languageSupport(provider);
        }
    }

    public static void lootSupport(BlockLootGen provider){
        for (WrappedBlockRegistryBuilder blockRegistry : blockList) {
            blockRegistry.lootSupport(provider);
        }
    }

    public static void tagSupport(BlockTagsGen provider){
        for (WrappedBlockRegistryBuilder wrappedBlockRegistry : blockList) {
            wrappedBlockRegistry.tagSupport(provider);
        }
    }
    public static void blockColorSupport(RegisterColorHandlersEvent.Block event){
        for (WrappedBlockRegistryBuilder wrappedBlockRegistry : blockList) {
            wrappedBlockRegistry.blockColorSupport(event);
        }
    }

    public static void customJsonSupport(HBMJsonProvider provider){
        for (WrappedBlockRegistryBuilder wrappedBlockRegistry : blockList) {
            wrappedBlockRegistry.customJsonSupport(provider);
        }
    }

    public static void tileSupport(){
        for (WrappedBlockRegistryBuilder wrappedBlockRegistry : blockList) {
            wrappedBlockRegistry.tileSupport();
        }
    }

    public static void menuSupport(){
        for (WrappedBlockRegistryBuilder wrappedBlockRegistry : blockList) {
            wrappedBlockRegistry.menuSupport();
        }
    }

    public static void guiSupport(){
        for (WrappedBlockRegistryBuilder wrappedBlockRegistry : blockList) {
            wrappedBlockRegistry.guiSupport();
        }
    }

    public static void rendererSupport(){
        for (WrappedBlockRegistryBuilder wrappedBlockRegistry : blockList) {
            wrappedBlockRegistry.rendererSupport();
        }
    }
}