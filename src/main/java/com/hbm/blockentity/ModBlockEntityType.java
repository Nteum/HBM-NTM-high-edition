package com.hbm.blockentity;

import com.hbm.block.HBMMachine;
import com.hbm.block.env.GlyphidSpawner;
import com.hbm.blockentity.base2.TileProxyCombo;
import com.hbm.blockentity.machine.*;
import com.hbm.blockentity.machine.IronCrateBlockEntity;
import com.hbm.blockentity.machine.SteelCrateBlockEntity;
import com.hbm.HBM;
import com.hbm.blockentity.tools.TileEntityGeiger;
import com.hbm.blockentity.machine.pile.ChicagoBreederBlockEntity;
import com.hbm.blockentity.machine.pile.ChicagoDetectorBlockEntity;
import com.hbm.blockentity.machine.pile.ChicagoFuelBlockEntity;
import com.hbm.blockentity.machine.pile.ChicagoSourceBlockEntity;
import com.hbm.blockentity.machine.rbmk.RBMKBaseEntity;
import com.hbm.blockentity.machine.rbmk.RBMKControlRodEntity;
import com.hbm.blockentity.machine.rbmk.RBMKFuelChannelEntity;
import com.hbm.blockentity.machine.rbmk.RBMKHeaterEntity;
import com.hbm.blockentity.machine.rbmk.RBMKPeripheralEntity;
import com.hbm.blockentity.tools.TileFoundryMold;
import com.hbm.blockentity.weapon.*;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.function.Supplier;

public class ModBlockEntityType {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, HBM.MODID);

    public static final RegistryObject<BlockEntityType<DifurnaceEntity>> DIFURNACE_ENTITY =
            REGISTER.register("difurnace_entity",()-> BlockEntityType.Builder.of(DifurnaceEntity::new, ModBlocks.machine_difurnace.get()).build(null));
    public static final RegistryObject<BlockEntityType<PressEntity>> PRESS_ENTITY =
            REGISTER.register("press_entity",()-> BlockEntityType.Builder.of(PressEntity::new, ModBlocks.machine_press.get()).build(null));
    public static final RegistryObject<BlockEntityType<NukeBombFatEntity>> NUKE_BOMB_FAT_ENTITY =
            REGISTER.register("nuke_bomb_entity",()-> BlockEntityType.Builder.of(NukeBombFatEntity::new, ModBlocks.bomb_fat_man.get()).build(null));
    public static final RegistryObject<BlockEntityType<NukeBombBoyEntity>> NUKE_BOMB_BOY_ENTITY =
            REGISTER.register("nuke_bomb_boy",()-> BlockEntityType.Builder.of(NukeBombBoyEntity::new, ModBlocks.bomb_boy.get()).build(null));
    public static final RegistryObject<BlockEntityType<NukeBombCustomEntity>> NUKE_BOMB_CUSTOM_ENTITY =
            REGISTER.register("nuke_bomb_custom",()-> BlockEntityType.Builder.of(NukeBombCustomEntity::new, ModBlocks.bomb_custom.get()).build(null));
    public static final RegistryObject<BlockEntityType<AssemblerEntity>> ASSEMBLER_ENTITY =
            REGISTER.register("assembler_entity",()-> BlockEntityType.Builder.of(AssemblerEntity::new, ModBlocks.machine_assembler.get()).build(null));
    public static final RegistryObject<BlockEntityType<CrucibleEntity>> CRUCIBLE_ENTITY =
            REGISTER.register("crucible_entity",()-> BlockEntityType.Builder.of(CrucibleEntity::new, ModBlocks.machine_crucible.get()).build(null));
