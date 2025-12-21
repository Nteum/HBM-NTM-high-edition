package com.hbm.registries;

import com.hbm.HBMLang;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.block.HBMBlockComponent;
import com.hbm.block.HBMMachine;
import com.hbm.item.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.ietf.jgss.Oid;

import static com.hbm.HBM.MODID;

public class ModCreativeModeTab {
    //创造模式物品栏注册表
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final RegistryObject<CreativeModeTab> HBM_ITEM = CREATIVE_MODE_TABS.register("hbm_item", () -> CreativeModeTab.builder()
        .title(Component.translatable(HBMLang.ITEMGROUP_ITEM.key()))
        .icon(() -> HBMtools.UPGRADE_BASE.get().getDefaultInstance())
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

            output.accept(HBMtools.UPGRADE_BASE.get());

            HBMComponent.creativeTab(output);
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
            output.accept(ModBlocks.machine_crucible.get());
            output.accept(ModBlocks.machine_assembler.get());
            output.accept(ModBlocks.machine_shredder.get());
	            output.accept(ModBlocks.machine_rbmk_base.get());
	            output.accept(ModBlocks.machine_rbmk_heater.get());
	            output.accept(ModBlocks.machine_rbmk_fuel_channel.get());
	            output.accept(ModBlocks.machine_rbmk_control_rod.get());
	            output.accept(ModBlocks.tokamak_controller.get());
            output.accept(ModBlocks.tokamak_casing.get());
            output.accept(ModBlocks.tokamak_coil.get());
            output.accept(ModBlocks.tokamak_heater.get());
            output.accept(ModBlocks.tokamak_injector.get());
	            output.accept(ModBlocks.tokamak_port.get());
//	            output.accept(ModItems.rbmk_lid.get());
//	            output.accept(ModItems.rbmk_lid_glass.get());
//	            output.accept(ModItems.rbmk_fuel_base.get());
//	            output.accept(ModItems.rbmk_fuel_lea.get());
//	            output.accept(ModItems.rbmk_fuel_leaus.get());
//	            output.accept(ModItems.rbmk_fuel_lep.get());
//	            output.accept(ModItems.rbmk_fuel_les.get());
//	            output.accept(ModItems.rbmk_fuel_mea.get());
//	            output.accept(ModItems.rbmk_fuel_men.get());
//	            output.accept(ModItems.rbmk_fuel_mep.get());
//	            output.accept(ModItems.rbmk_fuel_mes.get());
//	            output.accept(ModItems.rbmk_fuel_meu.get());
//	            output.accept(ModItems.rbmk_fuel_ueu.get());
//	            output.accept(ModItems.rbmk_fuel_mox.get());
//	            output.accept(ModItems.rbmk_fuel_thmeu.get());
//	            output.accept(ModItems.rbmk_fuel_heu233.get());
//	            output.accept(ModItems.rbmk_fuel_heu235.get());
//	            output.accept(ModItems.rbmk_fuel_heaus.get());
//	            output.accept(ModItems.rbmk_fuel_hea241.get());
//	            output.accept(ModItems.rbmk_fuel_hea242.get());
//	            output.accept(ModItems.rbmk_fuel_hen.get());
//	            output.accept(ModItems.rbmk_fuel_hep.get());
//	            output.accept(ModItems.rbmk_fuel_hep241.get());
//	            output.accept(ModItems.rbmk_fuel_hes.get());
//	            output.accept(ModItems.rbmk_fuel_drx.get());
//	            output.accept(ModItems.rbmk_fuel_flashlead.get());
//	            output.accept(ModItems.rbmk_fuel_balefire.get());
//	            output.accept(ModItems.rbmk_fuel_balefire_gold.get());
//	            output.accept(ModItems.rbmk_fuel_po210be.get());
//	            output.accept(ModItems.rbmk_fuel_pu238be.get());
//	            output.accept(ModItems.rbmk_fuel_ra226be.get());
//	            output.accept(ModItems.rbmk_fuel_zfb_base.get());
//	            output.accept(ModItems.rbmk_fuel_zfb_bismuth.get());
//	            output.accept(ModItems.rbmk_fuel_zfb_pu241.get());
//	            output.accept(ModItems.rbmk_fuel_zfb_am_mix.get());
//	            output.accept(ModItems.rbmk_fuel_test.get());
//	            output.accept(ModItems.rbmk_fuel_empty.get());
//	            output.accept(ModBlocks.RED_CABLE.get());
	            HBMMachine.creativeTab(output);
	        }).build());
    public static final RegistryObject<CreativeModeTab> HBM_TOOL = CREATIVE_MODE_TABS.register("hbm_weapon", () -> CreativeModeTab.builder()
        .title(Component.translatable(HBMLang.ITEMGROUP_TOOL.key()))
            .icon(()-> HBMItems.DETONATOR.get().getDefaultInstance())
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

            output.accept(HBMItems.SCREWDRIVER.get());

            HBMtools.creativeTab(output);
            HBMWeapon.creativeTab(output);
        for (ModFluids.FluidRegistryHolder registryHolder : ModFluids.fluidList) {
                output.accept((BucketItem)registryHolder.bucket().get());
        }
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
            ModBlocks.creativeTab(event);
            if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS){}

            else if (event.getTabKey() == CreativeModeTabs.COLORED_BLOCKS){}

            else if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS){}

            else if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS){}

            else if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS){}

            else if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES){}

            else if (event.getTabKey() == CreativeModeTabs.COMBAT){
                    HBMCombat.creativeTab(event.getEntries());
            }

            else if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS){}

            else if (event.getTabKey() == CreativeModeTabs.INGREDIENTS){}

            else if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS){}

            else if (event.getTabKey() == HBM_MISSILE.getKey()){
                    event.getEntries().put(new ItemStack(HBMWeapon.MP_WARHEAD_15_BALEFIRE.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    event.getEntries().put(new ItemStack(HBMMachine.LAUNCH_PAD.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    event.getEntries().put(new ItemStack(HBMtools.DESIGNATOR.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
    }
}
