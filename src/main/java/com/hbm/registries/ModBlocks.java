package com.hbm.registries;

import com.hbm.block.HBMBlockComponent;
import com.hbm.block.HBMMachine;
import com.hbm.block.decoriate.BlockTest12;
import com.hbm.block.env.BedRockOre;
import com.hbm.block.env.WasteEarth;
import com.hbm.block.env.WasteLeaves;
import com.hbm.block.logistic.BlockCable;
import com.hbm.block.machine.*;
import com.hbm.block.logistic.BlockConveyor;
import com.hbm.block.weapon.*;
import com.hbm.Inventory.fluid.ModFluids;
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
    static {
        HBMMachine.register(BLOCKS);
        HBMBlockComponent.register(BLOCKS);
    }
    //机械
    public static final RegistryObject<Block> machine_difurnace = registerBlockWithItem("machine_difurnace", ()->new BlockDifurnace(BlockBehaviour.Properties.of().lightLevel(litEmission(13))));
    public static final RegistryObject<Block> machine_electric_furnace = registerBlockWithItem("machine_electric_furnace", ()->new BlockElectricFurnace(BlockBehaviour.Properties.of().lightLevel(litEmission(13))));
    public static final RegistryObject<Block> machine_boiler = registerBlockWithItem("machine_boiler", ()->new BlockBoiler(BlockBehaviour.Properties.of().lightLevel(litEmission(13))));
    public static final RegistryObject<Block> machine_electric_boiler = registerBlockWithItem("machine_electric_boiler", ()->new BlockElectricBoiler(BlockBehaviour.Properties.of().lightLevel(litEmission(14))));
    public static final RegistryObject<Block> machine_nuclear_boiler = registerBlockWithItem("machine_nuclear_boiler", ()->new BlockNuclearBoiler(BlockBehaviour.Properties.of().lightLevel(litEmission(15))));
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
    public static final RegistryObject<Block> bomb_fat_man = registerBlockWithItem("bomb_fat_man",()->new NukeFat(BlockBehaviour.Properties.of(),200));
    public static final RegistryObject<Block> bomb_custom = registerBlockWithItem("bomb_custom",()->new NukeCustom(BlockBehaviour.Properties.of(),350));
    //发射台

    //流体
//    public static final RegistryObject<LiquidBlock> irradiated_water = BLOCKS.register("irradiated_water", ()->new LiquidBlock(ModFluids.IRRADIATED_WATER_SOURCE_BLOCK,BlockBehaviour.Properties.copy(Blocks.WATER).noLootTable()));
//    public static final RegistryObject<LiquidBlock> irradiated_polluted = BLOCKS.register("irradiated_polluted", ()->new LiquidBlock(ModFluids.IRRADIATED_POLLUTED_SOURCE_BLOCK,BlockBehaviour.Properties.copy(Blocks.WATER).noLootTable()));
//    public static final RegistryObject<LiquidBlock> sulfuric_acid = BLOCKS.register("sulfuric_acid", ()->new LiquidBlock(ModFluids.SULFURIC_ACID_SOURCE_BLOCK,BlockBehaviour.Properties.copy(Blocks.WATER).noLootTable()));

    //逻辑物
    public static final RegistryObject<Block> DUMMIBLE = registerBlockWithItem("dummible",()->new DummibleBlock(BlockBehaviour.Properties.copy(Blocks.STONE).noLootTable()));
    //装饰
    public static final RegistryObject<Block> TEST12 = registerBlockWithItem("test12",()->new BlockTest12(BlockBehaviour.Properties.of()));

    public static ToIntFunction<BlockState> litEmission(int value){
        return state -> {
            return state.getValue(BlockStateProperties.LIT)?value:0;
        };
    }

//    public static void registerItem(){
//        //主要用于注册一些特殊模型的物品
//        ModItems.ITEMS.register("bomb_fat_man",()->new NukeFat.NukeItem(bomb_fat_man.get(),new Item.Properties()));
//    }
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
        BLOCKS.register(modEventBus);
    }
}
