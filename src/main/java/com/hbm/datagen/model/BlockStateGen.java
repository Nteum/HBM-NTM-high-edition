package com.hbm.datagen.model;

import com.hbm.HBM;
import com.hbm.block.HBMBlockProperties;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.RegistryHelper;
import com.hbm.core.client.model.CustomPartsModel;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.hbm.registries.RegistryHelper.*;

public class BlockStateGen extends BlockStateProvider {
    private List<ICategoryStateProvider> categoryStateProviders = new ArrayList<>();
    public ModelGenData modelGenData = new ModelGenData(this);
    public BlockStateGen(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
        super(output, modid, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        HBM.LOGGER.info("HBM BlockStateGen: generating block and item models");
        for (ICategoryStateProvider categoryStateProvider : categoryStateProviders) {
            categoryStateProvider.registerStatesAndModels();
        }
        ModBlocks.genModel(this);
        //简单方块和物品
        horizontalBlockWithItem(ModBlocks.machine_battery.get(),this.models().orientable("machine_battery", HBM.rl("block/battery_side"), HBM.rl("block/battery_front"), HBM.rl("block/battery_top")));
        horizontalBlockWithItem(ModBlocks.machine_lithium_battery.get(),this.models().orientable("machine_lithium_battery", HBM.rl("block/battery_lithium_side"), HBM.rl("block/battery_lithium_front"), HBM.rl("block/battery_lithium_top")));
        horizontalBlockWithItem(ModBlocks.machine_schrabidium_battery.get(),this.models().orientable("machine_schrabidium_battery", HBM.rl("block/battery_schrabidium_side"), HBM.rl("block/battery_schrabidium_front"), HBM.rl("block/battery_schrabidium_top")));
        horizontalBlockWithItem(ModBlocks.machine_dineutronium_battery.get(),this.models().orientable("machine_dineutronium_battery", HBM.rl("block/battery_dineutronium_side"), HBM.rl("block/battery_dineutronium_front"), HBM.rl("block/battery_dineutronium_top")));
        horizontalBlockWithItem(ModBlocks.machine_shredder.get(),
            this.models().orientableWithBottom("machine_shredder",
                HBM.rl("block/machine_shredder_side"),
                HBM.rl("block/machine_shredder_front"),
                HBM.rl("block/machine_shredder_bottom"),
                HBM.rl("block/machine_shredder_top")));
        // Tokamak components
        ResourceLocation tokamakSide = HBM.rl("block/tokamak_coil");
        ResourceLocation tokamakFront = HBM.rl("block/tokamak_controller_front");
        ModelFile tokamakController = this.models().orientableWithBottom("tokamak_controller", tokamakSide, tokamakFront, tokamakSide, tokamakSide);
        horizontalBlockWithItem(ModBlocks.tokamak_controller.get(), tokamakController);
        simpleBlockWithItem(ModBlocks.tokamak_casing.get(), this.models().cubeAll("tokamak_casing", HBM.rl("block/block_steel")));
        simpleBlockWithItem(ModBlocks.tokamak_coil.get(), this.models().cubeAll("tokamak_coil", tokamakSide));
        simpleBlockWithItem(ModBlocks.tokamak_heater.get(), this.models().cubeAll("tokamak_heater", tokamakSide));
        simpleBlockWithItem(ModBlocks.tokamak_injector.get(), this.models().cubeAll("tokamak_injector", tokamakSide));
        simpleBlockWithItem(ModBlocks.tokamak_port.get(), this.models().cubeAll("tokamak_port", tokamakSide));

        //多状态的方块和物品
//        //1. 高炉
//        BlockModelBuilder machineDifurnace_off = this.models().orientableWithBottom("machine_difurnace_off", HBM.rl("block/difurnace_side"), HBM.rl("block/difurnace_front_off"), HBM.rl("block/difurnace_bottom"), HBM.rl("block/difurnace_top_off"));
//        BlockModelBuilder machineDifurnace_on = this.models().orientableWithBottom("machine_difurnace_on", HBM.rl("block/difurnace_side"), HBM.rl("block/difurnace_front_on"),HBM.rl("block/difurnace_bottom"), HBM.rl("block/difurnace_top_on"));
//        addBooleanStateWithFace(ModBlocks.machine_difurnace.get(), BlockStateProperties.LIT, machineDifurnace_off, machineDifurnace_on);
//        //2. 电炉
//        BlockModelBuilder machineElectricFurnaceOff = this.models().orientableWithBottom("machine_electric_furnace_off",
//            HBM.rl("block/furnace_electric_side"),
//            HBM.rl("block/furnace_electric_front"),
//            HBM.rl("block/furnace_electric_bottom"),
//            HBM.rl("block/furnace_electric_top"));
//        BlockModelBuilder machineElectricFurnaceOn = this.models().orientableWithBottom("machine_electric_furnace_on",
//            HBM.rl("block/furnace_electric_side"),
//            HBM.rl("block/furnace_electric_front_alter"),
//            HBM.rl("block/furnace_electric_bottom"),
//            HBM.rl("block/furnace_electric_top"));
//        addBooleanStateWithFace(ModBlocks.machine_electric_furnace.get(), BlockStateProperties.LIT, machineElectricFurnaceOff, machineElectricFurnaceOn);
//        //3. 加热器
//        BlockModelBuilder machine_boiler_off = this.models().orientable("machine_boiler_off",
//                HBM.rl("block/machine_boiler_side"),
//                HBM.rl("block/machine_boiler_front"),
//                HBM.rl("block/machine_boiler_top"));
//        BlockModelBuilder machine_boiler_on = this.models().orientable("machine_boiler_on",
//                HBM.rl("block/machine_boiler_side"),
//                HBM.rl("block/machine_boiler_front_lit"),
//                HBM.rl("block/machine_boiler_top"));
//        addBooleanStateWithFace(ModBlocks.machine_boiler.get(), BlockStateProperties.LIT, machine_boiler_off, machine_boiler_on);
//        //4. 电加热器
//        BlockModelBuilder machine_electric_boiler_off = this.models().orientable("machine_electric_boiler_off",
//                HBM.rl("block/machine_boiler_side"),
//                HBM.rl("block/machine_boiler_electric_front"),
//                HBM.rl("block/machine_boiler_electric_top"));
//        BlockModelBuilder machine_electric_boiler_on = this.models().orientable("machine_electric_boiler_on",
//                HBM.rl("block/machine_boiler_side"),
//                HBM.rl("block/machine_boiler_electric_front_lit"),
//                HBM.rl("block/machine_boiler_electric_top"));
//        addBooleanStateWithFace(ModBlocks.machine_electric_boiler.get(), BlockStateProperties.LIT, machine_electric_boiler_off, machine_electric_boiler_on);
//        //5. 核加热器
//        BlockModelBuilder machine_nuclear_boiler_off = this.models().orientable("machine_nuclear_boiler_off",
//                HBM.rl("block/machine_boiler_nuclear_side"),
//                HBM.rl("block/machine_boiler_nuclear_front"),
//                HBM.rl("block/machine_boiler_top"));
//        BlockModelBuilder machine_nuclear_boiler_on = this.models().orientable("machine_nuclear_boiler_on",
//                HBM.rl("block/machine_boiler_nuclear_side"),
//                HBM.rl("block/machine_boiler_nuclear_front_lit"),
//                HBM.rl("block/machine_boiler_top"));
//        addBooleanStateWithFace(ModBlocks.machine_nuclear_boiler.get(), BlockStateProperties.LIT, machine_nuclear_boiler_off, machine_nuclear_boiler_on);
//        // Nuclear bomb blockstates and item models are hand-authored so OBJ block models do not leak into GUI slots.

        //线缆
        cableBlockWithItem();
        // 新体系添加的物品
        horizontalBlockWithItem(ModBlocks.TEST12.get(),"block/test12/test12");
        addHorizontalModel(ModBlocks.SPACE_STATION_BASE.get(), "block/space_station_base");
        simpleBlockWithItem(ModBlocks.CONNECTOR.get(), genBuiltInModelFile(ModBlocks.CONNECTOR.get(), "existing"));
        addHorizontalModel(ModBlocks.machine_assembler.get(), "block/machine_assembler");
//        addHorizontalModel(ModBlocks.machine_press.get(), "block/press");
//        addHorizontalModel(ModBlocks.HEATER_FIREBOX.get(), "block/firebox");
        addHorizontalModel(ModBlocks.anvil_iron.get(),"block/anvil_iron");
        addHorizontalModel(ModBlocks.anvil_bismuth.get(),"block/anvil_bismuth");
        addHorizontalModel(ModBlocks.anvil_desh.get(),"block/anvil_desh");
        addHorizontalModel(ModBlocks.machine_cracking_tower.get(),"block/cracking_tower");
        addHorizontalModel(ModBlocks.machine_crucible.get(), "block/crucible");
        conveyor(ModBlocks.conveyor.get(), "block/conveyor");
        conveyorCrane(ModBlocks.CONVEYOR_INSERTER.get(), "block/conveyor_inserter");
        conveyorCrane(ModBlocks.CONVEYOR_EXTRACTOR.get(), "block/conveyor_extractor");
        addHorizontalModel(ModBlocks.MACHINE_CHEMPLANT.get(), "block/chemplant/chemplant_new_body");
//        addHorizontalModel(ModBlocks.BARREL_PLASTIC.get(), "block/barrel/barrel_plastic");
//        addHorizontalModel(ModBlocks.BARREL_CORRODED.get(), "block/barrel/barrel_corroded");
//        addHorizontalModel(ModBlocks.IRON_BARREL.get(), "block/barrel/barrel_iron");
//        addHorizontalModel(ModBlocks.STEEL_BARREL.get(), "block/barrel/barrel_steel");
//        addHorizontalModel(ModBlocks.TCALLOY_BARREL.get(), "block/barrel/barrel_tcalloy");
//        addHorizontalModel(ModBlocks.ANTIMATTER_BARREL.get(), "blockstates/barrel/barrel_antimatter");
//        addHorizontalModel(ModBlocks.GEIGER_COUNTER.get(), "block/geiger");
        addHorizontalModel(ModBlocks.LAUNCH_PAD.get(), "block/launch_pad");
        pipeBlockWithItem(ModBlocks.FLUID_DUCT_NEO.get());
        addHorizontalModel(ModBlocks.MINER_LARGE.get(), "block/miner_large");
        for (RegistryObject<Block> block : ModBlocks.BEDROCK_ORE.registryObjectMap.values()) {
            cubeWithOverlay(block.get(), ResourceLocation.tryParse("block/bedrock"), HBM.rl("block/ore_random_"), HBMBlockProperties.BEDROCK_ORE_VARIANT, "hbm:block/ore_bedrock");
        }
//        addBooleanStateWithOrientableModel(ModBlocks.machine_electric_furnace.get(), BlockStateProperties.LIT);
        addBooleanStateWithOrientableModel(ModBlocks.machine_boiler.get(), BlockStateProperties.LIT);
        addBooleanStateWithOrientableModel(ModBlocks.machine_electric_boiler.get(), BlockStateProperties.LIT);
        addBooleanStateWithOrientableModel(ModBlocks.machine_nuclear_boiler.get(), BlockStateProperties.LIT);
        addDifurnace(ModBlocks.MACHINE_DIFURNACE.get(), BlockStateProperties.LIT, HBMBlockProperties.WITH_HAT);
        addBooleanStateWithFace(ModBlocks.BLOCK_SLAG.get(), HBMBlockProperties.VARIANT, genBuiltInModelFile(ModBlocks.BLOCK_SLAG.get(), "cube_all"), genBuiltInModelFile(ModBlocks.BLOCK_SLAG.get(), "cube_all", "_alter", "_alter"));
        horizontalBlockWithItem(ModBlocks.BLOCK_C4.get(), genBuiltInModelFile(ModBlocks.BLOCK_C4.get(), "orientable_vertical"));
        horizontalBlockWithItem(ModBlocks.BLOCK_SEMTEX.get(), genBuiltInModelFile(ModBlocks.BLOCK_SEMTEX.get(), "orientable_vertical"));
        simpleBlockWithItem(ModBlocks.STONE_POROUS.get(), models().getExistingFile(ResourceLocation.tryParse("block/stone")));

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

    public void addHorizontalModel(Block block,String name){
        ModelFile.ExistingModelFile existingFile = this.models().getExistingFile(HBM.rl(name));
//        this.horizontalBlock(block,existingFile);
        getVariantBuilder(block).forAllStatesExcept(state -> ConfiguredModel.builder()
                        .modelFile(existingFile)
                        .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                        .build(), HBMBlockProperties.IS_CORE);
        this.simpleBlockItem(block,existingFile);
    }

    private void cubeWithOverlay(Block block, ResourceLocation base, ResourceLocation overlay, Property property){
        cubeWithOverlay(block, base, overlay, property, "");
    }
    private void cubeWithOverlay(Block block, ResourceLocation base, ResourceLocation overlay, Property property, String specifiedModelName){
        String name = name(block);
        getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder().modelFile(models()
                .withExistingParent(specifiedModelName.isEmpty() ? name : specifiedModelName + "_" + state.getValue(property), HBM.rl("block/abstract/cube_all_with_tint_overlay"))
                .texture("base", base).texture("overlay", overlay.withSuffix(state.getValue(property).toString()))).build());
        this.simpleBlockItem(block, models().cubeAll(name, base));
    }
    // 专用于传送带模型
    private void conveyor(Block block,String name){
        ModelFile.ExistingModelFile existingFile = this.models().getExistingFile(HBM.rl(name));
        getVariantBuilder(block).forAllStates(state -> {
            int variant = state.getValue(HBMBlockProperties.VARIANT8).intValue();
            return ConfiguredModel.builder()
                    .modelFile(switch (variant){
                        case 0 -> existingFile;
                        case 1 -> this.models().getExistingFile(HBM.rl(name + "_left"));
                        case 2 -> this.models().getExistingFile(HBM.rl(name + "_right"));
                        case 3 -> this.models().getExistingFile(HBM.rl(name + "_up_1"));
                        case 4 -> this.models().getExistingFile(HBM.rl(name + "_up_2"));
                        case 5 -> this.models().getExistingFile(HBM.rl(name + "_up_3"));
                        case 6 -> this.models().getExistingFile(HBM.rl(name + "_down_1"));
                        case 7 -> this.models().getExistingFile(HBM.rl(name + "_down_2"));
                        default -> throw new IllegalStateException("Unexpected value: " + variant);
                    })
                    .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                    .build();
        });
        this.simpleBlockItem(block,existingFile);
    }
    private void addBooleanStateWithOrientableModel(Block block, BooleanProperty booleanProperty){
        addBooleanStateWithFace(block, booleanProperty, genBuiltInModelFile(block, "orientable"), genBuiltInModelFile(block, "orientable", "_alter", "", "_alter", ""));
    }
    /** 添加有两个状态，并带有水平方向的方块（HBM的方块机器大部分属于此列） */
    private void addBooleanStateWithFace(Block block, BooleanProperty booleanProperty, ModelFile model1, ModelFile model2){
        this.getVariantBuilder(block)
                .forAllStates(state -> {
                    Boolean value = state.getValue(booleanProperty);
                    return ConfiguredModel.builder()
                            .modelFile(value == Boolean.FALSE ? model1 : model2)
                            .rotationY(state.hasProperty(BlockStateProperties.HORIZONTAL_FACING) ? ((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360 : 0)
                            .build();
                });
        this.simpleBlockItem(block,model1);
    }
    private void addDifurnace(Block block, BooleanProperty booleanProperty, BooleanProperty booleanProperty2){
        addTwoBooleanState(block, booleanProperty, booleanProperty2,
                genBuiltInModelFile(block, "orientable", "_off", "_alt", "_off_alt", "_off_alt"),
                genBuiltInModelFile(block, "orientable", "_off_tall", "_tall", "_off_tall", "_off_alt"),
                genBuiltInModelFile(block, "orientable", "_on", "_alt", "_on_alt", "_on_alt"),
                genBuiltInModelFile(block, "orientable", "_on_tall", "_tall", "_on_tall", "_on_alt")
        );
    }
    private void addTwoBooleanState(Block block, BooleanProperty booleanProperty, BooleanProperty booleanProperty2, ModelFile model1, ModelFile model2, ModelFile model3, ModelFile model4){
        this.getVariantBuilder(block)
                .forAllStates(state -> {
                    Boolean value = state.getValue(booleanProperty);
                    boolean value2 = state.getValue(booleanProperty2);
                    return ConfiguredModel.builder()
                            .modelFile(value == Boolean.FALSE ? (value2 == Boolean.FALSE ? model1 : model2) : value2 == Boolean.FALSE ? model3 : model4)
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

    public static String path(Block block){
        return key(block).getPath();
    }
    public static ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }
    public String name(Block block) {
        return key(block).getPath();
    }

    public ModelFile genBuiltInModelFile(Block block, String type, String ... nicknames){
        String name = name(block);
        ResourceLocation blockTexture = blockTexture(block);
        return switch (type){
            case "cube_all" -> models().cubeAll(name + getOrBlank(nicknames, 0), blockTexture.withSuffix(getOrBlank(nicknames, 1)));
            case "cube_top" -> models().cubeTop(name, blockTexture.withSuffix("_side"), blockTexture.withSuffix("_top"));
            case "cube_bottom_top" -> models().cubeBottomTop(name, blockTexture.withSuffix("_side"), blockTexture.withSuffix("_bottom"), blockTexture.withSuffix("_top"));
            case "cube_column" -> models().cubeColumn(name, blockTexture.withSuffix("_side"), blockTexture.withSuffix("_end"));
            case "leaves" -> models().leaves(name, blockTexture);
            // 使用给定的model文件，这里假定只有
            case "existing" -> models().getExistingFile(HBM.rl(name));
            case "orientable" -> models().orientable(name + getOrBlank(nicknames, 0), blockTexture.withSuffix("_side" + getOrBlank(nicknames, 1)), blockTexture.withSuffix("_front" + getOrBlank(nicknames, 2)), blockTexture.withSuffix("_top" + getOrBlank(nicknames, 3)));
            case "orientable_vertical" -> models().orientableVertical(name, blockTexture.withSuffix("_side"), blockTexture.withSuffix("_front"));
            case "cross" -> models().cross(name, blockTexture);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };

    }
    private static String getOrBlank(String[] array, int i){
        return RegistryHelper.getOrBlank(array, i);
//        return array.length > i ? array[i] : "";
    }
    // 输送带控制器，鬼知道为什么bob用了crane这个词
    // 就是纯纯的屎山，我都不知道该说什么，摊上这玩意算我倒霉，硬着头皮搞了三个晚上，
    /**
     * 1. in 默认方向north，y轴旋转控制水平面，x轴旋转控制上下面
     * 2. arrow1 和in方向相对，默认方向south，旋转与in同步
     * 3. arrow2 默认方向east，即in的右侧。
     *          in在水平面，且相对方向为2，arrow2跟随in旋转，相对方向为1，arrow2 x轴转180度，y轴反向旋转；
     *          in在垂直面，arrow2 x轴旋转90/-90度，可满足左右情况
     * 4. arrow3 默认方向up，即in的上方。
     *          in在水平面，arrow3 y轴跟随in旋转，旋转x轴可满足下方。
     *          in在垂直面，x轴旋转至南北两个面。
     * */
    private void conveyorCrane(Block block, String name){
        ModelFile.ExistingModelFile existingFile;
        MultiPartBlockStateBuilder builder = getMultipartBuilder(block);
        builder.part().modelFile(this.models().getExistingFile(HBM.rl(name + "_cube"))).addModel().end();
        for (Direction dir : BlockStateProperties.FACING.getPossibleValues()) {
            existingFile = this.models().getExistingFile(HBM.rl(name + "_in"));
            if (dir.getAxis().isHorizontal()) {
                builder.part().modelFile(existingFile).rotationY((((int) dir.toYRot()) + 180) % 360).uvLock(true).addModel().condition(BlockStateProperties.FACING, dir);
            }else {
                builder.part().modelFile(existingFile).rotationX(dir == Direction.UP ? -90 : 90).uvLock(true).addModel().condition(BlockStateProperties.FACING, dir);
            }
            for (int relativeDir : HBMBlockProperties.RELATIVE_DIRECTION.getPossibleValues()) {
                int[] xyRot = new int[]{dir == Direction.UP ? -90 : dir == Direction.DOWN ? 90 : 0, dir.getAxis().isHorizontal() ? (((int) dir.toYRot()) + 180) % 360 : 0};
                if (relativeDir == 0) existingFile = this.models().getExistingFile(HBM.rl(name + "_arrow1"));
                else {
                    boolean horizontal = dir.getAxis().isHorizontal();
                    switch (relativeDir){
                        case 1 -> {
                            existingFile = this.models().getExistingFile(HBM.rl(name + "_arrow2"));
                            xyRot[0] = (xyRot[0] + (horizontal ? 180 : 0)) % 360;
                            xyRot[1] = (xyRot[1] + 180) % 360;
                        }case 2 -> {
                            existingFile = this.models().getExistingFile(HBM.rl(name + "_arrow2"));
                        }
                        case 3 -> {
                            if (horizontal){
                                existingFile = this.models().getExistingFile(HBM.rl(name + "_arrow3"));
                                xyRot[1] = (xyRot[1] + 270) % 360;
                            }else {
                                existingFile = this.models().getExistingFile(HBM.rl(name + "_arrow2"));
                                xyRot[1] = 90;
                            }
                        }case 4 -> {
                            if (horizontal){
                                existingFile = this.models().getExistingFile(HBM.rl(name + "_arrow3"));
                                xyRot[0] = (xyRot[0] + 180) % 360;
                                xyRot[1] = (xyRot[1] + 270) % 360;
                            }else {
                                existingFile = this.models().getExistingFile(HBM.rl(name + "_arrow2"));
                                xyRot[1] = -90;
                            }
                        }
                    }
                }
                builder.part().modelFile(existingFile).rotationY(xyRot[1]).rotationX(xyRot[0]).addModel().condition(BlockStateProperties.FACING, dir).condition(HBMBlockProperties.RELATIVE_DIRECTION, relativeDir);
            }
        }
        this.simpleBlockItem(block, this.models().getExistingFile(HBM.rl(name + "_item")));
    }

    private void pipeBlockWithItem(Block block){
        ModelFile.ExistingModelFile inventory = models().getExistingFile(HBM.rl("block/pipes/pipe_inv"));
        ModelFile.ExistingModelFile core = models().getExistingFile(HBM.rl("block/pipes/pipe_core"));
        ModelFile.ExistingModelFile north = models().getExistingFile(HBM.rl("block/pipes/pipe_north"));
        ModelFile.ExistingModelFile south = models().getExistingFile(HBM.rl("block/pipes/pipe_south"));
        ModelFile.ExistingModelFile east = models().getExistingFile(HBM.rl("block/pipes/pipe_east"));
        ModelFile.ExistingModelFile west = models().getExistingFile(HBM.rl("block/pipes/pipe_west"));
        ModelFile.ExistingModelFile up = models().getExistingFile(HBM.rl("block/pipes/pipe_up"));
        ModelFile.ExistingModelFile down = models().getExistingFile(HBM.rl("block/pipes/pipe_down"));
        MultiPartBlockStateBuilder builder = getMultipartBuilder(block).part().modelFile(core).addModel().end();
        builder.part().modelFile(east).addModel().condition(PipeBlock.EAST,true);
        builder.part().modelFile(west).addModel().condition(PipeBlock.WEST,true);
        builder.part().modelFile(north).addModel().condition(PipeBlock.NORTH,true);
        builder.part().modelFile(south).addModel().condition(PipeBlock.SOUTH,true);
        builder.part().modelFile(up).addModel().condition(PipeBlock.UP,true);
        builder.part().modelFile(down).addModel().condition(PipeBlock.DOWN,true);
        simpleBlockItem(block, inventory);
    }

    private BlockModelBuilder genSimpleModel(Block block, float scale){
        return genSimpleModel(block, key(block), key(block), scale);
    }

    public BlockModelBuilder genSimpleModel(Block block, ResourceLocation model, ResourceLocation texture, float size){
        BlockModelBuilder builder = models().getBuilder(path(block)).parent(models().getExistingFile(ResourceLocation.fromNamespaceAndPath("minecraft", "block/block")));
        builder.customLoader(CustomPartsModel.LoaderBuilder::new).setModel(model).autoCull(false).flipV(true);
        builder.renderType("cutout").texture("texture0", texture).texture("particle", texture);
        // 由于模型还需要在renderer中复用，
        builder.rootTransforms().translation(0.5f, 0, 0.5f);
        float baseScale = 1 / size;
        float offsetX = size / 6;
        float offsetY = size / 2;
        float offsetZ = 0;
        // 1. GUI 界面（原版：旋转 30, 135, 0 | 缩放 0.625f）
        // 💡 我们将原版缩放乘以你的基础缩放，位移加上你的基础位移
        builder.transforms()
                .transform(ItemDisplayContext.GUI)
                .rotation(30, 135, 0)
                .translation(offsetX, - offsetY, offsetZ)
                .scale(0.625f * baseScale).end()

                // 2. 第三人称右手（原版：旋转 75, 45, 0 | 缩放 0.375f）
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
                .rotation(75, 45, 0)
                .translation(0 + offsetX, 2.5f , 0 + offsetZ)
                .scale(0.375f * baseScale).end()

                // 3. 第一人称右手（原版：旋转 0, 45, 0 | 缩放 0.4f）
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                .rotation(0, 45, 0)
                .translation(offsetX, - offsetY, offsetZ)
                .scale(0.4f * baseScale).end()

                // 4. 地面掉落物（原版：缩放 0.25f）
                .transform(ItemDisplayContext.GROUND)
                .rotation(0, 0, 0)
                .translation(offsetX, 3.0f , offsetZ)
                .scale(0.25f * baseScale).end()

                // 5. 物品展示框（原版：缩放 0.5f）
                .transform(ItemDisplayContext.FIXED)
                .rotation(0, 0, 0)
                .translation(offsetX, - offsetY, offsetZ)
                .scale(0.5f * baseScale).end()
                .end();
        return builder;
    }

    /**
     * 模型文件生成的内容
     */
    public enum Type{
        CUBE_ALL, CUBE_TOP, CUBE_BOTTOM_TOP, CUBE_COLUMN, LEAVES, EXISTING, ORIENTABLE, ORIENTABLE_WITH_BOTTOM, ORIENTABLE_VERTICAL, OBJ, STANDALONE
    }
    public static class ModelGenData{
        BlockStateProvider parent;
        public Type type = null;
        public Block block;
        /**
         * 理想情况是完全由block生成所有的texture路径，但很多文件名和block名不匹配
         */
        public ResourceLocation specificModelRL;
        public String modelRLSuffix;
        public ResourceLocation[] texRL;   // 额外规定的贴图的位置
        public String[] texSuffix;
        public BiFunction<Block, BlockStateGen, ModelFile> factory;
        public ResourceLocation existModelFile;
        public float size;

        public ModelGenData(BlockStateProvider provider){
            this.parent = provider;
        }

        private void reset(){
            type = null;
            block = null;
            specificModelRL = null;
            modelRLSuffix = null;
            texSuffix = null;
            texRL = null;
            factory = null;
            existModelFile = null;
            size = 0;
        }

        public ModelFile build(){
            ModelFile modelFile = buildNoReset();
            reset();
            return modelFile;
        }
        public ModelFile buildNoReset(){
            String name = specificModelRL == null ? path(block) : specificModelRL.getPath();
            name = modelRLSuffix == null || modelRLSuffix.isEmpty() ? name : name + modelRLSuffix;
            ResourceLocation blockTexture = specificModelRL == null ? blockTexture(block) : RegistryHelper.prefix(specificModelRL, ModelProvider.BLOCK_FOLDER + "/");

            ModelFile model = switch (type){
                case CUBE_ALL -> models().cubeAll(name, getOrDefault(texRL, 0, blockTexture.withSuffix(getOrBlank(texSuffix, 0))));
                case CUBE_TOP -> models().cubeTop(
                        name,
                        getOrDefault(texRL, 0, blockTexture.withSuffix("_side" + getOrBlank(texSuffix, 0))),
                        getOrDefault(texRL, 1, blockTexture.withSuffix("_top" + getOrBlank(texSuffix, 1)))
                );case CUBE_BOTTOM_TOP -> models().cubeBottomTop(
                        name,
                        getOrDefault(texRL, 0, blockTexture.withSuffix("_side" + getOrBlank(texSuffix, 0))),
                        getOrDefault(texRL, 1, blockTexture.withSuffix("_bottom" + getOrBlank(texSuffix, 1))),
                        getOrDefault(texRL, 2, blockTexture.withSuffix("_top" + getOrBlank(texSuffix, 2)))
                );case CUBE_COLUMN -> models().cubeColumn(
                        name,
                        getOrDefault(texRL, 0, blockTexture.withSuffix(containIdx(texSuffix, 1) ? "_side" : getOrBlank(texSuffix, 1))),
                        getOrDefault(texRL, 1, blockTexture.withSuffix("_end" + getOrBlank(texSuffix, 2)))
                );case LEAVES -> models().leaves(name, getOrDefault(texRL, 0, blockTexture.withSuffix(getOrBlank(texSuffix, 1))));
                case EXISTING -> models().getExistingFile(specificModelRL);
                case ORIENTABLE -> models().orientable(
                        name,
                        getOrDefault(texRL, 0, blockTexture.withSuffix(containIdx(texSuffix, 0) ? "_side" : getOrBlank(texSuffix, 0))),
                        getOrDefault(texRL, 1, blockTexture.withSuffix(containIdx(texSuffix, 1) ? "_front" : getOrBlank(texSuffix, 1))),
                        getOrDefault(texRL, 2, blockTexture.withSuffix(containIdx(texSuffix, 2) ? "_top" : getOrBlank(texSuffix, 2)))
                );case ORIENTABLE_WITH_BOTTOM -> models().orientableWithBottom(
                        name,
                        getOrDefault(texRL, 0, blockTexture.withSuffix(containIdx(texSuffix, 0) ? "_side" : getOrBlank(texSuffix, 0))),
                        getOrDefault(texRL, 1, blockTexture.withSuffix(containIdx(texSuffix, 1) ? "_front" : getOrBlank(texSuffix, 1))),
                        getOrDefault(texRL, 2, blockTexture.withSuffix(containIdx(texSuffix, 2) ? "_bottom" : getOrBlank(texSuffix, 2))),
                        getOrDefault(texRL, 3, blockTexture.withSuffix(containIdx(texSuffix, 3) ? "_top" : getOrBlank(texSuffix, 3)))
                );case ORIENTABLE_VERTICAL -> models().orientableVertical(
                        name,
                        getOrDefault(texRL, 0, blockTexture.withSuffix(containIdx(texSuffix, 0) ? "_side" : getOrBlank(texSuffix, 0))),
                        getOrDefault(texRL, 1, blockTexture.withSuffix(containIdx(texSuffix, 1) ? "_front" : getOrBlank(texSuffix, 1)))
                );case STANDALONE -> factory.apply(block, (BlockStateGen) parent);
                case OBJ -> genObjJson(block, existModelFile, texRL == null || texRL.length == 0 ? blockTexture : texRL[0], size);
            };
            return model;
        }

        public BlockModelBuilder genObjJson(Block block, ResourceLocation model, ResourceLocation texture, float size){
            BlockModelBuilder builder = models().getBuilder(path(block)).parent(models().getExistingFile(ResourceLocation.fromNamespaceAndPath("minecraft", "block/block")));
            builder.customLoader(CustomPartsModel.LoaderBuilder::new).setModel(model).autoCull(false).flipV(true);
            builder.renderType("cutout").texture("texture0", texture).texture("particle", texture);
            // 由于模型还需要在renderer中复用，
            builder.rootTransforms().translation(0.5f, 0, 0.5f);
            float baseScale = 1 / size;
            float offsetX = size / 6;
            float offsetY = size / 2;
            float offsetZ = 0;
            // 1. GUI 界面（原版：旋转 30, 135, 0 | 缩放 0.625f）
            // 💡 我们将原版缩放乘以你的基础缩放，位移加上你的基础位移
            builder.transforms()
                    .transform(ItemDisplayContext.GUI)
                    .rotation(30, 135, 0)
                    .translation(offsetX, - offsetY, offsetZ)
                    .scale(0.625f * baseScale).end()

                    // 2. 第三人称右手（原版：旋转 75, 45, 0 | 缩放 0.375f）
                    .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
                    .rotation(75, 45, 0)
                    .translation(0 + offsetX, 2.5f , 0 + offsetZ)
                    .scale(0.375f * baseScale).end()

                    // 3. 第一人称右手（原版：旋转 0, 45, 0 | 缩放 0.4f）
                    .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                    .rotation(0, 45, 0)
                    .translation(offsetX, - offsetY, offsetZ)
                    .scale(0.4f * baseScale).end()

                    // 4. 地面掉落物（原版：缩放 0.25f）
                    .transform(ItemDisplayContext.GROUND)
                    .rotation(0, 0, 0)
                    .translation(offsetX, 3.0f , offsetZ)
                    .scale(0.25f * baseScale).end()

                    // 5. 物品展示框（原版：缩放 0.5f）
                    .transform(ItemDisplayContext.FIXED)
                    .rotation(0, 0, 0)
                    .translation(offsetX, - offsetY, offsetZ)
                    .scale(0.5f * baseScale).end()
                    .end();
            return builder;
        }

        private BlockModelProvider models() {
            return parent.models();
        }

        private ResourceLocation blockTexture(Block block){
            return parent.blockTexture(block);
        }
    }

    public void horizontalBlockItem(Block block, ModelFile modelFile) {
        getVariantBuilder(block).forAllStatesExcept(state -> ConfiguredModel.builder()
                .modelFile(modelFile)
                .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                .build(), HBMBlockProperties.IS_CORE);
        this.simpleBlockItem(block, modelFile);
    }

    public void horizontalBlockItem(Block block) {
        if (block.defaultBlockState().hasProperty(BlockStateProperties.LIT)) {
            this.modelGenData.modelRLSuffix = "_on";
            String[] suf = this.modelGenData.texSuffix;
            this.modelGenData.texSuffix = switch (this.modelGenData.type){
                case ORIENTABLE -> suf == null ? new String[]{"_side", "_front_on", "_top"} : new String[]{suf[0], "_on" + suf[1], suf[2]};
                case ORIENTABLE_WITH_BOTTOM -> suf == null ? new String[]{"_side", "_front_on", "_bottom", "_top"} : new String[]{suf[0], suf[1], "_on" + suf[2], suf[3]};
                default -> this.modelGenData.texSuffix;
            };

            ModelFile modelFileOn = this.modelGenData.buildNoReset();

            this.modelGenData.modelRLSuffix = "_off";
            this.modelGenData.texSuffix = switch (this.modelGenData.type){
                case ORIENTABLE -> suf == null ? new String[]{"_side", "_front_off", "_top"} : new String[]{suf[0], "_off" + suf[1], suf[2]};
                case ORIENTABLE_WITH_BOTTOM -> suf == null ? new String[]{"_side", "_front_off", "_bottom", "_top"} : new String[]{suf[0], suf[1], "_off" + suf[2], suf[3]};
                default -> this.modelGenData.texSuffix;
            };
            ModelFile modelFileOff = this.modelGenData.build();
            getVariantBuilder(block).forAllStatesExcept(state -> ConfiguredModel.builder()
                    .modelFile(state.getValue(BlockStateProperties.LIT) ? modelFileOn : modelFileOff)
                    .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                    .build(), HBMBlockProperties.IS_CORE);
            this.simpleBlockItem(block, modelFileOff);
        } else if (block.defaultBlockState().hasProperty(HBMBlockProperties.BROKEN)) {
            ModelFile modelFileNormal = this.modelGenData.buildNoReset();
            this.modelGenData.modelRLSuffix = "_broken";
            this.modelGenData.existModelFile = this.modelGenData.specificModelRL;
            ModelFile modelFileExploded = this.modelGenData.build();
            getVariantBuilder(block).forAllStatesExcept(state -> ConfiguredModel.builder()
                    .modelFile(state.getValue(HBMBlockProperties.BROKEN) ? modelFileNormal : modelFileExploded)
                    .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                    .build(), HBMBlockProperties.IS_CORE);
            this.simpleBlockItem(block, modelFileNormal);
        } else {
                this.horizontalBlockItem(block, this.modelGenData.build());
        }
    }
}
