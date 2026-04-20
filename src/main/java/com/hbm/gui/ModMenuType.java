package com.hbm.gui;

import com.hbm.HBM;
import com.hbm.gui.menu.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuType {
    public static final DeferredRegister<MenuType<?>> MOD_MENU_TYPES = DeferredRegister.create(Registries.MENU, HBM.MODID);
    public static final RegistryObject<MenuType<DifurnaceMenu>> DIFURNACE_MENU =
            MOD_MENU_TYPES.register("difurnace_menu",()->new MenuType<>(DifurnaceMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<PressMenu>> PRESS_MENU =
            MOD_MENU_TYPES.register("press_menu",()->IForgeMenuType.create(PressMenu::new));
    public static final RegistryObject<MenuType<BatteryMenu>> BATTERY_MENU =
            MOD_MENU_TYPES.register("battery_menu",()->new MenuType<>(BatteryMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<AssemblerMenu>> ASSEMBLER_MENU = register("menu_assembler", AssemblerMenu::new);
//            MOD_MENU_TYPES.register("assembler_menu",()->new MenuType<>(AssemblerMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<ChemplantMenu>> CHEMPLANT_MENU =
            MOD_MENU_TYPES.register("chemplant_menu", ()->IForgeMenuType.create((windowId, inv, data) -> (ChemplantMenu) ITileAccess.getInstance(windowId,inv,data, ChemplantMenu.class)));
    public static final RegistryObject<MenuType<BarrelMenu>> BARREL_MENU =
            MOD_MENU_TYPES.register("barrel_menu",()->IForgeMenuType.create((windowId, inv, data) -> (BarrelMenu) ITileAccess.getInstance(windowId,inv,data, BarrelMenu.class)));
    public static final RegistryObject<MenuType<GasTurbineMenu>> GAS_TURBINE_MENU =
            MOD_MENU_TYPES.register("gas_turbine_menu", () -> IForgeMenuType.create((windowId, inv, data) -> (GasTurbineMenu) ITileAccess.getInstance(windowId, inv, data, GasTurbineMenu.class)));
    public static final RegistryObject<MenuType<ElectricFurnaceMenu>> ELECTRIC_FURNACE_MENU =
            MOD_MENU_TYPES.register("electric_furnace_menu",()->new MenuType<>(ElectricFurnaceMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<LaunchPadMenu>> LAUNCH_PAD_MENU =
            MOD_MENU_TYPES.register("launch_pad_menu",()->new MenuType<>(LaunchPadMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<ShredderMenu>> SHREDDER_MENU =
            MOD_MENU_TYPES.register("shredder_menu",()->new MenuType<>(ShredderMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<TokamakMenu>> TOKAMAK_MENU =
            MOD_MENU_TYPES.register("tokamak_menu",()->new MenuType<>(TokamakMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<PWRMenu>> PWR_MENU =
            MOD_MENU_TYPES.register("pwr_menu", () -> new MenuType<>(PWRMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<ZirnoxMenu>> ZIRNOX_MENU =
            MOD_MENU_TYPES.register("zirnox_menu", () -> new MenuType<>(ZirnoxMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<RBMKBaseMenu>> RBMK_BASE_MENU =
            MOD_MENU_TYPES.register("rbmk_base_menu",()->new MenuType<>(RBMKBaseMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<RBMKFuelChannelMenu>> RBMK_FUEL_CHANNEL_MENU =
            MOD_MENU_TYPES.register("rbmk_fuel_channel_menu", () -> IForgeMenuType.create(RBMKFuelChannelMenu::new));
    public static final RegistryObject<MenuType<RBMKControlRodMenu>> RBMK_CONTROL_ROD_MENU =
            MOD_MENU_TYPES.register("rbmk_control_rod_menu", () -> new MenuType<>(RBMKControlRodMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<RBMKPeripheralMenu>> RBMK_PERIPHERAL_MENU =
            MOD_MENU_TYPES.register("rbmk_peripheral_menu", () -> IForgeMenuType.create(RBMKPeripheralMenu::new));
    public static final RegistryObject<MenuType<RBMKAutoloaderMenu>> RBMK_AUTOLOADER_MENU =
            MOD_MENU_TYPES.register("rbmk_autoloader_menu", () -> IForgeMenuType.create(RBMKAutoloaderMenu::new));
    public static final RegistryObject<MenuType<RBMKKeypadConfigMenu>> RBMK_KEYPAD_CONFIG_MENU =
            MOD_MENU_TYPES.register("rbmk_keypad_config_menu", () -> IForgeMenuType.create(RBMKKeypadConfigMenu::new));
    public static final RegistryObject<MenuType<RBMKGaugeConfigMenu>> RBMK_GAUGE_CONFIG_MENU =
            MOD_MENU_TYPES.register("rbmk_gauge_config_menu", () -> IForgeMenuType.create(RBMKGaugeConfigMenu::new));
    public static final RegistryObject<MenuType<RBMKRadioControllerMenu>> RBMK_RADIO_CONTROLLER_MENU =
            MOD_MENU_TYPES.register("rbmk_radio_controller_menu", () -> IForgeMenuType.create(RBMKRadioControllerMenu::new));
    public static final RegistryObject<MenuType<IronCrateMenu>> IRON_CRATE_MENU =
            MOD_MENU_TYPES.register("iron_crate_menu", () -> IForgeMenuType.create(IronCrateMenu::new));
    public static final RegistryObject<MenuType<SteelCrateMenu>> STEEL_CRATE_MENU =
            MOD_MENU_TYPES.register("steel_crate_menu", () -> IForgeMenuType.create(SteelCrateMenu::new));
    public static final RegistryObject<MenuType<WoodBurnerMenu>> WOOD_BURNER_MENU =
            MOD_MENU_TYPES.register("wood_burner_menu", () -> IForgeMenuType.create(WoodBurnerMenu::new));
    public static final RegistryObject<MenuType<ICFMenu>> ICF_MENU =
            MOD_MENU_TYPES.register("icf_menu", () -> new MenuType<>(ICFMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<ICFPressMenu>> ICF_PRESS_MENU =
            MOD_MENU_TYPES.register("icf_press_menu", () -> new MenuType<>(ICFPressMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<com.hbm.gui.menu.ResearchReactorMenu>> RESEARCH_REACTOR_MENU =
            MOD_MENU_TYPES.register("research_reactor_menu", () -> new MenuType<>(com.hbm.gui.menu.ResearchReactorMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<com.hbm.gui.menu.BreederReactorMenu>> BREEDER_REACTOR_MENU =
            MOD_MENU_TYPES.register("breeder_reactor_menu", () -> new MenuType<>(com.hbm.gui.menu.BreederReactorMenu::new, FeatureFlags.VANILLA_SET));
    public static final RegistryObject<MenuType<MenuFirebox>> MENU_FIREBOX = register("menu_firebox", MenuFirebox::new);
    public static final RegistryObject<MenuType<MenuCrucible>> MENU_CRUCIBLE = register("menu_crucible", MenuCrucible::new);
    public static final RegistryObject<MenuType<MenuConveyorExtractor>> MENU_CONVEYOR_EXTRACTOR = register("menu_conveyor_extractor", MenuConveyorExtractor::new);
    public static final RegistryObject<MenuType<MenuConveyorInserter>> MENU_CONVEYOR_INSERTER = register("menu_conveyor_inserter", MenuConveyorInserter::new);
    public static final RegistryObject<MenuType<MenuConveyorRouter>> MENU_CONVEYOR_ROUTER = register("menu_conveyor_router", MenuConveyorRouter::new);
    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String key, IContainerFactory<T> factory){
        return MOD_MENU_TYPES.register(key, () -> IForgeMenuType.create(factory));
    }
}