//    public static final RegistryObject<BlockEntityType<BedRockOre.BedRockOreEntity>> BEDROCK_ORE_ENTITY =
//            REGISTER.register("bedrock_ore_entity",()-> BlockEntityType.Builder.of(BedRockOre.BedRockOreEntity::new, ModBlocks.BEDROCK_ORE.get()).build(null));
    public static final RegistryObject<BlockEntityType<CableEntity>> CABLE_ENTITY =
            REGISTER.register("cable_entity",()-> BlockEntityType.Builder.of(CableEntity::new, ModBlocks.RED_CABLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<PipeEntity>> PIPE_ENTITY =
            REGISTER.register("pipe_entity",()-> BlockEntityType.Builder.of(PipeEntity::new, HBMMachine.FLUID_PIPE.get()).build(null));
    public static final RegistryObject<BlockEntityType<BatteryEntity>> BATTERY_ENTITY =
            REGISTER.register("battery_entity",()-> BlockEntityType.Builder.of(BatteryEntity::new, ModBlocks.machine_battery.get(),ModBlocks.machine_lithium_battery.get(),ModBlocks.machine_dineutronium_battery.get(),ModBlocks.machine_schrabidium_battery.get()).build(null));
    public static final RegistryObject<BlockEntityType<LaunchPadTileEntity>> LAUNCHPAD_ENTITY =
            REGISTER.register("launchpad_entity",()-> BlockEntityType.Builder.of(LaunchPadTileEntity::new, HBMMachine.LAUNCH_PAD.get()).build(null));

    public static final RegistryObject<BlockEntityType<ElectricFurnaceEntity>> ELECTRIC_FURNACE_ENTITY =
            REGISTER.register("electric_furnace_entity",()-> BlockEntityType.Builder.of(ElectricFurnaceEntity::new, ModBlocks.machine_electric_furnace.get()).build(null));
    public static final RegistryObject<BlockEntityType<BoilerEntity>> BOILER_ENTITY =
            REGISTER.register("boiler_entity",()-> BlockEntityType.Builder.of(BoilerEntity::new, ModBlocks.machine_boiler.get()).build(null));
    public static final RegistryObject<BlockEntityType<ElectricBoilerEntity>> ELECTRIC_BOILER_ENTITY =
            REGISTER.register("electric_boiler_entity",()-> BlockEntityType.Builder.of(ElectricBoilerEntity::new, ModBlocks.machine_electric_boiler.get()).build(null));
    public static final RegistryObject<BlockEntityType<NuclearBoilerEntity>> NUCLEAR_BOILER_ENTITY =
            REGISTER.register("nuclear_boiler_entity",()-> BlockEntityType.Builder.of(NuclearBoilerEntity::new, ModBlocks.machine_nuclear_boiler.get()).build(null));
    public static final RegistryObject<BlockEntityType<ChemplantEntity>> CHEMPLANT_ENTITY =
            REGISTER.register("chemplant_entity",()-> BlockEntityType.Builder.of(ChemplantEntity::new, HBMMachine.CHEMPLANT.get()).build(null));
    public static final RegistryObject<BlockEntityType<BarrelEntity>> BARREL_ENTITY =
            REGISTER.register("barrel_entity",()-> BlockEntityType.Builder.of(BarrelEntity::new, HBMMachine.PLASTIC_BARREL.get(),HBMMachine.CORRODED_BARREL.get(),HBMMachine.IRON_BARREL.get(),HBMMachine.STEEL_BARREL.get(),HBMMachine.TCALLOY_BARREL.get(),HBMMachine.ANTIMATTER_BARREL.get()).build(null));
    public static final RegistryObject<BlockEntityType<CondenserBlockEntity>> CONDENSER_ENTITY =
            REGISTER.register("condenser_entity", () -> BlockEntityType.Builder.of(CondenserBlockEntity::new, ModBlocks.machine_condenser.get()).build(null));
    public static final RegistryObject<BlockEntityType<CoolingTowerBlockEntity>> COOLING_TOWER_ENTITY =
            REGISTER.register("cooling_tower_entity", () -> BlockEntityType.Builder.of(CoolingTowerBlockEntity::new, ModBlocks.machine_cooling_tower.get()).build(null));
    public static final RegistryObject<BlockEntityType<GasTurbineBlockEntity>> GAS_TURBINE_ENTITY =
            REGISTER.register("gas_turbine_entity", () -> BlockEntityType.Builder.of(GasTurbineBlockEntity::new, ModBlocks.machine_turbine_gas.get()).build(null));
    public static final RegistryObject<BlockEntityType<ChicagoFuelBlockEntity>> CHICAGO_FUEL =
            REGISTER.register("chicago_fuel", () -> BlockEntityType.Builder.of(ChicagoFuelBlockEntity::new, ModBlocks.chicago_graphite_fuel.get()).build(null));
    public static final RegistryObject<BlockEntityType<ChicagoSourceBlockEntity>> CHICAGO_SOURCE =
            REGISTER.register("chicago_source", () -> BlockEntityType.Builder.of(ChicagoSourceBlockEntity::new, ModBlocks.chicago_graphite_source.get()).build(null));
    public static final RegistryObject<BlockEntityType<ChicagoBreederBlockEntity>> CHICAGO_BREEDER =
            REGISTER.register("chicago_breeder", () -> BlockEntityType.Builder.of(ChicagoBreederBlockEntity::new, ModBlocks.chicago_graphite_breeder.get()).build(null));
    public static final RegistryObject<BlockEntityType<ChicagoDetectorBlockEntity>> CHICAGO_DETECTOR =
            REGISTER.register("chicago_detector", () -> BlockEntityType.Builder.of(ChicagoDetectorBlockEntity::new, ModBlocks.chicago_graphite_detector.get()).build(null));
    public static final RegistryObject<BlockEntityType<ShredderEntity>> SHREDDER_ENTITY =
            REGISTER.register("shredder_entity",()-> BlockEntityType.Builder.of(ShredderEntity::new, ModBlocks.machine_shredder.get()).build(null));
    public static final RegistryObject<BlockEntityType<WoodBurnerBlockEntity>> WOOD_BURNER_ENTITY =
            REGISTER.register("wood_burner_entity", () -> BlockEntityType.Builder.of(WoodBurnerBlockEntity::new, ModBlocks.machine_wood_burner.get()).build(null));
    public static final RegistryObject<BlockEntityType<CrackingTowerEntity>> CRACKING_TOWER_ENTITY =
            REGISTER.register("cracking_tower_entity", () -> BlockEntityType.Builder.of(CrackingTowerEntity::new, ModBlocks.machine_cracking_tower.get()).build(null));
    public static final RegistryObject<BlockEntityType<IronCrateBlockEntity>> IRON_CRATE_ENTITY =
            REGISTER.register("iron_crate_entity", () -> BlockEntityType.Builder.of(IronCrateBlockEntity::new, ModBlocks.crate_iron.get()).build(null));
    public static final RegistryObject<BlockEntityType<SteelCrateBlockEntity>> STEEL_CRATE_ENTITY =
            REGISTER.register("steel_crate_entity", () -> BlockEntityType.Builder.of(SteelCrateBlockEntity::new, ModBlocks.crate_steel.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileProxyCombo>> PROXY_ENTITY =
            REGISTER.register("proxy_entity",()-> BlockEntityType.Builder.of(TileProxyCombo::new,
                    ModBlocks.machine_crucible.get(), ModBlocks.machine_assembler.get(), ModBlocks.machine_cracking_tower.get(), HBMMachine.CHEMPLANT.get(),
                    HBMMachine.LAUNCH_PAD.get(), ModBlocks.bomb_boy.get(), ModBlocks.bomb_custom.get(), ModBlocks.bomb_fat_man.get(), ModBlocks.machine_zirnox.get(),
                    ModBlocks.SPACE_STATION_BASE.get(), ModBlocks.HEATER_FIREBOX.get()
            ).build(null));
    public static final RegistryObject<BlockEntityType<TileEntityGeiger>> GEIGER_COUNTER =
            REGISTER.register("geiger_counter",()-> BlockEntityType.Builder.of(TileEntityGeiger::new, HBMMachine.GEIGER_COUNTER.get()).build(null));
    public static final RegistryObject<BlockEntityType<GlyphidSpawner.GlyphidSpawnerEntity>> GLYPHID_SPAWNER =
            REGISTER.register("glyphid_spawner",()-> BlockEntityType.Builder.of(GlyphidSpawner.GlyphidSpawnerEntity::new, ModBlocks.GLYPHID_SPAWNER.get()).build(null));
    public static final RegistryObject<BlockEntityType<RBMKBaseEntity>> RBMK_BASE_ENTITY =
            REGISTER.register("rbmk_base_entity", () -> BlockEntityType.Builder.of(RBMKBaseEntity::new, ModBlocks.machine_rbmk_base.get()).build(null));
    public static final RegistryObject<BlockEntityType<RBMKHeaterEntity>> RBMK_HEATER_ENTITY =
            REGISTER.register("rbmk_heater_entity", () -> BlockEntityType.Builder.of(RBMKHeaterEntity::new, ModBlocks.machine_rbmk_heater.get()).build(null));
    public static final RegistryObject<BlockEntityType<RBMKFuelChannelEntity>> RBMK_FUEL_CHANNEL_ENTITY =
            REGISTER.register("rbmk_fuel_channel_entity", () -> BlockEntityType.Builder.of(RBMKFuelChannelEntity::new, ModBlocks.machine_rbmk_fuel_channel.get()).build(null));
    public static final RegistryObject<BlockEntityType<RBMKControlRodEntity>> RBMK_CONTROL_ROD_ENTITY =
            REGISTER.register("rbmk_control_rod_entity", () -> BlockEntityType.Builder.of(RBMKControlRodEntity::new, ModBlocks.machine_rbmk_control_rod.get()).build(null));
    public static final RegistryObject<BlockEntityType<RBMKPeripheralEntity>> RBMK_PERIPHERAL_ENTITY =
            REGISTER.register("rbmk_peripheral_entity", () -> BlockEntityType.Builder.of(RBMKPeripheralEntity::new,
                    ModBlocks.machine_rbmk_console.get(),
                    ModBlocks.machine_rbmk_element.get(),
                    ModBlocks.machine_rbmk_reflector.get(),
                    ModBlocks.machine_rbmk_debris.get(),
                    ModBlocks.machine_rbmk_crane_console.get(),
                    ModBlocks.machine_rbmk_autoloader.get()
            ).build(null));
    public static final RegistryObject<BlockEntityType<com.hbm.blockentity.machine.tokamak.TokamakControllerBlockEntity>> TOKAMAK_CONTROLLER =
            REGISTER.register("tokamak_controller", () -> BlockEntityType.Builder.of(com.hbm.blockentity.machine.tokamak.TokamakControllerBlockEntity::new, ModBlocks.tokamak_controller.get()).build(null));
    public static final RegistryObject<BlockEntityType<PWRControllerBlockEntity>> PWR_CONTROLLER_ENTITY =
            REGISTER.register("pwr_controller_entity", () -> BlockEntityType.Builder.of(PWRControllerBlockEntity::new, ModBlocks.pwr_controller.get()).build(null));
    public static final RegistryObject<BlockEntityType<PWRBlockEntity>> PWR_BLOCK_ENTITY =
            REGISTER.register("pwr_block_entity", () -> BlockEntityType.Builder.of(PWRBlockEntity::new, ModBlocks.pwr_block.get()).build(null));
    public static final RegistryObject<BlockEntityType<com.hbm.blockentity.machine.tokamak.TokamakPortBlockEntity>> TOKAMAK_PORT_ENTITY =
            REGISTER.register("tokamak_port_entity", () -> BlockEntityType.Builder.of(com.hbm.blockentity.machine.tokamak.TokamakPortBlockEntity::new, ModBlocks.tokamak_port.get()).build(null));
    public static final RegistryObject<BlockEntityType<com.hbm.blockentity.machine.tokamak.TokamakInjectorBlockEntity>> TOKAMAK_INJECTOR_ENTITY =
            REGISTER.register("tokamak_injector_entity", () -> BlockEntityType.Builder.of(com.hbm.blockentity.machine.tokamak.TokamakInjectorBlockEntity::new, ModBlocks.tokamak_injector.get()).build(null));
    public static final RegistryObject<BlockEntityType<ZirnoxReactorBlockEntity>> ZIRNOX_REACTOR_ENTITY =
            REGISTER.register("zirnox_reactor_entity", () -> BlockEntityType.Builder.of(ZirnoxReactorBlockEntity::new, ModBlocks.machine_zirnox.get()).build(null));
    public static final RegistryObject<BlockEntityType<com.hbm.blockentity.machine.icf.ICFReactorBlockEntity>> ICF_REACTOR_ENTITY =
            REGISTER.register("icf_reactor_entity", () -> BlockEntityType.Builder.of(com.hbm.blockentity.machine.icf.ICFReactorBlockEntity::new, ModBlocks.machine_icf.get()).build(null));
    public static final RegistryObject<BlockEntityType<com.hbm.blockentity.machine.icf.ICFControllerBlockEntity>> ICF_CONTROLLER_ENTITY =
            REGISTER.register("icf_controller_entity", () -> BlockEntityType.Builder.of(com.hbm.blockentity.machine.icf.ICFControllerBlockEntity::new, ModBlocks.machine_icf_controller.get()).build(null));
    public static final RegistryObject<BlockEntityType<com.hbm.blockentity.machine.icf.ICFPressBlockEntity>> ICF_PRESS_ENTITY =
            REGISTER.register("icf_press_entity", () -> BlockEntityType.Builder.of(com.hbm.blockentity.machine.icf.ICFPressBlockEntity::new, ModBlocks.machine_icf_press.get()).build(null));
    public static final RegistryObject<BlockEntityType<com.hbm.blockentity.machine.research.ResearchReactorBlockEntity>> RESEARCH_REACTOR_ENTITY =
            REGISTER.register("research_reactor_entity", () -> BlockEntityType.Builder.of(com.hbm.blockentity.machine.research.ResearchReactorBlockEntity::new, ModBlocks.machine_research_reactor.get()).build(null));
    public static final RegistryObject<BlockEntityType<com.hbm.blockentity.machine.research.BreederReactorBlockEntity>> BREEDER_REACTOR_ENTITY =
            REGISTER.register("breeder_reactor_entity", () -> BlockEntityType.Builder.of(com.hbm.blockentity.machine.research.BreederReactorBlockEntity::new, ModBlocks.machine_reactor_breeding.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileSpaceStation>> TILE_SPACE_STATION =
            REGISTER.register("tile_space_station", () -> BlockEntityType.Builder.of(TileSpaceStation::new, ModBlocks.SPACE_STATION_BASE.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileConnector>> TILE_CONNECTOR =
            REGISTER.register("tile_connector", () -> BlockEntityType.Builder.of(TileConnector::new, ModBlocks.CONNECTOR.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileFireboxBase>> TILE_FIREBOX = register("tile_firebox", TileFireBox::new, ModBlocks.HEATER_FIREBOX);
    public static final RegistryObject<BlockEntityType<TileFoundryMold>> TILE_FOUNDRY_MOLD = register("tile_foundrymold", TileFoundryMold::new, ModBlocks.FOUNDRY_MOLD);
    // 注册函数
    private static<T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String key, BlockEntityType.BlockEntitySupplier<T> pFactory, Supplier<Block>... pValidBlocks){
        return REGISTER.register(key, () -> BlockEntityType.Builder.of(pFactory, Arrays.stream(pValidBlocks).map(Supplier::get).toArray(Block[]::new)).build(null));
    }
}
