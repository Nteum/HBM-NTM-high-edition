package com.hbm.datagen;

import com.hbm.HBMLang;
import com.hbm.Inventory.fluid.ModFluids;

import com.hbm.blockentity.interfaces.IUpgradeInfoProvider;
import com.hbm.item.HBMCombat;
import com.hbm.registries.HBMDamage;
import com.hbm.registries.ModKeyMapping;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import net.minecraft.data.PackOutput;

import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

public class LanguageProvider extends net.minecraftforge.common.data.LanguageProvider {
    public static final Map<String, String> READY_TO_ADD = new HashMap<>();
    private final Set<String> seenKeys = new HashSet<>();
    public LanguageProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        autoAdd();
        ModItems.languageSupport(this);
        HBMDamage.languageSupport(this);
        ModFluids.localName(this);
        ModKeyMapping.localName(this);

        // ModItems.languageSupport(this);
        ModBlocks.languageSupport(this);

        addItems();
        addBlocks();
        addTooltip();
        // addLookTooltip();
        // addGeneral();

        // addEffect();
        // autoAdd();

        // //物品
        // this.add(ModItems.coke_coal.get(),"Coal Coke");
        // 方块
        this.add(ModBlocks.machine_difurnace.get(), "Blast Furnace");
        this.add(ModBlocks.machine_electric_furnace.get(), "Electric Furnace");
        this.add(ModBlocks.machine_boiler.get(), "Oil Heater");
        this.add(ModBlocks.machine_electric_boiler.get(), "Electric Oil Heater");
        this.add(ModBlocks.machine_nuclear_boiler.get(), "Nuclear Oil Heater");
        this.add(ModBlocks.machine_battery.get(), "Energy Storage Block");
        this.add(ModBlocks.machine_lithium_battery.get(), "Li-Ion Energy Storage Block");
        this.add(ModBlocks.machine_schrabidium_battery.get(), "Schrabidium Energy Storage Block");
        this.add(ModBlocks.machine_dineutronium_battery.get(), "Spark Energy Storage Block");
        this.add(ModBlocks.anvil_iron.get(), "Tier 1 anvil");
        this.add(ModBlocks.machine_shredder.get(), "Shredder");
        this.add(ModBlocks.machine_condenser.get(), "Condenser");
        this.add(ModBlocks.machine_cooling_tower.get(), "Auxiliary Cooling Tower");
        this.add(ModBlocks.tokamak_controller.get(), "Tokamak Controller");
        this.add(ModBlocks.tokamak_casing.get(), "Tokamak Casing");
        this.add(ModBlocks.tokamak_coil.get(), "Tokamak Field Coil");
        this.add(ModBlocks.tokamak_heater.get(), "Tokamak Heater");
        this.add(ModBlocks.tokamak_injector.get(), "Tokamak Injector");
        this.add(ModBlocks.tokamak_port.get(), "Tokamak Port");
        this.add(ModBlocks.machine_icf.get(), "ICF Reactor");
        this.add(ModBlocks.machine_research_reactor.get(), "Research Reactor");
        this.add(ModBlocks.machine_reactor_breeding.get(), "Breeder Reactor");
        // //方块实体
        // this.add("hbmxx.container.difurnace","Blast Furnace");
        // this.add("hbmxx.container.crucible","Crucible");

