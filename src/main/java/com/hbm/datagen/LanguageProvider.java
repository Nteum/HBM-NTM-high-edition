package com.hbm.datagen;

import com.hbm.HBMLang;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.api.text.ILangEntry;
import com.hbm.block.HBMBlockComponent;
import com.hbm.block.HBMMachine;
import com.hbm.blockentity.interfaces.IUpgradeInfoProvider;
import com.hbm.item.HBMCombat;
import com.hbm.item.HBMComponent;
import com.hbm.item.HBMItems;
import com.hbm.registries.HBMDamage;
import com.hbm.registries.ModKeyMapping;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import net.minecraft.data.PackOutput;

public class LanguageProvider extends net.minecraftforge.common.data.LanguageProvider {
    public LanguageProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        autoAdd();
        HBMItems.languageSupport(this);
        HBMDamage.languageSupport(this);
        ModFluids.localName(this);
        ModKeyMapping.localName(this);

        ModItems.languageSupport(this);
        ModBlocks.languageSupport(this);

        addCreativeTabs();
        addItems();
        addBlocks();
        addTooltip();
//        addLookTooltip();
//        addGeneral();

//        addEffect();
//        autoAdd();

//        //物品
//        this.add(ModItems.coke_coal.get(),"Coal Coke");
        //方块
        this.add(ModBlocks.machine_difurnace.get(),"Blast Furnace");
        this.add(ModBlocks.machine_electric_furnace.get(),"Electric Furnace");
        this.add(ModBlocks.machine_boiler.get(),"Oil Heater");
        this.add(ModBlocks.machine_electric_boiler.get(),"Electric Oil Heater");
        this.add(ModBlocks.machine_nuclear_boiler.get(),"Nuclear Oil Heater");
        this.add(ModBlocks.machine_battery.get(),"Energy Storage Block");
        this.add(ModBlocks.machine_lithium_battery.get(),"Li-Ion Energy Storage Block");
        this.add(ModBlocks.machine_schrabidium_battery.get(),"Schrabidium Energy Storage Block");
        this.add(ModBlocks.machine_dineutronium_battery.get(),"Spark Energy Storage Block");
        this.add(ModBlocks.anvil_iron.get(),"Tier 1 anvil");
        this.add(ModBlocks.machine_shredder.get(),"Shredder");
        this.add(ModBlocks.tokamak_controller.get(),"Tokamak Controller");
        this.add(ModBlocks.tokamak_casing.get(),"Tokamak Casing");
        this.add(ModBlocks.tokamak_coil.get(),"Tokamak Field Coil");
        this.add(ModBlocks.tokamak_heater.get(),"Tokamak Heater");
        this.add(ModBlocks.tokamak_injector.get(),"Tokamak Injector");
        this.add(ModBlocks.tokamak_port.get(),"Tokamak Port");
//        //方块实体
//        this.add("hbmxx.container.difurnace","Blast Furnace");
//        this.add("hbmxx.container.crucible","Crucible");
    }
    private void addCreativeTabs(){
//        this.add(HBMLang.ITEMGROUP_ITEM);
//        this.add(HBMLang.ITEMGROUP_BLOCK);
//        this.add(HBMLang.ITEMGROUP_MACHINE);
//        this.add(HBMLang.ITEMGROUP_TOOL);
//        this.add(HBMLang.ITEMGROUP_WEAPON);
    }
    private void addItems(){
        HBMComponent.languageSupport(this);
        HBMCombat.languageSupport(this);
    }
    private void addBlocks(){
        HBMMachine.languageSupport(this);
        HBMBlockComponent.languageSupport(this);
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
        this.add(ModBlocks.crate_iron.get(), "Iron Crate");
        this.add("container.hbm.crate_iron", "Iron Crate");
        this.add(ModBlocks.crate_steel.get(), "Steel Crate");
        this.add("container.hbm.crate_steel", "Steel Crate");
        this.add(ModBlocks.machine_wood_burner.get(), "Wood Burner Generator");
        this.add("container.hbm.machine_wood_burner", "Wood Burner Generator");
        this.add("tooltip.hbm.crate_empty", "[Empty]");
        this.add("tooltip.hbm.crate_more", "  and %s more...");
        this.add("tooltip.hbm.crate_fill", "  Used %s / %s slots");
        this.add(HBMItems.rbmk_control_rod.get(), "RBMK Control Rod");
        this.add(HBMItems.WOOD_ASH_POWDER.get(), "Wood Ash Powder");
        this.add("gui.hbm.wood_burner.no_fuel", "No fuel loaded.");
        this.add("gui.hbm.wood_burner.enabled", "Enabled");
        this.add("gui.hbm.wood_burner.disabled", "Disabled");
        this.add("gui.hbm.wood_burner.time", "%ss remaining");
    }
