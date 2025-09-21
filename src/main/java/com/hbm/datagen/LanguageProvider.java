package com.hbm.datagen;

import com.hbm.HBMLang;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.api.text.ILangEntry;
import com.hbm.block.HBMBlockComponent;
import com.hbm.block.HBMMachine;
import com.hbm.blockentity.interfaces.IUpgradeInfoProvider;
import com.hbm.item.HBMComponent;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import net.minecraft.data.PackOutput;

public class LanguageProvider extends net.minecraftforge.common.data.LanguageProvider {
    public LanguageProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        addCreativeTabs();
        addItems();
        addBlocks();
        addMisc();
        addTooltip();
        addContainer();
        addFluidTrait();
        addLookTooltip();
        addDebug();
        addGeneral();
        ModFluids.localName(this);

        //物品
        this.add(ModItems.coke_coal.get(),"Coal Coke");
        //方块
        this.add(ModBlocks.URANIUM_ORE.get(),"Uranium Ore");
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
//        //方块实体
//        this.add("hbmxx.container.difurnace","Blast Furnace");
//        this.add("hbmxx.container.crucible","Crucible");
    }
    private void addCreativeTabs(){
        this.add(HBMLang.ITEMGROUP_ITEM.key(), "HBM Item");
        this.add(HBMLang.ITEMGROUP_BLOCK.key(), "HBM Block");
        this.add(HBMLang.ITEMGROUP_MACHINE.key(), "HBM Machine");
        this.add(HBMLang.ITEMGROUP_TOOL.key(), "HBM Tool");
    }
    private void addItems(){
        HBMComponent.languageSupport(this);
    }
    private void addBlocks(){
        HBMMachine.languageSupport(this);
        HBMBlockComponent.languageSupport(this);
    }
    private void addMisc(){
        this.add(HBMLang.ENERGY.key(), "Energy: %1$s");
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
        this.add(HBMLang.UPGRADE_RADIUS, "Forcefield Range Upgrade\nRadius +16 / Consumption +500\nStacks to 16");
        this.add(HBMLang.UPGRADE_HEALTH, "Forcefield Health Upgrade\nMax. Health +50 / Consumption +250\nStacks to 16");
        this.add(HBMLang.UPGRADE_SMELTER, "Mining Laser Upgrade\nSmelts blocks. Easy enough.");
        this.add(HBMLang.UPGRADE_SHREDDER, "Mining Laser Upgrade\nCrunches ores");
        this.add(HBMLang.UPGRADE_CENTRIFUGE, "Mining Laser Upgrade\nHopefully self-explanatory");
        this.add(HBMLang.UPGRADE_CRYSTALLIZER, "Mining Laser Upgrade\nYour new best friend");
        this.add(HBMLang.UPGRADE_SCREAM, "Mining Laser Upgrade\nIt's like in Super Mario where all blocks are\nactually Toads, but here it's Half-Life scientists\nand they scream. A lot.");
        this.add(HBMLang.UPGRADE_NULLIFIER, "Mining Laser Upgrade\n50% chance to override worthless items with /dev/zero\n50% chance to move worthless items to /dev/null");
        this.add(HBMLang.UPGRADE_GC_SPEED, "Gas Centrifuge Upgrade\nAllows for total isotopic separation of HEUF6\nalso your centrifuge goes sicko mode");

        this.add(HBMLang.TOOLTIP_LEFT_TIME, "Please wait %s s");
        this.add(HBMLang.TOOLTIP_TANK_VOLUME, "%s : %s mB");
        this.add(HBMLang.TOOLTIP_ENERGY, "Energy: %s HE");
    }
    private void addContainer(){
        this.add(HBMLang.DIFURNACE, "Blast Furnace");
        this.add(HBMLang.CRUCIBLE, "Crucible");
        this.add(HBMLang.ELECTRIC_FURNACE, "Electric Furnace");
        this.add(HBMLang.BOILER, "Boiler");
        this.add(HBMLang.ELECTRIC_BOILER, "Electric Boiler");
        this.add(HBMLang.NUCLEAR_BOILER, "Nuclear Boiler");
        this.add(HBMLang.ASSEMBLER, "Assembler");
        this.add(HBMLang.CHEMPLANT, "Chemical Plant");
//        this.add(HBMLang.BARREL, "HBM Barrel");
    }
    private void addFluidTrait(){
        this.add(HBMLang.FT_GASEOUS, "[Gaseous]");
        this.add(HBMLang.FT_GASEOUS_ART, "[Gaseous at Room Temperature]");
        this.add(HBMLang.FT_LIQUID, "[Liquid]");
        this.add(HBMLang.FT_VISCOUS, "[Viscous]");
        this.add(HBMLang.FT_AMAT, "[Antimatter]");
        this.add(HBMLang.FT_LEAD_CONTAINER, "[Requires hazardous material tank to hold]");
        this.add(HBMLang.FT_DELICIOUS, "[Delicious]");
        this.add(HBMLang.FT_UNSIPHONABLE, "[Ignored by siphon]");
        this.add(HBMLang.FT_COMBUSTIBLE1, "[Combustible]");
        this.add(HBMLang.FT_COMBUSTIBLE2, "Provides %s HE per bucket");
        this.add(HBMLang.FT_COMBUSTIBLE3, "Fuel grade: %s");
        this.add(HBMLang.FT_THERMAL_CAPACITY, "Thermal capacity: %s TU per %s mB");
        this.add(HBMLang.FT_EFFICIENCY, "[ %s ] Efficiency: %s %");
        this.add(HBMLang.FT_CORROSIVE1, "[Strongly Corrosive]");
        this.add(HBMLang.FT_CORROSIVE2, "[Corrosive]");
        this.add(HBMLang.FT_FLAMMABLE1, "[Flammable]");
        this.add(HBMLang.FT_FLAMMABLE2, "Provides %s TU per bucket");
        this.add(HBMLang.FT_PHEROMONE1, "[Glyphid Pheromones]");
        this.add(HBMLang.FT_PHEROMONE2, "[Modified Pheromones]");
        this.add(HBMLang.FT_POISON, "[Toxic Fumes]");
        this.add(HBMLang.FT_PER_MB, " - %s %s per mB");
        this.add(HBMLang.FT_POLLUTION1, "[Polluting]");
        this.add(HBMLang.FT_POLLUTION2, "When spilled:");
        this.add(HBMLang.FT_POLLUTION3, "When burned:");
        this.add(HBMLang.FT_PWRMODERATOR, "[PWR Flux Multiplier]");
        this.add(HBMLang.FT_CORE_FLUX, "Core flux + %s %");
        this.add(HBMLang.FT_RADIOACTIVE, "[Radioactive]");
    }
    private void addDebug(){
        this.add(HBMLang.CACHED_DATA, "Cached data: ");
        this.add(HBMLang.POS_DATA, "Block pos [%s]");
        this.add(HBMLang.CHUNK_DATA, "Chunk pos %s");
        this.add(HBMLang.BLOCK_STATE_LOSE, "Block in [%s] can't found !");
        this.add(HBMLang.BLOCK_STATE_INFO, "Block in [%s] is %s.");
    }
    private void addGeneral(){
        this.add(HBMLang.RECIPE, "recipe");
    }
    private void addLookTooltip(){
        this.add(HBMLang.LOOKTOOLTIP_CHEMPLANT, "<- tank %s");
    }
    private void add(ILangEntry entry,String value){
        this.add(entry.key(), value);
    }
}
