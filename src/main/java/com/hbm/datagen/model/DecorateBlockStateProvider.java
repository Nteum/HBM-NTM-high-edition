package com.hbm.datagen.model;

import com.hbm.HBM;
import com.hbm.block.HBMMachine;
import com.hbm.registries.ModBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.registries.ForgeRegistries;

public class DecorateBlockStateProvider implements ICategoryStateProvider{
    BlockStateProvider stateProvider;
    public DecorateBlockStateProvider(BlockStateProvider stateProvider){
        this.stateProvider = stateProvider;
    }
    @Override
    public void registerStatesAndModels(){
        horizontalBlockWithItem(ModBlocks.TEST12.get(),"block/test12/test12");
        stateProvider.cubeAll(HBMMachine.DEBUG_BLOCK.get());
    }
    protected void horizontalBlockWithItem(Block block, ModelFile model){
        stateProvider.horizontalBlock(block,model);
        stateProvider.simpleBlockItem(block,model);
    }
    protected void horizontalBlockWithItem(Block block, String path){
        ModelFile.ExistingModelFile model = stateProvider.models().getExistingFile(HBM.rl(path));
        stateProvider.horizontalBlock(block,model);
        stateProvider.simpleBlockItem(block,model);
    }
    protected void simpleBlockWithItem(Block block, String path){
        ModelFile.ExistingModelFile model = stateProvider.models().getExistingFile(HBM.rl(path));
        stateProvider.simpleBlockWithItem(block,model);
    }
    private ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }
}
