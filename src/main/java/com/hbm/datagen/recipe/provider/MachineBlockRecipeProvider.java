package com.hbm.datagen.recipe.provider;

import com.hbm.datagen.recipe.ISubRecipeProvider;

import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import com.hbm.registries.ModTags;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

/**
 * Vanilla crafting recipes for core HBM machines/blocks (excludes Chicago pile for now).
 */
public class MachineBlockRecipeProvider implements ISubRecipeProvider {

    @Override
    public void addRecipes(Consumer<FinishedRecipe> consumer) {
        basicMachines(consumer);
        infrastructure(consumer);
        batteryBlocks(consumer);
        reactors(consumer);
        rbmkPeripherals(consumer);
        pwrComponents(consumer);
    }

    private static void basicMachines(Consumer<FinishedRecipe> consumer) {
        shaped(ModBlocks.machine_difurnace.get())
                .pattern("STS")
                .pattern("CHC")
                .pattern("SFS")
                .define('S', ModItems.PLATE_STEEL.get())
                .define('T', ModItems.INGOT_TUNGSTEN.get())
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .define('H', Items.HOPPER)
                .define('F', Blocks.FURNACE)
                .unlockedBy(hasName(ModItems.PLATE_STEEL.get()), hasItem(ModItems.PLATE_STEEL.get()))
                .save(consumer);

        shaped(ModBlocks.machine_electric_furnace.get())
                .pattern("SLS")
                .pattern("CFC")
                .pattern("SRS")
                .define('S', ModItems.PLATE_STEEL.get())
                .define('L', ModItems.COIL_COPPER.get())
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .define('F', Blocks.FURNACE)
                .define('R', Items.REDSTONE_BLOCK)
                .unlockedBy(hasName(ModBlocks.machine_difurnace.get()), hasItem(ModBlocks.machine_difurnace.get()))
                .save(consumer);

        shaped(ModBlocks.machine_boiler.get())
                .pattern("SIS")
                .pattern("BFB")
                .pattern("SIS")
                .define('S', ModItems.PLATE_STEEL.get())
                .define('I', Items.IRON_BARS)
                .define('B', Items.BUCKET)
                .define('F', Blocks.FURNACE)
                .unlockedBy(hasName(Items.BUCKET), hasItem(Items.BUCKET))
                .save(consumer);

        shaped(ModBlocks.machine_electric_boiler.get())
                .pattern("SLS")
                .pattern("CBC")
                .pattern("SRS")
                .define('S', ModItems.PLATE_STEEL.get())
                .define('L', ModItems.COIL_COPPER.get())
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .define('B', ModBlocks.machine_boiler.get())
                .define('R', Items.REDSTONE)
                .unlockedBy(hasName(ModBlocks.machine_boiler.get()), hasItem(ModBlocks.machine_boiler.get()))
                .save(consumer);

        shaped(ModBlocks.machine_nuclear_boiler.get())
                .pattern("GPG")
                .pattern("BHB")
                .pattern("GUG")
                .define('G', ModItems.INGOT_GRAPHITE.get())
                .define('P', ModItems.PELLET_RTG.get())
                .define('B', ModBlocks.machine_electric_boiler.get())
                .define('H', ModItems.CIRCUIT_BASIC.get())
                .define('U', ModItems.INGOT_U235.get())
                .unlockedBy(hasName(ModBlocks.machine_electric_boiler.get()), hasItem(ModBlocks.machine_electric_boiler.get()))
                .save(consumer);

        shaped(ModBlocks.machine_press.get())
                .pattern("IPI")
                .pattern("BFB")
                .pattern("IPI")
                .define('I', Items.IRON_INGOT)
                .define('P', Blocks.PISTON)
                .define('B', Blocks.IRON_BLOCK)
                .define('F', Blocks.FURNACE)
                .unlockedBy(hasName(Items.IRON_INGOT), hasItem(Items.IRON_INGOT))
                .save(consumer);

        shaped(ModBlocks.machine_shredder.get())
                .pattern("SBS")
                .pattern("PEP")
                .pattern("SBS")
                .define('S', ModItems.PLATE_STEEL.get())
                .define('B', Items.IRON_BARS)
                .define('P', ModItems.SAWBLADE.get())
                .define('E', ModItems.CIRCUIT_BASIC.get())
                .unlockedBy(hasName(ModItems.SAWBLADE.get()), hasItem(ModItems.SAWBLADE.get()))
                .save(consumer);

        shaped(ModBlocks.machine_assembler.get())
                .pattern("SCS")
                .pattern("PMB")
                .pattern("SCS")
                .define('S', ModItems.PLATE_STEEL.get())
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .define('P', ModItems.PLATE_POLYMER.get())
                .define('M', ModItems.MOTOR.get())
                .define('B', Blocks.CRAFTING_TABLE)
                .unlockedBy(hasName(ModItems.MOTOR.get()), hasItem(ModItems.MOTOR.get()))
                .save(consumer);

        shaped(ModBlocks.machine_crucible.get())
                .pattern("FSF")
                .pattern("LBL")
                .pattern("FSF")
                .define('F', ModItems.INGOT_FIREBRICK.get())
                .define('S', ModItems.PLATE_STEEL.get())
                .define('L', Items.LAVA_BUCKET)
                .define('B', Items.BUCKET)
                .unlockedBy(hasName(ModItems.INGOT_FIREBRICK.get()), hasItem(ModItems.INGOT_FIREBRICK.get()))
                .save(consumer);

        shaped(ModBlocks.machine_cracking_tower.get())
                .pattern("SPS")
                .pattern("CHC")
                .pattern("SPS")
                .define('S', ModItems.PLATE_STEEL.get())
                .define('P', ModItems.PIPES_STEEL.get())
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .define('H', ModItems.COIL_COPPER.get())
                .unlockedBy(hasName(ModItems.PIPES_STEEL.get()), hasItem(ModItems.PIPES_STEEL.get()))
                .save(consumer);

        shaped(ModBlocks.machine_condenser.get())
                .pattern("SWS")
                .pattern("GBG")
                .pattern("SCS")
                .define('S', ModItems.PLATE_STEEL.get())
                .define('W', Items.WATER_BUCKET)
                .define('G', Blocks.GLASS)
                .define('B', Items.BUCKET)
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .unlockedBy(hasName(Blocks.GLASS), hasItem(Blocks.GLASS))
                .save(consumer);

        shaped(ModBlocks.machine_cooling_tower.get())
                .pattern("SBS")
                .pattern("GCG")
                .pattern("SWS")
                .define('S', ModItems.PLATE_STEEL.get())
                .define('B', Blocks.IRON_BARS)
                .define('G', Blocks.GLASS)
                .define('C', ModBlocks.machine_condenser.get())
                .define('W', Items.WATER_BUCKET)
                .unlockedBy(hasName(ModBlocks.machine_condenser.get()), hasItem(ModBlocks.machine_condenser.get()))
                .save(consumer);

        shaped(ModBlocks.machine_turbine_gas.get())
                .pattern("SCS")
                .pattern("TMT")
                .pattern("SRS")
                .define('S', ModItems.PLATE_STEEL.get())
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .define('T', ModItems.TURBINE_TITANIUM.get())
                .define('M', ModItems.MOTOR.get())
                .define('R', Items.REDSTONE_BLOCK)
                .unlockedBy(hasName(ModItems.TURBINE_TITANIUM.get()), hasItem(ModItems.TURBINE_TITANIUM.get()))
                .save(consumer);

        shaped(ModBlocks.machine_zirnox.get())
                .pattern("ZCZ")
                .pattern("RBR")
                .pattern("ZHZ")
                .define('Z', ModItems.rod_zirnox.get())
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .define('R', ModItems.rod_zirnox_tritium.get())
                .define('B', ModItems.INGOT_ZIRCONIUM.get())
                .define('H', ModItems.INGOT_SCHRABIDIUM.get())
                .unlockedBy(hasName(ModItems.rod_zirnox.get()), hasItem(ModItems.rod_zirnox.get()))
                .save(consumer);
    }

