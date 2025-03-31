package com.hbm.datagen.model;

import com.hbm.HBM;
import com.hbm.block.env.BedRockOre;
import com.hbm.registries.ModBlocks;
import com.hbm.model.Models;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class BlockStateGen extends BlockStateProvider {
    private List<ICategoryStateProvider> categoryStateProviders = new ArrayList<>();
    public BlockStateGen(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
        super(output, modid, exFileHelper);
        categoryStateProviders.add(new DecorateBlockStateProvider(this));
    }

    @Override
    protected void registerStatesAndModels() {
        for (ICategoryStateProvider categoryStateProvider : categoryStateProviders) {
            categoryStateProvider.registerStatesAndModels();
        }
        //简单方块和物品
        horizontalBlockWithItem(ModBlocks.machine_battery.get(),this.models().orientable("machine_battery", new ResourceLocation(HBM.MODID, "block/battery_side"), new ResourceLocation(HBM.MODID, "block/battery_front"), new ResourceLocation(HBM.MODID, "block/battery_top")));
        horizontalBlockWithItem(ModBlocks.machine_lithium_battery.get(),this.models().orientable("machine_lithium_battery", new ResourceLocation(HBM.MODID, "block/battery_lithium_side"), new ResourceLocation(HBM.MODID, "block/battery_lithium_front"), new ResourceLocation(HBM.MODID, "block/battery_lithium_top")));
        horizontalBlockWithItem(ModBlocks.machine_schrabidium_battery.get(),this.models().orientable("machine_schrabidium_battery", new ResourceLocation(HBM.MODID, "block/battery_schrabidium_side"), new ResourceLocation(HBM.MODID, "block/battery_schrabidium_front"), new ResourceLocation(HBM.MODID, "block/battery_schrabidium_top")));
        horizontalBlockWithItem(ModBlocks.machine_dineutronium_battery.get(),this.models().orientable("machine_dineutronium_battery", new ResourceLocation(HBM.MODID, "block/battery_dineutronium_side"), new ResourceLocation(HBM.MODID, "block/battery_dineutronium_front"), new ResourceLocation(HBM.MODID, "block/battery_dineutronium_top")));

        simpleBlockWithItem(ModBlocks.WAST_EARTH.get(),this.models().cubeBottomTop("wast_earth", HBM.rl("block/env/waste_earth_side"), HBM.rl("block/env/waste_earth_bottom"), HBM.rl("block/env/waste_earth_top")));
        simpleBlockWithItem(ModBlocks.WAST_LEAVES.get(),this.models().leaves("wast_leaves", HBM.rl("block/env/waste_leaves")));
        simpleBlockWithItem(ModBlocks.URANIUM_ORE.get(),this.models().cubeAll("uranium_ore", HBM.rl("block/env/ore_uranium")));
        simpleBlockWithItem(ModBlocks.DEEPSLATE_URANIUM_ORE.get(),this.models().cubeAll("deepslate_uranium_ore", HBM.rl("block/env/ore_uranium_deepslate")));
        addEnumStateBlock(ModBlocks.BEDROCK_ORE.get(), BedRockOre.TYPE, (value)->enumModelFileFunction_BedRockOreType((BedRockOre.BedRockOreType) value));
        simpleBlockItem(ModBlocks.BEDROCK_ORE.get(), this.models().cubeAll("bedrock_ore", new ResourceLocation("block/bedrock")));
        simpleBlockWithItem(ModBlocks.RARE_EARTH_ORE.get(),this.models().cubeAll("rare_earth", HBM.rl("block/env/ore_rare")));
        simpleBlockWithItem(ModBlocks.DEEPSLATE_RARE_EARTH_ORE.get(),this.models().cubeAll("deepslate_rare_ore", HBM.rl("block/env/ore_rare_deepslate")));
        simpleBlockWithItem(ModBlocks.ASBESTOS_BLOCK.get(),this.models().cubeAll("asbestos_block", HBM.rl("block/env/block_asbestos")));
        simpleBlockWithItem(ModBlocks.ASBESTOS_ORE.get(),this.models().cubeAll("asbestos_ore", HBM.rl("block/env/ore_asbestos")));
        simpleBlockWithItem(ModBlocks.BASALT_ASBESTOS_ORE.get(),this.models().cubeTop("basalt_asbestos_ore", HBM.rl("block/env/ore_asbestos_basalt"), HBM.rl("block/env/ore_asbestos_basalt_top")));
        simpleBlockWithItem(ModBlocks.SA326_ORE.get(),this.models().cubeAll("sa326_ore", HBM.rl("block/env/ore_schrabidium")));
        simpleBlockWithItem(ModBlocks.LITHIUM_ORE.get(),this.models().cubeAll("lithium_ore", HBM.rl("block/env/ore_lithium")));
        simpleBlockWithItem(ModBlocks.DEPTH_STONE.get(),this.models().cubeAll("depth_stone", HBM.rl("block/env/stone_depth")));

        ModelFile.ExistingModelFile conveyorModel = this.models().getExistingFile(new ResourceLocation(HBM.MODID, "block/conveyor"));
        horizontalBlock(ModBlocks.conveyor.get(),conveyorModel);
        simpleBlockItem(ModBlocks.conveyor.get(),conveyorModel);
        //多状态的方块和物品
        //1. 高炉
        BlockModelBuilder machineDifurnace_off = this.models().orientableWithBottom("machine_difurnace_off", new ResourceLocation(HBM.MODID, "block/difurnace_side"), new ResourceLocation(HBM.MODID, "block/difurnace_front_off"), new ResourceLocation(HBM.MODID, "block/difurnace_bottom"), new ResourceLocation(HBM.MODID, "block/difurnace_top_off"));
        BlockModelBuilder machineDifurnace_on = this.models().orientableWithBottom("machine_difurnace_on", new ResourceLocation(HBM.MODID, "block/difurnace_side"), new ResourceLocation(HBM.MODID, "block/difurnace_front_on"),new ResourceLocation(HBM.MODID, "block/difurnace_bottom"), new ResourceLocation(HBM.MODID, "block/difurnace_top_on"));
        addBooleanStateWithFace(ModBlocks.machine_difurnace.get(), BlockStateProperties.LIT, machineDifurnace_off, machineDifurnace_on);
        //2. 电炉
        BlockModelBuilder machineElectricFurnaceOff = this.models().orientableWithBottom("machine_electric_furnace_off",
            new ResourceLocation(HBM.MODID, "block/machine_electric_furnace_side"),
            new ResourceLocation(HBM.MODID, "block/machine_electric_furnace_front_off"),
            new ResourceLocation(HBM.MODID, "block/machine_electric_furnace_bottom"),
            new ResourceLocation(HBM.MODID,"block/machine_electric_furnace_top"));
        BlockModelBuilder machineElectricFurnaceOn = this.models().orientableWithBottom("machine_electric_furnace_on",
            new ResourceLocation(HBM.MODID, "block/machine_electric_furnace_side"),
            new ResourceLocation(HBM.MODID, "block/machine_electric_furnace_front_on"),
            new ResourceLocation(HBM.MODID, "block/machine_electric_furnace_bottom"),
            new ResourceLocation(HBM.MODID,"block/machine_electric_furnace_top"));
        addBooleanStateWithFace(ModBlocks.machine_electric_furnace.get(), BlockStateProperties.LIT, machineElectricFurnaceOff, machineElectricFurnaceOn);
        //3. 加热器
        BlockModelBuilder machine_boiler_off = this.models().orientable("machine_boiler_off",
                new ResourceLocation(HBM.MODID, "block/machine_boiler_side"),
                new ResourceLocation(HBM.MODID, "block/machine_boiler_front"),
                new ResourceLocation(HBM.MODID, "block/machine_boiler_top"));
        BlockModelBuilder machine_boiler_on = this.models().orientable("machine_boiler_on",
                new ResourceLocation(HBM.MODID, "block/machine_boiler_side"),
                new ResourceLocation(HBM.MODID, "block/machine_boiler_front_lit"),
                new ResourceLocation(HBM.MODID, "block/machine_boiler_top"));
        addBooleanStateWithFace(ModBlocks.machine_boiler.get(), BlockStateProperties.LIT, machine_boiler_off, machine_boiler_on);
        //4. 电加热器
        BlockModelBuilder machine_electric_boiler_off = this.models().orientable("machine_electric_boiler_off",
                new ResourceLocation(HBM.MODID, "block/machine_boiler_side"),
                new ResourceLocation(HBM.MODID, "block/machine_boiler_electric_front"),
                new ResourceLocation(HBM.MODID, "block/machine_boiler_electric_top"));
        BlockModelBuilder machine_electric_boiler_on = this.models().orientable("machine_electric_boiler_on",
                new ResourceLocation(HBM.MODID, "block/machine_boiler_side"),
                new ResourceLocation(HBM.MODID, "block/machine_boiler_electric_front_lit"),
                new ResourceLocation(HBM.MODID, "block/machine_boiler_electric_top"));
        addBooleanStateWithFace(ModBlocks.machine_electric_boiler.get(), BlockStateProperties.LIT, machine_electric_boiler_off, machine_electric_boiler_on);
        //5. 核加热器
        BlockModelBuilder machine_nuclear_boiler_off = this.models().orientable("machine_nuclear_boiler_off",
                new ResourceLocation(HBM.MODID, "block/machine_boiler_nuclear_side"),
                new ResourceLocation(HBM.MODID, "block/machine_boiler_nuclear_front"),
                new ResourceLocation(HBM.MODID, "block/machine_boiler_top"));
        BlockModelBuilder machine_nuclear_boiler_on = this.models().orientable("machine_nuclear_boiler_on",
                new ResourceLocation(HBM.MODID, "block/machine_boiler_nuclear_side"),
                new ResourceLocation(HBM.MODID, "block/machine_boiler_nuclear_front_lit"),
                new ResourceLocation(HBM.MODID, "block/machine_boiler_top"));
        addBooleanStateWithFace(ModBlocks.machine_nuclear_boiler.get(), BlockStateProperties.LIT, machine_nuclear_boiler_off, machine_nuclear_boiler_on);
        //obj机器
        addObjHorizonalModel(ModBlocks.anvil_iron.get(),"block/anvil/anvil_iron");
        addObjHorizonalModel(ModBlocks.anvil_bismuth.get(),"block/anvil/anvil_bismuth");
        addObjHorizonalModel(ModBlocks.anvil_desh.get(),"block/anvil/anvil_desh");
        addObjHorizonalModel(ModBlocks.machine_cracking_tower.get(),"block/cracking_tower/machine_cracking_tower");
        var press_body_model = this.models().getExistingFile(new ResourceLocation(HBM.MODID, "block/press/press_body"));
        var press_head_model = this.models().getExistingFile(new ResourceLocation(HBM.MODID, "block/press/press_head"));
//        MultiPartBlockStateBuilder builder = this.getMultipartBuilder(ModBlocks.machine_press.get())
//                .part().modelFile(press_body_model).addModel().end()
//                .part().modelFile(press_head_model).addModel().end();
//        itemModels().getBuilder("hbmxx:item/machine_press").parent(press_body_model);
        this.simpleBlockWithItem(ModBlocks.machine_press.get(),press_body_model);
        this.simpleBlock(ModBlocks.part_press_head.get(),press_head_model);
        var assembler_body_model = this.models().getExistingFile(new ResourceLocation(HBM.MODID, "block/assembler/assembler_body"));
        var assembler_arm_model = this.models().getExistingFile(new ResourceLocation(HBM.MODID, "block/assembler/assembler_arm"));
        var assembler_cog_model = this.models().getExistingFile(new ResourceLocation(HBM.MODID, "block/assembler/assembler_cog"));
        var assembler_slider_model = this.models().getExistingFile(new ResourceLocation(HBM.MODID, "block/assembler/assembler_slider"));
        this.horizontalBlock(ModBlocks.machine_assembler.get(),assembler_body_model);
        this.simpleBlockItem(ModBlocks.machine_assembler.get(),assembler_body_model);
        //坩埚模型
        var crucible_model = this.models().getExistingFile(Models.CRUCIBLE);
        this.horizontalBlock(ModBlocks.machine_crucible.get(),crucible_model);
        this.simpleBlockItem(ModBlocks.machine_crucible.get(),crucible_model);

        ModelFile.ExistingModelFile bomb_model_fatman = this.models().getExistingFile(new ResourceLocation(HBM.MODID, "block/bomb/fat_man"));
        this.simpleBlockItem(ModBlocks.bomb_fat_man.get(),bomb_model_fatman);
        ModelFile.ExistingModelFile bomb_model_boy = this.models().getExistingFile(HBM.rl("block/bomb/boy"));
        this.simpleBlockItem(ModBlocks.bomb_boy.get(),bomb_model_boy);
        ModelFile.ExistingModelFile bomb_model_custom = this.models().getExistingFile(HBM.rl("block/bomb/custom"));
        this.simpleBlockItem(ModBlocks.bomb_custom.get(),bomb_model_custom);

        //线缆
        cableBlockWithItem();
        //电池
    }

    public void addObjHorizonalModel(Block block,String name){
        ModelFile.ExistingModelFile existingFile = this.models().getExistingFile(new ResourceLocation(HBM.MODID, name));
        this.horizontalBlock(block,existingFile);
        this.simpleBlockItem(block,existingFile);
    }
    /** 添加有两个状态，并带有水平方向的方块（HBM的方块机器大部分属于此列） */
    private void addBooleanStateWithFace(Block block, BooleanProperty booleanProperty, ModelFile model1, ModelFile model2){
        this.getVariantBuilder(block)
                .forAllStates(state -> {
                    Boolean value = state.getValue(booleanProperty);
                    return ConfiguredModel.builder()
                            .modelFile(value == Boolean.FALSE ? model1 : model2)
                            .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                            .build();
                });
        this.simpleBlockItem(block,model1);
    }

    private <E extends Enum<E> & StringRepresentable> void addEnumStateBlock(Block block, EnumProperty<E> enumProperty, Function<Enum<E>, ModelFile> enumModelFileFunction){
        this.getVariantBuilder(block)
                .forAllStates(state -> {
                    E value = state.getValue(enumProperty);
                    return ConfiguredModel.builder()
                            .modelFile(enumModelFileFunction.apply(value))
                            .build();
                });
    }

    public ModelFile enumModelFileFunction_BedRockOreType(BedRockOre.BedRockOreType value) {
        return switch (value){
            case IRON -> models().getExistingFile(HBM.rl("block/env/bedrock_ore_iron"));
            case COPPER -> models().getExistingFile(HBM.rl("block/env/bedrock_ore_copper"));
            default -> null;
        };
    }
    protected void horizontalBlockWithItem(Block block, ModelFile model){
        horizontalBlock(block,model);
        simpleBlockItem(block,model);
    }

    private void cableBlockWithItem(){
        ModelFile.ExistingModelFile inventory = this.models().getExistingFile(HBM.rl("block/pipes/cable_neo"));
        ModelFile.ExistingModelFile core = this.models().getExistingFile(HBM.rl("block/pipes/cable_core"));
        ModelFile.ExistingModelFile side = this.models().getExistingFile(HBM.rl("block/pipes/cable_side"));
        MultiPartBlockStateBuilder builder = this.getMultipartBuilder(ModBlocks.RED_CABLE.get()).part().modelFile(core).addModel().end();
        sixWayMultipart(builder,side);
        simpleBlockItem(ModBlocks.RED_CABLE.get(), inventory);
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

    private ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }
    private ResourceLocation hbm(String key){ return new ResourceLocation(HBM.MODID,key);}
}
