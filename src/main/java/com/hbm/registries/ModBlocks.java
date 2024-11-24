package com.hbm.registries;

import com.hbm.block.env.BedRockOre;
import com.hbm.block.env.WasteEarth;
import com.hbm.block.env.WasteLeaves;
import com.hbm.block.machine.*;
import com.hbm.block.network.BlockConveyor;
import com.hbm.block.weapon.NukeCustom;
import com.hbm.block.weapon.NukeFat;
import com.hbm.fluid.ModFluids;
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

import static com.hbm.main.HBMxx.MODID;

public class ModBlocks {
    //方块注册表
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

    public static final RegistryObject<Block> machine_difurnace = registerBlockWithItem("machine_difurnace",
            ()->new BlockDifurnace(BlockBehaviour.Properties.of().lightLevel(litEmission(13))));
    public static final RegistryObject<Block> machine_electric_furnace = registerBlockWithItem("machine_electric_furnace",
            ()->new BlockElectricFurnace(BlockBehaviour.Properties.of().lightLevel(litEmission(13))));
    public static final RegistryObject<Block> machine_boiler = registerBlockWithItem("machine_boiler",
            ()->new BlockElectricFurnace(BlockBehaviour.Properties.of().lightLevel(litEmission(13))));
    public static final RegistryObject<Block> machine_electric_boiler = registerBlockWithItem("machine_electric_boiler",
            ()->new BlockElectricFurnace(BlockBehaviour.Properties.of().lightLevel(litEmission(14))));
    public static final RegistryObject<Block> machine_nuclear_boiler = registerBlockWithItem("machine_nuclear_boiler",
            ()->new BlockElectricFurnace(BlockBehaviour.Properties.of().lightLevel(litEmission(15))));
    public static final RegistryObject<Block> machine_press = registerBlockWithItem("machine_press",
            ()->new BlockPress(BlockBehaviour.Properties.of()));
    public static final RegistryObject<Block> machine_battery = registerBlockWithItem("machine_battery",()->new BlockBattery(BlockBehaviour.Properties.of(),10L));
    public static final RegistryObject<Block> machine_lithium_battery = registerBlockWithItem("machine_lithium_battery",()->new BlockBattery(BlockBehaviour.Properties.of(),10L));
    public static final RegistryObject<Block> machine_schrabidium_battery = registerBlockWithItem("machine_schrabidium_battery",()->new BlockBattery(BlockBehaviour.Properties.of(),10L));
    public static final RegistryObject<Block> machine_dineutronium_battery = registerBlockWithItem("machine_dineutronium_battery",()->new BlockBattery(BlockBehaviour.Properties.of(),10L));
    public static final RegistryObject<Block> anvil_iron = registerBlockWithItem("anvil_iron",()->new BlockAnvil(BlockBehaviour.Properties.of()));
    public static final RegistryObject<Block> anvil_desh = registerBlockWithItem("anvil_desh",()->new BlockAnvil(BlockBehaviour.Properties.of()));
    public static final RegistryObject<Block> anvil_bismuth = registerBlockWithItem("anvil_bismuth",()->new BlockAnvil(BlockBehaviour.Properties.of()));
    public static final RegistryObject<Block> machine_cracking_tower = registerBlockWithItem("machine_cracking_tower",()->new BlockCrackingTower(BlockBehaviour.Properties.of()));
    public static final RegistryObject<Block> machine_assembler = registerBlockWithItem("machine_assembler",()->new BlockAssembler(BlockBehaviour.Properties.of()));
    public static final RegistryObject<Block> machine_crucible = registerBlockWithItem("machine_crucible",()->new BlockCrucible(BlockBehaviour.Properties.of()));
    //模型部分（仅仅用于加载模型渲染，而不会在游戏单独出现，名称以part开头）
    public static final RegistryObject<Block> part_press_head = BLOCKS.register("part_press_head",()->new Block(BlockBehaviour.Properties.of()));
    //流体
    public static final RegistryObject<LiquidBlock> irradiated_water = BLOCKS.register("irradiated_water",
            ()->new LiquidBlock(ModFluids.IRRADIATED_WATER_SOURCE_BLOCK,BlockBehaviour.Properties.copy(Blocks.WATER)));
    public static final RegistryObject<LiquidBlock> irradiated_polluted = BLOCKS.register("irradiated_polluted",
            ()->new LiquidBlock(ModFluids.IRRADIATED_POLLUTED_SOURCE_BLOCK,BlockBehaviour.Properties.copy(Blocks.WATER)));
    public static final RegistryObject<LiquidBlock> sulfuric_acid = BLOCKS.register("sulfuric_acid",
            ()->new LiquidBlock(ModFluids.SULFURIC_ACID_SOURCE_BLOCK,BlockBehaviour.Properties.copy(Blocks.WATER)));
    //输送带
    public static final RegistryObject<Block> conveyor = registerBlockWithItem("conveyor",()->new BlockConveyor(BlockBehaviour.Properties.of()));
    //炸弹
    public static final RegistryObject<Block> bomb_fat_man = BLOCKS.register("bomb_fat_man",()->new NukeFat(BlockBehaviour.Properties.of()));
    public static final RegistryObject<Block> bomb_custom = registerBlockWithItem("bomb_custom",()->new NukeCustom(BlockBehaviour.Properties.of()));

    //自然物
    public static final RegistryObject<Block> WAST_LEAVES = registerBlockWithItem("wast_leaves",()->new WasteLeaves(BlockBehaviour.Properties.copy(Blocks.ACACIA_LEAVES)));
    public static final RegistryObject<Block> WAST_EARTH = registerBlockWithItem("wast_earth",()->new WasteEarth(BlockBehaviour.Properties.copy(Blocks.DIRT)));
    public static final RegistryObject<Block> BEDROCK_ORE = registerBlockWithItem("bedrock_ore",()->new BedRockOre(BlockBehaviour.Properties.copy(Blocks.BEDROCK)));
    public static final RegistryObject<Block> URANIUM_ORE = registerBlockWithItem("uranium_ore",()->new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)));
    public static final RegistryObject<Block> DEEPSLATE_URANIUM_ORE = registerBlockWithItem("deepslate_uranium_ore",()->new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE)));

    public static ToIntFunction<BlockState> litEmission(int value){
        return state -> {
            return state.getValue(BlockStateProperties.LIT)?value:0;
        };
    }

    public static void registerItem(){
        //主要用于注册一些特殊模型的物品
        ModItems.ITEMS.register("bomb_fat_man",()->new NukeFat.NukeItem(bomb_fat_man.get(),new Item.Properties()));
    }
    public static RegistryObject<Block> registerBlockWithItem(final String name, final Supplier<? extends Block> sup){
        RegistryObject<Block> block = BLOCKS.register(name,sup);
        ModItems.ITEMS.register(name,()->new BlockItem(block.get(),new Item.Properties()));

        return block;
    }
    public static void register(IEventBus modEventBus){
        registerItem();
        BLOCKS.register(modEventBus);
    }
}
