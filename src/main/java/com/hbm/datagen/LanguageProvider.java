package com.hbm.datagen;

import com.hbm.HBMLang;
import com.hbm.block.HBMMachine;
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
}
