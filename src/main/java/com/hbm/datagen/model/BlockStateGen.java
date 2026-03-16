package com.hbm.datagen.model;

import com.hbm.HBM;
import com.hbm.block.HBMBlockProperties;
import com.hbm.block.base.BlockDummyable;
import com.hbm.block.env.BedRockOre;
import com.hbm.registries.ModBlocks;
import com.hbm.render.model.Models;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class BlockStateGen extends BlockStateProvider {
    private List<ICategoryStateProvider> categoryStateProviders = new ArrayList<>();
    public BlockStateGen(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
        super(output, modid, exFileHelper);
        categoryStateProviders.add(new DecorateBlockStateProvider(this));
        categoryStateProviders.add(new ObjMachineBlockStateProvider(this));
    }

    @Override
    protected void registerStatesAndModels() {
        HBM.LOGGER.info("HBM BlockStateGen: generating block and item models");
        for (ICategoryStateProvider categoryStateProvider : categoryStateProviders) {
            categoryStateProvider.registerStatesAndModels();
        }
        ModBlocks.genModel(this);
        //简单方块和物品
        horizontalBlockWithItem(ModBlocks.machine_battery.get(),this.models().orientable("machine_battery", new ResourceLocation(HBM.MODID, "block/battery_side"), new ResourceLocation(HBM.MODID, "block/battery_front"), new ResourceLocation(HBM.MODID, "block/battery_top")));
        horizontalBlockWithItem(ModBlocks.machine_lithium_battery.get(),this.models().orientable("machine_lithium_battery", new ResourceLocation(HBM.MODID, "block/battery_lithium_side"), new ResourceLocation(HBM.MODID, "block/battery_lithium_front"), new ResourceLocation(HBM.MODID, "block/battery_lithium_top")));
        horizontalBlockWithItem(ModBlocks.machine_schrabidium_battery.get(),this.models().orientable("machine_schrabidium_battery", new ResourceLocation(HBM.MODID, "block/battery_schrabidium_side"), new ResourceLocation(HBM.MODID, "block/battery_schrabidium_front"), new ResourceLocation(HBM.MODID, "block/battery_schrabidium_top")));
        horizontalBlockWithItem(ModBlocks.machine_dineutronium_battery.get(),this.models().orientable("machine_dineutronium_battery", new ResourceLocation(HBM.MODID, "block/battery_dineutronium_side"), new ResourceLocation(HBM.MODID, "block/battery_dineutronium_front"), new ResourceLocation(HBM.MODID, "block/battery_dineutronium_top")));
        horizontalBlockWithItem(ModBlocks.machine_shredder.get(),
            this.models().orientableWithBottom("machine_shredder",
                new ResourceLocation(HBM.MODID, "block/machine_shredder_side"),
                new ResourceLocation(HBM.MODID, "block/machine_shredder_front"),
                new ResourceLocation(HBM.MODID, "block/machine_shredder_bottom"),
                new ResourceLocation(HBM.MODID, "block/machine_shredder_top")));
        // Tokamak components
        ResourceLocation tokamakSide = new ResourceLocation(HBM.MODID, "block/tokamak_coil");
        ResourceLocation tokamakFront = new ResourceLocation(HBM.MODID, "block/tokamak_controller_front");
        ModelFile tokamakController = this.models().orientableWithBottom("tokamak_controller", tokamakSide, tokamakFront, tokamakSide, tokamakSide);
        horizontalBlockWithItem(ModBlocks.tokamak_controller.get(), tokamakController);
        simpleBlockWithItem(ModBlocks.tokamak_casing.get(), this.models().cubeAll("tokamak_casing", new ResourceLocation(HBM.MODID, "block/block_steel")));
        simpleBlockWithItem(ModBlocks.tokamak_coil.get(), this.models().cubeAll("tokamak_coil", tokamakSide));
        simpleBlockWithItem(ModBlocks.tokamak_heater.get(), this.models().cubeAll("tokamak_heater", tokamakSide));
        simpleBlockWithItem(ModBlocks.tokamak_injector.get(), this.models().cubeAll("tokamak_injector", tokamakSide));
        simpleBlockWithItem(ModBlocks.tokamak_port.get(), this.models().cubeAll("tokamak_port", tokamakSide));

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
        ModelFile.ExistingModelFile bomb_model_fatman = this.models().getExistingFile(new ResourceLocation(HBM.MODID, "block/bomb/fat_man"));
        this.simpleBlockItem(ModBlocks.bomb_fat_man.get(),bomb_model_fatman);
        ModelFile.ExistingModelFile bomb_model_boy = this.models().getExistingFile(HBM.rl("block/bomb/boy"));
        this.simpleBlockItem(ModBlocks.bomb_boy.get(),bomb_model_boy);
        ModelFile.ExistingModelFile bomb_model_custom = this.models().getExistingFile(HBM.rl("block/bomb/custom"));
        this.simpleBlockItem(ModBlocks.bomb_custom.get(),bomb_model_custom);

        //线缆
        cableBlockWithItem();
        // 新体系添加的物品
        simpleBlockWithItem(ModBlocks.WASTE_LEAVES.get(), genBuiltInModelFile(ModBlocks.WASTE_LEAVES.get(), "leaves"));
        simpleBlockWithItem(ModBlocks.WASTE_GRASS.get(), genBuiltInModelFile(ModBlocks.WASTE_GRASS.get(), "cube_bottom_top"));
        addObjHorizonalModel(ModBlocks.SPACE_STATION_BASE.get(), "block/space_station_base");
        simpleBlockWithItem(ModBlocks.CONNECTOR.get(), genBuiltInModelFile(ModBlocks.CONNECTOR.get(), "existing"));
        addObjHorizonalModel(ModBlocks.machine_assembler.get(), "block/assembler_body");
        addObjHorizonalModel(ModBlocks.machine_press.get(), "block/press");
        addObjHorizonalModel(ModBlocks.HEATER_FIREBOX.get(), "block/firebox");
        addObjHorizonalModel(ModBlocks.anvil_iron.get(),"block/anvil/anvil_iron");
        addObjHorizonalModel(ModBlocks.anvil_bismuth.get(),"block/anvil/anvil_bismuth");
        addObjHorizonalModel(ModBlocks.anvil_desh.get(),"block/anvil/anvil_desh");
        addObjHorizonalModel(ModBlocks.machine_cracking_tower.get(),"block/cracking_tower/machine_cracking_tower");
        addObjHorizonalModel(ModBlocks.machine_crucible.get(), "block/crucible");
    }
    // 方块和物品：纯cube all
    public void simpleBlockWithItem(Block block){
        ModelFile modelFile = cubeAll(block);
        simpleBlock(block, modelFile);
        simpleBlockItem(block, modelFile);
    }
    public void simpleBlockWithItem(Block block, String path){
        ModelFile.ExistingModelFile model = models().getExistingFile(HBM.rl(path));
        simpleBlockWithItem(block,model);
    }
    public void horizontalBlockWithItem(Block block){
        horizontalBlockWithItem(block, key(block).getPath());
    }
    public void horizontalBlockWithItem(Block block, String path){
        ModelFile.ExistingModelFile model = models().getExistingFile(HBM.rl(path));
        horizontalBlockWithItem(block, model);
    }
    public void horizontalBlockWithItem(Block block, ModelFile model){
        horizontalBlock(block,model);
        simpleBlockItem(block,model);
    }

    public void addObjHorizonalModel(Block block,String name){
        ModelFile.ExistingModelFile existingFile = this.models().getExistingFile(new ResourceLocation(HBM.MODID, name));
//        this.horizontalBlock(block,existingFile);
        getVariantBuilder(block)
                .forAllStatesExcept(state -> ConfiguredModel.builder()
                        .modelFile(existingFile)
                        .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                        .build(), HBMBlockProperties.IS_CORE);
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

    public  <E extends Enum<E> & StringRepresentable> void addEnumStateBlock(Block block, EnumProperty<E> enumProperty, Function<Enum<E>, ModelFile> enumModelFileFunction){
        this.getVariantBuilder(block)
                .forAllStates(state -> {
                    E value = state.getValue(enumProperty);
                    return ConfiguredModel.builder()
                            .modelFile(enumModelFileFunction.apply(value))
                            .build();
                });
    }

    public void addIntStateCubeAllBlock(Block block, IntegerProperty integerProperty){
        ResourceLocation blockTexture = blockTexture(block);
        VariantBlockStateBuilder variantBuilder = this.getVariantBuilder(block);
        VariantBlockStateBuilder.PartialBlockstate partialState = variantBuilder.partialState();
        for (Integer value : integerProperty.getPossibleValues()) {
            partialState.with(integerProperty, value).addModels(ConfiguredModel.builder().modelFile(models().cubeAll(name(block) + "_" + value, blockTexture.withSuffix("_" + value))).buildLast());
        }
    }

    public ModelFile enumModelFileFunction_BedRockOreType(BedRockOre.BedRockOreType value) {
        return switch (value){
            case IRON -> models().getExistingFile(HBM.rl("block/env/bedrock_ore_iron"));
            case COPPER -> models().getExistingFile(HBM.rl("block/env/bedrock_ore_copper"));
            default -> null;
        };
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

    public String path(Block block){
        return key(block).getPath();
    }
    public ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }
    public String name(Block block) {
        return key(block).getPath();
    }

    public ModelFile genBuiltInModelFile(Block block, String type){
        String name = name(block);
        ResourceLocation blockTexture = blockTexture(block);
        return switch (type){
            case "cube_all" -> cubeAll(block);
            case "cube_top" -> models().cubeTop(name, blockTexture.withSuffix("side"), blockTexture.withSuffix("top"));
            case "cube_bottom_top" -> models().cubeBottomTop(name, blockTexture.withSuffix("side"), blockTexture.withSuffix("bottom"), blockTexture.withSuffix("top"));
            case "cube_column" -> models().cubeColumn(name, blockTexture.withSuffix("side"), blockTexture.withSuffix("end"));
            case "leaves" -> models().leaves(name, blockTexture);
            // 使用给定的model文件，这里假定只有
            case "existing" -> models().getExistingFile(HBM.rl(name));
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }
}
