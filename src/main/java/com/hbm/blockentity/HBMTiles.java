package com.hbm.blockentity;

import com.hbm.block.env.BedRockOreTE;
import com.hbm.block.env.GlyphidSpawner;
import com.hbm.blockentity.base.TileProxyCombo;
import com.hbm.blockentity.logistic.*;
import com.hbm.blockentity.machine.*;
import com.hbm.blockentity.machine.IronCrateBE;
import com.hbm.blockentity.machine.SteelCrateBE;
import com.hbm.HBM;
import com.hbm.blockentity.machine.icf.ICFControllerBE;
import com.hbm.blockentity.machine.icf.ICFPressBE;
import com.hbm.blockentity.machine.icf.ICFReactorBE;
import com.hbm.blockentity.machine.rbmk.*;
import com.hbm.blockentity.machine.research.BreederReactorBE;
import com.hbm.blockentity.machine.research.ResearchReactorBE;
import com.hbm.blockentity.machine.tokamak.TokamakControllerBE;
import com.hbm.blockentity.tools.TileEntityGeigerBE;
import com.hbm.blockentity.machine.pile.ChicagoBreederBlockEntity;
import com.hbm.blockentity.machine.pile.ChicagoDetectorBlockEntity;
import com.hbm.blockentity.machine.pile.ChicagoFuelBlockEntity;
import com.hbm.blockentity.machine.pile.ChicagoSourceBlockEntity;
import com.hbm.blockentity.machine.rbmk.RBMKNumitronEntityBE;
import com.hbm.blockentity.tools.TileFoundryMold;
import com.hbm.blockentity.weapon.*;
import com.hbm.core.blockentity.BEProxy;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;
import java.util.function.Supplier;

public class HBMTiles {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, HBM.MODID);
    public static final Map<String, RegistryObject<? extends BlockEntityType>> tileTypes = new HashMap<>();
    public static final Set<Supplier<Block>> dummyableBlocks = new HashSet<>();

    public static RegistryObject<BlockEntityType<BEProxy>> PROXY;

//    public static final RegistryObject<BlockEntityType<DifurnaceEntity>> DIFURNACE_ENTITY =
//            REGISTER.register("difurnace_entity",()-> BlockEntityType.Builder.of(DifurnaceEntity::new, ModBlocks.machine_difurnace.get()).build(null));
//    public static final RegistryObject<BlockEntityType<PressEntityBE>> PRESS_ENTITY =
//            REGISTER.register("press_entity",()-> BlockEntityType.Builder.of(PressEntityBE::new, ModBlocks.machine_press.get()).build(null));
//    public static final RegistryObject<BlockEntityType<NukeBombFatEntityBE>> NUKE_BOMB_FAT_ENTITY =
//            REGISTER.register("nuke_bomb_entity",()-> BlockEntityType.Builder.of(NukeBombFatEntityBE::new, ModBlocks.bomb_fat_man.get()).build(null));
//    public static final RegistryObject<BlockEntityType<NukeBombBoyEntityBE>> NUKE_BOMB_BOY_ENTITY =
//            REGISTER.register("nuke_bomb_boy",()-> BlockEntityType.Builder.of(NukeBombBoyEntityBE::new, ModBlocks.bomb_boy.get()).build(null));
//    public static final RegistryObject<BlockEntityType<NukeBombCustomEntityBE>> NUKE_BOMB_CUSTOM_ENTITY =
//            REGISTER.register("nuke_bomb_custom",()-> BlockEntityType.Builder.of(NukeBombCustomEntityBE::new, ModBlocks.bomb_custom.get()).build(null));
    public static final RegistryObject<BlockEntityType<AssemblerEntityBE>> ASSEMBLER_ENTITY =
            REGISTER.register("assembler_entity",()-> BlockEntityType.Builder.of(AssemblerEntityBE::new, ModBlocks.machine_assembler.get()).build(null));
    public static final RegistryObject<BlockEntityType<CrucibleEntityBE>> CRUCIBLE_ENTITY =
            REGISTER.register("crucible_entity",()-> BlockEntityType.Builder.of(CrucibleEntityBE::new, ModBlocks.machine_crucible.get()).build(null));
