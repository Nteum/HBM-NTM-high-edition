package com.hbm.registries;

import com.hbm.HBMLang;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.hbm.HBM.MODID;

public class ModTabs {
    //创造模式物品栏注册表
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> PARTS = CREATIVE_MODE_TABS.register("hbm_parts", () -> CreativeModeTab.builder().title(Component.translatable(HBMLang.HBM_PARTS.key())).icon(() -> ModItems.INGOT_URANIUM.get().getDefaultInstance()).build());
    public static final RegistryObject<CreativeModeTab> CONTROL = CREATIVE_MODE_TABS.register("hbm_control", () -> CreativeModeTab.builder().title(Component.translatable(HBMLang.HBM_CONTROL.key())).icon(() -> ModItems.PELLET_RTG.get().getDefaultInstance()).build());
    public static final RegistryObject<CreativeModeTab> TEMPLATE = CREATIVE_MODE_TABS.register("hbm_template", () -> CreativeModeTab.builder().title(Component.translatable(HBMLang.HBM_TEMPLATE.key())).icon(() -> ModItems.ASSEMBLY_TEMPLATE.get().getDefaultInstance()).build());
    public static final RegistryObject<CreativeModeTab> BLOCKS = CREATIVE_MODE_TABS.register("hbm_blocks", () -> CreativeModeTab.builder().title(Component.translatable(HBMLang.HBM_BLOCKS.key())).icon(() -> ModBlocks.ORE_URANIUM.get().asItem().getDefaultInstance()).build());
    public static final RegistryObject<CreativeModeTab> MACHINE = CREATIVE_MODE_TABS.register("hbm_machine", () -> CreativeModeTab.builder().title(Component.translatable(HBMLang.HBM_MACHINE.key())).icon(() -> ModBlocks.PWR_CONTROLLER.get().asItem().getDefaultInstance()).build());
    public static final RegistryObject<CreativeModeTab> NUKE = CREATIVE_MODE_TABS.register("hbm_nuke", () -> CreativeModeTab.builder().title(Component.translatable(HBMLang.HBM_NUKE.key())).icon(() -> ModBlocks.BOMB_FAT_MAN.get().asItem().getDefaultInstance()).build());
    public static final RegistryObject<CreativeModeTab> MISSILE = CREATIVE_MODE_TABS.register("hbm_missile", () -> CreativeModeTab.builder().title(Component.translatable(HBMLang.HBM_MISSILE.key())).icon(() -> ModItems.MISSILE_NUCLEAR.get().getDefaultInstance()).build());
    public static final RegistryObject<CreativeModeTab> WEAPON = CREATIVE_MODE_TABS.register("hbm_weapon", () -> CreativeModeTab.builder().title(Component.translatable(HBMLang.HBM_WEAPON.key())).icon(() -> ModItems.GUN_RIFLE.get().getDefaultInstance()).build());
    public static final RegistryObject<CreativeModeTab> CONSUMABLE = CREATIVE_MODE_TABS.register("hbm_consumable", () -> CreativeModeTab.builder().title(Component.translatable(HBMLang.HBM_CONSUMABLE.key())).icon(() -> ModItems.BOTTLE_NUKA.get().getDefaultInstance()).build());

//    public static final RegistryObject<CreativeModeTab> HBM_ITEM = CREATIVE_MODE_TABS.register("hbm_item", () -> CreativeModeTab.builder()
//        .title(Component.translatable(HBMLang.ITEMGROUP_ITEM.key()))
//        .icon(() -> HBMtools.UPGRADE_BASE.get().getDefaultInstance())
//        .displayItems((parameters, output) -> {
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
//
//            output.accept(HBMtools.UPGRADE_BASE.get());
//
//            HBMComponent.creativeTab(output);
//        }).build());
//    public static final RegistryObject<CreativeModeTab> HBM_BLOCK = CREATIVE_MODE_TABS.register("hbm_block", () -> CreativeModeTab.builder()
//        .title(Component.translatable(HBMLang.ITEMGROUP_BLOCK.key()))
//        .icon(()->ModBlocks.Ores.ORE_URANIUM.get().asItem().getDefaultInstance())
//        .displayItems((parameters, output) -> {
//            output.accept(ModBlocks.TEST12.get());
//
//            HBMBlockComponent.creativeTab(output);
//        }).build());
//    public static final RegistryObject<CreativeModeTab> HBM_MACHINE = CREATIVE_MODE_TABS.register("hbm_machine", () -> CreativeModeTab.builder()
//        .title(Component.translatable(HBMLang.ITEMGROUP_MACHINE.key()))
//        .icon(()->ModBlocks.machine_electric_furnace.get().asItem().getDefaultInstance())
//        .displayItems((parameters, output) -> {
//            output.accept(ModBlocks.machine_difurnace.get());
//            output.accept(ModBlocks.machine_press.get());
//            output.accept(ModBlocks.machine_electric_furnace.get());
//            output.accept(ModBlocks.machine_boiler.get());
//            output.accept(ModBlocks.machine_electric_boiler.get());
//            output.accept(ModBlocks.machine_nuclear_boiler.get());
//            output.accept(ModBlocks.machine_battery.get());
//            output.accept(ModBlocks.machine_lithium_battery.get());
//            output.accept(ModBlocks.machine_schrabidium_battery.get());
//            output.accept(ModBlocks.machine_dineutronium_battery.get());
//            output.accept(ModBlocks.anvil_iron.get());
//            output.accept(ModBlocks.anvil_desh.get());
//            output.accept(ModBlocks.anvil_bismuth.get());
//            output.accept(ModBlocks.machine_cracking_tower.get());
//            output.accept(ModBlocks.machine_crucible.get());
//            output.accept(ModBlocks.machine_assembler.get());
//            output.accept(ModBlocks.RED_CABLE.get());
//            HBMMachine.creativeTab(output);
//        }).build());
//    public static final RegistryObject<CreativeModeTab> HBM_TOOL = CREATIVE_MODE_TABS.register("hbm_weapon", () -> CreativeModeTab.builder()
//        .title(Component.translatable(HBMLang.ITEMGROUP_TOOL.key()))
//            .icon(()->ModItems.DETONATOR.get().getDefaultInstance())
//        .displayItems((parameters, output) -> {
////            output.accept(ModItems.detonator.get());
//
//            output.accept(ModItems.grenade_generic.get());
//            output.accept(ModItems.grenade_strong.get());
//            output.accept(ModItems.grenade_fire.get());
//            output.accept(ModItems.grenade_frag.get());
//            output.accept(ModItems.grenade_black_hole.get());
//
////            output.accept(ModItems.bucket_irradiated_water.get());
////            output.accept(ModItems.bucket_irradiated_polluted.get());
////            output.accept(ModItems.bucket_sulfuric_acid.get());
//            output.accept(ModBlocks.conveyor.get());
//            output.accept(ModBlocks.bomb_boy.get());
//            output.accept(ModBlocks.bomb_custom.get());
//
//            output.accept(ModItems.SCREWDRIVER.get());
//
//            HBMtools.creativeTab(output);
//            HBMWeapon.creativeTab(output);
//        for (ModFluids.FluidRegistryHolder registryHolder : ModFluids.fluidList) {
//                output.accept((BucketItem)registryHolder.bucket().get());
//        }
//        }).build());
//    public static final RegistryObject<CreativeModeTab> HBM_MISSILE = CREATIVE_MODE_TABS.register("hbm_missile", () -> CreativeModeTab.builder()
//        .title(Component.translatable(HBMLang.ITEMGROUP_BLOCK.key()))
//        .icon(()->HBMWeapon.MP_WARHEAD_15_BALEFIRE.get().asItem().getDefaultInstance())
////        .displayItems((parameters, output) -> {
////                output.accept(HBMWeapon.MP_WARHEAD_15_BALEFIRE.get());
////        })
//            .build());
    /**
     * 将模组中的物品注册到原版创造模式物品栏中
     * */
    public static void addCreative(BuildCreativeModeTabContentsEvent event){
            ModItems.creativeTab(event);
            ModBlocks.creativeTab(event);
//            if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS){}
//
//            else if (event.getTabKey() == CreativeModeTabs.COLORED_BLOCKS){}
//
//            else if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS){}
//
//            else if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS){}
//
//            else if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS){}
//
//            else if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES){}
//
//            else if (event.getTabKey() == CreativeModeTabs.COMBAT){
//                    HBMCombat.creativeTab(event.getEntries());
//            }
//
//            else if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS){}
//
//            else if (event.getTabKey() == CreativeModeTabs.INGREDIENTS){}
//
//            else if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS){}
//
//            else if (event.getTabKey() == HBM_MISSILE.getKey()){
//                    event.getEntries().put(new ItemStack(HBMWeapon.MP_WARHEAD_15_BALEFIRE.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
//                    event.getEntries().put(new ItemStack(HBMMachine.LAUNCH_PAD.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
//                    event.getEntries().put(new ItemStack(HBMtools.DESIGNATOR.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
//            }
    }
}
