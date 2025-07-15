package com.hbm.datagen;

import com.hbm.HBMLang;
import com.hbm.api.text.ILangEntry;
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
        //物品
        this.add(ModItems.ingot_steel.get(),"Steel Ignot");
        this.add(ModItems.plate_steel.get(),"Steel Plate");
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
        //方块实体
        this.add("hbmxx.container.difurnace","Blast Furnace");
        this.add("hbmxx.container.crucible","Crucible");
    }
    private void addCreativeTabs(){
        this.add(HBMLang.ITEMGROUP_ITEM.getTranslationKey(), "HBM Item");
        this.add(HBMLang.ITEMGROUP_BLOCK.getTranslationKey(), "HBM Block");
        this.add(HBMLang.ITEMGROUP_MACHINE.getTranslationKey(), "HBM Machine");
        this.add(HBMLang.ITEMGROUP_TOOL.getTranslationKey(), "HBM Tool");
    }
    private void addItems(){
        HBMComponent.languageSupport(this);
    }
    private void addBlocks(){
        HBMMachine.languageSupport(this);
    }
    private void addMisc(){
        this.add(HBMLang.ENERGY.getTranslationKey(), "Energy: %1$s");
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
    }

    private void add(ILangEntry entry,String value){
        this.add(entry.getTranslationKey(), value);
    }
}
