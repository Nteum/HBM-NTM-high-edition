package com.hbm;

import com.hbm.datagen.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class DalisFunModDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(ModLanguageGenerator::new);
        pack.addProvider(ModModelGenerator::new);
        pack.addProvider(ModBlockTagGenerator::new);
        pack.addProvider(ModBlockLootTables::new);
        pack.addProvider(ModRecipteGenerator::new);
        pack.addProvider(ModItemTagGenerator::new);
    }
}
