package com.hbm.registries;

import com.hbm.HBMLang;
import com.hbm.block.HBMBlockComponent;
import com.hbm.block.HBMMachine;
import com.hbm.item.*;
import com.hbm.item.zirnox.ItemZirnoxRod;
import com.hbm.item.icf.ItemICFPellet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import com.hbm.Inventory.fluid.ModFluids;
import java.util.List;

import static com.hbm.HBM.MODID;

public class ModCreativeModeTab {
    //创造模式物品栏注册表
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final RegistryObject<CreativeModeTab> HBM_ITEM = CREATIVE_MODE_TABS.register("hbm_item", () -> CreativeModeTab.builder()
        .title(Component.translatable(HBMLang.ITEMGROUP_ITEM.key()))
        .icon(() -> ModItems.UPGRADE_BASE.get().getDefaultInstance())
	        .displayItems((parameters, output) -> {
//            output.accept(ModItems.ingot_red_copper.get());
//            output.accept(ModItems.ingot_tungsten.get());
//            output.accept(ModItems.ingot_aluminium.get());
//            output.accept(ModItems.ingot_lead.get());
//            output.accept(ModItems.ingot_zirconium.get());
//            output.accept(ModItems.ingot_magnetized_tungsten.get());
//            output.accept(ModItems.ingot_solinium.get());
//            output.accept(ModItems.ingot_advanced_alloy.get());
//            output.accept(ModItems.plate_iron.get());
//            output.accept(ModItems.plate_advanced_alloy.get());
//            output.accept(ModItems.fluorite.get());
//            output.accept(ModItems.nugget_zirconium.get());
//            output.accept(ModItems.solid_fuel.get());
//            output.accept(ModItems.lignite.get());
//            output.accept(ModItems.powder_lignite.get());
//            output.accept(ModItems.powder_coal.get());
//            output.accept(ModItems.powder_coal_tiny.get());
//            output.accept(ModItems.coke_coal.get());
//            output.accept(ModItems.coke_lignite.get());
//            output.accept(ModItems.coke_petroleum.get());
//            output.accept(ModItems.briquette_wood.get());
//            output.accept(ModItems.briquette_coal.get());
//            output.accept(ModItems.briquette_lignite.get());
//
//            output.accept(ModItems.BEDROCK_ORE.get());

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

//            HBMComponent.creativeTab(output);
        }).build());
    public static final RegistryObject<CreativeModeTab> HBM_BLOCK = CREATIVE_MODE_TABS.register("hbm_block", () -> CreativeModeTab.builder()
        .title(Component.translatable(HBMLang.ITEMGROUP_BLOCK.key()))
        .icon(()->HBMBlockComponent.URANIUM_ORE.get().asItem().getDefaultInstance())
        .displayItems((parameters, output) -> {
            output.accept(ModBlocks.TEST12.get());

            HBMBlockComponent.creativeTab(output);
        }).build());
    public static final RegistryObject<CreativeModeTab> HBM_MACHINE = CREATIVE_MODE_TABS.register("hbm_machine", () -> CreativeModeTab.builder()
        .title(Component.translatable(HBMLang.ITEMGROUP_MACHINE.key()))
        .icon(()->ModBlocks.machine_electric_furnace.get().asItem().getDefaultInstance())
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
            output.accept(ItemICFPellet.createStack(ItemICFPellet.FuelType.DEUTERIUM, ItemICFPellet.FuelType.TRITIUM, false));
            output.accept(ItemICFPellet.createStack(ItemICFPellet.FuelType.HELIUM3, ItemICFPellet.FuelType.HELIUM4, false));
            output.accept(ItemICFPellet.createStack(ItemICFPellet.FuelType.LITHIUM, ItemICFPellet.FuelType.OXYGEN, false));
            output.accept(ItemICFPellet.createStack(ItemICFPellet.FuelType.SODIUM, ItemICFPellet.FuelType.CHLORINE, true));
            output.accept(ItemICFPellet.createStack(ItemICFPellet.FuelType.BERYLLIUM, ItemICFPellet.FuelType.CALCIUM, true));
            addRBMKItems(output);
	            HBMMachine.creativeTab(output);
	        }).build());
    public static final RegistryObject<CreativeModeTab> HBM_TOOL = CREATIVE_MODE_TABS.register("hbm_weapon", () -> CreativeModeTab.builder()
        .title(Component.translatable(HBMLang.ITEMGROUP_TOOL.key()))
            .icon(()-> ModItems.DETONATOR.get().getDefaultInstance())
        .displayItems((parameters, output) -> {
//            output.accept(ModItems.detonator.get());

//            output.accept(ModItems.grenade_generic.get());
//            output.accept(ModItems.grenade_strong.get());
//            output.accept(ModItems.grenade_fire.get());
//            output.accept(ModItems.grenade_frag.get());
//            output.accept(ModItems.grenade_black_hole.get());

//            output.accept(ModItems.bucket_irradiated_water.get());
//            output.accept(ModItems.bucket_irradiated_polluted.get());
//            output.accept(ModItems.bucket_sulfuric_acid.get());
            output.accept(ModBlocks.conveyor.get());
            output.accept(ModBlocks.crate_iron.get());
            output.accept(ModBlocks.crate_steel.get());

//            HBMtools.creativeTab(output);
            HBMWeapon.creativeTab(output);
        for (ModFluids.FluidRegistryHolder registryHolder : ModFluids.fluidList) {
                output.accept((BucketItem)registryHolder.bucket().get());
        }
        }).build());
    public static final RegistryObject<CreativeModeTab> HBM_EQUIPMENT = CREATIVE_MODE_TABS.register("hbm_equipment", () -> CreativeModeTab.builder()
        .title(Component.translatable(HBMLang.ITEMGROUP_EQUIPMENT.key()))
        .icon(() -> ModItems.BIG_SWORD.get().getDefaultInstance())
        .displayItems((parameters, output) -> {
            output.accept(ModItems.REDSTONE_SWORD.get());
            output.accept(ModItems.BIG_SWORD.get());
            HBMCombat.creativeTab(output);
        }).build());
    public static final RegistryObject<CreativeModeTab> HBM_MISSILE = CREATIVE_MODE_TABS.register("hbm_missile", () -> CreativeModeTab.builder()
        .title(Component.translatable(HBMLang.ITEMGROUP_BLOCK.key()))
        .icon(()->HBMWeapon.MP_WARHEAD_15_BALEFIRE.get().asItem().getDefaultInstance())
//        .displayItems((parameters, output) -> {
//                output.accept(HBMWeapon.MP_WARHEAD_15_BALEFIRE.get());
//        })
            .build());
    /**
     * 将模组中的物品注册到原版创造模式物品栏中
     * */
    public static void addCreative(BuildCreativeModeTabContentsEvent event){
            ModItems.creativeTab(event);
            ModItems.creativeTab(event);
            ModBlocks.creativeTab(event);
            if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS){}

            else if (event.getTabKey() == CreativeModeTabs.COLORED_BLOCKS){}

            else if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS){}

            else if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS){}

            else if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS){}

            else if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES){}

            else if (event.getTabKey() == CreativeModeTabs.COMBAT){
            }

            else if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS){}

            else if (event.getTabKey() == CreativeModeTabs.INGREDIENTS){}

            else if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS){}

            else if (event.getTabKey() == HBM_MISSILE.getKey()){
                    event.getEntries().put(new ItemStack(HBMWeapon.MP_WARHEAD_15_BALEFIRE.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    event.getEntries().put(new ItemStack(HBMMachine.LAUNCH_PAD.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
//                    event.getEntries().put(new ItemStack(HBMtools.DESIGNATOR.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
    }

    private static final List<RegistryObject<? extends Item>> RBMK_FUEL_ITEMS = List.of(
            ModItems.rbmk_fuel_base,
            ModItems.rbmk_fuel_lea,
            ModItems.rbmk_fuel_leaus,
            ModItems.rbmk_fuel_lep,
            ModItems.rbmk_fuel_les,
            ModItems.rbmk_fuel_mea,
            ModItems.rbmk_fuel_men,
            ModItems.rbmk_fuel_mep,
            ModItems.rbmk_fuel_mes,
            ModItems.rbmk_fuel_meu,
            ModItems.rbmk_fuel_ueu,
            ModItems.rbmk_fuel_mox,
            ModItems.rbmk_fuel_heu233,
            ModItems.rbmk_fuel_heu235,
            ModItems.rbmk_fuel_heaus,
            ModItems.rbmk_fuel_hea241,
            ModItems.rbmk_fuel_hea242,
            ModItems.rbmk_fuel_hen,
            ModItems.rbmk_fuel_hep,
            ModItems.rbmk_fuel_hep241,
            ModItems.rbmk_fuel_hes,
            ModItems.rbmk_fuel_thmeu,
            ModItems.rbmk_fuel_drx,
            ModItems.rbmk_fuel_flashlead,
            ModItems.rbmk_fuel_balefire,
            ModItems.rbmk_fuel_balefire_gold,
            ModItems.rbmk_fuel_po210be,
            ModItems.rbmk_fuel_pu238be,
            ModItems.rbmk_fuel_ra226be,
            ModItems.rbmk_fuel_zfb_base,
            ModItems.rbmk_fuel_zfb_bismuth,
            ModItems.rbmk_fuel_zfb_pu241,
            ModItems.rbmk_fuel_zfb_am_mix,
            ModItems.rbmk_fuel_test
    );

    private static void addRBMKItems(CreativeModeTab.Output output) {
        output.accept(ModItems.rbmk_lid.get());
        output.accept(ModItems.rbmk_lid_glass.get());
        output.accept(ModItems.rbmk_control_rod.get());
        RBMK_FUEL_ITEMS.forEach(fuel -> output.accept(fuel.get()));
        output.accept(ModItems.rbmk_fuel_empty.get());
    }
}
