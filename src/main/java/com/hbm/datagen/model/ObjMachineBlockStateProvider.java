package com.hbm.datagen.model;

import com.hbm.HBM;
import com.hbm.block.HBMMachine;
import com.hbm.registries.ModBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;

public class ObjMachineBlockStateProvider extends DecorateBlockStateProvider{
    public ObjMachineBlockStateProvider(BlockStateProvider stateProvider){
        super(stateProvider);
    }
    @Override
    public void registerStatesAndModels() {
        horizontalBlockWithItem(ModBlocks.machine_assembler.get(), "block/assembler/assembler_body");
        horizontalBlockWithItem(HBMMachine.CHEMPLANT.get(), "block/chemplant/chemplant_new_body");
        horizontalBlockWithItem(HBMMachine.PLASTIC_BARREL.get(), "block/barrel/barrel_plastic");
        horizontalBlockWithItem(HBMMachine.CORRODED_BARREL.get(), "block/barrel/barrel_corroded");
        horizontalBlockWithItem(HBMMachine.IRON_BARREL.get(), "block/barrel/barrel_iron");
        horizontalBlockWithItem(HBMMachine.STEEL_BARREL.get(), "block/barrel/barrel_steel");
        horizontalBlockWithItem(HBMMachine.TCALLOY_BARREL.get(), "block/barrel/barrel_tcalloy");
        horizontalBlockWithItem(HBMMachine.ANTIMATTER_BARREL.get(), "block/barrel/barrel_antimatter");
    }
}
