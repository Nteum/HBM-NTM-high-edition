package com.hbm.datagen;

import com.hbm.block.ModBlocks;
import com.hbm.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class ModLanguageGenerator extends FabricLanguageProvider {
    public ModLanguageGenerator(FabricDataOutput dataOutput) {
        super(dataOutput,"en_us");
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        //物品表
        translationBuilder.add("itemGroup.dalisfunmod.hbm_parts", "Industry Parts");
        //物品
        translationBuilder.add(ModItems.ingot_uranium,"Uranium Ingot");
        translationBuilder.add(ModItems.ingot_u233,"Uranium-233 Ingot");
        translationBuilder.add(ModItems.ingot_u235,"Uranium-235 Ingot");
        translationBuilder.add(ModItems.ingot_u238,"Uranium-238 Ingot");
        translationBuilder.add(ModItems.nugget_uranium,"Uranium Nugget");
        translationBuilder.add(ModItems.billet_uranium,"Uranium Billet");
        //方块
        translationBuilder.add(ModBlocks.ore_uranium,"Uranium Ore");
    }
}
