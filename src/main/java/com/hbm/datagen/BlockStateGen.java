package com.hbm.datagen;

import com.hbm.HBMxx;
import com.hbm.block.ModBlocks;
import com.hbm.model.Models;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.client.model.obj.ObjLoader;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockStateGen extends BlockStateProvider {
    public BlockStateGen(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
        super(output, modid, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        //简单方块和物品
        simpleBlockWithItem(ModBlocks.ore_uranium.get(),this.cubeAll(ModBlocks.ore_uranium.get()));
        simpleBlockWithItem(ModBlocks.machine_battery.get(),this.models().orientable("machine_battery",
                new ResourceLocation(HBMxx.MODID, "block/battery_side"),
                new ResourceLocation(HBMxx.MODID, "block/battery_front"),
                new ResourceLocation(HBMxx.MODID, "block/battery_top")));
        simpleBlockWithItem(ModBlocks.machine_lithium_battery.get(),this.models().orientable("machine_lithium_battery",
                new ResourceLocation(HBMxx.MODID, "block/battery_lithium_side"),
                new ResourceLocation(HBMxx.MODID, "block/battery_lithium_front"),
                new ResourceLocation(HBMxx.MODID, "block/battery_lithium_top")));
        simpleBlockWithItem(ModBlocks.machine_schrabidium_battery.get(),this.models().orientable("machine_schrabidium_battery",
                new ResourceLocation(HBMxx.MODID, "block/battery_schrabidium_side"),
                new ResourceLocation(HBMxx.MODID, "block/battery_schrabidium_front"),
                new ResourceLocation(HBMxx.MODID, "block/battery_schrabidium_top")));
        simpleBlockWithItem(ModBlocks.machine_dineutronium_battery.get(),this.models().orientable("machine_dineutronium_battery",
                new ResourceLocation(HBMxx.MODID, "block/battery_dineutronium_side"),
                new ResourceLocation(HBMxx.MODID, "block/battery_dineutronium_front"),
                new ResourceLocation(HBMxx.MODID, "block/battery_dineutronium_top")));

        ModelFile.ExistingModelFile conveyorModel = this.models().getExistingFile(new ResourceLocation(HBMxx.MODID, "block/conveyor"));
        horizontalBlock(ModBlocks.conveyor.get(),conveyorModel);
        simpleBlockItem(ModBlocks.conveyor.get(),conveyorModel);
        //多状态的方块和物品
        //1. 高炉
        BlockModelBuilder machineDifurnace_off = this.models().orientableWithBottom("machine_difurnace_off",
            new ResourceLocation(HBMxx.MODID, "block/difurnace_side"),
            new ResourceLocation(HBMxx.MODID, "block/difurnace_front_off"),
            new ResourceLocation(HBMxx.MODID, "block/difurnace_bottom"),
            new ResourceLocation(HBMxx.MODID, "block/difurnace_top_off"));
        BlockModelBuilder machineDifurnace_on = this.models().orientableWithBottom("machine_difurnace_on",
            new ResourceLocation(HBMxx.MODID, "block/difurnace_side"),
            new ResourceLocation(HBMxx.MODID, "block/difurnace_front_on"),
                new ResourceLocation(HBMxx.MODID, "block/difurnace_bottom"),
            new ResourceLocation(HBMxx.MODID, "block/difurnace_top_on"));
        this.getVariantBuilder(ModBlocks.machine_difurnace.get())
            .forAllStates(state -> {
                Boolean value = state.getValue(BlockStateProperties.LIT);
                return ConfiguredModel.builder()
                    .modelFile(value == Boolean.FALSE ? machineDifurnace_off : machineDifurnace_on)
                    .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                    .build();
                });
        this.simpleBlockItem(ModBlocks.machine_difurnace.get(),machineDifurnace_off);
        //2. 电炉
        BlockModelBuilder machineElectricFurnaceOff = this.models().orientableWithBottom("machine_electric_furnace_off",
            new ResourceLocation(HBMxx.MODID, "block/machine_electric_furnace_side"),
            new ResourceLocation(HBMxx.MODID, "block/machine_electric_furnace_front_off"),
            new ResourceLocation(HBMxx.MODID, "block/machine_electric_furnace_bottom"),
            new ResourceLocation(HBMxx.MODID,"block/machine_electric_furnace_top"));
        BlockModelBuilder machineElectricFurnaceOn = this.models().orientableWithBottom("machine_electric_furnace_on",
            new ResourceLocation(HBMxx.MODID, "block/machine_electric_furnace_side"),
            new ResourceLocation(HBMxx.MODID, "block/machine_electric_furnace_front_on"),
            new ResourceLocation(HBMxx.MODID, "block/machine_electric_furnace_bottom"),
            new ResourceLocation(HBMxx.MODID,"block/machine_electric_furnace_top"));
        this.getVariantBuilder(ModBlocks.machine_electric_furnace.get())
                .forAllStates(state -> {
                    Boolean value = state.getValue(BlockStateProperties.LIT);
                    return ConfiguredModel.builder()
                            .modelFile(value == Boolean.FALSE ? machineElectricFurnaceOff : machineElectricFurnaceOn)
                            .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                            .build();
                });
        this.simpleBlockItem(ModBlocks.machine_electric_furnace.get(),machineElectricFurnaceOff);
        //3. 加热器
        BlockModelBuilder machine_boiler_off = this.models().orientable("machine_boiler_off",
                new ResourceLocation(HBMxx.MODID, "block/machine_boiler_side"),
                new ResourceLocation(HBMxx.MODID, "block/machine_boiler_front"),
                new ResourceLocation(HBMxx.MODID, "block/machine_boiler_top"));
        BlockModelBuilder machine_boiler_on = this.models().orientable("machine_boiler_on",
                new ResourceLocation(HBMxx.MODID, "block/machine_boiler_side"),
                new ResourceLocation(HBMxx.MODID, "block/machine_boiler_front_lit"),
                new ResourceLocation(HBMxx.MODID, "block/machine_boiler_top"));
        this.getVariantBuilder(ModBlocks.machine_boiler.get())
                .forAllStates(state -> {
                    Boolean value = state.getValue(BlockStateProperties.LIT);
                    return ConfiguredModel.builder()
                            .modelFile(value == Boolean.FALSE ? machine_boiler_off : machine_boiler_on)
                            .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                            .build();
                });
        this.simpleBlockItem(ModBlocks.machine_boiler.get(),machine_boiler_off);
        //4. 电加热器
        BlockModelBuilder machine_electric_boiler_off = this.models().orientable("machine_electric_boiler_off",
                new ResourceLocation(HBMxx.MODID, "block/machine_boiler_side"),
                new ResourceLocation(HBMxx.MODID, "block/machine_boiler_electric_front"),
                new ResourceLocation(HBMxx.MODID, "block/machine_boiler_electric_top"));
        BlockModelBuilder machine_electric_boiler_on = this.models().orientable("machine_electric_boiler_on",
                new ResourceLocation(HBMxx.MODID, "block/machine_boiler_side"),
                new ResourceLocation(HBMxx.MODID, "block/machine_boiler_electric_front_lit"),
                new ResourceLocation(HBMxx.MODID, "block/machine_boiler_electric_top"));
        this.getVariantBuilder(ModBlocks.machine_electric_boiler.get())
                .forAllStates(state -> {
                    Boolean value = state.getValue(BlockStateProperties.LIT);
                    return ConfiguredModel.builder()
                            .modelFile(value == Boolean.FALSE ? machine_electric_boiler_off : machine_electric_boiler_on)
                            .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                            .build();
                });
        this.simpleBlockItem(ModBlocks.machine_electric_boiler.get(),machine_electric_boiler_off);
        //5. 核加热器
        BlockModelBuilder machine_nuclear_boiler_off = this.models().orientable("machine_nuclear_boiler_off",
                new ResourceLocation(HBMxx.MODID, "block/machine_boiler_nuclear_side"),
                new ResourceLocation(HBMxx.MODID, "block/machine_boiler_nuclear_front"),
                new ResourceLocation(HBMxx.MODID, "block/machine_boiler_top"));
        BlockModelBuilder machine_nuclear_boiler_on = this.models().orientable("machine_nuclear_boiler_on",
                new ResourceLocation(HBMxx.MODID, "block/machine_boiler_nuclear_side"),
                new ResourceLocation(HBMxx.MODID, "block/machine_boiler_nuclear_front_lit"),
                new ResourceLocation(HBMxx.MODID, "block/machine_boiler_top"));
        this.getVariantBuilder(ModBlocks.machine_nuclear_boiler.get())
                .forAllStates(state -> {
                    Boolean value = state.getValue(BlockStateProperties.LIT);
                    return ConfiguredModel.builder()
                            .modelFile(value == Boolean.FALSE ? machine_nuclear_boiler_off : machine_nuclear_boiler_on)
                            .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                            .build();
                });
        this.simpleBlockItem(ModBlocks.machine_nuclear_boiler.get(),machine_nuclear_boiler_off);
        //obj机器
        addObjHorizonalModel(ModBlocks.anvil_iron.get(),"block/anvil/anvil_iron");
        addObjHorizonalModel(ModBlocks.anvil_bismuth.get(),"block/anvil/anvil_bismuth");
        addObjHorizonalModel(ModBlocks.anvil_desh.get(),"block/anvil/anvil_desh");
        addObjHorizonalModel(ModBlocks.machine_cracking_tower.get(),"block/cracking_tower/machine_cracking_tower");
        var press_body_model = this.models().getExistingFile(new ResourceLocation(HBMxx.MODID, "block/press/press_body"));
        var press_head_model = this.models().getExistingFile(new ResourceLocation(HBMxx.MODID, "block/press/press_head"));
//        MultiPartBlockStateBuilder builder = this.getMultipartBuilder(ModBlocks.machine_press.get())
//                .part().modelFile(press_body_model).addModel().end()
//                .part().modelFile(press_head_model).addModel().end();
//        itemModels().getBuilder("hbmxx:item/machine_press").parent(press_body_model);
        this.simpleBlockWithItem(ModBlocks.machine_press.get(),press_body_model);
        this.simpleBlock(ModBlocks.part_press_head.get(),press_head_model);
        var assembler_body_model = this.models().getExistingFile(new ResourceLocation(HBMxx.MODID, "block/assembler/assembler_body"));
        var assembler_arm_model = this.models().getExistingFile(new ResourceLocation(HBMxx.MODID, "block/assembler/assembler_arm"));
        var assembler_cog_model = this.models().getExistingFile(new ResourceLocation(HBMxx.MODID, "block/assembler/assembler_cog"));
        var assembler_slider_model = this.models().getExistingFile(new ResourceLocation(HBMxx.MODID, "block/assembler/assembler_slider"));
        this.horizontalBlock(ModBlocks.machine_assembler.get(),assembler_body_model);
        this.simpleBlockItem(ModBlocks.machine_assembler.get(),assembler_body_model);
        //坩埚模型
        var crucible_model = this.models().getExistingFile(Models.CRUCIBLE);
        this.horizontalBlock(ModBlocks.machine_crucible.get(),crucible_model);
        this.simpleBlockItem(ModBlocks.machine_crucible.get(),crucible_model);

        ModelFile.ExistingModelFile bomb_model_fatman = this.models().getExistingFile(new ResourceLocation(HBMxx.MODID, "block/bomb/fat_man"));
//        this.horizontalBlock(ModBlocks.bomb_fat_man.get(),bomb_model_fatman);
        this.simpleBlockItem(ModBlocks.bomb_fat_man.get(),bomb_model_fatman);
    }

    public void addObjHorizonalModel(Block block,String name){
        ModelFile.ExistingModelFile existingFile = this.models().getExistingFile(new ResourceLocation(HBMxx.MODID, name));
        this.horizontalBlock(block,existingFile);
        this.simpleBlockItem(block,existingFile);
    }
//    public void addBombItem(Block block,ModelFile model){
//        this.itemModels().getBuilder(key(block).getPath())
//                .parent(model)
//                .customLoader();
//    }
    private ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }
}