//    public static final RegistryObject<BlockEntityType<BedRockOre.BedRockOreEntity>> BEDROCK_ORE_ENTITY =
//            REGISTER.register("bedrock_ore_entity",()-> BlockEntityType.Builder.of(BedRockOre.BedRockOreEntity::new, ModBlocks.BEDROCK_ORE.get()).build(null));
    public static final RegistryObject<BlockEntityType<CableEntityBEPipeBase>> CABLE_ENTITY =
            REGISTER.register("cable_entity",()-> BlockEntityType.Builder.of(CableEntityBEPipeBase::new, ModBlocks.RED_CABLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<PipeEntityBEPipeBase>> PIPE_ENTITY =
            REGISTER.register("pipe_entity",()-> BlockEntityType.Builder.of(PipeEntityBEPipeBase::new, ModBlocks.FLUID_DUCT_NEO.get()).build(null));
    public static final RegistryObject<BlockEntityType<BatteryEntityBE>> BATTERY_ENTITY =
            REGISTER.register("battery_entity",()-> BlockEntityType.Builder.of(BatteryEntityBE::new, ModBlocks.machine_battery.get(),ModBlocks.machine_lithium_battery.get(),ModBlocks.machine_dineutronium_battery.get(),ModBlocks.machine_schrabidium_battery.get()).build(null));
    public static final RegistryObject<BlockEntityType<LaunchPadTileEntityBE>> LAUNCHPAD_ENTITY =
            REGISTER.register("launchpad_entity",()-> BlockEntityType.Builder.of(LaunchPadTileEntityBE::new, ModBlocks.LAUNCH_PAD.get()).build(null));

//    public static final RegistryObject<BlockEntityType<ElectricFurnaceEntityBE>> ELECTRIC_FURNACE_ENTITY =
//            REGISTER.register("electric_furnace_entity",()-> BlockEntityType.Builder.of(ElectricFurnaceEntityBE::new, ModBlocks.machine_electric_furnace.get()).build(null));
    public static final RegistryObject<BlockEntityType<BoilerEntityBE>> BOILER_ENTITY =
            REGISTER.register("boiler_entity",()-> BlockEntityType.Builder.of(BoilerEntityBE::new, ModBlocks.machine_boiler.get()).build(null));
    public static final RegistryObject<BlockEntityType<ElectricBoilerEntityBE>> ELECTRIC_BOILER_ENTITY =
            REGISTER.register("electric_boiler_entity",()-> BlockEntityType.Builder.of(ElectricBoilerEntityBE::new, ModBlocks.machine_electric_boiler.get()).build(null));
    public static final RegistryObject<BlockEntityType<NuclearBoilerEntityBE>> NUCLEAR_BOILER_ENTITY =
            REGISTER.register("nuclear_boiler_entity",()-> BlockEntityType.Builder.of(NuclearBoilerEntityBE::new, ModBlocks.machine_nuclear_boiler.get()).build(null));
    public static final RegistryObject<BlockEntityType<ChemplantEntityBE>> CHEMPLANT_ENTITY =
            REGISTER.register("chemplant_entity",()-> BlockEntityType.Builder.of(ChemplantEntityBE::new, ModBlocks.MACHINE_CHEMPLANT.get()).build(null));
//    public static final RegistryObject<BlockEntityType<BarrelEntityBE>> BARREL_ENTITY =
//            REGISTER.register("barrel_entity",()-> BlockEntityType.Builder.of(BarrelEntityBE::new, ModBlocks.BARREL_PLASTIC.get(),ModBlocks.BARREL_CORRODED.get(),ModBlocks.IRON_BARREL.get(),ModBlocks.STEEL_BARREL.get(),ModBlocks.TCALLOY_BARREL.get(),ModBlocks.ANTIMATTER_BARREL.get()).build(null));
    public static final RegistryObject<BlockEntityType<CondenserBE>> CONDENSER_ENTITY =
            REGISTER.register("condenser_entity", () -> BlockEntityType.Builder.of(CondenserBE::new, ModBlocks.machine_condenser.get()).build(null));
    public static final RegistryObject<BlockEntityType<CoolingTowerBE>> COOLING_TOWER_ENTITY =
            REGISTER.register("cooling_tower_entity", () -> BlockEntityType.Builder.of(CoolingTowerBE::new, ModBlocks.machine_cooling_tower.get()).build(null));
    public static final RegistryObject<BlockEntityType<GasTurbineBE>> GAS_TURBINE_ENTITY =
            REGISTER.register("gas_turbine_entity", () -> BlockEntityType.Builder.of(GasTurbineBE::new, ModBlocks.machine_turbine_gas.get()).build(null));
    public static final RegistryObject<BlockEntityType<ChicagoFuelBlockEntity>> CHICAGO_FUEL =
            REGISTER.register("chicago_fuel", () -> BlockEntityType.Builder.of(ChicagoFuelBlockEntity::new, ModBlocks.chicago_graphite_fuel.get()).build(null));
    public static final RegistryObject<BlockEntityType<ChicagoSourceBlockEntity>> CHICAGO_SOURCE =
            REGISTER.register("chicago_source", () -> BlockEntityType.Builder.of(ChicagoSourceBlockEntity::new, ModBlocks.chicago_graphite_source.get()).build(null));
    public static final RegistryObject<BlockEntityType<ChicagoBreederBlockEntity>> CHICAGO_BREEDER =
            REGISTER.register("chicago_breeder", () -> BlockEntityType.Builder.of(ChicagoBreederBlockEntity::new, ModBlocks.chicago_graphite_breeder.get()).build(null));
    public static final RegistryObject<BlockEntityType<ChicagoDetectorBlockEntity>> CHICAGO_DETECTOR =
            REGISTER.register("chicago_detector", () -> BlockEntityType.Builder.of(ChicagoDetectorBlockEntity::new, ModBlocks.chicago_graphite_detector.get()).build(null));
    public static final RegistryObject<BlockEntityType<ShredderEntityBE>> SHREDDER_ENTITY =
            REGISTER.register("shredder_entity",()-> BlockEntityType.Builder.of(ShredderEntityBE::new, ModBlocks.machine_shredder.get()).build(null));
    public static final RegistryObject<BlockEntityType<WoodBurnerBE>> WOOD_BURNER_ENTITY =
            REGISTER.register("wood_burner_entity", () -> BlockEntityType.Builder.of(WoodBurnerBE::new, ModBlocks.machine_wood_burner.get()).build(null));
    public static final RegistryObject<BlockEntityType<CrackingTowerEntityBE>> CRACKING_TOWER_ENTITY =
            REGISTER.register("cracking_tower_entity", () -> BlockEntityType.Builder.of(CrackingTowerEntityBE::new, ModBlocks.machine_cracking_tower.get()).build(null));
    public static final RegistryObject<BlockEntityType<IronCrateBE>> IRON_CRATE_ENTITY =
            REGISTER.register("iron_crate_entity", () -> BlockEntityType.Builder.of(IronCrateBE::new, ModBlocks.crate_iron.get()).build(null));
    public static final RegistryObject<BlockEntityType<SteelCrateBE>> STEEL_CRATE_ENTITY =
            REGISTER.register("steel_crate_entity", () -> BlockEntityType.Builder.of(SteelCrateBE::new, ModBlocks.crate_steel.get()).build(null));
    public static RegistryObject<BlockEntityType<TileProxyCombo>> PROXY_ENTITY;
//            =
//            REGISTER.register("proxy_entity",()-> BlockEntityType.Builder.of(TileProxyCombo::new,
//                    ModBlocks.machine_crucible.get(), ModBlocks.machine_assembler.get(), ModBlocks.machine_cracking_tower.get(), ModBlocks.CHEMPLANT.get(),
//                    ModBlocks.LAUNCH_PAD.get(), ModBlocks.bomb_boy.get(), ModBlocks.bomb_custom.get(), ModBlocks.bomb_fat_man.get(), ModBlocks.machine_zirnox.get(),
//                    ModBlocks.SPACE_STATION_BASE.get(), ModBlocks.HEATER_FIREBOX.get()
//            ).build(null));
//    public static final RegistryObject<BlockEntityType<TileEntityGeigerBE>> GEIGER_COUNTER =
//            REGISTER.register("geiger_counter",()-> BlockEntityType.Builder.of(TileEntityGeigerBE::new, ModBlocks.GEIGER_COUNTER.get()).build(null));
    public static final RegistryObject<BlockEntityType<GlyphidSpawner.GlyphidSpawnerEntity>> GLYPHID_SPAWNER =
            REGISTER.register("glyphid_spawner",()-> BlockEntityType.Builder.of(GlyphidSpawner.GlyphidSpawnerEntity::new, ModBlocks.GLYPHID_SPAWNER.get()).build(null));
    public static final RegistryObject<BlockEntityType<RBMKBaseEntityBE>> RBMK_BASE_ENTITY =
            REGISTER.register("rbmk_base_entity", () -> BlockEntityType.Builder.of(RBMKBaseEntityBE::new, ModBlocks.machine_rbmk_base.get()).build(null));
    public static final RegistryObject<BlockEntityType<RBMKHeaterEntityBE>> RBMK_HEATER_ENTITY =
            REGISTER.register("rbmk_heater_entity", () -> BlockEntityType.Builder.of(RBMKHeaterEntityBE::new, ModBlocks.machine_rbmk_heater.get()).build(null));
    public static final RegistryObject<BlockEntityType<RBMKFuelChannelEntityBE>> RBMK_FUEL_CHANNEL_ENTITY =
            REGISTER.register("rbmk_fuel_channel_entity", () -> BlockEntityType.Builder.of(RBMKFuelChannelEntityBE::new, ModBlocks.machine_rbmk_fuel_channel.get()).build(null));
    public static final RegistryObject<BlockEntityType<RBMKControlRodEntityBE>> RBMK_CONTROL_ROD_ENTITY =
            REGISTER.register("rbmk_control_rod_entity", () -> BlockEntityType.Builder.of(RBMKControlRodEntityBE::new,
                    ModBlocks.machine_rbmk_control_rod.get(),
                    ModBlocks.machine_rbmk_control_auto.get()).build(null));
    public static final RegistryObject<BlockEntityType<RBMKBoilerEntityBE>> RBMK_BOILER_ENTITY =
            REGISTER.register("rbmk_boiler_entity", () -> BlockEntityType.Builder.of(RBMKBoilerEntityBE::new, ModBlocks.machine_rbmk_boiler.get()).build(null));
    public static final RegistryObject<BlockEntityType<RBMKOutgasserEntityBE>> RBMK_OUTGASSER_ENTITY =
            REGISTER.register("rbmk_outgasser_entity", () -> BlockEntityType.Builder.of(RBMKOutgasserEntityBE::new, ModBlocks.machine_rbmk_outgasser.get()).build(null));
    public static final RegistryObject<BlockEntityType<RBMKStorageEntityBE>> RBMK_STORAGE_ENTITY =
            REGISTER.register("rbmk_storage_entity", () -> BlockEntityType.Builder.of(RBMKStorageEntityBE::new, ModBlocks.machine_rbmk_storage.get()).build(null));
    public static final RegistryObject<BlockEntityType<RBMKCoolerEntityBE>> RBMK_COOLER_ENTITY =
            REGISTER.register("rbmk_cooler_entity", () -> BlockEntityType.Builder.of(RBMKCoolerEntityBE::new, ModBlocks.machine_rbmk_cooler.get()).build(null));
    public static final RegistryObject<BlockEntityType<RBMKPeripheralEntityBE>> RBMK_PERIPHERAL_ENTITY =
            REGISTER.register("rbmk_peripheral_entity", () -> BlockEntityType.Builder.of(RBMKPeripheralEntityBE::new,
                    ModBlocks.machine_rbmk_console.get(),
                    ModBlocks.machine_rbmk_debris.get(),
                    ModBlocks.machine_rbmk_crane_console.get(),
                    ModBlocks.machine_rbmk_autoloader.get()
            ).build(null));
    public static final RegistryObject<BlockEntityType<RBMKDisplayEntityBE>> RBMK_DISPLAY_ENTITY =
            REGISTER.register("rbmk_display_entity", () -> BlockEntityType.Builder.of(RBMKDisplayEntityBE::new,
                    ModBlocks.machine_rbmk_display.get()).build(null));
    public static final RegistryObject<BlockEntityType<RBMKGraphEntityBE>> RBMK_GRAPH_ENTITY =
            REGISTER.register("rbmk_graph_entity", () -> BlockEntityType.Builder.of(RBMKGraphEntityBE::new,
                    ModBlocks.machine_rbmk_graph.get()).build(null));
    public static final RegistryObject<BlockEntityType<RBMKNumitronEntityBE>> RBMK_NUMITRON_ENTITY =
            REGISTER.register("rbmk_numitron_entity", () -> BlockEntityType.Builder.of(RBMKNumitronEntityBE::new,
                    ModBlocks.machine_rbmk_numitron.get()).build(null));
    public static final RegistryObject<BlockEntityType<RBMKKeypadEntityBE>> RBMK_KEYPAD_ENTITY =
            REGISTER.register("rbmk_keypad_entity", () -> BlockEntityType.Builder.of(RBMKKeypadEntityBE::new,
                    ModBlocks.machine_rbmk_keypad.get()).build(null));
    public static final RegistryObject<BlockEntityType<RBMKGaugeEntityBE>> RBMK_GAUGE_ENTITY =
            REGISTER.register("rbmk_gauge_entity", () -> BlockEntityType.Builder.of(RBMKGaugeEntityBE::new,
                    ModBlocks.machine_rbmk_gauge.get()).build(null));
    public static final RegistryObject<BlockEntityType<RBMKRadioControllerEntityBE>> RBMK_RADIO_CONTROLLER_ENTITY =
            REGISTER.register("rbmk_radio_controller_entity", () -> BlockEntityType.Builder.of(RBMKRadioControllerEntityBE::new,
                    ModBlocks.radio_torch_controller.get()).build(null));
    public static final RegistryObject<BlockEntityType<RBMKSteamPortEntityBE>> RBMK_STEAM_PORT_ENTITY =
            REGISTER.register("rbmk_steam_port_entity", () -> BlockEntityType.Builder.of(RBMKSteamPortEntityBE::new,
                    ModBlocks.rbmk_steam_inlet.get(),
                    ModBlocks.rbmk_steam_outlet.get()).build(null));
    public static final RegistryObject<BlockEntityType<TokamakControllerBE>> TOKAMAK_CONTROLLER =
            REGISTER.register("tokamak_controller", () -> BlockEntityType.Builder.of(TokamakControllerBE::new, ModBlocks.tokamak_controller.get()).build(null));
    public static final RegistryObject<BlockEntityType<PWRControllerBE>> PWR_CONTROLLER_ENTITY =
            REGISTER.register("pwr_controller_entity", () -> BlockEntityType.Builder.of(PWRControllerBE::new, ModBlocks.pwr_controller.get()).build(null));
    public static final RegistryObject<BlockEntityType<PWRBlockEntity>> PWR_BLOCK_ENTITY =
            REGISTER.register("pwr_block_entity", () -> BlockEntityType.Builder.of(PWRBlockEntity::new, ModBlocks.pwr_block.get()).build(null));
    public static final RegistryObject<BlockEntityType<com.hbm.blockentity.machine.tokamak.TokamakPortBlockEntity>> TOKAMAK_PORT_ENTITY =
            REGISTER.register("tokamak_port_entity", () -> BlockEntityType.Builder.of(com.hbm.blockentity.machine.tokamak.TokamakPortBlockEntity::new, ModBlocks.tokamak_port.get()).build(null));
    public static final RegistryObject<BlockEntityType<com.hbm.blockentity.machine.tokamak.TokamakInjectorBlockEntity>> TOKAMAK_INJECTOR_ENTITY =
            REGISTER.register("tokamak_injector_entity", () -> BlockEntityType.Builder.of(com.hbm.blockentity.machine.tokamak.TokamakInjectorBlockEntity::new, ModBlocks.tokamak_injector.get()).build(null));
    public static final RegistryObject<BlockEntityType<ZirnoxReactorBE>> ZIRNOX_REACTOR_ENTITY =
            REGISTER.register("zirnox_reactor_entity", () -> BlockEntityType.Builder.of(ZirnoxReactorBE::new, ModBlocks.machine_zirnox.get()).build(null));
    public static final RegistryObject<BlockEntityType<ICFReactorBE>> ICF_REACTOR_ENTITY =
            REGISTER.register("icf_reactor_entity", () -> BlockEntityType.Builder.of(ICFReactorBE::new, ModBlocks.machine_icf.get()).build(null));
    public static final RegistryObject<BlockEntityType<ICFControllerBE>> ICF_CONTROLLER_ENTITY =
            REGISTER.register("icf_controller_entity", () -> BlockEntityType.Builder.of(ICFControllerBE::new, ModBlocks.machine_icf_controller.get()).build(null));
    public static final RegistryObject<BlockEntityType<ICFPressBE>> ICF_PRESS_ENTITY =
            REGISTER.register("icf_press_entity", () -> BlockEntityType.Builder.of(ICFPressBE::new, ModBlocks.machine_icf_press.get()).build(null));
    public static final RegistryObject<BlockEntityType<ResearchReactorBE>> RESEARCH_REACTOR_ENTITY =
            REGISTER.register("research_reactor_entity", () -> BlockEntityType.Builder.of(ResearchReactorBE::new, ModBlocks.machine_research_reactor.get()).build(null));
    public static final RegistryObject<BlockEntityType<BreederReactorBE>> BREEDER_REACTOR_ENTITY =
            REGISTER.register("breeder_reactor_entity", () -> BlockEntityType.Builder.of(BreederReactorBE::new, ModBlocks.machine_reactor_breeding.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileSpaceStation>> TILE_SPACE_STATION =
            REGISTER.register("tile_space_station", () -> BlockEntityType.Builder.of(TileSpaceStation::new, ModBlocks.SPACE_STATION_BASE.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileConnector>> TILE_CONNECTOR =
            REGISTER.register("tile_connector", () -> BlockEntityType.Builder.of(TileConnector::new, ModBlocks.CONNECTOR.get()).build(null));
//    public static final RegistryObject<BlockEntityType<TileFireboxBase>> TILE_FIREBOX = register("tile_firebox", TileFireBox::new, ModBlocks.HEATER_FIREBOX);
    public static final RegistryObject<BlockEntityType<TileFoundryMold>> TILE_FOUNDRY_MOLD = register("tile_foundrymold", TileFoundryMold::new, ModBlocks.FOUNDRY_MOLD);
    public static final RegistryObject<BlockEntityType<TileConveyor>> TILE_CONVEYOR = register("tile_conveyor", TileConveyor::new, ModBlocks.conveyor);
    public static final RegistryObject<BlockEntityType<TileConveyorExtractor>> TILE_CONVEYOR_EXTRACTOR = register("tile_conveyor_extractor", TileConveyorExtractor::new, ModBlocks.CONVEYOR_EXTRACTOR);
    public static final RegistryObject<BlockEntityType<TileConveyorInserter>> TILE_CONVEYOR_INSERTER = register("tile_conveyor_inserter", TileConveyorInserter::new, ModBlocks.CONVEYOR_INSERTER);
    public static final RegistryObject<BlockEntityType<TileConveyorRouter>> TILE_CONVEYOR_ROUTER = register("tile_conveyor_router", TileConveyorRouter::new, ModBlocks.CONVEYOR_ROUTER);
    public static final RegistryObject<BlockEntityType<TileMinerLarge>> TILE_MINER_LARGE = register("tile_miner_large", TileMinerLarge::new, ModBlocks.MINER_LARGE);
    @Deprecated
    public static final RegistryObject<BlockEntityType<BedRockOreTE.TileBedrockOre>> TILE_BEDROCK_ORE = register("tile_bedrock_ore", BedRockOreTE.TileBedrockOre::new, ModBlocks.DEPTH_STONE);
    // 注册函数
    public static<T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String key, BlockEntityType.BlockEntitySupplier<T> pFactory, Supplier<Block>... pValidBlocks){
        var registerObj = REGISTER.register(key, () -> BlockEntityType.Builder.of(pFactory, Arrays.stream(pValidBlocks).map(Supplier::get).toArray(Block[]::new)).build(null));
        tileTypes.put(key, registerObj);
        return registerObj;
    }

    public static void registerBus(IEventBus bus){
        ModBlocks.tileSupport();
        PROXY_ENTITY = REGISTER.register("proxy_entity",()-> BlockEntityType.Builder.of(TileProxyCombo::new,
                combine(new ArrayList<>(dummyableBlocks.stream().map(Supplier::get).toList()),
                        ModBlocks.machine_crucible.get(), ModBlocks.machine_assembler.get(), ModBlocks.machine_cracking_tower.get(), ModBlocks.MACHINE_CHEMPLANT.get(),
                        ModBlocks.LAUNCH_PAD.get(),
//                        ModBlocks.bomb_boy.get(), ModBlocks.bomb_custom.get(), ModBlocks.bomb_fat_man.get(),
                        ModBlocks.machine_zirnox.get(),
                        ModBlocks.SPACE_STATION_BASE.get(), ModBlocks.HEATER_FIREBOX.get())
        ).build(null));

        PROXY = REGISTER.register("proxy",()-> BlockEntityType.Builder.of(BEProxy::new, dummyableBlocks.stream().map(Supplier::get).toArray(Block[]::new)).build(null));
        REGISTER.register(bus);
    }

    // 工具
    private static Block[] combine(List<Block> blocks, Block ... blockList){
        blocks.addAll(List.of(blockList));
        return blocks.toArray(Block[]::new);
    }

    public static BlockEntityType<?> getTypeById(String id){
        return tileTypes.get("tile_" + id).get();
    }

    public static String getId(String s){
        return "tile_" + s;
    }
}