        READY_TO_ADD.forEach(this::add);
    }

    @Override
    public void add(String key, String value) {
        if (!seenKeys.add(key)) {
            return;
        }
        super.add(key, value);
    }

    private void addItems() {
        // HBMComponent.languageSupport(this);
        HBMCombat.languageSupport(this);
//        this.add(HBMtools.POLLUTION_DETECTOR.get(), "Pollution Detector");
//        this.add(HBMtools.ORE_SCANNER.get(), "Ore Density Scanner");
    }

    private void addBlocks() {
//        HBMMachine.languageSupport(this);
//        HBMBlockComponent.languageSupport(this);
    }

    private void addTooltip(){
        this.add(IUpgradeInfoProvider.KEY_ACID,"Acid required %s");
        this.add(IUpgradeInfoProvider.KEY_BURN,"Burn %smb/t for %sHE");
        this.add(IUpgradeInfoProvider.KEY_CONSUMPTION,"Consumption %s");
        this.add(IUpgradeInfoProvider.KEY_COOLANT_CONSUMPTION,"Coolant Consumption %s");
        this.add(IUpgradeInfoProvider.KEY_DELAY,"Process time %s");
        this.add(IUpgradeInfoProvider.KEY_SPEED,"Process speed %s");
        this.add(IUpgradeInfoProvider.KEY_EFFICIENCY,"Fortune %s");
        this.add(IUpgradeInfoProvider.KEY_PRODUCTIVITY,"Productivity %s");
        this.add(IUpgradeInfoProvider.KEY_FORTUNE,"Fortune %s");
        this.add(IUpgradeInfoProvider.KEY_RANGE,"Range %s");
        this.add("gui.hbm.shredder.no_blade", "Error: Shredder blades are broken or missing!");
        this.add("gui.hbm.tokamak.start", "Start fusion");
        this.add("gui.hbm.tokamak.stop", "Scram");
        this.add("gui.hbm.tokamak.running", "Status: Online");
        this.add("gui.hbm.tokamak.idle", "Status: Idle");
        this.add("gui.hbm.rbmk.az5", "AZ-5 SCRAM");
        this.add("gui.hbm.rbmk.heat", "Heat: %1$s / %2$s kHE");
        this.add("gui.hbm.rbmk.energy", "Energy: %1$s / %2$s HE");
        this.add("gui.hbm.rbmk.coolant", "Coolant: %s mB");
        this.add("gui.hbm.rbmk.steam", "Steam: %s mB");
        this.add("gui.hbm.rbmk.control_local", "Local control: %s%%");
        this.add("gui.hbm.rbmk.control_global", "Global control: %s%%");
        this.add("gui.hbm.rbmk.no_column", "Column not registered");
        this.add("gui.hbm.rbmk.section.status", "Status");
        this.add("gui.hbm.rbmk.section.fluids", "Coolant loop");
        this.add("gui.hbm.rbmk.section.control", "Control");
        this.add("gui.hbm.rbmk.section.fuel", "Fuel channel");
        this.add("gui.hbm.rbmk.section.advanced", "Advanced data");
        this.add("gui.hbm.rbmk.details.show", "Show details");
        this.add("gui.hbm.rbmk.details.hide", "Hide details");
        this.add("gui.hbm.rbmk.energy_short", "HE: %s / %s");
        this.add("gui.hbm.rbmk.status", "Status: %s");
        this.add("gui.hbm.rbmk.status.offline", "Offline");
        this.add("gui.hbm.rbmk.status.running", "Running");
        this.add("gui.hbm.rbmk.status.warning", "Hot");
        this.add("gui.hbm.rbmk.status.critical", "Critical");
        this.add("gui.hbm.rbmk.fuel_progress", "Fuel: %s / %s ticks (%s%%)");
        this.add("gui.hbm.rbmk.heat_rate", "Heat throughput: %s HE/s");
        this.add("gui.hbm.rbmk.columns_online", "Columns online: %s");
        this.add("gui.hbm.rbmk.insert_up", "Insert +");
        this.add("gui.hbm.rbmk.insert_down", "Insert -");
        this.add("gui.hbm.rbmk.insert_scram", "AZ-5");
        this.add("gui.hbm.rbmk.insert_release", "Retract");
        this.add("gui.hbm.rbmk.insertion", "Control rod: %s / %s (%s%%)");
        this.add("gui.hbm.rbmk.control.az5_hint", "AZ-5: Emergency SCRAM entire column.");
        this.add("gui.hbm.rbmk.control.no_manual", "Automated channel – no manual controls.");
        this.add("gui.hbm.rbmk.peripheral_kind", "Peripheral: %s");
        this.add("gui.hbm.rbmk.control.slider_hint", "Manual override: drag the slider to adjust insertion depth.");
        this.add("gui.hbm.rbmk.slider.percent", "Manual override: %s%%");
        this.add("gui.hbm.rbmk.control.locked_hint", "Manual override locked during SCRAM response.");
        this.add("gui.hbm.rbmk.az5_engaged", "Emergency SCRAM active (%ss)");
        this.add("gui.hbm.rbmk.az5_locked", "AZ-5 cooling down – manual control disabled.");
        this.add("gui.hbm.rbmk.control.missing_rod", "Insert a control rod assembly to unlock manual control.");
        this.add("gui.hbm.rbmk.console.az5_hint", "AZ-5: Trip every control rod immediately.");
        this.add("gui.hbm.rbmk.console.idle", "Console idle — no linked columns.");
        this.add("gui.hbm.rbmk.action.coolant", "Coolant low — refill the loop.");
        this.add("gui.hbm.rbmk.action.dump_power", "Energy buffers full — route HE or power down.");
        this.add("gui.hbm.rbmk.action.normal", "Status nominal.");
        this.add("gui.hbm.rbmk.action.insert_rods", "Insert control rods / consider AZ-5.");
        this.add("gui.hbm.rbmk.action.raise_rods", "Rods fully inserted — you can raise them.");
        this.add("item.hbm.rbmk_tool", "RBMK Console Linking Device");
        this.add("item.hbm.rbmk_tool.desc1", "Sneak-right-click a RBMK column to store its location.");
        this.add("item.hbm.rbmk_tool.desc2", "Then sneak-right-click a console/crane to force-link it.");
        this.add("item.hbm.rbmk_tool.target", "Stored column: [%1$s, %2$s, %3$s]");
        this.add("item.hbm.rbmk_tool.target_missing", "No stored RBMK column.");
        this.add("item.hbm.rbmk_tool.linked", "Stored RBMK column at [%1$s, %2$s, %3$s].");
        this.add("item.hbm.rbmk_tool.set", "Linked %4$s to column [%1$s, %2$s, %3$s].");
        this.add("item.hbm.rbmk_tool.invalid", "No RBMK column at [%1$s, %2$s, %3$s].");
        this.add("item.hbm.dosimeter", "Dosimeter");
        this.add("item.hbm.dosimeter.desc1", "Passive Geiger counter tuned for environmental dose.");
        this.add("item.hbm.digamma_diagnostic", "Digamma Diagnostic");
        this.add("item.hbm.digamma_diagnostic.desc1", "Sneak-right-click to read personal digamma exposure.");
        this.add("container.hbm.crate_iron", "Iron Crate");
        this.add("container.hbm.crate_steel", "Steel Crate");
        this.add(ModBlocks.machine_wood_burner.get(), "Wood Burner Generator");
        this.add("container.hbm.machine_wood_burner", "Wood Burner Generator");
        this.add("tooltip.hbm.crate_empty", "[Empty]");
        this.add("tooltip.hbm.crate_more", "  and %s more...");
        this.add("tooltip.hbm.crate_fill", "  Used %s / %s slots");
        this.add(ModItems.rbmk_control_rod.get(), "RBMK Control Rod");
        this.add(ModBlocks.machine_icf_controller.get(), "ICF Laser Controller");
        this.add(ModBlocks.machine_icf_press.get(), "ICF Fuel Press");
        this.add(ModItems.icf_pellet.get(), "ICF Fuel Pellet");
        this.add(ModItems.icf_pellet_depleted.get(), "Depleted ICF Fuel Pellet");
        this.add(ModItems.icf_pellet_empty.get(), "Empty ICF Fuel Pellet");
        this.add(ModItems.plate_fuel_u233.get(), "U-233 Research Plate");
        this.add(ModItems.plate_fuel_u235.get(), "U-235 Research Plate");
        this.add(ModItems.plate_fuel_mox.get(), "MOX Research Plate");
        this.add(ModItems.plate_fuel_pu239.get(), "Pu-239 Research Plate");
        this.add(ModItems.plate_fuel_sa326.get(), "SA-326 Research Plate");
        this.add(ModItems.plate_fuel_ra226be.get(), "Ra-226/Be Source Plate");
        this.add(ModItems.plate_fuel_pu238be.get(), "Pu-238/Be Source Plate");
        this.add(ModItems.waste_plate_u233.get(), "Spent U-233 Plate");
        this.add(ModItems.waste_plate_u235.get(), "Spent U-235 Plate");
        this.add(ModItems.waste_plate_mox.get(), "Spent MOX Plate");
        this.add(ModItems.waste_plate_pu239.get(), "Spent Pu-239 Plate");
        this.add(ModItems.waste_plate_sa326.get(), "Spent SA-326 Plate");
        this.add(ModItems.waste_plate_ra226be.get(), "Spent Ra-226/Be Plate");
        this.add(ModItems.waste_plate_pu238be.get(), "Spent Pu-238/Be Plate");
        this.add(ModItems.WOOD_ASH_POWDER.get(), "Wood Ash Powder");
        this.add(ModItems.rod_breeder_single.get(), "Breeder Rod");
        this.add(ModItems.rod_breeder_dual.get(), "Dual Breeder Rod");
        this.add(ModItems.rod_breeder_quad.get(), "Quad Breeder Rod");
        this.add(ModItems.rod_empty.get(), "Empty Rod Casing");
        this.add(ModItems.rod_dual_empty.get(), "Empty Dual Rod Casing");
        this.add(ModItems.rod_quad_empty.get(), "Empty Quad Rod Casing");
        this.add("gui.hbm.wood_burner.no_fuel", "No fuel loaded.");
        this.add("gui.hbm.wood_burner.enabled", "Enabled");
        this.add("gui.hbm.wood_burner.disabled", "Disabled");
        this.add("gui.hbm.wood_burner.time", "%ss remaining");
        this.add("message.hbm.chicago.status.fuel", "Fuel channel: %1$s - Wear %2$s/%3$s (%4$s%%) - Heat %5$s/%6$s kHE (%7$s%%)");
        this.add("message.hbm.chicago.status.fuel_empty", "Fuel channel: empty");
        this.add("message.hbm.chicago.status.source", "Source channel: %1$s - Output %2$s n/t × %3$s beams");
        this.add("message.hbm.chicago.status.source_empty", "Source channel: empty");
        this.add("message.hbm.chicago.status.breeder", "Breeder channel: %1$s - Progress %2$s/%3$s (%4$s%%) - Last flux %5$s n/t");
        this.add("message.hbm.chicago.status.breeder_empty", "Breeder channel: empty");
        this.add("message.hbm.chicago.status.detector", "Detector channel: %1$s - Threshold %2$s n/t");
        this.add("message.hbm.chicago.status.detector_empty", "Detector channel: empty - Threshold %1$s n/t");
        this.add("message.hbm.chicago.status.unknown", "Chicago channel: no diagnostic data available.");
        this.add("message.hbm.pollution_detector", "Pollution – Soot: %1$s  Poison: %2$s  Heavy metal: %3$s");
        this.add("message.hbm.ore_scanner", "Ore scanner: %1$s ores within %2$s×%3$s blocks");
        this.add("tooltip.hbm.icf_controller", "Right-click to align with the reactor. Sneak-right-click to toggle.");
        this.add("gui.hbm.icf.heat", "Containment heat: %s kHE");
        this.add("gui.hbm.icf.coolant", "Coolant: %s / %s mB");
        this.add("gui.hbm.icf.coolant_hot", "Heated coolant: %s / %s mB");
        this.add("gui.hbm.icf_controller.enabled", "Controller enabled");
        this.add("gui.hbm.icf_controller.disabled", "Controller disabled");
        this.add("gui.hbm.icf_press.muon", "Stored muons: %s / %s");
        this.add("gui.hbm.icf_press.left", "Left fuel tank: %s / %s mB");
        this.add("gui.hbm.icf_press.right", "Right fuel tank: %s / %s mB");
        this.add("gui.hbm.icf.laser", "Laser input: %s kTU");
        this.add("jei.hbm.machine_icf.info1", "Multiblock: place the ICF Reactor facing an empty 17x7x17 chamber (offset one block forward) to auto-form the shell.");
        this.add("jei.hbm.machine_icf.info2", "Coolant ports: top is cold input, bottom is hot output. Four diagonal service pylons (+/-6, +3, +/-3) also expose the fluid capability.");
        this.add("jei.hbm.machine_icf.info3", "Load pellets into the five input slots; the core auto-feeds the active slot and converts 25 kHE of heat per mB into hot coolant.");
        this.add("jei.hbm.machine_icf_controller.info1", "Acts as a heavy laser; supply up to 50,000,000 HE via cables or batteries.");
        this.add("jei.hbm.machine_icf_controller.info2", "Right-click to rescan the target reactor, sneak-right-click to toggle. Needs a clear line-of-sight within 48 blocks.");
        this.add("jei.hbm.machine_icf_controller.info3", "Each controller pushes up to 400,000 TU per tick. Soft blocks along the beam are vaporized automatically.");
        this.add("jei.hbm.machine_icf_press.info1", "Combine an Empty ICF Pellet with two different fuels (cells/ingots) in the left and right inputs; the upper slots act as buffers.");
        this.add("jei.hbm.machine_icf_press.info2", "Muon capsules charge the press for 16 uses and return an empty particle capsule in the lower-right slot.");
        this.add("jei.hbm.machine_icf_press.info3", "Once both fuels are valid the press assembles a pellet automatically. Outputs can be extracted by hoppers or pipes.");
    }

    // private void addContainer(){
    // this.add(HBMLang.DIFURNACE);
    // this.add(HBMLang.CRUCIBLE);
    // this.add(HBMLang.ELECTRIC_FURNACE);
    // this.add(HBMLang.BOILER);
    // this.add(HBMLang.ELECTRIC_BOILER);
    // this.add(HBMLang.NUCLEAR_BOILER);
    // this.add(HBMLang.ASSEMBLER);
    // this.add(HBMLang.CHEMPLANT);
    // this.add(HBMLang.SHREDDER);
    // this.add(HBMLang.BARREL);
    // this.add(HBMLang.BATTERY);
    // this.add(HBMLang.TOKAMAK);
    // this.add(HBMLang.RBMK);
    // }
    // private void addFluidTrait(){
    // this.add(HBMLang.FT_GASEOUS);
    // this.add(HBMLang.FT_GASEOUS_ART);
    // this.add(HBMLang.FT_LIQUID);
    // this.add(HBMLang.FT_VISCOUS);
    // this.add(HBMLang.FT_PLASMA);
    // this.add(HBMLang.FT_AMAT);
    // this.add(HBMLang.FT_LEAD_CONTAINER);
    // this.add(HBMLang.FT_DELICIOUS);
    // this.add(HBMLang.FT_UNSIPHONABLE);
    // this.add(HBMLang.FT_FLAME);
    // this.add(HBMLang.FT_VENT_RADIATION);
    // this.add(HBMLang.FT_COMBUSTIBLE1);
    // this.add(HBMLang.FT_COMBUSTIBLE2);
    // this.add(HBMLang.FT_COMBUSTIBLE3);
    // this.add(HBMLang.FT_THERMAL_CAPACITY);
    // this.add(HBMLang.FT_EFFICIENCY);
    // this.add(HBMLang.FT_CORROSIVE1);
    // this.add(HBMLang.FT_CORROSIVE2);
    // this.add(HBMLang.FT_FLAMMABLE1);
    // this.add(HBMLang.FT_FLAMMABLE2);
    // this.add(HBMLang.FT_HEATABLE1);
    // this.add(HBMLang.FT_PHEROMONE1);
    // this.add(HBMLang.FT_PHEROMONE2);
    // this.add(HBMLang.FT_POISON);
    // this.add(HBMLang.FT_PER_MB);
    // this.add(HBMLang.FT_POLLUTION1);
    // this.add(HBMLang.FT_POLLUTION2);
    // this.add(HBMLang.FT_POLLUTION3);
    // this.add(HBMLang.FT_PWRMODERATOR);
    // this.add(HBMLang.FT_CORE_FLUX);
    // this.add(HBMLang.FT_RADIOACTIVE);
    // }
    // private void addDebug(){
    // this.add(HBMLang.CACHED_DATA);
    // this.add(HBMLang.POS_DATA);
    // this.add(HBMLang.CHUNK_DATA);
    // this.add(HBMLang.BLOCK_STATE_LOSE);
    // this.add(HBMLang.BLOCK_STATE_INFO);
    // }
    private void autoAdd() {
        for (HBMLang value : HBMLang.values()) {
            this.add(value);
        }
    }

    // private void addEffect(){
    // this.add(HBMLang.EFFECT_RADIATION);
    // }
    // private void addGeneral(){
    // this.add(HBMLang.RECIPE);
    // }
    // private void addLookTooltip(){
    // this.add(HBMLang.LOOKTOOLTIP_CHEMPLANT);
    // }
    private void add(HBMLang entry) {
        this.add(entry.key(), entry.content());
    }
}
