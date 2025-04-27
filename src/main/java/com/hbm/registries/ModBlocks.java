package com.hbm.registries;

import com.hbm.block.decoriate.BlockTest12;
import com.hbm.block.env.BedRockOre;
import com.hbm.block.env.WasteEarth;
import com.hbm.block.env.WasteLeaves;
import com.hbm.block.machine.*;
import com.hbm.block.network.BlockConveyor;
import com.hbm.block.weapon.*;
import com.hbm.fluid.ModFluids;
import com.hbm.item.tool.BatteryBlockItem;
import com.hbm.block.base.DummibleBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import static com.hbm.HBM.MODID;

public class ModBlocks {
    //方块注册表
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    //机械
    public static final RegistryObject<Block> machine_difurnace = registerBlockWithItem("machine_difurnace", ()->new BlockDifurnace(BlockBehaviour.Properties.of().lightLevel(litEmission(13))));
    public static final RegistryObject<Block> machine_electric_furnace = registerBlockWithItem("machine_electric_furnace", ()->new BlockElectricFurnace(BlockBehaviour.Properties.of().lightLevel(litEmission(13))));
    public static final RegistryObject<Block> machine_boiler = registerBlockWithItem("machine_boiler", ()->new BlockElectricFurnace(BlockBehaviour.Properties.of().lightLevel(litEmission(13))));
    public static final RegistryObject<Block> machine_electric_boiler = registerBlockWithItem("machine_electric_boiler", ()->new BlockElectricFurnace(BlockBehaviour.Properties.of().lightLevel(litEmission(14))));
    public static final RegistryObject<Block> machine_nuclear_boiler = registerBlockWithItem("machine_nuclear_boiler", ()->new BlockElectricFurnace(BlockBehaviour.Properties.of().lightLevel(litEmission(15))));
    public static final RegistryObject<Block> machine_press = registerBlockWithItem("machine_press", ()->new BlockPress(BlockBehaviour.Properties.of()));
    public static final RegistryObject<Block> machine_battery = registerBattery("machine_battery",()->new BlockBattery(BlockBehaviour.Properties.of(), BlockBattery.BatteryType.BASIC));
    public static final RegistryObject<Block> machine_lithium_battery = registerBattery("machine_lithium_battery",()->new BlockBattery(BlockBehaviour.Properties.of(), BlockBattery.BatteryType.LITHIUM));
    public static final RegistryObject<Block> machine_schrabidium_battery = registerBattery("machine_schrabidium_battery",()->new BlockBattery(BlockBehaviour.Properties.of(), BlockBattery.BatteryType.SCHRABIDIUM));
    public static final RegistryObject<Block> machine_dineutronium_battery = registerBattery("machine_dineutronium_battery",()->new BlockBattery(BlockBehaviour.Properties.of(), BlockBattery.BatteryType.DINEUTRONIUM));
    public static final RegistryObject<Block> anvil_iron = registerBlockWithItem("anvil_iron",()->new BlockAnvil(BlockBehaviour.Properties.of()));
    public static final RegistryObject<Block> anvil_desh = registerBlockWithItem("anvil_desh",()->new BlockAnvil(BlockBehaviour.Properties.of()));
    public static final RegistryObject<Block> anvil_bismuth = registerBlockWithItem("anvil_bismuth",()->new BlockAnvil(BlockBehaviour.Properties.of()));
    public static final RegistryObject<Block> machine_cracking_tower = registerBlockWithItem("machine_cracking_tower",()->new BlockCrackingTower(BlockBehaviour.Properties.of()));
    public static final RegistryObject<Block> machine_assembler = registerBlockWithItem("machine_assembler",()->new BlockAssembler(BlockBehaviour.Properties.of()));
    public static final RegistryObject<Block> machine_crucible = registerBlockWithItem("machine_crucible",()->new BlockCrucible(BlockBehaviour.Properties.of()));
    //模型部分（仅仅用于加载模型渲染，而不会在游戏单独出现，名称以part开头）
    public static final RegistryObject<Block> part_press_head = BLOCKS.register("part_press_head",()->new Block(BlockBehaviour.Properties.of().noLootTable()));
    //电力
    public static final RegistryObject<Block> RED_CABLE = registerBlockWithItem("red_cable",()->new BlockCable(BlockBehaviour.Properties.copy(Blocks.STONE_BRICK_WALL)));
    //输送带
    public static final RegistryObject<Block> conveyor = registerBlockWithItem("conveyor",()->new BlockConveyor(BlockBehaviour.Properties.of()));
    //炸弹
    public static final RegistryObject<Block> bomb_boy = registerBlockWithItem("bomb_boy",()->new NukeBoy(BlockBehaviour.Properties.of(),120));
    public static final RegistryObject<Block> bomb_fat_man = BLOCKS.register("bomb_fat_man",()->new NukeFat(BlockBehaviour.Properties.of(),200));
    public static final RegistryObject<Block> bomb_custom = registerBlockWithItem("bomb_custom",()->new NukeCustom(BlockBehaviour.Properties.of(),350));
    //发射台
    public static final RegistryObject<Block> LAUNCHPAD_BASIC = registerBlockWithItem("launchpad_basic",()->new LaunchPadBasic(BlockBehaviour.Properties.of()));
    public static final RegistryObject<Block> LAUNCHPAD_COMPACT = registerBlockWithItem("launchpad_compact",()->new LaunchPadCompact(BlockBehaviour.Properties.of()));
    public static final RegistryObject<Block> LAUNCHPAD_LARGE = registerBlockWithItem("launchpad_large",()->new LaunchPadLarge(BlockBehaviour.Properties.of()));
    public static final RegistryObject<Block> LAUNCHPAD_SOYUZ = registerBlockWithItem("launchpad_soyuz",()->new LaunchPadSoyuz(BlockBehaviour.Properties.of()));

