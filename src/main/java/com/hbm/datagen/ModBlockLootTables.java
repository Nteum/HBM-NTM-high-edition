package com.hbm.datagen;

import com.hbm.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

public class ModBlockLootTables extends FabricBlockLootTableProvider {

    public ModBlockLootTables(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.ore_uranium); //掉落自身
        addDrop(ModBlocks.machine_electric_furnace);
        addDrop(ModBlocks.concrete);
        addDrop(ModBlocks.concrete_colored);
    }
}