    private static void infrastructure(Consumer<FinishedRecipe> consumer) {
        shaped(ModBlocks.conveyor.get(), 6)
                .pattern("LLL")
                .pattern("SMS")
                .pattern("LLL")
                .define('L', Items.LEATHER)
                .define('S', ModItems.PLATE_STEEL.get())
                .define('M', ModItems.MOTOR.get())
                .unlockedBy(hasName(ModItems.MOTOR.get()), hasItem(ModItems.MOTOR.get()))
                .save(consumer);

        shaped(ModBlocks.crate_iron.get())
                .pattern("PPP")
                .pattern("I I")
                .pattern("III")
                .define('P', ModItems.PLATE_IRON.get())
                .define('I', Items.IRON_INGOT)
                .unlockedBy(hasName(ModItems.PLATE_IRON.get()), hasItem(ModItems.PLATE_IRON.get()))
                .save(consumer);

        shaped(ModBlocks.crate_steel.get())
                .pattern("PPP")
                .pattern("I I")
                .pattern("III")
                .define('P', ModItems.PLATE_STEEL.get())
                .define('I', ModItems.INGOT_STEEL.get())
                .unlockedBy(hasName(ModBlocks.crate_iron.get()), hasItem(ModBlocks.crate_iron.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModBlocks.RED_CABLE.get(), 4)
                .pattern(" P ")
                .pattern("WWW")
                .pattern(" P ")
                .define('P', ModItems.PLATE_POLYMER.get())
                .define('W', ModItems.WIRE_FINE_ALUMINIUM.get())
                .unlockedBy(hasName(ModItems.WIRE_FINE_ALUMINIUM.get()), hasItem(ModItems.WIRE_FINE_ALUMINIUM.get()))
                .save(consumer);
    }

    private static void batteryBlocks(Consumer<FinishedRecipe> consumer) {
        shaped(ModBlocks.machine_battery.get())
                .pattern("SRS")
                .pattern("CBC")
                .pattern("SRS")
                .define('S', ModItems.PLATE_STEEL.get())
                .define('R', Items.REDSTONE_BLOCK)
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .define('B', Ingredient.of(ModTags.Items.BATTERY))
                .unlockedBy(hasName(ModItems.PLATE_STEEL.get()), hasItem(ModItems.PLATE_STEEL.get()))
                .save(consumer);

        shaped(ModBlocks.machine_lithium_battery.get())
                .pattern("SSS")
                .pattern("LBL")
                .pattern("SSS")
                .define('S', ModItems.PLATE_STEEL.get())
                .define('L', ModItems.BATTERY_LITHIUM.get())
                .define('B', ModBlocks.machine_battery.get())
                .unlockedBy(hasName(ModBlocks.machine_battery.get()), hasItem(ModBlocks.machine_battery.get()))
                .save(consumer);

        shaped(ModBlocks.machine_schrabidium_battery.get())
                .pattern("PPP")
                .pattern("ABA")
                .pattern("PPP")
                .define('P', ModItems.PLATE_SCHRABIDIUM.get())
                .define('A', ModItems.BATTERY_ADVANCED.get())
                .define('B', ModBlocks.machine_lithium_battery.get())
                .unlockedBy(hasName(ModBlocks.machine_lithium_battery.get()), hasItem(ModBlocks.machine_lithium_battery.get()))
                .save(consumer);

        shaped(ModBlocks.machine_dineutronium_battery.get())
                .pattern("NDN")
                .pattern("LBL")
                .pattern("NDN")
                .define('N', ModItems.PLATE_DINEUTRONIUM.get())
                .define('D', ModItems.PELLET_RTG.get())
                .define('L', ModItems.BATTERY_ADVANCED.get())
                .define('B', ModBlocks.machine_schrabidium_battery.get())
                .unlockedBy(hasName(ModBlocks.machine_schrabidium_battery.get()), hasItem(ModBlocks.machine_schrabidium_battery.get()))
                .save(consumer);
    }

    private static void reactors(Consumer<FinishedRecipe> consumer) {
        shaped(ModBlocks.machine_reactor_breeding.get())
                .pattern("GGG")
                .pattern("CBC")
                .pattern("GTG")
                .define('G', ModItems.INGOT_GRAPHITE.get())
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .define('B', ModItems.PILE_ROD_LITHIUM.get())
                .define('T', ModItems.PILE_ROD_PLUTONIUM.get())
                .unlockedBy(hasName(ModItems.PILE_ROD_LITHIUM.get()), hasItem(ModItems.PILE_ROD_LITHIUM.get()))
                .save(consumer);

        shaped(ModBlocks.machine_research_reactor.get())
                .pattern("GGG")
                .pattern("CEC")
                .pattern("GLG")
                .define('G', ModItems.INGOT_GRAPHITE.get())
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .define('E', ModItems.PILE_ROD_SOURCE.get())
                .define('L', ModItems.PILE_ROD_BORON.get())
                .unlockedBy(hasName(ModItems.PILE_ROD_SOURCE.get()), hasItem(ModItems.PILE_ROD_SOURCE.get()))
                .save(consumer);
    }

    private static void rbmkPeripherals(Consumer<FinishedRecipe> consumer) {
        shaped(ModBlocks.machine_rbmk_console.get())
                .pattern("SCS")
                .pattern("RBR")
                .pattern("SCS")
                .define('S', ModItems.PLATE_STEEL.get())
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .define('R', Items.REDSTONE_TORCH)
                .define('B', ModItems.COIL_COPPER.get())
                .unlockedBy(hasName(ModBlocks.machine_rbmk_base.get()), hasItem(ModBlocks.machine_rbmk_base.get()))
                .save(consumer);

        shaped(ModBlocks.machine_rbmk_element.get())
                .pattern("GGG")
                .pattern("GPG")
                .pattern("GGG")
                .define('G', ModItems.INGOT_GRAPHITE.get())
                .define('P', ModItems.PILE_ROD_SOURCE.get())
                .unlockedBy(hasName(ModItems.INGOT_GRAPHITE.get()), hasItem(ModItems.INGOT_GRAPHITE.get()))
                .save(consumer);

        shaped(ModBlocks.machine_rbmk_reflector.get())
                .pattern("BBB")
                .pattern("GCG")
                .pattern("BBB")
                .define('B', ModItems.INGOT_BORON.get())
                .define('G', ModItems.INGOT_GRAPHITE.get())
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .unlockedBy(hasName(ModBlocks.machine_rbmk_element.get()), hasItem(ModBlocks.machine_rbmk_element.get()))
                .save(consumer);

        shaped(ModBlocks.machine_rbmk_autoloader.get())
                .pattern("SPS")
                .pattern("CMC")
                .pattern("SRS")
                .define('S', ModItems.PLATE_STEEL.get())
                .define('P', ModItems.PISTON_SET.get())
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .define('M', ModItems.MOTOR.get())
                .define('R', Items.REDSTONE_BLOCK)
                .unlockedBy(hasName(ModItems.MOTOR.get()), hasItem(ModItems.MOTOR.get()))
                .save(consumer);

        shaped(ModBlocks.machine_rbmk_crane_console.get())
                .pattern("SBS")
                .pattern("MCM")
                .pattern("SRS")
                .define('S', ModItems.PLATE_STEEL.get())
                .define('B', Blocks.IRON_BARS)
                .define('M', ModItems.MOTOR.get())
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .define('R', Items.REDSTONE)
                .unlockedBy(hasName(ModBlocks.machine_rbmk_console.get()), hasItem(ModBlocks.machine_rbmk_console.get()))
                .save(consumer);

        shaped(ModBlocks.machine_rbmk_debris.get(), 4)
                .pattern("GIG")
                .pattern("IFI")
                .pattern("GIG")
                .define('G', ModItems.INGOT_GRAPHITE.get())
                .define('I', Items.IRON_NUGGET)
                .define('F', ModBlocks.machine_rbmk_element.get())
                .unlockedBy(hasName(ModBlocks.machine_rbmk_element.get()), hasItem(ModBlocks.machine_rbmk_element.get()))
                .save(consumer);
    }

    private static void pwrComponents(Consumer<FinishedRecipe> consumer) {
        shaped(ModBlocks.pwr_casing.get(), 4)
                .pattern("LSL")
                .pattern("SPS")
                .pattern("LSL")
                .define('L', ModItems.PLATE_LEAD.get())
                .define('S', ModItems.PLATE_STEEL.get())
                .define('P', Items.SMOOTH_STONE)
                .unlockedBy(hasName(ModItems.PLATE_LEAD.get()), hasItem(ModItems.PLATE_LEAD.get()))
                .save(consumer);

        shaped(ModBlocks.pwr_controller.get())
                .pattern("SCS")
                .pattern("LPL")
                .pattern("SMS")
                .define('S', ModItems.PLATE_STEEL.get())
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .define('L', ModItems.PLATE_LEAD.get())
                .define('P', ModBlocks.pwr_casing.get())
                .define('M', ModItems.MOTOR.get())
                .unlockedBy(hasName(ModBlocks.pwr_casing.get()), hasItem(ModBlocks.pwr_casing.get()))
                .save(consumer);

        shaped(ModBlocks.pwr_port.get(), 4)
                .pattern("SPS")
                .pattern("CAC")
                .pattern("SPS")
                .define('S', ModItems.PLATE_STEEL.get())
                .define('P', ModItems.PIPES_STEEL.get())
                .define('C', ModItems.PLATE_LEAD.get())
                .define('A', ModBlocks.pwr_casing.get())
                .unlockedBy(hasName(ModItems.PIPES_STEEL.get()), hasItem(ModItems.PIPES_STEEL.get()))
                .save(consumer);

        shaped(ModBlocks.pwr_reflector.get(), 4)
                .pattern("BGB")
                .pattern("GSG")
                .pattern("BGB")
                .define('B', ModItems.INGOT_BORON.get())
                .define('G', ModItems.INGOT_GRAPHITE.get())
                .define('S', ModItems.PLATE_STEEL.get())
                .unlockedBy(hasName(ModItems.INGOT_BORON.get()), hasItem(ModItems.INGOT_BORON.get()))
                .save(consumer);

        shaped(ModBlocks.pwr_fuel_block.get(), 2)
                .pattern("SUS")
                .pattern("UCU")
                .pattern("SUS")
                .define('S', ModItems.PLATE_STEEL.get())
                .define('U', ModItems.INGOT_URANIUM_FUEL.get())
                .define('C', ModItems.INGOT_MOX_FUEL.get())
                .unlockedBy(hasName(ModItems.INGOT_URANIUM_FUEL.get()), hasItem(ModItems.INGOT_URANIUM_FUEL.get()))
                .save(consumer);

        shaped(ModBlocks.pwr_control.get(), 2)
                .pattern("SBS")
                .pattern("MCM")
                .pattern("SBS")
                .define('S', ModItems.PLATE_STEEL.get())
                .define('B', ModItems.INGOT_BORON.get())
                .define('M', ModItems.MOTOR.get())
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .unlockedBy(hasName(ModItems.INGOT_BORON.get()), hasItem(ModItems.INGOT_BORON.get()))
                .save(consumer);

        shaped(ModBlocks.pwr_channel.get(), 4)
                .pattern("PCP")
                .pattern("S S")
                .pattern("PCP")
                .define('P', ModItems.PIPES_STEEL.get())
                .define('C', ModItems.PLATE_COPPER.get())
                .define('S', ModItems.PLATE_STEEL.get())
                .unlockedBy(hasName(ModItems.PIPES_STEEL.get()), hasItem(ModItems.PIPES_STEEL.get()))
                .save(consumer);

        shaped(ModBlocks.pwr_heatex.get(), 2)
                .pattern("CSC")
                .pattern("M M")
                .pattern("CSC")
                .define('C', ModItems.PLATE_COPPER.get())
                .define('S', ModItems.PLATE_STEEL.get())
                .define('M', ModItems.MOTOR.get())
                .unlockedBy(hasName(ModItems.PLATE_COPPER.get()), hasItem(ModItems.PLATE_COPPER.get()))
                .save(consumer);

        shaped(ModBlocks.pwr_heatsink.get(), 2)
                .pattern("TCT")
                .pattern("CSC")
                .pattern("TCT")
                .define('T', ModItems.PLATE_TITANIUM.get())
                .define('C', ModItems.PLATE_COPPER.get())
                .define('S', ModItems.PLATE_STEEL.get())
                .unlockedBy(hasName(ModItems.PLATE_TITANIUM.get()), hasItem(ModItems.PLATE_TITANIUM.get()))
                .save(consumer);

        shaped(ModBlocks.pwr_neutron_source.get())
                .pattern("LSL")
                .pattern("SRS")
                .pattern("LSL")
                .define('L', ModItems.PLATE_LEAD.get())
                .define('S', ModItems.PLATE_STEEL.get())
                .define('R', ModItems.BILLET_RA226BE.get())
                .unlockedBy(hasName(ModItems.BILLET_RA226BE.get()), hasItem(ModItems.BILLET_RA226BE.get()))
                .save(consumer);
    }

    private static ShapedRecipeBuilder shaped(ItemLike output) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output);
    }

    private static ShapedRecipeBuilder shaped(ItemLike output, int count) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output, count);
    }

    private static String hasName(ItemLike item) {
        return "has_" + ForgeRegistries.ITEMS.getKey(item.asItem()).getPath();
    }

    private static InventoryChangeTrigger.TriggerInstance hasItem(ItemLike item) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(item);
    }
}