    //流体
    public static final RegistryObject<LiquidBlock> irradiated_water = BLOCKS.register("irradiated_water", ()->new LiquidBlock(ModFluids.IRRADIATED_WATER_SOURCE_BLOCK,BlockBehaviour.Properties.copy(Blocks.WATER).noLootTable()));
    public static final RegistryObject<LiquidBlock> irradiated_polluted = BLOCKS.register("irradiated_polluted", ()->new LiquidBlock(ModFluids.IRRADIATED_POLLUTED_SOURCE_BLOCK,BlockBehaviour.Properties.copy(Blocks.WATER).noLootTable()));
    public static final RegistryObject<LiquidBlock> sulfuric_acid = BLOCKS.register("sulfuric_acid", ()->new LiquidBlock(ModFluids.SULFURIC_ACID_SOURCE_BLOCK,BlockBehaviour.Properties.copy(Blocks.WATER).noLootTable()));

    //自然物
    //ores
    public static final RegistryObject<Block> WAST_LEAVES = registerBlockWithItem("wast_leaves",()->new WasteLeaves(BlockBehaviour.Properties.copy(Blocks.ACACIA_LEAVES).noLootTable()));
    public static final RegistryObject<Block> WAST_EARTH = registerBlockWithItem("wast_earth",()->new WasteEarth(BlockBehaviour.Properties.copy(Blocks.DIRT)));
    public static final RegistryObject<Block> URANIUM_ORE = registerBlockWithItem("uranium_ore",()->new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)));
    public static final RegistryObject<Block> DEEPSLATE_URANIUM_ORE = registerBlockWithItem("deepslate_uranium_ore",()->new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE)));
    public static final RegistryObject<Block> SCORCHED_URANIUM_ORE = registerBlockWithItem("scorched_uranium_ore",()->new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)));
    public static final RegistryObject<Block> TITANIUM_ORE = registerBlockWithItem("titanium_ore",()->new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)));
    public static final RegistryObject<Block> THORIUM_ORE = registerBlockWithItem("thorium_ore",()->new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)));
    public static final RegistryObject<Block> NITER_ORE = registerBlockWithItem("niter_ore",()->new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)));
    public static final RegistryObject<Block> TUNGSTEN_ORE = registerBlockWithItem("tungsten_ore",()->new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)));
    public static final RegistryObject<Block> ALUMINIUM_ORE = registerBlockWithItem("aluminium_ore",()->new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)));
    public static final RegistryObject<Block> FLUORITE_ORE = registerBlockWithItem("fluorite_ore",()->new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)));
    public static final RegistryObject<Block> LEAD_ORE = registerBlockWithItem("lead_ore",()->new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)));
    public static final RegistryObject<Block> BERYLLIUM_ORE = registerBlockWithItem("beryllium_ore",()->new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> SA326_ORE = registerBlockWithItem("sa326_ore",()->new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_DIAMOND_ORE)));
    public static final RegistryObject<Block> ASBESTOS_BLOCK = registerBlockWithItem("asbestos_block",()->new Block(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL)));
    public static final RegistryObject<Block> ASBESTOS_ORE = registerBlockWithItem("asbestos_ore",()->new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> BASALT_ASBESTOS_ORE = registerBlockWithItem("basalt_asbestos_ore",()->new Block(BlockBehaviour.Properties.copy(Blocks.BASALT)));
    //oil
    public static final RegistryObject<Block> OIL_ORE = registerBlockWithItem("oil_ore",()->new Block(BlockBehaviour.Properties.copy(Blocks.GOLD_ORE)));
    public static final RegistryObject<Block> OIL_ORE_EMPTY = registerBlockWithItem("oil_ore_empty",()->new Block(BlockBehaviour.Properties.copy(Blocks.GOLD_ORE)));
    public static final RegistryObject<Block> OIL_ORE_SAND = registerBlockWithItem("oil_ore_sand",()->new Block(BlockBehaviour.Properties.copy(Blocks.SAND)));
    //rare ore
    public static final RegistryObject<Block> RARE_EARTH_ORE = registerBlockWithItem("rare_earth",()->new Block(BlockBehaviour.Properties.copy(Blocks.GOLD_ORE)));
    public static final RegistryObject<Block> DEEPSLATE_RARE_EARTH_ORE = registerBlockWithItem("deepslate_rare_ore",()->new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_GOLD_ORE)));
    public static final RegistryObject<Block> LITHIUM_ORE = registerBlockWithItem("lithium_ore",()->new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)));
    public static final RegistryObject<Block> COBALT_ORE = registerBlockWithItem("cobalt_ore",()->new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)));
    public static final RegistryObject<Block> COLTAN_ORE = registerBlockWithItem("coltan_ore",()->new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)));
    //geniss ore
    public static final RegistryObject<Block> GENISS_GAS_ORE = registerBlockWithItem("geniss_gas_ore",()->new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)));
    //nether ore
    public static final RegistryObject<Block> SMOLDER_ORE_NETHER = registerBlockWithItem("smolder_ore_nether",()->new Block(BlockBehaviour.Properties.copy(Blocks.NETHER_QUARTZ_ORE)));
    public static final RegistryObject<Block> PLUTONIUM_ORE_NETHER = registerBlockWithItem("plutonium_ore_nether",()->new Block(BlockBehaviour.Properties.copy(Blocks.NETHER_QUARTZ_ORE)));
    public static final RegistryObject<Block> FIRE_ORE_NETHER = registerBlockWithItem("fire_ore_nether",()->new Block(BlockBehaviour.Properties.copy(Blocks.NETHER_BRICK_WALL)));
    //end ore
    public static final RegistryObject<Block> TIKITE_ORE_END = registerBlockWithItem("tikite_ore_end",()->new Block(BlockBehaviour.Properties.copy(Blocks.END_STONE)));
    //bedrock ore
    public static final RegistryObject<Block> BEDROCK_ORE = registerBlockWithItem("bedrock_ore",()->new BedRockOre(BlockBehaviour.Properties.copy(Blocks.BEDROCK)));
    public static final RegistryObject<Block> DEPTH_STONE = registerBlockWithItem("depth_stone",()->new Block(BlockBehaviour.Properties.copy(Blocks.REINFORCED_DEEPSLATE)));

    //逻辑物
    public static final RegistryObject<Block> DUMMIBLE = registerBlockWithItem("dummible",()->new DummibleBlock(BlockBehaviour.Properties.copy(Blocks.STONE).noLootTable()));
    //装饰
    public static final RegistryObject<Block> TEST12 = registerBlockWithItem("test12",()->new BlockTest12(BlockBehaviour.Properties.of()));

    public static ToIntFunction<BlockState> litEmission(int value){
        return state -> {
            return state.getValue(BlockStateProperties.LIT)?value:0;
        };
    }

    public static void registerItem(){
        //主要用于注册一些特殊模型的物品
        ModItems.ITEMS.register("bomb_fat_man",()->new NukeFat.NukeItem(bomb_fat_man.get(),new Item.Properties()));
    }
    public static RegistryObject<Block> registerBattery(final String name, final Supplier<? extends Block> blocksup){
        RegistryObject<Block> block = BLOCKS.register(name,blocksup);
        ModItems.ITEMS.register(name,()->new BatteryBlockItem(block.get(),new Item.Properties()));
        return block;
    }
    public static RegistryObject<Block> registerBlockWithItem(final String name, final Supplier<? extends Block> blocksup){
        RegistryObject<Block> block = BLOCKS.register(name,blocksup);
        ModItems.ITEMS.register(name,()->new BlockItem(block.get(),new Item.Properties()));
        return block;
    }
    public static void register(IEventBus modEventBus){
        registerItem();
        BLOCKS.register(modEventBus);
    }
}
