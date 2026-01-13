package com.hbm.datagen.model;

import com.hbm.HBM;
import com.hbm.block.HBMBlockComponent;
import com.hbm.block.HBMMachine;
import com.hbm.registries.ModBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.registries.ForgeRegistries;

public class DecorateBlockStateProvider implements ICategoryStateProvider{
    BlockStateGen stateProvider;
    public DecorateBlockStateProvider(BlockStateGen stateProvider){
        this.stateProvider = stateProvider;
    }
    @Override
    public void registerStatesAndModels(){
        stateProvider.horizontalBlockWithItem(ModBlocks.TEST12.get(),"block/test12/test12");

        HBMBlockComponent.genModel(stateProvider);
    }
    private ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }
}
