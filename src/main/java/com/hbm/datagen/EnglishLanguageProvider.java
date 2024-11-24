package com.hbm.datagen;

import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class EnglishLanguageProvider extends LanguageProvider {
    public EnglishLanguageProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        //创造模式物品栏
        this.add("itemGroup.hbm_item","HBM Item");
        this.add("itemGroup.hbm_block","HBM Block");
        this.add("itemGroup.hbm_machine","HBM Machine");
        this.add("itemGroup.hbm_tool","HBM Tool");
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
}
