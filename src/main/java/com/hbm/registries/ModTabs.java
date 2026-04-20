package com.hbm.registries;

import com.hbm.HBMLang;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.item.HBMCombat;
import com.hbm.item.HBMWeapon;
import com.hbm.item.icf.ItemICFPellet;
import com.hbm.item.zirnox.ItemZirnoxRod;
import com.hbm.registries.WrapperRegistry.WrappedBlockRegistry;
import com.hbm.registries.WrapperRegistry.WrappedItemRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.hbm.HBM.MODID;

public class ModTabs {
    //创造模式物品栏注册表
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final ResourceKey<CreativeModeTab> PARTS_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(MODID, "hbm_parts"));
    public static final ResourceKey<CreativeModeTab> CONTROL_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(MODID, "hbm_control"));
    public static final ResourceKey<CreativeModeTab> TEMPLATE_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(MODID, "hbm_template"));
    public static final ResourceKey<CreativeModeTab> BLOCKS_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(MODID, "hbm_blocks"));
    public static final ResourceKey<CreativeModeTab> MACHINE_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(MODID, "hbm_machines"));
    public static final ResourceKey<CreativeModeTab> NUKE_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(MODID, "hbm_nuke"));
    public static final ResourceKey<CreativeModeTab> MISSILE_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(MODID, "hbm_missiles"));
    public static final ResourceKey<CreativeModeTab> WEAPON_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(MODID, "hbm_weapons"));
    public static final ResourceKey<CreativeModeTab> CONSUMABLE_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(MODID, "hbm_consumable"));

    public static final RegistryObject<CreativeModeTab> PARTS = CREATIVE_MODE_TABS.register("hbm_parts", () -> CreativeModeTab.builder()
            .title(Component.translatable(HBMLang.HBM_PARTS.key()))
            .icon(() -> ModItems.INGOT_URANIUM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                addItemsForTab(output, PARTS_KEY);
                output.accept(ModItems.UPGRADE_BASE.get());
                output.accept(ModItems.UPGRADE_BASE.get());
                output.accept(ModItems.WOOD_ASH_POWDER.get());
                for (com.hbm.reactor.pwr.PWRFuelType type : com.hbm.reactor.pwr.PWRFuelType.values()) {
                    output.accept(com.hbm.item.pwr.ItemPWRFuel.createStack(ModItems.pwr_fuel.get(), type));
                    output.accept(com.hbm.item.pwr.ItemPWRFuel.createStack(ModItems.pwr_fuel_hot.get(), type));
                    output.accept(com.hbm.item.pwr.ItemPWRFuel.createStack(ModItems.pwr_fuel_depleted.get(), type));
                }
                for (ItemZirnoxRod.ZirnoxRodType type : ItemZirnoxRod.ZirnoxRodType.values()) {
                    output.accept(ItemZirnoxRod.createStack(ModItems.rod_zirnox.get(), type));
                }
                output.accept(ModItems.rod_zirnox_empty.get());
                output.accept(ModItems.rod_zirnox_tritium.get());
                output.accept(ModItems.rod_zirnox_natural_uranium_fuel_depleted.get());
                output.accept(ModItems.rod_zirnox_uranium_fuel_depleted.get());
                output.accept(ModItems.rod_zirnox_thorium_fuel_depleted.get());
                output.accept(ModItems.rod_zirnox_mox_fuel_depleted.get());
                output.accept(ModItems.rod_zirnox_plutonium_fuel_depleted.get());
                output.accept(ModItems.rod_zirnox_u233_fuel_depleted.get());
                output.accept(ModItems.rod_zirnox_u235_fuel_depleted.get());
                output.accept(ModItems.rod_zirnox_les_fuel_depleted.get());
                output.accept(ModItems.rod_zirnox_zfb_mox_depleted.get());
                output.accept(ModItems.rod_empty.get());
                output.accept(ModItems.rod_dual_empty.get());
                output.accept(ModItems.rod_quad_empty.get());
                for (com.hbm.item.research.ItemBreedingRod.RodType type : com.hbm.item.research.ItemBreedingRod.RodType.values()) {
                    output.accept(com.hbm.item.research.ItemBreedingRod.createStack(com.hbm.item.research.ItemBreedingRod.RodForm.SINGLE, type));
                    output.accept(com.hbm.item.research.ItemBreedingRod.createStack(com.hbm.item.research.ItemBreedingRod.RodForm.DUAL, type));
                    output.accept(com.hbm.item.research.ItemBreedingRod.createStack(com.hbm.item.research.ItemBreedingRod.RodForm.QUAD, type));
                }
            })
            .build());
    public static final RegistryObject<CreativeModeTab> CONTROL = CREATIVE_MODE_TABS.register("hbm_control", () -> CreativeModeTab.builder()
            .title(Component.translatable(HBMLang.HBM_CONTROL.key()))
            .icon(() -> ModItems.RTG_UNIT.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                addItemsForTab(output, CONTROL_KEY);
                addToolItems(output);
                output.accept(ModBlocks.crate_iron.get());
                output.accept(ModBlocks.crate_steel.get());
                for (ModFluids.FluidRegistryHolder registryHolder : ModFluids.fluidList) {
                    output.accept((BucketItem)registryHolder.bucket().get());
                }
            })
            .build());
    public static final RegistryObject<CreativeModeTab> TEMPLATE = CREATIVE_MODE_TABS.register("hbm_template", () -> CreativeModeTab.builder()
            .title(Component.translatable(HBMLang.HBM_TEMPLATE.key()))
            .icon(() -> ModItems.ASSEMBLY_TEMPLATE.get().getDefaultInstance())
            .displayItems((parameters, output) -> addItemsForTab(output, TEMPLATE_KEY))
            .build());
    public static final RegistryObject<CreativeModeTab> BLOCKS = CREATIVE_MODE_TABS.register("hbm_blocks", () -> CreativeModeTab.builder()
            .title(Component.translatable(HBMLang.HBM_BLOCKS.key()))
            .icon(() -> ModBlocks.URANIUM_ORE.get().asItem().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ModBlocks.TEST12.get());
                addBlocksForTab(output, BLOCKS_KEY);
//                ModBlocks.creativeTab(output);
            })
            .build());
    public static final RegistryObject<CreativeModeTab> MACHINE = CREATIVE_MODE_TABS.register("hbm_machines", () -> CreativeModeTab.builder()
            .title(Component.translatable(HBMLang.HBM_MACHINE.key()))
            .icon(() -> Blocks.DIRT.asItem().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ModBlocks.machine_difurnace.get());
                output.accept(ModBlocks.machine_press.get());
                output.accept(ModBlocks.machine_electric_furnace.get());
                output.accept(ModBlocks.machine_boiler.get());
                output.accept(ModBlocks.machine_electric_boiler.get());
                output.accept(ModBlocks.machine_nuclear_boiler.get());
                output.accept(ModBlocks.machine_battery.get());
                output.accept(ModBlocks.machine_lithium_battery.get());
                output.accept(ModBlocks.machine_schrabidium_battery.get());
                output.accept(ModBlocks.machine_dineutronium_battery.get());
                output.accept(ModBlocks.anvil_iron.get());
                output.accept(ModBlocks.anvil_desh.get());
                output.accept(ModBlocks.anvil_bismuth.get());
                output.accept(ModBlocks.machine_cracking_tower.get());
                output.accept(ModBlocks.machine_condenser.get());
                output.accept(ModBlocks.machine_cooling_tower.get());
                output.accept(ModBlocks.machine_turbine_gas.get());
                output.accept(ModBlocks.machine_crucible.get());
                output.accept(ModBlocks.machine_assembler.get());
                output.accept(ModBlocks.machine_shredder.get());
                output.accept(ModBlocks.machine_wood_burner.get());
                output.accept(ModBlocks.machine_rbmk_base.get());
                output.accept(ModBlocks.machine_rbmk_heater.get());
                output.accept(ModBlocks.machine_rbmk_fuel_channel.get());
                output.accept(ModBlocks.machine_rbmk_control_rod.get());
                output.accept(ModBlocks.machine_rbmk_control_auto.get());
                output.accept(ModBlocks.machine_rbmk_boiler.get());
                output.accept(ModBlocks.machine_rbmk_moderator.get());
                output.accept(ModBlocks.machine_rbmk_absorber.get());
                output.accept(ModBlocks.machine_rbmk_outgasser.get());
                output.accept(ModBlocks.machine_rbmk_storage.get());
                output.accept(ModBlocks.machine_rbmk_cooler.get());
                output.accept(ModBlocks.machine_rbmk_console.get());
                output.accept(ModBlocks.machine_rbmk_display.get());
                output.accept(ModBlocks.machine_rbmk_graph.get());
                output.accept(ModBlocks.machine_rbmk_numitron.get());
                output.accept(ModBlocks.machine_rbmk_keypad.get());
                output.accept(ModBlocks.machine_rbmk_gauge.get());
                output.accept(ModBlocks.radio_torch_controller.get());
                output.accept(ModBlocks.rbmk_steam_inlet.get());
                output.accept(ModBlocks.rbmk_steam_outlet.get());
                output.accept(ModBlocks.machine_rbmk_element.get());
                output.accept(ModBlocks.machine_rbmk_reflector.get());
                output.accept(ModBlocks.machine_rbmk_debris.get());
                output.accept(ModBlocks.machine_rbmk_crane_console.get());
                output.accept(ModBlocks.machine_rbmk_autoloader.get());
                output.accept(ModBlocks.tokamak_controller.get());
                output.accept(ModBlocks.tokamak_casing.get());
                output.accept(ModBlocks.tokamak_coil.get());
                output.accept(ModBlocks.tokamak_heater.get());
                output.accept(ModBlocks.tokamak_injector.get());
                output.accept(ModBlocks.tokamak_port.get());
                output.accept(ModBlocks.pwr_controller.get());
                output.accept(ModBlocks.pwr_casing.get());
                output.accept(ModBlocks.pwr_port.get());
                output.accept(ModBlocks.pwr_reflector.get());
                output.accept(ModBlocks.pwr_fuel_block.get());
                output.accept(ModBlocks.pwr_control.get());
                output.accept(ModBlocks.pwr_channel.get());
                output.accept(ModBlocks.pwr_heatex.get());
                output.accept(ModBlocks.pwr_heatsink.get());
                output.accept(ModBlocks.pwr_neutron_source.get());
                output.accept(ModBlocks.machine_zirnox.get());
                output.accept(ModBlocks.machine_icf.get());
                output.accept(ModBlocks.machine_icf_controller.get());
                output.accept(ModBlocks.machine_icf_press.get());
                output.accept(ModBlocks.machine_research_reactor.get());
                output.accept(ModBlocks.machine_reactor_breeding.get());
                output.accept(ModItems.icf_pellet_empty.get());
                output.accept(ModItems.icf_pellet_depleted.get());
                output.accept(ItemICFPellet.createStack(ItemICFPellet.FuelType.DEUTERIUM,
                        ItemICFPellet.FuelType.TRITIUM, false));
                output.accept(ItemICFPellet.createStack(ItemICFPellet.FuelType.HELIUM3,
                        ItemICFPellet.FuelType.HELIUM4, false));
                output.accept(ItemICFPellet.createStack(ItemICFPellet.FuelType.LITHIUM,
                        ItemICFPellet.FuelType.OXYGEN, false));
                output.accept(ItemICFPellet.createStack(ItemICFPellet.FuelType.SODIUM,
                        ItemICFPellet.FuelType.CHLORINE, true));
                output.accept(ItemICFPellet.createStack(ItemICFPellet.FuelType.BERYLLIUM,
                        ItemICFPellet.FuelType.CALCIUM, true));
                addRBMKItems(output);
//                HBMMachine.creativeTab(output);
            })
            .build());
    public static final RegistryObject<CreativeModeTab> NUKE = CREATIVE_MODE_TABS.register("hbm_nuke", () -> CreativeModeTab.builder()
            .title(Component.translatable(HBMLang.HBM_NUKE.key()))
            .icon(() -> ModBlocks.bomb_fat_man.get().asItem().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ModBlocks.bomb_boy.get());
                output.accept(ModBlocks.bomb_fat_man.get());
                output.accept(ModBlocks.bomb_custom.get());
                addItemsForTab(output, NUKE_KEY);
            })
            .build());
    public static final RegistryObject<CreativeModeTab> MISSILE = CREATIVE_MODE_TABS.register("hbm_missiles", () -> CreativeModeTab.builder()
            .title(Component.translatable(HBMLang.HBM_MISSILE.key()))
            .icon(() -> ModItems.MISSILE_NUCLEAR.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                addItemsForTab(output, MISSILE_KEY);
                output.accept(ModBlocks.LAUNCH_PAD.get());
                output.accept(ModItems.MISSILE_NUCLEAR.get());
                output.accept(ModItems.MISSILE_GENERIC.get());
                output.accept(HBMWeapon.MP_WARHEAD_15_BALEFIRE.get());
                HBMWeapon.creativeTab(output);
            })
            .build());
    public static final RegistryObject<CreativeModeTab> WEAPON = CREATIVE_MODE_TABS.register("hbm_weapons", () -> CreativeModeTab.builder()
            .title(Component.translatable(HBMLang.HBM_WEAPON.key()))
            .icon(Items.WOODEN_SWORD::getDefaultInstance)
            .displayItems((parameters, output) -> {
                addItemsForTab(output, WEAPON_KEY);
                output.accept(ModItems.REDSTONE_SWORD.get());
                output.accept(ModItems.BIG_SWORD.get());
                output.accept(ModItems.grenade_generic.get());
                output.accept(ModItems.grenade_strong.get());
                output.accept(ModItems.grenade_fire.get());
                output.accept(ModItems.grenade_frag.get());
                output.accept(ModItems.grenade_black_hole.get());
                HBMCombat.creativeTab(output);
            })
            .build());
    public static final RegistryObject<CreativeModeTab> CONSUMABLE = CREATIVE_MODE_TABS.register("hbm_consumable", () -> CreativeModeTab.builder()
            .title(Component.translatable(HBMLang.HBM_CONSUMABLE.key()))
            .icon(() -> ModItems.BOTTLE_NUKA.get().getDefaultInstance())
            .displayItems((parameters, output) -> addItemsForTab(output, CONSUMABLE_KEY))
            .build());

    private static void addItemsForTab(CreativeModeTab.Output output, ResourceKey<CreativeModeTab> tabKey) {
        for (WrappedItemRegistry itemRegistry : ModItems.itemList) {
            if (tabKey.equals(itemRegistry.creativeKey)) {
                output.accept(itemRegistry.get());
            }
        }
    }

    private static void addBlocksForTab(CreativeModeTab.Output output, ResourceKey<CreativeModeTab> tabKey) {
        for (WrappedBlockRegistry blockRegistry : ModBlocks.blockList) {
            if (tabKey.equals(blockRegistry.creativeKey)) {
                output.accept(blockRegistry.get());
            }
        }
    }

    private static void addToolItems(CreativeModeTab.Output output) {
        output.accept(ModItems.UPGRADE_BASE.get());
        output.accept(ModItems.GEIGER_COUNTER.get());
        output.accept(ModItems.BUILD_WAND.get());
        output.accept(ModItems.DEBUG_WAND.get());
        output.accept(ModItems.DESIGNATOR.get());
        output.accept(ModItems.RBMK_TOOL.get());
        output.accept(ModItems.DOSIMETER.get());
        output.accept(ModItems.DIGAMMA_DIAGNOSTIC.get());
        output.accept(ModItems.POLLUTION_DETECTOR.get());
        output.accept(ModItems.ORE_CANNER.get());
        output.accept(ModItems.SCREWDRIVER.get());
        output.accept(ModItems.reacher.get());
    }

    private static void addRBMKItems(CreativeModeTab.Output output) {
        output.accept(ModItems.rbmk_lid.get());
        output.accept(ModItems.rbmk_lid_glass.get());
        output.accept(ModItems.rbmk_control_rod.get());
        output.accept(ModItems.rbmk_fuel_base.get());
        output.accept(ModItems.rbmk_fuel_lea.get());
        output.accept(ModItems.rbmk_fuel_leaus.get());
        output.accept(ModItems.rbmk_fuel_lep.get());
        output.accept(ModItems.rbmk_fuel_les.get());
        output.accept(ModItems.rbmk_fuel_mea.get());
        output.accept(ModItems.rbmk_fuel_men.get());
        output.accept(ModItems.rbmk_fuel_mep.get());
        output.accept(ModItems.rbmk_fuel_mes.get());
        output.accept(ModItems.rbmk_fuel_meu.get());
        output.accept(ModItems.rbmk_fuel_ueu.get());
        output.accept(ModItems.rbmk_fuel_mox.get());
        output.accept(ModItems.rbmk_fuel_heu233.get());
        output.accept(ModItems.rbmk_fuel_heu235.get());
        output.accept(ModItems.rbmk_fuel_heaus.get());
        output.accept(ModItems.rbmk_fuel_hea241.get());
        output.accept(ModItems.rbmk_fuel_hea242.get());
        output.accept(ModItems.rbmk_fuel_hen.get());
        output.accept(ModItems.rbmk_fuel_hep.get());
        output.accept(ModItems.rbmk_fuel_hep241.get());
        output.accept(ModItems.rbmk_fuel_hes.get());
        output.accept(ModItems.rbmk_fuel_thmeu.get());
        output.accept(ModItems.rbmk_fuel_drx.get());
        output.accept(ModItems.rbmk_fuel_flashlead.get());
        output.accept(ModItems.rbmk_fuel_balefire.get());
        output.accept(ModItems.rbmk_fuel_balefire_gold.get());
        output.accept(ModItems.rbmk_fuel_po210be.get());
        output.accept(ModItems.rbmk_fuel_pu238be.get());
        output.accept(ModItems.rbmk_fuel_ra226be.get());
        output.accept(ModItems.rbmk_fuel_zfb_base.get());
        output.accept(ModItems.rbmk_fuel_zfb_bismuth.get());
        output.accept(ModItems.rbmk_fuel_zfb_pu241.get());
        output.accept(ModItems.rbmk_fuel_zfb_am_mix.get());
        output.accept(ModItems.rbmk_fuel_test.get());
        output.accept(ModItems.rbmk_fuel_empty.get());
        for (ItemZirnoxRod.ZirnoxRodType type : ItemZirnoxRod.ZirnoxRodType.values()) {
            output.accept(ItemZirnoxRod.createStack(ModItems.rod_zirnox.get(), type));
        }
        output.accept(ModItems.rod_zirnox_empty.get());
        output.accept(ModItems.rod_zirnox_tritium.get());
        output.accept(ModItems.rod_zirnox_natural_uranium_fuel_depleted.get());
        output.accept(ModItems.rod_zirnox_uranium_fuel_depleted.get());
        output.accept(ModItems.rod_zirnox_thorium_fuel_depleted.get());
        output.accept(ModItems.rod_zirnox_mox_fuel_depleted.get());
        output.accept(ModItems.rod_zirnox_plutonium_fuel_depleted.get());
        output.accept(ModItems.rod_zirnox_u233_fuel_depleted.get());
        output.accept(ModItems.rod_zirnox_u235_fuel_depleted.get());
        output.accept(ModItems.rod_zirnox_les_fuel_depleted.get());
        output.accept(ModItems.rod_zirnox_zfb_mox_depleted.get());
        output.accept(ModItems.rod_empty.get());
        output.accept(ModItems.rod_dual_empty.get());
        output.accept(ModItems.rod_quad_empty.get());
    }

    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event){
        ModItems.creativeTab(event);
        ModBlocks.creativeTab(event);
    }
}