//    private void addContainer(){
//        this.add(HBMLang.DIFURNACE);
//        this.add(HBMLang.CRUCIBLE);
//        this.add(HBMLang.ELECTRIC_FURNACE);
//        this.add(HBMLang.BOILER);
//        this.add(HBMLang.ELECTRIC_BOILER);
//        this.add(HBMLang.NUCLEAR_BOILER);
//        this.add(HBMLang.ASSEMBLER);
//        this.add(HBMLang.CHEMPLANT);
//        this.add(HBMLang.SHREDDER);
//        this.add(HBMLang.BARREL);
//        this.add(HBMLang.BATTERY);
//        this.add(HBMLang.TOKAMAK);
//        this.add(HBMLang.RBMK);
//    }
//    private void addFluidTrait(){
//        this.add(HBMLang.FT_GASEOUS);
//        this.add(HBMLang.FT_GASEOUS_ART);
//        this.add(HBMLang.FT_LIQUID);
//        this.add(HBMLang.FT_VISCOUS);
//        this.add(HBMLang.FT_PLASMA);
//        this.add(HBMLang.FT_AMAT);
//        this.add(HBMLang.FT_LEAD_CONTAINER);
//        this.add(HBMLang.FT_DELICIOUS);
//        this.add(HBMLang.FT_UNSIPHONABLE);
//        this.add(HBMLang.FT_FLAME);
//        this.add(HBMLang.FT_VENT_RADIATION);
//        this.add(HBMLang.FT_COMBUSTIBLE1);
//        this.add(HBMLang.FT_COMBUSTIBLE2);
//        this.add(HBMLang.FT_COMBUSTIBLE3);
//        this.add(HBMLang.FT_THERMAL_CAPACITY);
//        this.add(HBMLang.FT_EFFICIENCY);
//        this.add(HBMLang.FT_CORROSIVE1);
//        this.add(HBMLang.FT_CORROSIVE2);
//        this.add(HBMLang.FT_FLAMMABLE1);
//        this.add(HBMLang.FT_FLAMMABLE2);
//        this.add(HBMLang.FT_HEATABLE1);
//        this.add(HBMLang.FT_PHEROMONE1);
//        this.add(HBMLang.FT_PHEROMONE2);
//        this.add(HBMLang.FT_POISON);
//        this.add(HBMLang.FT_PER_MB);
//        this.add(HBMLang.FT_POLLUTION1);
//        this.add(HBMLang.FT_POLLUTION2);
//        this.add(HBMLang.FT_POLLUTION3);
//        this.add(HBMLang.FT_PWRMODERATOR);
//        this.add(HBMLang.FT_CORE_FLUX);
//        this.add(HBMLang.FT_RADIOACTIVE);
//    }
//    private void addDebug(){
//        this.add(HBMLang.CACHED_DATA);
//        this.add(HBMLang.POS_DATA);
//        this.add(HBMLang.CHUNK_DATA);
//        this.add(HBMLang.BLOCK_STATE_LOSE);
//        this.add(HBMLang.BLOCK_STATE_INFO);
//    }
    private void autoAdd(){
        for (HBMLang value : HBMLang.values()) {
            this.add(value);
        }
    }
//    private void addEffect(){
//        this.add(HBMLang.EFFECT_RADIATION);
//    }
//    private void addGeneral(){
//        this.add(HBMLang.RECIPE);
//    }
//    private void addLookTooltip(){
//        this.add(HBMLang.LOOKTOOLTIP_CHEMPLANT);
//    }
    private void add(HBMLang entry){
        this.add(entry.key(), entry.content());
    }
}
