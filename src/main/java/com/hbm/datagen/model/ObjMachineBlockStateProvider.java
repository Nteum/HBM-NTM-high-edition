package com.hbm.datagen.model;

import com.hbm.HBM;
import com.hbm.block.HBMMachine;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;

public class ObjMachineBlockStateProvider extends DecorateBlockStateProvider{
    public ObjMachineBlockStateProvider(BlockStateGen stateProvider){
        super(stateProvider);
    }
    @Override
    public void registerStatesAndModels() {
        HBMMachine.model(stateProvider);
//        stateProvider.horizontalBlockWithItem(ModBlocks.machine_assembler.get(), "block/assembler/assembler_body");
        stateProvider.horizontalBlockWithItem(HBMMachine.CHEMPLANT.get(), "block/chemplant/chemplant_new_body");
        stateProvider.horizontalBlockWithItem(HBMMachine.PLASTIC_BARREL.get(), "block/barrel/barrel_plastic");
        stateProvider.horizontalBlockWithItem(HBMMachine.CORRODED_BARREL.get(), "block/barrel/barrel_corroded");
        stateProvider.horizontalBlockWithItem(HBMMachine.IRON_BARREL.get(), "block/barrel/barrel_iron");
        stateProvider.horizontalBlockWithItem(HBMMachine.STEEL_BARREL.get(), "block/barrel/barrel_steel");
        stateProvider.horizontalBlockWithItem(HBMMachine.TCALLOY_BARREL.get(), "block/barrel/barrel_tcalloy");
        stateProvider.horizontalBlockWithItem(HBMMachine.ANTIMATTER_BARREL.get(), "block/barrel/barrel_antimatter");
        //流体管道
        pipeBlockWithItem(HBMMachine.FLUID_PIPE.get());
    }

    private void pipeBlockWithItem(Block block){
        ModelFile.ExistingModelFile inventory = stateProvider.models().getExistingFile(HBM.rl("block/pipes/pipe_inv"));
        ModelFile.ExistingModelFile core = stateProvider.models().getExistingFile(HBM.rl("block/pipes/pipe_core"));
        ModelFile.ExistingModelFile north = stateProvider.models().getExistingFile(HBM.rl("block/pipes/pipe_north"));
        ModelFile.ExistingModelFile south = stateProvider.models().getExistingFile(HBM.rl("block/pipes/pipe_south"));
        ModelFile.ExistingModelFile east = stateProvider.models().getExistingFile(HBM.rl("block/pipes/pipe_east"));
        ModelFile.ExistingModelFile west = stateProvider.models().getExistingFile(HBM.rl("block/pipes/pipe_west"));
        ModelFile.ExistingModelFile up = stateProvider.models().getExistingFile(HBM.rl("block/pipes/pipe_up"));
        ModelFile.ExistingModelFile down = stateProvider.models().getExistingFile(HBM.rl("block/pipes/pipe_down"));
        MultiPartBlockStateBuilder builder = stateProvider.getMultipartBuilder(block).part().modelFile(core).addModel().end();
        builder.part().modelFile(east).addModel().condition(PipeBlock.EAST,true);
        builder.part().modelFile(west).addModel().condition(PipeBlock.WEST,true);
        builder.part().modelFile(north).addModel().condition(PipeBlock.NORTH,true);
        builder.part().modelFile(south).addModel().condition(PipeBlock.SOUTH,true);
        builder.part().modelFile(up).addModel().condition(PipeBlock.UP,true);
        builder.part().modelFile(down).addModel().condition(PipeBlock.DOWN,true);
        stateProvider.simpleBlockItem(block, inventory);
    }
    public void sixWayMultipart(MultiPartBlockStateBuilder builder, ModelFile side) {
        PipeBlock.PROPERTY_BY_DIRECTION.entrySet().forEach(e -> {
            Direction dir = e.getKey();
            if (dir.getAxis().isHorizontal()) {
                builder.part().modelFile(side).rotationY((((int) dir.toYRot()) + 180) % 360).uvLock(true).addModel()
                        .condition(e.getValue(), true);
            }else {
                builder.part().modelFile(side).rotationX(dir == Direction.UP ? -90 : 90).uvLock(true).addModel()
                        .condition(e.getValue(), true);
            }
        });
    }
}
