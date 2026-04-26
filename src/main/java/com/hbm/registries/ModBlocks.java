package com.hbm.registries;

import com.hbm.HBMKey;

import com.hbm.block.HBMBlockProperties;
import com.hbm.block.base.BlockBase;
import com.hbm.block.decoriate.BlockMolten;
import com.hbm.block.decoriate.BlockOre;
import com.hbm.block.decoriate.BlockTest12;
import com.hbm.block.env.*;
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
import com.hbm.blockentity.machine.rbmk.RBMKBoilerEntity;
import com.hbm.blockentity.machine.rbmk.RBMKCoolerEntity;
import com.hbm.blockentity.machine.rbmk.RBMKOutgasserEntity;
import com.hbm.blockentity.machine.rbmk.RBMKStorageEntity;
import com.hbm.block.machine.research.BlockResearchReactor;
import com.hbm.block.machine.generator.BlockPWR;
import com.hbm.block.machine.generator.BlockPWRController;
import com.hbm.block.machine.generator.BlockPWRPillar;
import com.hbm.block.machine.generator.BlockGenericPWR;
import com.hbm.block.space.BlockSpaceStation;
import com.hbm.block.tools.FoundryMold;
import com.hbm.block.weapon.NukeBoy;
import com.hbm.block.weapon.NukeCustom;
import com.hbm.block.weapon.NukeFat;
import com.hbm.config.ConfigBomb;
import com.hbm.datagen.LanguageProvider;
import com.hbm.datagen.loot.BlockLootGen;
import com.hbm.datagen.model.BlockStateGen;
import com.hbm.datagen.tag.BlockTagsGen;
import com.hbm.item.blockitem.IronCrateItem;
import com.hbm.item.blockitem.SteelCrateItem;
import com.hbm.item.tool.BatteryBlockItem;
import com.hbm.reactor.rbmk.RBMKPeripheralType;
import com.hbm.registries.WrapperRegistry.BlockBuilder;
import com.hbm.registries.WrapperRegistry.WrappedBlockRegistry;
import com.hbm.debug.BlockDebug;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.Shapes;
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
    public static final List<WrappedBlockRegistry> blockList = new ArrayList<>();
    private static final List<RegistryObject<Block>> legacyMachineTagBlocks = new ArrayList<>();

    //机械
    public static final RegistryObject<Block> CHEMPLANT = add("chemplant", ()->new BlockChemplant(BlockBehaviour.Properties.of().strength(5.0F).explosionResistance(30.0F)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "Chemical Plant", HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> PLASTIC_BARREL = add("barrel_plastic", ()->new BlockFluidBarrel(BlockBehaviour.Properties.of().strength(2.0F).explosionResistance(5.0F).requiresCorrectToolForDrops().sound(SoundType.STONE), BlockFluidBarrel.BarrelProperties.of().capacity(12000)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "Safe Barrel™", HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> CORRODED_BARREL = add("barrel_corroded", ()->new BlockFluidBarrel(BlockBehaviour.Properties.of().strength(2.0F).explosionResistance(5.0F).requiresCorrectToolForDrops().sound(SoundType.METAL),BlockFluidBarrel.BarrelProperties.of().capacity(6000).hotResist().corrosiveResistance().highCorroResist().leaky()), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "Corroded Barrel", HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> IRON_BARREL = add("barrel_iron", ()->new BlockFluidBarrel(BlockBehaviour.Properties.of().strength(2.0F).explosionResistance(5.0F).requiresCorrectToolForDrops().sound(SoundType.METAL),BlockFluidBarrel.BarrelProperties.of().capacity(8000).hotResist()), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "Iron Barrel", HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> STEEL_BARREL = add("barrel_steel", ()->new BlockFluidBarrel(BlockBehaviour.Properties.of().strength(2.0F).explosionResistance(5.0F).requiresCorrectToolForDrops().sound(SoundType.METAL),BlockFluidBarrel.BarrelProperties.of().capacity(16000).hotResist().corrosiveResistance()), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "Steel Barrel", HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> TCALLOY_BARREL = add("barrel_tcalloy", ()->new BlockFluidBarrel(BlockBehaviour.Properties.of().strength(2.0F).explosionResistance(5.0F).requiresCorrectToolForDrops().sound(SoundType.METAL),BlockFluidBarrel.BarrelProperties.of().capacity(24000).hotResist().highCorroResist()), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "Technetium Steel Barrel", HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> ANTIMATTER_BARREL = add("barrel_antimatter", ()->new BlockFluidBarrel(BlockBehaviour.Properties.of().strength(2.0F).explosionResistance(5.0F).requiresCorrectToolForDrops().sound(SoundType.METAL),BlockFluidBarrel.BarrelProperties.of().capacity(16000).hotResist().highCorroResist().antimatter()), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "Magnetic Antimatter Container", HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> FLUID_PIPE = add("fluid_pipe", ()->new BlockFluidPipe(BlockBehaviour.Properties.of().strength(5.0F).explosionResistance(10.0F)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "Fluid Pipe", HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> GEIGER_COUNTER = add("geiger", ()->new GeigerCounter(BlockBehaviour.Properties.of().strength(5.0F).explosionResistance(10.0F)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "Geiger Counter", HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> LAUNCH_PAD = add("launch_pad", ()->new LaunchPad(BlockBehaviour.Properties.of().strength(5.0F).explosionResistance(10.0F)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, "Launch Pad", HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> machine_difurnace = registerMachineBlockWithItem("machine_difurnace", ()->new BlockDifurnace(Properties.of().lightLevel(litEmission(13))));
    public static final RegistryObject<Block> machine_electric_furnace = registerMachineBlockWithItem("machine_electric_furnace", ()->new BlockElectricFurnace(Properties.of().lightLevel(litEmission(13))));
    public static final RegistryObject<Block> FURNACE_ELECTRIC = machine_electric_furnace;
    public static final RegistryObject<Block> machine_boiler = registerMachineBlockWithItem("machine_boiler", ()->new BlockBoiler(Properties.of().lightLevel(litEmission(13))));
    public static final RegistryObject<Block> machine_electric_boiler = registerMachineBlockWithItem("machine_electric_boiler", ()->new BlockElectricBoiler(Properties.of().lightLevel(litEmission(14))));
    public static final RegistryObject<Block> machine_nuclear_boiler = registerMachineBlockWithItem("machine_nuclear_boiler", ()->new BlockNuclearBoiler(Properties.of().lightLevel(litEmission(15))));
    public static final RegistryObject<Block> machine_press = registerMachineBlockWithItem("machine_press", ()->new BlockPress(Properties.of()));
    public static final RegistryObject<Block> PRESS_PREHEATER = add("press_preheater", ()->new BlockBase(Properties.of()), ModTabs.MACHINE.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.ORDERLY_GEN, HBMKey.DROP_SELF);
    public static final RegistryObject<Block> machine_shredder = registerMachineBlockWithItem("machine_shredder", ()->new BlockShredder(Properties.of()));
    public static final RegistryObject<Block> machine_wood_burner = registerMachineBlockWithItem("machine_wood_burner",
            () -> new WoodBurnerBlock(Properties.of().strength(3.0F).sound(SoundType.METAL)
                    .lightLevel(state -> state.getValue(WoodBurnerBlock.LIT) ? 13 : 0)));
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

    // Chicago Pile components
    public static final RegistryObject<Block> chicago_graphite_block = new BlockBuilder("chicago_graphite_block",
            () -> new ChicagoGraphiteBlock(BlockBehaviour.Properties.of().strength(4.0F).explosionResistance(10.0F).sound(SoundType.STONE).requiresCorrectToolForDrops()))
            .tab(ModTabs.MACHINE.getKey())
            .build();
    public static final RegistryObject<Block> chicago_graphite_drilled = new BlockBuilder("chicago_graphite_drilled",
            () -> new ChicagoGraphiteDrilledBlock(BlockBehaviour.Properties.of().strength(4.0F).explosionResistance(10.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()))
            .tab(ModTabs.MACHINE.getKey())
            .build();
    public static final RegistryObject<Block> chicago_graphite_rod = new BlockBuilder("chicago_graphite_rod",
            () -> new ChicagoGraphiteRodBlock(BlockBehaviour.Properties.of().strength(4.0F).explosionResistance(10.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()))
            .tab(ModTabs.MACHINE.getKey())
            .build();
    public static final RegistryObject<Block> chicago_graphite_fuel = new BlockBuilder("chicago_graphite_fuel",
            () -> new ChicagoGraphiteFuelBlock(BlockBehaviour.Properties.of().strength(4.0F).explosionResistance(10.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()))
            .tab(ModTabs.MACHINE.getKey())
            .build();
    public static final RegistryObject<Block> chicago_graphite_source = new BlockBuilder("chicago_graphite_source",
            () -> new ChicagoGraphiteSourceBlock(BlockBehaviour.Properties.of().strength(4.0F).explosionResistance(10.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()))
            .tab(ModTabs.MACHINE.getKey())
            .build();
    public static final RegistryObject<Block> chicago_graphite_breeder = new BlockBuilder("chicago_graphite_breeder",
            () -> new ChicagoGraphiteBreederBlock(BlockBehaviour.Properties.of().strength(4.0F).explosionResistance(10.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()))
            .tab(ModTabs.MACHINE.getKey())
            .build();
    public static final RegistryObject<Block> chicago_graphite_detector = new BlockBuilder("chicago_graphite_detector",
            () -> new ChicagoGraphiteDetectorBlock(BlockBehaviour.Properties.of().strength(4.0F).explosionResistance(10.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().lightLevel(state -> state.getValue(ChicagoPileStateProperties.TRIGGERED) ? 4 : 0)))
            .tab(ModTabs.MACHINE.getKey())
            .build();
    public static final RegistryObject<Block> chicago_graphite_tritium = new BlockBuilder("chicago_graphite_tritium",
            () -> new ChicagoGraphiteTritiumBlock(BlockBehaviour.Properties.of().strength(4.0F).explosionResistance(10.0F).sound(SoundType.METAL).requiresCorrectToolForDrops()))
            .tab(ModTabs.MACHINE.getKey())
            .build();
    public static final RegistryObject<Block> machine_battery = registerMachineBattery("machine_battery",()->new BlockBattery(Properties.of(), BlockBattery.BatteryType.BASIC));
    public static final RegistryObject<Block> machine_lithium_battery = registerMachineBattery("machine_lithium_battery",()->new BlockBattery(Properties.of(), BlockBattery.BatteryType.LITHIUM));
    public static final RegistryObject<Block> machine_schrabidium_battery = registerMachineBattery("machine_schrabidium_battery",()->new BlockBattery(Properties.of(), BlockBattery.BatteryType.SCHRABIDIUM));
    public static final RegistryObject<Block> machine_dineutronium_battery = registerMachineBattery("machine_dineutronium_battery",()->new BlockBattery(Properties.of(), BlockBattery.BatteryType.DINEUTRONIUM));
    public static final RegistryObject<Block> BATTERY = machine_battery;
    public static final RegistryObject<Block> BATTERY_LITHIUM = machine_lithium_battery;
    public static final RegistryObject<Block> BATTERY_SCHRABIDIUM = machine_schrabidium_battery;
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
    public static final RegistryObject<Block> machine_rbmk_boiler = registerMachineBlockWithItem("machine_rbmk_boiler", () -> new BlockRBMKColumn(Properties.of().strength(4.0F).explosionResistance(12.0F), RBMKBoilerEntity::new));
    public static final RegistryObject<Block> machine_rbmk_moderator = registerMachineBlockWithItem("machine_rbmk_moderator", () -> new BlockRBMKColumn(Properties.of().strength(4.0F).explosionResistance(16.0F)));
    public static final RegistryObject<Block> machine_rbmk_absorber = registerMachineBlockWithItem("machine_rbmk_absorber", () -> new BlockRBMKColumn(Properties.of().strength(4.0F).explosionResistance(16.0F)));
    public static final RegistryObject<Block> machine_rbmk_outgasser = registerMachineBlockWithItem("machine_rbmk_outgasser", () -> new BlockRBMKColumn(Properties.of().strength(4.0F).explosionResistance(16.0F), RBMKOutgasserEntity::new));
    public static final RegistryObject<Block> machine_rbmk_storage = registerMachineBlockWithItem("machine_rbmk_storage", () -> new BlockRBMKColumn(Properties.of().strength(4.0F).explosionResistance(16.0F), RBMKStorageEntity::new));
    public static final RegistryObject<Block> machine_rbmk_cooler = registerMachineBlockWithItem("machine_rbmk_cooler", () -> new BlockRBMKColumn(Properties.of().strength(4.0F).explosionResistance(16.0F), RBMKCoolerEntity::new));
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
        registerLegacyBlockItemAlias("deco_rbmk", machine_rbmk_base);
        registerLegacyBlockItemAlias("deco_rbmk_smooth", machine_rbmk_base);
    }
    public static final RegistryObject<Block> HEATER_FIREBOX = add("firebox", ()->new BlockFireBox(Properties.copy(Blocks.IRON_BLOCK)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.ORDERLY_GEN, HBMKey.DROP_SELF);
    //电力
    public static final RegistryObject<Block> RED_CABLE = add("red_cable", ()->new BlockCable(Properties.copy(Blocks.STONE_BRICK_WALL)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.ORDERLY_GEN, HBMKey.DROP_SELF);
    public static final RegistryObject<Block> CONNECTOR = add("connector", ()->new BlockConnector(Properties.of()), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.ORDERLY_GEN, HBMKey.DROP_SELF);
    // 物流体系
    public static final RegistryObject<Block> conveyor = new BlockBuilder("conveyor", ()->new Conveyor(Properties.copy(Blocks.STONE))).tab(ModTabs.CONTROL.getKey()).model(HBMKey.MODEL_STANDALONE).loc(HBMKey.ORDERLY_GEN).loot(HBMKey.DROP_SELF).tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL).build();
    public static final RegistryObject<Block> CONVEYOR_INSERTER = new BlockBuilder("conveyor_inserter", ()->new ConveyorInserter(Properties.copy(Blocks.STONE))).tab(ModTabs.CONTROL.getKey()).model(HBMKey.MODEL_STANDALONE).loc(HBMKey.ORDERLY_GEN).loot(HBMKey.DROP_SELF).tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL).build();
    public static final RegistryObject<Block> CONVEYOR_EXTRACTOR = new BlockBuilder("conveyor_extractor", ()->new ConveyorExtractor(Properties.copy(Blocks.STONE))).tab(ModTabs.CONTROL.getKey()).model(HBMKey.MODEL_STANDALONE).loc(HBMKey.ORDERLY_GEN).loot(HBMKey.DROP_SELF).tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL).build();
    public static final RegistryObject<Block> CONVEYOR_ROUTER = new BlockBuilder("conveyor_router", ()->new ConveyorRouter(Properties.copy(Blocks.STONE))).tab(ModTabs.CONTROL.getKey()).model(HBMKey.MODEL_EXISTING).loc(HBMKey.ORDERLY_GEN).loot(HBMKey.DROP_SELF).tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL).build();
    public static final RegistryObject<Block> crate_iron =
            new BlockBuilder("crate_iron", () -> new IronCrateBlock(Properties.of().strength(3.0F).sound(SoundType.WOOD)))
                    .tab(ModTabs.MACHINE.getKey()).loc(HBMKey.REVERSE_GEN)
                    .item(block -> new IronCrateItem(block, new Item.Properties().stacksTo(1)))
                    .build();
    public static final RegistryObject<Block> crate_steel =
            new BlockBuilder("crate_steel", () -> new SteelCrateBlock(Properties.of().strength(4.0F).sound(SoundType.METAL)))
                    .tab(ModTabs.MACHINE.getKey()).loc(HBMKey.REVERSE_GEN)
                    .item(block -> new SteelCrateItem(block, new Item.Properties().stacksTo(1))).loc(HBMKey.REVERSE_GEN)
                    .build();
    //炸弹
    public static final RegistryObject<Block> bomb_boy = registerBlockWithItem("bomb_boy",()->new NukeBoy(Properties.of(), ConfigBomb.boyRadius));
    public static final RegistryObject<Block> bomb_fat_man = registerBlockWithItem("bomb_fat_man",()->new NukeFat(Properties.of(), ConfigBomb.manRadius));
    public static final RegistryObject<Block> bomb_custom = registerBlockWithItem("bomb_custom",()->new NukeCustom(Properties.of(), ConfigBomb.manRadius));
    public static final RegistryObject<Block> BOMB_FAT_MAN = bomb_fat_man;
    //发射台
    //装饰
    public static final RegistryObject<Block> TEST12 = registerBlockWithItem("test12",()->new BlockTest12(Properties.of()));
    // glyphid
    public static final RegistryObject<Block> GLYPHID_BLOCK = add("glyphid_block", ()->new GlyphidBlock(Properties.copy(Blocks.STONE).pushReaction(PushReaction.IGNORE).explosionResistance(0.5f)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.ORDERLY_GEN, HBMKey.DROP_NONE);
    public static final RegistryObject<Block> GLYPHID_SPAWNER = add("glyphid_spawner", ()->new GlyphidSpawner(Properties.copy(Blocks.STONE).pushReaction(PushReaction.IGNORE).explosionResistance(0.5f)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.ORDERLY_GEN, HBMKey.DROP_NONE);
    // 陨石
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
    public static final RegistryObject<Block> ORE_METEOR_IRON = add("ore_meteor_iron", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF);
    public static final RegistryObject<Block> ORE_METEOR_COPPER = add("ore_meteor_copper", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF);
    public static final RegistryObject<Block> ORE_METEOR_ALUMINIUM = add("ore_meteor_aluminium", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF);
    public static final RegistryObject<Block> ORE_METEOR_RAREEARTH = add("ore_meteor_rareearth", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF);
    public static final RegistryObject<Block> ORE_METEOR_COBALT = add("ore_meteor_cobalt", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF);

    public static final RegistryObject<Block> TAINT = block("taint", () -> new Block(Properties.copy(Blocks.IRON_BLOCK)));
//    public static final RegistryObject<Block> WASTE_GRASS = add("waste_grass", () -> new WasteEarth(Properties.copy(Blocks.DIRT)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_DIFURNACE, HBMKey.ORDERLY_GEN, HBMKey.DROP_STANDALONE);
    // casting
    public static final RegistryObject<Block> FOUNDRY_MOLD = new BlockBuilder("foundry_mold", () -> new FoundryMold(Properties.copy(Blocks.STONE)))
            .tab(ModTabs.MACHINE.getKey()).model(HBMKey.MODEL_EXISTING).loc(HBMKey.ORDERLY_GEN).loot(HBMKey.DROP_SELF).tags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL).build();
    // 方块
    public static final RegistryObject<Block> STEEL_BLOCK = block("block_steel", ()->new Block(BlockBehaviour.Properties.of()), BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> SELLAFIELD_SLAKED = block("sellafield_slaked", ()->new Block(BlockBehaviour.Properties.of().explosionResistance(5.0f)), BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> WASTE_LEAVES = add("waste_leaves",()->new WasteLeaves(BlockBehaviour.Properties.copy(Blocks.ACACIA_LEAVES).noLootTable()), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_LEAVES, HBMKey.ORDERLY_GEN, HBMKey.DROP_NONE);
    public static final RegistryObject<Block> WASTE_EARTH = add("waste_earth",()->new WasteEarth(BlockBehaviour.Properties.copy(Blocks.DIRT)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_BOTTOM_TOP, HBMKey.ORDERLY_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
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
    public static final RegistryObject<Block> ASBESTOS_BLOCK = add("block_asbestos",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
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
    public static final RegistryObject<Block> BEDROCK_ORE = add("ore_bedrock",()->new BedRockOre(BlockBehaviour.Properties.copy(Blocks.BEDROCK)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.REVERSE_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> DEPTH_STONE = add("depth_stone",()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.REINFORCED_DEEPSLATE)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.ORDERLY_GEN, HBMKey.DROP_SELF, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL);
    public static final RegistryObject<Block> BLOCK_COKE_COAL = registerBlockWithItem("block_coke_coal", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COAL_BLOCK)));
    public static final RegistryObject<Block> BLOCK_COKE_LIGNITE = registerBlockWithItem("block_coke_lignite", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COAL_BLOCK)));
    public static final RegistryObject<Block> BLOCK_COKE_PETROLEUM = registerBlockWithItem("block_coke_petroleum", () -> new Block(BlockBehaviour.Properties.copy(Blocks.COAL_BLOCK)));
    public static final RegistryObject<Block> BLOCK_SCRAP = registerBlockWithItem("block_scrap", () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    /**
     * 航天版方块
     * */
    public static final RegistryObject<Block> moon_rock = block("moon_rock", ()->new Block(Properties.of().sound(SoundType.STONE).strength(1.5f, 10f)));
    public static final RegistryObject<Block> moon_turf = block("moon_turf", ()->new FallingBlock(Properties.of().sound(SoundType.SAND).strength(0.5f)));
    public static final RegistryObject<Block> SPACE_STATION_BASE = add("space_station_base", ()->new BlockSpaceStation(Properties.of().sound(SoundType.SAND).strength(0.5f)), ModTabs.MACHINE.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.ORDERLY_GEN, HBMKey.DROP_SELF);
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
        ModItems.ITEMS.register(name,()->new BatteryBlockItem(block.get(),new Item.Properties()));
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
        return new BlockBuilder(name, sup).tab(tabKey).model(genModelWay).loc(genNameWay).loot(lootWay).tags(keys).build();
    }

    /**
     * 其他属性注册的钩子
     * */
    public static void creativeTab(BuildCreativeModeTabContentsEvent event){
        for (WrappedBlockRegistry blockRegistry : blockList) {
            blockRegistry.creativeTabSupport(event);
        }
    }

    public static void genModel(BlockStateGen provider){
        for (WrappedBlockRegistry blockRegistry : blockList) {
            blockRegistry.modelSupport(provider);
        }
        provider.addIntStateCubeAllBlock(ModBlocks.GLYPHID_BLOCK.get(), HBMBlockProperties.VARIANT3);
        provider.addIntStateCubeAllBlock(ModBlocks.GLYPHID_SPAWNER.get(), HBMBlockProperties.VARIANT3);
    }
    public static void languageSupport(LanguageProvider provider){
        for (WrappedBlockRegistry blockRegistry : blockList) {
            blockRegistry.languageSupport(provider);
        }
    }

    public static void lootSupport(BlockLootGen provider){
        for (WrappedBlockRegistry blockRegistry : blockList) {
            blockRegistry.lootSupport(provider);
        }
    }

    public static void tagSupport(BlockTagsGen provider){
        for (WrappedBlockRegistry wrappedBlockRegistry : blockList) {
            wrappedBlockRegistry.tagSupport(provider);
        }
    }
}
