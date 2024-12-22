package com.hbm.registries;

import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.hbm.main.HBMxx.MODID;

public class ModCreativeModeTab {
    //创造模式物品栏注册表
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final RegistryObject<CreativeModeTab> HBM_ITEM = CREATIVE_MODE_TABS.register("hbm_item", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup.hbm_item"))
        .icon(() -> ModItems.ingot_steel.get().getDefaultInstance())
        .displayItems((parameters, output) -> {
            output.accept(ModItems.ingot_steel.get());
            output.accept(ModItems.ingot_red_copper.get());
            output.accept(ModItems.ingot_tungsten.get());
            output.accept(ModItems.ingot_aluminium.get());
            output.accept(ModItems.ingot_lead.get());
            output.accept(ModItems.ingot_zirconium.get());
            output.accept(ModItems.ingot_magnetized_tungsten.get());
            output.accept(ModItems.ingot_solinium.get());
            output.accept(ModItems.ingot_advanced_alloy.get());
            output.accept(ModItems.plate_steel.get());
            output.accept(ModItems.plate_iron.get());
            output.accept(ModItems.plate_advanced_alloy.get());
            output.accept(ModItems.fluorite.get());
            output.accept(ModItems.nugget_zirconium.get());
            output.accept(ModItems.solid_fuel.get());
            output.accept(ModItems.lignite.get());
            output.accept(ModItems.powder_lignite.get());
            output.accept(ModItems.powder_coal.get());
            output.accept(ModItems.powder_coal_tiny.get());
            output.accept(ModItems.coke_coal.get());
            output.accept(ModItems.coke_lignite.get());
            output.accept(ModItems.coke_petroleum.get());
            output.accept(ModItems.briquette_wood.get());
            output.accept(ModItems.briquette_coal.get());
            output.accept(ModItems.briquette_lignite.get());

            output.accept(ModItems.BEDROCK_ORE.get());
        }).build());
    public static final RegistryObject<CreativeModeTab> HBM_BLOCK = CREATIVE_MODE_TABS.register("hbm_block", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup.hbm_block"))
        .icon(()->ModBlocks.URANIUM_ORE.get().asItem().getDefaultInstance())
        .displayItems((parameters, output) -> {
            output.accept(ModBlocks.URANIUM_ORE.get());
            output.accept(ModBlocks.DEEPSLATE_URANIUM_ORE.get());
            output.accept(ModBlocks.BEDROCK_ORE.get());
            output.accept(ModBlocks.RARE_EARTH_ORE.get());
            output.accept(ModBlocks.DEEPSLATE_RARE_EARTH_ORE.get());
            output.accept(ModBlocks.ASBESTOS_BLOCK.get());
            output.accept(ModBlocks.ASBESTOS_ORE.get());
            output.accept(ModBlocks.BASALT_ASBESTOS_ORE.get());
            output.accept(ModBlocks.SA326_ORE.get());
            output.accept(ModBlocks.LITHIUM_ORE.get());
            output.accept(ModBlocks.DEPTH_STONE.get());
        }).build());
    public static final RegistryObject<CreativeModeTab> HBM_MACHINE = CREATIVE_MODE_TABS.register("hbm_machine", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup.hbm_machine"))
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
            output.accept(ModBlocks.RED_CABLE.get());
        }).build());
    public static final RegistryObject<CreativeModeTab> HBM_TOOL = CREATIVE_MODE_TABS.register("hbm_weapon", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup.hbm_tool"))
            .icon(()->ModItems.detonator.get().asItem().getDefaultInstance())
        .displayItems((parameters, output) -> {
            output.accept(ModItems.detonator.get());

            output.accept(ModItems.grenade_generic.get());
            output.accept(ModItems.grenade_strong.get());
            output.accept(ModItems.grenade_fire.get());
            output.accept(ModItems.grenade_frag.get());
            output.accept(ModItems.grenade_black_hole.get());

            output.accept(ModItems.bucket_irradiated_water.get());
            output.accept(ModItems.bucket_irradiated_polluted.get());
            output.accept(ModItems.bucket_sulfuric_acid.get());
            output.accept(ModBlocks.conveyor.get());
            output.accept(ModBlocks.bomb_fat_man.get());
            output.accept(ModBlocks.bomb_custom.get());
        }).build());

    /**
     * 将模组中的物品注册到原版创造模式物品栏中
     * */
    public static void addCreative(BuildCreativeModeTabContentsEvent event){
            if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS){
//                    event.accept();
            }

            else if (event.getTabKey() == CreativeModeTabs.COLORED_BLOCKS){}

            else if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS){}

            else if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS){}

            else if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS){}

            else if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES){}

            else if (event.getTabKey() == CreativeModeTabs.COMBAT){}

            else if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS){}

            else if (event.getTabKey() == CreativeModeTabs.INGREDIENTS){}

            else if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS){}
    }
}
